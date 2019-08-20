package com.elitecore.elitesm.datamanager.servermgr.drivers.subscriberprofile.database.data;

public interface IDBSubscriberProfileParamPoolValueData {

	public long getAppOrder(); 
	public void setAppOrder(long appOrder);
	
	public String getName();
	public void setName(String name);
	
	public String getParamPoolId();
	public void setParamPoolId(String paramPoolId);
	
	public String getValue();
	public void setValue(String value);
	
	public String getSerialNumber();
	public void setSerialNumber(String serialNumber);
	
	public String getFieldId();
	public void setFieldId(String fieldId);
}
