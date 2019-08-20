package com.elitecore.elitesm.web.driver.subscriberprofile.database.forms;

import java.util.ArrayList;
import java.util.List;

import com.elitecore.elitesm.datamanager.servermgr.drivers.subscriberprofile.database.data.DBSubscriberProfileParamPoolValueData;
import com.elitecore.elitesm.web.radius.base.forms.BaseDictionaryForm;

public class UpdateValuePoolForm extends BaseDictionaryForm{
	
	private String poolName;
	private String poolValue;
	private String valuePoolAction;
	private int itemIndex;	
	private List dbdsDatasourceParamPoolList = new ArrayList();
	private String[]select;
	private String paramId;
	private String toggleAll;
	private String status;
	private String navigationCode;
	private String queryString;
	
	public String getQueryString() {
		return queryString;
	}
	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}
	public String getNavigationCode() {
		return navigationCode;
	}
	public void setNavigationCode(String navigationCode) {
		this.navigationCode = navigationCode;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getToggleAll() {
		return toggleAll;
	}
	public void setToggleAll(String toggleAll) {
		this.toggleAll = toggleAll;
	}
	public String getParamId() {
		return paramId;
	}
	public void setParamId(String paramId) {
		this.paramId = paramId;
	}
	public int getItemIndex() {
		return itemIndex;
	}
	public void setItemIndex(int itemIndex) {
		this.itemIndex = itemIndex;
	}
	public String getPoolName() {
		return poolName;
	}
	public void setPoolName(String poolName) {
		this.poolName = poolName;
	}
	public String getPoolValue() {
		return poolValue;
	}
	public void setPoolValue(String poolValue) {
		this.poolValue = poolValue;
	}
	public String getValuePoolAction() {
		return valuePoolAction;
	}
	public void setValuePoolAction(String valuePoolAction) {
		this.valuePoolAction = valuePoolAction;
	}
	
	public List getDbdsDatasourceParamPoolList() {
		return dbdsDatasourceParamPoolList;
	}
	public void setDbdsDatasourceParamPoolList(List dbdsDatasourceParamPoolList) {
		this.dbdsDatasourceParamPoolList = dbdsDatasourceParamPoolList;
	}
	public String[] getSelect() {
		return select;
	}
	public void setSelect(String[] select) {
		this.select = select;
	}
	
	public String getName(int index) {
		return ((DBSubscriberProfileParamPoolValueData)dbdsDatasourceParamPoolList.get(index)).getName();
		
	}
	public void setName(int index,String name) {
		while(dbdsDatasourceParamPoolList.size() - 1 < index)
			dbdsDatasourceParamPoolList.add(new DBSubscriberProfileParamPoolValueData());
		((DBSubscriberProfileParamPoolValueData)dbdsDatasourceParamPoolList.get(index)).setName(name);
	}
	
	public String getValue(int index) {
		return ((DBSubscriberProfileParamPoolValueData)dbdsDatasourceParamPoolList.get(index)).getValue();
	}
	public void setValue(int index,String value) {
		while(dbdsDatasourceParamPoolList.size() - 1 < index)
			dbdsDatasourceParamPoolList.add(new DBSubscriberProfileParamPoolValueData());
		((DBSubscriberProfileParamPoolValueData)dbdsDatasourceParamPoolList.get(index)).setValue(value);
	}
	
	public String getSerialNumber(int index) {
		return ((DBSubscriberProfileParamPoolValueData)dbdsDatasourceParamPoolList.get(index)).getSerialNumber();
	}
	public void setSerialNumber(int index,String serialNumber) {
		try{
		while(dbdsDatasourceParamPoolList.size() - 1 < index)
			dbdsDatasourceParamPoolList.add(new DBSubscriberProfileParamPoolValueData());
		((DBSubscriberProfileParamPoolValueData)dbdsDatasourceParamPoolList.get(index)).setSerialNumber(serialNumber);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
		
	
	

}
