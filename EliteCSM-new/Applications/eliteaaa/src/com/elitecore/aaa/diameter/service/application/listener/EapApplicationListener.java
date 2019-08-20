package com.elitecore.aaa.diameter.service.application.listener;

import java.util.List;
import java.util.Optional;

import com.elitecore.aaa.diameter.applications.commons.SessionTimeoutAVPHandler;
import com.elitecore.aaa.diameter.conf.impl.DiameterEAPServiceConfigurable;
import com.elitecore.aaa.diameter.policies.applicationpolicy.EapAppPolicy;
import com.elitecore.aaa.diameter.policies.diameterpolicy.DiameterPolicyManager;
import com.elitecore.aaa.diameter.service.DiameterServiceContext;
import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.service.application.ApplicationResponse;
import com.elitecore.aaa.diameter.sessionmanager.DiameterSessionManager;
import com.elitecore.aaa.diameter.util.DiameterProcessHelper;
import com.elitecore.aaa.util.constants.AAAServerConstants;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.diameterapi.core.common.session.Session;
import com.elitecore.diameterapi.core.common.session.SessionFactoryManager;
import com.elitecore.diameterapi.core.common.session.SessionFactoryType;
import com.elitecore.diameterapi.core.common.util.constant.ApplicationEnum;
import com.elitecore.diameterapi.core.stack.IStackContext;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.session.DiameterSession;
import com.elitecore.diameterapi.diameter.common.util.DiameterUtility;
import com.elitecore.diameterapi.diameter.common.util.constant.ApplicationIdentifier;
import com.elitecore.diameterapi.diameter.common.util.constant.CommandCode;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAttributeValueConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterErrorMessageConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCodeCategory;
import com.elitecore.diameterapi.diameter.stack.AppListenerInitializationFaildException;
import com.elitecore.diameterapi.diameter.stack.application.SessionReleaseIndiactor;
import com.elitecore.diameterapi.diameter.stack.application.sessionrelease.SessionReleaseIndicatorFactory;

public class EapApplicationListener extends AAAServerApplication {


	private static final String MODULE = "EAP-APP-LSTNR";

	private DiameterEAPServiceConfigurable diameterEAPServiceConfiguration;
	private boolean isInitialized;

	private final DiameterSessionManager sessionManager;

	private SessionManagementHandler<ApplicationRequest, ApplicationResponse> sessionManagementHandler;
	private SessionTimeoutAVPHandler<ApplicationRequest, ApplicationResponse> sessionTimeoutHandler;

	public EapApplicationListener(DiameterServiceContext serviceContext,
			IStackContext stackContext,
			DiameterEAPServiceConfigurable eapApplicationConfiguration,
			DiameterSessionManager diameterSessionManager, SessionFactoryManager sessionFactoryManager) {
		super(stackContext, serviceContext, sessionFactoryManager, eapApplicationConfiguration.getSupportedApplication());
		this.diameterEAPServiceConfiguration = eapApplicationConfiguration;
		this.sessionManager = diameterSessionManager;
		this.sessionTimeoutHandler = new SessionTimeoutAVPHandler<>();
	}

	@Override
	public void init() throws AppListenerInitializationFaildException {
		if(!isInitialized) {
			super.init();
			sessionManagementHandler = new SessionManagementHandler<>(sessionManager, 
					getSessionReleaseIndicator());
			registerPlugins(getServiceContext().getServerContext().getDiameterPluginManager().getNameToPluginMap(), diameterEAPServiceConfiguration.getInPlugins(), diameterEAPServiceConfiguration.getOutPlugins());
			LogManager.getLogger().info(MODULE, "Application Successfully initialized.");
			isInitialized = true;
		}

	}

	@Override
	protected SessionReleaseIndiactor createSessionReleaseIndicator(ApplicationEnum applicationEnum) {
		return SessionReleaseIndicatorFactory.getDefaultSessionReleaseIndiactor();
	}

	@Override
	protected void handleApplicationRequest(DiameterSession session, ApplicationRequest appRequest,
			ApplicationResponse appResponse) {

		LogManager.getLogger().info(MODULE, "Processing EAP Application request.");
		if (isEligible(appRequest.getDiameterRequest())) {
				selectEapAppPolicy(appRequest, appResponse);
			if (appRequest.getDiameterRequest().getCommandCode() == CommandCode.SESSION_TERMINATION.code) {
				handleSessionTerminationRequest(appRequest, appResponse, session);
			} else {
				handleEAPRequest(session, appRequest, appResponse);
			}
		} else {
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Sending " + ResultCode.DIAMETER_UNABLE_TO_COMPLY + 
						", Reason: Invalid Request");
			}
			DiameterUtility.addOrReplaceAvp(DiameterAVPConstants.RESULT_CODE, 
					appResponse.getDiameterAnswer(), 
					ResultCode.DIAMETER_UNABLE_TO_COMPLY.code + "");
		}
	}

	private void handleEAPRequest(Session session,
			ApplicationRequest applicationRequest,
			ApplicationResponse applicationResponse) {
		if(applicationRequest.getApplicationPolicy() == null) {
			return;
		}

		EapAppPolicy eapAppPolicy = (EapAppPolicy)applicationRequest.getApplicationPolicy();
		if (LogManager.getLogger().isInfoLogLevel()) {
			LogManager.getLogger().info(MODULE, "Handling request using TGPP application policy: "
					+ eapAppPolicy.getPolicyName());
		}
		eapAppPolicy.handleRequest(applicationRequest,applicationResponse, (DiameterSession) session);
	}

	private void selectEapAppPolicy(ApplicationRequest applicationRequest,
			ApplicationResponse applicationResponse) {
		EapAppPolicy applicationPolicy = (EapAppPolicy) getServiceContext().selectServicePolicy(applicationRequest);
		if (applicationPolicy == null) {
			LogManager.getLogger().warn( MODULE, "No policy satisfied for the request");
			DiameterProcessHelper.rejectResponse(applicationResponse, ResultCode.DIAMETER_UNABLE_TO_COMPLY, DiameterErrorMessageConstants.NO_POLICY_SATISFIED);
			return;
		}

		applicationRequest.setApplicationPolicy(applicationPolicy);
		}

	@Override
	protected void finalPreResponseProcessing(ApplicationRequest applicationRequest,
			ApplicationResponse applicationResponse, ISession session) {
		IDiameterAVP resultCodeAVP = applicationResponse.getDiameterAnswer().getAVP(DiameterAVPConstants.RESULT_CODE_STR);
		if (resultCodeAVP != null) {
			long resultCode = resultCodeAVP.getInteger();
			
			if (ResultCodeCategory.getResultCodeCategory(resultCode) == ResultCodeCategory.RC2XXX) {
				applyReplyItem(applicationRequest, applicationResponse);
				validateSessionTimeoutAVP(applicationRequest, applicationResponse, session);
				
				EapAppPolicy applicationPolicy = (EapAppPolicy) applicationRequest.getApplicationPolicy();
				if (applicationPolicy != null) {
					if (applicationPolicy.isSessionManagementEnabled()) {
						sessionManagementHandler.handleSessionManagement(applicationRequest, applicationResponse, fetchSessionStatus(applicationRequest.getDiameterRequest()));
					} else {
						if (LogManager.getLogger().isDebugLogLevel()) {
							LogManager.getLogger().debug(MODULE, "skipping session management for policy: " + applicationPolicy.getPolicyName()
									+ " as session management is disabled");
						}
					}
				}
			} else {
				if (LogManager.getLogger().isDebugLogLevel()) {
					LogManager.getLogger().debug(MODULE,"skipping pre-response processing as result code: " + resultCode + " is other than success category");		
				}
			}
		}
		
	}
	
	private void validateSessionTimeoutAVP(
			ApplicationRequest applicationRequest,
			ApplicationResponse applicationResponse,
			ISession session) {
		if (sessionTimeoutHandler.isEligible(applicationRequest, applicationResponse)) {
			sessionTimeoutHandler.handleRequest(applicationRequest, applicationResponse, session);
			addMSKRevalidationTimeToSessionTimeout(applicationRequest, applicationResponse);
		}
	}
	
	/*
	 * Do not change the order of the execution of this method as fail-fast approach is used.
	 */
	private void addMSKRevalidationTimeToSessionTimeout(ApplicationRequest applicationRequest, ApplicationResponse applicationResponse) {
		if (applicationRequest.getParameter(AAAServerConstants.MSK_REVALIDATION_TIME) == null) {
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "MSK Revalidation time will not be added, "
						+ "Reason: MSK Revalidation time parameter not found");
			}
			return;
		}
		long mskRevalidationTime = 0;
		try {
			 mskRevalidationTime = (Long)applicationRequest.getParameter(AAAServerConstants.MSK_REVALIDATION_TIME);
		} catch (NumberFormatException e) {
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Skipping the MSK Revalidation time addition, "
						+ "Reason: Found invalid MSK Revalidation time: " + (String)applicationRequest.getParameter(AAAServerConstants.MSK_REVALIDATION_TIME));
			}
			return;
		}
		
		if (mskRevalidationTime <= 0) {
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Skipping the MSK Revalidation time addition, "
						+ "Reason: It is disabled by setting its value: " + mskRevalidationTime);
			}
			return;
		}
		
		IDiameterAVP sessionTimeoutAvp = applicationResponse.getAVP(DiameterAVPConstants.SESSION_TIMEOUT, true);
		if (sessionTimeoutAvp != null 
				&& sessionTimeoutAvp.getStringValue() != null 
				&& sessionTimeoutAvp.getStringValue().isEmpty() == false) {
			
			sessionTimeoutAvp.setInteger(sessionTimeoutAvp.getInteger() + mskRevalidationTime);
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Successfully added MSK Revalidation time: " + mskRevalidationTime + " to session timeout attribute");
			}
		} else {
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Skipping the MSK Revalidation time addition, "
						+ "Reason: Session timeout attribute(0:27) not found");
			}
			return;
		}
	}
	
	private void handleSessionTerminationRequest(ApplicationRequest applicationRequest,
			ApplicationResponse applicationResponse, DiameterSession session) {
		if (applicationRequest.getApplicationPolicy() != null ) {
			((EapAppPolicy)applicationRequest.getApplicationPolicy()).handleSessionTerminationRequest(applicationRequest, applicationResponse, session);
		}
		
		DiameterUtility.addOrReplaceAvp(DiameterAVPConstants.RESULT_CODE, 
				applicationResponse.getDiameterAnswer(), 
				ResultCode.DIAMETER_SUCCESS.code + "");
	}

	private boolean isEligible(DiameterRequest request) {
		if (isKnownCommandCode(request.getCommandCode())) {
			if (isWellFormedRequest(request)) {
				return true;
			}
		}
		return false;
	}


	private boolean isWellFormedRequest(DiameterRequest request) {
		if (request.getAVP(DiameterAVPConstants.SESSION_ID) == null) {
			return false;
		}

		if (CommandCode.SESSION_TERMINATION.code == request.getCommandCode()) {
			return true;
		}

		return request.getAVP(DiameterAVPConstants.EAP_PAYLOAD) != null;
	}

	private boolean isKnownCommandCode(int commandCode) {
		return CommandCode.DIAMETER_EAP.code == commandCode
				|| CommandCode.SESSION_TERMINATION.code == commandCode;
	}

	@Override
	public String getApplicationIdentifier() {
		return "EapApplication";
	}

	protected String getModuleName() {
		return MODULE;
	}

	@SuppressWarnings("unchecked")
	private void applyReplyItem(ApplicationRequest request,ApplicationResponse response) {
		String replyItem = (String)request.getParameter(AAAServerConstants.CUSTOMER_REPLY_ITEM);
		if (replyItem == null) {
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "No customer reply items configured");
			}
			return;
		}

		if (LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(MODULE, "Applying customer reply items: " + replyItem);
		}

		DiameterPolicyManager.getInstance(DiameterPolicyManager.DIAMETER_AUTHORIZATION_POLICY)
		.applyReplyItems(request.getDiameterRequest(), response.getDiameterAnswer(), 
				(List<String>)request.getParameter(AAAServerConstants.SATISFIED_POLICIES), 
				replyItem);
	}


	private String fetchSessionStatus(DiameterRequest diameterRequest) {
		if (CommandCode.SESSION_TERMINATION.code == diameterRequest.getCommandCode()) {
			return DiameterAttributeValueConstants.EC_SESSON_STATUS_DELETED;
		}
		return DiameterAttributeValueConstants.EC_SESSON_STATUS_INACTIVE;
	}

	@Override
	protected void resumeApplicationRequest(DiameterSession session, ApplicationRequest request,
			ApplicationResponse response) {
		// no ops as resumption is not supported
	}


}
