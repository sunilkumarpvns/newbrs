package com.elitecore.netvertexsm.datamanager.gateway.profile.data;

import java.util.List;

public interface IDiameterProfileData {
	public long getProfileId();
	public void setProfileId(long profileId);
	public String getSupportedVendorList();
	public void setSupportedVendorList(String supportedVendorList);
	public String getSupportedStandard();
	public void setSupportedStandard(String supportedStandard);
	
	public List<DiameterAttributeMapData> getAttributeMapList();
	public void setAttributeMapList(List<DiameterAttributeMapData> attributeMapList);
}
