package com.elitecore.corenetvertex.pm.pkg.factory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.elitecore.corenetvertex.constants.AggregationKey;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.DataUnit;
import com.elitecore.corenetvertex.constants.TimeUnit;
import com.elitecore.corenetvertex.constants.UsageType;
import com.elitecore.corenetvertex.pkg.dataservicetype.DataServiceTypeData;
import com.elitecore.corenetvertex.pkg.notification.NotificationTemplateData;
import com.elitecore.corenetvertex.pkg.notification.NotificationTemplateType;
import com.elitecore.corenetvertex.pkg.notification.QuotaNotificationData;
import com.elitecore.corenetvertex.pkg.rnc.QuotaUsageType;
import com.elitecore.corenetvertex.pkg.rnc.VolumeUnitType;
import com.elitecore.corenetvertex.pm.factory.RnCQuotaProfileFactory;
import com.elitecore.corenetvertex.pm.pkg.PackageFactory;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.QoSProfile;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.AllowedUsageBuilderJava;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfileDetail;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.RncProfileDetail;
import com.elitecore.corenetvertex.pm.pkg.notification.QuotaLimitEvent;
import com.elitecore.corenetvertex.pm.pkg.notification.QuotaNotificationScheme;
import com.elitecore.corenetvertex.pm.pkg.notification.QuotaThresholdEvent;
import com.elitecore.corenetvertex.pm.util.MockQuotaProfile;
import com.elitecore.corenetvertex.service.notification.NotificationEvent;
import com.elitecore.corenetvertex.service.notification.Template;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static com.elitecore.corenetvertex.constants.AggregationKey.BILLING_CYCLE;
import static com.elitecore.corenetvertex.constants.AggregationKey.DAILY;
import static com.elitecore.corenetvertex.constants.AggregationKey.WEEKLY;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

@RunWith(HierarchicalContextRunner.class)
public class QuotaNotificationFactoryTest {

    public static final String DETAIL_ID = "id";
    public static final String DETAIL_NAME = "name";
    private PackageFactory packageFactory;
    private QuotaNotificationSchemeFactory factory;
    private static final String QUOTA_PROFILE_ID = "QUOTA_PROFILE_ID";
    private static final String QUOTA_PROFILE_NAME = "QUOTA_PROFILE_NAME";
    private List<QoSProfile> qoSProfilesLst;
    private List<String> partialFailReasons;
    private RncProfileDetail rncProfileDetail;
    private List<QuotaNotificationData> quotaNotificationDatas;
    private MockQuotaProfile quotaProfile;
    private int threshold;
    private String dataServiceTypeId = "DATA_SERVICE_TYPE_1";

    @Before
    public void setUp() {
        packageFactory = new PackageFactory();
        factory = new QuotaNotificationSchemeFactory(packageFactory);
        Map<String, QuotaProfileDetail> serviceToDetailMap = new HashMap<>();
        rncProfileDetail = new RnCQuotaProfileFactory(DETAIL_ID, DETAIL_NAME).randomBalanceWithRate().create();
        serviceToDetailMap.put(CommonConstants.ALL_SERVICE_ID, rncProfileDetail);
        quotaProfile = MockQuotaProfile.create(QUOTA_PROFILE_ID, QUOTA_PROFILE_NAME, serviceToDetailMap);
        QoSProfile qoSProfile = mock(QoSProfile.class);
        when(qoSProfile.getQuotaProfile()).thenReturn(quotaProfile);
        qoSProfilesLst = Arrays.asList(qoSProfile);
        partialFailReasons = new ArrayList<>();
        quotaNotificationDatas = new ArrayList<>();
        threshold = RandomUtils.nextInt(1,100);
    }

    public class Create_ReturnNullWhen {

        public class NotificationDataNotFound {

            @Test
            public void create_ReturnNullWhenNotificationDataAreNull() {
                quotaNotificationDatas = null;
                assertNull(factory.createQuotaNotificationScheme(quotaNotificationDatas, qoSProfilesLst, partialFailReasons));
            }

            @Test
            public void create_ReturnNullWhenNotificationDataAreEmpty() {
                quotaNotificationDatas = new ArrayList<>();
                assertNull(factory.createQuotaNotificationScheme(quotaNotificationDatas, qoSProfilesLst, partialFailReasons));
            }
        }

        @Test
        public void oneConfiguredAndEventCreationSkipped() {
            quotaNotificationDatas.add(createQuotaNotificationObject(BILLING_CYCLE));
            assertNull(factory.createQuotaNotificationScheme(quotaNotificationDatas, qoSProfilesLst, partialFailReasons));
            assertEquals(1, partialFailReasons.size());
        }

        @Test
        public void twoConfiguredAndBothEventCreationSkipped() {
            quotaNotificationDatas.add(createQuotaNotificationObject(BILLING_CYCLE));
            quotaNotificationDatas.add(createQuotaNotificationObject(BILLING_CYCLE));
            assertNull(factory.createQuotaNotificationScheme(quotaNotificationDatas, qoSProfilesLst, partialFailReasons));
            assertEquals(2, partialFailReasons.size());
        }

        @Test
        public void emailAndSMSBothNotificationTemplateNotConfigured() {
            quotaNotificationDatas.add(createTemplateWithoutEmailSMSForBaseAddOn());
            assertNull(factory.createQuotaNotificationScheme(quotaNotificationDatas, qoSProfilesLst, partialFailReasons));
            assertEquals(1, partialFailReasons.size());
        }

    }

    private QuotaNotificationData createQuotaNotificationObject(AggregationKey key) {
        return createQuotaNotificationObject(key,dataServiceTypeId);
    }

    private QuotaNotificationData createQuotaNotificationObject(AggregationKey key,String serviceId) {
        QuotaNotificationData quotaNotificationData = new QuotaNotificationData();
        quotaNotificationData.setEmailTemplateData(createTemplateWithEmail());
        quotaNotificationData.setQuotaProfileName(QUOTA_PROFILE_NAME);
        quotaNotificationData.setFupLevel(0);
        quotaNotificationData.setAggregationKey(key);
        quotaNotificationData.setThreshold(threshold);
        DataServiceTypeData data = new DataServiceTypeData();
        data.setId(serviceId);
        quotaNotificationData.setDataServiceTypeData(data);
        return quotaNotificationData;
    }

    private QuotaNotificationData createTemplateWithoutEmailSMSForBaseAddOn() {
        QuotaNotificationData data = createQuotaNotificationObject(BILLING_CYCLE);
        data.setEmailTemplateData(null);
        data.setThreshold(10);
        return data;
    }

    public class CreateReturnsQuotaNotificationScheme {

        private RnCQuotaProfileFactory rncProfileDetailFactory;

        @Before
        public void setUp() {
            rncProfileDetailFactory = new RnCQuotaProfileFactory(DETAIL_ID, DETAIL_NAME).randomBalanceWithRate();
        }

        @Test
        public void eventCreationSucceed() {
            quotaNotificationDatas.add(createQuotaNotificationObject(BILLING_CYCLE));
            quotaNotificationDatas.add(createQuotaNotificationObject(DAILY));
            quotaNotificationDatas.add(createQuotaNotificationObject(WEEKLY));
            rncProfileDetailFactory.withQuotaUsageType(QuotaUsageType.VOLUME)
                    .withVolumeUnitType(VolumeUnitType.TOTAL);

            int billingCycleQuota = RandomUtils.nextInt(1, 100000);
            int dailyCycleQuota = RandomUtils.nextInt(1, 100000);
            int weeklyCycleQuota = RandomUtils.nextInt(1, 100000);

            rncProfileDetailFactory.withBillingCycleUsage(new AllowedUsageBuilderJava().total(billingCycleQuota, DataUnit.BYTE).createBillingCycleAllowedUsage());
            rncProfileDetailFactory.withDailyCycleUsage(new AllowedUsageBuilderJava().total(dailyCycleQuota, DataUnit.BYTE).createDailyAllowedUsage());
            rncProfileDetailFactory.withWeeklyCycleUsage(new AllowedUsageBuilderJava().total(weeklyCycleQuota, DataUnit.BYTE).createWeeklyAllowedUsage());
            rncProfileDetail = rncProfileDetailFactory.create();

            quotaProfile.withHsqQuotaProfileDetail(CommonConstants.ALL_SERVICE_ID, rncProfileDetail);

            QuotaNotificationScheme actualScheme = factory.createQuotaNotificationScheme(quotaNotificationDatas, qoSProfilesLst, partialFailReasons);
            assertEquals(1, actualScheme.getQuotaThresgoldEvents().size());
            assertEquals(1, actualScheme.getQuotaThresgoldEvents().get(0).size());

            assertEquals(2, actualScheme.getQuotaLimitEvents().size());
            assertEquals(1, actualScheme.getQuotaLimitEvents().get(0).size());
            assertEquals(1, actualScheme.getQuotaLimitEvents().get(1).size());

            QuotaThresholdEvent expectedBillingEvent = (QuotaThresholdEvent)createExpectedEvent(UsageType.VOLUME, threshold, billingCycleQuota, BILLING_CYCLE);
            QuotaLimitEvent expectedDailyEvent = (QuotaLimitEvent)createExpectedEvent(UsageType.VOLUME, threshold, dailyCycleQuota, DAILY);
            QuotaLimitEvent expectedWeeklyEvent = (QuotaLimitEvent)createExpectedEvent(UsageType.VOLUME, threshold, weeklyCycleQuota, WEEKLY);

            assertReflectionEquals(expectedBillingEvent, actualScheme.getQuotaThresgoldEvents().get(0).get(0));
            if(actualScheme.getQuotaLimitEvents().get(0).get(0).getAggregationKey().equals(DAILY)) {
                assertReflectionEquals(expectedDailyEvent, actualScheme.getQuotaLimitEvents().get(0).get(0));
                assertReflectionEquals(expectedWeeklyEvent, actualScheme.getQuotaLimitEvents().get(1).get(0));
            }
            else {
                assertReflectionEquals(expectedWeeklyEvent, actualScheme.getQuotaLimitEvents().get(0).get(0));
                assertReflectionEquals(expectedDailyEvent, actualScheme.getQuotaLimitEvents().get(1).get(0));
            }
        }

        @Test
        public void eventsShouldBeSortedAscendingOrderThreshold() {
            int threshold1 = 70;
            int threshold2 = 80;
            int threshold3 = 40;

            int configuredQuota = RandomUtils.nextInt(1, 10000);
            int dailyCycleQuota = RandomUtils.nextInt(1, 10000);
            int weeklyCycleQuota = RandomUtils.nextInt(1, 100000);

            quotaNotificationDatas.add(createTemplateWithEmailAndThresold(threshold1, BILLING_CYCLE));
            quotaNotificationDatas.add(createTemplateWithEmailAndThresold(threshold2, BILLING_CYCLE));
            quotaNotificationDatas.add(createTemplateWithEmailAndThresold(threshold3, BILLING_CYCLE));

            quotaNotificationDatas.add(createTemplateWithEmailAndThresold(threshold1, DAILY));
            quotaNotificationDatas.add(createTemplateWithEmailAndThresold(threshold2, DAILY));
            quotaNotificationDatas.add(createTemplateWithEmailAndThresold(threshold3, DAILY));

            quotaNotificationDatas.add(createTemplateWithEmailAndThresold(threshold1, WEEKLY));
            quotaNotificationDatas.add(createTemplateWithEmailAndThresold(threshold2, WEEKLY));
            quotaNotificationDatas.add(createTemplateWithEmailAndThresold(threshold3, WEEKLY));

            rncProfileDetailFactory.withQuotaUsageType(QuotaUsageType.VOLUME).withVolumeUnitType(VolumeUnitType.TOTAL);
            rncProfileDetailFactory.withBillingCycleUsage(new AllowedUsageBuilderJava().total(configuredQuota, DataUnit.BYTE).createBillingCycleAllowedUsage());
            rncProfileDetailFactory.withDailyCycleUsage(new AllowedUsageBuilderJava().total(dailyCycleQuota, DataUnit.BYTE).createDailyAllowedUsage());
            rncProfileDetailFactory.withWeeklyCycleUsage(new AllowedUsageBuilderJava().total(weeklyCycleQuota, DataUnit.BYTE).createWeeklyAllowedUsage());
            rncProfileDetail = rncProfileDetailFactory.create();
            quotaProfile.withHsqQuotaProfileDetail(CommonConstants.ALL_SERVICE_ID, rncProfileDetail);

            QuotaNotificationScheme actualScheme = factory.createQuotaNotificationScheme(quotaNotificationDatas, qoSProfilesLst, partialFailReasons);
            assertEquals(1, actualScheme.getQuotaThresgoldEvents().size());
            assertEquals(3, actualScheme.getQuotaThresgoldEvents().get(0).size());

            assertEquals(2, actualScheme.getQuotaLimitEvents().size());
            assertEquals(3, actualScheme.getQuotaLimitEvents().get(0).size());
            assertEquals(3, actualScheme.getQuotaLimitEvents().get(1).size());

            QuotaThresholdEvent expectedEvent1 = (QuotaThresholdEvent)createExpectedEvent(UsageType.VOLUME, threshold1, configuredQuota, BILLING_CYCLE);
            QuotaThresholdEvent expectedEvent2 = (QuotaThresholdEvent)createExpectedEvent(UsageType.VOLUME, threshold2, configuredQuota, BILLING_CYCLE);
            QuotaThresholdEvent expectedEvent3 = (QuotaThresholdEvent)createExpectedEvent(UsageType.VOLUME, threshold3, configuredQuota, BILLING_CYCLE);

            QuotaLimitEvent expectedEventDaily1 = (QuotaLimitEvent)createExpectedEvent(UsageType.VOLUME, threshold1, dailyCycleQuota, DAILY);
            QuotaLimitEvent expectedEventDaily2 = (QuotaLimitEvent)createExpectedEvent(UsageType.VOLUME, threshold2, dailyCycleQuota, DAILY);
            QuotaLimitEvent expectedEventDaily3 = (QuotaLimitEvent)createExpectedEvent(UsageType.VOLUME, threshold3, dailyCycleQuota, DAILY);

            QuotaLimitEvent expectedEventWeekly1 = (QuotaLimitEvent)createExpectedEvent(UsageType.VOLUME, threshold1, weeklyCycleQuota, WEEKLY);
            QuotaLimitEvent expectedEventWeekly2 = (QuotaLimitEvent)createExpectedEvent(UsageType.VOLUME, threshold2, weeklyCycleQuota, WEEKLY);
            QuotaLimitEvent expectedEventWeekly3 = (QuotaLimitEvent)createExpectedEvent(UsageType.VOLUME, threshold3, weeklyCycleQuota, WEEKLY);

            assertReflectionEquals(expectedEvent2, actualScheme.getQuotaThresgoldEvents().get(0).get(0));
            assertReflectionEquals(expectedEvent1, actualScheme.getQuotaThresgoldEvents().get(0).get(1));
            assertReflectionEquals(expectedEvent3, actualScheme.getQuotaThresgoldEvents().get(0).get(2));

            if(actualScheme.getQuotaLimitEvents().get(0).get(0).getAggregationKey().equals(DAILY)) {
                assertReflectionEquals(expectedEventDaily2, actualScheme.getQuotaLimitEvents().get(0).get(0));
                assertReflectionEquals(expectedEventDaily1, actualScheme.getQuotaLimitEvents().get(0).get(1));
                assertReflectionEquals(expectedEventDaily3, actualScheme.getQuotaLimitEvents().get(0).get(2));

                assertReflectionEquals(expectedEventWeekly2, actualScheme.getQuotaLimitEvents().get(1).get(0));
                assertReflectionEquals(expectedEventWeekly1, actualScheme.getQuotaLimitEvents().get(1).get(1));
                assertReflectionEquals(expectedEventWeekly3, actualScheme.getQuotaLimitEvents().get(1).get(2));
            }
            else {
                assertReflectionEquals(expectedEventDaily2, actualScheme.getQuotaLimitEvents().get(1).get(0));
                assertReflectionEquals(expectedEventDaily1, actualScheme.getQuotaLimitEvents().get(1).get(1));
                assertReflectionEquals(expectedEventDaily3, actualScheme.getQuotaLimitEvents().get(1).get(2));

                assertReflectionEquals(expectedEventWeekly2, actualScheme.getQuotaLimitEvents().get(0).get(0));
                assertReflectionEquals(expectedEventWeekly1, actualScheme.getQuotaLimitEvents().get(0).get(1));
                assertReflectionEquals(expectedEventWeekly3, actualScheme.getQuotaLimitEvents().get(0).get(2));
            }
        }

        private QuotaNotificationData createTemplateWithEmailAndThresold(int configuredThresold, AggregationKey key) {
            QuotaNotificationData data = createQuotaNotificationObject(key);
            data.setThreshold(configuredThresold);
            data.setEmailTemplateData(createTemplateWithEmail());
            return data;
        }
    }

    private NotificationEvent createExpectedEvent(UsageType usageType, int threshold, int configuredQuota, AggregationKey key) {
        NotificationTemplateData emailNotificationData = new NotificationTemplateData();
        emailNotificationData.setTemplateType(NotificationTemplateType.EMAIL);
        NotificationEvent event;
        if(key == BILLING_CYCLE) {
            event = new QuotaThresholdEvent(usageType, key,
                    rncProfileDetail.getDataServiceType().getServiceIdentifier(),
                    rncProfileDetail.getRatingGroup().getIdentifier(),
                    QUOTA_PROFILE_ID,
                    configuredQuota - (configuredQuota * threshold/100), 0, new Template(emailNotificationData.getId(), emailNotificationData.getName(), emailNotificationData.getSubject(),
                    emailNotificationData.getTemplateData()),
                    null);
        } else {
            event = new QuotaLimitEvent(usageType, key,
                    rncProfileDetail.getDataServiceType().getServiceIdentifier(),
                    rncProfileDetail.getRatingGroup().getIdentifier(),
                    QUOTA_PROFILE_ID,
                    (configuredQuota * threshold)/100, 0, new Template(emailNotificationData.getId(), emailNotificationData.getName(), emailNotificationData.getSubject(),
                    emailNotificationData.getTemplateData()),
                    null);
        }
        return event;
    }

    public class PartialReasonShouldBeAddedWhen {

        public class CalculatedThresholdIsLessThanZero {

            private RnCQuotaProfileFactory rncProfileDetailFactory;

            @Before
            public void setUp() {
                rncProfileDetailFactory = new RnCQuotaProfileFactory(DETAIL_ID, DETAIL_NAME).randomBalanceWithRate();
                quotaNotificationDatas.add(createQuotaNotificationObject(BILLING_CYCLE));
            }

            @Test
            public void quotaUsageTypeIsTotalAndTotalIsUndefined() {
                setTotalUndefined();
                assertNull(factory.createQuotaNotificationScheme(quotaNotificationDatas, qoSProfilesLst, partialFailReasons));
                assertEquals(1, partialFailReasons.size());
                assertThat(partialFailReasons.get(0), containsString("Allowed Usage is zero/undefined/unlimited"));
            }

            private void setTotalUndefined() {
                rncProfileDetailFactory.withQuotaUsageType(QuotaUsageType.VOLUME)
                        .withVolumeUnitType(VolumeUnitType.TOTAL);
                rncProfileDetailFactory.withBillingCycleUsage(new AllowedUsageBuilderJava().total(CommonConstants.QUOTA_UNDEFINED, DataUnit.BYTE).createBillingCycleAllowedUsage());
                quotaProfile.withHsqQuotaProfileDetail(CommonConstants.ALL_SERVICE_ID, rncProfileDetailFactory.create());
            }

            @Test
            public void quotaUsageTypeIsDownloadAndDownloadIsUndefined() {
                setDownloadUndefined();

                assertNull(factory.createQuotaNotificationScheme(quotaNotificationDatas, qoSProfilesLst, partialFailReasons));
                assertEquals(1, partialFailReasons.size());
                assertThat(partialFailReasons.get(0), containsString("Allowed Usage is zero/undefined/unlimited"));
            }

            private void setDownloadUndefined() {
                rncProfileDetailFactory.withQuotaUsageType(QuotaUsageType.VOLUME)
                        .withVolumeUnitType(VolumeUnitType.DOWNLOAD);
                rncProfileDetailFactory.withBillingCycleUsage(new AllowedUsageBuilderJava().download(CommonConstants.QUOTA_UNDEFINED, DataUnit.BYTE).createBillingCycleAllowedUsage());
                quotaProfile.withHsqQuotaProfileDetail(CommonConstants.ALL_SERVICE_ID, rncProfileDetailFactory.create());
            }

            @Test
            public void quotaUsageTypeIsUploadAndUploadIsUndefined() {
                setUploadUndefined();

                assertNull(factory.createQuotaNotificationScheme(quotaNotificationDatas, qoSProfilesLst, partialFailReasons));
                assertEquals(1, partialFailReasons.size());
                assertThat(partialFailReasons.get(0), containsString("Allowed Usage is zero/undefined/unlimited"));
            }

            private void setUploadUndefined() {
                rncProfileDetailFactory.withQuotaUsageType(QuotaUsageType.VOLUME)
                        .withVolumeUnitType(VolumeUnitType.UPLOAD);
                rncProfileDetailFactory.withBillingCycleUsage(new AllowedUsageBuilderJava().upload(CommonConstants.QUOTA_UNDEFINED, DataUnit.BYTE).createBillingCycleAllowedUsage());
                quotaProfile.withHsqQuotaProfileDetail(CommonConstants.ALL_SERVICE_ID, rncProfileDetailFactory.create());
            }

            @Test
            public void quotaUsageTypeIsTimeAndTimeIsUndefined() {
                setTimeUndefined();

                assertNull(factory.createQuotaNotificationScheme(quotaNotificationDatas, qoSProfilesLst, partialFailReasons));
                assertEquals(1, partialFailReasons.size());
                assertThat(partialFailReasons.get(0), containsString("Allowed Usage is zero/undefined/unlimited"));
            }

            private void setTimeUndefined() {
                rncProfileDetailFactory.withQuotaUsageType(QuotaUsageType.TIME);
                rncProfileDetailFactory.withBillingCycleUsage(new AllowedUsageBuilderJava().time(CommonConstants.QUOTA_UNDEFINED, TimeUnit.SECOND).createBillingCycleAllowedUsage());
                quotaProfile.withHsqQuotaProfileDetail(CommonConstants.ALL_SERVICE_ID, rncProfileDetailFactory.create());
            }
        }

        public class CalculatedThresholdZero {

            private RnCQuotaProfileFactory rncProfileDetailFactory;

            @Before
            public void setUp() {
                rncProfileDetailFactory = new RnCQuotaProfileFactory(DETAIL_ID, DETAIL_NAME).randomBalanceWithRate();
                quotaNotificationDatas.add(createQuotaNotificationObject(BILLING_CYCLE));
            }

            @Test
            public void totalIsZero() {
                setTotalZero();
                assertNull(factory.createQuotaNotificationScheme(quotaNotificationDatas, qoSProfilesLst, partialFailReasons));
                assertEquals(1, partialFailReasons.size());
                assertThat(partialFailReasons.get(0), containsString("Allowed Usage is zero/undefined/unlimited"));
            }

            private void setTotalZero() {
                rncProfileDetailFactory.withQuotaUsageType(QuotaUsageType.VOLUME)
                        .withVolumeUnitType(VolumeUnitType.TOTAL);
                rncProfileDetailFactory.withBillingCycleUsage(new AllowedUsageBuilderJava().total(CommonConstants.QUOTA_UNDEFINED, DataUnit.BYTE).createBillingCycleAllowedUsage());
                quotaProfile.withHsqQuotaProfileDetail(CommonConstants.ALL_SERVICE_ID, rncProfileDetailFactory.create());
            }

            @Test
            public void downloadIsZero() {
                setDownloadZero();
                assertNull(factory.createQuotaNotificationScheme(quotaNotificationDatas, qoSProfilesLst, partialFailReasons));
                assertEquals(1, partialFailReasons.size());
                assertThat(partialFailReasons.get(0), containsString("Allowed Usage is zero/undefined/unlimited"));
            }

            private void setDownloadZero() {
                rncProfileDetailFactory.withQuotaUsageType(QuotaUsageType.VOLUME)
                        .withVolumeUnitType(VolumeUnitType.DOWNLOAD);
                rncProfileDetailFactory.withBillingCycleUsage(new AllowedUsageBuilderJava().download(CommonConstants.QUOTA_UNDEFINED, DataUnit.BYTE).createBillingCycleAllowedUsage());
                quotaProfile.withHsqQuotaProfileDetail(CommonConstants.ALL_SERVICE_ID, rncProfileDetailFactory.create());
            }

            @Test
            public void uploadIsZero() {
                setUploadZero();
                assertNull(factory.createQuotaNotificationScheme(quotaNotificationDatas, qoSProfilesLst, partialFailReasons));
                assertEquals(1, partialFailReasons.size());
                assertThat(partialFailReasons.get(0), containsString("Allowed Usage is zero/undefined/unlimited"));
            }

            private void setUploadZero() {
                rncProfileDetailFactory.withQuotaUsageType(QuotaUsageType.VOLUME)
                        .withVolumeUnitType(VolumeUnitType.UPLOAD);
                rncProfileDetailFactory.withBillingCycleUsage(new AllowedUsageBuilderJava().upload(CommonConstants.QUOTA_UNDEFINED, DataUnit.BYTE).createBillingCycleAllowedUsage());
                quotaProfile.withHsqQuotaProfileDetail(CommonConstants.ALL_SERVICE_ID, rncProfileDetailFactory.create());
            }

            @Test
            public void timeIsZero() {
                setTimeZero();
                assertNull(factory.createQuotaNotificationScheme(quotaNotificationDatas, qoSProfilesLst, partialFailReasons));
                assertEquals(1, partialFailReasons.size());
                assertThat(partialFailReasons.get(0), containsString("Allowed Usage is zero/undefined/unlimited"));
            }

            private void setTimeZero() {
                rncProfileDetailFactory.withQuotaUsageType(QuotaUsageType.TIME);
                rncProfileDetailFactory.withBillingCycleUsage(new AllowedUsageBuilderJava().time(CommonConstants.QUOTA_UNDEFINED, TimeUnit.SECOND).createBillingCycleAllowedUsage());
                quotaProfile.withHsqQuotaProfileDetail(CommonConstants.ALL_SERVICE_ID, rncProfileDetailFactory.create());
            }
        }

        public class MeteringTypeIsInvalid {

            private RnCQuotaProfileFactory rncProfileDetailFactory;
            private QuotaNotificationData quotaNotificationData;
            @Before
            public void setUp() {
                rncProfileDetailFactory = new RnCQuotaProfileFactory(DETAIL_ID, DETAIL_NAME).randomBalanceWithRate();
                quotaNotificationData = createQuotaNotificationObject(BILLING_CYCLE);
                quotaNotificationData.setEmailTemplateData(createTemplateWithEmail());
                quotaNotificationDatas.add(quotaNotificationData);
            }

            @Test
            public void quotaUsageTypeIsVolumeAndUnitTypeIsNull() {
                VolumeUnitType volumeUnitType = null;
                QuotaUsageType quotaUsageType = QuotaUsageType.VOLUME;
                rncProfileDetailFactory.withQuotaUsageType(quotaUsageType).withVolumeUnitType(volumeUnitType);
                quotaProfile.withHsqQuotaProfileDetail(CommonConstants.ALL_SERVICE_ID, rncProfileDetailFactory.create());

                assertNull(factory.createQuotaNotificationScheme(quotaNotificationDatas, qoSProfilesLst, partialFailReasons));
                assertEquals(1, partialFailReasons.size());
                assertThat(partialFailReasons.get(0), containsString("Invalid Volume Unit Type: " + volumeUnitType + " with Quota Usage Type: " + quotaUsageType));
            }


        }

        @Test
        public void twoConfiguredAndOneEventCreationSucceed() {
            quotaNotificationDatas.add(createQuotaNotificationObject(BILLING_CYCLE));
            quotaNotificationDatas.add(createTemplateWithoutEmailSMSForBaseAddOn());
            RnCQuotaProfileFactory rncProfileDetailFactory = new RnCQuotaProfileFactory(DETAIL_ID, DETAIL_NAME)
                    .randomBalanceWithRate()
                    .withQuotaUsageType(QuotaUsageType.VOLUME)
                    .withVolumeUnitType(VolumeUnitType.TOTAL);
            quotaProfile.withHsqQuotaProfileDetail(CommonConstants.ALL_SERVICE_ID, rncProfileDetailFactory.create());

            assertNotNull(factory.createQuotaNotificationScheme(quotaNotificationDatas, qoSProfilesLst, partialFailReasons));
            assertThat(partialFailReasons.get(0), containsString("Email or SMS template is not configured"));
        }

        @Test
        public void whenServiceConfiguredInNotificationHasNoQuotaDefinedInRncProfile(){
            String anyOtherServiceIdThenAllService = UUID.randomUUID().toString();
            quotaNotificationDatas.add(createQuotaNotificationObject(BILLING_CYCLE,anyOtherServiceIdThenAllService));
            quotaNotificationDatas.add(createQuotaNotificationObject(BILLING_CYCLE));
            RnCQuotaProfileFactory rncProfileDetailFactory = new RnCQuotaProfileFactory(DETAIL_ID, DETAIL_NAME)
                    .randomBalanceWithRate()
                    .withQuotaUsageType(QuotaUsageType.VOLUME)
                    .withVolumeUnitType(VolumeUnitType.TOTAL);

            quotaProfile.withHsqQuotaProfileDetail(CommonConstants.ALL_SERVICE_ID, rncProfileDetailFactory.create());

            assertNotNull(factory.createQuotaNotificationScheme(quotaNotificationDatas, qoSProfilesLst, partialFailReasons));
            assertThat(partialFailReasons.get(0), containsString("RnC Profile Detail not found for service id: "+ anyOtherServiceIdThenAllService));
        }
    }

    private NotificationTemplateData createTemplateWithEmail() {
        NotificationTemplateData emailNotificationData = new NotificationTemplateData();
        emailNotificationData.setTemplateType(NotificationTemplateType.EMAIL);
        return emailNotificationData;
    }
}
