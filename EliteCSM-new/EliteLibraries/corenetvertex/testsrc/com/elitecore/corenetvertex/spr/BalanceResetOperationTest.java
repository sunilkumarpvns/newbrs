package com.elitecore.corenetvertex.spr;

import com.elitecore.commons.base.FixedTimeSource;
import com.elitecore.corenetvertex.constants.BalanceLevel;
import com.elitecore.corenetvertex.constants.CommonStatusValues;
import com.elitecore.corenetvertex.constants.QuotaProfileType;
import com.elitecore.corenetvertex.constants.RenewalIntervalUnit;
import com.elitecore.corenetvertex.core.transaction.Transaction;
import com.elitecore.corenetvertex.core.transaction.TransactionException;
import com.elitecore.corenetvertex.data.CarryForwardStatus;
import com.elitecore.corenetvertex.data.ResetBalanceStatus;
import com.elitecore.corenetvertex.pm.DummyPolicyRepository;
import com.elitecore.corenetvertex.pm.factory.RnCQuotaProfileFactory;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfile;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfileDetail;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.RncProfileDetail;
import com.elitecore.corenetvertex.pm.util.MockBasePackage;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.corenetvertex.spr.data.SPRInfoImpl;
import com.elitecore.corenetvertex.spr.data.Subscription;
import com.elitecore.corenetvertex.spr.data.SubscriptionType;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import junitparams.JUnitParamsRunner;
import org.apache.commons.lang3.RandomUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.UUID;

import static com.elitecore.corenetvertex.constants.SubscriptionState.SUBSCRIBED;
import static net.sf.json.test.JSONAssert.assertEquals;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.spy;

@RunWith(JUnitParamsRunner.class)
public class BalanceResetOperationTest {

    private final String SUBSCRIPTION_ID = "testSubscription";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private DummyPolicyRepository policyRepository;

    ResetBalanceStatus status;
    Timestamp subscriptionExpiryTime;
    String quotaProfileId;
    private RncProfileDetail rncProfileDetail;
    private BalanceResetOperation balanceResetOperation;
    private Transaction transaction;

    @Before
    public void setUp() throws Exception {
        status = ResetBalanceStatus.RESET;
        subscriptionExpiryTime = new Timestamp(System.currentTimeMillis());
        MockitoAnnotations.initMocks(this);
        transaction = spy(Transaction.class);

        MockBasePackage mockBasePackage = MockBasePackage.create("testPkgId", UUID.randomUUID().toString());
        mockBasePackage.quotaProfileTypeIsRnC();

        quotaProfileId = UUID.randomUUID().toString();
        rncProfileDetail = new RnCQuotaProfileFactory(quotaProfileId, UUID.randomUUID().toString()).randomBalanceWithRate().create();
        Map<String, QuotaProfileDetail> hashMap = new HashMap<>();
        hashMap.put("test", rncProfileDetail);
        QuotaProfile quotaProfile = new QuotaProfile("test", mockBasePackage.getName(), quotaProfileId, BalanceLevel.HSQ, 2, RenewalIntervalUnit.MONTH, QuotaProfileType.RnC_BASED, Arrays.asList(hashMap), CommonStatusValues.ENABLE.isBooleanValue(),CommonStatusValues.ENABLE.isBooleanValue());
        mockBasePackage.mockQuotaProfie(quotaProfile);

        policyRepository = new DummyPolicyRepository();
        policyRepository.addBasePackage(mockBasePackage);

        balanceResetOperation = new BalanceResetOperation(policyRepository, new FixedTimeSource(getTime(13, 0)));
    }

    @Test
    public void newBalanceCreatedWithNOT_CARRY_FORWARDstatusWhenPackageExpiresBeforeRenewalInterval() throws OperationFailedException, TransactionException, SQLException {
        subscriptionExpiryTime = new Timestamp(getTime(12, 1));
        int billDay = 13;
        long billingCycleResetTime = getTime(12, 0);
        String renewalInterval = "0:TILL_BILL_DATE";

        NonMonetoryBalance nonMonetaryBalance = prepareNonMonetaryBalance(billingCycleResetTime, renewalInterval, status, CarryForwardStatus.CARRY_FORWARD);

        NonMonetoryBalance provisionedBalances = balanceResetOperation.performDataBalanceOperations(nonMonetaryBalance, createSprInfo("testSub", billDay));

        Assert.assertNotNull(provisionedBalances);
        Assert.assertEquals(provisionedBalances.getCarryForwardStatus(), CarryForwardStatus.NOT_CARRY_FORWARD);
    }

   @Test
    public void addsNewBalanceRowForTheNextBillingCycleOneDayBeforeBalanceExpiryWhenRenewalIntervalIsTillBillDate() throws OperationFailedException, TransactionException, SQLException {
        int billDay = 15;
        long billingCycleResetTime = getTime(14, 0);
        String renewalInterval = "0:TILL_BILL_DATE";

        NonMonetoryBalance nonMonetaryBalances = prepareNonMonetaryBalance(billingCycleResetTime, renewalInterval, status, CarryForwardStatus.CARRY_FORWARD);

        NonMonetoryBalance provisionedBalances = balanceResetOperation.performDataBalanceOperations(nonMonetaryBalances, createSprInfo("testSub", billDay));

        assertNotNull(provisionedBalances);
        assertEquals(nonMonetaryBalances.getBillingCycleResetTime() + 1, provisionedBalances.getStartTime());
        assertEquals(getTime(14, 1, 2018, 23, 59, 59, 999), provisionedBalances.getBillingCycleResetTime());
    }

    @Test
    public void addsNewBalanceRowForTheNextBillingCycleOneDayAfterBalanceExpiryWhenRenewalIntervalIsTillBillDate() throws OperationFailedException, TransactionException, SQLException {
        int billDay = 13;
        long billingCycleResetTime = getTime(12, 0);
        String renewalInterval = "0:TILL_BILL_DATE";

        NonMonetoryBalance nonMonetaryBalances = prepareNonMonetaryBalance(billingCycleResetTime, renewalInterval, status, CarryForwardStatus.CARRY_FORWARD);

        NonMonetoryBalance provisionedBalances = balanceResetOperation.performDataBalanceOperations(nonMonetaryBalances, createSprInfo("testSub", billDay));

        assertNotNull(provisionedBalances);
        assertEquals(nonMonetaryBalances.getBillingCycleResetTime() + 1, provisionedBalances.getStartTime());
        assertEquals(getTime(12, 1, 2018, 23, 59, 59, 999), provisionedBalances.getBillingCycleResetTime());
    }

    @Test
    public void addsProratedBalanceAccordingToNewBillDayWhenRequestReceivedAfterOneDayOfBalanceExpiryAndRenewalIntervalIsTillBillDate() throws OperationFailedException, TransactionException, SQLException {
        int newBillDay = 20;
        long billingCycleResetTime = getTime(12, 0);
        String renewalInterval = "0:TILL_BILL_DATE";

        NonMonetoryBalance nonMonetaryBalances = prepareNonMonetaryBalance(billingCycleResetTime, renewalInterval, status, CarryForwardStatus.CARRY_FORWARD);

        NonMonetoryBalance provisionedBalances = balanceResetOperation.performDataBalanceOperations(nonMonetaryBalances, createSprInfo("testSub", newBillDay));

        assertNotNull(provisionedBalances);
        assertEquals(nonMonetaryBalances.getBillingCycleResetTime() + 1, provisionedBalances.getStartTime());
        assertEquals(getTime(19, 0, 2018, 23, 59, 59, 999), provisionedBalances.getBillingCycleResetTime());

        assertEquals(259, provisionedBalances.getBillingCycleAvailableVolume());
        assertEquals(259, provisionedBalances.getBillingCycleTotalVolume());

        assertEquals(259, provisionedBalances.getBillingCycleAvailableTime());
        assertEquals(259, provisionedBalances.getBillingCycleTime());
    }

    @Test
    public void AddsBalanceForTheLastBillingCycleOfASubscriptionWithStatusOfNOTRESETWhenRenewalUnitIsTillBillDate() throws OperationFailedException, TransactionException, SQLException {
        subscriptionExpiryTime = new Timestamp(getTime(12, 1));
        int billDay = 13;
        long billingCycleResetTime = getTime(12, 0);
        String renewalInterval = "0:TILL_BILL_DATE";

        NonMonetoryBalance nonMonetaryBalances = prepareNonMonetaryBalance(billingCycleResetTime, renewalInterval, status, CarryForwardStatus.CARRY_FORWARD);

        NonMonetoryBalance provisionedBalances = balanceResetOperation.performDataBalanceOperations(nonMonetaryBalances, createSprInfo("testSub", billDay));
        
        assertNotNull(provisionedBalances);
        assertEquals(nonMonetaryBalances.getBillingCycleResetTime() + 1, provisionedBalances.getStartTime());
        assertEquals(getTime(12, 1, 2018, 23, 59, 59, 999), provisionedBalances.getBillingCycleResetTime());

        assertEquals(ResetBalanceStatus.NOT_RESET, provisionedBalances.getStatus());
    }


    @Test
    public void addsNewBalanceForTheSameMonthOneDayBeforeBalanceExpiryWhenRenewalIntervalIsMonthEnd() throws OperationFailedException, TransactionException, SQLException {
        int billDay = 15;
        long billingCycleResetTime = getTime(14, 0);
        String renewalInterval = "1:MONTH_END";

        NonMonetoryBalance nonMonetaryBalances = prepareNonMonetaryBalance(billingCycleResetTime, renewalInterval, status, CarryForwardStatus.CARRY_FORWARD);

        NonMonetoryBalance provisionedBalances = balanceResetOperation.performDataBalanceOperations(nonMonetaryBalances, createSprInfo("testSub", billDay));


        assertNotNull(provisionedBalances);
        assertEquals(nonMonetaryBalances.getBillingCycleResetTime() + 1, provisionedBalances.getStartTime());
        assertEquals(getTime(31, 0, 2018, 23, 59, 59, 999), provisionedBalances.getBillingCycleResetTime());
    }

    @Test
    public void addsNewBalanceForTheNextMonthOneDayBeforeBalanceExpiryWhenRenewalIntervalIsMonthEnd() throws OperationFailedException, TransactionException, SQLException {
        balanceResetOperation = new BalanceResetOperation(policyRepository, new FixedTimeSource(getTime(30, 0)));
        int billDay = 15;
        long billingCycleResetTime = getTime(31, 0);
        String renewalInterval = "1:MONTH_END";

        NonMonetoryBalance nonMonetaryBalances = prepareNonMonetaryBalance(billingCycleResetTime, renewalInterval, status, CarryForwardStatus.CARRY_FORWARD);

        NonMonetoryBalance provisionedBalances = balanceResetOperation.performDataBalanceOperations(nonMonetaryBalances, createSprInfo("testSub", billDay));


        assertNotNull(provisionedBalances);
        assertEquals(nonMonetaryBalances.getBillingCycleResetTime() + 1, provisionedBalances.getStartTime());
        assertEquals(getTime(28, 1, 2018, 23, 59, 59, 999), provisionedBalances.getBillingCycleResetTime());
    }

    @Test
    public void addsNewBalanceForTheSameMonthOneDayAfterBalanceExpiryWhenRenewalIntervalIsMonthEnd() throws OperationFailedException, TransactionException, SQLException {
        int billDay = 13;
        long billingCycleResetTime = getTime(12, 0);
        String renewalInterval = "1:MONTH_END";

        NonMonetoryBalance nonMonetaryBalances = prepareNonMonetaryBalance(billingCycleResetTime, renewalInterval, status, CarryForwardStatus.CARRY_FORWARD);

        NonMonetoryBalance provisionedBalances = balanceResetOperation.performDataBalanceOperations(nonMonetaryBalances, createSprInfo("testSub", billDay));


        assertNotNull(provisionedBalances);
        assertEquals(nonMonetaryBalances.getBillingCycleResetTime() + 1, provisionedBalances.getStartTime());
        assertEquals(getTime(31, 0, 2018, 23, 59, 59, 999), provisionedBalances.getBillingCycleResetTime());
    }

    @Test
    public void addsNewBalanceForTheLastDayOfTheMonthOneDayAfterBalanceExpiryWhenRenewalIntervalIsMonthEnd() throws OperationFailedException, TransactionException, SQLException {
        balanceResetOperation = new BalanceResetOperation(policyRepository, new FixedTimeSource(getTime(1, 1)));
        int billDay = 15;
        long billingCycleResetTime = getTime(31, 0, 2018, 23, 59, 59, 999);
        String renewalInterval = "1:MONTH_END";

        NonMonetoryBalance nonMonetaryBalances = prepareNonMonetaryBalance(billingCycleResetTime, renewalInterval, status, CarryForwardStatus.CARRY_FORWARD);

        NonMonetoryBalance provisionedBalances = balanceResetOperation.performDataBalanceOperations(nonMonetaryBalances, createSprInfo("testSub", billDay));


        assertNotNull(provisionedBalances);
        assertEquals(nonMonetaryBalances.getBillingCycleResetTime() + 1, provisionedBalances.getStartTime());
        assertEquals(getTime(28, 1, 2018, 23, 59, 59, 999), provisionedBalances.getBillingCycleResetTime());
    }

    @Test
    public void addsNewBalanceForTheNextMonthOneDayBeforeBalanceExpiryWhenRenewalIntervalIsMonth() throws OperationFailedException, TransactionException, SQLException {
        int billDay = 15;
        long billingCycleResetTime = getTime(14, 0);
        String renewalInterval = "1:MONTH";

        NonMonetoryBalance nonMonetaryBalances = prepareNonMonetaryBalance(billingCycleResetTime, renewalInterval, status, CarryForwardStatus.CARRY_FORWARD);

        NonMonetoryBalance provisionedBalances = balanceResetOperation.performDataBalanceOperations(nonMonetaryBalances, createSprInfo("testSub", billDay));


        assertNotNull(provisionedBalances);
        assertEquals(nonMonetaryBalances.getBillingCycleResetTime() + 1, provisionedBalances.getStartTime());
        assertEquals(getTime(14, 1, 2018, 23, 59, 59, 999), provisionedBalances.getBillingCycleResetTime());
    }

    @Test
    public void addsNewBalanceForTheNextMonthOneDayAfterBalanceExpiryWhenRenewalIntervalIsMonth() throws OperationFailedException, TransactionException, SQLException {
        int billDay = 13;
        long billingCycleResetTime = getTime(12, 0);
        String renewalInterval = "1:MONTH";

        NonMonetoryBalance nonMonetaryBalances = prepareNonMonetaryBalance(billingCycleResetTime, renewalInterval, status, CarryForwardStatus.CARRY_FORWARD);

        NonMonetoryBalance provisionedBalances = balanceResetOperation.performDataBalanceOperations(nonMonetaryBalances, createSprInfo("testSub", billDay));


        assertNotNull(provisionedBalances);
        assertEquals(nonMonetaryBalances.getBillingCycleResetTime() + 1, provisionedBalances.getStartTime());
        assertEquals(getTime(12, 1, 2018, 23, 59, 59, 999), provisionedBalances.getBillingCycleResetTime());
    }

    @Test
    public void addsNewBalanceForTheNextDayOnTheNextDayOfBalanceExpiryWhenRenewalIntervalIsOneDay() throws OperationFailedException, TransactionException, SQLException {
        int billDay = 15;
        long billingCycleResetTime = getTime(15, 0);
        String renewalInterval = "1:DAY";
        balanceResetOperation = new BalanceResetOperation(policyRepository, new FixedTimeSource(getTime(16, 0)));

        NonMonetoryBalance nonMonetaryBalances = prepareNonMonetaryBalance(billingCycleResetTime, renewalInterval, status, CarryForwardStatus.CARRY_FORWARD);

        NonMonetoryBalance provisionedBalances = balanceResetOperation.performDataBalanceOperations(nonMonetaryBalances, createSprInfo("testSub", billDay));


        assertNotNull(provisionedBalances);
        assertEquals(nonMonetaryBalances.getBillingCycleResetTime() + 1, provisionedBalances.getStartTime());
        assertEquals(getTime(16, 0), provisionedBalances.getBillingCycleResetTime());
    }

    @Test
    public void addsNewBalanceForTheNextSevenDaysOnTheDayOfBalanceExpiryWhenRenewalIntervalIsSevenDay() throws OperationFailedException, TransactionException, SQLException {
        int billDay = 15;
        long billingCycleResetTime = getTime(13, 0);
        String renewalInterval = "7:DAY";

        NonMonetoryBalance nonMonetaryBalances = prepareNonMonetaryBalance(billingCycleResetTime, renewalInterval, status, CarryForwardStatus.CARRY_FORWARD);

        NonMonetoryBalance provisionedBalances = balanceResetOperation.performDataBalanceOperations(nonMonetaryBalances, createSprInfo("testSub", billDay));


        assertNotNull(provisionedBalances);
        assertEquals(nonMonetaryBalances.getBillingCycleResetTime() + 1, provisionedBalances.getStartTime());
        assertEquals(getTime(20, 0, 2018, 23, 59, 59, 999), provisionedBalances.getBillingCycleResetTime());
    }

    @Test
    public void addsNewBalanceTillNextHourForTheExpiredOrToBeExpiredBalanceOnAnHourlyBasisBeforeTheBalanceExpiry() throws OperationFailedException, TransactionException, SQLException {
        int billDay = 15;
        long billingCycleResetTime = getTime(15, 0, 2018, 12, 00, 00, 000);
        String renewalInterval = "1:HOUR";

        NonMonetoryBalance nonMonetaryBalances = prepareNonMonetaryBalance(billingCycleResetTime, renewalInterval, status, CarryForwardStatus.CARRY_FORWARD);

        NonMonetoryBalance provisionedBalances = balanceResetOperation.performDataBalanceOperations(nonMonetaryBalances, createSprInfo("testSub", billDay));


        assertNotNull(provisionedBalances);
        assertEquals(nonMonetaryBalances.getBillingCycleResetTime() + 1, provisionedBalances.getStartTime());
        assertEquals(getTime(15, 0, 2018, 13, 00, 00, 000), provisionedBalances.getBillingCycleResetTime());
    }

    @Test
    public void addsNewBalanceTillNextHourForTheExpiredOrToBeExpiredBalanceOnAnHourlyBasisAfterTheBalanceExpiry() throws OperationFailedException, TransactionException, SQLException {
        int billDay = 15;
        long billingCycleResetTime = getTime(15, 0, 2018, 12, 00, 00, 000);
        String renewalInterval = "1:HOUR";
        balanceResetOperation = new BalanceResetOperation(policyRepository, new FixedTimeSource(getTime(15, 0, 2018, 13, 00, 00, 000)));

        NonMonetoryBalance nonMonetaryBalances = prepareNonMonetaryBalance(billingCycleResetTime, renewalInterval, status, CarryForwardStatus.CARRY_FORWARD);

        NonMonetoryBalance provisionedBalances = balanceResetOperation.performDataBalanceOperations(nonMonetaryBalances, createSprInfo("testSub", billDay));


        assertNotNull(provisionedBalances);
        assertEquals(nonMonetaryBalances.getBillingCycleResetTime() + 1, provisionedBalances.getStartTime());
        assertEquals(getTime(15, 0, 2018, 13, 00, 00, 000), provisionedBalances.getBillingCycleResetTime());
    }

    @Test
    public void addsNewBalanceTillMidNightForTheExpiredOrToBeExpiredBalanceWhenTheRenewalIntervalIsMidNight() throws OperationFailedException, TransactionException, SQLException {
        int billDay = 15;
        long billingCycleResetTime = getTime(15, 0, 2018, 23, 59, 59, 999);
        String renewalInterval = "1:MID_NIGHT";
        balanceResetOperation = new BalanceResetOperation(policyRepository, new FixedTimeSource(getTime(16, 0, 2018, 00, 00, 01, 000)));

        NonMonetoryBalance nonMonetaryBalances = prepareNonMonetaryBalance(billingCycleResetTime, renewalInterval, status, CarryForwardStatus.CARRY_FORWARD);

        NonMonetoryBalance provisionedBalances = balanceResetOperation.performDataBalanceOperations(nonMonetaryBalances, createSprInfo("testSub", billDay));


        assertNotNull(provisionedBalances);
        assertEquals(nonMonetaryBalances.getBillingCycleResetTime() + 1, provisionedBalances.getStartTime());
        assertEquals(getTime(16, 0, 2018, 23, 59, 59, 999), provisionedBalances.getBillingCycleResetTime());
    }

    @Test
    public void addsBaseSubscriptionBalanceForNextBillingCycleWhenRenewalIntervalIsTillBillDate() throws SQLException, TransactionException, OperationFailedException {
        int billDay = 13;
        long billingCycleResetTime = getTime(12, 0);
        String renewalInterval = "0:TILL_BILL_DATE";

        NonMonetoryBalance nonMonetaryBalances = createServiceRGNonMonitoryBalances(UUID.randomUUID().toString(), billingCycleResetTime, ResetBalanceStatus.RESET, renewalInterval, null, CarryForwardStatus.CARRY_FORWARD);

        NonMonetoryBalance provisionedBalances = balanceResetOperation.performDataBalanceOperations(nonMonetaryBalances, createSprInfo("testSub", billDay));

        assertNotNull(provisionedBalances);
        assertEquals(nonMonetaryBalances.getBillingCycleResetTime() + 1, provisionedBalances.getStartTime());
        assertEquals(getTime(12, 1, 2018, 23, 59, 59, 999), provisionedBalances.getBillingCycleResetTime());
    }

    @Test
    public void additionOfNewBalanceIsSkippedWhenCurrentTimeIsExactlyEqualToTwoDaysBeforeBalanceExpiry() throws OperationFailedException, TransactionException, SQLException {
        int billDay = 16;
        long billingCycleResetTime = getTime(15, 0);
        String renewalInterval = "0:TILL_BILL_DATE";

        NonMonetoryBalance nonMonetaryBalances = prepareNonMonetaryBalance(billingCycleResetTime, renewalInterval, status, CarryForwardStatus.CARRY_FORWARD);

        NonMonetoryBalance testSub = balanceResetOperation.performDataBalanceOperations(nonMonetaryBalances, createSprInfo("testSub", billDay));

        assertThat(testSub, is(nullValue()));
    }

    @Test
    public void additionOfNewBalanceIsSkippedWhenBalanceIsNotExpiredWithinTwoDays() throws OperationFailedException, TransactionException, SQLException {
        int billDay = 20;
        long billingCycleResetTime = getTime(19, 0);
        String renewalInterval = "0:TILL_BILL_DATE";

        NonMonetoryBalance nonMonetaryBalances = prepareNonMonetaryBalance(billingCycleResetTime, renewalInterval, status, CarryForwardStatus.CARRY_FORWARD);

        NonMonetoryBalance testSub = balanceResetOperation.performDataBalanceOperations(nonMonetaryBalances, createSprInfo("testSub", billDay));

        assertThat(testSub, is(nullValue()));
    }

    @Test
    public void additionOfNewBalanceIsSkippedWhenRenewalIntervalIsNotConfigured() throws OperationFailedException, TransactionException, SQLException {
        int billDay = 15;
        long billingCycleResetTime = getTime(14, 0);
        String renewalInterval = "";

        NonMonetoryBalance nonMonetaryBalances = prepareNonMonetaryBalance(billingCycleResetTime, renewalInterval, status, CarryForwardStatus.CARRY_FORWARD);

        NonMonetoryBalance testSub = balanceResetOperation.performDataBalanceOperations(nonMonetaryBalances, createSprInfo("testSub", billDay));

        assertThat(testSub, is(nullValue()));
    }

    @Test
    public void skipsAdditionOfNewBalanceWhenResetStatusOfExpiredOrToBeExpiredBalanceIsAnythingElseOtherThanRESET() throws OperationFailedException, TransactionException, SQLException {
        status = ResetBalanceStatus.NOT_RESET;
        int billDay = 20;
        long billingCycleResetTime = getTime(19, 0);
        String renewalInterval = "0:TILL_BILL_DATE";

        NonMonetoryBalance nonMonetaryBalances = prepareNonMonetaryBalance(billingCycleResetTime, renewalInterval, status, CarryForwardStatus.CARRY_FORWARD);

        NonMonetoryBalance testSub = balanceResetOperation.performDataBalanceOperations(nonMonetaryBalances, createSprInfo("testSub", billDay));

        assertThat(testSub, is(nullValue()));
    }

    @Test
    public void DAILY_RESET_TIME_is_properly_calculated_as_the_end_of_same_day_as_that_of_the_newly_provisioned_balance_start_date() throws SQLException, TransactionException, OperationFailedException {
        int billDay = 15;
        long billingCycleResetTime = getTime(14, 0);
        String renewalInterval = "1:MONTH";

        NonMonetoryBalance nonMonetaryBalances = prepareNonMonetaryBalance(billingCycleResetTime, renewalInterval, status, CarryForwardStatus.CARRY_FORWARD);

        NonMonetoryBalance provisionedBalances = balanceResetOperation.performDataBalanceOperations(nonMonetaryBalances, createSprInfo("testSub", billDay));


        assertNotNull(provisionedBalances);
        assertEquals(nonMonetaryBalances.getBillingCycleResetTime() + 1, provisionedBalances.getStartTime());
        assertEquals(getTime(14, 1, 2018, 23, 59, 59, 999), provisionedBalances.getBillingCycleResetTime());
        assertEquals(getTime(15, 0, 2018, 23, 59, 59, 0), provisionedBalances.getDailyResetTime());
    }

    @Test
    public void WEEKLY_RESET_TIME_is_properly_calculated_as_the_last_day_of_the_week_starting_from_the_start_date_of_newly_provisioned_balance() throws SQLException, TransactionException, OperationFailedException {
        int billDay = 15;
        long billingCycleResetTime = getTime(14, 0);
        String renewalInterval = "1:MONTH";

        NonMonetoryBalance nonMonetaryBalances = prepareNonMonetaryBalance(billingCycleResetTime, renewalInterval, status, CarryForwardStatus.CARRY_FORWARD);

        NonMonetoryBalance provisionedBalances = balanceResetOperation.performDataBalanceOperations(nonMonetaryBalances, createSprInfo("testSub", billDay));


        assertNotNull(provisionedBalances);
        assertEquals(nonMonetaryBalances.getBillingCycleResetTime() + 1, provisionedBalances.getStartTime());
        assertEquals(getTime(14, 1, 2018, 23, 59, 59, 999), provisionedBalances.getBillingCycleResetTime());
        assertEquals(getTime(20, 0, 2018, 23, 59, 59, 0), provisionedBalances.getWeeklyResetTime());
    }

    @Test
    public void newlyAddedBalancesShouldHaveAvailableBalanceEqualToTotalBalance() throws SQLException, TransactionException, OperationFailedException {
        int billDay = 15;
        long billingCycleResetTime = getTime(14, 0);
        String renewalInterval = "1:MONTH";

        NonMonetoryBalance nonMonetaryBalances = prepareNonMonetaryBalance(billingCycleResetTime, renewalInterval, status, CarryForwardStatus.CARRY_FORWARD);

        NonMonetoryBalance provisionedBalances = balanceResetOperation.performDataBalanceOperations(nonMonetaryBalances, createSprInfo("testSub", billDay));


        assertNotNull(provisionedBalances);
        assertEquals(nonMonetaryBalances.getBillingCycleResetTime() + 1, provisionedBalances.getStartTime());
        assertEquals(getTime(14, 1, 2018, 23, 59, 59, 999), provisionedBalances.getBillingCycleResetTime());

        assertEquals(provisionedBalances.getBillingCycleTotalVolume(), provisionedBalances.getBillingCycleTotalVolume());
        assertEquals(provisionedBalances.getBillingCycleTime(), provisionedBalances.getBillingCycleAvailableTime());

        Assert.assertEquals(provisionedBalances.getDailyVolume(), 0);
        Assert.assertEquals(provisionedBalances.getDailyTime(), 0);
        Assert.assertEquals(provisionedBalances.getWeeklyVolume(), 0);
        Assert.assertEquals(provisionedBalances.getDailyTime(), 0);
    }

    /*
        This test cases is for documentation purpose as whenever new renewal interval added then we need to add logic in reset operation
    */
    @Test
    public void testCaseFailsWhenAnyNewValueIsAddedInRenewalIntervalUnitEnum(){
        List renewalIntervalUnits = new ArrayList<String>();
        renewalIntervalUnits.add("MONTH");
        renewalIntervalUnits.add("MONTH_END");
        renewalIntervalUnits.add("TILL_BILL_DATE");
        renewalIntervalUnits.add("DAY");
        renewalIntervalUnits.add("HOUR");
        renewalIntervalUnits.add("MID_NIGHT");

        Optional<RenewalIntervalUnit> newlyAddedRenewalIntervalUnit = Arrays.stream(RenewalIntervalUnit.values())
                .filter(renewalIntervalUnit -> renewalIntervalUnits.indexOf(renewalIntervalUnit.name()) == -1)
                .findFirst();

        assertFalse("Renewal Interval unit added", newlyAddedRenewalIntervalUnit.isPresent());
    }

    private SPRInfo createSprInfo(String subscriberIdentity, int billDay) {
        SPRInfoImpl sprInfo = new SPRInfoImpl();
        sprInfo.setSubscriberIdentity(subscriberIdentity);
        sprInfo.setExpiryDate(new Timestamp(System.currentTimeMillis()));
        sprInfo.setBillingDate(billDay);
        addSubscripiton(sprInfo);
        return sprInfo;
    }


    public void addSubscripiton(SPRInfoImpl sprInfo) {
        MockSubscriptionProvider subscriptionProvider = new MockSubscriptionProvider();
        subscriptionProvider.addSubscription(getSubscription());
        sprInfo.setSubscriptionProvider(subscriptionProvider);
    }

    private Subscription getSubscription() {
        Subscription subscription = new Subscription.SubscriptionBuilder()
                .withId("testSubscription")
                .withSubscriberIdentity("testSub")
                .withPackageId("testPkgId")
                .withProductOfferId("testPOId")
                .withStartTime(new Timestamp(getTime(13, 0)))
                .withEndTime(subscriptionExpiryTime)
                .withStatus(SUBSCRIBED)
                .withPriority(100)
                .withType(SubscriptionType.ADDON).build();
        return subscription;
    }

    private NonMonetoryBalance createServiceRGNonMonitoryBalances(String id, long billingCycleResetTime, ResetBalanceStatus status, String renewalInterval, String subscriptionId, CarryForwardStatus carryForwardStatus) {
        Random random = new Random();
        NonMonetoryBalance serviceRgNonMonitoryBalance = new NonMonetoryBalance.NonMonetaryBalanceBuilder(id,
                RandomUtils.nextInt(0, Integer.MAX_VALUE),
                "testPkgId",
                1l,
                UUID.randomUUID().toString(),
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
                .withCarryForwardStatus(carryForwardStatus)
                .build();
        return serviceRgNonMonitoryBalance;
    }

    private NonMonetoryBalance prepareNonMonetaryBalance(long billingCycleResetTime, String renewalInterval, ResetBalanceStatus status, CarryForwardStatus carryForwardStatus) {
        return createServiceRGNonMonitoryBalances(UUID.randomUUID().toString(), billingCycleResetTime, status, renewalInterval, SUBSCRIPTION_ID, carryForwardStatus);
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
