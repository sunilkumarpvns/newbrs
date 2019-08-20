package com.elitecore.aaa.diameter.service.application.handlers;

import com.elitecore.aaa.core.authprotocol.IAuthMethodHandler;
import com.elitecore.aaa.core.authprotocol.exception.AuthorizationFailedException;
import com.elitecore.aaa.core.conf.SPIKeyConfiguration;
import com.elitecore.aaa.core.conf.WimaxConfiguration;
import com.elitecore.aaa.core.eap.session.EAPSessionManager;
import com.elitecore.aaa.core.wimax.BaseWimaxHandler;
import com.elitecore.aaa.core.wimax.WimaxRequest;
import com.elitecore.aaa.core.wimax.WimaxResponse;
import com.elitecore.aaa.core.wimax.WimaxSessionManager;
import com.elitecore.aaa.core.wimax.keys.KeyManager;
import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.service.application.ApplicationResponse;
import com.elitecore.aaa.radius.service.auth.constant.AuthReplyMessageConstant;
import com.elitecore.aaa.util.constants.AAAServerConstants;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.util.constants.CommonConstants;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.core.servicex.ServiceContext;
import com.elitecore.coreeap.fsm.eap.IEapStateMachine;
import com.elitecore.coreradius.commons.util.constants.WimaxAttrConstants;
import com.elitecore.coreradius.commons.util.constants.WimaxAttrConstants.PPAQ;
import com.elitecore.coreradius.commons.util.constants.WimaxAttrConstants.ResultCode;
import com.elitecore.diameterapi.diameter.common.data.PeerData;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.packet.avps.grouped.AvpGrouped;
import com.elitecore.diameterapi.diameter.common.util.DiameterDictionary;
import com.elitecore.diameterapi.diameter.common.util.DiameterUtility;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterConstants;

import java.util.List;

import static com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants.CALLING_STATION_ID;

public class DiameterAuthWimaxHandler<T extends ApplicationRequest, V extends ApplicationResponse> extends BaseWimaxHandler<T, V> implements DiameterApplicationHandler<T, V>, IAuthMethodHandler {

	private static final String MODULE = "DIA_WIMAX_HANDLER";
	private static final String EAP_KEY_DATA = "EAP_KEY_DATA";
	private static final String EAP_IDENTITY = "EAP_IDENTITY";
	private static final String EAP_SESSION_ID = "EAP_SESSION_ID";
	private static final int ACCOUNTING_CAPABILITIES = 1025;
	private static final int HOTLINE_CAPABILITIES = 1026;
	private static final int IDLE_MODE_NOTIFICATION_CAPABILITIES = 1027;
	private static final String WIMAX_IP_TECHNOLOGY = "wimax-ip-technology";
	
	public DiameterAuthWimaxHandler(ServiceContext context, WimaxConfiguration wimaxConfiguration, SPIKeyConfiguration spiKeyConfiguration, 
			WimaxSessionManager wimaxSessionManager, EAPSessionManager eapSessionManager, KeyManager keyManager) {
		super(context, wimaxConfiguration, spiKeyConfiguration, wimaxSessionManager, eapSessionManager, keyManager);
	}

	@Override
	public void init() throws InitializationFailedException {
		
	}

	@Override
	public WimaxRequest formWimaxRequest(T serviceRequest) {
		WimaxRequest wimaxRequest = new WimaxRequest();
		
		//setting the SourceInfo parameters
		IDiameterAVP diameterAVP = serviceRequest.getInfoAvp(DiameterAVPConstants.EC_REQUESTER_ID);
		if (diameterAVP != null) {
			wimaxRequest.getSourceInfo().setSourceIP(diameterAVP.getStringValue());
		}
		diameterAVP = serviceRequest.getAVP(DiameterAVPConstants.NAS_IP_ADDRESS);
		if (diameterAVP != null) {
			wimaxRequest.getSourceInfo().setNAS_IP_Address(diameterAVP.getStringValue());
		}
		diameterAVP = serviceRequest.getAVP(DiameterAVPConstants.NAS_IDENTIFIER);
		if (diameterAVP != null) {
			wimaxRequest.getSourceInfo().setNAS_Identifier(diameterAVP.getStringValue());
		}
		diameterAVP = serviceRequest.getAVP(DiameterAVPConstants.NAS_PORT);
		if (diameterAVP != null) {
			wimaxRequest.getSourceInfo().setNAS_Port((int)diameterAVP.getInteger());
		}
		diameterAVP = serviceRequest.getAVP(DiameterAVPConstants.NAS_PORT_TYPE);
		if (diameterAVP != null) {
			wimaxRequest.getSourceInfo().setNAS_Port_Type((int)diameterAVP.getInteger());
		}
		diameterAVP = serviceRequest.getAVP(DiameterAVPConstants.NAS_PORT_ID);
		if (diameterAVP != null) {
			wimaxRequest.getSourceInfo().setNAS_Port_ID(diameterAVP.getStringValue());
		}
		
		//setting the CUI from the service request
		Object parameterValue = serviceRequest.getParameter(AAAServerConstants.CUI_KEY);
		if (parameterValue != null) {
			wimaxRequest.setCUI((String)parameterValue);
		}
		
		//setting the Username
		diameterAVP = serviceRequest.getAVP(DiameterAVPConstants.USER_NAME);
		if(diameterAVP != null){
			wimaxRequest.setUserName(diameterAVP.getStringValue());
		}
		
		//setting the calling station ID
		diameterAVP = serviceRequest.getAVP(CALLING_STATION_ID);
		if (diameterAVP != null) {
			wimaxRequest.getSourceInfo().setCalling_Station_ID(diameterAVP.getStringValue());
		} else if (serviceRequest.getAccountData() != null) {
			wimaxRequest.getSourceInfo().setCalling_Station_ID(serviceRequest.getAccountData().getCallingStationId());
		}
		
		//setting the DHCP_RK_KEY_ID
		diameterAVP = serviceRequest.getAVP(toWiMaxVendorId(WimaxAttrConstants.DHCP_RK_KEY_ID.getIntValue()));
		if (diameterAVP != null) {
			wimaxRequest.setDHCP_RK_KEY_ID((int)diameterAVP.getInteger());
		}
		
		//setting the HA parameters
		diameterAVP = serviceRequest.getAVP(toWiMaxVendorId(WimaxAttrConstants.MN_HA_MIP4_SPI.getIntValue()));
		if (diameterAVP != null) {
			wimaxRequest.getHA().setMN_HA_MIP4_SPI((int)diameterAVP.getInteger());
		}
		diameterAVP = serviceRequest.getAVP(toWiMaxVendorId(WimaxAttrConstants.HA_RK_SPI.getIntValue()));
		if (diameterAVP != null) {
			wimaxRequest.getHA().setHA_RK_SPI((int)diameterAVP.getInteger());
		}
		
		//setting the AAA session ID
		diameterAVP = serviceRequest.getAVP(toWiMaxVendorId(WimaxAttrConstants.AAA_SESSION_ID.getIntValue()));
		if (diameterAVP !=  null) {
			wimaxRequest.setAAA_Session_ID(diameterAVP.getStringValue());
		}
		
		AvpGrouped wimaxAttribute = (AvpGrouped) serviceRequest.getAVP(toWiMaxVendorId(WimaxAttrConstants.WIMAX_CAPABILITY.getIntValue())); 
		
		//setting the Wimax Capabilities
		if(wimaxAttribute != null){
		
			IDiameterAVP subWimaxAttribute = wimaxAttribute.getSubAttribute(toWiMaxVendorId(
					WimaxAttrConstants.WimaxCapability.WIMAX_RELEASE.getIntValue()));
			
			if(subWimaxAttribute != null){
				wimaxRequest.getWimaxCapabilities().setWimaxRelease((int)subWimaxAttribute.getInteger());
			}
			
			subWimaxAttribute = wimaxAttribute.getSubAttribute(toWiMaxVendorId(ACCOUNTING_CAPABILITIES));
			
			if(subWimaxAttribute != null){
				wimaxRequest.getWimaxCapabilities().setAccountingCapabilities((int)subWimaxAttribute.getInteger());
			}
			
			subWimaxAttribute = wimaxAttribute.getSubAttribute(toWiMaxVendorId(HOTLINE_CAPABILITIES));
			
			if(subWimaxAttribute != null){
				wimaxRequest.getWimaxCapabilities().setHotliningCapabilities((int)subWimaxAttribute.getInteger());
			}
			
			subWimaxAttribute = wimaxAttribute.getSubAttribute(toWiMaxVendorId(IDLE_MODE_NOTIFICATION_CAPABILITIES));
			
			if(subWimaxAttribute != null){
				wimaxRequest.getWimaxCapabilities().setIdleModeNotificationCapabilities((int)subWimaxAttribute.getInteger());
			}
		}
		
		
		//setting the service Type
		diameterAVP = serviceRequest.getAVP(DiameterAVPConstants.SERVICE_TYPE);
		if (diameterAVP != null) {
		wimaxRequest.setServiceType((int)diameterAVP.getInteger());
		}
		
		//setting the EAP parameters
		if(serviceRequest.getAVP(DiameterAVPConstants.EAP_PAYLOAD) != null){
			wimaxRequest.getEAP().setEapRequest(true);
		}
		
		//setting the PPAQ parameters
		if (serviceRequest.getAVP(toWiMaxVendorId(WimaxAttrConstants.PPAQ.getIntValue())) != null) {
			IDiameterAVP ppaqSubAttr = serviceRequest.getAVP(toWiMaxVendorId(WimaxAttrConstants.PPAQ.getIntValue(), 
					PPAQ.CHECK_BALANCE_RESULT.getIntValue()));
			if(ppaqSubAttr != null){
				wimaxRequest.getPPAQ().setCheckBalanceResult((int)ppaqSubAttr.getInteger());
			}
			ppaqSubAttr = serviceRequest.getAVP(toWiMaxVendorId(WimaxAttrConstants.PPAQ.getIntValue(), 
					PPAQ.COST_INFORMATION_AVP.getIntValue()));
			if(ppaqSubAttr != null){
				wimaxRequest.getPPAQ().setCostInformationAVP(ppaqSubAttr.getBytes());
			}
			ppaqSubAttr = serviceRequest.getAVP(toWiMaxVendorId(WimaxAttrConstants.PPAQ.getIntValue(), 
					PPAQ.DURATION_QUOTA.getIntValue()));
			if(ppaqSubAttr != null){
				wimaxRequest.getPPAQ().setDurationQuota((int)ppaqSubAttr.getInteger());
			}
			ppaqSubAttr = serviceRequest.getAVP(toWiMaxVendorId(WimaxAttrConstants.PPAQ.getIntValue(), 
					PPAQ.DURATION_THRESHOLD.getIntValue()));
			if(ppaqSubAttr != null){
				wimaxRequest.getPPAQ().setDurationThreshold((int)ppaqSubAttr.getInteger());
			}
			ppaqSubAttr = serviceRequest.getAVP(toWiMaxVendorId(WimaxAttrConstants.PPAQ.getIntValue(), 
					PPAQ.POOL_ID.getIntValue()));
			if(ppaqSubAttr != null){
				wimaxRequest.getPPAQ().setPoolID((int)ppaqSubAttr.getInteger());
			}
			ppaqSubAttr = serviceRequest.getAVP(toWiMaxVendorId(WimaxAttrConstants.PPAQ.getIntValue(), 
					PPAQ.POOL_MULTIPLIER.getIntValue()));
			if(ppaqSubAttr != null){
				wimaxRequest.getPPAQ().setPoolMultiplier((int)ppaqSubAttr.getInteger());
			}
			ppaqSubAttr = serviceRequest.getAVP(toWiMaxVendorId(WimaxAttrConstants.PPAQ.getIntValue(), 
					PPAQ.PREPAID_SERVER.getIntValue()));
			if(ppaqSubAttr != null){
				wimaxRequest.getPPAQ().setPrepaidServer(ppaqSubAttr.getStringValue());
			}
			ppaqSubAttr = serviceRequest.getAVP(toWiMaxVendorId(WimaxAttrConstants.PPAQ.getIntValue(), 
					PPAQ.QUOTA_IDENTIFIER.getIntValue()));
			if(ppaqSubAttr != null){
				wimaxRequest.getPPAQ().setQuotaIdentifier((int)ppaqSubAttr.getInteger());
			}
			
			ppaqSubAttr = serviceRequest.getAVP(toWiMaxVendorId(WimaxAttrConstants.PPAQ.getIntValue(), 
					PPAQ.RATING_GROUP_ID.getIntValue()));
			if(ppaqSubAttr != null){
				wimaxRequest.getPPAQ().setRatingGroupID((int)ppaqSubAttr.getInteger());	
			}
			ppaqSubAttr = serviceRequest.getAVP(toWiMaxVendorId(WimaxAttrConstants.PPAQ.getIntValue(), 
					PPAQ.REQUESTED_ACTION.getIntValue()));
			if(ppaqSubAttr != null){
				wimaxRequest.getPPAQ().setRequestedAction((int)ppaqSubAttr.getInteger());
			}
			ppaqSubAttr = serviceRequest.getAVP(toWiMaxVendorId(WimaxAttrConstants.PPAQ.getIntValue(), 
					PPAQ.RESOURCE_QUOTA.getIntValue()));
			if(ppaqSubAttr != null){
				wimaxRequest.getPPAQ().setResourceQuota((int)ppaqSubAttr.getInteger());
			}
			ppaqSubAttr = serviceRequest.getAVP(toWiMaxVendorId(WimaxAttrConstants.PPAQ.getIntValue(), 
					PPAQ.RESOURCE_THRESHOLD.getIntValue()));
			if(ppaqSubAttr != null){
				wimaxRequest.getPPAQ().setResourceThreshold((int)ppaqSubAttr.getInteger());
			}
			ppaqSubAttr = serviceRequest.getAVP(toWiMaxVendorId(WimaxAttrConstants.PPAQ.getIntValue(), 
					PPAQ.SERVICE_ID.getIntValue()));
			if(ppaqSubAttr != null){
				wimaxRequest.getPPAQ().setServiceID(ppaqSubAttr.getStringValue());
			}
			ppaqSubAttr = serviceRequest.getAVP(toWiMaxVendorId(WimaxAttrConstants.PPAQ.getIntValue(), 
					PPAQ.TERMINATION_ACTION.getIntValue()));
			if(ppaqSubAttr != null){
				wimaxRequest.getPPAQ().setTerminationAction((int)ppaqSubAttr.getInteger());
			}
			ppaqSubAttr = serviceRequest.getAVP(toWiMaxVendorId(WimaxAttrConstants.PPAQ.getIntValue(), 
					PPAQ.UPDATE_REASON.getIntValue()));
			if(ppaqSubAttr != null){
				wimaxRequest.getPPAQ().setUpdateReason((int)ppaqSubAttr.getInteger());
			}
			ppaqSubAttr = serviceRequest.getAVP(toWiMaxVendorId(WimaxAttrConstants.PPAQ.getIntValue(), 
					PPAQ.VOLUME_QUOTA.getIntValue()));
			if(ppaqSubAttr != null){
				wimaxRequest.getPPAQ().setVolumeQuota((int)ppaqSubAttr.getInteger());
			}
			ppaqSubAttr = serviceRequest.getAVP(toWiMaxVendorId(WimaxAttrConstants.PPAQ.getIntValue(), 
					PPAQ.VOLUME_THRESHOLD.getIntValue()));
			if(ppaqSubAttr != null){
				wimaxRequest.getPPAQ().setVolumeThreshold((int)ppaqSubAttr.getInteger());
			}
			
		}
		//setting the eap values
		IEapStateMachine eapSession = (IEapStateMachine) serviceRequest.getParameter("EAP_SESSION");
		if(eapSession != null){
			wimaxRequest.getEAP().setCurrentMethod(eapSession.getCurrentMethod());
			wimaxRequest.getEAP().setEapIdentity((String)serviceRequest.getParameter(EAP_IDENTITY));
			wimaxRequest.getEAP().setSessionID((String)serviceRequest.getParameter(EAP_SESSION_ID));
		}
		wimaxRequest.getEAP().setMSK((byte[])serviceRequest.getParameter(EAP_KEY_DATA));
		if(serviceRequest.getAVP(DiameterAVPConstants.EAP_PAYLOAD) != null){
			wimaxRequest.getEAP().setEapMessage(serviceRequest.getAVP(DiameterAVPConstants.EAP_PAYLOAD).getStringValue());
		}
		
		//ADDING THE CISCO SPECIFIC ATTRIBUTES
//		if(serviceRequest.getAVP(DiameterAVPConstants.CISCO_SSG_SERVICE_INFO)!=null){
			//wimaxRequest.getCisco().setCisco_ssg_service_info(serviceRequest.getRadiusAttributes(RadiusConstants.CISCO_VENDOR_ID,RadiusAttributeConstants.CISCO_SSG_SERVICE_INFO).iterator().next().getStringValue().trim().equalsIgnoreCase("N-prepaid-3g"))
//		}
		return wimaxRequest;
	
	}

	private String toWiMaxVendorId(Integer... keyId) {
		return DiameterConstants.WIMAX_VENDOR_ID + ":" + Strings.join("." + DiameterConstants.WIMAX_VENDOR_ID + ":", keyId);
	}

	@Override
	public WimaxResponse formWimaxResponse(T serviceRequest, V serviceResponse) {

		WimaxResponse wimaxResponse = new WimaxResponse();

		IDiameterAVP diameterAVP = serviceResponse.getAVP(toWiMaxVendorId(WimaxAttrConstants.DHCPV4_SERVER.getIntValue()));
		if (diameterAVP != null) {
			wimaxResponse.getDHCP().setDHCP_Server(diameterAVP.getStringValue());
		}
		
		diameterAVP = serviceResponse.getAVP(toWiMaxVendorId(WimaxAttrConstants.HA_IP_MIP4.getIntValue()));
		if (diameterAVP != null) {
			wimaxResponse.getHA().setHA_IP_MIP4(diameterAVP.getStringValue());
		}
		
		if(serviceRequest.getDiameterRequest().getPeerData() != null){
			PeerData peerData = serviceRequest.getDiameterRequest().getPeerData();
			wimaxResponse.getClientData().setDHCPAddress(peerData.getDHCPAddress());
			wimaxResponse.getClientData().setHAAddress(peerData.getHAAddress());
		}
		
		//setting the session timeout
		diameterAVP = serviceResponse.getAVP(DiameterAVPConstants.SESSION_TIMEOUT);
		if (diameterAVP != null) {
			wimaxResponse.setSessionTimeoutInSeconds(diameterAVP.getInteger());
		}
		return wimaxResponse;
	
	}
	private void addDiameterSuccessAvp(ApplicationResponse response) {
		IDiameterAVP resultCodeAvp = response.getAVP(DiameterAVPConstants.RESULT_CODE);
		if (resultCodeAvp == null){
			resultCodeAvp = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.RESULT_CODE);
			response.addAVP(resultCodeAvp);
		}
		resultCodeAvp.setInteger(com.elitecore.diameterapi.diameter.common.util.constant.ResultCode.DIAMETER_SUCCESS.code);
	}
	
	@Override
	public void createServiceResponse(T request, V response,
			WimaxRequest wimaxRequest, WimaxResponse wimaxResponse) {
		
		//setting the packet type of the service response
		if(wimaxResponse.getResultCode() == ResultCode.AUTH_SUCCESS){
			addDiameterSuccessAvp(response);
		}else if(wimaxResponse.getResultCode() == ResultCode.AUTH_REJECT){
			DiameterUtility.addOrReplaceAvp(DiameterAVPConstants.RESULT_CODE, response.getDiameterAnswer(), com.elitecore.diameterapi.diameter.common.util.constant.ResultCode.DIAMETER_AUTHORIZATION_REJECTED.code + "");
			addErrorMessageAvp(response, wimaxResponse);
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
			addMandatoryWimaxAttributes(wimaxRequest,wimaxResponse,request, response);
			addHAAttributes(request,response,wimaxRequest, wimaxResponse);
			return;
			
		}
		
		
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

	private void addErrorMessageAvp(V response, WimaxResponse wimaxResponse) {
		DiameterUtility.addOrReplaceAvp(DiameterAVPConstants.ERROR_MESSAGE, response.getDiameterAnswer(), wimaxResponse.getResponseMessage());
	}

	@Override
	public void handleRequest(T request, V response, ISession session) {
		if(request.getAVP(toWiMaxVendorId(WimaxAttrConstants.WIMAX_CAPABILITY.getIntValue())) !=null||
				request.getAVP(toWiMaxVendorId(WimaxAttrConstants.PPAC.getIntValue())) !=null||
				request.getAVP(toWiMaxVendorId(WimaxAttrConstants.PPAQ.getIntValue())) !=null||
				request.getAVP(toWiMaxVendorId(WimaxAttrConstants.PTS.getIntValue())) !=null||
				request.getAVP(toWiMaxVendorId(WimaxAttrConstants.DHCP_RK_KEY_ID.getIntValue())) !=null) {
			if(LogManager.getLogger().isLogLevel(LogLevel.TRACE)) {
				LogManager.getLogger().trace(MODULE, "Wimax Prepaid attribute (either WIMAX-CAP PPAC PPAQ PTS DHCPRKID) present in request, request eligible for wimax handling");
			}
		} else {
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
				LogManager.getLogger().trace(MODULE, "None of Wimax Prepaid attributes (WIMAX-CAP PPAC PPAQ PTS DHCPRKID) present in request, request not eligible for wimax handling");
			}
			return ;
		}
		
		IDiameterAVP resultCode = response.getAVP(DiameterAVPConstants.RESULT_CODE);
		if (resultCode == null) {
			LogManager.getLogger().debug(MODULE, "result code avp not found in response, Skipping wimax handling");
			return;
		}
		
		if (resultCode.getInteger() == com.elitecore.diameterapi.diameter.common.util.constant.ResultCode.DIAMETER_MULTI_ROUND_AUTH.code) {
			LogManager.getLogger().trace(MODULE, "Response Packet is ACCESS_CHALLENGE, Skipping wimax handling");
			return;
		}
		
		if(LogManager.getLogger().isLogLevel(LogLevel.TRACE)){
			LogManager.getLogger().trace(MODULE, "Handling WiMAX Request: " + request);
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
		IDiameterAVP resourceAVPair  = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.ELITE_RESOURCE_MANAGER_AVPAIR);
		if (resourceAVPair != null) {
			resourceAVPair.setStringValue("Wimax=true");
			request.addInfoAvp(resourceAVPair);	
		} else {
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
				LogManager.getLogger().debug(MODULE, "Elitecore-resource-manager(" 
						+ DiameterAVPConstants.ELITE_RESOURCE_MANAGER_AVPAIR + ") attribute not found in Diameter dictionary");
			}
		}
	}

	private void addCUIAndUserName(ApplicationResponse response,WimaxRequest wimaxRequest, WimaxResponse wimaxResponse){
//		String cui = wimaxResponse.getCUI();
//		if(cui != null){
//
//			IDiameterAVP cuiAttr = response.getAVP(DiameterAVPConstants.CUI);
//			if(cuiAttr ==null){
//				cuiAttr = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.CUI);
//				if(cuiAttr != null){
//					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
//						LogManager.getLogger().debug(MODULE, "CUI(0:89) attribute not found in HA response, adding with the value of CUI: " + cui);
//					}
//					cuiAttr.setStringValue(cui);
//					response.addAVP(cuiAttr);
//				}else{
//					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
//						LogManager.getLogger().debug(MODULE, "CUI(0:89) attribute not found in Diameter dictionary.");
//					}
//				}
//			}else {
//				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
//					LogManager.getLogger().debug(MODULE, "CUI(0:89) attribute already present in HA response, replacing with the value of CUI: " + cui);
//				}
//				cuiAttr.setStringValue(cui);
//			}
//			
//			IDiameterAVP userName = response.getAVP(DiameterAVPConstants.USER_NAME);
//			if(userName == null){
//				userName = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.USER_NAME);
//				if(userName != null){
//					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
//						LogManager.getLogger().debug(MODULE, "User-Name(0:1) attribute not found in HA response, adding with the value of CUI: " + cui);
//					}
//					userName.setStringValue(cui);
//					response.addAVP(userName);
//				}else{
//					if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
//						LogManager.getLogger().warn(MODULE, "User-Name(0:1) attribute not found in DiameterDictionary.");
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
	private void addMandatoryWimaxAttributes(WimaxRequest wimaxRequest, WimaxResponse wimaxResponse,ApplicationRequest request, ApplicationResponse response) {
		
		String ipTechValueStr = System.getProperty(WIMAX_IP_TECHNOLOGY);
		
		if (Strings.isNullOrBlank(ipTechValueStr) == false) {
			try {
				int ipTechValue = Integer.parseInt(ipTechValueStr);
				IDiameterAVP ipTechAttribute = DiameterDictionary.getInstance().getKnownAttribute(toWiMaxVendorId(WimaxAttrConstants.IP_TECHNOLOGY.getIntValue()));
				if (ipTechAttribute != null) {
					ipTechAttribute.setInteger(ipTechValue);
					response.addAVP(ipTechAttribute);
				} else {
					if (LogManager.getLogger().isDebugLogLevel()) {
						LogManager.getLogger().debug(MODULE, "Unable to add WIMAX_IP_TECHNOLOGY (24757:23) attribute, Reason: not found in dictionary");
					}
				}
			} catch(Exception e) {
				LogManager.getLogger().error(MODULE, "Invalid value: " + ipTechValueStr + " configured for IP-Technology(23) attribute");
			}
		}
		
		
		//this special check has been kept due to JIRA ELTIEAAA-1880, if the WiMAX processing is done remotely then
		//Our AAA will not add this attribute
		if(response.getAVP(toWiMaxVendorId(WimaxAttrConstants.AAA_SESSION_ID.getIntValue())) == null) {
			IDiameterAVP aaaSessionIdAttr = DiameterDictionary.getInstance()
					.getKnownAttribute(toWiMaxVendorId(WimaxAttrConstants.AAA_SESSION_ID.getIntValue()));
			if (aaaSessionIdAttr != null) {
				aaaSessionIdAttr.setStringValue(wimaxResponse.getAAA_Session_ID());
				response.addAVP(aaaSessionIdAttr);
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
		if(response.getAVP(toWiMaxVendorId(WimaxAttrConstants.WIMAX_CAPABILITY.getIntValue())) == null ) {
			
			AvpGrouped wimaxCapabilityAttr = (AvpGrouped) DiameterDictionary.getInstance()
					.getKnownAttribute(toWiMaxVendorId(WimaxAttrConstants.WIMAX_CAPABILITY.getIntValue()));
			if (wimaxCapabilityAttr != null) {
				
				IDiameterAVP acctCapAttr = DiameterDictionary.getInstance()
						.getKnownAttribute(toWiMaxVendorId(ACCOUNTING_CAPABILITIES));	
				if (acctCapAttr != null) {
					//accounting capabilities
					acctCapAttr.setInteger(wimaxResponse.getWimaxCapabilities().getAccountingCapabilities());
					wimaxCapabilityAttr.addSubAvp(acctCapAttr);
				} else {
					if (LogManager.getLogger().isDebugLogLevel()) {
						LogManager.getLogger().debug(MODULE, "Unable to add Accounting-Capabilities(24757:" 
								+ ACCOUNTING_CAPABILITIES + ") attribute, Reason: not found in dictionary");
					}
				}
				
				//hotlining capabilities
				if(wimaxResponse.getWimaxCapabilities().getHotliningCapabilities() != null){
					IDiameterAVP hotliningAttr = DiameterDictionary.getInstance()
							.getKnownAttribute(toWiMaxVendorId(HOTLINE_CAPABILITIES));
					if (hotliningAttr != null) {
						hotliningAttr.setInteger(wimaxResponse.getWimaxCapabilities().getHotliningCapabilities());
						wimaxCapabilityAttr.addSubAvp(hotliningAttr);
					} else {
						if (LogManager.getLogger().isDebugLogLevel()) {
							LogManager.getLogger().debug(MODULE, "Unable to add Hotlining-Capabilities(24757:" 
									+ HOTLINE_CAPABILITIES + ") attribute, Reason: not found in dictionary");
						}
					}
				}
				
				//Idle Mode Notification Capabilities
				IDiameterAVP idleModeNotificationCapabilityAttr = DiameterDictionary.getInstance()
						.getKnownAttribute(toWiMaxVendorId(IDLE_MODE_NOTIFICATION_CAPABILITIES));
				if (idleModeNotificationCapabilityAttr != null) {
					idleModeNotificationCapabilityAttr.setInteger(wimaxResponse.getWimaxCapabilities().getIdleModeNotificationCapabilities());
					wimaxCapabilityAttr.addSubAvp(idleModeNotificationCapabilityAttr);
				} else {
					if (LogManager.getLogger().isDebugLogLevel()) {
						LogManager.getLogger().debug(MODULE, "Unable to add Idle-Mode-Notification-capabilities(24757:" 
								+ IDLE_MODE_NOTIFICATION_CAPABILITIES + ") attribute, Reason: not found in dictionary");
					}
				}
				
				//adding the wimax capability attribute to the response
				response.addAVP(wimaxCapabilityAttr);
			} else {
				if (LogManager.getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "Unable to add Wimax-capability-attribute(24757:" 
							+ WimaxAttrConstants.WIMAX_CAPABILITY.getIntValue() + ") attribute, Reason: not found in dictionary");
				}
			}
			
		}else{
			
			AvpGrouped wimaxCapAttr = (AvpGrouped)((ApplicationResponse)response)
					.getAVP(toWiMaxVendorId(WimaxAttrConstants.WIMAX_CAPABILITY.getIntValue()));
			
			if(wimaxCapAttr.getSubAttribute(toWiMaxVendorId(ACCOUNTING_CAPABILITIES)) == null){
				if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
					LogManager.getLogger().info(MODULE, "Wimax-Capability attribute is present in response but does not contain Accounting Capabilities, using value from configuration");
				IDiameterAVP acctCapAttr = DiameterDictionary.getInstance()
						.getKnownAttribute(toWiMaxVendorId(ACCOUNTING_CAPABILITIES));
				if (acctCapAttr != null) {
					//getting the value from the wimaxResponse where the value has been set from the wimax configuration
					acctCapAttr.setInteger(wimaxResponse.getWimaxCapabilities().getAccountingCapabilities());
					wimaxCapAttr.addSubAvp(acctCapAttr);
				} else {
					if (LogManager.getLogger().isDebugLogLevel()) {
						LogManager.getLogger().debug(MODULE, "Unable to add Accounting-Capabilities(24757:" 
								+ ACCOUNTING_CAPABILITIES + ") attribute, Reason: not found in dictionary");
					}
				}
			}
			if(wimaxCapAttr.getSubAttribute(toWiMaxVendorId(HOTLINE_CAPABILITIES)) == null ){
				if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
					LogManager.getLogger().info(MODULE, "Wimax-Capability attribute is present in response but does not contain Hotlining Capabilities, using value from request packet");
				IDiameterAVP resHotlineAttr = DiameterDictionary.getInstance()
						.getKnownAttribute(toWiMaxVendorId(HOTLINE_CAPABILITIES));
				if (resHotlineAttr != null) {
					IDiameterAVP reqWimaxhotlineAttr = ((ApplicationRequest)request)
							.getAVP(toWiMaxVendorId(HOTLINE_CAPABILITIES));
					
					if(reqWimaxhotlineAttr != null){
						//this means that the request contains hotline capabilities and copy that same value
						resHotlineAttr.setInteger(reqWimaxhotlineAttr.getInteger());	
					}else{
						if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
							LogManager.getLogger().info(MODULE, "Wimax-Capability attribute is present in request but does not contain Hotlining Capabilities");
						if(wimaxResponse.getWimaxCapabilities().getHotliningCapabilities() != null)
							resHotlineAttr.setInteger(wimaxResponse.getWimaxCapabilities().getHotliningCapabilities());
					}
					wimaxCapAttr.addSubAvp(resHotlineAttr);
				} else {
					if (LogManager.getLogger().isDebugLogLevel()) {
						LogManager.getLogger().debug(MODULE, "Unable to add Hotlining-Capabilities(24757:" 
								+ HOTLINE_CAPABILITIES + ") attribute, Reason: not found in dictionary");
					}
				}
			}
			if(wimaxCapAttr.getSubAttribute(toWiMaxVendorId(IDLE_MODE_NOTIFICATION_CAPABILITIES)) == null ){
				if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
					LogManager.getLogger().info(MODULE, "Wimax-Capability attribute is present in response but does not contain Idle Mode Notification Capabilities, using value from request packet");
				IDiameterAVP resIdleModeNotificationAttr = DiameterDictionary.getInstance()
						.getKnownAttribute(toWiMaxVendorId(IDLE_MODE_NOTIFICATION_CAPABILITIES));
				if (resIdleModeNotificationAttr != null) {
					IDiameterAVP reqIdleModeNotificationAttr = ((ApplicationRequest)request)
							.getAVP(toWiMaxVendorId(WimaxAttrConstants.WIMAX_CAPABILITY.getIntValue(), IDLE_MODE_NOTIFICATION_CAPABILITIES));
					if(reqIdleModeNotificationAttr != null){
						//this means that the request contains hotline capabilities and copy that same value
						resIdleModeNotificationAttr.setInteger(reqIdleModeNotificationAttr.getInteger());	
					}else{
						resIdleModeNotificationAttr.setInteger(wimaxResponse.getWimaxCapabilities().getIdleModeNotificationCapabilities());
					}
					wimaxCapAttr.addSubAvp(resIdleModeNotificationAttr);
				} else {
					if (LogManager.getLogger().isDebugLogLevel()) {
						LogManager.getLogger().debug(MODULE, "Unable to add Idle-Mode-Notification-Capabilities(24757:" 
								+ IDLE_MODE_NOTIFICATION_CAPABILITIES + ") attribute, Reason: not found in dictionary");
					}
				}
			}
			
		}
		
		//DNS list and the check that was done in the older during adding in the service response
		if(response.getAVP(toWiMaxVendorId(WimaxAttrConstants.DNS.getIntValue())) == null && 
				request.getAVP(toWiMaxVendorId(WimaxAttrConstants.MN_HA_MIP4_SPI.getIntValue())) == null) {
			List<byte[]> dnsList = wimaxResponse.getDNSList();
			if(dnsList == null){
				LogManager.getLogger().info(MODULE, "DNS List is not configured");
				return;
			}
			for(int i = 0; i < dnsList.size(); i++){
				IDiameterAVP dnsAttrib = DiameterDictionary.getInstance()
						.getKnownAttribute(toWiMaxVendorId(WimaxAttrConstants.DNS.getIntValue()));
				if (dnsAttrib != null) {
					dnsAttrib.setValueBytes(dnsList.get(i));
					response.addAVP(dnsAttrib);
				} else {
					if (LogManager.getLogger().isDebugLogLevel()) {
						LogManager.getLogger().debug(MODULE, "Unable to add DNS-Attribute(24757:" 
								+ WimaxAttrConstants.DNS.getIntValue() + ") attribute, Reason: not found in dictionary");
					}
				}
			}
		}
	}

	private void addAuthOnlyAndEapRequestAttributes(ApplicationRequest request,ApplicationResponse response, WimaxResponse wimaxResponse) {
//		int salt = 32768;
		//setting the DHCP_Server_address
		if(wimaxResponse.getDHCP().getDHCP_Server() != null && !CommonConstants.RESERVED_IPV_4_ADDRESS.equals(wimaxResponse.getDHCP().getDHCP_Server())){
			IDiameterAVP dhcp_ip_addr_attr = DiameterDictionary.getInstance()
					.getKnownAttribute(toWiMaxVendorId(WimaxAttrConstants.DHCPV4_SERVER.getIntValue()));
			if (dhcp_ip_addr_attr != null) {
				dhcp_ip_addr_attr.setStringValue(wimaxResponse.getDHCP().getDHCP_Server());
				response.addAVP(dhcp_ip_addr_attr);	
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
		
		IDiameterAVP diameterAttribute = null;

		//			 HA_IP_MIP4
		//added the check for the reserved IP address for HA IP MIP4
		if (CommonConstants.RESERVED_IPV_4_ADDRESS.equals(wimaxResponse.getHA().getHA_IP_MIP4()) == false) {
			IDiameterAVP ha_ip_mip4_attr = DiameterDictionary.getInstance().getKnownAttribute(toWiMaxVendorId(WimaxAttrConstants.HA_IP_MIP4.getIntValue()));
			if (ha_ip_mip4_attr != null) {
				ha_ip_mip4_attr.setStringValue(wimaxResponse.getHA().getHA_IP_MIP4());
				response.addAVP(ha_ip_mip4_attr);
			} else {
				if (LogManager.getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "Unable to add HA-IP-MIP4(24757:" 
							+ WimaxAttrConstants.HA_IP_MIP4.getIntValue() + ") attribute, Reason: not found in dictionary");
				}
			}
		} else {
			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "HA IP address is " + wimaxResponse.getHA().getHA_IP_MIP4() + ", HA_IP_MIP4 (24757:6) will not be added in response");
			}
		}

		//		 HA_RK_KEY
		diameterAttribute = DiameterDictionary.getInstance().getKnownAttribute(toWiMaxVendorId(WimaxAttrConstants.HA_RK_KEY.getIntValue()));
		if (diameterAttribute != null) {
			diameterAttribute.setValueBytes(wimaxResponse.getHA().getHA_RK_KEY());
			response.addAVP(diameterAttribute);							
		} else {
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Unable to add HA-RK-KEY(24757:" 
						+ WimaxAttrConstants.HA_RK_KEY.getIntValue() + ") attribute, Reason: not found in dictionary");
			}
		}

		//		 HA_RK_SPI
		diameterAttribute = DiameterDictionary.getInstance().getKnownAttribute(toWiMaxVendorId(WimaxAttrConstants.HA_RK_SPI.getIntValue()));
		if (diameterAttribute != null) {
			diameterAttribute.setInteger(wimaxResponse.getHA().getHA_RK_SPI());
			response.addAVP(diameterAttribute);							
		} else {
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Unable to add HA-RK-SPI(24757:" 
						+ WimaxAttrConstants.HA_RK_SPI.getIntValue() + ") attribute, Reason: not found in dictionary");
			}
		}

		//		 HA_RK_Lifetime
		diameterAttribute = DiameterDictionary.getInstance().getKnownAttribute(toWiMaxVendorId(WimaxAttrConstants.HA_RK_LIFETIME.getIntValue()));
		if (diameterAttribute != null) {
			diameterAttribute.setInteger(wimaxResponse.getHA().getHA_RK_LIFETIME());
			response.addAVP(diameterAttribute);	
		} else {
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Unable to add HA_RK_LIFETIME(24757:" 
						+ WimaxAttrConstants.HA_RK_LIFETIME.getIntValue() + ") attribute, Reason: not found in dictionary");
			}
		}
		
		//adding the wimax keys
//		IDiameterAVP mskAttr = DiameterDictionary.getInstance().getAttribute(toWiMaxVendorId(464));
//		mskAttr.setValueBytes(wimaxResponse.getMSK());
//		response.addAVP(mskAttr);
		
		
		if(wimaxResponse.getDHCP().getDHCP_Server() != null){			
			//setting the DHCP_RK
			diameterAttribute = DiameterDictionary.getInstance()
					.getKnownAttribute(toWiMaxVendorId(WimaxAttrConstants.DHCP_RK.getIntValue()));
			if (diameterAttribute != null) {
				diameterAttribute.setValueBytes(wimaxResponse.getDHCP().getDHCP_RK());
				response.addAVP(diameterAttribute);							
			} else {
				if (LogManager.getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "Unable to add DHCP_RK(24757:" 
							+ WimaxAttrConstants.DHCP_RK.getIntValue() + ") attribute, Reason: not found in dictionary");
				}
			}

			// DHCP_RK_ID
			diameterAttribute = DiameterDictionary.getInstance()
					.getKnownAttribute(toWiMaxVendorId(WimaxAttrConstants.DHCP_RK_KEY_ID.getIntValue()));
			if (diameterAttribute != null) {
				diameterAttribute.setInteger(wimaxResponse.getDHCP().getDHCP_RK_KEY_ID());
				response.addAVP(diameterAttribute);							
			} else {
				if (LogManager.getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "Unable to add DHCP_RK_KEY_ID(24757:" 
							+ WimaxAttrConstants.DHCP_RK_KEY_ID.getIntValue() + ") attribute, Reason: not found in dictionary");
				}
			}

			// DHCP_RK_Lifetime
			diameterAttribute = DiameterDictionary.getInstance()
					.getKnownAttribute(toWiMaxVendorId(WimaxAttrConstants.DHCP_RK_LIFETIME.getIntValue()));
			if (diameterAttribute != null) {
				diameterAttribute.setInteger(wimaxResponse.getDHCP().getDHCP_RK_LIFETIME());
				response.addAVP(diameterAttribute);	
			} else {
				if (LogManager.getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "Unable to add DHCP_RK_LIFETIME(24757:" 
							+ WimaxAttrConstants.DHCP_RK_LIFETIME.getIntValue() + ") attribute, Reason: not found in dictionary");
				}
			}
		}

		
		IDiameterAVP sessionTimeout = ((ApplicationResponse)response).getAVP(DiameterAVPConstants.SESSION_TIMEOUT_STR);
		if(sessionTimeout == null){
			sessionTimeout = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.SESSION_TIMEOUT);
			if (sessionTimeout != null) {
				response.addAVP(sessionTimeout);
			} else {
				if (LogManager.getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "Unable to add Session-Timeout(" 
							+ DiameterAVPConstants.SESSION_TIMEOUT + ") attribute, Reason: not found in dictionary");
				}
			}
		}
		sessionTimeout.setInteger(wimaxResponse.getSessionTimeoutInSeconds());
		

		//			 MN_HA_MIP4_KEY
		diameterAttribute = DiameterDictionary.getInstance()
				.getKnownAttribute(toWiMaxVendorId(WimaxAttrConstants.MN_HA_MIP4_KEY.getIntValue()));
		if (diameterAttribute != null) {
			diameterAttribute.setValueBytes(wimaxResponse.getHA().getMN_HA_MIP4_KEY());
			response.addAVP(diameterAttribute);
		} else {
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Unable to add MN-HA-MIP4-KEY(24757:" 
						+ WimaxAttrConstants.MN_HA_MIP4_KEY.getIntValue() + ") attribute, Reason: not found in dictionary");
			}
		}

		//MN_HA_MIP4_SPI
		if(response.getAVP(toWiMaxVendorId(WimaxAttrConstants.MN_HA_MIP4_SPI.getIntValue())) == null) {
			diameterAttribute = DiameterDictionary.getInstance()
					.getKnownAttribute(toWiMaxVendorId(WimaxAttrConstants.MN_HA_MIP4_SPI.getIntValue()));
			if (diameterAttribute != null) {
				diameterAttribute.setInteger(wimaxResponse.getHA().getMN_HA_MIP4_SPI());
				response.addAVP(diameterAttribute);
			} else {
				if (LogManager.getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "Unable to add MN-HA-MIP4-SPI(24757:" 
							+ WimaxAttrConstants.MN_HA_MIP4_SPI.getIntValue() + ") attribute, Reason: not found in dictionary");
				}
			}
		}else{
			// not sure about this test
			diameterAttribute = response.getAVP(toWiMaxVendorId(WimaxAttrConstants.MN_HA_MIP4_SPI.getIntValue()));
			diameterAttribute.setInteger(wimaxResponse.getHA().getMN_HA_MIP4_SPI());
		}
		//FA_RK_KEY
		diameterAttribute = DiameterDictionary.getInstance()
				.getKnownAttribute(toWiMaxVendorId(WimaxAttrConstants.FA_RK_KEY.getIntValue()));
		if (diameterAttribute != null) {
			diameterAttribute.setValueBytes(wimaxResponse.getFA().getFA_RK_KEY());
			response.addAVP(diameterAttribute);						
		} else {
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Unable to add FA-RK-KEY(24757:" 
						+ WimaxAttrConstants.FA_RK_KEY.getIntValue() + ") attribute, Reason: not found in dictionary");
			}
		}
		
		//		 FA_RK_SPI (value equals MN_HA_CMIP4_SPI)
		diameterAttribute = DiameterDictionary.getInstance()
				.getKnownAttribute(toWiMaxVendorId(WimaxAttrConstants.FA_RK_SPI.getIntValue()));
		if (diameterAttribute != null) {
			diameterAttribute.setInteger(wimaxResponse.getFA().getFA_RK_SPI() & 0xFFFFFFFF);
			response.addAVP(diameterAttribute);
		} else {
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Unable to add FA_RK_SPI(24757:" 
						+ WimaxAttrConstants.FA_RK_SPI.getIntValue() + ") attribute, Reason: not found in dictionary");
			}
		}

		
	}

	private void addHAAttributes(ApplicationRequest request,ApplicationResponse response, WimaxRequest wimaxRequest, WimaxResponse wimaxResponse) {
		//add HA_RK_KEY
//		int salt = 32768;
		if(wimaxRequest.getHA().getHA_RK_SPI() != null){
			IDiameterAVP diaAttribute = DiameterDictionary.getInstance()
					.getKnownAttribute(toWiMaxVendorId(WimaxAttrConstants.HA_RK_KEY.getIntValue()));
			if (diaAttribute != null) {
				diaAttribute.setValueBytes(wimaxResponse.getHA().getHA_RK_KEY());
				response.addAVP(diaAttribute);
			} else {
				if (LogManager.getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "Unable to add HA-RK-KEY(24757:" 
							+ WimaxAttrConstants.HA_RK_KEY.getIntValue() + ") attribute, Reason: not found in dictionary");
				}
			}

			// add HA_RK_SPI
			diaAttribute = DiameterDictionary.getInstance()
					.getKnownAttribute(toWiMaxVendorId(WimaxAttrConstants.HA_RK_SPI.getIntValue()));
			if (diaAttribute != null) {
				diaAttribute.setInteger(wimaxResponse.getHA().getHA_RK_SPI());
				response.addAVP(diaAttribute);
			} else {
				if (LogManager.getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "Unable to add HA-RK-SPI(24757:" 
							+ WimaxAttrConstants.HA_RK_SPI.getIntValue() + ") attribute, Reason: not found in dictionary");
				}
			}

			// add HA_RK_LIFETIME
			diaAttribute = DiameterDictionary.getInstance()
					.getKnownAttribute(toWiMaxVendorId(WimaxAttrConstants.HA_RK_LIFETIME.getIntValue()));
			if (diaAttribute != null) {
				diaAttribute.setInteger(wimaxResponse.getHA().getHA_RK_LIFETIME());
				response.addAVP(diaAttribute);
			} else {
				if (LogManager.getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "Unable to add HA_RK_LIFETIME(24757:" 
							+ WimaxAttrConstants.HA_RK_LIFETIME.getIntValue() + ") attribute, Reason: not found in dictionary");
				}
			}
		}
		
		//	add	MN_HA_MIP4_KEY
		IDiameterAVP diaAttribute = DiameterDictionary.getInstance()
				.getKnownAttribute(toWiMaxVendorId(WimaxAttrConstants.MN_HA_MIP4_KEY.getIntValue()));
		if (diaAttribute != null) {
			diaAttribute.setValueBytes(wimaxResponse.getHA().getMN_HA_MIP4_KEY());
			response.addAVP(diaAttribute);
		} else {
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Unable to add MN-HA-MIP4-KEY(24757:" 
						+ WimaxAttrConstants.MN_HA_MIP4_KEY.getIntValue() + ") attribute, Reason: not found in dictionary");
			}
		}
		
//		add	MN_HA_MIP4_SPI
		diaAttribute = DiameterDictionary.getInstance()
				.getKnownAttribute(toWiMaxVendorId(WimaxAttrConstants.MN_HA_MIP4_SPI.getIntValue()));
		if (diaAttribute != null) {
			diaAttribute.setInteger(wimaxResponse.getHA().getMN_HA_MIP4_SPI());
			response.addAVP(diaAttribute);							
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
//			IDiameterAVP cuiAttr = response.getAVP(DiameterAVPConstants.CUI);
//			if(cuiAttr ==null){
//				cuiAttr = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.CUI);
//				if(cuiAttr!=null){
//					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
//						LogManager.getLogger().debug(MODULE, "CUI(0:89) attribute not present in HA Response, adding the attribute with value of CUI: " + cui);
//	}
//					cuiAttr.setStringValue(cui);
//					response.addAVP(cuiAttr);
//				}else{
//					if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
//						LogManager.getLogger().warn(MODULE, "CUI(0:89) attribute not found in DiameterDictionary.");
//					}
//				}
//			}else {
//				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
//					LogManager.getLogger().debug(MODULE, "CUI(0:89) attribute already present in HA Response. Replacing the value with value of CUI : " + cui);
//				}
//				cuiAttr.setStringValue(cui);
//			}
//
//			IDiameterAVP userName = response.getAVP(DiameterAVPConstants.USER_NAME);
//			if(userName ==null){
//				userName = DiameterDictionary.getInstance().getKnownAttribute(DiameterAVPConstants.USER_NAME);
//				if(userName!=null){
//					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
//						LogManager.getLogger().debug(MODULE, "User-Name(0:1) attribute not present in HA Response, adding the attribute with value of CUI: " + cui);
//					}
//					userName.setStringValue(cui);
//					response.addAVP(userName);
//				}else{
//					if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
//						LogManager.getLogger().warn(MODULE, "User-Name(0:1) attribute not found in DiameterDictionary.");
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

	private void addDHCPAttributes(ApplicationRequest request, ApplicationResponse response, WimaxResponse wimaxResponse) {
		   // DHCP_RK
		IDiameterAVP diaAttribute = DiameterDictionary.getInstance()
				.getKnownAttribute(toWiMaxVendorId(WimaxAttrConstants.DHCP_RK.getIntValue()));
		if (diaAttribute != null) {
			diaAttribute.setValueBytes(wimaxResponse.getDHCP().getDHCP_RK());
			response.addAVP(diaAttribute);
		} else {
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Unable to add DHCP_RK(24757:" 
						+ WimaxAttrConstants.DHCP_RK.getIntValue() + ") attribute, Reason: not found in dictionary");
			}
		}
		
		//		 DHCP_RK_ID
		IDiameterAVP dhcpRkId = DiameterDictionary.getInstance()
				.getKnownAttribute(toWiMaxVendorId(WimaxAttrConstants.DHCP_RK_KEY_ID.getIntValue()));
		if (dhcpRkId != null) {
			dhcpRkId.setInteger(wimaxResponse.getDHCP().getDHCP_RK_KEY_ID());
			response.addAVP(dhcpRkId);
		} else {
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Unable to add DHCP_RK_KEY_ID(24757:"
						+ WimaxAttrConstants.DHCP_RK_KEY_ID.getIntValue() + ") attribute, Reason: not found in dictionary");
			}
		}
		
		//DHCP_RK_LIFETIME
		IDiameterAVP dhcpRkLifeTime = DiameterDictionary.getInstance()
				.getKnownAttribute(toWiMaxVendorId(WimaxAttrConstants.DHCP_RK_LIFETIME.getIntValue()));
		if (dhcpRkLifeTime != null) {
			dhcpRkLifeTime.setInteger(wimaxResponse.getDHCP().getDHCP_RK_LIFETIME());
			response.addAVP(dhcpRkLifeTime);
		} else {
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Unable to add DHCP_RK_LIFETIME(24757:" 
						+ WimaxAttrConstants.DHCP_RK_LIFETIME.getIntValue() + ") attribute, Reason: not found in dictionary");
			}
		}
	}

	@Override
	public void reInit() throws InitializationFailedException {
		
	}

	@Override
	public boolean isEligible(T request, V response) {
		return true;
	}

	@Override
	public boolean isResponseBehaviorApplicable() {
		return false;
	}
}
