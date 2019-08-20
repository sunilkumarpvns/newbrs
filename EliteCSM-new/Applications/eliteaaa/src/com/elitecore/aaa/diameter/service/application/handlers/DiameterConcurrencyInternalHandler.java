package com.elitecore.aaa.diameter.service.application.handlers;

import javax.annotation.Nonnull;

import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.service.application.ApplicationResponse;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;

public class DiameterConcurrencyInternalHandler<T extends ApplicationRequest, V extends ApplicationResponse>
implements DiameterApplicationHandler<T, V> {
	private static final String MODULE = "ADD-CONC-INTERNAL-HNDLR";
	public static final String ADDITIONAL_CONCURRENT_LOGIN_POLICY = "ADDITIONAL_CONCURRENT_LOGIN_POLICY";
	
	private final String additionalConcurrentLoginPolicyName;

	public DiameterConcurrencyInternalHandler(@Nonnull String additionalConcurrentLoginPolicyName) {
		this.additionalConcurrentLoginPolicyName = additionalConcurrentLoginPolicyName;
	}
	
	@Override
	public void init() throws InitializationFailedException {
		
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
		IDiameterAVP concurrentLoginPolicyAvp = findConcurrentLoginPolicyAttribute(request, response);
		if (concurrentLoginPolicyAvp == null) {
			LogManager.getLogger().debug(MODULE, "No Concurrent Login Policy AVP found in request or response so skipping further processing.");
			return;
		}
		
		if (LogManager.getLogger().isInfoLogLevel()) {
			LogManager.getLogger().info(MODULE, "Replacing Concurrent-Login-Policy AVP with value of additional concurrent login policy: "
					+ additionalConcurrentLoginPolicyName);
		}
		concurrentLoginPolicyAvp.setStringValue(additionalConcurrentLoginPolicyName);
	}
	
	private IDiameterAVP findConcurrentLoginPolicyAttribute(ApplicationRequest request,
			ApplicationResponse response) {

		IDiameterAVP concurrentLoginPolicyAvp = request.getAVP(DiameterAVPConstants.EC_CONCURRENT_LOGIN_POLICY_NAME, true);
		
		if (concurrentLoginPolicyAvp == null) {
			concurrentLoginPolicyAvp = response.getAVP(DiameterAVPConstants.EC_CONCURRENT_LOGIN_POLICY_NAME, true);
		}
		
		return concurrentLoginPolicyAvp;
	}

	@Override
	public boolean isResponseBehaviorApplicable() {
		return false;
	}

}
