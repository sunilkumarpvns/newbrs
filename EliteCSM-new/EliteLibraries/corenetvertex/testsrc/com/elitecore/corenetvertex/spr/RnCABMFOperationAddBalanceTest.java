package com.elitecore.corenetvertex.spr;

import com.elitecore.commons.base.DBUtility;
import com.elitecore.commons.base.FixedTimeSource;
import com.elitecore.commons.base.TimeSource;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.CommonStatusValues;
import com.elitecore.corenetvertex.constants.RenewalIntervalUnit;
import com.elitecore.corenetvertex.core.alerts.AlertListener;
import com.elitecore.corenetvertex.core.alerts.Alerts;
import com.elitecore.corenetvertex.core.transaction.DummyTransactionFactory;
import com.elitecore.corenetvertex.core.transaction.Transaction;
import com.elitecore.corenetvertex.core.transaction.TransactionException;
import com.elitecore.corenetvertex.core.transaction.TransactionFactory;
import com.elitecore.corenetvertex.pkg.ChargingType;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.pm.rnc.pkg.RnCPackage;
import com.elitecore.corenetvertex.pm.rnc.ratecard.NonMonetaryRateCard;
import com.elitecore.corenetvertex.pm.rnc.rcgroup.RateCardGroup;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.corenetvertex.spr.data.SPRInfoImpl;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.corenetvertex.util.HibernateSessionFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.UUID;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RnCABMFOperationAddBalanceTest {


    private static final int HIGH_RESPONSE_TIME_LIMIT_IN_MS = 100;
    private static final String DS_NAME = "test-DB";
    private static final String TRANSACTION = "transaction";
    private static final String PREPARED_STATEMENT = "preparedStatement";
    private static final String BEGIN = "begin";
    private static final String EXECUTE = "execute";
    private String currency="INR";


    @Mock
    private AlertListener alertListener;
    @Mock private PolicyRepository policyRepository;
    private DummyTransactionFactory transactionFactory;
    private RnCABMFOperation rncAbmfOperation;
    private RnCABMFOperation rncAbmfOperationFixedTimeSource;
    private RnCPackage rnCPackage;
    private NonMonetaryRateCard nonMonetaryRateCard;
    private RateCardGroup rateCardGroup;
    private List<RateCardGroup> rcgList = new ArrayList<>();
    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    private HibernateSessionFactory hibernateSessionFactory;


    @Before
    public void setUp() throws Exception{
        MockitoAnnotations.initMocks(this);
        String folderName = UUID.randomUUID().toString();
        //jdbc:h2:mem:create-drop
        String sid = UUID.randomUUID().toString();
        String connectionURL = "jdbc:h2:mem:" + sid;
        DummyDBDataSource dbDataSource = new DummyDBDataSource("1", DS_NAME, connectionURL, "", "", 1, 5000, 3000);
        transactionFactory = new DummyTransactionFactory(dbDataSource);
        transactionFactory.createTransaction();
        Properties hibernateProperties = new Properties();
        hibernateProperties.setProperty("hibernate.connection.url", connectionURL);
        hibernateProperties.setProperty("hibernate.hbm2ddl.auto", "create-drop");
        hibernateSessionFactory = HibernateSessionFactory.create("hibernate/test-hibernate.cfg.xml", hibernateProperties);

        rncAbmfOperation = new RnCABMFOperation(alertListener, policyRepository,10, 2);
        rncAbmfOperationFixedTimeSource = new RnCABMFOperation(alertListener,policyRepository, 10, 2, getFixedTimeSource());

        nonMonetaryRateCard = spy(new NonMonetaryRateCard("rc1", null, null, null, 0,0, 0, null, 0,0, null, null, null, null,0, RenewalIntervalUnit.TILL_BILL_DATE, CommonStatusValues.DISABLE.isBooleanValue()));
        rateCardGroup = new RateCardGroup(UUID.randomUUID().toString(), null, null, null, nonMonetaryRateCard, null,null, null, 0,null,null);
        rcgList.add(rateCardGroup);
        rnCPackage = spy(new RnCPackage("pkg1", "pkgName", null, null, rcgList, null,null, null, PkgMode.LIVE, null, null, null , null, ChargingType.SESSION,currency));
    }

    private TimeSource getFixedTimeSource(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, 5);
        calendar.set(Calendar.HOUR_OF_DAY, 22);
        calendar.set(Calendar.YEAR, 2038);
        return new FixedTimeSource(calendar.getTimeInMillis());
    }

    @After
    public void tearDownConnection() throws Exception {
        Connection connection = transactionFactory.getConnection();
        if(Objects.nonNull(connection)) {
            DBUtility.closeQuietly(connection);
        }
        try {
            hibernateSessionFactory.shutdown();

        } catch (Exception ex) {
            LogManager.getLogger().trace(ex);
        }

    }

    @Test
    public void test_add_balance_should_generate_DB_HIGH_QUERY_RESPONSE_TIME_alert_when_execution_time_is_high() throws Exception{

        TransactionFactory transactionFactory = setUpMockToGenerateHIGHRESPONSEAlertForDirectDebitBalance();
        SPRInfo sprInfo = createSPRInfo();
        rncAbmfOperation.addBalance(sprInfo.getSubscriberIdentity(), null, rnCPackage, getTransaction(transactionFactory), null,sprInfo.getBillingDate());

        verify(alertListener, only())
                .generateSystemAlert(Mockito.anyString(), Mockito.eq(Alerts.HIGH_QUERY_RESPONSE_TIME), Mockito.anyString(), Mockito.anyString());
    }

    @Test
    public void test_add_balance_should_generate_QUERY_TIMEOUT_alert_when_query_timeout_time_is_high() throws Exception {
        TransactionFactory transactionFactory = setUpMockToGenerateQUERYTIMEOUTAlertForDirectDebitBalance();

        try {
            SPRInfo sprInfo = createSPRInfo();
            rncAbmfOperation.addBalance(sprInfo.getSubscriberIdentity(), null, rnCPackage, getTransaction(transactionFactory), null,sprInfo.getBillingDate());
        } catch (OperationFailedException e) {
            verify(alertListener, only())
                    .generateSystemAlert(Mockito.anyString(), Mockito.eq(Alerts.QUERY_TIME_OUT), Mockito.anyString(), Mockito.anyString(), Mockito.anyInt(), Mockito
                            .anyString());
        }
    }

    @Test
    public void test_add_balance_alert_is_generated_when_sql_exception_is_thrown_due_to_QUERY_TIME_OUT_ERROR() throws Exception {
        SQLException sqlException = new SQLException("query timeout", "timeout", 1013);
        setupMockToGenerateExceptionOnUpdateTransaction(EXECUTE, sqlException);
        Transaction transaction = transactionFactory.createTransaction();

        try {
            SPRInfo sprInfo = createSPRInfo();
            rncAbmfOperation.addBalance(sprInfo.getSubscriberIdentity(), null, rnCPackage, getTransaction(transactionFactory), null,sprInfo.getBillingDate());
        } catch (OperationFailedException e) {

            verify(alertListener, only())
                    .generateSystemAlert(Mockito.anyString(), Mockito.eq(Alerts.QUERY_TIME_OUT), Mockito.anyString(), Mockito.anyString(), Mockito.anyInt(), Mockito.anyString());
        }
    }

    @Test
    public void throwsOperationFailedExceptionWhenNullPackageIsPassed() throws Exception {
        expectedException.expect(OperationFailedException.class);
        expectedException.expectMessage("Error while subscriber ABMF Operation. Reason. RnC Package is null");
        Transaction transaction = transactionFactory.createTransaction();
        rncAbmfOperation.addBalance(createSPRInfo().getSubscriberIdentity(), null,null,transaction, null,createSPRInfo().getBillingDate());
    }

    @Test
    public void addBalanceForRnCProductOfferAndProvisionProratedQuotaForTime() throws OperationFailedException {

        doReturn(true).when(nonMonetaryRateCard).getProration();
        doReturn(10000l).when(nonMonetaryRateCard).getTimeMinorUnit();
        doReturn("rc2").when(nonMonetaryRateCard).getId();
        SPRInfo sprInfo = createSPRInfo();
        rncAbmfOperationFixedTimeSource.addBalance(sprInfo.getSubscriberIdentity(), null, rnCPackage, transactionFactory.createTransaction(), "po1", sprInfo.getBillingDate());
        SubscriberRnCNonMonetaryBalance nonMonetaryBalance = rncAbmfOperation.getNonMonetaryBalance(sprInfo.getSubscriberIdentity(), rnCPackage.getId(), null, transactionFactory);
        Assert.assertTrue(nonMonetaryBalance.getPackageBalance(rnCPackage.getId()).getBalance("rc2").getBillingCycleTotal()==7500l);
    }

    @Test
    public void addBalanceForRnCProductOfferAndProvisionProratedQuotaForEvent() throws OperationFailedException {

        doReturn(true).when(nonMonetaryRateCard).getProration();
        doReturn(200l).when(nonMonetaryRateCard).getEvent();
        doReturn(ChargingType.EVENT).when(rnCPackage).getChargingType();
        SPRInfo sprInfo = createSPRInfo();
        rncAbmfOperationFixedTimeSource.addBalance(sprInfo.getSubscriberIdentity(), null, rnCPackage, transactionFactory.createTransaction(), "po2", sprInfo.getBillingDate());

        SubscriberRnCNonMonetaryBalance nonMonetaryBalance = rncAbmfOperation.getNonMonetaryBalance(sprInfo.getSubscriberIdentity(), rnCPackage.getId(), null, transactionFactory);
        Assert.assertTrue(nonMonetaryBalance.getPackageBalance(rnCPackage.getId()).getBalance("rc1").getBillingCycleTotal()==150l);
    }
    private SPRInfo createSPRInfo(){
        SPRInfoImpl sprInfoImpl = new SPRInfoImpl();
        sprInfoImpl.setSubscriberIdentity("test");
        sprInfoImpl.setBillingDate(26);
        return sprInfoImpl;
    }

    private Transaction getTransaction(TransactionFactory transactionFactory) throws  OperationFailedException, TransactionException{
        if (transactionFactory.isAlive() == false) {
            throw new OperationFailedException("Unable to perform add balance operation for subscriber ID. Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
        }

        Transaction transaction = transactionFactory.createTransaction();

        if (transaction == null) {
            throw new OperationFailedException("Unable to perform add balance operation for subscriber ID. Reason: Datasource not available", ResultCode.SERVICE_UNAVAILABLE);
        }

        transaction.begin();

        return transaction;
    }

    private void setupMockToGenerateExceptionOnUpdateTransaction(String whenToThrow, Class<? extends Throwable> exceptionToBeThrown) throws Exception{

        Throwable throwable = exceptionToBeThrown.getConstructor(String.class).newInstance("from test");
        setupMockToGenerateExceptionOnUpdateTransaction(whenToThrow, throwable);
    }

    private void setupMockToGenerateExceptionOnUpdateTransaction(String whenToThrow,  Throwable throwable) throws Exception{
        Transaction transaction = mock(Transaction.class);
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        ResultSet resultSet = mock(ResultSet.class);

        transactionFactory = spy(transactionFactory);
        doReturn(transaction).when(transactionFactory).createTransaction();
        doReturn(preparedStatement).when(transaction).prepareStatement(Mockito.anyString());
        doReturn(1).when(preparedStatement).executeUpdate();

        if (TRANSACTION.equals(whenToThrow)) {
            doReturn(null).when(transactionFactory).createTransaction();
        } else if (PREPARED_STATEMENT.equals(whenToThrow)) {
            doThrow(throwable).when(transaction).prepareStatement(Mockito.anyString());
        } else if (BEGIN.equals(whenToThrow)) {
            doThrow(throwable).when(transaction).begin();
        } else if (EXECUTE.equals(whenToThrow)) {
            doThrow(throwable).when(preparedStatement).executeUpdate();
        }
    }

    private TransactionFactory setUpMockToGenerateQUERYTIMEOUTAlertForDirectDebitBalance() throws Exception{
        TransactionFactory factory = spy(transactionFactory);
        doReturn(true).when(factory).isAlive();
        Transaction transaction = mock(Transaction.class);
        PreparedStatement preparedStatement = mock(PreparedStatement.class);

        doReturn(transaction).when(factory).createTransaction();
        doReturn(preparedStatement).when(transaction).prepareStatement(Mockito.anyString());

        when(preparedStatement.executeUpdate()).thenThrow(new SQLException("query timeout", "timeout", 1013));

        return factory;

    }

    private TransactionFactory setUpMockToGenerateHIGHRESPONSEAlertForDirectDebitBalance() throws Exception{
        TransactionFactory factory = spy(transactionFactory);
        Transaction transaction = mock(Transaction.class);
        PreparedStatement preparedStatement = mock(PreparedStatement.class);

        doReturn(transaction).when(factory).createTransaction();
        doReturn(preparedStatement).when(transaction).prepareStatement(Mockito.anyString());

        when(preparedStatement.executeUpdate()).thenAnswer(new Answer<Integer>() {

            public Integer answer(org.mockito.invocation.InvocationOnMock invocation) throws Throwable {
                long startTime = System.currentTimeMillis();
                long elapsedTime = 0;
                while (elapsedTime < HIGH_RESPONSE_TIME_LIMIT_IN_MS + 10) {
                    elapsedTime = System.currentTimeMillis() - startTime;
                }

                return 1;
            }

        });

        return factory;
    }

}
