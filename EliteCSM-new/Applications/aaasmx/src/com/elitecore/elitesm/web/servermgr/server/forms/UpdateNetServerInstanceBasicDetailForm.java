package com.elitecore.elitesm.web.servermgr.server.forms;

import java.util.List;

import com.elitecore.elitesm.datamanager.core.system.staff.data.StaffData;
import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class UpdateNetServerInstanceBasicDetailForm extends BaseWebForm{
	private String netServerId;
	private String name;
	private String netServerType;
	private String description;
	private String action;
	private String javaHome;
	private String serverHome;
	private List<StaffData> staffDataList;
	private String staff;
	private String instanceStaffRelId;
	private boolean alive;
	
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
	public String getNetServerId() {
		return netServerId;
	}
	public void setNetServerId(String netServerId) {
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

	public List<StaffData> getStaffDataList() {
		return staffDataList;
	}

	public void setStaffDataList(List<StaffData> staffDataList) {
		this.staffDataList = staffDataList;
	}

	public String getStaff() {
		return staff;
	}

	public void setStaff(String staff) {
		this.staff = staff;
	}

	public String getInstanceStaffRelId() {
		return instanceStaffRelId;
	}

	public void setInstanceStaffRelId(String instanceStaffRelId) {
		this.instanceStaffRelId = instanceStaffRelId;
	}

	public boolean isAlive() {
		return alive;
	}

	public void setAlive(boolean alive) {
		this.alive = alive;
	}

	

	
}
