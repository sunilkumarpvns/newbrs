package com.elitecore.aaa.diameter.util;

import static junitparams.JUnitParamsRunner.$;
import static org.junit.Assert.assertFalse;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.elitecore.aaa.core.diameter.DummyDiameterDictionary;
import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.service.application.ApplicationResponse;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.serverx.servicepolicy.BaseServicePolicy;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.util.DiameterUtility;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

@RunWith(JUnitParamsRunner.class)
public class HotlineUtillityTest {

	private DiameterRequest request;
	private ApplicationRequest applicationRequest;
	private ApplicationResponse applicationResponse;
	private BaseServicePolicy<ApplicationRequest> dummyPolicy;
	
	@BeforeClass
	public static void setUpBeforeClass() 	{
		DummyDiameterDictionary.getInstance();
	}
	
	@Before 
	public void setup() {
		request = new DiameterRequest();
		applicationRequest = new ApplicationRequest(request);
		applicationResponse = new ApplicationResponse(request);
		
		dummyPolicy = new BaseServicePolicy<ApplicationRequest>(null) {

			@Override
			public boolean assignRequest(ApplicationRequest request) {
				return false;
			}

			@Override
			public String getPolicyName() {
				return "DUMMY POLICY";
			}

			@Override
			public void init() throws InitializationFailedException {
				
			}
		};
		applicationRequest.setApplicationPolicy(dummyPolicy);
		applicationResponse.getDiameterAnswer().addAvp(DiameterAVPConstants.RESULT_CODE, ResultCode.DIAMETER_COMMAND_UNSUPPORTED.code);
	}
	
	@Test
	public void requestIsNotEligibleForHotliningIfResponseDoesNotContainResultCodeAvp() {
		
		givenResponseWithoutResultCode();
		
		assertNotEligibleForHotlining();
	}
	
	@Test
	public void requestIsNotEligibleForHotlingIfNoApplicationPolicyIsSelected() {
		
		givenNoApplicationPolicySelectedForRequest();
		
		assertNotEligibleForHotlining();
	}
	
	@Test
	@Parameters(method = "nonFailureCategoryResultCodes")
	public void requestIsNotEligibleForHotliningIfResultCodeIsNonFailureCategory(ResultCode resultCode) {
		givenResponseWithResultCode(resultCode);
		
		assertNotEligibleForHotlining();
	}

	private void givenResponseWithResultCode(ResultCode resultCode) {
		DiameterUtility.addOrReplaceAvp(DiameterAVPConstants.RESULT_CODE, applicationResponse.getDiameterAnswer(), String.valueOf(resultCode.code));
	}
	
	public Object[] nonFailureCategoryResultCodes() {
		return $(
				ResultCode.DIAMETER_SUCCESS,
				ResultCode.DIAMETER_LIMITED_SUCCESS,
				ResultCode.DIAMETER_MULTI_ROUND_AUTH
		);
	}

	private void givenNoApplicationPolicySelectedForRequest() {
		applicationRequest.setApplicationPolicy(null);
	}

	private void assertNotEligibleForHotlining() {
		assertFalse(HotlineUtility.isEligibleForHotlining(
				applicationRequest, applicationResponse));
	}

	private void givenResponseWithoutResultCode() {
		applicationResponse.getDiameterAnswer().removeAVP(applicationResponse.getAVP(DiameterAVPConstants.RESULT_CODE));
	}
}
