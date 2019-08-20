package com.elitecore.elitesm.web.servermgr.service.forms;

import com.elitecore.elitesm.web.radius.base.forms.BaseDictionaryForm;

public class UpdateNetServiceSynchronizeConfigDetailForm extends BaseDictionaryForm{
	private Long netServerId;
	private String netServiceId;
	private String adminInterfaceIP;
	private String adminInterfacePort;
	private String action;
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getAdminInterfaceIP() {
		return adminInterfaceIP;
	}
	public void setAdminInterfaceIP(String adminInterfaceIP) {
		this.adminInterfaceIP = adminInterfaceIP;
	}
	public String getAdminInterfacePort() {
		return adminInterfacePort;
	}
	public void setAdminInterfacePort(String adminInterfacePort) {
		this.adminInterfacePort = adminInterfacePort;
	}
	public Long getNetServerId() {
		return netServerId;
	}
	public void setNetServerId(Long netServerId) {
		this.netServerId = netServerId;
	}
	public String getNetServiceId() {
		return netServiceId;
	}
	public void setNetServiceId(String netServiceId) {
		this.netServiceId = netServiceId;
	}

}
