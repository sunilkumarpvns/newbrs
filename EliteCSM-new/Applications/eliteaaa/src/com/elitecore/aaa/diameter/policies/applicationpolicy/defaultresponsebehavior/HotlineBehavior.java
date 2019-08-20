package com.elitecore.aaa.diameter.policies.applicationpolicy.defaultresponsebehavior;

import static com.elitecore.commons.base.Preconditions.checkArgument;
import static com.elitecore.commons.base.Preconditions.checkNotNull;

import com.elitecore.aaa.diameter.policies.diameterpolicy.DiameterPolicyManager;
import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.service.application.ApplicationResponse;
import com.elitecore.aaa.diameter.util.DiameterProcessHelper;
import com.elitecore.aaa.diameter.util.HotlineUtility.HotlineProcessor;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.serverx.policies.ParserException;
import com.elitecore.core.serverx.policies.PolicyFailedException;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterErrorMessageConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;

public class HotlineBehavior extends DefaultResponseBehavior {
	
	private static final String MODULE = "HOTLINE-REPONSE-BEHAVIOR";
	private static final ResultCode DEFAULT_RESULT_CODE = ResultCode.DIAMETER_UNABLE_TO_COMPLY;
	private final HotlineProcessor hotlineProcessor;
	private String hotlinePolicy;
	
	public HotlineBehavior(String hotlinePolicy) {
		this(hotlinePolicy, DiameterPolicyManager.getInstance(DiameterPolicyManager.DIAMETER_AUTHORIZATION_POLICY));
	}

	HotlineBehavior(String hotlinePolicy,
			DiameterPolicyManager diameterPolicyManager) {
		this.hotlinePolicy = checkNotNull(hotlinePolicy, "parameter is null");
		checkArgument(hotlinePolicy.trim().length() > 0, "parameter is blank");
		this.hotlineProcessor = new HotlineProcessor(diameterPolicyManager);
	}

	@Override
	public void apply(ApplicationRequest applicationRequest, ApplicationResponse applicationResponse) {
		//Rejected as only a negative response should make the request eligible for hotlining
		DiameterProcessHelper.rejectResponse(applicationResponse, DEFAULT_RESULT_CODE, DiameterErrorMessageConstants.DEFAULT_RESPONSE_BEHAVIOR_APPLIED);
		
		try {
			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "Applying default response behavior HOTLINE for policy " + applicationRequest.getApplicationPolicy().getPolicyName());
			}
			
			hotlineProcessor.applyHotlinePolicy(applicationRequest, applicationResponse, hotlinePolicy);
			
		} catch (PolicyFailedException ex) {
			if (LogManager.getLogger().isWarnLogLevel()) {
				LogManager.getLogger().warn(MODULE, "Failed to apply Hotline Policy "+ hotlinePolicy
						+", Reason: " + ex.getMessage() + ". Applying REJECT as default behavior.");
			}
			DiameterProcessHelper.rejectResponse(applicationResponse, DEFAULT_RESULT_CODE, DiameterErrorMessageConstants.DEFAULT_RESPONSE_BEHAVIOR_APPLIED);
		} catch (ParserException ex) {
			if (LogManager.getLogger().isWarnLogLevel()) {
				LogManager.getLogger().warn(MODULE, "Failed to apply Hotline Policy "+ hotlinePolicy
						+", Reason: " + ex.getMessage() + " applying REJECT as default behavior.");
			}
			DiameterProcessHelper.rejectResponse(applicationResponse, DEFAULT_RESULT_CODE, DiameterErrorMessageConstants.DEFAULT_RESPONSE_BEHAVIOR_APPLIED);
		}
	}
}
