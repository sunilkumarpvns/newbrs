package com.elitecore.license.crypt;

public class PlaintextEncryptor implements LicenseEncryptor {

	@Override
	public String encryptLicenseKey(String licenseKey) {
		return licenseKey;
	}

	@Override
	public String encryptPublicKey(String string) {
		return string;
	}
	
}