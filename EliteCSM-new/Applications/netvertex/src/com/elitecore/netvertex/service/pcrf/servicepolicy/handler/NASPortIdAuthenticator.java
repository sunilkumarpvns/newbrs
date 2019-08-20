package com.elitecore.netvertex.service.pcrf.servicepolicy.handler;

import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.diameterapi.diameter.common.util.DiameterUtility;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.servicepolicy.authprotocol.exception.AuthenticationFailedException;

import java.util.StringTokenizer;

public class NASPortIdAuthenticator implements Authenticator{
	private static final String MODULE = "NAS-PORT-ID-AUTH";
	private static final String INVALID_NAS_PORT_ID = "Invalid NAS-Port-Id";

	public NASPortIdAuthenticator(){}
	
	public void authenticate(PCRFRequest request) throws AuthenticationFailedException {

		SPRInfo userProfile = request.getSPRInfo();
		String subscriberNasPortId = userProfile.getNasPortId();

		if (Strings.isNullOrEmpty(subscriberNasPortId)) {
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Skipping NAS-Port-Id Authentication. Reason: NAS-Port-Id not found in SPR for Subscriber Identity: " + userProfile.getSubscriberIdentity());
			}
			return;
		}

		String receivedNasPortId = request.getAttribute(PCRFKeyConstants.CS_NAS_PORT_ID.getVal());

		if (Strings.isNullOrEmpty(receivedNasPortId)) {
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Skipping NAS-Port-Id Authentication. Reason: NAS-Port-Id not found in request for Subscriber Identity: " + userProfile.getSubscriberIdentity());
			}
			return;
		}

		if ( receivedNasPortId.contains(subscriberNasPortId) == false ) {
			throw new AuthenticationFailedException("NAS-Port-Id match did not found in Request's NAS-Port-ID for Profile", INVALID_NAS_PORT_ID);
		} else {
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "NAS-Port-Id match found in Request's NAS-Port-Id for profile with Subscriber Identity: " + userProfile.getSubscriberIdentity());
			}
		}

	}
	
	private boolean matches(String subscriberNasPortId, String receivedNasPortId) {
		StringTokenizer tokens = new StringTokenizer(subscriberNasPortId, ",;");
		
		for (int cntr = 0 ; cntr<tokens.countTokens() ; cntr++) {
			if (DiameterUtility.matches(receivedNasPortId, tokens.nextToken())) {
				return true;
			}
		}
		return false;
	}
	 	
}