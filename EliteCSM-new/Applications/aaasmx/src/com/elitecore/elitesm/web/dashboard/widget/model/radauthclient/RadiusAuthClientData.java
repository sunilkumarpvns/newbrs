package com.elitecore.elitesm.web.dashboard.widget.model.radauthclient;

import java.sql.Timestamp;

public class RadiusAuthClientData {
	private String instanceID;
	private Timestamp createTime;
	private Long radiusAuthClientIndex;			
	private String radiusAuthClientAddress;			
	private String radiusAuthClientID;				
	private Long radiusAuthServAccessRequests;	
	private Long dupAccessRequests;				
	private Long radiusAuthServAccessAccepts;		
	private Long radiusAuthServAccessRejects;		
	private Long radiusAuthServAccessChallenges;	
	private Long malformedAccessRequests; 		
	private Long badAuthenticators; 			
	private Long radiusAuthServPacketsDropped;	
	private Long radiusAuthServUnknownTypes;
	
	
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
	public Long getRadiusAuthClientIndex() {
		return radiusAuthClientIndex;
	}
	public void setRadiusAuthClientIndex(Long radiusAuthClientIndex) {
		this.radiusAuthClientIndex = radiusAuthClientIndex;
	}
	public String getRadiusAuthClientAddress() {
		return radiusAuthClientAddress;
	}
	public void setRadiusAuthClientAddress(String radiusAuthClientAddress) {
		this.radiusAuthClientAddress = radiusAuthClientAddress;
	}
	public String getRadiusAuthClientID() {
		return radiusAuthClientID;
	}
	public void setRadiusAuthClientID(String radiusAuthClientID) {
		this.radiusAuthClientID = radiusAuthClientID;
	}
	public Long getRadiusAuthServAccessRequests() {
		return radiusAuthServAccessRequests;
	}
	public void setRadiusAuthServAccessRequests(Long radiusAuthServAccessRequests) {
		this.radiusAuthServAccessRequests = radiusAuthServAccessRequests;
	}
	public Long getDupAccessRequests() {
		return dupAccessRequests;
	}
	public void setDupAccessRequests(Long dupAccessRequests) {
		this.dupAccessRequests = dupAccessRequests;
	}
	public Long getRadiusAuthServAccessAccepts() {
		return radiusAuthServAccessAccepts;
	}
	public void setRadiusAuthServAccessAccepts(Long radiusAuthServAccessAccepts) {
		this.radiusAuthServAccessAccepts = radiusAuthServAccessAccepts;
	}
	public Long getRadiusAuthServAccessRejects() {
		return radiusAuthServAccessRejects;
	}
	public void setRadiusAuthServAccessRejects(Long radiusAuthServAccessRejects) {
		this.radiusAuthServAccessRejects = radiusAuthServAccessRejects;
	}
	public Long getRadiusAuthServAccessChallenges() {
		return radiusAuthServAccessChallenges;
	}
	public void setRadiusAuthServAccessChallenges(
			Long radiusAuthServAccessChallenges) {
		this.radiusAuthServAccessChallenges = radiusAuthServAccessChallenges;
	}
	public Long getMalformedAccessRequests() {
		return malformedAccessRequests;
	}
	public void setMalformedAccessRequests(Long malformedAccessRequests) {
		this.malformedAccessRequests = malformedAccessRequests;
	}
	public Long getBadAuthenticators() {
		return badAuthenticators;
	}
	public void setBadAuthenticators(Long badAuthenticators) {
		this.badAuthenticators = badAuthenticators;
	}
	public Long getRadiusAuthServPacketsDropped() {
		return radiusAuthServPacketsDropped;
	}
	public void setRadiusAuthServPacketsDropped(Long radiusAuthServPacketsDropped) {
		this.radiusAuthServPacketsDropped = radiusAuthServPacketsDropped;
	}
	public Long getRadiusAuthServUnknownTypes() {
		return radiusAuthServUnknownTypes;
	}
	public void setRadiusAuthServUnknownTypes(Long radiusAuthServUnknownTypes) {
		this.radiusAuthServUnknownTypes = radiusAuthServUnknownTypes;
	}
	
		
}
