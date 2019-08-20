package com.elitecore.netvertexsm.datamanager.gateway.profile.data;

import java.util.List;

public interface IRadiusProfileData {
	public long getProfileId();
	public void setProfileId(long profileId);
	public String getSupportedVendorList();
	public void setSupportedVendorList(String supportedVendorList);
	public void setAttributeMapList(List<RadiusAttributeMapData> attributeMapList);
	public List<RadiusAttributeMapData> getAttributeMapList();
}
