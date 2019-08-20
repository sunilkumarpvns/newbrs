package com.elitecore.elitesm.web.dashboard.widget.model.radacctesistatistics;

import java.sql.Timestamp;

public class RadiusAcctESIStatData {
	
	private String instanceID;
	private Timestamp createTime;
	private Integer radiusAccServerIndex;
	private String radiusAccServerAddress;
	private Integer clientServerPortNumber;
	private Integer radiusAccClientRoundTripTime;
	private Integer radiusAccClientRequests;
	private Integer radiusAccClientRetransmissions;
	private Integer radiusAccClientResponses;
	private Integer clientMalformedResponses;
	private Integer clientBadAuthenticators;
	private Integer radiusAccClientPendingRequests;
	private Integer radiusAccClientTimeouts;
	private Integer radiusAccClientUnknownTypes;
	private Integer radiusAccClientPacketsDropped;
	private String startTime;
	private String endTime;
	private String acctServerName;
	
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
	public Integer getRadiusAccServerIndex() {
		return radiusAccServerIndex;
	}
	public void setRadiusAccServerIndex(Integer radiusAccServerIndex) {
		this.radiusAccServerIndex = radiusAccServerIndex;
	}
	public String getRadiusAccServerAddress() {
		return radiusAccServerAddress;
	}
	public void setRadiusAccServerAddress(String radiusAccServerAddress) {
		this.radiusAccServerAddress = radiusAccServerAddress;
	}
	public Integer getClientServerPortNumber() {
		return clientServerPortNumber;
	}
	public void setClientServerPortNumber(Integer clientServerPortNumber) {
		this.clientServerPortNumber = clientServerPortNumber;
	}
	public Integer getRadiusAccClientRoundTripTime() {
		return radiusAccClientRoundTripTime;
	}
	public void setRadiusAccClientRoundTripTime(Integer radiusAccClientRoundTripTime) {
		this.radiusAccClientRoundTripTime = radiusAccClientRoundTripTime;
	}
	public Integer getRadiusAccClientRequests() {
		return radiusAccClientRequests;
	}
	public void setRadiusAccClientRequests(Integer radiusAccClientRequests) {
		this.radiusAccClientRequests = radiusAccClientRequests;
	}
	public Integer getRadiusAccClientRetransmissions() {
		return radiusAccClientRetransmissions;
	}
	public void setRadiusAccClientRetransmissions(
			Integer radiusAccClientRetransmissions) {
		this.radiusAccClientRetransmissions = radiusAccClientRetransmissions;
	}
	public Integer getRadiusAccClientResponses() {
		return radiusAccClientResponses;
	}
	public void setRadiusAccClientResponses(Integer radiusAccClientResponses) {
		this.radiusAccClientResponses = radiusAccClientResponses;
	}
	public Integer getClientMalformedResponses() {
		return clientMalformedResponses;
	}
	public void setClientMalformedResponses(Integer clientMalformedResponses) {
		this.clientMalformedResponses = clientMalformedResponses;
	}
	public Integer getClientBadAuthenticators() {
		return clientBadAuthenticators;
	}
	public void setClientBadAuthenticators(Integer clientBadAuthenticators) {
		this.clientBadAuthenticators = clientBadAuthenticators;
	}
	public Integer getRadiusAccClientPendingRequests() {
		return radiusAccClientPendingRequests;
	}
	public void setRadiusAccClientPendingRequests(
			Integer radiusAccClientPendingRequests) {
		this.radiusAccClientPendingRequests = radiusAccClientPendingRequests;
	}
	public Integer getRadiusAccClientTimeouts() {
		return radiusAccClientTimeouts;
	}
	public void setRadiusAccClientTimeouts(Integer radiusAccClientTimeouts) {
		this.radiusAccClientTimeouts = radiusAccClientTimeouts;
	}
	public Integer getRadiusAccClientUnknownTypes() {
		return radiusAccClientUnknownTypes;
	}
	public void setRadiusAccClientUnknownTypes(Integer radiusAccClientUnknownTypes) {
		this.radiusAccClientUnknownTypes = radiusAccClientUnknownTypes;
	}
	public Integer getRadiusAccClientPacketsDropped() {
		return radiusAccClientPacketsDropped;
	}
	public void setRadiusAccClientPacketsDropped(
			Integer radiusAccClientPacketsDropped) {
		this.radiusAccClientPacketsDropped = radiusAccClientPacketsDropped;
	}
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public String getAcctServerName() {
		return acctServerName;
	}
	public void setAcctServerName(String acctServerName) {
		this.acctServerName = acctServerName;
	}
}
