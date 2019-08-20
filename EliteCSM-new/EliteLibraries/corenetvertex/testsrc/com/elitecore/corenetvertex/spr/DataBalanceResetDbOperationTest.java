package com.elitecore.corenetvertex.spr;

import com.elitecore.commons.base.DBUtility;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.core.alerts.AlertListener;
import com.elitecore.corenetvertex.core.transaction.DummyTransactionFactory;
import com.elitecore.corenetvertex.core.transaction.Transaction;
import com.elitecore.corenetvertex.core.transaction.TransactionException;
import com.elitecore.corenetvertex.data.CarryForwardStatus;
import com.elitecore.corenetvertex.data.ResetBalanceStatus;
import com.elitecore.corenetvertex.util.HibernateSessionFactory;
import junitparams.JUnitParamsRunner;
import org.apache.commons.lang3.RandomUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.unitils.reflectionassert.ReflectionAssert;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.Random;
import java.util.UUID;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnitParamsRunner.class)
public class DataBalanceResetDbOperationTest {

    private static final String DS_NAME = "test-DB";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock private AlertListener alertListener;

    private DummyTransactionFactory transactionFactory;
    private Transaction transaction;
    private DataBalanceResetDBOperation dataBalanceResetDbOperation;
    private HibernateSessionFactory hibernateSessionFactory;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        //jdbc:h2:mem:create-drop
        String sid = UUID.randomUUID().toString();
        String connectionURL = "jdbc:h2:mem:" + sid;
        DummyDBDataSource dbDataSource = new DummyDBDataSource("1", DS_NAME, connectionURL, "", "", 1, 5000, 3000);
        transactionFactory = new DummyTransactionFactory(dbDataSource);
        transaction = transactionFactory.createTransaction();
        Properties hibernateProperties = new Properties();
        hibernateProperties.setProperty("hibernate.connection.url", connectionURL);
        hibernateProperties.setProperty("hibernate.hbm2ddl.auto", "create-drop");
        hibernateSessionFactory = HibernateSessionFactory.create("hibernate/test-hibernate.cfg.xml", hibernateProperties);
        dataBalanceResetDbOperation = new DataBalanceResetDBOperation(10, alertListener);
    }

    @After
    public void tearDownConnection() throws Exception {
        Connection connection = transactionFactory.getConnection();
        if (Objects.nonNull(connection)) {
            DBUtility.closeQuietly(connection);
        }
        hibernateSessionFactory.shutdown();
    }

    @Test
    public void batchIsExecutedForInsertOperationsWhenBatchSizeIsReached() throws TransactionException, SQLException {
        List<NonMonetoryBalance> nonMonetaryBalances = createNonMonetaryBalances(7);
        Transaction transactionForBatchExecution = spy(transactionFactory.createTransaction());

        PreparedStatement preparedStatement = spy(PreparedStatement.class);
        doReturn(preparedStatement).when(transactionForBatchExecution).prepareStatement(anyString());

        dataBalanceResetDbOperation.executeInsertOperations(nonMonetaryBalances, transactionForBatchExecution,3);
        verify(preparedStatement,times(3)).executeBatch();
    }

    @Test
    public void dataInsertedWhenBatchExecutionCompleted() throws SQLException, TransactionException {
        List<NonMonetoryBalance> nonMonetaryBalances = createNonMonetaryBalances(7);
        dataBalanceResetDbOperation.executeInsertOperations(nonMonetaryBalances, transaction,7);
        List<TblmDataBalanceEntity> actualEntities = hibernateSessionFactory.get(TblmDataBalanceEntity.class);
        List<TblmDataBalanceEntity> expectedDataBalanceEntities = getExpectedDataBalanceEntity(nonMonetaryBalances, actualEntities);
        ReflectionAssert.assertReflectionEquals(expectedDataBalanceEntities, actualEntities);
    }

    @Test
    public void batchIsExecutedForUpdateOperationsWhenBatchSizeIsReached() throws TransactionException, SQLException {
        List<NonMonetoryBalance> nonMonetaryBalances = createNonMonetaryBalances(7);
        Transaction transactionForBatchExecution = spy(transactionFactory.createTransaction());

        PreparedStatement preparedStatement = spy(PreparedStatement.class);
        doReturn(preparedStatement).when(transactionForBatchExecution).prepareStatement(anyString());

        dataBalanceResetDbOperation.executeUpdateOperations(nonMonetaryBalances, transactionForBatchExecution,3);
        verify(preparedStatement,times(3)).executeBatch();
    }

    private List<NonMonetoryBalance> createNonMonetaryBalances(int noOfNonMonetaryBalances) {
        List<NonMonetoryBalance> nonMonetaryBalances = new ArrayList<NonMonetoryBalance>();
        for(int index = 0;index<noOfNonMonetaryBalances;index++) {
            nonMonetaryBalances.add(createServiceRGNonMonitoryBalances());
        }
        return nonMonetaryBalances;
    }

    @Test
    public void prepareStatementClosesProperlyAfterInsertBalanceBatch() throws Exception {
        List<NonMonetoryBalance> nonMonetaryBalances = new ArrayList<NonMonetoryBalance>();
        NonMonetoryBalance balance1 = createServiceRGNonMonitoryBalances();
        NonMonetoryBalance balance2 = createServiceRGNonMonitoryBalances();
        NonMonetoryBalance balance3 = createServiceRGNonMonitoryBalances();
        nonMonetaryBalances.add(balance1);
        nonMonetaryBalances.add(balance2);
        nonMonetaryBalances.add(balance3);

        Transaction transaction = mock(Transaction.class);
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        doReturn(preparedStatement).when(transaction).prepareStatement(Mockito.anyString());

        dataBalanceResetDbOperation.executeInsertOperations(nonMonetaryBalances, transaction, CommonConstants.DEFAULT_BATCH_SIZE);

        verify(preparedStatement).close();
    }

    @Test
    public void prepareStatementClosesProperlyAfterUpdateBatchOperation() throws Exception {
        Transaction transaction = mock(Transaction.class);
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        doReturn(preparedStatement).when(transaction).prepareStatement(Mockito.anyString());

        NonMonetoryBalance serviceRgNonMonitoryBalance = createServiceRGNonMonitoryBalances("testNonMonBalanceId","1","1",ResetBalanceStatus.RESET,"0:TILL_BILL_DATE");
        List nonMonetaryBalances = new ArrayList<NonMonetoryBalance>();
        nonMonetaryBalances.add(serviceRgNonMonitoryBalance);

        TblmDataBalanceEntity dataBalanceEntity = new TblmDataBalanceEntity();
        dataBalanceEntity.setId(serviceRgNonMonitoryBalance.getId());
        hibernateSessionFactory.save(dataBalanceEntity);

        dataBalanceResetDbOperation.executeUpdateOperations(nonMonetaryBalances, transaction, CommonConstants.DEFAULT_BATCH_SIZE);

        verify(preparedStatement).close();
    }

    private List<TblmDataBalanceEntity> getExpectedDataBalanceEntity(List<NonMonetoryBalance> nonMonetaryBalances, List<TblmDataBalanceEntity> actualEntities) {
        List<TblmDataBalanceEntity> expectedDataBalances = new ArrayList();
        for(int index = 0; index < actualEntities.size(); index++){
            TblmDataBalanceEntity expectedDataBalanceEntity = new TblmDataBalanceEntity();
            expectedDataBalanceEntity.setId(actualEntities.get(index).getId());
            expectedDataBalanceEntity.setSubscriberId(actualEntities.get(index).getSubscriberId());
            expectedDataBalanceEntity.setCarryForwardTime(actualEntities.get(index).getCarryForwardTime());
            expectedDataBalanceEntity.setCarryForwardVolume(actualEntities.get(index).getCarryForwardVolume());
            expectedDataBalanceEntity.setReservationTime(actualEntities.get(index).getReservationTime());
            expectedDataBalanceEntity.setReservationVolume(actualEntities.get(index).getReservationVolume());
            expectedDataBalanceEntity.setLastUpdateTime(actualEntities.get(index).getLastUpdateTime());
            expectedDataBalanceEntity.setStartTime(actualEntities.get(index).getStartTime());
            expectedDataBalanceEntity.setSubscriptionId(nonMonetaryBalances.get(index).getSubscriberIdentity());
            expectedDataBalanceEntity.setPackageId(nonMonetaryBalances.get(index).getPackageId());
            expectedDataBalanceEntity.setSubscriptionId(nonMonetaryBalances.get(index).getSubscriptionId());
            expectedDataBalanceEntity.setQuotaProfileId(nonMonetaryBalances.get(index).getQuotaProfileId());
            expectedDataBalanceEntity.setServiceId(nonMonetaryBalances.get(index).getServiceId());
            expectedDataBalanceEntity.setBalanceLevel(nonMonetaryBalances.get(index).getLevel());
            expectedDataBalanceEntity.setRatingGroup(nonMonetaryBalances.get(index).getRatingGroupId());
            expectedDataBalanceEntity.setBillingCycleTotalVolume(nonMonetaryBalances.get(index).getBillingCycleTotalVolume());
            expectedDataBalanceEntity.setBillingCycleAvailableVolume(nonMonetaryBalances.get(index).getBillingCycleAvailableVolume());
            expectedDataBalanceEntity.setBillingCycleTime(nonMonetaryBalances.get(index).getBillingCycleTime());
            expectedDataBalanceEntity.setBillingCycleAvailableTime(nonMonetaryBalances.get(index).getBillingCycleAvailableTime());
            expectedDataBalanceEntity.setDailyVolume(nonMonetaryBalances.get(index).getDailyVolume());
            expectedDataBalanceEntity.setDailyTime(nonMonetaryBalances.get(index).getDailyTime());
            expectedDataBalanceEntity.setWeeklyVolume(nonMonetaryBalances.get(index).getWeeklyVolume());
            expectedDataBalanceEntity.setWeeklyTime(nonMonetaryBalances.get(index).getWeeklyTime());
            expectedDataBalanceEntity.setDailyResetTime(actualEntities.get(0).getDailyResetTime());
            expectedDataBalanceEntity.setWeeklyResetTime(actualEntities.get(0).getWeeklyResetTime());
            expectedDataBalanceEntity.setQuotaExpiryTime(actualEntities.get(0).getQuotaExpiryTime());
            expectedDataBalanceEntity.setStatus(ResetBalanceStatus.NOT_RESET.name());
            expectedDataBalanceEntity.setRenewalInterval(nonMonetaryBalances.get(index).getRenewalInterval());
            expectedDataBalanceEntity.setCarryForwardStatus(nonMonetaryBalances.get(index).getCarryForwardStatus().name());
            expectedDataBalances.add(expectedDataBalanceEntity);
        }
        return expectedDataBalances;
    }

    private Transaction setUpMockToGenerateQUERYTIMEOUTAlertForChangeStatus() throws Exception {
        Transaction transaction = mock(Transaction.class);
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        doReturn(preparedStatement).when(transaction).prepareStatement(Mockito.anyString());
        when(preparedStatement.executeUpdate()).thenThrow(new SQLException("query timeout", "timeout", 1013));
        return transaction;

    }

    private NonMonetoryBalance createServiceRGNonMonitoryBalances() {
        return createServiceRGNonMonitoryBalances(UUID.randomUUID().toString(), "1", "1", ResetBalanceStatus.NOT_RESET, "1:TILL_BILL_DATE");
    }

    private NonMonetoryBalance createServiceRGNonMonitoryBalances(String id, String subscriberId, String subscriptionId, ResetBalanceStatus status, String renewalInterval) {
        Random random = new Random();

        NonMonetoryBalance serviceRgNonMonitoryBalance = new NonMonetoryBalance.NonMonetaryBalanceBuilder(id,
                RandomUtils.nextInt(0, Integer.MAX_VALUE),
                UUID.randomUUID().toString(),
                1l,
                subscriberId,
                subscriptionId,
                random.nextInt(1),
                UUID.randomUUID().toString(), status, renewalInterval, null)
                .withBillingCycleVolumeBalance(1000, 1000)
                .withBillingCycleTimeBalance(1000,1000)
                .withStartTime(getTime(13, 0, 2018,23,59,59,999))
                .withBillingCycleResetTime(getTime(13, 1, 2018,23,59,59,999))
                .withCarryForwardStatus(CarryForwardStatus.NOT_CARRY_FORWARD)
                .build();
        return serviceRgNonMonitoryBalance;
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
