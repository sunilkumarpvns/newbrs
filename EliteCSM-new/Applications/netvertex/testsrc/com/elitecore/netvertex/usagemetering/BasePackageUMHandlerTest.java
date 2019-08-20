package com.elitecore.netvertex.usagemetering;

import com.elitecore.commons.base.Function;
import com.elitecore.commons.kpi.exception.InitializationFailedException;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.DeploymentMode;
import com.elitecore.corenetvertex.constants.RenewalIntervalUnit;
import com.elitecore.corenetvertex.data.SessionUsage;
import com.elitecore.corenetvertex.pkg.PkgData;
import com.elitecore.corenetvertex.pkg.qos.QosProfileData;
import com.elitecore.corenetvertex.pkg.quota.QuotaProfileData;
import com.elitecore.corenetvertex.pm.HibernateDataReader;
import com.elitecore.corenetvertex.pm.PolicyManager;
import com.elitecore.corenetvertex.pm.pkg.PackageFactory;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfile;
import com.elitecore.corenetvertex.pm.rnc.notification.ThresholdNotificationSchemeFactory;
import com.elitecore.corenetvertex.pm.rnc.pkg.RnCPackageFactory;
import com.elitecore.corenetvertex.pm.rnc.ratecard.RateCardFactory;
import com.elitecore.corenetvertex.pm.rnc.ratecard.RateCardVersionFactory;
import com.elitecore.corenetvertex.pm.rnc.rcgroup.RateCardGroupFactory;
import com.elitecore.corenetvertex.spr.ServiceUsage;
import com.elitecore.corenetvertex.spr.SubscriberUsage;
import com.elitecore.corenetvertex.spr.SubscriberUsage.SubscriberUsageBuilder;
import com.elitecore.corenetvertex.util.HibernateSessionFactory;
import com.elitecore.netvertex.core.ddf.CacheAwareDDFTable;
import com.elitecore.netvertex.core.servicepolicy.ExecutionContext;
import com.elitecore.netvertex.pm.rnc.RnCFactory;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.impl.PCRFRequestImpl;
import com.elitecore.netvertex.service.pcrf.impl.PCRFResponseImpl;
import com.elitecore.netvertex.usagemetering.factory.UMInfoFactory;
import com.elitecore.netvertex.util.CalenderUtil;
import junitparams.JUnitParamsRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.unitils.reflectionassert.ReflectionAssert;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

@RunWith(JUnitParamsRunner.class)
public class BasePackageUMHandlerTest {

	private static final String PACKAGE_ID = "test1";
	private static final String PRODUCT_OFFER_ID = "productOfferId";
	private static final String QUOTA_PROFILE_ID = "quotaId";
	private static final String SUBSCRIBER_ID = "9797979797";
	private static final String MONITORING_KEY = "monitoringkey";
	private PCRFRequest pcrfRequest;
	private Function<List<SubscriberUsage>, Map<String, Map<String, SubscriberUsage>>> listToMapTransformer = new SubscriberUsageListToMapFunction();
	private PCRFResponse pcrfResponse;
	@Mock
	private com.elitecore.corenetvertex.pm.pkg.datapackage.Package subscriberPackage;
	@Mock
	private QuotaProfile quotaProfile;

	private Calendar baseCalender = Calendar.getInstance();
	private HibernateSessionFactory hibernateSessionFactory;
	private boolean isInitialized = false;
	private DeploymentMode deploymentMode = DeploymentMode.PCC;

	@Before
	public void setUp() throws Exception{
		MockitoAnnotations.initMocks(this);
		pcrfRequest = new PCRFRequestImpl();
		pcrfResponse = new PCRFResponseImpl();
		when(subscriberPackage.getName()).thenReturn(PACKAGE_ID);
        Mockito.when(subscriberPackage.getQuotaProfile(QUOTA_PROFILE_ID)).thenReturn(quotaProfile);
		doReturn(CommonConstants.ALL_SERVICE_ID).when(subscriberPackage).getServiceId(MONITORING_KEY);
		doReturn(quotaProfile).when(subscriberPackage).getQuotaProfileByMonitoringKey(MONITORING_KEY);
		doReturn(QUOTA_PROFILE_ID).when(quotaProfile).getId();
		doReturn(PACKAGE_ID).when(subscriberPackage).getId();

		baseCalender.setTimeInMillis(Timestamp.valueOf("2017-05-17 11:11:11").getTime());


		String ssid = UUID.randomUUID().toString();
		Properties hibernateProperties = new Properties();
		hibernateProperties.setProperty("hibernate.connection.url", "jdbc:h2:mem:" + ssid);
		hibernateProperties.setProperty("hibernate.hbm2ddl.auto", "create-drop");
		hibernateProperties.setProperty("hibernate.show_sql","true");
		hibernateSessionFactory = HibernateSessionFactory.create("hibernate/test-hibernate.cfg.xml", hibernateProperties);

		if(isInitialized==false){
			createAndSaveBasePackage();
			PolicyManager policyManager = new PolicyManager();
			PolicyManager.setInstance(policyManager);
			RnCFactory rnCFactory = new RnCFactory();
			initPolicyManager(rnCFactory);
			isInitialized = true;
		}
	}

	private void initPolicyManager(RnCFactory rnCFactory) throws InitializationFailedException {
		PolicyManager.getInstance().init("test", hibernateSessionFactory.getSessionFactory(), new HibernateDataReader(), new PackageFactory(),new RnCPackageFactory(new RateCardGroupFactory(new RateCardFactory(new RateCardVersionFactory(rnCFactory), rnCFactory), rnCFactory), rnCFactory,new ThresholdNotificationSchemeFactory(rnCFactory)), deploymentMode);
	}


	@After
	public void tearDown(){
		hibernateSessionFactory.shutdown();
		PolicyManager.release();
	}

	public void createAndSaveBasePackage(){
		PkgData pkgData = new PkgData();
		pkgData.setId(PACKAGE_ID);
		QosProfileData qosProfileData = new QosProfileData();
		qosProfileData.setId("QOS_ID");

		com.elitecore.corenetvertex.pkg.quota.QuotaProfileData quotaProfileData = new QuotaProfileData();
		quotaProfileData.setId(QUOTA_PROFILE_ID);
		quotaProfileData.setRenewalInterval(2);
		quotaProfileData.setRenewalIntervalUnit(RenewalIntervalUnit.MONTH_END.name());

		qosProfileData.setQuotaProfile(quotaProfileData);

		hibernateSessionFactory.save(quotaProfileData);
		List qosList = new ArrayList();
		qosList.add(qosProfileData);
		hibernateSessionFactory.save(qosProfileData);

		pkgData.setQosProfiles(qosList);

		List quotaList = new ArrayList();
		quotaList.add(quotaProfileData);

		pkgData.setQuotaProfiles(quotaList);

		hibernateSessionFactory.save(pkgData);
	}

	@Test
	public void test_addPCCLevelReportedUsage_addUsageToExisting_when_DailyResetTimeExpiredAndSessionStaredInCurrentDate() {
		// setup
		Calendar previousDayResetTime = CalenderUtil.getPreviousDayMidNight(baseCalender);
		Calendar weeklyResetTime = CalenderUtil.getNextWeeklyResetTime(baseCalender);
		Calendar sessionStartTime = CalenderUtil.getPreviousHourTime(baseCalender);
		long billingCycleResetTime = RenewalIntervalUnit.MONTH_END.addTime(System.currentTimeMillis(),2);
		Calendar currentTime = baseCalender;

		ServiceUnit existingUnits = new ServiceUnit(0, 5, 15, 20);
		ServiceUnit reportedUnits = new ServiceUnit(0, 5, 15, 20);
		setSessionStartTimeInPCRFRequest(sessionStartTime);

		ServiceUsage currentServiceUsage = createServiceUsage(existingUnits, previousDayResetTime.getTimeInMillis(), weeklyResetTime.getTimeInMillis(),billingCycleResetTime);
		SessionUsage sessionLevelUsage = null;
		UsageMonitoringInfo usuInstace = UMInfoFactory.of(UMLevel.PCC_RULE_LEVEL).usuInstace(MONITORING_KEY, reportedUnits);
		SubscriberUsage expectedSubscriberUsage = createSubscriberUsage(existingUnits, reportedUnits, 0, weeklyResetTime.getTimeInMillis(),billingCycleResetTime);
		expectedSubscriberUsage.setDailyTotal(reportedUnits.getTotalOctets());
		expectedSubscriberUsage.setDailyUpload(reportedUnits.getInputOctets());
		expectedSubscriberUsage.setDailyDownload(reportedUnits.getOutputOctets());
		expectedSubscriberUsage.setDailyTime(reportedUnits.getTime());

		// execution
		BasePackageUMHandler basePackageUMHandler = new BasePackageUMHandler(SUBSCRIBER_ID, subscriberPackage, PRODUCT_OFFER_ID, createExecutionContext(currentTime), currentServiceUsage, sessionLevelUsage);
		basePackageUMHandler.init();
		basePackageUMHandler.addPCCLevelReportedUsage(usuInstace);

		// assert
		assertThat(basePackageUMHandler, PackageUMHandlerMatchers.hasCurrentUsage(expectedSubscriberUsage));
		assertThat(basePackageUMHandler, PackageUMHandlerMatchers.hasUsageToUpdateInDB(expectedSubscriberUsage));
	}

	@Test
	public void test_addPCCLevelReportedUsage_resetUsage_when_DailyResetTimeExpiredAndSessionStaredInPreviousDay() {
		// setup
		Calendar previousDayResetTime = CalenderUtil.getPreviousDayMidNight(baseCalender);
		Calendar weeklyResetTime = CalenderUtil.getNextWeeklyResetTime(baseCalender);
		Calendar sessionStartTime = CalenderUtil.getPreviousDay(baseCalender);
		long billingCycleResetTime = RenewalIntervalUnit.MONTH_END.addTime(System.currentTimeMillis(),2);
		Calendar currentTime = baseCalender;

		ServiceUnit existingUnits = new ServiceUnit(0, 5, 15, 20);
		ServiceUnit reportedUnits = new ServiceUnit(0, 5, 15, 20);
		setSessionStartTimeInPCRFRequest(sessionStartTime);

		ServiceUsage currentServiceUsage = createServiceUsage(existingUnits, previousDayResetTime.getTimeInMillis(), weeklyResetTime.getTimeInMillis(), billingCycleResetTime);
		SessionUsage sessionLevelUsage = null;
		UsageMonitoringInfo usuInstace = UMInfoFactory.of(UMLevel.PCC_RULE_LEVEL).usuInstace(MONITORING_KEY, reportedUnits);
		SubscriberUsage expectedSubscriberUsage = createSubscriberUsage(existingUnits, reportedUnits, 0, weeklyResetTime.getTimeInMillis(),billingCycleResetTime);
		expectedSubscriberUsage.setDailyTotal(0);
		expectedSubscriberUsage.setDailyUpload(0);
		expectedSubscriberUsage.setDailyDownload(0);
		expectedSubscriberUsage.setDailyTime(0);

		// execution
		BasePackageUMHandler basePackageUMHandler = new BasePackageUMHandler(SUBSCRIBER_ID, subscriberPackage, PRODUCT_OFFER_ID, createExecutionContext(currentTime), currentServiceUsage, sessionLevelUsage);
		basePackageUMHandler.init();
		basePackageUMHandler.addPCCLevelReportedUsage(usuInstace);

		// assert
		assertThat(basePackageUMHandler, PackageUMHandlerMatchers.hasCurrentUsage(expectedSubscriberUsage));
		assertThat(basePackageUMHandler, PackageUMHandlerMatchers.hasUsageToUpdateInDB(expectedSubscriberUsage));
	}

	@Test
	public void test_addSessionLevelReportedUsage_addUsageToExisting_when_DailyResetTimeExpiredAndSessionStaredInCurrentDate() {

		// setup
		Calendar previousDayResetTime = CalenderUtil.getPreviousDayMidNight(baseCalender);
		Calendar weeklyResetTime = CalenderUtil.getNextWeeklyResetTime(baseCalender);
		Calendar sessionStartTime = CalenderUtil.getPreviousHourTime(baseCalender);
		long billingCycleResetTime = RenewalIntervalUnit.MONTH_END.addTime(System.currentTimeMillis(),2);
		Calendar currentTime = baseCalender;

		ServiceUnit existingUnits = new ServiceUnit(0, 5, 15, 20);
		ServiceUnit reportedUnits = new ServiceUnit(0, 5, 15, 20);

		setSessionStartTimeInPCRFRequest(sessionStartTime);
		ServiceUsage currentServiceUsage = createServiceUsage(existingUnits, previousDayResetTime.getTimeInMillis(), weeklyResetTime.getTimeInMillis(), billingCycleResetTime);
		SessionUsage sessionLevelUsage = null;
		UsageMonitoringInfo usuInstace = UMInfoFactory.of(UMLevel.SESSION_LEVEL).usuInstace(MONITORING_KEY, reportedUnits);
		SubscriberUsage expectedSubscriberUsage = createSubscriberUsage(existingUnits, reportedUnits, 0, weeklyResetTime.getTimeInMillis(),billingCycleResetTime);
		expectedSubscriberUsage.setDailyTotal(reportedUnits.getTotalOctets());
		expectedSubscriberUsage.setDailyUpload(reportedUnits.getInputOctets());
		expectedSubscriberUsage.setDailyDownload(reportedUnits.getOutputOctets());
		expectedSubscriberUsage.setDailyTime(reportedUnits.getTime());

		// execution
		BasePackageUMHandler basePackageUMHandler = new BasePackageUMHandler(SUBSCRIBER_ID, subscriberPackage, PRODUCT_OFFER_ID, createExecutionContext(currentTime), currentServiceUsage, sessionLevelUsage);
		basePackageUMHandler.init();
		basePackageUMHandler.addSessionLevelReportedUsage(usuInstace, QUOTA_PROFILE_ID);

		// assert
		assertThat(basePackageUMHandler, PackageUMHandlerMatchers.hasCurrentUsage(expectedSubscriberUsage));
		assertThat(basePackageUMHandler, PackageUMHandlerMatchers.hasUsageToUpdateInDB(expectedSubscriberUsage));
	}

	@Test
	public void test_addSessionLevelReportedUsage_resetUsage_when_DailyResetTimeExpiredAndSessionStaredInPreviousDay() {

		// setup
		Calendar previousDayResetTime = CalenderUtil.getPreviousDayMidNight(baseCalender);
		Calendar weeklyResetTime = CalenderUtil.getNextWeeklyResetTime(baseCalender);
		Calendar sessionStartTime = CalenderUtil.getPreviousDay(baseCalender);
		long billingCycleResetTime = RenewalIntervalUnit.MONTH_END.addTime(System.currentTimeMillis(),2);
		Calendar currentTime = baseCalender;

		ServiceUnit existingUnits = new ServiceUnit(0, 5, 15, 20);
		ServiceUnit reportedUnits = new ServiceUnit(0, 5, 15, 20);
		setSessionStartTimeInPCRFRequest(sessionStartTime);

		ServiceUsage currentServiceUsage = createServiceUsage(existingUnits, previousDayResetTime.getTimeInMillis(), weeklyResetTime.getTimeInMillis(), billingCycleResetTime);
		SessionUsage sessionLevelUsage = null;
		UsageMonitoringInfo usuInstace = UMInfoFactory.of(UMLevel.SESSION_LEVEL).usuInstace(MONITORING_KEY, reportedUnits);

		SubscriberUsage expectedSubscriberUsage = createSubscriberUsage(existingUnits, reportedUnits, 0, weeklyResetTime.getTimeInMillis(),billingCycleResetTime);
		expectedSubscriberUsage.setDailyTotal(0);
		expectedSubscriberUsage.setDailyUpload(0);
		expectedSubscriberUsage.setDailyDownload(0);
		expectedSubscriberUsage.setDailyTime(0);

		// execution
		PackageUMHandler basePackageUMHandler = new BasePackageUMHandler(SUBSCRIBER_ID, subscriberPackage, PRODUCT_OFFER_ID, createExecutionContext(currentTime), currentServiceUsage, sessionLevelUsage);
		basePackageUMHandler.init();
		basePackageUMHandler.addSessionLevelReportedUsage(usuInstace, QUOTA_PROFILE_ID);

		// assert
		assertThat(basePackageUMHandler, PackageUMHandlerMatchers.hasCurrentUsage(expectedSubscriberUsage));
		assertThat(basePackageUMHandler, PackageUMHandlerMatchers.hasUsageToUpdateInDB(expectedSubscriberUsage));
	}

	@Test
	public void test_addPCCLevelReportedUsage_addUsageToExisting_when_WeeklyResetTimeExpiredAndSessionStaredInCurrentWeek() {
		// setup
		Calendar dayResetTime = CalenderUtil.getTodayMidNight(baseCalender);
		Calendar weeklyResetTime = CalenderUtil.getPreviousWeeklyResetTime(baseCalender);
		Calendar sessionStartTime = CalenderUtil.getPreviousHourTime(baseCalender);
		long billingCycleResetTime = RenewalIntervalUnit.MONTH_END.addTime(System.currentTimeMillis(),2);
		Calendar currentTime = baseCalender;

		ServiceUnit existingUnits = new ServiceUnit(0, 5, 15, 20);
		ServiceUnit reportedUnits = new ServiceUnit(0, 5, 15, 20);
		setSessionStartTimeInPCRFRequest(sessionStartTime);
		ServiceUsage currentServiceUsage = createServiceUsage(existingUnits, dayResetTime.getTimeInMillis(), weeklyResetTime.getTimeInMillis(), billingCycleResetTime);
		SessionUsage sessionLevelUsage = null;
		UsageMonitoringInfo usuInstace = UMInfoFactory.of(UMLevel.PCC_RULE_LEVEL).usuInstace(MONITORING_KEY, reportedUnits);
		SubscriberUsage expectedSubscriberUsage = createSubscriberUsage(existingUnits, reportedUnits, dayResetTime.getTimeInMillis(), 0,billingCycleResetTime);
		expectedSubscriberUsage.setWeeklyTotal(reportedUnits.getTotalOctets());
		expectedSubscriberUsage.setWeeklyDownload(reportedUnits.getOutputOctets());
		expectedSubscriberUsage.setWeeklyUpload(reportedUnits.getInputOctets());
		expectedSubscriberUsage.setWeeklyTime(reportedUnits.getTime());

		// execution
		PackageUMHandler basePackageUMHandler = new BasePackageUMHandler(SUBSCRIBER_ID, subscriberPackage, PRODUCT_OFFER_ID, createExecutionContext(currentTime), currentServiceUsage, sessionLevelUsage);
		basePackageUMHandler.init();
		basePackageUMHandler.addPCCLevelReportedUsage(usuInstace);

		// assert
		assertThat(basePackageUMHandler, PackageUMHandlerMatchers.hasCurrentUsage(expectedSubscriberUsage));
		assertThat(basePackageUMHandler, PackageUMHandlerMatchers.hasUsageToUpdateInDB(expectedSubscriberUsage));
	}

	@Test
	public void test_addPCCLevelReportedUsage_resetUsage_when_WeeklyResetTimeExpiredAndSessionStaredInPreviousWeek() {

		// setup
		Calendar dailyResetTime = CalenderUtil.getTodayMidNight(baseCalender);
		Calendar weeklyResetTime = CalenderUtil.getPreviousWeeklyResetTime(baseCalender);
		Calendar sessionStartTime = CalenderUtil.getPreviousWeekTime(baseCalender);
		long billingCycleResetTime = RenewalIntervalUnit.MONTH_END.addTime(System.currentTimeMillis(),2);
		Calendar currentTime = baseCalender;

		ServiceUnit existingUnits = new ServiceUnit(0, 5, 15, 20);
		ServiceUnit reportedUnits = new ServiceUnit(0, 5, 15, 20);
		setSessionStartTimeInPCRFRequest(sessionStartTime);
		ServiceUsage currentServiceUsage = createServiceUsage(existingUnits, dailyResetTime.getTimeInMillis(), weeklyResetTime.getTimeInMillis(), billingCycleResetTime);
		SessionUsage sessionLevelUsage = null;
		UsageMonitoringInfo usuInstace = UMInfoFactory.of(UMLevel.PCC_RULE_LEVEL).usuInstace(MONITORING_KEY, reportedUnits);
		SubscriberUsage expectedSubscriberUsage = createSubscriberUsage(existingUnits, reportedUnits, dailyResetTime.getTimeInMillis(), 0,billingCycleResetTime);
		expectedSubscriberUsage.setWeeklyTotal(0);
		expectedSubscriberUsage.setWeeklyUpload(0);
		expectedSubscriberUsage.setWeeklyDownload(0);
		expectedSubscriberUsage.setWeeklyTime(0);

		// execution
		PackageUMHandler basePackageUMHandler = new BasePackageUMHandler(SUBSCRIBER_ID, subscriberPackage, PRODUCT_OFFER_ID, createExecutionContext(currentTime), currentServiceUsage, sessionLevelUsage);
		basePackageUMHandler.init();
		basePackageUMHandler.addPCCLevelReportedUsage(usuInstace);

		// assert
		assertThat(basePackageUMHandler, PackageUMHandlerMatchers.hasCurrentUsage(expectedSubscriberUsage));
		assertThat(basePackageUMHandler, PackageUMHandlerMatchers.hasUsageToUpdateInDB(expectedSubscriberUsage));

	}

	@Test
	public void test_addSessionLevelReportedUsage_addUsageToExisting_when_WeeklyResetTimeExpiredAndSessionStaredInCurrentWeek() {

		// setup
		Calendar dailyResetTime = CalenderUtil.getTodayMidNight(baseCalender);
		Calendar weeklyResetTime = CalenderUtil.getPreviousWeeklyResetTime(baseCalender);
		Calendar sessionStartTime = CalenderUtil.getPreviousHourTime(baseCalender);
		long billingCycleResetTime = RenewalIntervalUnit.MONTH_END.addTime(System.currentTimeMillis(),2);
		Calendar currentTime = baseCalender;

		ServiceUnit existingUnits = new ServiceUnit(0, 5, 15, 20);
		ServiceUnit reportedUnits = new ServiceUnit(0, 5, 15, 20);

		setSessionStartTimeInPCRFRequest(sessionStartTime);
		ServiceUsage currentServiceUsage = createServiceUsage(existingUnits, dailyResetTime.getTimeInMillis(), weeklyResetTime.getTimeInMillis(), billingCycleResetTime);
		SessionUsage sessionLevelUsage = null;
		UsageMonitoringInfo usuInstace = UMInfoFactory.of(UMLevel.SESSION_LEVEL).usuInstace(MONITORING_KEY, reportedUnits);
		SubscriberUsage expectedSubscriberUsage = createSubscriberUsage(existingUnits, reportedUnits, dailyResetTime.getTimeInMillis(), 0,billingCycleResetTime);
		expectedSubscriberUsage.setWeeklyTotal(reportedUnits.getTotalOctets());
		expectedSubscriberUsage.setWeeklyUpload(reportedUnits.getInputOctets());
		expectedSubscriberUsage.setWeeklyDownload(reportedUnits.getOutputOctets());
		expectedSubscriberUsage.setWeeklyTime(reportedUnits.getTime());

		// execution
		PackageUMHandler basePackageUMHandler = new BasePackageUMHandler(SUBSCRIBER_ID, subscriberPackage, PRODUCT_OFFER_ID, createExecutionContext(currentTime), currentServiceUsage, sessionLevelUsage);
		basePackageUMHandler.init();
		basePackageUMHandler.addSessionLevelReportedUsage(usuInstace, QUOTA_PROFILE_ID);

		// assert
		assertThat(basePackageUMHandler, PackageUMHandlerMatchers.hasCurrentUsage(expectedSubscriberUsage));
		assertThat(basePackageUMHandler, PackageUMHandlerMatchers.hasUsageToUpdateInDB(expectedSubscriberUsage));
	}

    @Test
    public void test_addSessionLevelReportedUsage_resetUsage_when_WeeklyResetTimeExpiredAndSessionStaredInPreviousWeek() {

        // setup
        Calendar dailyResetTime = CalenderUtil.getTodayMidNight(baseCalender);
        Calendar weeklyResetTime = CalenderUtil.getPreviousWeeklyResetTime(baseCalender);
        Calendar sessionStartTime = CalenderUtil.getPreviousWeekTime(baseCalender);
        long billingCycleResetTime = RenewalIntervalUnit.MONTH_END.addTime(System.currentTimeMillis(),2);
        Calendar currentTime = baseCalender;

        ServiceUnit existingUnits = new ServiceUnit(0, 5, 15, 20);
        ServiceUnit reportedUnits = new ServiceUnit(0, 5, 15, 20);
        setSessionStartTimeInPCRFRequest(sessionStartTime);
        ServiceUsage currentServiceUsage = createServiceUsage(existingUnits, dailyResetTime.getTimeInMillis(), weeklyResetTime.getTimeInMillis(), billingCycleResetTime);
        SessionUsage sessionLevelUsage = null;
        UsageMonitoringInfo usuInstace = UMInfoFactory.of(UMLevel.SESSION_LEVEL).usuInstace(MONITORING_KEY, reportedUnits);
        SubscriberUsage expectedSubscriberUsage = createSubscriberUsage(existingUnits, reportedUnits, dailyResetTime.getTimeInMillis(), 0,billingCycleResetTime);
        expectedSubscriberUsage.setWeeklyTotal(0);
        expectedSubscriberUsage.setWeeklyUpload(0);
        expectedSubscriberUsage.setWeeklyDownload(0);
        expectedSubscriberUsage.setWeeklyTime(0);

        // execution
        PackageUMHandler basePackageUMHandler = new BasePackageUMHandler(SUBSCRIBER_ID, subscriberPackage, PRODUCT_OFFER_ID, createExecutionContext(currentTime), currentServiceUsage, sessionLevelUsage);
        basePackageUMHandler.init();
        basePackageUMHandler.addSessionLevelReportedUsage(usuInstace, QUOTA_PROFILE_ID);

        // assert
        assertThat(basePackageUMHandler, PackageUMHandlerMatchers.hasCurrentUsage(expectedSubscriberUsage));
        assertThat(basePackageUMHandler, PackageUMHandlerMatchers.hasUsageToUpdateInDB(expectedSubscriberUsage));

    }

    @Test
    public void test_addNewUsageWhenUsageNotFoundCurrentServiceUsageWithCorrentBillingCycleResetTime() {

        // setup
        Calendar sessionStartTime = CalenderUtil.getPreviousHourTime(baseCalender);
        long billingCycleResetTime = RenewalIntervalUnit.MONTH_END.addTime(System.currentTimeMillis(),2);
        Calendar currentTime = baseCalender;


        ServiceUnit reportedUnits = new ServiceUnit(0, 5, 15, 20);
        setSessionStartTimeInPCRFRequest(sessionStartTime);
        SessionUsage sessionLevelUsage = null;
        UsageMonitoringInfo usuInstace = UMInfoFactory.of(UMLevel.SESSION_LEVEL).usuInstace(MONITORING_KEY, reportedUnits);
        SubscriberUsage expectedSubscriberUsage
                = createSubscriberNewUsage(reportedUnits, 0, 0,billingCycleResetTime);

        ServiceUsage currentServiceUsage = new ServiceUsage(null);

        Mockito.when(quotaProfile.getRenewalInterval()).thenReturn(2);
        Mockito.when(quotaProfile.getRenewalIntervalUnit()).thenReturn(RenewalIntervalUnit.MONTH_END);

        // execution
        PackageUMHandler basePackageUMHandler = new BasePackageUMHandler(SUBSCRIBER_ID, subscriberPackage, PRODUCT_OFFER_ID,
                createExecutionContext(currentTime), currentServiceUsage, sessionLevelUsage);
        basePackageUMHandler.init();
        basePackageUMHandler.addSessionLevelReportedUsage(usuInstace, QUOTA_PROFILE_ID);
        // assert
        ReflectionAssert.assertLenientEquals(expectedSubscriberUsage,
                new ArrayList<>(basePackageUMHandler.getInsertList()).get(0));

    }

    @Test
    public void test_addNewUsageWhenUsageNotFoundCurrentServiceUsageWithZeroResetTimeForNoRenewalIntervalSet() {

        // setup
        Calendar sessionStartTime = CalenderUtil.getPreviousHourTime(baseCalender);
        Calendar currentTime = baseCalender;


        ServiceUnit reportedUnits = new ServiceUnit(0, 5, 15, 20);
        setSessionStartTimeInPCRFRequest(sessionStartTime);
        SessionUsage sessionLevelUsage = null;
        UsageMonitoringInfo usuInstace = UMInfoFactory.of(UMLevel.SESSION_LEVEL).usuInstace(MONITORING_KEY, reportedUnits);
        SubscriberUsage expectedSubscriberUsage
                = createSubscriberNewUsage(reportedUnits, 0, 0,0);

        ServiceUsage currentServiceUsage = new ServiceUsage(null);

        Mockito.when(quotaProfile.getRenewalInterval()).thenReturn(0);
        Mockito.when(quotaProfile.getRenewalIntervalUnit()).thenReturn(RenewalIntervalUnit.DAY);

        // execution
        PackageUMHandler basePackageUMHandler = new BasePackageUMHandler(SUBSCRIBER_ID, subscriberPackage, PRODUCT_OFFER_ID,
                createExecutionContext(currentTime), currentServiceUsage, sessionLevelUsage);
        basePackageUMHandler.init();
        basePackageUMHandler.addSessionLevelReportedUsage(usuInstace, QUOTA_PROFILE_ID);
        // assert
        ReflectionAssert.assertLenientEquals(expectedSubscriberUsage,
                new ArrayList<>(basePackageUMHandler.getInsertList()).get(0));

    }

    private ServiceUsage createServiceUsage(ServiceUnit existingUnit, long dailyResetTime, long weeklyResetTime, long billingCycleResetTime) {
        SubscriberUsage subscriberUsage = createSubscriberUsage(existingUnit, dailyResetTime, weeklyResetTime, billingCycleResetTime);
        return new ServiceUsage(listToMapTransformer.apply(Arrays.asList(subscriberUsage)));
    }

    private SubscriberUsage createSubscriberUsage(ServiceUnit existingUnit, long dailyResetTime, long weeklyResetTime, long billingCycleResetTime) {
        return new SubscriberUsageBuilder("1", SUBSCRIBER_ID, CommonConstants.ALL_SERVICE_ID, QUOTA_PROFILE_ID, PACKAGE_ID,PRODUCT_OFFER_ID)
                .withAllTypeUsage(existingUnit.getTotalOctets(), existingUnit.getOutputOctets(), existingUnit.getInputOctets(), existingUnit.getTime())
                .withDailyResetTime(dailyResetTime)
                .withWeeklyResetTime(weeklyResetTime)
                .withBillingCycleResetTime(billingCycleResetTime)
                .build();
    }

    private SubscriberUsage createSubscriberNewUsage(ServiceUnit existingUnit, long dailyResetTime, long weeklyResetTime, long billingCycleResetTime) {
        return new SubscriberUsageBuilder("0", SUBSCRIBER_ID, CommonConstants.ALL_SERVICE_ID, QUOTA_PROFILE_ID, PACKAGE_ID,PRODUCT_OFFER_ID)
                .withAllTypeUsage(existingUnit.getTotalOctets(), existingUnit.getOutputOctets(), existingUnit.getInputOctets(), existingUnit.getTime())
                .withDailyResetTime(dailyResetTime)
                .withWeeklyResetTime(weeklyResetTime)
                .withBillingCycleResetTime(billingCycleResetTime)
                .build();
    }

	private SubscriberUsage createSubscriberUsage(ServiceUnit existingUnit, ServiceUnit reportedUnit, long dailyResetTime, long weeklyResetTime, long billingCycleResetTime) {
		SubscriberUsage subscriberUsage = new SubscriberUsageBuilder("1", SUBSCRIBER_ID, CommonConstants.ALL_SERVICE_ID, QUOTA_PROFILE_ID, PACKAGE_ID,PRODUCT_OFFER_ID)
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

	private ExecutionContext createExecutionContext(Calendar currentTime) {
		return new ExecutionContextExt(pcrfRequest, pcrfResponse, currentTime.getTimeInMillis());
	}

	private void setSessionStartTimeInPCRFRequest(Calendar sessionStartTime) {
		pcrfRequest.setSessionStartTime(new Date(sessionStartTime.getTimeInMillis()));
	}
}
