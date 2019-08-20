package com.elitecore.aaa.radius.drivers.conf.impl;

import static com.elitecore.aaa.radius.drivers.TableRadiusCustomerSchema.radiusCustomer;
import static com.elitecore.core.CoreLibMatchers.ESCommunicatorMatchers.isAlive;
import static com.elitecore.core.CoreLibMatchers.ESCommunicatorMatchers.isDead;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import com.elitecore.aaa.core.data.AccountData;
import com.elitecore.aaa.core.data.AccountDataFieldMapping;
import com.elitecore.aaa.core.data.DataFieldMappingImpl;
import com.elitecore.aaa.core.drivers.ChangeCaseStrategy;
import com.elitecore.aaa.core.drivers.StripUserIdentityStrategy;
import com.elitecore.aaa.core.radius.dictionary.RadiusDictionaryTestHarness;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.radius.drivers.RadDBAuthDirver;
import com.elitecore.aaa.radius.drivers.TableRadiusCustomerSchema;
import com.elitecore.aaa.radius.drivers.TableRadiusCustomerSchema.RadiusCustomerInsertQueryBuilder;
import com.elitecore.aaa.radius.service.auth.RadAuthRequest;
import com.elitecore.aaa.radius.service.base.RadAuthRequestBuilder;
import com.elitecore.commons.base.DBUtility;
import com.elitecore.commons.tests.PrintMethodRule;
import com.elitecore.core.commons.drivers.DriverInitializationFailedException;
import com.elitecore.core.commons.drivers.DriverProcessFailedException;
import com.elitecore.core.commons.util.db.DBConnectionManagerSpy;
import com.elitecore.core.commons.util.db.DataSourceException;
import com.elitecore.core.commons.util.db.DatabaseInitializationException;
import com.elitecore.core.commons.util.db.DatabaseTypeNotSupportedException;
import com.elitecore.core.commons.util.db.DerbyConnectionProvider;
import com.elitecore.core.systemx.esix.FakeTaskScheduler;

import de.bechte.junit.runners.context.HierarchicalContextRunner;

/**
 * 
 * @author narendra.pathai
 *
 */
@RunWith(HierarchicalContextRunner.class)
public class RadDBAuthDriverGetAccountDataTest {

	private static final String TEST_USER = "TestUser";

	private AAAServerContext serverContext;
	private RadDBAuthDriverConfigurable configurable;
	private RadDBAuthDriverConfigurationImpl driverConfiguration;
	private FakeTaskScheduler fakeTaskScheduler;
	private DerbyConnectionProvider derbyConnectionProvider;
	private DBConnectionManagerSpy dbConnectionManagerSpy;

	@Rule
	public PrintMethodRule printMethod = new PrintMethodRule();

	@Rule
	public ExpectedException exception = ExpectedException.none();

	@BeforeClass
	public static void setUpBeforeClass() throws SQLException, ClassNotFoundException, DatabaseInitializationException, DatabaseTypeNotSupportedException {
		RadiusDictionaryTestHarness.getInstance();
		createTableSchema();
	}

	private static void createTableSchema() throws ClassNotFoundException, SQLException, DatabaseInitializationException, DatabaseTypeNotSupportedException {
		System.out.println("Creating table schema for radius customer");
		DerbyConnectionProvider connectionProvider = new DerbyConnectionProvider(null);
		Connection connection = null;
		try {
			connection = connectionProvider.getConnectionManager().getConnection();
			TableRadiusCustomerSchema schema = new TableRadiusCustomerSchema();
			schema.create(connection);
		} finally {
			DBUtility.closeQuietly(connection);
		}
	}

	@Before
	public void setUp() throws ClassNotFoundException, DatabaseInitializationException, DatabaseTypeNotSupportedException, DataSourceException {
		fakeTaskScheduler = new FakeTaskScheduler();
		derbyConnectionProvider = new DerbyConnectionProvider(fakeTaskScheduler);
		serverContext = mock(AAAServerContext.class);
		when(serverContext.getTaskScheduler()).thenReturn(fakeTaskScheduler);

		configurable = new RadDBAuthDriverConfigurable();
		driverConfiguration = new RadDBAuthDriverConfigurationImpl();
		driverConfiguration.setDriverName("TestDriver");
		configurable.setRadDBAuthdriverList(Arrays.asList(driverConfiguration));
		driverConfiguration.setDSName(derbyConnectionProvider.getDataSource().getDataSourceName());

		dbConnectionManagerSpy = DBConnectionManagerSpy.create(derbyConnectionProvider.getConnectionManager());

		createBasicFieldMappings();
	}

	private void createBasicFieldMappings() {
		AccountDataFieldMapping accountDataFieldMapping = new AccountDataFieldMapping();
		DataFieldMappingImpl fieldMapping = new DataFieldMappingImpl();
		fieldMapping.setLogicalName(AccountDataFieldMapping.USER_NAME);
		fieldMapping.setFieldName("USER_IDENTITY");
		accountDataFieldMapping.setFieldMappingList(Arrays.asList(fieldMapping));
		driverConfiguration.setAccountDataFieldMapping(accountDataFieldMapping);
	}

	public class NonEAPRequest {

		public class HandlerLevelOrClientLevelUserIdentityAttributes {

			@Before
			public void setUp() {
				
			}

			@Test
			public void fetchesSubscriberProfileUsingUserIdentityAttributeConfigured() throws Exception {
				RadDBAuthDirver driver = createDriver();

				RadAuthRequest radAuthRequest = new RadAuthRequestBuilder()
						.addAttribute("0:1", TEST_USER)
						.build();

				insert(radiusCustomer().withUserName(TEST_USER));

				AccountData accountData = driver.getAccountData(radAuthRequest, Arrays.asList("0:1"), ChangeCaseStrategy.none(), false, StripUserIdentityStrategy.none(), null);
				accountData = driver.getAccountData(radAuthRequest, Arrays.asList("0:1"), ChangeCaseStrategy.none(), false, StripUserIdentityStrategy.none(), null);

				assertThat(accountData, is(notNullValue()));
				assertThat(accountData.getUserIdentity(), is(equalTo(TEST_USER)));
			}

			@Test
			public void ifUserIdentityAttributeIsNotPresentInRequestThenFetchesSubscriberProfileUsingOtherUserIdentityAttributes() throws Exception {
				RadDBAuthDirver driver = createDriver();

				RadAuthRequest radAuthRequest = new RadAuthRequestBuilder().addAttribute("0:31", TEST_USER).build();

				insert(radiusCustomer().withUserName(TEST_USER));

				AccountData accountData = driver.getAccountData(radAuthRequest, Arrays.asList("0:1","0:31"), ChangeCaseStrategy.none(), false, StripUserIdentityStrategy.none(), null);
				assertThat(accountData, is(notNullValue()));
				assertThat(accountData.getUserIdentity(), is(equalTo(TEST_USER)));
			}

			@Test
			public void cannotFindSubscriberProfileIfNoUserIdentityAttributeIsPresentInRequest() throws Exception {
				RadDBAuthDirver driver = createDriver();

				RadAuthRequest radAuthRequest = new RadAuthRequestBuilder().build();

				insert(radiusCustomer().withUserName(TEST_USER));

				AccountData accountData = driver.getAccountData(radAuthRequest, Arrays.asList("0:1","0:31"), ChangeCaseStrategy.none(), false, StripUserIdentityStrategy.none(), null);

				assertThat(accountData, is(nullValue()));
			}

			public class DriverLevelUserIdentityAttributePresent {

				private static final String TEST_CALLING_STATION_ID = "TestCallingStationId";
				private static final String ANY_VALUE = "Any";

				@Test
				public void overridesHandlerOrClientLevelUserIdentityWithDriverLevelIfPresentInRequest() throws Exception {
					driverConfiguration.setUserIdentity("0:31");

					RadDBAuthDirver driver = createDriver();

					RadAuthRequest radAuthRequest = new RadAuthRequestBuilder()
							.addAttribute("0:1", TEST_USER)
							.addAttribute("0:31", TEST_CALLING_STATION_ID).build();

					insert(radiusCustomer().withUserName(TEST_CALLING_STATION_ID));

					AccountData accountData = driver.getAccountData(radAuthRequest, Arrays.asList("0:1"), ChangeCaseStrategy.none(), false, StripUserIdentityStrategy.none(), null);

					assertThat(accountData, is(notNullValue()));
					assertThat(accountData.getUserIdentity(), is(equalTo(TEST_CALLING_STATION_ID)));
				}

				@Test
				public void multipleUserIdentityAttributesCanBeConfiguredAndFirstFoundIsConsidered() throws Exception {
					driverConfiguration.setUserIdentity("0:31,0:89");

					RadDBAuthDirver driver = createDriver();

					RadAuthRequest radAuthRequest = new RadAuthRequestBuilder()
							.addAttribute("0:1", TEST_USER)
							.addAttribute("0:31", TEST_CALLING_STATION_ID)
							.addAttribute("0:89", ANY_VALUE).build();

					insert(radiusCustomer().withUserName(TEST_CALLING_STATION_ID));

					AccountData accountData = driver.getAccountData(radAuthRequest, Arrays.asList("0:1"), ChangeCaseStrategy.none(), false, StripUserIdentityStrategy.none(), null);

					assertThat(accountData, is(notNullValue()));
					assertThat(accountData.getUserIdentity(), is(equalTo(TEST_CALLING_STATION_ID)));
				}
			}
		}

		@Test
		public void throwsDriverProcessingFailedExceptionIfDatasourceProcessingFailsWhileFetchingConnection() throws Exception {
			RadDBAuthDirver driver = createDriver();

			RadAuthRequest radAuthRequest = new RadAuthRequestBuilder()
					.addAttribute("0:1", TEST_USER).build();

			dbConnectionManagerSpy.simulateDatasourceExceptionFromGetConnection();

			exception.expect(DriverProcessFailedException.class);

			driver.getAccountData(radAuthRequest, Arrays.asList("0:1"), ChangeCaseStrategy.none(), false, StripUserIdentityStrategy.none(), null);
		}
		
		@Test
		public void throwsDriverProcessingFailedExceptionIfDatasourceProcessingFailsDueToDBDownWhileFetchingConnection() throws Exception {
			RadDBAuthDirver driver = createDriver();

			RadAuthRequest radAuthRequest = new RadAuthRequestBuilder()
					.addAttribute("0:1", TEST_USER).build();

			dbConnectionManagerSpy.simulateDBDownDatasourceExceptionFromGetConnection();

			exception.expect(DriverProcessFailedException.class);

			driver.getAccountData(radAuthRequest, Arrays.asList("0:1"), ChangeCaseStrategy.none(), false, StripUserIdentityStrategy.none(), null);
		}
		
		@Test
		public void driverIsMarkedDeadIfDatasourceProcessingFailsDueToDBDownWhileFetchingConnection() throws Exception {
			RadDBAuthDirver driver = createDriver();

			RadAuthRequest radAuthRequest = new RadAuthRequestBuilder()
					.addAttribute("0:1", TEST_USER).build();

			dbConnectionManagerSpy.simulateDBDownDatasourceExceptionFromGetConnection();

			try {
				driver.getAccountData(radAuthRequest, Arrays.asList("0:1"), ChangeCaseStrategy.none(), false, StripUserIdentityStrategy.none(), null);
				fail("Expected exception");
			} catch (DriverProcessFailedException ex) {
				assertThat(driver, isDead());
			}
			
		}

		@Test
		public void incrementsErrorCounterIfDatasourceProcessingFailsWhileFetchingConnection() throws Exception {
			RadDBAuthDirver driver = createDriver();

			RadAuthRequest radAuthRequest = new RadAuthRequestBuilder()
					.addAttribute("0:1", TEST_USER).build();

			dbConnectionManagerSpy.simulateDatasourceExceptionFromGetConnection();

			try {
				driver.getAccountData(radAuthRequest, Arrays.asList("0:1"), ChangeCaseStrategy.none(), false, StripUserIdentityStrategy.none(), null);
				fail("Exception was expected to be thrown");
			} catch (DriverProcessFailedException ex) {
				assertThat(driver.getStatistics().getTotalErrors(), is(1L));
			}
		}

		@Test
		public void throwsDriverProcessingFailedExceptionIfDatasourceProcessingFailsWhileExecutingQuery_WithSQLRecoverableException() throws Exception {
			RadDBAuthDirver driver = createDriver();

			RadAuthRequest radAuthRequest = new RadAuthRequestBuilder()
					.addAttribute("0:1", TEST_USER).build();

			dbConnectionManagerSpy.simulateSQLRecoverableExceptionFromPreparedStatement();

			exception.expect(DriverProcessFailedException.class);

			driver.getAccountData(radAuthRequest, Arrays.asList("0:1"), ChangeCaseStrategy.none(), false, StripUserIdentityStrategy.none(), null);
		}

		@Test
		public void driverIsMarkedDeadIfDatasourceProcessingFailsWhileExecutingQuery_WithSQLRecoverableException() throws Exception {
			RadDBAuthDirver driver = createDriver();

			RadAuthRequest radAuthRequest = new RadAuthRequestBuilder()
					.addAttribute("0:1", TEST_USER).build();

			dbConnectionManagerSpy.simulateSQLRecoverableExceptionFromPreparedStatement();

			try {
				driver.getAccountData(radAuthRequest, Arrays.asList("0:1"), ChangeCaseStrategy.none(), false, StripUserIdentityStrategy.none(), null);
				fail("Exception was expected to be thrown");
			} catch (DriverProcessFailedException ex) {
				assertThat(driver, isDead());
			}
		}

		public class QueryTimeout {

			@Before
			public void setUp() throws SQLException {
			
			}

			@Test
			public void driverStaysAliveIfTimeoutCountIsNotReached() throws Exception {
				driverConfiguration.setMaxQueryTimeoutCount(2);
				RadDBAuthDirver driver = createDriver();

				RadAuthRequest radAuthRequest = new RadAuthRequestBuilder()
						.addAttribute("0:1", TEST_USER).build();

				dbConnectionManagerSpy.simulateQueryTimeout();

				fetchAccountDataExpectingFailure(radAuthRequest, driver);

				assertThat(driver, isAlive());
			}

			@Test
			public void driverIsDeadMarkedIfTimeoutCountIsReached() throws Exception {
				driverConfiguration.setMaxQueryTimeoutCount(1);
				RadDBAuthDirver driver = createDriver();

				RadAuthRequest radAuthRequest = new RadAuthRequestBuilder()
						.addAttribute("0:1", TEST_USER).build();

				dbConnectionManagerSpy.simulateQueryTimeout();

				fetchAccountDataExpectingFailure(radAuthRequest, driver);
				fetchAccountDataExpectingFailure(radAuthRequest, driver);

				assertThat(driver, isDead());
			}

			private void fetchAccountDataExpectingFailure(RadAuthRequest radAuthRequest, RadDBAuthDirver driver) {
				try {
					driver.getAccountData(radAuthRequest, Arrays.asList("0:1"), ChangeCaseStrategy.none(), false, StripUserIdentityStrategy.none(), null);
					fail("Expected processing failure");
				} catch (DriverProcessFailedException ex) {

				}
			}
		}
	}

	public class EAPRequest {

		private static final String EAP_USER = "EAPUser";
		
		@Test
		public void throwsDriverProcessingFailedExceptionIfDatasourceProcessingFailsWhileFetchingConnection() throws Exception {
			RadAuthRequest radAuthRequest = new RadAuthRequestBuilder()
					.addAttribute("0:1", TEST_USER).build();

			RadDBAuthDirver driver = createDriver();

			dbConnectionManagerSpy.simulateDatasourceExceptionFromGetConnection();

			exception.expect(DriverProcessFailedException.class);

			driver.getAccountData(radAuthRequest, ChangeCaseStrategy.none(), false, StripUserIdentityStrategy.none(), null, TEST_USER);
		}

		@Test
		public void incrementsErrorCounterIfDatasourceProcessingFailsWhileFetchingConnection() throws Exception {
			RadAuthRequest radAuthRequest = new RadAuthRequestBuilder()
					.addAttribute("0:1", TEST_USER).build();

			RadDBAuthDirver driver = createDriver();

			dbConnectionManagerSpy.simulateDatasourceExceptionFromGetConnection();

			try {
				driver.getAccountData(radAuthRequest, ChangeCaseStrategy.none(), false, StripUserIdentityStrategy.none(), null, TEST_USER);
				fail("Exception was expected to be thrown");
			} catch (DriverProcessFailedException ex) {
				assertThat(driver.getStatistics().getTotalErrors(), is(1L));
			}
		}

		@Test
		public void throwsDriverProcessingFailedExceptionIfDatasourceProcessingFailsWhileExecutingQuery_WithSQLRecoverableException() throws Exception {
			RadDBAuthDirver driver = createDriver();

			RadAuthRequest radAuthRequest = new RadAuthRequestBuilder()
					.addAttribute("0:1", TEST_USER).build();


			dbConnectionManagerSpy.simulateSQLRecoverableExceptionFromPreparedStatement();

			exception.expect(DriverProcessFailedException.class);

			driver.getAccountData(radAuthRequest, ChangeCaseStrategy.none(), false, StripUserIdentityStrategy.none(), null, TEST_USER);
		}

		@Test
		public void driverIsMarkedDeadIfDatasourceProcessingFailsWhileExecutingQuery_WithSQLRecoverableException() throws Exception {
			RadDBAuthDirver driver = createDriver();

			RadAuthRequest radAuthRequest = new RadAuthRequestBuilder()
					.addAttribute("0:1", TEST_USER).build();

			
			dbConnectionManagerSpy.simulateSQLRecoverableExceptionFromPreparedStatement();

			try {
				driver.getAccountData(radAuthRequest, ChangeCaseStrategy.none(), false, StripUserIdentityStrategy.none(), null, TEST_USER);
			} catch (DriverProcessFailedException ex) {
				assertThat(driver, isDead());
			}
		}
		
		@Test
		public void driverIsMarkedDeadIfDatasourceProcessingFailsDueToDBDownWhileFetchingConnection() throws Exception {
			RadDBAuthDirver driver = createDriver();

			RadAuthRequest radAuthRequest = new RadAuthRequestBuilder()
					.addAttribute("0:1", TEST_USER).build();

			dbConnectionManagerSpy.simulateDBDownDatasourceExceptionFromGetConnection();

			try {
				driver.getAccountData(radAuthRequest, ChangeCaseStrategy.none(), false, StripUserIdentityStrategy.none(), null, TEST_USER);
				fail("Expected exception");
			} catch (DriverProcessFailedException ex) {
				assertThat(driver, isDead());
			}
			
		}

		public class HandlerLevelOrClientLevelUserIdentityAttributes {

			@Before
			public void setUp() {
			}

			@Test
			public void hasNoEffect() throws Exception {
				RadDBAuthDirver driver = createDriver();

				RadAuthRequest radAuthRequest = new RadAuthRequestBuilder().addAttribute("0:1", TEST_USER).build();

				insert(radiusCustomer().withUserName(EAP_USER));

				AccountData accountData = driver.getAccountData(radAuthRequest, ChangeCaseStrategy.none(), false, StripUserIdentityStrategy.none(), null, EAP_USER);
				assertThat(accountData, is(notNullValue()));
				assertThat(accountData.getUserName(), is(equalTo(EAP_USER)));
			}

		}

		public class DriverLevelUserIdentityAttributePresent {

			private static final String TEST_CALLING_STATION_ID = "TestCallingStationId";
			private static final String ANY_VALUE = "Any";

			@Test
			public void overridesUserIdentityWithDriverLevelIfPresentInRequest() throws Exception {
				driverConfiguration.setUserIdentity("0:31");

				RadDBAuthDirver driver = createDriver();

				RadAuthRequest radAuthRequest = new RadAuthRequestBuilder()
						.addAttribute("0:31", TEST_CALLING_STATION_ID).build();

				insert(radiusCustomer().withUserName(TEST_CALLING_STATION_ID));

				AccountData accountData = driver.getAccountData(radAuthRequest, ChangeCaseStrategy.none(), false, StripUserIdentityStrategy.none(), null, EAP_USER);

				assertThat(accountData, is(notNullValue()));
				assertThat(accountData.getUserIdentity(), is(equalTo(TEST_CALLING_STATION_ID)));
			}

			@Test
			public void multipleUserIdentityAttributesCanBeConfiguredAndFirstFoundIsConsidered() throws Exception {
				driverConfiguration.setUserIdentity("0:31,0:89");

				RadDBAuthDirver driver = createDriver();

				RadAuthRequest radAuthRequest = new RadAuthRequestBuilder()
						.addAttribute("0:31", TEST_CALLING_STATION_ID)
						.addAttribute("0:89", ANY_VALUE).build();

				insert(radiusCustomer().withUserName(TEST_CALLING_STATION_ID));

				AccountData accountData = driver.getAccountData(radAuthRequest, ChangeCaseStrategy.none(), false, StripUserIdentityStrategy.none(), null, EAP_USER);

				assertThat(accountData, is(notNullValue()));
				assertThat(accountData.getUserIdentity(), is(equalTo(TEST_CALLING_STATION_ID)));
			}
		}
		
		public class QueryTimeout {

			@Before
			public void setUp() throws SQLException {
			
			}

			@Test
			public void driverStaysAliveIfTimeoutCountIsNotReached() throws Exception {
				driverConfiguration.setMaxQueryTimeoutCount(2);
				RadAuthRequest radAuthRequest = new RadAuthRequestBuilder()
						.addAttribute("0:1", TEST_USER).build();

				RadDBAuthDirver driver = createDriver();

				dbConnectionManagerSpy.simulateQueryTimeout();

				fetchAccountDataExpectingFailure(radAuthRequest, driver);

				assertThat(driver, isAlive());
			}

			@Test
			public void driverIsDeadMarkedIfTimeoutCountIsReached() throws Exception {
				driverConfiguration.setMaxQueryTimeoutCount(1);
				RadAuthRequest radAuthRequest = new RadAuthRequestBuilder()
						.addAttribute("0:1", TEST_USER).build();

				RadDBAuthDirver driver = createDriver();

				dbConnectionManagerSpy.simulateQueryTimeout();

				fetchAccountDataExpectingFailure(radAuthRequest, driver);
				fetchAccountDataExpectingFailure(radAuthRequest, driver);

				assertThat(driver, isDead());
			}

			private void fetchAccountDataExpectingFailure(RadAuthRequest radAuthRequest, RadDBAuthDirver driver) {
				try {
					driver.getAccountData(radAuthRequest, ChangeCaseStrategy.none(), false, StripUserIdentityStrategy.none(), null, TEST_USER);
					fail("Expected processing failure");
				} catch (DriverProcessFailedException ex) {

				}
			}
		}
	}

	private void insert(RadiusCustomerInsertQueryBuilder subscriberProfile) throws SQLException {
		Connection connection = null;
		try {
			connection = derbyConnectionProvider.getConnectionManager().getConnection();
			TableRadiusCustomerSchema schema = new TableRadiusCustomerSchema();
			schema.insert(connection, subscriberProfile);
		} finally {
			DBUtility.closeQuietly(connection);
		}
	}

	@After
	public void truncateSchema() throws SQLException, ClassNotFoundException, DatabaseInitializationException, DatabaseTypeNotSupportedException {
		DerbyConnectionProvider connectionProvider = new DerbyConnectionProvider(null);
		Connection connection = null;
		try {
			connection = connectionProvider.getConnectionManager().getConnection();
			TableRadiusCustomerSchema schema = new TableRadiusCustomerSchema();
			schema.truncate(connection);
		} finally {
			DBUtility.closeQuietly(connection);
		}
	}
	
	@AfterClass
	public static void dropSchema() throws SQLException, DatabaseInitializationException, DatabaseTypeNotSupportedException {
		System.out.println("Dropping table schema for radius customer");
		DerbyConnectionProvider connectionProvider = new DerbyConnectionProvider(null);
		Connection connection = null;
		try {
			connection = connectionProvider.getConnectionManager().getConnection();
			TableRadiusCustomerSchema schema = new TableRadiusCustomerSchema();
			schema.drop(connection);
		} finally {
			DBUtility.closeQuietly(connection);
		}
	}

	private RadDBAuthDirver createDriver() throws DriverInitializationFailedException {
		configurable.postReadProcessing();

		RadDBAuthDirver driver = new RadDBAuthDirver(serverContext, driverConfiguration, derbyConnectionProvider.getDataSource(), dbConnectionManagerSpy.getSpiedInstance());
		driver.init();
		return driver;
	}
}
