package com.elitecore.netvertexsm.web.servermgr.sessionmgr.form;

import java.util.List;

import org.apache.struts.action.ActionForm;

import com.elitecore.netvertexsm.datamanager.datasource.database.data.DatabaseDSData;

public class CreateSessionInstanceForm extends ActionForm {
	
//	Gx Session instance properties	
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
	private String userIdentityField;
	private String userIdentityReferringAttr;
	private String gatewayURLField;
	private String gatewayURLReferringAttr;
	private String isBearerSessionEnabled;
	private String isRxSessionEnabled;
	
//	Gx Session Field Map	
	private String gxDBFieldName;
	private String gxReferringAttr;
	private int gxDatatype;

//	Bearer Session Conf  
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
	
//	Rx Session Instance Conf
	private String rxIdentityFieldName;
	private String identityReferringAttr;
	private String tableName;
	private String seqName;
	private String rxStartTimeField;
	private String rxLastUpdateTimeField;
	private String tablePrimaryKeyField;
    private String mediaTypeField;
    private String mediaTypeReferringAttr;
    private String umbrFieldName;
    private String umbrReferringAttr;
    private String dmbrFieldName;
    private String dmbrReferringAttr;
    private String afAppIdFieldName;
    private String afAppIdReferringAttr;
    private String subscriptionIdFieldName;
    private String subscriptionIdReferringAttr;
    private String gxCoRelationFieldName;
    private String gxCoRelationAttr;
    private String framedIpAddrFieldName;
    private String framedIpAddrAttr;
	
	List<DatabaseDSData> databaseDsList;
	
	
	public String getGatewayURLField() {
		return gatewayURLField;
	}
	public void setGatewayURLField(String gatewayURLField) {
		this.gatewayURLField = gatewayURLField;
	}
	public String getGatewayURLReferringAttr() {
		return gatewayURLReferringAttr;
	}
	public void setGatewayURLReferringAttr(String gatewayURLReferringAttr) {
		this.gatewayURLReferringAttr = gatewayURLReferringAttr;
	}
	public String getUserIdentityField() {
		return userIdentityField;
	}
	public void setUserIdentityField(String userIdentityField) {
		this.userIdentityField = userIdentityField;
	}
	public String getUserIdentityReferringAttr() {
		return userIdentityReferringAttr;
	}
	public void setUserIdentityReferringAttr(String userIdentityReferringAttr) {
		this.userIdentityReferringAttr = userIdentityReferringAttr;
	}
	public String getIsRxSessionEnabled() {
		return isRxSessionEnabled;
	}
	public void setIsRxSessionEnabled(String isRxSessionEnabled) {
		this.isRxSessionEnabled = isRxSessionEnabled;
	}
	public String getRxIdentityFieldName() {
		return rxIdentityFieldName;
	}
	public void setRxIdentityFieldName(String rxIdentityFieldName) {
		this.rxIdentityFieldName = rxIdentityFieldName;
	}
	public String getIdentityReferringAttr() {
		return identityReferringAttr;
	}
	public void setIdentityReferringAttr(String identityReferringAttr) {
		this.identityReferringAttr = identityReferringAttr;
	}
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	public String getSeqName() {
		return seqName;
	}
	public void setSeqName(String seqName) {
		this.seqName = seqName;
	}
	public String getRxStartTimeField() {
		return rxStartTimeField;
	}
	public void setRxStartTimeField(String rxStartTimeField) {
		this.rxStartTimeField = rxStartTimeField;
	}
	public String getRxLastUpdateTimeField() {
		return rxLastUpdateTimeField;
	}
	public void setRxLastUpdateTimeField(String rxLastUpdateTimeField) {
		this.rxLastUpdateTimeField = rxLastUpdateTimeField;
	}
	public String getMediaTypeField() {
		return mediaTypeField;
	}
	public void setMediaTypeField(String mediaTypeField) {
		this.mediaTypeField = mediaTypeField;
	}
	public String getTablePrimaryKeyField() {
		return tablePrimaryKeyField;
	}
	public void setTablePrimaryKeyField(String tablePrimaryKeyField) {
		this.tablePrimaryKeyField = tablePrimaryKeyField;
	}
	public String getMediaTypeReferringAttr() {
		return mediaTypeReferringAttr;
	}
	public void setMediaTypeReferringAttr(String mediaTypeReferringAttr) {
		this.mediaTypeReferringAttr = mediaTypeReferringAttr;
	}
	public String getUmbrFieldName() {
		return umbrFieldName;
	}
	public void setUmbrFieldName(String umbrFieldName) {
		this.umbrFieldName = umbrFieldName;
	}
	public String getUmbrReferringAttr() {
		return umbrReferringAttr;
	}
	public void setUmbrReferringAttr(String umbrReferringAttr) {
		this.umbrReferringAttr = umbrReferringAttr;
	}
	public String getDmbrFieldName() {
		return dmbrFieldName;
	}
	public void setDmbrFieldName(String dmbrFieldName) {
		this.dmbrFieldName = dmbrFieldName;
	}
	public String getDmbrReferringAttr() {
		return dmbrReferringAttr;
	}
	public void setDmbrReferringAttr(String dmbrReferringAttr) {
		this.dmbrReferringAttr = dmbrReferringAttr;
	}
	public String getAfAppIdFieldName() {
		return afAppIdFieldName;
	}
	public void setAfAppIdFieldName(String afAppIdFieldName) {
		this.afAppIdFieldName = afAppIdFieldName;
	}
	public String getAfAppIdReferringAttr() {
		return afAppIdReferringAttr;
	}
	public void setAfAppIdReferringAttr(String afAppIdReferringAttr) {
		this.afAppIdReferringAttr = afAppIdReferringAttr;
	}
	public String getSubscriptionIdFieldName() {
		return subscriptionIdFieldName;
	}
	public void setSubscriptionIdFieldName(String subscriptionIdFieldName) {
		this.subscriptionIdFieldName = subscriptionIdFieldName;
	}
	public String getSubscriptionIdReferringAttr() {
		return subscriptionIdReferringAttr;
	}
	public void setSubscriptionIdReferringAttr(String subscriptionIdReferringAttr) {
		this.subscriptionIdReferringAttr = subscriptionIdReferringAttr;
	}
	public String getGxCoRelationFieldName() {
		return gxCoRelationFieldName;
	}
	public void setGxCoRelationFieldName(String gxCoRelationFieldName) {
		this.gxCoRelationFieldName = gxCoRelationFieldName;
	}
	public String getGxCoRelationAttr() {
		return gxCoRelationAttr;
	}
	public void setGxCoRelationAttr(String gxCoRelationAttr) {
		this.gxCoRelationAttr = gxCoRelationAttr;
	}
	public String getFramedIpAddrFieldName() {
		return framedIpAddrFieldName;
	}
	public void setFramedIpAddrFieldName(String framedIpAddrFieldName) {
		this.framedIpAddrFieldName = framedIpAddrFieldName;
	}
	public String getFramedIpAddrAttr() {
		return framedIpAddrAttr;
	}
	public void setFramedIpAddrAttr(String framedIpAddrAttr) {
		this.framedIpAddrAttr = framedIpAddrAttr;
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
