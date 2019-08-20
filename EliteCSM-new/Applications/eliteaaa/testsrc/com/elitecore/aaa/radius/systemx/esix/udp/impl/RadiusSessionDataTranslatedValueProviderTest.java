package com.elitecore.aaa.radius.systemx.esix.udp.impl;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.radius.service.base.RadAuthRequestBuilder;
import com.elitecore.aaa.radius.systemx.esix.udp.RadUDPRequest;
import com.elitecore.coreradius.commons.util.InvalidAttributeIdException;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;

public class RadiusSessionDataTranslatedValueProviderTest {
	private RadUDPRequest request;
	private RadiusSessionDataTranslatedValueProvider valueProvider;

	@Before
	public void setUp() throws InvalidAttributeIdException {
		this.request = createRequest();
		valueProvider = new RadiusSessionDataTranslatedValueProvider(request);
	}

	private RadUDPRequest createRequest() throws InvalidAttributeIdException {
		RadServiceRequest radSeerviceRequest = new RadAuthRequestBuilder().addAttribute(RadiusAttributeConstants.USER_NAME_STR, "elite")
				.addAttribute("21067:117:1", "groupAVPValue").build();

		return new RadUDPRequestImpl(radSeerviceRequest.getRequestBytes(),"secret");
	}

	@Test
	public void providesValueFromRequest() {
		assertEquals(request.getRadiusPacket().getRadiusAttribute(RadiusAttributeConstants.USER_NAME_STR).getStringValue(), valueProvider.getStringValue("0:1"));
	}

	@Test
	public void providesValueFromTheRequestIfConfiguredWith$REQKeyword() {

		assertEquals(request.getRadiusPacket().getRadiusAttribute(RadiusAttributeConstants.USER_NAME_STR).getStringValue(), valueProvider.getStringValue("$REQ:0:1"));
		assertEquals("groupAVPValue", valueProvider.getStringValue("$REQ:21067:117:1"));
	}

	@Test
	public void providesNullIfConfiguredWith$RESKeyword() {
		assertEquals(null, valueProvider.getStringValue("$RES:0:1"));
	}

}
