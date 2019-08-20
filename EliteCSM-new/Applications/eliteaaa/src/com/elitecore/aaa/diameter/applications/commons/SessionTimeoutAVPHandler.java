package com.elitecore.aaa.diameter.applications.commons;

import java.util.List;

import com.elitecore.aaa.core.data.AccountData;
import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.service.application.ApplicationResponse;
import com.elitecore.aaa.diameter.service.application.handlers.DiameterApplicationHandler;
import com.elitecore.aaa.util.constants.AAAServerConstants;
import com.elitecore.commons.annotations.VisibleForTesting;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.TimeSource;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.util.DiameterDictionary;
import com.elitecore.diameterapi.diameter.common.util.constant.CommandCode;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;

/**
 * 
 * This handler checks whether session timeout AVP has to be added in answer.
 * EliteAAA sends the lowest value possible for session timeout.
 * 
 * <p>
 * If session timeout value should be sent then it evaluates the minimum session timeout value out of all
 * the session timeout avps available, to send the lowest of all values.
 * 
 * <p>
 * If subscriber profile contains max session time property and the lowest possible session time calculated
 * from available avps is higher than subscriber profile value, then profile max session time value will be used. 
 * 
 * <p>
 * After this handler, answer will either contain no session timeout avp in case session timeout is disabled,
 * or will contain a single session timeout with minimum possible value.
 * 
 * @author narendra.pathai
 *
 */
// TODO NARENDRA need to document all the behaviors by unit test cases.
public class SessionTimeoutAVPHandler<T extends ApplicationRequest, V extends ApplicationResponse>
implements DiameterApplicationHandler<T, V> {

	private static final String MODULE = "SESSION-TIMEOUT-AVP-HNDLR";
	private final TimeSource timeSource;

	public SessionTimeoutAVPHandler() {
		this(TimeSource.systemTimeSource());
	}
	
	@VisibleForTesting
	SessionTimeoutAVPHandler(TimeSource timeSource) {
		this.timeSource = timeSource;
	}
	
	@Override
	public void reInit() throws InitializationFailedException {
		// no-op
	}

	@Override
	public void init() throws InitializationFailedException {
		// no-op
	}

	@Override
	public boolean isEligible(T request, V response) {
		return request.getDiameterRequest().getCommandCode() == CommandCode.AUTHENTICATION_AUTHORIZATION.code 
				|| request.getDiameterRequest().getCommandCode() == CommandCode.DIAMETER_EAP.code;
	}

	@Override
	public void handleRequest(T request, V response, ISession session) {
		
		if (LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(MODULE, "request processing through session timeout avp handler.");
		}
		
		DiameterAnswer answer = response.getDiameterAnswer();
		
		List<IDiameterAVP> responseSessionTimeOutAttrList = answer.getAVPList(DiameterAVPConstants.SESSION_TIMEOUT, true);
		if (Collectionz.isNullOrEmpty(responseSessionTimeOutAttrList)) {
			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "No Session-Timeout avp found in answer, so will not be added in answer.");
			}
			return;
		}
		
		answer.removeAllAVPs(responseSessionTimeOutAttrList, true);

		Integer sessionTimeoutFromAuthorization = (Integer)request.getParameter(AAAServerConstants.SESSION_TIMEOUT);
		if (sessionTimeoutFromAuthorization != null 
				&& sessionTimeoutFromAuthorization <= AAAServerConstants.SESSION_TIMEOUT_DISABLED) {
			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "Session-Timeout avp will not be added in answer as it is disabled from policy level configuration.");
			}
			return;
		}

		long minimumSessionTime = responseSessionTimeOutAttrList.get(0).getInteger();
		int numOfAttrs = responseSessionTimeOutAttrList.size();
		for (int i = 1; i < numOfAttrs; i++) {
			IDiameterAVP sessionTimeAttr = responseSessionTimeOutAttrList.get(i);
			if (minimumSessionTime > sessionTimeAttr.getInteger()) {
				minimumSessionTime = sessionTimeAttr.getInteger();
			}
		}

		AccountData accountData = request.getAccountData();
		if (accountData != null) {
			if (accountData.getMaxSessionTime() > 0 
					&& accountData.getMaxSessionTime() < minimumSessionTime) {
				if (LogManager.getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE, "Using subscriber profile max session time: " 
							+ accountData.getMaxSessionTime() + " as it is lower than minimum of all session"
							+ " timeout avp values: " + minimumSessionTime);
				}
				minimumSessionTime = accountData.getMaxSessionTime();
			}
			
			if (accountData.isExpiryDateCheckRequired() 
					&& accountData.getExpiryDate() != null 
					&& !accountData.isGracePeriodApplicable()
					&& request.getDiameterRequest().getAVP(DiameterAVPConstants.EC_HOTLINE_REASON, true) == null) {
				long accountExpiryRemainingTime = (accountData.getExpiryDate().getTime() - timeSource.currentTimeInMillis())/1000;
				if (accountExpiryRemainingTime < minimumSessionTime) {
					if (LogManager.getLogger().isDebugLogLevel()) {
						LogManager.getLogger().debug(MODULE, "Using subscriber profile remaining expiry time: " 
								+ accountExpiryRemainingTime + " as it is lower than minimum of all session"
								+ " timeout avp values and profile max session time.");
					}
					minimumSessionTime = accountExpiryRemainingTime;
				}
			}
		}

		if (minimumSessionTime > 0) {
			IDiameterAVP sessionTimeoutAttribute = DiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.SESSION_TIMEOUT);
			sessionTimeoutAttribute.setInteger(minimumSessionTime);
			answer.addAvp(sessionTimeoutAttribute);
			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "Minimum Session-Timeout = " + minimumSessionTime + " will be sent in answer.");
			}
		} else {
			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "Session-Timeout avp will not be added in answer as minimum value is less or equal to 0.");
			}
		}
	}

	@Override
	public boolean isResponseBehaviorApplicable() {
		return false;
	}
}
