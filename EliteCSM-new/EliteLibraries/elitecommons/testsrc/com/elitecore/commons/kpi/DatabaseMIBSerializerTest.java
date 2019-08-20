package com.elitecore.commons.kpi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.concurrent.atomic.AtomicLong;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.elitecore.commons.kpi.annotation.Column;
import com.elitecore.commons.kpi.annotation.Table;
import com.elitecore.commons.kpi.config.KpiConfiguration;
import com.elitecore.commons.kpi.exception.InitializationFailedException;
import com.elitecore.commons.kpi.exception.RegistrationFailedException;
import com.elitecore.commons.kpi.exception.StartupFailedException;
import com.elitecore.commons.kpi.handler.ConnectionProvider;
import com.elitecore.commons.kpi.handler.DatabaseMIBSerializer;
import com.elitecore.commons.kpi.handler.IntervalBasedTask;
import com.elitecore.commons.kpi.handler.Scheduler;
import com.elitecore.commons.kpi.handler.SingleExecutionTask;
import com.elitecore.commons.kpi.mib.autogen.KpiCounterEntryMBean;
import com.elitecore.commons.kpi.mib.autogen.Kpitesting;
import com.elitecore.commons.kpi.mib.autogen.KpitestingMBean;
import com.elitecore.commons.kpi.mib.autogen.TEST_MIB;
import com.elitecore.commons.kpi.mib.autogen.TableKpiTestingTable;
import com.elitecore.commons.logging.ConsoleLogger;
import com.elitecore.commons.logging.ILogger;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.commons.logging.NullLogger;
import com.sun.management.snmp.SnmpStatusException;
import com.sun.management.snmp.agent.SnmpMib;


@RunWith(JUnitParamsRunner.class)
public class DatabaseMIBSerializerTest {

	private static final String KPI_TESTING_ENTRY_TABLE = "kpiTestingTable";
	private static final String KPI_TESTING_TABLE = "kpiTesting";
	private static final int BATCH_SIZE = 50;
	private static final int CORE_POOL_SIZE = 15;
	private static final String INSTANCE_ID = "instanceid";
	private static ConnectionProvider testConnectionProvider;
	private static Connection connection;
	private TestMIBImpl testMib;
	private TableKpiTestingTable tableKpiTestingTable;
	private DatabaseMIBSerializer databaseMIBSerializer;
	private SynchronousScheduler synchronousScheduler;

	@Mock private KpiConfiguration kpiConfig;
	@Rule public ExpectedException expectedException = ExpectedException.none();
	
	@BeforeClass
	public static void setUpBeforeClass() throws ClassNotFoundException, SQLException {
		testConnectionProvider = new TestConnectionProvider();
		connection = testConnectionProvider.getConnection();
		createTables();
	}
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		synchronousScheduler = new SynchronousScheduler();
		String instanceId = INSTANCE_ID;
		databaseMIBSerializer = new DatabaseMIBSerializerExt(instanceId, kpiConfig, testConnectionProvider);
		
		initKpiTestMib();
		
		when(kpiConfig.getDSName()).thenReturn("dataSourceName");
		when(kpiConfig.getMaxNoOfThreads()).thenReturn(CORE_POOL_SIZE);
		when(kpiConfig.getBatchSize()).thenReturn(BATCH_SIZE);
		
	}
	
	/*
	 * Testing constructor
	 * 
	 */
	@Test
	public void testConstructor_ThrowNullPointerException_WhenConnectionProviderIsNull() {
		expectedException.expect(NullPointerException.class);
		expectedException.expectMessage("connectionProvider is null");
		databaseMIBSerializer = new DatabaseMIBSerializer(INSTANCE_ID, kpiConfig, (ConnectionProvider) null);
	}
	
	/*
	 * Testing init() 
	 */
	@Test
	public void testInit_ShouldNotThrowAnyException_WhenItIsCalled() throws InitializationFailedException {
		databaseMIBSerializer.init();
	}
	
	@Test
	public void testInit_ShouldThrowInitializationFailedException_WhenItIsFailedToInitialize() throws InitializationFailedException {
		DatabaseMIBSerializer dbMIBSerializerSpy = Mockito.spy(databaseMIBSerializer);
		Mockito.doThrow(new InitializationFailedException("Initialization failed")).when(dbMIBSerializerSpy).init();
		expectedException.expect(InitializationFailedException.class);
		dbMIBSerializerSpy.init();
	}
	
	/*
	 * Testing registerMib()
	 */
	
	@Test
	public void testRegisterMib_ShouldThrowRegistrationFailedException_WhenSnmpMibIsNull() throws InitializationFailedException, RegistrationFailedException {
		databaseMIBSerializer.init();
		expectedException.expect(RegistrationFailedException.class);
		expectedException.expectMessage("snmpMib is null");
		databaseMIBSerializer.registerMib((SnmpMib)null);
	}
	
	@Test 
	public void testRegisterMib_ShouldThrowRegistrationFailedException_WhenItIsNotInitialized() throws IllegalAccessException, RegistrationFailedException {
		expectedException.expect(RegistrationFailedException.class);
		expectedException.expectMessage("KPI Service is not initialized");
		databaseMIBSerializer.registerMib(testMib);
	}
	
	@Test
	public void testRegisterMib_ShouldThrowRegistrationFailedException_WhenThisIsCalledAfterCallingStartMethod() throws IllegalAccessException, RegistrationFailedException, StartupFailedException, InitializationFailedException {
		databaseMIBSerializer.init();
		databaseMIBSerializer.start();
		
		expectedException.expect(RegistrationFailedException.class);
		expectedException.expectMessage("SNMP MIB " + testMib.getMibName() + " can not be registered, Reason: KPI Service is running");
		databaseMIBSerializer.registerMib(testMib);
	}

	@Test
	public void testRegisterMib_ShouldThrowRegistrationFailedException_WhenThisIsCalledAfterCallingStopMethod() throws IllegalAccessException, RegistrationFailedException, StartupFailedException, InitializationFailedException {
		databaseMIBSerializer.init();
		databaseMIBSerializer.start();
		databaseMIBSerializer.stop();
		
		expectedException.expect(RegistrationFailedException.class);
		expectedException.expectMessage("SNMP MIB " + testMib.getMibName() + " can not be registered, Reason: KPI Service is stopped");
		databaseMIBSerializer.registerMib(testMib);
	}
	
	@Test
	public void testRegisterMib_ShouldThrowRegistrationFailedException_WhenKPIServiceIsStartedWithoutInitializingItAndThenTriedToRegisterMIB() throws RegistrationFailedException, IllegalAccessException{
		try {
			databaseMIBSerializer.start();
		} catch (StartupFailedException e) {
		}
		expectedException.expect(RegistrationFailedException.class);
		expectedException.expectMessage("KPI Service is not initialized");
		databaseMIBSerializer.registerMib(testMib);
	}
	
	/*
	 * 
	 * Testing start() method
	 * 
	 */
	@Test
	public void testStart_ShouldThrowStartupFailedException_WhenKPISStartedBeforeInitializingIt() throws StartupFailedException {
		expectedException.expect(StartupFailedException.class);
		expectedException.expectMessage("KPI Service is not initialized");
		databaseMIBSerializer.start();
	}
	
	/*
	 * Testing stop()
	 */
	@Test
	public void testStop_ShouldReturnFalseForIsKPIServiceRunning_WhenKPIServiceIsStopped() throws RegistrationFailedException, IllegalAccessException, StartupFailedException, InitializationFailedException {
		databaseMIBSerializer.init();
		databaseMIBSerializer.start();
		databaseMIBSerializer.stop();
		assertFalse("After KPI Service is stopped, isKPIServiceRunning() must return false", databaseMIBSerializer.isKPIServiceRunning());
	}
	
	/*
	 * Testing isInitialized()
	 */
	@Test
	public void testIsInitialized_ShouldReturnTrue_WhenKPIServiceIsInitialized() throws InitializationFailedException {
		databaseMIBSerializer.init();
		boolean actualBooleanValue = databaseMIBSerializer.isInitialized();
		assertTrue("isInitialized() must return true when KPI is initialized successfully", actualBooleanValue);
	}
	
	@Test
	public void testIsInitialized_ShouldRetrunTrue_WhenKPIServiceIsStarted() throws InitializationFailedException, StartupFailedException {
		databaseMIBSerializer.init();
		databaseMIBSerializer.start();
		boolean actualBooleanValue = databaseMIBSerializer.isInitialized();
		assertTrue("isInitialized() must return true when KPI is started successfully", actualBooleanValue);
	}
	
	@Test
	public void testIsInitialized_ShouldReturnFalse_WhenKPIServiceIsStopped() throws StartupFailedException, InitializationFailedException {
		databaseMIBSerializer.init();
		databaseMIBSerializer.start();
		databaseMIBSerializer.stop();
		boolean actualBooleanValue = databaseMIBSerializer.isInitialized();
		assertFalse("isInitialized() must return false when KPI is stopped", actualBooleanValue);
	}
	
	/*
	 * Testing isKPIServiceRunning()
	 * 
	 */
	@Test
	public void testIsKPIServiceRunning_ShouldReturnTrue_WhenKPIServiceStartSuccessfully() throws RegistrationFailedException, IllegalAccessException, StartupFailedException, InitializationFailedException {
		databaseMIBSerializer.init();
		databaseMIBSerializer.start();
		assertTrue("isKPIServiceRunning() must return true KPI Service start successfully", databaseMIBSerializer.isKPIServiceRunning());
	}
	
	@Test
	public void testIsKPIServiceRunning_ShouldReturnFalse_WhenStartupFailedExceptionOccurs() throws StartupFailedException {
		expectedException.expect(StartupFailedException.class);
		databaseMIBSerializer.start();
		assertFalse("isKPIServiceRunning() must return false on StartupFailedException", databaseMIBSerializer.isKPIServiceRunning());
	}
	
	@Test
	public void testIsKPIServiceRunning_ShouldReturnFalse_WhenRegistrationFailedExceptionOccurs() throws StartupFailedException, RegistrationFailedException, IllegalAccessException {
		expectedException.expect(RegistrationFailedException.class);
		databaseMIBSerializer.registerMib(testMib);
		databaseMIBSerializer.start();
		assertFalse("isKPIServiceRunning() must return false on RegistrationFailedException", databaseMIBSerializer.isKPIServiceRunning());
	}
	
	/*
	 * calling stop() before start()
	 */
	@Test
	public void testStop_ShouldLogProperMessage_WhenStopIsCalledBeforeStop() throws InitializationFailedException {
		databaseMIBSerializer.init();
		assertLogMessage("KPI service is not yet started");
		databaseMIBSerializer.stop();
	}
	
	/*
	 * Testing restart()
	 */
	@Test
	public void testRestart_ShouldNotThrowStartupFailedException_WhenInitIsNotCalledExplicitlyOnRestart() throws StartupFailedException, InitializationFailedException {
		databaseMIBSerializer.restart();
	}

	@Test
	public void testRestart_ShouldGetInitializedImplicitly_WhenKPIServiceIsRestarted() throws StartupFailedException, InitializationFailedException {
		databaseMIBSerializer.restart();
		assertTrue("KPI Service must be initialized when it is restarted", databaseMIBSerializer.isInitialized());
	}
	
	@Test
	public void testRestart_ShouldGetKPIServiceRunning_WhenKPIServiceIsRestarted() throws StartupFailedException, InitializationFailedException {
		databaseMIBSerializer.restart();
		assertTrue("KPI Service must be running when it is restarted", databaseMIBSerializer.isKPIServiceRunning());
	}
	
	@Test
	public void testRestart_ShouldLogProperMessage_WhenKPIServiceIsNotStoppedAndRunning() throws InitializationFailedException, StartupFailedException {
		databaseMIBSerializer.init();
		databaseMIBSerializer.start();
		assertLogMessage("KPI Service is already running");
		databaseMIBSerializer.restart();
	}

	private void assertLogMessage(final String expectedString) {
		ILogger logger = mock(NullLogger.class);
		LogManager.setDefaultLogger(logger);
		doAnswer(new Answer<Void>() {
			@Override
			public Void answer(InvocationOnMock invocation) throws Throwable {
				Object[] arguments = invocation.getArguments();
				assertEquals(expectedString, arguments[1]);
				return null;
			}
		}).when(logger).info(Matchers.anyString(), Matchers.anyString());
	}
	
	/*
	 * Testing that data is dumped in database or not when start() is called
	 */
	@Test
	public void testStart_ShouldDumpData_WhenKPIServiceIsInitializedAndMIBIsRegisteredAndItIsStartedProperly() throws RegistrationFailedException, IllegalAccessException, StartupFailedException, SQLException, InitializationFailedException {
		databaseMIBSerializer.init();
		databaseMIBSerializer.registerMib(testMib);
		databaseMIBSerializer.start();
		assertQuery("There must be 1 records in kpitesting table", 1, "select count(*) from kpitesting");
		assertQuery("There must be 2 records in kpiTestingTable table", 2, "select count(*) from kpiTestingTable");
	}
	
	@Test
	public void testStart_ShouldReflectIncrementCounterValueByOneInDatabase_WhenCounterValueForKpiTestingIsIncreamented() throws RegistrationFailedException, StartupFailedException, InitializationFailedException {
		databaseMIBSerializer.init();
		databaseMIBSerializer.registerMib(testMib);
		increamentCounterBy(1);
		databaseMIBSerializer.start();
		assertQuery("totalTestCounter1 value must be 1", 1, "select totalTestCounter1 from kpiTesting");
		assertQuery("totalTestCounter2 value must be 1", 1, "select totalTestCounter2 from kpiTesting");
	}
	
	@Test
	@Parameters(value = {"1","2"})
	public void testStart_ShouldReflectIncrementCounterValueByOneInDatabase_WhenCounterValueOfKpiTestingTableTableIsIncreamentedForEntry(int kpiCounterIndex) throws RegistrationFailedException, StartupFailedException, InitializationFailedException {
		databaseMIBSerializer.init();
		databaseMIBSerializer.registerMib(testMib);
		increamentCounterBy(1);
		databaseMIBSerializer.start();
		
		assertQuery("testCounter1 value must be 1", 1, "select testCounter1 from kpiTestingTable where kpiCounterIndex = " + kpiCounterIndex);
		assertQuery("testCounter2 value must be 1", 1, "select testCounter2 from kpiTestingTable where kpiCounterIndex = " + kpiCounterIndex);
	}
	
	@Test
	public void testStart_ShouldReflectIncrementCounterValueByTwoInDatabase_WhenCounterValueIsIncreamented() throws RegistrationFailedException, StartupFailedException, InitializationFailedException {
		databaseMIBSerializer.init();
		databaseMIBSerializer.registerMib(testMib);
		increamentCounterBy(2);
		databaseMIBSerializer.start();
		assertQuery("totalTestCounter1 value must be 1", 2, "select totalTestCounter1 from kpiTesting");
		assertQuery("totalTestCounter2 value must be 1", 2, "select totalTestCounter2 from kpiTesting");
		
		assertQuery("testCounter1 value must be 1", 2, "select testCounter1 from kpiTestingTable where kpiCounterIndex = " + 1);
		assertQuery("testCounter2 value must be 1", 2, "select testCounter2 from kpiTestingTable where kpiCounterIndex = " + 1);
		assertQuery("testCounter1 value must be 1", 2, "select testCounter1 from kpiTestingTable where kpiCounterIndex = " + 2);
		assertQuery("testCounter2 value must be 1", 2, "select testCounter2 from kpiTestingTable where kpiCounterIndex = " + 2);
	}
	
	@Test
	public void testRestart_ShouldDumpData_WhenKPIServiceIsInitializedAndMIBIsRegisteredAndItIsStartedandThenRestarted() throws RegistrationFailedException, IllegalAccessException, StartupFailedException, SQLException, InitializationFailedException {
		databaseMIBSerializer.init();
		databaseMIBSerializer.registerMib(testMib);
		databaseMIBSerializer.start();
		cleanup();
		databaseMIBSerializer.stop();
		databaseMIBSerializer.restart();
		assertQuery("There must be 1 records in kpitesting table", 1, "select count(*) from kpitesting");
		assertQuery("There must be 2 records in kpiTestingTable table", 2, "select count(*) from kpiTestingTable");
	}
	
	@After
	public void cleanup() {
		try {
			Statement statement = connection.createStatement();
			statement.executeUpdate("delete from " + KPI_TESTING_TABLE);
			statement.executeUpdate("delete from " + KPI_TESTING_ENTRY_TABLE);
			connection.commit();
		} catch (SQLException e) {
			fail(e.getMessage());
		}
	}
	
	@After
	public void resetLogger() {
		LogManager.setDefaultLogger(new ConsoleLogger());
	}
	
	private void assertQuery(String failMessage, int expectedValue, String query) {
		try {
			Statement statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(query);
			int actualValue = 0;
			if(resultSet.next()) {
				actualValue = resultSet.getInt(1);
			}
			assertEquals(failMessage, expectedValue, actualValue);
		} catch (SQLException e) {
			fail(e.getMessage());
		}
	}

	class SynchronousScheduler extends Scheduler {
		@Override
		public void scheduleIntervalBasedTask(IntervalBasedTask task) {
			task.execute();
		}
		
		@Override
		public void scheduleSingleExecutionTask(SingleExecutionTask task) {
			task.execute();
		}
		
		@Override
		public void stopScheduler() {
			
		}
	}
	
	private static class TestConnectionProvider implements ConnectionProvider{

		public TestConnectionProvider() throws ClassNotFoundException {
			Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
		}

		@Override
		public Connection getConnection() throws SQLException {
			Connection conn = null;
			try {
					conn = DriverManager.getConnection("jdbc:derby:memory:TestingDB;create=true");
			} catch (SQLException e) {
				fail("Problem in creating Derby Database Connection, Reason: "+e.getMessage());
			}
			return conn;
		}
	}
	
	
	
	
	/*
	 * MIB Classes
	 */
	
	@SuppressWarnings("serial")
	public class TestMIBImpl extends TEST_MIB {
		
		KpitestingMBean kpitestingMBean;
		
		public TestMIBImpl() {
			kpitestingMBean = new KpitestingMbeanImpl();
		}
		
		@Override
		@Table(name = "kpitesting")
		protected Object createKpitestingMBean(String groupName, String groupOid, ObjectName groupObjname, MBeanServer server) {
			return kpitestingMBean;
		}
		
		@Override
		protected ObjectName getGroupObjectName(String name, String oid,
				String defaultName) throws MalformedObjectNameException {
			return new ObjectName("SnmpAgent:type=private.elitecore.radius.kpitesting,name=" + name);
		}
		
		public void setTableKpiTestingTable(TableKpiTestingTable tableKpiTestingTable) {
			((KpitestingMbeanImpl)kpitestingMBean).setTableKpiTestingTable(tableKpiTestingTable);
		}
		
		public KpitestingMBean getKpitestingMBean() {
			return kpitestingMBean;
		}
	}
	
	@SuppressWarnings("serial")
	public class KpitestingMbeanImpl extends Kpitesting {

		private AtomicLong totalTestCounter1 = new AtomicLong();
		private AtomicLong totalTestCounter2 = new AtomicLong();
		private TableKpiTestingTable tableKpiTestingTable;
		
		@Override
		@Table(name = KPI_TESTING_ENTRY_TABLE)
		public TableKpiTestingTable accessKpiTestingTable() {
			return tableKpiTestingTable;
		}

		@Override
		@Column(name = "totalTestCounter1", type = Types.BIGINT)
		public Long getTotalTestCounter1() throws SnmpStatusException {
			return totalTestCounter1.longValue();
		}
		
		@Override
		@Column(name = "totalTestCounter2", type = Types.BIGINT)
		public Long getTotalTestCounter2() throws SnmpStatusException {
			return totalTestCounter2.longValue();
		}
		
		public void incrementTotalTestCounter1() {
			totalTestCounter1.incrementAndGet();
		}
		
		public void incrementTotalTestCounter2() {
			totalTestCounter2.incrementAndGet();
		}
		
		public void setTableKpiTestingTable(TableKpiTestingTable tableKpiTestingTable) {
			this.tableKpiTestingTable = tableKpiTestingTable;
		}
	}
	
	@SuppressWarnings("serial")
	public class TableKpiTestingTableImpl extends TableKpiTestingTable {

		public TableKpiTestingTableImpl(SnmpMib myMib, MBeanServer server) {
			super(myMib, server);
		}
		
		@Override
		public synchronized void addEntry(KpiCounterEntryMBean entry, ObjectName name) throws SnmpStatusException {
			super.addEntry(entry, name);
			if(server != null){
				try {
					server.registerMBean(entry, name);
				} catch (InstanceAlreadyExistsException e) {
					e.printStackTrace();
				} catch (MBeanRegistrationException e) {
					e.printStackTrace();
				} catch (NotCompliantMBeanException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
	public class KPICounterEntryMbeanImpl implements KpiCounterEntryMBean {

		protected AtomicLong testCounter1 = new AtomicLong();
		protected AtomicLong testCounter2 = new AtomicLong();
		private String name;
		private int index;
		
		public KPICounterEntryMbeanImpl(int index, String name) {
			this.index = index;
			this.name = name;
		}
		
		@Override
		@Column(name = "testCounter1", type = Types.BIGINT)
		public Long getTestCounter1() throws SnmpStatusException {
			return testCounter1.longValue();
		}

		@Override
		@Column(name = "testCounter2", type = Types.BIGINT)
		public Long getTestCounter2() throws SnmpStatusException {
			return testCounter2.longValue();
		}

		@Override
		@Column(name = "kpiCounterIndex", type = Types.INTEGER)
		public Integer getKpiCounterIndex() throws SnmpStatusException {
			return index;
		}
		
		public void incrementTestCounter1() {
			testCounter1.incrementAndGet();
		}
		
		public void incrementTestCounter2() {
			testCounter2.incrementAndGet();
		}
	
		public String getObjectName(){
			return "SnmpAgent:type=private.elitecore.radius.kpitesting,name=" + name;
		}
		
	}
	
	private void initKpiTestMib() throws Exception {
		testMib = new TestMIBImpl();
		testMib.init();
		
		tableKpiTestingTable = new TableKpiTestingTableImpl(testMib, null);
		
		KPICounterEntryMbeanImpl kpiCounterEntryMbeanImpl = new KPICounterEntryMbeanImpl(1, "10.106.1.106");
		tableKpiTestingTable.addEntry(kpiCounterEntryMbeanImpl, new ObjectName(kpiCounterEntryMbeanImpl.getObjectName()));
		
		KPICounterEntryMbeanImpl kpiCounterEntryMbeanImpl2 = new KPICounterEntryMbeanImpl(2, "10.106.1.92");
		tableKpiTestingTable.addEntry(kpiCounterEntryMbeanImpl2, new ObjectName(kpiCounterEntryMbeanImpl2.getObjectName()));
		
		testMib.setTableKpiTestingTable(tableKpiTestingTable);
	}
	
	private void increamentCounterBy(int count) {
		for(int i=0 ;i < count ; i++) {
			((KpitestingMbeanImpl)testMib.getKpitestingMBean()).incrementTotalTestCounter1();
			((KpitestingMbeanImpl)testMib.getKpitestingMBean()).incrementTotalTestCounter2();

			KpiCounterEntryMBean[] entries = tableKpiTestingTable.getEntries();
			for (KpiCounterEntryMBean kpiCounterEntryMBean : entries) {
				((KPICounterEntryMbeanImpl)kpiCounterEntryMBean).incrementTestCounter1();
				((KPICounterEntryMbeanImpl)kpiCounterEntryMBean).incrementTestCounter2();
			}
		}
	}
	
	private static void createTables() {
		try {
			connection.prepareCall("create table kpitesting (instanceID VARCHAR(50),createtime TIMESTAMP, totalTestCounter1 decimal(20),totalTestCounter2 decimal(20))")
					.execute();
			
			connection.prepareCall("create table kpiTestingTable(instanceID VARCHAR(50),createtime TIMESTAMP, kpiCounterIndex decimal(10), testCounter1 decimal(20),testCounter2 decimal(20))")
					.execute();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	class DatabaseMIBSerializerExt extends DatabaseMIBSerializer {

		public DatabaseMIBSerializerExt(String instanceId, KpiConfiguration kpiConfig,ConnectionProvider connectionProvider) {
			super(instanceId, kpiConfig, connectionProvider);
		}

		@Override
		public Scheduler createScheduler() {
			return synchronousScheduler;
		}
	}
}
