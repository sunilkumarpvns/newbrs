package com.elitecore.elitesm.web.dashboard.widget.model.radacctclient;

import java.sql.Timestamp;

public class RadiusAcctClientData {
	private String instanceID;
	private Timestamp createTime;
	private Long radiusAccClientIndex;			
	private String radiusAccClientAddress;			
	private String radiusAccClientID;				
	private Long radiusAccServPacketsDropped;
	private Long radiusAccServRequests;
	private Long radiusAccServDupRequests;	
	private Long radiusAccServResponses;	
	private Long radiusAccServBadAuthenticators;	
	private Long radiusAccServMalformedRequests;
	private Long radiusAccServNoRecords;
	private Long radiusAccServUnknownTypes;
	
	public String getInstanceID() {
		return instanceID;
	}
	public void setInstanceID(String instanceID) {
		this.instanceID = instanceID;
	}
	public Timestamp getCreateTime() {
		return createTime;
	}
	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}
	public Long getRadiusAccClientIndex() {
		return radiusAccClientIndex;
	}
	public void setRadiusAccClientIndex(Long radiusAccClientIndex) {
		this.radiusAccClientIndex = radiusAccClientIndex;
	}
	public String getRadiusAccClientAddress() {
		return radiusAccClientAddress;
	}
	public void setRadiusAccClientAddress(String radiusAccClientAddress) {
		this.radiusAccClientAddress = radiusAccClientAddress;
	}
	public String getRadiusAccClientID() {
		return radiusAccClientID;
	}
	public void setRadiusAccClientID(String radiusAccClientID) {
		this.radiusAccClientID = radiusAccClientID;
	}
	public Long getRadiusAccServPacketsDropped() {
		return radiusAccServPacketsDropped;
	}
	public void setRadiusAccServPacketsDropped(Long radiusAccServPacketsDropped) {
		this.radiusAccServPacketsDropped = radiusAccServPacketsDropped;
	}
	public Long getRadiusAccServRequests() {
		return radiusAccServRequests;
	}
	public void setRadiusAccServRequests(Long radiusAccServRequests) {
		this.radiusAccServRequests = radiusAccServRequests;
	}
	public Long getRadiusAccServDupRequests() {
		return radiusAccServDupRequests;
	}
	public void setRadiusAccServDupRequests(Long radiusAccServDupRequests) {
		this.radiusAccServDupRequests = radiusAccServDupRequests;
	}
	public Long getRadiusAccServResponses() {
		return radiusAccServResponses;
	}
	public void setRadiusAccServResponses(Long radiusAccServResponses) {
		this.radiusAccServResponses = radiusAccServResponses;
	}
	public Long getRadiusAccServBadAuthenticators() {
		return radiusAccServBadAuthenticators;
	}
	public void setRadiusAccServBadAuthenticators(
			Long radiusAccServBadAuthenticators) {
		this.radiusAccServBadAuthenticators = radiusAccServBadAuthenticators;
	}
	public Long getRadiusAccServMalformedRequests() {
		return radiusAccServMalformedRequests;
	}
	public void setRadiusAccServMalformedRequests(
			Long radiusAccServMalformedRequests) {
		this.radiusAccServMalformedRequests = radiusAccServMalformedRequests;
	}
	public Long getRadiusAccServNoRecords() {
		return radiusAccServNoRecords;
	}
	public void setRadiusAccServNoRecords(Long radiusAccServNoRecords) {
		this.radiusAccServNoRecords = radiusAccServNoRecords;
	}
	public Long getRadiusAccServUnknownTypes() {
		return radiusAccServUnknownTypes;
	}
	public void setRadiusAccServUnknownTypes(Long radiusAccServUnknownTypes) {
		this.radiusAccServUnknownTypes = radiusAccServUnknownTypes;
	}
	
	
}

 