package com.elitecore.netvertex.service.offlinernc.servicepolicy.handler;

import static com.elitecore.corenetvertex.constants.OfflineRnCKeyConstants.ABSLOUTE_FILE_NAME;
import static com.elitecore.corenetvertex.constants.OfflineRnCKeyConstants.ACCOUNTED_COST;
import static com.elitecore.corenetvertex.constants.OfflineRnCKeyConstants.ACCOUNTING_EFFECT;
import static com.elitecore.corenetvertex.constants.OfflineRnCKeyConstants.ACCOUNT_ID;
import static com.elitecore.corenetvertex.constants.OfflineRnCKeyConstants.SESSION_CONNECT_TIME;
import static org.hamcrest.CoreMatchers.containsString;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.text.ParseException;

import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.commons.tests.PrintMethodRule;
import com.elitecore.corenetvertex.constants.AccountingEffect;
import com.elitecore.netvertex.gateway.file.util.OFCSFileHelper;
import com.elitecore.netvertex.service.offlinernc.OfflineRnCServiceContext;
import com.elitecore.netvertex.service.offlinernc.core.DailyUsageListener;
import com.elitecore.netvertex.service.offlinernc.core.DailyUsageStats;
import com.elitecore.netvertex.service.offlinernc.core.OfflineRnCEvent;
import com.elitecore.netvertex.service.offlinernc.core.OfflineRnCException;
import com.elitecore.netvertex.service.offlinernc.core.PacketOutputStream;
import com.elitecore.netvertex.service.offlinernc.core.RnCRequest;
import com.elitecore.netvertex.service.offlinernc.core.RnCResponse;
import com.elitecore.netvertex.service.offlinernc.ratecard.RnCPacketBuilder;
import com.elitecore.netvertex.service.offlinernc.util.BigDecimalFormatter;
import com.elitecore.netvertex.service.offlinernc.util.RoundingModeTypes;

import de.bechte.junit.runners.context.HierarchicalContextRunner;

@RunWith(HierarchicalContextRunner.class)
public class DailyAggregationHandlerTest {

	private static final int RATING_DECIMAL_POINT = 2;
	private static final String TEST_FILE_CSV = "testFile.csv";
	private static final String TEST_ACCOUNT_1 = "test_Account_1";
	private static final String TEST_ACCOUNT_2 = "test_Account_2";
	private static final String TIME_FORMAT = "dd-MM-yyyy HH:mm:ss";
	private static final String MODULE = "DAILY-AGGREGATION-HANDLER-TEST";
	private static final String RATING_KEY = SESSION_CONNECT_TIME.getName();
	private static final String SESSION_CONNECT_TIME_VALUE = "02-11-2018 10:10:10";
	private static final String TEST_FILE_CSV_ABSOLUTE_PATH = "/data/files/testFile.csv";
	private static final RoundingModeTypes ROUNDING_MODE_TYPE = RoundingModeTypes.UPPER;
	private static final PacketOutputStream UNUSED = null;
	
	private RnCRequest request;
	private RnCResponse response;
	private DailyAggregationHandler handler; 
	
	@Mock private DailyUsageListener usageListener;
	@Mock private OfflineRnCServiceContext serviceContext;
	@Rule public PrintMethodRule printMethod = new PrintMethodRule();
	@Rule public ExpectedException expectedException = ExpectedException.none();
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		request = RnCPacketBuilder.requestBuilder()
				.with(ACCOUNT_ID, TEST_ACCOUNT_1)
				.with(ACCOUNTED_COST, "100.50")
				.with(SESSION_CONNECT_TIME, SESSION_CONNECT_TIME_VALUE)
				.build();
		response = RnCResponse.of(request);
		
		request.setAttribute(ACCOUNTED_COST.getName(), "100.50");
		
		BigDecimalFormatter currencyFormatter = new BigDecimalFormatter(RATING_DECIMAL_POINT, ROUNDING_MODE_TYPE);
		Mockito.when(serviceContext.getBigDecimalFormatter()).thenReturn(currencyFormatter);
		handler = new DailyAggregationHandler(RATING_KEY, TIME_FORMAT, usageListener);
	}
	
	@After
	public void printLog() {
		LogManager.getLogger().info(MODULE, request.getTrace());
	}

	public class FailsToAggregateIf {

		@Before
		public void setup() {
			request = RnCPacketBuilder.requestBuilder()
					.with(SESSION_CONNECT_TIME, SESSION_CONNECT_TIME_VALUE)
					.build();
			request.setLogicalFileName(TEST_FILE_CSV);
			request.setAttribute(ABSLOUTE_FILE_NAME.getName(),
					TEST_FILE_CSV_ABSOLUTE_PATH);
			request.setEventType(OfflineRnCEvent.CDR);
			
			response = RnCPacketBuilder.responseBuilder()
					.with(ACCOUNT_ID, TEST_ACCOUNT_1)
					.with(ACCOUNTED_COST, "100.50")
					.with(ACCOUNTING_EFFECT, AccountingEffect.DR.name())
					.build();
		}
		
		@Test
		public void logicalFileNameIsAbsentInRequest() throws OfflineRnCException {
			request.setLogicalFileName(null);
			handler.handleRequest(request, response, UNUSED);
			assertThat(request.getTrace(), containsString("Failed to aggregate daily usage, Reason: File name is missing from request"));
		}

		@Test
		public void absoluteFileNameIsAbsentInRequest() throws OfflineRnCException {
			request.setAttribute(ABSLOUTE_FILE_NAME.getName(), null);
			
			handler.handleRequest(request, response, UNUSED);
			assertThat(request.getTrace(), containsString("Failed to aggregate daily usage, Reason: File name is missing from request"));
		}
		
		@Test
		public void accountedCostIsAbsentInResponse() throws OfflineRnCException {
			response.setAttribute(ACCOUNTED_COST, null);
			expectedException.expect(OfflineRnCException.class);
			expectedException.expectMessage(CoreMatchers.is("InvalidEDR-MissingKey-ACCOUNTED-COST"));
			handler.handleRequest(request, response, UNUSED);
		}
		
		@Test
		public void accountNameIsAbsentInResponse() throws OfflineRnCException {
			response.setAttribute(ACCOUNT_ID, null);
			expectedException.expect(OfflineRnCException.class);
			expectedException.expectMessage(CoreMatchers.is("InvalidEDR-MissingKey-ACCOUNT-ID"));
			handler.handleRequest(request, response, UNUSED);
		}
		
		@Test
		public void ratingKeyIsAbsentInRequest() throws OfflineRnCException {
			request.setAttribute(SESSION_CONNECT_TIME.getName(), null);
			expectedException.expect(OfflineRnCException.class);
			expectedException.expectMessage(CoreMatchers.is("InvalidEDR-MissingKey-" + RATING_KEY));
			handler.handleRequest(request, response, UNUSED);
		}
		
		@Test
		public void accountingEffectIsAbsentInResponse() throws OfflineRnCException {
			response.setAttribute(ACCOUNTING_EFFECT, null);
			expectedException.expect(OfflineRnCException.class);
			expectedException.expectMessage(CoreMatchers.is("InvalidEDR-MissingKey-ACCOUNTING-EFFECT"));
			handler.handleRequest(request, response, UNUSED);
		}
		
	}
	
	@Test
	public void storesAccountWiseAggregatedDailyUnbilledUsage() throws OfflineRnCException, ParseException {
		request = RnCPacketBuilder.requestBuilder()
				.with(SESSION_CONNECT_TIME, SESSION_CONNECT_TIME_VALUE)
				.build();
		request.setLogicalFileName(TEST_FILE_CSV);
		request.setAttribute(ABSLOUTE_FILE_NAME.getName(),
				TEST_FILE_CSV_ABSOLUTE_PATH);
		request.setEventType(OfflineRnCEvent.CDR);
		
		response = RnCPacketBuilder.responseBuilder()
				.with(ACCOUNT_ID, TEST_ACCOUNT_1)
				.with(ACCOUNTED_COST, "100.515")
				.with(ACCOUNTING_EFFECT, AccountingEffect.DR.name())
				.build();
		
		handler.handleRequest(request, response, null);
		
		response = RnCPacketBuilder.responseBuilder()
				.with(ACCOUNT_ID, TEST_ACCOUNT_2)
				.with(ACCOUNTED_COST, "180.628")
				.with(ACCOUNTING_EFFECT, AccountingEffect.DR.name())
				.build();
		
		
		handler.handleRequest(request, response, UNUSED);
		
		request.setEventType(OfflineRnCEvent.EOF);
		handler.handleRequest(request, response, UNUSED);
		
		DailyUsageStats expectedDailyUsageForAcctOne = new DailyUsageStats(TEST_ACCOUNT_1,
				OFCSFileHelper.sqlDateOf(SESSION_CONNECT_TIME_VALUE, TIME_FORMAT),
				new BigDecimal("100.515"));
		
		DailyUsageStats expectedDailyUsageForAcctTwo = new DailyUsageStats(TEST_ACCOUNT_2,
				OFCSFileHelper.sqlDateOf(SESSION_CONNECT_TIME_VALUE, TIME_FORMAT),
				new BigDecimal("180.628"));
		
		verify(usageListener).recordStats(expectedDailyUsageForAcctOne);
		verify(usageListener).recordStats(expectedDailyUsageForAcctTwo);
	}
	
	public class WhileAggregating {
		
		@Test
		public void usageOfDRTypeAreAdded() throws OfflineRnCException, ParseException {
			request = RnCPacketBuilder.requestBuilder()
					.with(SESSION_CONNECT_TIME, SESSION_CONNECT_TIME_VALUE)
					.build();
			request.setLogicalFileName(TEST_FILE_CSV);
			request.setAttribute(ABSLOUTE_FILE_NAME.getName(),
					TEST_FILE_CSV_ABSOLUTE_PATH);
			request.setEventType(OfflineRnCEvent.CDR);
			
			response = RnCPacketBuilder.responseBuilder()
					.with(ACCOUNT_ID, TEST_ACCOUNT_1)
					.with(ACCOUNTED_COST, "100.50")
					.with(ACCOUNTING_EFFECT, AccountingEffect.DR.name())
					.build();
			
			handler.handleRequest(request, response, null);
			
			response = RnCPacketBuilder.responseBuilder()
					.with(ACCOUNT_ID, TEST_ACCOUNT_1)
					.with(ACCOUNTED_COST, "10.50")
					.with(ACCOUNTING_EFFECT, AccountingEffect.DR.name())
					.build();
			
			
			handler.handleRequest(request, response, UNUSED);
			
			request.setEventType(OfflineRnCEvent.EOF);
			handler.handleRequest(request, response, UNUSED);
			
			DailyUsageStats dailyUsageStats = new DailyUsageStats(TEST_ACCOUNT_1,
					OFCSFileHelper.sqlDateOf(SESSION_CONNECT_TIME_VALUE, TIME_FORMAT),
					new BigDecimal("111.00"));
			verify(usageListener).recordStats(dailyUsageStats);
		}
		
		@Test
		public void usageOfCRTypeAreSubtracted() throws OfflineRnCException, ParseException {
			request = RnCPacketBuilder.requestBuilder()
					.with(SESSION_CONNECT_TIME, SESSION_CONNECT_TIME_VALUE)
					.build();
			request.setLogicalFileName(TEST_FILE_CSV);
			request.setAttribute(ABSLOUTE_FILE_NAME.getName(),
					TEST_FILE_CSV_ABSOLUTE_PATH);
			request.setEventType(OfflineRnCEvent.CDR);
			
			response = RnCPacketBuilder.responseBuilder()
					.with(ACCOUNT_ID, TEST_ACCOUNT_1)
					.with(ACCOUNTED_COST, "100.50")
					.with(ACCOUNTING_EFFECT, AccountingEffect.CR.name())
					.build();
			
			handler.handleRequest(request, response, UNUSED);
			
			response = RnCPacketBuilder.responseBuilder()
					.with(ACCOUNT_ID, TEST_ACCOUNT_1)
					.with(ACCOUNTED_COST, "10.50")
					.with(ACCOUNTING_EFFECT, AccountingEffect.DR.name())
					.build();
			
			handler.handleRequest(request, response, UNUSED);
			
			request.setEventType(OfflineRnCEvent.EOF);
			handler.handleRequest(request, response, UNUSED);
			
			DailyUsageStats dailyUsageStats = new DailyUsageStats(TEST_ACCOUNT_1,
					OFCSFileHelper.sqlDateOf(SESSION_CONNECT_TIME_VALUE, TIME_FORMAT),
					new BigDecimal("-90.00"));
			
			verify(usageListener).recordStats(dailyUsageStats);
		}
	}
}