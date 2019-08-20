package com.elitecore.corenetvertex.spr;

import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.constants.ValidityPeriodUnit;
import com.elitecore.corenetvertex.core.alerts.AlertListener;
import com.elitecore.corenetvertex.core.transaction.DBTransaction;
import com.elitecore.corenetvertex.core.transaction.DummyTransactionFactory;
import com.elitecore.corenetvertex.core.transaction.Transaction;
import com.elitecore.corenetvertex.core.transaction.TransactionException;
import com.elitecore.corenetvertex.core.transaction.exception.TransactionErrorCode;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pkg.PkgType;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.pm.offer.ProductOffer;
import com.elitecore.corenetvertex.pm.pkg.datapackage.AddOn;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfile;
import com.elitecore.corenetvertex.pm.store.ProductOfferStore;
import com.elitecore.corenetvertex.spr.data.SPRInfoImpl;
import com.elitecore.corenetvertex.spr.data.Subscription;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.corenetvertex.util.DerbyUtil;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyShort;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(HierarchicalContextRunner.class)
public class UMOperationThrowsOperationFailedExceptionWithServiceUnavailableResultCodeTest {
	public static final String DS_NAME = "test-DB";
	private static final int QUERY_TIMEOUT_CODE = 1013;
	private static final int ORACLE_DBDOWN_ERRORCODE = 17002;
	private String subscriberID = "101";
	private DummyTransactionFactory transactionFactory;
	private Transaction transaction;
	private PreparedStatement preparedStatement;
	private SPRInfoImpl sprInfo;
	private SubscriptionOperationImpl subscriptionOperation;
	private DBTransaction dbTransaction;
	private PolicyRepository policyRepository;
	@Mock
	private UMOperation umOperation;
	private AlertListener alertListener;
	@Mock
	private AddOn addOn;
	private String packageId;
	private SubscriberUsage subscriberUsage;
	private Map<String, Subscription> addOnSubscriptions;
	private static final String currency = "INR";

	@BeforeClass
	public static void setUpClass() throws Exception {
		Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
	}

	@Before
	public void setUp() throws Exception {

		MockitoAnnotations.initMocks(this);

		DummyDBDataSource dbDataSource = new DummyDBDataSource("1", DS_NAME, "jdbc:derby:memory:TestingDB;create=true", "", "", 1, 5000, 3000);
		transactionFactory = spy((DummyTransactionFactory) new DummyTransactionFactoryBuilder().withDBDataSource(dbDataSource, 1).build());
		transactionFactory.createTransaction();
		this.policyRepository = mock(PolicyRepository.class);
		this.transaction = mock(Transaction.class);
		this.preparedStatement = mock(PreparedStatement.class);
		this.dbTransaction = mock(DBTransaction.class);
		this.alertListener = mock(AlertListener.class);
		this.sprInfo = new SPRInfoImpl();
        QuotaProfile  quotaProfile = mock(QuotaProfile.class);
		doReturn(transaction).when(transactionFactory).createTransaction();
		doReturn(dbTransaction).when(transactionFactory).createReadOnlyTransaction();
		doReturn(preparedStatement).when(transaction).prepareStatement(anyString());
		doReturn(preparedStatement).when(dbTransaction).prepareStatement(anyString());
		doNothing().when(preparedStatement).setString(anyShort(), anyString());
		doReturn(Arrays.asList(quotaProfile)).when(addOn).getQuotaProfiles();
		when(policyRepository.getPkgDataById(Mockito.anyString())).thenReturn(addOn);
		ProductOfferStore productOfferStore = spy(ProductOfferStore.class);
		Mockito.when(policyRepository.getProductOffer()).thenReturn(productOfferStore);

		ProductOffer productOffer = spy(new ProductOffer("test", "test", null, PkgType.BASE, PkgMode.LIVE, 0, ValidityPeriodUnit.DAY,
				0.0,0.0,PkgStatus.ACTIVE, null, null,"addonId", null, null,
				null, PolicyStatus.SUCCESS, null, null,false, null, null, policyRepository,null,null,new HashMap<>(),currency));

		Mockito.when(productOfferStore.byId(Mockito.anyString())).thenReturn(productOffer);
		Mockito.when(productOfferStore.byName(Mockito.anyString())).thenReturn(productOffer);

		umOperation = new UMOperation(alertListener, policyRepository);

		subscriberUsage = new SubscriberUsage.SubscriberUsageBuilder("id",
				subscriberID,
				"serviceId",
				"quotaProfileId",
				"packageId","productOfferId")
				.build();

		addOnSubscriptions = new HashMap<String, Subscription>();
		addOnSubscriptions.put("key1", new Subscription.SubscriptionBuilder().build());

	}

	@After
	public void tearDown() throws SQLException {
		transactionFactory.getConnection().close();
		DerbyUtil.closeDerby("TestingDB");
	}

	public class getUsage {

		@Test(expected = OperationFailedException.class)
		public void transactionFactoryIsNotAlive() throws Exception {
			setUpTransactionFactoryIsNotAlive();
			executeAndAssert();
		}

		@Test(expected = OperationFailedException.class)
		public void transactionNotCreated() throws Exception {
			setUpTransactionNotCreated();
			executeAndAssert();
		}

		@Test(expected = OperationFailedException.class)
		public void querytimeOut() throws Exception {
			setUpMockToGenerateSQLExceptionOnExecuteQuery(QUERY_TIMEOUT_CODE, false);
			executeAndAssert();
		}

		@Test(expected = OperationFailedException.class)
		public void dbDownException() throws Exception {
			setUpMockToGenerateSQLExceptionOnExecuteQuery(ORACLE_DBDOWN_ERRORCODE,  true);
			executeAndAssert();
		}

		@Test(expected = OperationFailedException.class)
		public void connectionNotFound() throws Exception {
			setUpMockToGenerateConnectionNotFound();
			executeAndAssert();
		}

		private void executeAndAssert() throws OperationFailedException {
			try {
				umOperation.getUsage(subscriberID, null,
						transactionFactory);
			} catch (OperationFailedException e) {
				assertSame(ResultCode.SERVICE_UNAVAILABLE, e.getErrorCode());
				throw e;
			}
			fail("getUsage should throw Exception");
		}
	}

	public class replace {

		@Test(expected = OperationFailedException.class)
		public void transactionFactoryIsNotAlive() throws Exception {
			setUpTransactionFactoryIsNotAlive();
			executeAndAssert();
		}

		@Test(expected = OperationFailedException.class)
		public void transactionNotCreated() throws Exception {
			setUpTransactionNotCreated();
			executeAndAssert();
		}

		@Test(expected = OperationFailedException.class)
		public void querytimeOut() throws Exception {
			setUpMockToGenerateSQLExceptionOnExecuteQuery(QUERY_TIMEOUT_CODE, false);
			executeAndAssert();
		}

		@Test(expected = OperationFailedException.class)
		public void dbDownException() throws Exception {
			setUpMockToGenerateSQLExceptionOnExecuteQuery(ORACLE_DBDOWN_ERRORCODE,  true);
			executeAndAssert();
		}

		@Test(expected = OperationFailedException.class)
		public void connectionNotFound() throws Exception {
			setUpMockToGenerateConnectionNotFound();
			executeAndAssert();
		}

		private void executeAndAssert() throws OperationFailedException {
			try {
				umOperation.replace(subscriberID, Arrays.asList(subscriberUsage),
						transactionFactory);
			} catch (OperationFailedException e) {
				assertSame(ResultCode.SERVICE_UNAVAILABLE, e.getErrorCode());
				throw e;
			}
			fail("getUsage should throw Exception");
		}
	}

	public class insert {

		@Test(expected = OperationFailedException.class)
		public void transactionFactoryIsNotAlive() throws Exception {
			setUpTransactionFactoryIsNotAlive();
			executeAndAssert();
		}

		@Test(expected = OperationFailedException.class)
		public void transactionNotCreated() throws Exception {
			setUpTransactionNotCreated();
			executeAndAssert();
		}

		@Test(expected = OperationFailedException.class)
		public void querytimeOut() throws Exception {
			setUpMockToGenerateSQLExceptionOnExecuteUpdate(QUERY_TIMEOUT_CODE, false);
			executeAndAssert();
		}

		@Test(expected = OperationFailedException.class)
		public void dbDownException() throws Exception {
			setUpMockToGenerateSQLExceptionOnExecuteUpdate(ORACLE_DBDOWN_ERRORCODE,  true);
			executeAndAssert();
		}

		@Test(expected = OperationFailedException.class)
		public void connectionNotFound() throws Exception {
			setUpMockToGenerateConnectionNotFound();
			executeAndAssert();
		}

		private void executeAndAssert() throws OperationFailedException {
			try {
				umOperation.insert(subscriberID, Arrays.asList(subscriberUsage), transactionFactory);
			} catch (OperationFailedException e) {
				assertSame(ResultCode.SERVICE_UNAVAILABLE, e.getErrorCode());
				throw e;
			}
			fail("getUsage should throw Exception");
		}
	}



	public class resetUsage {

		@Test(expected = OperationFailedException.class)
		public void transactionFactoryIsNotAlive() throws Exception {
			setUpTransactionFactoryIsNotAlive();
			executeAndAssert();
		}

		@Test(expected = OperationFailedException.class)
		public void transactionNotCreated() throws Exception {
			setUpTransactionNotCreated();
			executeAndAssert();
		}

		@Test(expected = OperationFailedException.class)
		public void querytimeOut() throws Exception {
			setUpMockToGenerateSQLExceptionOnExecuteUpdate(QUERY_TIMEOUT_CODE, false);
			executeAndAssert();
		}

		@Test(expected = OperationFailedException.class)
		public void dbDownException() throws Exception {
			setUpMockToGenerateSQLExceptionOnExecuteUpdate(ORACLE_DBDOWN_ERRORCODE,  true);
			executeAndAssert();
		}

		@Test(expected = OperationFailedException.class)
		public void connectionNotFound() throws Exception {
			setUpMockToGenerateConnectionNotFound();
			executeAndAssert();
		}

		private void executeAndAssert() throws OperationFailedException {
			try {
				umOperation.resetUsage(subscriberID, packageId, transactionFactory);
			} catch (OperationFailedException e) {
				assertSame(ResultCode.SERVICE_UNAVAILABLE, e.getErrorCode());
				throw e;
			}
			fail("resetUsage should throw Exception");
		}
	}

	public class addToExisting {

		@Test(expected = OperationFailedException.class)
		public void transactionFactoryIsNotAlive() throws Exception {
			setUpTransactionFactoryIsNotAlive();
			executeAndAssert();
		}

		@Test(expected = OperationFailedException.class)
		public void transactionNotCreated() throws Exception {
			setUpTransactionNotCreated();
			executeAndAssert();
		}

		@Test(expected = OperationFailedException.class)
		public void querytimeOut() throws Exception {
			setUpMockToGenerateSQLExceptionOnExecuteUpdate(QUERY_TIMEOUT_CODE, false);
			executeAndAssert();
		}

		@Test(expected = OperationFailedException.class)
		public void dbDownException() throws Exception {
			setUpMockToGenerateSQLExceptionOnExecuteUpdate(ORACLE_DBDOWN_ERRORCODE,  true);
			executeAndAssert();
		}

		@Test(expected = OperationFailedException.class)
		public void connectionNotFound() throws Exception {
			setUpMockToGenerateConnectionNotFound();
			executeAndAssert();
		}

		private void executeAndAssert() throws OperationFailedException {
			try {
				umOperation.addToExisting(subscriberID, Arrays.asList(subscriberUsage), transactionFactory);
			} catch (OperationFailedException e) {
				assertSame(ResultCode.SERVICE_UNAVAILABLE, e.getErrorCode());
				throw e;
			}
			fail("addToExisting should throw Exception");
		}
	}

	public class deleteBaseAndPromotionalPackageUsage {

		@Test(expected = OperationFailedException.class)
		public void querytimeOut() throws Exception {
			setUpMockToGenerateSQLExceptionOnExecuteUpdate(QUERY_TIMEOUT_CODE, false);
			executeAndAssert();
		}

		@Test(expected = OperationFailedException.class)
		public void dbDownException() throws Exception {
			setUpMockToGenerateSQLExceptionOnExecuteUpdate(ORACLE_DBDOWN_ERRORCODE,  true);
			executeAndAssert();
		}

		private void executeAndAssert() throws Exception {
			try {
				umOperation.deleteBaseAndPromotionalPackageUsage(subscriberID, transaction);
			} catch (OperationFailedException e) {
				assertSame(ResultCode.SERVICE_UNAVAILABLE, e.getErrorCode());
				throw e;
			}
			fail("deleteBaseAndPromotionalPackageUsage should throw Exception");
		}
	}

	public class deleteBasePackageUsage {

		@Test(expected = OperationFailedException.class)
		public void querytimeOut() throws Exception {
			setUpMockToGenerateSQLExceptionOnExecuteUpdate(QUERY_TIMEOUT_CODE, false);
			executeAndAssert();
		}

		@Test(expected = OperationFailedException.class)
		public void dbDownException() throws Exception {
			setUpMockToGenerateSQLExceptionOnExecuteUpdate(ORACLE_DBDOWN_ERRORCODE,  true);
			executeAndAssert();
		}

		private void executeAndAssert() throws Exception {
			try {
				umOperation.deleteBasePackageUsage(subscriberID, packageId, transaction);
			} catch (OperationFailedException e) {
				assertSame(ResultCode.SERVICE_UNAVAILABLE, e.getErrorCode());
				throw e;
			}
			fail("deleteBaseAndPromotionalPackageUsage should throw Exception");
		}
	}

	public class resetBillingCycle {

		@Test(expected = OperationFailedException.class)
		public void transactionFactoryIsNotAlive() throws Exception {
			setUpTransactionFactoryIsNotAlive();
			executeAndAssert();
		}

		@Test(expected = OperationFailedException.class)
		public void transactionNotCreated() throws Exception {
			setUpTransactionNotCreated();
			executeAndAssert();
		}

		@Test(expected = OperationFailedException.class)
		public void querytimeOut() throws Exception {
			setUpMockToGenerateSQLExceptionOnExecuteUpdate(QUERY_TIMEOUT_CODE, false);
			executeAndAssert();
		}

		@Test(expected = OperationFailedException.class)
		public void dbDownException() throws Exception {
			setUpMockToGenerateSQLExceptionOnExecuteUpdate(ORACLE_DBDOWN_ERRORCODE,  true);
			executeAndAssert();
		}

		@Test(expected = OperationFailedException.class)
		public void connectionNotFound() throws Exception {
			setUpMockToGenerateConnectionNotFound();
			executeAndAssert();
		}

		private void executeAndAssert() throws Exception {
			try {
				umOperation.resetBillingCycle(subscriberID, subscriberID, packageId,
				0, "resetReason", "parameter1",
						"parameter2", "parameter3", transactionFactory);
			} catch (OperationFailedException e) {
				assertSame(ResultCode.SERVICE_UNAVAILABLE, e.getErrorCode());
				throw e;
			}
			fail("resetBillingCycle should throw Exception");
		}
	}

	public class scheduleForUsageDelete {

		@Test(expected = OperationFailedException.class)
		public void querytimeOut() throws Exception {
			setUpMockToGenerateSQLExceptionOnExecuteUpdate(QUERY_TIMEOUT_CODE, false);
			executeAndAssert();
		}

		@Test(expected = OperationFailedException.class)
		public void dbDownException() throws Exception {
			setUpMockToGenerateSQLExceptionOnExecuteUpdate(ORACLE_DBDOWN_ERRORCODE,  true);
			executeAndAssert();
		}

		private void executeAndAssert() throws Exception {
			try {
				umOperation.scheduleForUsageDelete(subscriberID, subscriberID, "currentProductOfferId", "resetReason", "parameter1",
						"parameter2","parameter3", transaction);
			} catch (OperationFailedException e) {
				assertSame(ResultCode.SERVICE_UNAVAILABLE, e.getErrorCode());
				throw e;
			}
			fail("scheduleForUsageDelete should throw Exception");
		}
	}

	public class getBalance {

		@Test(expected = OperationFailedException.class)
		public void querytimeOut() throws Exception {
			setUpMockToGenerateSQLExceptionOnExecuteQuery(QUERY_TIMEOUT_CODE, false);
			executeAndAssert();
		}

		@Test(expected = OperationFailedException.class)
		public void dbDownException() throws Exception {
			setUpMockToGenerateSQLExceptionOnExecuteQuery(ORACLE_DBDOWN_ERRORCODE,  true);
			executeAndAssert();
		}

		private void executeAndAssert() throws Exception {
			try {
				umOperation.getBalance(sprInfo, "pkgId", "key1",
						addOnSubscriptions, transactionFactory);
			} catch (OperationFailedException e) {
				assertSame(ResultCode.SERVICE_UNAVAILABLE, e.getErrorCode());
				throw e;
			}
			fail("getBalance should throw Exception");
		}
	}

	private void setUpMockToGenerateConnectionNotFound() throws TransactionException {
		doThrow(new TransactionException("Connection not found", TransactionErrorCode.CONNECTION_NOT_FOUND)).when(transaction).begin();
		doThrow(new TransactionException("Connection not found", TransactionErrorCode.CONNECTION_NOT_FOUND)).when(dbTransaction).begin();
	}

	private void setUpMockToGenerateSQLExceptionOnExecuteQuery(int errorCode, boolean isDbDownError) throws Exception {
		doReturn(isDbDownError).when(transaction).isDBDownSQLException(any(SQLException.class));
		doReturn(isDbDownError).when(dbTransaction).isDBDownSQLException(any(SQLException.class));
		when(preparedStatement.executeQuery()).thenThrow(new SQLException("sql error", "sql error", errorCode));
	}
	private void setUpMockToGenerateSQLExceptionOnExecuteUpdate(int errorCode, boolean isDbDownError) throws Exception {
		doReturn(isDbDownError).when(transaction).isDBDownSQLException(any(SQLException.class));
		doReturn(isDbDownError).when(dbTransaction).isDBDownSQLException(any(SQLException.class));
		when(preparedStatement.executeUpdate()).thenThrow(new SQLException("sql error", "sql error", errorCode));
	}

	private void setUpTransactionFactoryIsNotAlive() {
		when(transactionFactory.isAlive()).thenReturn(false);
	}

	private void setUpTransactionNotCreated() {
		when(transactionFactory.createTransaction()).thenReturn(null);
		when(transactionFactory.createReadOnlyTransaction()).thenReturn(null);
	}

}
