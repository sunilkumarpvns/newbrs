package com.elitecore.commons.kpi.handler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.elitecore.commons.base.DBUtility;
import com.elitecore.commons.base.Preconditions;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.kpi.annotation.Column;
import com.elitecore.commons.kpi.annotation.Table;
import com.elitecore.commons.kpi.config.KpiConfiguration;
import com.elitecore.commons.kpi.data.TableData;
import com.elitecore.commons.kpi.exception.InitializationFailedException;
import com.elitecore.commons.kpi.exception.RegistrationFailedException;
import com.elitecore.commons.kpi.exception.StartupFailedException;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.commons.threads.EliteThreadFactory;
import com.sun.management.snmp.agent.SnmpMib;

public class DatabaseMIBSerializer implements MIBSerializer {

	private static final String MODULE = "DB_MIB_SERIALIZER";
	private KpiConfiguration kpiConfig;
	private List<SnmpMib> mibs;
	private Set<TableData> tables;
	private Map<String, String> tableToQuery;
	private InsertTask insertTask;
	private IntervalBasedTask[] batchAdder;
	private List<InsertData> insertDataList;
	private ConnectionProvider connectionProvider;
	private String instanceId;
	private Scheduler scheduler;
	private TableGenerator tableGenerator;
	private QueryGenerator queryGenerator;
	
	/*
	 * Possible KPI Service status.
	 */
	private MIBSerializerState currentState;
	
	/**
	 * Used to register SNMP MIB. MIBs that consist of service specific tables can also be registered. 
	 * Generates tables from SNMP MIBs that are registered and also generate queries for each table.
	 * Table is generated when the annotation {@link Table} is found over any method in MIB implementation 
	 * with table name specified by {@code name} attribute of {@link Table} annotation.
	 * Column name for the table is specified by {@code name} attribute of {@link Column} annotation.   
	 * It also schedules task for each table to fetch values for all the counters of MIB 
	 * and also schedule one task for dumping the counters into database.
	 * 
	 * It is also capable for restarting the KPI Service. If there are no entries during start up and after some time some entries
	 * are added then on restart those entries will be serialized otherwise it will not be serialized. 
	 * 
	 * <p>
	 * Note: Here core pool size must be greater than zero (0), otherwise it will throw IllegalArgumentException internally.
	 * </p>
	 * 
	 * @param instanceId server instance id
	 * @param kpiConfig configuration for KPI Service
	 * @param connectionProvider provider for database connection
	 * @throws NullPointerException if {@code connectionProvider} is null 
	 */
	public DatabaseMIBSerializer(String instanceId, KpiConfiguration kpiConfig, ConnectionProvider connectionProvider) {
		this.connectionProvider = Preconditions.checkNotNull(connectionProvider, "connectionProvider is null");
		this.kpiConfig = kpiConfig;
		this.instanceId = instanceId;
		this.mibs = new ArrayList<SnmpMib>();
		
		//TODO: scheduler will be come from outside and we will not create it and stop it on stop and we will just cancel the task
		//		that are submitted to the scheduler. Right Now Do not pass from outside as it is generated inside
		this.scheduler = createScheduler();
		this.currentState = MIBSerializerState.NOT_INITIALIZED;
	}
	
	/**
	 * Used just for test case
	 * 
	 * @param instanceId
	 * @param kpiConfig
	 * @param testConnectionProvider
	 * @param scheduler
	 */
	public DatabaseMIBSerializer(String instanceId, KpiConfiguration kpiConfig, ConnectionProvider testConnectionProvider, Scheduler scheduler) {
		this(instanceId,kpiConfig, testConnectionProvider);
		this.scheduler = scheduler;
	}

	/**
	 * Initialize KPI service to serialize MIB in database.
	 * 
	 * <p>
	 * NOTE: This must be called before calling {@code register()} or {@code start()} 
	 * </p>
	 */
	@Override
	public void init() throws InitializationFailedException {
		if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
			LogManager.getLogger().info(MODULE, "Initializing KPI Service");
		}

		this.tables = new HashSet<TableData>();
		this.tableToQuery = new HashMap<String, String>();
		this.insertDataList = new ArrayList<InsertData>();

		this.tableGenerator = new TableGenerator();
		this.queryGenerator = new QueryGenerator();

		this.currentState = MIBSerializerState.INITIALIZED;
		if(LogManager.getLogger().isLogLevel(LogLevel.INFO)){
			LogManager.getLogger().info(MODULE, "KPI Service initialized successfully");
		}
	}
	
	/**
	 * It register SNMP MIB so that database serializer uses this MIB for serialization process.
	 * 
	 * <p>
	 * NOTE: Do not require to re-register all the MIB that are registered before during restart of KPI service.
	 * </p>
	 * @param snmpMib snmp mib to register to the mib serializer
	 * @throws RegistrationFailedException when snmp mib passed is null, when kpi service is not initialized 
	 * or it is started or it is in shutdown in progress state or it is stopped
	 * 
	 */
	@Override
	public void registerMib(SnmpMib snmpMib) throws RegistrationFailedException {
		if(snmpMib == null) {
			throw new RegistrationFailedException("snmpMib is null");
		}
		currentState.registerMib(snmpMib, this);
	}

	/*
	 * First temporary table list is generated from table generator. 
	 * temporary because we need to give it to query generator only those tables that are generated currently and not all tables 
	 */
	private void scanMib(SnmpMib snmpMib) {
		Set<TableData> tables = new HashSet<TableData>(tableGenerator.generateTables(snmpMib));

		this.tableToQuery.putAll(queryGenerator.generateQuerys(instanceId, tables));

		this.tables.addAll(tables);

		if(LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
			LogManager.getLogger().info(MODULE, "SNMP MIB " + snmpMib.getMibName() + " processed successfully");
		}
	}
	
	private void generateTask() {
		
		if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
			LogManager.getLogger().info(MODULE, "Generating batch adding task for KPI Service");
		
		batchAdder = new AddTask[tables.size()];
		InsertData insertData = null;
		Iterator<TableData> tableIterator = tables.iterator();
		
		for(int i=0 ; tableIterator.hasNext() ; i++) {
			TableData tableData = tableIterator.next();
			insertData = new InsertData(tableToQuery.get(tableData.getTableName()), kpiConfig, tableData.getTableName());
			insertDataList.add(insertData);
			
			batchAdder[i] = new AddTask(tableData, insertData, kpiConfig);
		}
		insertTask = new InsertTask(insertDataList, kpiConfig, connectionProvider);
	}
	
	/**
	 * starts KPI service. It includes generating one task for each table for fetching values of the counters from MIB.
	 * It also generate one another task for dumping batches to the database based on the connection provided to the 
	 * database MIB serializer. 
	 * <p>
	 * Moreover, it schedules all the task generated and these task are executed at specific interval
	 * specified by {@code queryInterval} and {@code dumpInterval} in KPI configuration.
	 * </p> 
	 * <p>
	 * NOTE: Be sure that connection is provided properly otherwise there will be problem in dumping data into database.
	 * </p>
	 */
	@Override
	public void start() throws StartupFailedException {
		if(!isInitialized()) {
			LogManager.getLogger().error(MODULE, "KPI Service cannot be started, Reason: It is not initialized");
			throw new StartupFailedException("KPI Service is not initialized");
		}

		this.currentState = MIBSerializerState.STARTUP_IN_PROGRESS;
		
		generateTask();

		scheduleTask();

		this.currentState = MIBSerializerState.RUNNING;
	}

	private void scheduleTask() {
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
			LogManager.getLogger().debug(MODULE, "Scheduling task for fetching counters for " + tables.size() + " tables");
		}

		for(int i=0 ; i < batchAdder.length ; i++) {
			scheduler.scheduleIntervalBasedTask(batchAdder[i]);
		}

		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
			LogManager.getLogger().debug(MODULE, "Scheduling task to dump batches in database");
		}

		scheduler.scheduleIntervalBasedTask(insertTask);
	}

	/**
	 * Stops the KPI service. If it is not yet started then nothing will happen, so no use to stop without starting.
	 * Stopping the KPI service includes stopping the scheduler, clears the table, clears the query 
	 * and change the state of the KPI service.
	 */
	@Override
	public void stop() {
		if(!isKPIServiceRunning()) {
			LogManager.getLogger().info(MODULE, "KPI service is not yet started");
			return;
		}

		if(LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
			LogManager.getLogger().info(MODULE, "Stopping KPI Service");
		}
		this.currentState = MIBSerializerState.SHUTDOWN_IN_PROGRESS;
		scheduler.stopScheduler();
		tables.clear();
		tableToQuery.clear();
		insertDataList.clear();
		this.currentState = MIBSerializerState.STOPPED;
	}
	
	/**
	 * Restarts the KPI service. If KPI service is already running then it will not do anything.
	 * It creates the scheduler again, then initialize the KPI service and then it will scan all the MIBs 
	 * that are registered on startup.
	 * 
	 * After registering all MIB, KPI service will be started which includes generating task and scheduling the task for dumping 
	 * the data into the database.
	 * 
	 * Doing restart will include tables for the MIB's client-specific-tables that are not included during startup 
	 * as they did not have any client entries during startup.
	 */
	@Override
	public void restart() throws InitializationFailedException , StartupFailedException {
		if(isKPIServiceRunning()) {
			LogManager.getLogger().info(MODULE, "KPI Service is already running");
			return;
		}
		
		if(LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
			LogManager.getLogger().info(MODULE, "Restarting KPI Service");
		}
		
		this.scheduler = createScheduler();
		
		init();

		for (SnmpMib mib : mibs) {
			scanMib(mib);
		}

		start();
	}

	/*
	 * just for testing purpose
	 */
	public Scheduler createScheduler() {
		return new Scheduler(kpiConfig.getMaxNoOfThreads(), new EliteThreadFactory(KpiConfiguration.KPI_THREAD_KEY,KpiConfiguration.KPI_THREAD_NAME_PREFIX,Thread.NORM_PRIORITY));
	}
	
	/**
	 * Specifies if KPI service is running or not. 
	 * When KPI service is started using its start() its state will become running.
	 *  
	 * @return true  - if KPI service is running <br>
	 * 		   false - if KPI service is not running
	 */
	@Override
	public boolean isKPIServiceRunning() {
		return MIBSerializerState.RUNNING.equals(currentState);
	}

	/**
	 * Specifies that whether ot not KPI service is initialized or not.
	 * 
	 * @return
	 * 		true  - if KPI service state is INITIALIZED or RUNNING or STARTUP-IN-PROGRESS<br>
	 * 		false - if KPI service state is NOT-INITIALIZED or SHUTDOWN-IN-PROGRESS or STOPPED
	 * 
	 */
	@Override
	public boolean isInitialized() {
		return MIBSerializerState.INITIALIZED.equals(currentState) 
				|| MIBSerializerState.RUNNING.equals(currentState) 
				|| MIBSerializerState.STARTUP_IN_PROGRESS.equals(currentState);
	}
	
	enum MIBSerializerState {

		NOT_INITIALIZED("Not Initialized") {
			@Override
			void registerMib(SnmpMib snmpMib, DatabaseMIBSerializer databaseMIBSerializer) throws RegistrationFailedException {
				throw new RegistrationFailedException("SNMP MIB " + snmpMib.getMibName() + " can not be registered, Reason: KPI Service is not initialized");
			}
		},
		INITIALIZED("Initialized"){
			@Override
			void registerMib(SnmpMib snmpMib, DatabaseMIBSerializer databaseMIBSerializer) throws RegistrationFailedException {
				if(!databaseMIBSerializer.mibs.contains(snmpMib)) {
					databaseMIBSerializer.mibs.add(snmpMib);
					if(LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
						LogManager.getLogger().info(MODULE, "SNMP MIB " + snmpMib.getMibName() + " registered successfully");
					}
					databaseMIBSerializer.scanMib(snmpMib);
				}
				
			}
		},
		STARTUP_IN_PROGRESS("Startup In Progress"){
			@Override
			void registerMib(SnmpMib snmpMib, DatabaseMIBSerializer databaseMIBSerializer) throws RegistrationFailedException {
				throw new RegistrationFailedException("SNMP MIB " + snmpMib.getMibName() + " registration is closed, Reason: KPI Service is signaled for running");
			}
		},
		RUNNING("Running"){
			@Override
			void registerMib(SnmpMib snmpMib, DatabaseMIBSerializer databaseMIBSerializer) throws RegistrationFailedException {
				throw new RegistrationFailedException("SNMP MIB " + snmpMib.getMibName() + " can not be registered, Reason: KPI Service is running");
			}
		},
		SHUTDOWN_IN_PROGRESS("Shutdown In Progress"){
			@Override
			void registerMib(SnmpMib snmpMib, DatabaseMIBSerializer databaseMIBSerializer) throws RegistrationFailedException {
				throw new RegistrationFailedException("SNMP MIB " + snmpMib.getMibName() + " can not be registered, Reason: KPI Service scheduler is signaled for shutdown");
			}
		},
		STOPPED("Stopped"){
			@Override
			void registerMib(SnmpMib snmpMib, DatabaseMIBSerializer databaseMIBSerializer) throws RegistrationFailedException {
				throw new RegistrationFailedException("SNMP MIB " + snmpMib.getMibName() + " can not be registered, Reason: KPI Service is stopped");
			}
		};

		MIBSerializerState(String state) {
		}

		abstract void registerMib(SnmpMib snmpMib, DatabaseMIBSerializer databaseMIBSerializer) throws RegistrationFailedException;
	}

	@Override
	public String flush() {
		Iterator<TableData> tableIterator = tables.iterator();
		String output = "Cannot flush KPIs, Reason: Connection not available";
		Connection connection = null;
		PreparedStatement prepareStatement = null;
		try {
			connection = connectionProvider.getConnection();
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("\n+" + Strings.repeat("-", 32) + "+" + Strings.repeat("-", 9) + "+");
			stringBuilder.append("\n| " + Strings.padEnd("          Table Name", 30, ' ') + " | Result  |");
			stringBuilder.append("\n+" + Strings.repeat("-", 32) + "+" + Strings.repeat("-", 9) + "+");
			while(tableIterator.hasNext()) {
				TableData tableData = tableIterator.next();
				try {
					prepareStatement = connection.prepareStatement("TRUNCATE TABLE " + tableData.getTableName());
					prepareStatement.executeUpdate();
					stringBuilder.append("\n| " + Strings.padEnd(tableData.getTableName(), 30, ' ') + " | SUCCESS |");
				} catch (SQLException e) {
					stringBuilder.append("\n| " + Strings.padEnd(tableData.getTableName(), 30, ' ') + " | FAILED  |");
					LogManager.getLogger().warn(MODULE, "Error in truncating table: " + tableData.getTableName() + ", Reason: " + e.getMessage());
					LogManager.getLogger().trace(MODULE, e);
				}
			}
			connection.commit();
			stringBuilder.append("\n+" + Strings.repeat("-", 32) + "+" + Strings.repeat("-", 9) + "+");
			output = stringBuilder.toString();
		} catch (SQLException e) {
			LogManager.getLogger().error(MODULE, "Error in flushing KPIs, Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		} finally {
			DBUtility.closeQuietly(prepareStatement);
			DBUtility.closeQuietly(connection);
		}
		return output;
	}
}
