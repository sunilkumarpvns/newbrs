package com.elitecore.coreeap.util.tls.signature;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.SignatureException;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.coreeap.data.tls.TLSSecurityKeys;
import com.elitecore.coreeap.packet.types.tls.TLSException;

public abstract class Signature{
	private PrivateKey serverPrivateKey;
	private boolean isTestMode;
	
	public Signature(PrivateKey serverPrivateKey, boolean isTestMode) {
		this.serverPrivateKey = serverPrivateKey;
		this.isTestMode = isTestMode;
	}
	
	public abstract boolean isSigningCapable();
	public abstract byte[] sign(byte[] data, TLSSecurityKeys tlsSecurityKeys) throws TLSException;
	public abstract boolean verify(byte[] signatureBytes, byte[] data, PublicKey publicKey);
	public abstract String getModuleName();
	
	protected byte[] sign(byte[] plainBytes, String algorithm) throws TLSException{
		byte[] signatureBytes = null;
		try {
			java.security.Signature signature = java.security.Signature.getInstance(algorithm);
			
			initSignature(signature);
			
			signature.update(plainBytes);
			signatureBytes = signature.sign();
		} catch (InvalidKeyException e) {
			if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
				LogManager.getLogger().trace(getModuleName(), "Invalid Key Exception: " + e.getLocalizedMessage());
			LogManager.getLogger().trace(getModuleName(), e);
			throw new TLSException(e);
		} catch (NoSuchAlgorithmException e) {
			if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
				LogManager.getLogger().trace(getModuleName(), "No Such Algorithm: " + e.getLocalizedMessage());
			LogManager.getLogger().trace(getModuleName(), e);
			throw new TLSException(e);
		} catch (SignatureException e) {
			if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
				LogManager.getLogger().trace(getModuleName(), "Signature Exception: " + e.getLocalizedMessage());
			LogManager.getLogger().trace(getModuleName(), e);
			throw new TLSException(e);
		}
		return signatureBytes;
	}
	
	private void initSignature(java.security.Signature signature) throws NoSuchAlgorithmException, InvalidKeyException {
		if(isTestMode){
			SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
			secureRandom.setSeed(new byte[]{0});
			signature.initSign(serverPrivateKey, secureRandom);
		}else{
			signature.initSign(serverPrivateKey);
		}
		
	}

	public boolean verify(byte[] signedBytes, byte[] verifyBytes, PublicKey publicKey, String algorithm){
		try {
			java.security.Signature signature = java.security.Signature.getInstance(algorithm);
			signature.initVerify(publicKey);
			signature.update(verifyBytes);
			return signature.verify(signedBytes);
		} catch (InvalidKeyException e) {
			if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
				LogManager.getLogger().trace(getModuleName(), "Invalid Key Exception: " + e.getLocalizedMessage());
			LogManager.getLogger().trace(getModuleName(), e);
		} catch (NoSuchAlgorithmException e) {
			if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
				LogManager.getLogger().trace(getModuleName(), "No Such Algorithm: " + e.getLocalizedMessage());
			LogManager.getLogger().trace(getModuleName(), e);
		} catch (SignatureException e) {
			if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
				LogManager.getLogger().trace(getModuleName(), "Signature Exception: " + e.getLocalizedMessage());
			LogManager.getLogger().trace(getModuleName(), e);
		}
		return false;
	}

}