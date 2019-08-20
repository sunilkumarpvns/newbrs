package com.elitecore.aaa.radius.service.auth.policy.handlers;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.radius.service.RadServiceResponse;
import com.elitecore.aaa.radius.service.auth.RadAuthRequest;
import com.elitecore.aaa.radius.service.auth.RadAuthResponse;
import com.elitecore.aaa.radius.service.auth.constant.AuthReplyMessageConstant;
import com.elitecore.aaa.radius.service.auth.handlers.RadAuthServiceHandler;
import com.elitecore.aaa.radius.service.auth.policy.handlers.conf.ResponseAttributeAdditionHandlerData;
import com.elitecore.aaa.radius.service.base.policy.handler.ResponseAttributeAdditionHandlerSupport;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.data.ValueProvider;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;

/**
 * 
 * @author narendra.pathai
 *
 */
public class AuthResponseAttributeAdditionHandler extends ResponseAttributeAdditionHandlerSupport<RadAuthRequest, RadAuthResponse> implements RadAuthServiceHandler {
	
	private final ResponseAttributeAdditionHandlerData data;
	
	public AuthResponseAttributeAdditionHandler(ResponseAttributeAdditionHandlerData data) {
		this.data = data;
	}

	public boolean isEligible(RadAuthRequest request, RadAuthResponse response) {
		return true;
	}

	@Override
	public void reInit() throws InitializationFailedException {

	}

	@Override
	public boolean isResponseBehaviorApplicable() {
		return false;
	}
	
	@Override
	public void handleRequest(RadAuthRequest request, RadAuthResponse response, ISession session) {
		super.handleRequest(request, response, session);
		if (response.isFurtherProcessingRequired()) {
			copyResponseAttributesFromRequest(request, response);
		}
	}

	private void copyResponseAttributesFromRequest(RadAuthRequest request, RadAuthResponse response) {
		copyIPv4Attributes(request, response);
		
		copyIPv6Attributes(request, response);
		
		copyFramedPoolAttribute(request, response);
	}

	private void copyFramedPoolAttribute(RadAuthRequest request, RadAuthResponse response) {
		IRadiusAttribute framedPool = Dictionary.getInstance().getKnownAttribute(RadiusAttributeConstants.FRAMED_POOL);
		if (framedPool == null) {
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Framed-Pool attribute not found in dictionary, will not be added in response.");
			}
			return;
		}
		
		IRadiusAttribute eliteFramedPool = request.getInfoAttribute(RadiusConstants.ELITECORE_VENDOR_ID,
				RadiusAttributeConstants.ELITE_FRAMED_POOL_NAME);
		
		if (eliteFramedPool != null) {
			framedPool.setStringValue(eliteFramedPool.getStringValue());
			response.addAttribute(framedPool);
		} else {
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Elite-Profile-Framed-Pool-Name not found in request as info attribute, " 
						+ "may not have been configured in subscriber profile. So will not be added in response.");
			}
		}
	}

	private void copyIPv6Attributes(RadAuthRequest request, RadAuthResponse response) {
		IRadiusAttribute framedIPV6Prefix = Dictionary.getInstance().getKnownAttribute(RadiusAttributeConstants.FRAMED_IPV6_PREFIX);
		if (framedIPV6Prefix == null) {
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Framed-IPv6-Prefix attribute not found in dictionary, will not be added in response.");
			}
			return;
		}
		
		Collection<IRadiusAttribute> ipv6Prefixes = request.getInfoAttributes(RadiusConstants.STANDARD_VENDOR_ID, RadiusAttributeConstants.FRAMED_IPV6_PREFIX);
		if (Collectionz.isNullOrEmpty(ipv6Prefixes)) {
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Framed-IPv6-Prefix not found in request as info attribute, " 
						+ "may not have been configured in subscriber profile. So will not be added in response.");
			}
			return;
		}
		
		for (IRadiusAttribute ipv6Prefix : ipv6Prefixes) {
			framedIPV6Prefix = Dictionary.getInstance().getKnownAttribute(RadiusAttributeConstants.FRAMED_IPV6_PREFIX);
			framedIPV6Prefix.setStringValue(ipv6Prefix.getStringValue());
			response.addAttribute(framedIPV6Prefix);
		}
	}

	private void copyIPv4Attributes(RadAuthRequest request, RadAuthResponse response) {
		IRadiusAttribute framedIPV4Address = Dictionary.getInstance().getKnownAttribute(RadiusAttributeConstants.FRAMED_IP_ADDRESS);
		if (framedIPV4Address == null) {
			
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Framed-Ip-Address attribute not found in dictionary, will not be added in response.");
			}
			return;
		}
		
		IRadiusAttribute eliteProfileIPv4Address = request.getRadiusAttribute(true, RadiusConstants.STANDARD_VENDOR_ID,RadiusAttributeConstants.FRAMED_IP_ADDRESS);
		
		if (eliteProfileIPv4Address != null) {
			framedIPV4Address.setStringValue(eliteProfileIPv4Address.getStringValue());
			response.addAttribute(framedIPV4Address);
		} else {
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Framed-IP-Address not found in request as info attribute, " 
						+ "may not have been configured in subscriber profile. So will not be added in response.");
			}
		}
		
		IRadiusAttribute framedNetMask = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.FRAMED_IP_NETMASK);
		if (framedNetMask == null) {
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Framed-Ip-Netmask attribute not found in dictionary, will not be added in response.");
			}
			return;
		}
		
		IRadiusAttribute eliteIPNetmask = request.getInfoAttribute(RadiusConstants.STANDARD_VENDOR_ID, RadiusAttributeConstants.FRAMED_IP_NETMASK);
		if (eliteIPNetmask != null) {
			framedNetMask.setStringValue(eliteIPNetmask.getStringValue());
			response.addAttribute(framedNetMask);
		} else {
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, " Framed-IP-Netmask not found in request as info attribute, " 
						+ "may not have been configured in subscriber profile. So will not be added in response.");
			}
		}
	}

	@Override
	protected void applyResponseAttributes(RadServiceRequest request,
			RadServiceResponse response, ValueProvider valueProvider,
			Map<String, List<String>> responseAttributes) {

		IRadiusAttribute responseAttribute=null;
		String strAttributeId = null;

		for(Entry<String, List<String>> attributeIdToValues : responseAttributes.entrySet()){
			strAttributeId = attributeIdToValues.getKey();

			for (String attributeDefaultValue : attributeIdToValues.getValue()) {
				String value = valueProvider.getStringValue(attributeDefaultValue);
				if(value != null){
					responseAttribute = Dictionary.getInstance().getKnownAttribute(strAttributeId);
					if(responseAttribute !=null){
						responseAttribute.setStringValue(value);
						response.addAttribute(responseAttribute);
					}else{
						if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
							LogManager.getLogger().debug(MODULE, "Attribute for Attribute-ID: "+strAttributeId+" not added to response packet.Reason : Attribute not found in dictionary");
					}
				}else{
					response.setFurtherProcessingRequired(false);					
					response.setResponseMessage(AuthReplyMessageConstant.NO_DEFAULTVALUE_FOUND_FOR_RESPONSE_ATTRIBUTE_ID+strAttributeId);				
					response.setPacketType(RadiusConstants.ACCESS_REJECT_MESSAGE);
				}
						}
					}

	}

	@Override
	protected String getConfiguredResponseAttribute() {
		return data.getRadiusServicePolicyData().getAuthResponseAttributes();
	}

}
