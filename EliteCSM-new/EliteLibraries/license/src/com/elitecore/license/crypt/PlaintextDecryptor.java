package com.elitecore.license.crypt;

import com.elitecore.license.base.exception.InvalidLicenseKeyException;

public class PlaintextDecryptor implements LicenseDecryptor {

	@Override
	public String decryptLicenseKey(String plaintextLicenseKey) throws InvalidLicenseKeyException {
		return plaintextLicenseKey;
	}
	@Override
	public String decryptPublicKey(String plaintextPubKey) {
		return plaintextPubKey;
	}
	
}