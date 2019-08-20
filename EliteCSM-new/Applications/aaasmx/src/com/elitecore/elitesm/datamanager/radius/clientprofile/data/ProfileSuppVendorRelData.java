package com.elitecore.elitesm.datamanager.radius.clientprofile.data;

import java.io.Serializable;

import com.elitecore.elitesm.datamanager.core.base.data.BaseData;

public class ProfileSuppVendorRelData extends BaseData implements Serializable{

	private static final long serialVersionUID = 1L;
	private String vendorInstanceId;
	private String profileId;
	
	public String getVendorInstanceId() {
		return vendorInstanceId;
	}
	public void setVendorInstanceId(String vendorInstanceId) {
		this.vendorInstanceId = vendorInstanceId;
	}
	public String getProfileId() {
		return profileId;
	}
	public void setProfileId(String profileId) {
		this.profileId = profileId;
	}
	

}
