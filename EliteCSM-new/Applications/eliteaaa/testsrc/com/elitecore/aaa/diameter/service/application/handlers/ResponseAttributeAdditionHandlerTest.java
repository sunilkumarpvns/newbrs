package com.elitecore.aaa.diameter.service.application.handlers;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import com.elitecore.aaa.core.diameter.DummyDiameterDictionary;
import com.elitecore.aaa.diameter.policies.applicationpolicy.conf.impl.CommandCodeResponseAttribute;
import com.elitecore.aaa.diameter.policies.tgppserver.TGPPServerPolicyData;
import com.elitecore.aaa.diameter.service.DiameterServiceContext;
import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.service.application.ApplicationResponse;
import com.elitecore.commons.tests.PrintMethodRule;
import com.elitecore.core.serverx.policies.ManagerInitialzationException;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAssertion;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;

import de.bechte.junit.runners.context.HierarchicalContextRunner;

@RunWith(HierarchicalContextRunner.class)
public class ResponseAttributeAdditionHandlerTest {

	private static final String AUTHENTICATION_AUTHORIZATION_COMMAND_CODE = "265";
	private static final String CALLING_STATION_ID = "0:31";
	private static final String USER_NAME = "0:1";
	private ResponseAttributeAdditionHandler<ApplicationRequest, ApplicationResponse> responseAttributeAdditionHandler;
	private TGPPServerPolicyData policyConfiguration;
	private CommandCodeResponseAttribute commandCodeResponseAttribute;
	private DiameterRequest diameterRequest = new DiameterRequest();

	private ApplicationRequest request;
	private ApplicationResponse response;

	@Mock private DiameterServiceContext serviceContext;
	
	@Rule public PrintMethodRule printMethod = new PrintMethodRule();

	@BeforeClass
	public static void setUpBeforeClass() throws ManagerInitialzationException {
		DummyDiameterDictionary.getInstance();
	}

	@Before
	public void setUp() throws Exception {
		request = new ApplicationRequest(diameterRequest);
		response = new ApplicationResponse(diameterRequest);
		response.getDiameterAnswer().setCommandCode(Integer.parseInt(AUTHENTICATION_AUTHORIZATION_COMMAND_CODE));

		policyConfiguration = new TGPPServerPolicyData();
		commandCodeResponseAttribute = new CommandCodeResponseAttribute();
	}

	@Test
	public void isEligibleAlwaysReturnsTrue() throws Exception {
		createHandlerWithPolicyData(policyConfiguration);

		assertTrue(responseAttributeAdditionHandler.isEligible(request, response));
	}

	@Test
	public void responsePacketWillNotAlteredIfNoResponseAttributeIsConfigured() throws Exception {
		givenResponseAvps("");
		
		createHandlerWithPolicyData(policyConfiguration);
		
		byte[] preResponseByte = response.getDiameterAnswer().getBytes();

		responseAttributeAdditionHandler.handleRequest(request, response, null);
		
		byte[] postResponseByte = response.getDiameterAnswer().getBytes();
		
		assertArrayEquals(preResponseByte, postResponseByte);

	}
	
	public class WhenHardcodedValueIsConfigured {

		private static final String UNKONWN_AVP = "0:999=1235";


		@Test
		public void hardcodedValuesCanBeConfiguredDirectlyWithoutDoubleQuotes() throws Exception {
			givenResponseAvps("0:89=cui-value");

			createHandlerWithPolicyData(policyConfiguration);

			responseAttributeAdditionHandler.handleRequest(request, response, null);

			DiameterAssertion.assertThat(response.getDiameterAnswer())
			.containsAVP("0:89", "cui-value");
			
		}
		
		@Test
		public void multipleResponseAttributesCanBeConfiguredSeparatedByComma() throws Exception {
			givenResponseAvps("0:89=cui-value,0:25=class-value");
			
			createHandlerWithPolicyData(policyConfiguration);

			responseAttributeAdditionHandler.handleRequest(request, response, null);

			DiameterAssertion.assertThat(response.getDiameterAnswer())
				.containsAVP("0:89", "cui-value")
				.containsAVP("0:25", "class-value");
		}
		
		@Test
		public void addsConfiguredAttributesInResponsePacket() throws Exception {
			givenResponseAvps("0:89=cui-value,0:25=class-value");
			
			createHandlerWithPolicyData(policyConfiguration);

			responseAttributeAdditionHandler.handleRequest(request, response, null);

			DiameterAssertion.assertThat(response.getDiameterAnswer())
				.containsAVP("0:89", "cui-value")
				.containsAVP("0:25", "class-value");
		}

		@Test
		public void addsResultCodeDiameterUnableToComplyIfUnknownAVPIsConfigured() throws Exception {
			givenResponseAvps(UNKONWN_AVP);
			
			createHandlerWithPolicyData(policyConfiguration);

			responseAttributeAdditionHandler.handleRequest(request, response, null);

			DiameterAssertion.assertThat(response.getDiameterAnswer())
				.hasResultCode(ResultCode.DIAMETER_UNABLE_TO_COMPLY);
		}

		@Test
		public void updatesExistingResultCodeToDiameterUnableToComplyIfUnknownAvpIsConfigured() throws Exception {
			givenResponseAvps(UNKONWN_AVP);
			
			createHandlerWithPolicyData(policyConfiguration);
			
			response.getDiameterAnswer().addAvp(DiameterAVPConstants.RESULT_CODE, 
					ResultCode.DIAMETER_APPLICATION_UNSUPPORTED.toString());

			responseAttributeAdditionHandler.handleRequest(request, response, null);

			DiameterAssertion.assertThat(response.getDiameterAnswer())
				.hasResultCode(ResultCode.DIAMETER_UNABLE_TO_COMPLY);
		}


		@Test
		public void hintsSystemToSkipFurtherProcessingIfUnknownAvpsIsConfigured() throws Exception {
			givenResponseAvps(UNKONWN_AVP);
			
			createHandlerWithPolicyData(policyConfiguration);

			responseAttributeAdditionHandler.handleRequest(request, response, null);

			assertThat(response.isFurtherProcessingRequired(), is(equalTo(false)));
		}

	}

	public class FetchAvpFromRequestPacket {

		@Test
		public void canBeFetchedUsingREQKeyword() throws Exception {
			givenResponseAvps("0:11=$REQ:0:31");
			
			createHandlerWithPolicyData(policyConfiguration);
			
			String calling_station_id = "10-20-30-40";
			diameterRequest.addAvp(CALLING_STATION_ID, calling_station_id);

			responseAttributeAdditionHandler.handleRequest(request, response, null);

			DiameterAssertion.assertThat(response.getDiameterAnswer())
				.containsAVP("0:11", calling_station_id);
		}

		@Test
		public void multipleRequestAvpsCanBeConfiguredSeparatedByComma() throws Exception {
			givenResponseAvps("0:11=$REQ:0:31,0:1=$REQ:0:1");
			
			createHandlerWithPolicyData(policyConfiguration);
			
			String calling_station_id = "10-20-30-40";
			diameterRequest.addAvp(CALLING_STATION_ID, calling_station_id);

			String username = "somevalue";
			diameterRequest.addAvp(USER_NAME, username);
			
			responseAttributeAdditionHandler.handleRequest(request, response, null);

			DiameterAssertion.assertThat(response.getDiameterAnswer())
				.containsAVP("0:11", calling_station_id)
				.containsAVP(USER_NAME, username);
		}

		@Test
		public void addsConfiguredAvpsInResponsePacket() throws Exception {
			givenResponseAvps("0:11=$REQ:0:31");
			
			createHandlerWithPolicyData(policyConfiguration);
			
			String calling_station_id = "10-20-30-40";
			diameterRequest.addAvp(CALLING_STATION_ID, calling_station_id);

			responseAttributeAdditionHandler.handleRequest(request, response, null);

			DiameterAssertion.assertThat(response.getDiameterAnswer())
				.containsAVP("0:11", calling_station_id);
		}

		@Test
		public void addsResultCodeDiameterUnableToComplyIfConfiguredAvpNotFoundInRequestPacket() throws Exception {
			givenResponseAvps("0:11=$REQ:0:31");
			createHandlerWithPolicyData(policyConfiguration);

			responseAttributeAdditionHandler.handleRequest(request, response, null);

			DiameterAssertion.assertThat(response.getDiameterAnswer()).hasResultCode(ResultCode.DIAMETER_UNABLE_TO_COMPLY);
		}

		@Test
		public void updatesExistingResultCodeToDiameterUnableToComplyIfConfiguredAvpNotFoundInRequestPacket() throws Exception {
			givenResponseAvps("0:11=$REQ:0:31");
			
			createHandlerWithPolicyData(policyConfiguration);
			
			response.getDiameterAnswer().addAvp(DiameterAVPConstants.RESULT_CODE, ResultCode.DIAMETER_APPLICATION_UNSUPPORTED.toString());

			responseAttributeAdditionHandler.handleRequest(request, response, null);

			DiameterAssertion.assertThat(response.getDiameterAnswer())
				.hasResultCode(ResultCode.DIAMETER_UNABLE_TO_COMPLY);

		}


		@Test
		public void hintsSystemToSkipFurtherProcessingIfConfiguredAvpNotFoundInRequestPacket() throws Exception {
			givenResponseAvps("0:11=$REQ:0:31");
			
			createHandlerWithPolicyData(policyConfiguration);
			
			responseAttributeAdditionHandler.handleRequest(request, response, null);
			
			assertThat(response.isFurtherProcessingRequired(), is(equalTo(false)));
		}

	}

	public class FetchAvpFromResponsePacket {
		
		@Test
		public void canBeFetchedUsingRESKeyword() throws Exception {
			givenResponseAvps("0:11=$RES:0:25");

			createHandlerWithPolicyData(policyConfiguration);

			String Class_value = "class attribute";
			response.getDiameterAnswer().addAvp(DiameterAVPConstants.CLASS, Class_value);

			responseAttributeAdditionHandler.handleRequest(request, response, null);

			DiameterAssertion.assertThat(response.getDiameterAnswer())
			.containsAVP("0:11", "class attribute");

		}
		
		@Test
		public void multipleResponseAvpsCanBeConfiguredSeparatedByComma() throws Exception {
			givenResponseAvps("0:11=$RES:0:25,0:89=$RES:0:27");
			
			createHandlerWithPolicyData(policyConfiguration);
			
			response.getDiameterAnswer().addAvp(DiameterAVPConstants.CLASS, "class-attribute");
			response.getDiameterAnswer().addAvp(DiameterAVPConstants.SESSION_TIMEOUT, "1");
			
			responseAttributeAdditionHandler.handleRequest(request, response, null);

			DiameterAssertion.assertThat(response.getDiameterAnswer())
				.containsAVP("0:11", "class-attribute")
				.containsAVP("0:89", "1");
		}
		
		@Test
		public void addsConfiguredAvpsInResponsePacket() throws Exception {
			givenResponseAvps("0:11=$RES:0:25");
			
			createHandlerWithPolicyData(policyConfiguration);
			
			response.getDiameterAnswer().addAvp(DiameterAVPConstants.CLASS, "class attribute");

			responseAttributeAdditionHandler.handleRequest(request, response, null);

			DiameterAssertion.assertThat(response.getDiameterAnswer())
				.containsAVP("0:11", "class attribute");
		}

		@Test
		public void addsResultCodeDiameterUnableToComplyIfConfiguredAvpNotFoundInResponsePacket() throws Exception {
			givenResponseAvps("0:11=$RES:0:4");
			
			createHandlerWithPolicyData(policyConfiguration);

			responseAttributeAdditionHandler.handleRequest(request, response, null);

			DiameterAssertion.assertThat(response.getDiameterAnswer())
				.hasResultCode(ResultCode.DIAMETER_UNABLE_TO_COMPLY);
		}

		@Test
		public void updatesExistingResultCodeToDiameterUnableToComplyIfConfiguredAvpNotFoundInResponsePacket() throws Exception {
			givenResponseAvps("0:11=$RES:0:4");
			createHandlerWithPolicyData(policyConfiguration);
			response.getDiameterAnswer().addAvp(DiameterAVPConstants.RESULT_CODE, ResultCode.DIAMETER_APPLICATION_UNSUPPORTED.toString());

			responseAttributeAdditionHandler.handleRequest(request, response, null);

			DiameterAssertion.assertThat(response.getDiameterAnswer()).hasResultCode(ResultCode.DIAMETER_UNABLE_TO_COMPLY);

		}


		@Test
		public void hintsSystemToSkipFurtherProcessingIfConfiguredAvpNotFoundInResponsePacket() throws Exception {
			givenResponseAvps("0:11=$RES:0:999");
			createHandlerWithPolicyData(policyConfiguration);

			responseAttributeAdditionHandler.handleRequest(request, response, null);

			assertThat(response.isFurtherProcessingRequired(), is(equalTo(false)));
		}


	}

	public class WhenGropedAttributeIsConfigured {
		
		@Test
		public void configuredValueGivenInJsonFormatWithoutDoubleQuotes() throws Exception {
			givenResponseAvps("0:284 = { \"0:280\" = \"2001\" }");
			createHandlerWithPolicyData(policyConfiguration);

			responseAttributeAdditionHandler.handleRequest(request, response, null);

			DiameterAssertion.assertThat(response.getDiameterAnswer())
			.containsAVP("0:284.0:280", "2001");

		}
		
		@Test
		public void multipleGroupedAvpsCanBeConfiguredSeparatedByComma() throws Exception {
			givenResponseAvps("0:284 = { '0:280' = '2001' }, 0:401 = { '0:64' = '1' }");
			createHandlerWithPolicyData(policyConfiguration);

			responseAttributeAdditionHandler.handleRequest(request, response, null);

			DiameterAssertion.assertThat(response.getDiameterAnswer())
			.containsAVP("0:284.0:280", "2001")
			.containsAVP("0:401.0:64", "1");

		}
		
		@Test
		public void addsConfiguredAvpsInResponsePacket() throws Exception {
			givenResponseAvps("0:284 = { '0:280' = '2001' }");
			createHandlerWithPolicyData(policyConfiguration);

			responseAttributeAdditionHandler.handleRequest(request, response, null);

			DiameterAssertion.assertThat(response.getDiameterAnswer())
			.containsAVP("0:284.0:280", "2001");

		}

		@Test
		public void addsResultCodeDiameterUnableToComplyIfUnknownGropedAttributeIsConfigured() throws Exception {
			givenResponseAvps("0:999 = { '0:280' = '2001' }");
			createHandlerWithPolicyData(policyConfiguration);

			responseAttributeAdditionHandler.handleRequest(request, response, null);

			DiameterAssertion.assertThat(response.getDiameterAnswer()).hasResultCode(ResultCode.DIAMETER_UNABLE_TO_COMPLY);

		}

		@Test
		public void updatesExistingResultCodeDiameterUnableToComplyIfUnknownGropedAttributeIsConfigured() throws Exception {
			givenResponseAvps("0:999 = { '0:280' = '2001' }");
			createHandlerWithPolicyData(policyConfiguration);
			response.getDiameterAnswer().addAvp(DiameterAVPConstants.RESULT_CODE, ResultCode.DIAMETER_APPLICATION_UNSUPPORTED.toString());

			responseAttributeAdditionHandler.handleRequest(request, response, null);

			DiameterAssertion.assertThat(response.getDiameterAnswer()).hasResultCode(ResultCode.DIAMETER_UNABLE_TO_COMPLY);

		}


		@Test
		public void hintsSystemToSkipFurtherProcessingIfUnknownGropedAttributeIsConfigured() throws Exception {
			givenResponseAvps("0:999 = { '0:280' = '2001' }");
			createHandlerWithPolicyData(policyConfiguration);
			diameterRequest.addAvp(USER_NAME, "test");

			responseAttributeAdditionHandler.handleRequest(request, response, null);

			assertThat(response.isFurtherProcessingRequired(), is(equalTo(false)));
		}

	}

	@Test
	public void addsResultCodeDiameterUnableToComplyIfAnyOfConfiguredAttributesIsUnkonwnOrNotFound() throws Exception {
		givenResponseAvps("0:89=1235,0:11=$REQ:0:31,0:31=$RES:0:264,0:284 = { '0:280' = '2001' }");
		
		createHandlerWithPolicyData(policyConfiguration);

		responseAttributeAdditionHandler.handleRequest(request, response, null);

		DiameterAssertion.assertThat(response.getDiameterAnswer()).hasResultCode(ResultCode.DIAMETER_UNABLE_TO_COMPLY);
	}
	
	@Test
	public void updatesExistingResultCodeDiameterUnableToComplyIfAnyOfConfiguredAttributesIsUnkonwnOrNotFound() throws Exception {
		givenResponseAvps("0:89=1235,0:11=$REQ:0:31,0:31=$RES:0:264,0:284 = { '0:280' = '2001' }");
		createHandlerWithPolicyData(policyConfiguration);
		response.getDiameterAnswer().addAvp(DiameterAVPConstants.RESULT_CODE, ResultCode.DIAMETER_APPLICATION_UNSUPPORTED.toString());

		responseAttributeAdditionHandler.handleRequest(request, response, null);

		DiameterAssertion.assertThat(response.getDiameterAnswer()).hasResultCode(ResultCode.DIAMETER_UNABLE_TO_COMPLY);

	}
	
	@Test
	public void defaultResponseBehaviourIsNeverApplicable() {
		createHandlerWithPolicyData(policyConfiguration);
		
		assertFalse(responseAttributeAdditionHandler.isResponseBehaviorApplicable());
	}

	
	private void createHandlerWithPolicyData(TGPPServerPolicyData policyData) {
		responseAttributeAdditionHandler = new ResponseAttributeAdditionHandler<ApplicationRequest, ApplicationResponse>(serviceContext, policyData);
	}

	private void givenResponseAvps(String responseAttributeString) throws Exception {
		commandCodeResponseAttribute.setCommandCodes(AUTHENTICATION_AUTHORIZATION_COMMAND_CODE);
		commandCodeResponseAttribute.setResponseAttributes(responseAttributeString);
		policyConfiguration.getCommandCodeResponseAttributesList().add(commandCodeResponseAttribute);
		policyConfiguration.postRead();
	}
}
