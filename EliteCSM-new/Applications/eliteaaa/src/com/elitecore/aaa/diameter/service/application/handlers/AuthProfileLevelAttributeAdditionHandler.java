package com.elitecore.aaa.diameter.service.application.handlers;

import java.util.List;

import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.service.application.ApplicationResponse;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.util.DiameterDictionary;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;

public class AuthProfileLevelAttributeAdditionHandler <T extends ApplicationRequest, V extends ApplicationResponse>
implements DiameterApplicationHandler<T, V>{

	private static final String MODULE = "PROFILE-ATTR-ADDITION-HNDLR";
	
	private final DiameterDictionary diameterDictionary;

	public AuthProfileLevelAttributeAdditionHandler() {
		this (DiameterDictionary.getInstance());
	}
	
	AuthProfileLevelAttributeAdditionHandler(DiameterDictionary diameterDictionary) {
		this.diameterDictionary = diameterDictionary;
	}

	@Override
	public void init() throws InitializationFailedException {
		LogManager.getLogger().info(MODULE, "Initializing auth profile level attribute addition handler");
	}
	
	
	@Override
	public void reInit() throws InitializationFailedException {
		
	}

	@Override
	public boolean isEligible(T request, V response) {
		return true;
	}

	@Override
	public void handleRequest(T request, V response, ISession session) {
		copyResponseAVPFromRequest(request, response);
	}

	private void copyResponseAVPFromRequest(ApplicationRequest request, ApplicationResponse response) {
		
		copyIPv4AVPs(request, response);
		
		copyIPv6AVPs(request, response);
		
		copyFramedPoolAVP(request, response);
	}

	private void copyIPv4AVPs(ApplicationRequest request, ApplicationResponse response) {
		
		IDiameterAVP framedIPv4Avp = diameterDictionary.getKnownAttribute(DiameterAVPConstants.FRAMED_IP_ADDRESS);
		if (framedIPv4Avp == null) {
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Framed-Ip-Address not found in dictionary, will not be added in response.");
			}
			return;
		}
		
		IDiameterAVP eliteFramedIPv4Avp = request.getAVP(DiameterAVPConstants.FRAMED_IP_ADDRESS, true);
		if (eliteFramedIPv4Avp != null) {
			framedIPv4Avp.setStringValue(eliteFramedIPv4Avp.getStringValue());
			response.addAVP(framedIPv4Avp);
		} else {
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Framed-Ip-Address not found in request, " 
						+ "may not have been configured in profile. So will not be added in response as Framed-IPv4-Address.");
			}
		}
		
		IDiameterAVP framedIPNetMask = diameterDictionary.getKnownAttribute(DiameterAVPConstants.FRAMED_IP_NETMASK);
		if (framedIPNetMask == null) {
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Framed-Ip-Netmask not found in dictionary, will not be added in response.");
			}
			return;
		}
		
		IDiameterAVP eliteFramedIPNetMaskAvp = request.getAVP(DiameterAVPConstants.FRAMED_IP_NETMASK, true);
		if (eliteFramedIPNetMaskAvp != null) {
			framedIPNetMask.setStringValue(eliteFramedIPNetMaskAvp.getStringValue());
			response.addAVP(framedIPNetMask);
		} else {
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Framed-Ip-Netmask not found in request, " 
						+ "may not have been configured in profile. So will not be added in response as Framed-IP-Netmask.");
			}
		}
		
	}
	
	private void copyIPv6AVPs(ApplicationRequest request, ApplicationResponse response) {
		IDiameterAVP framedIPv6PrefixAvp = diameterDictionary.getKnownAttribute(DiameterAVPConstants.FRAMED_IPV6_PREFIX);
		if (framedIPv6PrefixAvp == null) {
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Framed-IPv6-Prefix attribute not found in dictionary, will not be added in response.");
			}
			return;
		}
		
		List<IDiameterAVP> eliteFramedIPv6PrefixAvps = request.getAVPList(DiameterAVPConstants.FRAMED_IPV6_PREFIX, true);
		if (Collectionz.isNullOrEmpty(eliteFramedIPv6PrefixAvps)) {
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Framed-IPv6-Prefix not found in request, " 
						+ "may not have been configured in profile. So will not be added in response as Framed-IPv6-Prefix.");
			}
			return;
		}
		
		for (IDiameterAVP eliteFramedIPv6PrefixAvp : eliteFramedIPv6PrefixAvps) {
			framedIPv6PrefixAvp = diameterDictionary.getKnownAttribute(DiameterAVPConstants.FRAMED_IPV6_PREFIX);
			framedIPv6PrefixAvp.setStringValue(eliteFramedIPv6PrefixAvp.getStringValue());
			response.addAVP(framedIPv6PrefixAvp);
		}
	}
	
	private void copyFramedPoolAVP(ApplicationRequest request, ApplicationResponse response) {
		IDiameterAVP framedPoolAvp = diameterDictionary.getKnownAttribute(DiameterAVPConstants.FRAMED_POOL);
		if (framedPoolAvp == null) {
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Framed-Pool not found in dictionary, will not be added in response.");
			}
			return;
		}
		
		IDiameterAVP eliteFramePoolAvp = request.getInfoAvp(DiameterAVPConstants.EC_FRAMED_POOL_NAME);
		if (eliteFramePoolAvp != null) {
			framedPoolAvp.setStringValue(eliteFramePoolAvp.getStringValue());
			response.addAVP(framedPoolAvp);
		} else {
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Framed-Pool not found in request, " 
						+ "may not have been configured in profile. So will not be added in response as Framed-Pool.");
			}
		}
	}


	@Override
	public boolean isResponseBehaviorApplicable() {
		return false;
	}



}
