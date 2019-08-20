package com.elitecore.aaa.radius.session;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

import com.elitecore.aaa.core.radius.dictionary.RadiusDictionaryTestHarness;
import com.elitecore.aaa.radius.service.auth.RadAuthRequest;
import com.elitecore.aaa.radius.service.base.RadAuthRequestBuilder;
import com.elitecore.commons.tests.PrintMethodRule;
import com.elitecore.coreradius.commons.util.InvalidAttributeIdException;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;

public class RadiusSessionIdTest {

	private static final String USER_NAME = "test";
	private static final String WIMAX_SESSION_ID = "24757:4";
	private static final String CORRELATION_ID = "5535:44";
	private static final String WIMAX_SESSION_ID_VALUE = "0x31313131";
	private static final String CORRELATION_ID_VALUE = "2222";
	private RadAuthRequest request;

	@Rule public PrintMethodRule print = new PrintMethodRule();

	@BeforeClass
	public static void setUp() throws Exception {
		RadiusDictionaryTestHarness.getInstance();
	}

	@Test
	public void returnsValueOfWimaxSessionIdAttributeIfItAvailable() throws Exception {
		request = new RadAuthRequestBuilder()
				.addAttribute(WIMAX_SESSION_ID, WIMAX_SESSION_ID_VALUE)
				.addAttribute(CORRELATION_ID, CORRELATION_ID_VALUE)
				.addAttribute(RadiusAttributeConstants.USER_NAME_STR, USER_NAME)
				.packetType(RadiusConstants.ACCESS_REQUEST_MESSAGE)
				.build();

		assertThat(RadiusSessionId.sessionId(request), is(WIMAX_SESSION_ID_VALUE));

	}

	@Test
	public void returnsValueOfCorrelationIdAttributeIfWimaxSessionIdIsNotAvailable() throws Exception {

		request =  new RadAuthRequestBuilder()
				.addAttribute(CORRELATION_ID, CORRELATION_ID_VALUE)
				.addAttribute(RadiusAttributeConstants.USER_NAME_STR, USER_NAME)
				.packetType(RadiusConstants.ACCESS_REQUEST_MESSAGE)
				.build();

		assertThat(RadiusSessionId.sessionId(request), is(CORRELATION_ID_VALUE));

	}
	
	@Test
	public void createRadiusSessionIdForTheUsingAvailableConfiguredAttributeWhenNeitherOfWimaxSessionIdNorCorrelationIdIsPresentInRequest() throws Exception {

		System.setProperty(RadiusSessionId.RADIUS_SESSION_ID, "0:1;0:31,0:4");

		request =  new RadAuthRequestBuilder()
				.addAttribute(RadiusAttributeConstants.USER_NAME_STR, USER_NAME)
				.addAttribute(RadiusAttributeConstants.CALLING_STATION_ID_STR, "10.10.10.10")
				.addAttribute(RadiusAttributeConstants.NAS_IP_ADDRESS_STR, "20.20.20.20")
				.packetType(RadiusConstants.ACCESS_REQUEST_MESSAGE)
				.build();

		assertThat(RadiusSessionId.sessionId(request), is("test;10.10.10.10;20.20.20.20"));

		request =  new RadAuthRequestBuilder()
				.addAttribute(RadiusAttributeConstants.USER_NAME_STR, USER_NAME)
				.packetType(RadiusConstants.ACCESS_REQUEST_MESSAGE)
				.build();

		assertThat(RadiusSessionId.sessionId(request), is(USER_NAME));

		request =  new RadAuthRequestBuilder()
				.packetType(RadiusConstants.ACCESS_REQUEST_MESSAGE)
				.build();

		assertNull(RadiusSessionId.sessionId(request));

	}
	
	@Test
	public void sessionIdIsCreatedBasedOnLatestValueForRadiusSessionIdInMiscellaneousConfiguration() throws InvalidAttributeIdException {
		// This value is set while reading of miscellaneous configuration

		System.setProperty(RadiusSessionId.RADIUS_SESSION_ID, "0:1;0:31,0:4");

		request =  new RadAuthRequestBuilder()
				.addAttribute(RadiusAttributeConstants.USER_NAME_STR, USER_NAME)
				.addAttribute(RadiusAttributeConstants.CALLING_STATION_ID_STR, "10.10.10.10")
				.addAttribute(RadiusAttributeConstants.NAS_IP_ADDRESS_STR, "20.20.20.20")
				.packetType(RadiusConstants.ACCESS_REQUEST_MESSAGE)
				.build();

		assertThat(RadiusSessionId.sessionId(request), is("test;10.10.10.10;20.20.20.20"));

		System.setProperty(RadiusSessionId.RADIUS_SESSION_ID, "0:1;0:31");

		assertThat(RadiusSessionId.sessionId(request), is("test;10.10.10.10"));
	}
	
}
