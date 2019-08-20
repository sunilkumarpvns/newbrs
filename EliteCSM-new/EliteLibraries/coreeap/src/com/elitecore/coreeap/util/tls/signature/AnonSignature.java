package com.elitecore.coreeap.util.tls.signature;

import java.security.PrivateKey;
import java.security.PublicKey;

import com.elitecore.coreeap.data.tls.TLSSecurityKeys;

public class AnonSignature extends com.elitecore.coreeap.util.tls.signature.Signature{
	private static final String MODULE = "ANONYMUS_SIGNATURE";

	public AnonSignature(PrivateKey serverPrivateKey,boolean isTestMode) {
		super(serverPrivateKey,isTestMode);
	}

	@Override
	public boolean verify(byte[] signatureBytes, byte[] data, PublicKey publicKey) {
		throw new UnsupportedOperationException("Anonymous does not require verification");
	}

	@Override
	public byte[] sign(byte[] data, TLSSecurityKeys tlsSecurityKeys) {
		throw new UnsupportedOperationException("Anonymous does not have signing capability");
	}

	@Override
	public boolean isSigningCapable() {
		return false;
	}

	@Override
	public String getModuleName() {
		return MODULE;
	}

}
