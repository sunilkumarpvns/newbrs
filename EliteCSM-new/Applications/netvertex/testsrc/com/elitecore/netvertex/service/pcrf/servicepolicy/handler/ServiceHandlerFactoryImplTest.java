package com.elitecore.netvertex.service.pcrf.servicepolicy.handler;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.netvertex.core.session.SessionOperation;
import com.elitecore.netvertex.service.pcrf.PCRFServiceContext;
import com.elitecore.netvertex.service.pcrf.servicepolicy.conf.PccServicePolicyConfiguration;

@RunWith(JUnitParamsRunner.class)
public class ServiceHandlerFactoryImplTest {

	@Mock private PCRFServiceContext pcrfServiceContext;
	@Mock private SessionOperation sessionOperation;
	@Mock private PccServicePolicyConfiguration servicePolicyConfiguration;
	@Rule public ExpectedException expectedException = ExpectedException.none();
	
	private ServiceHandlerFactoryImpl serviceHandlerFactory;
	
	@Before
	public void setUp() {
		
		MockitoAnnotations.initMocks(this);
		Mockito.doReturn("PCRF1").when(servicePolicyConfiguration).getName();
	}
	

	public Object[][] dataProvider_for_test_ServiceFactory_should_throw_InitializationExeption() {
		// ServiceHandlerType[] handlerTypes = ServiceHandlerType.values();
		 
		return new Object[][]{
				{
					ServiceHandlerType.SUBSCRIBER_PROFILE_HANDLER
				},
				{
					ServiceHandlerType.USAGE_METERING_HANDLER
				},
				{
					ServiceHandlerType.PCRF_SY_HANDLER
				},
				{
					ServiceHandlerType.SUBSCRIBER_POLICY_HANDLER
				},
				{
					ServiceHandlerType.SESSION_HANDLER
				},
				{
					ServiceHandlerType.POLICY_CDR_HANDLER
				}
		};	
	}
	
	
	
	@Test @Parameters(method="dataProvider_for_test_ServiceFactory_should_throw_InitializationExeption")
	public void test_ServiceFactory_should_throw_InitializationFailedException_when_any_handler_failed_to_initialized(ServiceHandlerType handlerType) throws Exception {
		serviceHandlerFactory = new ServiceHandlerFactoryImpl(pcrfServiceContext, sessionOperation);
		expectedException.expect(InitializationFailedException.class);
		serviceHandlerFactory = Mockito.spy(serviceHandlerFactory);
		Mockito.doThrow(new InitializationFailedException()).when(serviceHandlerFactory).serviceHandlerOf(handlerType, servicePolicyConfiguration);
		serviceHandlerFactory.serviceHandlerOf(handlerType, servicePolicyConfiguration);
	}
}
