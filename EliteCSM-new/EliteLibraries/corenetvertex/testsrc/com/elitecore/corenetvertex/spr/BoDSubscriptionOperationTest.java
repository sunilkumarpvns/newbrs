package com.elitecore.corenetvertex.spr;

import com.elitecore.commons.base.DBUtility;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.core.transaction.DummyTransactionFactory;
import com.elitecore.corenetvertex.core.transaction.Transaction;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.pm.bod.BoDPackage;
import com.elitecore.corenetvertex.pm.store.BoDPackageStore;
import com.elitecore.corenetvertex.spr.data.SubscriptionData;
import com.elitecore.corenetvertex.util.DerbyUtil;
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
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(JUnitParamsRunner.class)
public class BoDSubscriptionOperationTest { // NOSONAR

	private static final String PREPARESTATEMENT = "preparedStatement";
	private static final String BEGIN = "begin";
	private static final String EXECUTE = "execute";
	private static final String GET = "get";
	private String testDB = UUID.randomUUID().toString();

	@Mock
	private PolicyRepository policyRepository;
	@Mock
	BoDPackageStore bodPackageStore;
	@Mock
	private BoDPackage bodPackage;

	private DummyTransactionFactory transactionFactory;
	@Rule
	public ExpectedException expectedException = ExpectedException.none();
	private BoDSubscriptionOperationTestSuite.BoDSubscriptionDBHelper helper;

	@BeforeClass
	public static void setUpClass() throws Exception {
		Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
	}

	@Before
	public void setUp() throws Exception {

		MockitoAnnotations.initMocks(this);
        String url = "jdbc:derby:memory:"+ testDB +";create=true";
        DummyDBDataSource dbDataSource = new DummyDBDataSource("1", SubscriptionOperationTestSuite.DS_NAME, url, "", "", 1, 5000, 3000);
		transactionFactory = (DummyTransactionFactory) new DummyTransactionFactoryBuilder().withDBDataSource(dbDataSource, 1).build();

		helper = new BoDSubscriptionOperationTestSuite.BoDSubscriptionDBHelper(transactionFactory);

		createTablesAndInsertSubscriptionRecords();
		when(policyRepository.getBoDPackage().byId(Mockito.anyString())).thenReturn(bodPackage);

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

	/**
	 * <PRE>
	 * 3 subscription created
	 * SubscriberIdentity > BoDsubscription
	 * 				 101  ---- > 1,3 
	 * 	 			 102  ---- > 2
	 * 	 			 103  ---- > no subscription
	 * </PRE>
	 */
	private void createTablesAndInsertSubscriptionRecords() throws Exception {
		LogManager.getLogger().debug(this.getClass().getSimpleName(), "creating DB");

		helper.createTables();

		helper.insertBoDRecords(getBoDSubscritionData());

		LogManager.getLogger().debug(this.getClass().getSimpleName(), "DB created");
	}

	/**
	 * Add Adddon record here.
	 * 
	 * Used for inserting table entry and generating expected entity
	 */
	private List<SubscriptionData> getBoDSubscritionData() {

		SubscriptionData record1 = new SubscriptionData("101", bodPackage.getId(), "2014-10-29 09:26:50.12", "2020-12-31 09:26:50.12",
				"2", "0", "1", "2020-12-31 09:26:50.12",null);

		SubscriptionData record2 = new SubscriptionData("102",  bodPackage.getId(), "2014-10-29 09:26:50.12", "2020-12-31 09:26:50.12",
				"2", "0", "2", "2020-12-31 09:26:50.12",null);

		SubscriptionData record3 = new SubscriptionData("101", bodPackage.getId(), "2014-10-29 09:26:50.12", "2020-12-31 09:26:50.12",
				"2", "0", "3", "2020-12-31 09:26:50.12",null);
		
		record1.setBod(bodPackage);
		record2.setBod(bodPackage);
		record3.setBod(bodPackage);
		
		return Arrays.asList(record1, record2, record3);
	}

	@After
	public void afterDropTables() throws Exception {
		helper.dropTables();
		DBUtility.closeQuietly(transactionFactory.getConnection());
        DerbyUtil.closeDerby(testDB);
	}

}
