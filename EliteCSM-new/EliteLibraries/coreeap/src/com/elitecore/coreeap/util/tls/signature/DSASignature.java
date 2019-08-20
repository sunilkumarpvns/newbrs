package com.elitecore.coreeap.util.tls.signature;

import java.security.PrivateKey;
import java.security.PublicKey;

import com.elitecore.coreeap.commons.util.constants.CommonConstants;
import com.elitecore.coreeap.data.tls.TLSSecurityKeys;
import com.elitecore.coreeap.packet.types.tls.TLSException;
import com.elitecore.coreeap.util.constants.tls.HashAlgorithm;
import com.elitecore.coreeap.util.tls.TLSUtility;

public class DSASignature extends com.elitecore.coreeap.util.tls.signature.Signature {
	private static final String MODULE = "DSA_SIGNATURE";
	
	public DSASignature(PrivateKey serverPrivateKey,boolean isTestMode) {
		super(serverPrivateKey,isTestMode);
	}
	
	 /***
     * 
     * 0                    2                   4                   6                    8 (in Bytes)           
     * -----------------------------------------------------------------------------------
     * |  Signature bytes   |          			Signature
     * |       Length       |					  bytes
     * -----------------------------------------------------------------------------------
     *	
     *	above structure shows the return bytes from sign( byte[], TLSSecurityKeys )   
     * 
     */

	@Override
	public byte[] sign(byte[] data, TLSSecurityKeys tlsSecurityKeys) throws TLSException {
			/***
			 * 	According to RFC 2246 
			 * 	Section 7.4.3 Server Key Exchange
			 * 		DSA signature contains 20 byte signed SHA-1 hash.
			 */
			return prependLength(
					super.sign(
							TLSUtility.doHash(
									tlsSecurityKeys.getClientRandom(), 
									tlsSecurityKeys.getServerRandom(), 
									data, 
									HashAlgorithm.SHA1
							), 
							CommonConstants.NONE_WITH_DSA
					)
			);
	}

	@Override
	public boolean verify(byte[] signedBytes, byte[] data, PublicKey publicKey) {
		return super.verify(
				signedBytes, 
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
	
	private byte[] prependLength(byte[] data){
		return TLSUtility.appendBytes(
				new byte[] {
						(byte)(data.length >>> 8), 
						(byte)data.length
				}, 
				data
		);
	}
}
