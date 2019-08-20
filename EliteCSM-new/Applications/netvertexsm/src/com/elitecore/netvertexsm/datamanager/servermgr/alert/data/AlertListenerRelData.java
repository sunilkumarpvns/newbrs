package com.elitecore.netvertexsm.datamanager.servermgr.alert.data;

import java.io.Serializable;

public class AlertListenerRelData implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private long instanceId;
	private String typeId;
	private String floodControl;

	private AlertListenerData alertListenerData;
	
	public long getInstanceId() {
		return instanceId;
	}
	
	public void setInstanceId(long instanceId) {
		this.instanceId = instanceId;
	}
	
	public String getTypeId() {
		return typeId;
	}
	
	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}
	
	public AlertListenerData getAlertListenerData() {
		return alertListenerData;
	}
	
	public void setAlertListenerData(AlertListenerData alertListenerData) {
		this.alertListenerData = alertListenerData;
	}

	public String getFloodControl() {
		return floodControl;
	}

	public void setFloodControl(String floodControl) {
		this.floodControl = floodControl;
	}


}
