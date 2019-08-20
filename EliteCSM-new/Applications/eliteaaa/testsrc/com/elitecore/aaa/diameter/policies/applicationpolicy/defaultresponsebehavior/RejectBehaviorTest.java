package com.elitecore.aaa.diameter.policies.applicationpolicy.defaultresponsebehavior;

import static com.elitecore.diameterapi.diameter.DiameterMatchers.PacketMatchers.hasResultCode;
import static com.elitecore.diameterapi.diameter.common.util.constant.ResultCode.DIAMETER_UNABLE_TO_COMPLY;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import com.elitecore.aaa.core.diameter.DummyDiameterDictionary;
import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.service.application.ApplicationResponse;
import com.elitecore.commons.tests.PrintMethodRule;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.serverx.servicepolicy.BaseServicePolicy;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAssertion;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;

import de.bechte.junit.runners.context.HierarchicalContextRunner;

@RunWith(HierarchicalContextRunner.class)
public class RejectBehaviorTest {

	private static final String DEFAULT_ERROR_MESSAGE = "Default response behavior applied";
	private static final ResultCode DEFAULT_RESULT_CODE = DIAMETER_UNABLE_TO_COMPLY;
	private ApplicationRequest applicationRequest;
	private ApplicationResponse applicationResponse;
	private BaseServicePolicy<ApplicationRequest> dummyPolicy;
	private RejectBehavior rejectBehavior;
	
	@Rule public ExpectedException exception = ExpectedException.none();
	@Rule public PrintMethodRule printMethod = new PrintMethodRule();

	@BeforeClass
	public static void setUpBeforeClass() {
		DummyDiameterDictionary.getInstance();
	}

	@Before
	public void setUp() {
		DiameterRequest request = new DiameterRequest(true);
		applicationRequest = new ApplicationRequest(request);
		applicationResponse = new ApplicationResponse(request);
		dummyPolicy = new BaseServicePolicy<ApplicationRequest>(null) {

			@Override
			public boolean assignRequest(ApplicationRequest request) {
				// TODO Auto-generated method stub
				return false;
			}

			@Override
			public String getPolicyName() {
				// TODO Auto-generated method stub
				return "DUMMY POLICY";
			}

			@Override
			public void init() throws InitializationFailedException {
				// TODO Auto-generated method stub
				
			}
		};
		applicationRequest.setApplicationPolicy(dummyPolicy);
	}

	public class FunctionalTests {
		
		@Test
		public void defaultResultCodeIsDiameterUnableToComply() {
			assertEquals(ResultCode.DIAMETER_UNABLE_TO_COMPLY, RejectBehavior.DEFAULT_RESULT_CODE);
		}

		@Test
		public void defaultErrorMessageIsDefaultResponseBehaviorApplied() {
			assertEquals(DEFAULT_ERROR_MESSAGE, RejectBehavior.DEFAULT_ERROR_MESSAGE);
		}

		@Test
		public void rejectsRequestWithGivenResultCodeAndErrorMessage() {
			givenConfiguration("4001,Authentication Rejected");
			whenApplied();
			requestIsRejectedWith(ResultCode.fromCode(4001), "Authentication Rejected");
		}

		@Test 
		public void rejectsRequestWithDefaultResultCodeAndErrorMessageWhenConfiguredResultCodeIsInvalid() {
			givenConfiguration("6789,any string");
			whenApplied();
			requestIsRejectedWith(DEFAULT_RESULT_CODE, DEFAULT_ERROR_MESSAGE);
		}

		@Test
		public void rejectsRequestWithDefaultResultCodeAndErrorMessageWhenConfiguredResultCodeIsNonNumeric() {
			givenConfiguration("4001a,Authorization Rejected");
			whenApplied();
			requestIsRejectedWith(DEFAULT_RESULT_CODE, DEFAULT_ERROR_MESSAGE);
		}

		@Test
		public void ignoresLeadingAndTrailingWhitespaceCharactersWithinConfiguredResultCodeAndErrorMessage() {
			givenConfiguration("    4001  , 	Authorization rejected           ");
			whenApplied();
			requestIsRejectedWith(ResultCode.fromCode(4001), "Authorization rejected");
		}

		@Test
		public void rejectsRequestWithConfiguredResultCodeAndDefaultErrorMessageWhenOnlyResultCodeIsConfigured() {
			givenConfiguration("4001");
			whenApplied();
			requestIsRejectedWith(ResultCode.fromCode(4001), DEFAULT_ERROR_MESSAGE);
		}
	}
	
	public class DeveloperTests {
		
		@Test
		public void throwsNullPointerExceptionWhenConfigurationIsNull() {
			exception.expect(NullPointerException.class);
			exception.expectMessage(equalTo("Response behavior parameter is null"));
			
			new RejectBehavior(null);
		}
		
		@Test
		public void throwsIllegalArgumentExceptionIfConfiguredResponseParameterIsBlank() {
			exception.expect(IllegalArgumentException.class);
			exception.expectMessage(equalTo("Default Response Behavior Parameter is blank"));
			
			new RejectBehavior("");
		}
	}

	private void requestIsRejectedWith(ResultCode resultCode, String errorMessage) {
		assertFalse(applicationResponse.isFurtherProcessingRequired());
		assertTrue(applicationResponse.isProcessingCompleted());
		assertThat(applicationResponse.getDiameterAnswer(), hasResultCode(resultCode));
		
		DiameterAssertion.assertThat(applicationResponse.getDiameterAnswer())
		.containsAVP(DiameterAVPConstants.ERROR_MESSAGE, errorMessage);
	}

	private void whenApplied() {
		rejectBehavior.apply(applicationRequest, applicationResponse);
	}

	private void givenConfiguration(String configuration) {
		rejectBehavior = new RejectBehavior(configuration);
	}
}
