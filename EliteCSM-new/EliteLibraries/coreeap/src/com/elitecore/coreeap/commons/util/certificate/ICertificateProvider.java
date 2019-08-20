package com.elitecore.coreeap.commons.util.certificate;

import java.security.PrivateKey;
import java.security.PublicKey;

public interface ICertificateProvider {
	public byte[] decrypt(byte[] encData,PrivateKey privateKey,String algorithm);
	public byte[] decrypt(byte[] encData,PublicKey publicKey,String algorithm);
}
