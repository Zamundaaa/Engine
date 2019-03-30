package networking;

import java.net.InetAddress;

public class TrueAddress {

	protected InetAddress ip;
	protected int port;

	public TrueAddress(InetAddress ip, int port) {
		this.ip = ip;
		this.port = port;
	}

	public InetAddress ip() {
		return ip;
	}

	public int port() {
		return port;
	}

	public TrueAddress set(InetAddress ip, int port) {
		this.ip = ip;
		this.port = port;
		return this;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof TrueAddress) {
			TrueAddress other = (TrueAddress) o;
			return other.ip.equals(this.ip) && other.port == this.port;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return port * ip.hashCode();
	}

}
