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
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
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

@RunWith(HierarchicalContextRunner.class)
public class CarryForwardOperationQuotaNotProvisionedTest {
    public static final int ZERO_CARRY_FORWARD = 0;
    public static final String SUBSCRIBER_IDENTITY = "SUBSCRIBER_1";
    private String dummyName = "TEST_QUOTA_PROFILE";
    public static final String TEST_PKG_ID = "testPkgId";
    private String quotaProfileId = "TEST_QUOTA_PROFILE";

    private NonMonetoryBalance expiredHSQService1Balance;
    private NonMonetoryBalance expiredHSQService2Balance;
    private NonMonetoryBalance expiredFUP1Service1Balance;
    private NonMonetoryBalance expiredFUP2Service1Balance;

    private NonMonetoryBalance provisionedHSQService1Balance;
    private NonMonetoryBalance provisionedHSQService2Balance;
    private NonMonetoryBalance provisionedFUP1Service1Balance;
    private NonMonetoryBalance provisionedFUP2Service1Balance;

    private List<NonMonetoryBalance> nonMonetoryBalanceList;
    private DataBalanceOperation dataBalanceOperation;

    @Mock
    PolicyRepository policyRepository;
    private CarryForwardOperation carryForwardOperation;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        carryForwardOperation = new CarryForwardOperation(policyRepository, new FixedTimeSource(getTime(13, 0)));
    }

    public class CarryForwardForHybridQuotaType {
        long carryForwardVolumeLimit;
        long carryForwardTimeLimit;

        @Before
        public void setUp() {
            Random random = new Random();
            int hsqService1BalanceTime = 1000;
            int hsqService2BalanceTime = 2000;
            int fup1Service1BalanceTime = 3000;
            int fup2Service1BalanceTime = 4000;

            int hsqService1Balance = 10485760; //10,20,30,40 MB
            int hsqService2Balance = 20971520;
            int fup1Service1Balance = 31457280;
            int fup2Service1Balance = 41943040;
            
            carryForwardVolumeLimit = CommonConstants.QUOTA_UNLIMITED;
            carryForwardTimeLimit = CommonConstants.QUOTA_UNLIMITED;

            long billingCycleResetTime = getTime(12, 0);
            long billingCycleResetTimeForNextMonth = getTime(12,1);
            String renewalInterval = "0:TILL_BILL_DATE";

            expiredHSQService1Balance = createServiceRGNonMonitoryBalances(UUID.randomUUID().toString(), billingCycleResetTime, SUBSCRIBER_IDENTITY,
                    ResetBalanceStatus.RESET_DONE, hsqService1Balance, random.nextInt(hsqService1Balance), hsqService1BalanceTime, random.nextInt(hsqService1BalanceTime), renewalInterval, null, 0, 1);
            expiredHSQService2Balance = createServiceRGNonMonitoryBalances(UUID.randomUUID().toString(), billingCycleResetTime, SUBSCRIBER_IDENTITY,
                    ResetBalanceStatus.RESET_DONE, hsqService2Balance, random.nextInt(hsqService2Balance), hsqService2BalanceTime, random.nextInt(hsqService2BalanceTime), renewalInterval, null, 0, 4);
            expiredFUP1Service1Balance = createServiceRGNonMonitoryBalances(UUID.randomUUID().toString(), billingCycleResetTime, SUBSCRIBER_IDENTITY,
                    ResetBalanceStatus.RESET_DONE, fup1Service1Balance, random.nextInt(fup1Service1Balance), fup1Service1BalanceTime, random.nextInt(fup1Service1BalanceTime), renewalInterval, null, 1, 1);
            expiredFUP2Service1Balance = createServiceRGNonMonitoryBalances(UUID.randomUUID().toString(), billingCycleResetTime, SUBSCRIBER_IDENTITY,
                    ResetBalanceStatus.RESET_DONE, fup2Service1Balance, random.nextInt(fup2Service1Balance), fup2Service1BalanceTime, random.nextInt(fup2Service1BalanceTime), renewalInterval, null, 2, 1);

            provisionedHSQService1Balance = createServiceRGNonMonitoryBalances(UUID.randomUUID().toString(), billingCycleResetTimeForNextMonth, SUBSCRIBER_IDENTITY,
                    ResetBalanceStatus.RESET, hsqService1Balance, hsqService1Balance, hsqService1BalanceTime, hsqService1BalanceTime, renewalInterval, null, 0, 1);
            provisionedHSQService2Balance = createServiceRGNonMonitoryBalances(UUID.randomUUID().toString(), billingCycleResetTimeForNextMonth, SUBSCRIBER_IDENTITY,
                    ResetBalanceStatus.RESET, hsqService2Balance, hsqService2Balance, hsqService2BalanceTime, hsqService2BalanceTime, renewalInterval, null, 0, 4);
            provisionedFUP1Service1Balance = createServiceRGNonMonitoryBalances(UUID.randomUUID().toString(), billingCycleResetTimeForNextMonth, SUBSCRIBER_IDENTITY,
                    ResetBalanceStatus.RESET, fup1Service1Balance, fup1Service1Balance, fup1Service1BalanceTime, fup1Service1BalanceTime, renewalInterval, null, 1, 1);
            provisionedFUP2Service1Balance = createServiceRGNonMonitoryBalances(UUID.randomUUID().toString(), billingCycleResetTimeForNextMonth, SUBSCRIBER_IDENTITY,
                    ResetBalanceStatus.RESET, fup2Service1Balance, fup2Service1Balance, fup2Service1BalanceTime, fup2Service1BalanceTime, renewalInterval, null, 2, 1);

            nonMonetoryBalanceList = new ArrayList<>();

            dataBalanceOperation = new DataBalanceOperation();
            dataBalanceOperation.setNonMonetaryBalanceInsertOperations(provisionedHSQService1Balance);
            dataBalanceOperation.setNonMonetaryBalanceInsertOperations(provisionedHSQService2Balance);
            dataBalanceOperation.setNonMonetaryBalanceInsertOperations(provisionedFUP1Service1Balance);
            dataBalanceOperation.setNonMonetaryBalanceInsertOperations(provisionedFUP2Service1Balance);
        }

        @Test
        public void expiredQuotaGetsCarriedForwardWhenNoCapLimitProvided() throws OperationFailedException, TransactionException, SQLException {
            QuotaProfile quotaProfile = getQuotaProfile(QuotaUsageType.HYBRID, BalanceLevel.FUP1, carryForwardVolumeLimit, carryForwardTimeLimit);
            doReturn(quotaProfile).when(policyRepository).getQuotaProfile(anyString(),anyString());
            carryForwardOperation.performCarryForwardOperation(expiredHSQService1Balance, dataBalanceOperation, nonMonetoryBalanceList);
            carryForwardOperation.performCarryForwardOperation(expiredHSQService2Balance, dataBalanceOperation, nonMonetoryBalanceList);
            carryForwardOperation.performCarryForwardOperation(expiredFUP1Service1Balance, dataBalanceOperation, nonMonetoryBalanceList);
            carryForwardOperation.performCarryForwardOperation(expiredFUP2Service1Balance, dataBalanceOperation, nonMonetoryBalanceList);

            Assert.assertEquals(provisionedHSQService1Balance.getCarryForwardVolume(), expiredHSQService1Balance.getBillingCycleAvailableVolume());
            Assert.assertEquals(provisionedHSQService2Balance.getCarryForwardVolume(), expiredHSQService2Balance.getBillingCycleAvailableVolume());
            Assert.assertEquals(provisionedFUP1Service1Balance.getCarryForwardVolume(), expiredFUP1Service1Balance.getBillingCycleAvailableVolume());

            Assert.assertEquals(provisionedFUP2Service1Balance.getCarryForwardVolume(), ZERO_CARRY_FORWARD);

            Assert.assertEquals(provisionedHSQService1Balance.getCarryForwardTime(), expiredHSQService1Balance.getBillingCycleAvailableTime());
            Assert.assertEquals(provisionedHSQService2Balance.getCarryForwardTime(), expiredHSQService2Balance.getBillingCycleAvailableTime());
            Assert.assertEquals(provisionedFUP1Service1Balance.getCarryForwardTime(), expiredFUP1Service1Balance.getBillingCycleAvailableTime());

            Assert.assertEquals(provisionedFUP2Service1Balance.getCarryForwardTime(), ZERO_CARRY_FORWARD);

            Assert.assertTrue(provisionedHSQService1Balance.getBillingCycleAvailableVolume() ==
                    provisionedHSQService1Balance.getBillingCycleTotalVolume() + expiredHSQService1Balance.getBillingCycleAvailableVolume());
            Assert.assertTrue(provisionedHSQService2Balance.getBillingCycleAvailableVolume() ==
                    provisionedHSQService2Balance.getBillingCycleTotalVolume() + expiredHSQService2Balance.getBillingCycleAvailableVolume());
            Assert.assertTrue(provisionedFUP1Service1Balance.getBillingCycleAvailableVolume() ==
                    provisionedFUP1Service1Balance.getBillingCycleTotalVolume() + expiredFUP1Service1Balance.getBillingCycleAvailableVolume());

            Assert.assertEquals(provisionedFUP2Service1Balance.getBillingCycleAvailableVolume(),expiredFUP2Service1Balance.getBillingCycleTotalVolume());

            Assert.assertTrue(provisionedHSQService1Balance.getBillingCycleAvailableTime() ==
                    provisionedHSQService1Balance.getBillingCycleTime() + expiredHSQService1Balance.getBillingCycleAvailableTime());
            Assert.assertTrue(provisionedHSQService2Balance.getBillingCycleAvailableTime() ==
                    provisionedHSQService2Balance.getBillingCycleTime() + expiredHSQService2Balance.getBillingCycleAvailableTime());
            Assert.assertTrue(provisionedFUP1Service1Balance.getBillingCycleAvailableTime() ==
                    provisionedFUP1Service1Balance.getBillingCycleTime() + expiredFUP1Service1Balance.getBillingCycleAvailableTime());

            Assert.assertEquals(provisionedFUP2Service1Balance.getBillingCycleAvailableTime(),expiredFUP2Service1Balance.getBillingCycleTime() + ZERO_CARRY_FORWARD);

            Assert.assertFalse(dataBalanceOperation.getNonMonetaryBalanceUpdateOperation().contains(provisionedHSQService1Balance));
            Assert.assertFalse(dataBalanceOperation.getNonMonetaryBalanceUpdateOperation().contains(provisionedHSQService2Balance));
            Assert.assertFalse(dataBalanceOperation.getNonMonetaryBalanceUpdateOperation().contains(provisionedFUP1Service1Balance));
            Assert.assertFalse(dataBalanceOperation.getNonMonetaryBalanceUpdateOperation().contains(provisionedFUP2Service1Balance));
        }

        @Test
        public void quotaWillBeCarriedForwardBasedOnCap() throws OperationFailedException, TransactionException, SQLException {
            carryForwardVolumeLimit = 25;
            carryForwardTimeLimit = 50;
            QuotaProfile quotaProfile = getQuotaProfile(QuotaUsageType.HYBRID, BalanceLevel.FUP1, carryForwardVolumeLimit, carryForwardTimeLimit);
            doReturn(quotaProfile).when(policyRepository).getQuotaProfile(anyString(),anyString());
            carryForwardOperation.performCarryForwardOperation(expiredHSQService1Balance, dataBalanceOperation, nonMonetoryBalanceList);
            carryForwardOperation.performCarryForwardOperation(expiredHSQService2Balance, dataBalanceOperation, nonMonetoryBalanceList);
            carryForwardOperation.performCarryForwardOperation(expiredFUP1Service1Balance, dataBalanceOperation, nonMonetoryBalanceList);
            carryForwardOperation.performCarryForwardOperation(expiredFUP2Service1Balance, dataBalanceOperation, nonMonetoryBalanceList);

            Assert.assertEquals(provisionedHSQService1Balance.getCarryForwardTime(), timeToCarryForward(expiredHSQService1Balance, carryForwardTimeLimit));
            Assert.assertEquals(provisionedHSQService1Balance.getCarryForwardVolume(), quotaToCarryForward(expiredHSQService1Balance, carryForwardVolumeLimit));

            Assert.assertEquals(provisionedHSQService2Balance.getCarryForwardTime(), timeToCarryForward(expiredHSQService2Balance, carryForwardTimeLimit));
            Assert.assertEquals(provisionedHSQService2Balance.getCarryForwardVolume(), quotaToCarryForward(expiredHSQService2Balance, carryForwardVolumeLimit));

            Assert.assertEquals(provisionedFUP1Service1Balance.getCarryForwardTime(), timeToCarryForward(expiredFUP1Service1Balance, carryForwardTimeLimit));
            Assert.assertEquals(provisionedFUP1Service1Balance.getCarryForwardVolume(), quotaToCarryForward(expiredFUP1Service1Balance, carryForwardVolumeLimit));

            Assert.assertTrue(provisionedHSQService1Balance.getBillingCycleAvailableVolume() ==
                    provisionedHSQService1Balance.getBillingCycleTotalVolume() + quotaToCarryForward(expiredHSQService1Balance, carryForwardVolumeLimit));
            Assert.assertTrue(provisionedHSQService1Balance.getBillingCycleAvailableTime() ==
                    provisionedHSQService1Balance.getBillingCycleTime() + timeToCarryForward(expiredHSQService1Balance, carryForwardTimeLimit));

            Assert.assertTrue(provisionedHSQService2Balance.getBillingCycleAvailableVolume() ==
                    provisionedHSQService2Balance.getBillingCycleTotalVolume() + quotaToCarryForward(expiredHSQService2Balance, carryForwardVolumeLimit));
            Assert.assertTrue(provisionedHSQService2Balance.getBillingCycleAvailableTime() ==
                    provisionedHSQService2Balance.getBillingCycleTime() + timeToCarryForward(expiredHSQService2Balance, carryForwardTimeLimit));

            Assert.assertTrue(provisionedFUP1Service1Balance.getBillingCycleAvailableVolume() ==
                    provisionedFUP1Service1Balance.getBillingCycleTotalVolume() + quotaToCarryForward(expiredFUP1Service1Balance, carryForwardVolumeLimit));
            Assert.assertTrue(provisionedFUP1Service1Balance.getBillingCycleAvailableTime() ==
                    provisionedFUP1Service1Balance.getBillingCycleTime() + timeToCarryForward(expiredFUP1Service1Balance, carryForwardTimeLimit));

            Assert.assertFalse(dataBalanceOperation.getNonMonetaryBalanceUpdateOperation().contains(provisionedHSQService1Balance));
            Assert.assertFalse(dataBalanceOperation.getNonMonetaryBalanceUpdateOperation().contains(provisionedHSQService2Balance));
            Assert.assertFalse(dataBalanceOperation.getNonMonetaryBalanceUpdateOperation().contains(provisionedFUP1Service1Balance));
            Assert.assertFalse(dataBalanceOperation.getNonMonetaryBalanceUpdateOperation().contains(provisionedFUP2Service1Balance));
        }
    }

    private long quotaToCarryForward(NonMonetoryBalance expiredBalance, long carryForwardLimit) {
        return expiredBalance.getBillingCycleAvailableVolume() > DataUnit.MB.toBytes(carryForwardLimit)?
                DataUnit.MB.toBytes(carryForwardLimit):expiredBalance.getBillingCycleAvailableVolume();
    }

    private long timeToCarryForward(NonMonetoryBalance expiredBalance, long carryForwardTimeLimit) {
        return expiredBalance.getBillingCycleAvailableTime() > TimeUnit.MINUTE.toSeconds(carryForwardTimeLimit)?
                TimeUnit.MINUTE.toSeconds(carryForwardTimeLimit):expiredBalance.getBillingCycleAvailableTime();
    }

    public class CarryForwardForTimeQuotaType {
        long carryForwardVolumeLimit = CommonConstants.QUOTA_UNDEFINED;
        long carryForwardTimeLimit = CommonConstants.QUOTA_UNLIMITED;

        @Before
        public void setUp() {
            Random random = new Random();
            int hsqService1Balance = 1000;
            int hsqService2Balance = 2000;
            int fup1Service1Balance = 3000;
            int fup2Service1Balance = 4000;

            long billingCycleResetTime = getTime(12, 0);
            long billingCycleResetTimeForNextMonth = getTime(12, 1);
            String renewalInterval = "0:TILL_BILL_DATE";

            expiredHSQService1Balance = createServiceRGNonMonitoryBalances(UUID.randomUUID().toString(), billingCycleResetTime, SUBSCRIBER_IDENTITY,
                    ResetBalanceStatus.RESET_DONE, CommonConstants.QUOTA_UNDEFINED, CommonConstants.QUOTA_UNDEFINED, hsqService1Balance, random.nextInt(hsqService1Balance), renewalInterval, null, 0, 1);
            expiredHSQService2Balance = createServiceRGNonMonitoryBalances(UUID.randomUUID().toString(), billingCycleResetTime, SUBSCRIBER_IDENTITY,
                    ResetBalanceStatus.RESET_DONE, CommonConstants.QUOTA_UNDEFINED, CommonConstants.QUOTA_UNDEFINED, hsqService2Balance, random.nextInt(hsqService2Balance), renewalInterval, null, 0, 4);
            expiredFUP1Service1Balance = createServiceRGNonMonitoryBalances(UUID.randomUUID().toString(), billingCycleResetTime, SUBSCRIBER_IDENTITY,
                    ResetBalanceStatus.RESET_DONE, CommonConstants.QUOTA_UNDEFINED, CommonConstants.QUOTA_UNDEFINED, fup1Service1Balance, random.nextInt(fup1Service1Balance), renewalInterval, null, 1, 1);
            expiredFUP2Service1Balance = createServiceRGNonMonitoryBalances(UUID.randomUUID().toString(), billingCycleResetTime, SUBSCRIBER_IDENTITY,
                    ResetBalanceStatus.RESET_DONE, CommonConstants.QUOTA_UNDEFINED, CommonConstants.QUOTA_UNDEFINED, fup2Service1Balance, random.nextInt(fup2Service1Balance), renewalInterval, null, 2, 1);

            provisionedHSQService1Balance = createServiceRGNonMonitoryBalances(UUID.randomUUID().toString(), billingCycleResetTimeForNextMonth, SUBSCRIBER_IDENTITY,
                    ResetBalanceStatus.RESET, CommonConstants.QUOTA_UNDEFINED, CommonConstants.QUOTA_UNDEFINED, hsqService1Balance, hsqService1Balance, renewalInterval, null, 0, 1);
            provisionedHSQService2Balance = createServiceRGNonMonitoryBalances(UUID.randomUUID().toString(), billingCycleResetTimeForNextMonth, SUBSCRIBER_IDENTITY,
                    ResetBalanceStatus.RESET, CommonConstants.QUOTA_UNDEFINED, CommonConstants.QUOTA_UNDEFINED, hsqService2Balance, hsqService2Balance, renewalInterval, null, 0, 4);
            provisionedFUP1Service1Balance = createServiceRGNonMonitoryBalances(UUID.randomUUID().toString(), billingCycleResetTimeForNextMonth, SUBSCRIBER_IDENTITY,
                    ResetBalanceStatus.RESET, CommonConstants.QUOTA_UNDEFINED, CommonConstants.QUOTA_UNDEFINED, fup1Service1Balance, fup1Service1Balance, renewalInterval, null, 1, 1);
            provisionedFUP2Service1Balance = createServiceRGNonMonitoryBalances(UUID.randomUUID().toString(), billingCycleResetTimeForNextMonth, SUBSCRIBER_IDENTITY,
                    ResetBalanceStatus.RESET, CommonConstants.QUOTA_UNDEFINED, CommonConstants.QUOTA_UNDEFINED, fup2Service1Balance, fup2Service1Balance, renewalInterval, null, 2, 1);

            nonMonetoryBalanceList = new ArrayList<>();

            dataBalanceOperation = new DataBalanceOperation();
            dataBalanceOperation.setNonMonetaryBalanceInsertOperations(provisionedHSQService1Balance);
            dataBalanceOperation.setNonMonetaryBalanceInsertOperations(provisionedHSQService2Balance);
            dataBalanceOperation.setNonMonetaryBalanceInsertOperations(provisionedFUP1Service1Balance);
            dataBalanceOperation.setNonMonetaryBalanceInsertOperations(provisionedFUP2Service1Balance);
        }

        @Test
        public void expiredQuotaGetsCarriedForwardWhenNoCapLimitProvided() throws OperationFailedException, TransactionException, SQLException {
            QuotaProfile quotaProfile = getQuotaProfile(QuotaUsageType.TIME, BalanceLevel.FUP1, carryForwardVolumeLimit, carryForwardTimeLimit);
            doReturn(quotaProfile).when(policyRepository).getQuotaProfile(anyString(), anyString());

            carryForwardOperation.performCarryForwardOperation(expiredHSQService1Balance, dataBalanceOperation, nonMonetoryBalanceList);
            carryForwardOperation.performCarryForwardOperation(expiredHSQService2Balance, dataBalanceOperation, nonMonetoryBalanceList);
            carryForwardOperation.performCarryForwardOperation(expiredFUP1Service1Balance, dataBalanceOperation, nonMonetoryBalanceList);
            carryForwardOperation.performCarryForwardOperation(expiredFUP2Service1Balance, dataBalanceOperation, nonMonetoryBalanceList);



            Assert.assertEquals(provisionedHSQService1Balance.getCarryForwardVolume(), ZERO_CARRY_FORWARD);
            Assert.assertEquals(provisionedHSQService2Balance.getCarryForwardVolume(), ZERO_CARRY_FORWARD);
            Assert.assertEquals(provisionedFUP1Service1Balance.getCarryForwardVolume(), ZERO_CARRY_FORWARD);
            Assert.assertEquals(provisionedFUP2Service1Balance.getCarryForwardVolume(), ZERO_CARRY_FORWARD);

            Assert.assertEquals(provisionedHSQService1Balance.getCarryForwardTime(), expiredHSQService1Balance.getBillingCycleAvailableTime());
            Assert.assertEquals(provisionedHSQService2Balance.getCarryForwardTime(), expiredHSQService2Balance.getBillingCycleAvailableTime());
            Assert.assertEquals(provisionedFUP1Service1Balance.getCarryForwardTime(), expiredFUP1Service1Balance.getBillingCycleAvailableTime());
            Assert.assertEquals(provisionedFUP2Service1Balance.getCarryForwardTime(), ZERO_CARRY_FORWARD);

            Assert.assertEquals(provisionedHSQService1Balance.getBillingCycleAvailableTime(),
                    provisionedHSQService1Balance.getBillingCycleTime() + expiredHSQService1Balance.getBillingCycleAvailableTime());
            Assert.assertEquals(provisionedHSQService2Balance.getBillingCycleAvailableTime(),
                    provisionedHSQService2Balance.getBillingCycleTime() + expiredHSQService2Balance.getBillingCycleAvailableTime());
            Assert.assertEquals(provisionedFUP1Service1Balance.getBillingCycleAvailableTime(),
                    provisionedFUP1Service1Balance.getBillingCycleTime() + expiredFUP1Service1Balance.getBillingCycleAvailableTime());

            Assert.assertEquals(provisionedFUP2Service1Balance.getBillingCycleAvailableTime(), expiredFUP2Service1Balance.getBillingCycleTime() + ZERO_CARRY_FORWARD);

            Assert.assertFalse(dataBalanceOperation.getNonMonetaryBalanceUpdateOperation().contains(provisionedHSQService1Balance));
            Assert.assertFalse(dataBalanceOperation.getNonMonetaryBalanceUpdateOperation().contains(provisionedHSQService2Balance));
            Assert.assertFalse(dataBalanceOperation.getNonMonetaryBalanceUpdateOperation().contains(provisionedFUP1Service1Balance));
            Assert.assertFalse(dataBalanceOperation.getNonMonetaryBalanceUpdateOperation().contains(provisionedFUP2Service1Balance));
        }

        @Test
        public void quotaWillBeCarriedForwardBasedOnCapForTime() throws OperationFailedException, TransactionException, SQLException {
            carryForwardVolumeLimit = QUOTA_UNDEFINED;
            carryForwardTimeLimit = 50;
            QuotaProfile quotaProfile = getQuotaProfile(QuotaUsageType.TIME, BalanceLevel.FUP1, carryForwardVolumeLimit, carryForwardTimeLimit);
            doReturn(quotaProfile).when(policyRepository).getQuotaProfile(anyString(),anyString());

            carryForwardOperation.performCarryForwardOperation(expiredHSQService1Balance, dataBalanceOperation, nonMonetoryBalanceList);
            carryForwardOperation.performCarryForwardOperation(expiredHSQService2Balance, dataBalanceOperation, nonMonetoryBalanceList);
            carryForwardOperation.performCarryForwardOperation(expiredFUP1Service1Balance, dataBalanceOperation, nonMonetoryBalanceList);
            carryForwardOperation.performCarryForwardOperation(expiredFUP2Service1Balance, dataBalanceOperation, nonMonetoryBalanceList);

            Assert.assertEquals(provisionedHSQService1Balance.getCarryForwardTime(), timeToCarryForward(expiredHSQService1Balance, carryForwardTimeLimit));
            Assert.assertEquals(provisionedHSQService2Balance.getCarryForwardTime(), timeToCarryForward(expiredHSQService2Balance, carryForwardTimeLimit));
            Assert.assertEquals(provisionedFUP1Service1Balance.getCarryForwardTime(), timeToCarryForward(expiredFUP1Service1Balance, carryForwardTimeLimit));
            Assert.assertEquals(provisionedFUP2Service1Balance.getCarryForwardTime(), ZERO_CARRY_FORWARD);

            Assert.assertEquals(provisionedHSQService1Balance.getBillingCycleAvailableTime(),
                    provisionedHSQService1Balance.getBillingCycleTime() + timeToCarryForward(expiredHSQService1Balance, carryForwardTimeLimit));
            Assert.assertEquals(provisionedHSQService2Balance.getBillingCycleAvailableTime(),
                    provisionedHSQService2Balance.getBillingCycleTime() + timeToCarryForward(expiredHSQService2Balance, carryForwardTimeLimit));
            Assert.assertEquals(provisionedFUP1Service1Balance.getBillingCycleAvailableTime(),
                    provisionedFUP1Service1Balance.getBillingCycleTime() + timeToCarryForward(expiredFUP1Service1Balance, carryForwardTimeLimit));

            Assert.assertEquals(provisionedFUP2Service1Balance.getBillingCycleAvailableTime(), expiredFUP2Service1Balance.getBillingCycleTime() + ZERO_CARRY_FORWARD);

            Assert.assertFalse(dataBalanceOperation.getNonMonetaryBalanceUpdateOperation().contains(provisionedHSQService1Balance));
            Assert.assertFalse(dataBalanceOperation.getNonMonetaryBalanceUpdateOperation().contains(provisionedHSQService2Balance));
            Assert.assertFalse(dataBalanceOperation.getNonMonetaryBalanceUpdateOperation().contains(provisionedFUP1Service1Balance));
            Assert.assertFalse(dataBalanceOperation.getNonMonetaryBalanceUpdateOperation().contains(provisionedFUP2Service1Balance));
        }

        @Test
        public void volumeWillNotBeCarriedForward() throws OperationFailedException, TransactionException, SQLException {
            carryForwardVolumeLimit = QUOTA_UNDEFINED;
            carryForwardTimeLimit = 50;
            QuotaProfile quotaProfile = getQuotaProfile(QuotaUsageType.TIME, BalanceLevel.FUP1, carryForwardVolumeLimit, carryForwardTimeLimit);
            doReturn(quotaProfile).when(policyRepository).getQuotaProfile(anyString(),anyString());

            carryForwardOperation.performCarryForwardOperation(expiredHSQService1Balance, dataBalanceOperation, nonMonetoryBalanceList);
            carryForwardOperation.performCarryForwardOperation(expiredHSQService2Balance, dataBalanceOperation, nonMonetoryBalanceList);
            carryForwardOperation.performCarryForwardOperation(expiredFUP1Service1Balance, dataBalanceOperation, nonMonetoryBalanceList);
            carryForwardOperation.performCarryForwardOperation(expiredFUP2Service1Balance, dataBalanceOperation, nonMonetoryBalanceList);

            Assert.assertEquals(provisionedHSQService1Balance.getCarryForwardVolume(), ZERO_CARRY_FORWARD);
            Assert.assertEquals(provisionedHSQService2Balance.getCarryForwardVolume(), ZERO_CARRY_FORWARD);
            Assert.assertEquals(provisionedFUP1Service1Balance.getCarryForwardVolume(), ZERO_CARRY_FORWARD);
            Assert.assertEquals(provisionedFUP2Service1Balance.getCarryForwardVolume(), ZERO_CARRY_FORWARD);

            Assert.assertEquals(provisionedHSQService1Balance.getBillingCycleAvailableVolume(), expiredHSQService1Balance.getBillingCycleAvailableVolume());
            Assert.assertEquals(provisionedHSQService2Balance.getBillingCycleAvailableVolume(), expiredHSQService2Balance.getBillingCycleAvailableVolume());
            Assert.assertEquals(provisionedFUP1Service1Balance.getBillingCycleAvailableVolume(), expiredFUP1Service1Balance.getBillingCycleAvailableVolume());
            Assert.assertEquals(provisionedFUP2Service1Balance.getBillingCycleAvailableVolume(), expiredFUP2Service1Balance.getBillingCycleAvailableVolume());

            Assert.assertFalse(dataBalanceOperation.getNonMonetaryBalanceUpdateOperation().contains(provisionedHSQService1Balance));
            Assert.assertFalse(dataBalanceOperation.getNonMonetaryBalanceUpdateOperation().contains(provisionedHSQService2Balance));
            Assert.assertFalse(dataBalanceOperation.getNonMonetaryBalanceUpdateOperation().contains(provisionedFUP1Service1Balance));
            Assert.assertFalse(dataBalanceOperation.getNonMonetaryBalanceUpdateOperation().contains(provisionedFUP2Service1Balance));
        }
    }

    public class CarryForwardOperationForVolumeQuotaType {
        @Before
        public void setUp() {
            Random random = new Random();
            int hsqService1Balance = 10485760; //10,20,30,40 MB
            int hsqService2Balance = 20971520;
            int fup1Service1Balance = 31457280;
            int fup2Service1Balance = 41943040;

            long billingCycleResetTime = getTime(12, 0);
            long billingCycleResetTimeForNextMonth = getTime(12,1);
            String renewalInterval = "0:TILL_BILL_DATE";

            expiredHSQService1Balance = createServiceRGNonMonitoryBalances(UUID.randomUUID().toString(), billingCycleResetTime, SUBSCRIBER_IDENTITY,
                    ResetBalanceStatus.RESET_DONE, hsqService1Balance,random.nextInt(hsqService1Balance),CommonConstants.QUOTA_UNDEFINED, CommonConstants.QUOTA_UNDEFINED, renewalInterval, null, 0, 1);
            expiredHSQService2Balance = createServiceRGNonMonitoryBalances(UUID.randomUUID().toString(), billingCycleResetTime, SUBSCRIBER_IDENTITY,
                    ResetBalanceStatus.RESET_DONE, hsqService2Balance,random.nextInt(hsqService2Balance),CommonConstants.QUOTA_UNDEFINED, CommonConstants.QUOTA_UNDEFINED, renewalInterval, null, 0, 4);
            expiredFUP1Service1Balance = createServiceRGNonMonitoryBalances(UUID.randomUUID().toString(), billingCycleResetTime, SUBSCRIBER_IDENTITY,
                    ResetBalanceStatus.RESET_DONE, fup1Service1Balance,random.nextInt(fup1Service1Balance),CommonConstants.QUOTA_UNDEFINED, CommonConstants.QUOTA_UNDEFINED,renewalInterval, null, 1, 1);
            expiredFUP2Service1Balance = createServiceRGNonMonitoryBalances(UUID.randomUUID().toString(), billingCycleResetTime, SUBSCRIBER_IDENTITY,
                    ResetBalanceStatus.RESET_DONE, fup2Service1Balance, random.nextInt(fup2Service1Balance), CommonConstants.QUOTA_UNDEFINED, CommonConstants.QUOTA_UNDEFINED,renewalInterval, null, 2, 1);

            provisionedHSQService1Balance = createServiceRGNonMonitoryBalances(UUID.randomUUID().toString(), billingCycleResetTimeForNextMonth, SUBSCRIBER_IDENTITY,
                    ResetBalanceStatus.RESET, hsqService1Balance, hsqService1Balance, CommonConstants.QUOTA_UNDEFINED, CommonConstants.QUOTA_UNDEFINED, renewalInterval, null, 0, 1);
            provisionedHSQService2Balance = createServiceRGNonMonitoryBalances(UUID.randomUUID().toString(), billingCycleResetTimeForNextMonth, SUBSCRIBER_IDENTITY,
                    ResetBalanceStatus.RESET, hsqService2Balance, hsqService2Balance, CommonConstants.QUOTA_UNDEFINED, CommonConstants.QUOTA_UNDEFINED, renewalInterval, null, 0, 4);
            provisionedFUP1Service1Balance = createServiceRGNonMonitoryBalances(UUID.randomUUID().toString(), billingCycleResetTimeForNextMonth, SUBSCRIBER_IDENTITY,
                    ResetBalanceStatus.RESET, fup1Service1Balance, fup1Service1Balance, CommonConstants.QUOTA_UNDEFINED, CommonConstants.QUOTA_UNDEFINED, renewalInterval, null, 1, 1);
            provisionedFUP2Service1Balance = createServiceRGNonMonitoryBalances(UUID.randomUUID().toString(), billingCycleResetTimeForNextMonth, SUBSCRIBER_IDENTITY,
                    ResetBalanceStatus.RESET, fup2Service1Balance, fup2Service1Balance, CommonConstants.QUOTA_UNDEFINED, CommonConstants.QUOTA_UNDEFINED, renewalInterval, null, 2, 1);

            nonMonetoryBalanceList = new ArrayList<>();

            dataBalanceOperation = new DataBalanceOperation();
            dataBalanceOperation.setNonMonetaryBalanceInsertOperations(provisionedHSQService1Balance);
            dataBalanceOperation.setNonMonetaryBalanceInsertOperations(provisionedHSQService2Balance);
            dataBalanceOperation.setNonMonetaryBalanceInsertOperations(provisionedFUP1Service1Balance);
            dataBalanceOperation.setNonMonetaryBalanceInsertOperations(provisionedFUP2Service1Balance);
        }

        @Test
        public void expiredQuotaGetsCarriedForwardWhenNoCapLimitProvided() throws OperationFailedException, TransactionException, SQLException {
            long carryForwardVolumeLimit = CommonConstants.QUOTA_UNLIMITED;
            long carryForwardTimeLimit = CommonConstants.QUOTA_UNDEFINED;

            QuotaProfile quotaProfile = getQuotaProfile(QuotaUsageType.VOLUME, BalanceLevel.FUP1, carryForwardVolumeLimit, carryForwardTimeLimit);
            doReturn(quotaProfile).when(policyRepository).getQuotaProfile(anyString(),anyString());

            carryForwardOperation.performCarryForwardOperation(expiredHSQService1Balance, dataBalanceOperation, nonMonetoryBalanceList);
            carryForwardOperation.performCarryForwardOperation(expiredHSQService2Balance, dataBalanceOperation, nonMonetoryBalanceList);
            carryForwardOperation.performCarryForwardOperation(expiredFUP1Service1Balance, dataBalanceOperation, nonMonetoryBalanceList);
            carryForwardOperation.performCarryForwardOperation(expiredFUP2Service1Balance, dataBalanceOperation, nonMonetoryBalanceList);

            Assert.assertEquals(provisionedHSQService1Balance.getCarryForwardVolume(), expiredHSQService1Balance.getBillingCycleAvailableVolume());
            Assert.assertEquals(provisionedHSQService2Balance.getCarryForwardVolume(), expiredHSQService2Balance.getBillingCycleAvailableVolume());
            Assert.assertEquals(provisionedFUP1Service1Balance.getCarryForwardVolume(), expiredFUP1Service1Balance.getBillingCycleAvailableVolume());
            Assert.assertEquals(provisionedFUP2Service1Balance.getCarryForwardVolume(), ZERO_CARRY_FORWARD);

            Assert.assertEquals(provisionedHSQService1Balance.getCarryForwardTime(), ZERO_CARRY_FORWARD);
            Assert.assertEquals(provisionedHSQService2Balance.getCarryForwardTime(), ZERO_CARRY_FORWARD);
            Assert.assertEquals(provisionedFUP1Service1Balance.getCarryForwardTime(), ZERO_CARRY_FORWARD);
            Assert.assertEquals(provisionedFUP2Service1Balance.getCarryForwardTime(), ZERO_CARRY_FORWARD);

            Assert.assertEquals(provisionedHSQService1Balance.getBillingCycleAvailableVolume(),
                    provisionedHSQService1Balance.getBillingCycleTotalVolume() + expiredHSQService1Balance.getBillingCycleAvailableVolume());
            Assert.assertEquals(provisionedHSQService2Balance.getBillingCycleAvailableVolume(),
                    provisionedHSQService2Balance.getBillingCycleTotalVolume() + expiredHSQService2Balance.getBillingCycleAvailableVolume());
            Assert.assertEquals(provisionedFUP1Service1Balance.getBillingCycleAvailableVolume(),
                    provisionedFUP1Service1Balance.getBillingCycleTotalVolume() + expiredFUP1Service1Balance.getBillingCycleAvailableVolume());
            Assert.assertEquals(provisionedFUP2Service1Balance.getBillingCycleAvailableVolume(),expiredFUP2Service1Balance.getBillingCycleTotalVolume());

            Assert.assertFalse(dataBalanceOperation.getNonMonetaryBalanceUpdateOperation().contains(provisionedHSQService1Balance));
            Assert.assertFalse(dataBalanceOperation.getNonMonetaryBalanceUpdateOperation().contains(provisionedHSQService2Balance));
            Assert.assertFalse(dataBalanceOperation.getNonMonetaryBalanceUpdateOperation().contains(provisionedFUP1Service1Balance));
            Assert.assertFalse(dataBalanceOperation.getNonMonetaryBalanceUpdateOperation().contains(provisionedFUP2Service1Balance));
        }

        @Test
        public void quotaWillBeCarriedForwardBasedOnCap() throws OperationFailedException, TransactionException, SQLException {
            long carryForwardVolumeLimit = 25; //25MB
            long carryForwardTimeLimit = QUOTA_UNDEFINED;

            QuotaProfile quotaProfile = getQuotaProfile(QuotaUsageType.VOLUME, BalanceLevel.FUP1, carryForwardVolumeLimit, carryForwardTimeLimit);
            doReturn(quotaProfile).when(policyRepository).getQuotaProfile(anyString(),anyString());

            carryForwardOperation.performCarryForwardOperation(expiredHSQService1Balance, dataBalanceOperation, nonMonetoryBalanceList);
            carryForwardOperation.performCarryForwardOperation(expiredHSQService2Balance, dataBalanceOperation, nonMonetoryBalanceList);
            carryForwardOperation.performCarryForwardOperation(expiredFUP1Service1Balance, dataBalanceOperation, nonMonetoryBalanceList);
            carryForwardOperation.performCarryForwardOperation(expiredFUP2Service1Balance, dataBalanceOperation, nonMonetoryBalanceList);

            Assert.assertEquals(provisionedHSQService1Balance.getCarryForwardVolume(), quotaToCarryForward(expiredHSQService1Balance, carryForwardVolumeLimit));
            Assert.assertEquals(provisionedHSQService2Balance.getCarryForwardVolume(), quotaToCarryForward(expiredHSQService2Balance, carryForwardVolumeLimit));

            Assert.assertEquals(provisionedFUP1Service1Balance.getCarryForwardVolume(), quotaToCarryForward(expiredFUP1Service1Balance, carryForwardVolumeLimit));
            Assert.assertEquals(provisionedFUP2Service1Balance.getCarryForwardVolume(), ZERO_CARRY_FORWARD);

            Assert.assertEquals(provisionedHSQService1Balance.getBillingCycleAvailableVolume(),
                    provisionedHSQService1Balance.getBillingCycleTotalVolume() + quotaToCarryForward(expiredHSQService1Balance, carryForwardVolumeLimit));
            Assert.assertEquals(provisionedHSQService2Balance.getBillingCycleAvailableVolume(),
                    provisionedHSQService2Balance.getBillingCycleTotalVolume() + quotaToCarryForward(expiredHSQService2Balance, carryForwardVolumeLimit));
            Assert.assertEquals(provisionedFUP1Service1Balance.getBillingCycleAvailableVolume(),
                    provisionedFUP1Service1Balance.getBillingCycleTotalVolume() + quotaToCarryForward(expiredFUP1Service1Balance, carryForwardVolumeLimit));
            Assert.assertEquals(provisionedFUP2Service1Balance.getBillingCycleAvailableVolume(),expiredFUP2Service1Balance.getBillingCycleTotalVolume());

            Assert.assertFalse(dataBalanceOperation.getNonMonetaryBalanceUpdateOperation().contains(provisionedHSQService1Balance));
            Assert.assertFalse(dataBalanceOperation.getNonMonetaryBalanceUpdateOperation().contains(provisionedHSQService2Balance));
            Assert.assertFalse(dataBalanceOperation.getNonMonetaryBalanceUpdateOperation().contains(provisionedFUP1Service1Balance));
            Assert.assertFalse(dataBalanceOperation.getNonMonetaryBalanceUpdateOperation().contains(provisionedFUP2Service1Balance));
        }

        @Test
        public void timeWillNotBeCarriedForward() throws OperationFailedException, TransactionException, SQLException {
            long carryForwardVolumeLimit = 25;
            long carryForwardTimeLimit = QUOTA_UNDEFINED;

            QuotaProfile quotaProfile = getQuotaProfile(QuotaUsageType.VOLUME, BalanceLevel.FUP1, carryForwardVolumeLimit, carryForwardTimeLimit);
            doReturn(quotaProfile).when(policyRepository).getQuotaProfile(anyString(),anyString());

            carryForwardOperation.performCarryForwardOperation(expiredHSQService1Balance, dataBalanceOperation, nonMonetoryBalanceList);
            carryForwardOperation.performCarryForwardOperation(expiredHSQService2Balance, dataBalanceOperation, nonMonetoryBalanceList);
            carryForwardOperation.performCarryForwardOperation(expiredFUP1Service1Balance, dataBalanceOperation, nonMonetoryBalanceList);
            carryForwardOperation.performCarryForwardOperation(expiredFUP2Service1Balance, dataBalanceOperation, nonMonetoryBalanceList);

            Assert.assertEquals(provisionedHSQService1Balance.getCarryForwardTime(), ZERO_CARRY_FORWARD);
            Assert.assertEquals(provisionedHSQService2Balance.getCarryForwardTime(), ZERO_CARRY_FORWARD);
            Assert.assertEquals(provisionedFUP1Service1Balance.getCarryForwardTime(), ZERO_CARRY_FORWARD);
            Assert.assertEquals(provisionedFUP2Service1Balance.getCarryForwardVolume(), ZERO_CARRY_FORWARD);

            Assert.assertEquals(provisionedHSQService1Balance.getBillingCycleAvailableTime(), expiredHSQService1Balance.getBillingCycleAvailableTime());
            Assert.assertEquals(provisionedHSQService2Balance.getBillingCycleAvailableTime(), expiredHSQService2Balance.getBillingCycleAvailableTime());
            Assert.assertEquals(provisionedFUP1Service1Balance.getBillingCycleAvailableTime(), expiredFUP1Service1Balance.getBillingCycleAvailableTime());
            Assert.assertEquals(provisionedFUP2Service1Balance.getBillingCycleAvailableTime(), expiredFUP2Service1Balance.getBillingCycleAvailableTime());
        }
    }


    @Test
    public void unlimitedQuotaWillNotBeCarriedForward() throws OperationFailedException, TransactionException, SQLException {
        long carryForwardVolumeLimit = CommonConstants.QUOTA_UNLIMITED;
        long carryForwardTimeLimit = CommonConstants.QUOTA_UNLIMITED;
        QuotaProfile quotaProfile = getQuotaProfile(QuotaUsageType.VOLUME, BalanceLevel.FUP1, carryForwardVolumeLimit, carryForwardTimeLimit);

        long billingCycleResetTime = getTime(12, 0);
        long billingCycleResetTimeForNextMonth = getTime(12,1);
        String renewalInterval = "0:TILL_BILL_DATE";

        NonMonetoryBalance expiredHSQService1Balance = createServiceRGNonMonitoryBalances(UUID.randomUUID().toString(), billingCycleResetTime, SUBSCRIBER_IDENTITY,
                ResetBalanceStatus.RESET_DONE, QUOTA_UNLIMITED, QUOTA_UNLIMITED - 5000,CommonConstants.QUOTA_UNDEFINED, CommonConstants.QUOTA_UNDEFINED, renewalInterval, null, 0, 1);
        NonMonetoryBalance expiredHSQService2Balance = createServiceRGNonMonitoryBalances(UUID.randomUUID().toString(), billingCycleResetTime, SUBSCRIBER_IDENTITY,
                ResetBalanceStatus.RESET_DONE, 300,150,CommonConstants.QUOTA_UNDEFINED, CommonConstants.QUOTA_UNDEFINED, renewalInterval, null, 0, 4);
        NonMonetoryBalance expiredFUP1Service1Balance = createServiceRGNonMonitoryBalances(UUID.randomUUID().toString(), billingCycleResetTime, SUBSCRIBER_IDENTITY,
                ResetBalanceStatus.RESET_DONE, QUOTA_UNLIMITED, QUOTA_UNLIMITED - 5000,CommonConstants.QUOTA_UNDEFINED, CommonConstants.QUOTA_UNDEFINED,renewalInterval, null, 1, 1);
        NonMonetoryBalance expiredFUP2Service1Balance = createServiceRGNonMonitoryBalances(UUID.randomUUID().toString(), billingCycleResetTime, SUBSCRIBER_IDENTITY,
                ResetBalanceStatus.RESET_DONE, 500, 250, CommonConstants.QUOTA_UNDEFINED, CommonConstants.QUOTA_UNDEFINED,renewalInterval, null, 2, 1);

        doReturn(quotaProfile).when(policyRepository).getQuotaProfile(anyString(),anyString());

        List<NonMonetoryBalance> nonMonetoryBalanceList = new ArrayList<>();
        nonMonetoryBalanceList.add(expiredHSQService1Balance);
        nonMonetoryBalanceList.add(expiredHSQService2Balance);
        nonMonetoryBalanceList.add(expiredFUP1Service1Balance);
        nonMonetoryBalanceList.add(expiredFUP2Service1Balance);

        DataBalanceOperation dataBalanceOperation = new DataBalanceOperation();
        NonMonetoryBalance provisionedHSQService1Balance = createServiceRGNonMonitoryBalances(UUID.randomUUID().toString(), billingCycleResetTimeForNextMonth, SUBSCRIBER_IDENTITY,
                ResetBalanceStatus.RESET, QUOTA_UNLIMITED, QUOTA_UNLIMITED, CommonConstants.QUOTA_UNDEFINED, CommonConstants.QUOTA_UNDEFINED, renewalInterval, null, 0, 1);
        NonMonetoryBalance provisionedHSQService2Balance = createServiceRGNonMonitoryBalances(UUID.randomUUID().toString(), billingCycleResetTimeForNextMonth, SUBSCRIBER_IDENTITY,
                ResetBalanceStatus.RESET, 300, 300, CommonConstants.QUOTA_UNDEFINED, CommonConstants.QUOTA_UNDEFINED, renewalInterval, null, 0, 4);
        NonMonetoryBalance provisionedFUP1Service1Balance = createServiceRGNonMonitoryBalances(UUID.randomUUID().toString(), billingCycleResetTimeForNextMonth, SUBSCRIBER_IDENTITY,
                ResetBalanceStatus.RESET, QUOTA_UNLIMITED, QUOTA_UNLIMITED, CommonConstants.QUOTA_UNDEFINED, CommonConstants.QUOTA_UNDEFINED, renewalInterval, null, 1, 1);
        NonMonetoryBalance provisionedFUP2Service1Balance = createServiceRGNonMonitoryBalances(UUID.randomUUID().toString(), billingCycleResetTimeForNextMonth, SUBSCRIBER_IDENTITY,
                ResetBalanceStatus.RESET, 500, 500, CommonConstants.QUOTA_UNDEFINED, CommonConstants.QUOTA_UNDEFINED, renewalInterval, null, 2, 1);

        dataBalanceOperation.setNonMonetaryBalanceInsertOperations(provisionedHSQService1Balance);
        dataBalanceOperation.setNonMonetaryBalanceInsertOperations(provisionedHSQService2Balance);
        dataBalanceOperation.setNonMonetaryBalanceInsertOperations(provisionedFUP1Service1Balance);
        dataBalanceOperation.setNonMonetaryBalanceInsertOperations(provisionedFUP2Service1Balance);

        carryForwardOperation.performCarryForwardOperation(expiredHSQService1Balance, dataBalanceOperation, nonMonetoryBalanceList);
        carryForwardOperation.performCarryForwardOperation(expiredHSQService2Balance, dataBalanceOperation, nonMonetoryBalanceList);
        carryForwardOperation.performCarryForwardOperation(expiredFUP1Service1Balance, dataBalanceOperation, nonMonetoryBalanceList);
        carryForwardOperation.performCarryForwardOperation(expiredFUP2Service1Balance, dataBalanceOperation, nonMonetoryBalanceList);

        Assert.assertEquals(provisionedHSQService1Balance.getCarryForwardVolume(), ZERO_CARRY_FORWARD);
        Assert.assertEquals(provisionedHSQService2Balance.getCarryForwardVolume(), expiredHSQService2Balance.getBillingCycleAvailableVolume());
        Assert.assertEquals(provisionedFUP1Service1Balance.getCarryForwardVolume(), ZERO_CARRY_FORWARD);
        Assert.assertEquals(provisionedFUP2Service1Balance.getCarryForwardVolume(), ZERO_CARRY_FORWARD);

        Assert.assertTrue(provisionedHSQService1Balance.getBillingCycleAvailableVolume() ==
                QUOTA_UNLIMITED);
        Assert.assertTrue(provisionedHSQService2Balance.getBillingCycleAvailableVolume() ==
                provisionedHSQService2Balance.getBillingCycleTotalVolume() + expiredHSQService2Balance.getBillingCycleAvailableVolume());
        Assert.assertTrue(provisionedFUP1Service1Balance.getBillingCycleAvailableVolume() ==
                QUOTA_UNLIMITED);
        Assert.assertEquals(provisionedFUP2Service1Balance.getBillingCycleAvailableVolume(),expiredFUP2Service1Balance.getBillingCycleTotalVolume());
    }

    private QuotaProfile getQuotaProfile(QuotaUsageType quotaUsageType, BalanceLevel balanceLevel, long carryForwardVolumeLimit, long carryForwardTimeLimit) {
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

        hsq.put("DATA_SERVICE_TYPE_1", new RncProfileDetail("hsq_Quota_"+dummyName, dataServiceType1, 0, rg, allowedUsageMap, 0, 0, 0, 0, DataUnit.BYTE.name(), TimeUnit.SECOND.name(), 0.0, null,
                quotaUsageType,null, quotaProfileId, true, pccProfileName, carryForwardVolumeLimit, carryForwardTimeLimit, "test"));
        hsq.put("DATA_SERVICE_TYPE_4", new RncProfileDetail("hsq_Quota_"+dummyName, dataServiceType4, 0, rg, allowedUsageMap, 0, 0, 0, 0, DataUnit.BYTE.name(), TimeUnit.SECOND.name(), 0.0, null,
                quotaUsageType,null, quotaProfileId, true, pccProfileName, carryForwardVolumeLimit, carryForwardTimeLimit, "test"));

        fup1.put("DATA_SERVICE_TYPE_1", new RncProfileDetail("hsq_Quota_"+dummyName, dataServiceType1, 1, rg, allowedUsageMap, 0, 0, 0, 0, DataUnit.BYTE.name(), TimeUnit.SECOND.name(), 0.0, null,
                quotaUsageType,null, quotaProfileId, false, pccProfileName, carryForwardVolumeLimit, carryForwardTimeLimit, "test"));
        fup2.put("DATA_SERVICE_TYPE_1", new RncProfileDetail("hsq_Quota_"+dummyName, dataServiceType1, 2, rg, allowedUsageMap, 0, 0, 0, 0, DataUnit.BYTE.name(), TimeUnit.SECOND.name(), 0.0, null,
                quotaUsageType,null, quotaProfileId, false, pccProfileName, carryForwardVolumeLimit, carryForwardTimeLimit, "test"));

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