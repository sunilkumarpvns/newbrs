package com.elitecore.core.util.url;

import com.elitecore.commons.base.Equality;

public class SocketDetail {
	
	private final String ipAddress;
	private final int port;
	
	public SocketDetail(String ipAddress, int port) {
		this.ipAddress = ipAddress;
		this.port = port;
	}
	
	@Override
	public int hashCode() {
		return ipAddress.hashCode() + port;
	}
	
	public String getIPAddress() {
		return ipAddress;
	}
	
	public int getPort() {
		return port;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if ((obj instanceof SocketDetail) == false) {
			return false;
		}
		SocketDetail that = (SocketDetail) obj;
		return Equality.areEqual(this.ipAddress, that.ipAddress)
				&& Equality.areEqual(this.port, that.port);
	}
	
	@Override
	public String toString() {
		if (ipAddress.contains(":")) {
			return ("["+ipAddress+"]"+ ":" + port);
		}
		return (ipAddress + ":" + port);
	}
}
