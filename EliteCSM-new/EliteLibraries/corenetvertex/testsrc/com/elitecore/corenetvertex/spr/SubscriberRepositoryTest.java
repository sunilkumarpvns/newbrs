package com.elitecore.corenetvertex.spr;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.DBUtility;
import com.elitecore.corenetvertex.constants.CustomerType;
import com.elitecore.corenetvertex.constants.SubscriberStatus;
import com.elitecore.corenetvertex.core.alerts.AlertConstants;
import com.elitecore.corenetvertex.core.alerts.AlertListener;
import com.elitecore.corenetvertex.core.alerts.Alerts;
import com.elitecore.corenetvertex.core.transaction.DummyTransactionFactory;
import com.elitecore.corenetvertex.core.transaction.Transaction;
import com.elitecore.corenetvertex.core.transaction.TransactionException;
import com.elitecore.corenetvertex.core.transaction.exception.TransactionErrorCode;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.spr.SubscriberRepositoryTestSuite.SPROperationTestHelper;
import com.elitecore.corenetvertex.spr.SubscriberRepositoryTestSuite.UMConfigurationImpl;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.corenetvertex.spr.data.SPRInfoImpl;
import com.elitecore.corenetvertex.spr.data.SubscriberProfileData;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.util.DerbyUtil;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;
import org.unitils.reflectionassert.ReflectionAssert;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(JUnitParamsRunner.class)
public class SubscriberRepositoryTest {

	private static final String DS_NAME = "test-DB";
	private static final String PREPARESTATEMENT = "preparedStatement";
	private static final String BEGIN = "begin";
	private static final String EXECUTE = "execute";
	private static final String GET = "get";

	private DummyTransactionFactory transactionFactory;
	private SPROperationTestHelper helper;
	private UMConfigurationImpl umConfiguration;

	private SubscriberRepositoryTestSuite.ABMFConfigurationImpl abmFconfiguration;
	private AlertListener alertListener;
	private PolicyRepository policyRepository;
	private UMOperation umOperation;

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@BeforeClass
	public static void setUpClass() throws Exception {
		Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
	}

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		DummyDBDataSource dbDataSource = new DummyDBDataSource("1", DS_NAME, "jdbc:derby:memory:TestingDB;create=true", "", "", 1, 5000, 3000);

		abmFconfiguration = new SubscriberRepositoryTestSuite.ABMFConfigurationImpl();
		policyRepository = mock(PolicyRepository.class);
		alertListener = mock(AlertListener.class);
		umOperation = spy(new UMOperation(alertListener, policyRepository));

		transactionFactory = spy((DummyTransactionFactory) new DummyTransactionFactoryBuilder().withDBDataSource(dbDataSource, 1).build());

		helper = new SPROperationTestHelper(transactionFactory);
		/* only table created, no data entered so all will be unknown user */
        helper.createProfileTable();
        helper.createUsageTable();
	}

	private List<SubscriberProfileData> getProfiles() {
		SubscriberProfileData data1 = new SubscriberProfileData.SubscriberProfileDataBuilder()
				.withSubscriberIdentity("101")
				.withImsi("1234")
				.withMsisdn("9797979797")
				.withUserName("user1")
				.withPassword("user1")
				.withStatus(SubscriberStatus.ACTIVE.name())
				.withPhone("123456").build();

		SubscriberProfileData data2 = new SubscriberProfileData.SubscriberProfileDataBuilder()
				.withSubscriberIdentity("102")
				.withImsi("12345")
				.withMsisdn("9797979798")
				.withUserName("user2")
				.withPassword("user2")
				.withStatus(SubscriberStatus.ACTIVE.name())
				.withPhone("123456").build();

		SubscriberProfileData data3 = new SubscriberProfileData.SubscriberProfileDataBuilder()
				.withSubscriberIdentity("103")
				.withImsi("123456")
				.withMsisdn("9797979799")
				.withUserName("user3")
				.withPassword("user3")
				.withStatus(SubscriberStatus.ACTIVE.name())
				.withPhone("123456").build();

		return Arrays.asList(data1, data2, data3);
	}

	@Test
	@Parameters(value = { "101", "102", "103" })
	public void test_getSPRInfo_should_give_profile_when_user_found_from_DB(
			String subscriberIdentity) throws Exception {

		helper.insertProfile(getProfiles());
        SubscriberRepository operation = createSubscriberRepository(alertListener);

        SPRInfo actualResult = operation.getProfile(subscriberIdentity);
		SPRInfo expectedResuilt = helper.getExpectedSPRInfoForSubscriber(subscriberIdentity);

		assertSPRInfoEquals(actualResult, expectedResuilt);
	}

	private void assertSPRInfoEquals(SPRInfo actualResult, SPRInfo expectedResuilt) {
		expectedResuilt.setSprLoadTime(actualResult.getSprLoadTime());
		expectedResuilt.setSprReadTime(actualResult.getSprReadTime());
		ReflectionAssert.assertReflectionEquals(expectedResuilt, actualResult);
	}

	/**
	 * This fields will be ignored during checking equality of two profiles.
	 * 
	 */
	private String[] getIngoreFieldsForEquals() {

		return new String[] { "subscriptionProvider", "addOnSubscriptions"
				, "usageProvider", "currentUsage", "bodSubscription" };
	}

	@Test
	@Parameters(value = { "xyz", "abc", "dsff &&", "&*%*&^", "11", "" })
	public void test_getSPRInfo_should_give_unknown_profile_when_user_not_found(
			String subscriberIdentity) throws Exception {

        SubscriberRepository operation = createSubscriberRepository(alertListener);


        SPRInfo actualResult = operation.getProfile(subscriberIdentity);
		SPRInfo expectedResuilt = helper.getExpectedSPRInfoForSubscriber(subscriberIdentity);

		assertSPRInfoEquals(actualResult, expectedResuilt);
	}

    private SubscriberRepository createSubscriberRepository(AlertListener alertListener) throws Exception {
        return new SubscriberRepositoryImpl(null,
                    null,
				transactionFactory,
                    alertListener,
                    null,
                    umOperation,
                    abmFconfiguration,
                    null,
                    Collectionz.<String>newArrayList(),
                    null,
                    null,
                    null, null, null, null, null, "INR");
    }

    private void setUpMockTransactionFactoryDead() throws Exception {
		when(transactionFactory.isAlive()).thenReturn(false);
	}

	@Test
	public void test_getSPRInfo_return_SPRInfoWithUnAvailableStatus() throws Exception {


		setUpMockTransactionFactoryDead();

        SubscriberRepository operation = createSubscriberRepository(alertListener);

		SPRInfo actual = operation.getProfile("101");

		SPRInfo expected = new SPRInfoImpl.SPRInfoBuilder().withStatus("UNAVAILABLE")
				.withCustomerType(CustomerType.PREPAID.val)
				.withCui("101")
				.withSubscriberIdentity("101")
				.withGroupIds(Collections.emptyList()).build();

		ReflectionAssert.assertReflectionEquals(expected, actual);

	}

	@SuppressWarnings("unused")
	private Object[][] dataProviderFor_getSPRInfo_should_throw_OperationFailedException_when_any_exception_thrown() {
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
	 * when SQLException is thrown, it will be handled in method.
	 * 
	 * Empty field map will be returned. See console for Trace log
	 */
	@Test
	@Parameters(method = "dataProviderFor_getSPRInfo_should_throw_OperationFailedException_when_any_exception_thrown")
	public void test_getSPRInfo_should_throw_OperationFailedException_when_any_exception_thrown(
			String whenToThrow,
			Class<? extends Throwable> exceptionToBeThrown) throws Exception {

		setupMockToGenerateSQLException(whenToThrow, exceptionToBeThrown);

		expectedException.expect(OperationFailedException.class);

        SubscriberRepository operation = createSubscriberRepository(alertListener);

        operation.getProfile("101");
	}

	/* transaction is mocked just to throw Exception when begin() is called */
	public void setupMockToGenerateSQLException(String whenToThrow, Class<? extends Throwable> exceptionToBeThrown) throws Exception {
		Transaction transaction = mock(Transaction.class);
		PreparedStatement preparedStatement = mock(PreparedStatement.class);
		ResultSet resultSet = mock(ResultSet.class);


		doReturn(transaction).when(transactionFactory).createTransaction();
		doReturn(transaction).when(transactionFactory).createReadOnlyTransaction();
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

	@Test
	public void test_getSPRInfo_should_generate_DB_NO_CONNECTION_alert_when_connection_not_found() throws Exception {

		AlertListener alertListener = mock(AlertListener.class);
		setUpMockTogenerateDB_NO_CONNECTION_alert();
        SubscriberRepository operation = createSubscriberRepository(alertListener);

		operation.getProfile("101");
		verify(alertListener, only())
				.generateSystemAlert(Mockito.anyString(), Mockito.eq(Alerts.DATABASE_CONNECTION_NOT_AVAILABLE), Mockito.anyString(), Mockito.anyString());

	}

	private void setUpMockTogenerateDB_NO_CONNECTION_alert() throws Exception {
		Transaction transaction = mock(Transaction.class);
		doReturn(transaction).when(transactionFactory).createTransaction();
		doReturn(transaction).when(transactionFactory).createReadOnlyTransaction();
		doThrow(new TransactionException("Connection not found", TransactionErrorCode.CONNECTION_NOT_FOUND)).when(transaction).begin();
	}

	@Test
	public void test_getSPRInfo_should_generate_DB_HIGH_QUERY_RESPONSE_TIME_alert_when_execution_time_is_high() throws Exception {
		helper.insertProfile(getProfiles());
		setUpMockToGenerateHIGHRESPONSEAlert();
		AlertListener alertListener = mock(AlertListener.class);

        SubscriberRepository operation = createSubscriberRepository(alertListener);
        operation.getProfile("101");

		verify(alertListener, only()).generateSystemAlert(Mockito.anyString(), Mockito.eq(Alerts.HIGH_QUERY_RESPONSE_TIME),
				Mockito.anyString(), Mockito.anyString());
	}

	@Test
	public void test_getSPRInfo_should_generate_QUERYTIMEOUT_alert_when() throws Exception {

		setUpMockToGenerateQUERYTIMEOUTalert();
		AlertListener alertListener = mock(AlertListener.class);

        SubscriberRepository operation = createSubscriberRepository(alertListener);

        try {
			operation.getProfile("101");
		} catch (OperationFailedException e) {
			verify(alertListener, only())
					.generateSystemAlert(Mockito.anyString(), Mockito.eq(Alerts.QUERY_TIME_OUT), Mockito.anyString(), Mockito.anyString());
			throw e;
		}
	}

	private void setUpMockToGenerateQUERYTIMEOUTalert() throws Exception {
		Transaction transaction = mock(Transaction.class);
		PreparedStatement preparedStatement = mock(PreparedStatement.class);

		doReturn(transaction).when(transactionFactory).createTransaction();
		doReturn(transaction).when(transactionFactory).createReadOnlyTransaction();
		doReturn(preparedStatement).when(transaction).prepareStatement(Mockito.anyString());

		when(preparedStatement.executeQuery()).thenThrow(new SQLException("query timeout", "timeout", 1013));

	}

	private void setUpMockToGenerateHIGHRESPONSEAlert() throws Exception {
		Transaction transaction = mock(Transaction.class);
		PreparedStatement preparedStatement = mock(PreparedStatement.class);

		doReturn(transaction).when(transactionFactory).createTransaction();
		doReturn(transaction).when(transactionFactory).createReadOnlyTransaction();
		doReturn(preparedStatement).when(transaction).prepareStatement(Mockito.anyString());


		when(preparedStatement.executeQuery()).thenAnswer((Answer<ResultSet>) invocation -> {

			long startTime = System.currentTimeMillis();
			long elapsedTime = 0;
			while (elapsedTime < AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS + 10) {
				elapsedTime = System.currentTimeMillis() - startTime;
			}
			return mock(ResultSet.class);
		});

	}

	@After
	public void afterDropTables() throws Exception {
		doCallRealMethod().when(transactionFactory).createTransaction();
		helper.dropProfileTables();
		helper.dropUsageTable();
		DBUtility.closeQuietly(transactionFactory.getConnection());
        DerbyUtil.closeDerby("TestingDB");
	}
}
