package com.elitecore.aaa.radius.service.auth.handlers;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.elitecore.aaa.core.authprotocol.BaseAuthMethodHandler;
import com.elitecore.aaa.core.authprotocol.Digest;
import com.elitecore.aaa.core.authprotocol.exception.AuthenticationFailedException;
import com.elitecore.aaa.core.authprotocol.exception.UserNotFoundException;
import com.elitecore.aaa.core.conf.DigestConfiguration;
import com.elitecore.aaa.core.conf.impl.DigestConfigurationImpl;
import com.elitecore.aaa.core.constant.AuthMethods;
import com.elitecore.aaa.core.data.AccountData;
import com.elitecore.aaa.core.digest.session.DigestSession;
import com.elitecore.aaa.core.subscriber.IAccountInfoProvider;
import com.elitecore.aaa.radius.service.auth.RadAuthRequest;
import com.elitecore.aaa.radius.service.auth.RadAuthResponse;
import com.elitecore.aaa.radius.service.auth.RadAuthServiceContext;
import com.elitecore.aaa.radius.service.auth.constant.AuthReplyMessageConstant;
import com.elitecore.aaa.radius.service.auth.policy.handlers.conf.AuthenticationHandlerData;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.attributes.StringAttribute;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;
import com.elitecore.coreradius.util.digest.DigestConstants;
import com.elitecore.passwordutil.DecryptionFailedException;
import com.elitecore.passwordutil.DecryptionNotSupportedException;
import com.elitecore.passwordutil.NoSuchEncryptionException;
import com.elitecore.passwordutil.PasswordEncryption;

public class RadDigestHandler extends BaseAuthMethodHandler implements IRadAuthMethodHandler {

	public static final String MODULE = "RAD_DIGEST_HANDLER";
	private Map<String,DigestSession> digestSessionMap;
	private DigestConfiguration digestConfig;
	
	public RadDigestHandler(RadAuthServiceContext serviceContext, AuthenticationHandlerData data) {
		super(serviceContext);
		this.digestSessionMap = Collections.synchronizedMap(new HashMap<String,DigestSession>(100));
		String digestConfigId = data.getDigestConfigId();
		
		if(serviceContext.getAuthConfiguration().getDigestConfiguration(digestConfigId) !=null)
			this.digestConfig = serviceContext.getAuthConfiguration().getDigestConfiguration(digestConfigId) ;
		else
			//digestConfig = new DigestConfigurationImpl(serviceContext.getServerContext(),digestConfigId);
			digestConfig = new DigestConfigurationImpl();
		
		
	}
	
	@Override
	public void init() throws InitializationFailedException{
		// reading done in Configuration class;
	}
	
	@Override
	public void handleRequest(RadAuthRequest radiusServiceRequest, RadAuthResponse radiusServiceResponse, IAccountInfoProvider accountInfoProvider) throws AuthenticationFailedException {
		
		IRadiusAttribute username = radiusServiceRequest.getRadiusAttribute(RadiusAttributeConstants.USER_NAME);
		DigestSession digestSession = digestSessionMap.get(username.getStringValue());		

		if(digestSession == null){
			digestSession = new  DigestSession(username.getStringValue());
			digestSessionMap.put(username.getStringValue(),digestSession);
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "New Digest session created for user: " + username.getStringValue());
		}else{
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Using existing Digest session for user: " + username.getStringValue());
		}

		HashMap<Integer,String> digestRequestAttributes = null;
		ArrayList<IRadiusAttribute> digestResponseAttributes =null;
		if((digestRequestAttributes=validDigestAuthenticationRequest(radiusServiceRequest,digestConfig)) != null){	 
			AccountData accountData = accountInfoProvider.getAccountData(radiusServiceRequest,radiusServiceResponse);
			if(accountData == null){
				throw new UserNotFoundException();
			}
			String decodedPassword = null;
			int encryptionType = 0;
			try {
				if(accountData.getEncryptionType() != null)
					encryptionType =Integer.parseInt(accountData.getEncryptionType());
				decodedPassword = PasswordEncryption.getInstance().decrypt(accountData.getPassword(), encryptionType);
			} catch (NumberFormatException e) {
				throw new AuthenticationFailedException("Authentication failed due to invalid encryption type.",e);
			} catch (NoSuchEncryptionException e) {
				throw new AuthenticationFailedException("Authentication failed due to UnSupported Password Encryption Type.",e);
			} catch (DecryptionNotSupportedException e) {
				throw new AuthenticationFailedException("Authentication failed due to improper password encryption format.",e);
			} catch (DecryptionFailedException e) {
				throw new AuthenticationFailedException("Authentication failed due to exception in decryption.",e);
			}
			if(decodedPassword == null){
				radiusServiceResponse.setFurtherProcessingRequired(false);
				radiusServiceResponse.setResponseMessage(AuthReplyMessageConstant.DIGEST_FAILURE);	
				radiusServiceResponse.setPacketType(RadiusConstants.ACCESS_REJECT_MESSAGE);
			}
			accountData.setPassword(decodedPassword);
			accountData.setEncryptionType("0");

			radiusServiceRequest.setAccountData(accountData);			
			digestResponseAttributes = Digest.handleDigestRequest(digestRequestAttributes,radiusServiceResponse.getClientData().getSharedSecret(radiusServiceRequest.getPacketType()),accountData,digestSession,radiusServiceRequest.getAuthenticator());
		}else{
			digestResponseAttributes = Digest.generateChallengeRequest(digestConfig, digestSession); 
		}	

		if(digestSession.getRadiusPacketType() != -1){
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Digest authentication response attributes : " + digestResponseAttributes + "\n");

			if(digestSession.getRadiusPacketType() == RadiusConstants.ACCESS_ACCEPT_MESSAGE){
				radiusServiceResponse.setPacketType(RadiusConstants.ACCESS_ACCEPT_MESSAGE);
				radiusServiceResponse.setResponseMessage(AuthReplyMessageConstant.AUTHENTICATION_SUCCESS);
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "Packet type for Digest response: ACCESS_ACCEPT");

				if(digestSession.isRemoveSession()){				
					digestSessionMap.remove(digestSession.getUserName());
					if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
						LogManager.getLogger().trace(MODULE,"Digest session removed for: " + digestSession.getUserName());
				}
			}else if(digestSession.getRadiusPacketType() == RadiusConstants.ACCESS_CHALLENGE_MESSAGE){
				radiusServiceResponse.setPacketType(RadiusConstants.ACCESS_CHALLENGE_MESSAGE);
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "Packet type for Digest response: ACCESS_CHALLENGE");
				radiusServiceResponse.setFurtherProcessingRequired(false);

			}else if(digestSession.getRadiusPacketType() == RadiusConstants.ACCESS_REJECT_MESSAGE){
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "Packet type for Digest response: ACCESS_REJECT");

				String failureReason = digestSession.getFailureReason();
				if(failureReason == null || failureReason.length() <= 0){
					failureReason = AuthReplyMessageConstant.DIGEST_FAILURE; 
				}

				if(digestSession.isRemoveSession()){
					digestSessionMap.remove(digestSession.getUserName());
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE,"Digest session removed for : " + digestSession.getUserName());
				}	
				radiusServiceResponse.setFurtherProcessingRequired(false);
				radiusServiceResponse.setResponseMessage(failureReason);
				radiusServiceResponse.setPacketType(RadiusConstants.ACCESS_REJECT_MESSAGE);
				return;
			}

			Iterator<IRadiusAttribute> digestResponseAttributesIterator = digestResponseAttributes.iterator();
			while(digestResponseAttributesIterator.hasNext()){
				IRadiusAttribute radAttr = digestResponseAttributesIterator.next();
				radiusServiceResponse.addAttribute(radAttr);
			}
		}else if(digestSession.getRadiusPacketType() == -1){
			LogManager.getLogger().warn(MODULE, "Invalid request received");
			radiusServiceResponse.setPacketType(-1);
			radiusServiceResponse.markForDropRequest();
		}else{
			radiusServiceResponse.setFurtherProcessingRequired(false);
			radiusServiceResponse.setResponseMessage(AuthReplyMessageConstant.INVALID_PACKETTYPE_FOR_DIGEST_RESPONSE_PACKET);			
			radiusServiceResponse.setPacketType(RadiusConstants.ACCESS_REJECT_MESSAGE);
			return;
		}		
	}

	private HashMap<Integer,String> validDigestAuthenticationRequest(RadAuthRequest radiusServiceRequest,DigestConfiguration digestConfig){			
		/*if(digestConfig == null){
			throw new InvalidRequestException("Digest Configuration not found.");
		}*/
		/***
		 * here there are 5 parameter which is required in digest authentication
		 * (1) username - as defualt in radius 
		 * (2) realm
		 * (3) nonce
		 * (4) digest-uri
		 * (5) response
		 * (6) QOP
		 * (7) Algorithm 
		 * So here if the counter reaches to the 4 means it is a valid request.
		 */
		int Counter = 0,requiredNumberOfDigestAttribute=4;
		boolean draftBasedAuthentication = false;		
		Collection<IRadiusAttribute> requestAttributes = radiusServiceRequest.getRadiusAttributes();		
		HashMap<Integer,String> digestAttributes = new HashMap<Integer,String>();
		Iterator<IRadiusAttribute> requestAttributesIterator = requestAttributes.iterator();
		IRadiusAttribute username = radiusServiceRequest.getRadiusAttribute(RadiusAttributeConstants.USER_NAME);
		DigestSession digestSession = digestSessionMap.get(username.getStringValue());
		if(digestConfig==null){
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "Digest configuration parameter not found.");
			digestSession.setRadiusPacketType(-1);
			return null;
		}
		while(requestAttributesIterator.hasNext()){
			IRadiusAttribute radiusAttribute = requestAttributesIterator.next();
			switch(radiusAttribute.getID()){			
			case RadiusAttributeConstants.DIGEST_AKA_AUTS :	
				digestAttributes.put(RadiusAttributeConstants.DIGEST_AKA_AUTS,radiusAttribute.getStringValue());
				break;
			case RadiusAttributeConstants.DIGEST_ALGORITHM:
				digestAttributes.put(RadiusAttributeConstants.DIGEST_ALGORITHM,radiusAttribute.getStringValue());
				break;
			case RadiusAttributeConstants.DIGEST_CNONCE:
				digestAttributes.put(RadiusAttributeConstants.DIGEST_CNONCE,radiusAttribute.getStringValue());
				Counter++;
				break;
			case RadiusAttributeConstants.DIGEST_DOMAIN:
				digestAttributes.put(RadiusAttributeConstants.DIGEST_DOMAIN,radiusAttribute.getStringValue());
				break;
			case RadiusAttributeConstants.DIGEST_ENTITY_BODY_HASH:
				digestAttributes.put(RadiusAttributeConstants.DIGEST_ENTITY_BODY_HASH,radiusAttribute.getStringValue());
				Counter++;
				break;
			case RadiusAttributeConstants.DIGEST_HA1:
				digestAttributes.put(RadiusAttributeConstants.DIGEST_HA1,radiusAttribute.getStringValue());
				break;
			case RadiusAttributeConstants.DIGEST_METHOD:
				digestAttributes.put(RadiusAttributeConstants.DIGEST_METHOD,radiusAttribute.getStringValue());
				Counter++;
				break;
			case RadiusAttributeConstants.DIGEST_NEXTNONCE:
				digestAttributes.put(RadiusAttributeConstants.DIGEST_NEXTNONCE,radiusAttribute.getStringValue());
				break;
			case RadiusAttributeConstants.DIGEST_NONCE:
				digestAttributes.put(RadiusAttributeConstants.DIGEST_NONCE,radiusAttribute.getStringValue());				
				Counter ++;
				break;
			case RadiusAttributeConstants.DIGEST_NONCE_COUNT:
				digestAttributes.put(RadiusAttributeConstants.DIGEST_NONCE_COUNT,radiusAttribute.getStringValue());
				Counter++;
				break;
			case RadiusAttributeConstants.DIGEST_OPAQUE:
				digestAttributes.put(RadiusAttributeConstants.DIGEST_OPAQUE,radiusAttribute.getStringValue());
				break;
			case RadiusAttributeConstants.DIGEST_QOP:
				digestAttributes.put(RadiusAttributeConstants.DIGEST_QOP,radiusAttribute.getStringValue());
				if(radiusAttribute.getStringValue().equals("auth-int")){
					requiredNumberOfDigestAttribute = requiredNumberOfDigestAttribute + 2;
				}else{
					requiredNumberOfDigestAttribute++;
				}
				Counter++;
				break;
			case RadiusAttributeConstants.DIGEST_REALM:
				digestAttributes.put(RadiusAttributeConstants.DIGEST_REALM,radiusAttribute.getStringValue());
				Counter ++;
				break;
			case RadiusAttributeConstants.DIGEST_RESPONSE:
				digestAttributes.put(RadiusAttributeConstants.DIGEST_RESPONSE,radiusAttribute.getStringValue());
				Counter ++;
				break;
			case RadiusAttributeConstants.DIGEST_RESPONSE_AUTH:
				digestAttributes.put(RadiusAttributeConstants.DIGEST_RESPONSE_AUTH,radiusAttribute.getStringValue());
				break;
			case RadiusAttributeConstants.DIGEST_STALE:
				digestAttributes.put(RadiusAttributeConstants.DIGEST_STALE,radiusAttribute.getStringValue());
				break;
			case RadiusAttributeConstants.DIGEST_URI:
				digestAttributes.put(RadiusAttributeConstants.DIGEST_URI,radiusAttribute.getStringValue());			
				Counter ++;
				break;
			case RadiusAttributeConstants.DIGEST_USERNAME:
				digestAttributes.put(RadiusAttributeConstants.DIGEST_USERNAME,radiusAttribute.getStringValue());			
				break;			
			case RadiusAttributeConstants.USER_NAME:
				digestAttributes.put(RadiusAttributeConstants.USER_NAME,radiusAttribute.getStringValue());
				break;
			case RadiusAttributeConstants.DRAFT_DIGEST_ATTRIBUTES:
				if(digestConfig.getIsDraftAAASIPEnable()){
					if(!draftBasedAuthentication)
						draftBasedAuthentication=true;
					byte[] digestAttributeBytes = radiusAttribute.getValueBytes();
					int byteRead =0;
					while(byteRead < digestAttributeBytes.length){
						byte[] tempBytesArray = new byte[digestAttributeBytes.length - byteRead];
						System.arraycopy(digestAttributeBytes,byteRead,tempBytesArray,0,tempBytesArray.length);
						StringAttribute stringAttribute = new StringAttribute();
						stringAttribute.setBytes(tempBytesArray);
						switch(stringAttribute.getType()){
						case DigestConstants.DIGEST_DRAFT_REALM       :
							digestAttributes.put(RadiusAttributeConstants.DIGEST_REALM,stringAttribute.getStringValue());
							Counter ++;
							break;
						case DigestConstants.DIGEST_DRAFT_NONCE       :
							digestAttributes.put(RadiusAttributeConstants.DIGEST_NONCE,stringAttribute.getStringValue());				
							Counter ++;
							break;
						case DigestConstants.DIGEST_DRAFT_METHOD      :
							digestAttributes.put(RadiusAttributeConstants.DIGEST_METHOD,stringAttribute.getStringValue());
							Counter++;
							break;
						case DigestConstants.DIGEST_DRAFT_URI         :
							digestAttributes.put(RadiusAttributeConstants.DIGEST_URI,stringAttribute.getStringValue());			
							Counter ++;
							break;
						case DigestConstants.DIGEST_DRAFT_QOP         :
							digestAttributes.put(RadiusAttributeConstants.DIGEST_QOP,stringAttribute.getStringValue());
							if(stringAttribute.getStringValue().equals("auth-int")){
								requiredNumberOfDigestAttribute = requiredNumberOfDigestAttribute + 2;
							}else{
								requiredNumberOfDigestAttribute++;
							}
							Counter++;
							break;
						case DigestConstants.DIGEST_DRAFT_ALGORITHM   :
							digestAttributes.put(RadiusAttributeConstants.DIGEST_ALGORITHM,stringAttribute.getStringValue());
							break;
						case DigestConstants.DIGEST_DRAFT_BODY_DIGEST :
							digestAttributes.put(RadiusAttributeConstants.DIGEST_ENTITY_BODY_HASH,stringAttribute.getStringValue());
							Counter++;
							break;
						case DigestConstants.DIGEST_DRAFT_CNONCE      :							
							digestAttributes.put(RadiusAttributeConstants.DIGEST_CNONCE,stringAttribute.getStringValue());							
							Counter++;
							break;
						case DigestConstants.DIGEST_DRAFT_NONCE_COUNT :
							digestAttributes.put(RadiusAttributeConstants.DIGEST_NONCE_COUNT,stringAttribute.getStringValue());
							Counter++;
							break;
						case DigestConstants.DIGEST_DRAFT_USER_NAME   :
							digestAttributes.put(RadiusAttributeConstants.DIGEST_USERNAME,stringAttribute.getStringValue());
							break;
						default:
						}						
						byteRead += stringAttribute.getLength();
					}
				}else{
					if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
						LogManager.getLogger().warn(MODULE,"Draft-aaa-sip-digest authentication is disabled. Request dropped.");
					digestSession.setRadiusPacketType(-1);
					return null;
				}
				break;
			case RadiusAttributeConstants.DRAFT_DIGEST_RESPONSE:
				if(digestConfig.getIsDraftAAASIPEnable()){
					digestAttributes.put(RadiusAttributeConstants.DIGEST_RESPONSE,radiusAttribute.getStringValue());
					Counter ++;
				}
				break;
			case RadiusAttributeConstants.SIP_AOR:
				digestAttributes.put(RadiusAttributeConstants.DIGEST_URI,radiusAttribute.getStringValue());
				Counter ++;
				break;
			default:
			}
		}		
		digestSession.setParameter(DigestConstants.DRAFT_BASED_DIGEST_REQUEST,draftBasedAuthentication);
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(MODULE,"Digest request attribute map: " + digestAttributes);
		if(Counter >= requiredNumberOfDigestAttribute)
			return(digestAttributes);
		else
			digestSession.setRadiusPacketType(RadiusConstants.ACCESS_CHALLENGE_MESSAGE);
		return(null);
	}	

	@Override
	public boolean isEligible(RadAuthRequest radiusRequest) {
		boolean isEligible = false;
		if(radiusRequest.getRadiusAttributes(RadiusAttributeConstants.DIGEST_REALM) != null
				|| radiusRequest.getRadiusAttributes(RadiusAttributeConstants.DIGEST_NONCE) != null
				|| radiusRequest.getRadiusAttributes(RadiusAttributeConstants.DRAFT_DIGEST_ATTRIBUTES) != null
				|| radiusRequest.getRadiusAttributes(RadiusAttributeConstants.DRAFT_DIGEST_RESPONSE) != null
				|| radiusRequest.getRadiusAttributes(RadiusAttributeConstants.DIGEST_RESPONSE) != null
				|| radiusRequest.getRadiusAttributes(RadiusAttributeConstants.DIGEST_METHOD) != null
				|| radiusRequest.getRadiusAttributes(RadiusAttributeConstants.DIGEST_URI) != null
				|| radiusRequest.getRadiusAttributes(RadiusAttributeConstants.DIGEST_QOP) != null
				|| radiusRequest.getRadiusAttributes(RadiusAttributeConstants.DIGEST_ALGORITHM) != null
				|| radiusRequest.getRadiusAttributes(RadiusAttributeConstants.DIGEST_ENTITY_BODY_HASH) != null
				|| radiusRequest.getRadiusAttributes(RadiusAttributeConstants.DIGEST_CNONCE) != null
				|| radiusRequest.getRadiusAttributes(RadiusAttributeConstants.DIGEST_NONCE_COUNT) != null
				|| radiusRequest.getRadiusAttributes(RadiusAttributeConstants.DIGEST_USERNAME) != null
				|| radiusRequest.getRadiusAttributes(RadiusAttributeConstants.DIGEST_OPAQUE) != null
				|| radiusRequest.getRadiusAttributes(RadiusAttributeConstants.DIGEST_AUTH_PARAM) != null
				|| radiusRequest.getRadiusAttributes(RadiusAttributeConstants.DIGEST_AKA_AUTS) != null
				|| radiusRequest.getRadiusAttributes(RadiusAttributeConstants.SIP_AOR) != null){

			isEligible=true;
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Request eligible for Digest authentication.");
		}
		return isEligible;
	}
	
	@Override
	public String toString() {
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);
		out.println("    		Digest Authentication");
		out.println(String.valueOf(digestConfig));
		out.close();
		return stringBuffer.toString();
	}

	@Override
	public void reInit() throws InitializationFailedException {
//		RadAuthServiceContext radAuthServiceContext = (RadAuthServiceContext)getServiceContext();
//		int digestConfigId = radAuthServiceContext.getAuthConfiguration().getAuthServicePolicyConfiguraion(authPolicyId).getDigestConfId();
//		if(radAuthServiceContext.getAuthConfiguration().getDigestConfiguration(digestConfigId) !=null)
//			this.digestConfig = radAuthServiceContext.getAuthConfiguration().getDigestConfiguration(digestConfigId) ;
	}
	@Override
	public int getMethodType() {
		return AuthMethods.DIGEST;
	}
}
