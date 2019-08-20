package com.elitecore.coreradius.commons.util;

public interface ILicenseValidator {

	public static final ILicenseValidator SUPPORT_ALL_VENDORS = vendorId -> true;
	
	public boolean isVendorSupported(String vendorId);
	
}
