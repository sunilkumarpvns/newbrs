package com.elitecore.netvertex.service.pcrf.servicepolicy.handler;

import com.elitecore.corenetvertex.constants.PCRFEvent;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyValueConstants;
import com.elitecore.corenetvertex.constants.SyMode;
import com.elitecore.diameterapi.core.common.peer.group.DiameterPeerGroupParameter;
import com.elitecore.diameterapi.diameter.common.util.constant.CommandCode;
import com.elitecore.netvertex.core.conf.impl.DummyNetvertexServerConfiguration;
import com.elitecore.netvertex.core.servicepolicy.ExecutionContext;
import com.elitecore.netvertex.core.util.PCRFRequestBuilder;
import com.elitecore.netvertex.core.util.PCRFResponseBuilder;
import com.elitecore.netvertex.gateway.diameter.conf.DiameterGatewayConfiguration;
import com.elitecore.netvertex.pm.DummyPolicyRepository;
import com.elitecore.netvertex.pm.util.MockBasePackage;
import com.elitecore.netvertex.pm.util.MockProductOffer;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.PCRFResponseListner;
import com.elitecore.netvertex.service.pcrf.PCRFServiceContext;
import com.elitecore.netvertex.service.pcrf.servicepolicy.handler.PCRFSyHandlerTest.NetvertexServerContextImpl;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.EnumSet;

import static junit.framework.Assert.assertSame;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(JUnitParamsRunner.class)
public class PCRFSyHandlerWithPullMode {

	private PCRFSyHandler pcrfSyHandler;
	@Mock private PCRFServiceContext serviceContext;  
	@Mock private ExecutionContext executionContext;
	
	private NetvertexServerContextImpl netvertexServerContext;
	private PCRFResponseBuilder pcrfResponseBuilder;
	private PCRFRequestBuilder pcrfRequestBuilder;
	
	@SuppressWarnings("unchecked")
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		DiameterGatewayConfiguration diameterGatewayConfiguration = mock(DiameterGatewayConfiguration.class);

		netvertexServerContext = spy(new NetvertexServerContextImpl(true));
		when(serviceContext.getServerContext()).thenReturn(netvertexServerContext);
        DummyNetvertexServerConfiguration serverConfiguration = netvertexServerContext.getServerConfiguration();
        serverConfiguration.spyMiscConf();
        when(serverConfiguration.getDiameterGatewayConfiguration(1)).thenReturn(diameterGatewayConfiguration);
		
		pcrfSyHandler = new PCRFSyHandler("test", serviceContext, "1", SyMode.PULL, null);
		pcrfSyHandler.init();
		
		pcrfRequestBuilder = new PCRFRequestBuilder().
				addCoreSessionID("123").
				addAttribute(PCRFKeyConstants.CS_SY_SESSION_ID, "Sy.123");
				
		pcrfResponseBuilder = new PCRFResponseBuilder().addCoreSessionID("123").
										addAttribute(PCRFKeyConstants.CS_SY_SESSION_ID, "Sy.123");

        MockBasePackage mockBasePackage = ((DummyPolicyRepository) (netvertexServerContext.getPolicyRepository())).mockBasePackage();
        mockBasePackage.quotaProfileIsSy().mockQuotaProfie();


		DummyPolicyRepository policyRepository = (DummyPolicyRepository) netvertexServerContext.getPolicyRepository();
		MockProductOffer mockProductOffer = MockProductOffer.create(policyRepository, "productOfferId1", "productOfferId2");
		mockProductOffer.addDataPackage(mockBasePackage);
		policyRepository.addProductOffer(mockProductOffer);

		pcrfResponseBuilder.addAttribute(PCRFKeyConstants.SUB_PRODUCT_OFFER, mockProductOffer.getName());
	}
	
	public static Object[][] data_provider_for_process_should_send_SLR_to_sy_gateway_on_request_except_session_stop_and_gateway_reboot() {
		Object [][] parameters = new Object[PCRFEvent.values().length -2][];
		int index = 0;
		for(PCRFEvent pcrfEvent : PCRFEvent.values()) {
			if(pcrfEvent != PCRFEvent.SESSION_STOP
					&& pcrfEvent != PCRFEvent.GATEWAY_REBOOT)
			parameters[index++] = new Object[]{pcrfEvent};
		}
		
		return parameters;
	}
	
	
	
	@Test
	@Parameters(method="data_provider_for_process_should_send_SLR_to_sy_gateway_on_request_except_session_stop_and_gateway_reboot")
	public void test_process_should_send_SLR_to_sy_gateway_on_request_except_session_stop_and_gateway_reboot(PCRFEvent event) {

        PCRFResponse pcrfResponse = pcrfResponseBuilder.build();
        pcrfSyHandler.handleRequest(pcrfRequestBuilder.build(), pcrfResponse, executionContext);
		
		assertSame(CommandCode.SPENDING_LIMIT, netvertexServerContext.getCommandCode());
		assertSame(pcrfResponse, netvertexServerContext.getPCRFResponse());
	}
	
	@Test
	public void test_process_should_fetch_previous_communicated_peer_from_diameter_session_on_re_authorization() {
		
		String syGateway = "syGateway";
		
		PCRFRequest pcrfRequest = pcrfRequestBuilder.addAttribute(PCRFKeyConstants.CS_SY_GATEWAY_NAME, syGateway).build();
		PCRFResponse pcrfResponse = pcrfResponseBuilder.addAttribute(PCRFKeyConstants.CS_SY_GATEWAY_NAME, syGateway).build();
		
		pcrfRequest.setPCRFEvents(EnumSet.of(PCRFEvent.REAUTHORIZE));
		
		
		String value = "newGateway";
		when(netvertexServerContext.getSyGatewayName(pcrfRequest.getAttribute(PCRFKeyConstants.CS_SY_SESSION_ID.val))).thenReturn(value);
		
		pcrfSyHandler.handleRequest(pcrfRequest, pcrfResponse, executionContext);
		
		assertSame(value, netvertexServerContext.getPrimaryGatewayName());
		assertSame(value, pcrfResponse.getAttribute(PCRFKeyConstants.CS_SY_GATEWAY_NAME.val));
		assertSame(value, pcrfRequest.getAttribute(PCRFKeyConstants.CS_SY_GATEWAY_NAME.val));
	}
	
	@Test
	public void test_process_should_fetch_previous_communicated_peer_from_request_if_peer_not_found_from_diameter_session_on_re_authorization() {
		
		String syGateway = "syGateway";
		
		PCRFRequest pcrfRequest = pcrfRequestBuilder.addAttribute(PCRFKeyConstants.CS_SY_GATEWAY_NAME, syGateway).build();
		PCRFResponse pcrfResponse = pcrfResponseBuilder.addAttribute(PCRFKeyConstants.CS_SY_GATEWAY_NAME, syGateway).build();
		
		EnumSet<PCRFEvent> pcrfEvents = EnumSet.of(PCRFEvent.REAUTHORIZE);
		pcrfRequest.setPCRFEvents(pcrfEvents);
		
		pcrfSyHandler.handleRequest(pcrfRequest, pcrfResponse, executionContext);
		
		assertSame(syGateway, netvertexServerContext.getPrimaryGatewayName());
	}
	
	@Test
	public void test_process_should_not_provide_communicated_peer_if_not_found_from_request_as_well_from_from_diameter_session_on_re_authorization() {
				
		PCRFRequest pcrfRequest = pcrfRequestBuilder.build();
		PCRFResponse pcrfResponse = pcrfResponseBuilder.build();
		
		EnumSet<PCRFEvent> pcrfEvents = EnumSet.of(PCRFEvent.REAUTHORIZE);
		pcrfRequest.setPCRFEvents(pcrfEvents);
		
		pcrfSyHandler.handleRequest(pcrfRequest, pcrfResponse, executionContext);
		
		assertNull(netvertexServerContext.getPrimaryGatewayName());
	}
	
	@Test
	public void test_process_should_fetch_previous_communicated_peer_from_request_on_event_other_than_re_authorization_session_stop_and_gateway_reboot() {
		
		String syGateway = "syGateway";
		
		PCRFRequest pcrfRequest = pcrfRequestBuilder.addAttribute(PCRFKeyConstants.CS_SY_GATEWAY_NAME, syGateway).build();
		PCRFResponse pcrfResponse = pcrfResponseBuilder.addAttribute(PCRFKeyConstants.CS_SY_GATEWAY_NAME, syGateway).build();
		
		pcrfRequest.getPCRFEvents().remove(PCRFEvent.SESSION_STOP);
		pcrfRequest.getPCRFEvents().remove(PCRFEvent.GATEWAY_REBOOT);
		pcrfRequest.getPCRFEvents().remove(PCRFEvent.REAUTHORIZE);
		
		pcrfSyHandler.handleRequest(pcrfRequest, pcrfResponse, executionContext);
		
		assertSame(syGateway, netvertexServerContext.getPrimaryGatewayName());
	}
	
	@Test
	public void test_process_should_not_provide_peer_if_not_found_from_request_on_event_other_than_re_authorization_session_stop_and_gateway_reboot() {
		
		PCRFRequest pcrfRequest = pcrfRequestBuilder.build();
		PCRFResponse pcrfResponse = pcrfResponseBuilder.build();
		
		pcrfRequest.getPCRFEvents().remove(PCRFEvent.SESSION_STOP);
		pcrfRequest.getPCRFEvents().remove(PCRFEvent.GATEWAY_REBOOT);
		pcrfRequest.getPCRFEvents().remove(PCRFEvent.REAUTHORIZE);
		
		pcrfSyHandler.handleRequest(pcrfRequest, pcrfResponse, executionContext);
		
		assertNull(netvertexServerContext.getPrimaryGatewayName());
	}
	
	
	
	@Test
	public void test_process_should_not_provide_peer_if_sy_session_id_not_exist() {
		
		String syGateway = "syGateway";
		
		PCRFRequest pcrfRequest = pcrfRequestBuilder.addAttribute(PCRFKeyConstants.CS_SY_GATEWAY_NAME, syGateway).build();
		PCRFResponse pcrfResponse = pcrfResponseBuilder.addAttribute(PCRFKeyConstants.CS_SY_GATEWAY_NAME, syGateway).build();
		
		pcrfRequest.removeAttribute(PCRFKeyConstants.CS_SY_SESSION_ID.val);
		pcrfResponse.removeAttribute(PCRFKeyConstants.CS_SY_SESSION_ID.val);
		
		pcrfRequest.getPCRFEvents().remove(PCRFEvent.SESSION_STOP);
		pcrfRequest.getPCRFEvents().remove(PCRFEvent.GATEWAY_REBOOT);
		
		pcrfSyHandler.handleRequest(pcrfRequest, pcrfResponse, executionContext);
		
		assertNull(netvertexServerContext.getPrimaryGatewayName());
	}
	
	@Test
	public void test_process_should_set_SyCommunication_to_FAIL_on_sy_communication_failuer() {
		
		
		PCRFRequest pcrfRequest = pcrfRequestBuilder.build();
		PCRFResponse pcrfResponse = pcrfResponseBuilder.build();
		
		pcrfRequest.removeAttribute(PCRFKeyConstants.CS_SY_SESSION_ID.val);
		pcrfResponse.removeAttribute(PCRFKeyConstants.CS_SY_SESSION_ID.val);
		
		pcrfRequest.getPCRFEvents().remove(PCRFEvent.SESSION_STOP);
		pcrfRequest.getPCRFEvents().remove(PCRFEvent.GATEWAY_REBOOT);
		
		when(netvertexServerContext.sendSyRequest(any(PCRFResponse.class), 
				any(DiameterPeerGroupParameter.class), 
				Mockito.anyString(), 
				any(PCRFResponseListner.class), 
				any(CommandCode.class))).thenReturn(false);


        pcrfSyHandler.handleRequest(pcrfRequest, pcrfResponse, executionContext);
		
		assertSame(PCRFKeyValueConstants.SY_COMMUNICATION_FAIL.val, pcrfResponse.getAttribute(PCRFKeyConstants.SY_COMMUNICATION.val));
	}
	
	@Test
	public void test_process_should_set_funther_processing_false_and_execution_completed_false_on_success() {
		
		
		PCRFRequest pcrfRequest = pcrfRequestBuilder.build();
		PCRFResponse pcrfResponse = pcrfResponseBuilder.build();
		
		pcrfRequest.removeAttribute(PCRFKeyConstants.CS_SY_SESSION_ID.val);
		pcrfResponse.removeAttribute(PCRFKeyConstants.CS_SY_SESSION_ID.val);
		
		pcrfRequest.getPCRFEvents().remove(PCRFEvent.SESSION_STOP);
		pcrfRequest.getPCRFEvents().remove(PCRFEvent.GATEWAY_REBOOT);
		
		pcrfSyHandler.handleRequest(pcrfRequest, pcrfResponse, executionContext);
		
		
		assertFalse(pcrfResponse.isProcessingCompleted());
		assertFalse(pcrfResponse.isFurtherProcessingRequired());
	}
	
	@After
	public void checkNotSendSTR() {
		verify(netvertexServerContext, times(0)).sendSyRequest(any(PCRFResponse.class), 
				any(DiameterPeerGroupParameter.class), Mockito.anyString(), any(PCRFResponseListner.class), Mockito.same(CommandCode.SESSION_TERMINATION));
	}
}
