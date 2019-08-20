package com.elitecore.elitesm.datamanager.rm.concurrentloginpolicy.data;

public interface IConcurrentLoginPolicyDetailData {
	
	public String getConcurrentLoginId();
	public void setConcurrentLoginId(String concurrentLoginId);
	
	public Integer getLogin();
	public void setLogin(Integer login);
	
	public String getAttributeValue();
	public void setAttributeValue(String attributeValue);
	
	public int getSerialNumber();
	public void setSerialNumber(int serialNumber);
	
	Integer getOrderNumber();
	void setOrderNumber(Integer orderNumber);
}
