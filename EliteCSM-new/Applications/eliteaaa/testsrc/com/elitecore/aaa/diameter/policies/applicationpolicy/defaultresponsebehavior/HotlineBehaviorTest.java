package com.elitecore.aaa.diameter.policies.applicationpolicy.defaultresponsebehavior;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doThrow;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import com.elitecore.aaa.core.diameter.DummyDiameterDictionary;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.diameter.policies.diameterpolicy.DiameterPolicyManager;
import com.elitecore.aaa.diameter.policies.diameterpolicy.InMemoryDiameterPolicyManager;
import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.service.application.ApplicationResponse;
import com.elitecore.commons.tests.PrintMethodRule;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.serverx.policies.ManagerInitialzationException;
import com.elitecore.core.serverx.policies.ParserException;
import com.elitecore.core.serverx.policies.PolicyFailedException;
import com.elitecore.core.serverx.policies.data.IPolicyData;
import com.elitecore.core.serverx.policies.data.PolicyData;
import com.elitecore.core.serverx.servicepolicy.BaseServicePolicy;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAssertion;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterErrorMessageConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

@RunWith(JUnitParamsRunner.class)
public class HotlineBehaviorTest {
	private static final String CHECK_ITEM_NOT_SATISFIED_POLICY = "policyWithReplyItemsAndFailsToApplySuccessfully";

	private static final String SUCCESSFULL_POLICY = "policyWithReplyItemsAndToBeAppliedSuccessfully";

	private static final String UNKNOWN_POLICY = "arbitraryValue";

	 

	@Rule public PrintMethodRule printMethod = new PrintMethodRule();
	
	@Rule public ExpectedException exception = ExpectedException.none();
	
	private HotlineBehavior hotlineBehavior;
	private ApplicationRequest applicationRequest;
	private ApplicationResponse applicationResponse;
	private BaseServicePolicy<ApplicationRequest> dummyPolicy;
	private InMemoryDiameterPolicyManager diameterPolicyManager;

	@BeforeClass
	public static void setUpBeforeClass() {
		DummyDiameterDictionary.getInstance();
	}
	
	@Before
	public void setUp() throws ManagerInitialzationException {
		DiameterRequest request = new DiameterRequest();
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
		applicationResponse.getDiameterAnswer().addAvp(DiameterAVPConstants.RESULT_CODE, ResultCode.DIAMETER_SUCCESS.code);
		
		IPolicyData successfullPolicy = createSuccessfullPolicy();
		IPolicyData unsuccessfullPolicy = createUnsuccessfullPolicy();
		List<IPolicyData> policies = new ArrayList<IPolicyData>();
		policies.add(successfullPolicy);
		policies.add(unsuccessfullPolicy);
		diameterPolicyManager = Mockito.spy(new InMemoryDiameterPolicyManager(DiameterPolicyManager.DIAMETER_AUTHORIZATION_POLICY, policies));
		diameterPolicyManager.initCache(Mockito.mock(AAAServerContext.class), false);
	}

	private IPolicyData createUnsuccessfullPolicy() {
		return createPolicyHaving(CHECK_ITEM_NOT_SATISFIED_POLICY,"0:264=\"\"","0:25=\"added from Test\"");
	}

	private IPolicyData createSuccessfullPolicy() {
		return createPolicyHaving(SUCCESSFULL_POLICY, "$RES:0:268=\"2001\"", "0:25=\"added from Test\"");
	}

	@Test
	public void requestIsRejectedWhenConfiguredHotlinePolicyDoesNotExist() throws ManagerInitialzationException {
		givenConfiguredHotlinePolicyIs(UNKNOWN_POLICY);
		
		whenApplied();
		
		rejectsTheRequestWith(ResultCode.DIAMETER_UNABLE_TO_COMPLY, DiameterErrorMessageConstants.DEFAULT_RESPONSE_BEHAVIOR_APPLIED);
	}
	
		
	@Test
	public void resultCodeIsSetToDiametereSuccessOnSuccessfullHotlining() throws ManagerInitialzationException {
		givenConfiguredHotlinePolicyIs(SUCCESSFULL_POLICY);
		
		whenApplied();
		
		resultCodeIsSuccess();
	}

	@Test
	public void throwsNullPointerExceptionWhenHotlinePolicyIsNull() {
		exception.expect(NullPointerException.class);
		exception.expectMessage(equalTo("parameter is null"));
		
		new HotlineBehavior(null);
	}
	
	@Test
	@Parameters({
		"",
		" ",
		"\t",
		"\r\t\n"
	})
	public void throwsIllegalArgumentExceptionIfPolicyNameIsBlank(String hotlinePolicy) throws ManagerInitialzationException {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(equalTo("parameter is blank"));
		
		new HotlineBehavior(hotlinePolicy);
	}

	@Test
	public void replyItemsAreAddedWhenHotlinePolicyIsSuccessfullyApplied() throws ManagerInitialzationException {
		givenConfiguredHotlinePolicyIs(SUCCESSFULL_POLICY);

		whenApplied();

		replyItemsAreAdded();
	}

	@Test
	public void satisfiedPoliciesAVPHavingNamesOfSuccessfullyAppliedPoliciesIsAddedInResponsePacket() throws ManagerInitialzationException {
		givenConfiguredHotlinePolicyIs(SUCCESSFULL_POLICY);
		
		whenApplied();
		
		DiameterAssertion.assertThat(applicationResponse.getDiameterAnswer()).containsAVP(DiameterAVPConstants.EC_SATISFIED_POLICIES, "*,policyWithReplyItemsAndToBeAppliedSuccessfully");		
	}
	
	@Test
	public void policyProessingIsSkippedAfterSuccessfullHotlining() {
		givenConfiguredHotlinePolicyIs(SUCCESSFULL_POLICY);
		
		whenApplied();
		
		assertTrue(applicationResponse.isProcessingCompleted());
		assertFalse(applicationResponse.isFurtherProcessingRequired());
	}
	
	@Test
	public void hotlineReasonAVPIsAddedIntoRequestPacketAfterSuccessfullHotlining() throws ManagerInitialzationException {
		givenConfiguredHotlinePolicyIs(SUCCESSFULL_POLICY);
		
		whenApplied();
		
		DiameterAssertion.assertThat(applicationRequest.getDiameterRequest()).containsAVP(DiameterAVPConstants.EC_HOTLINE_REASON, DiameterErrorMessageConstants.DEFAULT_RESPONSE_BEHAVIOR_APPLIED);
	}
	
	@Test
	public void replyItemsAreNotAddedWhenCheckItemsAreNotSatisfied() throws ManagerInitialzationException {
		givenConfiguredHotlinePolicyIs(CHECK_ITEM_NOT_SATISFIED_POLICY);
		
		whenApplied();
		
		DiameterAssertion.assertThat(applicationResponse.getDiameterAnswer()).doesNotContainAVP(DiameterAVPConstants.CLASS_STR);
	}

	
	@Test
	public void requestIsRejectedWithDiameterUnableToComplyWhenCheckItemsAreNotSatisfied() throws ManagerInitialzationException {
		givenConfiguredHotlinePolicyIs(CHECK_ITEM_NOT_SATISFIED_POLICY);
		
		whenApplied();
		
		rejectsTheRequestWith(ResultCode.DIAMETER_UNABLE_TO_COMPLY, DiameterErrorMessageConstants.DEFAULT_RESPONSE_BEHAVIOR_APPLIED);
	}
	
	@Test
	public void requestIsRejectedWithDiameterUnableToComplyWhenPolicyParsingFails() throws ManagerInitialzationException, ParserException, PolicyFailedException {
		givenConfiguredHotlinePolicyIs(CHECK_ITEM_NOT_SATISFIED_POLICY);
		
		doThrow(ParserException.class).when(diameterPolicyManager).applyPolicies(Mockito.eq(applicationRequest.getDiameterRequest()), Mockito.eq(applicationResponse.getDiameterAnswer()), Mockito.anyString());
		
		whenApplied();
		
		rejectsTheRequestWith(ResultCode.DIAMETER_UNABLE_TO_COMPLY, DiameterErrorMessageConstants.DEFAULT_RESPONSE_BEHAVIOR_APPLIED);
	}
	
	public void resultCodeIsSuccess() {
		DiameterAssertion.assertThat(applicationResponse.getDiameterAnswer())
		.hasResultCode(ResultCode.DIAMETER_SUCCESS);
	}
	
	private void rejectsTheRequestWith(ResultCode diameterUnableToComply,
			String errorMessage) {
		DiameterAssertion.assertThat(applicationResponse.getDiameterAnswer()).hasResultCode(ResultCode.DIAMETER_UNABLE_TO_COMPLY);
		DiameterAssertion.assertThat(applicationResponse.getDiameterAnswer()).containsAVP(
				DiameterAVPConstants.ERROR_MESSAGE_STR, errorMessage);
	}

	private void replyItemsAreAdded() {
		DiameterAssertion.assertThat(applicationResponse.getDiameterAnswer()).containsAVP(DiameterAVPConstants.CLASS_STR,"added from Test");
	}
	
	private void givenConfiguredHotlinePolicyIs(String hotlinePolicy) {
		 hotlineBehavior = new HotlineBehavior(hotlinePolicy, diameterPolicyManager);
	}
	
	private void whenApplied() {
		hotlineBehavior.apply(applicationRequest, applicationResponse);
	}
	
	private IPolicyData createPolicyHaving(String strPolicyName, String strCheckItem, String strReplyItem) {
		IPolicyData newPolicyData = new PolicyData();
		newPolicyData.setPolicyName(strPolicyName);
		newPolicyData.setCheckItem(strCheckItem);
		newPolicyData.setReplyItem(strReplyItem);
		return newPolicyData;
	}
}
