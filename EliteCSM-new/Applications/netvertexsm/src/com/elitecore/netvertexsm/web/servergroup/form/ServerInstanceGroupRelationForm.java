package com.elitecore.netvertexsm.web.servergroup.form;


public class ServerInstanceGroupRelationForm {

	private String serverWeightage;
	private String serverName;
	private String description;
	private String adminHost;
	private Integer adminPort;
	private String netServerId;
	private String serverGroupId;

	public String getServerWeightage() {
		return serverWeightage;
	}

	public void setServerWeightage(String serverWeightage) {
		this.serverWeightage = serverWeightage;
	}

	public String getServerName() {
		return serverName;
	}

	public void setServerName(String serverName) {
		this.serverName = serverName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getAdminHost() {
		return adminHost;
	}

	public void setAdminHost(String adminHost) {
		this.adminHost = adminHost;
	}

	public Integer getAdminPort() {
		return adminPort;
	}

	public void setAdminPort(Integer adminPort) {
		this.adminPort = adminPort;
	}

	public String getNetServerId() {
		return netServerId;
	}

	public void setNetServerId(String netServerId) {
		this.netServerId = netServerId;
	}

	public String getServerGroupId() {
		return serverGroupId;
	}

	public void setServerGroupId(String serverGroupId) {
		this.serverGroupId = serverGroupId;
	}

}
