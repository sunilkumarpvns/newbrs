package com.elitecore.aaa.radius.drivers;

import static com.elitecore.core.CoreLibMatchers.ESCommunicatorMatchers.isDead;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
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
import com.elitecore.aaa.core.data.AccountDataBuilder;
import com.elitecore.aaa.core.data.AccountDataFieldMapping;
import com.elitecore.aaa.core.data.DataFieldMappingImpl;
import com.elitecore.aaa.core.drivers.ChangeCaseStrategy;
import com.elitecore.aaa.core.drivers.StripUserIdentityStrategy;
import com.elitecore.aaa.core.radius.dictionary.RadiusDictionaryTestHarness;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.radius.drivers.conf.impl.RadDBAuthDriverConfigurable;
import com.elitecore.aaa.radius.drivers.conf.impl.RadDBAuthDriverConfigurationImpl;
import com.elitecore.aaa.radius.service.auth.RadAuthRequest;
import com.elitecore.aaa.radius.service.base.RadAuthRequestBuilder;
import com.elitecore.commons.base.DBUtility;
import com.elitecore.commons.tests.PrintMethodRule;
import com.elitecore.core.commons.drivers.DriverInitializationFailedException;
import com.elitecore.core.commons.util.db.DBConnectionManagerSpy;
import com.elitecore.core.commons.util.db.DataSourceException;
import com.elitecore.core.commons.util.db.DatabaseInitializationException;
import com.elitecore.core.commons.util.db.DatabaseTypeNotSupportedException;
import com.elitecore.core.commons.util.db.DerbyConnectionProvider;
import com.elitecore.core.systemx.esix.FakeTaskScheduler;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

/**
 * 
 * @author narendra.pathai
 *
 */
@RunWith(JUnitParamsRunner.class)
public class RadDBAuthDriverSaveAccountDataTest {
	private static final String TEST_USER = "TestUser";
	private DBConnectionManagerSpy dbConnectionManagerSpy;
	private static DerbyConnectionProvider derbyConnectionProvider;
	private static FakeTaskScheduler fakeTaskScheduler;
	private AAAServerContext serverContext;
	private RadDBAuthDriverConfigurable configurable;
	private RadDBAuthDriverConfigurationImpl driverConfiguration;

	@Rule
	public PrintMethodRule printMethod = new PrintMethodRule();

	@Rule
	public ExpectedException exception = ExpectedException.none();

	@BeforeClass
	public static void setUpBeforeClass() throws SQLException, ClassNotFoundException, DatabaseInitializationException, DatabaseTypeNotSupportedException {
		RadiusDictionaryTestHarness.getInstance();
		fakeTaskScheduler = new FakeTaskScheduler();
		derbyConnectionProvider = new DerbyConnectionProvider(fakeTaskScheduler);
		createTableSchema();
	}

	private static void createTableSchema() throws ClassNotFoundException, SQLException, DatabaseInitializationException, DatabaseTypeNotSupportedException {
		System.out.println("Creating table schema for radius customer");
		Connection connection = null;
		try {
			connection = derbyConnectionProvider.getConnectionManager().getConnection();
			TableRadiusCustomerSchema schema = new TableRadiusCustomerSchema();
			schema.create(connection);
		} finally {
			DBUtility.closeQuietly(connection);
		}
	}

	@Before
	public void setUp() throws ClassNotFoundException, DatabaseInitializationException, DatabaseTypeNotSupportedException, DataSourceException {
		serverContext = mock(AAAServerContext.class);
		when(serverContext.getTaskScheduler()).thenReturn(fakeTaskScheduler);

		configurable = new RadDBAuthDriverConfigurable();
		driverConfiguration = new RadDBAuthDriverConfigurationImpl();
		driverConfiguration.setDriverName("TestDriver");
		configurable.setRadDBAuthdriverList(Arrays.asList(driverConfiguration));
		driverConfiguration.setDSName(derbyConnectionProvider.getDataSource().getDataSourceName());

		createBasicFieldMappings();
		
		dbConnectionManagerSpy = DBConnectionManagerSpy.create(derbyConnectionProvider.getConnectionManager());
	}

	private void createBasicFieldMappings() {
		AccountDataFieldMapping accountDataFieldMapping = new AccountDataFieldMapping();
		DataFieldMappingImpl fieldMapping = new DataFieldMappingImpl();
		fieldMapping.setLogicalName(AccountDataFieldMapping.USER_NAME);
		fieldMapping.setFieldName("USER_IDENTITY");
		accountDataFieldMapping.getFieldMappingList().add(fieldMapping);
		driverConfiguration.setAccountDataFieldMapping(accountDataFieldMapping);
	}

	public static Object[][] dataFor_saveAccountData() {
		return new Object[][] {
			new Object[] { AccountDataFieldMapping.IMSI, "IMSI", "VALUE", 
					new AccountDataBuilder().userIdentity(TEST_USER).username(TEST_USER).imsi("VALUE").build() },

			new Object[] { AccountDataFieldMapping.IMEI, "IMEI", "VALUE", 
					new AccountDataBuilder().userIdentity(TEST_USER).username(TEST_USER).imei("VALUE").build() },

			new Object[] { AccountDataFieldMapping.MSISDN, "MSISDN", "VALUE", 
					new AccountDataBuilder().userIdentity(TEST_USER).username(TEST_USER).msisdn("VALUE").build() },

			new Object[] { AccountDataFieldMapping.MEID, "MEID", "VALUE", 
					new AccountDataBuilder().userIdentity(TEST_USER).username(TEST_USER).meid("VALUE").build() },

			new Object[] { AccountDataFieldMapping.MDN, "MDN", "VALUE", 
					new AccountDataBuilder().userIdentity(TEST_USER).username(TEST_USER).mdn("VALUE").build() },

			new Object[] { AccountDataFieldMapping.CUI, "CUI", "VALUE", 
					new AccountDataBuilder().userIdentity(TEST_USER).username(TEST_USER).cui("VALUE").build() },

			new Object[] { AccountDataFieldMapping.PASSWORD_CHECK, "PASSWORDCHECK", "YES", 
					new AccountDataBuilder().userIdentity(TEST_USER).username(TEST_USER).passwordCheck("YES").build() },

			new Object[] { AccountDataFieldMapping.USER_PASSWORD, "PASSWORD", "VALUE", 
					new AccountDataBuilder().userIdentity(TEST_USER).username(TEST_USER).password("VALUE").build() },

			new Object[] { AccountDataFieldMapping.ENCRYPTION_TYPE, "ENCRYPTIONTYPE", "1", 
					new AccountDataBuilder().userIdentity(TEST_USER).username(TEST_USER).encryptionType("1").build() },

			new Object[] { AccountDataFieldMapping.CUSTOMER_TYPE, "CUSTOMERTYPE", "VALUE", 
					new AccountDataBuilder().userIdentity(TEST_USER).username(TEST_USER).customerType("VALUE").build() },
			
			new Object[] { AccountDataFieldMapping.CUSTOMER_SERVICES, "CUSTOMERSERVICES", "VALUE", 
					new AccountDataBuilder().userIdentity(TEST_USER).username(TEST_USER).customerServices("VALUE").build() },

			new Object[] { AccountDataFieldMapping.SERVICE_TYPE, "CUSTOMERSERVICES", "VALUE", 
					new AccountDataBuilder().userIdentity(TEST_USER).username(TEST_USER).serviceType("VALUE").build() },

			new Object[] { AccountDataFieldMapping.CALLING_STATION_ID, "CALLINGSTATIONID", "VALUE", 
					new AccountDataBuilder().userIdentity(TEST_USER).username(TEST_USER).callingStationId("VALUE").build() },

			new Object[] { AccountDataFieldMapping.CALLED_STATION_ID, "CALLEDSTATIONID", "VALUE", 
					new AccountDataBuilder().userIdentity(TEST_USER).username(TEST_USER).calledStationId("VALUE").build() },

			new Object[] { AccountDataFieldMapping.MAX_SESSION_TIME, "PARAM1", "100", 
					new AccountDataBuilder().userIdentity(TEST_USER).username(TEST_USER).maxSessionTime(100).build() },

			new Object[] { AccountDataFieldMapping.MAC_VALIDATION, "MACVALIDATION", "Yes", 
					new AccountDataBuilder().userIdentity(TEST_USER).username(TEST_USER).macValidation(true).build() },

			new Object[] { AccountDataFieldMapping.CUSTOMER_CHECK_ITEMS, "CUSTOMERCHECKITEM", "VALUE", 
					new AccountDataBuilder().userIdentity(TEST_USER).username(TEST_USER).checkItems("VALUE").build() },

			new Object[] { AccountDataFieldMapping.CUSTOMER_REJECT_ITEMS, "CUSTOMERREJECTITEM", "VALUE", 
					new AccountDataBuilder().userIdentity(TEST_USER).username(TEST_USER).rejectItems("VALUE").build() },

			new Object[] { AccountDataFieldMapping.CUSTOMER_REPLY_ITEMS, "CUSTOMERREPLYITEM", "VALUE", 
					new AccountDataBuilder().userIdentity(TEST_USER).username(TEST_USER).replyItems("VALUE").build() },

			new Object[] { AccountDataFieldMapping.LOGIN_POLICY, "CONCURRENTLOGINPOLICY", "VALUE", 
					new AccountDataBuilder().userIdentity(TEST_USER).username(TEST_USER).concurrentLoginPolicy("VALUE").build() },

			new Object[] { AccountDataFieldMapping.ACCESS_POLICY, "ACCESSPOLICY", "VALUE", 
					new AccountDataBuilder().userIdentity(TEST_USER).username(TEST_USER).accessPolicy("VALUE").build() },

			new Object[] { AccountDataFieldMapping.CREDIT_LIMIT, "CREDITLIMIT", "100", 
					new AccountDataBuilder().userIdentity(TEST_USER).username(TEST_USER).creditLimit(100).creditLimitCheck(true).build() },

			//			new Object[] { AccountDataFieldMapping.EXPIRY_DATE, "EXPIRYDATE", todayString, 
			//					new AccountDataBuilder().userIdentity(TEST_USER).username(TEST_USER).expiryDate(today).build() },

			new Object[] { AccountDataFieldMapping.EMAIL, "CUSTOMERALTEMAILID", "test@sterlitetech.com", 
					new AccountDataBuilder().userIdentity(TEST_USER).username(TEST_USER).email("test@sterlitetech.com").build() },

			new Object[] { AccountDataFieldMapping.CUSTOMER_STATUS, "CUSTOMERSTATUS", "A", 
					new AccountDataBuilder().userIdentity(TEST_USER).username(TEST_USER).customerStatus("A").build() },

			new Object[] { AccountDataFieldMapping.PARAM1, "PARAM1", "PARAM1VALUE", 
					new AccountDataBuilder().userIdentity(TEST_USER).username(TEST_USER).param1("PARAM1VALUE").build() },

			new Object[] { AccountDataFieldMapping.PARAM2, "PARAM2", "PARAM2VALUE", 
					new AccountDataBuilder().userIdentity(TEST_USER).username(TEST_USER).param2("PARAM2VALUE").build() },

			new Object[] { AccountDataFieldMapping.PARAM3, "PARAM3", "PARAM3VALUE", 
					new AccountDataBuilder().userIdentity(TEST_USER).username(TEST_USER).param3("PARAM3VALUE").build() },

			new Object[] { AccountDataFieldMapping.PARAM4, "PARAM4", "PARAM4VALUE", 
					new AccountDataBuilder().userIdentity(TEST_USER).username(TEST_USER).param4("PARAM4VALUE").build() },

			new Object[] { AccountDataFieldMapping.PARAM5, "PARAM5", "PARAM5VALUE", 
					new AccountDataBuilder().userIdentity(TEST_USER).username(TEST_USER).param5("PARAM5VALUE").build() },

			new Object[] { AccountDataFieldMapping.HOTLINE_POLICY, "HOTLINEPOLICY", "VALUE", 
					new AccountDataBuilder().userIdentity(TEST_USER).username(TEST_USER).hotlinePolicy("VALUE").build() },

			new Object[] { AccountDataFieldMapping.GRACE_PERIOD, "GRACEPERIOD", "VALUE", 
					new AccountDataBuilder().userIdentity(TEST_USER).username(TEST_USER).gracePolicy("VALUE").build() },

			new Object[] { AccountDataFieldMapping.CALLBACK_ID, "CALLBACKID", "VALUE", 
					new AccountDataBuilder().userIdentity(TEST_USER).username(TEST_USER).callbackId("VALUE").build() },

			new Object[] { AccountDataFieldMapping.DEVICE_VENDOR, "DEVICEVENDOR", "VALUE", 
					new AccountDataBuilder().userIdentity(TEST_USER).username(TEST_USER).deviceVendor("VALUE").build() },

			new Object[] { AccountDataFieldMapping.DEVICE_NAME, "DEVICENAME", "VALUE", 
					new AccountDataBuilder().userIdentity(TEST_USER).username(TEST_USER).deviceName("VALUE").build() },

			new Object[] { AccountDataFieldMapping.DEVICE_PORT, "DEVICEPORT", "VALUE", 
					new AccountDataBuilder().userIdentity(TEST_USER).username(TEST_USER).devicePort("VALUE").build() },

			new Object[] { AccountDataFieldMapping.GEO_LOCATION, "GEOLOCATION", "VALUE", 
					new AccountDataBuilder().userIdentity(TEST_USER).username(TEST_USER).geoLocation("VALUE").build() },

			new Object[] { AccountDataFieldMapping.DEVICE_VLAN, "DEVICEVLAN", "VALUE", 
					new AccountDataBuilder().userIdentity(TEST_USER).username(TEST_USER).deviceVLAN("VALUE").build() },

			new Object[] { AccountDataFieldMapping.DYNAMIC_CHECK_ITEMS, "DYNAMICCHECKITEM", "VALUE", 
					new AccountDataBuilder().userIdentity(TEST_USER).username(TEST_USER).dynamicCheckItems("VALUE").build() },

			new Object[] { AccountDataFieldMapping.GROUP_NAME, "GROUPNAME", "VALUE", 
					new AccountDataBuilder().userIdentity(TEST_USER).username(TEST_USER).groupName("VALUE").build() }
		};
	}

	@Test
	@Parameters(method = "dataFor_saveAccountData")
	public void saveAccountData(String logicalName, String columnName, String value, AccountData expectedAccountData) throws Exception {
		disableSequencing();

		addFieldMapping(logicalName, columnName);

		RadDBAuthDirver driver = createDriver();

		driver.saveAccountData(expectedAccountData);

		RadAuthRequest request = new RadAuthRequestBuilder().addAttribute("0:1", TEST_USER).build();

		AccountData savedAccountData = driver.getAccountData(request, ChangeCaseStrategy.none(), false, StripUserIdentityStrategy.none(), null, TEST_USER);

		assertThat(savedAccountData, is(notNullValue()));
		assertThat(savedAccountData, is(equalTo(expectedAccountData)));
	}

	public static Object[][] dataFor_savingAuthorizationPolicy() {
		return new Object[][] {
			new Object[] { AccountDataFieldMapping.AUTHORIZATION_POLICY, "RADIUSPOLICY", "POLICY", 
					new AccountDataBuilder().userIdentity(TEST_USER).username(TEST_USER).authorizationPolicy("POLICY").build() }
		};
	}
	
	@Test
	@Parameters(method = "dataFor_savingAuthorizationPolicy")
	public void savingAuthorizationPolicy(String logicalName, String columnName, String value, AccountData accountData) throws Exception {
		disableSequencing();

		addFieldMapping(logicalName, columnName);

		RadDBAuthDirver driver = createDriver();

		driver.saveAccountData(accountData);

		RadAuthRequest request = new RadAuthRequestBuilder().addAttribute("0:1", TEST_USER).build();

		AccountData savedAccountData = driver.getAccountData(request, ChangeCaseStrategy.none(), false, StripUserIdentityStrategy.none(), null, TEST_USER);

		// Because account data while saving and retrieving encloses policy within parenthesis
		accountData.setAuthorizationPolicy(accountData.getAuthorizationPolicy());

		assertThat(savedAccountData, is(notNullValue()));
		assertThat(savedAccountData, is(equalTo(accountData)));
	}

	public static Object[][] dataFor_cachingAdditionalPolicy() {
		return new Object[][] {
			new Object[] { AccountDataFieldMapping.ADDITIONAL_POLICY, "ADDITIONALPOLICY", "VALUE", 
					new AccountDataBuilder().userIdentity(TEST_USER).username(TEST_USER).additionalPolicy("VALUE").build() }
		};
	}

	@Test
	@Parameters(method = "dataFor_cachingAdditionalPolicy")
	public void savingAdditionalPolicy(String logicalName, String columnName, String value, AccountData accountData) throws Exception {
		disableSequencing();

		addFieldMapping(logicalName, columnName);

		RadDBAuthDirver driver = createDriver();

		driver.saveAccountData(accountData);

		RadAuthRequest request = new RadAuthRequestBuilder().addAttribute("0:1", TEST_USER).build();

		AccountData savedAccountData = driver.getAccountData(request, ChangeCaseStrategy.none(), false, StripUserIdentityStrategy.none(), null, TEST_USER);

		// Because account data while saving and retrieving encloses policy within parenthesis
		accountData.setAdditionalPolicy(accountData.getAdditionalPolicy());

		assertThat(savedAccountData, is(notNullValue()));
		assertThat(savedAccountData, is(equalTo(accountData)));
	}

	public static Object[][] dataFor_cachingSubscriberProfileDoesNotConsiderTheseMappings() {

		return new Object[][] {
			new Object[] { AccountDataFieldMapping.FRAMED_IPV4_ADDRESS, "FRAMEDIPV4ADDRESS", "0.0.0.0", 
					new AccountDataBuilder().userIdentity(TEST_USER).username(TEST_USER).framedIPv4Address("0.0.0.0").build() },

			new Object[] { AccountDataFieldMapping.FRAMED_IPV6_PREFIX, "FRAMEDIPV6PREFIX", "VALUE", 
					new AccountDataBuilder().userIdentity(TEST_USER).username(TEST_USER).framedIPv6Prefix("VALUE").build() },

			new Object[] { AccountDataFieldMapping.FRAMED_POOL, "FRAMEDPOOL", "VALUE", 
					new AccountDataBuilder().userIdentity(TEST_USER).username(TEST_USER).framedPool("VALUE").build() },

			new Object[] { AccountDataFieldMapping.POLICY_GROUP, "RADIUSPOLICYGROUP", "VALUE", 
					new AccountDataBuilder().userIdentity(TEST_USER).username(TEST_USER).policyGroup("VALUE").build() },
		};
	}

	@Test
	@Parameters(method = "dataFor_cachingSubscriberProfileDoesNotConsiderTheseMappings")
	public void savingSubscriberProfileDoesNotConsiderTheseMappings(String logicalName, String columnName, String value, AccountData accountData) throws Exception {
		disableSequencing();
		addFieldMapping(logicalName, columnName);

		RadDBAuthDirver driver = createDriver();

		driver.saveAccountData(accountData);

		RadAuthRequest request = new RadAuthRequestBuilder().addAttribute("0:1", TEST_USER).build();

		AccountData savedAccountData = driver.getAccountData(request, ChangeCaseStrategy.none(), false, StripUserIdentityStrategy.none(), null, TEST_USER);

		assertThat(savedAccountData, is(notNullValue()));

		assertThat(savedAccountData, is(equalTo(new AccountDataBuilder().userIdentity(TEST_USER).username(TEST_USER).build())));
	}

	@Test
	@Parameters(method = "dataFor_saveAccountData")
	public void udpateAccountData(String logicalName, String columnName, String value, AccountData updatedAccountData) throws Exception {
		disableSequencing();

		addFieldMapping(logicalName, columnName);

		RadDBAuthDirver driver = createDriver();

		AccountData accountData = new AccountDataBuilder().username(TEST_USER).userIdentity(TEST_USER).build();

		driver.saveAccountData(accountData);

		driver.saveAccountData(updatedAccountData);

		RadAuthRequest request = new RadAuthRequestBuilder().addAttribute("0:1", TEST_USER).build();

		AccountData savedAccountData = driver.getAccountData(request, ChangeCaseStrategy.none(), false, StripUserIdentityStrategy.none(), null, TEST_USER);

		assertThat(savedAccountData, is(notNullValue()));
		assertThat(savedAccountData, is(equalTo(updatedAccountData)));
	}
	
	@Test
	@Parameters(method = "dataFor_savingAuthorizationPolicy")
	public void updatingAuthorizationPolicy(String logicalName, String columnName, String value, AccountData updatedAccountData) throws Exception {
		disableSequencing();

		addFieldMapping(logicalName, columnName);

		RadDBAuthDirver driver = createDriver();
		
		AccountData accountData = new AccountDataBuilder().username(TEST_USER).userIdentity(TEST_USER).build();
		
		driver.saveAccountData(accountData);
		
		driver.saveAccountData(updatedAccountData);

		RadAuthRequest request = new RadAuthRequestBuilder().addAttribute("0:1", TEST_USER).build();

		AccountData savedAccountData = driver.getAccountData(request, ChangeCaseStrategy.none(), false, StripUserIdentityStrategy.none(), null, TEST_USER);

		// Because account data while saving and retrieving encloses policy within parenthesis
		updatedAccountData.setAuthorizationPolicy(updatedAccountData.getAuthorizationPolicy());

		assertThat(savedAccountData, is(notNullValue()));
		assertThat(savedAccountData, is(equalTo(updatedAccountData)));
	}
	
	@Test
	@Parameters(method = "dataFor_cachingAdditionalPolicy")
	public void updatingAdditionalPolicy(String logicalName, String columnName, String value, AccountData updatedAccountData) throws Exception {
		disableSequencing();

		addFieldMapping(logicalName, columnName);

		RadDBAuthDirver driver = createDriver();
		
		AccountData accountData = new AccountDataBuilder().username(TEST_USER).userIdentity(TEST_USER).build();
		
		driver.saveAccountData(accountData);
		
		driver.saveAccountData(updatedAccountData);

		RadAuthRequest request = new RadAuthRequestBuilder().addAttribute("0:1", TEST_USER).build();

		AccountData savedAccountData = driver.getAccountData(request, ChangeCaseStrategy.none(), false, StripUserIdentityStrategy.none(), null, TEST_USER);

		// Because account data while saving and retrieving encloses policy within parenthesis
		updatedAccountData.setAdditionalPolicy(updatedAccountData.getAdditionalPolicy());

		assertThat(savedAccountData, is(notNullValue()));
		assertThat(savedAccountData, is(equalTo(updatedAccountData)));
	}
	
	@Test
	public void ignoresQuietlyAnySQLExceptionWhileSavingAccountData() throws Exception {
		disableSequencing();

		RadDBAuthDirver driver = createDriver();
		
		AccountData accountData = new AccountDataBuilder().username(TEST_USER).userIdentity(TEST_USER).build();

		dbConnectionManagerSpy.simulateSQLRecoverableExceptionFromPreparedStatement();
		
		driver.saveAccountData(accountData);
	}
	
	@Test
	public void ignoresQuietlyAnyDatasourceExceptionWhileSavingAccountData() throws Exception {
		disableSequencing();

		RadDBAuthDirver driver = createDriver();
		
		AccountData accountData = new AccountDataBuilder().username(TEST_USER).userIdentity(TEST_USER).build();

		dbConnectionManagerSpy.simulateDatasourceExceptionFromGetConnection();
		
		driver.saveAccountData(accountData);
	}
	
	@Test
	public void driverIsMarkedDeadOnAnyDBDownDatasourceExceptionWhileSavingAccountData() throws Exception {
		disableSequencing();

		RadDBAuthDirver driver = createDriver();
		
		AccountData accountData = new AccountDataBuilder().username(TEST_USER).userIdentity(TEST_USER).build();

		dbConnectionManagerSpy.simulateDBDownDatasourceExceptionFromGetConnection();
		
		driver.saveAccountData(accountData);
		
		assertThat(driver, isDead());
	}

	private void disableSequencing() {
		driverConfiguration.getCacheParams().setSequenceName(null);
	}

	private void addFieldMapping(String logicalName, String columnName) {
		DataFieldMappingImpl fieldMapping = new DataFieldMappingImpl();
		fieldMapping.setLogicalName(logicalName);
		fieldMapping.setFieldName(columnName);
		driverConfiguration.getAccountDataFieldMapping().getFieldMappingList().add(fieldMapping);
	}

	@After
	public void truncateSchema() throws SQLException, ClassNotFoundException, DatabaseInitializationException, DatabaseTypeNotSupportedException {
		Connection connection = null;
		try {
			connection = derbyConnectionProvider.getConnectionManager().getConnection();
			TableRadiusCustomerSchema schema = new TableRadiusCustomerSchema();
			schema.truncate(connection);
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

	@AfterClass
	public static void dropSchema() throws SQLException, DatabaseInitializationException, DatabaseTypeNotSupportedException {
		System.out.println("Dropping table schema for radius customer");
		Connection connection = null;
		try {
			connection = derbyConnectionProvider.getConnectionManager().getConnection();
			TableRadiusCustomerSchema schema = new TableRadiusCustomerSchema();
			schema.drop(connection);
		} finally {
			DBUtility.closeQuietly(connection);
		}
	}
}
