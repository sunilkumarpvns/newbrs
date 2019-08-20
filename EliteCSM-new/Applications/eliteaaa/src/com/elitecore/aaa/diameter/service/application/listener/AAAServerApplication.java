package com.elitecore.aaa.diameter.service.application.listener;


import static com.elitecore.aaa.diameter.service.application.listener.AAAServerApplication.NegativeRespopnseRetainableAVPs.NEGATIVE_RESPONSE_RETAINABLE_AVPS;
import static com.elitecore.diameterapi.core.common.session.SessionFactoryType.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import com.elitecore.aaa.diameter.service.DiameterServiceContext;
import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.service.application.ApplicationResponse;
import com.elitecore.aaa.diameter.service.application.handlers.DiameterAsyncRequestExecutor;
import com.elitecore.aaa.diameter.util.HotlineUtility;
import com.elitecore.commons.base.Predicate;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.imdg.HazelcastImdgInstance;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.core.systemx.esix.CommunicationException;
import com.elitecore.diameterapi.core.common.session.Session;
import com.elitecore.diameterapi.core.common.session.SessionFactoryManager;
import com.elitecore.diameterapi.core.common.util.constant.ApplicationEnum;
import com.elitecore.diameterapi.core.stack.IStackContext;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.session.DiameterSession;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.stack.AppListenerInitializationFaildException;
import com.elitecore.diameterapi.diameter.stack.application.ServerApplication;

public abstract class AAAServerApplication extends ServerApplication {
	private static final String MODULE = "AAA-SVR-APP";
	private final DiameterServiceContext serviceContext;
	private SessionFactoryManager sessionFactoryManager;

	public AAAServerApplication(IStackContext stackContext,
			DiameterServiceContext serviceContext,
			SessionFactoryManager sessionFactoryManager,
			ApplicationEnum... applicationEnum) {
		super(stackContext, applicationEnum);
		this.serviceContext = serviceContext;
		this.sessionFactoryManager = sessionFactoryManager;
	}
	
	public AAAServerApplication(IStackContext stackContext, 
			DiameterServiceContext serviceContext) {
		super(stackContext);
		this.serviceContext = serviceContext;
	}
	
	public enum NegativeRespopnseRetainableAVPs {
		SESSION_ID(DiameterAVPConstants.SESSION_ID),
		RESULT_CODE(DiameterAVPConstants.RESULT_CODE),
		ORIGIN_HOST(DiameterAVPConstants.ORIGIN_HOST),
		ORIGIN_REALM(DiameterAVPConstants.ORIGIN_REALM),
		ERROR_MESSAGE(DiameterAVPConstants.ERROR_MESSAGE),
		DESTINATION_HOST(DiameterAVPConstants.DESTINATION_HOST),
		DESTINATION_REALM(DiameterAVPConstants.DESTINATION_REALM),
		PROXY_INFO(DiameterAVPConstants.PROXY_INFO),
		USER_NAME(DiameterAVPConstants.USER_NAME),
		EAP_PAYLOAD(DiameterAVPConstants.EAP_PAYLOAD),
		FAILED_AVP(DiameterAVPConstants.FAILED_AVP),
		ROUTE_RECORD(DiameterAVPConstants.ROUTE_RECORD),
		PROXY_STATE(DiameterAVPConstants.PROXY_STATE),
		AUTH_APPLICATION_ID(DiameterAVPConstants.AUTH_APPLICATION_ID),
		AUTH_REQUEST_TYPE(DiameterAVPConstants.AUTH_REQUEST_TYPE),
		ACCT_APPLICATION_ID(DiameterAVPConstants.ACCT_APPLICATION_ID),
		CC_REQUEST_TYPE(DiameterAVPConstants.CC_REQUEST_TYPE),
		CC_REQUEST_NUMBER(DiameterAVPConstants.CC_REQUEST_NUMBER),
		ACCOUNTING_RECORD_TYPE(DiameterAVPConstants.ACCOUNTING_RECORD_TYPE),
		ACCOUNTING_RECORD_NUMBER(DiameterAVPConstants.ACCOUNTING_RECORD_NUMBER);
		
		public final String value;
		public static final Map<String, String> retainableAvpMap;
		
		static {
			retainableAvpMap = new HashMap<String, String>();
			
			for (NegativeRespopnseRetainableAVPs avp : NegativeRespopnseRetainableAVPs.values()) {
				retainableAvpMap.put(avp.value, avp.value);
			}
		}
		
		private NegativeRespopnseRetainableAVPs( String value ) {
			this.value = value;
		}
		
		public final static Predicate<IDiameterAVP> NEGATIVE_RESPONSE_RETAINABLE_AVPS = new Predicate<IDiameterAVP>() {
			@Override
			public boolean apply(IDiameterAVP avp) {
				if (retainableAvpMap.containsKey(avp.getAVPId())) {
					return true;
				}
				return false;
			}
		};
	}
	
	@Override
	public void init() throws AppListenerInitializationFaildException {
		super.init();
		HazelcastImdgInstance hazelcastImdgInstance = getServiceContext().getServerContext().getHazelcastImdgInstance();
		for (ApplicationEnum appEnum : getApplicationEnum()) {
			try {
				sessionFactoryManager.register( appEnum.getApplicationId(), 
						hazelcastImdgInstance == null ? INMEMORY : HAZELCAST,
						Optional.ofNullable(hazelcastImdgInstance));
			} catch (InitializationFailedException e) {
				throw new AppListenerInitializationFaildException(e);
			}
		}

	}
	
	public DiameterServiceContext getServiceContext() {
		return serviceContext;
	}

	@Override
	protected final void processApplicationRequest(Session session, DiameterRequest diameterRequest) {
		super.processApplicationRequest(session, diameterRequest);
		ApplicationResponse applicationResponse = new ApplicationResponse(diameterRequest);
		ApplicationRequest applicationRequest = new ApplicationRequest(diameterRequest);

		synchronized (applicationRequest) {
			handleApplicationRequest((DiameterSession) session, applicationRequest, applicationResponse);
			
			if (applicationResponse.isMarkedForDropRequest() == false 
					&& applicationResponse.isProcessingCompleted()) {
				if (HotlineUtility.isEligibleForHotlining(applicationRequest, applicationResponse)) {
					HotlineUtility.doHotlining(applicationRequest, applicationResponse);
				}
				try {
					finalPreResponseProcessing(applicationRequest,applicationResponse, session);
					/*  This check is for dbFailure drop action. --> diameterSessionManager */
					if (applicationResponse.isMarkedForDropRequest() == false) {
						sendDiameterAnswer((DiameterSession)session, applicationRequest.getDiameterRequest(), applicationResponse.getDiameterAnswer());
					}
				} catch (CommunicationException e) {
					LogManager.getLogger().error(MODULE, "Unable to send diameter answer for request with HbH-ID=" 
						+ diameterRequest.getHop_by_hopIdentifier() + "EtE-ID=" 
						+ diameterRequest.getEnd_to_endIdentifier() + ", Reason: " + e.getMessage());
					LogManager.getLogger().trace(MODULE, e);
				}
				exceptionSafePostResponseProcessing(applicationRequest,applicationResponse,(DiameterSession) session);
			}
		}
	}

	
	
	

		protected abstract void handleApplicationRequest(DiameterSession session, ApplicationRequest applicationRequest,
			ApplicationResponse applicationResponse);
	
	/**
	 * This method specifies the pre-response processing before sending final response
	 * @param applicationRequest  
	 * @param applicationResponse
	 * @param session session details
	 */
	protected abstract void finalPreResponseProcessing(ApplicationRequest applicationRequest,
			ApplicationResponse applicationResponse, ISession session);

	protected void postResponseProcessing(ApplicationRequest applicationRequest, ApplicationResponse applicationResponse,
			ISession session) {
		
	}

	public final void resumeApplicationRequest(DiameterSession session, ApplicationRequest request, ApplicationResponse response, 
			DiameterAsyncRequestExecutor unitOfWork) {

		synchronized (request) {
			unitOfWork.handleServiceRequest(request, response, session);

			resumeApplicationRequest(session, request, response);
			if (response.isMarkedForDropRequest() == false 
					&& response.isProcessingCompleted()) {
				if(HotlineUtility.isEligibleForHotlining(request, response)) {
					HotlineUtility.doHotlining(request, response);
				}
				try {
					finalPreResponseProcessing(request, response, session);
					/*  This check is for dbFailure drop action. --> diameterSessionManager */
					if (response.isMarkedForDropRequest() == false) {
						sendDiameterAnswer(session, request.getDiameterRequest(), response.getDiameterAnswer());
					}
				} catch (CommunicationException e) {
					LogManager.getLogger().error(MODULE, "Unable to send diameter answer for request with HbH-ID=" 
							+ request.getDiameterRequest().getHop_by_hopIdentifier() + "EtE-ID=" 
							+ request.getDiameterRequest().getEnd_to_endIdentifier() + ", Reason: " + e.getMessage());
						LogManager.getLogger().trace(MODULE, e);
				}
				exceptionSafePostResponseProcessing(request,response, session);
			}
		}
	}

	private void exceptionSafePostResponseProcessing(ApplicationRequest request, ApplicationResponse response,
			DiameterSession session) {
		try {
			postResponseProcessing(request, response, session);
		} catch (RuntimeException e) {
			LogManager.getLogger().error(MODULE, "Unknown error occured during post response processing: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}
	}
	
	@Override
	public final void preProcess(DiameterSession session, DiameterAnswer diameterAnswer) {
		retainNegativeResponseAVPs(diameterAnswer);
	}
	
	private void retainNegativeResponseAVPs(DiameterAnswer diameterAnswer) {
		if (diameterAnswer.isFailureCategory()) {
			diameterAnswer.retain(NEGATIVE_RESPONSE_RETAINABLE_AVPS);
		}
	}

	protected abstract void resumeApplicationRequest(DiameterSession session, ApplicationRequest request,
			ApplicationResponse response);
}
