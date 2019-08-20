package com.elitecore.elitesm.web.dashboard.widget.model.nasesistatistics;

import java.sql.Timestamp;

public class NasESIStatData {
	
	private String instanceID;
	private Timestamp createTime;
	private Integer radiusDynAuthServerIndex;
	private String radiusDynAuthServerAddress;
	private Integer rClientPortNumber;
	
	private Integer clientDisconRequests; //Disconnect Request
	private Integer radiusDynAuthClientDisconNaks; //Disconnect Naks
	private Integer radiusDynAuthClientDisconAcks;//Disconnect Acks
	private Integer clientDisconTimeouts;//Disconnect Timeouts
	
	private Integer radiusDynAuthClientCoARequests; //CoA Requests
	private Integer radiusDynAuthClientCoAAcks; //CoA Acks
	private Integer radiusDynAuthClientCoANaks;//CoA Naks
	private Integer radiusDynAuthClientCoATimeouts;//CoA Timeouts

	private String startTime;
	private String endTime;
	
	private String dynaAuthServerName;
	private Integer dynaAuthServerPort;
	
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
	public Integer getRadiusDynAuthServerIndex() {
		return radiusDynAuthServerIndex;
	}
	public void setRadiusDynAuthServerIndex(Integer radiusDynAuthServerIndex) {
		this.radiusDynAuthServerIndex = radiusDynAuthServerIndex;
	}
	public String getRadiusDynAuthServerAddress() {
		return radiusDynAuthServerAddress;
	}
	public void setRadiusDynAuthServerAddress(String radiusDynAuthServerAddress) {
		this.radiusDynAuthServerAddress = radiusDynAuthServerAddress;
	}
	public Integer getrClientPortNumber() {
		return rClientPortNumber;
	}
	public void setrClientPortNumber(Integer rClientPortNumber) {
		this.rClientPortNumber = rClientPortNumber;
	}
	public Integer getClientDisconRequests() {
		return clientDisconRequests;
	}
	public void setClientDisconRequests(Integer clientDisconRequests) {
		this.clientDisconRequests = clientDisconRequests;
	}
	public Integer getRadiusDynAuthClientCoARequests() {
		return radiusDynAuthClientCoARequests;
	}
	public void setRadiusDynAuthClientCoARequests(
			Integer radiusDynAuthClientCoARequests) {
		this.radiusDynAuthClientCoARequests = radiusDynAuthClientCoARequests;
	}
	public Integer getRadiusDynAuthClientDisconNaks() {
		return radiusDynAuthClientDisconNaks;
	}
	public void setRadiusDynAuthClientDisconNaks(
			Integer radiusDynAuthClientDisconNaks) {
		this.radiusDynAuthClientDisconNaks = radiusDynAuthClientDisconNaks;
	}
	public Integer getRadiusDynAuthClientDisconAcks() {
		return radiusDynAuthClientDisconAcks;
	}
	public void setRadiusDynAuthClientDisconAcks(
			Integer radiusDynAuthClientDisconAcks) {
		this.radiusDynAuthClientDisconAcks = radiusDynAuthClientDisconAcks;
	}
	public Integer getRadiusDynAuthClientCoAAcks() {
		return radiusDynAuthClientCoAAcks;
	}
	public void setRadiusDynAuthClientCoAAcks(Integer radiusDynAuthClientCoAAcks) {
		this.radiusDynAuthClientCoAAcks = radiusDynAuthClientCoAAcks;
	}
	public Integer getRadiusDynAuthClientCoANaks() {
		return radiusDynAuthClientCoANaks;
	}
	public void setRadiusDynAuthClientCoANaks(Integer radiusDynAuthClientCoANaks) {
		this.radiusDynAuthClientCoANaks = radiusDynAuthClientCoANaks;
	}
	public Integer getRadiusDynAuthClientCoATimeouts() {
		return radiusDynAuthClientCoATimeouts;
	}
	public void setRadiusDynAuthClientCoATimeouts(
			Integer radiusDynAuthClientCoATimeouts) {
		this.radiusDynAuthClientCoATimeouts = radiusDynAuthClientCoATimeouts;
	}
	public Integer getClientDisconTimeouts() {
		return clientDisconTimeouts;
	}
	public void setClientDisconTimeouts(Integer clientDisconTimeouts) {
		this.clientDisconTimeouts = clientDisconTimeouts;
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
	public String getDynaAuthServerName() {
		return dynaAuthServerName;
	}
	public void setDynaAuthServerName(String dynaAuthServerName) {
		this.dynaAuthServerName = dynaAuthServerName;
	}
	public Integer getDynaAuthServerPort() {
		return dynaAuthServerPort;
	}
	public void setDynaAuthServerPort(Integer dynaAuthServerPort) {
		this.dynaAuthServerPort = dynaAuthServerPort;
	}
}
