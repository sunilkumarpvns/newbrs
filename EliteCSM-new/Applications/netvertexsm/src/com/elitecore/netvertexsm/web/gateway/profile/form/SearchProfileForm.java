package com.elitecore.netvertexsm.web.gateway.profile.form;

import java.util.List;
import java.util.Set;

import org.apache.struts.action.ActionForm;

import com.elitecore.netvertexsm.datamanager.gateway.gateway.data.GatewayData;
import com.elitecore.netvertexsm.datamanager.gateway.profile.data.GatewayProfileData;

public class SearchProfileForm extends ActionForm {

	private static final long serialVersionUID = 1L;
		
	private long profileId;
	private String profileName;
	private String gatewayTypeId;
	private Long vedorId;
	private String deviceModel;
	private Integer maxThroughput;
	private Integer bufferBW;
	private String description;
	private Set<GatewayData> gatewaySet;
	private GatewayData gatewayData;	
	private String status;
	private long pageNumber=0;		
	private long totalPages;
	private long totalRecords;
	private List<GatewayProfileData> listSearchProfile;
	private List profileList;
	private String action;
			
	public String getProfileName() {
		return profileName;
	}
	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}
	public String getGatewayTypeId() {
		return gatewayTypeId;
	}
	public void setGatewayTypeId(String gatewayTypeId) {
		this.gatewayTypeId = gatewayTypeId;
	}
	public Long getVedorId() {
		return vedorId;
	}
	public void setVedorId(Long vedorId) {
		this.vedorId = vedorId;
	}
	public String getDeviceModel() {
		return deviceModel;
	}
	public void setDeviceModel(String deviceModel) {
		this.deviceModel = deviceModel;
	}
	public Integer getMaxThroughput() {
		return maxThroughput;
	}
	public void setMaxThroughput(Integer maxThroughput) {
		this.maxThroughput = maxThroughput;
	}
	public Integer getBufferBW() {
		return bufferBW;
	}
	public void setBufferBW(Integer bufferBW) {
		this.bufferBW = bufferBW;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Set<GatewayData> getGatewaySet() {
		return gatewaySet;
	}
	public void setGatewaySet(Set<GatewayData> gatewaySet) {
		this.gatewaySet = gatewaySet;
	}
	public GatewayData getGatewayData() {
		return gatewayData;
	}
	public void setGatewayData(GatewayData gatewayData) {
		this.gatewayData = gatewayData;
	}
	public List<GatewayProfileData> getListSearchProfile() {
		return listSearchProfile;
	}
	public void setListSearchProfile(List<GatewayProfileData> listSearchProfile) {
		this.listSearchProfile = listSearchProfile;
	}
	public List getProfileList() {
		return profileList;
	}
	public void setProfileList(List profileList) {
		this.profileList = profileList;
	}
	public long getProfileId() {
		return profileId;
	}
	public void setProfileId(long profileId) {
		this.profileId = profileId;
	}
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
}
