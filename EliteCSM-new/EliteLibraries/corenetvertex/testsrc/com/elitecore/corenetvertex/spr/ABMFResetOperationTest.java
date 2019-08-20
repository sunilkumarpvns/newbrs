package com.elitecore.corenetvertex.spr;

import com.elitecore.commons.base.FixedTimeSource;
import com.elitecore.commons.base.TimeSource;
import com.elitecore.corenetvertex.core.transaction.DummyTransactionFactory;
import com.elitecore.corenetvertex.core.transaction.Transaction;
import com.elitecore.corenetvertex.core.transaction.TransactionException;
import com.elitecore.corenetvertex.data.ResetBalanceStatus;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.unitils.reflectionassert.ReflectionAssert;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static com.elitecore.corenetvertex.data.ResetBalanceStatus.RESET_DONE;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.MockitoAnnotations.initMocks;

public class ABMFResetOperationTest {
    public static final int CURRENT_DAY = 5;
    public static final int CURRENT_MONTH = 1;
    ABMFResetOperations abmfResetOperations;

    @Mock BalanceResetOperation balanceResetOperation;

    @Mock CarryForwardOperation carryForwardOperation;

    @Mock DataBalanceResetDBOperation dataBalanceResetDBOperation;

    @Mock SPRInfo sprInfo;
    private DataBalanceOperation dataBalanceOperation;

    @Mock private DummyTransactionFactory transactionFactory;
    @Mock private Transaction transaction;

    @Rule public ExpectedException expectedException = ExpectedException.none();
    private DataBalanceOperationFactory dataBalanceOperationFactory;

    @Before
    public void setUp() {
        initMocks(this);
        dataBalanceOperationFactory = spy(new DataBalanceOperationFactory());
        abmfResetOperations = new ABMFResetOperations(balanceResetOperation, carryForwardOperation, dataBalanceResetDBOperation, getFixedTimeSource(), dataBalanceOperationFactory);
    }

    @Test
    public void newBalanceIsAddedForExpiredBalance() throws OperationFailedException, SQLException, TransactionException {
        NonMonetoryBalance expiredBalance = createServiceRGNonMonitoryBalances(UUID.randomUUID().toString(), getTime(CURRENT_DAY-1,CURRENT_MONTH));
        NonMonetoryBalance exepectedExpiredBalance = expiredBalance.copy();
        exepectedExpiredBalance.setStatus(RESET_DONE);
        NonMonetoryBalance newProvisionBalanceForExpiredBalance = createServiceRGNonMonitoryBalances(UUID.randomUUID().toString(), getTime(CURRENT_DAY-1,CURRENT_MONTH+1));

        List<NonMonetoryBalance> nonMonetoryBalanceList = new ArrayList<>();
        nonMonetoryBalanceList.add(expiredBalance);

        createDummyTransaction();

        doReturn(newProvisionBalanceForExpiredBalance).when(balanceResetOperation).performDataBalanceOperations(expiredBalance, sprInfo);
        doNothing().when(carryForwardOperation).performCarryForwardOperation(expiredBalance, dataBalanceOperation, nonMonetoryBalanceList);

        DataBalanceOperation dataBalanceOperation = spy(new DataBalanceOperation());
        doReturn(dataBalanceOperation).when(dataBalanceOperationFactory).createDataBalanceOperation();

        abmfResetOperations.performBalanceOperations(sprInfo, transactionFactory, nonMonetoryBalanceList);

        ReflectionAssert.assertReflectionEquals(dataBalanceOperation.getNonMonetaryBalanceInsertOperation().get(0), newProvisionBalanceForExpiredBalance);
        ReflectionAssert.assertReflectionEquals(exepectedExpiredBalance, expiredBalance);
        Assert.assertEquals(expiredBalance.getStatus().name(), RESET_DONE.name());
    }

    private void createDummyTransaction() {
        doReturn(true).when(transactionFactory).isAlive();
        doReturn(transaction).when(transactionFactory).createTransaction();
    }

    @Test(expected = OperationFailedException.class)
    public void operationFailedExceptionThrownWhenTransactionFactoryIsNotAlive() throws OperationFailedException {
        doReturn(false).when(transactionFactory).isAlive();

        List<NonMonetoryBalance> nonMonetoryBalanceList = new ArrayList<>();
        abmfResetOperations.performBalanceOperations(sprInfo, transactionFactory, nonMonetoryBalanceList);
    }

    @Test(expected = OperationFailedException.class)
    public void operationFailedExceptionThrownWhenTransactionIsNotCreated() throws OperationFailedException {
        doReturn(true).when(transactionFactory).isAlive();
        doReturn(null).when(transactionFactory).createTransaction();

        List<NonMonetoryBalance> nonMonetoryBalanceList = new ArrayList<>();
        abmfResetOperations.performBalanceOperations(sprInfo, transactionFactory, nonMonetoryBalanceList);
    }

    @Test
    public void executeDBOperationCalledIfAnyMonetaryBalanceGotProvisioned() throws OperationFailedException, SQLException, TransactionException {
        NonMonetoryBalance expiredBalance = createServiceRGNonMonitoryBalances(UUID.randomUUID().toString(), getTime(CURRENT_DAY-1,CURRENT_MONTH));
        NonMonetoryBalance provisionedBalance = createServiceRGNonMonitoryBalances(UUID.randomUUID().toString(), getTime(CURRENT_DAY-1,CURRENT_MONTH+1));

        List<NonMonetoryBalance> nonMonetoryBalanceList = new ArrayList<>();
        nonMonetoryBalanceList.add(expiredBalance);

        createDummyTransaction();

        doReturn(provisionedBalance).when(balanceResetOperation).performDataBalanceOperations(expiredBalance, sprInfo);
        doNothing().when(carryForwardOperation).performCarryForwardOperation(expiredBalance, dataBalanceOperation, nonMonetoryBalanceList);

        abmfResetOperations.performBalanceOperations(sprInfo, transactionFactory, nonMonetoryBalanceList);
        verify(dataBalanceResetDBOperation).executeInsertOperations(anyListOf(NonMonetoryBalance.class), any(Transaction.class), any(Integer.class));
        verify(dataBalanceResetDBOperation).executeUpdateOperations(anyListOf(NonMonetoryBalance.class), any(Transaction.class), any(Integer.class));
    }

    @Test
    public void carryForwardOperationIsNotPerformedIfBalanceIsStillActive() throws OperationFailedException, SQLException, TransactionException {
        NonMonetoryBalance expiredBalance = createServiceRGNonMonitoryBalances(UUID.randomUUID().toString(), getTime(CURRENT_DAY+1,CURRENT_MONTH));
        NonMonetoryBalance provisionedBalance = createServiceRGNonMonitoryBalances(UUID.randomUUID().toString(), getTime(CURRENT_DAY+1,CURRENT_MONTH+1));

        List<NonMonetoryBalance> nonMonetoryBalanceList = new ArrayList<>();
        nonMonetoryBalanceList.add(expiredBalance);

        createDummyTransaction();

        doReturn(provisionedBalance).when(balanceResetOperation).performDataBalanceOperations(expiredBalance, sprInfo);
        abmfResetOperations.performBalanceOperations(sprInfo, transactionFactory, nonMonetoryBalanceList);
        verifyZeroInteractions(carryForwardOperation);
    }

    @Test
    public void carryForwardOperationIsPerformedWhenBalanceIsExpired() throws OperationFailedException, SQLException, TransactionException {
        NonMonetoryBalance expiredBalance = createServiceRGNonMonitoryBalances(UUID.randomUUID().toString(), getTime(CURRENT_DAY-1,CURRENT_MONTH));
        NonMonetoryBalance provisionedBalance = createServiceRGNonMonitoryBalances(UUID.randomUUID().toString(), getTime(CURRENT_DAY-1,CURRENT_MONTH+1));

        List<NonMonetoryBalance> nonMonetoryBalanceList = new ArrayList<>();
        nonMonetoryBalanceList.add(expiredBalance);

        createDummyTransaction();
        DataBalanceOperation dataBalanceOperation = spy(new DataBalanceOperation());
        doReturn(dataBalanceOperation).when(dataBalanceOperationFactory).createDataBalanceOperation();

        doReturn(provisionedBalance).when(balanceResetOperation).performDataBalanceOperations(expiredBalance, sprInfo);
        abmfResetOperations.performBalanceOperations(sprInfo, transactionFactory, nonMonetoryBalanceList);
        verify(carryForwardOperation, times(1)).performCarryForwardOperation(expiredBalance, dataBalanceOperation, nonMonetoryBalanceList);
    }

    protected NonMonetoryBalance createServiceRGNonMonitoryBalances(String id, long billingCycleResetTime) {
        Random random = new Random();

        NonMonetoryBalance serviceRgNonMonitoryBalance = new NonMonetoryBalance.NonMonetaryBalanceBuilder(id,
                org.apache.commons.lang3.RandomUtils.nextInt(0, Integer.MAX_VALUE),
                UUID.randomUUID().toString(),
                1l,
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                random.nextInt(2),
                UUID.randomUUID().toString(), ResetBalanceStatus.NOT_RESET, null, null)
                .withBillingCycleVolumeBalance(random.nextInt(),random.nextInt()).withBillingCycleResetTime(billingCycleResetTime)
                .withBillingCycleTimeBalance(random.nextInt(),random.nextInt())
                .withDailyUsage(random.nextInt(),random.nextInt()).withDailyResetTime(random.nextInt())
                .withWeeklyUsage(random.nextInt(),random.nextInt()).withWeeklyResetTime(random.nextInt())
                .withReservation(random.nextInt(),random.nextInt())
                .build();
        return serviceRgNonMonitoryBalance;
    }

    private TimeSource getFixedTimeSource(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, CURRENT_MONTH);
        calendar.set(Calendar.DAY_OF_MONTH, CURRENT_DAY);
        calendar.set(Calendar.HOUR_OF_DAY, 22);
        calendar.set(Calendar.YEAR, 2018);
        return new FixedTimeSource(calendar.getTimeInMillis());
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
