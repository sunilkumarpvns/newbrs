package com.elitecore.aaa.core.wimax;

import java.util.List;
import java.util.Random;

import com.elitecore.aaa.core.BaseVendorSpecificHandler;
import com.elitecore.aaa.core.authprotocol.exception.AuthorizationFailedException;
import com.elitecore.aaa.core.conf.SPIKeyConfiguration;
import com.elitecore.aaa.core.conf.WimaxConfiguration;
import com.elitecore.aaa.core.eap.session.EAPSessionManager;
import com.elitecore.aaa.core.wimax.keys.KeyManager;
import com.elitecore.aaa.radius.service.auth.constant.AuthReplyMessageConstant;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.servicex.ServiceContext;
import com.elitecore.core.servicex.ServiceRequest;
import com.elitecore.core.servicex.ServiceResponse;
import com.elitecore.coreeap.util.constants.EapTypeConstants;
import com.elitecore.coreradius.commons.util.constants.WimaxAttrConstants.ResultCode;

public abstract class BaseWimaxHandler<T extends ServiceRequest, V extends ServiceResponse> extends BaseVendorSpecificHandler {

	private static final String MODULE = "BASE-WIMAX-HANDLER";
	private DHCPRequestHandler dhcpRequestHandler;
	private HARequestHandler haRequestHandler;
	private AuthenticateOnlyRequestHandler authenticateOnlyRequestHandler;
	private AuthorizeOnlyRequestHandler authorizeOnlyRequestHandler;
	private AuthWimaxRequestHandler authWimaxRequestHandler;
	private WimaxSessionManager wimaxSession;
	private EAPSessionManager eapSessionManager;
	private WimaxConfiguration wimaxConfiguration;

	public BaseWimaxHandler(ServiceContext serviceContext, WimaxConfiguration wimaxConfiguration, SPIKeyConfiguration spiKeyConfiguration, 
			WimaxSessionManager wimaxSessionManager, EAPSessionManager eapSessionManager, KeyManager keyManager) {
		super(serviceContext);
		this.wimaxConfiguration = wimaxConfiguration;
		this.wimaxSession = wimaxSessionManager;
		this.eapSessionManager = eapSessionManager;
		dhcpRequestHandler = new DHCPRequestHandler(keyManager);
		haRequestHandler = new HARequestHandler();
		authenticateOnlyRequestHandler = new AuthenticateOnlyRequestHandler(keyManager, 
				spiKeyConfiguration);
		authorizeOnlyRequestHandler = new AuthorizeOnlyRequestHandler();
		authWimaxRequestHandler = new AuthWimaxRequestHandler(eapSessionManager,
				keyManager, spiKeyConfiguration);
	}

	public abstract WimaxRequest formWimaxRequest(T request);
	public abstract WimaxResponse formWimaxResponse(T request, V response);
	public abstract void createServiceResponse(T request, V response, WimaxRequest wimaxRequest, WimaxResponse wimaxResponse);

	public void handleWimaxRequest(WimaxRequest wimaxRequest, WimaxResponse wimaxResponse) throws AuthorizationFailedException{

		WimaxSessionData wimaxSessionData = null;					

		// check if the service is from DHCP Server.
		if(dhcpRequestHandler.isEligible(wimaxRequest, wimaxResponse)){
			dhcpRequestHandler.handleRequest(wimaxRequest, wimaxResponse,null);			
			return;
		}
		// if not dhcp request ....

		String userName = "";
		if(wimaxRequest.getUserName() == null){
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
				LogManager.getLogger().info(MODULE, "No user-name attribute present in the request packet.");
			}
		}else{
			userName = wimaxRequest.getUserName();
		}

		String strCallingStationId = null;
		strCallingStationId = wimaxRequest.getSourceInfo().getCalling_Station_ID();
		if(strCallingStationId != null){
			strCallingStationId = strCallingStationId.trim();
		}

		if(haRequestHandler.isEligible(wimaxRequest, wimaxResponse)){

			if(strCallingStationId != null && strCallingStationId.length() > 0){				
				wimaxSessionData = wimaxSession.getWimaxSession(WimaxSessionManager.WIMAX_CALLING_STATION_ID,strCallingStationId);
			}else{
				wimaxSessionData = wimaxSession.getWimaxSession(WimaxSessionManager.WIMAX_USERNAME,userName);
			}
			
			if(wimaxSessionData == null){
				/// build failure packet related code was there ............
				LogManager.getLogger().debug(MODULE, "WiMAX session mandatory to process HA request cannot be located. Sending Access Reject");
				wimaxResponse.setFurtherProcessingRequired(false);
				wimaxResponse.setResponseMessage(AuthReplyMessageConstant.WIMAX_SESSION_NOT_FOUND_FOR_HA);
				wimaxResponse.setResultCode(ResultCode.AUTH_REJECT);
				return;
			}
			haRequestHandler.handleRequest(wimaxRequest, wimaxResponse,wimaxSessionData);	
			addMandatoryWimaxAttributes(wimaxSessionData, wimaxRequest, wimaxResponse);			
			wimaxResponse.setFurtherProcessingRequired(true);
			wimaxResponse.setResponseMessage(AuthReplyMessageConstant.AUTHENTICATION_SUCCESS);
			wimaxResponse.setResultCode(ResultCode.AUTH_SUCCESS);
			return;			
		}		

		//   TODO  store the default session time-out configured in auth-service in some variable.		
		String str_ha_ip_address = null;

		if(wimaxResponse.getHA().getHA_IP_MIP4() == null){
			str_ha_ip_address = wimaxResponse.getClientData().getHAAddress();
			if(str_ha_ip_address == null){
				if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
					LogManager.getLogger().warn(MODULE, "HA IP address not configured for client " + wimaxRequest.getSourceInfo().getSourceIP()+ " using 0.0.0.0 as default value.");					
				str_ha_ip_address = new String("0.0.0.0");
			}
		} else{			
			str_ha_ip_address = wimaxResponse.getHA().getHA_IP_MIP4();
		}
		//for dhcp request ....

		String str_dhcp_IPAddress = null;	
		if (wimaxResponse.getDHCP().getDHCP_Server() == null) {
			str_dhcp_IPAddress = wimaxResponse.getClientData().getDHCPAddress();
			if (str_dhcp_IPAddress == null) {
				if (LogManager.getLogger().isWarnLogLevel()) {
					LogManager.getLogger().warn(MODULE, "DHCP IP address not configured for client " + wimaxRequest.getSourceInfo().getSourceIP());				
				}
			}
		} else {			
			str_dhcp_IPAddress = wimaxResponse.getDHCP().getDHCP_Server();
		}

		if (authorizeOnlyRequestHandler.isEligible(wimaxRequest, wimaxResponse)) {		

			wimaxSessionData = wimaxSession.getWimaxSession(WimaxSessionManager.WIMAX_CALLING_STATION_ID, strCallingStationId);

			if (wimaxSessionData != null) {
				((WimaxSessionDataImpl)wimaxSessionData).setCallingStationId(strCallingStationId);
				((WimaxSessionDataImpl)wimaxSessionData).setUsername(userName);
				updateWimaxSessionWithReqInfo(wimaxRequest, wimaxSessionData);
				wimaxSession.updateWimaxSessionData(wimaxSessionData);				
			} else {
				wimaxSessionData = wimaxSession.getWimaxSession(WimaxSessionManager.WIMAX_USERNAME, userName);
			}
			authorizeOnlyRequestHandler.handleRequest(wimaxRequest, wimaxResponse, wimaxSessionData);
			addMandatoryWimaxAttributes(wimaxSessionData, wimaxRequest, wimaxResponse);

			//TODO: Here considering Update reason > 3 for session removal. When support for Tariff Switching will be given, update reason = 5 will have to be excluded from the following condition.
			//// quota reached wimax session removed.
			if(wimaxSessionData!= null && wimaxRequest.getPPAQ().getUpdateReason() != null && wimaxRequest.getPPAQ().getUpdateReason() != 5 && wimaxRequest.getPPAQ().getUpdateReason() > 3){			
				LogManager.getLogger().warn(MODULE, "Wimax Session removed for session id: " + wimaxSessionData.getCallingStationId());
				wimaxSession.removeSession(wimaxSessionData);
			}

			wimaxResponse.setFurtherProcessingRequired(true);
			wimaxResponse.setResponseMessage(AuthReplyMessageConstant.AUTHENTICATION_SUCCESS);
			wimaxResponse.setResultCode(ResultCode.AUTH_SUCCESS);

		}  
		// if it is not authorize only request ....
		else{
			if(strCallingStationId == null || strCallingStationId.length() == 0){
				///  build failure packet related code was there .......
				wimaxResponse.setFurtherProcessingRequired(false);
				wimaxResponse.setResponseMessage("Calling station id not found, mandatory for WiMAX authentication.");
				wimaxResponse.setResultCode(ResultCode.AUTH_REJECT);
				
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
					LogManager.getLogger().debug(MODULE, "Calling station id not found in request for user: " + userName+ 
							", which is mandatory for WiMAX authentication");
				}
				return;
			}

			if(wimaxRequest.getEAP().getEapMessage()!= null && eapSessionManager.isSessionExist(wimaxRequest.getEAP().getSessionID())){
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "EAP Session found for session id: " + wimaxRequest.getEAP().getSessionID());

				//receiving the cloned session
				wimaxSessionData = wimaxSession.getWimaxSession(WimaxSessionManager.WIMAX_CALLING_STATION_ID,strCallingStationId);

				//removing the old session from memory
				if(wimaxSessionData != null){
					wimaxSession.removeSession(wimaxSessionData);
				}
				
				if(wimaxSessionData == null){
					wimaxSessionData = wimaxSession.createWimaxSession(WimaxSessionManager.WIMAX_CALLING_STATION_ID,strCallingStationId);
				}
				updateWimaxSessionWithReqInfo(wimaxRequest,wimaxSessionData);			

				((WimaxSessionDataImpl)wimaxSessionData).setCUI(wimaxRequest.getCUI());
				((WimaxSessionDataImpl)wimaxSessionData).setUsername(userName);

				if ((wimaxRequest.getEAP().getCurrentMethod() == EapTypeConstants.TLS.typeId 
						||  wimaxRequest.getEAP().getCurrentMethod() == EapTypeConstants.TTLS.typeId) == false) {
					if (LogManager.getLogger().isDebugLogLevel()) {
						LogManager.getLogger().debug(MODULE, "The User is not authenticated using EAP-TTLS or " +
								"EAP-TLS methods so only mandatory WiMAX attributes are added.");
					}
					addMandatoryWimaxAttributes(wimaxSessionData, wimaxRequest, wimaxResponse);
				}else{		
					((WimaxSessionDataImpl)wimaxSessionData).setHA_IP_MIP4(str_ha_ip_address);					
					((WimaxSessionDataImpl)wimaxSessionData).setDHCPV4_SERVER(str_dhcp_IPAddress);

					if(authenticateOnlyRequestHandler.isEligible(wimaxRequest, wimaxResponse)){					
						authenticateOnlyRequestHandler.handleRequest(wimaxRequest, wimaxResponse, wimaxSessionData);
						addMandatoryWimaxAttributes(wimaxSessionData, wimaxRequest, wimaxResponse);
					}else {
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "Service Type : other");
						String str_aaa_sessionid = generateAAASessionID(wimaxRequest);
						((WimaxSessionDataImpl)wimaxSessionData).setAaaSessionId(str_aaa_sessionid);
						wimaxSession.updateWimaxSessionData(wimaxSessionData);
						authWimaxRequestHandler.handleRequest(wimaxRequest, wimaxResponse, wimaxSessionData);
						addMandatoryWimaxAttributes(wimaxSessionData, wimaxRequest, wimaxResponse);
					}
				}
				wimaxSession.updateWimaxSessionData(wimaxSessionData);
			}else {
				//for the case when the request received is not for EAP and where only mandatory wimax attributes are to be added
				wimaxSessionData = wimaxSession.getWimaxSession(WimaxSessionManager.WIMAX_CALLING_STATION_ID,strCallingStationId);
				if(wimaxSessionData != null){
					wimaxSession.removeSession(wimaxSessionData);
				}
				
				if(wimaxSessionData == null){
					wimaxSessionData = wimaxSession.createWimaxSession(WimaxSessionManager.WIMAX_CALLING_STATION_ID,strCallingStationId);
				}
				updateWimaxSessionWithReqInfo(wimaxRequest,wimaxSessionData);			

				((WimaxSessionDataImpl)wimaxSessionData).setUsername(userName);

				LogManager.getLogger().trace(MODULE, "Request received is not for EAP, " +
						"only mandatory WiMAX attributes will be added; " +
						"i.e. WiMAX capapbility, AAA-Session-Id and DNS.");
				
				addMandatoryWimaxAttributes(wimaxSessionData, wimaxRequest, wimaxResponse);

				//finally updating the wimax session 
				wimaxSession.updateWimaxSessionData(wimaxSessionData);
			}
		}
	}

	private void addMandatoryWimaxAttributes(WimaxSessionData wimaxSessionData,WimaxRequest wimaxRequest,WimaxResponse wimaxResponse){				

		String aaaSessionId = null;		
		int acctCapabilities = 1;
		int hotlineCapability = 0;   // no hotlining
		int idleModeNotificationCapabilities = 0;


		if(wimaxRequest.getAAA_Session_ID() != null){
			aaaSessionId = wimaxRequest.getAAA_Session_ID();
		}else{
			if(wimaxSessionData!= null && wimaxSessionData.getAAASessionId()!= null){
				aaaSessionId = wimaxSessionData.getAAASessionId();
			}else{
				aaaSessionId = generateAAASessionID((wimaxRequest));
				if(wimaxSessionData != null){
					((WimaxSessionDataImpl)wimaxSessionData).setAaaSessionId(aaaSessionId);					
				}
			}
		}

		wimaxResponse.setAAA_Session_ID(aaaSessionId);

		// adding the wimax capability attribute 

		// 2 - Accounting capabilities  (Adding the value mentioned in the configuration ).

		if(wimaxConfiguration != null ){
			acctCapabilities = wimaxConfiguration.getAccountingCapabilities();
		}		
		wimaxResponse.getWimaxCapabilities().setAccountingCapabilities(acctCapabilities);

		// 3 - Hotlining Capabilities
		if(wimaxRequest.getWimaxCapabilities().getHotliningCapabilities() != null){				
			hotlineCapability = wimaxRequest.getWimaxCapabilities().getHotliningCapabilities();
			if(wimaxSessionData != null)
				((WimaxSessionDataImpl)wimaxSessionData).setHotliningCapabilities(wimaxRequest.getWimaxCapabilities().getHotliningCapabilities());
		}else{
			if(wimaxSessionData != null && wimaxSessionData.getHotliningCapabilities() != null){
				hotlineCapability = wimaxSessionData.getHotliningCapabilities();
			}
		}
		if(hotlineCapability > 0){
			wimaxResponse.getWimaxCapabilities().setHotliningCapabilities(hotlineCapability);
		}

		// 4 - Idle Mode Notification Capabilities			

		if(wimaxConfiguration != null){
			idleModeNotificationCapabilities = wimaxConfiguration.getIdleModeNotificationCapabilities();
		}		
		wimaxResponse.getWimaxCapabilities().setIdleModeNotificationCapabilities(idleModeNotificationCapabilities);


		//adding the DNS List into the wimax Response the check is kept in the create service Response

		List<byte[]> dnsList = wimaxResponse.getClientData().getDNSList(); 			
		if(dnsList != null && !dnsList.isEmpty()){
			wimaxResponse.setDNSList(dnsList);
		}

	}

	private void updateWimaxSessionWithReqInfo(WimaxRequest wimaxRequest, WimaxSessionData wimaxSessionData){

		// NAS-IP-Address
		if(wimaxRequest.getSourceInfo().getNAS_IP_Address() != null)
			((WimaxSessionDataImpl)wimaxSessionData).setNasIP(wimaxRequest.getSourceInfo().getNAS_IP_Address());
		else if(((WimaxSessionDataImpl)wimaxSessionData).getNasIP() == null)
			((WimaxSessionDataImpl)wimaxSessionData).setNasIP(wimaxRequest.getSourceInfo().getSourceIP());

		// NAS-Identifier
		if(wimaxRequest.getSourceInfo().getNAS_Identifier() != null)
			((WimaxSessionDataImpl)wimaxSessionData).setNasIdentifier(wimaxRequest.getSourceInfo().getNAS_Identifier());

		// NAS-Port
		if(wimaxRequest.getSourceInfo().getNAS_Port() != null)
			((WimaxSessionDataImpl)wimaxSessionData).setNasPort(wimaxRequest.getSourceInfo().getNAS_Port().toString());

		// NAS-Port-Type
		if(wimaxRequest.getSourceInfo().getNAS_Port_Type()!= null)
			((WimaxSessionDataImpl)wimaxSessionData).setNasPortType(wimaxRequest.getSourceInfo().getNAS_Port_Type().toString());

		// NAS-Port-Id
		if(wimaxRequest.getSourceInfo().getNAS_Port_ID()!= null)
			((WimaxSessionDataImpl)wimaxSessionData).setNasPortId(wimaxRequest.getSourceInfo().getNAS_Port_ID());

		// Username
		((WimaxSessionDataImpl)wimaxSessionData).setUsername(wimaxRequest.getUserName());
	}


	private String generateAAASessionID(WimaxRequest wimaxRequest) {
		String randomString = new String();
		randomString = wimaxRequest.getRequestReceivedTimeInMillis() + String.valueOf(new Random().nextInt(Integer.MAX_VALUE));		
		return randomString;
	}


	public final boolean isDHCPRequest(WimaxRequest wimaxRequest, WimaxResponse wimaxResponse){
		return dhcpRequestHandler.isEligible(wimaxRequest, wimaxResponse);
	}

	public final boolean isHARequest(WimaxRequest wimaxRequest, WimaxResponse wimaxResponse){
		return haRequestHandler.isEligible(wimaxRequest, wimaxResponse);
	}

	public final boolean isAuthorizeOnlyRequest(WimaxRequest wimaxRequest, WimaxResponse wimaxResponse){
		return authorizeOnlyRequestHandler.isEligible(wimaxRequest, wimaxResponse);
	}

	public final boolean isAuthenticateOnlyRequest(WimaxRequest wimaxRequest, WimaxResponse wimaxResponse){
		return authenticateOnlyRequestHandler.isEligible(wimaxRequest, wimaxResponse);
	}

	public final boolean isEAPRequest(WimaxRequest wimaxRequest, WimaxResponse wimaxResponse){
		return authWimaxRequestHandler.isEligible(wimaxRequest, wimaxResponse);
	}
}
