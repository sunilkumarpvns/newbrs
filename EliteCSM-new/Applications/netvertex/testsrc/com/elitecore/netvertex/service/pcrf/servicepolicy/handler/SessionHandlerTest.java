package com.elitecore.netvertex.service.pcrf.servicepolicy.handler;

import com.elitecore.core.serverx.sessionx.Criteria;
import com.elitecore.core.serverx.sessionx.SessionException;
import com.elitecore.core.serverx.sessionx.criterion.Restrictions;
import com.elitecore.core.serverx.sessionx.impl.CriteriaImpl;
import com.elitecore.corenetvertex.constants.PCRFEvent;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.SessionTypeConstant;
import com.elitecore.corenetvertex.pm.pkg.PCCRuleImpl;
import com.elitecore.netvertex.core.ddf.CacheAwareDDFTable;
import com.elitecore.netvertex.core.servicepolicy.ExecutionContext;
import com.elitecore.netvertex.core.session.SessionOperation;
import com.elitecore.netvertex.core.util.PCRFPacketUtil;
import com.elitecore.netvertex.core.util.PCRFRequestBuilder;
import com.elitecore.netvertex.core.util.PCRFResponseBuilder;
import com.elitecore.netvertex.service.pcrf.DummyPCRFServiceContext;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.impl.PCRFResponseImpl;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.unitils.reflectionassert.ReflectionAssert;
import org.unitils.reflectionassert.ReflectionComparatorMode;

import java.util.ArrayList;

import static org.mockito.Mockito.*;


@RunWith(JUnitParamsRunner.class)
public class SessionHandlerTest {

    public static final String INR = "INR";
    @Mock SessionOperation sessionOperation;
	
	private SessionHandler sessionHandler;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		sessionHandler = new SessionHandler(new DummyPCRFServiceContext(), sessionOperation);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test_handle_method_should_call_delete_by_core_session_id_when_PCRF_request_is_terminate_request() {
		
		String coreSessionId = "123";

		PCRFRequest pcrfRequest = new PCRFRequestBuilder(PCRFEvent.SESSION_STOP).addSessionType(SessionTypeConstant.RADIUS).addCoreSessionID(coreSessionId).build();
		
		PCRFResponseImpl pcrfResponse = new PCRFResponseImpl();
		
		PCRFPacketUtil.buildPCRFResponse(pcrfRequest, pcrfResponse);

		sessionHandler.handleRequest(pcrfRequest, pcrfResponse, new ExecutionContext(pcrfRequest, null, CacheAwareDDFTable.getInstance(), INR));
		
		verify(sessionOperation, times(1)).deleteCoreSessionByCoreSessionId(coreSessionId);
		verifyNoMoreInteractions(sessionOperation);
		
	}
	
	@Test
	public void test_handle_method_should_call_createCoreSession_when_PCRF_request_is_session_start() {
		
		String coreSessionId = "123";

		PCRFRequest pcrfRequest = new PCRFRequestBuilder(PCRFEvent.SESSION_START).addSessionType(SessionTypeConstant.RADIUS).addCoreSessionID(coreSessionId).build();
		
		PCRFResponseImpl pcrfResponse = new PCRFResponseImpl();
		
		PCRFPacketUtil.buildPCRFResponse(pcrfRequest, pcrfResponse);

		sessionHandler.handleRequest(pcrfRequest, pcrfResponse, new ExecutionContext(pcrfRequest, null, CacheAwareDDFTable.getInstance(),INR));
		
		verify(sessionOperation, times(1)).createCoreSession(pcrfResponse);
		verifyNoMoreInteractions(sessionOperation);
	}

	
	@Test
	public void test_handle_method_should_call_createSessionRule_when_PCRF_request_is_session_start() {
		
		String coreSessionId = "123";
		
		PCCRuleImpl pccRule = new PCCRuleImpl.PCCRuleBuilder("1", "PCC1").build();
		
		PCRFRequest pcrfRequest =  new PCRFRequestBuilder(PCRFEvent.SESSION_START)
											.addCoreSessionID(coreSessionId)
											.addSessionID("sessionId")
				.addSessionType(SessionTypeConstant.RADIUS)
											.addAttribute(PCRFKeyConstants.GX_SESSION_ID.val, "gx-core")
											.build();
		PCRFResponse pcrfResponse = new PCRFResponseBuilder().addSessionType(SessionTypeConstant.RADIUS).setInstallablePCCRules(pccRule).addSessionType(SessionTypeConstant.RX).build();
		
		PCRFPacketUtil.buildPCRFResponse(pcrfRequest, pcrfResponse);


		sessionHandler.handleRequest(pcrfRequest, pcrfResponse, new ExecutionContext(pcrfRequest, null, CacheAwareDDFTable.getInstance(), INR));
		
		PCRFResponse expectedResponse =  new PCRFResponseBuilder()
										.addCoreSessionID(pcrfRequest.getAttribute(PCRFKeyConstants.GX_SESSION_ID.val)+":"+SessionTypeConstant.GX.val)
										.addAttribute(PCRFKeyConstants.CS_AF_SESSION_ID.val, pcrfRequest.getAttribute(PCRFKeyConstants.CS_SESSION_ID.val))
										.setInstallablePCCRules(pccRule)
										.build();
		
		verify(sessionOperation, times(1)).createCoreSession(pcrfResponse);
		verifyNoMoreInteractions(sessionOperation);
	}
	
	
	@Test
	public void test_handle_method_should_call_createCoreSession_when_PCRF_request_is_session_update_and_session_not_found() {
		
		String coreSessionId = "123";

		PCRFRequest pcrfRequest = new PCRFRequestBuilder(PCRFEvent.SESSION_UPDATE).addSessionType(SessionTypeConstant.RADIUS).addCoreSessionID(coreSessionId).build();
		
		PCRFResponseImpl pcrfResponse = new PCRFResponseImpl();
		
		PCRFPacketUtil.buildPCRFResponse(pcrfRequest, pcrfResponse);

		sessionHandler.handleRequest(pcrfRequest, pcrfResponse, new ExecutionContext(pcrfRequest, null, CacheAwareDDFTable.getInstance(), INR));
		
		verify(sessionOperation, times(1)).createCoreSession(pcrfResponse);
		verifyNoMoreInteractions(sessionOperation);
	}
	
	@Test
	public void test_handle_method_should_call_createCoreSession_when_PCRF_request_is_session_reset_and_session_not_found() {
		
		String coreSessionId = "123";

		PCRFRequest pcrfRequest = new PCRFRequestBuilder(PCRFEvent.SESSION_RESET).addSessionType(SessionTypeConstant.RADIUS).addCoreSessionID(coreSessionId).build();
		
		PCRFResponseImpl pcrfResponse = new PCRFResponseImpl();
		
		PCRFPacketUtil.buildPCRFResponse(pcrfRequest, pcrfResponse);

		sessionHandler.handleRequest(pcrfRequest, pcrfResponse, new ExecutionContext(pcrfRequest, null, CacheAwareDDFTable.getInstance(), INR));
		
		verify(sessionOperation, times(1)).createCoreSession(pcrfResponse);
		verifyNoMoreInteractions(sessionOperation);
	}
	
	
	@Test
	public void test_handle_method_should_call_updateCoreSession_when_PCRF_request_is_session_update() {
		
		String coreSessionId = "123";

		PCRFRequest pcrfRequest = new PCRFRequestBuilder(PCRFEvent.SESSION_UPDATE).addSessionType(SessionTypeConstant.RADIUS).addCoreSessionID(coreSessionId).build();
		
		pcrfRequest.setSessionFound(true);
		
		PCRFResponseImpl pcrfResponse = new PCRFResponseImpl();
		
		PCRFPacketUtil.buildPCRFResponse(pcrfRequest, pcrfResponse);

		sessionHandler.handleRequest(pcrfRequest, pcrfResponse, new ExecutionContext(pcrfRequest, null, CacheAwareDDFTable.getInstance(), INR));
		
		verify(sessionOperation, times(1)).updateCoreSession(pcrfResponse);
		verifyNoMoreInteractions(sessionOperation);
	}
	
	@Test
	public void test_handle_method_should_call_updateCoreSession_when_PCRF_request_is_session_reset() {
		
		String coreSessionId = "123";

		PCRFRequest pcrfRequest = new PCRFRequestBuilder(PCRFEvent.SESSION_RESET).addSessionType(SessionTypeConstant.RADIUS).addCoreSessionID(coreSessionId).build();
		
		pcrfRequest.setSessionFound(true);
		
		PCRFResponseImpl pcrfResponse = new PCRFResponseImpl();
		
		PCRFPacketUtil.buildPCRFResponse(pcrfRequest, pcrfResponse);

		sessionHandler.handleRequest(pcrfRequest, pcrfResponse, new ExecutionContext(pcrfRequest, null, CacheAwareDDFTable.getInstance(), INR));
		
		verify(sessionOperation, times(1)).updateCoreSession(pcrfResponse);
		verifyNoMoreInteractions(sessionOperation);
	}
	
	@Test
	public void test_handle_method_should_call_deleteCore_when_PCRF_request_is_gateway_reboot() throws SessionException {
		
		String gatewayAddress = "gateway1";
		
		PCRFRequest pcrfRequest =  new PCRFRequestBuilder(PCRFEvent.GATEWAY_REBOOT)
				.addSessionType(SessionTypeConstant.RADIUS)
				.addAttribute(PCRFKeyConstants.CS_GATEWAY_ADDRESS, gatewayAddress)
				.build();
		
		
		PCRFResponseImpl pcrfResponse = new PCRFResponseImpl();
		
		PCRFPacketUtil.buildPCRFResponse(pcrfRequest, pcrfResponse);		
		
		Criteria actualCriteria = new CriteriaImpl("coresession-schema");
		
		when(sessionOperation.getCoreSessionCriteria()).thenReturn(actualCriteria);
		when(sessionOperation.getSessionRuleCriteria()).thenReturn(new CriteriaImpl("sessionRule-schema"));

		sessionHandler.handleRequest(pcrfRequest, pcrfResponse, new ExecutionContext(pcrfRequest, null, CacheAwareDDFTable.getInstance(), INR));
		
		Criteria expectedCriteria = new CriteriaImpl("coresession-schema");
		
		
		expectedCriteria.add(Restrictions.eq(PCRFKeyConstants.CS_GATEWAY_ADDRESS.val, gatewayAddress));
		
		verify(sessionOperation, times(1)).deleteCoreSession(actualCriteria);
		ReflectionAssert.assertReflectionEquals(expectedCriteria, actualCriteria, ReflectionComparatorMode.LENIENT_ORDER);
	}
	
	
	private Object[][] data_provider_for_handle_method_should_not_call_any_method_if_PCRF_request_does_not_contain_start_or_update_or_stop_or_reset_or_gateway_reboot() {
		
		
		ArrayList<PCRFEvent> pcrfEvents = new ArrayList<PCRFEvent>();
		for(PCRFEvent pcrfEvent : PCRFEvent.values()){
			switch (pcrfEvent) {
			case SESSION_START:
			case SESSION_UPDATE:
			case SESSION_RESET:
			case SESSION_STOP:
			case GATEWAY_REBOOT:
				break;

			default:
				pcrfEvents.add(pcrfEvent);
			}
			
		}
		
		Object [][] obj =  new Object [pcrfEvents.size()][];
		
		for (int index = 0; index < pcrfEvents.size(); index++) {
			obj[index] = new Object[] {pcrfEvents.get(index)};
		}
		
		return obj;
	}
	
	@Test
	@Parameters( method = "data_provider_for_handle_method_should_not_call_any_method_if_PCRF_request_does_not_contain_start_or_update_or_stop_or_reset_or_gateway_reboot")
	public void test_handle_method_should_not_call_any_method_if_PCRF_request_does_not_contain_start_or_update_or_stop_or_reset_or_gateway_reboot(PCRFEvent pcrfEvent) throws SessionException {

		PCRFRequest pcrfRequest = new PCRFRequestBuilder(pcrfEvent).addSessionType(SessionTypeConstant.RADIUS).build();
		
		
		PCRFResponseImpl pcrfResponse = new PCRFResponseImpl();
		
		PCRFPacketUtil.buildPCRFResponse(pcrfRequest, pcrfResponse);

		sessionHandler.handleRequest(pcrfRequest, pcrfResponse, new ExecutionContext(pcrfRequest, null, CacheAwareDDFTable.getInstance(), INR));
		
		
		verifyZeroInteractions(sessionOperation);
		
	}
	
	private Object[][] data_provider_for_handle_method_should_not_call_any_method_if_PCRF_request_does_not_contain_core_session_Id_and_event_does_not_gateway_reboot() {
		
		
		ArrayList<PCRFEvent> pcrfEvents = new ArrayList<PCRFEvent>();
		for(PCRFEvent pcrfEvent : PCRFEvent.values()){
			switch (pcrfEvent) {
			case GATEWAY_REBOOT:
				break;

			default:
				pcrfEvents.add(pcrfEvent);
			}
			
		}
		
		Object [][] obj =  new Object [pcrfEvents.size()][];
		
		for (int index = 0; index < pcrfEvents.size(); index++) {
			obj[index] = new Object[] {pcrfEvents.get(index)};
		}
		
		return obj;
	}
	
	@Test
	@Parameters( method = "data_provider_for_handle_method_should_not_call_any_method_if_PCRF_request_does_not_contain_core_session_Id_and_event_does_not_gateway_reboot")
	public void test_handle_method_should_not_call_any_method_if_PCRF_request_does_not_contain_core_session_Id_and_event_does_not_gateway_reboot(PCRFEvent pcrfEvent) throws SessionException {

		PCRFRequest pcrfRequest = new PCRFRequestBuilder(pcrfEvent).addSessionType(SessionTypeConstant.RADIUS).build();
		
		
		PCRFResponseImpl pcrfResponse = new PCRFResponseImpl();
		
		PCRFPacketUtil.buildPCRFResponse(pcrfRequest, pcrfResponse);

		sessionHandler.handleRequest(pcrfRequest, pcrfResponse, new ExecutionContext(pcrfRequest, null, CacheAwareDDFTable.getInstance(), INR));
		
		
		verifyZeroInteractions(sessionOperation);
	}

	

}
