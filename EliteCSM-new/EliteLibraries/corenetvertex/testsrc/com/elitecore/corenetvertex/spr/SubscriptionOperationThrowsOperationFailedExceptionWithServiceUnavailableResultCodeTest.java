package com.elitecore.corenetvertex.spr;

import com.elitecore.commons.base.DBUtility;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.QuotaProfileType;
import com.elitecore.corenetvertex.constants.SubscriptionState;
import com.elitecore.corenetvertex.constants.ValidityPeriodUnit;
import com.elitecore.corenetvertex.core.alerts.AlertListener;
import com.elitecore.corenetvertex.core.transaction.DBTransaction;
import com.elitecore.corenetvertex.core.transaction.DummyTransactionFactory;
import com.elitecore.corenetvertex.core.transaction.Transaction;
import com.elitecore.corenetvertex.core.transaction.TransactionException;
import com.elitecore.corenetvertex.core.transaction.exception.TransactionErrorCode;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.pm.offer.ProductOffer;
import com.elitecore.corenetvertex.pm.pkg.datapackage.AddOn;
import com.elitecore.corenetvertex.pm.store.ProductOfferStore;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.corenetvertex.spr.data.SPRInfoImpl;
import com.elitecore.corenetvertex.spr.data.SubscriberInfo;
import com.elitecore.corenetvertex.spr.data.Subscription;
import com.elitecore.corenetvertex.spr.ddf.SubscriptionParameter;
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
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyCollection;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyShort;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(HierarchicalContextRunner.class)
public class SubscriptionOperationThrowsOperationFailedExceptionWithServiceUnavailableResultCodeTest { //NOSONAR

	private static final int QUERY_TIMEOUT_CODE = 1013;
	private static final int ORACLE_DBDOWN_ERRORCODE = 17002;
	private String subscriberID = "101";
	private DummyTransactionFactory transactionFactory;
	private AlertListener alertListener;
	private Transaction transaction;
	private PreparedStatement preparedStatement;
	private SPRInfoImpl sprInfo;
	private SubscriptionOperationImpl subscriptionOperation;
	private DBTransaction dbTransaction;
	private PolicyRepository policyRepository;
	private String parentId;
	private String addonId = "id";
	private String addonName = "name";
	@Mock
	private UMOperation umOperation;
	@Mock
	private ABMFOperation abmfOperation;
	@Mock
	private AddOn addOn;
	@Mock
	private ProductOffer productOffer;
	private Integer subscriptionStatusValue = 2;
	private Long startTime = System.currentTimeMillis();
	private Long endTime = System.currentTimeMillis()+(3600*1000*24);
	private String param1;
	private String param2;
	private String subscriptionId = "subscriptionId";
	private String rejectReason = "rejectReason";
	private Integer priority = CommonConstants.DEFAULT_SUBSCRIPTION_PRIORITY;
	private Subscription addOnSubscription;

	@BeforeClass
	public static void setUpClass() throws Exception {
		Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
	}

	@Before
	public void setUp() throws Exception {

		MockitoAnnotations.initMocks(this);

		DummyDBDataSource dbDataSource = new DummyDBDataSource("1", SubscriptionOperationTestSuite.DS_NAME, "jdbc:derby:memory:TestingDB;create=true", "", "", 1, 5000, 3000);
		transactionFactory = spy((DummyTransactionFactory) new DummyTransactionFactoryBuilder().withDBDataSource(dbDataSource, 1).build());
		transactionFactory.createTransaction();
		this.policyRepository = mock(PolicyRepository.class);
		this.transaction = mock(Transaction.class);
		this.preparedStatement = mock(PreparedStatement.class);
		this.dbTransaction = mock(DBTransaction.class);
		this.alertListener = mock(AlertListener.class);
		doReturn(transaction).when(transactionFactory).createTransaction();
		doReturn(dbTransaction).when(transactionFactory).createReadOnlyTransaction();
		doReturn(preparedStatement).when(transaction).prepareStatement(anyString());
		doReturn(preparedStatement).when(dbTransaction).prepareStatement(anyString());
		doNothing().when(preparedStatement).setString(anyShort(), anyString());
		when(policyRepository.getActiveAddOnById(Mockito.anyString())).thenReturn(addOn);
		when(policyRepository.getActiveAddOnByName(Mockito.anyString())).thenReturn(addOn);
		when(policyRepository.getActiveAddOnByName(Mockito.anyString())).thenReturn(addOn);
		ProductOfferStore productOfferStore = mock(ProductOfferStore.class);
		when(policyRepository.getProductOffer()).thenReturn(productOfferStore);
		when(productOfferStore.byId(Mockito.anyString())).thenReturn(productOffer);
		when(productOfferStore.byName(Mockito.anyString())).thenReturn(productOffer);
		when(productOffer.getDataServicePkgData()).thenReturn(addOn);
		when(addOn.getValidityPeriodUnit()).thenReturn(ValidityPeriodUnit.DAY);
		when(addOn.getQuotaProfileType()).thenReturn(QuotaProfileType.USAGE_METERING_BASED);
		subscriptionOperation = new SubscriptionOperationImpl(alertListener, policyRepository, umOperation, abmfOperation, null, null, null);
		addOnSubscription = new Subscription.SubscriptionBuilder()
				.withStatus(SubscriptionState.STARTED)
				.withId(subscriptionId)
				.withPackageId(addonId)
				.withEndTime(null)
				.withStartTime(null)
				.withSubscriberIdentity(subscriberID)
				.build();
	}

    @After
	public void tearDown() throws SQLException {
		DBUtility.closeQuietly(transactionFactory.getConnection());
		DerbyUtil.closeDerby("TestingDB");
	}

	public class subscribeAddOnById {

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
				subscriptionOperation.subscribeAddOnProductOfferById(
                        new SubscriptionParameter(createSprInfo(subscriberID), parentId, addonId, subscriptionStatusValue, null, null, null, startTime, endTime, null, param1, param2, null,null), transactionFactory, null);
			} catch (OperationFailedException e) {
				assertSame(ResultCode.SERVICE_UNAVAILABLE, e.getErrorCode());
				throw e;
			}
			fail("subscribeAddOnProductOfferById should throw Exception");
		}


	}

	public class subscribeAddOnByName {

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
				subscriptionOperation.subscribeProductOfferByName(
                        new SubscriptionParameter(createSprInfo(subscriberID), parentId, null,subscriptionStatusValue, addonName,  null, null, startTime, endTime, null, param1, param2, null,null), transactionFactory);
			} catch (OperationFailedException e) {
				assertSame(ResultCode.SERVICE_UNAVAILABLE, e.getErrorCode());
				throw e;
			}
			fail("subscribeProductOfferAddOnByName should throw Exception");
		}
	}

	public class getAddonSubscriptions {

		@Test(expected = OperationFailedException.class)
		public void querytimeOut() throws Exception {
			setUpMockToGenerateSQLExceptionOnExecuteQuery(QUERY_TIMEOUT_CODE, false);
			doReturn(false).when(dbTransaction).isDBDownSQLException(any(SQLException.class));
			executeAndAssert();
		}

		@Test(expected = OperationFailedException.class)
		public void dbDownException() throws Exception {
			setUpMockToGenerateSQLExceptionOnExecuteQuery(ORACLE_DBDOWN_ERRORCODE,  true);
			doReturn(true).when(dbTransaction).isDBDownSQLException(any(SQLException.class));
			executeAndAssert();
		}

		@Test(expected = OperationFailedException.class)
		public void connectionNotFound() throws Exception {
			setUpMockToGenerateConnectionNotFound();
			executeAndAssert();
		}

		private void executeAndAssert() throws OperationFailedException {
			try {
				subscriptionOperation.getSubscriptions(subscriberID, transactionFactory);
			} catch (OperationFailedException e) {
				assertSame(ResultCode.SERVICE_UNAVAILABLE, e.getErrorCode());
				throw e;
			}
			fail("getSubscriptions should throw Exception");
		}
	}

	public class updateSubscription {

		@Before
		public void setUp() {
			subscriptionStatusValue = 5;
		}
		@Test(expected = OperationFailedException.class)
		public void querytimeOut() throws Exception {
			setUpMockToGenerateSQLExceptionOnExecuteUpdate(QUERY_TIMEOUT_CODE, false);
			subscriptionOperation =  spy(subscriptionOperation);
			doReturn(addOnSubscription).when(subscriptionOperation).getActiveSubscriptionBySubscriptionId(anyString(), anyLong(), eq(transactionFactory));
			executeAndAssert();
		}

		@Test(expected = OperationFailedException.class)
		public void dbDownException() throws Exception {
			setUpMockToGenerateSQLExceptionOnExecuteUpdate(ORACLE_DBDOWN_ERRORCODE,  true);
			subscriptionOperation =  spy(subscriptionOperation);
			doReturn(addOnSubscription).when(subscriptionOperation).getActiveSubscriptionBySubscriptionId(anyString(), anyLong(), eq(transactionFactory));
			executeAndAssert();
		}

		@Test(expected = OperationFailedException.class)
		public void connectionNotFound() throws Exception {
			setUpMockToGenerateConnectionNotFound();
			executeAndAssert();
		}

		private void executeAndAssert() throws OperationFailedException {
			try {
				subscriptionOperation.updateSubscription(subscriberID,
						subscriptionId, subscriptionStatusValue,
						startTime,
						endTime,
                        priority, rejectReason,
						transactionFactory);
			} catch (OperationFailedException e) {
				assertSame(ResultCode.SERVICE_UNAVAILABLE, e.getErrorCode());
				throw e;
			}
			fail("updateSubscription should throw Exception");
		}
	}

	public class unsubscribeBySubscriberId {

		@Test(expected = OperationFailedException.class)
		public void querytimeOut() throws Exception {
			setUpMockToGenerateSQLExceptionOnExecuteUpdate(QUERY_TIMEOUT_CODE, false);
			subscriptionOperation =  spy(subscriptionOperation);
			doReturn(addOnSubscription).when(subscriptionOperation).getActiveSubscriptionBySubscriptionId(anyString(), anyLong(), eq(transactionFactory));
			executeAndAssert();
		}

		@Test(expected = OperationFailedException.class)
		public void dbDownException() throws Exception {
			setUpMockToGenerateSQLExceptionOnExecuteUpdate(ORACLE_DBDOWN_ERRORCODE,  true);
			subscriptionOperation =  spy(subscriptionOperation);
			doReturn(addOnSubscription).when(subscriptionOperation).getActiveSubscriptionBySubscriptionId(anyString(), anyLong(), eq(transactionFactory));
			executeAndAssert();
		}

		private void executeAndAssert() throws Exception {
			try {
				subscriptionOperation.unsubscribeBySubscriberId(subscriberID, transaction);
			} catch (OperationFailedException e) {
				assertSame(ResultCode.SERVICE_UNAVAILABLE, e.getErrorCode());
				throw e;
			}
			fail("unsubscribeBySubscriberId should throw Exception");
		}
	}

	public class importSubscriptionsAndUsages {

		List<SubscriberUsage> basePackageUsages;

		@Before
		public void setUp() {
			basePackageUsages = new ArrayList<SubscriberUsage>();

			SubscriberUsage subscriberUsage = new SubscriberUsage.SubscriberUsageBuilder("id",
					subscriberID,
					"serviceId",
					"quotaProfileId",
					"packageId","productOfferId").build();

			basePackageUsages.add(subscriberUsage);
		}

		@Test(expected = OperationFailedException.class)
		public void dbDownException() throws Exception {
			doThrow(new TransactionException("error", TransactionErrorCode.CONNECTION_NOT_FOUND)).when(umOperation).insert(anyString(), anyCollection(), eq(transaction));
			doReturn(true).when(transaction).isDBDownSQLException(any(SQLException.class));
			executeAndAssert();
		}

		private void executeAndAssert() throws Exception {
			try {

				SubscriberInfo subscriberInfo = new SubscriberInfo(null, null, basePackageUsages);

				subscriptionOperation.importSubscriptionsAndUsages(createSprInfo(subscriberID), subscriberInfo, transaction);

			} catch (OperationFailedException e) {
				assertSame(ResultCode.SERVICE_UNAVAILABLE, e.getErrorCode());
				throw e;
			}
			fail("unsubscribeBySubscriberId should throw Exception");
		}
	}

	private void setUpMockToGenerateConnectionNotFound() throws TransactionException {
		doThrow(new TransactionException("Connection not found", TransactionErrorCode.CONNECTION_NOT_FOUND)).when(transaction).begin();
		doThrow(new TransactionException("Connection not found", TransactionErrorCode.CONNECTION_NOT_FOUND)).when(dbTransaction).begin();
	}

	private void setUpMockToGenerateSQLExceptionOnExecuteQuery(int errorCode, boolean isDbDownError) throws Exception {
		doReturn(isDbDownError).when(transaction).isDBDownSQLException(any(SQLException.class));
		when(preparedStatement.executeQuery()).thenThrow(new SQLException("sql error", "sql error", errorCode));
	}
	private void setUpMockToGenerateSQLExceptionOnExecuteUpdate(int errorCode, boolean isDbDownError) throws Exception {
		doReturn(isDbDownError).when(transaction).isDBDownSQLException(any(SQLException.class));
		when(preparedStatement.executeUpdate()).thenThrow(new SQLException("sql error", "sql error", errorCode));
	}

	private SPRInfo createSprInfo(String subscriberIdentity) {
		SPRInfoImpl sprInfo = new SPRInfoImpl();
		sprInfo.setSubscriberIdentity(subscriberIdentity);
		return sprInfo;
	}
}
