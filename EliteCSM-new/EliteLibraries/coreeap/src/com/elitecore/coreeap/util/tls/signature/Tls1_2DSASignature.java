package com.elitecore.coreeap.util.tls.signature;

import java.security.PrivateKey;
import java.security.PublicKey;

import com.elitecore.coreeap.commons.util.constants.CommonConstants;
import com.elitecore.coreeap.data.tls.TLSSecurityKeys;
import com.elitecore.coreeap.packet.types.tls.TLSException;
import com.elitecore.coreeap.util.constants.tls.HashAlgorithm;
import com.elitecore.coreeap.util.constants.tls.SignatureAlgorithm;
import com.elitecore.coreeap.util.tls.TLSUtility;

public class Tls1_2DSASignature extends com.elitecore.coreeap.util.tls.signature.Signature {
	private static final String MODULE = "TLS1-2_DSA_SIGNATURE";

	public Tls1_2DSASignature(PrivateKey serverPrivateKey, boolean isTestMode) {
		super(serverPrivateKey,isTestMode);
	}
	
	/***
     * 
     * 0                    2                   4                   6                    8 (in Bytes)           
     * -----------------------------------------------------------------------------------
     * | hash     signature |  Signature bytes  |          		Signature
     * | algo		algo	|       Length      |				  bytes
     * -----------------------------------------------------------------------------------
     *	
     *	above structure shows the return bytes from sign( byte[], TLSSecurityKeys )   
     * 
     */
	
	@Override
	public byte[] sign(byte[] data, TLSSecurityKeys tlsSecurityKeys) throws TLSException {
		return prependAlgorithmAndLength(
				super.sign(
						TLSUtility.doHash(
								tlsSecurityKeys.getClientRandom(), 
								tlsSecurityKeys.getServerRandom(), 
								data, 
								HashAlgorithm.SHA1
						), 
						CommonConstants.NONE_WITH_DSA
				), 
				HashAlgorithm.SHA1
		);
	}

	@Override
	public boolean verify(byte[] signatureBytes, byte[] data, PublicKey publicKey) {
		return super.verify(
				signatureBytes, 
				TLSUtility.doHash(
						null, 
						null, 
						data, 
						HashAlgorithm.SHA1
				), 
				publicKey, 
				CommonConstants.NONE_WITH_DSA
		);
	}
		
	@Override
	public boolean isSigningCapable() {
		return true;
	}

	@Override
	public String getModuleName() {
		return MODULE;
	}
	
	private byte[] prependAlgorithmAndLength(byte[] data, HashAlgorithm hash){
		return TLSUtility.appendBytes(
				new byte[] {
						(byte)hash.getValue(), 
						(byte)SignatureAlgorithm.DSA.getValue(), 
						(byte)(data.length >>> 8), 
						(byte)data.length
				}, 
				data
		);
	}
}
