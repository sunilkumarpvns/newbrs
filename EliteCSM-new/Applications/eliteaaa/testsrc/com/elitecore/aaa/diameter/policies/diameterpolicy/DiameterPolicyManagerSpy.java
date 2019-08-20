package com.elitecore.aaa.diameter.policies.diameterpolicy;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;

import java.util.ArrayList;
import java.util.Arrays;

import org.mockito.Mockito;

import com.elitecore.aaa.diameter.policies.diameterpolicy.DiameterPolicyManager;
import com.elitecore.core.serverx.policies.ParserException;
import com.elitecore.core.serverx.policies.PolicyFailedException;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;

public class DiameterPolicyManagerSpy {
	
	public DiameterPolicyManager spiedPolicyManager;

	public DiameterPolicyManagerSpy(DiameterPolicyManager spiedPolicyManager) {
		this.spiedPolicyManager = spiedPolicyManager;
	}

	public void simulatePolicyApplicationResult(String policyExpression, String... result) throws ParserException, PolicyFailedException {
		doReturn(new ArrayList<String>(Arrays.asList(result))).when(
				spiedPolicyManager).applyPolicies(any(DiameterRequest.class), any(DiameterAnswer.class),
						Mockito.eq(policyExpression), anyString(), anyString(), anyString(), Mockito.eq(true), Mockito.eq(false), Mockito.eq(false));
	}

	public void simulateParsingFailureOf(String policyName) throws ParserException, PolicyFailedException {
		doThrow(new ParserException("some message")).when(
				spiedPolicyManager).applyPolicies(any(DiameterRequest.class), any(DiameterAnswer.class),
						Mockito.eq("(" + policyName + ")"), anyString(), anyString(), anyString(), Mockito.eq(true), Mockito.eq(false), Mockito.eq(false));
	}

	public void simulateFailedPolicy(String policyName) throws ParserException, PolicyFailedException {
		doThrow(new PolicyFailedException("Policy Failed")).when(
				spiedPolicyManager).applyPolicies(any(DiameterRequest.class), any(DiameterAnswer.class),
						Mockito.eq("(" + policyName + ")"), anyString(), anyString(), anyString(), Mockito.eq(true), Mockito.eq(false), Mockito.eq(false));
	}

	public static DiameterPolicyManagerSpy create(DiameterPolicyManager policyManager) {
		return new DiameterPolicyManagerSpy(spy(policyManager));
	}
	
	
}