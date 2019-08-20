package com.elitecore.elitesm.web.dashboard.widget.model.radacctserv;

import java.sql.Timestamp;

public class RadiusAcctServData {
	private String instanceID;
	private Timestamp createtime;
	private String radiusAccServIdent;
	private Long radiusAccServUpTime;
	private Long radiusAccServResetTime;
	private String radiusAccServConfigReset;
	private Long radiusAccServTotalRequests;    
	private Long totalInvalidRequests;          
	private Long radiusAccServTotalDupRequests; 
	private Long radiusAccServTotalResponses;   
	private Long totalMalformedRequests;        
	private Long totalBadAuthenticators;        
	private Long totalPacketsDropped;           
	private Long radiusAccServTotalNoRecords;   
	private Long radiusAccServTotalUnknownTypes;
	
	public String getInstanceID() {
		return instanceID;
	}
	public void setInstanceID(String instanceID) {
		this.instanceID = instanceID;
	}
	public Timestamp getCreatetime() {
		return createtime;
	}
	public void setCreatetime(Timestamp createtime) {
		this.createtime = createtime;
	}
	public String getRadiusAccServIdent() {
		return radiusAccServIdent;
	}
	public void setRadiusAccServIdent(String radiusAccServIdent) {
		this.radiusAccServIdent = radiusAccServIdent;
	}
	public Long getRadiusAccServUpTime() {
		return radiusAccServUpTime;
	}
	public void setRadiusAccServUpTime(Long radiusAccServUpTime) {
		this.radiusAccServUpTime = radiusAccServUpTime;
	}
	public Long getRadiusAccServResetTime() {
		return radiusAccServResetTime;
	}
	public void setRadiusAccServResetTime(Long radiusAccServResetTime) {
		this.radiusAccServResetTime = radiusAccServResetTime;
	}
	public String getRadiusAccServConfigReset() {
		return radiusAccServConfigReset;
	}
	public void setRadiusAccServConfigReset(String radiusAccServConfigReset) {
		this.radiusAccServConfigReset = radiusAccServConfigReset;
	}
	public Long getRadiusAccServTotalRequests() {
		return radiusAccServTotalRequests;
	}
	public void setRadiusAccServTotalRequests(Long radiusAccServTotalRequests) {
		this.radiusAccServTotalRequests = radiusAccServTotalRequests;
	}
	public Long getTotalInvalidRequests() {
		return totalInvalidRequests;
	}
	public void setTotalInvalidRequests(Long totalInvalidRequests) {
		this.totalInvalidRequests = totalInvalidRequests;
	}
	public Long getRadiusAccServTotalDupRequests() {
		return radiusAccServTotalDupRequests;
	}
	public void setRadiusAccServTotalDupRequests(Long radiusAccServTotalDupRequests) {
		this.radiusAccServTotalDupRequests = radiusAccServTotalDupRequests;
	}
	public Long getRadiusAccServTotalResponses() {
		return radiusAccServTotalResponses;
	}
	public void setRadiusAccServTotalResponses(Long radiusAccServTotalResponses) {
		this.radiusAccServTotalResponses = radiusAccServTotalResponses;
	}
	public Long getTotalMalformedRequests() {
		return totalMalformedRequests;
	}
	public void setTotalMalformedRequests(Long totalMalformedRequests) {
		this.totalMalformedRequests = totalMalformedRequests;
	}
	public Long getTotalBadAuthenticators() {
		return totalBadAuthenticators;
	}
	public void setTotalBadAuthenticators(Long totalBadAuthenticators) {
		this.totalBadAuthenticators = totalBadAuthenticators;
	}
	public Long getTotalPacketsDropped() {
		return totalPacketsDropped;
	}
	public void setTotalPacketsDropped(Long totalPacketsDropped) {
		this.totalPacketsDropped = totalPacketsDropped;
	}
	public Long getRadiusAccServTotalNoRecords() {
		return radiusAccServTotalNoRecords;
	}
	public void setRadiusAccServTotalNoRecords(Long radiusAccServTotalNoRecords) {
		this.radiusAccServTotalNoRecords = radiusAccServTotalNoRecords;
	}
	public Long getRadiusAccServTotalUnknownTypes() {
		return radiusAccServTotalUnknownTypes;
	}
	public void setRadiusAccServTotalUnknownTypes(
			Long radiusAccServTotalUnknownTypes) {
		this.radiusAccServTotalUnknownTypes = radiusAccServTotalUnknownTypes;
	}
	
	
}
