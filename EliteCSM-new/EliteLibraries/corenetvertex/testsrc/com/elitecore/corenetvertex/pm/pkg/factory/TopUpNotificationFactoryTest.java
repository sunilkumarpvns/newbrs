package com.elitecore.corenetvertex.pm.pkg.factory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.elitecore.corenetvertex.constants.AggregationKey;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.DataUnit;
import com.elitecore.corenetvertex.constants.TimeUnit;
import com.elitecore.corenetvertex.constants.UsageType;
import com.elitecore.corenetvertex.pd.notification.TopUpNotificationData;
import com.elitecore.corenetvertex.pkg.notification.NotificationTemplateData;
import com.elitecore.corenetvertex.pkg.notification.NotificationTemplateType;
import com.elitecore.corenetvertex.pkg.rnc.QuotaUsageType;
import com.elitecore.corenetvertex.pkg.rnc.VolumeUnitType;
import com.elitecore.corenetvertex.pm.factory.RnCQuotaProfileFactory;
import com.elitecore.corenetvertex.pm.pkg.PackageFactory;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.AllowedUsageBuilderJava;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfileDetail;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.RncProfileDetail;
import com.elitecore.corenetvertex.pm.pkg.notification.QuotaNotificationScheme;
import com.elitecore.corenetvertex.pm.pkg.notification.QuotaThresholdEvent;
import com.elitecore.corenetvertex.pm.util.MockQuotaProfile;
import com.elitecore.corenetvertex.service.notification.Template;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

@RunWith(HierarchicalContextRunner.class)
public class TopUpNotificationFactoryTest {

    public static final String DETAIL_ID = "id";
    public static final String DETAIL_NAME = "name";
    private PackageFactory packageFactory;
    private QuotaNotificationSchemeFactory factory;
    private MockQuotaProfile quotaProfile;
    private static final String QUOTA_PROFILE_ID = "QUOTA_PROFILE_ID";
    private static final String QUOTA_PROFILE_NAME = "QUOTA_PROFILE_NAME";
    private List<String> partialFailReasons;
    private RncProfileDetail rncProfileDetail;
    private List<TopUpNotificationData> topUpNotificationDatas;
    private int threshold;

    @Before
    public void setUp() {
        packageFactory = new PackageFactory();
        factory = new QuotaNotificationSchemeFactory(packageFactory);
        Map<String, QuotaProfileDetail> serviceToDetailMap = new HashMap<>();
        rncProfileDetail = new RnCQuotaProfileFactory(DETAIL_ID, DETAIL_NAME).randomBalanceWithRate().create();
        serviceToDetailMap.put(CommonConstants.ALL_SERVICE_ID, rncProfileDetail);
        quotaProfile = MockQuotaProfile.create(QUOTA_PROFILE_ID, QUOTA_PROFILE_NAME, serviceToDetailMap);
        partialFailReasons = new ArrayList<>();
        topUpNotificationDatas = new ArrayList<>();
        threshold = RandomUtils.nextInt(1,100);
    }

    public class Create_ReturnNullWhen {

        public class NotificationDataNotFound {

            @Test
            public void create_ReturnNullWhenNotificationDataAreNull() {
                topUpNotificationDatas = null;
                assertNull(factory.createTopUpQuotaNotificationScheme(topUpNotificationDatas, quotaProfile, partialFailReasons));
            }

            @Test
            public void create_ReturnNullWhenNotificationDataAreEmpty() {
                topUpNotificationDatas = new ArrayList<>();
                assertNull(factory.createTopUpQuotaNotificationScheme(topUpNotificationDatas, quotaProfile, partialFailReasons));
            }
        }

        @Test
        public void oneConfiguredAndEventCreationSkipped() {
            TopUpNotificationData topUpNotificationData = new TopUpNotificationData();
            topUpNotificationDatas.add(topUpNotificationData);
            assertNull(factory.createTopUpQuotaNotificationScheme(topUpNotificationDatas, quotaProfile, partialFailReasons));
            assertEquals(1, partialFailReasons.size());
        }

        @Test
        public void twoConfiguredAndBothEventCreationSkipped() {
            topUpNotificationDatas.add(new TopUpNotificationData());
            topUpNotificationDatas.add(new TopUpNotificationData());
            assertNull(factory.createTopUpQuotaNotificationScheme(topUpNotificationDatas, quotaProfile, partialFailReasons));
            assertEquals(2, partialFailReasons.size());
        }

        @Test
        public void quotaProfileIsNull() {
            quotaProfile = null;
            topUpNotificationDatas.add(new TopUpNotificationData());
            assertNull(factory.createTopUpQuotaNotificationScheme(topUpNotificationDatas, quotaProfile, partialFailReasons));
            assertTrue(partialFailReasons.isEmpty());
        }

        @Test
        public void emailAndSMSBothNotificationTemplateNotConfigured() {
            topUpNotificationDatas.add(createTemplateWithoutEmailSMS());
            assertNull(factory.createTopUpQuotaNotificationScheme(topUpNotificationDatas, quotaProfile, partialFailReasons));
            assertEquals(1, partialFailReasons.size());
        }

    }

    private TopUpNotificationData createTemplateWithoutEmailSMS() {
        TopUpNotificationData data = new TopUpNotificationData();
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
            topUpNotificationDatas.add(createTemplateWithEmail());

            rncProfileDetailFactory.withQuotaUsageType(QuotaUsageType.VOLUME)
                    .withVolumeUnitType(VolumeUnitType.TOTAL);

            int configuredQuota = RandomUtils.nextInt(1, 100000);

            rncProfileDetailFactory.withBillingCycleUsage(new AllowedUsageBuilderJava().total(configuredQuota, DataUnit.BYTE).createBillingCycleAllowedUsage());
            rncProfileDetail = rncProfileDetailFactory.create();
            quotaProfile = quotaProfile.withHsqQuotaProfileDetail(CommonConstants.ALL_SERVICE_ID, rncProfileDetail);

            QuotaNotificationScheme actualScheme = factory.createTopUpQuotaNotificationScheme(topUpNotificationDatas, quotaProfile, partialFailReasons);
            assertEquals(1, actualScheme.getQuotaThresgoldEvents().size());
            assertEquals(1, actualScheme.getQuotaThresgoldEvents().get(0).size());

            QuotaThresholdEvent expectedEvent = createExpectedEvent(UsageType.VOLUME, threshold, configuredQuota);
            assertReflectionEquals(expectedEvent, actualScheme.getQuotaThresgoldEvents().get(0).get(0));
        }

        @Test
        public void eventsShouldBeSortedAscendingOrderThreshold() {
            int threshold1 = 70;
            int threshold2 = 80;
            int threshold3 = 40;

            int configuredQuota = RandomUtils.nextInt(1, 10000);

            topUpNotificationDatas.add(createTemplateWithEmailAndThresold(threshold1));
            topUpNotificationDatas.add(createTemplateWithEmailAndThresold(threshold2));
            topUpNotificationDatas.add(createTemplateWithEmailAndThresold(threshold3));
            rncProfileDetailFactory.withQuotaUsageType(QuotaUsageType.VOLUME).withVolumeUnitType(VolumeUnitType.TOTAL);
            rncProfileDetailFactory.withBillingCycleUsage(new AllowedUsageBuilderJava().total(configuredQuota, DataUnit.BYTE).createBillingCycleAllowedUsage());
            rncProfileDetail = rncProfileDetailFactory.create();
            quotaProfile = quotaProfile.withHsqQuotaProfileDetail(CommonConstants.ALL_SERVICE_ID, rncProfileDetail);

            QuotaNotificationScheme actualScheme = factory.createTopUpQuotaNotificationScheme(topUpNotificationDatas, quotaProfile, partialFailReasons);
            assertEquals(1, actualScheme.getQuotaThresgoldEvents().size());
            assertEquals(3, actualScheme.getQuotaThresgoldEvents().get(0).size());

            QuotaThresholdEvent expectedEvent1 = createExpectedEvent(UsageType.VOLUME, threshold1, configuredQuota);
            QuotaThresholdEvent expectedEvent2 = createExpectedEvent(UsageType.VOLUME, threshold2, configuredQuota);
            QuotaThresholdEvent expectedEvent3 = createExpectedEvent(UsageType.VOLUME, threshold3, configuredQuota);
            assertReflectionEquals(expectedEvent2, actualScheme.getQuotaThresgoldEvents().get(0).get(0));
            assertReflectionEquals(expectedEvent1, actualScheme.getQuotaThresgoldEvents().get(0).get(1));
            assertReflectionEquals(expectedEvent3, actualScheme.getQuotaThresgoldEvents().get(0).get(2));
        }

        private TopUpNotificationData createTemplateWithEmailAndThresold(int configuredThresold) {
            TopUpNotificationData data = new TopUpNotificationData();
            data.setThreshold(configuredThresold);
            NotificationTemplateData emailNotificationData = new NotificationTemplateData();
            emailNotificationData.setTemplateType(NotificationTemplateType.EMAIL);
            data.setEmailTemplateData(emailNotificationData);
            return data;
        }
    }

    private QuotaThresholdEvent createExpectedEvent(UsageType usageType, int threshold, int configuredQuota) {
        NotificationTemplateData emailNotificationData = new NotificationTemplateData();
        emailNotificationData.setTemplateType(NotificationTemplateType.EMAIL);

        QuotaThresholdEvent event = new QuotaThresholdEvent(usageType, AggregationKey.BILLING_CYCLE,
                rncProfileDetail.getDataServiceType().getServiceIdentifier(),
                rncProfileDetail.getRatingGroup().getIdentifier(),
                QUOTA_PROFILE_ID,
                configuredQuota - configuredQuota * threshold/100, 0, new Template(emailNotificationData.getId(), emailNotificationData.getName(), emailNotificationData.getSubject(),
                emailNotificationData.getTemplateData()),
                null);
        return event;
    }

    public class PartialReasonShouldBeAddedWhen {

        public class CalculatedThresholdIsLessThanZero {

            private RnCQuotaProfileFactory rncProfileDetailFactory;

            @Before
            public void setUp() {
                rncProfileDetailFactory = new RnCQuotaProfileFactory(DETAIL_ID, DETAIL_NAME).randomBalanceWithRate();
                topUpNotificationDatas.add(createTemplateWithEmail());
            }

            @Test
            public void quotaUsageTypeIsTotalAndTotalIsUndefined() {
                setTotalUndefined();
                assertNull(factory.createTopUpQuotaNotificationScheme(topUpNotificationDatas, quotaProfile, partialFailReasons));
                assertEquals(1, partialFailReasons.size());
                assertThat(partialFailReasons.get(0), containsString("Allowed Usage is zero/undefined/unlimited"));
            }

            private void setTotalUndefined() {
                rncProfileDetailFactory.withQuotaUsageType(QuotaUsageType.VOLUME)
                        .withVolumeUnitType(VolumeUnitType.TOTAL);
                rncProfileDetailFactory.withBillingCycleUsage(new AllowedUsageBuilderJava().total(CommonConstants.QUOTA_UNDEFINED, DataUnit.BYTE).createBillingCycleAllowedUsage());
                quotaProfile = quotaProfile.withHsqQuotaProfileDetail(CommonConstants.ALL_SERVICE_ID, rncProfileDetailFactory.create());
            }

            @Test
            public void quotaUsageTypeIsDownloadAndDownloadIsUndefined() {
                setDownloadUndefined();

                assertNull(factory.createTopUpQuotaNotificationScheme(topUpNotificationDatas, quotaProfile, partialFailReasons));
                assertEquals(1, partialFailReasons.size());
                assertThat(partialFailReasons.get(0), containsString("Allowed Usage is zero/undefined/unlimited"));
            }

            private void setDownloadUndefined() {
                rncProfileDetailFactory.withQuotaUsageType(QuotaUsageType.VOLUME)
                        .withVolumeUnitType(VolumeUnitType.DOWNLOAD);
                rncProfileDetailFactory.withBillingCycleUsage(new AllowedUsageBuilderJava().download(CommonConstants.QUOTA_UNDEFINED, DataUnit.BYTE).createBillingCycleAllowedUsage());
                quotaProfile = quotaProfile.withHsqQuotaProfileDetail(CommonConstants.ALL_SERVICE_ID, rncProfileDetailFactory.create());
            }

            @Test
            public void quotaUsageTypeIsUploadAndUploadIsUndefined() {
                setUploadUndefined();

                assertNull(factory.createTopUpQuotaNotificationScheme(topUpNotificationDatas, quotaProfile, partialFailReasons));
                assertEquals(1, partialFailReasons.size());
                assertThat(partialFailReasons.get(0), containsString("Allowed Usage is zero/undefined/unlimited"));
            }

            private void setUploadUndefined() {
                rncProfileDetailFactory.withQuotaUsageType(QuotaUsageType.VOLUME)
                        .withVolumeUnitType(VolumeUnitType.UPLOAD);
                rncProfileDetailFactory.withBillingCycleUsage(new AllowedUsageBuilderJava().upload(CommonConstants.QUOTA_UNDEFINED, DataUnit.BYTE).createBillingCycleAllowedUsage());
                quotaProfile = quotaProfile.withHsqQuotaProfileDetail(CommonConstants.ALL_SERVICE_ID, rncProfileDetailFactory.create());
            }

            @Test
            public void quotaUsageTypeIsTimeAndTimeIsUndefined() {
                setTimeUndefined();

                assertNull(factory.createTopUpQuotaNotificationScheme(topUpNotificationDatas, quotaProfile, partialFailReasons));
                assertEquals(1, partialFailReasons.size());
                assertThat(partialFailReasons.get(0), containsString("Allowed Usage is zero/undefined/unlimited"));
            }

            private void setTimeUndefined() {
                rncProfileDetailFactory.withQuotaUsageType(QuotaUsageType.TIME);
                rncProfileDetailFactory.withBillingCycleUsage(new AllowedUsageBuilderJava().time(CommonConstants.QUOTA_UNDEFINED, TimeUnit.SECOND).createBillingCycleAllowedUsage());
                quotaProfile = quotaProfile.withHsqQuotaProfileDetail(CommonConstants.ALL_SERVICE_ID, rncProfileDetailFactory.create());
            }
        }

        public class CalculatedThresholdZero {

            private RnCQuotaProfileFactory rncProfileDetailFactory;

            @Before
            public void setUp() {
                rncProfileDetailFactory = new RnCQuotaProfileFactory(DETAIL_ID, DETAIL_NAME).randomBalanceWithRate();
                topUpNotificationDatas.add(createTemplateWithEmail());
            }

            @Test
            public void totalIsZero() {
                setTotalZero();
                assertNull(factory.createTopUpQuotaNotificationScheme(topUpNotificationDatas, quotaProfile, partialFailReasons));
                assertEquals(1, partialFailReasons.size());
                assertThat(partialFailReasons.get(0), containsString("Allowed Usage is zero/undefined/unlimited"));
            }

            private void setTotalZero() {
                rncProfileDetailFactory.withQuotaUsageType(QuotaUsageType.VOLUME)
                        .withVolumeUnitType(VolumeUnitType.TOTAL);
                rncProfileDetailFactory.withBillingCycleUsage(new AllowedUsageBuilderJava().total(CommonConstants.QUOTA_UNDEFINED, DataUnit.BYTE).createBillingCycleAllowedUsage());
                quotaProfile = quotaProfile.withHsqQuotaProfileDetail(CommonConstants.ALL_SERVICE_ID, rncProfileDetailFactory.create());
            }

            @Test
            public void downloadIsZero() {
                setDownloadZero();
                assertNull(factory.createTopUpQuotaNotificationScheme(topUpNotificationDatas, quotaProfile, partialFailReasons));
                assertEquals(1, partialFailReasons.size());
                assertThat(partialFailReasons.get(0), containsString("Allowed Usage is zero/undefined/unlimited"));
            }

            private void setDownloadZero() {
                rncProfileDetailFactory.withQuotaUsageType(QuotaUsageType.VOLUME)
                        .withVolumeUnitType(VolumeUnitType.DOWNLOAD);
                rncProfileDetailFactory.withBillingCycleUsage(new AllowedUsageBuilderJava().download(CommonConstants.QUOTA_UNDEFINED, DataUnit.BYTE).createBillingCycleAllowedUsage());
                quotaProfile = quotaProfile.withHsqQuotaProfileDetail(CommonConstants.ALL_SERVICE_ID, rncProfileDetailFactory.create());
            }

            @Test
            public void uploadIsZero() {
                setUploadZero();
                assertNull(factory.createTopUpQuotaNotificationScheme(topUpNotificationDatas, quotaProfile, partialFailReasons));
                assertEquals(1, partialFailReasons.size());
                assertThat(partialFailReasons.get(0), containsString("Allowed Usage is zero/undefined/unlimited"));
            }

            private void setUploadZero() {
                rncProfileDetailFactory.withQuotaUsageType(QuotaUsageType.VOLUME)
                        .withVolumeUnitType(VolumeUnitType.UPLOAD);
                rncProfileDetailFactory.withBillingCycleUsage(new AllowedUsageBuilderJava().upload(CommonConstants.QUOTA_UNDEFINED, DataUnit.BYTE).createBillingCycleAllowedUsage());
                quotaProfile = quotaProfile.withHsqQuotaProfileDetail(CommonConstants.ALL_SERVICE_ID, rncProfileDetailFactory.create());
            }

            @Test
            public void timeIsZero() {
                setTimeZero();
                assertNull(factory.createTopUpQuotaNotificationScheme(topUpNotificationDatas, quotaProfile, partialFailReasons));
                assertEquals(1, partialFailReasons.size());
                assertThat(partialFailReasons.get(0), containsString("Allowed Usage is zero/undefined/unlimited"));
            }

            private void setTimeZero() {
                rncProfileDetailFactory.withQuotaUsageType(QuotaUsageType.TIME);
                rncProfileDetailFactory.withBillingCycleUsage(new AllowedUsageBuilderJava().time(CommonConstants.QUOTA_UNDEFINED, TimeUnit.SECOND).createBillingCycleAllowedUsage());
                quotaProfile = quotaProfile.withHsqQuotaProfileDetail(CommonConstants.ALL_SERVICE_ID, rncProfileDetailFactory.create());
            }
        }

        public class MeteringTypeIsInvalid {

            private RnCQuotaProfileFactory rncProfileDetailFactory;

            @Before
            public void setUp() {
                rncProfileDetailFactory = new RnCQuotaProfileFactory(DETAIL_ID, DETAIL_NAME).randomBalanceWithRate();
                topUpNotificationDatas.add(createTemplateWithEmail());
            }

            @Test
            public void quotaUsageTypeIsVolumeAndUnitTypeIsNull() {
                VolumeUnitType volumeUnitType = null;
                QuotaUsageType quotaUsageType = QuotaUsageType.VOLUME;
                rncProfileDetailFactory.withQuotaUsageType(quotaUsageType).withVolumeUnitType(volumeUnitType);
                quotaProfile = quotaProfile.withHsqQuotaProfileDetail(CommonConstants.ALL_SERVICE_ID, rncProfileDetailFactory.create());

                assertNull(factory.createTopUpQuotaNotificationScheme(topUpNotificationDatas, quotaProfile, partialFailReasons));
                assertEquals(1, partialFailReasons.size());
                assertThat(partialFailReasons.get(0), containsString("Invalid Volume Unit Type: " + volumeUnitType + " with Quota Usage Type: " + quotaUsageType));
            }


        }

        @Test
        public void twoConfiguredAndOneEventCreationSucceed() {
            topUpNotificationDatas.add(createTemplateWithEmail());
            topUpNotificationDatas.add(createTemplateWithoutEmailSMS());
            RnCQuotaProfileFactory rncProfileDetailFactory = new RnCQuotaProfileFactory(DETAIL_ID, DETAIL_NAME)
                    .randomBalanceWithRate()
                    .withQuotaUsageType(QuotaUsageType.VOLUME)
                    .withVolumeUnitType(VolumeUnitType.TOTAL);
            quotaProfile = quotaProfile.withHsqQuotaProfileDetail(CommonConstants.ALL_SERVICE_ID, rncProfileDetailFactory.create());

            assertNotNull(factory.createTopUpQuotaNotificationScheme(topUpNotificationDatas, quotaProfile, partialFailReasons));
            assertThat(partialFailReasons.get(0), containsString("Email or SMS template is not configured"));
        }
    }

    private TopUpNotificationData createTemplateWithEmail() {
        TopUpNotificationData data = new TopUpNotificationData();
        data.setThreshold(threshold);
        NotificationTemplateData emailNotificationData = new NotificationTemplateData();
        emailNotificationData.setTemplateType(NotificationTemplateType.EMAIL);
        data.setEmailTemplateData(emailNotificationData);
        return data;
    }
}
