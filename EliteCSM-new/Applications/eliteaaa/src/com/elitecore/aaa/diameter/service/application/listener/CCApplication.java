package com.elitecore.aaa.diameter.service.application.listener;

import com.elitecore.aaa.diameter.policies.applicationpolicy.CCAppPolicy;
import com.elitecore.aaa.diameter.policies.applicationpolicy.conf.impl.DiameterCCServiceConfigurable;
import com.elitecore.aaa.diameter.service.DiameterServiceContext;
import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.service.application.ApplicationResponse;
import com.elitecore.aaa.diameter.sessionmanager.DiameterSessionManager;
import com.elitecore.aaa.diameter.util.DiameterProcessHelper;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.diameterapi.core.common.session.SessionFactoryManager;
import com.elitecore.diameterapi.core.common.util.constant.ApplicationEnum;
import com.elitecore.diameterapi.core.stack.IStackContext;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.session.DiameterSession;
import com.elitecore.diameterapi.diameter.common.util.DiameterUtility;
import com.elitecore.diameterapi.diameter.common.util.constant.CommandCode;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAttributeValueConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterErrorMessageConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;
import com.elitecore.diameterapi.diameter.stack.AppListenerInitializationFaildException;
import com.elitecore.diameterapi.diameter.stack.application.SessionReleaseIndiactor;
import com.elitecore.diameterapi.diameter.stack.application.sessionrelease.AppDefaultSessionReleaseIndicator;

/**
 * Class <code>CCApplication</code> is intended to handle CC application Specific Request.
 * So, this class will be useful in case of CC application doesn't required CC State machine to handle request. 
 *  
 *
 * @author  HemalTalsania
 */

public class CCApplication extends AAAServerApplication {
	
	private static final String MODULE = "CC-APP";
	private DiameterCCServiceConfigurable diameterCCServiceConfiguration;
	private boolean isInitialized;
	private final DiameterSessionManager diameterSessionManager;
	private SessionManagementHandler<ApplicationRequest, ApplicationResponse> sessionManagementHandler;
	
	public CCApplication(DiameterServiceContext serviceContext, IStackContext stackContext,
			DiameterCCServiceConfigurable ccApplicationConfiguration, DiameterSessionManager diameterSessionManager, SessionFactoryManager sessionFactoryManager) {
		super(stackContext, serviceContext, sessionFactoryManager, ccApplicationConfiguration.getSupportedApplication());
		this.diameterCCServiceConfiguration = ccApplicationConfiguration;
		this.diameterSessionManager = diameterSessionManager;
	}

	@Override
	public void init() throws AppListenerInitializationFaildException {
		if(!isInitialized) {
			super.init();
			registerPlugins(getServiceContext().getServerContext().getDiameterPluginManager().getNameToPluginMap(),diameterCCServiceConfiguration.getInPlugins(), diameterCCServiceConfiguration.getOutPlugins());
			sessionManagementHandler = new SessionManagementHandler<>(diameterSessionManager, getSessionReleaseIndicator());
			LogManager.getLogger().info(MODULE, "Application Successfully initialized.");
			isInitialized = true;
		}
	}
	
	@Override
	protected SessionReleaseIndiactor createSessionReleaseIndicator(ApplicationEnum applicationEnum) {
		return new AppDefaultSessionReleaseIndicator();
	}
	
	@Override
	public String getApplicationIdentifier() {
		return "CCApplication";
	}
	
	protected String getModuleName() {
		return MODULE;
	}

	@Override
	protected void handleApplicationRequest(DiameterSession session, ApplicationRequest appRequest,
			ApplicationResponse appResponse) {
	
		if (LogManager.getLogger().isInfoLogLevel()) {
			LogManager.getLogger().info(MODULE, "Processing CC Application request.");
		}
		
		selectCcAppPolicy(appRequest, appResponse);
		
		if (appRequest.getDiameterRequest().getCommandCode() == CommandCode.SESSION_TERMINATION.code) {
			handleSessionTerminationRequest(appRequest, appResponse);
		} else {
			handleCCRequest(appRequest, appResponse, session);

	}

	}

	private void handleSessionTerminationRequest(ApplicationRequest appRequest, ApplicationResponse appResponse) {
		CCAppPolicy ccAppPolicy = (CCAppPolicy) appRequest.getApplicationPolicy();
		if (ccAppPolicy != null) {
			ccAppPolicy.locateSession(appRequest, appResponse);
			doSessionManagement(appRequest, appResponse, ccAppPolicy);
		}
		DiameterUtility.addOrReplaceAvp(DiameterAVPConstants.RESULT_CODE, appResponse.getDiameterAnswer(),
				ResultCode.DIAMETER_SUCCESS.code + "");
	}

	private void handleCCRequest(ApplicationRequest applicationRequest,
			ApplicationResponse applicationResponse, DiameterSession session) {
		CCAppPolicy ccAppPolicy = (CCAppPolicy) applicationRequest.getApplicationPolicy();
		if (ccAppPolicy == null) {
			return;
		}
		if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info( MODULE, "Applying policy: " 
						+ ccAppPolicy.getPolicyName());
		}
		applicationRequest.setApplicationPolicy(ccAppPolicy); 

		ccAppPolicy.handleRequest(applicationRequest,applicationResponse, session);
		
		doSessionManagement(applicationRequest, applicationResponse, ccAppPolicy);
	}

	private void doSessionManagement(ApplicationRequest applicationRequest, ApplicationResponse applicationResponse,
			CCAppPolicy applicationPolicy) {
		if (applicationResponse.isMarkedForDropRequest() == false 
				&& applicationResponse.isProcessingCompleted()
				&& applicationResponse.isFurtherProcessingRequired()) {

			if (applicationPolicy.isSessionManagementEnabled()) {
				sessionManagementHandler.handleSessionManagement(applicationRequest, applicationResponse, 
						fetchSessionStatus(applicationRequest.getDiameterRequest()));
			}
		}
	}

	private void selectCcAppPolicy(ApplicationRequest applicationRequest,
			ApplicationResponse applicationResponse) {
		CCAppPolicy applicationPolicy = (CCAppPolicy) getServiceContext().selectServicePolicy(applicationRequest);
		if (applicationPolicy == null) {
			LogManager.getLogger().warn( MODULE, "No policy satisfied for the request");
			DiameterProcessHelper.rejectResponse(applicationResponse, ResultCode.DIAMETER_RATING_FAILED, DiameterErrorMessageConstants.NO_POLICY_SATISFIED);
			return;
		}
		applicationRequest.setApplicationPolicy(applicationPolicy);
	}

	private String fetchSessionStatus(DiameterRequest diameterRequest) {
		int commandCode = diameterRequest.getCommandCode();
		if (CommandCode.CREDIT_CONTROL.code == commandCode) {
			IDiameterAVP avp = diameterRequest.getAVP(DiameterAVPConstants.CC_REQUEST_TYPE, true);
			if (avp != null) {
				if (avp.getInteger() == DiameterAttributeValueConstants.DIAMETER_TERMINATION_REQUEST) {
					return DiameterAttributeValueConstants.EC_SESSON_STATUS_DELETED;
				} else {
					return DiameterAttributeValueConstants.EC_SESSON_STATUS_ACTIVE;
				}
			}
		} else if (CommandCode.SESSION_TERMINATION.code == commandCode) {
			return DiameterAttributeValueConstants.EC_SESSON_STATUS_DELETED;
		}
		return null;
	}

	@Override
	protected void resumeApplicationRequest(DiameterSession session, ApplicationRequest request,
			ApplicationResponse response) {
		throw new UnsupportedOperationException("Resumption is not supported.");
	}

	@Override
	protected void finalPreResponseProcessing(ApplicationRequest applicationRequest,
			ApplicationResponse applicationResponse, ISession session) {
		//no need to process
	}
}
