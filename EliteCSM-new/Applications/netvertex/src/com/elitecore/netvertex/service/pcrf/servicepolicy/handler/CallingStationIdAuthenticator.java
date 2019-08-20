package com.elitecore.netvertex.service.pcrf.servicepolicy.handler;

import java.util.StringTokenizer;

import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.diameterapi.diameter.common.util.DiameterUtility;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.servicepolicy.authprotocol.exception.AuthenticationFailedException;

public class CallingStationIdAuthenticator implements Authenticator{
	private static final String MODULE = "CALLING-STATION-ID-AUTH";
	private static final String INVALID_CALLING_STATION_ID = "Invalid Calling-Station-Id";

	public void authenticate(PCRFRequest request) throws AuthenticationFailedException {

		SPRInfo userProfile = request.getSPRInfo();

		String subscriberCallingStationId = userProfile.getCallingStationId();

		if (Strings.isNullOrEmpty(subscriberCallingStationId)) {
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Skipping Calling-Station-Id Authentication. Reason: Calling-Station-Id not found in SPR for Subscriber Identity: " + userProfile.getSubscriberIdentity());
			}
			return;
		}

		String receivedCallingStationId = request.getAttribute(PCRFKeyConstants.CS_CALLING_STATION_ID.getVal());

		if (Strings.isNullOrEmpty(receivedCallingStationId)) {
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Skipping Calling-Station-Id Authentication. Reason: Calling-Station-Id not found in request for Subscriber Identity: " + userProfile.getSubscriberIdentity());
			}
			return;
		}

		if (!matches(subscriberCallingStationId, receivedCallingStationId)) {
			throw new AuthenticationFailedException("Calling-Station-Id did not matched with Profile", INVALID_CALLING_STATION_ID);
		} else {
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Calling-Station-Id matches with profile for Subscriber Identity: " + userProfile.getSubscriberIdentity());
			}
		}

	}
	
	private boolean matches(String subscriberCallingStationId, String receivedCallingStationId) {
		StringTokenizer tokens = new StringTokenizer(subscriberCallingStationId, ",;");

		final int totalTokens = tokens.countTokens();
		for (int cntr = 0 ; cntr<totalTokens; cntr++) {
			if (DiameterUtility.matches(receivedCallingStationId, tokens.nextToken().trim())) {
				return true;
			}
		}
		return false;
	}
}