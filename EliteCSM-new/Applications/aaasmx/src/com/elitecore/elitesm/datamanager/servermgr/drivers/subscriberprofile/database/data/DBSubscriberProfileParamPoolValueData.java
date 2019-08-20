package com.elitecore.elitesm.datamanager.servermgr.drivers.subscriberprofile.database.data;

import com.elitecore.elitesm.datamanager.core.base.data.BaseData;

public class DBSubscriberProfileParamPoolValueData extends BaseData implements IDBSubscriberProfileParamPoolValueData {

	private String paramPoolId;
	private String name;
	private String value;
	private long appOrder;
	private String fieldId;
	private String SerialNumber;
	
	public String getSerialNumber() {
		return SerialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		SerialNumber = serialNumber;
	}
	
	public String getFieldId() {
		return fieldId;
	}
	public void setFieldId(String fieldId) {
		this.fieldId = fieldId;
	}
	public long getAppOrder() {
		return appOrder;
	}
	public void setAppOrder(long appOrder) {
		this.appOrder = appOrder;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public String getParamPoolId() {
		return paramPoolId;
	}
	public void setParamPoolId(String paramPoolId) {
		this.paramPoolId = paramPoolId;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	
}
