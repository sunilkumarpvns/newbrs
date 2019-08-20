package com.elitecore.netvertexsm.datamanager.servermgr.sessionmgr.data;


public class SessionManagerDBConfiguration {

	private String connectionUrl;
	private String dbUserName;
	private String dbPassword;
	private String tableName="TBLCORESESS";
	private String bearerSessionTableName="TBLBEARERSESS";
	
	private String sequenceName="SEQ_CORESESS";
	
	//fields
	
	private String identityField  		="CID";
	private String sessionIdField 		="SESSIONID";
	private String startTimeField 		="CREATETIME";
	private String lastUpdateTimeField	="LASTUPDATETIME";
	private String subscriptionIdField		="SUBSCRIPTIONID";
	private String framedIpAddressField	="FRAMEDIPADDRESS";

	private String locationField	="LOCATION";
	private String gatewayUrlField="GATEWAYURL";
	private String userEquipmentField="USEREQUIPMENT";
	private String coreRelationField= "SESSIONID";

	
	public String getConnectionUrl() {
		return connectionUrl;
	}
	
	public void setConnectionUrl(String connectionUrl) {
		this.connectionUrl = connectionUrl;
	}
	
	public String getDbUserName() {
		return dbUserName;
	}
	
	public void setDbUserName(String dbUserName) {
		this.dbUserName = dbUserName;
	}
	
	public String getDbPassword() {
		return dbPassword;
	}

	public void setDbPassword(String dbPassword) {
		this.dbPassword = dbPassword;
	}
	
	public String getTableName() {
		return tableName;
	}
	
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getSequenceName() {
		return sequenceName;
	}
	
	public void setSequenceName(String sequenceName) {
		this.sequenceName = sequenceName;
	}

	public String getIdentityField() {
		return identityField;
	}

	public void setIdentityField(String identityField) {
		this.identityField = identityField;
	}

	public String getSessionIdField() {
		return sessionIdField;
	}

	public void setSessionIdField(String sessionIdField) {
		this.sessionIdField = sessionIdField;
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

	
	public void setFramedIpAddressField(String framedIpAddressField) {
		this.framedIpAddressField = framedIpAddressField;
	}

	
	public String getFramedIpAddressField() {
		return framedIpAddressField;
	}

	public String getSubscriptionIdField() {
		return subscriptionIdField;
	}

	public void setSubscriptionIdField(String subscriptionIdField) {
		this.subscriptionIdField = subscriptionIdField;
	}

	public String getLocationField() {
		return locationField;
	}

	public void setLocationField(String locationField) {
		this.locationField = locationField;
	}

	public String getGatewayUrlField() {
		return gatewayUrlField;
	}

	public void setGatewayUrlField(String gatewayUrlField) {
		this.gatewayUrlField = gatewayUrlField;
	}

	public String getUserEquipmentField() {
		return userEquipmentField;
	}

	public void setUserEquipmentField(String userEquipmentField) {
		this.userEquipmentField = userEquipmentField;
	}

	public String getBearerSessionTableName() {
		return bearerSessionTableName;
	}

	public void setBearerSessionTableName(String bearerSessionTableName) {
		this.bearerSessionTableName = bearerSessionTableName;
	}

	public String getCoreRelationField() {
		return coreRelationField;
	}

	public void setCoreRelationField(String coreRelationField) {
		this.coreRelationField = coreRelationField;
	}
	
	
}
