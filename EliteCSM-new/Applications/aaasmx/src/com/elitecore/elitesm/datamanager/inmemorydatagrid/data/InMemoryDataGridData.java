package com.elitecore.elitesm.datamanager.inmemorydatagrid.data;

import java.io.Serializable;

import com.elitecore.elitesm.datamanager.core.base.data.BaseData;

public class InMemoryDataGridData extends BaseData implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	private String imdgID;
	private byte[] imdgXml;
	private String auditUId;

	public String getImdgID() {
		return imdgID;
	}
	public void setImdgID(String imdgID) {
		this.imdgID = imdgID;
	}
	public byte[] getImdgXml() {
		return imdgXml;
	}
	public void setImdgXml(byte[] imdgXml) {
		this.imdgXml = imdgXml;
	}
	public String getAuditUId() {
		return auditUId;
	}
	public void setAuditUId(String auditUId) {
		this.auditUId = auditUId;
	}
}
