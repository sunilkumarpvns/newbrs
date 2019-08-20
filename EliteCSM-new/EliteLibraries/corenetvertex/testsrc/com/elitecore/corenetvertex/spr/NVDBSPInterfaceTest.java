package com.elitecore.corenetvertex.spr;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.PasswordEncryptionType;
import com.elitecore.corenetvertex.constants.SubscriberStatus;
import com.elitecore.corenetvertex.core.alerts.AlertConstants;
import com.elitecore.corenetvertex.core.alerts.AlertListener;
import com.elitecore.corenetvertex.core.alerts.Alerts;
import com.elitecore.corenetvertex.core.db.exception.DBDownException;
import com.elitecore.corenetvertex.core.transaction.DummyTransactionFactory;
import com.elitecore.corenetvertex.core.transaction.Transaction;
import com.elitecore.corenetvertex.core.transaction.TransactionException;
import com.elitecore.corenetvertex.core.transaction.TransactionFactory;
import com.elitecore.corenetvertex.core.transaction.exception.TransactionErrorCode;
import com.elitecore.corenetvertex.spr.data.SPRFields;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.corenetvertex.spr.data.SPRInfoImpl;
import com.elitecore.corenetvertex.spr.data.SubscriberProfileData;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.util.DerbyUtil;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.*;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.stubbing.Answer;
import org.unitils.reflectionassert.ReflectionComparatorMode;

import java.sql.*;
import java.util.*;
import java.util.Date;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static org.junit.Assert.*;
import static org.mockito.Matchers.anyShort;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static org.unitils.reflectionassert.ReflectionAssert.assertLenientEquals;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;


@RunWith(JUnitParamsRunner.class)
public class NVDBSPInterfaceTest {

	private static final String DS_NAME = "test-DB";
	private static final String PREPARESTATEMENT = "preparedStatement";
	private static final String BEGIN = "begin";
	private static final String EXECUTE = "execute";
	private static final String GET = "get";
	private DummyTransactionFactory transactionFactory;
	private SubscriberProfileTestHelper helper;

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
		transactionFactory = (DummyTransactionFactory) new DummyTransactionFactoryBuilder().withDBDataSource(dbDataSource, 1).build();

		helper = new SubscriberProfileTestHelper(transactionFactory);
		createTablesAndInsertProfileRecords();
	}

	/**
	 * Creates SPR with 3 users
	 * 
	 */
	private void createTablesAndInsertProfileRecords() throws Exception {
		LogManager.getLogger().debug("test", "creating SPR");

		helper.createTables();
		helper.insertProfiles(generateExpectedSPRFields());

		getLogger().debug(this.getClass().getSimpleName(), "SPR created");
	}

	private List<SubscriberProfileData> generateExpectedSPRFields() {

		SubscriberProfileData data1 = new SubscriberProfileData.SubscriberProfileDataBuilder()
				.withSubscriberIdentity("101")
				.withImsi("1234")
				.withMsisdn("9797979797")
				.withUserName("user1")
				.withPassword("user1")
				.withPhone("123456")
				// .withBirthdate(new java.sql.Timestamp(new Date().getTime()))
				.build();

		SubscriberProfileData data2 = new SubscriberProfileData.SubscriberProfileDataBuilder()
				.withSubscriberIdentity("102")
				.withImsi("12345")
				.withMsisdn("9797979798")
				.withUserName("user2")
				.withPassword("user2")
				.withPhone("123456").build();

		SubscriberProfileData data3 = new SubscriberProfileData.SubscriberProfileDataBuilder()
				.withSubscriberIdentity("103")
				.withImsi("123456")
				.withMsisdn("9797979799")
				.withUserName("user3")
				.withPassword("user3")
				.withPhone("123456").build();

		return Arrays.asList(data1, data2, data3);
	}

	/**
	 * it drops all tables,
	 */
	private void dropTables() throws Exception {

		helper.dropTable();
		getLogger().debug("test", "Tables Dropped");
	}

	@After
	public void afterDropTables() throws Exception {
		dropTables();
		transactionFactory.getConnection().close();
		DerbyUtil.closeDerby("TestingDB");
	}

	/**
	 * identity field default Subscriber Identity
	 */
	@Test
	@Parameters(value = {"101", "102", "103"})
	public void test_getProfile_should_return_SPRFieldsMap(String subscriberIdentity) throws Exception {

		NVDBSPInterface spInterface = getNewProfileOperation();
		SPRInfo actualProfile = spInterface.getProfile(subscriberIdentity);
		SPRInfo expectedProfile = helper.getExpectedProfileForSubscriber(subscriberIdentity);

		expectedProfile.setSprLoadTime(actualProfile.getSprLoadTime());
		expectedProfile.setSprReadTime(actualProfile.getSprReadTime());
		assertLenientEquals(expectedProfile, actualProfile);
	}

	@Test
	@Parameters(value = {"97979797996", "11", ""})
	public void test_getProfile_not_return_profile_for_Unknown_SubscriberIdentity(String subscriberIdentity) throws Exception {

		NVDBSPInterface spInterface = getNewProfileOperation();
		SPRInfo actualProfile = spInterface.getProfile(subscriberIdentity);

		assertNull(actualProfile);
	}

	private NVDBSPInterface getNewProfileOperation() {
		return new NVDBSPInterface(mock(AlertListener.class), transactionFactory);
	}

	@Test
	@Parameters(value = { "1542", "44211", "4554", "abc", "xyz", "ghi" })
	public void test_getProfile_should_return_null_when_subscriber_not_exist_in_SPR(String subscriberIdentity) throws Exception {
		NVDBSPInterface subscriberProfileOperation = getNewProfileOperation();

		assertLenientEquals(null, subscriberProfileOperation.getProfile(subscriberIdentity));

	}

	/* transaction is mocked just to throw Exception when begin() is called */
	public void setupMockToGenerateSQLException(String whenToThrow, Class<? extends Throwable> exceptionToBeThrown) throws Exception {
		Transaction transaction = mock(Transaction.class);
		PreparedStatement preparedStatement = mock(PreparedStatement.class);
		ResultSet resultSet = mock(ResultSet.class);

		transactionFactory = spy(transactionFactory);
		doReturn(transaction).when(transactionFactory).createTransaction();
		doReturn(transaction).when(transactionFactory).createReadOnlyTransaction();
		doReturn(preparedStatement).when(transaction).prepareStatement(Mockito.anyString());
		doReturn(resultSet).when(preparedStatement).executeQuery();

		if (PREPARESTATEMENT.equals(whenToThrow)) {
			doThrow(exceptionToBeThrown).when(transaction).prepareStatement(anyString());
		} else if (BEGIN.equals(whenToThrow)) {
			doThrow(exceptionToBeThrown).when(transaction).begin();
		} else if (EXECUTE.equals(whenToThrow)) {
			doThrow(exceptionToBeThrown).when(preparedStatement).executeQuery();
		} else if (GET.equals(whenToThrow)) {
			doThrow(exceptionToBeThrown).when(resultSet).next();
		}

	}

	@SuppressWarnings("unused")
	private Object[][] dataProviderFor_getProfile_should_throw_OperationFailedException_when_any_exception_thrown() {
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
	@Parameters(method = "dataProviderFor_getProfile_should_throw_OperationFailedException_when_any_exception_thrown")
	public void test_getProfile_should_throw_OperationFailedException_when_any_exception_thrown(
			String whenToThrow,
			Class<? extends Throwable> exceptionToBeThrown) throws Exception {

		setupMockToGenerateSQLException(whenToThrow, exceptionToBeThrown);

		expectedException.expect(OperationFailedException.class);

		NVDBSPInterface spInterface = getNewProfileOperation();

		spInterface.getProfile("9797979797");
	}

	private void setUpMockTransactionFactoryDead() {
		transactionFactory = spy(transactionFactory);
		when(transactionFactory.isAlive()).thenReturn(false);
	}

	@Test
	public void test_getProfile_should_throw_DBDownException_when_DB_is_down() throws Exception {

		setUpMockTransactionFactoryDead();

		NVDBSPInterface spInterface = getNewProfileOperation();

		expectedException.expect(DBDownException.class);

		spInterface.getProfile("101");

	}

	/**
	 * After all operation, transaction should end properly
	 * 
	 * Actually its closing connection, so verified connection.close() call
	 * 
	 */
	@Test
	public void test_getProfile_should_always_end_transaction() throws Exception {

		NVDBSPInterface subscriberProfileOperation = getNewProfileOperation();

		subscriberProfileOperation.getProfile("101");

		Connection connection = getConnection();

		assertTrue(connection.isClosed());
	}

	@Test
	public void test_getProfile_should_generate_DB_NO_CONNECTION_alert_when_connection_not_found() throws Exception {
		AlertListener alertListener = mock(AlertListener.class);
		TransactionFactory transactionFactory = setUpMockTogenerateDB_NO_CONNECTION_alert();
		NVDBSPInterface operation = new NVDBSPInterface(alertListener, transactionFactory);

		expectedException.expect(DBDownException.class);
		
		try {
			operation.getProfile("101");
		} catch (OperationFailedException e) {
			verify(alertListener, only())
					.generateSystemAlert(Mockito.anyString(), Mockito.eq(Alerts.DATABASE_CONNECTION_NOT_AVAILABLE), Mockito.anyString(), Mockito.anyString());
			throw e;
		}
		fail("getProfile should throw Exception");
	}

	private TransactionFactory setUpMockTogenerateDB_NO_CONNECTION_alert() throws TransactionException {
		TransactionFactory transactionFactory = spy(this.transactionFactory);
		Transaction transaction = mock(Transaction.class);
		doReturn(transaction).when(transactionFactory).createTransaction();
		doReturn(transaction).when(transactionFactory).createReadOnlyTransaction();
		doThrow(new TransactionException("Connection not found", TransactionErrorCode.CONNECTION_NOT_FOUND)).when(transaction).begin();
		return transactionFactory;
	}

	@Test
	public void test_getProfile_should_generate_DB_HIGH_QUERY_RESPONSE_TIME_alert_when_execution_time_is_high() throws Exception {
		TransactionFactory transactionFactory = setUpMockToGenerateHIGHRESPONSEAlert();
		AlertListener alertListener = mock(AlertListener.class);
		NVDBSPInterface operation = new NVDBSPInterface(alertListener, transactionFactory);
		operation.getProfile("101");


		verify(alertListener, only())
				.generateSystemAlert(Mockito.anyString(), Mockito.eq(Alerts.HIGH_QUERY_RESPONSE_TIME), Mockito.anyString(), Mockito.anyString());
	}

	@Test
	public void test_getProfile_should_generate_QUERYTIMEOUT_alert_when() throws Exception {
		AlertListener alertListener = mock(AlertListener.class);
		TransactionFactory transactionFactory = setUpMockToGenerateQUERYTIMEOUTalert();
		NVDBSPInterface operation = new NVDBSPInterface(alertListener, transactionFactory);

		expectedException.expect(DBDownException.class);
		try {
			operation.getProfile("101");
		} catch (DBDownException e) {
			verify(alertListener, only())
					.generateSystemAlert(anyString(), eq(Alerts.QUERY_TIME_OUT), anyString(), anyString());
			throw e;
		}
		fail("getProfile should throw Exception");
	}

	private TransactionFactory setUpMockToGenerateQUERYTIMEOUTalert() throws Exception {
		TransactionFactory transactionFactory = spy(this.transactionFactory);
		Transaction transaction = mock(Transaction.class);
		PreparedStatement preparedStatement = mock(PreparedStatement.class);

		doReturn(transaction).when(transactionFactory).createTransaction();
		doReturn(transaction).when(transactionFactory).createReadOnlyTransaction();
		doReturn(preparedStatement).when(transaction).prepareStatement(Mockito.anyString());

		when(preparedStatement.executeQuery()).thenThrow(new SQLException("query timeout", "timeout", CommonConstants.QUERY_TIMEOUT_ERRORCODE));
		return transactionFactory;
	}

	private TransactionFactory setUpMockToGenerateQUERYTIMEOUTalertForAddProfile() throws Exception {
		TransactionFactory transactionFactory = spy(this.transactionFactory);
		Transaction transaction = mock(Transaction.class);
		PreparedStatement preparedStatement = mock(PreparedStatement.class);
		doReturn(transaction).when(transactionFactory).createTransaction();
		doReturn(preparedStatement).when(transaction).prepareStatement(anyString());
		doNothing().when(preparedStatement).setString(anyShort(), anyString());

		when(preparedStatement.execute()).thenThrow(new SQLException("query timeout", "timeout", 1013));
		return transactionFactory;
	}

	private TransactionFactory setUpMockToGenerateHIGHRESPONSEAlert() throws Exception {
		TransactionFactory transactionFactory = spy(this.transactionFactory);
		Transaction transaction = mock(Transaction.class);
		PreparedStatement preparedStatement = mock(PreparedStatement.class);

		doReturn(transaction).when(transactionFactory).createTransaction();
		doReturn(transaction).when(transactionFactory).createReadOnlyTransaction();
		doReturn(preparedStatement).when(transaction).prepareStatement(Mockito.anyString());

		when(preparedStatement.executeQuery()).thenAnswer(new Answer<ResultSet>() {

			public ResultSet answer(org.mockito.invocation.InvocationOnMock invocation) throws Throwable {

				long startTime = System.currentTimeMillis();
				long elapsedTime = 0;
				while (elapsedTime < AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS + 10) {
					elapsedTime = System.currentTimeMillis() - startTime;
				}
				return mock(ResultSet.class);
			};

		});
		return transactionFactory;
	}

	private TransactionFactory setUpMockToGenerateHIGHRESPONSEAlertForStatement() throws Exception {
		TransactionFactory transactionFactory = spy(this.transactionFactory);
		Transaction transaction = mock(Transaction.class);
		PreparedStatement preparedStatement = mock(PreparedStatement.class);
		doReturn(transaction).when(transactionFactory).createTransaction();
		doReturn(preparedStatement).when(transaction).prepareStatement(anyString());
		doNothing().when(preparedStatement).setString(anyShort(), anyString());

		when(preparedStatement.execute()).thenAnswer(new Answer<Boolean>() {

			public Boolean answer(org.mockito.invocation.InvocationOnMock invocation) throws Throwable {

				long startTime = System.currentTimeMillis();
				long elapsedTime = 0;
				while (elapsedTime < AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS + 10) {
					elapsedTime = System.currentTimeMillis() - startTime;
				}
				return true;
			};

		});
		return transactionFactory;
	}

	private Connection getConnection() {
		Connection connection = ((DummyTransactionFactory) transactionFactory).getConnection();
		return connection;
	}

	private static class SubscriberProfileTestHelper {

		private TransactionFactory transactionFactory;
		private Map<String, SPRInfo> sprFieldsById;

		public SubscriberProfileTestHelper(TransactionFactory factory) {
			transactionFactory = factory;
			sprFieldsById = new HashMap<String, SPRInfo>();
		}

		public void dropTable() throws Exception {
			executeQuery(SubscriberProfileData.dropTableQuery());
		}

		public void insertProfiles(List<SubscriberProfileData> subscriberProfileDatas) throws Exception {
			for (SubscriberProfileData data : subscriberProfileDatas) {
				insertProfile(data);
			}
		}
		
		public void insertProfile(SubscriberProfileData data) throws Exception {
			executeQuery(data.insertQuery());
			sprFieldsById.put(data.getSubscriberIdentity(), data.getSPRInfo());
		}

		public void createTables() throws Exception {
			executeQuery(SubscriberProfileData.createTableQuery());
		}

		private void executeQuery(String query) throws Exception {
			Transaction transaction = transactionFactory.createTransaction();

			try {
				transaction.begin();
				getLogger().debug(getClass().getSimpleName(), "Query: " + query);
				transaction.prepareStatement(query).execute();
			} finally {
				transaction.end();
			}
		}

		public SPRInfo getExpectedProfileForSubscriber(String subscriberIdentity) {
			return sprFieldsById.get(subscriberIdentity) != null ? sprFieldsById.get(subscriberIdentity) : null;
		}
	}



	/**
	 * when SQLException is thrown, it will be handled in method.
	 * 
	 * Empty field map will be returned. See console for Trace log
	 */
	@Test
	@Parameters(method = "dataProviderFor_addProfile_should_throw_OperationFailedException_when_any_exception_thrown")
	public void test_addProfile_should_throw_OperationFailedException_when_any_exception_thrown(
			String whenToThrow,
			Class<? extends Throwable> exceptionToBeThrown) throws Exception {

		setupMockToGenerateSQLException_addProfile(whenToThrow, exceptionToBeThrown);

		expectedException.expect(OperationFailedException.class);

		String subscriberID = "101";

		SPRInfo profile = helper.getExpectedProfileForSubscriber(subscriberID);

		NVDBSPInterface spInterface = new NVDBSPInterface(mock(AlertListener.class), transactionFactory);

		spInterface.addProfile(profile);
	}

	private void setupMockToGenerateSQLException_addProfile(String whenToThrow, Class<? extends Throwable> exceptionToBeThrown) throws Exception {
		Transaction transaction = mock(Transaction.class);
		PreparedStatement preparedStatement = mock(PreparedStatement.class);
		ResultSet resultSet = mock(ResultSet.class);

		transactionFactory = spy(transactionFactory);
		doReturn(transaction).when(transactionFactory).createTransaction();
		doReturn(preparedStatement).when(transaction).prepareStatement(Mockito.anyString());
		doReturn(resultSet).when(preparedStatement).executeQuery();

		if (PREPARESTATEMENT.equals(whenToThrow)) {
			doThrow(exceptionToBeThrown).when(transaction).prepareStatement(anyString());
		} else if (BEGIN.equals(whenToThrow)) {
			doThrow(exceptionToBeThrown).when(transaction).begin();
		} else if (EXECUTE.equals(whenToThrow)) {
			doThrow(exceptionToBeThrown).when(preparedStatement).execute();
		}
	}

	@Test
	public void test_addProfile_should_throw_OperationFailedException_when_DB_is_down() throws Exception {

		setUpMockTransactionFactoryDead();

		expectedException.expect(OperationFailedException.class);

		String subscriberID = "101";

		SPRInfo profile = helper.getExpectedProfileForSubscriber(subscriberID);

		NVDBSPInterface spInterface = new NVDBSPInterface(mock(AlertListener.class), transactionFactory);

		spInterface.addProfile(profile);

	}

	/**
	 * After all operation, transaction should end properly
	 * 
	 * Actually its closing connection, so verified connection.close() call
	 * 
	 */
	@Test
	public void test_addProfile_should_always_end_transaction() throws Exception {

		String subscriberID = "101";

		SPRInfo profile = helper.getExpectedProfileForSubscriber(subscriberID);

		NVDBSPInterface spInterface = new NVDBSPInterface(mock(AlertListener.class), transactionFactory);

		spInterface.addProfile(profile);

		Connection connection = getConnection();

		assertTrue(connection.isClosed());
	}

	@Test
	public void test_addProfile_should_throw_OperationFailedException_if_subscriber_identity_not_found_in_profile() throws Exception {

		transactionFactory = spy(transactionFactory);

		NVDBSPInterface spInterface = new NVDBSPInterface(mock(AlertListener.class), transactionFactory);

		expectedException.expect(OperationFailedException.class);

		spInterface.addProfile(new SPRInfoImpl());
	}

	@Test
	public void test_addProfile_should_generate_DB_NO_CONNECTION_alert_when_connection_not_found() throws Exception {
		AlertListener alertListener = mock(AlertListener.class);
		TransactionFactory transactionFactory = setUpMockTogenerateDB_NO_CONNECTION_alert();
		NVDBSPInterface operation = new NVDBSPInterface(alertListener, transactionFactory);

		String subscriberID = "101";

		SPRInfo profile = helper.getExpectedProfileForSubscriber(subscriberID);

		expectedException.expect(OperationFailedException.class);
		try {
			operation.addProfile(profile);
		} catch (OperationFailedException e) {
			verify(alertListener, only())
					.generateSystemAlert(Mockito.anyString(), Mockito.eq(Alerts.DATABASE_CONNECTION_NOT_AVAILABLE), Mockito.anyString(), Mockito.anyString());
			throw e;
		}
		fail("getProfile should throw Exception");
	}

	@Test
	public void test_addProfile_should_generate_DB_HIGH_QUERY_RESPONSE_TIME_alert_when_execution_time_is_high() throws Exception {
		TransactionFactory transactionFactory = setUpMockToGenerateHIGHRESPONSEAlertForStatement();
		AlertListener alertListener = mock(AlertListener.class);
		NVDBSPInterface operation = new NVDBSPInterface(alertListener, transactionFactory);

		String subscriberID = "101";

		SPRInfo profile = helper.getExpectedProfileForSubscriber(subscriberID);

		operation.addProfile(profile);

		verify(alertListener, only())
				.generateSystemAlert(Mockito.anyString(), Mockito.eq(Alerts.HIGH_QUERY_RESPONSE_TIME), Mockito.anyString(), Mockito.anyString());
	}

	@Test
	public void test_addProfile_should_generate_QUERYTIMEOUT_alert_when() throws Exception {
		AlertListener alertListener = mock(AlertListener.class);
		TransactionFactory transactionFactory = setUpMockToGenerateQUERYTIMEOUTalertForAddProfile();
		NVDBSPInterface operation = new NVDBSPInterface(alertListener, transactionFactory);

		String subscriberID = "101";

		SPRInfo profile = helper.getExpectedProfileForSubscriber(subscriberID);
		
		expectedException.expect(OperationFailedException.class);

		try {
			operation.addProfile(profile);
		} catch (OperationFailedException e) {
			verify(alertListener, only())
					.generateSystemAlert(Mockito.anyString(), Mockito.eq(Alerts.QUERY_TIME_OUT), Mockito.anyString(), Mockito.anyString());
			throw e;
		}
		fail("addProfile should throw Exception");
	}

	@SuppressWarnings("unused")
	private Object[][] dataProviderFor_addProfile_should_throw_OperationFailedException_when_any_exception_thrown() {
		return new Object[][] {
				{
						PREPARESTATEMENT, TransactionException.class
		},
				{
						BEGIN, TransactionException.class
		},
				{
						EXECUTE, SQLException.class
		}
		};
	}

	@Test
	public void test_updateProfile_should_generate_DB_NO_CONNECTION_alert_when_connection_not_found() throws Exception {
		AlertListener alertListener = mock(AlertListener.class);
		TransactionFactory transactionFactory = setUpMockTogenerateDB_NO_CONNECTION_alert_update();
		NVDBSPInterface spInterface = new NVDBSPInterface(alertListener, transactionFactory);

		String subscriberID = "101";

		SPRInfo profile = helper.getExpectedProfileForSubscriber(subscriberID);
		
		expectedException.expect(OperationFailedException.class);
		try {
			spInterface.updateProfile(subscriberID, createSPRFieldMap(profile));
		} catch (OperationFailedException e) {
			verify(alertListener, only())
					.generateSystemAlert(Mockito.anyString(), Mockito.eq(Alerts.DATABASE_CONNECTION_NOT_AVAILABLE), Mockito.anyString(), Mockito.anyString());
			throw e;
		}
		fail("updateProfile should throw Exception");
	}
	
	private EnumMap<SPRFields, String> createSPRFieldMap(SPRInfo sprInfo) {

		EnumMap<SPRFields, String> sprFieldMap = new EnumMap<SPRFields, String>(SPRFields.class);
		for (SPRFields sprField : SPRFields.values()) {
			/*
			 * Timestamp value is converted to millisecond
			 */
			if (Types.TIMESTAMP == sprField.type || Types.NUMERIC == sprField.type) {
				sprFieldMap.put(sprField, sprField.getNumericValue(sprInfo) == null ? null : sprField.getNumericValue(sprInfo)+"");
			} else if (Types.VARCHAR == sprField.type) {
				sprFieldMap.put(sprField, sprField.getStringValue(sprInfo));
			}
		}
		
		return sprFieldMap;
	}

	private TransactionFactory setUpMockTogenerateDB_NO_CONNECTION_alert_update() throws Exception {
		TransactionFactory transactionFactory = spy(this.transactionFactory);
		Transaction transaction = mock(Transaction.class);
		doReturn(transaction).when(transactionFactory).createTransaction();
		doThrow(new TransactionException("Connection not found", TransactionErrorCode.CONNECTION_NOT_FOUND)).when(transaction).begin();
		return transactionFactory;
	}

	@Test
	public void test_updateProfile_should_generate_DB_HIGH_QUERY_RESPONSE_TIME_alert_when_execution_time_is_high() throws Exception {
		TransactionFactory transactionFactory = setUpMockToGenerateHIGHRESPONSEAlertForUpdate();
		AlertListener alertListener = mock(AlertListener.class);
		NVDBSPInterface spInterface = new NVDBSPInterface(alertListener, transactionFactory);

		String subscriberID = "101";

		SPRInfo profile = helper.getExpectedProfileForSubscriber(subscriberID);

		spInterface.updateProfile(subscriberID, createSPRFieldMap(profile));

		verify(alertListener, only())
				.generateSystemAlert(Mockito.anyString(), Mockito.eq(Alerts.HIGH_QUERY_RESPONSE_TIME), Mockito.anyString(), Mockito.anyString());
	}

	private TransactionFactory setUpMockToGenerateHIGHRESPONSEAlertForUpdate() throws Exception {
		TransactionFactory transactionFactory = spy(this.transactionFactory);
		Transaction transaction = mock(Transaction.class);
		PreparedStatement preparedStatement = mock(PreparedStatement.class);
		doReturn(transaction).when(transactionFactory).createTransaction();
		doReturn(preparedStatement).when(transaction).prepareStatement(anyString());
		doNothing().when(preparedStatement).setString(anyShort(), anyString());

		when(preparedStatement.executeUpdate()).thenAnswer(new Answer<Integer>() {

			public Integer answer(org.mockito.invocation.InvocationOnMock invocation) throws Throwable {

				long startTime = System.currentTimeMillis();
				long elapsedTime = 0;
				while (elapsedTime < AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS + 10) {
					elapsedTime = System.currentTimeMillis() - startTime;
				}
				return 0;
			};

		});
		return transactionFactory;
	}

	@Test
	public void test_updateProfile_should_generate_QUERYTIMEOUT_alert_when() throws Exception {
		AlertListener alertListener = mock(AlertListener.class);
		TransactionFactory transactionFactory = setUpMockToGenerateQUERYTIMEOUTalertForUpdate();
		NVDBSPInterface spInterface = new NVDBSPInterface(alertListener, transactionFactory);

		String subscriberID = "101";

		SPRInfo profile = helper.getExpectedProfileForSubscriber(subscriberID);
		expectedException.expect(OperationFailedException.class);

		try {
			spInterface.updateProfile(subscriberID, createSPRFieldMap(profile));
		} catch (OperationFailedException e) {
			verify(alertListener, only())
					.generateSystemAlert(Mockito.anyString(), Mockito.eq(Alerts.QUERY_TIME_OUT), Mockito.anyString(), Mockito.anyString());
			throw e;
		}
		fail("updateProfile should throw Exception");
	}

	private TransactionFactory setUpMockToGenerateQUERYTIMEOUTalertForUpdate() throws Exception {
		TransactionFactory transactionFactory = spy(this.transactionFactory);
		Transaction transaction = mock(Transaction.class);
		PreparedStatement preparedStatement = mock(PreparedStatement.class);
		doReturn(transaction).when(transactionFactory).createTransaction();
		doReturn(preparedStatement).when(transaction).prepareStatement(anyString());
		doNothing().when(preparedStatement).setString(anyShort(), anyString());

		when(preparedStatement.executeUpdate()).thenThrow(new SQLException("query timeout", "timeout", 1013));
		return transactionFactory;
	}
	
	@Test
	public void test_updateProfile() throws Exception {
		NVDBSPInterface spInterface = new NVDBSPInterface(mock(AlertListener.class), transactionFactory);

		SubscriberProfileData data1 = getRecordToTestUpdate();

		helper.insertProfile(data1);
		
		SPRInfo profileTobeUpdated = getUpdatedProfile(data1);
		
		spInterface.updateProfile("111", createSPRFieldMap(profileTobeUpdated));
		
		SPRInfo actualProfile = spInterface.getProfile(data1.getSubscriberIdentity());

		profileTobeUpdated.setSprLoadTime(actualProfile.getSprLoadTime());
		profileTobeUpdated.setSprReadTime(actualProfile.getSprReadTime());
		assertReflectionEquals(profileTobeUpdated, actualProfile, ReflectionComparatorMode.LENIENT_ORDER);
	}
	
	private SPRInfo getUpdatedProfile(SubscriberProfileData data1) {
		SPRInfo updatedProfile = new SPRInfoImpl.SPRInfoBuilder()
					.withSubscriberIdentity("111")
					.withImsi("45678")
					.withMsisdn(data1.getMsisdn())
					.withUserName("user111")
					.withPassword("456789")
				.withEncryptionType("0")
					.withPhone("9898989898").build();
		return updatedProfile;
	}

	private SubscriberProfileData getRecordToTestUpdate() {
		SubscriberProfileData data1 = new SubscriberProfileData.SubscriberProfileDataBuilder()
            		.withSubscriberIdentity("111")
            		.withImsi("1234")
            		.withMsisdn("9797979797")
            		.withUserName("user1")
            		.withPassword("user1")
            		.withPhone("123456")
				.withEncryptionType("0")
            		.withBirthdate(new Timestamp(new Date().getTime()))
            		.build();
		return data1;
	}
	
	@Test
	public void test_updateProfile_should_not_change_value_of_identity_attribute() throws Exception {
		NVDBSPInterface spInterface = new NVDBSPInterface(mock(AlertListener.class), transactionFactory);

		SubscriberProfileData data1 = getRecordToTestUpdate();

		helper.insertProfile(data1);
		
		SPRInfo profileToBeUpdated  = getProfileAfterChangingIdentityAttribute(data1);
		
		spInterface.updateProfile("111", createSPRFieldMap(profileToBeUpdated));
		
		SPRInfo actualProfile = spInterface.getProfile(data1.getSubscriberIdentity());
		
		assertEquals(data1.getSubscriberIdentity(), actualProfile.getSubscriberIdentity());
	}

	private SPRInfo getProfileAfterChangingIdentityAttribute(SubscriberProfileData data1) {
		SPRInfo profile = new SPRInfoImpl.SPRInfoBuilder()
			.withSubscriberIdentity(data1.getSubscriberIdentity())
			.withImsi(data1.getImsi())
			.withMsisdn("123")
			.withUserName(data1.getUserName())
			.withPassword(data1.getPassword())
			.withPhone(data1.getPhone())
			.withBirthdate(data1.getBirthdate()).build();
		return profile;
	}
	
	@Test
	public void test_updateProfile_should_throw_OperationFailedException_when_DB_is_down() throws Exception {

		setUpMockTransactionFactoryDead();

		NVDBSPInterface spInterface = new NVDBSPInterface(mock(AlertListener.class), transactionFactory);

		SubscriberProfileData data1 = getRecordToTestUpdate();

		helper.insertProfile(data1);
		
		SPRInfo profileTobeUpdated = getUpdatedProfile(data1);

		expectedException.expect(OperationFailedException.class);
		spInterface.updateProfile("111", createSPRFieldMap(profileTobeUpdated));
	}
	
	@Test
	@Parameters(method = "dataProviderFor_updateProfile_should_throw_OperationFailedException_when_any_exception_thrown")
	public void test_updateProfile_should_throw_OperationFailedException_when_any_exception_thrown(
			String whenToThrow,
			Class<? extends Throwable> exceptionToBeThrown) throws Exception {

		setupMockToGenerateSQLException_updateProfile(whenToThrow, exceptionToBeThrown);
		
		NVDBSPInterface spInterface = new NVDBSPInterface(mock(AlertListener.class), transactionFactory);

		expectedException.expect(OperationFailedException.class);

		SubscriberProfileData data1 = getRecordToTestUpdate();

		helper.insertProfile(data1);
		
		SPRInfo profileTobeUpdated = getUpdatedProfile(data1);

		spInterface.updateProfile("111", createSPRFieldMap(profileTobeUpdated));
	}
	
	@SuppressWarnings("unused")
	private Object[][] dataProviderFor_updateProfile_should_throw_OperationFailedException_when_any_exception_thrown() {
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
		
		};
	}
	
	private void setupMockToGenerateSQLException_updateProfile(String whenToThrow, Class<? extends Throwable> exceptionToBeThrown) throws Exception {
		Transaction transaction = mock(Transaction.class);
		PreparedStatement preparedStatement = mock(PreparedStatement.class);

		transactionFactory = spy(transactionFactory);
		doReturn(transaction).when(transactionFactory).createTransaction();
		doReturn(preparedStatement).when(transaction).prepareStatement(Mockito.anyString());
		doReturn(1).when(preparedStatement).executeUpdate();

		if (PREPARESTATEMENT.equals(whenToThrow)) {
			doThrow(exceptionToBeThrown).when(transaction).prepareStatement(anyString());
		} else if (BEGIN.equals(whenToThrow)) {
			doThrow(exceptionToBeThrown).when(transaction).begin();
		} else if (EXECUTE.equals(whenToThrow)) {
			doThrow(exceptionToBeThrown).when(preparedStatement).executeUpdate();
		}
	}
	
	@Test
	public void test_deleteProfile_should_generate_DB_NO_CONNECTION_alert_when_connection_not_found() throws Exception {
		AlertListener alertListener = mock(AlertListener.class);
		TransactionFactory transactionFactory = setUpMockTogenerateDB_NO_CONNECTION_alert_delete();
		NVDBSPInterface spInterface = new NVDBSPInterface(alertListener, transactionFactory);

		String subscriberID = "101";

		expectedException.expect(OperationFailedException.class);
		try {
			spInterface.purgeProfile(subscriberID);
		} catch (OperationFailedException e) {
			verify(alertListener, only())
					.generateSystemAlert(Mockito.anyString(), Mockito.eq(Alerts.DATABASE_CONNECTION_NOT_AVAILABLE), Mockito.anyString(), Mockito.anyString());
			throw e;
		}
		fail("deleteProfile should throw Exception");
	}
	
	private TransactionFactory setUpMockTogenerateDB_NO_CONNECTION_alert_delete() throws Exception {
		TransactionFactory transactionFactory = spy(this.transactionFactory);
		Transaction transaction = mock(Transaction.class);
		doReturn(transaction).when(transactionFactory).createTransaction();
		doThrow(new TransactionException("Connection not found", TransactionErrorCode.CONNECTION_NOT_FOUND)).when(transaction).begin();
		return transactionFactory;
	}
	
	@Test
	public void test_deleteProfile_should_generate_DB_HIGH_QUERY_RESPONSE_TIME_alert_when_execution_time_is_high() throws Exception {
		TransactionFactory transactionFactory = setUpMockToGenerateHIGHRESPONSEAlertForDelete();
		AlertListener alertListener = mock(AlertListener.class);
		NVDBSPInterface spInterface = new NVDBSPInterface(alertListener, transactionFactory);

		String subscriberID = "101";
		
		spInterface.purgeProfile(subscriberID);
		
		verify(alertListener, only())
				.generateSystemAlert(Mockito.anyString(), Mockito.eq(Alerts.HIGH_QUERY_RESPONSE_TIME), Mockito.anyString(), Mockito.anyString());
	}
	
	private TransactionFactory setUpMockToGenerateHIGHRESPONSEAlertForDelete() throws Exception {
		TransactionFactory transactionFactory = spy(this.transactionFactory);
		Transaction transaction = mock(Transaction.class);
		PreparedStatement preparedStatement = mock(PreparedStatement.class);
		doReturn(transaction).when(transactionFactory).createTransaction();
		doReturn(preparedStatement).when(transaction).prepareStatement(anyString());
		doNothing().when(preparedStatement).setString(anyShort(), anyString());

		when(preparedStatement.executeUpdate()).thenAnswer(new Answer<Integer>() {

			public Integer answer(org.mockito.invocation.InvocationOnMock invocation) throws Throwable {

				long startTime = System.currentTimeMillis();
				long elapsedTime = 0;
				while (elapsedTime < AlertConstants.DB_HIGH_QUERY_RESPONSE_TIME_MS + 10) {
					elapsedTime = System.currentTimeMillis() - startTime;
				}
				return 0;
			};

		});
		return transactionFactory;
	}
	
	@Test
	public void test_deleteProfile_should_generate_QUERYTIMEOUT_alert_when() throws Exception {
		AlertListener alertListener = mock(AlertListener.class);
		TransactionFactory transactionFactory = setUpMockToGenerateQUERYTIMEOUTalertForDelete();
		NVDBSPInterface spInterface = new NVDBSPInterface(alertListener, transactionFactory);

		String subscriberID = "101";

		expectedException.expect(OperationFailedException.class);
		
		try {
			spInterface.purgeProfile(subscriberID);
		} catch (OperationFailedException e) {
			verify(alertListener, only())
					.generateSystemAlert(Mockito.anyString(), Mockito.eq(Alerts.QUERY_TIME_OUT), Mockito.anyString(), Mockito.anyString());
			throw e;
		}
		fail("deleteProfile should throw Exception");
	}
	
	private TransactionFactory setUpMockToGenerateQUERYTIMEOUTalertForDelete() throws Exception {
		TransactionFactory transactionFactory = spy(this.transactionFactory);
		Transaction transaction = mock(Transaction.class);
		PreparedStatement preparedStatement = mock(PreparedStatement.class);
		doReturn(transaction).when(transactionFactory).createTransaction();
		doReturn(preparedStatement).when(transaction).prepareStatement(anyString());
		doNothing().when(preparedStatement).setString(anyShort(), anyString());

		when(preparedStatement.executeUpdate()).thenThrow(new SQLException("query timeout", "timeout", 1013));
		return transactionFactory;
	}
	
	@Test
	public void test_deleteProfile_should_throw_OperationFailedException_when_DB_is_down() throws Exception {

		setUpMockTransactionFactoryDead();

		NVDBSPInterface spInterface = new NVDBSPInterface(mock(AlertListener.class), transactionFactory);

		expectedException.expect(OperationFailedException.class);

		spInterface.purgeProfile("101");
	}
	
	@Test
	public void test_deleteProfile_should_delete_profile_of_provided_subscriberIdentity() throws Exception {
		NVDBSPInterface spInterface = new NVDBSPInterface(mock(AlertListener.class), transactionFactory);
		
		String msisdn = "501";
		SPRInfo subscriber = getSubscriber(msisdn, SubscriberStatus.DELETED);
		
		spInterface.addProfile(subscriber);
		
		spInterface.purgeProfile(msisdn);
		
		assertNull(spInterface.getProfile(msisdn));
	}
	
	@Test
	@Parameters(method = "dataProviderFor_deleteProfile_should_throw_OperationFailedException_when_any_exception_thrown")
	public void test_deleteProfile_should_throw_OperationFailedException_when_any_exception_thrown(
			String whenToThrow,
			Class<? extends Throwable> exceptionToBeThrown) throws Exception {

		setupMockToGenerateSQLException_deleteProfile(whenToThrow, exceptionToBeThrown);
		
		NVDBSPInterface spInterface = new NVDBSPInterface(mock(AlertListener.class), transactionFactory);

		expectedException.expect(OperationFailedException.class);

		spInterface.purgeProfile("101");
	}
	
	@SuppressWarnings("unused")
	private Object[][] dataProviderFor_deleteProfile_should_throw_OperationFailedException_when_any_exception_thrown() {
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
		
		};
	}
	
	private void setupMockToGenerateSQLException_deleteProfile(String whenToThrow, Class<? extends Throwable> exceptionToBeThrown) throws Exception {
		Transaction transaction = mock(Transaction.class);
		PreparedStatement preparedStatement = mock(PreparedStatement.class);

		transactionFactory = spy(transactionFactory);
		doReturn(transaction).when(transactionFactory).createTransaction();
		doReturn(preparedStatement).when(transaction).prepareStatement(Mockito.anyString());
		doReturn(1).when(preparedStatement).executeUpdate();

		if (PREPARESTATEMENT.equals(whenToThrow)) {
			doThrow(exceptionToBeThrown).when(transaction).prepareStatement(anyString());
		} else if (BEGIN.equals(whenToThrow)) {
			doThrow(exceptionToBeThrown).when(transaction).begin();
		} else if (EXECUTE.equals(whenToThrow)) {
			doThrow(exceptionToBeThrown).when(preparedStatement).executeUpdate();
		}
	}
	
	@Test
	public void test_getDeleteMarkedProfiles_should_give_deleteMarked_subscribers() throws Exception {
		NVDBSPInterface spInterface = new NVDBSPInterface(mock(AlertListener.class), transactionFactory);
		
		SPRInfo sprInfo = getSubscriber("101", SubscriberStatus.DELETED);
		SPRInfo sprInfo1 = getSubscriber("102", SubscriberStatus.DELETED);
		SPRInfo sprInfo2 = getSubscriber("103", SubscriberStatus.ACTIVE);
		
		spInterface.addProfile(sprInfo);
		spInterface.addProfile(sprInfo1);
		spInterface.addProfile(sprInfo2);

		List<SPRInfo> deleteMarkedProfiles = spInterface.getDeleteMarkedProfiles();
		
		assertReflectionEquals(Arrays.asList(sprInfo, sprInfo1), deleteMarkedProfiles, ReflectionComparatorMode.LENIENT_ORDER);
	}


	/// works if identity field is MSISDN
	private SPRInfo getSubscriber(String msisdn, SubscriberStatus status) {

		return new SPRInfoImpl.SPRInfoBuilder().withStatus(status.name()).withEncryptionType(PasswordEncryptionType.NONE.strVal).withMsisdn(msisdn).withSubscriberIdentity(msisdn).build();
	}
	
	@Test
	public void test_restore_should_give_count_of_number_of_subscriber_restored() throws Exception {
		
		NVDBSPInterface spInterface = new NVDBSPInterface(mock(AlertListener.class), transactionFactory);
		
		String msisdn = "505";
		SPRInfo sprInfo = getSubscriber(msisdn, SubscriberStatus.DELETED);
		spInterface.addProfile(sprInfo);
		
		int expectedRestoreCount = 1;
		int actualRestoreCount = spInterface.restoreProfile(msisdn);
		
		assertEquals(expectedRestoreCount, actualRestoreCount);
	}
	
	@Test
	public void test_restore_should_give_zero_count_if_subscriber_not_exist() throws Exception {
		
		NVDBSPInterface spInterface = new NVDBSPInterface(mock(AlertListener.class), transactionFactory);
		
		int actualRestoreCount = spInterface.restoreProfile("501");
		
		assertEquals(0, actualRestoreCount);
	}
	
	@Test
	public void test_restore_should_change_subscriber_status_to_INACTIVE() throws Exception {
		
		NVDBSPInterface spInterface = new NVDBSPInterface(mock(AlertListener.class), transactionFactory);
		
		String msisdn = "505";
		SPRInfo sprInfo = getSubscriber(msisdn, SubscriberStatus.DELETED);
		//add subscriber with 'Deleted' status
		spInterface.addProfile(sprInfo);
		
		spInterface.restoreProfile(msisdn);
		
		SPRInfo profile = spInterface.getProfile(msisdn);
		assertEquals(SubscriberStatus.INACTIVE.name(), profile.getStatus());
	}
	
	@Test
	public void test_restore_multiple_should_restore_all_subscriber_provided() throws Exception {
		NVDBSPInterface spInterface = new NVDBSPInterface(mock(AlertListener.class), transactionFactory);

		List<String> subscribersToBeRestored = Arrays.asList("501", "502", "503", "504");
		
		addDeletedSubscribers(spInterface, subscribersToBeRestored);
		
		spInterface.restoreProfile(subscribersToBeRestored);

		for (String subscriberIdentity : subscribersToBeRestored) {
			SPRInfo profile = spInterface.getProfile(subscriberIdentity);
			assertEquals(SubscriberStatus.INACTIVE.name(), profile.getStatus());
		}		
	}

	private void addDeletedSubscribers(NVDBSPInterface spInterface, List<String> subscribersToBeRestored) throws OperationFailedException {
		SPRInfo sprInfo;
		for (String subscriberIdentity : subscribersToBeRestored) {
			sprInfo = getSubscriber(subscriberIdentity, SubscriberStatus.DELETED);
			// add subscriber with 'Deleted' status
			spInterface.addProfile(sprInfo);
		}
	}
	
	@Test
	public void test_restore_multiple_should_give_count_of_number_of_subscriber_restored() throws Exception {
		NVDBSPInterface spInterface = new NVDBSPInterface(mock(AlertListener.class), transactionFactory);

		List<String> subscribersToBeRestored = Arrays.asList("501", "502", "503", "504");
		
		addDeletedSubscribers(spInterface, subscribersToBeRestored);
		
		int actualRestoreCount = spInterface.restoreProfile(subscribersToBeRestored).size();

		assertEquals(subscribersToBeRestored.size(), actualRestoreCount);	
	}
}
