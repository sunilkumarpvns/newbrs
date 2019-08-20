package com.elitecore.corenetvertex.data;

import java.io.Serializable;

public class GatewayInfo  implements Serializable {
	
	private static final long serialVersionUID = -1803725634958826185L;

	private long activeSessions;
	private String gatewayType;
	private String location;
	private String areaName;
	private String connectionURL;
	private String status;
	
	public GatewayInfo(String gatewayType, String location, String areaName, String connectionURL, String status, long activeSessions) {
		this.gatewayType = gatewayType;
		this.location = location;
		this.areaName = areaName;
		this.connectionURL = connectionURL;
		this.status = status;
		this.activeSessions = activeSessions;
	}

	public long getActiveSessions() {
		return activeSessions;
	}
	
	public String getLocation() {
		return location;
	}

	public String getGatewayType() {
		return gatewayType;
	}

	public String getAreaName() {
		return areaName;
	}

	public String getConnectionURL() {
		return connectionURL;
	}

	public String getStatus() {
		return status;
	}	

}
