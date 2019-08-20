package com.elitecore.aaa.radius.policies.radiuspolicy;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;

import java.util.ArrayList;
import java.util.Arrays;

import com.elitecore.aaa.radius.service.auth.RadAuthRequest;
import com.elitecore.aaa.radius.service.auth.RadAuthResponse;
import com.elitecore.core.serverx.policies.ParserException;
import com.elitecore.core.serverx.policies.PolicyFailedException;

public class RadiusPolicyManagerSpy {
	
	public RadiusPolicyManager spiedPolicyManager;

	public RadiusPolicyManagerSpy (RadiusPolicyManager spiedPolicyManager) {
		this.spiedPolicyManager = spiedPolicyManager;
	}

	public void simulatePolicyApplicationResult(String policyExpression, String... result) throws ParserException, PolicyFailedException {
		doReturn(new ArrayList<String>(Arrays.asList(result))).when(
				spiedPolicyManager).applyPolicies(any(RadAuthRequest.class), any(RadAuthResponse.class),
						eq(policyExpression), anyInt(), anyString(), anyString(), anyString(),
						eq(true), eq(false), eq(false));
	}
	
	public void simulatePolicyWithPortalCheckItemApplicationResult(String policyExpression, String... result) throws ParserException, PolicyFailedException {
		doReturn(new ArrayList<String>(Arrays.asList(result))).when(
				spiedPolicyManager).applyPolicies(any(RadAuthRequest.class), any(RadAuthResponse.class),
						eq(policyExpression), anyInt(), anyString(),
						anyString(), anyString(), anyBoolean(),
						anyBoolean(), anyBoolean(), eq(true));
	}

	public void simulateParsingFailureOf(String policyName) throws ParserException, PolicyFailedException {
		doThrow(new ParserException("some message")).when(
				spiedPolicyManager).applyPolicies(any(RadAuthRequest.class), any(RadAuthResponse.class),
						eq("(" + policyName + ")"), anyInt(), anyString(), anyString(), anyString(),
						eq(true), eq(false), eq(false));
	}
	
	public void simulateFailedPolicy(String policyName) throws ParserException, PolicyFailedException {
		doThrow(new PolicyFailedException("Policy Failed")).when(
				spiedPolicyManager).applyPolicies(any(RadAuthRequest.class), any(RadAuthResponse.class),
						eq("(" + policyName + ")"), anyInt(), anyString(), anyString(), anyString(),
						eq(true), eq(false), eq(false));
	}
	
	public void simualteFailureOfPortalCheckItemPolicy(String policyName) throws ParserException, PolicyFailedException {
		doThrow(new PolicyFailedException("Policy Failed")).when(
				spiedPolicyManager).applyPolicies(any(RadAuthRequest.class), any(RadAuthResponse.class),
						eq(policyName), anyInt(), anyString(), anyString(), anyString(),
						eq(true), eq(false), eq(false), eq(true));
	}
	
	public void simualteParsingFailureOfPortalCheckItemPolicy(String policyName) throws ParserException, PolicyFailedException {
		doThrow(new ParserException("some message")).when(
				spiedPolicyManager).applyPolicies(any(RadAuthRequest.class), any(RadAuthResponse.class),
						eq(policyName), anyInt(), anyString(), anyString(), anyString(),
						eq(true), eq(false), eq(false), eq(true));
	}

	public static RadiusPolicyManagerSpy create(RadiusPolicyManager policyManager) {
		return new RadiusPolicyManagerSpy(spy(policyManager));
	}
}