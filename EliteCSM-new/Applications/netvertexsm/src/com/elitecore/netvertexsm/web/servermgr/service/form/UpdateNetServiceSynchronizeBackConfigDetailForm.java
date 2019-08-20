package com.elitecore.netvertexsm.web.servermgr.service.form;

import com.elitecore.netvertexsm.web.core.base.forms.BaseWebForm;

public class UpdateNetServiceSynchronizeBackConfigDetailForm extends BaseWebForm{
	private Long netServerId;
	private Long netServiceId;
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
	public Long getNetServiceId() {
		return netServiceId;
	}
	public void setNetServiceId(Long netServiceId) {
		this.netServiceId = netServiceId;
	}

}
