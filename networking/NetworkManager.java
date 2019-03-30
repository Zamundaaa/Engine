package networking;

import java.io.IOException;
import java.net.*;
import java.util.*;

import collectionsStuff.SmartByteBuffer;
import loaders.ModelCache;
import networking.NetworkConnection.ReceiveTask;
import tools.AppFolder;
import tools.GameThread;

public class NetworkManager {

	public static interface NetworkConnectionAction {
		public void action(NetworkConnection n);
	}

	public static final int MAX_DATA_LENGTH = 1500;

	private static String connIdentifier;
	private static boolean NetworkManagerRunning = true;
	private static DatagramSocket socket;
	private static DatagramPacket r = new DatagramPacket(new byte[MAX_DATA_LENGTH + 12], MAX_DATA_LENGTH + 12);
	private static SmartByteBuffer rbuff = new SmartByteBuffer(r.getData());
	private static List<NetworkConnection> connections = new ArrayList<>();
	private static Map<TrueAddress, NetworkConnection> connMap = new HashMap<>();
	private static boolean isServer;
	public static ReceiveTask defaultReceiveTask = (data, src) -> {
	};
	public static NetworkConnectionAction onConnection = (n) -> {
	};

	/**
	 * set the {@link NetworkManager#defaultReceiveTask} to something to make use of
	 * received packages
	 */
	public static void initServer(int port, String connectionIdentifier) throws IOException {
		if (socket != null && !socket.isClosed())
			socket.close();
		createThreads();
		isServer = true;
		connIdentifier = connectionIdentifier;
		socket = new DatagramSocket(port);
		socket.setSoTimeout(2000);
		NetworkManagerRunning = true;
		sender.start();
		receiver.start();
		ModelCache.mapTo(receiver);
		ModelCache.mapTo(sender);
	}

	/**
	 * set the {@link NetworkManager#defaultReceiveTask} to something to make use of
	 * received packages
	 * 
	 * @param server
	 * @param connectionIdentifier has to be the same on client and server or no
	 *                             connection will happen
	 * @param loginname
	 * @param password
	 * @throws IOException
	 */
	public static NetworkConnection initClient(InetAddress server, int serverPort, String connectionIdentifier,
			String loginname, String password) throws IOException {
		if (socket != null && !socket.isClosed())
			socket.close();
		createThreads();
		isServer = false;
		socket = new DatagramSocket();
		socket.setSoTimeout(2000);
		connIdentifier = connectionIdentifier;
		NetworkConnection conn = new NetworkConnection(server, serverPort, connectionIdentifier, loginname, password);
		NetworkManagerRunning = true;
		receiver.start();// so we can get a response!
		connections.add(conn);
		connMap.put(new TrueAddress(server, serverPort), conn);
		if (!conn.connect()) {
			stop();
			return null;
		}
		sender.start();
		ModelCache.mapTo(receiver);
		ModelCache.mapTo(sender);
		return conn;
	}

	public static DatagramSocket socket() {
		return socket;
	}

	public static void send(DatagramPacket p) throws IOException {
		socket.send(p);
	}

	public void setReceiveTasks(ReceiveTask r) {
		int x = 0;
		do {
			try {
				for (int i = 0; i < connections.size(); i++) {
					connections.get(i).setReceiveTask(r);
				}
				x = -1;
			} catch (Exception e) {
				x++;
				if (x == 10)
					e.printStackTrace(AppFolder.log);
			}
		} while (x > 0 && x < 11);
	}

	// actual work:

	private static GameThread sender;

	private static GameThread receiver;
	static {
		createThreads();
	}

	public static boolean checklogin(String name, String password) {
		// TODO also make sure that you can stop a single account from being logged in
		// 2x at the same time
		return true;
	}

	public static void stop() {
		NetworkManagerRunning = false;
		socket.close();
		connections.clear();
		connMap.clear();
	}

	public static List<NetworkConnection> connections() {
		return connections;
	}

	public static boolean isServer() {
		return isServer;
	}

	public static void createThreads() {
		if (receiver == null || !receiver.isAlive())
			receiver = new GameThread("NetworkManager receiver", 120) {
				private TrueAddress addressKey = new TrueAddress(null, 0);

				@Override
				public void loop() {
					if (!NetworkManagerRunning) {
						this.stopThread();
						return;
					}
					try {
						if (socket.isClosed())
							return;
						socket.receive(r);
						rbuff.resetPos();
						rbuff.setSize(r.getLength());
						NetworkConnection conn = connMap.get(addressKey.set(r.getAddress(), r.getPort()));
						if (conn != null) {
							conn.received(rbuff);
						} else if (rbuff.size() > 0) {
//						AppFolder.log.println("package from new source!");
							int flag = rbuff.read();
							if (flag != -1 && flag != 42) {
								AppFolder.log
										.println("wrong packet received from " + r.getAddress().getCanonicalHostName());
								return;
							} else {
								String identity = rbuff.readString();
								if (identity.equals(connIdentifier)) {
									String name = rbuff.readString(), password = rbuff.readString();
									if (checklogin(name, password)) {
										conn = new NetworkConnection(r.getAddress(), r.getPort(), identity, name,
												password, defaultReceiveTask);
										conn.received(null);
										conn.sendConnectConfirmation();
										// perhaps delete password hash here?
										connections.add(conn);
										connMap.put(new TrueAddress(r.getAddress(), r.getPort()), conn);

										AppFolder.log.println("[" + name + "] is connected to the server! (address "
												+ r.getAddress().getCanonicalHostName() + ", port " + r.getPort()
												+ ")");

										onConnection.action(conn);

									} else {
										AppFolder.log
												.println("unsuccessful login. [" + name + "] +pwd '" + password + "'");
									}
								} else {
									AppFolder.log.println("packet with wrong identity string received from "
											+ r.getAddress().getCanonicalHostName());
								}
							}
						}
					} catch (SocketTimeoutException ste) {

					} catch (Exception e) {
						e.printStackTrace(AppFolder.log);
					}
				}
			};
		if (sender == null || !sender.isAlive())
			sender = new GameThread("NetworkManager sender", 120) {

				@Override
				public void loop() {
					if (!NetworkManagerRunning) {
						this.stopThread();
						return;
					}
					try {
						for (int i = 0; i < connections.size(); i++) {
							if (!connections.get(i).hasConnection()) {
								NetworkConnection n = connections.remove(i);
								connMap.remove(n.dest);
								AppFolder.log.println("[" + n.login + "] lost connection!");
							} else {
								connections.get(i).update();
							}
						}
					} catch (Exception e) {
						e.printStackTrace(AppFolder.log);
					}
				}
			};
	}

}
