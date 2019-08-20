package com.elitecore.core.servicex.base;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.elitecore.commons.base.FixedTimeSource;
import com.elitecore.commons.base.TimeSource;
import com.elitecore.core.commons.configuration.LoadConfigurationException;
import com.elitecore.core.commons.util.constants.LifeCycleState;
import com.elitecore.core.commons.util.constants.ServiceRemarks;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.core.servicex.ServiceContext;
import com.elitecore.core.servicex.ServiceDescription;
import com.elitecore.core.servicex.ServiceInitializationException;

import de.bechte.junit.runners.context.HierarchicalContextRunner;

@RunWith(HierarchicalContextRunner.class)
public class BaseEliteServiceContractTest {

	@Rule public ExpectedException exception = ExpectedException.none();
	@Mock private ServerContext serverContext;

	private FixedTimeSource fixedTimeSource;
	private BaseEliteServiceStub service;
	private static final String DUMMY_SERVICE = "DUMMY-SERVICE";

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		this.fixedTimeSource = new FixedTimeSource();
		this.service = Mockito.spy(new BaseEliteServiceStub(serverContext));
	}

	@Test
	public void testTimeSource_ShouldThrowNullPointerException_IfTimeSourcePassedIsNull() {
		exception.expect(NullPointerException.class);
		exception.expectMessage("timesource is null");
		new BaseEliteServiceStub(serverContext, null);
	}

	@Test
	public void testGetStatus_ShouldReturnNotStartedStatus_AsDefaultValue() {
		assertEquals(LifeCycleState.NOT_STARTED.message, service.getStatus());
	}

	@Test
	public void testGetStartDate_ShouldReturnNull_AsDefaultValue() {
		assertNull(service.getStartDate());
	}

	@Test
	public void testIsInitialized_ShouldReturnFalse_AsDefaultValue() {
		assertFalse(service.isInitialized());
	}


	@Test
	public void testStart_ShouldThrowIllegalStateException_IfStartIsCalledEvenWhenInitializationFails() {
		exception.expect(IllegalStateException.class);
		exception.expectMessage("start() called without calling init() or even when init() failed");

		service.start();
	}

	@Test
	public void testInit_ShouldSetTheRemarkOfService_AsTheRemarkFromTheException_WhenInitServiceThrowsServiceInitializationException() throws ServiceInitializationException {
		doThrow(new ServiceInitializationException(ServiceRemarks.DATASOURCE_NOT_CONFIGURED, new Throwable()))
		.when(service).initService();

		initFailingServiceSilently();

		assertEquals(ServiceRemarks.DATASOURCE_NOT_CONFIGURED.remark, service.getRemarks());
	}

	@Test
	public void testInit_ShouldSetTheRemarkOfService_AsUnknownProblem_WhenInitServiceThrowsAnyRuntimeException() throws ServiceInitializationException {
		doThrow(RuntimeException.class).when(service).initService();

		initFailingServiceSilently();

		assertEquals(ServiceRemarks.UNKNOWN_PROBLEM.remark, service.getRemarks());
	}


	@Test
	public void testInit_ShouldRethrowTheCauseWrappedInsideServiceInitializationException_WhenInitServiceThrowsAnyRuntimeException() throws ServiceInitializationException {
		RuntimeException exception = new RuntimeException();

		doThrow(exception).when(service).initService();

		ServiceInitializationException actualException = initFailingServiceSilently();

		Assert.assertSame(exception, actualException.getCause());
	}

	@Test
	public void testInit_ShouldCallAbstractInitService_IfLicenseIsValid() throws ServiceInitializationException {
		service.init();

		verify(service, times(1)).initService();
	}

	@Test
	public void testInit_ShouldLeaveTheStateOfServiceAsNotStarted_AfterSuccessfullInitialization() throws ServiceInitializationException {
		service.init();

		assertEquals(LifeCycleState.NOT_STARTED.message, service.getStatus());
	}

	@Test
	public void testInit_ShouldLeaveStartDateOfServiceAsNull_AfterSuccessfullInitialization() throws ServiceInitializationException {
		service.init();

		assertNull(service.getStartDate());
	}

	@Test
	public void testInit_ShouldLeaveRemarksOfServiceAsNull_AfterSuccessfullInitialization() throws ServiceInitializationException {
		service.init();

		assertNull(service.getRemarks());
	}


	@Test
	public void serviceShouldHaveProperState_WhenInitServiceThrowsServiceInitializationException() {
		try {
			doThrow(ServiceInitializationException.class).when(service).initService();
			service.initService();
		} catch (ServiceInitializationException e) {

		}

		assertEquals(DUMMY_SERVICE, service.getServiceIdentifier());
		assertEquals(LifeCycleState.NOT_STARTED.message, service.getStatus());
		assertNull(service.getStartDate());
		assertNull(service.getRemarks());
	}

	@Test
	public void testInit_ShouldReturnThis() throws ServiceInitializationException {
		assertSame(service, service.init());
	}

	@Test
	public void testStart_ShouldThrowIllegalStateException_IfStartIsCalledWithoutCallingInit() {
		exception.expect(IllegalStateException.class);
		exception.expectMessage("start() called without calling init() or even when init() failed");

		service.start();
	}

	@Test
	public void testStart_ShouldCallAbstractStartServiceMethod_IfServiceIsSuccessfullyInitialized() throws ServiceInitializationException {
		service.init().start();

		verify(service, times(1)).startService();
	}

	@Test
	public void testStart_ShouldChangeTheStateAsRunning_IfServiceStartsSuccessfully() throws ServiceInitializationException {
		service.init().start();

		assertEquals(LifeCycleState.RUNNING.message, service.getStatus());
	}

	@Test
	public void testStart_ShouldSetTheStartDate_IfServiceStartSuccessfully() throws ServiceInitializationException {
		service.init().start();

		assertNotNull(service.getStartDate());
	}

	@Test
	public void testGetStartDate_ShouldReturnTheStartDateOfService_IfServiceStartSuccessfully() throws ServiceInitializationException {
		fixedTimeSource.setCurrentTimeInMillis(System.currentTimeMillis());
		service.init().start();
		assertEquals(fixedTimeSource.currentTimeInMillis(), service.getStartDate().getTime());
	}

	@Test
	public void testStart_ShouldChangeStateToBeRunningInLastConfiguration_IfConfigurationStateIsInLastGoodConfiguration() throws ServiceInitializationException {
		when(serverContext.isServerStartedWithLastConf()).thenReturn(true);

		service.init().start();

		assertEquals(LifeCycleState.RUNNING_WITH_LAST_CONF.message, service.getStatus());
	}

	@Test
	public void testStart_ShouldChangeTheStateToBeNotStarted_IfStartServiceImplementationIsNotAbleToStart() throws ServiceInitializationException {
		service.init();

		when(service.startService()).thenReturn(false);
		service.start();

		assertEquals(LifeCycleState.NOT_STARTED.message, service.getStatus());
	}

	@Test
	public void testStart_ShouldChangeTheStateToBeNotStarted_IfStartServiceImplementationThrowsRuntimeException() throws ServiceInitializationException {
		service.init();
		doThrow(RuntimeException.class).when(service).startService();
		try {
			service.start();
			throw new AssertionError("this must not occur");
		} catch (RuntimeException ex) {

		}

		assertEquals(LifeCycleState.NOT_STARTED.message, service.getStatus());
	}

	@Test
	public void testStart_ShouldChangeTheRemarkToBeUnknownProblem_IfStartServiceImplementationThrowsRuntimeException() throws ServiceInitializationException {
		service.init();
		doThrow(RuntimeException.class).when(service).startService();
		try {
			service.start();
			throw new AssertionError("this must not occur");
		} catch (RuntimeException ex) {

		}

		assertEquals(ServiceRemarks.UNKNOWN_PROBLEM.remark, service.getRemarks());
	}

	@Test
	public void testStart_ShouldRethrowTheSameExceptionOccured_WhichStartServiceImplementationThrows() throws ServiceInitializationException {
		service.init();
		doThrow(new RuntimeException("My message")).when(service).startService();

		exception.expect(RuntimeException.class);
		exception.expectMessage("My message");
		service.start();
	}

	@Test
	public void testStart_ShouldIgnoreSubsequentStartInvocations_IfServiceIsAlreadyStarted() throws ServiceInitializationException {
		service.init().start();
		assertFalse(service.start());
	}

	@Test
	public void testStart_ShouldCallStartServicesOnce_OnSubsequentInvocationOfStart() throws ServiceInitializationException {
		service.init().start();
		assertFalse(service.start());
		verify(service, times(1)).startService();
	}

	@Test
	public void serviceShouldHaveProperState_WhenServiceStartedSuccessfully() throws ServiceInitializationException {
		fixedTimeSource.setCurrentTimeInMillis(System.currentTimeMillis());

		service.init().start();

		assertEquals(DUMMY_SERVICE, service.getServiceIdentifier());
		assertEquals(LifeCycleState.RUNNING.message, service.getStatus());
		assertEquals(fixedTimeSource.currentTimeInMillis(), service.getStartDate().getTime());
		assertNull(service.getRemarks());
	}

	@Test
	public void serviceShouldHaveProperState_IfStartServiceImplementationThrowsRuntimeException() throws ServiceInitializationException {
		service.init();
		doThrow(RuntimeException.class).when(service).startService();
		try {
			service.start();
			throw new AssertionError("this must not occur");
		} catch (RuntimeException ex) {

		}

		assertEquals(DUMMY_SERVICE, service.getServiceIdentifier());
		assertEquals(LifeCycleState.NOT_STARTED.message, service.getStatus());
		assertNull(service.getStartDate());
		assertEquals(ServiceRemarks.UNKNOWN_PROBLEM.remark, service.getRemarks());
	}

	@Test
	public void testStop_ShouldSetTheIsStopRequestedFlag_WhenStopIsCalled() throws ServiceInitializationException {
		service.init().start();

		service.stop();

		assertTrue(service.isStopRequestDone());
	}

	@Test
	public void testStop_ShouldChangeTheStateOfServiceToBeStopped_IfImplementationSuccessfullyStops() throws ServiceInitializationException {
		service.init().start();

		when(service.stopService()).thenReturn(true);
		service.stop();

		assertEquals(LifeCycleState.STOPPED.message, service.getStatus());
	}

	@Test
	public void testStop_ShouldReturnTrueToSuggestThatServiceStoppedSuccessfully_IfImplementationSuccessfullyStops() throws ServiceInitializationException {
		service.init().start();

		when(service.stopService()).thenReturn(true);
		assertTrue(service.stop());
	}

	@Test
	public void testStop_ShouldReturnFalseToSuggestThatServiceWasUnableToStopsSuccessfully_IfImplementationFailsToStop() throws ServiceInitializationException {
		service.init().start();

		when(service.stopService()).thenReturn(false);
		assertFalse(service.stop());
	}

	@Test
	public void testStop_ShouldChangeTheStateOfServiceToBeShutdownInProgress_IfImplementationFailsToStop() throws ServiceInitializationException {
		service.init().start();

		when(service.stopService()).thenReturn(false);
		service.stop();

		assertEquals(LifeCycleState.SHUTDOWN_IN_PROGRESS.message, service.getStatus());
	}

	@Test
	public void testStart_ShouldIgnoreStartRequest_IfServiceShutdownHasFailed() throws ServiceInitializationException {
		service.init().start();
		when(service.stopService()).thenReturn(false);
		service.stop();

		assertFalse(service.start());
	}

	@Test
	public void testStart_ShouldRestartServiceOnSubsequentCallAfterStop_IfServiceSuccessfullyStops() throws ServiceInitializationException {
		service.init().start();
		when(service.stopService()).thenReturn(true);
		service.stop();

		assertTrue(service.start());
	}

	@Test
	public void serviceShouldHaveProperState_WhenStoppedSuccessfully() throws ServiceInitializationException {
		service.init().start();
		service.stop();

		assertEquals(DUMMY_SERVICE, service.getServiceIdentifier());
		assertEquals(LifeCycleState.STOPPED.message, service.getStatus());
		assertNotNull(service.getStartDate());
		assertNull(service.getRemarks());
	}

	private ServiceInitializationException initFailingServiceSilently() throws AssertionError {
		try {
			service.init();
			throw new AssertionError("This must not occur");
		} catch (ServiceInitializationException ex) {
			return ex;
		}
	}

	class BaseEliteServiceStub extends BaseEliteService {

		public BaseEliteServiceStub(ServerContext ctx) {
			super(ctx, fixedTimeSource);
		}

		public BaseEliteServiceStub(ServerContext ctx, TimeSource timeSource) {
			super(ctx, timeSource);
		}

		@Override
		public String getServiceIdentifier() {
			return "DUMMY-SERVICE";
		}

		@Override
		public String getServiceName() {
			return "Dummy Service";
		}

		@Override
		protected ServiceContext getServiceContext() {
			return null;
		}

		@Override
		public void readConfiguration() throws LoadConfigurationException {

		}

		public boolean isStopRequestDone() {
			return isStopRequested();
		}

		@Override
		public String getKey() {
			return "DUMMY_SERVICE";
		}

		@Override
		protected void initService() throws ServiceInitializationException {

		}

		@Override
		protected boolean startService() {
			return true;
		}

		@Override
		public boolean stopService() {
			return true;
		}

		@Override
		protected void shutdownService() {

		}

		@Override
		public ServiceDescription getDescription() {
			return null;
		}
	}
}