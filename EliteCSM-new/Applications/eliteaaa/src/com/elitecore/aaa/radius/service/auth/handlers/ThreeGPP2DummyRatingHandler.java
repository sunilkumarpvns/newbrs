package com.elitecore.aaa.radius.service.auth.handlers;

import java.util.HashMap;

import com.elitecore.aaa.radius.conf.DummyRatingConfiguration;
import com.elitecore.aaa.radius.service.auth.RadAuthRequest;
import com.elitecore.aaa.radius.service.auth.RadAuthResponse;
import com.elitecore.aaa.radius.service.auth.RadAuthServiceContext;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.coreradius.commons.attributes.BaseRadiusAttribute;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.RadiusUtility;
import com.elitecore.coreradius.commons.util.constants.Constants3GPP2;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeValuesConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;

/**
 * 
 * @author narendra.pathai
 *
 */
public class ThreeGPP2DummyRatingHandler implements RadAuthVendorSpecificHandler{

	public static final String MODULE = "3GPP2-DUMMY-HDLR";
	private DummyRatingConfiguration dummyRatingConfiguration;
	private int prepaidIteration = 0;
	private int thresholdProportion;
	private static HashMap<Object, Object> callingStationIdQuotaMap = null;	
	private static HashMap<Object, Object> callingStationIdIterationsMap = null;
	private RadAuthServiceContext serviceContext;
	
	public ThreeGPP2DummyRatingHandler(RadAuthServiceContext serviceContext) {
		this.serviceContext = serviceContext;
	}

	@Override
	public boolean isEligible(RadAuthRequest request) {
		if(request.getRadiusAttribute(RadiusConstants.VENDOR_3GPP2_ID,Constants3GPP2.CORRELATION_ID) != null || ((RadAuthRequest)request).getRadiusAttribute(RadiusConstants.VENDOR_3GPP2_ID, Constants3GPP2.MN_HA_SPI) != null){
			if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
				LogManager.getLogger().trace(MODULE, "3GPP2 Correlation ID or MN-HA-SPI present in request. Reqest eligible for 3GPP2 Dummy Rating.");
				return true;
		}
		return false;
	}

	@Override
	public void handleRequest(RadAuthRequest request, RadAuthResponse response) {
		if(request.getRadiusAttribute(RadiusAttributeConstants.SERVICE_TYPE) != null && request.getRadiusAttribute(RadiusAttributeConstants.SERVICE_TYPE).getIntValue() != RadiusAttributeValuesConstants.AUTHORIZE_ONLY){
			handleAuthRequest(request, response);
		}else{
			handleAuthorizeOnlyRequest(request, response);
		}
	}

	@Override
	public void init() throws InitializationFailedException {
		if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
			LogManager.getLogger().info(MODULE, "Initializing 3GPP2 Dummy Rating handler");
		}
		
		callingStationIdQuotaMap = new HashMap<Object, Object>();
    	callingStationIdIterationsMap = new HashMap<Object, Object>();
		
		dummyRatingConfiguration = serviceContext.getAuthConfiguration().getDummyRatingConfiguration();
		if(dummyRatingConfiguration == null){
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
				LogManager.getLogger().debug(MODULE, "Dummy Rating configuration not found.");
			}
			throw new InitializationFailedException("Dummy Rating configuration not found.");
		}
		
		String strPrepaidIterations = (String)(dummyRatingConfiguration.getDummyRatingConfiguration().get(DummyRatingConfiguration.ITERATIONS));
		if( strPrepaidIterations != null && strPrepaidIterations.trim().length() > 0){
			try{
				prepaidIteration = Integer.parseInt(strPrepaidIterations);
			}catch(NumberFormatException ex){
				prepaidIteration = 0;
				if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
					LogManager.getLogger().warn(MODULE, "Invalid value: " + strPrepaidIterations + " for prepaid iterations. Using default value: 0");
					
				}
			}
		}	
		
		String strThreshold = (String)dummyRatingConfiguration.getDummyRatingConfiguration().get(DummyRatingConfiguration.THRESHOLD);
		
		if(strThreshold != null && strThreshold.trim().length() > 0){
			try{
				thresholdProportion = Integer.parseInt(strThreshold);
			}catch(NumberFormatException ex){
				thresholdProportion = 90;
				if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
					LogManager.getLogger().warn(MODULE, "Invalid value: " + strThreshold + " for threshold. Using default value: 90");
				}
			}
		}
	}
	
	private void handleAuthRequest(RadAuthRequest request, RadAuthResponse response){
		if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
			LogManager.getLogger().info(MODULE, "Request packet received: "+request);
		
		if(request.getAccountData().getCustomerType() != null){
			if(request.getAccountData().getCustomerType().equalsIgnoreCase("prepaid")){
				if(dummyRatingConfiguration.getDummyRatingConfiguration().containsKey(DummyRatingConfiguration.QUOTA)){
					addPrepaidAttributes(request, response);
				}
				response.setFurtherProcessingRequired(false);
				response.setProcessingCompleted(true);
			}else{
				if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
					LogManager.getLogger().info(MODULE, "Customer type is not prepaid. So skipping further processing of this handler.");
				}
				return;
			}
		}else{
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
				LogManager.getLogger().info(MODULE, "Customer type not found in subscriber profile. Skipping further processing of this handler.");
			}
			return;
		}

		if(response.getPacketType() == RadiusConstants.ACCESS_ACCEPT_MESSAGE){
			String timeoutValue = (String) dummyRatingConfiguration.getDummyRatingConfiguration().get(DummyRatingConfiguration.SESSION_TIMEOUT);
			IRadiusAttribute sessionTimeout = response.getRadiusAttribute(RadiusAttributeConstants.SESSION_TIMEOUT);
			if(sessionTimeout != null){
				sessionTimeout.setStringValue(timeoutValue);
			}else{
				sessionTimeout = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.SESSION_TIMEOUT);
				sessionTimeout.setStringValue(timeoutValue);
				response.addAttribute(sessionTimeout);
			}
		}
		
		if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
			LogManager.getLogger().info(MODULE, "Response packet : "+response);

	}
	
	private void handleAuthorizeOnlyRequest(RadAuthRequest request, RadAuthResponse response){
		if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
			LogManager.getLogger().info(MODULE, "Request packet received: " + request);
		
		String callingStationId = null;
		
		if(request.getRadiusAttribute(RadiusAttributeConstants.CALLING_STATION_ID) != null){
			callingStationId = request.getRadiusAttribute(RadiusAttributeConstants.CALLING_STATION_ID).getStringValue();
		}
		
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(MODULE, "Calling-Staion-Id received: " + callingStationId);
		
		if(!callingStationIdQuotaMap.containsKey(callingStationId)){
			return;
			//TODO check here what should be the behavior
		}
		
		String timeoutValue = (String) dummyRatingConfiguration.getDummyRatingConfiguration().get(DummyRatingConfiguration.SESSION_TIMEOUT);
		IRadiusAttribute sessionTimeout = response.getRadiusAttribute(RadiusAttributeConstants.SESSION_TIMEOUT);
		if(sessionTimeout != null){
			sessionTimeout.setStringValue(timeoutValue);
		}else{
			sessionTimeout = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.SESSION_TIMEOUT);
			sessionTimeout.setStringValue(timeoutValue);
			response.addAttribute(sessionTimeout);
		}
		
		
		if(dummyRatingConfiguration.getDummyRatingConfiguration().containsKey(DummyRatingConfiguration.QUOTA)){
			addPrepaidAttributes(request, response);
		}
		
		response.setFurtherProcessingRequired(false);
		response.setProcessingCompleted(true);
		if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
			LogManager.getLogger().info(MODULE, "Response packet : "+response);

	}
	
	private void addPrepaidAttributes(RadAuthRequest request, RadAuthResponse response){

		long lVendorId = RadiusConstants.VENDOR_3GPP2_ID;
		String callingStationId = null;
		Boolean sendLastQuota = false;
		int iteration = prepaidIteration;
		
//		 To get the AAA-Session-Id from the response packet, to check if a reservation for that particular session exists or not.
		if(request.getRadiusAttribute(RadiusAttributeConstants.CALLING_STATION_ID) != null){
			callingStationId = request.getRadiusAttribute(RadiusAttributeConstants.CALLING_STATION_ID).getStringValue();
		}
		
		// To check the reason for which this request is received, in case of authorize-only request.
		IRadiusAttribute updateReasonAttr = request.getRadiusAttribute(RadiusConstants.VENDOR_3GPP2_ID, Constants3GPP2.UPDATE_REASON);
		if(updateReasonAttr != null && updateReasonAttr.getValueBytes()[0] >= (byte)4){
			sendLastQuota = true;
		}
		if(request.getRadiusAttribute(RadiusAttributeConstants.CALLING_STATION_ID) != null){
			callingStationId = request.getRadiusAttribute(RadiusAttributeConstants.CALLING_STATION_ID).getStringValue();
		}
		
		BaseRadiusAttribute ppaq = (BaseRadiusAttribute)Dictionary.getInstance().getAttribute(lVendorId, Constants3GPP2.PPAQ);
		
		byte[] attributeValue = null;
		
		BaseRadiusAttribute quotaId = (BaseRadiusAttribute)Dictionary.getInstance().getAttribute(lVendorId, Constants3GPP2.PPAQ, Constants3GPP2.QUOTA_ID);
		String qId = (String)dummyRatingConfiguration.getDummyRatingConfiguration().get(DummyRatingConfiguration.QUOTA_ID);
		quotaId.setStringValue(qId);
		attributeValue = RadiusUtility.appendBytes(attributeValue, quotaId.getBytes());
		
		if(callingStationIdIterationsMap.containsKey(callingStationId)){
			iteration = (Integer)callingStationIdIterationsMap.get(callingStationId);
		}
		
		String strQuota = (String)dummyRatingConfiguration.getDummyRatingConfiguration().get(DummyRatingConfiguration.QUOTA);
		int iQuota = 0;
		if(strQuota.contains(".")){
			iQuota = getVolumeFromString(strQuota, callingStationId);
		}else if(strQuota.length() > 0){
			iQuota = Integer.parseInt(strQuota);
		}
		
		if(iQuota == 0){
			return;
		}
		
		if(callingStationIdQuotaMap.containsKey(callingStationId)){
			if(iteration == 1 && !sendLastQuota){
				iQuota = (Integer)callingStationIdQuotaMap.get(callingStationId);
				callingStationIdQuotaMap.put(callingStationId, iQuota);
				iteration --;
				callingStationIdIterationsMap.put(callingStationId, iteration);
			}else if(iteration > 1 && !sendLastQuota){
				iQuota += (Integer)callingStationIdQuotaMap.get(callingStationId);
				callingStationIdQuotaMap.put(callingStationId, iQuota);
				iteration --;
				callingStationIdIterationsMap.put(callingStationId, iteration);
			}else{
				iQuota = (Integer)callingStationIdQuotaMap.get(callingStationId);
				callingStationIdQuotaMap.remove(callingStationId);
			}
		}else{
			callingStationIdIterationsMap.put(callingStationId, prepaidIteration);
			callingStationIdQuotaMap.put(callingStationId, iQuota);
		}
		
		if(iteration == 0){
			callingStationIdQuotaMap.remove(callingStationId);
		}
		
		int iThreshold = (thresholdProportion * iQuota) / 100;
		
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(MODULE, "Quota reserved for existing AAA session ids: " + callingStationIdQuotaMap);
		
		BaseRadiusAttribute quota = null;
		BaseRadiusAttribute threshold = null;
		BaseRadiusAttribute prepaidServer1 = null;
		BaseRadiusAttribute prepaidServer2 = null;
		BaseRadiusAttribute prepaidServer3 = null;
		
		String chargingType = (String)dummyRatingConfiguration.getDummyRatingConfiguration().get(DummyRatingConfiguration.CHARGING_TYPE);

		//Adding PPAC
		BaseRadiusAttribute  ppac = (BaseRadiusAttribute)Dictionary.getInstance().getAttribute(lVendorId,Constants3GPP2.PPAC);
		byte[] ppacattributeValue = null;
		BaseRadiusAttribute availableInClient = (BaseRadiusAttribute) Dictionary.getInstance().getAttribute(lVendorId,Constants3GPP2.PPAC,Constants3GPP2.PPAC_AVAILABLE_IN_CLIENT);
		BaseRadiusAttribute selectedForSession = (BaseRadiusAttribute) Dictionary.getInstance().getAttribute(lVendorId,Constants3GPP2.PPAC,Constants3GPP2.PPAC_SELECTED_FOR_SESSION);


		if(chargingType.equalsIgnoreCase("duration")){
			selectedForSession.setStringValue("2");
		}else if(chargingType.equalsIgnoreCase("volume")){
			selectedForSession.setStringValue("1");
		}
		availableInClient.setStringValue("3");
		ppacattributeValue = RadiusUtility.appendBytes(ppacattributeValue,availableInClient.getBytes());
		ppacattributeValue = RadiusUtility.appendBytes(ppacattributeValue, selectedForSession.getBytes());		
		ppac.addTLVAttribute(availableInClient);
		ppac.addTLVAttribute(selectedForSession);
		
		ppac.setValueBytes(ppacattributeValue);
		response.addAttribute(ppac);

		if(chargingType.equalsIgnoreCase("duration")){
			quota = (BaseRadiusAttribute)Dictionary.getInstance().getAttribute(lVendorId, Constants3GPP2.PPAQ, Constants3GPP2.DURATION_QUOTA);
			quota.setIntValue(iQuota);
			attributeValue = RadiusUtility.appendBytes(attributeValue, quota.getBytes());
			
			threshold = (BaseRadiusAttribute)Dictionary.getInstance().getAttribute(lVendorId, Constants3GPP2.PPAQ, Constants3GPP2.DURATION_THRESHOLD);
			threshold.setIntValue(iThreshold);
			attributeValue = RadiusUtility.appendBytes(attributeValue, threshold.getBytes());

		}else if(chargingType.equalsIgnoreCase("volume")){
			quota = (BaseRadiusAttribute)Dictionary.getInstance().getAttribute(lVendorId, Constants3GPP2.PPAQ, Constants3GPP2.VOLUME_QUOTA);
			quota.setIntValue(iQuota);

			
			attributeValue = RadiusUtility.appendBytes(attributeValue, quota.getBytes());
			
			threshold = (BaseRadiusAttribute)Dictionary.getInstance().getAttribute(lVendorId, Constants3GPP2.PPAQ, Constants3GPP2.VOLUME_THRESHOLD);
			threshold.setIntValue(iThreshold);
			attributeValue = RadiusUtility.appendBytes(attributeValue, threshold.getBytes());
		}
		
		prepaidServer1 = (BaseRadiusAttribute)Dictionary.getInstance().getAttribute(lVendorId,Constants3GPP2.PPAQ,Constants3GPP2.PREPAID_SERVER);
		prepaidServer1.setStringValue("FFFF"); //with FFFF it is working
		attributeValue = RadiusUtility.appendBytes(attributeValue,prepaidServer1.getBytes());

//		prepaidServer2 = (BaseTLVAttribute)Dictionary.getInstance().getAttribute(lVendorId,Constants3GPP2.PPAQ,Constants3GPP2.PREPAID_SERVER);
//		prepaidServer2.setStringValue("01234567890123456789");
//		attributeValue = RadiusUtility.appendBytes(attributeValue,prepaidServer2.getBytes());
//
//		prepaidServer3 = (BaseTLVAttribute)Dictionary.getInstance().getAttribute(lVendorId,Constants3GPP2.PPAQ,Constants3GPP2.PREPAID_SERVER);
//		prepaidServer3.setStringValue("01234567890123456789");
//		attributeValue = RadiusUtility.appendBytes(attributeValue,prepaidServer3.getBytes());

		ppaq.addTLVAttribute(quotaId);
		ppaq.addTLVAttribute(quota);		
		if(threshold != null){
			ppaq.addTLVAttribute(threshold);
		}
		ppaq.addTLVAttribute(prepaidServer1);
//		ppaq.addTLVAttribute(prepaidServer2);
//		ppaq.addTLVAttribute(prepaidServer3);
		ppaq.setValueBytes(attributeValue);
		response.addAttribute(ppaq);
	}
	
	private int getVolumeFromString(String strQuota, String aaaSessionId) {
		String parts[] = strQuota.split("[.]");
		String wholePart = null; 
		String fractionPart = null;
//		Logger.logInfo(MODULE, "number of parts: " + parts.length);
		if(parts.length < 2){
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "Total volume from configuration: " + strQuota);
				wholePart = strQuota;
//				aaaSessionIdExponentMap.put(aaaSessionId, 0);
		}else{
/*			for(int i=0; i<parts.length; i++){
				Logger.logInfo(MODULE, "part[" + i +"]: " + parts[i]);
			}
*/			
			if(parts.length == 2){
				wholePart = parts[0];
				fractionPart = parts[1];
			}
		}
		
		int whole = Integer.parseInt(wholePart);
		int fraction = 0;
		if(fractionPart != null){
			fraction = Integer.parseInt(fractionPart);
			for(int i=0; i<fractionPart.length(); i++){
				whole *= 10;
			}
		}
		
		int volume = whole + fraction;
		return volume;
	}
}
