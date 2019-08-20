package com.elitecore.elitesm.datamanager.radius.clientprofile.data;

import java.io.Serializable;

import com.elitecore.elitesm.datamanager.core.base.data.BaseData;

public class ClientTypeData extends BaseData implements Serializable{

	private static final long serialVersionUID = 1L;
	private long clientTypeId;
	private String clientTypeName;
	private String status;
	
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public long getClientTypeId() {
		return clientTypeId;
	}
	public void setClientTypeId(long clientTypeId) {
		this.clientTypeId = clientTypeId;
	}
	public String getClientTypeName() {
		return clientTypeName;
	}
	public void setClientTypeName(String clientTypeName) {
		this.clientTypeName = clientTypeName;
	}
	

}
