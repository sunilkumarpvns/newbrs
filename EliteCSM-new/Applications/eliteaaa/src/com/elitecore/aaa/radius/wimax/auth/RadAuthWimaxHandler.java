package com.elitecore.aaa.radius.wimax.auth;

import java.util.List;

import com.elitecore.aaa.core.authprotocol.exception.AuthorizationFailedException;
import com.elitecore.aaa.core.conf.SPIKeyConfiguration;
import com.elitecore.aaa.core.conf.WimaxConfiguration;
import com.elitecore.aaa.core.eap.session.EAPSessionManager;
import com.elitecore.aaa.core.wimax.BaseWimaxHandler;
import com.elitecore.aaa.core.wimax.WimaxRequest;
import com.elitecore.aaa.core.wimax.WimaxResponse;
import com.elitecore.aaa.core.wimax.WimaxSessionManager;
import com.elitecore.aaa.core.wimax.keys.KeyManager;
import com.elitecore.aaa.radius.service.auth.RadAuthRequest;
import com.elitecore.aaa.radius.service.auth.RadAuthResponse;
import com.elitecore.aaa.radius.service.auth.RadAuthServiceContext;
import com.elitecore.aaa.radius.service.auth.constant.AuthReplyMessageConstant;
import com.elitecore.aaa.radius.service.auth.handlers.RadAuthVendorSpecificHandler;
import com.elitecore.aaa.util.constants.AAAServerConstants;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.util.constants.CommonConstants;
import com.elitecore.coreradius.commons.attributes.BaseRadiusAttribute;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.attributes.wimaxattribute.WimaxGroupedAttribute;
import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;
import com.elitecore.coreradius.commons.util.constants.WimaxAttrConstants;
import com.elitecore.coreradius.commons.util.constants.WimaxAttrConstants.PPAQ;
import com.elitecore.coreradius.commons.util.constants.WimaxAttrConstants.ResultCode;
import com.elitecore.coreradius.commons.util.constants.WimaxAttrConstants.WimaxCapability;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;

public class RadAuthWimaxHandler extends BaseWimaxHandler<RadAuthRequest,RadAuthResponse> implements RadAuthVendorSpecificHandler{
	
	private static final String MODULE = "RAD_WIMAX_HANDLER";
	private static final String EAP_KEY_DATA = "EAP_KEY_DATA";
	private static final String EAP_IDENTITY = "EAP_IDENTITY";
	private static final String EAP_SESSION_ID = "EAP_SESSION_ID";
	
	public RadAuthWimaxHandler(RadAuthServiceContext serviceContext, WimaxConfiguration wimaxConfiguration, SPIKeyConfiguration spiKeyConfiguration, 
			WimaxSessionManager wimaxSessionManager, EAPSessionManager eapSessionManager, KeyManager keyManager){
		super(serviceContext, wimaxConfiguration, spiKeyConfiguration, wimaxSessionManager, eapSessionManager, keyManager);
	}

	public void handleRequest(RadAuthRequest request, RadAuthResponse response)  {
		
		if(response.getPacketType() == RadiusConstants.ACCESS_CHALLENGE_MESSAGE){
			LogManager.getLogger().trace(MODULE, "Response Packet is ACCESS_CHALLENGE, Skipping wimax handling");
			return;
		}
		
		if(LogManager.getLogger().isLogLevel(LogLevel.TRACE)){
			LogManager.getLogger().trace(MODULE, "Handling WiMAX Request");
		}
		
		//forming the Wimax Request and the Wimax Response
		WimaxRequest wimaxRequest = formWimaxRequest(request);
		WimaxResponse wimaxResponse = formWimaxResponse(request, response);
		
		//handling the wimax request 
		try {
			handleWimaxRequest(wimaxRequest, wimaxResponse);
		} catch (AuthorizationFailedException e) {
			wimaxResponse.setFurtherProcessingRequired(false);
			wimaxResponse.setResultCode(ResultCode.AUTH_REJECT);
			wimaxResponse.setResponseMessage(AuthReplyMessageConstant.WIMAX_PROCESSING_FAILED);
		}
		
		//call to the Create Service Response
		createServiceResponse(request,response,wimaxRequest, wimaxResponse);
		
		// TODO To indicate wimax request to RM
		IRadiusAttribute resourceAVPair  = Dictionary.getInstance().getAttribute(RadiusConstants.ELITECORE_VENDOR_ID, RadiusAttributeConstants.ELITE_RESOURCE_MANAGER_AVPAIR);
		resourceAVPair.setStringValue("Wimax=true");
		request.addInfoAttribute(resourceAVPair);		
	}
	
	//no change in this function as this is called before the handle request function
	@Override
	public boolean isEligible(RadAuthRequest request) {		 
		
		if(request.getRadiusAttribute(RadiusConstants.WIMAX_VENDOR_ID, WimaxAttrConstants.WIMAX_CAPABILITY.getIntValue()) !=null||
				request.getRadiusAttribute(RadiusConstants.WIMAX_VENDOR_ID, WimaxAttrConstants.PPAC.getIntValue()) !=null||
				request.getRadiusAttribute(RadiusConstants.WIMAX_VENDOR_ID, WimaxAttrConstants.PPAQ.getIntValue()) !=null||
				request.getRadiusAttribute(RadiusConstants.WIMAX_VENDOR_ID, WimaxAttrConstants.PTS.getIntValue()) !=null||
				request.getRadiusAttribute(RadiusConstants.WIMAX_VENDOR_ID, WimaxAttrConstants.DHCP_RK_KEY_ID.getIntValue()) !=null){
			if(LogManager.getLogger().isLogLevel(LogLevel.TRACE)) {
				LogManager.getLogger().trace(MODULE, "Wimax Prepaid attribute (either WIMAX-CAP PPAC PPAQ PTS DHCPRKID) present in request, request eligible for wimax handling");
			}
			return true;
		}
		
		if(LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
			LogManager.getLogger().trace(MODULE, "None of Wimax Prepaid attributes (WIMAX-CAP PPAC PPAQ PTS DHCPRKID) present in request, request not eligible for wimax handling");
		}
		
		return false;
	}

	@Override
	public void init() throws InitializationFailedException {				
	}

	@Override
	public WimaxRequest formWimaxRequest(RadAuthRequest serviceRequest) {
		final long lVendorId = RadiusConstants.WIMAX_VENDOR_ID;
		WimaxRequest wimaxRequest = new WimaxRequest();
		
		//setting the SourceInfo parameters
		if(serviceRequest.getClientIp() != null){
			wimaxRequest.getSourceInfo().setSourceIP(serviceRequest.getClientIp());
		}
		IRadiusAttribute attribute = serviceRequest.getRadiusAttribute(RadiusAttributeConstants.NAS_IP_ADDRESS);
		if (attribute != null) {
			wimaxRequest.getSourceInfo().setNAS_IP_Address(attribute.getStringValue());
		}
		
		attribute = serviceRequest.getRadiusAttribute(RadiusAttributeConstants.NAS_IDENTIFIER);
		if (attribute != null) {
			wimaxRequest.getSourceInfo().setNAS_Identifier(attribute.getStringValue());
		}
		
		attribute = serviceRequest.getRadiusAttribute(RadiusAttributeConstants.NAS_PORT);
		if (attribute != null) {
			wimaxRequest.getSourceInfo().setNAS_Port(attribute.getIntValue());
		}
		attribute = serviceRequest.getRadiusAttribute(RadiusAttributeConstants.NAS_PORT_TYPE);
		if (attribute != null) {
			wimaxRequest.getSourceInfo().setNAS_Port_Type(attribute.getIntValue());
		}
		attribute = serviceRequest.getRadiusAttribute(RadiusAttributeConstants.NAS_PORT_ID);
		if (attribute != null) {
			wimaxRequest.getSourceInfo().setNAS_Port_ID(attribute.getStringValue());
		}
		
		//setting the CUI from the service request
		if(serviceRequest.getParameter(AAAServerConstants.CUI_KEY) != null){
			wimaxRequest.setCUI((String)serviceRequest.getParameter(AAAServerConstants.CUI_KEY));
		}
		
		//setting the Username
		attribute = serviceRequest.getRadiusAttribute(RadiusAttributeConstants.USER_NAME);
		if (attribute != null) {
			wimaxRequest.setUserName(attribute.getStringValue());
		}
		
		//setting the calling station ID
		attribute = serviceRequest.getRadiusAttribute(RadiusAttributeConstants.CALLING_STATION_ID);
		if (attribute != null) {
			wimaxRequest.getSourceInfo().setCalling_Station_ID(attribute.getStringValue());
		}else if(serviceRequest.getAccountData() != null){
			wimaxRequest.getSourceInfo().setCalling_Station_ID(serviceRequest.getAccountData().getCallingStationId());
		}
		
		//setting the DHCP_RK_KEY_ID
		attribute = serviceRequest.getRadiusAttribute(lVendorId, WimaxAttrConstants.DHCP_RK_KEY_ID.getIntValue());
		if (attribute != null) {
			wimaxRequest.setDHCP_RK_KEY_ID(attribute.getIntValue());
		}
		
		//setting the HA parameters
		attribute = serviceRequest.getRadiusAttribute(lVendorId, WimaxAttrConstants.MN_HA_MIP4_SPI.getIntValue());
		if (attribute != null) {
			wimaxRequest.getHA().setMN_HA_MIP4_SPI(attribute.getIntValue());
		}
		attribute = serviceRequest.getRadiusAttribute(lVendorId, WimaxAttrConstants.HA_RK_SPI.getIntValue());
		if (attribute != null) {
			wimaxRequest.getHA().setHA_RK_SPI(attribute.getIntValue());
		}
		
		//setting the AAA session ID
		attribute = serviceRequest.getRadiusAttribute(lVendorId, WimaxAttrConstants.AAA_SESSION_ID.getIntValue());
		if (attribute !=  null) {
			wimaxRequest.setAAA_Session_ID(attribute.getStringValue());
		}
		
		//setting the Wimax Capabilities
		if (serviceRequest.getRadiusAttribute(lVendorId, WimaxAttrConstants.WIMAX_CAPABILITY.getIntValue()) != null) {
		
			if(serviceRequest.getRadiusAttribute(lVendorId, WimaxAttrConstants.WIMAX_CAPABILITY.getIntValue() , WimaxAttrConstants.WimaxCapability.WIMAX_RELEASE.getIntValue()) != null){
				wimaxRequest.getWimaxCapabilities().setWimaxRelease(serviceRequest.getRadiusAttribute(lVendorId, WimaxAttrConstants.WIMAX_CAPABILITY.getIntValue() , WimaxAttrConstants.WimaxCapability.WIMAX_RELEASE.getIntValue()).getIntValue());
			}
			if(serviceRequest.getRadiusAttribute(lVendorId, WimaxAttrConstants.WIMAX_CAPABILITY.getIntValue() , WimaxAttrConstants.WimaxCapability.ACCOUNTING_CAPABILITIES.getIntValue()) != null){
				wimaxRequest.getWimaxCapabilities().setHotliningCapabilities(serviceRequest.getRadiusAttribute(lVendorId, WimaxAttrConstants.WIMAX_CAPABILITY.getIntValue() , WimaxAttrConstants.WimaxCapability.ACCOUNTING_CAPABILITIES.getIntValue()).getIntValue());
			}
			if(serviceRequest.getRadiusAttribute(lVendorId, WimaxAttrConstants.WIMAX_CAPABILITY.getIntValue(), WimaxAttrConstants.WimaxCapability.HOTLINING_CAPABILITIES.getIntValue()) != null){
				wimaxRequest.getWimaxCapabilities().setHotliningCapabilities(serviceRequest.getRadiusAttribute(lVendorId, WimaxAttrConstants.WIMAX_CAPABILITY.getIntValue(), WimaxAttrConstants.WimaxCapability.HOTLINING_CAPABILITIES.getIntValue()).getIntValue());
			}
			if(serviceRequest.getRadiusAttribute(lVendorId, WimaxAttrConstants.WIMAX_CAPABILITY.getIntValue(), WimaxAttrConstants.WimaxCapability.IDLE_MODE_NOTIFICATION_CAPABILITIES.getIntValue()) != null){
				wimaxRequest.getWimaxCapabilities().setIdleModeNotificationCapabilities(serviceRequest.getRadiusAttribute(lVendorId, WimaxAttrConstants.WIMAX_CAPABILITY.getIntValue(), WimaxAttrConstants.WimaxCapability.IDLE_MODE_NOTIFICATION_CAPABILITIES.getIntValue()).getIntValue());
			}
		}
		
		
		//setting the service Type
		attribute = serviceRequest.getRadiusAttribute(RadiusAttributeConstants.SERVICE_TYPE);
		if (attribute != null) {
		wimaxRequest.setServiceType(attribute.getIntValue());
		}
		
		//setting the EAP parameters
		if(serviceRequest.getRadiusAttribute(RadiusAttributeConstants.EAP_MESSAGE) != null){
			wimaxRequest.getEAP().setEapRequest(true);
		}
		
		//setting the PPAQ parameters
		if(serviceRequest.getRadiusAttribute(lVendorId, WimaxAttrConstants.PPAQ.getIntValue()) != null){
			IRadiusAttribute ppaqSubAttr = serviceRequest.getRadiusAttribute(lVendorId, WimaxAttrConstants.PPAQ.getIntValue(), PPAQ.CHECK_BALANCE_RESULT.getIntValue());
			if(ppaqSubAttr != null){
				wimaxRequest.getPPAQ().setCheckBalanceResult(ppaqSubAttr.getIntValue());
			}
			ppaqSubAttr = serviceRequest.getRadiusAttribute(lVendorId, WimaxAttrConstants.PPAQ.getIntValue(), PPAQ.COST_INFORMATION_AVP.getIntValue());
			if(ppaqSubAttr != null){
				wimaxRequest.getPPAQ().setCostInformationAVP(ppaqSubAttr.getBytes());
			}
			ppaqSubAttr = serviceRequest.getRadiusAttribute(lVendorId, WimaxAttrConstants.PPAQ.getIntValue(), PPAQ.DURATION_QUOTA.getIntValue());
			if(ppaqSubAttr != null){
				wimaxRequest.getPPAQ().setDurationQuota(ppaqSubAttr.getIntValue());
			}
			ppaqSubAttr = serviceRequest.getRadiusAttribute(lVendorId, WimaxAttrConstants.PPAQ.getIntValue(), PPAQ.DURATION_THRESHOLD.getIntValue());
			if(ppaqSubAttr != null){
				wimaxRequest.getPPAQ().setDurationThreshold(ppaqSubAttr.getIntValue());
			}
			ppaqSubAttr = serviceRequest.getRadiusAttribute(lVendorId, WimaxAttrConstants.PPAQ.getIntValue(), PPAQ.POOL_ID.getIntValue());
			if(ppaqSubAttr != null){
				wimaxRequest.getPPAQ().setPoolID(ppaqSubAttr.getIntValue());
			}
			ppaqSubAttr = serviceRequest.getRadiusAttribute(lVendorId, WimaxAttrConstants.PPAQ.getIntValue(), PPAQ.POOL_MULTIPLIER.getIntValue());
			if(ppaqSubAttr != null){
				wimaxRequest.getPPAQ().setPoolMultiplier(ppaqSubAttr.getIntValue());
			}
			ppaqSubAttr = serviceRequest.getRadiusAttribute(lVendorId, WimaxAttrConstants.PPAQ.getIntValue(), PPAQ.PREPAID_SERVER.getIntValue());
			if(ppaqSubAttr != null){
				wimaxRequest.getPPAQ().setPrepaidServer(ppaqSubAttr.getStringValue());
			}
			ppaqSubAttr = serviceRequest.getRadiusAttribute(lVendorId, WimaxAttrConstants.PPAQ.getIntValue(), PPAQ.QUOTA_IDENTIFIER.getIntValue());
			if(ppaqSubAttr != null){
				wimaxRequest.getPPAQ().setQuotaIdentifier(ppaqSubAttr.getIntValue());
			}
			ppaqSubAttr = serviceRequest.getRadiusAttribute(lVendorId, WimaxAttrConstants.PPAQ.getIntValue(), PPAQ.RATING_GROUP_ID.getIntValue());
			if(ppaqSubAttr != null){
				wimaxRequest.getPPAQ().setRatingGroupID(ppaqSubAttr.getIntValue());	
			}
			ppaqSubAttr = serviceRequest.getRadiusAttribute(lVendorId, WimaxAttrConstants.PPAQ.getIntValue(), PPAQ.REQUESTED_ACTION.getIntValue());
			if(ppaqSubAttr != null){
				wimaxRequest.getPPAQ().setRequestedAction(ppaqSubAttr.getIntValue());
			}
			ppaqSubAttr = serviceRequest.getRadiusAttribute(lVendorId, WimaxAttrConstants.PPAQ.getIntValue(), PPAQ.RESOURCE_QUOTA.getIntValue());
			if(ppaqSubAttr != null){
				wimaxRequest.getPPAQ().setResourceQuota(ppaqSubAttr.getIntValue());
			}
			ppaqSubAttr = serviceRequest.getRadiusAttribute(lVendorId, WimaxAttrConstants.PPAQ.getIntValue(), PPAQ.RESOURCE_THRESHOLD.getIntValue());
			if(ppaqSubAttr != null){
				wimaxRequest.getPPAQ().setResourceThreshold(ppaqSubAttr.getIntValue());
			}
			ppaqSubAttr = serviceRequest.getRadiusAttribute(lVendorId, WimaxAttrConstants.PPAQ.getIntValue(), PPAQ.SERVICE_ID.getIntValue());
			if(ppaqSubAttr != null){
				wimaxRequest.getPPAQ().setServiceID(ppaqSubAttr.getStringValue());
			}
			ppaqSubAttr = serviceRequest.getRadiusAttribute(lVendorId, WimaxAttrConstants.PPAQ.getIntValue(), PPAQ.TERMINATION_ACTION.getIntValue());
			if(ppaqSubAttr != null){
				wimaxRequest.getPPAQ().setTerminationAction(ppaqSubAttr.getIntValue());
			}
			ppaqSubAttr = serviceRequest.getRadiusAttribute(lVendorId, WimaxAttrConstants.PPAQ.getIntValue(), PPAQ.UPDATE_REASON.getIntValue());
			if(ppaqSubAttr != null){
				wimaxRequest.getPPAQ().setUpdateReason(ppaqSubAttr.getIntValue());
			}
			ppaqSubAttr = serviceRequest.getRadiusAttribute(lVendorId, WimaxAttrConstants.PPAQ.getIntValue(), PPAQ.VOLUME_QUOTA.getIntValue());
			if(ppaqSubAttr != null){
				wimaxRequest.getPPAQ().setVolumeQuota(ppaqSubAttr.getIntValue());
			}
			ppaqSubAttr = serviceRequest.getRadiusAttribute(lVendorId, WimaxAttrConstants.PPAQ.getIntValue(), PPAQ.VOLUME_THRESHOLD.getIntValue());
			if(ppaqSubAttr != null){
				wimaxRequest.getPPAQ().setVolumeThreshold(ppaqSubAttr.getIntValue());
			}
			
		}
		//setting the eap values
		if(serviceRequest.getEapSession() != null){
			wimaxRequest.getEAP().setCurrentMethod(serviceRequest.getEapSession().getCurrentMethod());
			wimaxRequest.getEAP().setEapIdentity((String)serviceRequest.getParameter(EAP_IDENTITY));
			wimaxRequest.getEAP().setSessionID((String)serviceRequest.getParameter(EAP_SESSION_ID));
		}
		wimaxRequest.getEAP().setMSK((byte[])serviceRequest.getParameter(EAP_KEY_DATA));
		if(serviceRequest.getRadiusAttribute(RadiusAttributeConstants.EAP_MESSAGE) != null){
			wimaxRequest.getEAP().setEapMessage(serviceRequest.getRadiusAttribute(RadiusAttributeConstants.EAP_MESSAGE).getStringValue());
		}
		
		//ADDING THE CISCO SPECIFIC ATTRIBUTES
//		if(serviceRequest.getRadiusAttribute(RadiusConstants.CISCO_VENDOR_ID,RadiusAttributeConstants.CISCO_SSG_SERVICE_INFO)!=null){
			//wimaxRequest.getCisco().setCisco_ssg_service_info(serviceRequest.getRadiusAttributes(RadiusConstants.CISCO_VENDOR_ID,RadiusAttributeConstants.CISCO_SSG_SERVICE_INFO).iterator().next().getStringValue().trim().equalsIgnoreCase("N-prepaid-3g"))
//		}
		return wimaxRequest;
	}

	@Override
	public WimaxResponse formWimaxResponse(RadAuthRequest serviceRequest, RadAuthResponse serviceResponse) {
		WimaxResponse wimaxResponse = new WimaxResponse();

		IRadiusAttribute attribute = serviceResponse.getRadiusAttribute(RadiusConstants.WIMAX_VENDOR_ID, WimaxAttrConstants.DHCPV4_SERVER.getIntValue());
		if (attribute != null) {
			wimaxResponse.getDHCP().setDHCP_Server(attribute.getStringValue());
		}
		attribute = serviceResponse.getRadiusAttribute(RadiusConstants.WIMAX_VENDOR_ID,WimaxAttrConstants.HA_IP_MIP4.getIntValue());
		if (attribute != null) {
			wimaxResponse.getHA().setHA_IP_MIP4(attribute.getStringValue());
		}
		
		//setting the client data
		if(serviceResponse.getClientData() != null){
			wimaxResponse.getClientData().setDHCPAddress(serviceResponse.getClientData().getDHCPAddress());
			wimaxResponse.getClientData().setDNSList(serviceResponse.getClientData().getDnsList());
			wimaxResponse.getClientData().setHAAddress(serviceResponse.getClientData().getHAAddress());
			wimaxResponse.getClientData().setSharedSecret(serviceResponse.getClientData().getSharedSecret(serviceRequest.getPacketType()));
		}
		
		//setting the session timeout
		attribute = serviceResponse.getRadiusAttribute(RadiusAttributeConstants.SESSION_TIMEOUT);
		if (attribute != null) {
			wimaxResponse.setSessionTimeoutInSeconds(attribute.getIntValue());
		}
		return wimaxResponse;
	}
	
	
	@Override
	public void createServiceResponse(RadAuthRequest request, RadAuthResponse response, WimaxRequest wimaxRequest, WimaxResponse wimaxResponse){
		
		//setting the packet type of the service response
			if(wimaxResponse.getResultCode() == ResultCode.AUTH_SUCCESS){
				response.setPacketType(RadiusConstants.ACCESS_ACCEPT_MESSAGE);
		}else if(wimaxResponse.getResultCode() == ResultCode.AUTH_REJECT){
			response.setPacketType(RadiusConstants.ACCESS_REJECT_MESSAGE);
				}
				
				if(wimaxResponse.getResponseMessage() != null){
					response.setResponseMessage(wimaxResponse.getResponseMessage());
				}
				
		//Putting the DHCP_RK, DHCP_RK_KEY_ID, DHCP_RK_LIFETIME
		if(!wimaxResponse.isFurtherProcessingRequired()){
			return;
		}

		if(isDHCPRequest(wimaxRequest, wimaxResponse)){			
			addDHCPAttributes(request,response,wimaxResponse);	
			return;
		}
		
		if(isHARequest(wimaxRequest, wimaxResponse)){
			addCUIAndUserName(response,wimaxRequest,wimaxResponse);
			addMessageAuthenticator(response);
			addMandatoryWimaxAttributes(wimaxRequest,wimaxResponse,request, response);
			addHAAttributes(request,response,wimaxRequest, wimaxResponse);
			return;
			
		}
		
		addMessageAuthenticator(response);
		
		if(isAuthorizeOnlyRequest(wimaxRequest, wimaxResponse)){
			addMandatoryWimaxAttributes(wimaxRequest,wimaxResponse,request, response);
			return;
		}
		
		if(isAuthenticateOnlyRequest(wimaxRequest, wimaxResponse)){
			addAuthOnlyAndEapRequestAttributes(request, response, wimaxResponse);			
			addMandatoryWimaxAttributes(wimaxRequest,wimaxResponse,request, response);
			return;
		}
		
		if(isEAPRequest(wimaxRequest, wimaxResponse)){
			addAuthOnlyAndEapRequestAttributes(request, response, wimaxResponse);
			addMandatoryWimaxAttributes(wimaxRequest,wimaxResponse,request, response);
			return;
		}else{
			//this means that the request was not for EAP so add all the mandatory WiMAX attributes
			addMandatoryWimaxAttributes(wimaxRequest, wimaxResponse, request, response);
		}
	}

	private void addMessageAuthenticator(RadAuthResponse response){
		//adding the mandatory message authenticator attribute
		IRadiusAttribute messageAuthenticator = response.getRadiusAttribute(RadiusAttributeConstants.MESSAGE_AUTHENTICATOR);
		if(messageAuthenticator == null){
			messageAuthenticator = Dictionary.getInstance().getKnownAttribute(RadiusAttributeConstants.MESSAGE_AUTHENTICATOR);
			if(messageAuthenticator == null){
				if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
					LogManager.getLogger().warn(MODULE, "Message Authenticator(0:80) attribute not found in dictionary.");
				}
			}else{
				messageAuthenticator.setValueBytes(new byte[16]);
				response.addAttribute(messageAuthenticator);
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
					LogManager.getLogger().debug(MODULE, "Message Authenticator(0:80) added in response.");
				}
			}
		}
	}

	/* FIXME scheduled for removal in 6.6.4 
	 * Reason: WiMAX HA request handling always overwrites the CUI and USERNAME attributes with Inner identity,
	 * which affects CUI and Username response attribute existing value added according to policy configuration eg. Group.
	 * So need to remove this method
	 */
	private void addCUIAndUserName(RadAuthResponse response,WimaxRequest wimaxRequest, WimaxResponse wimaxResponse){
//		String cui = wimaxResponse.getCUI();
//		if(cui != null){
//
//			IRadiusAttribute cuiAttr = response.getRadiusAttribute(RadiusAttributeConstants.CUI);
//			if(cuiAttr ==null){
//				cuiAttr = Dictionary.getInstance().getKnownAttribute(RadiusAttributeConstants.CUI);
//				if(cuiAttr != null){
//					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
//						LogManager.getLogger().debug(MODULE, "CUI(0:89) attribute not found in HA response, adding with the value of CUI: " + cui);
//					}
//					cuiAttr.setStringValue(cui);
//					response.addAttribute(cuiAttr);
//				}else{
//					if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
//						LogManager.getLogger().warn(MODULE, "CUI(0:89) attribute not found in dictionary.");
//					}
//				}
//			}else {
//				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
//					LogManager.getLogger().debug(MODULE, "CUI(0:89) attribute already present in HA response, replacing with the value of CUI: " + cui);
//				}
//				cuiAttr.setStringValue(cui);
//			}
//			
//			IRadiusAttribute userName = response.getRadiusAttribute(RadiusAttributeConstants.USER_NAME);
//			if(userName == null){
//				userName = Dictionary.getInstance().getKnownAttribute(RadiusAttributeConstants.USER_NAME);
//				if(userName != null){
//					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
//						LogManager.getLogger().debug(MODULE, "User-Name(0:1) attribute not found in HA response, adding with the value of CUI: " + cui);
//					}
//					userName.setStringValue(cui);
//					response.addAttribute(userName);
//				}else{
//					if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
//						LogManager.getLogger().warn(MODULE, "User-Name(0:1) attribute not found in dictionary.");
//					}
//				}
//			}else {
//				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
//					LogManager.getLogger().debug(MODULE, "User-Name(0:1) attribute already present in HA response, replacing with the value of CUI: " + cui);
//				}
//				userName.setStringValue(cui);
//			}
//			
//		}else {
//			if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
//				LogManager.getLogger().warn(MODULE, "CUI not found for Session: "+wimaxRequest.getAAA_Session_ID());
//		}
	}
	private void addMandatoryWimaxAttributes(WimaxRequest wimaxRequest, WimaxResponse wimaxResponse,RadAuthRequest request, RadAuthResponse response) {
		long lVendorId = RadiusConstants.WIMAX_VENDOR_ID;
		
		//this special check has been kept due to JIRA ELTIEAAA-1880, if the WiMAX processing is done remotely then
		//Our AAA will not add this attribute
		if(response.getRadiusAttribute(lVendorId, WimaxAttrConstants.AAA_SESSION_ID.getIntValue()) == null){
			IRadiusAttribute aaaSessionIdAttr = Dictionary.getInstance().getKnownAttribute(lVendorId, WimaxAttrConstants.AAA_SESSION_ID.getIntValue());
			if (aaaSessionIdAttr != null) {
				aaaSessionIdAttr.setStringValue(wimaxResponse.getAAA_Session_ID());
				response.addAttribute(aaaSessionIdAttr);
			} else {
				if (LogManager.getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "Unable to add AAA-Session-id(24757:" 
							+ WimaxAttrConstants.AAA_SESSION_ID.getIntValue() + ") attribute, Reason: not found in dictionary");
				}
			}
		}else{
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
				LogManager.getLogger().info(MODULE, "AAA-Session-ID (24757:4) already present in response will not be added.");
			}
		}
		
		
		//wimax capabilities
		
		//the checks that were done in the previous version are to be done here
		if(response.getRadiusAttribute(lVendorId , WimaxAttrConstants.WIMAX_CAPABILITY.getIntValue()) == null ){
			
			WimaxGroupedAttribute wimaxCapabilityAttr = (WimaxGroupedAttribute)Dictionary.getInstance()
					.getKnownAttribute(lVendorId, WimaxAttrConstants.WIMAX_CAPABILITY.getIntValue());
			if (wimaxCapabilityAttr != null) {
				BaseRadiusAttribute acctCapAttr = (BaseRadiusAttribute)Dictionary.getInstance().getKnownAttribute(lVendorId, 
						WimaxAttrConstants.WIMAX_CAPABILITY.getIntValue(), 
						WimaxCapability.ACCOUNTING_CAPABILITIES.getIntValue());	
				if (acctCapAttr != null) {
					//accounting capabilities
					acctCapAttr.setIntValue(wimaxResponse.getWimaxCapabilities().getAccountingCapabilities());
					wimaxCapabilityAttr.addTLVAttribute(acctCapAttr);
				} else {
					if (LogManager.getLogger().isDebugLogLevel()) {
						LogManager.getLogger().debug(MODULE, "Unable to add Accounting-Capabilities(24757:" 
								+ WimaxCapability.ACCOUNTING_CAPABILITIES.getIntValue() 
								+ ") attribute, Reason: not found in dictionary");
					}
				}
				
				
				//hotlining capabilities
				if(wimaxResponse.getWimaxCapabilities().getHotliningCapabilities() != null){
					BaseRadiusAttribute hotliningAttr = (BaseRadiusAttribute)Dictionary.getInstance().getKnownAttribute(lVendorId, 
							WimaxAttrConstants.WIMAX_CAPABILITY.getIntValue(), 
							WimaxCapability.HOTLINING_CAPABILITIES.getIntValue());
					if (hotliningAttr != null) {
						hotliningAttr.setIntValue(wimaxResponse.getWimaxCapabilities().getHotliningCapabilities());
						wimaxCapabilityAttr.addTLVAttribute(hotliningAttr);
					} else {
						if (LogManager.getLogger().isDebugLogLevel()) {
							LogManager.getLogger().debug(MODULE, "Unable to add Hotlining-Capabilities(24757:" 
									+ WimaxCapability.HOTLINING_CAPABILITIES.getIntValue() 
									+ ") attribute, Reason: not found in dictionary");
						}
					}
				}
				
				//Idle Mode Notification Capabilities
				BaseRadiusAttribute idleModeNotificationCapabilityAttr = (BaseRadiusAttribute)Dictionary.getInstance().getKnownAttribute(lVendorId,
						WimaxAttrConstants.WIMAX_CAPABILITY.getIntValue(), 
						WimaxAttrConstants.WimaxCapability.IDLE_MODE_NOTIFICATION_CAPABILITIES.getIntValue());
				if (idleModeNotificationCapabilityAttr != null) {
					idleModeNotificationCapabilityAttr.setIntValue(wimaxResponse.getWimaxCapabilities().getIdleModeNotificationCapabilities());
					wimaxCapabilityAttr.addTLVAttribute(idleModeNotificationCapabilityAttr);
				} else {
					if (LogManager.getLogger().isDebugLogLevel()) {
						LogManager.getLogger().debug(MODULE, "Unable to add Idle-Mode-Notification-capabilities(24757:" 
								+ WimaxAttrConstants.WimaxCapability.IDLE_MODE_NOTIFICATION_CAPABILITIES.getIntValue() 
								+ ") attribute, Reason: not found in dictionary");
					}
				}
				
				//adding the wimax capability attribute to the response
				response.addAttribute(wimaxCapabilityAttr);
			} else {
				if (LogManager.getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "Unable to add Wimax-capability-attribute(24757:" 
							+ WimaxAttrConstants.WIMAX_CAPABILITY.getIntValue() + ") attribute, Reason: not found in dictionary");
				}
			}
			
		}else{
			
			WimaxGroupedAttribute wimaxCapAttr = (WimaxGroupedAttribute)((RadAuthResponse)response).getRadiusAttribute(RadiusConstants.WIMAX_VENDOR_ID, WimaxAttrConstants.WIMAX_CAPABILITY.getIntValue());
			
			if(wimaxCapAttr.getAttribute(WimaxAttrConstants.WimaxCapability.ACCOUNTING_CAPABILITIES.getIntValue()) == null){
				if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
					LogManager.getLogger().info(MODULE, "Wimax-Capability attribute is present in response but does not contain Accounting Capabilities, using value from configuration");
				IRadiusAttribute acctCapAttr = Dictionary.getInstance().getKnownAttribute(lVendorId, WimaxAttrConstants.WIMAX_CAPABILITY.getIntValue(), WimaxAttrConstants.WimaxCapability.ACCOUNTING_CAPABILITIES.getIntValue());
				if (acctCapAttr != null) {
					//getting the value from the wimaxResponse where the value has been set from the wimax configuration
					acctCapAttr.setIntValue(wimaxResponse.getWimaxCapabilities().getAccountingCapabilities());
					wimaxCapAttr.addTLVAttribute(acctCapAttr);
				} else {
					if (LogManager.getLogger().isDebugLogLevel()) {
						LogManager.getLogger().debug(MODULE, "Unable to add Accounting-Capabilities(24757:" 
								+ WimaxAttrConstants.WimaxCapability.ACCOUNTING_CAPABILITIES.getIntValue() 
								+ ") attribute, Reason: not found in dictionary");
					}
				}
			}
			if(wimaxCapAttr.getAttribute(WimaxAttrConstants.WimaxCapability.HOTLINING_CAPABILITIES.getIntValue()) == null ){
				if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
					LogManager.getLogger().info(MODULE, "Wimax-Capability attribute is present in response but does not contain Hotlining Capabilities, using value from request packet");
				IRadiusAttribute resHotlineAttr = Dictionary.getInstance().getKnownAttribute(lVendorId, WimaxAttrConstants.WIMAX_CAPABILITY.getIntValue(), WimaxAttrConstants.WimaxCapability.HOTLINING_CAPABILITIES.getIntValue());;
				if (resHotlineAttr != null) {
					IRadiusAttribute reqWimaxhotlineAttr = ((RadAuthRequest)request).getRadiusAttribute(RadiusConstants.WIMAX_VENDOR_ID, WimaxAttrConstants.WIMAX_CAPABILITY.getIntValue(), WimaxAttrConstants.WimaxCapability.HOTLINING_CAPABILITIES.getIntValue());
					if(reqWimaxhotlineAttr != null){
						//this means that the request contains hotline capabilities and copy that same value
						resHotlineAttr.setIntValue(reqWimaxhotlineAttr.getIntValue());	
					}else{
						if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
							LogManager.getLogger().info(MODULE, "Wimax-Capability attribute is present in request but does not contain Hotlining Capabilities");
						if(wimaxResponse.getWimaxCapabilities().getHotliningCapabilities() != null)
							resHotlineAttr.setIntValue(wimaxResponse.getWimaxCapabilities().getHotliningCapabilities());
					}
					wimaxCapAttr.addTLVAttribute(resHotlineAttr);
				} else {
					if (LogManager.getLogger().isDebugLogLevel()) {
						LogManager.getLogger().debug(MODULE, "Unable to add Hotlining-Capabilities(24757:" 
								+ WimaxAttrConstants.WimaxCapability.HOTLINING_CAPABILITIES.getIntValue() 
								+ ") attribute, Reason: not found in dictionary");
					}
				}
			}
			if(wimaxCapAttr.getAttribute(WimaxAttrConstants.WimaxCapability.IDLE_MODE_NOTIFICATION_CAPABILITIES.getIntValue()) == null ){
				if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
					LogManager.getLogger().info(MODULE, "Wimax-Capability attribute is present in response but does not contain Idle Mode Notification Capabilities, using value from request packet");
				IRadiusAttribute resIdleModeNotificationAttr = Dictionary.getInstance().getKnownAttribute(lVendorId, WimaxAttrConstants.WIMAX_CAPABILITY.getIntValue(), WimaxAttrConstants.WimaxCapability.IDLE_MODE_NOTIFICATION_CAPABILITIES.getIntValue());
				if (resIdleModeNotificationAttr != null) {
					IRadiusAttribute reqIdleModeNotificationAttr = ((RadAuthRequest)request).getRadiusAttribute(RadiusConstants.WIMAX_VENDOR_ID, WimaxAttrConstants.WIMAX_CAPABILITY.getIntValue(), WimaxAttrConstants.WimaxCapability.IDLE_MODE_NOTIFICATION_CAPABILITIES.getIntValue());
					if(reqIdleModeNotificationAttr != null){
						//this means that the request contains hotline capabilities and copy that same value
						resIdleModeNotificationAttr.setIntValue(reqIdleModeNotificationAttr.getIntValue());	
					}else{
						resIdleModeNotificationAttr.setIntValue(wimaxResponse.getWimaxCapabilities().getIdleModeNotificationCapabilities());
					}
					wimaxCapAttr.addTLVAttribute(resIdleModeNotificationAttr);
				} else {
					if (LogManager.getLogger().isDebugLogLevel()) {
						LogManager.getLogger().debug(MODULE, "Unable to add Idle-Mode-Notification-Capabilities(24757:" 
								+ WimaxAttrConstants.WimaxCapability.IDLE_MODE_NOTIFICATION_CAPABILITIES.getIntValue() 
								+ ") attribute, Reason: not found in dictionary");
					}
				}
			}
			
		}
		
		//DNS list and the check that was done in the older during adding in the service response
		if(response.getRadiusAttribute(lVendorId ,WimaxAttrConstants.DNS.getIntValue()) == null && request.getRadiusAttribute(lVendorId,WimaxAttrConstants.MN_HA_MIP4_SPI.getIntValue()) == null){
			List<byte[]> dnsList = wimaxResponse.getDNSList();
			if(dnsList == null){
				LogManager.getLogger().info(MODULE, "DNS List is not configured for the client " + response.getClientData().getClientIp());
				return;
			}
			for(int i = 0; i < dnsList.size(); i++){
				IRadiusAttribute dnsAttrib = Dictionary.getInstance().getKnownAttribute(lVendorId, WimaxAttrConstants.DNS.getIntValue());
				if (dnsAttrib != null) {
					dnsAttrib.setValueBytes(dnsList.get(i));
					response.addAttribute(dnsAttrib);
				} else {
					if (LogManager.getLogger().isDebugLogLevel()) {
						LogManager.getLogger().debug(MODULE, "Unable to add DNS-Attribute(24757:" 
								+ WimaxAttrConstants.DNS.getIntValue() + ") attribute, Reason: not found in dictionary");
					}
				}
			}
		}
	}

	private void addAuthOnlyAndEapRequestAttributes(RadAuthRequest request,RadAuthResponse response, WimaxResponse wimaxResponse) {
		int salt = 32768;
		long lVendorId = RadiusConstants.WIMAX_VENDOR_ID;
		//setting the DHCP_Server_address
		if(wimaxResponse.getDHCP().getDHCP_Server() != null && !CommonConstants.RESERVED_IPV_4_ADDRESS.equals(wimaxResponse.getDHCP().getDHCP_Server())){
			IRadiusAttribute dhcp_ip_addr_attr = Dictionary.getInstance().getKnownAttribute(lVendorId, WimaxAttrConstants.DHCPV4_SERVER.getIntValue());
			if (dhcp_ip_addr_attr != null) {
				dhcp_ip_addr_attr.setStringValue(wimaxResponse.getDHCP().getDHCP_Server());
				response.addAttribute(dhcp_ip_addr_attr);	
			} else {
				if (LogManager.getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "Unable to add DHCPv4_Server(24757:" 
							+ WimaxAttrConstants.DHCPV4_SERVER.getIntValue() 
							+ ") attribute, Reason: not found in dictionary");
				}
			}
		}else{
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
				LogManager.getLogger().info(MODULE, "DHCP IP address is " + wimaxResponse.getDHCP().getDHCP_Server() + ", DHCPv4_Server (24757:8) will not be added in response");
			}
		}
		
		IRadiusAttribute radAttr = null;

		//			 HA_IP_MIP4
		//added the check for the reserved IP address for HA IP MIP4
		if(!CommonConstants.RESERVED_IPV_4_ADDRESS.equals(wimaxResponse.getHA().getHA_IP_MIP4())){
			IRadiusAttribute ha_ip_mip4_attr = Dictionary.getInstance().getKnownAttribute(lVendorId, 
					WimaxAttrConstants.HA_IP_MIP4.getIntValue());
			if (ha_ip_mip4_attr != null) {
				ha_ip_mip4_attr.setStringValue(wimaxResponse.getHA().getHA_IP_MIP4());
				response.addAttribute(ha_ip_mip4_attr);
			} else {
				if (LogManager.getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "Unable to add HA-IP-MIP4(24757:" 
							+ WimaxAttrConstants.HA_IP_MIP4.getIntValue() + ") attribute, Reason: not found in dictionary");
				}
			}
		}else{
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
				LogManager.getLogger().info(MODULE, "HA IP address is " + wimaxResponse.getHA().getHA_IP_MIP4() + ", HA_IP_MIP4 (24757:6) will not be added in response");
			}
		}

		//		 HA_RK_KEY
		radAttr = Dictionary.getInstance().getKnownAttribute(lVendorId, WimaxAttrConstants.HA_RK_KEY.getIntValue());
		if (radAttr != null) {
			radAttr.setValue(wimaxResponse.getHA().getHA_RK_KEY(), salt+1, wimaxResponse.getClientData().getSharedSecret(),request.getAuthenticator());
			response.addAttribute(radAttr);							
		} else {
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Unable to add HA-RK-KEY(24757:" 
						+ WimaxAttrConstants.HA_RK_KEY.getIntValue() + ") attribute, Reason: not found in dictionary");
			}
		}

		//		 HA_RK_SPI
		radAttr = Dictionary.getInstance().getKnownAttribute(lVendorId, WimaxAttrConstants.HA_RK_SPI.getIntValue());
		if (radAttr != null) {
			radAttr.setIntValue(wimaxResponse.getHA().getHA_RK_SPI());
			response.addAttribute(radAttr);							
		} else {
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Unable to add HA-RK-SPI(24757:" 
						+ WimaxAttrConstants.HA_RK_SPI.getIntValue() + ") attribute, Reason: not found in dictionary");
			}
		}

		//		 HA_RK_Lifetime
		radAttr = Dictionary.getInstance().getKnownAttribute(lVendorId, WimaxAttrConstants.HA_RK_LIFETIME.getIntValue());
		if (radAttr != null) {
			radAttr.setLongValue(wimaxResponse.getHA().getHA_RK_LIFETIME());
			response.addAttribute(radAttr);	
		} else {
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Unable to add HA_RK_LIFETIME(24757:" 
						+ WimaxAttrConstants.HA_RK_LIFETIME.getIntValue() + ") attribute, Reason: not found in dictionary");
			}
		}
		
		//adding the wimax keys
		IRadiusAttribute mskAttr = Dictionary.getInstance().getKnownAttribute(lVendorId, WimaxAttrConstants.MSK.getIntValue());
		if (mskAttr != null) {
			mskAttr.setValue(wimaxResponse.getMSK(), salt, wimaxResponse.getClientData().getSharedSecret(),request.getAuthenticator());
			response.addAttribute(mskAttr);
		} else {
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Unable to add MSK(24757:" 
						+ WimaxAttrConstants.MSK.getIntValue() + ") attribute, Reason: not found in dictionary");
			}
		}
		
		
		if(wimaxResponse.getDHCP().getDHCP_Server() != null){			
			//setting the DHCP_RK
			radAttr = Dictionary.getInstance().getKnownAttribute(lVendorId, WimaxAttrConstants.DHCP_RK.getIntValue());
			if (radAttr != null) {
				radAttr.setValue(wimaxResponse.getDHCP().getDHCP_RK(), salt+1, wimaxResponse.getClientData().getSharedSecret(),request.getAuthenticator());
				response.addAttribute(radAttr);							
			} else {
				if (LogManager.getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "Unable to add DHCP_RK(24757:" + WimaxAttrConstants.DHCP_RK.getIntValue() 
							+ ") attribute, Reason: not found in dictionary");
				}
			}

			// DHCP_RK_ID
			radAttr = Dictionary.getInstance().getKnownAttribute(lVendorId, WimaxAttrConstants.DHCP_RK_KEY_ID.getIntValue());
			if (radAttr != null) {
				radAttr.setIntValue(wimaxResponse.getDHCP().getDHCP_RK_KEY_ID());
				response.addAttribute(radAttr);							
			} else {
				if (LogManager.getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "Unable to add DHCP_RK_KEY_ID(24757:" 
							+ WimaxAttrConstants.DHCP_RK_KEY_ID.getIntValue() + ") attribute, Reason: not found in dictionary");
				}
			}

			// DHCP_RK_Lifetime
			radAttr = Dictionary.getInstance().getKnownAttribute(lVendorId, WimaxAttrConstants.DHCP_RK_LIFETIME.getIntValue());
			if (radAttr != null) {
				radAttr.setLongValue(wimaxResponse.getDHCP().getDHCP_RK_LIFETIME());
				response.addAttribute(radAttr);	
			} else {
				if (LogManager.getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "Unable to add DHCP_RK_LIFETIME(24757:" 
							+ WimaxAttrConstants.DHCP_RK_LIFETIME.getIntValue() + ") attribute, Reason: not found in dictionary");
				}
			}
		}

		
		IRadiusAttribute sessionTimeout = ((RadAuthResponse)response).getRadiusAttribute(RadiusAttributeConstants.SESSION_TIMEOUT_STR);
		if(sessionTimeout == null){
			sessionTimeout = Dictionary.getInstance().getKnownAttribute(RadiusAttributeConstants.SESSION_TIMEOUT);
			if (sessionTimeout != null) {
				response.addAttribute(sessionTimeout);
			} else {
				if (LogManager.getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "Unable to add Session-Timeout(" 
							+ DiameterAVPConstants.SESSION_TIMEOUT + ") attribute, Reason: not found in dictionary");
				}
			}
		}
		sessionTimeout.setLongValue(wimaxResponse.getSessionTimeoutInSeconds());
		

		//			 MN_HA_MIP4_KEY
		radAttr = Dictionary.getInstance().getKnownAttribute(lVendorId, WimaxAttrConstants.MN_HA_MIP4_KEY.getIntValue());
		if (radAttr != null) {
			radAttr.setValue(wimaxResponse.getHA().getMN_HA_MIP4_KEY(), salt+2, wimaxResponse.getClientData().getSharedSecret(), request.getAuthenticator());
			response.addAttribute(radAttr);
		} else {
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Unable to add MN-HA-MIP4-KEY(24757:" 
						+ WimaxAttrConstants.MN_HA_MIP4_KEY.getIntValue() + ") attribute, Reason: not found in dictionary");
			}
		}

		//MN_HA_MIP4_SPI
		if(response.getRadiusAttribute(lVendorId,WimaxAttrConstants.MN_HA_MIP4_SPI.getIntValue()) == null){
			radAttr = Dictionary.getInstance().getKnownAttribute(lVendorId, WimaxAttrConstants.MN_HA_MIP4_SPI.getIntValue());
			if (radAttr != null) {
				radAttr.setLongValue(wimaxResponse.getHA().getMN_HA_MIP4_SPI());
				response.addAttribute(radAttr);
			} else {
				if (LogManager.getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "Unable to add MN-HA-MIP4-SPI(24757:" 
							+ WimaxAttrConstants.MN_HA_MIP4_SPI.getIntValue() + ") attribute, Reason: not found in dictionary");
				}
			}
		}else{
			// not sure about this test
			radAttr = response.getRadiusAttribute(lVendorId,WimaxAttrConstants.MN_HA_MIP4_SPI.getIntValue());
			radAttr.setLongValue(wimaxResponse.getHA().getMN_HA_MIP4_SPI());
		}
		//FA_RK_KEY
		radAttr = Dictionary.getInstance().getKnownAttribute(lVendorId, WimaxAttrConstants.FA_RK_KEY.getIntValue());
		if (radAttr != null) {
			radAttr.setValue(wimaxResponse.getFA().getFA_RK_KEY(), salt+3, wimaxResponse.getClientData().getSharedSecret(),request.getAuthenticator());
			response.addAttribute(radAttr);						
		} else {
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Unable to add FA-RK-KEY(24757:" 
						+ WimaxAttrConstants.FA_RK_KEY.getIntValue() + ") attribute, Reason: not found in dictionary");
			}
		}
		
		//		 FA_RK_SPI (value equals MN_HA_CMIP4_SPI)
		radAttr = Dictionary.getInstance().getKnownAttribute(lVendorId, WimaxAttrConstants.FA_RK_SPI.getIntValue());
		if (radAttr != null) {
			radAttr.setLongValue(wimaxResponse.getFA().getFA_RK_SPI());
			response.addAttribute(radAttr);
		} else {
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Unable to add FA_RK_SPI(24757:" 
						+ WimaxAttrConstants.FA_RK_SPI.getIntValue() + ") attribute, Reason: not found in dictionary");
			}
		}

		
	}

	private void addHAAttributes(RadAuthRequest request,RadAuthResponse response, WimaxRequest wimaxRequest, WimaxResponse wimaxResponse) {
		//add HA_RK_KEY
		long lVendorId = RadiusConstants.WIMAX_VENDOR_ID;
		int salt = 32768;
		if(wimaxRequest.getHA().getHA_RK_SPI() != null){
			IRadiusAttribute radAttribute = Dictionary.getInstance().getKnownAttribute(lVendorId, WimaxAttrConstants.HA_RK_KEY.getIntValue());
			if (radAttribute != null) {
				radAttribute.setValue(wimaxResponse.getHA().getHA_RK_KEY(), salt , wimaxResponse.getClientData().getSharedSecret(), request.getAuthenticator());
				response.addAttribute(radAttribute);
			} else {
				if (LogManager.getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "Unable to add HA-RK-KEY(24757:" 
							+ WimaxAttrConstants.HA_RK_KEY.getIntValue() + ") attribute, Reason: not found in dictionary");
				}
			}

			// add HA_RK_SPI
			radAttribute = Dictionary.getInstance().getKnownAttribute(lVendorId, WimaxAttrConstants.HA_RK_SPI.getIntValue());
			if (radAttribute != null) {
				radAttribute.setIntValue(wimaxResponse.getHA().getHA_RK_SPI());
				response.addAttribute(radAttribute);
			} else {
				if (LogManager.getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "Unable to add HA-RK-SPI(24757:" 
							+ WimaxAttrConstants.HA_RK_SPI.getIntValue() + ") attribute, Reason: not found in dictionary");
				}
			}

			// add HA_RK_LIFETIME
			radAttribute = Dictionary.getInstance().getKnownAttribute(lVendorId, WimaxAttrConstants.HA_RK_LIFETIME.getIntValue());
			if (radAttribute != null) {
				radAttribute.setLongValue(wimaxResponse.getHA().getHA_RK_LIFETIME());
				response.addAttribute(radAttribute);
			} else {
				if (LogManager.getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "Unable to add HA_RK_LIFETIME(24757:" 
							+ WimaxAttrConstants.HA_RK_LIFETIME.getIntValue() + ") attribute, Reason: not found in dictionary");
				}
			}
		}
		
		//	add	MN_HA_MIP4_KEY
		IRadiusAttribute radAttribute = Dictionary.getInstance().getKnownAttribute(lVendorId, WimaxAttrConstants.MN_HA_MIP4_KEY.getIntValue());
		if (radAttribute != null) {
			radAttribute.setValue(wimaxResponse.getHA().getMN_HA_MIP4_KEY(), salt+2, wimaxResponse.getClientData().getSharedSecret(), request.getAuthenticator());
			response.addAttribute(radAttribute);
		} else {
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Unable to add MN-HA-MIP4-KEY(24757:" 
						+ WimaxAttrConstants.MN_HA_MIP4_KEY.getIntValue() + ") attribute, Reason: not found in dictionary");
			}
		}
		
//		add	MN_HA_MIP4_SPI
		radAttribute = Dictionary.getInstance().getKnownAttribute(lVendorId, WimaxAttrConstants.MN_HA_MIP4_SPI.getIntValue());
		if (radAttribute != null) {
			radAttribute.setLongValue(wimaxResponse.getHA().getMN_HA_MIP4_SPI());
			response.addAttribute(radAttribute);							
		} else {
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Unable to add MN-HA-MIP4-SPI(24757:" 
						+ WimaxAttrConstants.MN_HA_MIP4_SPI.getIntValue() + ") attribute, Reason: not found in dictionary");
			}
		}
		
		
//		//adding the CUI if present in the wiMAX response
//		String cui = wimaxResponse.getCUI();
//		if(cui != null){
//			
//			IRadiusAttribute cuiAttr = response.getRadiusAttribute(RadiusAttributeConstants.CUI);
//			if(cuiAttr ==null){
//				cuiAttr = Dictionary.getInstance().getKnownAttribute(RadiusAttributeConstants.CUI);
//				if(cuiAttr!=null){
//					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
//						LogManager.getLogger().debug(MODULE, "CUI(0:89) attribute not present in HA Response, adding the attribute with value of CUI: " + cui);
//	}
//					cuiAttr.setStringValue(cui);
//					response.addAttribute(cuiAttr);
//				}else{
//					if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
//						LogManager.getLogger().warn(MODULE, "CUI(0:89) attribute not found in dictionary.");
//					}
//				}
//			}else {
//				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
//					LogManager.getLogger().debug(MODULE, "CUI(0:89) attribute already present in HA Response. Replacing the value with value of CUI : " + cui);
//				}
//				cuiAttr.setStringValue(cui);
//			}
//
//			IRadiusAttribute userName = response.getRadiusAttribute(RadiusAttributeConstants.USER_NAME);
//			if(userName ==null){
//				userName = Dictionary.getInstance().getKnownAttribute(RadiusAttributeConstants.USER_NAME);
//				if(userName!=null){
//					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
//						LogManager.getLogger().debug(MODULE, "User-Name(0:1) attribute not present in HA Response, adding the attribute with value of CUI: " + cui);
//					}
//					userName.setStringValue(cui);
//					response.addAttribute(userName);
//				}else{
//					if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
//						LogManager.getLogger().warn(MODULE, "User-Name(0:1) attribute not found in dictionary.");
//					}
//				}
//			}else {
//				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
//					LogManager.getLogger().debug(MODULE, "User-Name(0:1) attribute already present in HA Response. Replacing the value with value of CUI : " + cui);
//				}
//				userName.setStringValue(cui);
//			}
//
//		}else {
//			if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
//				LogManager.getLogger().warn(MODULE, "CUI not found for Session: "+wimaxRequest.getAAA_Session_ID());
//		}
	}

	private void addDHCPAttributes(RadAuthRequest request, RadAuthResponse response, WimaxResponse wimaxResponse) {
		long lVendorId = RadiusConstants.WIMAX_VENDOR_ID; 
		int salt = 40000;		
				
		   // DHCP_RK
		IRadiusAttribute radAttribute = Dictionary.getInstance().getKnownAttribute(lVendorId, WimaxAttrConstants.DHCP_RK.getIntValue());
		if (radAttribute != null) {
			radAttribute.setValue(wimaxResponse.getDHCP().getDHCP_RK(), salt, wimaxResponse.getClientData().getSharedSecret(), request.getAuthenticator());
			response.addAttribute(radAttribute);
		} else {
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Unable to add DHCP_RK(24757:" 
						+ WimaxAttrConstants.DHCP_RK.getIntValue() + ") attribute, Reason: not found in dictionary");
			}
		}
		
		//		 DHCP_RK_ID
		radAttribute = Dictionary.getInstance().getKnownAttribute(lVendorId, WimaxAttrConstants.DHCP_RK_KEY_ID.getIntValue());
		if (radAttribute != null) {
			radAttribute.setIntValue( wimaxResponse.getDHCP().getDHCP_RK_KEY_ID());
			response.addAttribute(radAttribute);
		} else {
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Unable to add DHCP_RK_KEY_ID(24757:"
						+ WimaxAttrConstants.DHCP_RK_KEY_ID.getIntValue() + ") attribute, Reason: not found in dictionary");
			}
		}
		
		//DHCP_RK_LIFETIME
		radAttribute = Dictionary.getInstance().getKnownAttribute(lVendorId, WimaxAttrConstants.DHCP_RK_LIFETIME.getIntValue());
		if (radAttribute != null) {
			radAttribute.setLongValue(wimaxResponse.getDHCP().getDHCP_RK_LIFETIME());
			response.addAttribute(radAttribute);
		} else {
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Unable to add DHCP_RK_LIFETIME(24757:" 
						+ WimaxAttrConstants.DHCP_RK_LIFETIME.getIntValue() + ") attribute, Reason: not found in dictionary");
			}
		}
		
	}
}	
	
	