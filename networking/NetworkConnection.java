package networking;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.util.*;

import collectionsStuff.ArrayListInt;
import collectionsStuff.SmartByteBuffer;
import generic.Savable;
import generic.Savable.SavableAction;
import generic.Serializer;
import tools.AppFolder;
import tools.Meth;

/**
 * (for now) only UDP because of game-focus. But can transmit packets with
 * certainty, so TCP may not be needed at all
 * 
 * @author xaver
 *
 */
public class NetworkConnection {

	// TODO perfect sync write/read

	// TODO: automatic package split

	public static interface ReceiveTask {

		/**
		 * only the data from position on is actually yours, what's before could be
		 * protocol-stuff. The buffer is re-used for every receive, so if you want to
		 * keep the data, copy it! You may send from this method but don't do anything
		 * using too much time as this is called from the thread that receives all the
		 * packages. So best keep it short and simple (or redirect the package to a
		 * processing thread of yours or the main thread)
		 */
		public void received(SmartByteBuffer data, NetworkConnection src);

	}

	// shall contain: Possible Login information, connection status (last
	// connection, ping, connected since etc) and possibly bandwidth used

	protected long connectionTimeout = 10_000;

	protected TrueAddress dest;
	protected String identifier, login, password;
	protected long lastConnection, lastSent;

	protected DatagramPacket s = new DatagramPacket(new byte[0], 0);

	protected List<Package> safeList = new ArrayList<>();
	protected ArrayDeque<Package> queue = new ArrayDeque<>(), TCPQueue = new ArrayDeque<>();

	protected short localPackageNumber = Short.MIN_VALUE, remotePackageNumber;
//	protected int 

	protected ReceiveTask receiveTask;

	protected List<SavableAction> syncAddedListeners = new ArrayList<>(), syncRemovedListeners = new ArrayList<>();

	/**
	 * for comparison so bandwidth can be saved
	 */
	protected Map<Integer, SmartByteBuffer> syncCompMap = new HashMap<>();
	protected SmartByteBuffer compBuffer = new SmartByteBuffer();

	protected Map<Integer, Savable> syncMap = new HashMap<>();
//	protected Map<Integer, SmartByteBuffer> comparisonMap = new HashMap<>();
	protected List<Savable> toSync = new ArrayList<>(), packlist = new ArrayList<>();
	protected ArrayListInt synclist = new ArrayListInt(), added = new ArrayListInt(), removed = new ArrayListInt();
	/**
	 * of the other side (normally server)
	 */
	protected Map<String, Short> saveVersions = new HashMap<>();

	protected int updatePointer = 0;

	public NetworkConnection(InetAddress dest, int port, String identifier, String loginname, String password) {
		this(dest, port, identifier, loginname, password, (data, src) -> {
		});
	}

	public NetworkConnection(InetAddress dest, int port, String identifier) {
		this(dest, port, identifier, (data, src) -> {
		});
	}

	public NetworkConnection(InetAddress dest, int port, String identifier, String loginname, String password,
			ReceiveTask r) {
		this(dest, port, identifier);
		this.login = loginname;
		this.password = password;
		this.receiveTask = r;
	}

	public NetworkConnection(InetAddress dest, int port, String identifier, ReceiveTask r) {
		this.dest = new TrueAddress(dest, port);
		this.identifier = identifier;
		this.receiveTask = r;
		s.setAddress(dest);
		s.setPort(port);
	}

	public void setReceiveTask(ReceiveTask r) {
		if (r != null)
			this.receiveTask = r;
		else
			this.receiveTask = (data, src) -> {
			};
	}

	/**
	 * blocks until the connection is established or a time of
	 * {@link NetworkConnection#connectionTimeout} has passed or the connection
	 * request has been denied by the server
	 * 
	 * @return if the connection attempt was successful
	 */
	public boolean connect() {
		AppFolder.log.println("trying to login at " + dest.ip.getCanonicalHostName() + " with identifier " + identifier
				+ " and login " + login + " + pwd " + password);
		SmartByteBuffer buff = SmartByteBuffer.getBuffer();
		buff.add((byte) (login == null ? -1 : 42));
		buff.addString(identifier);
		buff.addString(login);
		buff.addString(password);
		queueTCPlike(buff);
//		AppFolder.log.println("trying to connect to server with name " + login + " and password " + password);
		long start = System.currentTimeMillis();
		while (!hasConnection() && System.currentTimeMillis() - start < connectionTimeout) {
			update();
		}
		return hasConnection();
	}

	/**
	 * for server->client. Not really necessary but will perhaps be useful in the
	 * future
	 */
	public void sendConnectConfirmation() {
		SmartByteBuffer buff = SmartByteBuffer.getBuffer();
		buff.add((byte) -1);
		buff.addString(identifier);
		buff.addString(login);
		buff.addString(password);
		queueTCPlike(buff);
	}

	public void queue(SmartByteBuffer b) {
		queue(b, 0);
	}

	public void queueEnsuredArrival(SmartByteBuffer b) {
		queue(b, 1);
	}

	public void queueTCPlike(SmartByteBuffer b) {
		queue(b, 2);
	}

	public void queue(SmartByteBuffer b, int kind) {
		Package p = new Package(b, this);
		if (kind == 2)
			TCPQueue.add(p);
		else if (kind == 1)
			safeList.add(p);
		else
			queue.add(p);
	}

	public void queue(Package p) {
		queue(p, 0);
	}

	public void queueEnsuredArrival(Package p) {
		queue(p, 1);
	}

	public void queueTCPlike(Package p) {
		queue(p, 2);
	}

	/**
	 * @param p
	 * @param kind 1: ensure arrival. 2: ensure arrival in right order (like TCP).
	 *             Everything else: just send it over UDP.
	 * @return
	 */
	public void queue(Package p, int kind) {
		if (kind == 2)
			TCPQueue.add(p);
		else if (kind == 1)
			safeList.add(p);
		else
			queue.add(p);
	}

	public void update() {
		int sent = 0;
		for (; sent < 10 && !queue.isEmpty(); sent++) {
			Package p = queue.pop();
			if (NetworkManager.isServer())
				addSyncData(p.data());
			send(p);
			p.data().free();
		}
		if (!TCPQueue.isEmpty()) {
			Package p = TCPQueue.peek();
			int ack = acknowledgementStatus(p);
			if (ack == 1) {
				TCPQueue.pop().data().free();
			} else {
				send(p);
				sent++;
			}
		}
		for (int i = 0; i < 10 && i < safeList.size(); i++) {
			Package p = safeList.get(i);
			if (!p.sent()) {

			}
		}

		// to keep the connection open and to sync savables if no other packages are
		// sent:

		if (System.currentTimeMillis() - lastSent > 30) {
			queue(SmartByteBuffer.getBuffer());
		}

	}

	private void send(Package p) {
		try {
			s.setData(p.data().getArray(), 0, p.data().size());
			NetworkManager.send(s);
		} catch (IOException e) {
			e.printStackTrace(AppFolder.log);
		}
		lastSent = System.currentTimeMillis();
		p.setSent();
		p.setID(++localPackageNumber);
	}

	public int acknowledgementStatus(Package p) {
		return acknowledgementStatus(p.ID());
	}

	public int acknowledgementStatus(int packetID) {
		return packetID > Short.MIN_VALUE - 1 ? 1 : 0;
	}

	public boolean hasConnection() {
		return System.currentTimeMillis() - lastConnection < connectionTimeout;
	}

	public void received(SmartByteBuffer rbuff) {
//		AppFolder.log.println("received something!");
		if (rbuff == null) {
			lastConnection = System.currentTimeMillis();
			return;
		}
		if (!hasConnection()) {
			// only allow connection if the package has been bounced back correctly with the
			// serverflag set, so no random program can just establish a connection on the
			// IP address the server was on / should be on
			// TODO add version byte to this check! (in the meantime you could use the
			// identifier+version...)
			int flag = rbuff.read();
			if (flag == -1 && rbuff.readString().equals(identifier) && rbuff.readString().equals(login)
					&& rbuff.readString().equals(password)) {
				lastConnection = System.currentTimeMillis();
				return;
			}
			return;
		}
		lastConnection = System.currentTimeMillis();

		// preprocess packages (acks, compression, possibly encryption) and then give
		// the user the buffer with already fitting rbuff position and size

		if (rbuff.size() > 0) {
			if (!NetworkManager.isServer()) {
				int bytes = rbuff.getInt(rbuff.size() - 4);
				rbuff.setSize(rbuff.size() - 4);
				if (bytes > 0) {
					rbuff.setPosition(rbuff.size() - bytes);
					readSyncData(rbuff);
				}
				rbuff.setPosition(0);
				rbuff.setSize(rbuff.size() - bytes);
			}
			receiveTask.received(rbuff, this);
		}
	}

	private void addSyncData(SmartByteBuffer buff) {
		if (buff.size() > NetworkManager.MAX_DATA_LENGTH * 0.9f) {
			buff.addInt(0);
			return;
		}
		synchronized (toSync) {

			// FIXME all of this relies on every package being received with 100% certainty.
			// Added and removed have to be, no matter what, but updates? That makes the
			// usage of UDP completely irrelevant...

			// *pos*sibly something wrong
			int pos = buff.size();

			packlist.clear();
			for (int i = 0; i < 5 && !added.isEmpty(); i++)
				packlist.add(syncMap.get(added.get(i)));
			if (added.size() <= 5)
				added.clear();
			else
				added.removeAll(0, 4);
			Serializer.write(packlist, buff);

			int radded = 0;
			int rp = buff.size();
			buff.addInt(0);
			while (!removed.isEmpty() && buff.size() < NetworkManager.MAX_DATA_LENGTH * 0.9f) {
				buff.addInt(removed.remove(removed.size() - 1));
				radded++;
			}
			buff.setInt(rp, radded);

			// possibly keep a buffer with every entities' data and send the byte-diff (->
			// bitmask) or something. Do the same on client and voila you've cut down half
			// the bandwidth. Should work pretty well until you run out of RAM. Shouldn't
			// happen

			int updated = 0, reallyUpdated = 0;
			rp = buff.size();
			buff.addInt(0);
			while (updated < toSync.size() && buff.size() < NetworkManager.MAX_DATA_LENGTH * 0.9f) {
				if (updatePointer >= toSync.size())
					updatePointer = 0;
				Savable s = toSync.get(updatePointer++);
				compBuffer.clear();
				s.addData(compBuffer);
				SmartByteBuffer comp = syncCompMap.get(s.ID);
				if (comp == null || !comp.contentMatches(compBuffer)) {
					buff.addInt(s.ID);
					if (comp == null || compBuffer.size() != comp.size()) {
						buff.add(Byte.MIN_VALUE);
						buff.addAll(compBuffer);
						if (comp == null) {
							syncCompMap.put(s.ID, compBuffer.clone());
						}
					} else {
						int bitmasklength = Meth.ceil(compBuffer.size() / 8.0f);
						buff.add((byte) (bitmasklength + Byte.MIN_VALUE));
						int ptr = buff.size();
						for (int i = 0; i < bitmasklength; i++)
							buff.add((byte) 0);
						for (int i = 0; i < compBuffer.size();) {
							byte mask = 0x00;
							for (int i2 = 0; i < compBuffer.size() && i2 < 8; i2++) {
								if (compBuffer.get(i) != comp.get(i)) {
									mask = Meth.setBit(mask, i2, true);
									buff.add(compBuffer.get(i));
									comp.set(i, compBuffer.get(i));
								} else {
									mask = Meth.setBit(mask, i2, false);
								}
								i++;
							}
							buff.set(ptr++, mask);
						}
					}
					reallyUpdated++;
				}
				updated++;
			}
			buff.setInt(rp, reallyUpdated);

			buff.addInt(buff.size() - pos);

		}
	}

	private void readSyncData(SmartByteBuffer buff) {
		if (buff.remaining() > 0) {
			synchronized (toSync) {
				packlist.clear();
				List<Savable> slist = Serializer.read(this.packlist, buff);
				if (slist != null && slist.size() > 0) {
					for (int i = 0; i < slist.size(); i++) {
						Savable s = slist.get(i);
						if (s != null) {
//							AppFolder.log.println("received " + s);
							toSync.add(s);
							syncMap.put(s.ID, s);
							synclist.add(s.ID);
							SmartByteBuffer b = new SmartByteBuffer();
							s.addData(b);
							syncCompMap.put(s.ID, b);
							for (int i2 = 0; i2 < syncAddedListeners.size(); i2++)
								syncAddedListeners.get(i2).action(s);
						}
					}
				}

				int rem = buff.readInt();
				for (int i = 0; i < rem; i++) {
					int ID = buff.read();
					Savable s = syncMap.remove(ID);
					if (s != null) {
						synclist.removeValue(ID);
						toSync.remove(s);
						for (int i2 = 0; i2 < syncRemovedListeners.size(); i2++)
							syncRemovedListeners.get(i2).action(s);
					}
				}

				int updated = buff.readInt();
				for (int i = 0; i < updated; i++) {
					int ID = buff.readInt();
					Savable s = syncMap.get(ID);
					if (s != null) {
						SmartByteBuffer n = syncCompMap.get(s.ID);
						if (n != null) {
							int bitmasklength = buff.read() - Byte.MIN_VALUE;
							if (bitmasklength == 0) {
								int p = buff.position();
								s.applyData(buff, saveVersions);
								n.clear();
								n.addAll(buff, p, buff.position() - p);
							} else {
								int ptr = buff.position() + bitmasklength;
								int nptr = 0;
								for (int b = 0; b < bitmasklength; b++) {
									byte bitmask = buff.read();
									for (int b2 = 0; b2 < 8; b2++) {
										// IndexOutOfBoundsException!
										if (Meth.readBit(bitmask, b2)) {
											n.set(nptr, buff.get(ptr++));
										}
										nptr++;
									}
								}
								s.applyData(n, saveVersions);
							}
						} else {
							s.applyData(buff, saveVersions);
						}
					} else {
//						AppFolder.log.println("ID " + ID + " has a null result!");
					}
				}

			}
		}
	}

	public boolean synced(Savable s) {
		if (s == null)
			return false;
		return synclist.contains(s.ID);
	}

	public void syncSavable(Savable s) {
		synchronized (toSync) {
			toSync.add(s);
			syncMap.put(s.ID, s);
			synclist.add(s.ID);
			added.add(s.ID);
		}
//		AppFolder.log.println("added sync to " + s);
	}

	public void removeSync(Savable s) {
		synchronized (toSync) {
			toSync.remove(s);
			synclist.remove(s.ID);
			removed.add(s.ID);
		}
	}

	public void addSyncAddedListener(SavableAction s) {
		syncAddedListeners.add(s);
	}

	public void addSyncRemovedListener(SavableAction s) {
		syncRemovedListeners.add(s);
	}

	public List<Savable> syncModels() {
		return toSync;
	}

}
