package com.elitecore.elitesm.web.radius.clientprofile.forms;

import java.util.List;

import com.elitecore.elitesm.datamanager.radius.clientprofile.data.ClientTypeData;
import com.elitecore.elitesm.datamanager.radius.clientprofile.data.RadiusClientProfileData;
import com.elitecore.elitesm.datamanager.radius.clientprofile.data.VendorData;
import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class SearchClientProfileForm extends BaseWebForm{
	
	
	private List<RadiusClientProfileData> clientProfileList;
	private String profileName;
	private List<ClientTypeData> clientTypeList;
	private List<VendorData> vendorList;
	private long pageNumber;
	private long totalPages;
	private long totalRecords;
	
	private String action;
	
	private String clientTypeId;
	private String vendorInstanceId;
	

	
	
	

	public List<RadiusClientProfileData> getClientProfileList() {
		return clientProfileList;
	}

	public void setClientProfileList(List<RadiusClientProfileData> clientProfileList) {
		this.clientProfileList = clientProfileList;
	}

	public String getProfileName() {
		return profileName;
	}

	public void setProfileName(String profileName) {
		this.profileName = profileName;
	}

	public List<ClientTypeData> getClientTypeList() {
		return clientTypeList;
	}

	public void setClientTypeList(List<ClientTypeData> clientTypeList) {
		this.clientTypeList = clientTypeList;
	}

	public List<VendorData> getVendorList() {
		return vendorList;
	}

	public void setVendorList(List<VendorData> vendorList) {
		this.vendorList = vendorList;
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

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	

	public String getClientTypeId() {
		return clientTypeId;
	}

	public void setClientTypeId(String clientTypeId) {
		this.clientTypeId = clientTypeId;
	}

	public String getVendorInstanceId() {
		return vendorInstanceId;
	}

	public void setVendorInstanceId(String vendorInstanceId) {
		this.vendorInstanceId = vendorInstanceId;
	}


}
