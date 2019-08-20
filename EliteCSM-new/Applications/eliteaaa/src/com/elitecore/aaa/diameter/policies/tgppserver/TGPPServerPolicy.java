package com.elitecore.aaa.diameter.policies.tgppserver;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import com.elitecore.aaa.diameter.policies.applicationpolicy.ApplicationPolicy;
import com.elitecore.aaa.diameter.policies.applicationpolicy.defaultresponsebehavior.DefaultResponseBehavior;
import com.elitecore.aaa.diameter.service.DiameterServiceContext;
import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.service.application.ApplicationResponse;
import com.elitecore.aaa.diameter.service.application.handlers.DiameterApplicationHandler;
import com.elitecore.aaa.diameter.service.application.handlers.DiameterChainHandler;
import com.elitecore.aaa.diameter.util.DiameterProcessHelper;
import com.elitecore.aaa.radius.service.handlers.RadiusRequestExecutor;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Predicate;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.serverx.servicepolicy.BaseServicePolicy;
import com.elitecore.diameterapi.diameter.common.session.DiameterSession;
import com.elitecore.diameterapi.diameter.common.util.DiameterAVPValueProvider;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;
import com.elitecore.exprlib.parser.expression.LogicalExpression;

public class TGPPServerPolicy extends BaseServicePolicy<ApplicationRequest>
implements ApplicationPolicy {

	static final long ANY_INTERFACE_ID = 0;
	private static final String MODULE = "TGPP-SVR-POLICY";
	private static final Predicate<ApplicationRequest> ANY_INTERFACE_CONDITION = new Predicate<ApplicationRequest>() {

		@Override
		public boolean apply(ApplicationRequest input) {
			return true;
		}
	};

	private LogicalExpression ruleset;
	private TGPPServerPolicyData data;
	private List<CommandCodeFlow> commandCodeFlows = new ArrayList<CommandCodeFlow>();
	private DefaultResponseBehavior defaultResponseBehavior;

	public TGPPServerPolicy(DiameterServiceContext serviceContext, 
			TGPPServerPolicyData data) {
		super(serviceContext);
		this.data = data;
	}
	
	public void setRuleset(LogicalExpression ruleset) {
		this.ruleset = ruleset;
	}

	@Override
	public boolean assignRequest(ApplicationRequest request) {
		return ruleset.evaluate(new DiameterAVPValueProvider(request.getDiameterRequest()));
	}

	@Override
	public String getPolicyName() {
		return data.getName();
	}
	
	public void setDefaultResponseBehavior(
			DefaultResponseBehavior defaultResponseBehavior) {
		this.defaultResponseBehavior = defaultResponseBehavior;
	}
	
	public boolean isSessionManagementEnabled() {
		return data.isSessionManagementEnabled();
	}

	@Override
	public void init() throws InitializationFailedException {

	}

	public void addCommandCodeFlow(CommandCodeFlow commandCodeFlow) {
		this.commandCodeFlows.add(commandCodeFlow);
	}
	
	public CommandCodeFlow create(final CommandCodeFlowData flowData) {
		if (flowData.getInterfaceIds().contains(ANY_INTERFACE_ID)) {
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Command code(s): " + flowData.getCommandCodes() 
						+ " will be served for any interface.");
			}
			return new CommandCodeFlow(flowData, ANY_INTERFACE_CONDITION);
		} else {
			return new CommandCodeFlow(flowData, new Predicate<ApplicationRequest>() {
				
				@Override
				public boolean apply(ApplicationRequest input) {
					return flowData.getInterfaceIds().contains(input.getDiameterRequest().getApplicationID());
				}
			});
		}
	}

	public class CommandCodeFlow implements ApplicationPolicy {

		private CommandCodeFlowData commandCodeFlowData;
		
		private DiameterChainHandler<DiameterApplicationHandler<ApplicationRequest, ApplicationResponse>> chain 
			= new DiameterChainHandler<DiameterApplicationHandler<ApplicationRequest,ApplicationResponse>>();

		private DiameterApplicationHandler<ApplicationRequest, ApplicationResponse> postResponseHandler;
		
		private Predicate<ApplicationRequest> interfaceCondition; 

		CommandCodeFlow(CommandCodeFlowData data, Predicate<ApplicationRequest> interfaceCondition) {
			this.commandCodeFlowData = data;
			this.interfaceCondition = interfaceCondition;
		}

		public void addHandler(DiameterApplicationHandler<ApplicationRequest, ApplicationResponse> handler) {
			chain.addHandler(handler);
		}
		
		public DiameterApplicationHandler<ApplicationRequest, ApplicationResponse> getPostResponseHandler() {
			return postResponseHandler;
		}

		public void setPostResponseHandler(DiameterApplicationHandler<ApplicationRequest, ApplicationResponse> postResponseHandler) {
			this.postResponseHandler = postResponseHandler;
		}

		@Override
		public boolean assignRequest(ApplicationRequest request) {
			return commandCodeFlowData.getCommandCodes().contains(request.getDiameterRequest().getCommandCode())
					&& interfaceCondition.apply(request);
		}

		private RadiusRequestExecutor<ApplicationRequest, ApplicationResponse> createRequestExecutor(ApplicationRequest request, ApplicationResponse response) {
			return new RadiusRequestExecutor<ApplicationRequest, ApplicationResponse>(chain, request, response);
		}

		public @Nullable RadiusRequestExecutor<ApplicationRequest, ApplicationResponse> createPostResponseExecutor(
				ApplicationRequest appRequest, ApplicationResponse appResponse) {
			return new RadiusRequestExecutor<ApplicationRequest, ApplicationResponse>(postResponseHandler, appRequest, appResponse);
		}

		public CommandCodeFlowData getData() {
			return commandCodeFlowData;
		}
		
		@Override
		public void handleRequest(ApplicationRequest appRequest,
				ApplicationResponse appResponse, DiameterSession session) {
			
			if (applyDefaultResponseBehavior(appRequest, appResponse)) {
				if (LogManager.getLogger().isInfoLogLevel()) {
					LogManager.getLogger().info(MODULE, "Default Response behavior applied");
				}
				return;
			}
			
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Executing request with interface id: " 
						+ appRequest.getDiameterRequest().getApplicationID() + " and command code: " 
						+ appRequest.getDiameterRequest().getCommandCode());
			}
			
			RadiusRequestExecutor<ApplicationRequest, ApplicationResponse> executor 
				= createRequestExecutor(appRequest, appResponse);
			appRequest.setExecutor(executor);
			
			if (getData().getPostResponseHandlerData() != null 
					&& Collectionz.isNullOrEmpty(getData().getPostResponseHandlerData().getHandlersData()) == false) {
				
				RadiusRequestExecutor<ApplicationRequest, ApplicationResponse> postResponseExecutor = 
						createPostResponseExecutor(appRequest, appResponse);
				
				appRequest.setPostResponseExecutor(postResponseExecutor);
			}
			
			executor.startRequestExecution(session);
		}

		private boolean applyDefaultResponseBehavior(
				ApplicationRequest appRequest, ApplicationResponse appResponse) {
			boolean isResponseBehanviorApplicable = chain.isResponseBehaviorApplicable();
			if (isResponseBehanviorApplicable) {
				if (LogManager.getLogger().isInfoLogLevel()) {
					LogManager.getLogger().info(MODULE, "Request is eligible for Default Response behavior.");
				}
				defaultResponseBehavior.apply(appRequest, appResponse);
				return true;
			}
			return false;
		}

		@Override
		public String getPolicyName() {
			return TGPPServerPolicy.this.getPolicyName();
		}

		@Override
		public void init() throws InitializationFailedException {
			
		}
	}

	public CommandCodeFlow selectCommandCodeFlow(ApplicationRequest appRequest, ApplicationResponse appResponse) {
		for (CommandCodeFlow commandCodeflow : commandCodeFlows) {
			if (commandCodeflow.assignRequest(appRequest)) {
				return commandCodeflow;
			}
		}
		return null;
	}

	@Override
	public void handleRequest(ApplicationRequest appRequest,
			ApplicationResponse appResponse, DiameterSession session) {
		
		CommandCodeFlow commandCodeFlow = selectCommandCodeFlow(appRequest, appResponse);
		if (commandCodeFlow == null) {
			//TODO alert and log info
			DiameterProcessHelper.rejectResponse(appResponse, ResultCode.DIAMETER_COMMAND_UNSUPPORTED, "Cannot handle command code");
			return;
		}
		
		commandCodeFlow.handleRequest(appRequest, appResponse, session);
	}
}
