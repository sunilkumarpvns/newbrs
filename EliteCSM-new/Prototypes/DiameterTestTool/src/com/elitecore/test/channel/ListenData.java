package com.elitecore.test.channel;

import javax.xml.bind.annotation.XmlAttribute;

public class ListenData {
	private String ipAddress = "127.0.0.1";
	private int port = 3868;
	
	@XmlAttribute(name="ip")
	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	@XmlAttribute(name="port", required=true)
	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
}
