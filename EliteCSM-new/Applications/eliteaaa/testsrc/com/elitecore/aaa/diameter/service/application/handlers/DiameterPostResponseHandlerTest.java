package com.elitecore.aaa.diameter.service.application.handlers;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Iterator;

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
import com.elitecore.aaa.diameter.service.application.handlers.conf.DiameterApplicationHandlerData;
import com.elitecore.aaa.diameter.service.application.handlers.conf.DiameterPostResponseHandlerData;
import com.elitecore.aaa.radius.service.handlers.RadiusRequestExecutor;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;

public class DiameterPostResponseHandlerTest {

	private  DiameterPostResponseHandler diameterPostResponseHandler;
	private  DiameterPostResponseHandlerData diameterPostResponseHandlerData;
	private RadiusRequestExecutor<ApplicationRequest, ApplicationResponse> requestExecutor;
	private ApplicationResponse response;
	private ApplicationRequest request;
	private DiameterRequest diameterRequest;

	@Mock private DiameterServiceContext context;
	@Mock private DiameterApplicationHandler<ApplicationRequest,ApplicationResponse> handler1;
	@Mock private DiameterApplicationHandler<ApplicationRequest,ApplicationResponse> handler2;
	@Mock private DiameterApplicationHandlerData handlerData1;
	@Mock private DiameterApplicationHandlerData handlerData2;

	@BeforeClass
	public static void setUpBeforeClass() {
		DummyDiameterDictionary.getInstance();
	}

	@Before
	public void setUp() throws Exception {

		MockitoAnnotations.initMocks(this);

		createHandler();

		createRequestAndResponse();

		requestExecutor = new RadiusRequestExecutor<ApplicationRequest, ApplicationResponse>(diameterPostResponseHandler, request, response);
		request.setExecutor(requestExecutor);

	}

	@Test
	public void handlersAreAddedInChainAsPerConfiguredSequence() throws Exception {

		diameterPostResponseHandler.init();

		Iterator<DiameterApplicationHandler<ApplicationRequest, ApplicationResponse>> iterator = diameterPostResponseHandler.iterator();

		assertTrue(iterator.hasNext());
		assertThat(iterator.next(), is(sameInstance(handler1)));
		assertTrue(iterator.hasNext());
		assertThat(iterator.next(), is(sameInstance(handler2)));
	}

	@Test
	public void handlerInitializationFailsIfAnyHandlerInChainFailsToInitialize() {
		try {
			doThrow(InitializationFailedException.class).when(handler1).init();

			diameterPostResponseHandler.init();
			
			fail("Handler should throw initialization failed exception if any hanlder initialization fails");
		} catch (InitializationFailedException e) {
			// Expected
		}

		assertFalse(diameterPostResponseHandler.iterator().hasNext());
	}

	@Test
	public void allConfiguredHandlersAreInitialized() throws InitializationFailedException {

		diameterPostResponseHandler.init();

		verify(handler1).init();
		verify(handler2).init();
	}

	@Test
	public void eligibleHandlersAreAppliedAsPerConfiguredSequence() throws InitializationFailedException {

		diameterPostResponseHandler.init();

		when(handler1.isEligible(request, response)).thenReturn(true);
		when(handler2.isEligible(request, response)).thenReturn(true);

		diameterPostResponseHandler.handleRequest(request, response, ISession.NO_SESSION);

		InOrder order = inOrder(handler1, handler2);
		order.verify(handler1).handleRequest(request, response, ISession.NO_SESSION);
		order.verify(handler2).handleRequest(request, response, ISession.NO_SESSION);
	}

	private void createRequestAndResponse() {
		diameterRequest =  new DiameterRequest();
		request =  new ApplicationRequest(diameterRequest);
		response =  new ApplicationResponse(diameterRequest);
	}

	private void createHandler() {
		diameterPostResponseHandlerData = new DiameterPostResponseHandlerData();
		diameterPostResponseHandlerData.getHandlersData().add(handlerData1);
		diameterPostResponseHandlerData.getHandlersData().add(handlerData2);

		diameterPostResponseHandler  = new DiameterPostResponseHandler(context, diameterPostResponseHandlerData);

		when(handlerData1.createHandler(context)).thenReturn(handler1);
		when(handlerData2.createHandler(context)).thenReturn(handler2);
	}
}
