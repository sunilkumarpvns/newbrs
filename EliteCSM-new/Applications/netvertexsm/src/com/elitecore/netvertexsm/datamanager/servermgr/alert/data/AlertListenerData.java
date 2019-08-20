package com.elitecore.netvertexsm.datamanager.servermgr.alert.data;

import java.util.HashSet;

import com.elitecore.netvertexsm.web.core.base.BaseData;

public class AlertListenerData extends BaseData{
	private long listenerId;
	private String name;
	private String typeId;
	private AlertFileListenerData alertFileListenerData;
	private AlertTrapListenerData alertTrapListenerData;
	private AlertListenerTypeData alertListenerTypeData;
	private HashSet<AlertListenerRelData> alertListenerRelDataSet;
	private BaseAlertListener alertListener;
	public HashSet<AlertListenerRelData> getAlertListenerRelDataSet() {
		return alertListenerRelDataSet;
	}
	public void setAlertListenerRelDataSet(
			HashSet<AlertListenerRelData> alertListenerRelDataSet) {
		this.alertListenerRelDataSet = alertListenerRelDataSet;
	}
	public long getListenerId() {
		return listenerId;
	}
	public void setListenerId(long listenerId) {
		this.listenerId = listenerId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTypeId() {
		return typeId;
	}
	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}
	public AlertFileListenerData getAlertFileListenerData() {
		return alertFileListenerData;
	}
	public void setAlertFileListenerData(AlertFileListenerData alertFileListenerData) {
		this.alertFileListenerData = alertFileListenerData;
	}
	public AlertTrapListenerData getAlertTrapListenerData() {
		return alertTrapListenerData;
	}
	public void setAlertTrapListenerData(AlertTrapListenerData alertTrapListenerData) {
		this.alertTrapListenerData = alertTrapListenerData;
	}
	public AlertListenerTypeData getAlertListenerTypeData() {
		return alertListenerTypeData;
	}
	public void setAlertListenerTypeData(AlertListenerTypeData alertListenerTypeData) {
		this.alertListenerTypeData = alertListenerTypeData;
	}
	public BaseAlertListener getAlertListener() {
		return alertListener;
	}
	public void setAlertListener(BaseAlertListener alertListener) {
		this.alertListener = alertListener;
	}	 
}
