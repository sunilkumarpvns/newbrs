package com.elitecore.aaa.radius.drivers.conf.impl;

import static com.elitecore.core.CoreLibMatchers.ESCommunicatorMatchers.isDead;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertTrue;
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

import com.elitecore.aaa.core.constant.DriverTypes;
import com.elitecore.aaa.core.data.AccountDataFieldMapping;
import com.elitecore.aaa.core.data.DataFieldMappingImpl;
import com.elitecore.aaa.core.drivers.DBAuthDriver;
import com.elitecore.aaa.core.radius.dictionary.RadiusDictionaryTestHarness;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.radius.drivers.RadDBAuthDirver;
import com.elitecore.aaa.radius.drivers.TableRadiusCustomerSchema;
import com.elitecore.commons.base.DBUtility;
import com.elitecore.commons.tests.PrintMethodRule;
import com.elitecore.core.commons.drivers.DriverInitializationFailedException;
import com.elitecore.core.commons.util.db.DBConnectionManagerSpy;
import com.elitecore.core.commons.util.db.DataSourceException;
import com.elitecore.core.commons.util.db.DatabaseInitializationException;
import com.elitecore.core.commons.util.db.DatabaseTypeNotSupportedException;
import com.elitecore.core.commons.util.db.DerbyConnectionProvider;
import com.elitecore.core.systemx.esix.FakeTaskScheduler;

/**
 * 
 * @author narendra.pathai
 */
public class RadDBAuthDriverBasicTest {
	
	public static final int DRIVER_INSTANCE_ID = 1;

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
	
	/**
	 * This driver depends on {@code DBAuthDriver} for a lot of features. If it is changed then ensure
	 * that the features stay the same.
	 */
	@Test
	public void extendsDBAuthDriver() {
		assertTrue(DBAuthDriver.class.isAssignableFrom(RadDBAuthDirver.class));
	}
	
	@Test
	public void failsToInitializeIfNoDBFieldMappingIsConfigured() throws DriverInitializationFailedException {
		RadDBAuthDriverConfigurationImpl configuration = new RadDBAuthDriverConfigurationImpl();
		RadDBAuthDirver driver = new RadDBAuthDirver(serverContext, configuration, derbyConnectionProvider.getDataSource(), dbConnectionManagerSpy.getNonSpiedManager());

		exception.expect(DriverInitializationFailedException.class);
		exception.expectMessage("No DB Field Mapping Configured");
		driver.init();
	}
	
	@Test
	public void failsToInitializeIfDatabseTypeIsNotSupported() throws Exception {
		RadDBAuthDirver driver = createDriver();
		
		dbConnectionManagerSpy.simulateDatabaseTypeUnsupported();
		
		exception.expect(DriverInitializationFailedException.class);
		exception.expectMessage("Datasource Type is not Supported");
		
		driver.init();
	}
	
	@Test
	public void initializesSuccessfullyInCaseOfTransientDatabaseProblems() throws Exception {
		RadDBAuthDirver driver = createDriver();
		
		dbConnectionManagerSpy.simulateDatabaseInitializationFailure();
		
		driver.init();
	}
	
	@Test
	public void marksDriverAsDeadInCaseOfTransientDatabaseProblems() throws Exception {
		RadDBAuthDirver driver = createDriver();
		
		dbConnectionManagerSpy.simulateDatabaseInitializationFailure();
		
		driver.init();
		
		assertThat(driver, isDead());
	}
	
	@Test
	public void driverTypeIs_RAD_OPENDB_AUTH_DRIVER() throws DriverInitializationFailedException {
		RadDBAuthDirver driver = createDriver();
		
		assertThat(driver.getType(), is(equalTo(DriverTypes.RAD_OPENDB_AUTH_DRIVER.value)));
		assertThat(driver.getTypeName(), is(equalTo(DriverTypes.RAD_OPENDB_AUTH_DRIVER.name())));
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

	private RadDBAuthDirver createDriver() throws DriverInitializationFailedException {
		configurable.postReadProcessing();

		RadDBAuthDirver driver = new RadDBAuthDirver(serverContext, driverConfiguration, derbyConnectionProvider.getDataSource(), dbConnectionManagerSpy.getSpiedInstance());
		driver.init();
		return driver;
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
}
