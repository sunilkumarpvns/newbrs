package com.elitecore.corenetvertex.spr;

import com.elitecore.commons.base.FixedTimeSource;
import com.elitecore.corenetvertex.constants.AggregationKey;
import com.elitecore.corenetvertex.constants.BalanceLevel;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.CommonStatusValues;
import com.elitecore.corenetvertex.constants.DataUnit;
import com.elitecore.corenetvertex.constants.QuotaProfileType;
import com.elitecore.corenetvertex.constants.RenewalIntervalUnit;
import com.elitecore.corenetvertex.constants.TimeUnit;
import com.elitecore.corenetvertex.core.transaction.TransactionException;
import com.elitecore.corenetvertex.data.CarryForwardStatus;
import com.elitecore.corenetvertex.data.ResetBalanceStatus;
import com.elitecore.corenetvertex.pkg.rnc.QuotaUsageType;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.pm.pkg.DataServiceType;
import com.elitecore.corenetvertex.pm.pkg.RatingGroup;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.AllowedUsage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.BillingCycleAllowedUsage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.DailyAllowedUsage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfile;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfileDetail;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.RncProfileDetail;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.WeeklyAllowedUsage;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import static com.elitecore.corenetvertex.constants.CommonConstants.QUOTA_UNDEFINED;
import static com.elitecore.corenetvertex.constants.CommonConstants.QUOTA_UNLIMITED;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;

public class CarryForwardOperationQuotaWasProvisionedTest {
    private NonMonetoryBalance expiredHSQService1Balance;
    public static final int ZERO_CARRY_FORWARD = 0;

    private NonMonetoryBalance provisionedHSQService1Balance;

    private List<NonMonetoryBalance> nonMonetoryBalanceList;
    private DataBalanceOperation dataBalanceOperation;

    public static final String TEST_PKG_ID = "testPkgId";
    private String quotaProfileId = "TEST_QUOTA_PROFILE";
    private String dummyName = "TEST_QUOTA_PROFILE";

    @Mock
    PolicyRepository policyRepository;
    private CarryForwardOperation carryForwardOperation;

    private long carryForwardVolumeLimit;
    private long carryForwardTimeLimit;
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        carryForwardOperation = new CarryForwardOperation(policyRepository, new FixedTimeSource(getTime(13, 0)));

        Random random = new Random();
        int hsqService1Balance = 10*60*60; //10 MB

        long billingCycleResetTime = getTime(12, 0);
        long billingCycleResetTimeForNextMonth = getTime(12,1);
        String renewalInterval = "0:TILL_BILL_DATE";

        expiredHSQService1Balance = createServiceRGNonMonitoryBalances(UUID.randomUUID().toString(), billingCycleResetTime, "SUBSCRIBER_1",
                ResetBalanceStatus.RESET_DONE, hsqService1Balance,random.nextInt(hsqService1Balance), CommonConstants.QUOTA_UNDEFINED, CommonConstants.QUOTA_UNDEFINED, renewalInterval, null, 0, 1);

        provisionedHSQService1Balance = createServiceRGNonMonitoryBalances(UUID.randomUUID().toString(), billingCycleResetTimeForNextMonth, "SUBSCRIBER_1",
                ResetBalanceStatus.RESET, hsqService1Balance, hsqService1Balance, CommonConstants.QUOTA_UNDEFINED, CommonConstants.QUOTA_UNDEFINED, renewalInterval, null, 0, 1);

        nonMonetoryBalanceList = new ArrayList<>();
        nonMonetoryBalanceList.add(provisionedHSQService1Balance);
        dataBalanceOperation = new DataBalanceOperation();

        carryForwardVolumeLimit = QUOTA_UNLIMITED;
        carryForwardTimeLimit = QUOTA_UNDEFINED;
    }

    @Test
    public void balanceInsertedToUpdateObject() throws OperationFailedException, TransactionException, SQLException {
        QuotaProfile quotaProfile = getQuotaProfile(QuotaUsageType.HYBRID, BalanceLevel.FUP1, carryForwardVolumeLimit, carryForwardTimeLimit);
        doReturn(quotaProfile).when(policyRepository).getQuotaProfile(anyString(),anyString());

        carryForwardOperation.performCarryForwardOperation(expiredHSQService1Balance, dataBalanceOperation, nonMonetoryBalanceList);

        Assert.assertEquals(provisionedHSQService1Balance.getCarryForwardVolume(), expiredHSQService1Balance.getBillingCycleAvailableVolume());
        Assert.assertEquals(provisionedHSQService1Balance.getBillingCycleAvailableVolume(),
                expiredHSQService1Balance.getBillingCycleAvailableVolume() + provisionedHSQService1Balance.getBillingCycleTotalVolume());

        Assert.assertTrue(dataBalanceOperation.getNonMonetaryBalanceUpdateOperation().contains(provisionedHSQService1Balance));
    }

    private long getTime(int day, int month) {
        return getTime(day, month, 2018, 23, 59, 59, 999);
    }

    private long getTime(int day, int month, int year, int hour, int minutes, int seconds, int milliSeconds) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minutes);
        calendar.set(Calendar.SECOND, seconds);
        calendar.set(Calendar.MILLISECOND, milliSeconds);
        calendar.set(Calendar.YEAR, year);
        return calendar.getTimeInMillis();
    }

    private NonMonetoryBalance createServiceRGNonMonitoryBalances(String id, long billingCycleResetTime, String subscriberIdentity, ResetBalanceStatus status,
                                                                  long billingCycleTotalVolume, long billingCycleAvailableVolume,
                                                                  long billingCycleTotalTime, long billingCycleAvailableTime,
                                                                  String renewalInterval, String subscriptionId, int level, int serviceId) {
        Random random = new Random();
        NonMonetoryBalance serviceRgNonMonitoryBalance = new NonMonetoryBalance.NonMonetaryBalanceBuilder(id,
                RandomUtils.nextInt(0, Integer.MAX_VALUE),
                TEST_PKG_ID,
                1l,
                subscriberIdentity,
                subscriptionId,
                random.nextInt(1),
                quotaProfileId,
                status,
                renewalInterval,
                null)
                .withBillingCycleVolumeBalance(billingCycleTotalVolume, billingCycleAvailableVolume)
                .withBillingCycleTimeBalance(billingCycleTotalTime, billingCycleAvailableTime)
                .withStartTime(getTime(13, 0))
                .withBillingCycleResetTime(billingCycleResetTime)
                .withCarryForwardStatus(CarryForwardStatus.CARRY_FORWARD)
                .withLevel(level)
                .withServiceId(serviceId)
                .build();
        return serviceRgNonMonitoryBalance;
    }

    private QuotaProfile getQuotaProfile(QuotaUsageType quotaUsageType, BalanceLevel balanceLevel, long carryForwardVolumeLimit, long carryForwardTimeLimit) {
        String pccProfileName = "pccProfileName";

        List<Map<String, QuotaProfileDetail>> fupLevelserviceWiseQuotaProfileDetais= new ArrayList<>();

        RatingGroup rg = new RatingGroup("RATING_GROUP_1", "RATING_GROUP_1", "RATING_GROUP_1", 0);

        Map<String, QuotaProfileDetail > hsq = new HashMap<>();

        fupLevelserviceWiseQuotaProfileDetais.add(hsq);

        DataServiceType dataServiceType1 = new DataServiceType("DATA_SERVICE_TYPE_1", "Rg1", 1, Collections.emptyList(), Arrays.asList(rg));
        DataServiceType dataServiceType4 = new DataServiceType("DATA_SERVICE_TYPE_4", "Rg1", 4, Collections.emptyList(), Arrays.asList(rg));

        Map<AggregationKey, AllowedUsage> allowedUsageMap = getAllowedUsage();

        hsq.put("DATA_SERVICE_TYPE_1", new RncProfileDetail("hsq_Quota_"+dummyName, dataServiceType1, 0, rg, allowedUsageMap, 0, 0, 0, 0, DataUnit.BYTE.name(), TimeUnit.SECOND.name(), 0.0, null,
                quotaUsageType,null, quotaProfileId, true, pccProfileName, carryForwardVolumeLimit, carryForwardTimeLimit, "test"));

        QuotaProfile quotaProfile = new QuotaProfile("Quota_"+dummyName, dummyName,
                quotaProfileId, balanceLevel,2, RenewalIntervalUnit.MONTH, QuotaProfileType.RnC_BASED,
                fupLevelserviceWiseQuotaProfileDetais, CommonStatusValues.DISABLE.isBooleanValue(),CommonStatusValues.ENABLE.isBooleanValue());

        return quotaProfile;
    }

    private Map<AggregationKey, AllowedUsage> getAllowedUsage(){
        Map<AggregationKey, AllowedUsage> allowedUsageMap = new HashMap<>();

        AllowedUsage daily = new DailyAllowedUsage(1024*30,512*30,512*30,7200*30, DataUnit.MB, DataUnit.MB, DataUnit.MB, TimeUnit.SECOND);
        AllowedUsage weekly = new WeeklyAllowedUsage(1024*7,512*7,512*7,7200*7, DataUnit.MB, DataUnit.MB, DataUnit.MB, TimeUnit.SECOND);
        AllowedUsage billingCycle = new BillingCycleAllowedUsage(1024,512,512,7200, DataUnit.MB, DataUnit.MB, DataUnit.MB, TimeUnit.SECOND);

        allowedUsageMap.put(AggregationKey.DAILY, daily);
        allowedUsageMap.put(AggregationKey.WEEKLY, weekly);
        allowedUsageMap.put(AggregationKey.BILLING_CYCLE, billingCycle);

        return  allowedUsageMap;
    }
}
