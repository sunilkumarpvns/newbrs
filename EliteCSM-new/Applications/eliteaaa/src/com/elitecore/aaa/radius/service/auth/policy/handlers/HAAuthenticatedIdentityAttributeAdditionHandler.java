package com.elitecore.aaa.radius.service.auth.policy.handlers;

import com.elitecore.aaa.core.wimax.WimaxSessionManager;
import com.elitecore.aaa.core.wimax.WimaxSessionData;
import com.elitecore.aaa.radius.service.auth.RadAuthRequest;
import com.elitecore.aaa.radius.service.auth.RadAuthResponse;
import com.elitecore.aaa.radius.service.auth.RadAuthServiceContext;
import com.elitecore.aaa.radius.service.auth.constant.AuthReplyMessageConstant;
import com.elitecore.aaa.radius.service.auth.handlers.RadAuthServiceHandler;
import com.elitecore.aaa.radius.util.RadiusProcessHelper;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;
import com.elitecore.coreradius.commons.util.constants.WimaxAttrConstants;

public class HAAuthenticatedIdentityAttributeAdditionHandler implements RadAuthServiceHandler {
	
	private static final String MODULE = "HA-AUTH-IDENTITY-ADD-HANDLER";
	private RadAuthServiceContext serviceContext;

	public HAAuthenticatedIdentityAttributeAdditionHandler(RadAuthServiceContext serviceContext) {
		this.serviceContext = serviceContext;
	}

	@Override
	public boolean isEligible(RadAuthRequest request, RadAuthResponse response) {
		IRadiusAttribute ha_mip4_spi = request
				.getRadiusAttribute(RadiusConstants.WIMAX_VENDOR_ID, WimaxAttrConstants.MN_HA_MIP4_SPI.getIntValue());

		if (ha_mip4_spi != null) {
			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "MN_HA_MIP4_SPI(24757:11) found in the request. " +
						"So, eligible for HA handling");
			}
			return true;
		}
		if (LogManager.getLogger().isInfoLogLevel()) {
			LogManager.getLogger().info(MODULE, "MN_HA_MIP4_SPI(24757:11) not found in the request."
					+ " So, not eligible for HA handling");
		}
		return false;
	}

	@Override
	public void handleRequest(RadAuthRequest request, RadAuthResponse response, ISession session) {
		if (LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(MODULE, "Request handling started by HA Authenticated Identity addition handler");
		}
		
		IRadiusAttribute authenticatedUserIdentity = Dictionary.getInstance()
				.getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID, RadiusAttributeConstants.ELITE_AUTHENTICATED_USER_ID);
		if (authenticatedUserIdentity == null) {
			LogManager.getLogger().warn(MODULE, "Skipping addition of ELITE_AUTHENTICATED_USER_ID(21067:126) from Wimax session, "
					+ "as it is not found in dictionary");
			return;
		}
		
		IRadiusAttribute usernameAttribtue = request.getRadiusAttribute(RadiusAttributeConstants.USER_NAME);
		if (usernameAttribtue == null) {
			LogManager.getLogger().warn(MODULE, "Skipping addition of ELITE_AUTHENTICATED_USER_ID(21067:126) from Wimax session, "
					+ "as USER_NAME(0:1) attribute not found in the request");
			return;
		}
		
		String username = usernameAttribtue.getStringValue();

		WimaxSessionManager wimaxSession = serviceContext.getServerContext().getWimaxSessionManager();
		
		WimaxSessionData wimaxSessionData = wimaxSession.getWimaxSession(WimaxSessionManager.WIMAX_USERNAME, username);
		if (wimaxSessionData == null) {
			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "Sending Access Reject, " +
						"as WiMAX session data not available for username: " + username);
			}
			RadiusProcessHelper.rejectResponse(response, AuthReplyMessageConstant.WIMAX_SESSION_NOT_FOUND_FOR_HA);
			return;
		}

		setCUIInAuthenticatedUserIdentity(request, authenticatedUserIdentity, wimaxSessionData.getCUI());
	}

	private void setCUIInAuthenticatedUserIdentity(RadAuthRequest request,
			IRadiusAttribute authenticatedUserIdentity, String cui) {

		if (cui == null) {
			if (LogManager.getLogger().isWarnLogLevel()) {
				LogManager.getLogger().warn(MODULE, "ELITE_AUTHENTICATED_USER_ID(21067:126) attribute is not added in the request, " +
						"as CUI not found in wimax session data");
			}
			return;
		}
		
		authenticatedUserIdentity.setStringValue(cui);
		request.addInfoAttribute(authenticatedUserIdentity);
		
		if (LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(MODULE, "ELITE_AUTHENTICATED_USER_ID(21067:126) added successfully in the request");
		}
	}

	@Override
	public boolean isResponseBehaviorApplicable() {
		return false;
	}

	@Override
	public void init() throws InitializationFailedException {
		
	}

	@Override
	public void reInit() throws InitializationFailedException {
		
	}
}
