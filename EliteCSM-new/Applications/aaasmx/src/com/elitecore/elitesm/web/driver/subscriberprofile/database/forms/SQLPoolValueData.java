package com.elitecore.elitesm.web.driver.subscriberprofile.database.forms;

import com.elitecore.elitesm.datamanager.core.base.data.BaseData;

public class SQLPoolValueData extends BaseData{
	
	String name;
	String poolValue;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPoolValue() {
		return poolValue;
	}
	public void setPoolValue(String poolValue) {
		this.poolValue = poolValue;
	}

}
