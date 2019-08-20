package com.elitecore.netvertexsm.web.gateway.gateway.form;

import java.util.List;

import org.apache.struts.action.ActionForm;

public class ViewGatewayForm extends ActionForm {
	private String gatewayName;
	private int profileId;	
	private String areaName;	
	private String commProtocolId;
	private String connectionUrl;
	private String locationName;
	private int locationId;	
	private String description;	
	private List commProtocolList;
	private List profileList;
	private List locationList;
	

//	Radius Properties
	private String sharedSecret;
	private int maxRequestTimeout;	
	
//	Diameter Properties
	private String hostIdentity;
	private String realm;
	private int timeout;
	private boolean tlsEnable;
	private int retransmissionCnt;
	
	private String action;
	private String status;
	private long pageNumber;		
	private long totalPages;
	private long totalRecords;
	
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public long getPageNumber() {
		return pageNumber;
	}
	public void setPageNumber(long pageNumber) {
		this.pageNumber = pageNumber;
	}
	public long getTotalPages() {
		return totalPages;
	}
	public void setTotalPages(long totalPages) {
		this.totalPages = totalPages;
	}
	public long getTotalRecords() {
		return totalRecords;
	}
	public void setTotalRecords(long totalRecords) {
		this.totalRecords = totalRecords;
	}
	public String getGatewayName() {
		return gatewayName;
	}
	public void setGatewayName(String gatewayName) {
		this.gatewayName = gatewayName;
	}
	public int getProfileId() {
		return profileId;
	}
	public void setProfileId(int profileId) {
		this.profileId = profileId;
	}
	public String getAreaName() {
		return areaName;
	}
	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}
	public String getCommProtocolId() {
		return commProtocolId;
	}
	public void setCommProtocolId(String commProtocolId) {
		this.commProtocolId = commProtocolId;
	}
	public String getConnectionUrl() {
		return connectionUrl;
	}
	public void setConnectionUrl(String connectionUrl) {
		this.connectionUrl = connectionUrl;
	}
	public String getLocationName() {
		return locationName;
	}
	public void setLocationName(String locationName) {
		this.locationName = locationName;
	}
	public int getLocationId() {
		return locationId;
	}
	public void setLocationId(int locationId) {
		this.locationId = locationId;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public List getCommProtocolList() {
		return commProtocolList;
	}
	public void setCommProtocolList(List commProtocolList) {
		this.commProtocolList = commProtocolList;
	}
	public List getProfileList() {
		return profileList;
	}
	public void setProfileList(List profileList) {
		this.profileList = profileList;
	}
	public List getLocationList() {
		return locationList;
	}
	public void setLocationList(List locationList) {
		this.locationList = locationList;
	}
	public String getSharedSecret() {
		return sharedSecret;
	}
	public void setSharedSecret(String sharedSecret) {
		this.sharedSecret = sharedSecret;
	}
	public int getMaxRequestTimeout() {
		return maxRequestTimeout;
	}
	public void setMaxRequestTimeout(int maxRequestTimeout) {
		this.maxRequestTimeout = maxRequestTimeout;
	}
	public String getHostIdentity() {
		return hostIdentity;
	}
	public void setHostIdentity(String hostIdentity) {
		this.hostIdentity = hostIdentity;
	}
	public String getRealm() {
		return realm;
	}
	public void setRealm(String realm) {
		this.realm = realm;
	}
	public int getTimeout() {
		return timeout;
	}
	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}
	public boolean isTlsEnable() {
		return tlsEnable;
	}
	public void setTlsEnable(boolean tlsEnable) {
		this.tlsEnable = tlsEnable;
	}
	public int getRetransmissionCnt() {
		return retransmissionCnt;
	}
	public void setRetransmissionCnt(int retransmissionCnt) {
		this.retransmissionCnt = retransmissionCnt;
	}
}
