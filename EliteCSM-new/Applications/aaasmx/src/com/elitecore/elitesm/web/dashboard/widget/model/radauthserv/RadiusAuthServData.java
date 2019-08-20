package com.elitecore.elitesm.web.dashboard.widget.model.radauthserv;

import java.sql.Timestamp;

public class RadiusAuthServData {
	private String instanceID;
	private Timestamp createtime;
	private String radiusAuthServIdent;
	private Long radiusAuthServUpTime;
	private Long radiusAuthServResetTime;
	private String radiusAuthServConfigReset;
	private Long totalAccessRequests;
	private Long totalInvalidRequests;
	private Long totalDupAccessRequests;
	private Long totalAccessAccepts;
	private Long totalAccessRejects;
	private Long totalAccessChallenges;
	private Long totalMalformedAccessRequests;
	private Long totalBadAuthenticators;
	private Long totalPacketsDropped;
	private Long totalUnknownTypes;
	
	
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
	public String getRadiusAuthServIdent() {
		return radiusAuthServIdent;
	}
	public void setRadiusAuthServIdent(String radiusAuthServIdent) {
		this.radiusAuthServIdent = radiusAuthServIdent;
	}
	public Long getRadiusAuthServUpTime() {
		return radiusAuthServUpTime;
	}
	public void setRadiusAuthServUpTime(Long radiusAuthServUpTime) {
		this.radiusAuthServUpTime = radiusAuthServUpTime;
	}
	public Long getRadiusAuthServResetTime() {
		return radiusAuthServResetTime;
	}
	public void setRadiusAuthServResetTime(Long radiusAuthServResetTime) {
		this.radiusAuthServResetTime = radiusAuthServResetTime;
	}
	public String getRadiusAuthServConfigReset() {
		return radiusAuthServConfigReset;
	}
	public void setRadiusAuthServConfigReset(String radiusAuthServConfigReset) {
		this.radiusAuthServConfigReset = radiusAuthServConfigReset;
	}
	public Long getTotalAccessRequests() {
		return totalAccessRequests;
	}
	public void setTotalAccessRequests(Long totalAccessRequests) {
		this.totalAccessRequests = totalAccessRequests;
	}
	public Long getTotalInvalidRequests() {
		return totalInvalidRequests;
	}
	public void setTotalInvalidRequests(Long totalInvalidRequests) {
		this.totalInvalidRequests = totalInvalidRequests;
	}
	public Long getTotalDupAccessRequests() {
		return totalDupAccessRequests;
	}
	public void setTotalDupAccessRequests(Long totalDupAccessRequests) {
		this.totalDupAccessRequests = totalDupAccessRequests;
	}
	public Long getTotalAccessAccepts() {
		return totalAccessAccepts;
	}
	public void setTotalAccessAccepts(Long totalAccessAccepts) {
		this.totalAccessAccepts = totalAccessAccepts;
	}
	public Long getTotalAccessRejects() {
		return totalAccessRejects;
	}
	public void setTotalAccessRejects(Long totalAccessRejects) {
		this.totalAccessRejects = totalAccessRejects;
	}
	public Long getTotalAccessChallenges() {
		return totalAccessChallenges;
	}
	public void setTotalAccessChallenges(Long totalAccessChallenges) {
		this.totalAccessChallenges = totalAccessChallenges;
	}
	public Long getTotalMalformedAccessRequests() {
		return totalMalformedAccessRequests;
	}
	public void setTotalMalformedAccessRequests(Long totalMalformedAccessRequests) {
		this.totalMalformedAccessRequests = totalMalformedAccessRequests;
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
	public Long getTotalUnknownTypes() {
		return totalUnknownTypes;
	}
	public void setTotalUnknownTypes(Long totalUnknownTypes) {
		this.totalUnknownTypes = totalUnknownTypes;
	}
	
	

}
