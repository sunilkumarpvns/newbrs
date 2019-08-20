package com.elitecore.corenetvertex.spr;

import com.elitecore.commons.base.DBUtility;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.core.transaction.DummyTransactionFactory;
import com.elitecore.corenetvertex.core.transaction.Transaction;
import com.elitecore.corenetvertex.core.transaction.TransactionException;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.pm.pkg.datapackage.QuotaTopUp;
import com.elitecore.corenetvertex.spr.data.SubscriptionData;
import com.elitecore.corenetvertex.util.DerbyUtil;
import com.elitecore.corenetvertex.util.HibernateSessionFactory;
import junitparams.JUnitParamsRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;

@RunWith(JUnitParamsRunner.class)
public class
TopUpSubscriptionOperationTest {

    private static final String PREPARESTATEMENT = "preparedStatement";
    private static final String BEGIN = "begin";
    private static final String EXECUTE = "execute";
    private static final String GET = "get";

    @Mock
    private PolicyRepository policyRepository;
    @Mock
    private QuotaTopUp quotaTopUp;

    private DummyTransactionFactory transactionFactory;
    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    private TopUpSubscriptionOperationTestSuite.TopUpSubscriptionDBHelper helper;
    private
    HibernateSessionFactory hibernateSessionFactory;
    private String testingDB = UUID.randomUUID().toString();

    @BeforeClass
    public static void setUpClass() throws Exception {
        Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
    }

    @Before
    public void setUp() throws Exception {

        MockitoAnnotations.initMocks(this);
        DummyDBDataSource dbDataSource = new DummyDBDataSource("1", SubscriptionOperationTestSuite.DS_NAME, "jdbc:derby:memory:"
                + testingDB + ";create=true", "", "", 1, 1, 3000);
        transactionFactory = (DummyTransactionFactory)new DummyTransactionFactoryBuilder().withDBDataSource(dbDataSource, 1).build();

        helper = new TopUpSubscriptionOperationTestSuite.TopUpSubscriptionDBHelper(transactionFactory);

        createTablesAndInsertSubscriptionRecords();
        when(policyRepository.getQuotaTopUpById(Mockito.anyString())).thenReturn(quotaTopUp);
    }

    public void setupMockToGenerateException(String whenToThrow, Class<? extends Throwable> exceptionToBeThrown) throws Exception {
        Transaction transaction = mock(Transaction.class);
        PreparedStatement preparedStatement = mock(PreparedStatement.class);
        ResultSet resultSet = mock(ResultSet.class);

        transactionFactory = spy(transactionFactory);
        doReturn(transaction).when(transactionFactory).createTransaction();
        doReturn(preparedStatement).when(transaction).prepareStatement(Mockito.anyString());
        doReturn(resultSet).when(preparedStatement).executeQuery();

        if (PREPARESTATEMENT.equals(whenToThrow)) {
            doThrow(exceptionToBeThrown).when(transaction).prepareStatement(Mockito.anyString());
        } else if (BEGIN.equals(whenToThrow)) {
            doThrow(exceptionToBeThrown).when(transaction).begin();
        } else if (EXECUTE.equals(whenToThrow)) {
            doThrow(exceptionToBeThrown).when(preparedStatement).executeQuery();
        } else if (GET.equals(whenToThrow)) {
            doThrow(exceptionToBeThrown).when(resultSet).next();
        }

    }

    @SuppressWarnings("unused")
    private Object[][] dataProviderFor_test_getQuotaTopUpSubscriptions_should_throw_OperationFailedException_when_any_exception_thrown() {
        return new Object[][] {
                {
                        PREPARESTATEMENT, TransactionException.class
                },
                {
                        BEGIN, TransactionException.class
                },
                {
                        EXECUTE, SQLException.class
                },
                {
                        GET, SQLException.class
                }
        };
    }

    /**
     * <PRE>
     * 3 subscription created
     * SubscriberIdentity > Addonsubscription
     * 				 101  ---- > 1,3
     * 	 			 102  ---- > 2
     * 	 			 103  ---- > no subscription
     * </PRE>
     */
    private void createTablesAndInsertSubscriptionRecords() throws Exception {
        LogManager.getLogger().debug(this.getClass().getSimpleName(), "creating DB");

        helper.createTables();

        helper.insertAddonRecords(getAddonSubscritionData());

        LogManager.getLogger().debug(this.getClass().getSimpleName(), "DB created");
    }

    /**
     * Add Adddon record here.
     *
     * Used for inserting table entry and generating expected entity
     */
    private List<SubscriptionData> getAddonSubscritionData() {

        SubscriptionData record1 = new SubscriptionData("101", quotaTopUp.getId(), "2014-10-29 09:26:50.12", "2020-12-31 09:26:50.12",
                "2", "0", "1", "2020-12-31 09:26:50.12",null);

        SubscriptionData record2 = new SubscriptionData("102",  quotaTopUp.getId(), "2014-10-29 09:26:50.12", "2020-12-31 09:26:50.12",
                "2", "0", "2", "2020-12-31 09:26:50.12",null);

        SubscriptionData record3 = new SubscriptionData("101", quotaTopUp.getId(), "2014-10-29 09:26:50.12", "2020-12-31 09:26:50.12",
                "2", "0", "3", "2020-12-31 09:26:50.12",null);

        record1.setQuotaTopUp(quotaTopUp);
        record2.setQuotaTopUp(quotaTopUp);
        record3.setQuotaTopUp(quotaTopUp);

        return Arrays.asList(record1, record2, record3);
    }

    @After
    public void afterDropTables() throws Exception {
        helper.dropTables();
        DBUtility.closeQuietly(transactionFactory.getConnection());
        DerbyUtil.closeDerby(testingDB);
    }

}
