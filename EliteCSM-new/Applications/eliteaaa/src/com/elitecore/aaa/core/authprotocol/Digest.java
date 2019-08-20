/*
 * @(#)DigestRequestHandler	04/08/2008
 * Elitecore Technologies Ltd.
 */
package com.elitecore.aaa.core.authprotocol;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import com.elitecore.aaa.core.conf.DigestConfiguration;
import com.elitecore.aaa.core.data.AccountData;
import com.elitecore.aaa.core.digest.session.DigestSession;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.attributes.StringAttribute;
import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;
import com.elitecore.coreradius.util.digest.DigestConstants;
import com.elitecore.coreradius.util.digest.DigestUtil;

public class Digest {
	
	public static final String MODULE = "DIGEST REQUEST HANDLER";
	private static final int MPPE_RECV_KEY = 17;
	
	public static ArrayList<IRadiusAttribute> handleDigestRequest(HashMap<Integer,String> digestRequestAttributes, String strSharedSecret, AccountData accountData, DigestSession digestSession, byte[] authenticator){ 
		
		ArrayList<IRadiusAttribute> digestResponseAttributes = new ArrayList<IRadiusAttribute>();		
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(MODULE, "User profile for Digest authentication: " + accountData);	

		String radAttrUsername = (String) digestRequestAttributes.get(RadiusAttributeConstants.USER_NAME);
		String realm = (String) digestRequestAttributes.get(RadiusAttributeConstants.DIGEST_REALM);
		digestSession.setUserName(radAttrUsername);			

		byte[] ha1 = null;
		try {
			String nonce = (String)digestRequestAttributes.get(RadiusAttributeConstants.DIGEST_NONCE);
			String cNonce = (String)digestRequestAttributes.get(RadiusAttributeConstants.DIGEST_CNONCE);
			String digestalgorithm = (String)digestRequestAttributes.get(RadiusAttributeConstants.DIGEST_ALGORITHM);
			ha1 = DigestUtil.getHA1(radAttrUsername,realm,accountData.getPassword(),digestalgorithm,nonce,cNonce);
		} catch (IOException e1) {
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "Failed to generate HA1. Reason: " + e1.getMessage());
			LogManager.getLogger().trace(MODULE, e1);
			digestSession.setRadiusPacketType(-1);
			return null;
		}
		digestSession.setParameter(DigestConstants.DIGEST_HA1,ha1);
		String radAttrNonce = (String)digestRequestAttributes.get(RadiusAttributeConstants.DIGEST_NONCE);
		String radAttrRealm = (String)digestRequestAttributes.get(RadiusAttributeConstants.DIGEST_REALM);
		String nonce = (String)digestRequestAttributes.get(RadiusAttributeConstants.DIGEST_NONCE);

		if(nonce.equals(radAttrNonce) && realm.equals(radAttrRealm)){
			byte[] calculatedDigest = null;
			try {
				calculatedDigest = getDigest(digestRequestAttributes,digestSession);
				String calculatedDigestInHex = DigestUtil.bytesToHex(calculatedDigest);
				String receivedDigestResponse = (String)digestRequestAttributes.get(RadiusAttributeConstants.DIGEST_RESPONSE);					
				if(calculatedDigestInHex.equalsIgnoreCase(receivedDigestResponse)){
					ha1 = (byte[])digestSession.getParameter(DigestConstants.DIGEST_HA1);
					digestSession.setRadiusPacketType(RadiusConstants.ACCESS_ACCEPT_MESSAGE);
					byte[] digestResponseAuth = getResponseAuth(digestRequestAttributes,digestSession);
					String digestResponseAuthInHex = DigestUtil.bytesToHex(digestResponseAuth);
					if(!((Boolean)digestSession.getParameter(DigestConstants.DRAFT_BASED_DIGEST_REQUEST)).booleanValue()){

						IRadiusAttribute mppeRecvKey = (IRadiusAttribute)Dictionary.getInstance().getAttribute(RadiusConstants.MICROSOFT_VENDOR_ID,MPPE_RECV_KEY);
						byte[] salt = {1,0};
						byte[] encMppeRecvKeyValue = DigestUtil.encryptKeyRFC2868(ha1,strSharedSecret,authenticator,salt);
						byte[] keyValue = new byte[salt.length + encMppeRecvKeyValue.length];
						System.arraycopy(salt,0,keyValue,0,salt.length);
						System.arraycopy(encMppeRecvKeyValue,0,keyValue,salt.length,encMppeRecvKeyValue.length);
						mppeRecvKey.setValueBytes(keyValue);

						digestResponseAttributes.add(mppeRecvKey);

						IRadiusAttribute digestResponseAuthAttr = (IRadiusAttribute)Dictionary.getInstance().getAttribute(RadiusAttributeConstants.DIGEST_RESPONSE_AUTH);
						digestResponseAuthAttr.setStringValue(digestResponseAuthInHex);
						digestResponseAttributes.add(digestResponseAuthAttr);
					}else{
						IRadiusAttribute mppeRecvKey = (IRadiusAttribute)Dictionary.getInstance().getAttribute(RadiusConstants.MICROSOFT_VENDOR_ID,MPPE_RECV_KEY);
						byte[] salt = {1,0};
						byte[] encMppeRecvKeyValue = DigestUtil.encryptKeyRFC2868(ha1,strSharedSecret,authenticator,salt);
						byte[] keyValue = new byte[salt.length + encMppeRecvKeyValue.length];
						System.arraycopy(salt,0,keyValue,0,salt.length);
						System.arraycopy(encMppeRecvKeyValue,0,keyValue,salt.length,encMppeRecvKeyValue.length);
						mppeRecvKey.setValueBytes(keyValue);

						digestResponseAttributes.add(mppeRecvKey);

						IRadiusAttribute digestResponseAuthAttr = (IRadiusAttribute)Dictionary.getInstance().getAttribute(RadiusAttributeConstants.DIGEST_RESPONSE_AUTH);
						digestResponseAuthAttr.setStringValue(digestResponseAuthInHex);
						digestResponseAttributes.add(digestResponseAuthAttr);
					}
				}else{
					if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
						LogManager.getLogger().warn(MODULE, "Invalid Digest Response. Expected:" + calculatedDigestInHex + ", received: " + receivedDigestResponse);
					digestSession.setRadiusPacketType(RadiusConstants.ACCESS_REJECT_MESSAGE);
					digestSession.setFailureReason("Invalid Digest Password");
				}
				digestSession.setRemoveSession(true);
			} catch (IOException e) {
				if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
					LogManager.getLogger().warn(MODULE, "Failed to generate digest. Reason: " + e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
				digestSession.setRadiusPacketType(-1);
			}
		}else{
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "Invalid digest request, Reason: Received invalid nonce or realm.");
			digestSession.setRadiusPacketType(-1);
			digestSession.setRemoveSession(true);
		}
		return digestResponseAttributes;
	}
		
	public static ArrayList<IRadiusAttribute> generateChallengeRequest(DigestConfiguration digestConfig, DigestSession digestSession){		
		
		ArrayList<IRadiusAttribute> digestResponseAttributes = new ArrayList<IRadiusAttribute>();	
			if(digestSession.getRadiusPacketType() != -1){				
				if(!((Boolean)digestSession.getParameter(DigestConstants.DRAFT_BASED_DIGEST_REQUEST)).booleanValue()){
					//add Digest Nonce Attribute = 105
					IRadiusAttribute digestNonce = (IRadiusAttribute)Dictionary.getInstance().getAttribute(RadiusAttributeConstants.DIGEST_NONCE);
					String nonce =  generateDigestNonce(digestConfig,digestSession);
					digestSession.setParameter(DigestConstants.DIGEST_NONCE, nonce);
					digestNonce.setStringValue(nonce);
					digestResponseAttributes.add(digestNonce);
					
					//add Digest Realm Attribute = 104
					IRadiusAttribute digestRealm = (IRadiusAttribute)Dictionary.getInstance().getAttribute(RadiusAttributeConstants.DIGEST_REALM);
					digestRealm.setStringValue(digestConfig.getRealm()); 
					digestResponseAttributes.add(digestRealm);
					digestSession.setParameter(DigestConstants.DIGEST_REALM,digestConfig.getRealm()); 
					
					//add 2 Digest qop Attribute = 110
					IRadiusAttribute digestQoP = (IRadiusAttribute)Dictionary.getInstance().getAttribute(RadiusAttributeConstants.DIGEST_QOP);
					String defaultQoP = "auth";
					if(digestConfig.getDefaultQOP()!= null) { //this.configurationParamters.get(DigestConfiguration.DEFAULT_DIGEST_QOP) != null){
						defaultQoP = digestConfig.getDefaultQOP();     //(String )this.configurationParamters.get(DigestConfiguration.DEFAULT_DIGEST_QOP);
					}				
					digestQoP.setStringValue(defaultQoP);
					digestResponseAttributes.add(digestQoP);
					
					//add Digest Algorithm Attribute = 
					IRadiusAttribute digestAlgorithm = (IRadiusAttribute)Dictionary.getInstance().getAttribute(RadiusAttributeConstants.DIGEST_ALGORITHM);
					digestAlgorithm.setStringValue(digestConfig.getDefaultAlgorithm());//(String)this.configurationParamters.get(DigestConfiguration.DEFAULT_DIGEST_ALGORITHM));
					digestResponseAttributes.add(digestAlgorithm);
					
					//add Digest Opaque Attribute = 
	//				IRadiusAttribute digestOpaque = (IRadiusAttribute)Dictionary.getInstance().getAttribute(RadiusAttributeConstants.DIGEST_OPAQUE);
	//				digestOpaque.setStringValue((String)this.configurationParamters.get(DigestConfiguration.DIGEST_OPAQUE));						
	//				digestResponseAttributes.add(digestOpaque);
				}else{
					byte[] digestAttributeBytes = null;				
					
	//				add Digest Nonce Attribute = 2
					StringAttribute stringAttribute = new StringAttribute();
					stringAttribute.setType(DigestConstants.DIGEST_DRAFT_NONCE);
					String nonce =  generateDigestNonce(digestConfig,digestSession);
					digestSession.setParameter(DigestConstants.DIGEST_NONCE, nonce);
					stringAttribute.setStringValue(nonce);		
					digestAttributeBytes = DigestUtil.appendBytes(digestAttributeBytes,stringAttribute.getBytes());
					
					//add Digest Realm Attribute = 104				
					stringAttribute = new StringAttribute();
					stringAttribute.setType(DigestConstants.DIGEST_DRAFT_REALM);				
					stringAttribute.setStringValue(digestConfig.getRealm()); 				
					digestSession.setParameter(DigestConstants.DIGEST_REALM,digestConfig.getRealm()); 
					digestAttributeBytes = DigestUtil.appendBytes(digestAttributeBytes,stringAttribute.getBytes());
					
					//add 2 Digest qop Attribute = 110				
					stringAttribute = new StringAttribute();
					stringAttribute.setType(DigestConstants.DIGEST_DRAFT_QOP);
					String defaultQoP = "auth";
					if(digestConfig.getDefaultQOP() !=null){ 
						defaultQoP = digestConfig.getDefaultQOP(); 
					}
					stringAttribute.setStringValue(defaultQoP);
					digestAttributeBytes = DigestUtil.appendBytes(digestAttributeBytes,stringAttribute.getBytes());
					
					//add Digest Algorithm Attribute =				
					stringAttribute = new StringAttribute();
					stringAttribute.setType(DigestConstants.DIGEST_DRAFT_ALGORITHM);
					stringAttribute.setStringValue(digestConfig.getDefaultAlgorithm());
					digestAttributeBytes = DigestUtil.appendBytes(digestAttributeBytes,stringAttribute.getBytes());													
					
					
					IRadiusAttribute digestAttribute = (IRadiusAttribute)Dictionary.getInstance().getAttribute(RadiusAttributeConstants.DRAFT_DIGEST_ATTRIBUTES);
					digestAttribute.setValueBytes(digestAttributeBytes);
					digestResponseAttributes.add(digestAttribute);
				}
			}			
		return(digestResponseAttributes);
	}
	
	
	private static String generateDigestNonce(DigestConfiguration digestConfig, DigestSession digestSession) {
		

		int nonceLength = digestConfig.getDefaultNonceLength();
		if(nonceLength == 0 ){
		}		
		String digestNonce = DigestUtil.generateDigestNonce(nonceLength);		
		digestSession.setParameter(RadiusAttributeConstants.DIGEST_NONCE_STR,digestNonce);
		return digestNonce;
	}
	

	private static byte[] getDigest(HashMap<Integer,String> digestRequestAttributes,DigestSession digestSession)throws IOException{
	    byte digest[] = new byte[16];	    
	    byte[] ha1 = null;		
		String nonce = (String)digestRequestAttributes.get(RadiusAttributeConstants.DIGEST_NONCE);
		ha1 = (byte[])digestSession.getParameter(DigestConstants.DIGEST_HA1);
		String ha1Hex = DigestUtil.bytesToHex(ha1);
	    String radAttrQoP = (String)digestRequestAttributes.get(RadiusAttributeConstants.DIGEST_QOP);
    	String digestMethod = (String)digestRequestAttributes.get(RadiusAttributeConstants.DIGEST_METHOD);
    	String radAttrDigestURI = (String)digestRequestAttributes.get(RadiusAttributeConstants.DIGEST_URI);
    	String digestEntityBodyHash = (String)digestRequestAttributes.get(RadiusAttributeConstants.DIGEST_ENTITY_BODY_HASH);
    	String nonceCount = (String)digestRequestAttributes.get(RadiusAttributeConstants.DIGEST_NONCE_COUNT);
    	String cNonce = (String)digestRequestAttributes.get(RadiusAttributeConstants.DIGEST_CNONCE);
    	digest = DigestUtil.getDigest(ha1Hex, radAttrQoP, digestMethod, radAttrDigestURI, nonceCount, nonce, cNonce, digestEntityBodyHash);		
		return(digest);
    }

    private static byte[] getResponseAuth(HashMap<Integer,String> digestRequestAttributes, DigestSession digestSession) throws IOException{
	    byte authResponse[] = new byte[16];
	    String radAttrQoP = (String)digestRequestAttributes.get(RadiusAttributeConstants.DIGEST_QOP);
	    String radAttrUri = (String)digestRequestAttributes.get(RadiusAttributeConstants.DIGEST_URI);
	    String radAttrEntityBodyHash = (String)digestRequestAttributes.get(RadiusAttributeConstants.DIGEST_ENTITY_BODY_HASH);
	    String radAttrNonceCount = (String)digestRequestAttributes.get(RadiusAttributeConstants.DIGEST_NONCE_COUNT);
	    String radAttrCNonce = (String)digestRequestAttributes.get(RadiusAttributeConstants.DIGEST_CNONCE);
	    String nonce = (String)digestRequestAttributes.get(RadiusAttributeConstants.DIGEST_NONCE);
		byte[] ha1 = (byte[])digestSession.getParameter(DigestConstants.DIGEST_HA1);
		String ha1Hex = DigestUtil.bytesToHex(ha1);
	    authResponse = 	DigestUtil.getResponseAuth(ha1Hex, radAttrQoP, radAttrUri, radAttrEntityBodyHash, radAttrNonceCount, radAttrCNonce, nonce);
		return(authResponse);
	}

}
