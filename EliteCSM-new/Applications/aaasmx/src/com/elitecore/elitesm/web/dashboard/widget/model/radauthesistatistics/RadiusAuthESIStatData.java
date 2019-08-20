package com.elitecore.elitesm.web.dashboard.widget.model.radauthesistatistics;

import java.sql.Timestamp;

public class RadiusAuthESIStatData {
	
	private String instanceID;
	private Timestamp createTime;
	private Integer radAuthServerIndex;
	private String radAuthServerAddress;
	private Integer clientServerPortNumber;
	private Integer radAuthClientRoundTripTime;
	private Integer radAuthClientAccessRequests;
	private Integer clientAccessRetransmissions;
	private Integer radAuthClientAccessAccepts;
	private Integer radAuthClientAccessRejects;
	private Integer clientAccessChallenges;
	private Integer clientMalformedAccessResponses;
	private Integer clientBadAuthenticators;
	private Integer clientPendingRequests;
	private Integer radAuthClientTimeouts;
	private Integer radAuthClientUnknownTypes;
	private Integer radAuthClientPacketsDropped;
	private String startTime;
	private String endTime;
	private String authServerName;
	
	public String getInstanceID() {
		return instanceID;
	}
	public Timestamp getCreateTime() {
		return createTime;
	}
	public Integer getRadAuthServerIndex() {
		return radAuthServerIndex;
	}
	public String getRadAuthServerAddress() {
		return radAuthServerAddress;
	}
	public Integer getClientServerPortNumber() {
		return clientServerPortNumber;
	}
	public Integer getRadAuthClientRoundTripTime() {
		return radAuthClientRoundTripTime;
	}
	public Integer getRadAuthClientAccessRequests() {
		return radAuthClientAccessRequests;
	}
	public Integer getClientAccessRetransmissions() {
		return clientAccessRetransmissions;
	}
	public Integer getRadAuthClientAccessAccepts() {
		return radAuthClientAccessAccepts;
	}
	public Integer getRadAuthClientAccessRejects() {
		return radAuthClientAccessRejects;
	}
	public Integer getClientAccessChallenges() {
		return clientAccessChallenges;
	}
	public Integer getClientMalformedAccessResponses() {
		return clientMalformedAccessResponses;
	}
	public Integer getClientBadAuthenticators() {
		return clientBadAuthenticators;
	}
	public Integer getClientPendingRequests() {
		return clientPendingRequests;
	}
	public Integer getRadAuthClientTimeouts() {
		return radAuthClientTimeouts;
	}
	public Integer getRadAuthClientUnknownTypes() {
		return radAuthClientUnknownTypes;
	}
	public Integer getRadAuthClientPacketsDropped() {
		return radAuthClientPacketsDropped;
	}
	public void setInstanceID(String instanceID) {
		this.instanceID = instanceID;
	}
	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}
	public void setRadAuthServerIndex(Integer radAuthServerIndex) {
		this.radAuthServerIndex = radAuthServerIndex;
	}
	public void setRadAuthServerAddress(String radAuthServerAddress) {
		this.radAuthServerAddress = radAuthServerAddress;
	}
	public void setClientServerPortNumber(Integer clientServerPortNumber) {
		this.clientServerPortNumber = clientServerPortNumber;
	}
	public void setRadAuthClientRoundTripTime(Integer radAuthClientRoundTripTime) {
		this.radAuthClientRoundTripTime = radAuthClientRoundTripTime;
	}
	public void setRadAuthClientAccessRequests(Integer radAuthClientAccessRequests) {
		this.radAuthClientAccessRequests = radAuthClientAccessRequests;
	}
	public void setClientAccessRetransmissions(Integer clientAccessRetransmissions) {
		this.clientAccessRetransmissions = clientAccessRetransmissions;
	}
	public void setRadAuthClientAccessAccepts(Integer radAuthClientAccessAccepts) {
		this.radAuthClientAccessAccepts = radAuthClientAccessAccepts;
	}
	public void setRadAuthClientAccessRejects(Integer radAuthClientAccessRejects) {
		this.radAuthClientAccessRejects = radAuthClientAccessRejects;
	}
	public void setClientAccessChallenges(Integer clientAccessChallenges) {
		this.clientAccessChallenges = clientAccessChallenges;
	}
	public void setClientMalformedAccessResponses(
			Integer clientMalformedAccessResponses) {
		this.clientMalformedAccessResponses = clientMalformedAccessResponses;
	}
	public void setClientBadAuthenticators(Integer clientBadAuthenticators) {
		this.clientBadAuthenticators = clientBadAuthenticators;
	}
	public void setClientPendingRequests(Integer clientPendingRequests) {
		this.clientPendingRequests = clientPendingRequests;
	}
	public void setRadAuthClientTimeouts(Integer radAuthClientTimeouts) {
		this.radAuthClientTimeouts = radAuthClientTimeouts;
	}
	public void setRadAuthClientUnknownTypes(Integer radAuthClientUnknownTypes) {
		this.radAuthClientUnknownTypes = radAuthClientUnknownTypes;
	}
	public void setRadAuthClientPacketsDropped(Integer radAuthClientPacketsDropped) {
		this.radAuthClientPacketsDropped = radAuthClientPacketsDropped;
	}
	public String getStartTime() {
		return startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public String getAuthServerName() {
		return authServerName;
	}
	public void setAuthServerName(String authServerName) {
		this.authServerName = authServerName;
	}
	
	

}
