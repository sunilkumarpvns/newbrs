package com.elitecore.aaa.diameter.service.application.handlers;

import static com.elitecore.core.serverx.sessionx.inmemory.ISession.NO_SESSION;
import static com.elitecore.diameterapi.diameter.common.packet.DiameterAssertion.assertThat;
import static com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants.EC_CONCURRENT_LOGIN_POLICY_NAME;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.ListIterator;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.elitecore.aaa.core.diameter.DummyDiameterDictionary;
import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.service.application.ApplicationResponse;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;

import de.bechte.junit.runners.context.HierarchicalContextRunner;

@RunWith(HierarchicalContextRunner.class)
public class DiameterConcurrencyInternalHandlerTest {

	private static final String CONCURRENT_LOGIN_POLICY = "conc_policy";
	private static final String ADDITIONAL_CONCURRENT_POLICY = "add_conc_policy";
	private DiameterConcurrencyInternalHandler<ApplicationRequest, ApplicationResponse> handler;
	private DiameterRequest diameterRequest;
	private ApplicationRequest request;
	private ApplicationResponse response;
	
	@BeforeClass
	public static void setupBeforeClass() {
		DummyDiameterDictionary.getInstance();
	}
	
	@Before
	public void setup() {
		diameterRequest = new DiameterRequest();
		request = new ApplicationRequest(diameterRequest);
		response = new ApplicationResponse(diameterRequest);
		
		handler = new DiameterConcurrencyInternalHandler<ApplicationRequest, ApplicationResponse>(ADDITIONAL_CONCURRENT_POLICY);
	}
	
	@Test
	public void neverEligibleForDefaultResponseBehavior() {
		assertFalse("Default Response Behavior should not be applicable, as this handler" 
				+ "doesn not interacts with any external entity", handler.isResponseBehaviorApplicable());
	}
	
	@Test
	public void isAlwaysEligible() {
		assertTrue("Should always be eligible", handler.isEligible(request, response));
	}
	
	@Test
	public void replacesExistingConcurrentLoginPolicyWithAdditionalConcurrentPolicyInRequest() {
		request.getDiameterRequest().addAvp(EC_CONCURRENT_LOGIN_POLICY_NAME, CONCURRENT_LOGIN_POLICY);
		
		handler.handleRequest(request, response, NO_SESSION);
		
		assertThat(request.getDiameterRequest())
				  .containsAVP(EC_CONCURRENT_LOGIN_POLICY_NAME, ADDITIONAL_CONCURRENT_POLICY);
	}
	
	@Test
	public void replacesCurrentConcurrentLoginPolicyWithAdditionalConcurrentPolicyInResponse() {
		response.getDiameterAnswer().addAvp(EC_CONCURRENT_LOGIN_POLICY_NAME, CONCURRENT_LOGIN_POLICY);
		
		handler.handleRequest(request, response, NO_SESSION);
		
		assertThat(response.getDiameterAnswer())
				  .containsAVP(EC_CONCURRENT_LOGIN_POLICY_NAME, ADDITIONAL_CONCURRENT_POLICY);
	}
	
	@Test
	public void doesNotModifyRequestOrResponseIfConcurrentLoginPolicyIsNotPresentInEither() {
			
		ApplicationRequest expectedRequest = new ApplicationRequest(diameterRequest);
		ApplicationResponse expectedResponse = new ApplicationResponse(diameterRequest);
		
		assertThat(request.getDiameterRequest()).doesNotContainAVP(EC_CONCURRENT_LOGIN_POLICY_NAME);
		assertThat(response.getDiameterAnswer()).doesNotContainAVP(EC_CONCURRENT_LOGIN_POLICY_NAME);
		
		handler.handleRequest(request, response, NO_SESSION);
		
		verifyNoChange(request, expectedRequest);
		verifyNoChange(response, expectedResponse);
		
	}

	private void verifyNoChange(ApplicationResponse actual, ApplicationResponse expectedResponse) {
		ArrayList<IDiameterAVP> actualAvpList = actual.getDiameterAnswer().getAVPList();
		ArrayList<IDiameterAVP> expectedAvpList = expectedResponse.getDiameterAnswer().getAVPList();
		
		verify(actualAvpList, expectedAvpList);
	}

	private void verify(ArrayList<IDiameterAVP> actualAvpList, ArrayList<IDiameterAVP> expectedAvpList) {
		Assert.assertThat(actualAvpList.size(), CoreMatchers.equalTo(expectedAvpList.size()));

		ListIterator<IDiameterAVP> itrActual = actualAvpList.listIterator();
		ListIterator<IDiameterAVP> itrExpected = expectedAvpList.listIterator();

		while(itrActual.hasNext()) {
			assertTrue("AVPs being compared have Different values",
					itrActual.next().getStringValue().equals(itrExpected.next().getStringValue()));
		}
	}

	private void verifyNoChange(ApplicationRequest actual, ApplicationRequest expectedRequest) {
		ArrayList<IDiameterAVP> actualAvpList = actual.getDiameterRequest().getAVPList();
		ArrayList<IDiameterAVP> expectedAvpList = expectedRequest.getDiameterRequest().getAVPList();
		
		verify(actualAvpList, expectedAvpList);

	}
}
