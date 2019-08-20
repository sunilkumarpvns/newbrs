package com.elitecore.aaa.radius.service.auth.policy.handlers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.elitecore.aaa.core.conf.impl.ImdgIndexDetail;
import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.radius.service.auth.RadAuthRequest;
import com.elitecore.aaa.radius.service.auth.RadAuthResponse;
import com.elitecore.aaa.radius.service.auth.RadAuthServiceContext;
import com.elitecore.aaa.radius.service.auth.constant.AuthReplyMessageConstant;
import com.elitecore.aaa.radius.service.auth.handlers.RadAuthServiceHandler;
import com.elitecore.aaa.radius.session.imdg.HazelcastRadiusSession;
import com.elitecore.aaa.radius.session.imdg.HazelcastRadiusSession.SessionStatus;
import com.elitecore.aaa.radius.sessionx.ConcurrentPolicyConstants;
import com.elitecore.aaa.radius.sessionx.conf.ConcurrentLoginPolicyConfiguration;
import com.elitecore.aaa.radius.sessionx.conf.impl.ConcurrentLoginPolicyData;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;

public class RadImdgConcurrencyHandler implements RadAuthServiceHandler {
	private static final String MODULE = "RAD-IMDG-CONC-HNDLR";
	private ConcurrentLoginPolicyConfiguration concurrentLoginPolicyConfiguration;
	private RadAuthServiceContext serviceContext;
	private ImdgIndexDetail concurrencyIdenityIndexDetail;

	public RadImdgConcurrencyHandler(RadAuthServiceContext serviceContext, 
			RadImdgConcurrencyHandlerData radImdgConcurrencyHandlerData) {
		this.serviceContext = serviceContext;
		this.concurrentLoginPolicyConfiguration = serviceContext.getServerContext().
				getServerConfiguration().getConcurrentLoginPolicyConfiguration();
		this.concurrencyIdenityIndexDetail = serviceContext.getServerContext().getServerConfiguration()
				.getImdgConfigurable().getImdgConfigData().getImdgRadiusConfig().getRadiusIndexFieldMap()
				.get(radImdgConcurrencyHandlerData.getConcurrencyIdentityField());
	}

	@Override
	public void init() throws InitializationFailedException {
		if (LogManager.getLogger().isInfoLogLevel()) {
			LogManager.getLogger().info(MODULE, "Initializing Radius IMDG Concurrency handler.");
		}
	}

	@Override
	public boolean isEligible(RadAuthRequest request, RadAuthResponse response) {
		return true;
	}

	@Override
	public void handleRequest(RadAuthRequest request, RadAuthResponse response, ISession session) {
		String concurrentLoginPolicy = retriveConcurrentLoginPolicyName(request, response);

		ConcurrentLoginPolicyData policyData = concurrentLoginPolicyConfiguration
				.getConcurrentLoginPolicy(concurrentLoginPolicy);

		if (policyData == null) {
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE,
						"Configured Concurrent Login Policy: " + concurrentLoginPolicy + " not found.");
			}
			return;
		}

		String policyMode = policyData.getPolicyMode();
		String policyType = policyData.getPolicyType();
		String concurrencyIdentityValue = null;
		String identityAttributes = concurrencyIdenityIndexDetail.getImdgAttributeValue();

		if(!policyMode.equals(ConcurrentPolicyConstants.GENERAL_POLICY)){
			if(LogManager.getLogger().isWarnLogLevel()) {
				LogManager.getLogger().warn(MODULE, "Concurrency Failed due to Policy mode" + policyMode + " is Selected");
			}
			sendAccessReject(response, AuthReplyMessageConstant.CONCURRENCY_FAILED,
					"Concurrency Failed due invalid Policy Mode:"
							+ policyMode);
			return;
		}

		if(policyType.equals(ConcurrentPolicyConstants.INDIVIDUAL_POLICY)){
			concurrencyIdentityValue = getAttributeValue(request, concurrencyIdenityIndexDetail.getAttributeList());
			if(LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE,"User found in Individual policy : " + policyData.getName());
			}
		} else if (policyType.equals(ConcurrentPolicyConstants.GROUP_POLICY)) {
			identityAttributes = RadiusConstants.ELITECORE_VENDOR_ID + ":" + RadiusAttributeConstants.ELITE_PROFILE_USER_GROUP;
			if(LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE,"User found in Group policy : " + policyData.getName());
			}
			concurrencyIdentityValue = getAttributeValue(request, new ArrayList<>(Arrays.asList(identityAttributes)));
			request.setParameter(concurrencyIdenityIndexDetail.getImdgIndex(), concurrencyIdentityValue);
		} else {
			if(LogManager.getLogger().isWarnLogLevel()) {
				LogManager.getLogger().warn(MODULE, "Concurrency Failed due to Policy Type" + policyType + " is Selected");
			}
			sendAccessReject(response, AuthReplyMessageConstant.CONCURRENCY_FAILED,
					"Concurrency Failed due invalid Policy Type:"
							+ policyType);
			return;
		}

		if (Strings.isNullOrBlank(concurrencyIdentityValue)) {
			sendAccessReject(response, AuthReplyMessageConstant.CONCURRENCY_FAILED,
					"Concurrency Failed due to missing attributes:"
							+ identityAttributes);
			return;
		}

		applyConcurrency(request, response, policyData, concurrencyIdentityValue);

	}

	private void applyConcurrency(RadAuthRequest request, RadAuthResponse response,
			ConcurrentLoginPolicyData policyData, String concurrencyIdentityValue) {

		Collection<String> sessionIdentities = serviceContext.getServerContext()
				.search(concurrencyIdenityIndexDetail.getImdgIndex(), concurrencyIdentityValue);
		long activeSessionsCount = 0;
		if(!Collectionz.isNullOrEmpty(sessionIdentities)) {
			for(String sessionId : sessionIdentities) {
				HazelcastRadiusSession session = (HazelcastRadiusSession) serviceContext.getServerContext().getOrCreateRadiusSession(sessionId);
				if(session.getSessionStatus() == SessionStatus.ACTIVE) {
					activeSessionsCount++;
				}
			}
		}
		if(activeSessionsCount >= policyData.getMaxLogins()) {
			sendAccessReject(response, AuthReplyMessageConstant.MAX_LOGIN_LIMIT_REACHED,
					"Max Login Limit reached. Max Sessions Allowed: " + policyData.getMaxLogins());
		}
	}


	private void sendAccessReject(RadAuthResponse response, String responseMessage,
			String logMessage) {
		response.setFurtherProcessingRequired(false);
		response.setPacketType(RadiusConstants.ACCESS_REJECT_MESSAGE);
		response.setResponseMessage(responseMessage);

		if(logMessage != null && LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
			LogManager.getLogger().debug(MODULE, "Sending ACCESS-REJECT reason: " + logMessage);
		}

	}

	private String getAttributeValue(RadServiceRequest request, List<String> attributeList) {
		for(String attribute : attributeList) {
			IRadiusAttribute radAttribute = request.getRadiusAttribute(attribute);
			if (radAttribute != null) {
				return radAttribute.getStringValue();
			}
		}
		return null;
	}

	private String retriveConcurrentLoginPolicyName(RadAuthRequest request, RadAuthResponse response) {
		IRadiusAttribute concurrentLoginPolicyAttribute = 
				request.getRadiusAttribute(true,
						RadiusConstants.ELITECORE_VENDOR_ID,
						RadiusAttributeConstants.ELITE_CONCURRENT_LOGIN_POLICY_NAME);

		if (concurrentLoginPolicyAttribute == null) {
			concurrentLoginPolicyAttribute = response.getRadiusAttribute(true, 
					RadiusConstants.ELITECORE_VENDOR_ID,
					RadiusAttributeConstants.ELITE_CONCURRENT_LOGIN_POLICY_NAME);
		}

		if (concurrentLoginPolicyAttribute == null) {			
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE,  
						" No Concurrent Login Policy attribute (21067:144)" 
								+ " found in request or response so skipping further processing.");
			}
			return null;
		}

		return concurrentLoginPolicyAttribute.getStringValue();
	}

	@Override
	public boolean isResponseBehaviorApplicable() {
		return false;
	}

	@Override
	public void reInit() throws InitializationFailedException {
		// No Op		
	}

}
