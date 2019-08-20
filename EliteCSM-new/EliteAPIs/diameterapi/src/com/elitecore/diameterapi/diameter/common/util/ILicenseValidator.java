package com.elitecore.diameterapi.diameter.common.util;

public interface ILicenseValidator {
	public static final ILicenseValidator SUPPORT_ALL_VENDORS = new ILicenseValidator() {
		
		@Override
		public boolean isVendorSupported(String vendorId) {
			return true;
		}
	};
	
	public boolean isVendorSupported(String vendorId);
}
