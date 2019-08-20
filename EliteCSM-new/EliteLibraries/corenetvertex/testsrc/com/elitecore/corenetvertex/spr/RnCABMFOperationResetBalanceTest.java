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
import com.elitecore.corenetvertex.core.transaction.TransactionFactory;
import com.elitecore.corenetvertex.data.PackageType;
import com.elitecore.corenetvertex.data.ResetBalanceStatus;
import com.elitecore.corenetvertex.pkg.ChargingType;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.pm.rnc.pkg.RnCPackage;
import com.elitecore.corenetvertex.pm.rnc.ratecard.NonMonetaryRateCard;
import com.elitecore.corenetvertex.pm.rnc.rcgroup.RateCardGroup;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.corenetvertex.spr.data.SPRInfoImpl;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.util.DerbyUtil;
import com.elitecore.corenetvertex.util.HibernateSessionFactory;
import com.elitecore.corenetvertex.util.QueryBuilder;
import junitparams.JUnitParamsRunner;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnitParamsRunner.class)
public class RnCABMFOperationResetBalanceTest {

    private static final int HIGH_RESPONSE_TIME_LIMIT_IN_MS = 100;
    private static final String SUBSCRIBER_ID = UUID.randomUUID().toString();
    private String PRODUCT_OFFER_ID = UUID.randomUUID().toString();
    public String RNC_PACKAGE_ID = UUID.randomUUID().toString();
    private static final String DS_NAME = "test-DB";

    @Mock private AlertListener alertListener;
    @Mock private PolicyRepository policyRepository;
    private DummyTransactionFactory transactionFactory;
    private RnCABMFOperation rncAbmfOperation;
    private RnCABMFOperation rncAbmfOperationFixedTimeSource;
    private NonMonetaryRateCard nonMonetaryRateCard;
    private RateCardGroup rateCardGroup;
    private List<RateCardGroup> rcgList = new ArrayList<>();
    @Rule public ExpectedException expectedException = ExpectedException.none();
    private static RnCABMFOperationHelper helper;
    private HibernateSessionFactory hibernateSessionFactory;
    private String currency="INR";

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        String sid = UUID.randomUUID().toString();
        String connectionURL = "jdbc:h2:mem:"+ sid;
        DummyDBDataSource dbDataSource = new DummyDBDataSource("1", DS_NAME, connectionURL, "", "", 1, 5000, 3000);
        transactionFactory = new DummyTransactionFactory(dbDataSource);
        transactionFactory.createTransaction();
        Properties hibernateProperties = new Properties();
        hibernateProperties.setProperty("hibernate.connection.url", connectionURL);
        hibernateProperties.setProperty("hibernate.hbm2ddl.auto", "create-drop");
        hibernateSessionFactory = HibernateSessionFactory.create("hibernate/test-hibernate.cfg.xml", hibernateProperties);

        helper = new RnCABMFOperationHelper(transactionFactory);
        rncAbmfOperation = new RnCABMFOperation(alertListener, policyRepository, 10, 2);
        rncAbmfOperationFixedTimeSource = new RnCABMFOperation(alertListener, policyRepository, 10, 2, getFixedTimeSource());
        helper.createPackages();
        createTablesAndInsertUsageRecords();
    }

    private TimeSource getFixedTimeSource(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, 5);
        calendar.set(Calendar.HOUR_OF_DAY, 22);
        calendar.set(Calendar.YEAR, 2038);
        return new FixedTimeSource(calendar.getTimeInMillis());
    }

    private SPRInfo createSprInfo(String subscriberId){
        SPRInfoImpl sprInfo = new SPRInfoImpl();
        sprInfo.setSubscriberIdentity(subscriberId);
        sprInfo.setBillingDate(26);
        return sprInfo;
    }

    @Test
    public void resetBalanceGeneratesDBHIGHQUERYRESPONSETIMEAlertWhenExecutionTimeIsHigh() throws Exception{

        TransactionFactory transactionFactory = setUpMockToGenerateHIGHRESPONSEAlertForDirectDebitBalance();
        rncAbmfOperation.resetNonMonetaryBalance(createSprInfo(SUBSCRIBER_ID), null, helper.getRnCPackage(), transactionFactory);

        verify(alertListener, only())
                .generateSystemAlert(Mockito.anyString(), Mockito.eq(Alerts.HIGH_QUERY_RESPONSE_TIME), Mockito.anyString(), Mockito.anyString());
    }

    @Test
    public void resetBalanceGeneratesQUERYTIMEOUTAlertWhenQueryTimeoutTimeIsHigh() throws Exception {
        TransactionFactory transactionFactory = setUpMockToGenerateQUERYTIMEOUTAlertForDirectDebitBalance();

        try {
            rncAbmfOperation.resetNonMonetaryBalance(createSprInfo(SUBSCRIBER_ID), null, helper.getRnCPackage(), transactionFactory);
        } catch (OperationFailedException e) {
            verify(alertListener, only())
                    .generateSystemAlert(Mockito.anyString(), Mockito.eq(Alerts.QUERY_TIME_OUT), Mockito.anyString(), Mockito.anyString(), Mockito.anyInt(), Mockito
                            .anyString());
        }
    }

    @Test
    public void resetBalanceForRnCProductOffer() throws OperationFailedException {

        rncAbmfOperation.resetNonMonetaryBalance(createSprInfo(SUBSCRIBER_ID), PRODUCT_OFFER_ID, helper.getRnCPackage(), transactionFactory);

        SubscriberRnCNonMonetaryBalance nonMonetaryBalance = rncAbmfOperation.getNonMonetaryBalance(SUBSCRIBER_ID, RNC_PACKAGE_ID, null, transactionFactory);
        Assert.assertEquals(nonMonetaryBalance.getBalanceById("1").getDailyLimit(), 0);
    }

    @Test
    public void resetBalanceForRnCProductOfferAndProvisionProratedQuotaForTime() throws OperationFailedException {

        doReturn(true).when(nonMonetaryRateCard).getProration();
        doReturn(10000l).when(nonMonetaryRateCard).getTimeMinorUnit();
        rncAbmfOperationFixedTimeSource.resetNonMonetaryBalance(createSprInfo(SUBSCRIBER_ID), PRODUCT_OFFER_ID, helper.getRnCPackage(), transactionFactory);

        SubscriberRnCNonMonetaryBalance nonMonetaryBalance = rncAbmfOperation.getNonMonetaryBalance(SUBSCRIBER_ID, RNC_PACKAGE_ID, null, transactionFactory);
        Assert.assertEquals(nonMonetaryBalance.getBalanceById("1").getDailyLimit(), 0);
        Assert.assertTrue(nonMonetaryBalance.getBalanceById("1").getBillingCycleTotal()==7500l);
    }

    @Test
    public void resetBalanceForRnCProductOfferAndProvisionProratedQuotaForEvent() throws OperationFailedException {

        doReturn(true).when(nonMonetaryRateCard).getProration();
        doReturn(200l).when(nonMonetaryRateCard).getEvent();
        RnCPackage rnc = helper.getRnCPackage();
        doReturn(ChargingType.EVENT).when(rnc).getChargingType();
        rncAbmfOperationFixedTimeSource.resetNonMonetaryBalance(createSprInfo(SUBSCRIBER_ID), PRODUCT_OFFER_ID, helper.getRnCPackage(), transactionFactory);

        SubscriberRnCNonMonetaryBalance nonMonetaryBalance = rncAbmfOperation.getNonMonetaryBalance(SUBSCRIBER_ID, RNC_PACKAGE_ID, null, transactionFactory);
        Assert.assertEquals(nonMonetaryBalance.getBalanceById("1").getDailyLimit(), 0);
        Assert.assertTrue(nonMonetaryBalance.getBalanceById("1").getBillingCycleTotal()==150l);
    }

    @After
    public void dropTables() throws Exception {
        Connection connection = transactionFactory.getConnection();
        if(Objects.nonNull(connection)) {
            DBUtility.closeQuietly(connection);
        }
        helper.dropTables();
        DerbyUtil.closeDerby(DS_NAME);
    }

    private static void createTablesAndInsertUsageRecords() throws Exception {
        LogManager.getLogger().debug("test", "creating DB");

        helper.addBalanceDetailsInTable();

        LogManager.getLogger().debug("test", "DB created");
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

    private class RnCABMFOperationHelper {

        public TransactionFactory transactionFactory;
        public RnCPackage rnCPackage;

        public Map<String,TblmRnCBalanceEntity> dbBalance = null;

        public RnCABMFOperationHelper(TransactionFactory transactionFactory){
            this.transactionFactory = transactionFactory;
        }

        public void createTables() throws Exception {
            executeQuery(QueryBuilder.buildCreateQuery(TblmRnCBalanceEntity.class));
        }

        public void dropTables() throws Exception {
            executeQuery(QueryBuilder.buildDropQuery(TblmRnCBalanceEntity.class));
            getLogger().debug(this.getClass().getSimpleName(), "Tables Dropped");
        }

        public List<TblmRnCBalanceEntity> getBalanceList(){

            Long currentTime = System.currentTimeMillis();

            List<TblmRnCBalanceEntity> list = new ArrayList<>();
            long monthInterval=30*24*3600*1000L;
            long weekInterval=7*24*3600*1000L;
            long dayInterval=24*3600*1000L;

            TblmRnCBalanceEntity rnc1 = new TblmRnCBalanceEntity( "1",  SUBSCRIBER_ID, RNC_PACKAGE_ID, null, "1",
                    32212254720L, 32212254720L,
                    36000l, 36000l, new Timestamp(currentTime+dayInterval), new Timestamp(currentTime+weekInterval),new Timestamp(currentTime+monthInterval),
                    new Timestamp(currentTime), 0l, new Timestamp(currentTime), ResetBalanceStatus.NOT_RESET.name(), null, PRODUCT_OFFER_ID, ChargingType.SESSION.name());

            TblmRnCBalanceEntity rnc2 = new TblmRnCBalanceEntity( "2",  SUBSCRIBER_ID, RNC_PACKAGE_ID, null, "2",
                    32212254720L, 32212254720L,
                    36000l, 36000l, new Timestamp(currentTime+dayInterval), new Timestamp(currentTime+weekInterval),new Timestamp(currentTime+monthInterval),
                    new Timestamp(currentTime), 0l, new Timestamp(currentTime), ResetBalanceStatus.NOT_RESET.name(), null, PRODUCT_OFFER_ID, ChargingType.SESSION.name());

            list.add(rnc1);
            list.add(rnc2);

            // So that it can be retrieved during case verification
            dbBalance = list.stream().collect(Collectors.toMap(TblmRnCBalanceEntity::getId, Function.identity()));

            return list;
        }

        public Map<String, TblmRnCBalanceEntity> getDbBalance() {
            return dbBalance;
        }

        public void addBalanceDetailsInTable() throws Exception{
            List<TblmRnCBalanceEntity> list = getBalanceList();

            for (TblmRnCBalanceEntity subscriberBalance : list) {
                executeQuery(QueryBuilder.buildInsertQuery(subscriberBalance));
            }

        }

        public void createPackages(){

            nonMonetaryRateCard = spy(new NonMonetaryRateCard("1", null, null, null, 0,0, 0, null, 0,0, null, null, null, null, 1,RenewalIntervalUnit.TILL_BILL_DATE, CommonStatusValues.DISABLE.isBooleanValue()));
            rateCardGroup = new RateCardGroup(UUID.randomUUID().toString(), null, null, null, nonMonetaryRateCard, null,null, null, 0,null,null);
            rcgList.add(rateCardGroup);
            rnCPackage = spy(new RnCPackage(RNC_PACKAGE_ID, "pkgName", null, null, rcgList, null,null, null, PkgMode.LIVE, null, null, null , null, ChargingType.SESSION,currency));
        }

        public RnCPackage getRnCPackage(){
            return rnCPackage;
        }

        private void executeQuery(String query) throws Exception {
            Transaction transaction = transactionFactory.createTransaction();
            try {

                transaction.begin();
                transaction.prepareStatement(query).execute();

            } finally {
                transaction.end();
            }
        }
    }
}
