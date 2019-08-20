package com.elitecore.coreradius.commons.attributes;

public interface IRadiusVendorSpecificAttribute extends IRadiusAttribute {
	
	public long getVendorID();
	public void setVendorID(long vendorID);
}
