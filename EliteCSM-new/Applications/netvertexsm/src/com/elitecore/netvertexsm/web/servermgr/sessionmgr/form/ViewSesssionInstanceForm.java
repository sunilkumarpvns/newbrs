package com.elitecore.netvertexsm.web.servermgr.sessionmgr.form;

import java.util.List;

import org.apache.struts.action.ActionForm;

import com.elitecore.netvertexsm.datamanager.datasource.database.data.DatabaseDSData;

public class ViewSesssionInstanceForm extends ActionForm {
	
//	Gx Session instance properties	
	private long gxSMInstanceId;
	private String name;
	private String description;
	private String status;
	private Long datasourceId;
	private String gxIdentityFieldName;
	private String gxSequenceName;
	private String gxStartTimeField;
	private String gxLastUpdateTimeField; 
	private String sessionIdFieldName;
	private String gxSessionIdReferringAttr;
	private String isBearerSessionEnabled;
	
//	Gx Session Field Map	
	private String gxDBFieldName;
	private String gxReferringAttr;
	private int gxDatatype;

//	Bearer Session Conf  
	private long bearerSessionId;
	private String identityFieldName;
	private String sequenceName;
	private String startTimeField;
	private String lastUpdateTimeField;
	private String sessionIdField;
	private String sessionIdReferringAttr;
	private String coreRelationField;
	
//	Bearer Session Field Map
	private String dbFieldName;
	private String datatype;
	private String referringAttr;
	
	List<DatabaseDSData> databaseDsList;
	
	public long getBearerSessionId() {
		return bearerSessionId;
	}
	public void setBearerSessionId(long bearerSessionId) {
		this.bearerSessionId = bearerSessionId;
	}
	public long getGxSMInstanceId() {
		return gxSMInstanceId;
	}
	public void setGxSMInstanceId(long gxSMInstanceId) {
		this.gxSMInstanceId = gxSMInstanceId;
	}
	public String getGxDBFieldName() {
		return gxDBFieldName;
	}
	public void setGxDBFieldName(String gxDBFieldName) {
		this.gxDBFieldName = gxDBFieldName;
	}
	public String getGxReferringAttr() {
		return gxReferringAttr;
	}
	public void setGxReferringAttr(String gxReferringAttr) {
		this.gxReferringAttr = gxReferringAttr;
	}
	public int getGxDatatype() {
		return gxDatatype;
	}
	public void setGxDatatype(int gxDatatype) {
		this.gxDatatype = gxDatatype;
	}
	public String getDbFieldName() {
		return dbFieldName;
	}
	public void setDbFieldName(String dbFieldName) {
		this.dbFieldName = dbFieldName;
	}
	public String getDatatype() {
		return datatype;
	}
	public void setDatatype(String datatype) {
		this.datatype = datatype;
	}
	public String getReferringAttr() {
		return referringAttr;
	}
	public void setReferringAttr(String referringAttr) {
		this.referringAttr = referringAttr;
	}
	public String getSessionIdFieldName() {
		return sessionIdFieldName;
	}
	public void setSessionIdFieldName(String sessionIdFieldName) {
		this.sessionIdFieldName = sessionIdFieldName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public List<DatabaseDSData> getDatabaseDsList() {
		return databaseDsList;
	}
	public void setDatabaseDsList(List<DatabaseDSData> databaseDsList) {
		this.databaseDsList = databaseDsList;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public Long getDatasourceId() {
		return datasourceId;
	}
	public void setDatasourceId(Long datasourceId) {
		this.datasourceId = datasourceId;
	}
	public String getGxIdentityFieldName() {
		return gxIdentityFieldName;
	}
	public void setGxIdentityFieldName(String gxIdentityFieldName) {
		this.gxIdentityFieldName = gxIdentityFieldName;
	}
	public String getGxSequenceName() {
		return gxSequenceName;
	}
	public void setGxSequenceName(String gxSequenceName) {
		this.gxSequenceName = gxSequenceName;
	}
	public String getGxStartTimeField() {
		return gxStartTimeField;
	}
	public void setGxStartTimeField(String gxStartTimeField) {
		this.gxStartTimeField = gxStartTimeField;
	}
	public String getGxLastUpdateTimeField() {
		return gxLastUpdateTimeField;
	}
	public void setGxLastUpdateTimeField(String gxLastUpdateTimeField) {
		this.gxLastUpdateTimeField = gxLastUpdateTimeField;
	}
	public String getGxSessionIdReferringAttr() {
		return gxSessionIdReferringAttr;
	}
	public void setGxSessionIdReferringAttr(String gxSessionIdReferringAttr) {
		this.gxSessionIdReferringAttr = gxSessionIdReferringAttr;
	}
	public String getIsBearerSessionEnabled() {
		return isBearerSessionEnabled;
	}
	public void setIsBearerSessionEnabled(String isBearerSessionEnabled) {
		this.isBearerSessionEnabled = isBearerSessionEnabled;
	}
	public String getIdentityFieldName() {
		return identityFieldName;
	}
	public void setIdentityFieldName(String identityFieldName) {
		this.identityFieldName = identityFieldName;
	}
	public String getSequenceName() {
		return sequenceName;
	}
	public void setSequenceName(String sequenceName) {
		this.sequenceName = sequenceName;
	}
	public String getStartTimeField() {
		return startTimeField;
	}
	public void setStartTimeField(String startTimeField) {
		this.startTimeField = startTimeField;
	}
	public String getLastUpdateTimeField() {
		return lastUpdateTimeField;
	}
	public void setLastUpdateTimeField(String lastUpdateTimeField) {
		this.lastUpdateTimeField = lastUpdateTimeField;
	}
	public String getSessionIdField() {
		return sessionIdField;
	}
	public void setSessionIdField(String sessionIdField) {
		this.sessionIdField = sessionIdField;
	}
	public String getSessionIdReferringAttr() {
		return sessionIdReferringAttr;
	}
	public void setSessionIdReferringAttr(String sessionIdReferringAttr) {
		this.sessionIdReferringAttr = sessionIdReferringAttr;
	}
	public String getCoreRelationField() {
		return coreRelationField;
	}
	public void setCoreRelationField(String coreRelationField) {
		this.coreRelationField = coreRelationField;
	}
}

