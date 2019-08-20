package com.elitecore.netvertexsm.web.servermgr.server.form;

import com.elitecore.netvertexsm.web.core.base.forms.BaseWebForm;

public class NetServerReloadPolicyForm extends BaseWebForm{
	
	private long netServerId;
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
	public long getNetServerId() {
		return netServerId;
	}
	public void setNetServerId(long netServerId) {
		this.netServerId = netServerId;
	}

}

