package com.elitecore.elitesm.web.diameter.prioritytable.form;

import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class PriorityTableForm extends BaseWebForm{
	/**
	 * @author ishani.bhatt
	 */
	private static final long serialVersionUID = 1L;
	private long priorityTableId;
	private String applicationId;
	private String commnadCode;
	private String ipAddress;
	private int diameterSession;
	private int priority;
	private String action;
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public long getPriorityTableId() {
		return priorityTableId;
	}
	public void setPriorityTableId(long priorityTableId) {
		this.priorityTableId = priorityTableId;
	}
	public String getApplicationId() {
		return applicationId;
	}
	public void setApplicationId(String applicationId) {
		this.applicationId = applicationId;
	}
	public String getCommnadCode() {
		return commnadCode;
	}
	public void setCommnadCode(String commnadCode) {
		this.commnadCode = commnadCode;
	}
	public String getIpAddress() {
		return ipAddress;
	}
	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}
	public int getDiameterSession() {
		return diameterSession;
	}
	public void setDiameterSession(int diameterSession) {
		this.diameterSession = diameterSession;
	}
	public int getPriority() {
		return priority;
	}
	public void setPriority(int priority) {
		this.priority = priority;
	}
}
