package com.elitecore.aaa.diameter.policies.applicationpolicy;

import static org.mockito.Mockito.*;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.elitecore.aaa.core.diameter.DummyDiameterDictionary;
import com.elitecore.aaa.diameter.policies.applicationpolicy.conf.DiameterServicePolicyConfiguration;
import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.sessionmanager.DiameterSessionManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.serverx.servicepolicy.ServicePolicy;
import com.elitecore.core.servicex.ServiceContext;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;

public class DiameterServicePolicyContainerTest {

	private static final String INVALID_POLICY_CONFIGURATION = "1";
	private static final String VALID_POLICY_CONFIGURATION = "2";
	
	@Mock private ServiceContext serviceContext;
	@Mock private DiameterSessionManager diameterSessionManager;
	@Mock private DiameterServicePolicyConfiguration servicePolicyConfiguration;
	@Mock private ServicePolicy<ApplicationRequest> faultyServicePolicy;
	@Mock private ServicePolicy<ApplicationRequest> correctServicePolicy;
	
	private Map<String, DiameterServicePolicyConfiguration> policyConfMap = new LinkedHashMap<String, DiameterServicePolicyConfiguration>();
	private DiameterServicePolicyContainer diameterServicePolicyContainer = 
			Mockito.spy(new DiameterServicePolicyContainerStub(serviceContext, policyConfMap, diameterSessionManager));

	private ApplicationRequest applicationRequest;
	
	@BeforeClass
	public static void beforeSetup() {
		DummyDiameterDictionary.getInstance();
	}
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		applicationRequest = new ApplicationRequest(new DiameterRequest());
	}
	
	@Test
	public void givenPolicyInitalizaionFailsPolicyItIsNotAddedInList() throws InitializationFailedException {
		policyConfMap.put(INVALID_POLICY_CONFIGURATION, servicePolicyConfiguration);
		
		when(diameterServicePolicyContainer.getPolicyObject(
				any(ServiceContext.class),
				eq(INVALID_POLICY_CONFIGURATION),
				any(DiameterSessionManager.class))).thenReturn(faultyServicePolicy);
	
		doThrow(InitializationFailedException.class).when(faultyServicePolicy).init();
		
		diameterServicePolicyContainer.init();
	
		verify(faultyServicePolicy, only()).init();
		
		diameterServicePolicyContainer.applyPolicy(applicationRequest);
	}
	
	@Test
	public void onlyThePoliciesWhoseInitializationFailsAreNotAddedInTheList() throws InitializationFailedException {
		policyConfMap.put(INVALID_POLICY_CONFIGURATION, servicePolicyConfiguration);
		policyConfMap.put(VALID_POLICY_CONFIGURATION, servicePolicyConfiguration);
		
		when(diameterServicePolicyContainer.getPolicyObject(
				any(ServiceContext.class),
				eq(INVALID_POLICY_CONFIGURATION),
				any(DiameterSessionManager.class))).thenReturn(faultyServicePolicy);
	
		when(diameterServicePolicyContainer.getPolicyObject(
				any(ServiceContext.class),
				eq(VALID_POLICY_CONFIGURATION),
				any(DiameterSessionManager.class))).thenReturn(correctServicePolicy);
		
		doThrow(InitializationFailedException.class).when(faultyServicePolicy).init();
		
		diameterServicePolicyContainer.init();
	
		verify(faultyServicePolicy, only()).init();
		
		diameterServicePolicyContainer.applyPolicy(applicationRequest);
	
		verify(correctServicePolicy).assignRequest(applicationRequest);
	}
	
	private class DiameterServicePolicyContainerStub extends DiameterServicePolicyContainer {

		public DiameterServicePolicyContainerStub(ServiceContext serviceContext,
				Map<String, DiameterServicePolicyConfiguration> policyConfMap,
				DiameterSessionManager diameterSessionManager) {
			super(serviceContext, policyConfMap, diameterSessionManager);
		}

		@Override
		protected ServicePolicy<ApplicationRequest> getPolicyObject(ServiceContext serviceContext, String policyId,
				DiameterSessionManager diameterSessionManager) {
			return null;
		}
	}
}
