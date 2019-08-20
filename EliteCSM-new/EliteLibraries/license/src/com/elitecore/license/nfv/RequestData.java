package com.elitecore.license.nfv;

public class RequestData {

	private String serverName;
	private String serverId;
	private String data;
	private String version;
	
	public RequestData(String serverName, String serverId, String publicKey, String version) {
		this.serverName = serverName;
		this.serverId = serverId;
		this.data = publicKey;
		this.version = version;
	}
	
	public String getServerName() {
		return serverName;
	}

	public String getServerId() {
		return serverId;
	}
	
	public String getData() {
		return data;
	}

	public String getVersion() {
		return version;
	}
}
