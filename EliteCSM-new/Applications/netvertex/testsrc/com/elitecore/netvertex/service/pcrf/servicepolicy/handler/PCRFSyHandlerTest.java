package com.elitecore.netvertex.service.pcrf.servicepolicy.handler;

import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.serverx.sessionx.SessionData;
import com.elitecore.core.serverx.sessionx.impl.SessionDataImpl;
import com.elitecore.corenetvertex.constants.PCRFEvent;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyValueConstants;
import com.elitecore.corenetvertex.constants.SyMode;
import com.elitecore.diameterapi.core.common.peer.group.DiameterPeerGroupParameter;
import com.elitecore.diameterapi.diameter.common.util.constant.CommandCode;
import com.elitecore.netvertex.core.DummyNetvertexServerContextImpl;
import com.elitecore.netvertex.core.conf.MiscellaneousConfiguration;
import com.elitecore.netvertex.core.conf.impl.DummyNetvertexServerConfiguration;
import com.elitecore.netvertex.core.servicepolicy.ExecutionContext;
import com.elitecore.netvertex.core.session.SessionLocator;
import com.elitecore.netvertex.core.util.PCRFRequestBuilder;
import com.elitecore.netvertex.core.util.PCRFResponseBuilder;
import com.elitecore.netvertex.service.pcrf.*;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;

import static junit.framework.Assert.assertSame;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(Suite.class)
@SuiteClasses(value={PCRFSyHandlerWithPullMode.class,
					 PCRFSyHandlerWithPushMode.class,
					 PCRFSyHandlerTest.CommonTestCases.class,
		 			 PCRFSyHandlerTest.IsApplicabled.class}
			)
public class PCRFSyHandlerTest {

	@RunWith(JUnitParamsRunner.class)
	public static class CommonTestCases {
		
		private static final String NULL = "Null";
		private static final String EMPTY = "Empty";
		private PCRFSyHandler pcrfSyHandler;
		@Mock private PCRFServiceContext serviceContext;
		@Mock private ExecutionContext executionContext;
		@Mock private SessionLocator sessionLocator;
		
		private NetvertexServerContextImpl netvertexServerContext;
		private PCRFResponseBuilder pcrfResponseBuilder;
		private PCRFRequestBuilder pcrfRequestBuilder;

		@Rule public ExpectedException expectedException = ExpectedException.none();
		
		@SuppressWarnings("unchecked")
		@Before
		public void setUp() throws Exception {
			MockitoAnnotations.initMocks(this);

			DummyNetvertexServerConfiguration netvertexServerConfiguration = DummyNetvertexServerConfiguration.spy();
			netvertexServerContext = spy(new NetvertexServerContextImpl(true));
			when(serviceContext.getServerContext()).thenReturn(netvertexServerContext);
			when(netvertexServerContext.getServerConfiguration()).thenReturn(netvertexServerConfiguration);
			doReturn(true).when(netvertexServerContext).isLicenseValid(any(String.class),any(String.class));
            netvertexServerConfiguration.spyDiameterGatewayConfFor(1);
            netvertexServerConfiguration.spyMiscConf();
			

			pcrfRequestBuilder = new PCRFRequestBuilder().
					addCoreSessionID("123").
					addAttribute(PCRFKeyConstants.CS_SY_SESSION_ID, "Sy.123");
					
			pcrfResponseBuilder = new PCRFResponseBuilder().addCoreSessionID("123").
											addAttribute(PCRFKeyConstants.CS_SY_SESSION_ID, "Sy.123");
		}
		
		@Test
		@Parameters(value={EMPTY,NULL})
		public void test_should_throw_initialization_fail_exception_if_sy_gateway_not_provided(String option) throws Exception {
			
			if(EMPTY.equals(option)) {				
				pcrfSyHandler = new PCRFSyHandler("test", serviceContext, null, SyMode.PULL, null);
			} else {
				pcrfSyHandler = new PCRFSyHandler("test", serviceContext, null, SyMode.PULL, null);
			}
			
			expectedException.expect(InitializationFailedException.class);
			expectedException.expectMessage("Sy gateway is empty");
			pcrfSyHandler.init();
		}

		@Test
		@Parameters(value={"PULL","PUSH"})
		public void test_should_send_STR_on_SessionStop_event_if_sy_session_id_found_in_response(String syMode) throws Exception {
			pcrfSyHandler = new PCRFSyHandler("test", serviceContext, "1", SyMode.fromValue(syMode), null);
			pcrfSyHandler.init();

			PCRFRequest pcrfRequest = pcrfRequestBuilder.build();
			PCRFResponse pcrfResponse = pcrfResponseBuilder.build();

			pcrfRequest.setPCRFEvents(EnumSet.of(PCRFEvent.SESSION_STOP));

			pcrfSyHandler.handleRequest(pcrfRequest, pcrfResponse, executionContext);
			verifySendSTR(1);

		}

		@Test
		public void test_should_not_process_any_request_if_there_is_no_sy_license() throws Exception {
			doReturn(false).when(netvertexServerContext).isLicenseValid(any(String.class),any(String.class));
			pcrfSyHandler = new PCRFSyHandler("test", serviceContext, "1", SyMode.fromValue("PULL"), null);
			pcrfSyHandler.init();

			PCRFRequest pcrfRequest = pcrfRequestBuilder.build();
			PCRFResponse pcrfResponse = pcrfResponseBuilder.build();

			pcrfRequest.setPCRFEvents(EnumSet.of(PCRFEvent.SESSION_STOP));

			pcrfSyHandler.handleRequest(pcrfRequest, pcrfResponse, executionContext);
			verifySendSTR(0);

		}
		
		@Test
		@Parameters(value={"PULL","PUSH"})
		public void test_should_not_send_STR_on_SessionStop_event_if_sy_session_id_found_in_response(String syMode) throws Exception {
			pcrfSyHandler = new PCRFSyHandler("test", serviceContext, "1", SyMode.fromValue(syMode), null);
			pcrfSyHandler.init();
			
			PCRFRequest pcrfRequest = pcrfRequestBuilder.build();
			PCRFResponse pcrfResponse = pcrfResponseBuilder.build();
			
			pcrfRequest.removeAttribute(PCRFKeyConstants.CS_SY_SESSION_ID.val);
			pcrfResponse.removeAttribute(PCRFKeyConstants.CS_SY_SESSION_ID.val);
			
			pcrfRequest.setPCRFEvents(EnumSet.of(PCRFEvent.SESSION_STOP));
			
			pcrfSyHandler.handleRequest(pcrfRequest, pcrfResponse, executionContext);
			verifySendSTR(0);
			
		}
		
		@Test
		@Parameters(value={"PULL","PUSH"})
		public void test_should_not_send_STR_on_SessionStop_event_if_sy_session_id_not_found_in_response(String syMode) throws Exception {
			pcrfSyHandler = new PCRFSyHandler("test", serviceContext, "1", SyMode.fromValue(syMode), null);
			pcrfSyHandler.init();
			
			PCRFRequest pcrfRequest = pcrfRequestBuilder.build();
			PCRFResponse pcrfResponse = pcrfResponseBuilder.build();
			
			pcrfRequest.removeAttribute(PCRFKeyConstants.CS_SY_SESSION_ID.val);
			pcrfRequest.setPCRFEvents(EnumSet.of(PCRFEvent.SESSION_STOP));
			
			pcrfSyHandler.handleRequest(pcrfRequest, pcrfResponse, executionContext);
			verifySendSTR(1);
			
		}
		
		@Test
		@Parameters(value={"PULL","PUSH"})
		public void test_should_provide_communicated_gateway_name_from_request_if_present_on_SessionStop_event(String syMode) throws Exception {
			pcrfSyHandler = new PCRFSyHandler("test", serviceContext, "1", SyMode.fromValue(syMode), null);
			pcrfSyHandler.init();
			
			String gatewayName = "OCS";
			PCRFRequest pcrfRequest = pcrfRequestBuilder.addAttribute(PCRFKeyConstants.CS_SY_GATEWAY_NAME, gatewayName).build();
			PCRFResponse pcrfResponse = pcrfResponseBuilder.addAttribute(PCRFKeyConstants.CS_SY_GATEWAY_NAME, gatewayName).build();
			
			
			pcrfRequest.removeAttribute(PCRFKeyConstants.CS_SY_SESSION_ID.val);
			pcrfRequest.setPCRFEvents(EnumSet.of(PCRFEvent.SESSION_STOP));
			
			pcrfSyHandler.handleRequest(pcrfRequest, pcrfResponse, executionContext);
			assertSame(gatewayName, netvertexServerContext.getPrimaryGatewayName());
			verifySendSTR(1);
			
			
		}
		
		@Test
		@Parameters(value={"PULL","PUSH"})
		public void test_should_not_provide_communicated_gateway_name_if_not_present_in_request(String syMode) throws Exception {
			pcrfSyHandler = new PCRFSyHandler("test", serviceContext, "1", SyMode.fromValue(syMode), null);
			pcrfSyHandler.init();
			
			PCRFRequest pcrfRequest = pcrfRequestBuilder.build();
			PCRFResponse pcrfResponse = pcrfResponseBuilder.build();
			
			
			pcrfRequest.removeAttribute(PCRFKeyConstants.CS_SY_SESSION_ID.val);
			pcrfRequest.setPCRFEvents(EnumSet.of(PCRFEvent.SESSION_STOP));
			
			pcrfSyHandler.handleRequest(pcrfRequest, pcrfResponse, executionContext);
			assertNull(netvertexServerContext.getPrimaryGatewayName());
			verifySendSTR(1);
			
			
		}
		
		@Test
		@Parameters(value={"PULL","PUSH"})
		public void test_should_send_STR_to_each_gateway_session_on_GatewayReboot_event_if_sy_session_id_found_in_response(String syMode) throws Exception {
			
			
			List<SessionData> createSessionDatas = createSessionDatas(2);
		
			when(sessionLocator.getCoreSessionByGatewayAddress(Mockito.matches("OCS"))).thenReturn(createSessionDatas);
			
			pcrfSyHandler = new PCRFSyHandler("test", serviceContext, "1", SyMode.fromValue(syMode), sessionLocator);
			pcrfSyHandler.init();
			
			PCRFRequest pcrfRequest = pcrfRequestBuilder.addAttribute(PCRFKeyConstants.CS_GATEWAY_ADDRESS, "OCS").build();
			PCRFResponse pcrfResponse = pcrfResponseBuilder.addAttribute(PCRFKeyConstants.CS_GATEWAY_ADDRESS, "OCS").build();
			
			pcrfRequest.setPCRFEvents(EnumSet.of(PCRFEvent.GATEWAY_REBOOT));
			
			
			pcrfSyHandler.handleRequest(pcrfRequest, pcrfResponse, executionContext);
			verifySendSTR(2);
			
		}
		
		@Test
		@Parameters(value={"PULL","PUSH"})
		public void test_should_not_send_STR_if_empty_session_list_found_on_GatewayReboot_event_if_sy_session_id_found_in_response(String syMode) throws Exception {
			
			when(sessionLocator.getCoreSessionByGatewayAddress(Mockito.matches("OCS"))).thenReturn(Collections.<SessionData>emptyList());
			pcrfSyHandler = new PCRFSyHandler("test", serviceContext, "1", SyMode.fromValue(syMode), sessionLocator);
			pcrfSyHandler.init();
			
			PCRFRequest pcrfRequest = pcrfRequestBuilder.addAttribute(PCRFKeyConstants.CS_GATEWAY_ADDRESS, "OCS").build();
			PCRFResponse pcrfResponse = pcrfResponseBuilder.addAttribute(PCRFKeyConstants.CS_GATEWAY_ADDRESS, "OCS").build();
			
			pcrfRequest.setPCRFEvents(EnumSet.of(PCRFEvent.GATEWAY_REBOOT));
			
			
			pcrfSyHandler.handleRequest(pcrfRequest, pcrfResponse, executionContext);
			verifySendSTR(0);
			
		}
		
		@Test
		@Parameters(value={"PULL","PUSH"})
		public void test_should_not_send_STR_if_no_gateway_session_found_on_GatewayReboot_event_if_sy_session_id_found_in_response(String syMode) throws Exception {
			
			pcrfSyHandler = new PCRFSyHandler("test", serviceContext, "1", SyMode.fromValue(syMode), sessionLocator);
			pcrfSyHandler.init();
			
			PCRFRequest pcrfRequest = pcrfRequestBuilder.addAttribute(PCRFKeyConstants.CS_GATEWAY_ADDRESS, "OCS").build();
			PCRFResponse pcrfResponse = pcrfResponseBuilder.addAttribute(PCRFKeyConstants.CS_GATEWAY_ADDRESS, "OCS").build();
			
			pcrfRequest.setPCRFEvents(EnumSet.of(PCRFEvent.GATEWAY_REBOOT));
			
			
			pcrfSyHandler.handleRequest(pcrfRequest, pcrfResponse, executionContext);
			verifySendSTR(0);
			
		}
		
		@Test
		@Parameters(value={"PULL","PUSH"})
		public void test_should_skipp_STR_to_gateway_session_on_GatewayReboot_event_if_sy_session_id_not_found_from_session(String syMode) throws Exception {
			
			
			List<SessionData> createSessionDatas = createSessionDatas(2);
			Date currentTime = new Date(System.currentTimeMillis());
			SessionData sessionData = new SessionDataImpl("test", currentTime, currentTime);
			sessionData.addValue(PCRFKeyConstants.CS_SESSION_ID.val, "session");
			sessionData.addValue(PCRFKeyConstants.CS_CORESESSION_ID.val, "coreSession1");
			sessionData.addValue(PCRFKeyConstants.CS_GATEWAY_ADDRESS.val, "OCS");
			createSessionDatas.add(sessionData);
			
		
			when(sessionLocator.getCoreSessionByGatewayAddress(Mockito.matches("OCS"))).thenReturn(createSessionDatas);
			
			pcrfSyHandler = new PCRFSyHandler("test", serviceContext, "1", SyMode.fromValue(syMode), sessionLocator);
			pcrfSyHandler.init();
			
			PCRFRequest pcrfRequest = pcrfRequestBuilder.addAttribute(PCRFKeyConstants.CS_GATEWAY_ADDRESS, "OCS").build();
			PCRFResponse pcrfResponse = pcrfResponseBuilder.addAttribute(PCRFKeyConstants.CS_GATEWAY_ADDRESS, "OCS").build();
			
			pcrfRequest.setPCRFEvents(EnumSet.of(PCRFEvent.GATEWAY_REBOOT));
			
			
			pcrfSyHandler.handleRequest(pcrfRequest, pcrfResponse, executionContext);
			verifySendSTR(2);
			
		}
		
		@Test
		@Parameters(value={"PULL","PUSH"})
		public void test_should_send_communicated_gateway_if_found_in_session_in_STR_to_gateway_session_on_GatewayReboot_event(String syMode) throws Exception {
			
			List<SessionData> createSessionDatas = createSessionDatas(2);
			String syGateway = "OCS";
			createSessionDatas.get(0).addValue(PCRFKeyConstants.CS_SY_GATEWAY_NAME.val, syGateway);
		
			when(sessionLocator.getCoreSessionByGatewayAddress(Mockito.matches("PW-G"))).thenReturn(createSessionDatas);
			
			pcrfSyHandler = new PCRFSyHandler("test", serviceContext, "1", SyMode.fromValue(syMode), sessionLocator);
			pcrfSyHandler.init();
			
			PCRFRequest pcrfRequest = pcrfRequestBuilder.addAttribute(PCRFKeyConstants.CS_GATEWAY_ADDRESS, "PW-G").build();
			PCRFResponse pcrfResponse = pcrfResponseBuilder.addAttribute(PCRFKeyConstants.CS_GATEWAY_ADDRESS, "PW-G").build();
			
			pcrfRequest.setPCRFEvents(EnumSet.of(PCRFEvent.GATEWAY_REBOOT));
			
			pcrfSyHandler.handleRequest(pcrfRequest, pcrfResponse, executionContext);
			verify(netvertexServerContext, times(1)).sendSyRequest(any(PCRFResponse.class), 
					any(DiameterPeerGroupParameter.class), same(syGateway), any(PCRFResponseListner.class), Mockito.same(CommandCode.SESSION_TERMINATION));
			
			verify(netvertexServerContext, times(1)).sendSyRequest(any(PCRFResponse.class), 
					any(DiameterPeerGroupParameter.class), Mockito.isNull(String.class), any(PCRFResponseListner.class), Mockito.same(CommandCode.SESSION_TERMINATION));
			
		}

		private List<SessionData> createSessionDatas(int noOfSessionData) {
			
			ArrayList<SessionData> sessionDatas = new ArrayList<SessionData>(noOfSessionData);			
			Date currentTime = new Date(System.currentTimeMillis());
			
			for(int index = 0; index < noOfSessionData; index++) {	
				SessionData sessionData = new SessionDataImpl("test", currentTime, currentTime);
				sessionData.addValue(PCRFKeyConstants.CS_SESSION_ID.val, "session" + index);
				sessionData.addValue(PCRFKeyConstants.CS_CORESESSION_ID.val, "coreSession1" + index);
				sessionData.addValue(PCRFKeyConstants.CS_SY_SESSION_ID.val, "sySession" + index);
				sessionData.addValue(PCRFKeyConstants.CS_GATEWAY_ADDRESS.val, "PW-G");
				
				sessionDatas.add(sessionData);
			}
			
			return sessionDatas;
		}


		
		@After
		public void checkNotSendSLR() {
			verify(netvertexServerContext, times(0)).sendSyRequest(any(PCRFResponse.class), 
					any(DiameterPeerGroupParameter.class), Mockito.anyString(), any(PCRFResponseListner.class), Mockito.same(CommandCode.SPENDING_LIMIT));
		}
		
		private void verifySendSTR(int times) {
			verify(netvertexServerContext, times(times)).sendSyRequest(any(PCRFResponse.class), 
					any(DiameterPeerGroupParameter.class), Mockito.anyString(), any(PCRFResponseListner.class), Mockito.same(CommandCode.SESSION_TERMINATION));
		}
		
	}
	
	static class NetvertexServerContextImpl extends DummyNetvertexServerContextImpl {
		
		private PCRFResponse pcrfResponse;
		private DiameterPeerGroupParameter diameterPeerGroupParameter;
		private String primaryGatewayName;
		private PCRFResponseListner responseListner;
		private CommandCode commandCode;
		private final boolean result;
		
		public NetvertexServerContextImpl(boolean result) {
			super();
			this.result = result;

		}

		@Override
		public boolean sendSyRequest(PCRFResponse pcrfResponse, DiameterPeerGroupParameter diameterPeerGroupParameter, String primaryGatewayName,
				PCRFResponseListner responseListner, CommandCode commandCode) {
					this.pcrfResponse = pcrfResponse;
					this.diameterPeerGroupParameter = diameterPeerGroupParameter;
					this.primaryGatewayName = primaryGatewayName;
					this.responseListner = responseListner;
					this.commandCode = commandCode;

					return result;
		}

		public CommandCode getCommandCode() {
			return commandCode;
		}

		public PCRFResponse getPCRFResponse() {
			return pcrfResponse;
		}

		public String getPrimaryGatewayName() {
			return primaryGatewayName;
		}
		
	}

	@RunWith(HierarchicalContextRunner.class)
	public static class IsApplicabled {
		private PCRFSyHandler pcrfSyHandler;

		public class OnSNRSessionReAuthCause {

			private PCRFRequest pcrfRequest;
			private PCRFResponse pcrfResponse;
			private MiscellaneousConfiguration miscellaneousConfiguration;

			@Before
			public void setUp() throws InitializationFailedException {
				MockitoAnnotations.initMocks(this);
				DummyPCRFServiceContext serviceContext = DummyPCRFServiceContext.spy();
				miscellaneousConfiguration = serviceContext.getServerContext().getServerConfiguration().spyMiscConf();
				pcrfSyHandler = new PCRFSyHandler("test", serviceContext, null, SyMode.PULL, null);

				pcrfRequest = new PCRFRequestBuilder().build();
				pcrfResponse = new PCRFResponseBuilder().addAttribute(PCRFKeyConstants.RE_AUTH_CAUSE, PCRFKeyValueConstants.RE_AUTH_CAUSE_SNR.val).build();
			}

			@Test
			@Parameters(value={"PULL","PUSH"})
			public void test_PCRFSyHandlerAllowProcess_On_SLREnabledOnSLRFlagEnabled() throws InitializationFailedException {
				when(miscellaneousConfiguration.getSLREnabledOnSNR()).thenReturn(true);
				assertTrue("Sy Handler not applicable when Session Re-Auth-Cause is SNR", pcrfSyHandler.isApplicable(pcrfRequest, pcrfResponse));
			}
		}
	}
}
