package com.elitecore.aaa.diameter.service.application.handlers;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.elitecore.aaa.core.diameter.DummyDiameterDictionary;
import com.elitecore.aaa.diameter.service.DiameterServiceContext;
import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.service.application.ApplicationResponse;
import com.elitecore.aaa.diameter.service.application.handlers.conf.DiameterCDRGenerationHandlerData;
import com.elitecore.aaa.diameter.service.application.handlers.conf.DiameterCDRHandlerEntryData;
import com.elitecore.aaa.radius.service.handlers.RadiusRequestExecutor;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;

public class DiameterCDRGenerationHandlerTest {

	private DiameterCDRGenerationHandler cdrGenerationHandler;
	private DiameterCDRGenerationHandlerData cdrGenerationHandlerData;
	private ApplicationRequest request;
	private ApplicationResponse response;
	private DiameterRequest diameterRequest;
	private RadiusRequestExecutor<ApplicationRequest, ApplicationResponse> requestExecutor;

	@Mock private DiameterCDRHandlerEntryData handlerEntry1; 
	@Mock private DiameterCDRHandlerEntryData handlerEntry2;
	@Mock private DiameterCDRHandler handler1;
	@Mock private DiameterCDRHandler handler2;
	@Mock private DiameterServiceContext context;

	@BeforeClass
	public static void setUpBeforeClass() {
		DummyDiameterDictionary.getInstance();
	}

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);

		createRequestAndResponse();

		createHandler();
	}

	@Test
	public void allConfiguredHandlersAreInitialized() throws Exception {
		cdrGenerationHandler.init();

		verify(handler1).init();
		verify(handler2).init();
	}
	
	@Test
	public void handlerInitializationFailsIfAnyHandlerInChainFailsToInitialize() {

		try {
			doThrow(InitializationFailedException.class).when(handler1).init();

			cdrGenerationHandler.init();
			fail("Handler should throw initialization failed exception if any hanlder initialization fails");
		} catch (InitializationFailedException e) {
			// Expected
		}

		assertFalse(cdrGenerationHandler.iterator().hasNext());

	}

	@Test
	public void isAlwaysEligible() throws Exception {

		assertTrue(cdrGenerationHandler.isEligible(request, response));
	}

	@Test
	public void eligibleHandlersAreAppliedAsPerConfiguredSequence() throws Exception {

		when(handler1.isEligible(request, response)).thenReturn(true);
		when(handler2.isEligible(request, response)).thenReturn(true);

		cdrGenerationHandler.init();

		cdrGenerationHandler.handleRequest(request, response, ISession.NO_SESSION);

		InOrder order = inOrder(handler1, handler2);

		order.verify(handler1).handleRequest(request, response, ISession.NO_SESSION);
		order.verify(handler2).handleRequest(request, response, ISession.NO_SESSION);
	}
	
	@Test
	public void handlerIsOnlyApplyIfRuleSetSatisfied() throws Exception {

		when(handler1.isEligible(request, response)).thenReturn(true);
		when(handler2.isEligible(request, response)).thenReturn(true);
		
		when(handlerEntry1.getRuleset()).thenReturn("0:1=\"test1\"");
		when(handlerEntry2.getRuleset()).thenReturn("0:1=\"test2\"");
		
		cdrGenerationHandler.init();
		
		request.getDiameterRequest().addAvp(DiameterAVPConstants.USER_NAME, "test2");
		
		cdrGenerationHandler.handleRequest(request, response, ISession.NO_SESSION);

		verify(handler2).handleRequest(request, response, ISession.NO_SESSION);
	}

	private void createRequestAndResponse() {
		diameterRequest =  new DiameterRequest();
		request =  new ApplicationRequest(diameterRequest);
		response =  new ApplicationResponse(diameterRequest);
	}
	
	private void createHandler() {
		cdrGenerationHandlerData = new DiameterCDRGenerationHandlerData();

		cdrGenerationHandlerData.getEntries().add(handlerEntry1);
		cdrGenerationHandlerData.getEntries().add(handlerEntry2);

		cdrGenerationHandler = new DiameterCDRGenerationHandler(context, cdrGenerationHandlerData);

		when(handlerEntry1.createHandler(context)).thenReturn(handler1);
		when(handlerEntry2.createHandler(context)).thenReturn(handler2);

		requestExecutor = new RadiusRequestExecutor<ApplicationRequest, ApplicationResponse>(cdrGenerationHandler, request, response);
		request.setExecutor(requestExecutor);
	}
}
