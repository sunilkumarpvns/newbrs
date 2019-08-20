package com.elitecore.aaa.radius.service.auth.handlers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.elitecore.aaa.core.authprotocol.BaseAuthMethodHandler;
import com.elitecore.aaa.core.authprotocol.EAPHandler;
import com.elitecore.aaa.core.authprotocol.LDAP;
import com.elitecore.aaa.core.authprotocol.exception.AuthenticationFailedException;
import com.elitecore.aaa.core.authprotocol.exception.UserNotFoundException;
import com.elitecore.aaa.core.conf.EAPConfigurationData;
import com.elitecore.aaa.core.conf.impl.EAPConfigurationDataImpl;
import com.elitecore.aaa.core.constant.AuthMethods;
import com.elitecore.aaa.core.constant.DriverTypes;
import com.elitecore.aaa.core.data.AccountData;
import com.elitecore.aaa.core.drivers.DriverConfiguration;
import com.elitecore.aaa.core.eap.session.EAPSessionId;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.core.subscriber.IAccountInfoProvider;
import com.elitecore.aaa.radius.service.RadServiceResponse;
import com.elitecore.aaa.radius.service.auth.RadAuthRequest;
import com.elitecore.aaa.radius.service.auth.RadAuthResponse;
import com.elitecore.aaa.radius.service.auth.RadAuthServiceContext;
import com.elitecore.aaa.radius.service.auth.constant.AuthReplyMessageConstant;
import com.elitecore.aaa.radius.service.auth.policy.handlers.conf.AuthenticationHandlerData;
import com.elitecore.aaa.util.EliteUtility;
import com.elitecore.aaa.util.constants.AAAServerConstants;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.coreeap.data.AAAEapRespData;
import com.elitecore.coreeap.data.AccountInfoProviderException;
import com.elitecore.coreeap.data.ICustomerAccountInfo;
import com.elitecore.coreeap.data.ICustomerAccountInfoProvider;
import com.elitecore.coreeap.fsm.eap.IEapStateMachine;
import com.elitecore.coreeap.packet.EAPException;
import com.elitecore.coreeap.packet.EAPPacket;
import com.elitecore.coreeap.packet.EAPPacketFactory;
import com.elitecore.coreeap.packet.InvalidEAPPacketException;
import com.elitecore.coreeap.packet.types.EAPType;
import com.elitecore.coreeap.util.constants.AttributeConstants;
import com.elitecore.coreeap.util.constants.EapPacketConstants;
import com.elitecore.coreeap.util.constants.EapTypeConstants;
import com.elitecore.coreeap.util.constants.fsm.events.EapEvents;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.RadiusUtility;
import com.elitecore.coreradius.commons.util.constants.DynAuthErrorCode;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;
import com.elitecore.passwordutil.DecryptionFailedException;
import com.elitecore.passwordutil.DecryptionNotSupportedException;
import com.elitecore.passwordutil.NoSuchEncryptionException;
import com.elitecore.passwordutil.PasswordEncryption;


public class RadEAPHandler extends BaseAuthMethodHandler implements IRadAuthMethodHandler{

	public static final String MODULE = "RAD_EAP_HANDLER";
	public static final int MAX_RADIUS_ATTRIBUTE_VALUE_LENGTH = 253;
	
	private EAPHandler eapHandler;
	private RadAuthServiceContext serviceContext;
	private List<String> tempIdPrefixes = new ArrayList<String>();
	private boolean isTestMode = false;
	private final AuthenticationHandlerData data;
	private EAPConfigurationData eapConfig;
	
	public RadEAPHandler(RadAuthServiceContext serviceContext, AuthenticationHandlerData data){
		super(serviceContext);
		this.serviceContext = serviceContext;
		this.data = data;
	}
	
	public RadEAPHandler(RadAuthServiceContext serviceContext, AuthenticationHandlerData data, boolean testMode){
		this(serviceContext, data);
		this.isTestMode = testMode;
	}

	@Override
	public void init() throws InitializationFailedException  {
		String eapId = data.getEapConfigId();
		this.eapConfig = ((AAAServerContext)serviceContext.getServerContext()).getServerConfiguration().getEAPConfigurations().getEAPConfigurationDataForID(eapId);
		if(eapConfig == null)
			eapConfig = new EAPConfigurationDataImpl(serviceContext.getServerContext(),eapId);
		eapHandler = new EAPHandler((RadAuthServiceContext)getServiceContext(),eapConfig,RadiusAttributeConstants.EAP_MESSAGE,isTestMode);
		eapHandler.init();
		tempIdPrefixes.add(eapConfig.getAKAConfiguration().getPseudonymPrefix());
		tempIdPrefixes.add(eapConfig.getSIMConfiguration().getPseudoPrefix());
		tempIdPrefixes.add(eapConfig.getSIMConfiguration().getFastReauthPrefix());
	}
	
	public void handleRequest(final RadAuthRequest radiusServiceRequest,final RadAuthResponse radiusServiceResponse, final IAccountInfoProvider accountProvider) throws AuthenticationFailedException{
		
		ArrayList<IRadiusAttribute> eapMessageList = (ArrayList<IRadiusAttribute>)radiusServiceRequest.getRadiusAttributes(RadiusAttributeConstants.EAP_MESSAGE);

		if(eapMessageList != null && !eapMessageList.isEmpty()){
			
			String strEapSessionId = EAPSessionId.createEapSessionId(radiusServiceRequest, tempIdPrefixes);
			radiusServiceResponse.setParameter("EAP_SESSION_ID", strEapSessionId);
			
			ICustomerAccountInfoProvider customerAccountInfoProvider = new ICustomerAccountInfoProvider(){
				private static final String MODULE = "IAccountInfoProvider";
				private DriverConfiguration driverConfiguration = null;
				
				public ICustomerAccountInfo getCustomerAccountInfo() throws AccountInfoProviderException{
					AccountData accountData =accountProvider.getAccountData(radiusServiceRequest,radiusServiceResponse);
					return getCustomerProfile(accountData);
				}
				
				public ICustomerAccountInfo getCustomerAccountInfo(String strUserIdentity) throws AccountInfoProviderException{
					AccountData accountData =accountProvider.getAccountData(radiusServiceRequest,radiusServiceResponse,strUserIdentity);
					return (ICustomerAccountInfo)getCustomerProfile(accountData);
				}
				
				@Override
				public ICustomerAccountInfo getCustomerAccountInfo(String strUserIdentity, EapTypeConstants constant)
						throws AccountInfoProviderException {
					IRadiusAttribute infoAttribute = radiusServiceRequest.getInfoAttribute(RadiusConstants.ELITECORE_VENDOR_ID, RadiusAttributeConstants.ELITE_EAP_METHOD);
					if (infoAttribute == null) {
						infoAttribute = Dictionary.getInstance().getAttribute(RadiusConstants.ELITECORE_VENDOR_ID, RadiusAttributeConstants.ELITE_EAP_METHOD);
						radiusServiceRequest.addInfoAttribute(infoAttribute);
					}
					infoAttribute.setIntValue(constant.typeId);
					return getCustomerAccountInfo(strUserIdentity);
				}
				
				private AccountData getCustomerProfile(AccountData accountData){
					
					if(accountData == null)
						return null;
					String decodedPassword = null;
					int encryptionType = 0;
					try {
						try{
							if(accountData.getEncryptionType() != null)
								encryptionType = Integer.parseInt(accountData.getEncryptionType());
						}catch (NumberFormatException e) {
							if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
								LogManager.getLogger().debug(MODULE, "Invalid Password Encryption Type Format for user : "+accountData.getUserName());
						}
						decodedPassword = PasswordEncryption.getInstance().decrypt(accountData.getPassword(), encryptionType);
					} catch (NoSuchEncryptionException e) {
						throw new AccountInfoProviderException("Authentication failed due to Unsupported Password Encryption Type.",e,AccountInfoProviderException.PROFILE_OPERATION_EXCEPTION);
					} catch (DecryptionNotSupportedException e) {
						throw new AccountInfoProviderException("Authentication failed due to improper password encryption format.",e,AccountInfoProviderException.PROFILE_OPERATION_EXCEPTION);
					} catch (DecryptionFailedException e) {
						throw new AccountInfoProviderException("Authentication failed due to exception in decryption.",e,AccountInfoProviderException.PROFILE_OPERATION_EXCEPTION);
					} 
					if(decodedPassword == null){
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "Exception got during getting decoded password for user : "+accountData.getUserName()+", null account data returned");
						return null;
					}
					accountData.setPassword(decodedPassword);
					return accountData;
				}

				@Override
				public List<String> getClientUserIdentityAttributeList() {
					// TODO Auto-generated method stub
					return null;
				}
				
				@Override
				public boolean doLdapBindAuthentication(String decodedPassword,ICustomerAccountInfo accountData){
					//this check has been added in case someone calls bind authentication method without checking for the validity.
					//So in that case the configuration will be null and will have to be fetched
					if(driverConfiguration == null){
						driverConfiguration = ((AAAServerContext)getServiceContext().getServerContext()).getServerConfiguration().getDriverConfigurationProvider().getDriverConfiguration((String)radiusServiceRequest.getParameter(AAAServerConstants.DRIVER_INSTANCE_ID));
					}
					
					try{
						LDAP.verifyPassword(driverConfiguration, (String)radiusServiceRequest.getParameter(AAAServerConstants.DN), decodedPassword);
						return true;
					}catch (Exception e) {
						LogManager.getLogger().trace(MODULE, e);
						return false;
					}
				}
				
				@Override
				public boolean checkLDAPBindAuthenticationRequired(ICustomerAccountInfo accountData) {
					driverConfiguration = ((AAAServerContext)getServiceContext().getServerContext()).getServerConfiguration().getDriverConfigurationProvider().getDriverConfiguration((String)radiusServiceRequest.getParameter(AAAServerConstants.DRIVER_INSTANCE_ID));
					
					return accountData.getPasswordCheck().equalsIgnoreCase("bind") 
							&& driverConfiguration.getDriverType() == DriverTypes.RAD_LDAP_AUTH_DRIVER;
				}
			};

			byte[] eapPacketBytes = getBytesFromRadiusAtrCollection(eapMessageList);								

			IRadiusAttribute framedMTU = radiusServiceRequest.getRadiusAttribute(RadiusAttributeConstants.FRAMED_MTU);
			int configValue = eapHandler.getEapContext().getFragmentSize();

			if(framedMTU != null){
				int framedMTUValue = framedMTU.getIntValue();
				if(configValue > 0){
					if((framedMTUValue-4) < configValue){							
						configValue = framedMTUValue - 4;
					}
				}else if(configValue == 0){
					configValue = framedMTUValue - 4;
				}
			}
			
			EapEvents event = null;		
			byte[] eapReqPacketBytes = null;
			AAAEapRespData eapRespData = null;
			try{
				eapRespData = new AAAEapRespData(eapPacketBytes);					
				eapRespData.setFragmentedSize(configValue);		
				IRadiusAttribute callingStationId = radiusServiceRequest.getRadiusAttribute(RadiusAttributeConstants.CALLING_STATION_ID);
				if(callingStationId != null){
					String strCallingStationId = callingStationId.getStringValue();
					eapRespData.setOUI(strCallingStationId);
				}
				
				event =  (EapEvents) eapHandler.handleRequest(eapRespData, strEapSessionId,customerAccountInfoProvider);
				
				eapReqPacketBytes = eapRespData.getEapReqPacketBytes();
				
			}catch(EAPException e){
				//getLogger().trace(MODULE, e);
				if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
					LogManager.getLogger().warn(MODULE, "Invalid Radius Response Packet Type for EAP Response. Sending EAP-Failure");
				try {
					if (eapPacketBytes.length > 1){
						int identifier = eapPacketBytes[1] & 0xFF;
						EAPPacket eapFailurePacket = buildFailure(identifier);
						IRadiusAttribute respEapMessageAttr = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.EAP_MESSAGE);				
						respEapMessageAttr.setValueBytes(eapFailurePacket.getBytes());
						radiusServiceResponse.addAttribute(respEapMessageAttr);
					} else {
						if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
							LogManager.getLogger().warn(MODULE, "Malformed EAP-Packet received. Reason: " + e.getMessage() + "Dropping request.");
						radiusServiceResponse.markForDropRequest();
						
					}
				} catch (EAPException e1) {
					if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
						LogManager.getLogger().warn(MODULE, "Malformed EAP-Packet received. Reason: " + e.getMessage() + "Dropping request.");
					radiusServiceResponse.markForDropRequest();
					
				}
				radiusServiceResponse.setFurtherProcessingRequired(false);
				return;
				
			}catch(AccountInfoProviderException e){
				if(e.getCode() == AccountInfoProviderException.USER_NOT_FOUND){
					throw new UserNotFoundException();
				}else{
					throw new AuthenticationFailedException("EAP Failure",e); 
				}
			}

			if(event == EapEvents.EapSuccessEvent){
				ICustomerAccountInfo accountInfo= null; 
				
				try{
					accountInfo= eapHandler.getCustomerAccountInfo(strEapSessionId);
				}catch(AccountInfoProviderException e){
					if(e.getCode() == AccountInfoProviderException.USER_NOT_FOUND){
						throw new UserNotFoundException();
					}else{
						throw new AuthenticationFailedException("EAP Failure",e); 
					}
				}
				if(accountInfo==null)
					throw new UserNotFoundException();
				radiusServiceRequest.setParameter("AUTH_ACCOUNT_DATA_KEY", accountInfo);
				radiusServiceRequest.setParameter("ACCOUNT_DATA", accountInfo);
				radiusServiceRequest.setParameter("EAP_SESSION_ID", strEapSessionId);
				
				radiusServiceRequest.setAccountData((AccountData)accountInfo);
				radiusServiceRequest.setParameter("EAP_IDENTITY", eapHandler.getIdentity(strEapSessionId));
				radiusServiceRequest.setParameter(AAAServerConstants.MSK_REVALIDATION_TIME, eapConfig.getMskRevalidationTime());
				
				if(accountInfo.isMacValidation()){
					doMACValidation(radiusServiceRequest, strEapSessionId);
				}
				
				radiusServiceResponse.setPacketType(RadiusConstants.ACCESS_ACCEPT_MESSAGE);
				radiusServiceResponse.setResponseMessage(AuthReplyMessageConstant.AUTHENTICATION_SUCCESS);
				
				if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
					LogManager.getLogger().info(MODULE, "Radius Packet type for Session-Id  "+strEapSessionId+" : ACCESS_ACCEPT");
				
				byte[] eapKeyData = null;
				if(eapRespData.getAAAEapKeyData() != null){
					eapKeyData = eapRespData.getAAAEapKeyData();
					radiusServiceRequest.setParameter("EAP_KEY_DATA", eapKeyData);
				}else{
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
						LogManager.getLogger().debug(MODULE, "AAA EAP key data  for Session-Id : "+strEapSessionId+" is null");
				}
				

				//Add USERNAME, FRAMED-MTU and STATE as reply items if required.

				// username
				IEapStateMachine sessionState = ((AAAServerContext)getServiceContext().getServerContext()).getEapSessionManager().getEAPStateMachine(strEapSessionId);
				if (sessionState != null ){
					String userIdentityValue = sessionState.getUserIdentity();
					if(userIdentityValue != null)
						radiusServiceRequest.setParameter(AAAServerConstants.CUI_KEY,userIdentityValue);
				}
				
				EliteUtility.addSubscriberProfileRadiusVSA(radiusServiceRequest, (AccountData) accountInfo);

				// Service Type
				IRadiusAttribute serviceType = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.SERVICE_TYPE);
				if(radiusServiceRequest.getRadiusAttribute(RadiusAttributeConstants.SERVICE_TYPE) != null)
					serviceType.setIntValue(radiusServiceRequest.getRadiusAttribute(RadiusAttributeConstants.SERVICE_TYPE).getIntValue());
				else
					serviceType.setIntValue(2);
				
				radiusServiceResponse.addAttribute(serviceType);

				// State
				IRadiusAttribute radStatAtt = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.STATE);
				radStatAtt.setStringValue(strEapSessionId);
				radiusServiceResponse.addAttribute(radStatAtt);
				
				
				if(radiusServiceResponse.getClientData().isSupportedVendorId(RadiusConstants.MICROSOFT_VENDOR_ID) && eapKeyData != null){	
					// Add keys in MicroSoft attributes
					byte[] receiveKey = new byte[32];
					byte[] sendKey = new byte[32];
					System.arraycopy(eapKeyData, 0, receiveKey, 0, receiveKey.length);
					System.arraycopy(eapKeyData, receiveKey.length, sendKey, 0, sendKey.length);

					IRadiusAttribute radiusAttribute1 = (IRadiusAttribute)Dictionary.getInstance().getAttribute(RadiusConstants.MICROSOFT_VENDOR_ID,17);
					radiusAttribute1.setValue(receiveKey, 65535, radiusServiceResponse.getClientData().getSharedSecret(radiusServiceRequest.getPacketType()), radiusServiceRequest.getAuthenticator());
					radiusServiceResponse.addAttribute(radiusAttribute1);							

					radiusAttribute1 = (IRadiusAttribute)Dictionary.getInstance().getAttribute(RadiusConstants.MICROSOFT_VENDOR_ID,16);
					radiusAttribute1.setValue(sendKey, 65534, radiusServiceResponse.getClientData().getSharedSecret(radiusServiceRequest.getPacketType()), radiusServiceRequest.getAuthenticator());
					radiusServiceResponse.addAttribute(radiusAttribute1);							
					
				}
				if (!radiusServiceResponse.isMarkedForDropRequest()){
					getRadiusResponsePacketWithEAPAttributes(radiusServiceResponse,eapReqPacketBytes);
				}
				
			}else if(event == EapEvents.EapFailureEvent){
			
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "Radius Packet type for Session-Id "+strEapSessionId+" : ACCESS_REJECT");
				
				String failureReason = null;
				String userIdentityValue = null;
				IEapStateMachine sessionState = ((AAAServerContext)getServiceContext().getServerContext()).getEapSessionManager().getEAPStateMachine(strEapSessionId);
				if (sessionState != null ){
					userIdentityValue = sessionState.getUserIdentity();
					if(userIdentityValue != null)
						radiusServiceRequest.setParameter(AAAServerConstants.CUI_KEY,userIdentityValue);
				}
	
				failureReason = eapHandler.getFailureReason(strEapSessionId);
				if(failureReason == null || failureReason.length() <= 0){
					failureReason = AuthReplyMessageConstant.EAP_FAILURE;
				}
				
				throw new AuthenticationFailedException(failureReason);																																								
			}else {
				
				radiusServiceResponse.setFurtherProcessingRequired(false);
					
				radiusServiceResponse.setPacketType(RadiusConstants.ACCESS_CHALLENGE_MESSAGE);
				if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
					LogManager.getLogger().info(MODULE, "Radius Packet type for Session-Id  "+strEapSessionId+" : ACCESS_CHALLENGE");
					
	
				IRadiusAttribute sessionTimeout = (IRadiusAttribute)Dictionary.getInstance().getAttribute(RadiusAttributeConstants.SESSION_TIMEOUT);
				sessionTimeout.setIntValue(3000);
				radiusServiceResponse.addAttribute(sessionTimeout);
	
				IRadiusAttribute radStatAtt = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.STATE);
				radStatAtt.setStringValue(strEapSessionId);
				radiusServiceResponse.addAttribute(radStatAtt);
	
				IRadiusAttribute eventTimestamp = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.EVENT_TIMESTAMP);
				eventTimestamp.setLongValue(System.currentTimeMillis()/1000);
				radiusServiceResponse.addAttribute(eventTimestamp);
					
				/**
				 * RFC-3579 - 2.2 Invalid Packets
				 * */
				if(event == EapEvents.EapPacketIgnored ){
					
					if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
						LogManager.getLogger().warn(MODULE, "Packet Ignored. Reason: " + this.eapHandler.getIgnoreReason(strEapSessionId));
										
					if(!this.eapHandler.isTreatInvalidPacketAsFatal()){
						
						eapReqPacketBytes = this.eapHandler.getLastEAPRequestBytes(strEapSessionId);
							
						IRadiusAttribute errorCauseAttr = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.ERROR_CAUSE);
						errorCauseAttr.setIntValue(DynAuthErrorCode.InvalidEAPPacket.value);
						radiusServiceResponse.addAttribute(errorCauseAttr);
					}	
						
				}
				if (!radiusServiceResponse.isMarkedForDropRequest()){
					getRadiusResponsePacketWithEAPAttributes(radiusServiceResponse,eapReqPacketBytes);
				}
					
			}
			
			//Elite-1856 adding the Elite EAP VSA in response at INFO level
			addEAPAttributeAsInfoFrom(radiusServiceResponse);
		}		
	}
	
	private void addEAPAttributeAsInfoFrom(RadServiceResponse response) {
		Collection<IRadiusAttribute> eapMessageAttributes = response.getRadiusAttributes(RadiusAttributeConstants.EAP_MESSAGE);
		if(eapMessageAttributes != null && eapMessageAttributes.size() > 0){
			byte[] eapMessageBytes = getBytesFromRadiusAtrCollection(eapMessageAttributes);
			try {
				EAPPacket eapPacket = new EAPPacket(eapMessageBytes);
				IRadiusAttribute eapCodeAttr = Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID,RadiusAttributeConstants.ELITE_EAP_CODE);
				if(eapCodeAttr != null){
					eapCodeAttr.setIntValue(eapPacket.getCode());
					response.addInfoAttribute(eapCodeAttr);
				}
				List<EAPType> eapTypes = eapPacket.getEAPTypes();
				if(eapTypes != null && eapTypes.size() > 0){
					IRadiusAttribute eapMethodAttribute = Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID,RadiusAttributeConstants.ELITE_EAP_METHOD);
					if(eapMethodAttribute != null ){
						eapMethodAttribute.setIntValue(eapTypes.get(0).getType());
						response.addInfoAttribute(eapMethodAttribute);
					}
				}

			} catch (InvalidEAPPacketException e) {
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
					LogManager.getLogger().debug(MODULE, "Invalid EAP Packet received, ELITE-EAP-CODE (21067:211) and ELITE-EAP-CODE (21067:212) will not be added.");
				}
			}
		}
	}

	private void doMACValidation(final RadAuthRequest radiusServiceRequest,String strEapSessionId) throws AuthenticationFailedException {
		boolean macValidationResult = false;
		String strCallingStationId;
		
		IRadiusAttribute callingStationId = radiusServiceRequest.getRadiusAttribute(RadiusAttributeConstants.CALLING_STATION_ID, true);
		
		if(callingStationId == null) {
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
				LogManager.getLogger().info(MODULE, "Calling station ID not found for MAC validation for eap session id: " + strEapSessionId);
			}
			macValidationResult = false;
		}else{
			strCallingStationId = callingStationId.getStringValue();
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
				LogManager.getLogger().info(MODULE, "Performing MAC validation for eap session id: " + strEapSessionId + " with calling station id: " +  strCallingStationId);
			}
			macValidationResult = eapHandler.validateMAC(strEapSessionId, strCallingStationId);
		}
		
		if(!macValidationResult){
			if(eapHandler.getEapContext().isValidateMac()){
				throw new AuthenticationFailedException(AuthReplyMessageConstant.MAC_VALIDATION_FAILED_FOR_SESSION_ID + strEapSessionId);
			}
			
			//exception occurred but we need to allow such clients
			if(LogManager.getLogger().isLogLevel(LogLevel.TRACE)) {
				LogManager.getLogger().trace(MODULE, "MAC validation for eap is disabled, skipping validation process for eap session id: " + strEapSessionId);
			} 
		}
	}	

	private byte[] getBytesFromRadiusAtrCollection(Collection<IRadiusAttribute> radiusAttributeCollection) {
		
		int length = 0;
		Collection<byte[]> byteArrayCollection = new ArrayList<byte[]>();
		Iterator<IRadiusAttribute> attrItr = radiusAttributeCollection.iterator();
		while(attrItr.hasNext()){
			IRadiusAttribute temp = attrItr.next();
			byteArrayCollection.add(temp.getValueBytes());
			length+=temp.getValueBytes().length;
		}
		
		byte[] resultBytes = new byte[length];
		Iterator<byte[]> byteItr = byteArrayCollection.iterator();
		
		int count = 0;
		while(byteItr.hasNext()){
			byte[] tempBytes = byteItr.next();
			System.arraycopy(tempBytes, 0, resultBytes, count, tempBytes.length);
			count+=tempBytes.length;
		}
		return resultBytes;
	}
	
	private void getRadiusResponsePacketWithEAPAttributes( RadAuthResponse radiusServiceResponse, byte[] eapPacketBytes){
		
		Collection<byte[]> eapMessagesInResponse = RadiusUtility.getByteChunks(eapPacketBytes,MAX_RADIUS_ATTRIBUTE_VALUE_LENGTH); 
		Iterator<byte[]> eapMsgAttribItr = eapMessagesInResponse.iterator();
		while(eapMsgAttribItr.hasNext()){
			IRadiusAttribute eapMessageAttribute = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.EAP_MESSAGE);
			eapMessageAttribute.setValueBytes(eapMsgAttribItr.next());
			radiusServiceResponse.addAttribute(eapMessageAttribute);
		}
		
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(MODULE, "Number of EAP-Message attribute(s) in response: " + eapMessagesInResponse.size());
		
	}
	
	public boolean isEligible (RadAuthRequest radiusRequest){
		
		boolean isEligible= (radiusRequest.getRadiusAttribute(RadiusAttributeConstants.MESSAGE_AUTHENTICATOR) != null
				&& radiusRequest.getRadiusAttributes(RadiusAttributeConstants.EAP_MESSAGE) != null);
			
		if(isEligible){
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Request eligible for EAP authentication.");
		
		}
		else{
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Request doesn't contain required for EAP handling so, request is not eligible for EAP Authentication.");
		}
			
		return isEligible;
	}
	
	private EAPPacket buildFailure(int currentId) throws EAPException{
		EAPPacket failurePacket = EAPPacketFactory.getInstance().createEAPPacket(EapPacketConstants.FAILURE.packetId);
		try {
			failurePacket.setIdentifier(currentId);
		} catch (InvalidEAPPacketException e) {
			throw  new EAPException("Error during Building Failure. Reason: " + e.getMessage(), e);
		}
		failurePacket.resetLength();
		return failurePacket;
	}
	
	@Override
	public void reInit() throws InitializationFailedException {
		/*int eapId = serviceContext.getAuthConfiguration().getAuthServicePolicyConfiguraion(authPolicyId).getEapId();
		EAPConfigurationData eapConfig = ((AAAServerContext)serviceContext.getServerContext()).getServerConfiguration().getEAPConfigurations().getEAPConfigurationDataForID(eapId);
		if(eapConfig != null){
			eapHandler.reInit(eapConfig);
		}*/
	}
	@Override
	public int getMethodType() {
		return AuthMethods.EAP;
	}
}
