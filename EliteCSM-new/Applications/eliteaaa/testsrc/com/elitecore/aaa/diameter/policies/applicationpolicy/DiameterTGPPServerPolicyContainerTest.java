package com.elitecore.aaa.diameter.policies.applicationpolicy;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.elitecore.aaa.diameter.policies.applicationpolicy.conf.DiameterServicePolicyConfiguration;
import com.elitecore.aaa.diameter.policies.tgppserver.CommandCodeFlowData;
import com.elitecore.aaa.diameter.policies.tgppserver.TGPPServerPolicyData;
import com.elitecore.aaa.diameter.service.DiameterServiceContext;
import com.elitecore.aaa.diameter.sessionmanager.DiameterSessionManager;
import com.elitecore.core.commons.InitializationFailedException;

public class DiameterTGPPServerPolicyContainerTest {

	private static final String POLICY_ID = "1";
	private static final String INVALID_RULESET =  "0:1="; 
	private DiameterTGPPServerPolicyContainer diameterTGPPServerPolicyContainer;
	private Map<String, DiameterServicePolicyConfiguration> policyConfMap;
	
	@Mock private DiameterServiceContext diameterServiceContext;
	@Mock private DiameterSessionManager diameterSessionManager;
	@Mock private TGPPServerPolicyData tgppServerPolicyData;
	
	@Rule public ExpectedException expectedException = ExpectedException.none();
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		policyConfMap = new LinkedHashMap<String, DiameterServicePolicyConfiguration>();
		
		diameterTGPPServerPolicyContainer = new DiameterTGPPServerPolicyContainer(
				diameterServiceContext, policyConfMap, diameterSessionManager);
		
	}
	
	@Test
	public void throwsInitializationFailedExceptionIfRulesetIsInvalid() throws InitializationFailedException {
		policyConfMap.put(POLICY_ID, tgppServerPolicyData);
		Mockito.when(tgppServerPolicyData.getCommandCodeFlows()).thenReturn(new ArrayList<CommandCodeFlowData>());
		Mockito.when(tgppServerPolicyData.getRuleSet()).thenReturn(INVALID_RULESET);
		
		expectedException.expect(InitializationFailedException.class);
		
		diameterTGPPServerPolicyContainer.getPolicyObject(diameterServiceContext, POLICY_ID, diameterSessionManager);
	}
}
