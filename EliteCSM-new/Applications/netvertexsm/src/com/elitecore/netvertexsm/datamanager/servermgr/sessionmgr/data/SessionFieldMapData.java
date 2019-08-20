package com.elitecore.netvertexsm.datamanager.servermgr.sessionmgr.data;

public class SessionFieldMapData {
	
	private int coreSessionID;
	private int sessionConfID;
	private String type;
	private int datatype;
	private String fieldName;
	private String referringAttr;
	private String dataTypeName;
	
	public String getDataTypeName() {
		return dataTypeName;
	}
	public void setDataTypeName(String dataTypeName) {
		this.dataTypeName = dataTypeName;
	}
	
	  
	public int getSessionConfID() {
		return sessionConfID;
	}
	public void setSessionConfID(int sessionConfID) {
		this.sessionConfID = sessionConfID;
	}
	public int getCoreSessionID() {
		return coreSessionID;
	}
	public void setCoreSessionID(int coreSessionID) {
		this.coreSessionID = coreSessionID;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	      

	public int getDatatype() {
		return datatype;
	}
	public void setDatatype(int datatype) {
		this.datatype = datatype;
	}
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public String getReferringAttr() {
		return referringAttr;
	}
	public void setReferringAttr(String referringAttr) {
		this.referringAttr = referringAttr;
	}
	
}
