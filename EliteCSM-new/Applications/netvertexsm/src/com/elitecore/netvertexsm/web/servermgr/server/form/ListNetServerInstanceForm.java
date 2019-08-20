package com.elitecore.netvertexsm.web.servermgr.server.form;

import java.util.List;

import com.elitecore.netvertexsm.web.core.base.forms.BaseWebForm;

public class ListNetServerInstanceForm extends BaseWebForm{
	private String toggleAll;
	private String name;
	private String serverIdentification;
	private String serverType;
	private String description;
	private List listServer;
	private String action;
	private String status;
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
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getServerIdentification() {
		return serverIdentification;
	}
	public void setServerIdentification(String serverIdentification) {
		this.serverIdentification = serverIdentification;
	}
	public String getServerType() {
		return serverType;
	}
	public void setServerType(String serverType) {
		this.serverType = serverType;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	/*public List getStListServer() {
		return stListServer;
	}
	public void setStListServer(List stListServer) {
		this.stListServer = stListServer;
	}*/
	public List getListServer() {
		return listServer;
	}
	public void setListServer(List listServer) {
		this.listServer = listServer;
	}
	public String getToggleAll() {
		return toggleAll;
	}
	public void setToggleAll(String toggleAll) {
		this.toggleAll = toggleAll;
	}
}
