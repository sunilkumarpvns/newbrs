package com.elitecore.aaa.radius.service.auth.handlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.elitecore.aaa.radius.conf.DummyRatingConfiguration;
import com.elitecore.aaa.radius.service.auth.RadAuthRequest;
import com.elitecore.aaa.radius.service.auth.RadAuthResponse;
import com.elitecore.aaa.radius.service.auth.RadAuthServiceContext;
import com.elitecore.aaa.radius.service.auth.constant.AuthReplyMessageConstant;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.coreradius.commons.attributes.BaseRadiusAttribute;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.attributes.VendorSpecificAttribute;
import com.elitecore.coreradius.commons.attributes.wimaxattribute.PrepaidTLVAttribute;
import com.elitecore.coreradius.commons.attributes.wimaxattribute.WimaxGroupedAttribute;
import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.RadiusUtility;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeValuesConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;
import com.elitecore.coreradius.commons.util.constants.WimaxAttrConstants;
import com.elitecore.coreradius.commons.util.constants.WimaxAttrConstants.PPAQ;
import com.elitecore.coreradius.commons.util.constants.WimaxAttrConstants.PacketFlowDescriptor;
import com.elitecore.coreradius.commons.util.constants.WimaxAttrConstants.QoSDescriptor;

/**
 * 
 * @author narendra.pathai
 *
 */

public class WiMAXDummyRatingHandler implements RadAuthVendorSpecificHandler{

	public static final String MODULE = "WIMAX-DUMMY-RATING-HDLR";
	private RadAuthServiceContext authServiceContext;
	private DummyRatingConfiguration dummyRatingConfiguration;
	private static HashMap<Object, Object> aaaSessionIdQuotaMap = null;
	private static HashMap<Object, Object> aaaSessionIdExponentMap = null;
	private static HashMap<Object, Object> aaaSessionIdIterationsMap = null;
	int prepaidIteration ;
	int thresholdProportion;
	int termination_Action;
	int actionOnTerminateRequest;
	
	public WiMAXDummyRatingHandler(RadAuthServiceContext serviceContext) {
		this.authServiceContext = serviceContext;
	}

	@Override
	public boolean isEligible(RadAuthRequest request) {
		if(request.getRadiusAttribute(RadiusConstants.WIMAX_VENDOR_ID, WimaxAttrConstants.WIMAX_CAPABILITY.getIntValue()) !=null||
				request.getRadiusAttribute(RadiusConstants.WIMAX_VENDOR_ID, WimaxAttrConstants.PPAC.getIntValue()) !=null||
				request.getRadiusAttribute(RadiusConstants.WIMAX_VENDOR_ID, WimaxAttrConstants.PPAQ.getIntValue()) !=null||
				request.getRadiusAttribute(RadiusConstants.WIMAX_VENDOR_ID, WimaxAttrConstants.PTS.getIntValue()) !=null||
				request.getRadiusAttribute(RadiusConstants.WIMAX_VENDOR_ID, WimaxAttrConstants.DHCP_RK_KEY_ID.getIntValue()) !=null){
			if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
				LogManager.getLogger().trace(MODULE, "Wimax Prepaid attribute (either WIMAX-CAP PPAC PPAQ PTS DHCPRKID) present in request, request eligible for WiMAX Dummy Rating.");
				return true;
			
		}
		return false;
	}

	@Override
	public void handleRequest(RadAuthRequest request, RadAuthResponse response) {

		if((request.getRadiusAttribute(RadiusAttributeConstants.SERVICE_TYPE) != null && 
				request.getRadiusAttribute(RadiusAttributeConstants.SERVICE_TYPE).getIntValue()
				== RadiusAttributeValuesConstants.AUTHORIZE_ONLY)|| 
				(request.getRadiusAttribute(RadiusConstants.CISCO_VENDOR_ID,RadiusAttributeConstants.CISCO_SSG_SERVICE_INFO)
						!=null)){
			//TODO the test for the cisco in the 5.6 is still pending
			handleAuthorizeOnlyRequest(request, response);
		}else if (request.getRadiusAttribute(RadiusConstants.WIMAX_VENDOR_ID,WimaxAttrConstants.MN_HA_MIP4_SPI.getIntValue()) != null){
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
				LogManager.getLogger().info(MODULE, "Initial request recieved from HA");				
			handleHAInitialRequest(request, response);
		}else{
			handleAuthRequest(request, response);
		}
		
		//this will do the task of stopping the processing of other handlers
		response.setFurtherProcessingRequired(false);
		response.setProcessingCompleted(true);
	}
	
	private void handleAuthorizeOnlyRequest(RadAuthRequest request, RadAuthResponse response){

		/*if(getLogger().isLogLevel(LogLevel.INFO))
			getLogger().info(MODULE, "Request packet received: " + request);
		*/
		String aaaSessionId = null;
		
		IRadiusAttribute aaaSessionIdAttr = request.getRadiusAttribute(RadiusConstants.WIMAX_VENDOR_ID, WimaxAttrConstants.AAA_SESSION_ID.getIntValue());
		if(aaaSessionIdAttr != null){
			aaaSessionId = aaaSessionIdAttr.getStringValue();
		}
		
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(MODULE, "AAA Session ID received: " + aaaSessionId);
		
		if(!aaaSessionIdQuotaMap.containsKey(aaaSessionId)){
			response.setPacketType(RadiusConstants.ACCESS_REJECT_MESSAGE);
			response.setResponseMessage(AuthReplyMessageConstant.INITIAL_RESERVATION_NOT_AVAILABLE);
			response.setProcessingCompleted(true);
			response.setFurtherProcessingRequired(false);
			return;
		}
		
		String timeoutValue = (String)dummyRatingConfiguration.getDummyRatingConfiguration().get(DummyRatingConfiguration.SESSION_TIMEOUT);
		IRadiusAttribute sessionTimeout = response.getRadiusAttribute(RadiusAttributeConstants.SESSION_TIMEOUT);
		if(sessionTimeout != null){
			sessionTimeout.setStringValue(timeoutValue);
		}else{
			sessionTimeout = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.SESSION_TIMEOUT);
			if(sessionTimeout != null){
				sessionTimeout.setStringValue(timeoutValue);
				response.addAttribute(sessionTimeout);
			}
		}
		
		if(dummyRatingConfiguration.getDummyRatingConfiguration().containsKey(DummyRatingConfiguration.QUOTA)){
			addPrepaidAttributes(request, response);
		}
	}
	
	private void handleHAInitialRequest(RadAuthRequest request, RadAuthResponse response){

		if(request.getAccountData().getCustomerType().equalsIgnoreCase("prepaid")){
			if(dummyRatingConfiguration.getDummyRatingConfiguration().containsKey(DummyRatingConfiguration.QUOTA)){
				addPPAQForHaRequest(request,response);
			}
		}
		
		if(dummyRatingConfiguration.getDummyRatingConfiguration().containsKey(DummyRatingConfiguration.QOS)){
			addQosParameters(request,response);
		}
		
		if(dummyRatingConfiguration.getDummyRatingConfiguration().containsKey(DummyRatingConfiguration.PACKET_FLOW_DESCRIPTOR)){
			addPacketFlowParameters(request,response);
		}
		
		String timeoutValue = (String)dummyRatingConfiguration.getDummyRatingConfiguration().get(DummyRatingConfiguration.SESSION_TIMEOUT);
		if(response.getPacketType() == RadiusConstants.ACCESS_ACCEPT_MESSAGE){
			IRadiusAttribute sessionTimeout = response.getRadiusAttribute(RadiusAttributeConstants.SESSION_TIMEOUT);
			if(sessionTimeout != null && timeoutValue.trim().length() > 0){
				sessionTimeout.setStringValue(timeoutValue);
			}else{
				sessionTimeout = Dictionary.getInstance().getKnownAttribute(RadiusAttributeConstants.SESSION_TIMEOUT);
				if(sessionTimeout != null){
					sessionTimeout.setStringValue(String.valueOf(timeoutValue));
					response.addAttribute(sessionTimeout);
				}
			}
		}
		
	}
	
	private void handleAuthRequest(RadAuthRequest request, RadAuthResponse response){

		if(request.getAccountData().getCustomerType() != null && request.getAccountData().getCustomerType().equalsIgnoreCase("prepaid")){
			if(dummyRatingConfiguration.getDummyRatingConfiguration().containsKey(DummyRatingConfiguration.QUOTA)){
				addPrepaidAttributes(request,response);
			}
		}
		
		if(dummyRatingConfiguration.getDummyRatingConfiguration().containsKey(DummyRatingConfiguration.QOS)){
			addQosParameters(request,response);
		}
		
		if(dummyRatingConfiguration.getDummyRatingConfiguration().containsKey(DummyRatingConfiguration.PACKET_FLOW_DESCRIPTOR)){
			addPacketFlowParameters(request,response);
		}
		
		if(response.getPacketType() == RadiusConstants.ACCESS_ACCEPT_MESSAGE){
			String timeoutValue = (String)dummyRatingConfiguration.getDummyRatingConfiguration().get(DummyRatingConfiguration.SESSION_TIMEOUT);
			if(response.getPacketType() == RadiusConstants.ACCESS_ACCEPT_MESSAGE){
				IRadiusAttribute sessionTimeout = response.getRadiusAttribute(RadiusAttributeConstants.SESSION_TIMEOUT);
				if(sessionTimeout != null && timeoutValue.trim().length() > 0){
					sessionTimeout.setStringValue(timeoutValue);
				}else{
					sessionTimeout = Dictionary.getInstance().getKnownAttribute(RadiusAttributeConstants.SESSION_TIMEOUT);
					if(sessionTimeout != null){
						sessionTimeout.setStringValue(String.valueOf(timeoutValue));
						response.addAttribute(sessionTimeout);
					}
				}
			}
		}
	}
	
	private void addPrepaidAttributes(RadAuthRequest request, RadAuthResponse response){

		long lVendorId = RadiusConstants.WIMAX_VENDOR_ID;
		String aaaSessionId = null;
		Boolean sendLastQuota = false;
		Boolean termination = false;
		Boolean addAAASessionId = false;
		int iteration = prepaidIteration;
		
//		 To get the AAA-Session-Id from the response packet, to check if a reservation for that particular session exists or not.
		IRadiusAttribute aaaSessionIdAttribute = response.getRadiusAttribute(RadiusConstants.WIMAX_VENDOR_ID, WimaxAttrConstants.AAA_SESSION_ID.getIntValue());
		if(aaaSessionIdAttribute != null){
			aaaSessionId = aaaSessionIdAttribute.getStringValue();
		}
		
		if(aaaSessionId == null){
			addAAASessionId = true;
		}
		
		// To check the reason for which this request is received, in case of authorize-only request.
		if(request.getRadiusAttribute(RadiusAttributeConstants.VENDOR_SPECIFIC) != null){
			ArrayList<IRadiusAttribute> vendorSpecificAttributes = (ArrayList<IRadiusAttribute>)request.getRadiusAttributes(RadiusAttributeConstants.VENDOR_SPECIFIC);
			Iterator<IRadiusAttribute> vendorSpecificItr = vendorSpecificAttributes.iterator();
			
			while(vendorSpecificItr.hasNext()){
				VendorSpecificAttribute radiusVendorAttribute = (VendorSpecificAttribute)vendorSpecificItr.next();
				if(radiusVendorAttribute.getVendorID() == RadiusConstants.WIMAX_VENDOR_ID){
					if(radiusVendorAttribute.getVendorTypeAttribute().getAttribute(WimaxAttrConstants.PPAQ.getIntValue()) != null){
						WimaxGroupedAttribute receivedPPAQ = (WimaxGroupedAttribute)radiusVendorAttribute.getVendorTypeAttribute().getAttribute(WimaxAttrConstants.PPAQ.getIntValue());
						List<IRadiusAttribute> ppaqSubAttributes = receivedPPAQ.getTLVAttrList();
						if(ppaqSubAttributes!= null && !ppaqSubAttributes.isEmpty()){
							final int size = ppaqSubAttributes.size();
							for(int i = 0 ; i < size ; i ++){
								IRadiusAttribute subAttribute = ppaqSubAttributes.get(i);
								if(subAttribute.getType() == PPAQ.UPDATE_REASON.getIntValue() && subAttribute.getValueBytes()[0] >= (byte)4){
									sendLastQuota = true;
								}
							}
						}
					}
					if(radiusVendorAttribute.getVendorTypeAttribute().getAttribute(WimaxAttrConstants.AAA_SESSION_ID.getIntValue()) != null){
						aaaSessionId = radiusVendorAttribute.getVendorTypeAttribute().getAttribute(WimaxAttrConstants.AAA_SESSION_ID.getIntValue()).getStringValue();
					}
				}
			}
		}
		
		if(sendLastQuota && actionOnTerminateRequest == DummyRatingConfiguration.SEND_NO_QUOTA){
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Request received with update reason >= 4, no PPAQ will be sent in response");
			return;
		}
		
		if(addAAASessionId && aaaSessionId != null){
			IRadiusAttribute aaaSessionIdAttr = Dictionary.getInstance().getAttribute(lVendorId, WimaxAttrConstants.AAA_SESSION_ID.getIntValue());
			aaaSessionIdAttr.setStringValue(aaaSessionId);
			response.addAttribute(aaaSessionIdAttr);
		}
		
		WimaxGroupedAttribute ppaq = (WimaxGroupedAttribute)Dictionary.getInstance().getAttribute(lVendorId, WimaxAttrConstants.PPAQ.getIntValue());
		
		byte[] attributeValue = null;
		
		BaseRadiusAttribute quotaId = (BaseRadiusAttribute)Dictionary.getInstance().getAttribute(lVendorId, WimaxAttrConstants.PPAQ.getIntValue(), PPAQ.QUOTA_IDENTIFIER.getIntValue());
		String qId = (String)dummyRatingConfiguration.getDummyRatingConfiguration().get(DummyRatingConfiguration.QUOTA_ID);
		quotaId.setStringValue(qId);
		attributeValue = RadiusUtility.appendBytes(attributeValue, quotaId.getBytes());
		
		if(aaaSessionIdIterationsMap.containsKey(aaaSessionId)){
			iteration = (Integer)aaaSessionIdIterationsMap.get(aaaSessionId);
		}
		
		String strQuota = (String)dummyRatingConfiguration.getDummyRatingConfiguration().get(DummyRatingConfiguration.QUOTA);
		int iQuota = 0;
		if(strQuota.contains(".")){
			iQuota = getVolumeFromString(strQuota, aaaSessionId);
		}else if(strQuota.length() > 0){
			iQuota = Integer.parseInt(strQuota);
		}
		
		if(iQuota == 0){
			response.setPacketType(RadiusConstants.ACCESS_REJECT_MESSAGE);
			response.setResponseMessage(AuthReplyMessageConstant.INITIAL_RESERVATION_NOT_AVAILABLE);
			response.setFurtherProcessingRequired(false);
			response.setProcessingCompleted(true);
			return;
		}
		
		if(aaaSessionIdQuotaMap.containsKey(aaaSessionId)){
			if(iteration > 0 && !sendLastQuota){
				iQuota += (Integer)aaaSessionIdQuotaMap.get(aaaSessionId);
				aaaSessionIdQuotaMap.put(aaaSessionId, iQuota);
				iteration --;
				aaaSessionIdIterationsMap.put(aaaSessionId, iteration);
			}else{
				iQuota = (Integer)aaaSessionIdQuotaMap.get(aaaSessionId);
				aaaSessionIdQuotaMap.remove(aaaSessionId);
			}
		}else{
			aaaSessionIdIterationsMap.put(aaaSessionId, prepaidIteration);
			aaaSessionIdQuotaMap.put(aaaSessionId, iQuota);
		}
		
		if(iteration == 0){
			termination = true;
		}
		
		int iThreshold = (thresholdProportion * iQuota) / 100;
		
		int iExponent = 0;
		if(aaaSessionIdExponentMap.containsKey(aaaSessionId)){
			iExponent = (Integer)aaaSessionIdExponentMap.get(aaaSessionId) * -1;
		}
		
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
 			LogManager.getLogger().debug(MODULE, "Quota reserved for existing AAA session ids: " + aaaSessionIdQuotaMap);
		
		BaseRadiusAttribute quota = null;
		BaseRadiusAttribute threshold = null;
		BaseRadiusAttribute terminationAction = null;
		
		String chargingType = (String)dummyRatingConfiguration.getDummyRatingConfiguration().get(DummyRatingConfiguration.CHARGING_TYPE);
		
		if(chargingType.equalsIgnoreCase("duration")){
			quota = (BaseRadiusAttribute)Dictionary.getInstance().getAttribute(lVendorId, WimaxAttrConstants.PPAQ.getIntValue(), PPAQ.DURATION_QUOTA.getIntValue());
			quota.setIntValue(iQuota);
			attributeValue = RadiusUtility.appendBytes(attributeValue, quota.getBytes());
			
			if(termination){
				terminationAction = (BaseRadiusAttribute)Dictionary.getInstance().getAttribute(lVendorId, WimaxAttrConstants.PPAQ.getIntValue(), PPAQ.TERMINATION_ACTION.getIntValue());
//				terminationAction.setIntValue(Dictionary.getInstance().getVendorSubAttributeIntValue(lVendorId, WimaxAttrConstants.PPAQ, WimaxAttrConstants.TERMINATION_ACTION, "Terminate"));
				terminationAction.setIntValue(termination_Action);
				attributeValue = RadiusUtility.appendBytes(attributeValue, terminationAction.getBytes());
			}else{
				threshold = (BaseRadiusAttribute)Dictionary.getInstance().getAttribute(lVendorId, WimaxAttrConstants.PPAQ.getIntValue(), PPAQ.DURATION_THRESHOLD.getIntValue());
				threshold.setIntValue(iThreshold);
				attributeValue = RadiusUtility.appendBytes(attributeValue, threshold.getBytes());
			}
		}else if(chargingType.equalsIgnoreCase("volume")){
			quota = (BaseRadiusAttribute)Dictionary.getInstance().getAttribute(lVendorId, WimaxAttrConstants.PPAQ.getIntValue(), PPAQ.VOLUME_QUOTA.getIntValue());
			if(quota instanceof PrepaidTLVAttribute){
				((PrepaidTLVAttribute)quota).setValue(iQuota,0);
				if(iExponent != 0)
					((PrepaidTLVAttribute)quota).setValue(iQuota,iExponent);
			}else{
				quota.setIntValue(iQuota);
			}
			
			attributeValue = RadiusUtility.appendBytes(attributeValue, quota.getBytes());
			
			if(termination){
				terminationAction = (BaseRadiusAttribute)Dictionary.getInstance().getAttribute(lVendorId, WimaxAttrConstants.PPAQ.getIntValue(), PPAQ.TERMINATION_ACTION.getIntValue());
				terminationAction.setIntValue(termination_Action);
				attributeValue = RadiusUtility.appendBytes(attributeValue, terminationAction.getBytes());
			}else{
				threshold = (BaseRadiusAttribute)Dictionary.getInstance().getAttribute(lVendorId, WimaxAttrConstants.PPAQ.getIntValue(), PPAQ.VOLUME_THRESHOLD.getIntValue());
				if(threshold instanceof PrepaidTLVAttribute){
					((PrepaidTLVAttribute)threshold).setValue(iThreshold,0);
					if(iExponent != 0)
						((PrepaidTLVAttribute)threshold).setValue(iThreshold,iExponent);
				}else{
					threshold.setIntValue(iThreshold);
				}
				attributeValue = RadiusUtility.appendBytes(attributeValue, threshold.getBytes());
				
			}
		}
		
		ppaq.addTLVAttribute(quotaId);
		ppaq.addTLVAttribute(quota);
		if(terminationAction != null){
			ppaq.addTLVAttribute(terminationAction);
		}else if(threshold != null){
			ppaq.addTLVAttribute(threshold);
		}
		ppaq.setValueBytes(attributeValue);
		response.addAttribute(ppaq);
	}
	
	private void addPPAQForHaRequest(RadAuthRequest request, RadAuthResponse response){

		long lVendorId = RadiusConstants.WIMAX_VENDOR_ID;
		String aaaSessionId = null;
		Boolean sendLastQuota = false;
		Boolean termination = false;
		Boolean addAAASessionId = false;
		int iteration = prepaidIteration;
		
//		 To get the AAA-Session-Id from the response packet, to check if a reservation for that particular session exists or not.
		IRadiusAttribute aaaSessionIdAttribute = response.getRadiusAttribute(RadiusConstants.WIMAX_VENDOR_ID, WimaxAttrConstants.AAA_SESSION_ID.getIntValue());
		if(aaaSessionIdAttribute != null){
			aaaSessionId = aaaSessionIdAttribute.getStringValue();
		}
		
		if(aaaSessionId == null){
			addAAASessionId = true;
		}
		
		// To check the reason for which this request is received, in case of authorize-only request.
		if(request.getRadiusAttribute(RadiusAttributeConstants.VENDOR_SPECIFIC) != null){
			ArrayList<IRadiusAttribute> vendorSpecificAttributes = (ArrayList<IRadiusAttribute>)request.getRadiusAttributes(RadiusAttributeConstants.VENDOR_SPECIFIC);
			Iterator<IRadiusAttribute> vendorSpecificItr = vendorSpecificAttributes.iterator();
			
			while(vendorSpecificItr.hasNext()){
				VendorSpecificAttribute radiusVendorAttribute = (VendorSpecificAttribute)vendorSpecificItr.next();
				if(radiusVendorAttribute.getVendorID() == RadiusConstants.WIMAX_VENDOR_ID){
					if(radiusVendorAttribute.getVendorTypeAttribute().getAttribute(WimaxAttrConstants.PPAQ.getIntValue()) != null){
						WimaxGroupedAttribute receivedPPAQ = (WimaxGroupedAttribute)radiusVendorAttribute.getVendorTypeAttribute().getAttribute(WimaxAttrConstants.PPAQ.getIntValue());
						List ppaqSubAttributes = receivedPPAQ.getTLVAttrList();
						if(ppaqSubAttributes!= null && !ppaqSubAttributes.isEmpty()){
							final int size = ppaqSubAttributes.size();
							for(int i = 0 ; i < size ; i ++){
								BaseRadiusAttribute subAttribute = (BaseRadiusAttribute)ppaqSubAttributes.get(i);
								if(subAttribute.getType() == PPAQ.UPDATE_REASON.getIntValue() && subAttribute.getValueBytes()[0] >= (byte)4){
									sendLastQuota = true;
								}
							}
						}
					}
					if(radiusVendorAttribute.getVendorTypeAttribute().getAttribute(WimaxAttrConstants.AAA_SESSION_ID.getIntValue()) != null){
						aaaSessionId = radiusVendorAttribute.getVendorTypeAttribute().getAttribute(WimaxAttrConstants.AAA_SESSION_ID.getIntValue()).getStringValue();
					}
				}
			}
		}
		
		if(sendLastQuota && actionOnTerminateRequest == DummyRatingConfiguration.SEND_NO_QUOTA){
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Request received with update reason >= 4, no PPAQ will be sent in response");
			return;
		}
		
		if(addAAASessionId && aaaSessionId != null){
			IRadiusAttribute aaaSessionIdAttr = Dictionary.getInstance().getAttribute(lVendorId, WimaxAttrConstants.AAA_SESSION_ID.getIntValue());
			aaaSessionIdAttr.setStringValue(aaaSessionId);
			response.addAttribute(aaaSessionIdAttr);
		}
		
		WimaxGroupedAttribute ppaq = (WimaxGroupedAttribute)Dictionary.getInstance().getAttribute(lVendorId, WimaxAttrConstants.PPAQ.getIntValue());
		
		byte[] attributeValue = null;
		
		BaseRadiusAttribute quotaId = (BaseRadiusAttribute)Dictionary.getInstance().getAttribute(lVendorId,WimaxAttrConstants.PPAQ.getIntValue(), PPAQ.QUOTA_IDENTIFIER.getIntValue());
		String qId = (String)dummyRatingConfiguration.getDummyRatingConfiguration().get(DummyRatingConfiguration.QUOTA_ID);
		quotaId.setStringValue(qId);
		attributeValue = RadiusUtility.appendBytes(attributeValue, quotaId.getBytes());
		
		if(aaaSessionIdIterationsMap.containsKey(aaaSessionId)){
			iteration = (Integer)aaaSessionIdIterationsMap.get(aaaSessionId);
		}
		
		String strQuota = (String)dummyRatingConfiguration.getDummyRatingConfiguration().get(DummyRatingConfiguration.QUOTA);
		int iQuota = 0;
		if(strQuota.contains(".")){
			iQuota = getVolumeFromString(strQuota, aaaSessionId);
		}else if(strQuota.length() > 0){
			iQuota = Integer.parseInt(strQuota);
		}
		
		if(iQuota == 0){
			response.setPacketType(RadiusConstants.ACCESS_REJECT_MESSAGE);
			response.setResponseMessage(AuthReplyMessageConstant.INITIAL_RESERVATION_NOT_AVAILABLE);
			response.setFurtherProcessingRequired(false);
			response.setProcessingCompleted(true);
			return;
		}
	
		if(iteration == 0){
			termination = true;
		}
		
		int iThreshold = (thresholdProportion * iQuota) / 100;
		
		int iExponent = 0;
		if(aaaSessionIdExponentMap.containsKey(aaaSessionId)){
			iExponent = (Integer)aaaSessionIdExponentMap.get(aaaSessionId) * -1;
		}
		
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
 			LogManager.getLogger().debug(MODULE, "Quota reserved for existing AAA session ids: " + aaaSessionIdQuotaMap);
		
		BaseRadiusAttribute quota = null;
		BaseRadiusAttribute threshold = null;
		BaseRadiusAttribute terminationAction = null;
		
		String chargingType = (String)dummyRatingConfiguration.getDummyRatingConfiguration().get(DummyRatingConfiguration.CHARGING_TYPE);
		
		if(chargingType.equalsIgnoreCase("duration")){
			quota = (BaseRadiusAttribute)Dictionary.getInstance().getAttribute(lVendorId,WimaxAttrConstants.PPAQ.getIntValue(), PPAQ.DURATION_QUOTA.getIntValue());
			quota.setIntValue(iQuota);
			attributeValue = RadiusUtility.appendBytes(attributeValue, quota.getBytes());
			
			if(termination){
				terminationAction = (BaseRadiusAttribute)Dictionary.getInstance().getAttribute(lVendorId, WimaxAttrConstants.PPAQ.getIntValue(), PPAQ.TERMINATION_ACTION.getIntValue());
//				terminationAction.setIntValue(Dictionary.getInstance().getVendorSubAttributeIntValue(lVendorId, WimaxAttrConstants.PPAQ, WimaxAttrConstants.TERMINATION_ACTION, "Terminate"));
				terminationAction.setIntValue(termination_Action);
				attributeValue = RadiusUtility.appendBytes(attributeValue, terminationAction.getBytes());
			}else{
				threshold = (BaseRadiusAttribute)Dictionary.getInstance().getAttribute(lVendorId, WimaxAttrConstants.PPAQ.getIntValue(), PPAQ.DURATION_THRESHOLD.getIntValue());
				threshold.setIntValue(iThreshold);
				attributeValue = RadiusUtility.appendBytes(attributeValue, threshold.getBytes());
			}
		}else if(chargingType.equalsIgnoreCase("volume")){
			quota = (BaseRadiusAttribute)Dictionary.getInstance().getAttribute(lVendorId, PPAQ.VOLUME_QUOTA.getIntValue());
			if(quota instanceof PrepaidTLVAttribute){
				((PrepaidTLVAttribute)quota).setValue(iQuota,0);
				if(iExponent != 0)
					((PrepaidTLVAttribute)quota).setValue(iQuota,iExponent);
			}else{
				quota.setIntValue(iQuota);
			}
			
			attributeValue = RadiusUtility.appendBytes(attributeValue, quota.getBytes());
			
			if(termination){
				terminationAction = (BaseRadiusAttribute)Dictionary.getInstance().getAttribute(lVendorId, WimaxAttrConstants.PPAQ.getIntValue(), PPAQ.TERMINATION_ACTION.getIntValue());
				terminationAction.setIntValue(termination_Action);
				attributeValue = RadiusUtility.appendBytes(attributeValue, terminationAction.getBytes());
			}else{
				threshold = (BaseRadiusAttribute)Dictionary.getInstance().getAttribute(lVendorId, WimaxAttrConstants.PPAQ.getIntValue(), PPAQ.VOLUME_THRESHOLD.getIntValue());
				if(threshold instanceof PrepaidTLVAttribute){
					((PrepaidTLVAttribute)threshold).setValue(iThreshold,0);
					if(iExponent != 0)
						((PrepaidTLVAttribute)threshold).setValue(iThreshold,iExponent);
				}else{
					threshold.setIntValue(iThreshold);
				}
				attributeValue = RadiusUtility.appendBytes(attributeValue, threshold.getBytes());
				
			}
		}
		
		ppaq.addTLVAttribute(quotaId);
		ppaq.addTLVAttribute(quota);
		if(terminationAction != null){
			ppaq.addTLVAttribute(terminationAction);
		}else if(threshold != null){
			ppaq.addTLVAttribute(threshold);
		}
		ppaq.setValueBytes(attributeValue);
		response.addAttribute(ppaq);
	}

	private void addQosParameters(RadAuthRequest request, RadAuthResponse response){

		HashMap<?, ?> qosParameterMap = (HashMap<?, ?>)dummyRatingConfiguration.getDummyRatingConfiguration().get(DummyRatingConfiguration.QOS);
		long lVendorId = RadiusConstants.WIMAX_VENDOR_ID;
		
		WimaxGroupedAttribute qos = (WimaxGroupedAttribute)Dictionary.getInstance().getAttribute(RadiusConstants.WIMAX_VENDOR_ID, WimaxAttrConstants.QOS_DESCRIPTOR.getIntValue());
		byte[] attributeValue = null;
		BaseRadiusAttribute qosSubAttribute = null;
		
		if(qosParameterMap.containsKey(DummyRatingConfiguration.QOSID)){
			qosSubAttribute = (BaseRadiusAttribute)Dictionary.getInstance().getAttribute(lVendorId, WimaxAttrConstants.QOS_DESCRIPTOR.getIntValue(), QoSDescriptor.QOS_ID.getIntValue());
			String qosId = (String)qosParameterMap.get(DummyRatingConfiguration.QOSID);
			if(qosId.length() > 0)
				qosSubAttribute.setIntValue(Integer.parseInt(qosId));
			else
				qosSubAttribute.setIntValue(0);
			attributeValue = RadiusUtility.appendBytes(attributeValue, qosSubAttribute.getBytes());
			qos.addTLVAttribute(qosSubAttribute);
		}
		
		if(qosParameterMap.containsKey(DummyRatingConfiguration.GLOBAL_SERVICE_CLASS_NAME)){
			qosSubAttribute = (BaseRadiusAttribute)Dictionary.getInstance().getAttribute(lVendorId, WimaxAttrConstants.QOS_DESCRIPTOR.getIntValue(), QoSDescriptor.GLOBAL_SERIVCE_CLASS_NAME.getIntValue());
			String globalServiceClassName = (String)qosParameterMap.get(DummyRatingConfiguration.GLOBAL_SERVICE_CLASS_NAME);
			if(globalServiceClassName.length() > 0)
				qosSubAttribute.setStringValue(globalServiceClassName);
			else
				qosSubAttribute.setStringValue("");
			attributeValue = RadiusUtility.appendBytes(attributeValue, qosSubAttribute.getBytes());
			qos.addTLVAttribute(qosSubAttribute);
		}
		
		if(qosParameterMap.containsKey(DummyRatingConfiguration.SERVICE_CLASS_NAME)){
			qosSubAttribute = (BaseRadiusAttribute)Dictionary.getInstance().getAttribute(lVendorId, WimaxAttrConstants.QOS_DESCRIPTOR.getIntValue(), QoSDescriptor.SERVICE_CLASS_NAME.getIntValue());
			String serviceClassName = (String)qosParameterMap.get(DummyRatingConfiguration.SERVICE_CLASS_NAME);
			if(serviceClassName.length() > 0)
				qosSubAttribute.setStringValue(serviceClassName);
			else
				qosSubAttribute.setStringValue("");
			attributeValue = RadiusUtility.appendBytes(attributeValue, qosSubAttribute.getBytes());
			qos.addTLVAttribute(qosSubAttribute);
		}
		
		if(qosParameterMap.containsKey(DummyRatingConfiguration.SCHEDULE_TYPE)){
			qosSubAttribute = (BaseRadiusAttribute)Dictionary.getInstance().getAttribute(lVendorId, WimaxAttrConstants.QOS_DESCRIPTOR.getIntValue(), QoSDescriptor.SCHEDULE_TYPE.getIntValue());
			String scheduleType = (String)qosParameterMap.get(DummyRatingConfiguration.SCHEDULE_TYPE);
			if(scheduleType.length() > 0)
				qosSubAttribute.setIntValue(Integer.parseInt(scheduleType));
			else
//				qosSubAttribute.setIntValue(Dictionary.getInstance().getVendorSubAttributeIntValue(lVendorId, WimaxAttrConstants.QOS_DESCRIPTOR, WimaxAttrConstants.SCHEDULE_TYPE, "Best Effort"));
				qosSubAttribute.setIntValue(2);
			attributeValue = RadiusUtility.appendBytes(attributeValue, qosSubAttribute.getBytes());
			qos.addTLVAttribute(qosSubAttribute);
		}
		
		if(qosParameterMap.containsKey(DummyRatingConfiguration.TRAFFIC_PRIORITY)){
			qosSubAttribute = (BaseRadiusAttribute)Dictionary.getInstance().getAttribute(lVendorId, WimaxAttrConstants.QOS_DESCRIPTOR.getIntValue(), QoSDescriptor.TRAFFIC_PRIORITY.getIntValue());
			String trafficPriority = (String)qosParameterMap.get(DummyRatingConfiguration.TRAFFIC_PRIORITY);
			if(trafficPriority.length() > 0)
				qosSubAttribute.setIntValue(Integer.parseInt(trafficPriority));
			else
				qosSubAttribute.setIntValue(0);
			attributeValue = RadiusUtility.appendBytes(attributeValue, qosSubAttribute.getBytes());
			qos.addTLVAttribute(qosSubAttribute);
		}
		
		if(qosParameterMap.containsKey(DummyRatingConfiguration.MAX_TRAFFIC_RATE)){
			qosSubAttribute = (BaseRadiusAttribute)Dictionary.getInstance().getAttribute(lVendorId, WimaxAttrConstants.QOS_DESCRIPTOR.getIntValue(), QoSDescriptor.MAXIMUM_SUSTAINED_TRAFFIC_RATE.getIntValue());
			String trafficRate = (String)qosParameterMap.get(DummyRatingConfiguration.MAX_TRAFFIC_RATE);
			if(trafficRate.length() > 0)
				qosSubAttribute.setIntValue(Integer.parseInt(trafficRate));
			else
				qosSubAttribute.setIntValue(0);
			attributeValue = RadiusUtility.appendBytes(attributeValue, qosSubAttribute.getBytes());
			qos.addTLVAttribute(qosSubAttribute);
		}
		
		if(qosParameterMap.containsKey(DummyRatingConfiguration.MIN_TRAFFIC_RATE)){
			qosSubAttribute = (BaseRadiusAttribute)Dictionary.getInstance().getAttribute(lVendorId, WimaxAttrConstants.QOS_DESCRIPTOR.getIntValue(), QoSDescriptor.MINIMUM_RESERVED_TRAFFIC_RATE.getIntValue());
			String trafficRate = (String)qosParameterMap.get(DummyRatingConfiguration.MIN_TRAFFIC_RATE);
			if(trafficRate.length() > 0)
				qosSubAttribute.setIntValue(Integer.parseInt(trafficRate));
			else
				qosSubAttribute.setIntValue(0);
			attributeValue = RadiusUtility.appendBytes(attributeValue, qosSubAttribute.getBytes());
			qos.addTLVAttribute(qosSubAttribute);
		}
		
		if(qosParameterMap.containsKey(DummyRatingConfiguration.MAX_TRAFFIC_BURST)){
			qosSubAttribute = (BaseRadiusAttribute)Dictionary.getInstance().getAttribute(lVendorId, WimaxAttrConstants.QOS_DESCRIPTOR.getIntValue(), QoSDescriptor.MAXIMUM_TRAFFIC_BURST.getIntValue());
			String trafficBurst = (String)qosParameterMap.get(DummyRatingConfiguration.MAX_TRAFFIC_BURST);
			if(trafficBurst.length() > 0)
				qosSubAttribute.setIntValue(Integer.parseInt(trafficBurst));
			else
				qosSubAttribute.setIntValue(0);
			attributeValue = RadiusUtility.appendBytes(attributeValue, qosSubAttribute.getBytes());
			qos.addTLVAttribute(qosSubAttribute);
		}
		
		if(qosParameterMap.containsKey(DummyRatingConfiguration.TOLERATED_JITTER)){
			qosSubAttribute = (BaseRadiusAttribute)Dictionary.getInstance().getAttribute(lVendorId, WimaxAttrConstants.QOS_DESCRIPTOR.getIntValue(), QoSDescriptor.TOLERATED_JITTER.getIntValue());
			String toleratedJitter = (String)qosParameterMap.get(DummyRatingConfiguration.TOLERATED_JITTER);
			if(toleratedJitter.length() > 0)
				qosSubAttribute.setIntValue(Integer.parseInt(toleratedJitter));
			else
				qosSubAttribute.setIntValue(0);
			attributeValue = RadiusUtility.appendBytes(attributeValue, qosSubAttribute.getBytes());
			qos.addTLVAttribute(qosSubAttribute);
		}
		
		if(qosParameterMap.containsKey(DummyRatingConfiguration.MAX_LATENCY)){
			qosSubAttribute = (BaseRadiusAttribute)Dictionary.getInstance().getAttribute(lVendorId, WimaxAttrConstants.QOS_DESCRIPTOR.getIntValue(), QoSDescriptor.MAXIMUM_LATENCY.getIntValue());
			String maxLatency = (String)qosParameterMap.get(DummyRatingConfiguration.MAX_LATENCY);
			if(maxLatency.length() > 0)
				qosSubAttribute.setIntValue(Integer.parseInt(maxLatency));
			else
				qosSubAttribute.setIntValue(0);
			attributeValue = RadiusUtility.appendBytes(attributeValue, qosSubAttribute.getBytes());
			qos.addTLVAttribute(qosSubAttribute);
		}
		
		if(qosParameterMap.containsKey(DummyRatingConfiguration.REDUCED_RESOURCE_CODE)){
			qosSubAttribute = (BaseRadiusAttribute)Dictionary.getInstance().getAttribute(lVendorId, WimaxAttrConstants.QOS_DESCRIPTOR.getIntValue(), QoSDescriptor.REDUCED_RESOURCE_CODE.getIntValue());
			String reducedResourceCode = (String)qosParameterMap.get(DummyRatingConfiguration.REDUCED_RESOURCE_CODE);
			if(reducedResourceCode.length() > 0)
				qosSubAttribute.setIntValue(Integer.parseInt(reducedResourceCode));
			else
				qosSubAttribute.setIntValue(0);
			attributeValue = RadiusUtility.appendBytes(attributeValue, qosSubAttribute.getBytes());
			qos.addTLVAttribute(qosSubAttribute);
		}
		
		if(qosParameterMap.containsKey(DummyRatingConfiguration.MEDIA_FLOW_TYPE)){
			qosSubAttribute = (BaseRadiusAttribute)Dictionary.getInstance().getAttribute(lVendorId, WimaxAttrConstants.QOS_DESCRIPTOR.getIntValue(), QoSDescriptor.MEDIA_FLOW_TYPE.getIntValue());
			String mediaFlowType = (String)qosParameterMap.get(DummyRatingConfiguration.MEDIA_FLOW_TYPE);
			if(mediaFlowType.length() > 0)
				qosSubAttribute.setStringValue(mediaFlowType);
			else
				qosSubAttribute.setStringValue("");
			attributeValue = RadiusUtility.appendBytes(attributeValue, qosSubAttribute.getBytes());
			qos.addTLVAttribute(qosSubAttribute);
		}
		
		if(qosParameterMap.containsKey(DummyRatingConfiguration.UNSOLICITED_GRANT_INTERVAL)){
			qosSubAttribute = (BaseRadiusAttribute)Dictionary.getInstance().getAttribute(lVendorId, WimaxAttrConstants.QOS_DESCRIPTOR.getIntValue(), QoSDescriptor.UNSOLICITED_GRANT_INTERVAL.getIntValue());
			String grantInterval = (String)qosParameterMap.get(DummyRatingConfiguration.UNSOLICITED_GRANT_INTERVAL);
			if(grantInterval.length() > 0)
				qosSubAttribute.setIntValue(Integer.parseInt(grantInterval));
			else
				qosSubAttribute.setIntValue(0);
			attributeValue = RadiusUtility.appendBytes(attributeValue, qosSubAttribute.getBytes());
			qos.addTLVAttribute(qosSubAttribute);
		}
		
		if(qosParameterMap.containsKey(DummyRatingConfiguration.SDU_SIZE)){
			qosSubAttribute = (BaseRadiusAttribute)Dictionary.getInstance().getAttribute(lVendorId, WimaxAttrConstants.QOS_DESCRIPTOR.getIntValue(), QoSDescriptor.SDU_SIZE.getIntValue());
			String sduSize = (String)qosParameterMap.get(DummyRatingConfiguration.SDU_SIZE);
			if(sduSize.length() > 0)
				qosSubAttribute.setIntValue(Integer.parseInt(sduSize));
			else
				qosSubAttribute.setIntValue(0);
			attributeValue = RadiusUtility.appendBytes(attributeValue, qosSubAttribute.getBytes());
			qos.addTLVAttribute(qosSubAttribute);
		}
		
		if(qosParameterMap.containsKey(DummyRatingConfiguration.UNSOLICITED_POLLING_INTERVAL)){
			qosSubAttribute = (BaseRadiusAttribute)Dictionary.getInstance().getAttribute(lVendorId, WimaxAttrConstants.QOS_DESCRIPTOR.getIntValue(), QoSDescriptor.UNSOLICITED_POLLING_INTERVAL.getIntValue());
			String pollingInterval = (String)qosParameterMap.get(DummyRatingConfiguration.UNSOLICITED_POLLING_INTERVAL);
			if(pollingInterval.length() > 0)
				qosSubAttribute.setIntValue(Integer.parseInt(pollingInterval));
			else
				qosSubAttribute.setIntValue(0);
			attributeValue = RadiusUtility.appendBytes(attributeValue, qosSubAttribute.getBytes());
			qos.addTLVAttribute(qosSubAttribute);
		}
		
		qos.setValueBytes(attributeValue);
		
		response.addAttribute(qos);
	
	}
	
	private void addPacketFlowParameters(RadAuthRequest request, RadAuthResponse response){

		HashMap<?, ?> packetFlowParameterMap = (HashMap<?, ?>)dummyRatingConfiguration.getDummyRatingConfiguration().get(DummyRatingConfiguration.PACKET_FLOW_DESCRIPTOR);
		long lVendorId = RadiusConstants.WIMAX_VENDOR_ID;
		
		WimaxGroupedAttribute packetFlowDescriptor = (WimaxGroupedAttribute)Dictionary.getInstance().getAttribute(RadiusConstants.WIMAX_VENDOR_ID, WimaxAttrConstants.PACKET_FLOW_DESCRIPTOR.getIntValue());
		byte[] attributeValue = null;
		BaseRadiusAttribute packetFlowSubAttribute = null;
		
		if(packetFlowParameterMap.containsKey(DummyRatingConfiguration.PDFID)){
			packetFlowSubAttribute = (BaseRadiusAttribute)Dictionary.getInstance().getAttribute(lVendorId, WimaxAttrConstants.PACKET_FLOW_DESCRIPTOR.getIntValue(), PacketFlowDescriptor.PACKET_DATA_FLOW_ID.getIntValue());
			String pdfid = (String)packetFlowParameterMap.get(DummyRatingConfiguration.PDFID);
			if(pdfid.length() > 0)
				packetFlowSubAttribute.setIntValue(Integer.parseInt(pdfid));
			else
				packetFlowSubAttribute.setIntValue(1);
			attributeValue = RadiusUtility.appendBytes(attributeValue, packetFlowSubAttribute.getBytes());
			packetFlowDescriptor.addTLVAttribute(packetFlowSubAttribute);
		}
		
		if(packetFlowParameterMap.containsKey(DummyRatingConfiguration.SDFID)){
			packetFlowSubAttribute = (BaseRadiusAttribute)Dictionary.getInstance().getAttribute(lVendorId, WimaxAttrConstants.PACKET_FLOW_DESCRIPTOR.getIntValue(), PacketFlowDescriptor.SERVICE_DATA_FLOW_ID.getIntValue());
			String sdfid = (String)packetFlowParameterMap.get(DummyRatingConfiguration.SDFID);
			if(sdfid.length() > 0)
				packetFlowSubAttribute.setIntValue(Integer.parseInt(sdfid));
			else
				packetFlowSubAttribute.setIntValue(1);
			attributeValue = RadiusUtility.appendBytes(attributeValue, packetFlowSubAttribute.getBytes());
			packetFlowDescriptor.addTLVAttribute(packetFlowSubAttribute);
		}
		
		if(packetFlowParameterMap.containsKey(DummyRatingConfiguration.SERVICE_PROFILE_ID)){
			packetFlowSubAttribute = (BaseRadiusAttribute)Dictionary.getInstance().getAttribute(lVendorId, WimaxAttrConstants.PACKET_FLOW_DESCRIPTOR.getIntValue(), PacketFlowDescriptor.SERVICE_PROFILE_ID.getIntValue());
			String serviceProfileId = (String)packetFlowParameterMap.get(DummyRatingConfiguration.SERVICE_PROFILE_ID);
			if(serviceProfileId.length() > 0)
				packetFlowSubAttribute.setIntValue(Integer.parseInt(serviceProfileId));
			else
				packetFlowSubAttribute.setIntValue(1);
			attributeValue = RadiusUtility.appendBytes(attributeValue, packetFlowSubAttribute.getBytes());
			packetFlowDescriptor.addTLVAttribute(packetFlowSubAttribute);
		}
		
		if(packetFlowParameterMap.containsKey(DummyRatingConfiguration.DIRECTION)){
			packetFlowSubAttribute = (BaseRadiusAttribute)Dictionary.getInstance().getAttribute(lVendorId, WimaxAttrConstants.PACKET_FLOW_DESCRIPTOR.getIntValue(), PacketFlowDescriptor.DIRECTION.getIntValue());
			String direction = (String)packetFlowParameterMap.get(DummyRatingConfiguration.DIRECTION);
			if(direction.length() > 0)
				packetFlowSubAttribute.setIntValue(Integer.parseInt(direction));
			else
//				packetFlowSubAttribute.setIntValue(Dictionary.getInstance().getVendorSubAttributeIntValue(lVendorId, WimaxAttrConstants.PACKET_FLOW_DESCRIPTOR, WimaxAttrConstants.DIRECTION, "Bi-directional"));
				packetFlowSubAttribute.setIntValue(3);
			attributeValue = RadiusUtility.appendBytes(attributeValue, packetFlowSubAttribute.getBytes());
			packetFlowDescriptor.addTLVAttribute(packetFlowSubAttribute);
		}
		
		if(packetFlowParameterMap.containsKey(DummyRatingConfiguration.ACTIVATION_TRIGGER)){
			packetFlowSubAttribute = (BaseRadiusAttribute)Dictionary.getInstance().getAttribute(lVendorId, WimaxAttrConstants.PACKET_FLOW_DESCRIPTOR.getIntValue(), PacketFlowDescriptor.ACTIVATION_TRIGGER.getIntValue());
			String activationTrigger = (String)packetFlowParameterMap.get(DummyRatingConfiguration.ACTIVATION_TRIGGER);
			if(activationTrigger.length() > 0)
				packetFlowSubAttribute.setIntValue(Integer.parseInt(activationTrigger));
			else
				packetFlowSubAttribute.setIntValue(4);
			attributeValue = RadiusUtility.appendBytes(attributeValue, packetFlowSubAttribute.getBytes());
			packetFlowDescriptor.addTLVAttribute(packetFlowSubAttribute);
		}
		
		if(packetFlowParameterMap.containsKey(DummyRatingConfiguration.TRANSPORT_TYPE)){
			packetFlowSubAttribute = (BaseRadiusAttribute)Dictionary.getInstance().getAttribute(lVendorId, WimaxAttrConstants.PACKET_FLOW_DESCRIPTOR.getIntValue(), PacketFlowDescriptor.TRANSPORT_TYPE.getIntValue());
			String transportType = (String)packetFlowParameterMap.get(DummyRatingConfiguration.TRANSPORT_TYPE);
			if(transportType.length() > 0)
				packetFlowSubAttribute.setIntValue(Integer.parseInt(transportType));
			else
				packetFlowSubAttribute.setIntValue(1);
			attributeValue = RadiusUtility.appendBytes(attributeValue, packetFlowSubAttribute.getBytes());
			packetFlowDescriptor.addTLVAttribute(packetFlowSubAttribute);
		}
		
		if(packetFlowParameterMap.containsKey(DummyRatingConfiguration.UPLINK_QOS_ID)){
			packetFlowSubAttribute = (BaseRadiusAttribute)Dictionary.getInstance().getAttribute(lVendorId, WimaxAttrConstants.PACKET_FLOW_DESCRIPTOR.getIntValue(), PacketFlowDescriptor.UPLINK_QOS_ID.getIntValue());
			String uplinkQosId = (String)packetFlowParameterMap.get(DummyRatingConfiguration.UPLINK_QOS_ID);
			if(uplinkQosId.length() > 0)
				packetFlowSubAttribute.setIntValue(Integer.parseInt(uplinkQosId));
			else
				packetFlowSubAttribute.setIntValue(1);
			attributeValue = RadiusUtility.appendBytes(attributeValue, packetFlowSubAttribute.getBytes());
			packetFlowDescriptor.addTLVAttribute(packetFlowSubAttribute);
		}
		
		if(packetFlowParameterMap.containsKey(DummyRatingConfiguration.DOWNLINK_QOS_ID)){
			packetFlowSubAttribute = (BaseRadiusAttribute)Dictionary.getInstance().getAttribute(lVendorId, WimaxAttrConstants.PACKET_FLOW_DESCRIPTOR.getIntValue(), PacketFlowDescriptor.DOWNLINK_QOS_ID.getIntValue());
			String downlinkQosId = (String)packetFlowParameterMap.get(DummyRatingConfiguration.DOWNLINK_QOS_ID);
			if(downlinkQosId.length() > 0)
				packetFlowSubAttribute.setIntValue(Integer.parseInt(downlinkQosId));
			else
				packetFlowSubAttribute.setIntValue(1);
			attributeValue = RadiusUtility.appendBytes(attributeValue, packetFlowSubAttribute.getBytes());
			packetFlowDescriptor.addTLVAttribute(packetFlowSubAttribute);
		}
		
		if(packetFlowParameterMap.containsKey(DummyRatingConfiguration.UPLINK_CLASSIFIER)){
			packetFlowSubAttribute = (BaseRadiusAttribute)Dictionary.getInstance().getAttribute(lVendorId, WimaxAttrConstants.PACKET_FLOW_DESCRIPTOR.getIntValue(), PacketFlowDescriptor.UPLINK_CLASSIFIER.getIntValue());
			String uplinkClassifier = (String)packetFlowParameterMap.get(DummyRatingConfiguration.UPLINK_CLASSIFIER);
			if(uplinkClassifier.length() > 0)
				packetFlowSubAttribute.setStringValue(uplinkClassifier);
			else
				packetFlowSubAttribute.setStringValue("");
			attributeValue = RadiusUtility.appendBytes(attributeValue, packetFlowSubAttribute.getBytes());
			packetFlowDescriptor.addTLVAttribute(packetFlowSubAttribute);
		}
		
		if(packetFlowParameterMap.containsKey(DummyRatingConfiguration.DOWNLINK_CLASSIFIER)){
			packetFlowSubAttribute = (BaseRadiusAttribute)Dictionary.getInstance().getAttribute(lVendorId, WimaxAttrConstants.PACKET_FLOW_DESCRIPTOR.getIntValue(), PacketFlowDescriptor.DOWNLINK_CLASSIFIER.getIntValue());
			String downlinkClassifier = (String)packetFlowParameterMap.get(DummyRatingConfiguration.DOWNLINK_CLASSIFIER);
			if(downlinkClassifier.length() > 0)
				packetFlowSubAttribute.setStringValue(downlinkClassifier);
			else
				packetFlowSubAttribute.setStringValue("");
			attributeValue = RadiusUtility.appendBytes(attributeValue, packetFlowSubAttribute.getBytes());
			packetFlowDescriptor.addTLVAttribute(packetFlowSubAttribute);
		}
		
		packetFlowDescriptor.setValueBytes(attributeValue);
		response.addAttribute(packetFlowDescriptor);
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
			if(aaaSessionIdExponentMap.containsKey(aaaSessionId)){
				if(fractionPart.length() > (Integer)aaaSessionIdExponentMap.get(aaaSessionId)){
					aaaSessionIdExponentMap.put(aaaSessionId, fractionPart.length());
				}
			}else{
				aaaSessionIdExponentMap.put(aaaSessionId, fractionPart.length());
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
	
	@Override
	public void init() throws InitializationFailedException {
		if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
			LogManager.getLogger().info(MODULE, "Initializing WiMAX Dummy Rating handler");
		}
		aaaSessionIdQuotaMap = new HashMap<Object, Object>();
    	aaaSessionIdExponentMap = new HashMap<Object, Object>();
    	aaaSessionIdIterationsMap = new HashMap<Object, Object>();
		dummyRatingConfiguration = authServiceContext.getAuthConfiguration().getDummyRatingConfiguration();
		if(dummyRatingConfiguration == null){
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
				LogManager.getLogger().debug(MODULE, "Dummy Rating configuration not found");
			}
			throw new InitializationFailedException("Dummy Rating configuration not found");
		}
		
		try{
			prepaidIteration = Integer.parseInt((String)(dummyRatingConfiguration.getDummyRatingConfiguration().get(DummyRatingConfiguration.ITERATIONS)));
		}catch(NumberFormatException e){
			prepaidIteration = 0;
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "Invalid value for iteration, using default value 0");
		}
		String strThreshold = (String)dummyRatingConfiguration.getDummyRatingConfiguration().get(DummyRatingConfiguration.THRESHOLD);
		if(strThreshold != null && strThreshold.trim().length() > 0){
			try{
				thresholdProportion = Integer.parseInt(strThreshold);
			}catch(NumberFormatException e){
				thresholdProportion = 90;
				if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
					LogManager.getLogger().warn(MODULE, "Invalid value for threshold, using default value 90");
			}
		}
		String strTerminateAction = (String)dummyRatingConfiguration.getDummyRatingConfiguration().get(DummyRatingConfiguration.TERMINATION_ACTION);
		if(strTerminateAction != null && strTerminateAction.trim().length() > 0){
			try{
				termination_Action = Integer.parseInt(strTerminateAction);
			}catch(NumberFormatException ex){
				termination_Action = 1;
				if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
					LogManager.getLogger().warn(MODULE, "Invalid value for termination action, using default value 1");
			}
		}
		String strActionOnTerminate = (String)dummyRatingConfiguration.getDummyRatingConfiguration().get(DummyRatingConfiguration.ACTION_ON_TERMINATE_REQUEST);
		try{
			actionOnTerminateRequest = Integer.parseInt(strActionOnTerminate);
		}catch(NumberFormatException e){
			actionOnTerminateRequest = 0;
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "Invalid value for action on terminate request, using default value 0");
		}
	}
}
