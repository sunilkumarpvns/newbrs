package com.elitecore.corenetvertex.spr;

import com.elitecore.commons.base.DBUtility;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.constants.QuotaProfileType;
import com.elitecore.corenetvertex.core.alerts.AlertListener;
import com.elitecore.corenetvertex.core.transaction.DummyTransactionFactory;
import com.elitecore.corenetvertex.core.transaction.Transaction;
import com.elitecore.corenetvertex.core.transaction.TransactionException;
import com.elitecore.corenetvertex.core.transaction.TransactionFactory;
import com.elitecore.corenetvertex.core.transaction.exception.TransactionErrorCode;
import com.elitecore.corenetvertex.data.ResetBalanceStatus;
import com.elitecore.corenetvertex.pkg.ChargingType;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.pm.pkg.datapackage.BasePackage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.UserPackage;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.util.DerbyUtil;
import com.elitecore.corenetvertex.util.QueryBuilder;
import junitparams.JUnitParamsRunner;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.spy;

@RunWith(JUnitParamsRunner.class)
public class RnCABMFOperationGetBalanceTest {

    private static final String TABLE_NAME = "TBLM_RNC_BALANCE";
    private static final String SELECT_BALANCE_QUERY = "SELECT * FROM "+ TABLE_NAME + " WHERE SUBSCRIBER_ID = ? ";
    private static final String DS_NAME = "test-DB";
    private DummyTransactionFactory transactionFactory;
    private static ABMFOperationTestHelper helper;
    private String SUBSCRIBER_ID="123123";
    private String basePackageId = "BASE_1";
    private String testDB = UUID.randomUUID().toString();

    @Mock
    private PolicyRepository policyRepository;
    @Mock
    private AlertListener alertListener;

    RnCABMFOperation abmfOperation;

    @BeforeClass
    public static void setUpClass() throws Exception {
        Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
    }

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);


        String url = "jdbc:derby:memory:"+ testDB +";create=true";
        DummyDBDataSource dbDataSource = new DummyDBDataSource("1", DS_NAME, url, "", "", 1, 5000, 3000);
        transactionFactory = (DummyTransactionFactory)new DummyTransactionFactoryBuilder().withDBDataSource(dbDataSource, 100).build();

        helper = new ABMFOperationTestHelper(transactionFactory);
        helper.createPackages();

        try{
            createTablesAndInsertUsageRecords();
        } catch (Exception e) {

        }

        abmfOperation = new RnCABMFOperation(alertListener,policyRepository,0,0);
    }


    @Test
    public void when_subscriber_id_is_passed_it_will_return_all_details_available() throws Exception {
        SubscriberRnCNonMonetaryBalance balance = abmfOperation.getNonMonetaryBalance(SUBSCRIBER_ID,null,null, transactionFactory);
        Assert.assertNotNull(balance.getPackageBalance(basePackageId));
    }

    @Test
    public void subscriber_with_no_balance_record_it_will_return_zero_sized_list() throws Exception {
        SubscriberRnCNonMonetaryBalance balance = abmfOperation.getNonMonetaryBalance("InvalidSubscriberId",null,null, transactionFactory);
        Assert.assertEquals(0,balance.getPackageBalances().values().size());
    }

    @Test(expected = OperationFailedException.class)
    public void when_transaction_creation_fails_it_must_throw_OperationFailedException_with_message_telling_such_behaviour() throws Exception{

        transactionFactory = spy(transactionFactory);
        doReturn(null).when(transactionFactory).createTransaction();

        abmfOperation.getNonMonetaryBalance(SUBSCRIBER_ID,null,null, transactionFactory);

    }

    @Test(expected = OperationFailedException.class)
    public void when_transaction_factory_is_not_alive_it_must_throw_OperationFailedException_with_message_telling_such_behaviour() throws Exception{

        transactionFactory = spy(transactionFactory);
        doReturn(false).when(transactionFactory).isAlive();

        Mockito.when(transactionFactory.isAlive()).thenReturn(false);
        abmfOperation.getNonMonetaryBalance(SUBSCRIBER_ID,null,null, transactionFactory);

    }

    @Test(expected = OperationFailedException.class)
    public void when_transaction_begin_fails_it_must_handle_Exception() throws Exception{

        transactionFactory = spy(transactionFactory);
        Transaction transaction = transactionFactory.createTransaction();
        transaction = spy(transaction);
        Mockito.when(transactionFactory.createTransaction()).thenReturn(transaction);

        Mockito.doThrow(new TransactionException("exception", TransactionErrorCode.CONNECTION_NOT_FOUND))
                .when(transaction).begin();

        abmfOperation.getNonMonetaryBalance(SUBSCRIBER_ID,null,null, transactionFactory);

    }

    @Test(expected = OperationFailedException.class)
    public void when_for_any_reason_Exception_occurs_while_db_call() throws Exception{

        transactionFactory = spy(transactionFactory);
        Transaction transaction = transactionFactory.createTransaction();
        transaction = spy(transaction);
        Mockito.when(transactionFactory.createTransaction()).thenReturn(transaction);

        PreparedStatement pstm = transaction.prepareStatement(SELECT_BALANCE_QUERY);
        pstm = spy(pstm);
        Mockito.when(transaction.prepareStatement(SELECT_BALANCE_QUERY)).thenReturn(pstm);
        Mockito.doThrow(new ArrayIndexOutOfBoundsException()).when(pstm).executeQuery();

        abmfOperation.getNonMonetaryBalance(SUBSCRIBER_ID,null,null, transactionFactory);
    }

    @Test(expected = OperationFailedException.class)
    public void when_prepared_statement_compilation_fails_it_must_handle_Exception() throws Exception{

        transactionFactory = spy(transactionFactory);
        Transaction transaction = transactionFactory.createTransaction();
        transaction = spy(transaction);
        Mockito.when(transactionFactory.createTransaction()).thenReturn(transaction);

        PreparedStatement pstm = transaction.prepareStatement(SELECT_BALANCE_QUERY);
        pstm = spy(pstm);
        Mockito.when(transaction.prepareStatement(SELECT_BALANCE_QUERY)).thenReturn(pstm);
        Mockito.doThrow(new SQLException("","",ABMFOperation.QUERY_TIMEOUT_ERROR_CODE,null )).when(pstm).executeQuery();

        abmfOperation.getNonMonetaryBalance(SUBSCRIBER_ID,null,null, transactionFactory);

    }

    @Test
    public void when_transaction_end_throws_exception_it_must_handle_and_return_successful_result_no_assertion_needed_resultset_is_delayed_purposefully_to_test_system_alert() throws Exception{

        transactionFactory = spy(transactionFactory);
        Transaction transaction = transactionFactory.createTransaction();
        transaction = spy(transaction);
        doReturn(transaction).when(transactionFactory).createTransaction();
        doThrow(new TransactionException("exception", TransactionErrorCode.CONNECTION_NOT_FOUND)).when(transaction).end();


        abmfOperation.getNonMonetaryBalance(SUBSCRIBER_ID, null,null, transactionFactory);

    }

    @After
    public void dropTables() throws Exception {
        Connection connection = transactionFactory.getConnection();
        if(Objects.nonNull(connection)) {
            DBUtility.closeQuietly(connection);
        }
        helper.dropTables();
        DerbyUtil.closeDerby(testDB);
    }

    private static void createTablesAndInsertUsageRecords() throws Exception {
        LogManager.getLogger().debug("test", "creating DB");

        helper.createTables();
        helper.addBalanceDetailsInTable();

        LogManager.getLogger().debug("test", "DB created");
    }

    private class ABMFOperationTestHelper {

        public TransactionFactory transactionFactory;
        public String basePackageId = "BASE_1";
        public BasePackage basePackage;
        private String SUBSCRIBER_ID="123123";
        public String currency = "INR";


        public Map<String,TblmRnCBalanceEntity> dbBalance = null;

        public  ABMFOperationTestHelper(TransactionFactory transactionFactory){
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

            TblmRnCBalanceEntity base1 = new TblmRnCBalanceEntity( "1",  SUBSCRIBER_ID, basePackageId, null, "1",
                    32212254720L, 32212254720L,
                    36000l, 36000l, new Timestamp(currentTime+dayInterval), new Timestamp(currentTime+weekInterval),new Timestamp(currentTime+monthInterval),
                    new Timestamp(currentTime), 0l, new Timestamp(currentTime), ResetBalanceStatus.NOT_RESET.name(), null, null, ChargingType.SESSION.name());

            TblmRnCBalanceEntity base2 = new TblmRnCBalanceEntity( "2",  SUBSCRIBER_ID, basePackageId, null, "2",
                    32212254720L, 32212254720L,
                    36000l, 36000l, new Timestamp(currentTime+dayInterval), new Timestamp(currentTime+weekInterval),new Timestamp(currentTime+monthInterval),
                    new Timestamp(currentTime), 0l, new Timestamp(currentTime), ResetBalanceStatus.NOT_RESET.name(), null, null, ChargingType.SESSION.name());

            list.add(base1);
            list.add(base2);

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

            basePackage = new BasePackage(basePackageId,basePackageId, QuotaProfileType.RnC_BASED,
                    PkgStatus.ACTIVE,new ArrayList<>(),null, null,"",
                    0.0,null,null, null,null,null,
                    null,"","", null,currency);
        }

        public UserPackage getUserPackage(){
            return basePackage;
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
