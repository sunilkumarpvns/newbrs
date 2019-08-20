package com.elitecore.license.crypt;

import com.elitecore.license.base.exception.InvalidLicenseKeyException;

public interface LicenseDecryptor {
	public String decryptLicenseKey(String licenseKey) throws InvalidLicenseKeyException;

	public String decryptPublicKey(String string);
}