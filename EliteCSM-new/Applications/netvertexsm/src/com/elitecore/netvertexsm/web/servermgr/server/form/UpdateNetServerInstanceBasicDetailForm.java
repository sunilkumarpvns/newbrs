package com.elitecore.netvertexsm.web.servermgr.server.form;

import com.elitecore.netvertexsm.web.core.base.forms.BaseWebForm;

public class UpdateNetServerInstanceBasicDetailForm extends BaseWebForm{
	private Long netServerId;
	private String name;
	private String netServerType;
	private String description;
	private String action;
	private String javaHome;
	private String serverHome;

	public String getServerHome() {
		return serverHome;
	}

	public void setServerHome( String serverHome ) {
		this.serverHome = serverHome;
	}

	public String getJavaHome() {
		return javaHome;
	}

	public void setJavaHome( String javaHome ) {
		this.javaHome = javaHome;
	}

	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Long getNetServerId() {
		return netServerId;
	}
	public void setNetServerId(Long netServerId) {
		this.netServerId = netServerId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getNetServerType() {
		return netServerType;
	}
	public void setNetServerType(String netServerType) {
		this.netServerType = netServerType;
	}

}
