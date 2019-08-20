package com.elitecore.aaa.radius.session.imdg;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.radius.service.RadServiceResponse;
import com.elitecore.aaa.radius.service.base.RadAuthRequestBuilder;
import com.elitecore.coreradius.commons.util.InvalidAttributeIdException;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;

public class RadiusSessionDataValueProviderTest {

	private RadServiceRequest request;
	private RadServiceResponse response;
	private RadiusSessionDataValueProvider valueProvider;

	@Before
	public void setUp() throws InvalidAttributeIdException {
		this.request = createRequest();
		this.response= createResponse();
		valueProvider = new RadiusSessionDataValueProvider(request, response);
	}

	private RadServiceRequest createRequest() throws InvalidAttributeIdException {
		RadServiceRequest request = new RadAuthRequestBuilder().addAttribute(RadiusAttributeConstants.USER_NAME_STR, "elite")
				.addAttribute("21067:117:1", "groupAVPValue").build();
		return request;
	}

	private RadServiceResponse createResponse() throws InvalidAttributeIdException {
		RadServiceResponse response = new RadAuthRequestBuilder().addAttribute(RadiusAttributeConstants.USER_NAME_STR,"eliteRes")
				.addAttribute(RadiusAttributeConstants.NAS_IP_ADDRESS_STR,"127.0.0.1").buildResponse();
		return response;
	}

	@Test
	public void providesValueFromRequest() {
		assertEquals(request.getRadiusAttribute(RadiusAttributeConstants.USER_NAME_STR).getStringValue(), valueProvider.getStringValue("0:1"));
	}
	
	@Test
	public void providesValueFromTheRequestIfConfiguredWith$REQKeyword() {
		
		assertEquals(request.getRadiusAttribute(RadiusAttributeConstants.USER_NAME_STR).getStringValue(), valueProvider.getStringValue("$REQ:0:1"));
		assertEquals(request.getRadiusAttribute("21067:117:1").getStringValue(), valueProvider.getStringValue("$REQ:21067:117:1"));
	}

	@Test
	public void providesValueFromTheResponseIfConfiguredWith$RESKeyword() {
		assertEquals(response.getRadiusAttribute(RadiusAttributeConstants.USER_NAME_STR).getStringValue(), valueProvider.getStringValue("$RES:0:1"));
		assertEquals(response.getRadiusAttribute(RadiusAttributeConstants.NAS_IP_ADDRESS_STR).getStringValue(), valueProvider.getStringValue("$RES:0:4"));
	}
	
}
