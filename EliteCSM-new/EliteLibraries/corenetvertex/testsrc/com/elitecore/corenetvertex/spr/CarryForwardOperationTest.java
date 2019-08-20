package com.elitecore.corenetvertex.spr;

import com.elitecore.commons.base.FixedTimeSource;
import com.elitecore.corenetvertex.constants.AggregationKey;
import com.elitecore.corenetvertex.constants.BalanceLevel;
import com.elitecore.corenetvertex.constants.CommonStatusValues;
import com.elitecore.corenetvertex.constants.DataUnit;
import com.elitecore.corenetvertex.constants.QuotaProfileType;
import com.elitecore.corenetvertex.constants.RenewalIntervalUnit;
import com.elitecore.corenetvertex.constants.TimeUnit;
import com.elitecore.corenetvertex.core.transaction.TransactionException;
import com.elitecore.corenetvertex.data.CarryForwardStatus;
import com.elitecore.corenetvertex.data.ResetBalanceStatus;
import com.elitecore.corenetvertex.pm.DummyPolicyRepository;
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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;

@RunWith(Suite.class)
@SuiteClasses(
        {CarryForwardOperationQuotaWasProvisionedTest.class,
        CarryForwardOperationQuotaNotProvisionedTest.class}
)
public class CarryForwardOperationTest {

    public static final String TEST_PKG_ID = "testPkgId";
    private CarryForwardOperation carryForwardOperation;
    @Mock
    DummyPolicyRepository policyRepository;
    private QuotaProfile quotaProfile;

    private String dummyName = "TEST_QUOTA_PROFILE";
    private String quotaProfileId = "TEST_QUOTA_PROFILE";

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        carryForwardOperation = new CarryForwardOperation(policyRepository, new FixedTimeSource(getTime(13, 0)));

        quotaProfile = getQuotaProfile();
    }

    private QuotaProfile getQuotaProfile() {
        String pccProfileName = "pccProfileName";

        List<Map<String, QuotaProfileDetail>> fupLevelserviceWiseQuotaProfileDetais= new ArrayList<>();

        RatingGroup rg = new RatingGroup("RATING_GROUP_1", "RATING_GROUP_1", "RATING_GROUP_1", 0);

        Map<String, QuotaProfileDetail > hsq = new HashMap<>();
        Map<String, QuotaProfileDetail > fup1 = new HashMap<>();
        Map<String, QuotaProfileDetail > fup2 = new HashMap<>();

        fupLevelserviceWiseQuotaProfileDetais.add(hsq);
        fupLevelserviceWiseQuotaProfileDetais.add(fup1);
        fupLevelserviceWiseQuotaProfileDetais.add(fup2);

        DataServiceType dataServiceType1 = new DataServiceType("DATA_SERVICE_TYPE_1", "Rg1", 1, Collections.emptyList(), Arrays.asList(rg));
        DataServiceType dataServiceType4 = new DataServiceType("DATA_SERVICE_TYPE_4", "Rg1", 4, Collections.emptyList(), Arrays.asList(rg));

        Map<AggregationKey, AllowedUsage> allowedUsageMap = getAllowedUsage();

        hsq.put("DATA_SERVICE_TYPE_1", new RncProfileDetail("hsq_Quota_"+dummyName, dataServiceType1, 0, rg, allowedUsageMap, 0, 0, 0, 0, DataUnit.BYTE.name(), TimeUnit.SECOND.name(), 0.0, null, null,
                null, quotaProfileId, true, pccProfileName,0,0, "test"));
        hsq.put("DATA_SERVICE_TYPE_4", new RncProfileDetail("hsq_Quota_"+dummyName, dataServiceType4, 0, rg, allowedUsageMap, 0, 0, 0, 0, DataUnit.BYTE.name(), TimeUnit.SECOND.name(), 0.0, null, null,
                null, quotaProfileId, true, pccProfileName,0,0, "test"));

        fup1.put("DATA_SERVICE_TYPE_1", new RncProfileDetail("hsq_Quota_"+dummyName, dataServiceType1, 1, rg, allowedUsageMap, 0, 0, 0, 0, DataUnit.BYTE.name(), TimeUnit.SECOND.name(), 0.0, null, null,
                null, quotaProfileId, false, pccProfileName,0,0, "test"));
        fup2.put("DATA_SERVICE_TYPE_1", new RncProfileDetail("hsq_Quota_"+dummyName, dataServiceType1, 2, rg, allowedUsageMap, 0, 0, 0, 0, DataUnit.BYTE.name(), TimeUnit.SECOND.name(), 0.0, null, null,
                null, quotaProfileId, false, pccProfileName,0,0, "test"));

        QuotaProfile quotaProfile = new QuotaProfile("Quota_"+dummyName, dummyName,
                quotaProfileId, BalanceLevel.HSQ,2, RenewalIntervalUnit.MONTH, QuotaProfileType.RnC_BASED,
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

    @Test
    public void expiredBalanceWillBeUpdatedWithCARRY_FORWARD_DONEstatus() throws OperationFailedException, TransactionException, SQLException {
        long billingCycleResetTime = getTime(12, 0);

        String renewalInterval = "0:TILL_BILL_DATE";

        NonMonetoryBalance nonMonetoryBalanceForTestSubscriber1 = createServiceRGNonMonitoryBalances(UUID.randomUUID().toString(), billingCycleResetTime, "SUBSCRIBER_1",
                ResetBalanceStatus.RESET_DONE, renewalInterval, null, 0, 1);
        NonMonetoryBalance nonMonetoryBalanceForTestSubscriber2 = createServiceRGNonMonitoryBalances(UUID.randomUUID().toString(), billingCycleResetTime, "SUBSCRIBER_1",
                ResetBalanceStatus.RESET_DONE, renewalInterval, null, 0, 4);
        NonMonetoryBalance nonMonetoryBalanceForTestSubscriber3 = createServiceRGNonMonitoryBalances(UUID.randomUUID().toString(), billingCycleResetTime, "SUBSCRIBER_1",
                ResetBalanceStatus.RESET_DONE, renewalInterval, null, 1, 1);
        NonMonetoryBalance nonMonetoryBalanceForTestSubscriber4 = createServiceRGNonMonitoryBalances(UUID.randomUUID().toString(), billingCycleResetTime, "SUBSCRIBER_1",
                ResetBalanceStatus.RESET_DONE, renewalInterval, null, 2, 1);

        doReturn(quotaProfile).when(policyRepository).getQuotaProfile(anyString(),anyString());

        List<NonMonetoryBalance> nonMonetoryBalanceList = new ArrayList<>();
        nonMonetoryBalanceList.add(nonMonetoryBalanceForTestSubscriber1);
        nonMonetoryBalanceList.add(nonMonetoryBalanceForTestSubscriber2);
        nonMonetoryBalanceList.add(nonMonetoryBalanceForTestSubscriber3);
        nonMonetoryBalanceList.add(nonMonetoryBalanceForTestSubscriber4);

        DataBalanceOperation dataBalanceOperation = new DataBalanceOperation();
        carryForwardOperation.performCarryForwardOperation(nonMonetoryBalanceForTestSubscriber1, dataBalanceOperation, nonMonetoryBalanceList);
        carryForwardOperation.performCarryForwardOperation(nonMonetoryBalanceForTestSubscriber2, dataBalanceOperation, nonMonetoryBalanceList);
        carryForwardOperation.performCarryForwardOperation(nonMonetoryBalanceForTestSubscriber3, dataBalanceOperation, nonMonetoryBalanceList);
        carryForwardOperation.performCarryForwardOperation(nonMonetoryBalanceForTestSubscriber4, dataBalanceOperation, nonMonetoryBalanceList);

        assertEquals(nonMonetoryBalanceForTestSubscriber1.getCarryForwardStatus().name(), CarryForwardStatus.CARRY_FORWARD_DONE.name());
        assertEquals(nonMonetoryBalanceForTestSubscriber2.getCarryForwardStatus().name(), CarryForwardStatus.CARRY_FORWARD_DONE.name());
        assertEquals(nonMonetoryBalanceForTestSubscriber3.getCarryForwardStatus().name(), CarryForwardStatus.CARRY_FORWARD_DONE.name());
        assertEquals(nonMonetoryBalanceForTestSubscriber4.getCarryForwardStatus().name(), CarryForwardStatus.CARRY_FORWARD_DONE.name());
        assertTrue(dataBalanceOperation.getNonMonetaryBalanceUpdateOperation().size()>0);
    }

    @Test
    public void balanceWillNotUpdatIfQuotaIsActive() throws OperationFailedException, TransactionException, SQLException {
        long billingCycleResetTimeForNotExpiredUser = getTime(14, 0);

        String renewalInterval = "0:TILL_BILL_DATE";

        NonMonetoryBalance nonMonetoryBalanceForTestSubscriber1 = createServiceRGNonMonitoryBalances(UUID.randomUUID().toString(), billingCycleResetTimeForNotExpiredUser, "SUBSCRIBER_1",
                ResetBalanceStatus.RESET_DONE, renewalInterval, null, 0, 1);
        NonMonetoryBalance nonMonetoryBalanceForTestSubscriber2 = createServiceRGNonMonitoryBalances(UUID.randomUUID().toString(), billingCycleResetTimeForNotExpiredUser, "SUBSCRIBER_1",
                ResetBalanceStatus.RESET_DONE, renewalInterval, null, 0, 4);
        NonMonetoryBalance nonMonetoryBalanceForTestSubscriber3 = createServiceRGNonMonitoryBalances(UUID.randomUUID().toString(), billingCycleResetTimeForNotExpiredUser, "SUBSCRIBER_1",
                ResetBalanceStatus.RESET_DONE, renewalInterval, null, 1, 1);
        NonMonetoryBalance nonMonetoryBalanceForTestSubscriber4 = createServiceRGNonMonitoryBalances(UUID.randomUUID().toString(), billingCycleResetTimeForNotExpiredUser, "SUBSCRIBER_1",
                ResetBalanceStatus.RESET_DONE, renewalInterval, null, 2, 1);

        doReturn(quotaProfile).when(policyRepository).getQuotaProfile(anyString(),anyString());

        List<NonMonetoryBalance> nonMonetoryBalanceList = new ArrayList<>();
        nonMonetoryBalanceList.add(nonMonetoryBalanceForTestSubscriber1);
        nonMonetoryBalanceList.add(nonMonetoryBalanceForTestSubscriber2);
        nonMonetoryBalanceList.add(nonMonetoryBalanceForTestSubscriber3);
        nonMonetoryBalanceList.add(nonMonetoryBalanceForTestSubscriber4);

        DataBalanceOperation dataBalanceOperation = new DataBalanceOperation();
        carryForwardOperation.performCarryForwardOperation(nonMonetoryBalanceForTestSubscriber1, dataBalanceOperation, nonMonetoryBalanceList);
        carryForwardOperation.performCarryForwardOperation(nonMonetoryBalanceForTestSubscriber2, dataBalanceOperation, nonMonetoryBalanceList);
        carryForwardOperation.performCarryForwardOperation(nonMonetoryBalanceForTestSubscriber3, dataBalanceOperation, nonMonetoryBalanceList);
        carryForwardOperation.performCarryForwardOperation(nonMonetoryBalanceForTestSubscriber4, dataBalanceOperation, nonMonetoryBalanceList);

        assertEquals(nonMonetoryBalanceForTestSubscriber1.getCarryForwardStatus().name(), CarryForwardStatus.CARRY_FORWARD.name());
        assertEquals(nonMonetoryBalanceForTestSubscriber2.getCarryForwardStatus().name(), CarryForwardStatus.CARRY_FORWARD.name());
        assertEquals(nonMonetoryBalanceForTestSubscriber3.getCarryForwardStatus().name(), CarryForwardStatus.CARRY_FORWARD.name());
        assertEquals(nonMonetoryBalanceForTestSubscriber4.getCarryForwardStatus().name(), CarryForwardStatus.CARRY_FORWARD.name());
    }

    private NonMonetoryBalance createServiceRGNonMonitoryBalances(String id, long billingCycleResetTime, String subscriberIdentity, ResetBalanceStatus status,
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
                .withBillingCycleVolumeBalance(1000, 1000)
                .withBillingCycleTimeBalance(1000, 1000)
                .withStartTime(getTime(13, 0))
                .withBillingCycleResetTime(billingCycleResetTime)
                .withCarryForwardStatus(CarryForwardStatus.CARRY_FORWARD)
                .withLevel(level)
                .withServiceId(serviceId)
                .build();
        return serviceRgNonMonitoryBalance;
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
}
