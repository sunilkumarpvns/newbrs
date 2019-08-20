package com.elitecore.elitesm.web.servicepolicy.radiusservicepolicy;

public class ServerGroup {
	private Long serverGroupId;
	private Long serverId;
	private Long loadFactor;
	
	public Long getServerGroupId() {
		return serverGroupId;
	}
	public void setServerGroupId(Long serverGroupId) {
		this.serverGroupId = serverGroupId;
	}
	public Long getServerId() {
		return serverId;
	}
	public void setServerId(Long serverId) {
		this.serverId = serverId;
	}
	public Long getLoadFactor() {
		return loadFactor;
	}
	public void setLoadFactor(Long loadFactor) {
		this.loadFactor = loadFactor;
	}
}
