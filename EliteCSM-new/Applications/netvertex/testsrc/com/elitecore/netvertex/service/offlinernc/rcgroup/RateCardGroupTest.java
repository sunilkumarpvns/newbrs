package com.elitecore.netvertex.service.offlinernc.rcgroup;

import static com.elitecore.netvertex.service.offlinernc.util.TimeUtility.dayBefore;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;

import com.elitecore.commons.base.SimpleDateFormatThreadLocal;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.commons.tests.PrintMethodRule;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.corenetvertex.constants.OfflineRnCKeyConstants;
import com.elitecore.corenetvertex.constants.OfflineRnCKeyValueConstants;
import com.elitecore.corenetvertex.constants.RateCardGroupTimeSlotConstant;
import com.elitecore.corenetvertex.pd.calender.CalenderData;
import com.elitecore.corenetvertex.pd.calender.CalenderDetails;
import com.elitecore.corenetvertex.pd.currency.CurrencyData;
import com.elitecore.corenetvertex.pd.pbss.ratecard.RateCardData;
import com.elitecore.corenetvertex.pd.pbss.ratecard.RateCardVersionDetail;
import com.elitecore.corenetvertex.pd.pbss.ratecard.RateCardVersionRelation;
import com.elitecore.corenetvertex.pd.pbss.ratecardgroup.RateCardGroupData;
import com.elitecore.corenetvertex.pd.pbss.ratecardgroup.TimeSlotRelationData;
import com.elitecore.netvertex.core.DummyNetvertexServerContextImpl;
import com.elitecore.netvertex.core.conf.impl.DummyNetvertexServerConfiguration;
import com.elitecore.netvertex.core.conf.impl.SystemParameterConfiguration;
import com.elitecore.netvertex.service.offlinernc.DummyOfflineRnCServiceContext;
import com.elitecore.netvertex.service.offlinernc.core.OfflineRnCException;
import com.elitecore.netvertex.service.offlinernc.core.RnCRequest;
import com.elitecore.netvertex.service.offlinernc.core.RnCResponse;
import com.elitecore.netvertex.service.offlinernc.currency.CurrencyExchange;
import com.elitecore.netvertex.service.offlinernc.currency.CurrencyExchangeFactory;
import com.elitecore.netvertex.service.offlinernc.ratecard.RateCardFactory;

import de.bechte.junit.runners.context.HierarchicalContextRunner;


/*
 * TODO Pending cases
 * 
 * 1) Have not verified that end date exact then also calendar must be selected
 * 2) Add Logging
 *
 */
@RunWith(HierarchicalContextRunner.class)
public class RateCardGroupTest {

	private static final String SESSION_CONNECT_TIME = "15-01-2018 10:00:00";
	private static final String SESSION_DISCONNECT_TIME = "16-01-2018 10:00:00";
	private static final String CALLING_NAME = "India";
	private static final String CALLED_NAME = "United Kingdom";
	private static final String SESSION_TIME = "1";
	private static final String TIER_RATE_TYPE = "FLAT";
	private static final String ALWAYS_TRUE_CONDITION = "CALLING-NAME = \"India\"";
	private static final String ALWAYS_FALSE_CONDITION = "CALLING-NAME = \"UK\"";
	private static final String CURRENCY = "INR";
	private static final String ACCOUNT_EFFECT = "CR";
	private static final String RATE_UOM = "SECOND";
	private static final double PULSE = 1;
	private static final double SLAB = 100;
	private static final double SPECIAL_DAYS_RATE = 60;
	private static final double WEEKEND_RATE = 70;
	private static final double OFF_PEAK_RATE = 80;
	private static final double PEAK_RATE = 90;
	private static final double RATE2_RATE = 100;
	private static final double RATE3_RATE = 110;
	private static final String SPECIAL_RATE_CARD = "SPECIAL_RATE_CARD";
	private static final String WEEKEND_RATE_CARD = "WEEKEND_RATE_CARD";
	private static final String OFF_PEAK_RATE_CARD = "OFF_PEAK_RATE_CARD";
	private static final String PEAK_RATE_CARD = "PEAK_RATE_CARD";
	private static final String RATE2_RATE_CARD = "RATE2_RATE_CARD";
	private static final String RATE3_RATE_CARD = "RATE3_RATE_CARD";
	private static final String KEY_SEPARATOR = ":";

	private static final String RATING_KEY = "SESSION-CONNECT-TIME"; 
	private static final String TIME_FORMAT = "dd-MM-yyyy HH:mm:ss";


	@Rule
	public PrintMethodRule printMethodRule = new PrintMethodRule();

	private SimpleDateFormatThreadLocal simpleDateFormatThreadLocal = SimpleDateFormatThreadLocal.create(TIME_FORMAT);
	private RateCardGroupFactory rateCardGroupFactory;
	private RateCardGroupData rateCardGroupData;
	private RnCRequest request;
	private RnCResponse response;
	private RateCardGroup rateCardGroup;
	private List<RateCardVersionRelation> rateCardVersionRelations;
	private RateCardVersionRelation rateCardVersionRelation;
	private List<RateCardVersionDetail> rateCardVersionDetails;
	private RateCardVersionDetail rateCardVersionDetail;
	private Timestamp fromDate = Timestamp.valueOf("2018-01-15 10:00:00");
	private Timestamp toDate = Timestamp.valueOf("2018-02-15 10:00:00");
	private DummyOfflineRnCServiceContext offlineRnCServiceContext;
	private DummyNetvertexServerContextImpl netvertexServerContext;
	private DummyNetvertexServerConfiguration serverConfiguration;
	private CurrencyExchangeFactory currencyExchangeFactory;

	@Mock
	private SessionFactory sessionFactory;

	@Before
	public void setUp() throws InitializationFailedException {
		serverConfiguration = new DummyNetvertexServerConfiguration();
		SystemParameterConfiguration spySystemParams = serverConfiguration.spySystemParameterConf();

		Mockito.when(spySystemParams.getSystemCurrency()).thenReturn(CURRENCY);
		Mockito.when(spySystemParams.getMultiValueSeparator()).thenReturn(KEY_SEPARATOR);
		Mockito.when(spySystemParams.getRateSelectionWhenDateChange()).thenReturn(RATING_KEY);
		Mockito.when(spySystemParams.getEdrDateTimeStampFormat()).thenReturn(TIME_FORMAT);

		netvertexServerContext = new DummyNetvertexServerContextImpl();
		netvertexServerContext.setNetvertexServerConfiguration(serverConfiguration);

		offlineRnCServiceContext = new DummyOfflineRnCServiceContext();
		offlineRnCServiceContext.setServerContext(netvertexServerContext);

		currencyExchangeFactory = new CurrencyExchangeFactory(sessionFactory);
		List<CurrencyData> currencies = new ArrayList<>();
		CurrencyData currencyData = new CurrencyData();
		currencyData.setFromIsoCode(CURRENCY);
		currencyData.setToIsoCode(CURRENCY);
		currencyData.setRate(1.0);
		currencyData.setEffectiveDate(fromDate);
		currencies.add(currencyData);
		CurrencyExchange currencyExchange = currencyExchangeFactory.create(currencies);
		offlineRnCServiceContext.setCurrencyExchange(currencyExchange);

		rateCardGroupFactory = new RateCardGroupFactory(new RateCardFactory(offlineRnCServiceContext, spySystemParams),spySystemParams);
		rateCardGroupData = new RateCardGroupData();
		rateCardGroupData.setName("DefaultRCG");
		rateCardGroupData.setTimeSlotRelationData(createTimeSlotRelationsData());

		request = new RnCRequest();
		request.setAttribute(OfflineRnCKeyConstants.SESSION_CONNECT_TIME.getName(), SESSION_CONNECT_TIME);
		request.setAttribute(OfflineRnCKeyConstants.SESSION_DISCONNECT_TIME.getName(), SESSION_DISCONNECT_TIME);
		request.setAttribute(OfflineRnCKeyConstants.SESSION_TIME.getName(), SESSION_TIME);
		
		response = RnCResponse.of(request);
		response.setAttribute(OfflineRnCKeyConstants.CALLING_NAME, CALLING_NAME);
		response.setAttribute(OfflineRnCKeyConstants.CALLED_NAME, CALLED_NAME);
	}

	private List<TimeSlotRelationData> createTimeSlotRelationsData() {

		List<TimeSlotRelationData> timeSlotRelationDatas = new ArrayList<>();

		TimeSlotRelationData specialTimeSlotRelationData = new TimeSlotRelationData();
		TimeSlotRelationData weekEndTimeSlotRelationData = new TimeSlotRelationData();
		TimeSlotRelationData peakDaysSlotRelationData = new TimeSlotRelationData();
		TimeSlotRelationData offPeakDaysTimeSlotRelationData = new TimeSlotRelationData();

		specialTimeSlotRelationData.setType(RateCardGroupTimeSlotConstant.SPECIAL_DAY_RATE.getValue());
		specialTimeSlotRelationData.setDayOfWeek("1");
		specialTimeSlotRelationData.setTimePeriod("10");

		weekEndTimeSlotRelationData.setType(RateCardGroupTimeSlotConstant.WEEKEND_DAY_RATE.getValue());
		weekEndTimeSlotRelationData.setDayOfWeek("1");
		weekEndTimeSlotRelationData.setTimePeriod("10");

		peakDaysSlotRelationData.setType(RateCardGroupTimeSlotConstant.OFF_PEAK_DAY_RATE.getValue());
		peakDaysSlotRelationData.setDayOfWeek("1");
		peakDaysSlotRelationData.setTimePeriod("10");

		offPeakDaysTimeSlotRelationData.setType(RateCardGroupTimeSlotConstant.PEAK_DAY_RATE.getValue());
		offPeakDaysTimeSlotRelationData.setDayOfWeek("1");
		offPeakDaysTimeSlotRelationData.setTimePeriod("10");

		timeSlotRelationDatas.add(specialTimeSlotRelationData);
		timeSlotRelationDatas.add(weekEndTimeSlotRelationData);
		timeSlotRelationDatas.add(peakDaysSlotRelationData);
		timeSlotRelationDatas.add(offPeakDaysTimeSlotRelationData);

		return timeSlotRelationDatas;
	}

	public class Rate1 {

		@Test
		public void appliesPeakRateCardIfNoOtherRateCardIsConfigured() throws InitializationFailedException, OfflineRnCException {
			rateCardGroupData.setAdvanceCondition(ALWAYS_TRUE_CONDITION);
			rateCardGroupData.setPeakRateRateCard(peakRateCard());

			rateCardGroup = rateCardGroupFactory.create(rateCardGroupData, CURRENCY, ACCOUNT_EFFECT);

			boolean applied = rateCardGroup.apply(request, response);

			assertThat(applied, is(true));
			assertThat(response.getAttribute(OfflineRnCKeyConstants.RATE_CARD_GROUP),
					is(equalTo(rateCardGroupData.getName())));
			assertThat(response.getAttribute(OfflineRnCKeyConstants.RATE_CARD_ID1), is(equalTo(PEAK_RATE_CARD)));
			assertThat(response.getAttribute(OfflineRnCKeyConstants.RATE_TYPE), is(equalTo(OfflineRnCKeyValueConstants.RATE_TYPE_PEAK_DAY.val)));
		}
		
		@Test
		public void appliesOnlySpecialRateCardIfSpecialCalendarIsNotConfigured()
				throws OfflineRnCException, InitializationFailedException {
			rateCardGroupData.setAdvanceCondition(ALWAYS_TRUE_CONDITION);
			rateCardGroupData.setSpecialDayRateCard(specialDaysRateCard());

			rateCardGroup = rateCardGroupFactory.create(rateCardGroupData, CURRENCY, ACCOUNT_EFFECT);

			boolean applied = rateCardGroup.apply(request, response);

			assertThat(applied, is(true));
			assertThat(response.getAttribute(OfflineRnCKeyConstants.RATE_CARD_GROUP),
					is(equalTo(rateCardGroupData.getName())));
			assertThat(response.getAttribute(OfflineRnCKeyConstants.RATE_CARD_ID1), is(equalTo(SPECIAL_RATE_CARD)));
			assertThat(response.getAttribute(OfflineRnCKeyConstants.RATE_TYPE), is(equalTo(OfflineRnCKeyValueConstants.RATE_TYPE_SPECIAL_DAY.val)));

		}

		@Test
		public void isNotAppliedIfAnyRateCardIsNotConfigured()
				throws InitializationFailedException, OfflineRnCException {
			rateCardGroupData.setAdvanceCondition(ALWAYS_TRUE_CONDITION);

			rateCardGroup = rateCardGroupFactory.create(rateCardGroupData, CURRENCY, ACCOUNT_EFFECT);

			boolean applied = rateCardGroup.apply(request, response);

			assertThat(applied, is(false));

		}

		@Test
		public void isNotAppliedIfAdvancedConditionIsNotSatisfied()
				throws InitializationFailedException, OfflineRnCException {
			rateCardGroupData.setAdvanceCondition(ALWAYS_FALSE_CONDITION);
			rateCardGroupData.setSpecialDayRateCard(specialDaysRateCard());

			rateCardGroup = rateCardGroupFactory.create(rateCardGroupData, CURRENCY, ACCOUNT_EFFECT);

			boolean applied = rateCardGroup.apply(request, response);

			assertThat(applied, is(false));
		}

		@Test
		public void appliesOnlySpecialRateCardIfSessionStartTimeFallsInSpecialCalendar()
				throws OfflineRnCException, InitializationFailedException {
			rateCardGroupData.setAdvanceCondition(ALWAYS_TRUE_CONDITION);
			rateCardGroupData.setSpecialDayRateCard(specialDaysRateCard());
			rateCardGroupData.setSpecialDayCalender(createCalendar(fromDate, toDate));

			rateCardGroup = rateCardGroupFactory.create(rateCardGroupData, CURRENCY, ACCOUNT_EFFECT);

			boolean applied = rateCardGroup.apply(request, response);

			assertThat(applied, is(true));
			assertThat(response.getAttribute(OfflineRnCKeyConstants.RATE_CARD_GROUP),
					is(equalTo(rateCardGroupData.getName())));
			assertThat(response.getAttribute(OfflineRnCKeyConstants.RATE_CARD_ID1), is(equalTo(SPECIAL_RATE_CARD)));
			assertThat(response.getAttribute(OfflineRnCKeyConstants.RATE_TYPE), is(equalTo(OfflineRnCKeyValueConstants.RATE_TYPE_SPECIAL_DAY.val)));

		}

		@Test
		public void doesNotApplySpecialRateCardIfSessionStartTimeDoesNotFallWithinSpecialCalendar()
				throws OfflineRnCException, ParseException, InitializationFailedException {
			rateCardGroupData.setAdvanceCondition(ALWAYS_TRUE_CONDITION);
			rateCardGroupData.setSpecialDayRateCard(specialDaysRateCard());
			rateCardGroupData.setSpecialDayCalender(createCalendar(fromDate, toDate));

			rateCardGroup = rateCardGroupFactory.create(rateCardGroupData, CURRENCY, ACCOUNT_EFFECT);

			request.setAttribute(OfflineRnCKeyConstants.SESSION_CONNECT_TIME.getName(),
					dayBefore(SESSION_CONNECT_TIME, simpleDateFormatThreadLocal.get()));
			boolean applied = rateCardGroup.apply(request, response);

			assertThat(applied, is(false));
		}

		@Test
		public void failsWhenNoRateCardIsApplied() throws OfflineRnCException, InitializationFailedException {
			rateCardGroupData.setAdvanceCondition(ALWAYS_TRUE_CONDITION);
			rateCardGroupData.setSpecialDayCalender(createCalendar(fromDate, toDate));
			rateCardGroupData.setSpecialDayRateCard(specialDaysRateCard());
			rateCardGroupData.setWeekendRateRateCard(weekendRateCard());
			rateCardGroupData.setPeakRateRateCard(peakRateCard());
			rateCardGroupData.setOffpeakRateRateCard(offPeakRateCard());

			rateCardGroup = rateCardGroupFactory.create(rateCardGroupData, CURRENCY, ACCOUNT_EFFECT);
			request.setAttribute(OfflineRnCKeyConstants.SESSION_CONNECT_TIME.getName(), "14-01-2018 10:00:00");

			boolean applied = rateCardGroup.apply(request, response);

			assertThat(applied, is(false));
		}

		@Test
		public void appliesWeekEndRateCardWhenSpecialRateCardIsNotSatisfiedAndWeekendTimeSlotIsSatisfied()
				throws OfflineRnCException, InitializationFailedException {

			updateTimeSlot(Arrays.asList(RateCardGroupTimeSlotConstant.SPECIAL_DAY_RATE.getValue()));

			rateCardGroupData.setSpecialDayCalender(createCalendar(fromDate, toDate));

			rateCardGroupData.setAdvanceCondition(ALWAYS_TRUE_CONDITION);
			rateCardGroupData.setSpecialDayRateCard(specialDaysRateCard());
			rateCardGroupData.setWeekendRateRateCard(weekendRateCard());
			rateCardGroupData.setOffpeakRateRateCard(offPeakRateCard());
			rateCardGroupData.setPeakRateRateCard(peakRateCard());
			rateCardGroup = rateCardGroupFactory.create(rateCardGroupData, CURRENCY, ACCOUNT_EFFECT);

			boolean applied = rateCardGroup.apply(request, response);

			assertThat(applied, is(true));
			assertThat(response.getAttribute(OfflineRnCKeyConstants.RATE_CARD_GROUP),
					is(equalTo(rateCardGroupData.getName())));
			assertThat(response.getAttribute(OfflineRnCKeyConstants.RATE_CARD_ID1), is(equalTo(WEEKEND_RATE_CARD)));
			assertThat(response.getAttribute(OfflineRnCKeyConstants.RATE_TYPE), is(equalTo(OfflineRnCKeyValueConstants.RATE_TYPE_WEEKEND_DAY.val)));
		}

		@Test
		public void appliesWeekEndRateCardWhenSpecialRateCardTimeSlotsAreNotConfigured()
				throws OfflineRnCException, InitializationFailedException {

			List<TimeSlotRelationData> filtered = excludeTimeSlots(
					Arrays.asList(RateCardGroupTimeSlotConstant.SPECIAL_DAY_RATE.getValue()));

			rateCardGroupData.setTimeSlotRelationData(filtered);
			rateCardGroupData.setSpecialDayCalender(createCalendar(fromDate, toDate));
			rateCardGroupData.setAdvanceCondition(ALWAYS_TRUE_CONDITION);
			rateCardGroupData.setSpecialDayRateCard(specialDaysRateCard());
			rateCardGroupData.setWeekendRateRateCard(weekendRateCard());
			rateCardGroupData.setOffpeakRateRateCard(offPeakRateCard());
			rateCardGroupData.setPeakRateRateCard(peakRateCard());
			rateCardGroup = rateCardGroupFactory.create(rateCardGroupData, CURRENCY, ACCOUNT_EFFECT);

			boolean applied = rateCardGroup.apply(request, response);

			assertThat(applied, is(true));
			assertThat(response.getAttribute(OfflineRnCKeyConstants.RATE_CARD_GROUP),
					is(equalTo(rateCardGroupData.getName())));
			assertThat(response.getAttribute(OfflineRnCKeyConstants.RATE_CARD_ID1), is(equalTo(WEEKEND_RATE_CARD)));
			assertThat(response.getAttribute(OfflineRnCKeyConstants.RATE_TYPE), is(equalTo(OfflineRnCKeyValueConstants.RATE_TYPE_WEEKEND_DAY.val)));

		}

		@Test
		public void appliesOffPeakRateCardWhenSpecialRateCardTimeSlotsAndWeekEndRateCardTimeSlotsAreNotConfigured()
				throws OfflineRnCException, InitializationFailedException {

			List<TimeSlotRelationData> timeSlotRatationDatas = excludeTimeSlots(
					Arrays.asList(RateCardGroupTimeSlotConstant.SPECIAL_DAY_RATE.getValue(),
							RateCardGroupTimeSlotConstant.WEEKEND_DAY_RATE.getValue()));

			rateCardGroupData.setTimeSlotRelationData(timeSlotRatationDatas);
			rateCardGroupData.setSpecialDayCalender(createCalendar(fromDate, toDate));
			rateCardGroupData.setAdvanceCondition(ALWAYS_TRUE_CONDITION);
			rateCardGroupData.setSpecialDayRateCard(specialDaysRateCard());
			rateCardGroupData.setWeekendRateRateCard(weekendRateCard());
			rateCardGroupData.setOffpeakRateRateCard(offPeakRateCard());
			rateCardGroupData.setPeakRateRateCard(peakRateCard());
			rateCardGroup = rateCardGroupFactory.create(rateCardGroupData, CURRENCY, ACCOUNT_EFFECT);

			boolean applied = rateCardGroup.apply(request, response);

			assertThat(applied, is(true));
			assertThat(response.getAttribute(OfflineRnCKeyConstants.RATE_CARD_GROUP),
					is(equalTo(rateCardGroupData.getName())));
			assertThat(response.getAttribute(OfflineRnCKeyConstants.RATE_CARD_ID1), is(equalTo(OFF_PEAK_RATE_CARD)));
			assertThat(response.getAttribute(OfflineRnCKeyConstants.RATE_TYPE), is(equalTo(OfflineRnCKeyValueConstants.RATE_TYPE_OFF_PEAK_DAY.val)));
		}

		@Test
		public void appliesPeakRateCardWhenSpecialRateCardTimeSlotsWeekEndRateCardTimeSlotsAndOffPeakTimeSlotsAreNotConfigured()
				throws OfflineRnCException, InitializationFailedException {

			List<TimeSlotRelationData> timeSlotRatationDatas = excludeTimeSlots(
					Arrays.asList(RateCardGroupTimeSlotConstant.SPECIAL_DAY_RATE.getValue(),
							RateCardGroupTimeSlotConstant.WEEKEND_DAY_RATE.getValue(),
							RateCardGroupTimeSlotConstant.OFF_PEAK_DAY_RATE.getValue()));

			rateCardGroupData.setTimeSlotRelationData(timeSlotRatationDatas);
			rateCardGroupData.setSpecialDayCalender(createCalendar(fromDate, toDate));
			rateCardGroupData.setAdvanceCondition(ALWAYS_TRUE_CONDITION);
			rateCardGroupData.setSpecialDayRateCard(specialDaysRateCard());
			rateCardGroupData.setWeekendRateRateCard(weekendRateCard());
			rateCardGroupData.setOffpeakRateRateCard(offPeakRateCard());
			rateCardGroupData.setPeakRateRateCard(peakRateCard());
			rateCardGroup = rateCardGroupFactory.create(rateCardGroupData, CURRENCY, ACCOUNT_EFFECT);

			boolean applied = rateCardGroup.apply(request, response);

			assertThat(applied, is(true));
			assertThat(response.getAttribute(OfflineRnCKeyConstants.RATE_CARD_GROUP),
					is(equalTo(rateCardGroupData.getName())));
			assertThat(response.getAttribute(OfflineRnCKeyConstants.RATE_CARD_ID1), is(equalTo(PEAK_RATE_CARD)));
			assertThat(response.getAttribute(OfflineRnCKeyConstants.RATE_TYPE), is(equalTo(OfflineRnCKeyValueConstants.RATE_TYPE_PEAK_DAY.val)));

		}

		@Test
		public void appliesPeakRateCardWhenNoneOfTimeSlotsConfigured()
				throws OfflineRnCException, InitializationFailedException {

			List<TimeSlotRelationData> timeSlotRatationDatas = new ArrayList<>();

			rateCardGroupData.setTimeSlotRelationData(timeSlotRatationDatas);
			rateCardGroupData.setSpecialDayCalender(createCalendar(fromDate, toDate));
			rateCardGroupData.setAdvanceCondition(ALWAYS_TRUE_CONDITION);
			rateCardGroupData.setSpecialDayRateCard(specialDaysRateCard());
			rateCardGroupData.setWeekendRateRateCard(weekendRateCard());
			rateCardGroupData.setOffpeakRateRateCard(offPeakRateCard());
			rateCardGroupData.setPeakRateRateCard(peakRateCard());
			rateCardGroup = rateCardGroupFactory.create(rateCardGroupData, CURRENCY, ACCOUNT_EFFECT);

			boolean applied = rateCardGroup.apply(request, response);

			assertThat(applied, is(true));
			assertThat(response.getAttribute(OfflineRnCKeyConstants.RATE_CARD_GROUP),
					is(equalTo(rateCardGroupData.getName())));
			assertThat(response.getAttribute(OfflineRnCKeyConstants.RATE_CARD_ID1), is(equalTo(PEAK_RATE_CARD)));
			assertThat(response.getAttribute(OfflineRnCKeyConstants.RATE_TYPE), is(equalTo(OfflineRnCKeyValueConstants.RATE_TYPE_PEAK_DAY.val)));

		}

		@Test
		public void appliesOffPeakRateCardWhenSpecialRateCardAndWeekEndRateCardAreNotSatisfied()
				throws OfflineRnCException, InitializationFailedException {

			updateTimeSlot(Arrays.asList(RateCardGroupTimeSlotConstant.SPECIAL_DAY_RATE.getValue(),
					RateCardGroupTimeSlotConstant.WEEKEND_DAY_RATE.getValue()));

			rateCardGroupData.setSpecialDayCalender(createCalendar(fromDate, toDate));

			rateCardGroupData.setAdvanceCondition(ALWAYS_TRUE_CONDITION);
			rateCardGroupData.setSpecialDayRateCard(specialDaysRateCard());
			rateCardGroupData.setWeekendRateRateCard(weekendRateCard());
			rateCardGroupData.setPeakRateRateCard(peakRateCard());
			rateCardGroupData.setOffpeakRateRateCard(offPeakRateCard());
			rateCardGroup = rateCardGroupFactory.create(rateCardGroupData, CURRENCY, ACCOUNT_EFFECT);

			boolean applied = rateCardGroup.apply(request, response);

			assertThat(applied, is(true));
			assertThat(response.getAttribute(OfflineRnCKeyConstants.RATE_CARD_GROUP),
					is(equalTo(rateCardGroupData.getName())));
			assertThat(response.getAttribute(OfflineRnCKeyConstants.RATE_CARD_ID1), is(equalTo(OFF_PEAK_RATE_CARD)));
			assertThat(response.getAttribute(OfflineRnCKeyConstants.RATE_TYPE), is(equalTo(OfflineRnCKeyValueConstants.RATE_TYPE_OFF_PEAK_DAY.val)));

		}

		@Test
		public void appliesPeakRateCardWhenSpecialRateCardWeekEndRateCardAndOffPeakRateCardAreNotSatisfied()
				throws OfflineRnCException, InitializationFailedException {

			updateTimeSlot(Arrays.asList(RateCardGroupTimeSlotConstant.SPECIAL_DAY_RATE.getValue(),
					RateCardGroupTimeSlotConstant.WEEKEND_DAY_RATE.getValue(),
					RateCardGroupTimeSlotConstant.OFF_PEAK_DAY_RATE.getValue()));

			rateCardGroupData.setSpecialDayCalender(createCalendar(fromDate, toDate));

			rateCardGroupData.setAdvanceCondition(ALWAYS_TRUE_CONDITION);
			rateCardGroupData.setSpecialDayRateCard(specialDaysRateCard());
			rateCardGroupData.setWeekendRateRateCard(weekendRateCard());
			rateCardGroupData.setPeakRateRateCard(peakRateCard());
			rateCardGroup = rateCardGroupFactory.create(rateCardGroupData, CURRENCY, ACCOUNT_EFFECT);

			boolean applied = rateCardGroup.apply(request, response);

			assertThat(applied, is(true));
			assertThat(response.getAttribute(OfflineRnCKeyConstants.RATE_CARD_GROUP),
					is(equalTo(rateCardGroupData.getName())));
			assertThat(response.getAttribute(OfflineRnCKeyConstants.RATE_CARD_ID1), is(equalTo(PEAK_RATE_CARD)));
			assertThat(response.getAttribute(OfflineRnCKeyConstants.RATE_TYPE), is(equalTo(OfflineRnCKeyValueConstants.RATE_TYPE_PEAK_DAY.val)));

		}

		@Test
		public void failWhenNoneOfTimeSlotsAreSatisfied() throws OfflineRnCException, InitializationFailedException {
			updateTimeSlot(Stream.of(RateCardGroupTimeSlotConstant.values()).map(each -> each.getValue())
					.collect(Collectors.toList()));

			rateCardGroupData.setSpecialDayCalender(createCalendar(fromDate, toDate));
			rateCardGroupData.setAdvanceCondition(ALWAYS_TRUE_CONDITION);
			rateCardGroupData.setSpecialDayRateCard(specialDaysRateCard());
			rateCardGroupData.setWeekendRateRateCard(weekendRateCard());
			rateCardGroupData.setOffpeakRateRateCard(offPeakRateCard());
			rateCardGroupData.setPeakRateRateCard(peakRateCard());
			rateCardGroup = rateCardGroupFactory.create(rateCardGroupData, CURRENCY, ACCOUNT_EFFECT);

			boolean applied = rateCardGroup.apply(request, response);

			assertThat(applied, is(false));
		}
		
		@Test
		public void failWhenNoneOfTimePeriodsAreSatisfied() throws OfflineRnCException, InitializationFailedException {
			
			updateTimePeriods(Arrays.asList(RateCardGroupTimeSlotConstant.SPECIAL_DAY_RATE.getValue(),
					RateCardGroupTimeSlotConstant.WEEKEND_DAY_RATE.getValue(),
					RateCardGroupTimeSlotConstant.OFF_PEAK_DAY_RATE.getValue(),RateCardGroupTimeSlotConstant.PEAK_DAY_RATE.getValue()));
	
			rateCardGroupData.setSpecialDayCalender(createCalendar(fromDate, toDate));
			rateCardGroupData.setAdvanceCondition(ALWAYS_TRUE_CONDITION);
			rateCardGroupData.setSpecialDayRateCard(specialDaysRateCard());
			rateCardGroupData.setWeekendRateRateCard(weekendRateCard());
			rateCardGroupData.setOffpeakRateRateCard(offPeakRateCard());
			rateCardGroupData.setPeakRateRateCard(peakRateCard());
			rateCardGroup = rateCardGroupFactory.create(rateCardGroupData, CURRENCY, ACCOUNT_EFFECT);

			boolean applied = rateCardGroup.apply(request, response);

			assertThat(applied, is(false));
		}
		
	}
	
	

	

	public class IfRate1IsNotApplied {

		@Test
		public void doesNotApplyRate2AndRate3Card() throws InitializationFailedException, OfflineRnCException {

			rateCardGroupData.setAdvanceCondition(ALWAYS_TRUE_CONDITION);
			rateCardGroupData.setSpecialDayRateCard(specialDaysRateCard());
			rateCardGroupData.setOffpeakRateRateCard(offPeakRateCard());
			rateCardGroupData.setPeakRateRateCard(peakRateCard());
			rateCardGroupData.setWeekendRateRateCard(weekendRateCard());
			rateCardGroupData.setSpecialDayCalender(createCalendar(fromDate, toDate));
			rateCardGroupData.setRate2RateCard(rate2RateCard());
			rateCardGroupData.setRate3RateCard(rate3RateCard());

			rateCardGroup = rateCardGroupFactory.create(rateCardGroupData, CURRENCY, ACCOUNT_EFFECT);

			request.setAttribute(OfflineRnCKeyConstants.SESSION_CONNECT_TIME.getName(), "16-01-2018 10:00:00");
			boolean applied = rateCardGroup.apply(request, response);

			assertThat(applied, is(false));
			assertThat(response.getAttribute(OfflineRnCKeyConstants.RATE_CARD_GROUP), is(nullValue()));
			assertThat(response.getAttribute(OfflineRnCKeyConstants.RATE_CARD_ID1), is(nullValue()));
			assertThat(response.getAttribute(OfflineRnCKeyConstants.RATE_CARD_ID2), is(nullValue()));
			assertThat(response.getAttribute(OfflineRnCKeyConstants.RATE_CARD_ID3), is(nullValue()));
		}
	}

	public class IfRate1CardIsApplied {

		@Test
		public void appliesRate2AndRate3CardIfApplicable() throws InitializationFailedException, OfflineRnCException {
			rateCardGroupData.setAdvanceCondition(ALWAYS_TRUE_CONDITION);
			rateCardGroupData.setSpecialDayRateCard(specialDaysRateCard());
			rateCardGroupData.setOffpeakRateRateCard(offPeakRateCard());
			rateCardGroupData.setPeakRateRateCard(peakRateCard());
			rateCardGroupData.setWeekendRateRateCard(weekendRateCard());
			rateCardGroupData.setSpecialDayCalender(createCalendar(fromDate, toDate));
			rateCardGroupData.setRate2RateCard(rate2RateCard());
			rateCardGroupData.setRate3RateCard(rate3RateCard());

			rateCardGroup = rateCardGroupFactory.create(rateCardGroupData, CURRENCY, ACCOUNT_EFFECT);

			boolean applied = rateCardGroup.apply(request, response);

			assertThat(applied, is(true));
			assertThat(response.getAttribute(OfflineRnCKeyConstants.RATE_CARD_GROUP),
					is(equalTo(rateCardGroupData.getName())));
			assertThat(response.getAttribute(OfflineRnCKeyConstants.RATE_CARD_ID1), is(equalTo(SPECIAL_RATE_CARD)));
			assertThat(response.getAttribute(OfflineRnCKeyConstants.RATE_CARD_ID2), is(equalTo(RATE2_RATE_CARD)));
			assertThat(response.getAttribute(OfflineRnCKeyConstants.RATE_CARD_ID3), is(equalTo(RATE3_RATE_CARD)));
		}

		@Test
		public void doesNotApplyRate2AndRate3CardIfNotApplied()
				throws InitializationFailedException, OfflineRnCException {
			rateCardGroupData.setAdvanceCondition(ALWAYS_TRUE_CONDITION);
			rateCardGroupData.setSpecialDayRateCard(specialDaysRateCard());
			rateCardGroupData.setOffpeakRateRateCard(offPeakRateCard());
			rateCardGroupData.setPeakRateRateCard(peakRateCard());
			rateCardGroupData.setWeekendRateRateCard(weekendRateCard());
			rateCardGroupData.setSpecialDayCalender(createCalendar(fromDate, toDate));
			rateCardGroupData.setRate2RateCard(nonApplicableRate2RateCard());
			rateCardGroupData.setRate3RateCard(notApplicableRate3RateCard());

			rateCardGroup = rateCardGroupFactory.create(rateCardGroupData, CURRENCY, ACCOUNT_EFFECT);

			boolean applied = rateCardGroup.apply(request, response);

			assertThat(applied, is(true));
			assertThat(response.getAttribute(OfflineRnCKeyConstants.RATE_CARD_GROUP),
					is(equalTo(rateCardGroupData.getName())));
			assertThat(response.getAttribute(OfflineRnCKeyConstants.RATE_CARD_ID1), is(SPECIAL_RATE_CARD));
			assertThat(response.getAttribute(OfflineRnCKeyConstants.RATE_CARD_ID2), is(nullValue()));
			assertThat(response.getAttribute(OfflineRnCKeyConstants.RATE_CARD_ID3), is(nullValue()));
		}

		@Test
		public void appliesOnlyRate2CardIfRate3CardIsNotApplicable()
				throws InitializationFailedException, OfflineRnCException {
			rateCardGroupData.setAdvanceCondition(ALWAYS_TRUE_CONDITION);
			rateCardGroupData.setSpecialDayRateCard(specialDaysRateCard());
			rateCardGroupData.setOffpeakRateRateCard(offPeakRateCard());
			rateCardGroupData.setPeakRateRateCard(peakRateCard());
			rateCardGroupData.setWeekendRateRateCard(weekendRateCard());
			rateCardGroupData.setSpecialDayCalender(createCalendar(fromDate, toDate));
			rateCardGroupData.setRate2RateCard(rate2RateCard());
			rateCardGroupData.setRate3RateCard(notApplicableRate3RateCard());

			rateCardGroup = rateCardGroupFactory.create(rateCardGroupData, CURRENCY, ACCOUNT_EFFECT);

			boolean applied = rateCardGroup.apply(request, response);

			assertThat(applied, is(true));
			assertThat(response.getAttribute(OfflineRnCKeyConstants.RATE_CARD_GROUP),
					is(equalTo(rateCardGroupData.getName())));
			assertThat(response.getAttribute(OfflineRnCKeyConstants.RATE_CARD_ID1), is(SPECIAL_RATE_CARD));
			assertThat(response.getAttribute(OfflineRnCKeyConstants.RATE_CARD_ID2), is(RATE2_RATE_CARD));
			assertThat(response.getAttribute(OfflineRnCKeyConstants.RATE_CARD_ID3), is(nullValue()));
		}

		@Test
		public void appliesOnlyRate3CardIfRate2CardIsNotApplicable()
				throws InitializationFailedException, OfflineRnCException {
			rateCardGroupData.setAdvanceCondition(ALWAYS_TRUE_CONDITION);
			rateCardGroupData.setOffpeakRateRateCard(offPeakRateCard());
			rateCardGroupData.setPeakRateRateCard(peakRateCard());
			rateCardGroupData.setWeekendRateRateCard(weekendRateCard());
			rateCardGroupData.setSpecialDayCalender(createCalendar(fromDate, toDate));
			rateCardGroupData.setRate2RateCard(nonApplicableRate2RateCard());
			rateCardGroupData.setRate3RateCard(rate3RateCard());

			rateCardGroup = rateCardGroupFactory.create(rateCardGroupData, CURRENCY, ACCOUNT_EFFECT);
			boolean applied = rateCardGroup.apply(request, response);

			assertThat(applied, is(true));
			assertThat(response.getAttribute(OfflineRnCKeyConstants.RATE_CARD_GROUP),
					is(equalTo(rateCardGroupData.getName())));
			assertThat(response.getAttribute(OfflineRnCKeyConstants.RATE_CARD_ID1), is(equalTo(WEEKEND_RATE_CARD)));
			assertThat(response.getAttribute(OfflineRnCKeyConstants.RATE_CARD_ID2), is(nullValue()));
			assertThat(response.getAttribute(OfflineRnCKeyConstants.RATE_CARD_ID3), is(RATE3_RATE_CARD));
		}

	}

	private void updateTimeSlot(List<String> timeslotTypes) {
		rateCardGroupData.getTimeSlotRelationData().stream()
				.filter(timeSlotRelationData -> timeslotTypes.contains(timeSlotRelationData.getType()))
				.forEach(relationData -> relationData.setDayOfWeek("4"));
	}

	private CalenderData createCalendar(Timestamp fromDate, Timestamp toDate) {
		CalenderData calenderData = new CalenderData();
		List<CalenderDetails> listOfCalenderDetails = new ArrayList<>();
		CalenderDetails calenderDetails = new CalenderDetails();
		calenderDetails.setCalenderName("DefaultCalendar");
		calenderDetails.setFromDate(fromDate);
		calenderDetails.setToDate(toDate);
		calenderData.setCalenderList("DefaultCalendarList");
		listOfCalenderDetails.add(calenderDetails);
		calenderData.setCalenderDetails(listOfCalenderDetails);
		return calenderData;
	}

	private RateCardData specialDaysRateCard() {
		RateCardData specialDaysRateCard = new RateCardData();
		specialDaysRateCard.setName(SPECIAL_RATE_CARD);
		specialDaysRateCard.setRateUom(RATE_UOM);
		specialDaysRateCard.setPulseUom(RATE_UOM);
		specialDaysRateCard.setLabelKey1(OfflineRnCKeyConstants.CALLING_NAME.getName());
		specialDaysRateCard.setLabelKey2(OfflineRnCKeyConstants.CALLED_NAME.getName());

		rateCardVersionRelations = new ArrayList<>();
		rateCardVersionRelation = new RateCardVersionRelation();
		rateCardVersionRelation.setVersionName("Version 1");

		rateCardVersionDetails = new ArrayList<>();
		rateCardVersionDetail = new RateCardVersionDetail();
		rateCardVersionDetail.setLabel1(CALLING_NAME);
		rateCardVersionDetail.setLabel2(CALLED_NAME);
		rateCardVersionDetail.setFromDate(fromDate);
		rateCardVersionDetail.setRate1(SPECIAL_DAYS_RATE);
		rateCardVersionDetail.setPulse1(PULSE);
		rateCardVersionDetail.setSlab1(SLAB);
		rateCardVersionDetail.setTierRateType(TIER_RATE_TYPE);

		rateCardVersionDetails.add(rateCardVersionDetail);
		rateCardVersionRelation.setRateCardVersionDetail(rateCardVersionDetails);
		rateCardVersionRelations.add(rateCardVersionRelation);
		specialDaysRateCard.setRateCardVersionRelation(rateCardVersionRelations);

		return specialDaysRateCard;
	}

	private RateCardData weekendRateCard() {
		RateCardData weekEndRateCard = new RateCardData();
		weekEndRateCard.setName(WEEKEND_RATE_CARD);
		weekEndRateCard.setRateUom(RATE_UOM);
		weekEndRateCard.setPulseUom(RATE_UOM);

		weekEndRateCard.setLabelKey1(OfflineRnCKeyConstants.CALLING_NAME.getName());
		weekEndRateCard.setLabelKey2(OfflineRnCKeyConstants.CALLED_NAME.getName());

		rateCardVersionRelations = new ArrayList<>();
		rateCardVersionRelation = new RateCardVersionRelation();
		rateCardVersionRelation.setVersionName("Version 1");
		rateCardVersionDetails = new ArrayList<>();
		rateCardVersionDetail = new RateCardVersionDetail();

		rateCardVersionDetail.setLabel1(CALLING_NAME);
		rateCardVersionDetail.setLabel2(CALLED_NAME);
		rateCardVersionDetail.setFromDate(fromDate);
		rateCardVersionDetail.setRate1(WEEKEND_RATE);
		rateCardVersionDetail.setPulse1(PULSE);
		rateCardVersionDetail.setSlab1(SLAB);
		rateCardVersionDetail.setTierRateType(TIER_RATE_TYPE);

		rateCardVersionDetails.add(rateCardVersionDetail);
		rateCardVersionRelation.setRateCardVersionDetail(rateCardVersionDetails);
		rateCardVersionRelations.add(rateCardVersionRelation);
		weekEndRateCard.setRateCardVersionRelation(rateCardVersionRelations);

		return weekEndRateCard;
	}

	private RateCardData offPeakRateCard() {
		RateCardData offPeakRateCard = new RateCardData();
		offPeakRateCard.setName(OFF_PEAK_RATE_CARD);
		offPeakRateCard.setRateUom(RATE_UOM);
		offPeakRateCard.setPulseUom(RATE_UOM);

		offPeakRateCard.setLabelKey1(OfflineRnCKeyConstants.CALLING_NAME.getName());
		offPeakRateCard.setLabelKey2(OfflineRnCKeyConstants.CALLED_NAME.getName());

		rateCardVersionRelations = new ArrayList<>();
		rateCardVersionRelation = new RateCardVersionRelation();
		rateCardVersionRelation.setVersionName("Version 1");
		rateCardVersionDetails = new ArrayList<>();
		rateCardVersionDetail = new RateCardVersionDetail();

		rateCardVersionDetail.setLabel1(CALLING_NAME);
		rateCardVersionDetail.setLabel2(CALLED_NAME);
		rateCardVersionDetail.setFromDate(fromDate);
		rateCardVersionDetail.setRate1(OFF_PEAK_RATE);
		rateCardVersionDetail.setPulse1(PULSE);
		rateCardVersionDetail.setSlab1(SLAB);
		rateCardVersionDetail.setTierRateType(TIER_RATE_TYPE);

		rateCardVersionDetails.add(rateCardVersionDetail);
		rateCardVersionRelation.setRateCardVersionDetail(rateCardVersionDetails);
		rateCardVersionRelations.add(rateCardVersionRelation);
		offPeakRateCard.setRateCardVersionRelation(rateCardVersionRelations);

		return offPeakRateCard;
	}

	private RateCardData peakRateCard() {
		RateCardData peakRateCard = new RateCardData();
		peakRateCard.setName(PEAK_RATE_CARD);
		peakRateCard.setRateUom(RATE_UOM);
		peakRateCard.setPulseUom(RATE_UOM);
		peakRateCard.setLabelKey1(OfflineRnCKeyConstants.CALLING_NAME.getName());
		peakRateCard.setLabelKey2(OfflineRnCKeyConstants.CALLED_NAME.getName());

		rateCardVersionRelations = new ArrayList<>();
		rateCardVersionRelation = new RateCardVersionRelation();
		rateCardVersionRelation.setVersionName("Version 1");
		rateCardVersionDetails = new ArrayList<>();
		rateCardVersionDetail = new RateCardVersionDetail();
		rateCardVersionDetail.setLabel1(CALLING_NAME);
		rateCardVersionDetail.setLabel2(CALLED_NAME);
		rateCardVersionDetail.setFromDate(fromDate);
		rateCardVersionDetail.setRate1(PEAK_RATE);
		rateCardVersionDetail.setPulse1(PULSE);
		rateCardVersionDetail.setSlab1(SLAB);
		rateCardVersionDetail.setTierRateType(TIER_RATE_TYPE);

		rateCardVersionDetails.add(rateCardVersionDetail);
		rateCardVersionRelation.setRateCardVersionDetail(rateCardVersionDetails);
		rateCardVersionRelations.add(rateCardVersionRelation);
		peakRateCard.setRateCardVersionRelation(rateCardVersionRelations);

		return peakRateCard;
	}

	private RateCardData rate2RateCard() {
		RateCardData rate2RateCard = new RateCardData();
		rate2RateCard.setName(RATE2_RATE_CARD);
		rate2RateCard.setRateUom(RATE_UOM);
		rate2RateCard.setPulseUom(RATE_UOM);
		rate2RateCard.setLabelKey1(OfflineRnCKeyConstants.CALLING_NAME.getName());
		rate2RateCard.setLabelKey2(OfflineRnCKeyConstants.CALLED_NAME.getName());

		rateCardVersionRelations = new ArrayList<>();
		rateCardVersionRelation = new RateCardVersionRelation();
		rateCardVersionRelation.setVersionName("Version 1");
		rateCardVersionDetails = new ArrayList<>();
		rateCardVersionDetail = new RateCardVersionDetail();
		rateCardVersionDetail.setLabel1(CALLING_NAME);
		rateCardVersionDetail.setLabel2(CALLED_NAME);
		rateCardVersionDetail.setFromDate(fromDate);
		rateCardVersionDetail.setRate1(RATE2_RATE);
		rateCardVersionDetail.setPulse1(PULSE);
		rateCardVersionDetail.setSlab1(SLAB);
		rateCardVersionDetail.setTierRateType(TIER_RATE_TYPE);

		rateCardVersionDetails.add(rateCardVersionDetail);
		rateCardVersionRelation.setRateCardVersionDetail(rateCardVersionDetails);
		rateCardVersionRelations.add(rateCardVersionRelation);
		rate2RateCard.setRateCardVersionRelation(rateCardVersionRelations);

		return rate2RateCard;
	}

	private RateCardData rate3RateCard() {
		RateCardData rate3RateCard = new RateCardData();
		rate3RateCard.setName(RATE3_RATE_CARD);
		rate3RateCard.setRateUom(RATE_UOM);
		rate3RateCard.setPulseUom(RATE_UOM);
		rate3RateCard.setLabelKey1(OfflineRnCKeyConstants.CALLING_NAME.getName());
		rate3RateCard.setLabelKey2(OfflineRnCKeyConstants.CALLED_NAME.getName());

		rateCardVersionRelations = new ArrayList<>();
		rateCardVersionRelation = new RateCardVersionRelation();
		rateCardVersionRelation.setVersionName("Version 1");
		rateCardVersionDetails = new ArrayList<>();
		rateCardVersionDetail = new RateCardVersionDetail();
		rateCardVersionDetail.setLabel1(CALLING_NAME);
		rateCardVersionDetail.setLabel2(CALLED_NAME);
		rateCardVersionDetail.setFromDate(fromDate);
		rateCardVersionDetail.setRate1(RATE3_RATE);
		rateCardVersionDetail.setPulse1(PULSE);
		rateCardVersionDetail.setSlab1(SLAB);
		rateCardVersionDetail.setTierRateType(TIER_RATE_TYPE);

		rateCardVersionDetails.add(rateCardVersionDetail);
		rateCardVersionRelation.setRateCardVersionDetail(rateCardVersionDetails);
		rateCardVersionRelations.add(rateCardVersionRelation);
		rate3RateCard.setRateCardVersionRelation(rateCardVersionRelations);

		return rate3RateCard;
	}

	private RateCardData nonApplicableRate2RateCard() {
		RateCardData rate2RateCard = new RateCardData();
		rate2RateCard.setName(RATE2_RATE_CARD);
		rate2RateCard.setRateUom(RATE_UOM);
		rate2RateCard.setPulseUom(RATE_UOM);
		rate2RateCard.setLabelKey1(OfflineRnCKeyConstants.CALLING_NAME.getName());
		rate2RateCard.setLabelKey2(OfflineRnCKeyConstants.CALLED_NAME.getName());

		rateCardVersionRelations = new ArrayList<>();
		rateCardVersionRelation = new RateCardVersionRelation();
		rateCardVersionRelation.setVersionName("Version 1");
		rateCardVersionDetails = new ArrayList<>();
		rateCardVersionDetail = new RateCardVersionDetail();
		rateCardVersionDetail.setLabel1(CALLING_NAME);
		rateCardVersionDetail.setLabel2(CALLED_NAME);
		rateCardVersionDetail.setFromDate(toDate);
		rateCardVersionDetail.setRate1(RATE2_RATE);
		rateCardVersionDetail.setPulse1(PULSE);
		rateCardVersionDetail.setSlab1(SLAB);
		rateCardVersionDetail.setTierRateType(TIER_RATE_TYPE);

		rateCardVersionDetails.add(rateCardVersionDetail);
		rateCardVersionRelation.setRateCardVersionDetail(rateCardVersionDetails);
		rateCardVersionRelations.add(rateCardVersionRelation);
		rate2RateCard.setRateCardVersionRelation(rateCardVersionRelations);

		return rate2RateCard;
	}

	private RateCardData notApplicableRate3RateCard() {
		RateCardData rate3RateCard = new RateCardData();
		rate3RateCard.setName(RATE3_RATE_CARD);
		rate3RateCard.setRateUom(RATE_UOM);
		rate3RateCard.setPulseUom(RATE_UOM);
		rate3RateCard.setLabelKey1(OfflineRnCKeyConstants.CALLING_NAME.getName());
		rate3RateCard.setLabelKey2(OfflineRnCKeyConstants.CALLED_NAME.getName());

		rateCardVersionRelations = new ArrayList<>();
		rateCardVersionRelation = new RateCardVersionRelation();
		rateCardVersionRelation.setVersionName("Version 1");
		rateCardVersionDetails = new ArrayList<>();
		rateCardVersionDetail = new RateCardVersionDetail();
		rateCardVersionDetail.setLabel1(CALLING_NAME);
		rateCardVersionDetail.setLabel2(CALLED_NAME);
		rateCardVersionDetail.setFromDate(toDate);
		rateCardVersionDetail.setRate1(RATE3_RATE);
		rateCardVersionDetail.setPulse1(PULSE);
		rateCardVersionDetail.setSlab1(SLAB);
		rateCardVersionDetail.setTierRateType(TIER_RATE_TYPE);

		rateCardVersionDetails.add(rateCardVersionDetail);
		rateCardVersionRelation.setRateCardVersionDetail(rateCardVersionDetails);
		rateCardVersionRelations.add(rateCardVersionRelation);
		rate3RateCard.setRateCardVersionRelation(rateCardVersionRelations);

		return rate3RateCard;
	}

	private List<TimeSlotRelationData> excludeTimeSlots(List<String> types) {
		List<TimeSlotRelationData> filtered = rateCardGroupData.getTimeSlotRelationData().stream()
				.filter(each -> !types.contains(each.getType())).collect(Collectors.toList());
		return filtered;
	}
	
	private void updateTimePeriods(List<String> timeslotTypes) {
		rateCardGroupData.getTimeSlotRelationData().stream()
				.filter(timeSlotRelationData -> timeslotTypes.contains(timeSlotRelationData.getType()))
				.forEach(relationData -> relationData.setTimePeriod("9"));
	}
	
	@After
	public void printTrace() {
		LogManager.getLogger().debug("RCG-TEST", request.getTrace());
	}
}
