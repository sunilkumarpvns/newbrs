package com.elitecore.aaa.diameter.service.application.handlers;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.spy;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

import com.elitecore.aaa.core.diameter.DummyDiameterDictionary;
import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.service.application.ApplicationResponse;
import com.elitecore.commons.tests.PrintMethodRule;
import com.elitecore.core.serverx.policies.ManagerInitialzationException;
import com.elitecore.coreradius.commons.util.RadiusUtility;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAssertion;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.util.DiameterDictionary;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;

public class AuthProfileLevelAttributeAdditionHandlerTest {

	private AuthProfileLevelAttributeAdditionHandler<ApplicationRequest, ApplicationResponse> authProfileLevelAttributeAdditionalHandler;
	private ApplicationRequest request;
	private ApplicationResponse response;
	private DiameterRequest diameterRequest;
	
	private DiameterDictionary diameterDictionary;
	
	@Rule public PrintMethodRule printMethod = new PrintMethodRule();
	
	@BeforeClass
	public static void setUpBeforeClass() throws ManagerInitialzationException {
		DummyDiameterDictionary.getInstance();
	}

	@Before
	public void setUp() {
		diameterDictionary = spy(DiameterDictionary.getInstance());
		diameterRequest =  new DiameterRequest();
		request = new ApplicationRequest(diameterRequest);
		response = new ApplicationResponse(diameterRequest);
		authProfileLevelAttributeAdditionalHandler = new AuthProfileLevelAttributeAdditionHandler<ApplicationRequest, ApplicationResponse>(diameterDictionary);
	}

	@Test
	public void isAlwaysEligible() throws Exception {
		assertTrue(authProfileLevelAttributeAdditionalHandler.isEligible(request, response));
	}

	@Test
	public void copiesFramedIpAddressFromRequestToResponse() {
		request.getDiameterRequest().addAvp(DiameterAVPConstants.FRAMED_IP_ADDRESS, "10.20.30.40");

		authProfileLevelAttributeAdditionalHandler.handleRequest(request, response, null);

		DiameterAssertion.assertThat(response.getDiameterAnswer())
			.containsAVP(DiameterAVPConstants.FRAMED_IP_ADDRESS, "10.20.30.40");

	}

	@Test
	public void copiesFramedIpNetMaskFromRequestToResponse() {
		request.getDiameterRequest().addAvp(DiameterAVPConstants.FRAMED_IP_NETMASK, "255.255.255.0");

		authProfileLevelAttributeAdditionalHandler.handleRequest(request, response, null);

		DiameterAssertion.assertThat(response.getDiameterAnswer())
			.containsAVP(DiameterAVPConstants.FRAMED_IP_NETMASK, RadiusUtility.bytesToHex("255.255.255.0".getBytes()));

	}

	@Test
	public void copiesFramedIPV6PrefixFromRequestToResponse() {
		request.getDiameterRequest().addAvp(DiameterAVPConstants.FRAMED_IPV6_PREFIX, "fe80::a61f:72ff:fe91:6bbc");

		authProfileLevelAttributeAdditionalHandler.handleRequest(request, response, null);

		DiameterAssertion.assertThat(response.getDiameterAnswer())
			.containsAVP(DiameterAVPConstants.FRAMED_IPV6_PREFIX, RadiusUtility.bytesToHex("fe80::a61f:72ff:fe91:6bbc".getBytes()));

	}

	@Test
	public void copiesFramedPoolFromRequestToResponse() {
		request.getDiameterRequest().addInfoAvp(DiameterAVPConstants.EC_FRAMED_POOL_NAME, "framed-pool-name");

		authProfileLevelAttributeAdditionalHandler.handleRequest(request, response, null);

		DiameterAssertion.assertThat(response.getDiameterAnswer())
			.containsAVP(DiameterAVPConstants.FRAMED_POOL, RadiusUtility.bytesToHex("framed-pool-name".getBytes()));

	}
	
	@Test
	public void copyingOfFramedPoolInResponseCanBeDisabledByRemovingAvpFromDictionary() {
		
		Mockito.doReturn(null).when(diameterDictionary).getKnownAttribute(DiameterAVPConstants.FRAMED_POOL);
		
		request.getDiameterRequest().addInfoAvp(DiameterAVPConstants.EC_FRAMED_POOL_NAME, "framed-pool-name");

		authProfileLevelAttributeAdditionalHandler.handleRequest(request, response, null);
		
		
		DiameterAssertion.assertThat(response.getDiameterAnswer())
			.doesNotContainAVP(DiameterAVPConstants.FRAMED_POOL);

	}
	
	@Test
	public void copyingOfFramedIpAddressInResponseCanBeDisabledByRemovingAvpFromDictionary() {
		
		Mockito.doReturn(null).when(diameterDictionary).getKnownAttribute(DiameterAVPConstants.FRAMED_IP_ADDRESS);
		
		request.getDiameterRequest().addAvp(DiameterAVPConstants.FRAMED_IP_ADDRESS, "10.20.30.40");

		authProfileLevelAttributeAdditionalHandler.handleRequest(request, response, null);
		
		DiameterAssertion.assertThat(response.getDiameterAnswer())
			.doesNotContainAVP(DiameterAVPConstants.FRAMED_IP_ADDRESS);

	}
	
	@Test
	public void copyingOfFramedIpNetmaskInResponseCanBeDisabledByRemovingAvpFromDictionary() {

		Mockito.doReturn(null).when(diameterDictionary).getKnownAttribute(DiameterAVPConstants.FRAMED_IP_NETMASK);
		
		request.getDiameterRequest().addAvp(DiameterAVPConstants.FRAMED_IP_NETMASK, "255.255.255.0");

		authProfileLevelAttributeAdditionalHandler.handleRequest(request, response, null);
		
		
		DiameterAssertion.assertThat(response.getDiameterAnswer())
			.doesNotContainAVP(DiameterAVPConstants.FRAMED_IP_NETMASK);

	}
	
	@Test
	public void copyingOfFramedIpv6PrefixInResponseCanBeDisabledByRemovingAvpFromDictionary() {
		Mockito.doReturn(null).when(diameterDictionary).getKnownAttribute(DiameterAVPConstants.FRAMED_IPV6_PREFIX);

		request.getDiameterRequest().addAvp(DiameterAVPConstants.FRAMED_IPV6_PREFIX, "fe80::a61f:72ff:fe91:6bbc");

		authProfileLevelAttributeAdditionalHandler.handleRequest(request, response, null);

		DiameterAssertion.assertThat(response.getDiameterAnswer())
			.doesNotContainAVP(DiameterAVPConstants.FRAMED_IPV6_PREFIX);

	}
	
	@Test
	public void defaultResponseBehaviourIsNotApplicable() {
		assertFalse(authProfileLevelAttributeAdditionalHandler.isResponseBehaviorApplicable());
	}

}
