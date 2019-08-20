package com.elitecore.license.crypt;

import com.elitecore.license.util.EncryptDecrypt;

public class DefaultEncryptor implements LicenseEncryptor {

	@Override
	public String encryptLicenseKey(String licenseKey) {
		return EncryptDecrypt.encrypt(licenseKey);
	}

	@Override
	public String encryptPublicKey(String string) {
		return EncryptDecrypt.encrypt(string);
	}
}