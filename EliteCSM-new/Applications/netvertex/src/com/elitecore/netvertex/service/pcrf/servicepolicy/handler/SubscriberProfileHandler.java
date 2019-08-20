package com.elitecore.netvertex.service.pcrf.servicepolicy.handler;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.servicex.ServiceRequest;
import com.elitecore.core.servicex.ServiceResponse;
import com.elitecore.corenetvertex.constants.PCRFEvent;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyValueConstants;
import com.elitecore.corenetvertex.constants.SubscriberLookupOn;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.netvertex.core.servicepolicy.ExecutionContext;
import com.elitecore.netvertex.core.servicepolicy.handler.ServiceHandler;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.PCRFServiceContext;
import com.elitecore.netvertex.service.pcrf.servicepolicy.conf.PccServicePolicyConfiguration;

import javax.annotation.Nonnull;

public class SubscriberProfileHandler extends ServiceHandler {
	
	private static final String MODULE = "SUB-PROF-HDLR";

	@Nonnull private final PccServicePolicyConfiguration servicePolicyConfiguration;
	
	public SubscriberProfileHandler(PCRFServiceContext pcrfServiceContext, 
			PccServicePolicyConfiguration servicePolicyConfiguration) {
		
		super(pcrfServiceContext);
		this.servicePolicyConfiguration = servicePolicyConfiguration;
	}

	@Override
	public void init() throws InitializationFailedException {
		//IGNORED
	}
	
	@Override
	protected void process(ServiceRequest serviceRequest, ServiceResponse serviceResponse, ExecutionContext executionContext) {
		PCRFRequest request = (PCRFRequest) serviceRequest;
		PCRFResponse response = (PCRFResponse) serviceResponse;
		
		String sessionID = request.getAttribute(PCRFKeyConstants.CS_SESSION_ID.val);
		String identityAttribute = servicePolicyConfiguration.getIdentityAttribute();
		String userIdentity = request.getAttribute(identityAttribute);
		
		if(userIdentity == null) {
			if(request.getPCRFEvents().contains(PCRFEvent.SESSION_STOP)) {
				if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
					LogManager.getLogger().info(MODULE, "Processing PCRF Request with sessionID= " + sessionID+" to release session. Reason: PCRF request contains Session-Stop event");
				}
				return;
			}
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
				LogManager.getLogger().error(MODULE, "Rejecting invalid request. Reason: UserIdentity not found for identityAttribute " + servicePolicyConfiguration.getIdentityAttribute());
			
			response.setAttribute(PCRFKeyConstants.RESULT_CODE.getVal(), PCRFKeyValueConstants.RESULT_CODE_AUTHORIZATION_REJECTED.val);
			response.setFurtherProcessingRequired(false);
			return;
		}

		if(servicePolicyConfiguration.getSubscriberLookupOn().equals(SubscriberLookupOn.SUBSCRIBER_IDENTITY.name())){
			request.setAttribute(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val, userIdentity);
		}else{
			request.setAttribute(PCRFKeyConstants.SUB_ALTERNATE_IDENTITY.val, userIdentity);
		}

		try {
			executionContext.getSPR();
		} catch (OperationFailedException e) {
			getLogger().error(MODULE, "Rejecting request for subscriber ID: " + userIdentity + ", Session ID: "+ sessionID +". Reason: " + e.getMessage());
			if (ResultCode.INTERNAL_ERROR == e.getErrorCode()) {
				getLogger().trace(MODULE, e);
			}

			response.setAttribute(PCRFKeyConstants.RESULT_CODE.getVal(), PCRFKeyValueConstants.RESULT_CODE_INTERNAL_ERROR.val);
			response.setFurtherProcessingRequired(false);
			return;
		}

	}

	@Override
	protected void preProcess(ServiceRequest request, ServiceResponse response,
			ExecutionContext executionContext) {
		//IGNORED
	}

	@Override
	protected void postProcess(ServiceRequest request,
			ServiceResponse response, ExecutionContext executionContext) {
		//IGNORED
	}

	@Override
	protected boolean isApplicable(ServiceRequest serviceRequest, ServiceResponse serviceResponse) {
		PCRFRequest pcrfRequest = (PCRFRequest) serviceRequest;
		return pcrfRequest.getPCRFEvents().contains(PCRFEvent.AUTHENTICATE);
	}
	
	@Override
	public String getName() {
		return "Subscriber-Profile";
	}
	
}