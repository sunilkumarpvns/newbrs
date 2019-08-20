package com.elitecore.aaa.diameter.service.application.handlers;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.elitecore.aaa.core.diameter.DummyDiameterDictionary;
import com.elitecore.aaa.core.drivers.BaseAcctDriver;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.diameter.service.DiameterServiceContextStub;
import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.service.application.ApplicationResponse;
import com.elitecore.aaa.diameter.service.application.handlers.conf.DiameterCDRHandlerEntryData;
import com.elitecore.aaa.diameter.subscriber.DiameterProfileDriverDetails;
import com.elitecore.aaa.radius.conf.impl.PrimaryDriverDetail;
import com.elitecore.aaa.radius.conf.impl.SecondaryAndCacheDriverDetail;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.drivers.DriverInitializationFailedException;
import com.elitecore.core.commons.drivers.DriverProcessFailedException;
import com.elitecore.core.commons.drivers.TransientFailureException;
import com.elitecore.core.commons.plugins.script.DriverScript;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.core.serverx.manager.scripts.ExternalScriptsManager;
import com.elitecore.core.serverx.manager.scripts.ScriptContext;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.core.servicex.ServiceRequest;
import com.elitecore.core.servicex.ServiceResponse;
import com.elitecore.core.systemx.esix.FakeTaskScheduler;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import static com.elitecore.core.CoreLibMatchers.ServiceResponseMatchers.*;
import de.bechte.junit.runners.context.HierarchicalContextRunner;

/**
 * 
 * @author narendra.pathai
 *
 */
@RunWith(HierarchicalContextRunner.class)
public class DiameterCDRHandlerTest {

	private static final String PRIMARY_DRIVER_ID = "PrimaryDriverId";
	private static final String SECONDARY_DRIVER_ID = "SecondaryDriverId";
	private static final String UNUSED = "Unused";
	private static final String SCRIPT_NAME = "TestScript";

	private FakeTaskScheduler taskScheduler;
	private AAAServerContext serverContext;
	private DiameterServiceContextStub serviceContext;
	private ExternalScriptsManager externalScriptsManager;
	private DiameterCDRHandlerEntryData handlerData;
	private ApplicationRequest applicationRequest;
	private ApplicationResponse applicationResponse;
	private FakeDriver primaryDriver;
	private FakeDriver secondaryDriver;
	private DriverScript driverScript;

	@Rule
	public ExpectedException exception = ExpectedException.none();

	@BeforeClass
	public static void setUpBeforeClass() {
		DummyDiameterDictionary.getInstance();
	}

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		taskScheduler = new FakeTaskScheduler();
		serverContext = mock(AAAServerContext.class);
		when(serverContext.getTaskScheduler()).thenReturn(taskScheduler);
		serviceContext = spy(new DiameterServiceContextStub());
		externalScriptsManager = mock(ExternalScriptsManager.class);
		when(serverContext.getExternalScriptsManager()).thenReturn(externalScriptsManager);
		when(serviceContext.getServerContext()).thenReturn(serverContext);
		handlerData = new DiameterCDRHandlerEntryData();
		handlerData.setPolicyName("TestPolicy");
		handlerData.setHandlerName("TestHandler");
		handlerData.setDriverDetails(new DiameterProfileDriverDetails());
		handlerData.getDriverDetails().getPrimaryDriverGroup().add(new PrimaryDriverDetail(PRIMARY_DRIVER_ID, 1));
		handlerData.getDriverDetails().getSecondaryDriverGroup().add(new SecondaryAndCacheDriverDetail(SECONDARY_DRIVER_ID, UNUSED));

		DiameterRequest diameterRequest = new DiameterRequest();
		applicationRequest = new ApplicationRequest(diameterRequest);
		applicationResponse = new ApplicationResponse(diameterRequest);
		primaryDriver = spy(new FakeDriver("PrimaryDriver", serverContext));
		secondaryDriver = spy(new FakeDriver("SecondaryDriver", serverContext));
		driverScript = spy(new FakeDriverScript(new ScriptContext() {

			@Override
			public ServerContext getServerContext() {
				return serverContext;
			}
		}));
	}

	@Test
	public void throwsInitializationFailedExceptionIfNeitherOfPrimaryOrSecondaryDriverIsFound() throws InitializationFailedException {
		handlerData.getDriverDetails().getPrimaryDriverGroup().add(new PrimaryDriverDetail(PRIMARY_DRIVER_ID, 1));
		handlerData.getDriverDetails().getSecondaryDriverGroup().add(new SecondaryAndCacheDriverDetail(SECONDARY_DRIVER_ID, UNUSED));

		DiameterApplicationHandler<ApplicationRequest, ApplicationResponse> handler = 
				handlerData.createHandler(serviceContext);

		exception.expect(InitializationFailedException.class);

		handler.init();
	}

	public class WaitForCDRDumpIsEnabled {

		@Before
		public void setUp() {
			handlerData.setWait(true);
		}

		@Test
		public void dumpsCDRInSyncUsingPrimaryDriverIfSecondaryDriverIsNotFound() throws InitializationFailedException, DriverProcessFailedException {
			serviceContext.addDriver(PRIMARY_DRIVER_ID, primaryDriver);

			DiameterApplicationHandler<ApplicationRequest, ApplicationResponse> handler = createHandler();

			handler.handleRequest(applicationRequest, applicationResponse, ISession.NO_SESSION);

			verify(primaryDriver).handleAccountingRequest(applicationRequest);
		}

		@Test
		public void dumpsCDRInSyncUsingSecondaryDriverIfPrimaryDriverIsNotFound() throws InitializationFailedException, DriverProcessFailedException {
			serviceContext.addDriver(SECONDARY_DRIVER_ID, secondaryDriver);

			DiameterApplicationHandler<ApplicationRequest, ApplicationResponse> handler = createHandler();

			handler.handleRequest(applicationRequest, applicationResponse, ISession.NO_SESSION);

			verify(secondaryDriver).handleAccountingRequest(applicationRequest);
		}

		@Test
		public void dumpsCDRInSyncUsingSecondaryDriverIfPrimaryDriverIsDead() throws InitializationFailedException, DriverProcessFailedException {
			serviceContext.addDriver(PRIMARY_DRIVER_ID, primaryDriver);
			serviceContext.addDriver(SECONDARY_DRIVER_ID, secondaryDriver);

			DiameterApplicationHandler<ApplicationRequest, ApplicationResponse> handler = createHandler();

			primaryDriver.markDead();

			handler.handleRequest(applicationRequest, applicationResponse, ISession.NO_SESSION);

			verify(secondaryDriver).handleAccountingRequest(applicationRequest);
		}

		@Test
		public void hintsSystemToDropResponseIfDriverProcessingFails() throws InitializationFailedException, DriverProcessFailedException {
			serviceContext.addDriver(PRIMARY_DRIVER_ID, primaryDriver);

			DiameterApplicationHandler<ApplicationRequest, ApplicationResponse> handler = createHandler();

			doThrow(DriverProcessFailedException.class).when(primaryDriver).handleAccountingRequest(applicationRequest);

			handler.handleRequest(applicationRequest, applicationResponse, ISession.NO_SESSION);

			assertThat(applicationResponse, isDropped());
		}

		@Test
		public void skipsExecutionOfDriverScriptIfNotFound() throws InitializationFailedException, DriverProcessFailedException {
			handlerData.getDriverDetails().setDriverScript(SCRIPT_NAME);

			serviceContext.addDriver(PRIMARY_DRIVER_ID, primaryDriver);
			simulateUnknownScript();

			DiameterApplicationHandler<ApplicationRequest, ApplicationResponse> handler = createHandler();

			handler.handleRequest(applicationRequest, applicationResponse, ISession.NO_SESSION);

			verify(primaryDriver).handleAccountingRequest(applicationRequest);
		}

		@Test
		public void callsHooksOfScriptInProperOrderIfFound() throws InitializationFailedException, DriverProcessFailedException {
			handlerData.getDriverDetails().setDriverScript(SCRIPT_NAME);

			serviceContext.addDriver(PRIMARY_DRIVER_ID, primaryDriver);
			when(externalScriptsManager.getScript(SCRIPT_NAME, DriverScript.class)).thenReturn(driverScript);

			DiameterApplicationHandler<ApplicationRequest, ApplicationResponse> handler = createHandler();

			handler.handleRequest(applicationRequest, applicationResponse, ISession.NO_SESSION);

			InOrder inOrder = Mockito.inOrder(primaryDriver, driverScript);

			inOrder.verify(driverScript).preDriverProcessing(applicationRequest, applicationResponse);
			inOrder.verify(primaryDriver).handleAccountingRequest(applicationRequest);
			inOrder.verify(driverScript).postDriverProcessing(applicationRequest, applicationResponse);
		}

		@Test
		public void swallowsAnyExceptionRaisedInScriptHooks() throws InitializationFailedException, DriverProcessFailedException {
			handlerData.getDriverDetails().setDriverScript(SCRIPT_NAME);

			serviceContext.addDriver(PRIMARY_DRIVER_ID, primaryDriver);
			when(externalScriptsManager.getScript(SCRIPT_NAME, DriverScript.class)).thenReturn(driverScript);
			doThrow(new RuntimeException("Script processing failed")).when(driverScript).preDriverProcessing(applicationRequest, applicationResponse);
			doThrow(new RuntimeException("Script processing failed")).when(driverScript).postDriverProcessing(applicationRequest, applicationResponse);
			
			DiameterApplicationHandler<ApplicationRequest, ApplicationResponse> handler = createHandler();

			handler.handleRequest(applicationRequest, applicationResponse, ISession.NO_SESSION);
			
			verify(primaryDriver).handleAccountingRequest(applicationRequest);
		}
	}

	public class WaitForCDRDumpIsDisabled {

		@Before
		public void setUp() {
			handlerData.setWait(false);
		}

		@Test
		public void dumpsCDRInBackgroundUsingPrimaryDriverIfSecondaryDriverIsNotFound() throws InitializationFailedException, DriverProcessFailedException {
			serviceContext.addDriver(PRIMARY_DRIVER_ID, primaryDriver);

			DiameterApplicationHandler<ApplicationRequest, ApplicationResponse> handler = createHandler();

			handler.handleRequest(applicationRequest, applicationResponse, ISession.NO_SESSION);

			taskScheduler.tick();

			verify(primaryDriver).handleAccountingRequest(applicationRequest);
		}

		@Test
		public void dumpsCDRInBackgroundUsingSecondaryDriverIfPrimaryDriverIsNotFound() throws InitializationFailedException, DriverProcessFailedException {
			serviceContext.addDriver(SECONDARY_DRIVER_ID, secondaryDriver);

			DiameterApplicationHandler<ApplicationRequest, ApplicationResponse> handler = createHandler();

			handler.handleRequest(applicationRequest, applicationResponse, ISession.NO_SESSION);

			taskScheduler.tick();

			verify(secondaryDriver).handleAccountingRequest(applicationRequest);
		}

		@Test
		public void dumpsCDRInBackgroundUsingSecondaryDriverIfPrimaryDriverIsDead() throws InitializationFailedException, DriverProcessFailedException {
			serviceContext.addDriver(PRIMARY_DRIVER_ID, primaryDriver);
			serviceContext.addDriver(SECONDARY_DRIVER_ID, secondaryDriver);

			DiameterApplicationHandler<ApplicationRequest, ApplicationResponse> handler = createHandler();

			primaryDriver.markDead();

			handler.handleRequest(applicationRequest, applicationResponse, ISession.NO_SESSION);

			taskScheduler.tick();

			verify(secondaryDriver).handleAccountingRequest(applicationRequest);
		}

		@Test
		public void hintsSystemToDropResponseIfDriverProcessingFails() throws InitializationFailedException, DriverProcessFailedException {
			serviceContext.addDriver(PRIMARY_DRIVER_ID, primaryDriver);

			DiameterApplicationHandler<ApplicationRequest, ApplicationResponse> handler = createHandler();

			doThrow(DriverProcessFailedException.class).when(primaryDriver).handleAccountingRequest(applicationRequest);

			handler.handleRequest(applicationRequest, applicationResponse, ISession.NO_SESSION);

			taskScheduler.tick();

			assertThat(applicationResponse, isDropped());
		}

		@Test
		public void skipsExecutionOfDriverScriptIfNotFound() throws InitializationFailedException, DriverProcessFailedException {
			handlerData.getDriverDetails().setDriverScript(SCRIPT_NAME);

			serviceContext.addDriver(PRIMARY_DRIVER_ID, primaryDriver);
			simulateUnknownScript();

			DiameterApplicationHandler<ApplicationRequest, ApplicationResponse> handler = createHandler();

			handler.handleRequest(applicationRequest, applicationResponse, ISession.NO_SESSION);

			taskScheduler.tick();

			verify(primaryDriver).handleAccountingRequest(applicationRequest);
		}

		@Test
		public void callsHooksOfScriptInProperOrderIfFound() throws InitializationFailedException, DriverProcessFailedException {
			handlerData.getDriverDetails().setDriverScript(SCRIPT_NAME);

			serviceContext.addDriver(PRIMARY_DRIVER_ID, primaryDriver);
			when(externalScriptsManager.getScript(SCRIPT_NAME, DriverScript.class)).thenReturn(driverScript);

			DiameterApplicationHandler<ApplicationRequest, ApplicationResponse> handler = createHandler();

			handler.handleRequest(applicationRequest, applicationResponse, ISession.NO_SESSION);

			taskScheduler.tick();

			InOrder inOrder = Mockito.inOrder(primaryDriver, driverScript);

			inOrder.verify(driverScript).preDriverProcessing(applicationRequest, applicationResponse);
			inOrder.verify(primaryDriver).handleAccountingRequest(applicationRequest);
			inOrder.verify(driverScript).postDriverProcessing(applicationRequest, applicationResponse);
		}
		
		@Test
		public void swallowsAnyExceptionRaisedInScriptHooks() throws InitializationFailedException, DriverProcessFailedException {
			handlerData.getDriverDetails().setDriverScript(SCRIPT_NAME);

			serviceContext.addDriver(PRIMARY_DRIVER_ID, primaryDriver);
			when(externalScriptsManager.getScript(SCRIPT_NAME, DriverScript.class)).thenReturn(driverScript);
			doThrow(new RuntimeException("Script processing failed")).when(driverScript).preDriverProcessing(applicationRequest, applicationResponse);
			doThrow(new RuntimeException("Script processing failed")).when(driverScript).postDriverProcessing(applicationRequest, applicationResponse);
			
			DiameterApplicationHandler<ApplicationRequest, ApplicationResponse> handler = createHandler();

			handler.handleRequest(applicationRequest, applicationResponse, ISession.NO_SESSION);
			
			taskScheduler.tick();
			
			verify(primaryDriver).handleAccountingRequest(applicationRequest);
		}
	}

	public class DefaultResponseBehavior {

		@Test
		public void isApplicableIfBothPrimaryAndSecondaryDriversAreDead() throws InitializationFailedException {
			serviceContext.addDriver(PRIMARY_DRIVER_ID, primaryDriver);
			serviceContext.addDriver(SECONDARY_DRIVER_ID, secondaryDriver);

			DiameterApplicationHandler<ApplicationRequest, ApplicationResponse> handler = createHandler();

			primaryDriver.markDead();
			secondaryDriver.markDead();

			assertThat(handler.isResponseBehaviorApplicable(), is(true));
		}

		@Test
		public void isNotApplicableIfAnyDriverIsAlive() throws InitializationFailedException {
			serviceContext.addDriver(PRIMARY_DRIVER_ID, primaryDriver);
			serviceContext.addDriver(SECONDARY_DRIVER_ID, secondaryDriver);

			DiameterApplicationHandler<ApplicationRequest, ApplicationResponse> handler = createHandler();

			secondaryDriver.markDead();

			assertThat(handler.isResponseBehaviorApplicable(), is(false));
		}
	}

	private DiameterApplicationHandler<ApplicationRequest, ApplicationResponse> createHandler() throws InitializationFailedException {
		DiameterApplicationHandler<ApplicationRequest, ApplicationResponse> handler = 
				handlerData.createHandler(serviceContext);
		handler.init();
		return handler;
	}	

	private static class FakeDriver extends BaseAcctDriver {

		private String name;

		public FakeDriver(String name, ServerContext serverContext) {
			super(serverContext);
			this.name = name;
		}

		@Override
		public void scan() {

		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public String getTypeName() {
			return "FAKE_DRIVER";
		}

		@Override
		public int getType() {
			return 0;
		}

		@Override
		protected void handleServiceRequest(ServiceRequest serviceRequest)
				throws DriverProcessFailedException {

		}

		@Override
		protected void initInternal() throws TransientFailureException,
		DriverInitializationFailedException {

		}

		@Override
		protected int getStatusCheckDuration() {
			return 0;
		}
	}

	private class FakeDriverScript extends DriverScript {


		public FakeDriverScript(ScriptContext scriptContext) {
			super(scriptContext);
		}

		@Override
		public String getName() {
			return SCRIPT_NAME;
		}

		@Override
		protected void pre(ServiceRequest serviceRequest, ServiceResponse serviceResponse) {

		}

		@Override
		protected void post(ServiceRequest serviceRequest, ServiceResponse serviceResponse) {

		}
	}

	private void simulateUnknownScript() {
		doThrow(new IllegalArgumentException("Script not found")).when(externalScriptsManager).getScript(SCRIPT_NAME, DriverScript.class);
	}
}
