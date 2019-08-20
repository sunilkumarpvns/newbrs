package com.elitecore.elitesm.datamanager.servermgr.drivers.subscriberprofile.database.data;

import com.elitecore.elitesm.datamanager.core.base.data.BaseData;

public class DataTypeData extends BaseData implements IDataTypeData {
	
	private String dataTypeId;
	private String dataTypeName;
	
	public String getDataTypeId() {
		return dataTypeId;
	}
	public void setDataTypeId(String dataTypeId) {
		this.dataTypeId = dataTypeId;
	}
	public String getDataTypeName() {
		return dataTypeName;
	}
	public void setDataTypeName(String dataTypeName) {
		this.dataTypeName = dataTypeName;
	}

}
