package com.elitecore.elitesm.datamanager.sessionmanager.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SessionManagerDBConfiguration {

	public void setGroupNameField(String groupNameField) {
		this.groupNameField = groupNameField;
	}

	public void setNasPortTypeField(String nasPortTypeField) {
		this.nasPortTypeField = nasPortTypeField;
	}

	private String connectionUrl;
	private String userName;
	private String password;
	private String tableName="TBLMCONCURRENTUSERS";
	private String sequenceName="SEQ_TBLMCONCURRENTUSERS";
	
	//fields
	
	private String identityField  		="CONCUSERID";
	private String sessionIdField 		="ACCT_SESSION_ID";
	private String startTimeField 		="START_TIME";
	private String lastUpdateTimeField	="LAST_UPDATED_TIME";
	private String userNameField		="USER_NAME";
	private String nasIpAddressField	="NAS_IP_ADDRESS";
	private String framedIpAddressField	="FRAMED_IP_ADDRESS";
	private String groupNameField		="GROUPNAME";
	private String nasPortTypeField		="NAS_PORT_TYPE";
	private String userIdentityField	="USER_IDENTITY";
	private String sessionCloseRequestField  = "SESSION_CLOSE_REQUEST";
	private String concurrentUserId     ="CONCUSERID";
	private String protocolType = "PROTOCOLTYPE";
	
	private List<SMDBFieldMapData> fieldMappingList = new ArrayList<SMDBFieldMapData>();
	
	public String getConnectionUrl() {
		return connectionUrl;
	}
	
	public void setConnectionUrl(String connectionUrl) {
		this.connectionUrl = connectionUrl;
	}
	
	public String getUserName() {
		return userName;
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getConcurrentUserId() {
		return concurrentUserId;
	}

	public void setConcurrentUserId(String concurrentUserId) {
		this.concurrentUserId = concurrentUserId;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
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

	public String getUserNameField() {
		return userNameField;
	}

	public String getNasIpAddressField() {
		return nasIpAddressField;
	}

	public String getFramedIpAddressField() {
		return framedIpAddressField;
	}

	public String getGroupNameField() {
		return groupNameField;
	}

	public String getNasPortTypeField() {
		return nasPortTypeField;
	}

	public String getUserIdentityField() {
		return userIdentityField;
	}

	public String getSessionCloseRequestField() {
		return sessionCloseRequestField;
	}

	public List getFieldMappingList() {
		return fieldMappingList;
	}

	public void setFieldMappingList(List fieldMappingList) {
		this.fieldMappingList = fieldMappingList;
	}

	public String getProtocolType() {
		return protocolType;
	}

	public void setProtocolType(String protocolType) {
		this.protocolType = protocolType;
	}
	
}
