package com.elitecore.netvertex.service.offlinernc.servicepolicy.handler;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Date;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import com.elitecore.commons.base.SimpleDateFormatThreadLocal;
import com.elitecore.commons.tests.PrintMethodRule;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.corenetvertex.constants.OfflineRnCKeyConstants;
import com.elitecore.netvertex.core.conf.impl.DummyNetvertexServerConfiguration;
import com.elitecore.netvertex.core.conf.impl.SystemParameterConfiguration;
import com.elitecore.netvertex.service.offlinernc.core.OfflineRnCErrorCodes;
import com.elitecore.netvertex.service.offlinernc.core.OfflineRnCErrorMessages;
import com.elitecore.netvertex.service.offlinernc.core.OfflineRnCException;
import com.elitecore.netvertex.service.offlinernc.core.PacketOutputStream;
import com.elitecore.netvertex.service.offlinernc.core.RnCRequest;
import com.elitecore.netvertex.service.offlinernc.core.RnCResponse;
import com.elitecore.netvertex.service.offlinernc.guiding.Lob;
import com.elitecore.netvertex.service.offlinernc.guiding.Service;
import com.elitecore.netvertex.service.offlinernc.guiding.conf.GuidingConfiguration;
import com.elitecore.netvertex.service.offlinernc.util.TimeUtility;

import de.bechte.junit.runners.context.HierarchicalContextRunner;

@RunWith(HierarchicalContextRunner.class)
public class PartnerGuideHandlerTest {

	private static final String UNKNOWN_VALUE = "UnknownValue";
	private static final String ACCOUNT_IDENTIFIER = "AIRTEL_I";
	private static final String ACCOUNT_ID = "ABC-123";
	private static final String LOB_ALIAS = "DOM";
	private static final String LOB_NAME = "Domestic";
	private static final String SERVICE_ALIAS = "CALL";
	private static final String SERVICE_NAME = "Calling";
	
	private static final String TRAFFIC_TYPE = "Transit";
	private static final String ACCOUNT_IDENTIFIER_TYPE = "TADIG-CODE";
	private static final String PARTNER_NAME = "Airtel";
	private static final OfflineRnCErrorCodes PARTNER_NOT_FOUND_CODE = OfflineRnCErrorCodes.PARTNER_NOT_FOUND;

	private static final String SESSION_CONNECT_TIME = "02-02-2015 10:00:00";
	private static final String END_DATE = "02-03-2015 10:00:00";
	private static final String TIME_FORMAT = "dd-MM-yyyy HH:mm:ss";

	@Rule
	public PrintMethodRule printMethodRule = new PrintMethodRule();
	
	@Rule
	public ExpectedException expected = ExpectedException.none();

	private SimpleDateFormatThreadLocal simpleDateFormatThreadLocal = SimpleDateFormatThreadLocal.create(TIME_FORMAT);
	private PartnerGuideHandler partnerGuideHandler;
	private RnCRequest request;
	private GuidingConfiguration partnerGuideDetails;
	private RnCResponse response;
	private PacketOutputStream out;
	private DummyNetvertexServerConfiguration serverConfiguration;
	private SystemParameterConfiguration spySystemParameterConf;
	private Lob lob;
	private Service service;

	@Before
	public void setUp() throws InitializationFailedException, ParseException {
		serverConfiguration = new DummyNetvertexServerConfiguration();
		spySystemParameterConf = serverConfiguration.spySystemParameterConf();
		Mockito.when(spySystemParameterConf.getEdrDateTimeStampFormat()).thenReturn(TIME_FORMAT);
		
		
		partnerGuideDetails = new GuidingConfiguration();
		partnerGuideDetails.setAccountIdentifierType(ACCOUNT_IDENTIFIER_TYPE);
		partnerGuideDetails.setAccountIdentifier(ACCOUNT_IDENTIFIER);
		partnerGuideDetails.setAccountId(ACCOUNT_ID);
		partnerGuideDetails.setPartnerName(PARTNER_NAME);
		lob = new Lob(LOB_NAME, LOB_ALIAS);
		service = new Service(SERVICE_NAME,	SERVICE_ALIAS);
		partnerGuideDetails.setLob(lob);
		partnerGuideDetails.setService(service);
		partnerGuideDetails.setTrafficType(TRAFFIC_TYPE);

		Date date = simpleDateFormatThreadLocal.get().parse(SESSION_CONNECT_TIME);
		Timestamp startDate = new Timestamp(date.getTime());
		partnerGuideDetails.setStartDate(startDate);
		
		serverConfiguration = new DummyNetvertexServerConfiguration();
		SystemParameterConfiguration spySystemParameterConf = serverConfiguration.spySystemParameterConf();
		Mockito.when(spySystemParameterConf.getRateSelectionWhenDateChange()).thenReturn(OfflineRnCKeyConstants.SESSION_CONNECT_TIME.getName());
		Mockito.when(spySystemParameterConf.getEdrDateTimeStampFormat()).thenReturn(TIME_FORMAT);
		
		partnerGuideHandler = new PartnerGuideHandler(Arrays.asList(partnerGuideDetails), spySystemParameterConf);

		partnerGuideHandler.init();

		request = new RnCRequest();
		request.setAttribute(OfflineRnCKeyConstants.LINE_OF_BUSINESS.getName(), LOB_ALIAS);
		request.setAttribute(OfflineRnCKeyConstants.SERVICE_NAME.getName(), SERVICE_ALIAS);
		request.setAttribute(OfflineRnCKeyConstants.TRAFFIC_TYPE.getName(), TRAFFIC_TYPE);
		request.setAttribute(OfflineRnCKeyConstants.ACCOUNT_IDENTIFIER.getName(), ACCOUNT_IDENTIFIER);
		request.setAttribute(OfflineRnCKeyConstants.SESSION_CONNECT_TIME.getName(), SESSION_CONNECT_TIME);
		
		response = RnCResponse.of(request);
	}

	@Test
	public void selectsPartnerWhenKeyMatches() throws OfflineRnCException {
		partnerGuideHandler.handleRequest(request, response, out);

		assertThat(response.getAttribute(OfflineRnCKeyConstants.PARTNER_NAME.getName()), is(equalTo(PARTNER_NAME)));
		assertThat(response.getAttribute(OfflineRnCKeyConstants.ACCOUNT_ID.getName()), is(equalTo(ACCOUNT_ID)));
		assertThat(response.getAttribute(OfflineRnCKeyConstants.ACCOUNT_IDENTIFIER_TYPE.getName()), is(equalTo(ACCOUNT_IDENTIFIER_TYPE)));
	}

	@Test
	public void givenLoBAliasDoesNotMatchReturnsPartnerNotFoundCode() throws OfflineRnCException {
		try {
			request.setAttribute(OfflineRnCKeyConstants.LINE_OF_BUSINESS.getName(), UNKNOWN_VALUE);

			partnerGuideHandler.handleRequest(request, response, out);

			fail("Expected exception but was not thrown");
		} catch (OfflineRnCException ex) {
			assertPartnerNotFoundException(ex);
		}
	}


	@Test
	public void givenServiceAliasDoesNotMatchReturnsPartnerNotFoundCode() throws OfflineRnCException {
		try {
			request.setAttribute(OfflineRnCKeyConstants.SERVICE_NAME.getName(), UNKNOWN_VALUE);

			partnerGuideHandler.handleRequest(request, response, out);

			fail("Expected exception but was not thrown");
		} catch (OfflineRnCException ex) {
			assertPartnerNotFoundException(ex);
		}
	}

	@Test
	public void givenAccountIdentifierValueDoesNotMatchReturnsPartnerNotFoundCode() throws OfflineRnCException {
		try {
			request.setAttribute(OfflineRnCKeyConstants.ACCOUNT_IDENTIFIER.getName(), UNKNOWN_VALUE);

			partnerGuideHandler.handleRequest(request, response, out);

			fail("Expected exception but was not thrown");
		} catch (OfflineRnCException ex) {
			assertPartnerNotFoundException(ex);
		}
	}

	@Test
	public void givenTrafficTypeDoesNotMatchReturnsPartnerNotFoundCode() throws OfflineRnCException {
		try {
			request.setAttribute(OfflineRnCKeyConstants.TRAFFIC_TYPE.getName(), UNKNOWN_VALUE);

			partnerGuideHandler.handleRequest(request, response, out);
			fail("Expected exception but was not thrown");
		} catch (OfflineRnCException ex) {
			assertPartnerNotFoundException(ex);
		}
	}

	public class WhenEndDateIsNotSpecified {

		@Before
		public void setup() {
			partnerGuideDetails.setEndDate(null);
		}

		@Test
		public void sessionStartDateBeforeStartDateReturnsPartnerNotFoundCode() throws ParseException, OfflineRnCException {
			try {
				request.setAttribute(OfflineRnCKeyConstants.SESSION_CONNECT_TIME.getName(), TimeUtility.dayBefore(SESSION_CONNECT_TIME, simpleDateFormatThreadLocal.get()));

				partnerGuideHandler.handleRequest(request, response, out);

				fail("Expected exception but was not thrown");
			} catch (OfflineRnCException ex) {
				assertPartnerNotFoundException(ex);
			}
		}

		@Test
		public void sessionStartDateAfterStartDateReturnPartnerName() throws ParseException, OfflineRnCException {
			request.setAttribute(OfflineRnCKeyConstants.SESSION_CONNECT_TIME.getName(), TimeUtility.dayAfter(SESSION_CONNECT_TIME, simpleDateFormatThreadLocal.get()));

			partnerGuideHandler.handleRequest(request, response, out);

			assertEquals(PARTNER_NAME, response.getAttribute(OfflineRnCKeyConstants.PARTNER_NAME.getName()));
		}

		@Test
		public void whenSessionStartDateIsBeforeStartDateReturnsPartnerNotFoundCode() throws ParseException, OfflineRnCException {
			try {
				request.setAttribute(OfflineRnCKeyConstants.SESSION_CONNECT_TIME.getName(), TimeUtility.dayBefore(SESSION_CONNECT_TIME, simpleDateFormatThreadLocal.get()));

				partnerGuideHandler.handleRequest(request, response, out);

				fail("Expected exception but was not thrown");
			} catch (OfflineRnCException ex) {
				assertPartnerNotFoundException(ex);
			}
		}

		@Test
		public void whenSessionStartDateIsSameAsStartDateReturnPartnerName() throws OfflineRnCException {
			request.setAttribute(OfflineRnCKeyConstants.SESSION_CONNECT_TIME.getName(), SESSION_CONNECT_TIME);

			partnerGuideHandler.handleRequest(request, response, out);

			assertEquals(PARTNER_NAME, response.getAttribute(OfflineRnCKeyConstants.PARTNER_NAME.getName()));
		}
	}


	public class WhenEndDateIsSpecified {
		private Timestamp endDate;

		@Before
		public void setUp() throws ParseException, InitializationFailedException {
			Date date = simpleDateFormatThreadLocal.get().parse(END_DATE);
			endDate = new Timestamp(date.getTime());
			partnerGuideDetails.setEndDate(endDate);

			serverConfiguration = new DummyNetvertexServerConfiguration();
			SystemParameterConfiguration spySystemParameterConf = serverConfiguration.spySystemParameterConf();
			Mockito.when(spySystemParameterConf.getRateSelectionWhenDateChange()).thenReturn(OfflineRnCKeyConstants.SESSION_CONNECT_TIME.getName());
			Mockito.when(spySystemParameterConf.getEdrDateTimeStampFormat()).thenReturn(TIME_FORMAT);
			
			partnerGuideHandler = new PartnerGuideHandler(Arrays.asList(partnerGuideDetails),spySystemParameterConf);

			partnerGuideHandler.init();
		}

		@Test
		public void whenSessionStartDateIsSameAsEndDateReturnPartnerName() throws OfflineRnCException {
			request.setAttribute(OfflineRnCKeyConstants.SESSION_CONNECT_TIME.getName(), END_DATE);

			partnerGuideHandler.handleRequest(request, response, out);

			assertEquals(PARTNER_NAME, response.getAttribute(OfflineRnCKeyConstants.PARTNER_NAME.getName()));
		}

		@Test
		public void whenSessionStartDateIsAfterEndDateReturnsPartnerNotFoundCode() throws ParseException, OfflineRnCException {
			try {
				request.setAttribute(OfflineRnCKeyConstants.SESSION_CONNECT_TIME.getName(), TimeUtility.dayAfter(END_DATE, simpleDateFormatThreadLocal.get()));

				partnerGuideHandler.handleRequest(request, response, out);

				assertEquals(PARTNER_NOT_FOUND_CODE, response.getAttribute(OfflineRnCKeyConstants.ERROR_CODE.getName()));	fail("Expected exception but was not thrown");
			} catch (OfflineRnCException ex) {
				assertPartnerNotFoundException(ex);
			}
		}

		@Test
		public void whenSessionStartDateIsSameAsStartAndEndDateReturnsPartnerName() throws OfflineRnCException {
			partnerGuideDetails.setStartDate(endDate);
			request.setAttribute(OfflineRnCKeyConstants.SESSION_CONNECT_TIME.getName(), END_DATE);

			partnerGuideHandler.handleRequest(request, response, out);

			assertEquals(PARTNER_NAME, response.getAttribute(OfflineRnCKeyConstants.PARTNER_NAME.getName()));
		}

		@Test
		public void sessionStartDateBeforeStartDateReturnsPartnerNotFoundCode() throws ParseException {
			try {
				request.setAttribute(OfflineRnCKeyConstants.SESSION_CONNECT_TIME.getName(), TimeUtility.dayBefore(SESSION_CONNECT_TIME, simpleDateFormatThreadLocal.get()));

				partnerGuideHandler.handleRequest(request, response, out);

				fail("Expected exception but was not thrown");
			} catch (OfflineRnCException ex) {
				assertPartnerNotFoundException(ex);
			}
		}

		@Test
		public void sessionStartDateAfterStartDateReturnPartnerName() throws ParseException, OfflineRnCException {
			request.setAttribute(OfflineRnCKeyConstants.SESSION_CONNECT_TIME.getName(), TimeUtility.dayAfter(SESSION_CONNECT_TIME, simpleDateFormatThreadLocal.get()));

			partnerGuideHandler.handleRequest(request, response, out);

			assertEquals(PARTNER_NAME, response.getAttribute(OfflineRnCKeyConstants.PARTNER_NAME.getName()));
		}

		@Test
		public void whenSessionStartDateIsBeforeStartDateReturnsPartnerNotFoundCode() throws ParseException, OfflineRnCException {
			try {
				request.setAttribute(OfflineRnCKeyConstants.SESSION_CONNECT_TIME.getName(), TimeUtility.dayBefore(SESSION_CONNECT_TIME, simpleDateFormatThreadLocal.get()));

				partnerGuideHandler.handleRequest(request, response, out);

				fail("Expected exception but was not thrown");
			} catch (OfflineRnCException ex) {
				assertPartnerNotFoundException(ex);
			}
		}

		@Test
		public void whenSessionStartDateIsSameAsStartDateReturnPartnerName() throws OfflineRnCException {
			request.setAttribute(OfflineRnCKeyConstants.SESSION_CONNECT_TIME.getName(), SESSION_CONNECT_TIME);

			partnerGuideHandler.handleRequest(request, response, out);

			assertEquals(PARTNER_NAME, response.getAttribute(OfflineRnCKeyConstants.PARTNER_NAME.getName()));
		}
	}
	
	private static void assertPartnerNotFoundException(OfflineRnCException ex) {
		assertThat(ex.getCode(), is(equalTo(PARTNER_NOT_FOUND_CODE)));
		assertThat(ex.getMessage(), is(equalTo(OfflineRnCErrorMessages.PARTNER_NOT_FOUND.message())));
	}
}
