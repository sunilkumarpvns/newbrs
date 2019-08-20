package com.elitecore.core.commons.utilx.db;

import static com.elitecore.commons.logging.LogManager.ignoreTrace;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.elitecore.commons.base.DBUtility;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.util.db.DBConnectionManager;
import com.elitecore.core.commons.util.db.DataSourceException;
import com.elitecore.core.commons.util.db.DatabaseInitializationException;
import com.elitecore.core.commons.util.db.datasource.DBDataSource;
import com.elitecore.core.serverx.internal.tasks.AsyncTaskContext;
import com.elitecore.core.serverx.internal.tasks.SingleExecutionAsyncTask;
import com.elitecore.core.systemx.esix.ESCommunicator;
import com.elitecore.core.systemx.esix.ESCommunicatorImpl;
import com.elitecore.core.systemx.esix.TaskScheduler;

public class TransactionFactory extends ESCommunicatorImpl {

	private DBDataSource dataSource;
	private static final String MODULE = "TRAN-FACTORY";
	private List<DSStatusListener> dsSatusListeners;
	private int statusCheckDuration;
	private TaskScheduler taskSchedular;
	private DBConnectionManager dbConnectionManager;
	
	
	/**
	 * @param dataSource dataSource using which Transaction was created
	 * @param serviceContext ServiceContext
	 */
	public TransactionFactory(DBDataSource dataSource, TaskScheduler taskScheduler) {
		this(dataSource, taskScheduler, ESCommunicator.NO_SCANNER_THREAD);
	}
	
	/**
	 * @param dataSource dataSource using which Transaction was created
	 * @param taskScheduler 
	 * @param statusCheckDuration Interval duration for checking datasouce connection if DS is down 
	 * @see DSStatusListener
	 */
	public TransactionFactory(DBDataSource dataSource, TaskScheduler taskScheduler, int statusCheckDuration) {
		this(dataSource, taskScheduler, statusCheckDuration, DBConnectionManager.getInstance(dataSource.getDataSourceName()));
	}
	
	/**
	 * @param dataSource dataSource using which Transaction was created
	 * @param taskScheduler 
	 * @param statusCheckDuration Interval duration for checking datasouce connection if DS is down 
	 * @see DSStatusListener
	 */
	public TransactionFactory(DBDataSource dataSource, TaskScheduler taskScheduler, int statusCheckDuration,
			DBConnectionManager dbConnectionManager) {
		super(taskScheduler);
		this.taskSchedular = taskScheduler;
		this.dataSource = dataSource;
		this.dbConnectionManager = dbConnectionManager;
		this.dsSatusListeners = new ArrayList<DSStatusListener>();
		this.statusCheckDuration = statusCheckDuration;
	}
	
	
	public Transaction createTransaction(){
		return new UpdateOperationImpl() {
			@Override
			protected Connection getConnection() throws DataSourceException {
				return dbConnectionManager.getConnection();
			}

			/**
			 * markDB as Dead and send indication to DSStatusListener
			 */
			@Override
			public void markDead() {
				TransactionFactory.this.markDead();
			}
			
			public boolean isDBDownSQLException(SQLException ex){
				return dbConnectionManager.isDBDownSQLException(ex);
			}
		};
	}
	
	public DBTransaction createReadOnlyTransaction() {
		return new ReadOnlyTransaction() {
			@Override
			protected Connection getConnection() throws DataSourceException {
				return DBConnectionManager.getInstance(dataSource.getDataSourceName()).getConnection();
			}

			@Override
			public void markDead() {
				TransactionFactory.this.markDead();
			}

			@Override
			public boolean isDBDownSQLException(SQLException ex) {
				return DBConnectionManager.getInstance(dataSource.getDataSourceName()).isDBDownSQLException(ex);
			}
		};
	}
	/**
	 * scanner check for Database connection if DB is dead And markDS alive if Connection is found
	 */
	@Override
	public void scan(){

		Connection conn = null;
		PreparedStatement prepareStatement = null;
		if (isDatabaseAvailable()) {
			markAlive();
			return;
		}
		try {
			dbConnectionManager.reInit();
			conn = dbConnectionManager.getConnection();
			markAlive();
		} catch (DataSourceException e) {
			if(dbConnectionManager.isDBDownSQLException(e)){
				markDead();
			}
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
				LogManager.getLogger().warn(MODULE, "Error while checking for aliveness of datasource: " + 
						dataSource.getDataSourceName() + ", Reason: " + e.getMessage());
			}
			LogManager.getLogger().trace(MODULE, e);
		} catch (DatabaseInitializationException e) {
			markDead();
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN)){
				LogManager.getLogger().warn(MODULE, "Error while checking for aliveness of datasource: " + 
						dataSource.getDataSourceName() + ", Reason: " + e.getMessage());
			}
			LogManager.getLogger().trace(MODULE, e);
		} finally {
			DBUtility.closeQuietly(prepareStatement);
			DBUtility.closeQuietly(conn);
		}
	}
	
	private boolean isDatabaseAvailable() {
		
		Connection conn = null;
		PreparedStatement prepareStatement = null;
		ResultSet resultSet = null;
		try {
			conn =  dbConnectionManager.getConnection();
			prepareStatement = conn.prepareStatement(dbConnectionManager.getValidationQuery());
			resultSet = prepareStatement.executeQuery();
			return true;
		} catch (DataSourceException e) { 
			// already logged in getConnection();
			ignoreTrace(e);
		} catch (SQLException e) {
			if(LogManager.getLogger().isDebugLogLevel()){
				LogManager.getLogger().debug(MODULE, "Error while checking for aliveness of datasource: " + 
						dataSource.getDataSourceName() + ", Reason: " + e.getMessage());
			}
			LogManager.getLogger().trace(e);
		} finally {
			DBUtility.closeQuietly(resultSet);
			DBUtility.closeQuietly(prepareStatement);
			DBUtility.closeQuietly(conn);
		}
		return false;
	}

	public void registerDSStatusListener(DSStatusListener dsSatusListener){
		dsSatusListeners.add(dsSatusListener);
	}

	@Override
	protected int getStatusCheckDuration() {
		return statusCheckDuration;
	}
	
	
	public DBDataSource getDataSource(){
		return dataSource;
	}
	
	@Override
	public String getName() {
		return dataSource.getDataSourceName();
	}
	
	@Override
	public String getTypeName() {
		//returning the dummy values for this module
		return MODULE;
	}
	@Override
	protected boolean checkForFallback() {
		taskSchedular.scheduleSingleExecutionTask(
				new SingleExecutionAsyncTask() {
			
				@Override
				public TimeUnit getTimeUnit() {
					return TimeUnit.SECONDS;
				}
				
				@Override
				public long getInitialDelay() {
					return 0;
				}
				
				@Override
				public void execute(AsyncTaskContext context) {
					TransactionFactory.this.scan();
				}
		});
		
		return false;
	}
	
}
