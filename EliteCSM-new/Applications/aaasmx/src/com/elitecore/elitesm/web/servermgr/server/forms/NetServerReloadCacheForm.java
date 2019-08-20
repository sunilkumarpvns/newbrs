package com.elitecore.elitesm.web.servermgr.server.forms;

import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class NetServerReloadCacheForm extends BaseWebForm{
	
	private String netServerId;
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
	public String getNetServerId() {
		return netServerId;
	}
	public void setNetServerId(String netServerId) {
		this.netServerId = netServerId;
	}

}

