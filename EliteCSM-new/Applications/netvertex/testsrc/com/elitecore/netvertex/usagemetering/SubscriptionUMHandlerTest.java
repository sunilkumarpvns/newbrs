package com.elitecore.netvertex.usagemetering;

import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.elitecore.netvertex.core.ddf.CacheAwareDDFTable;
import junitparams.JUnitParamsRunner;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.elitecore.commons.base.Function;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.ValidityPeriodUnit;
import com.elitecore.corenetvertex.data.SessionUsage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.SubscriptionPackage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfile;
import com.elitecore.corenetvertex.spr.ServiceUsage;
import com.elitecore.corenetvertex.spr.SubscriberUsage;
import com.elitecore.corenetvertex.spr.SubscriberUsage.SubscriberUsageBuilder;
import com.elitecore.corenetvertex.spr.data.Subscription;
import com.elitecore.netvertex.core.servicepolicy.ExecutionContext;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.impl.PCRFRequestImpl;
import com.elitecore.netvertex.service.pcrf.impl.PCRFResponseImpl;
import com.elitecore.netvertex.usagemetering.factory.UMInfoFactory;
import com.elitecore.netvertex.util.CalenderUtil;

@RunWith(JUnitParamsRunner.class)
public class SubscriptionUMHandlerTest {

	private static final String PACKAGE_ID = "test1";
	private static final String PRODUCT_OFFER_ID = "testProductOffer1";
	private static final String SUBSCRIPTION_ID = "subscriptionid1";
	private static final String QUOTA_PROFILE_ID = "quotaId";
	private static final String SUBSCRIBER_ID = "9797979797";
	private static final String MONITORING_KEY = "monitoringkey";
	private PCRFRequest pcrfRequest;
	private Function<List<SubscriberUsage>, Map<String, Map<String, SubscriberUsage>>> listToMapTransformer = new SubscriberUsageListToMapFunction();
	private PCRFResponse pcrfResponse;
	@Mock
	private SubscriptionPackage subscriptionPackage;
	@Mock
	private Subscription subscription;
	@Mock
	private QuotaProfile quotaProfile;
	
	private Calendar baseCalender = Calendar.getInstance();

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		pcrfRequest = new PCRFRequestImpl();
		pcrfResponse = new PCRFResponseImpl();
		when(subscriptionPackage.getName()).thenReturn(PACKAGE_ID);
		doReturn(CommonConstants.ALL_SERVICE_ID).when(subscriptionPackage).getServiceId(MONITORING_KEY);
		doReturn(quotaProfile).when(subscriptionPackage).getQuotaProfileByMonitoringKey(MONITORING_KEY);
		doReturn(QUOTA_PROFILE_ID).when(quotaProfile).getId();
		doReturn(PACKAGE_ID).when(subscriptionPackage).getId();
		doReturn(SUBSCRIPTION_ID).when(subscription).getId();
		doReturn(ValidityPeriodUnit.DAY).when(subscriptionPackage).getValidityPeriodUnit();
		doReturn(Timestamp.valueOf("2017-05-30 23:59:59")).when(subscription).getEndTime();
		
		baseCalender.setTimeInMillis(Timestamp.valueOf("2017-05-17 11:11:11").getTime());
	}

	@Test
	public void test_addPCCLevelReportedUsage_addUsageToExisting_when_DailyResetTimeExpiredAndSessionStaredInCurrentDate() {
		// setup
		Calendar previousDayResetTime = CalenderUtil.getPreviousDayMidNight(baseCalender);
		Calendar weeklyResetTime = CalenderUtil.getNextWeeklyResetTime(baseCalender);
		Calendar billingResetTime = CalenderUtil.getBillingCycleResetTime(baseCalender);
		Calendar sessionStartTime = CalenderUtil.getPreviousHourTime(baseCalender);
		Calendar currentTime = baseCalender;
		
		ServiceUnit existingUnits = new ServiceUnit(0, 5, 15, 20);
		ServiceUnit reportedUnits = new ServiceUnit(0, 5, 15, 20);
		setSessionStartTimeInPCRFRequest(sessionStartTime);
		
		ServiceUsage currentServiceUsage = createServiceUsage(existingUnits, previousDayResetTime.getTimeInMillis(), weeklyResetTime.getTimeInMillis(), billingResetTime.getTimeInMillis());
		SessionUsage sessionLevelUsage = null;
		UsageMonitoringInfo usuInstace = UMInfoFactory.of(UMLevel.PCC_RULE_LEVEL).usuInstace(MONITORING_KEY, reportedUnits);
		SubscriberUsage expectedSubscriberUsage = createSubscriberUsage(existingUnits, reportedUnits, 0, weeklyResetTime.getTimeInMillis(), billingResetTime.getTimeInMillis());
		expectedSubscriberUsage.setDailyTotal(reportedUnits.getTotalOctets());
		expectedSubscriberUsage.setDailyUpload(reportedUnits.getInputOctets());
		expectedSubscriberUsage.setDailyDownload(reportedUnits.getOutputOctets());
		expectedSubscriberUsage.setDailyTime(reportedUnits.getTime());

		// execution
		SubscriptionUMHandler subscriptionUMHandler = new SubscriptionUMHandler(subscription, subscriptionPackage, createExecutionContext(currentTime), currentServiceUsage, sessionLevelUsage);
		subscriptionUMHandler.init();
		subscriptionUMHandler.addPCCLevelReportedUsage(usuInstace);

		// assert
		assertThat(subscriptionUMHandler, PackageUMHandlerMatchers.hasCurrentUsage(expectedSubscriberUsage));
		assertThat(subscriptionUMHandler, PackageUMHandlerMatchers.hasUsageToUpdateInDB(expectedSubscriberUsage));

	}

	@Test
	public void test_addPCCLevelReportedUsage_resetUsage_when_DailyResetTimeExpiredAndSessionStaredInPreviousDay() {
		// setup
		Calendar previousDayResetTime = CalenderUtil.getPreviousDayMidNight(baseCalender);
		Calendar weeklyResetTime = CalenderUtil.getNextWeeklyResetTime(baseCalender);
		Calendar billingResetTime = CalenderUtil.getBillingCycleResetTime(baseCalender);
		Calendar sessionStartTime = CalenderUtil.getPreviousDay(baseCalender);
		Calendar currentTime = baseCalender;
		
		ServiceUnit existingUnits = new ServiceUnit(0, 5, 15, 20);
		ServiceUnit reportedUnits = new ServiceUnit(0, 5, 15, 20);
		setSessionStartTimeInPCRFRequest(sessionStartTime);
		ServiceUsage currentServiceUsage = createServiceUsage(existingUnits, previousDayResetTime.getTimeInMillis(), weeklyResetTime.getTimeInMillis(), billingResetTime.getTimeInMillis());
		SessionUsage sessionLevelUsage = null;
		UsageMonitoringInfo usuInstace = UMInfoFactory.of(UMLevel.PCC_RULE_LEVEL).usuInstace(MONITORING_KEY, reportedUnits);
		SubscriberUsage expectedSubscriberUsage = createSubscriberUsage(existingUnits, reportedUnits, 0, weeklyResetTime.getTimeInMillis(), billingResetTime.getTimeInMillis());
		expectedSubscriberUsage.setDailyTotal(0);
		expectedSubscriberUsage.setDailyUpload(0);
		expectedSubscriberUsage.setDailyDownload(0);
		expectedSubscriberUsage.setDailyTime(0);

		// execution
		SubscriptionUMHandler subscriptionUMHandler = new SubscriptionUMHandler(subscription, subscriptionPackage, createExecutionContext(currentTime), currentServiceUsage, sessionLevelUsage);
		subscriptionUMHandler.init();
		subscriptionUMHandler.addPCCLevelReportedUsage(usuInstace);

		// assert
		assertThat(subscriptionUMHandler, PackageUMHandlerMatchers.hasCurrentUsage(expectedSubscriberUsage));
		assertThat(subscriptionUMHandler, PackageUMHandlerMatchers.hasUsageToUpdateInDB(expectedSubscriberUsage));
	}

	@Test
	public void test_addSessionLevelReportedUsage_addUsageToExisting_when_DailyResetTimeExpiredAndSessionStaredInCurrentDate() {

		// setup
		Calendar previousDayResetTime = CalenderUtil.getPreviousDayMidNight(baseCalender);
		Calendar weeklyResetTime = CalenderUtil.getNextWeeklyResetTime(baseCalender);
		Calendar billingResetTime = CalenderUtil.getBillingCycleResetTime(baseCalender);
		Calendar sessionStartTime = CalenderUtil.getPreviousHourTime(baseCalender);
		Calendar currentTime = baseCalender;
		
		ServiceUnit existingUnits = new ServiceUnit(0, 5, 15, 20);
		ServiceUnit reportedUnits = new ServiceUnit(0, 5, 15, 20);

		setSessionStartTimeInPCRFRequest(sessionStartTime);
		ServiceUsage currentServiceUsage = createServiceUsage(existingUnits, previousDayResetTime.getTimeInMillis(), weeklyResetTime.getTimeInMillis(), billingResetTime.getTimeInMillis());
		SessionUsage sessionLevelUsage = null;
		UsageMonitoringInfo usuInstace = UMInfoFactory.of(UMLevel.SESSION_LEVEL).usuInstace(MONITORING_KEY, reportedUnits);
		SubscriberUsage expectedSubscriberUsage = createSubscriberUsage(existingUnits, reportedUnits, 0, weeklyResetTime.getTimeInMillis(), billingResetTime.getTimeInMillis());
		expectedSubscriberUsage.setDailyTotal(reportedUnits.getTotalOctets());
		expectedSubscriberUsage.setDailyUpload(reportedUnits.getInputOctets());
		expectedSubscriberUsage.setDailyDownload(reportedUnits.getOutputOctets());
		expectedSubscriberUsage.setDailyTime(reportedUnits.getTime());

		// execution
		SubscriptionUMHandler subscriptionUMHandler = new SubscriptionUMHandler(subscription, subscriptionPackage, createExecutionContext(currentTime), currentServiceUsage, sessionLevelUsage);
		subscriptionUMHandler.init();
		subscriptionUMHandler.addPCCLevelReportedUsage(usuInstace);

		// assert
		assertThat(subscriptionUMHandler, PackageUMHandlerMatchers.hasCurrentUsage(expectedSubscriberUsage));
		assertThat(subscriptionUMHandler, PackageUMHandlerMatchers.hasUsageToUpdateInDB(expectedSubscriberUsage));
	}

	@Test
	public void test_addSessionLevelReportedUsage_resetUsage_when_DailyResetTimeExpiredAndSessionStaredInPreviousDay() {

		// setup
		Calendar previousDayResetTime = CalenderUtil.getPreviousDayMidNight(baseCalender);
		Calendar weeklyResetTime = CalenderUtil.getNextWeeklyResetTime(baseCalender);
		Calendar billingResetTime = CalenderUtil.getBillingCycleResetTime(baseCalender);
		Calendar sessionStartTime = CalenderUtil.getPreviousDay(baseCalender);
		Calendar currentTime = baseCalender;
		
		ServiceUnit existingUnits = new ServiceUnit(0, 5, 15, 20);
		ServiceUnit reportedUnits = new ServiceUnit(0, 5, 15, 20);
		pcrfRequest.setSessionStartTime(new Date(sessionStartTime.getTimeInMillis()));
		ExecutionContext executionContext = new ExecutionContextExt(pcrfRequest, pcrfResponse, currentTime.getTimeInMillis());
		ServiceUsage currentServiceUsage = createServiceUsage(existingUnits, previousDayResetTime.getTimeInMillis(), weeklyResetTime.getTimeInMillis(), billingResetTime.getTimeInMillis());
		SessionUsage sessionLevelUsage = null;
		UsageMonitoringInfo usuInstace = UMInfoFactory.of(UMLevel.SESSION_LEVEL).usuInstace(MONITORING_KEY, reportedUnits);
		SubscriberUsage expectedSubscriberUsage = createSubscriberUsage(existingUnits, reportedUnits, 0, weeklyResetTime.getTimeInMillis(), billingResetTime.getTimeInMillis());
		expectedSubscriberUsage.setDailyTotal(0);
		expectedSubscriberUsage.setDailyUpload(0);
		expectedSubscriberUsage.setDailyDownload(0);
		expectedSubscriberUsage.setDailyTime(0);

		// execution
		SubscriptionUMHandler subscriptionUMHandler = new SubscriptionUMHandler(subscription, subscriptionPackage, executionContext, currentServiceUsage, sessionLevelUsage);
		subscriptionUMHandler.init();
		subscriptionUMHandler.addPCCLevelReportedUsage(usuInstace);

		// assert
		assertThat(subscriptionUMHandler, PackageUMHandlerMatchers.hasCurrentUsage(expectedSubscriberUsage));
		assertThat(subscriptionUMHandler, PackageUMHandlerMatchers.hasUsageToUpdateInDB(expectedSubscriberUsage));
	}

	@Test
	public void test_addPCCLevelReportedUsage_addUsageToExisting_when_WeeklyResetTimeExpiredAndSessionStaredInCurrentWeek() {
		// setup
		Calendar dayResetTime = CalenderUtil.getTodayMidNight(baseCalender);
		Calendar weeklyResetTime = CalenderUtil.getPreviousWeeklyResetTime(baseCalender);
		Calendar billingResetTime = CalenderUtil.getBillingCycleResetTime(baseCalender);
		Calendar sessionStartTime = CalenderUtil.getPreviousHourTime(baseCalender);
		Calendar currentTime = baseCalender;
		
		ServiceUnit existingUnits = new ServiceUnit(0, 5, 15, 20);
		ServiceUnit reportedUnits = new ServiceUnit(0, 5, 15, 20);
		setSessionStartTimeInPCRFRequest(sessionStartTime);
		
		ServiceUsage currentServiceUsage = createServiceUsage(existingUnits, dayResetTime.getTimeInMillis(), weeklyResetTime.getTimeInMillis(), billingResetTime.getTimeInMillis());
		SessionUsage sessionLevelUsage = null;
		UsageMonitoringInfo usuInstace = UMInfoFactory.of(UMLevel.PCC_RULE_LEVEL).usuInstace(MONITORING_KEY, reportedUnits);
		SubscriberUsage expectedSubscriberUsage = createSubscriberUsage(existingUnits, reportedUnits, dayResetTime.getTimeInMillis(), 0, billingResetTime.getTimeInMillis());
		expectedSubscriberUsage.setWeeklyTotal(reportedUnits.getTotalOctets());
		expectedSubscriberUsage.setWeeklyDownload(reportedUnits.getOutputOctets());
		expectedSubscriberUsage.setWeeklyUpload(reportedUnits.getInputOctets());
		expectedSubscriberUsage.setWeeklyTime(reportedUnits.getTime());

		// execution
		SubscriptionUMHandler subscriptionUMHandler = new SubscriptionUMHandler(subscription, subscriptionPackage, createExecutionContext(currentTime), currentServiceUsage, sessionLevelUsage);
		subscriptionUMHandler.init();
		subscriptionUMHandler.addPCCLevelReportedUsage(usuInstace);

		// assert
		assertThat(subscriptionUMHandler, PackageUMHandlerMatchers.hasCurrentUsage(expectedSubscriberUsage));
		assertThat(subscriptionUMHandler, PackageUMHandlerMatchers.hasUsageToUpdateInDB(expectedSubscriberUsage));
	}

	private ExecutionContext createExecutionContext(Calendar currentTime) {
		return new ExecutionContextExt(pcrfRequest, pcrfResponse, currentTime.getTimeInMillis());
	}

	private void setSessionStartTimeInPCRFRequest(Calendar sessionStartTime) {
		pcrfRequest.setSessionStartTime(new Date(sessionStartTime.getTimeInMillis()));
	}

	@Test
	public void test_addPCCLevelReportedUsage_resetUsage_when_WeeklyResetTimeExpiredAndSessionStaredInPreviousWeek() {
		// setup
		Calendar dailyResetTime = CalenderUtil.getTodayMidNight(baseCalender);
		Calendar weeklyResetTime = CalenderUtil.getPreviousWeeklyResetTime(baseCalender);
		Calendar billingResetTime = CalenderUtil.getBillingCycleResetTime(baseCalender);
		Calendar sessionStartTime = CalenderUtil.getPreviousWeekTime(baseCalender);
		Calendar currentTime = baseCalender;
		
		ServiceUnit existingUnits = new ServiceUnit(0, 5, 15, 20);
		ServiceUnit reportedUnits = new ServiceUnit(0, 5, 15, 20);
		
		setSessionStartTimeInPCRFRequest(sessionStartTime);
		
		ServiceUsage currentServiceUsage = createServiceUsage(existingUnits, dailyResetTime.getTimeInMillis(), weeklyResetTime.getTimeInMillis(), billingResetTime.getTimeInMillis());
		SessionUsage sessionLevelUsage = null;
		UsageMonitoringInfo usuInstace = UMInfoFactory.of(UMLevel.PCC_RULE_LEVEL).usuInstace(MONITORING_KEY, reportedUnits);
		SubscriberUsage expectedSubscriberUsage = createSubscriberUsage(existingUnits, reportedUnits, dailyResetTime.getTimeInMillis(), 0, billingResetTime.getTimeInMillis());
		expectedSubscriberUsage.setWeeklyTotal(0);
		expectedSubscriberUsage.setWeeklyUpload(0);
		expectedSubscriberUsage.setWeeklyDownload(0);
		expectedSubscriberUsage.setWeeklyTime(0);

		// execution
		SubscriptionUMHandler subscriptionUMHandler = new SubscriptionUMHandler(subscription, subscriptionPackage, createExecutionContext(currentTime), currentServiceUsage, sessionLevelUsage);
		subscriptionUMHandler.init();
		subscriptionUMHandler.addPCCLevelReportedUsage(usuInstace);

		// assert
		assertThat(subscriptionUMHandler, PackageUMHandlerMatchers.hasCurrentUsage(expectedSubscriberUsage));
		assertThat(subscriptionUMHandler, PackageUMHandlerMatchers.hasUsageToUpdateInDB(expectedSubscriberUsage));
	}

	@Test
	public void test_addSessionLevelReportedUsage_addUsageToExisting_when_WeeklyResetTimeExpiredAndSessionStaredInCurrentWeek() {

		// setup
		Calendar dailyResetTime = CalenderUtil.getTodayMidNight(baseCalender);
		Calendar weeklyResetTime = CalenderUtil.getPreviousWeeklyResetTime(baseCalender);
		Calendar billingResetTime = CalenderUtil.getBillingCycleResetTime(baseCalender);
		Calendar sessionStartTime = CalenderUtil.getPreviousDay(baseCalender);
		Calendar currentTime = baseCalender;
		
		ServiceUnit existingUnits = new ServiceUnit(0, 5, 15, 20);
		ServiceUnit reportedUnits = new ServiceUnit(0, 5, 15, 20);

		setSessionStartTimeInPCRFRequest(sessionStartTime);
		ServiceUsage currentServiceUsage = createServiceUsage(existingUnits, dailyResetTime.getTimeInMillis(), weeklyResetTime.getTimeInMillis(), billingResetTime.getTimeInMillis());
		SessionUsage sessionLevelUsage = null;
		UsageMonitoringInfo usuInstace = UMInfoFactory.of(UMLevel.SESSION_LEVEL).usuInstace(MONITORING_KEY, reportedUnits);
		SubscriberUsage expectedSubscriberUsage = createSubscriberUsage(existingUnits, reportedUnits, dailyResetTime.getTimeInMillis(), 0, billingResetTime.getTimeInMillis());
		expectedSubscriberUsage.setWeeklyTotal(reportedUnits.getTotalOctets());
		expectedSubscriberUsage.setWeeklyUpload(reportedUnits.getInputOctets());
		expectedSubscriberUsage.setWeeklyDownload(reportedUnits.getOutputOctets());
		expectedSubscriberUsage.setWeeklyTime(reportedUnits.getTime());

		// execution
		SubscriptionUMHandler subscriptionUMHandler = new SubscriptionUMHandler(subscription, subscriptionPackage, createExecutionContext(currentTime), currentServiceUsage, sessionLevelUsage);
		subscriptionUMHandler.init();
		subscriptionUMHandler.addPCCLevelReportedUsage(usuInstace);

		// assert
		assertThat(subscriptionUMHandler, PackageUMHandlerMatchers.hasCurrentUsage(expectedSubscriberUsage));
		assertThat(subscriptionUMHandler, PackageUMHandlerMatchers.hasUsageToUpdateInDB(expectedSubscriberUsage));
	}

	@Test
	public void test_addSessionLevelReportedUsage_resetUsage_when_WeeklyResetTimeExpiredAndSessionStaredInPreviousWeek() {

		// setup
		Calendar dailyResetTime = CalenderUtil.getTodayMidNight(baseCalender);
		Calendar weeklyResetTime = CalenderUtil.getPreviousWeeklyResetTime(baseCalender);
		Calendar billingResetTime = CalenderUtil.getBillingCycleResetTime(baseCalender);
		Calendar sessionStartTime = CalenderUtil.getPreviousWeekTime(baseCalender);
		Calendar currentTime = baseCalender;
		
		ServiceUnit existingUnits = new ServiceUnit(0, 5, 15, 20);
		ServiceUnit reportedUnits = new ServiceUnit(0, 5, 15, 20);
		
		setSessionStartTimeInPCRFRequest(sessionStartTime);
		ServiceUsage currentServiceUsage = createServiceUsage(existingUnits, dailyResetTime.getTimeInMillis(), weeklyResetTime.getTimeInMillis(), billingResetTime.getTimeInMillis());
		SessionUsage sessionLevelUsage = null;
		UsageMonitoringInfo usuInstace = UMInfoFactory.of(UMLevel.SESSION_LEVEL).usuInstace(MONITORING_KEY, reportedUnits);
		SubscriberUsage expectedSubscriberUsage = createSubscriberUsage(existingUnits, reportedUnits, dailyResetTime.getTimeInMillis(), 0, billingResetTime.getTimeInMillis());
		expectedSubscriberUsage.setWeeklyTotal(0);
		expectedSubscriberUsage.setWeeklyUpload(0);
		expectedSubscriberUsage.setWeeklyDownload(0);
		expectedSubscriberUsage.setWeeklyTime(0);

		// execution
		SubscriptionUMHandler subscriptionUMHandler = new SubscriptionUMHandler(subscription, subscriptionPackage, createExecutionContext(currentTime), currentServiceUsage, sessionLevelUsage);
		subscriptionUMHandler.init();
		subscriptionUMHandler.addPCCLevelReportedUsage(usuInstace);

		// assert
		assertThat(subscriptionUMHandler, PackageUMHandlerMatchers.hasCurrentUsage(expectedSubscriberUsage));
		assertThat(subscriptionUMHandler, PackageUMHandlerMatchers.hasUsageToUpdateInDB(expectedSubscriberUsage));

	}

	private ServiceUsage createServiceUsage(ServiceUnit existingUnit, long dailyResetTime, long weeklyResetTime, long billingResetTime) {
		SubscriberUsage subscriberUsage = createSubscriberUsage(existingUnit, dailyResetTime, weeklyResetTime, billingResetTime);
		return new ServiceUsage(listToMapTransformer.apply(Arrays.asList(subscriberUsage)));
	}

	private SubscriberUsage createSubscriberUsage(ServiceUnit existingUnit, long dailyResetTime, long weeklyResetTime, long billingCycleResetTime) {
		return new SubscriberUsageBuilder("1", SUBSCRIBER_ID, CommonConstants.ALL_SERVICE_ID, QUOTA_PROFILE_ID, PACKAGE_ID,PRODUCT_OFFER_ID)
				.withSubscriptionId(SUBSCRIPTION_ID)
				.withAllTypeUsage(existingUnit.getTotalOctets(), existingUnit.getOutputOctets(), existingUnit.getInputOctets(), existingUnit.getTime())
				.withDailyResetTime(dailyResetTime)
				.withWeeklyResetTime(weeklyResetTime)
				.withBillingCycleResetTime(billingCycleResetTime)
				.build();
	}

	private SubscriberUsage createSubscriberUsage(ServiceUnit existingUnit, ServiceUnit reportedUnit, long dailyResetTime, long weeklyResetTime, long billingCycleResetTime) {
		SubscriberUsage subscriberUsage = new SubscriberUsageBuilder("1", SUBSCRIBER_ID, CommonConstants.ALL_SERVICE_ID, QUOTA_PROFILE_ID, PACKAGE_ID,PRODUCT_OFFER_ID)
				.withSubscriptionId(SUBSCRIPTION_ID)
				.withAllTypeUsage(existingUnit.getTotalOctets() + reportedUnit.getTotalOctets(), existingUnit.getOutputOctets()
						+ reportedUnit.getOutputOctets(), existingUnit.getInputOctets() + reportedUnit.getInputOctets(), existingUnit.getTime()
						+ reportedUnit.getTime())
				.withDailyResetTime(dailyResetTime)
				.withWeeklyResetTime(weeklyResetTime)
				.withBillingCycleResetTime(billingCycleResetTime)
				.build();

		return subscriberUsage;
	}

	private class ExecutionContextExt extends ExecutionContext {

        public static final String INR = "INR";
        private Calendar calendar;

		public ExecutionContextExt(PCRFRequest pcrfRequest, PCRFResponse pcrfResponse, long currentTime) {
			super(pcrfRequest, pcrfResponse, CacheAwareDDFTable.getInstance(), INR);
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(currentTime);
			this.calendar = calendar;
		}

		@Override
		public Calendar getCurrentTime() {
			return calendar;
		}
	}
}
