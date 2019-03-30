package networking;

import collectionsStuff.SmartByteBuffer;

public class Package {

	private SmartByteBuffer data;
	protected NetworkConnection dest;
	private int ID = Integer.MIN_VALUE;
	private boolean sent;

	public Package(SmartByteBuffer data, NetworkConnection dest) {
		this.data = data;
		this.dest = dest;
	}

	public SmartByteBuffer data() {
		return data;
	}

	public boolean sent() {
		return sent;
	}

	public void setSent() {
		sent = true;
	}

	public int ID() {
		return ID;
	}

	public void setID(int ID) {
		this.ID = ID;
	}

	public NetworkConnection dest() {
		return dest;
	}

}