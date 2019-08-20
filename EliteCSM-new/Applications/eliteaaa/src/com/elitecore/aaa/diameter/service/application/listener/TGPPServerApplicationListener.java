package com.elitecore.aaa.diameter.service.application.listener;

import java.util.List;

import com.elitecore.aaa.diameter.applications.commons.SessionTimeoutAVPHandler;
import com.elitecore.aaa.diameter.conf.DiameterTGPPServerConfigurable;
import com.elitecore.aaa.diameter.policies.diameterpolicy.DiameterPolicyManager;
import com.elitecore.aaa.diameter.policies.tgppserver.TGPPServerPolicy;
import com.elitecore.aaa.diameter.service.DiameterServiceContext;
import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.service.application.ApplicationResponse;
import com.elitecore.aaa.diameter.sessionmanager.DiameterSessionManager;
import com.elitecore.aaa.diameter.util.DiameterProcessHelper;
import com.elitecore.aaa.util.constants.AAAServerConstants;
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
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCodeCategory;
import com.elitecore.diameterapi.diameter.stack.AppListenerInitializationFaildException;
import com.elitecore.diameterapi.diameter.stack.application.SessionReleaseIndiactor;
import com.elitecore.diameterapi.diameter.stack.application.sessionrelease.AppDefaultSessionReleaseIndicator;

public class TGPPServerApplicationListener extends AAAServerApplication {

	private static final String MODULE = "TGPP-SVR-APP-LSTNR";
	private SessionManagementHandler<ApplicationRequest, ApplicationResponse> sessionManagementHandler;
	private boolean isInitialized = false;
	private final DiameterSessionManager diameterSessionManager;
	private SessionTimeoutAVPHandler<ApplicationRequest, ApplicationResponse> sessionTimeoutHandler;

	public TGPPServerApplicationListener(DiameterServiceContext serviceContext, IStackContext stackContext,
			DiameterTGPPServerConfigurable tgppServerConfigurable,
			DiameterSessionManager diameterSessionManager, SessionFactoryManager sessionFactoryManager) {
		super(stackContext, serviceContext, sessionFactoryManager, tgppServerConfigurable.getSupportedApplications());
		registerPlugins(getServiceContext().getServerContext().getDiameterPluginManager().getNameToPluginMap(), tgppServerConfigurable.getInPlugins(), tgppServerConfigurable.getOutPlugins());
		this.diameterSessionManager = diameterSessionManager;
		this.sessionTimeoutHandler = new SessionTimeoutAVPHandler<>();
	}

	@Override
	public void init() throws AppListenerInitializationFaildException {
		if(isInitialized) {
			LogManager.getLogger().info(MODULE,"TGPP Server Application is already initialized.");
			return;
		}
		super.init();
		/*
		 * This session release indicator will be 
		 * used to remove DB Session
		 * with App Default Session Release indicator
		 */
		if(diameterSessionManager != null) {
			sessionManagementHandler = new SessionManagementHandler<>(
					diameterSessionManager, getSessionReleaseIndicator());
		}
		LogManager.getLogger().info(MODULE, "TGPP Server Application Successfully initialized.");
		isInitialized = true;
	}

	@Override
	public String getApplicationIdentifier() {
		return "TGPPServer";
	}

	
	@Override
	protected void handleApplicationRequest(DiameterSession session, ApplicationRequest appRequest, 
			ApplicationResponse appResponse) {
		LogManager.getLogger().info(MODULE, "Processing TGPP Application request.");
		DiameterUtility.addOrReplaceAvp(DiameterAVPConstants.RESULT_CODE, appResponse.getDiameterAnswer(), ResultCode.DIAMETER_SUCCESS.code + "");
		
		servicePolicyProcessing(appRequest, appResponse, session);
		
		if (appRequest.getApplicationPolicy() == null && appRequest.getDiameterRequest().getCommandCode() == CommandCode.SESSION_TERMINATION.code) {
			DiameterProcessHelper.toSuccess(appResponse);
		} 
	}

	private void servicePolicyProcessing(ApplicationRequest appRequest, ApplicationResponse appResponse,
			DiameterSession session) {
		TGPPServerPolicy policy = (TGPPServerPolicy) getServiceContext().selectServicePolicy(appRequest);
		if (policy == null) {
			LogManager.getLogger().warn( MODULE, "No policy satisfied for the request");
			DiameterProcessHelper.rejectResponse(appResponse, ResultCode.DIAMETER_UNABLE_TO_COMPLY, DiameterErrorMessageConstants.NO_POLICY_SATISFIED);
			return;
		}
		appRequest.setApplicationPolicy(policy);
		if (LogManager.getLogger().isInfoLogLevel()) {
			LogManager.getLogger().debug(MODULE, "Handling request using TGPP server policy: "
					+ policy.getPolicyName());
		}
		
		policy.handleRequest(appRequest, appResponse, session);
	}
	
	
	@Override
	public void resumeApplicationRequest(DiameterSession session, ApplicationRequest appRequest, 
			ApplicationResponse appResponse) {
		if (LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(MODULE, "Resuming processing of application request.");
		}

		if (ResultCodeCategory.getResultCodeCategory(appResponse.getAVP(DiameterAVPConstants.RESULT_CODE).getInteger()).isFailureCategory) {
			appResponse.setProcessingCompleted(true);
			appResponse.setFurtherProcessingRequired(false);

			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Skipping processing of request due to failure error code: " 
						+ ResultCode.fromCode((int)appResponse.getAVP(DiameterAVPConstants.RESULT_CODE).getInteger()));
			}
		} else {
			appResponse.setFurtherProcessingRequired(true);
			appResponse.setProcessingCompleted(true);
			
			appRequest.getExecutor().resumeRequestExecution(session);
		}
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
				
				TGPPServerPolicy tgppServerPolicy =(TGPPServerPolicy)applicationRequest.getApplicationPolicy();
				if (tgppServerPolicy != null) {
					if (isSessionManagementApplicable(tgppServerPolicy)) {
						sessionManagementHandler.handleSessionManagement(applicationRequest, applicationResponse, fetchSessionStatus(applicationRequest.getDiameterRequest()));
					} else {
						if (LogManager.getLogger().isDebugLogLevel()) {
							LogManager.getLogger().debug(MODULE, "skipping session management for policy: " + tgppServerPolicy.getPolicyName()
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
			ApplicationResponse applicationResponse, ISession session) {
		if (sessionTimeoutHandler.isEligible(applicationRequest, applicationResponse)) {
			sessionTimeoutHandler.handleRequest(applicationRequest, applicationResponse, session);
			addMSKRevalidationTimeToSessionTimeout(applicationRequest, applicationResponse);
		}
	}
	
	private boolean isSessionManagementApplicable(TGPPServerPolicy policy) {
		return diameterSessionManager != null && policy.isSessionManagementEnabled();
	}

	@Override
	protected void postResponseProcessing(ApplicationRequest appRequest, ApplicationResponse appResponse,
			ISession session) {
		if (appRequest.getApplicationPolicy() == null) {
			return;
		}
		
		if (appRequest.getPostResponseExecutor() == null) {
			return;
		}
		
		//resetting the flag to allow further execution in case of negative result code
		appResponse.setFurtherProcessingRequired(true);
		appRequest.getPostResponseExecutor().startRequestExecution(session);
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
	
	@SuppressWarnings("unchecked")
	private void applyReplyItem(ApplicationRequest request, ApplicationResponse response) {
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
				int commandCode = diameterRequest.getCommandCode();
				if (CommandCode.AUTHENTICATION_AUTHORIZATION.code == commandCode) {
					return DiameterAttributeValueConstants.EC_SESSON_STATUS_INACTIVE;
				} else if (CommandCode.ACCOUNTING.code == commandCode) {
					IDiameterAVP avp = diameterRequest.getAVP(DiameterAVPConstants.ACCOUNTING_RECORD_TYPE, true);
					if (avp != null) {
						if (avp.getInteger() == DiameterAttributeValueConstants.STOP_RECORD) {
							return DiameterAttributeValueConstants.EC_SESSON_STATUS_DELETED;
						} else {
							return DiameterAttributeValueConstants.EC_SESSON_STATUS_ACTIVE;
						}
					}
				} else if (CommandCode.SESSION_TERMINATION.code == commandCode) {
					return DiameterAttributeValueConstants.EC_SESSON_STATUS_DELETED;
				} else if (CommandCode.CREDIT_CONTROL.code == commandCode) {
					IDiameterAVP avp = diameterRequest.getAVP(DiameterAVPConstants.CC_REQUEST_TYPE, true);
					if (avp != null) {
						if (avp.getInteger() == DiameterAttributeValueConstants.DIAMETER_TERMINATION_REQUEST){
							return DiameterAttributeValueConstants.EC_SESSON_STATUS_DELETED;
						} else {
							return DiameterAttributeValueConstants.EC_SESSON_STATUS_ACTIVE;
						}
					}
				} else if (CommandCode.DIAMETER_EAP.code == commandCode) {
					return DiameterAttributeValueConstants.EC_SESSON_STATUS_INACTIVE;
				}
				return null;
			}
		 

	@Override
	protected SessionReleaseIndiactor createSessionReleaseIndicator(ApplicationEnum applicationEnum) {
		return new AppDefaultSessionReleaseIndicator();
	}

}
