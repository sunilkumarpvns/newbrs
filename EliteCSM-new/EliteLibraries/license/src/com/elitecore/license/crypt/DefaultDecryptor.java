package com.elitecore.license.crypt;

import com.elitecore.license.base.exception.InvalidLicenseKeyException;
import com.elitecore.license.util.EncryptDecrypt;

public class DefaultDecryptor implements LicenseDecryptor {
	
	@Override
	public String decryptLicenseKey(String licenseKey) throws InvalidLicenseKeyException {
		return EncryptDecrypt.decrypt(licenseKey);
	}

	@Override
	public String decryptPublicKey(String encryptedPubKey) {
		return EncryptDecrypt.decrypt(encryptedPubKey);
	}
}