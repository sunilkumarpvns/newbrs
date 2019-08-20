package com.elitecore.aaa.diameter.policies.tgppserver;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.elitecore.aaa.core.conf.context.AAAConfigurationContext;
import com.elitecore.aaa.core.diameter.DummyDiameterDictionary;
import com.elitecore.aaa.diameter.policies.applicationpolicy.DiameterTGPPServerPolicyContainer;
import com.elitecore.aaa.diameter.policies.applicationpolicy.conf.DiameterServicePolicyConfiguration;
import com.elitecore.aaa.diameter.policies.applicationpolicy.defaultresponsebehavior.DefaultResponseBehavior.DefaultResponseBehaviorType;
import com.elitecore.aaa.diameter.service.DiameterServiceContext;
import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.service.application.ApplicationResponse;
import com.elitecore.aaa.diameter.service.application.handlers.DiameterApplicationHandler;
import com.elitecore.aaa.diameter.service.application.handlers.conf.DiameterApplicationHandlerData;
import com.elitecore.aaa.diameter.service.application.handlers.conf.DiameterPostResponseHandlerData;
import com.elitecore.aaa.diameter.sessionmanager.DiameterSessionManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.serverx.servicepolicy.ServicePolicy;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.core.servicex.ServiceContext;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.routerx.agent.DiameterTestSupport;
import com.elitecore.diameterapi.diameter.common.util.constant.CommandCode;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import net.sf.json.JSONObject;

@RunWith(HierarchicalContextRunner.class)
public class TGPPServerPolicyDefaultResponseBehaviorTest extends DiameterTestSupport {

	private ApplicationRequest appRequest;
	private ApplicationResponse appResponse;
	private TGPPServerPolicy tgppPolicy;

	@BeforeClass
	public static void loadDicationary() {
		DummyDiameterDictionary.getInstance();
	}

	@Before
	public void setUp() throws Exception {
		super.setUp();
		DiameterRequest request = createCCInitialRequest();
		appRequest = new ApplicationRequest(request);
		appResponse = new ApplicationResponse(request);
	}

	public class SingleCommandCodeFlow {
		
		@Test
		public void appliesDefaultResponseBehaviorIfOnlyHandlerConfiguredIsNotResponsive() throws InitializationFailedException {
			givenHandlers(nonResponsiveHandler());

			whenPolicyIsApplied();

			assertResponseBehaviorIsApplied(appResponse);
		}

		@Test
		public void doesNotApplyDefaultResponseBehaviorIfOnlyHandlerConfiguredIsResponsive() throws InitializationFailedException {
			givenHandlers(responsiveHandler());

			whenPolicyIsApplied();

			assertResponseBehaviorIsNotApplied(appResponse);
		}

		@Test
		public void appliesDefaultResponseBehaviorWhenAnyHandlerOutOfAllTheHandlersIsNotResponsive() throws InitializationFailedException {
			givenHandlers(responsiveHandler(), nonResponsiveHandler(), responsiveHandler());

			whenPolicyIsApplied();

			assertResponseBehaviorIsApplied(appResponse);
		}

		@Test
		public void doesNotApplyDefaultResponseBehaviorWhenAllHandlerHandlersAreResponsive() throws InitializationFailedException {
			givenHandlers(responsiveHandler(), responsiveHandler());

			whenPolicyIsApplied();

			assertResponseBehaviorIsNotApplied(appResponse);
		}
		
		private void givenHandlers(DiameterApplicationHandlerData... handlersData)
				throws InitializationFailedException {
			TGPPServerPolicyData data = new TGPPServerPolicyData();
			data.setName("Test");
			data.setRuleSet("0:263=\"*\"");
			CommandCodeFlowData commandCodeFlowData = new CommandCodeFlowData();
			commandCodeFlowData.setInterfaceId("0");
			commandCodeFlowData.setCommandCode(CommandCode.CREDIT_CONTROL.code + "");
			for (DiameterApplicationHandlerData handlerData : handlersData) {
				commandCodeFlowData.getHandlersData().add(handlerData);
			}
			DiameterPostResponseHandlerData postResponseHandlerData = new DiameterPostResponseHandlerData();
            commandCodeFlowData.setPostResponseHandlerData(postResponseHandlerData);
			data.getCommandCodeFlows().add(commandCodeFlowData);

			data.setDefaultResponseBehaviorType(DefaultResponseBehaviorType.DROP);
			data.postRead();

			Map<String, DiameterServicePolicyConfiguration> idToPolicyConfigurationMap = new HashMap<String, DiameterServicePolicyConfiguration>();
			idToPolicyConfigurationMap.put(data.getId(), data);

			DiameterTGPPServerPolicyContainerStub container = new DiameterTGPPServerPolicyContainerStub(
					mock(DiameterServiceContext.class), idToPolicyConfigurationMap, null);

			tgppPolicy = (TGPPServerPolicy)(container.getPolicyObject(mock(ServiceContext.class), data.getId(), null));
			tgppPolicy.init();
			appRequest.setApplicationPolicy(tgppPolicy);
		}

	}
	
	public class MultipleCommandCodeFlows {
		
		private CommandCodeFlowData commandCode = new CommandCodeFlowData();
		private CommandCodeFlowData otherCommandCode = new CommandCodeFlowData();
		private ApplicationRequest otherApplicationRequest;
		private ApplicationResponse otherApplicationResponse;
		
		@Before
		public void setUp() {
			commandCode.setCommandCode(CommandCode.CREDIT_CONTROL.code + "");
			otherCommandCode.setCommandCode(CommandCode.RE_AUTHORIZATION.code + "");
			commandCode.setInterfaceId("0");
			otherCommandCode.setInterfaceId("0");
			DiameterRequest rar = createRAR(getPeerOperation(ORIGINATOR_PEER_1[1]).getDiameterPeer().getPeerData());
			otherApplicationRequest = new ApplicationRequest(rar);
			otherApplicationResponse = new ApplicationResponse(rar);
		}
		
		@Test
		public void aNonResponsiveCommandCodeFlowDoesNotAffectRequestHandlingUsingOtherCommandCodeFlow() throws InitializationFailedException {
			givenOneCommandCodeFlowIsResponsiveAndOtherIsNot();
			
			whenRequestIsEligibleForNonResponsiveCommandCodeFlow();
			
			assertResponseBehaviorIsApplied(appResponse);
			
			whenRequestIsEligibleForResponsiveCommandCodeFlow();
			
			assertResponseBehaviorIsNotApplied(otherApplicationResponse);
		}

		private void givenOneCommandCodeFlowIsResponsiveAndOtherIsNot() throws InitializationFailedException {
			commandCode.getHandlersData().add(nonResponsiveHandler());
			otherCommandCode.getHandlersData().add(responsiveHandler());
			
			DiameterPostResponseHandlerData postResponseHandlerData = new DiameterPostResponseHandlerData();
            commandCode.setPostResponseHandlerData(postResponseHandlerData);
            otherCommandCode.setPostResponseHandlerData(postResponseHandlerData);
			initPolicy();
		}
		
		private void whenRequestIsEligibleForResponsiveCommandCodeFlow() {
			tgppPolicy.handleRequest(otherApplicationRequest, otherApplicationResponse, getSession());
		}

		private void whenRequestIsEligibleForNonResponsiveCommandCodeFlow() throws InitializationFailedException {
			tgppPolicy.handleRequest(appRequest, appResponse, getSession());
		}

		private void initPolicy()
				throws InitializationFailedException {
			TGPPServerPolicyData data = new TGPPServerPolicyData();
			data.setName("Test");
			data.setRuleSet("0:263=\"*\"");
			data.getCommandCodeFlows().add(commandCode);
			data.getCommandCodeFlows().add(otherCommandCode);

			data.setDefaultResponseBehaviorType(DefaultResponseBehaviorType.DROP);
			data.postRead();

			Map<String, DiameterServicePolicyConfiguration> idToPolicyConfigurationMap = new HashMap<String, DiameterServicePolicyConfiguration>();
			idToPolicyConfigurationMap.put(data.getId(), data);

			DiameterTGPPServerPolicyContainerStub container = new DiameterTGPPServerPolicyContainerStub(
					mock(DiameterServiceContext.class), idToPolicyConfigurationMap, null);

			tgppPolicy = (TGPPServerPolicy)(container.getPolicyObject(mock(ServiceContext.class), data.getId(), null));
			tgppPolicy.init();
			appRequest.setApplicationPolicy(tgppPolicy);
			otherApplicationRequest.setApplicationPolicy(tgppPolicy);
		}
	}

	private void assertResponseBehaviorIsNotApplied(ApplicationResponse appResponse) {
		assertRequestNotDropped(appRequest, appResponse);
	}

	private void assertResponseBehaviorIsApplied(ApplicationResponse appResponse) {
		assertRequestDropped(appRequest, appResponse);
	}


	private ResponsiveHandlerData responsiveHandler() {
		return new ResponsiveHandlerData();
	}

	private NonResponsiveHandlerData nonResponsiveHandler() {
		return new NonResponsiveHandlerData();
	}

	public void whenPolicyIsApplied() {
		tgppPolicy.handleRequest(appRequest, appResponse, getSession());
	}

	private void assertRequestDropped(ApplicationRequest appRequest,
			ApplicationResponse appResponse) {

		assertTrue(appResponse.isProcessingCompleted());
		assertTrue(appResponse.isMarkedForDropRequest());
		assertFalse(appResponse.isFurtherProcessingRequired());
	}

	private void assertRequestNotDropped(ApplicationRequest appRequest,
			ApplicationResponse appResponse) {
		assertFalse(appResponse.isMarkedForDropRequest());
		assertTrue(appResponse.isFurtherProcessingRequired());
	}

	private class DiameterTGPPServerPolicyContainerStub extends DiameterTGPPServerPolicyContainer {

		public DiameterTGPPServerPolicyContainerStub(ServiceContext serviceContext,
				Map<String, DiameterServicePolicyConfiguration> policyConfMap,
				DiameterSessionManager diameterSessionManager) {
			super(serviceContext, policyConfMap, diameterSessionManager);

		}

		@Override
		public ServicePolicy<ApplicationRequest> getPolicyObject(
				ServiceContext serviceContext, String policyId,
				DiameterSessionManager diameterSessionManager) throws InitializationFailedException {

			return super.getPolicyObject(serviceContext, policyId, diameterSessionManager);
		}
	}

	private class NonResponsiveHandlerData implements DiameterApplicationHandlerData {

		@Override
		public JSONObject toJson() {
			return null;
		}

		@Override
		public boolean isEnabled() {
			return true;
		}

		@Override
		public TGPPServerPolicyData getPolicyData() {
			return null;
		}

		@Override
		public void setPolicyData(TGPPServerPolicyData data) {

		}

		@Override
		public AAAConfigurationContext getConfigurationContext() {
			return null;
		}

		@Override
		public void setConfigurationContext(AAAConfigurationContext context) {

		}

		@Override
		public DiameterApplicationHandler<ApplicationRequest, ApplicationResponse> createHandler(
				DiameterServiceContext context) {
			return new NonResponsiveHandler();
		}

		@Override
		public void postRead() {

		}

		@Override
		public String getHandlerName() {
			return "NON-RESPONSIVE-HANDLER";
		}
	}

	private class NonResponsiveHandler implements DiameterApplicationHandler<ApplicationRequest, ApplicationResponse> {

		@Override
		public void init() throws InitializationFailedException {

		}

		@Override
		public boolean isEligible(ApplicationRequest request,
				ApplicationResponse response) {
			return true;
		}

		@Override
		public void handleRequest(ApplicationRequest request,
				ApplicationResponse response, ISession session) {
			throw new AssertionError("Handle must not be called for a non responsive handler.");
		}

		@Override
		public boolean isResponseBehaviorApplicable() {
			return true;
		}

		@Override
		public void reInit() throws InitializationFailedException {

		}
	}

	private class ResponsiveHandlerData implements DiameterApplicationHandlerData {

		@Override
		public JSONObject toJson() {
			return null;
		}

		@Override
		public boolean isEnabled() {
			return true;
		}

		@Override
		public TGPPServerPolicyData getPolicyData() {
			return null;
		}

		@Override
		public void setPolicyData(TGPPServerPolicyData data) {

		}

		@Override
		public AAAConfigurationContext getConfigurationContext() {
			return null;
		}

		@Override
		public void setConfigurationContext(AAAConfigurationContext context) {

		}

		@Override
		public DiameterApplicationHandler<ApplicationRequest, ApplicationResponse> createHandler(
				DiameterServiceContext context) {
			return new ResponsiveHandler();
		}

		@Override
		public void postRead() {

		}

		@Override
		public String getHandlerName() {
			return "RESPONSIVE-HANDLER";
		}
	}

	private class ResponsiveHandler implements DiameterApplicationHandler<ApplicationRequest, ApplicationResponse> {

		@Override
		public void init() throws InitializationFailedException {

		}

		@Override
		public boolean isEligible(ApplicationRequest request,
				ApplicationResponse response) {
			return true;
		}

		@Override
		public void handleRequest(ApplicationRequest request,
				ApplicationResponse response, ISession session) {

		}

		@Override
		public boolean isResponseBehaviorApplicable() {
			return false;
		}

		@Override
		public void reInit() throws InitializationFailedException {

		}
	}
}
