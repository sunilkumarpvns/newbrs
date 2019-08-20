package com.elitecore.license.crypt;

public interface LicenseEncryptor {
	public String encryptLicenseKey(String licenseKey);

	public String encryptPublicKey(String string);
}