package com.elitecore.netvertex.core.util;

import com.elitecore.commons.base.Splitter;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.serverx.sessionx.SessionData;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.GatewayTypeConstant;
import com.elitecore.corenetvertex.constants.PCRFEvent;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyValueConstants;
import com.elitecore.corenetvertex.constants.SessionTypeConstant;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.IPCANQoS;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.IPCANQoS.IPCANQoSBuilder;
import com.elitecore.corenetvertex.spr.data.SPRFields;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.corenetvertex.util.ActivePCCRuleParser;
import com.elitecore.corenetvertex.util.GsonFactory;
import com.elitecore.corenetvertex.util.SessionUsageParser;
import com.elitecore.netvertex.pm.quota.QuotaReservation;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;

import java.util.LinkedHashMap;
import java.util.List;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class PCRFPacketUtil {

	private static final String MODULE = "PCRF-PKT-UTIL";
	private static final Splitter AT_SPLITTER = Strings.splitter('@').trimTokens();
	private static final Splitter HASH_SPLITTER = Strings.splitter('#').trimTokens();
	private static final Splitter SEMICOLON_BASE_SPLITTER = Splitter.on(CommonConstants.SEMICOLON).trimTokens();
	private static final Splitter COLON_BASE_SPLITTER = Splitter.on(CommonConstants.COLON).trimTokens();

	public static void buildPCRFRequest(SessionData sessionData , PCRFRequest pcrfRequest){
		
		for(String key : sessionData.getKeySet()){

			if(key.equals(PCRFKeyConstants.CS_REQ_IP_CAN_QOS.val)){
				pcrfRequest.setRequestedQoS(createSessionQoS(sessionData.getValue(key), pcrfRequest.getAttribute(PCRFKeyConstants.QOS_UPGRADE.val)));
			} else if(key.equals(PCRFKeyConstants.CS_USAGE_RESERVATION.val)){
				pcrfRequest.setUsageReservations(createUsageReservationKey(sessionData.getValue(key)));
			} else if(key.equals(PCRFKeyConstants.CS_ACTIVE_PCC_RULES.val)){
				pcrfRequest.setActivePccRules(ActivePCCRuleParser.deserialize(sessionData.getValue(key)));
			} else if(key.equals(PCRFKeyConstants.CS_ACTIVE_CHARGING_RULE_BASE_NAMES.val)){
				pcrfRequest.setActiveChargingRuleBaseNames(ActivePCCRuleParser.deserialize(sessionData.getValue(key)));
			} else if(key.equals(PCRFKeyConstants.CS_PACKAGE_USAGE.val)){
				pcrfRequest.setSessionUsage(SessionUsageParser.deserialize(sessionData.getValue(key)));
			} else if(key.equals(PCRFKeyConstants.CS_QUOTA_RESERVATION.val) && Strings.isNullOrBlank(sessionData.getValue(key)) == false){
				pcrfRequest.setQuotaReservation(GsonFactory.defaultInstance().fromJson(sessionData.getValue(key), QuotaReservation.class));
				pcrfRequest.setAttribute(PCRFKeyConstants.CS_QUOTA_RESERVATION.val, sessionData.getValue(key));
			} else if(key.equals(PCRFKeyConstants.CS_UNACCOUNTED_QUOTA.val) && Strings.isNullOrBlank(sessionData.getValue(key)) == false){
				pcrfRequest.setUnAccountedQuota(GsonFactory.defaultInstance().fromJson(sessionData.getValue(key), QuotaReservation.class));
				pcrfRequest.setAttribute(PCRFKeyConstants.CS_UNACCOUNTED_QUOTA.val, sessionData.getValue(key));
			} else if(key.equals(PCRFKeyConstants.CS_LOCATION.val) && Strings.isNullOrBlank(sessionData.getValue(key)) == false){
				LocationSerializationUtil.deSerialize(pcrfRequest, sessionData.getValue(key));
			}else {
				pcrfRequest.setAttribute(key, sessionData.getValue(key));
			}
			
		}

		pcrfRequest.setSessionStartTime(sessionData.getCreationTime());
		pcrfRequest.setSessionFound(true);
		pcrfRequest.setSessionLoadTime(sessionData.getSessionLoadTime());
		
		SessionTypeConstant sessionType = SessionTypeConstant.fromValue(sessionData.getValue(PCRFKeyConstants.CS_SESSION_TYPE.getVal()));
		if(sessionType == null){
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
				LogManager.getLogger().warn(MODULE, "Gateway Type not added in PCRF Request. Reason: Session Type not found from Session");
			}
		}else{
			GatewayTypeConstant gatewayType = sessionType.gatewayType;
			if(gatewayType == null){
				if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
					LogManager.getLogger().warn(MODULE, "Gateway Type not added in PCRF Request. Reason: " +
					"Gateway Type not found for Session Type: " + sessionType.val);
				}
			}else{
				pcrfRequest.setAttribute(PCRFKeyConstants.GATEWAY_TYPE.getVal(), gatewayType.getVal());
			}
		}
	}

	public static void buildPCRFRequest(PCRFResponse response, PCRFRequest pcrfRequest) {
		
		for(String key : response.getKeySet()){
			if(key.equals(PCRFKeyConstants.CS_REQ_IP_CAN_QOS.val)){
				String jsonString = response.getAttribute(key);
				if(jsonString != null){
					IPCANQoSBuilder ipBuilder = new IPCANQoSBuilder().withJson(jsonString);
					
					
					String qoSUpgradeVal = pcrfRequest.getAttribute(PCRFKeyConstants.QOS_UPGRADE.val);
					if(qoSUpgradeVal != null){
						ipBuilder.withQoSUpgrade(PCRFKeyValueConstants.QOS_UPGRADE_SUPPORTED.val.equals(qoSUpgradeVal));
					}
					
					pcrfRequest.setRequestedQoS(ipBuilder.build());
				}
				
			} else {
				pcrfRequest.setAttribute(key, response.getAttribute(key));
			}
			
		}

		pcrfRequest.setSessionStartTime(response.getSessionStartTime());
		pcrfRequest.setSessionFound(true);
		
		SessionTypeConstant sessionType = SessionTypeConstant.fromValue(response.getAttribute(PCRFKeyConstants.CS_SESSION_TYPE.val));
		if(sessionType == null){
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
				LogManager.getLogger().warn(MODULE, "Gateway Type not added in PCRF Request. Reason: Session Type not found from Session");
			}
		}else{
			GatewayTypeConstant gatewayType = sessionType.gatewayType;
			if(gatewayType == null){
				if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
					LogManager.getLogger().warn(MODULE, "Gateway Type not added in PCRF Request. Reason: " +
					"Gateway Type not found for Session Type: " + sessionType.val);
				}
			}else{
				pcrfRequest.setAttribute(PCRFKeyConstants.GATEWAY_TYPE.getVal(), gatewayType.getVal());
			}
		}
	
	}
	
	private static IPCANQoS createSessionQoS(String jsonString, String qosUpgradeVal) {
		if(Strings.isNullOrBlank(jsonString)){
			return null;
        }

		IPCANQoSBuilder ipBuilder = new IPCANQoSBuilder().withJson(jsonString);

		if(qosUpgradeVal != null){
			ipBuilder.withQoSUpgrade(PCRFKeyValueConstants.QOS_UPGRADE_SUPPORTED.val.equals(qosUpgradeVal));
		}

		return ipBuilder.build();
	}

	public static LinkedHashMap<String,String> createUsageReservationKey(String usageReservationStr) {

		LinkedHashMap<String, String> ruleToPackage = new LinkedHashMap<String, String>();

		if (Strings.isNullOrBlank(usageReservationStr)) {
			if(getLogger().isDebugLogLevel())
				getLogger().debug(MODULE, "Reservation string not found");
		}

		for (String keyValueToken : SEMICOLON_BASE_SPLITTER.split(usageReservationStr)) {
			List<String> keyVal = COLON_BASE_SPLITTER.split(keyValueToken);

			if (keyVal.size() != 2) {
				getLogger().warn(MODULE, "Skip token: " + keyValueToken + ". Invalid format, format should be Key:Value");
				continue;
			}

			ruleToPackage.put(keyVal.get(0), keyVal.get(1));
		}

		return ruleToPackage;
	}

	public static void buildPCRFRequest(SessionData sessionData , PCRFRequest pcrfRequest,boolean override){
		if(pcrfRequest == null){
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "Session Attributes not added in PCRF Request. Reason: " +
						"PCRF Request not provided");
			return;
		}
		
		
		for(String key : sessionData.getKeySet()){

			if(override == false && pcrfRequest.getAttribute(key) != null){
				continue;
			}
			
			if(key.equals(PCRFKeyConstants.CS_REQ_IP_CAN_QOS.val)){
				pcrfRequest.setRequestedQoS(createSessionQoS(sessionData.getValue(key), pcrfRequest.getAttribute(PCRFKeyConstants.QOS_UPGRADE.val)));
			} else if(key.equals(PCRFKeyConstants.CS_USAGE_RESERVATION.val)){
				pcrfRequest.setUsageReservations(createUsageReservationKey(sessionData.getValue(key)));
			} else if(key.equals(PCRFKeyConstants.CS_ACTIVE_PCC_RULES.val)){
				pcrfRequest.setActivePccRules(ActivePCCRuleParser.deserialize(sessionData.getValue(key)));
			} else if(key.equals(PCRFKeyConstants.CS_ACTIVE_CHARGING_RULE_BASE_NAMES.val)){
				pcrfRequest.setActiveChargingRuleBaseNames(ActivePCCRuleParser.deserialize(sessionData.getValue(key)));
			} else if(key.equals(PCRFKeyConstants.CS_PACKAGE_USAGE.val)){
				pcrfRequest.setSessionUsage(SessionUsageParser.deserialize(sessionData.getValue(key)));
			} else if(key.equals(PCRFKeyConstants.CS_QUOTA_RESERVATION.val) && Strings.isNullOrBlank(sessionData.getValue(key)) == false) {
				pcrfRequest.setQuotaReservation(GsonFactory.defaultInstance().fromJson(sessionData.getValue(key), QuotaReservation.class));
                pcrfRequest.setAttribute(PCRFKeyConstants.CS_QUOTA_RESERVATION.val, sessionData.getValue(key));
			} else if(key.equals(PCRFKeyConstants.CS_UNACCOUNTED_QUOTA.val) && Strings.isNullOrBlank(sessionData.getValue(key)) == false) {
				pcrfRequest.setUnAccountedQuota(GsonFactory.defaultInstance().fromJson(sessionData.getValue(key), QuotaReservation.class));
				pcrfRequest.setAttribute(PCRFKeyConstants.CS_UNACCOUNTED_QUOTA.val, sessionData.getValue(key));
			} else if(key.equals(PCRFKeyConstants.CS_LOCATION.val) && Strings.isNullOrBlank(sessionData.getValue(key)) == false){
				LocationSerializationUtil.deSerialize(pcrfRequest, sessionData.getValue(key));
			} else {
				pcrfRequest.setAttribute(key, sessionData.getValue(key));
			}
		}

		pcrfRequest.setSessionStartTime(sessionData.getCreationTime());
		pcrfRequest.setSessionFound(true);
		pcrfRequest.setSessionLoadTime(sessionData.getSessionLoadTime());
		
		SessionTypeConstant sessionType = SessionTypeConstant.fromValue(pcrfRequest.getAttribute(PCRFKeyConstants.CS_SESSION_TYPE.val));
		if(sessionType == null){
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
				LogManager.getLogger().warn(MODULE, "Gateway Type not added in PCRF Request. Reason: Session Type not found from Session");
			}
		}else{
			GatewayTypeConstant gatewayType = sessionType.gatewayType;
			if(gatewayType == null){
				if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
					LogManager.getLogger().warn(MODULE, "Gateway Type not added in PCRF Request. Reason: " +
							"Gateway Type not found for Session Type: " + sessionType.val);
				}
			}else{
				pcrfRequest.setAttribute(PCRFKeyConstants.GATEWAY_TYPE.getVal(), gatewayType.getVal());
			}
		}
	}
	
	public static void buildPCRFResponse(PCRFRequest pcrfRequest , PCRFResponse pcrfResponse){

		for(String key : pcrfRequest.getKeySet()) {
			pcrfResponse.setAttribute(key, pcrfRequest.getAttribute(key));
		}

		pcrfResponse.setSessionStartTime(pcrfRequest.getSessionStartTime());
		pcrfResponse.setMediaComponents(pcrfRequest.getMediaComponents());
		pcrfResponse.setPreviousActiveAFSessions(pcrfRequest.getAFActivePCCRule());
	}

	
	public static void buildPCRFResponse(SessionData sessionData , PCRFResponse pcrfResponse){


		for(String key : sessionData.getKeySet()){
		
			if(key.equals(PCRFKeyConstants.CS_USAGE_RESERVATION.val)){
				pcrfResponse.setUsageReservations(createUsageReservationKey(sessionData.getValue(key)));
			} else if(key.equals(PCRFKeyConstants.CS_ACTIVE_PCC_RULES.val)){
				pcrfResponse.setActivePccRules(ActivePCCRuleParser.deserialize(sessionData.getValue(key)));
			} else if(key.equals(PCRFKeyConstants.CS_ACTIVE_CHARGING_RULE_BASE_NAMES.val)){
				pcrfResponse.setActiveChargingRuleBaseNames(ActivePCCRuleParser.deserialize(sessionData.getValue(key)));
			} else if(key.equals(PCRFKeyConstants.CS_PACKAGE_USAGE.val)){
				pcrfResponse.setSessionUsage(SessionUsageParser.deserialize(sessionData.getValue(key)));
			} else if(key.equals(PCRFKeyConstants.CS_QUOTA_RESERVATION.val) && Strings.isNullOrBlank(sessionData.getValue(key)) == false){
				pcrfResponse.setQuotaReservation(GsonFactory.defaultInstance().fromJson(sessionData.getValue(key), QuotaReservation.class));
				pcrfResponse.setAttribute(PCRFKeyConstants.CS_QUOTA_RESERVATION.val, sessionData.getValue(key));
			} else if(key.equals(PCRFKeyConstants.CS_UNACCOUNTED_QUOTA.val) && Strings.isNullOrBlank(sessionData.getValue(key)) == false){
				pcrfResponse.setUnAccountedQuota(GsonFactory.defaultInstance().fromJson(sessionData.getValue(key), QuotaReservation.class));
				pcrfResponse.setAttribute(PCRFKeyConstants.CS_UNACCOUNTED_QUOTA.val, sessionData.getValue(key));
			} else if(key.equals(PCRFKeyConstants.CS_LOCATION.val) && Strings.isNullOrBlank(sessionData.getValue(key)) == false){
				LocationSerializationUtil.deSerialize(pcrfResponse, sessionData.getValue(key));
			} else {
				pcrfResponse.setAttribute(key, sessionData.getValue(key));
			}
			
		}

		pcrfResponse.setSessionStartTime(sessionData.getCreationTime());
		
		
		SessionTypeConstant sessionType = SessionTypeConstant.fromValue(sessionData.getValue(PCRFKeyConstants.CS_SESSION_TYPE.getVal()));
		
		if(sessionType == null){
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
				LogManager.getLogger().info(MODULE, "Gateway Type not added in PCRF Request. Reason: Session Type not found from Session");
			}
			
		}else{
			GatewayTypeConstant gatewayType = sessionType.gatewayType;
			if(gatewayType == null){
				if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
					LogManager.getLogger().info(MODULE, "Gateway Type not added in PCRF Request. " +
					"Reason: Gateway Type not found for Session Type: " + sessionType.val);
				}
			}else{
				pcrfResponse.setAttribute(PCRFKeyConstants.GATEWAY_TYPE.getVal(), gatewayType.getVal());
			}
		}
		
	}
	
	/**
	 * Setting subscriber profile parameters into PCRFrequest
	 * @param request
	 * @param response
	 * @param userProfile
	 */
	public static void setProfileAttributes(PCRFRequest request, PCRFResponse response, SPRInfo userProfile) {
		
		
		for(SPRFields sprField : SPRFields.values()){
			
			if(sprField.pcrfKey != null) {
				
				String val = sprField.getStringValue(userProfile);
				if(val != null) {
					request.setAttribute(sprField.pcrfKey.val, val);
					response.setAttribute(sprField.pcrfKey.val, val);
				}
			}
		}

		response.setSPRfetchTime(request.getSPRFetchTime());
		response.setSPRReadTime(request.getSPRReadTime());

		if(request.getAttribute(PCRFKeyConstants.CS_USERNAME.getVal()) == null && userProfile.getUserName() != null){
			request.setAttribute(PCRFKeyConstants.CS_USERNAME.getVal(), userProfile.getUserName());
			response.setAttribute(PCRFKeyConstants.CS_USERNAME.getVal(), userProfile.getUserName());
		}
		if(request.getAttribute(PCRFKeyConstants.CS_SUBSCRIPTION_ID_TYPE_IMSI.getVal()) == null && userProfile.getImsi() != null){
			request.setAttribute(PCRFKeyConstants.CS_SUBSCRIPTION_ID_TYPE_IMSI.getVal(), userProfile.getImsi());
			response.setAttribute(PCRFKeyConstants.CS_SUBSCRIPTION_ID_TYPE_IMSI.getVal(), userProfile.getImsi());
		}
		if(request.getAttribute(PCRFKeyConstants.CS_SUBSCRIPTION_ID_TYPE_SIPURI.getVal()) == null && userProfile.getSipURL()!= null){
			request.setAttribute(PCRFKeyConstants.CS_SUBSCRIPTION_ID_TYPE_SIPURI.getVal(), userProfile.getSipURL());
			response.setAttribute(PCRFKeyConstants.CS_SUBSCRIPTION_ID_TYPE_SIPURI.getVal(), userProfile.getSipURL());
		}
		if(request.getAttribute(PCRFKeyConstants.CS_SUBSCRIPTION_ID_TYPE_MSISDN.getVal()) == null && userProfile.getMsisdn() != null){
			request.setAttribute(PCRFKeyConstants.CS_SUBSCRIPTION_ID_TYPE_MSISDN.getVal(), userProfile.getMsisdn());
			response.setAttribute(PCRFKeyConstants.CS_SUBSCRIPTION_ID_TYPE_MSISDN.getVal(), userProfile.getMsisdn());
		}
		if(request.getAttribute(PCRFKeyConstants.USER_EQUIPMENT_EUI64.getVal()) == null && userProfile.getEui64() != null){
			request.setAttribute(PCRFKeyConstants.USER_EQUIPMENT_EUI64.getVal(), userProfile.getEui64());
			response.setAttribute(PCRFKeyConstants.USER_EQUIPMENT_EUI64.getVal(), userProfile.getEui64());
		}
		
		request.setAttribute(PCRFKeyConstants.SUB_UNKNOWN_USER.getVal(), Boolean.toString(userProfile.isUnknownUser()));
		response.setAttribute(PCRFKeyConstants.SUB_UNKNOWN_USER.getVal(), Boolean.toString(userProfile.isUnknownUser()));
		
		request.setAttribute(PCRFKeyConstants.SUB_PROFILE_EXPIRED_HOURS.val, String.valueOf(userProfile.getProfileExpiredHours()));
		response.setAttribute(PCRFKeyConstants.SUB_PROFILE_EXPIRED_HOURS.val, String.valueOf(userProfile.getProfileExpiredHours()));
		
		request.setAttribute(PCRFKeyConstants.CS_USER_IDENTITY.getVal(), userProfile.getSubscriberIdentity());
		response.setAttribute(PCRFKeyConstants.CS_USER_IDENTITY.getVal(), userProfile.getSubscriberIdentity());

		response.setAttribute(PCRFKeyConstants.SUB_ALTERNATE_IDENTITY.getVal(),request.getAttribute(PCRFKeyConstants.SUB_ALTERNATE_IDENTITY.getVal()));

	}

	public static List<String> parseSelectedQoSName(String qosWithHash) {
		return HASH_SPLITTER.split(qosWithHash);
	}

	public static List<String> parseTopUp(String pkg) {
		return AT_SPLITTER.split(pkg);
	}

	public static boolean isConsecutiveRequest(PCRFRequest pcrfRequest) {

		if (isReAuthRequest(pcrfRequest)) {
			return  false;
		}

		if (isInitialRequest(pcrfRequest)) {
			return  false;
		}

		String previousRequestNo = pcrfRequest.getAttribute(PCRFKeyConstants.PREVIOUS_REQUEST_NUMBER.getVal());
		String currentRequestNo = pcrfRequest.getAttribute(PCRFKeyConstants.REQUEST_NUMBER.getVal());

		if(currentRequestNo == null){
			return true;
		}

		if(previousRequestNo == null){
			return false;
		}


		long previousReq;
		long currentReq;
		try{
			previousReq = Long.parseLong(previousRequestNo);
		}catch(NumberFormatException e){
			if(getLogger().isDebugLogLevel()){
				getLogger().debug(MODULE, "Invalid previous request no " + previousRequestNo);
			}
			return false;
		}
		try{
			currentReq = Long.parseLong(currentRequestNo);
		}catch(NumberFormatException e){
			if(getLogger().isDebugLogLevel()){
				getLogger().debug(MODULE, "Invalid current request no " + currentRequestNo);
			}
			return true;
		}

		return currentReq == (previousReq + 1);


	}

	public static boolean isInitialRequest(PCRFRequest request) {
		return request.getPCRFEvents().contains(PCRFEvent.SESSION_START);
	}
	public static boolean isTerminateRequest(PCRFRequest pcrfRequest) {
		return pcrfRequest.getPCRFEvents().contains(PCRFEvent.SESSION_STOP);
	}

	public static boolean isReAuthRequest(PCRFRequest request) {

		return request.getPCRFEvents().contains(PCRFEvent.REAUTHORIZE);
	}

	public static boolean isForceFullReAuthRequest(PCRFRequest request) {

		if (isReAuthRequest(request) == false) {
			return false;
		}

		return PCRFKeyValueConstants.FORCEFUL_SESSION_RE_AUTH.val.equals(request.getAttribute(PCRFKeyConstants.SESSION_RE_AUTH.val));
	}
}
