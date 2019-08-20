package com.elitecore.corenetvertex.constants;

import com.elitecore.commons.tests.PrintMethodRule;
import com.elitecore.corenetvertex.pkg.quota.QuotaProfileDetailData;
import com.elitecore.corenetvertex.pkg.rnc.RncProfileData;
import com.elitecore.corenetvertex.pkg.rnc.RncProfileDetailData;
import com.elitecore.corenetvertex.pkg.rnc.RncProfileDetailDataFactory;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.AllowedUsage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.BillingCycleAllowedUsage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.DailyAllowedUsage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.WeeklyAllowedUsage;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.apache.commons.lang.math.RandomUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.unitils.reflectionassert.ReflectionAssert;

import static org.junit.Assert.assertEquals;

@RunWith(HierarchicalContextRunner.class)
public class AggregationKeyTest {

    private QuotaProfileDetailData inputData;

    private RncProfileDetailData rncProfileDetailData;
    @Rule
    public PrintMethodRule printMethod = new PrintMethodRule();

    private RncProfileData rncProfileData;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        inputData = new QuotaProfileDetailData();
        inputData.setTotal(RandomUtils.nextLong());
        inputData.setTotalUnit(DataUnit.KB.name());
        inputData.setDownload(RandomUtils.nextLong());
        inputData.setDownloadUnit(DataUnit.MB.name());
        inputData.setUpload(RandomUtils.nextLong());
        inputData.setUploadUnit(DataUnit.KB.name());
        inputData.setTime(RandomUtils.nextLong());
        inputData.setTimeUnit(TimeUnit.MINUTE.name());

        rncProfileData = new RncProfileData();
        this.rncProfileDetailData = RncProfileDetailDataFactory.create();
        this.rncProfileDetailData.setRncProfileData(rncProfileData);
    }

    private void assertAllowedUsage(AllowedUsage allowedUsage) {
        assertEquals(inputData.getTotal().longValue(), allowedUsage.getTotal());
        assertEquals(DataUnit.KB, allowedUsage.getTotalUnit());
        assertEquals(inputData.getDownload().longValue(), allowedUsage.getDownload());
        assertEquals(DataUnit.MB, allowedUsage.getDownloadUnit());
        assertEquals(inputData.getUpload().longValue(), allowedUsage.getUpload());
        assertEquals(DataUnit.KB, allowedUsage.getUploadUnit());
        assertEquals(inputData.getTime().longValue(), allowedUsage.getTime());
        assertEquals(TimeUnit.MINUTE, allowedUsage.getTimeUnit());
    }

    public class BILLING_CYCLE {
        AggregationKey aggregationKey;
        @Before
        public void setUp() {
            aggregationKey = AggregationKey.BILLING_CYCLE;
        }

        @Test
        public void createAllowedUsage_shouldCreateAllowedUsageWithProvidedConfiguration() {
            AllowedUsage allowedUsage = aggregationKey.createAllowedUsage(inputData);
            assertAllowedUsage(allowedUsage);
        }

        public class QuotaTypeIsHybrid {
            @Before
            public void setUp() {
                rncProfileData.setQuotaType("HYBRID");
            }

            @Test
            public void createAllowedUsageWithDownloadUnitOfRnCQuotaProfileDetailDataWhenUnitTypeIsDownload() {
                rncProfileData.setUnitType("DOWNLOAD");
                AllowedUsage allowedUsage = aggregationKey.createAllowedUsage(rncProfileDetailData);

                BillingCycleAllowedUsage expectedObject = new BillingCycleAllowedUsage(CommonConstants.QUOTA_UNDEFINED,
                        rncProfileDetailData.getBalance(), CommonConstants.QUOTA_UNDEFINED, rncProfileDetailData.getTimeBalance(),
                        DataUnit.valueOf(rncProfileDetailData.getBalanceUnit()), DataUnit.valueOf(rncProfileDetailData.getBalanceUnit()),
                        DataUnit.valueOf(rncProfileDetailData.getBalanceUnit()), TimeUnit.fromVal(rncProfileDetailData.getTimeBalanceUnit()));

                ReflectionAssert.assertReflectionEquals(expectedObject, allowedUsage);
            }

            @Test
            public void createAllowedUsageWithUploadUnitOfRnCQuotaProfileDetailDataWhenUnitTypeIsUpload() {
                rncProfileData.setUnitType("UPLOAD");
                AllowedUsage allowedUsage = AggregationKey.BILLING_CYCLE.createAllowedUsage(rncProfileDetailData);

                BillingCycleAllowedUsage expectedObject = new BillingCycleAllowedUsage(CommonConstants.QUOTA_UNDEFINED,
                        CommonConstants.QUOTA_UNDEFINED, rncProfileDetailData.getBalance(), rncProfileDetailData.getTimeBalance(),
                        DataUnit.valueOf(rncProfileDetailData.getBalanceUnit()), DataUnit.valueOf(rncProfileDetailData.getBalanceUnit()),
                        DataUnit.valueOf(rncProfileDetailData.getBalanceUnit()), TimeUnit.fromVal(rncProfileDetailData.getTimeBalanceUnit()));

                ReflectionAssert.assertReflectionEquals(expectedObject, allowedUsage);
            }

            @Test
            public void createAllowedUsageWithTotalUnitOfRnCQuotaProfileDetailDataWhenUnitTypeIsTotal() {
                rncProfileData.setUnitType("TOTAL");
                AllowedUsage allowedUsage = AggregationKey.BILLING_CYCLE.createAllowedUsage(rncProfileDetailData);

                BillingCycleAllowedUsage expectedObject = new BillingCycleAllowedUsage(rncProfileDetailData.getBalance(), CommonConstants.QUOTA_UNDEFINED,
                        CommonConstants.QUOTA_UNDEFINED, rncProfileDetailData.getTimeBalance(),
                        DataUnit.valueOf(rncProfileDetailData.getBalanceUnit()), DataUnit.valueOf(rncProfileDetailData.getBalanceUnit()),
                        DataUnit.valueOf(rncProfileDetailData.getBalanceUnit()), TimeUnit.fromVal(rncProfileDetailData.getTimeBalanceUnit()));

                ReflectionAssert.assertReflectionEquals(expectedObject, allowedUsage);
            }

        }

        public class QuotaTypeIsVolume {
            @Before
            public void setUp() {
                rncProfileData.setQuotaType("VOLUME");
            }

            @Test
            public void createAllowedUsageWithDownloadUnitOfRnCQuotaProfileDetailDataWhenUnitTypeIsDownload() {
                rncProfileData.setUnitType("DOWNLOAD");
                AllowedUsage allowedUsage = aggregationKey.createAllowedUsage(rncProfileDetailData);

                BillingCycleAllowedUsage expectedObject = new BillingCycleAllowedUsage(CommonConstants.QUOTA_UNDEFINED,
                        rncProfileDetailData.getBalance(), CommonConstants.QUOTA_UNDEFINED, CommonConstants.QUOTA_UNDEFINED,
                        DataUnit.valueOf(rncProfileDetailData.getBalanceUnit()), DataUnit.valueOf(rncProfileDetailData.getBalanceUnit()),
                        DataUnit.valueOf(rncProfileDetailData.getBalanceUnit()), TimeUnit.fromVal(rncProfileDetailData.getTimeBalanceUnit()));

                ReflectionAssert.assertReflectionEquals(expectedObject, allowedUsage);
            }

            @Test
            public void createAllowedUsageWithUploadUnitOfRnCQuotaProfileDetailDataWhenUnitTypeIsUpload() {
                rncProfileData.setUnitType("UPLOAD");
                AllowedUsage allowedUsage = aggregationKey.createAllowedUsage(rncProfileDetailData);

                BillingCycleAllowedUsage expectedObject = new BillingCycleAllowedUsage(CommonConstants.QUOTA_UNDEFINED,
                        CommonConstants.QUOTA_UNDEFINED, rncProfileDetailData.getBalance(), CommonConstants.QUOTA_UNDEFINED,
                        DataUnit.valueOf(rncProfileDetailData.getBalanceUnit()), DataUnit.valueOf(rncProfileDetailData.getBalanceUnit()),
                        DataUnit.valueOf(rncProfileDetailData.getBalanceUnit()), TimeUnit.fromVal(rncProfileDetailData.getTimeBalanceUnit()));

                ReflectionAssert.assertReflectionEquals(expectedObject, allowedUsage);
            }

            @Test
            public void createAllowedUsageWithTotalUnitOfRnCQuotaProfileDetailDataWhenUnitTypeIsTotal() {
                rncProfileData.setUnitType("TOTAL");
                AllowedUsage allowedUsage = aggregationKey.createAllowedUsage(rncProfileDetailData);

                BillingCycleAllowedUsage expectedObject = new BillingCycleAllowedUsage(rncProfileDetailData.getBalance(), CommonConstants.QUOTA_UNDEFINED,
                        CommonConstants.QUOTA_UNDEFINED, CommonConstants.QUOTA_UNDEFINED,
                        DataUnit.valueOf(rncProfileDetailData.getBalanceUnit()), DataUnit.valueOf(rncProfileDetailData.getBalanceUnit()),
                        DataUnit.valueOf(rncProfileDetailData.getBalanceUnit()), TimeUnit.fromVal(rncProfileDetailData.getTimeBalanceUnit()));

                ReflectionAssert.assertReflectionEquals(expectedObject, allowedUsage);
            }
        }

        public class QuotaTypeIsTime {
            @Before
            public void setUp() {
                rncProfileData.setQuotaType("TIME");
            }

            @Test
            public void createAllowedUsageOfRnCQuotaProfileDetailData() {
                AllowedUsage allowedUsage = aggregationKey.createAllowedUsage(rncProfileDetailData);

                BillingCycleAllowedUsage expectedObject = new BillingCycleAllowedUsage(CommonConstants.QUOTA_UNDEFINED,
                        CommonConstants.QUOTA_UNDEFINED, CommonConstants.QUOTA_UNDEFINED, rncProfileDetailData.getTimeBalance(),
                        DataUnit.valueOf(rncProfileDetailData.getBalanceUnit()), DataUnit.valueOf(rncProfileDetailData.getBalanceUnit()),
                        DataUnit.valueOf(rncProfileDetailData.getBalanceUnit()), TimeUnit.fromVal(rncProfileDetailData.getTimeBalanceUnit()));

                ReflectionAssert.assertReflectionEquals(expectedObject, allowedUsage);
            }
        }
    }


    public class CUSTOM {
        @Test
        public void createAllowedUsage_shouldCreateAllowedUsageWithProvidedConfiguration() {
            AllowedUsage allowedUsage = AggregationKey.CUSTOM.createAllowedUsage(inputData);
            assertAllowedUsage(allowedUsage);
        }
    }

    public class WEEKLY {
        AggregationKey aggregationKey;
        @Before
        public void setUp() {
            aggregationKey = AggregationKey.WEEKLY;
        }

        @Test
        public void createAllowedUsage_shouldCreateAllowedUsageWithProvidedConfiguration() {
            AllowedUsage allowedUsage = aggregationKey.createAllowedUsage(inputData);
            assertAllowedUsage(allowedUsage);
        }
        public class QuotaTypeIsHybrid {
            @Before
            public void setUp() {
                rncProfileData.setQuotaType("HYBRID");
            }

            @Test
            public void createAllowedUsageWithDownloadUnitOfRnCQuotaProfileDetailDataWhenUnitTypeIsDownload() {
                rncProfileData.setUnitType("DOWNLOAD");
                AllowedUsage allowedUsage = aggregationKey.createAllowedUsage(rncProfileDetailData);

                WeeklyAllowedUsage expectedObject = new WeeklyAllowedUsage(CommonConstants.QUOTA_UNDEFINED,
                        rncProfileDetailData.getWeeklyUsageLimit(), CommonConstants.QUOTA_UNDEFINED, rncProfileDetailData.getWeeklyTimeLimit(),
                        DataUnit.valueOf(rncProfileDetailData.getBalanceUnit()), DataUnit.valueOf(rncProfileDetailData.getBalanceUnit()),
                        DataUnit.valueOf(rncProfileDetailData.getBalanceUnit()), TimeUnit.fromVal(rncProfileDetailData.getTimeBalanceUnit()));

                ReflectionAssert.assertReflectionEquals(expectedObject, allowedUsage);
            }

            @Test
            public void createAllowedUsageWithUploadUnitOfRnCQuotaProfileDetailDataWhenUnitTypeIsUpload() {
                rncProfileData.setUnitType("UPLOAD");
                AllowedUsage allowedUsage = aggregationKey.createAllowedUsage(rncProfileDetailData);

                WeeklyAllowedUsage expectedObject = new WeeklyAllowedUsage(CommonConstants.QUOTA_UNDEFINED,
                        CommonConstants.QUOTA_UNDEFINED, rncProfileDetailData.getWeeklyUsageLimit(), rncProfileDetailData.getWeeklyTimeLimit(),
                        DataUnit.valueOf(rncProfileDetailData.getBalanceUnit()), DataUnit.valueOf(rncProfileDetailData.getBalanceUnit()),
                        DataUnit.valueOf(rncProfileDetailData.getBalanceUnit()), TimeUnit.fromVal(rncProfileDetailData.getTimeBalanceUnit()));

                ReflectionAssert.assertReflectionEquals(expectedObject, allowedUsage);
            }

            @Test
            public void createAllowedUsageWithTotalUnitOfRnCQuotaProfileDetailDataWhenUnitTypeIsTotal() {
                rncProfileData.setUnitType("TOTAL");
                AllowedUsage allowedUsage = aggregationKey.createAllowedUsage(rncProfileDetailData);

                WeeklyAllowedUsage expectedObject = new WeeklyAllowedUsage(rncProfileDetailData.getWeeklyTimeLimit(), CommonConstants.QUOTA_UNDEFINED,
                        CommonConstants.QUOTA_UNDEFINED, rncProfileDetailData.getWeeklyTimeLimit(),
                        DataUnit.valueOf(rncProfileDetailData.getBalanceUnit()), DataUnit.valueOf(rncProfileDetailData.getBalanceUnit()),
                        DataUnit.valueOf(rncProfileDetailData.getBalanceUnit()), TimeUnit.fromVal(rncProfileDetailData.getTimeBalanceUnit()));

                ReflectionAssert.assertReflectionEquals(expectedObject, allowedUsage);
            }

        }

        public class QuotaTypeIsVolume {
            @Before
            public void setUp() {
                rncProfileData.setQuotaType("VOLUME");
            }

            @Test
            public void createAllowedUsageWithDownloadUnitOfRnCQuotaProfileDetailDataWhenUnitTypeIsDownload() {
                rncProfileData.setUnitType("DOWNLOAD");
                AllowedUsage allowedUsage = aggregationKey.createAllowedUsage(rncProfileDetailData);

                WeeklyAllowedUsage expectedObject = new WeeklyAllowedUsage(CommonConstants.QUOTA_UNDEFINED,
                        rncProfileDetailData.getWeeklyUsageLimit(), CommonConstants.QUOTA_UNDEFINED, CommonConstants.QUOTA_UNDEFINED,
                        DataUnit.valueOf(rncProfileDetailData.getBalanceUnit()), DataUnit.valueOf(rncProfileDetailData.getBalanceUnit()),
                        DataUnit.valueOf(rncProfileDetailData.getBalanceUnit()), TimeUnit.fromVal(rncProfileDetailData.getTimeBalanceUnit()));

                ReflectionAssert.assertReflectionEquals(expectedObject, allowedUsage);
            }

            @Test
            public void createAllowedUsageWithUploadUnitOfRnCQuotaProfileDetailDataWhenUnitTypeIsUpload() {
                rncProfileData.setUnitType("UPLOAD");
                AllowedUsage allowedUsage = aggregationKey.createAllowedUsage(rncProfileDetailData);

                WeeklyAllowedUsage expectedObject = new WeeklyAllowedUsage(CommonConstants.QUOTA_UNDEFINED,
                        CommonConstants.QUOTA_UNDEFINED, rncProfileDetailData.getWeeklyUsageLimit(), CommonConstants.QUOTA_UNDEFINED,
                        DataUnit.valueOf(rncProfileDetailData.getBalanceUnit()), DataUnit.valueOf(rncProfileDetailData.getBalanceUnit()),
                        DataUnit.valueOf(rncProfileDetailData.getBalanceUnit()), TimeUnit.fromVal(rncProfileDetailData.getTimeBalanceUnit()));

                ReflectionAssert.assertReflectionEquals(expectedObject, allowedUsage);
            }

            @Test
            public void createAllowedUsageWithTotalUnitOfRnCQuotaProfileDetailDataWhenUnitTypeIsTotal() {
                rncProfileData.setUnitType("TOTAL");
                AllowedUsage allowedUsage = aggregationKey.createAllowedUsage(rncProfileDetailData);

                WeeklyAllowedUsage expectedObject = new WeeklyAllowedUsage(rncProfileDetailData.getWeeklyUsageLimit(), CommonConstants.QUOTA_UNDEFINED,
                        CommonConstants.QUOTA_UNDEFINED, CommonConstants.QUOTA_UNDEFINED,
                        DataUnit.valueOf(rncProfileDetailData.getBalanceUnit()), DataUnit.valueOf(rncProfileDetailData.getBalanceUnit()),
                        DataUnit.valueOf(rncProfileDetailData.getBalanceUnit()), TimeUnit.fromVal(rncProfileDetailData.getTimeBalanceUnit()));

                ReflectionAssert.assertReflectionEquals(expectedObject, allowedUsage);
            }
        }

        public class QuotaTypeIsTime {
            @Before
            public void setUp() {
                rncProfileData.setQuotaType("TIME");
            }

            @Test
            public void createAllowedUsageOfRnCQuotaProfileDetailData() {
                AllowedUsage allowedUsage = aggregationKey.createAllowedUsage(rncProfileDetailData);

                WeeklyAllowedUsage expectedObject = new WeeklyAllowedUsage(CommonConstants.QUOTA_UNDEFINED,
                        CommonConstants.QUOTA_UNDEFINED, CommonConstants.QUOTA_UNDEFINED, rncProfileDetailData.getWeeklyTimeLimit(),
                        DataUnit.valueOf(rncProfileDetailData.getBalanceUnit()), DataUnit.valueOf(rncProfileDetailData.getBalanceUnit()),
                        DataUnit.valueOf(rncProfileDetailData.getBalanceUnit()), TimeUnit.fromVal(rncProfileDetailData.getTimeBalanceUnit()));

                ReflectionAssert.assertReflectionEquals(expectedObject, allowedUsage);
            }
        }
    }

    public class DAILY {
        AggregationKey aggregationKey;
        @Before
        public void setUp() {
            aggregationKey = AggregationKey.DAILY;
        }

        @Test
        public void createAllowedUsage_shouldCreateAllowedUsageWithProvidedConfigurationOfQuotaProfileDetailData() {
            AllowedUsage allowedUsage = aggregationKey.createAllowedUsage(inputData);
            assertAllowedUsage(allowedUsage);
        }

        public class QuotaTypeIsHybrid {
            @Before
            public void setUp() {
                rncProfileData.setQuotaType("HYBRID");
            }

            @Test
            public void createAllowedUsageWithDownloadUnitOfRnCQuotaProfileDetailDataWhenUnitTypeIsDownload() {
                rncProfileData.setUnitType("DOWNLOAD");
                AllowedUsage allowedUsage = aggregationKey.createAllowedUsage(rncProfileDetailData);

                DailyAllowedUsage expectedObject = new DailyAllowedUsage(CommonConstants.QUOTA_UNDEFINED,
                        rncProfileDetailData.getDailyUsageLimit(), CommonConstants.QUOTA_UNDEFINED, rncProfileDetailData.getDailyTimeLimit(),
                        DataUnit.valueOf(rncProfileDetailData.getBalanceUnit()), DataUnit.valueOf(rncProfileDetailData.getBalanceUnit()),
                        DataUnit.valueOf(rncProfileDetailData.getBalanceUnit()), TimeUnit.fromVal(rncProfileDetailData.getTimeBalanceUnit()));

                ReflectionAssert.assertReflectionEquals(expectedObject, allowedUsage);
            }

            @Test
            public void createAllowedUsageWithUploadUnitOfRnCQuotaProfileDetailDataWhenUnitTypeIsUpload() {
                rncProfileData.setUnitType("UPLOAD");
                AllowedUsage allowedUsage = aggregationKey.createAllowedUsage(rncProfileDetailData);

                DailyAllowedUsage expectedObject = new DailyAllowedUsage(CommonConstants.QUOTA_UNDEFINED,
                        CommonConstants.QUOTA_UNDEFINED, rncProfileDetailData.getDailyUsageLimit(), rncProfileDetailData.getDailyTimeLimit(),
                        DataUnit.valueOf(rncProfileDetailData.getBalanceUnit()), DataUnit.valueOf(rncProfileDetailData.getBalanceUnit()),
                        DataUnit.valueOf(rncProfileDetailData.getBalanceUnit()), TimeUnit.fromVal(rncProfileDetailData.getTimeBalanceUnit()));

                ReflectionAssert.assertReflectionEquals(expectedObject, allowedUsage);
            }

            @Test
            public void createAllowedUsageWithTotalUnitOfRnCQuotaProfileDetailDataWhenUnitTypeIsTotal() {
                rncProfileData.setUnitType("TOTAL");
                AllowedUsage allowedUsage = aggregationKey.createAllowedUsage(rncProfileDetailData);

                DailyAllowedUsage expectedObject = new DailyAllowedUsage(rncProfileDetailData.getDailyUsageLimit(), CommonConstants.QUOTA_UNDEFINED,
                        CommonConstants.QUOTA_UNDEFINED, rncProfileDetailData.getDailyTimeLimit(),
                        DataUnit.valueOf(rncProfileDetailData.getBalanceUnit()), DataUnit.valueOf(rncProfileDetailData.getBalanceUnit()),
                        DataUnit.valueOf(rncProfileDetailData.getBalanceUnit()), TimeUnit.fromVal(rncProfileDetailData.getTimeBalanceUnit()));

                ReflectionAssert.assertReflectionEquals(expectedObject, allowedUsage);
            }

        }

        public class QuotaTypeIsVolume {
            @Before
            public void setUp() {
                rncProfileData.setQuotaType("VOLUME");
            }

            @Test
            public void createAllowedUsageWithDownloadUnitOfRnCQuotaProfileDetailDataWhenUnitTypeIsDownload() {
                rncProfileData.setUnitType("DOWNLOAD");
                AllowedUsage allowedUsage = aggregationKey.createAllowedUsage(rncProfileDetailData);

                DailyAllowedUsage expectedObject = new DailyAllowedUsage(CommonConstants.QUOTA_UNDEFINED,
                        rncProfileDetailData.getDailyUsageLimit(), CommonConstants.QUOTA_UNDEFINED, CommonConstants.QUOTA_UNDEFINED,
                        DataUnit.valueOf(rncProfileDetailData.getBalanceUnit()), DataUnit.valueOf(rncProfileDetailData.getBalanceUnit()),
                        DataUnit.valueOf(rncProfileDetailData.getBalanceUnit()), TimeUnit.fromVal(rncProfileDetailData.getTimeBalanceUnit()));

                ReflectionAssert.assertReflectionEquals(expectedObject, allowedUsage);
            }

            @Test
            public void createAllowedUsageWithUploadUnitOfRnCQuotaProfileDetailDataWhenUnitTypeIsUpload() {
                rncProfileData.setUnitType("UPLOAD");
                AllowedUsage allowedUsage = aggregationKey.createAllowedUsage(rncProfileDetailData);

                DailyAllowedUsage expectedObject = new DailyAllowedUsage(CommonConstants.QUOTA_UNDEFINED,
                        CommonConstants.QUOTA_UNDEFINED, rncProfileDetailData.getDailyUsageLimit(), CommonConstants.QUOTA_UNDEFINED,
                        DataUnit.valueOf(rncProfileDetailData.getBalanceUnit()), DataUnit.valueOf(rncProfileDetailData.getBalanceUnit()),
                        DataUnit.valueOf(rncProfileDetailData.getBalanceUnit()), TimeUnit.fromVal(rncProfileDetailData.getTimeBalanceUnit()));

                ReflectionAssert.assertReflectionEquals(expectedObject, allowedUsage);
            }

            @Test
            public void createAllowedUsageWithTotalUnitOfRnCQuotaProfileDetailDataWhenUnitTypeIsTotal() {
                rncProfileData.setUnitType("TOTAL");
                AllowedUsage allowedUsage = aggregationKey.createAllowedUsage(rncProfileDetailData);

                DailyAllowedUsage expectedObject = new DailyAllowedUsage(rncProfileDetailData.getDailyUsageLimit(), CommonConstants.QUOTA_UNDEFINED,
                        CommonConstants.QUOTA_UNDEFINED, CommonConstants.QUOTA_UNDEFINED,
                        DataUnit.valueOf(rncProfileDetailData.getBalanceUnit()), DataUnit.valueOf(rncProfileDetailData.getBalanceUnit()),
                        DataUnit.valueOf(rncProfileDetailData.getBalanceUnit()), TimeUnit.fromVal(rncProfileDetailData.getTimeBalanceUnit()));

                ReflectionAssert.assertReflectionEquals(expectedObject, allowedUsage);
            }
        }

        public class QuotaTypeIsTime {
            @Before
            public void setUp() {
                rncProfileData.setQuotaType("TIME");
            }

            @Test
            public void createAllowedUsageOfRnCQuotaProfileDetailData() {
                AllowedUsage allowedUsage = aggregationKey.createAllowedUsage(rncProfileDetailData);

                DailyAllowedUsage expectedObject = new DailyAllowedUsage(CommonConstants.QUOTA_UNDEFINED,
                        CommonConstants.QUOTA_UNDEFINED, CommonConstants.QUOTA_UNDEFINED, rncProfileDetailData.getDailyTimeLimit(),
                        DataUnit.valueOf(rncProfileDetailData.getBalanceUnit()), DataUnit.valueOf(rncProfileDetailData.getBalanceUnit()),
                        DataUnit.valueOf(rncProfileDetailData.getBalanceUnit()), TimeUnit.fromVal(rncProfileDetailData.getTimeBalanceUnit()));

                ReflectionAssert.assertReflectionEquals(expectedObject, allowedUsage);
            }
        }
    }
}