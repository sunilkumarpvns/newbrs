package com.elitecore.coreeap.util.tls.keyexchange;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;

import javax.crypto.KeyAgreement;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.DHPublicKeySpec;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.coreeap.commons.util.constants.CommonConstants;

public class DHKeyExchange implements KeyExchange {
	
	private static final String MODULE = "DH_KEY_EXCHANGE";
	private static final byte[] STATIC_Y_VALUE = {0, -94, 31, 9, -24, -118, -18, 90, -112, -124, -37, 66, 62, 11, 39, 102, 108, -52, -88, -11, 31, 20, 13, -73, -61, -66, -41, 73, -20, 42, -122, -79, 32, -61, -39, -32, -50, 36, -14, -35, 43, -85, -95, -70, 47, 14, 119, 24, 117, -114, -105, 116, 113, 99, 88, -100, 108, -50, 53, -20, 11, -13, 17, 119, -99};
	private static final byte[] STATIC_PRIVATE_KEY = {48, -127, -45, 2, 1, 0, 48, -127, -104, 6, 9, 42, -122, 72, -122, -9, 13, 1, 3, 1, 48, -127, -118, 2, 65, 0, -65, 36, 77, -110, -106, -37, -10, -119, -83, -121, 16, -115, -5, 68, -56, 127, -121, 4, 76, 62, -92, -63, 46, 120, 122, -32, -46, -86, -117, -120, 34, 86, -62, 17, -29, 103, 97, -104, 80, -96, 81, -68, 82, 50, -72, -27, -47, 109, -44, -30, -108, 94, 69, 54, -69, 6, 79, -18, 37, 16, -49, 127, 2, 99, 2, 65, 0, -65, 2, -53, -87, 89, -74, -69, -22, -28, -54, -14, -111, 90, -100, -103, -79, -95, 102, -118, 107, -91, 21, -128, 15, 11, -106, 124, 119, -10, 125, 44, -64, -37, -20, -60, 101, -82, 14, 87, 95, 3, -78, -50, -90, 90, 78, -47, -26, -52, 10, 12, -94, 56, 18, -122, 72, 59, 65, 74, -53, 77, -14, -76, -63, 2, 2, 1, -128, 4, 51, 2, 49, 0, -59, -16, -47, -75, -15, -19, -7, -116, -4, -50, 104, -83, 86, 81, 41, 100, 6, -121, 126, -37, 77, 82, -126, 71, -120, -4, 32, -65, 104, -106, 13, -36, 107, -5, 80, 112, -15, 117, 37, 1, -30, -39, -121, -122, 74, -97, 83, 1};
	private static final int P_G_YS_LENGTH = 6;
	
	/***
	 *  Static Diffie-Hellman parameters with size 512 bits (64 bytes).  
	 */
	private static final byte[] DEFAULT_P_VALUE_64_BYTES = {-65, 36, 77, -110, -106, -37, -10, -119, -83, -121, 16, -115, -5, 68, -56, 127, -121, 4, 76, 62, -92, -63, 46, 120, 122, -32, -46, -86, -117, -120, 34, 86, -62, 17, -29, 103, 97, -104, 80, -96, 81, -68, 82, 50, -72, -27, -47, 109, -44, -30, -108, 94, 69, 54, -69, 6, 79, -18, 37, 16, -49, 127, 2, 99};
	private static final byte[] DEFAULT_G_VALUE_64_BYTES = {-65, 2, -53, -87, 89, -74, -69, -22, -28, -54, -14, -111, 90, -100, -103, -79, -95, 102, -118, 107, -91, 21, -128, 15, 11, -106, 124, 119, -10, 125, 44, -64, -37, -20, -60, 101, -82, 14, 87, 95, 3, -78, -50, -90, 90, 78, -47, -26, -52, 10, 12, -94, 56, 18, -122, 72, 59, 65, 74, -53, 77, -14, -76, -63};
	private byte[] p = DEFAULT_P_VALUE_64_BYTES;
	private byte[] g = DEFAULT_G_VALUE_64_BYTES;
	
	private byte[] y_s;
	private KeyPair keyPair;
	private boolean isTestMode;
	
	public DHKeyExchange(boolean isTestMode) {
		this.isTestMode = isTestMode;
	}
	
	/**
	 * This constructor MUST only be used in the testing environment and not in production code
	 * @param p
	 * @param g
	 */
	public DHKeyExchange(byte[] p, byte[] g){
		this(true);
		this.p = p;
		this.g = g;
	}
	
	@Override
	public byte[] generateParameters() {
		byte[] serverParams = null;
		int serverParamsIndex = 0;
		
		try {
			y_s = generateY();
			
			serverParams= new byte[p.length + g.length + y_s.length + P_G_YS_LENGTH];
			serverParams[serverParamsIndex++] = (byte)(p.length >>> 8);
			serverParams[serverParamsIndex++] = (byte)(p.length);
			System.arraycopy(p, 0, serverParams, serverParamsIndex, p.length);
			serverParamsIndex += p.length;
			serverParams[serverParamsIndex++] = (byte)(g.length >>> 8);
			serverParams[serverParamsIndex++] = (byte)(g.length);
			System.arraycopy(g, 0, serverParams, serverParamsIndex, g.length);
			serverParamsIndex += g.length;
			serverParams[serverParamsIndex++] = (byte)(y_s.length >>> 8);
			serverParams[serverParamsIndex++] = (byte)(y_s.length);
			System.arraycopy(y_s, 0, serverParams, serverParamsIndex, y_s.length);
			serverParamsIndex += y_s.length;
			
		} catch (InvalidAlgorithmParameterException e) {
			if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
				LogManager.getLogger().trace(MODULE, "Invalid Algorithm Parameter: " + e.getLocalizedMessage());
			LogManager.getLogger().trace(MODULE, e);
		} catch (NoSuchAlgorithmException e) {
			if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
				LogManager.getLogger().trace(MODULE, "No Such Algorithm: " + e.getLocalizedMessage());
			LogManager.getLogger().trace(MODULE, e);
		}
		return serverParams;
	}

	private byte[] generateY() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException {
		if(isTestMode()){
			return STATIC_Y_VALUE;
		}
		
		KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(CommonConstants.DH);
		keyPairGenerator.initialize(new DHParameterSpec(new BigInteger(1, p),new BigInteger(1, g)));
		keyPair = keyPairGenerator.generateKeyPair();
		DHPublicKey dhPublicKey = (DHPublicKey) keyPair.getPublic();
		return dhPublicKey.getY().toByteArray();
	}

	@Override
	public byte[] generatePMS(byte[] keyExchangeValue) {
		byte[] pMS = null;
		
		try {
			
			BigInteger dh_yc = new BigInteger(1,keyExchangeValue);
			
			PrivateKey privateKey = generatePrivateKey();
			
			PublicKey senderPublicKey = KeyFactory.getInstance(CommonConstants.DH).generatePublic(new DHPublicKeySpec(dh_yc, new BigInteger(1,p), new BigInteger(1,g)));
			KeyAgreement keyAgreement = KeyAgreement.getInstance(CommonConstants.DH);
			keyAgreement.init(privateKey);
			keyAgreement.doPhase(senderPublicKey, true);
			pMS = keyAgreement.generateSecret();

		} catch (NoSuchAlgorithmException e) {
			if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
				LogManager.getLogger().trace(MODULE, "No Such Algorithm: " + e.getLocalizedMessage());
			LogManager.getLogger().trace(MODULE, e);
		} catch (InvalidKeyException e) {
			if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
				LogManager.getLogger().trace(MODULE, "Invalid Key Exception: " + e.getLocalizedMessage());
			LogManager.getLogger().trace(MODULE, e);
		} catch (InvalidKeySpecException e) {
			if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
				LogManager.getLogger().trace(MODULE, "Invalid Keyspec Exception: " + e.getLocalizedMessage());
			LogManager.getLogger().trace(MODULE, e);
		} catch (IllegalStateException e) {
			if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
				LogManager.getLogger().trace(MODULE, "Illegal State Exception: " + e.getLocalizedMessage());
			LogManager.getLogger().trace(MODULE, e);
		}

		return pMS;
	}
	
	private PrivateKey generatePrivateKey() throws NoSuchAlgorithmException, InvalidKeySpecException{
		if(isTestMode()){
			PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(STATIC_PRIVATE_KEY);
			KeyFactory keyFactory = KeyFactory.getInstance(CommonConstants.DH);
			return keyFactory.generatePrivate(spec);
		}
		
		return keyPair.getPrivate();
		
	}
	
	private boolean isTestMode(){
		return this.isTestMode;
	}
}
