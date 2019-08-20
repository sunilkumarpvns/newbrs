package com.elitecore.aaa.radius.plugins.userstatistic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import com.elitecore.aaa.alert.Alerts;
import com.elitecore.aaa.core.conf.impl.DatabaseDSConfigurationImpl;
import com.elitecore.aaa.radius.plugins.core.BaseRadPlugin;
import com.elitecore.aaa.radius.plugins.userstatistic.conf.UserStatisticPostAuthPluginConfiguration;
import com.elitecore.aaa.radius.plugins.userstatistic.conf.impl.UserStatisticPostAuthPluginConfigurationImpl;
import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.radius.service.RadServiceResponse;
import com.elitecore.aaa.util.constants.AAAServerConstants;
import com.elitecore.commons.annotations.VisibleForTesting;
import com.elitecore.commons.base.DBUtility;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.plugins.PluginContext;
import com.elitecore.core.commons.plugins.data.PluginCallerIdentity;
import com.elitecore.core.commons.util.db.DBConnectionManager;
import com.elitecore.core.commons.util.db.DBVendors;
import com.elitecore.core.commons.util.db.DatabaseInitializationException;
import com.elitecore.core.commons.util.db.DatabaseTypeNotSupportedException;
import com.elitecore.core.commons.util.db.datasource.DBDataSource;
import com.elitecore.core.commons.utilx.db.TransactionFactory;
import com.elitecore.core.serverx.alert.AlertSeverity;
import com.elitecore.core.serverx.internal.tasks.AsyncTaskContext;
import com.elitecore.core.serverx.internal.tasks.base.BaseIntervalBasedTask;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.core.systemx.esix.ESCommunicator;
import com.elitecore.core.systemx.esix.ESIEventListener;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;

/**
 * This plugin is mainly used to record the user statistics of access request in DB.
 * So, it helps to keep the user logging informations. 
 * <p>
 * The plugin is only applicable in post plugin configuration of authentication service,
 * post plugin of authentication flow in radius service policy, and 
 * as a response viz. post plugin in plugin handler.
 * 
 * @author soniya
 */
public class UserStatisticPostAuthPlugin extends BaseRadPlugin<RadServiceRequest, RadServiceResponse> {
	
	private static final int MAX_BATCH_SIZE = 1000;

	private static final String MODULE = "USER-STATISTICS-PLUGIN";
	
	private static final int QUERY_TIMEOUT_ERRORCODE = 1013;

	private static final String USER_STATISTICS_ID_FIELD_NAME = "USERSTATISTICSID";

	private static final String USER_STATISTICS_ID_SEQUENCE_NAME = "SEQ_USERSTATISTICS";

	private static final String CREAT_DATE_FIELD = "CREATE_DATE";

	private static final String REQUEST_PACKET = "0";

	private static final String USER_IDENTITY = "USER_IDENTITY";

	private UserStatisticPostAuthPluginConfigurationImpl configuration;
	private String strDataSourceName;
	private ConcurrentLinkedQueue<ArrayList<String>> statisticsOfUsers;
	private String insertQuery;
	private AtomicLong transientErrorCount;
	private DatabaseDSConfigurationImpl databaseDSConfiguration;

	private DBConnectionManager connectionManager;

	public UserStatisticPostAuthPlugin (PluginContext pluginContext, 
			DatabaseDSConfigurationImpl databaseDSConfiguration, 
			UserStatisticPostAuthPluginConfigurationImpl configuration) {
		
		this(pluginContext, databaseDSConfiguration, configuration, DBConnectionManager.getInstance(configuration.getDataSourceName()));
	}
	
	 UserStatisticPostAuthPlugin (PluginContext pluginContext, 
			DatabaseDSConfigurationImpl databaseDSConfiguration, 
			UserStatisticPostAuthPluginConfigurationImpl configuration, DBConnectionManager connectionManager) {

		super(pluginContext, configuration.getPluginInfo());
		this.databaseDSConfiguration = databaseDSConfiguration;
		this.configuration = configuration;
		this.transientErrorCount = new AtomicLong();
		this.connectionManager = connectionManager;
		this.strDataSourceName = configuration.getDataSourceName();
	}


	/**
	 * 
	 * Records user statistics if packet type is Access Accept or Access Reject. Does not dump any record for Access Challenge packets.
	 */
	@Override
	public void handlePostRequest(RadServiceRequest serviceRequest,
			RadServiceResponse serviceResponse, String argument, PluginCallerIdentity callerID, ISession session) {

		if (LogManager.getLogger().isInfoLogLevel()) {
			LogManager.getLogger().info(MODULE, "Request processing started with plugin: " + this.configuration.getName());
		}

		if (serviceResponse.getPacketType() == RadiusConstants.ACCESS_CHALLENGE_MESSAGE) {
			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "User statistic post auth plugin is not applicable for Access challenge message.");
			}
			return;
		}
		addValuesToBatch(serviceRequest, serviceResponse);		
	}
	
	@SuppressWarnings("unchecked")
	private void addValuesToBatch(RadServiceRequest radServiceRequest, RadServiceResponse radServiceResponse){

		ArrayList<String> valueList = new ArrayList<String>();
		valueList.add((String)radServiceRequest.getParameter(AAAServerConstants.CUI_KEY));
		List<Map<String, Object>> dbFieldMapping = this.configuration.getDbFieldMapping();

		for (int i=0; i<dbFieldMapping.size(); i++) {

			Map<String, Object> fieldMapping = dbFieldMapping.get(i);
			List<String> iAttributeID =  (List<String>) fieldMapping.get(UserStatisticPostAuthPluginConfiguration.ATTRIBUTE_IDS);
			String packetType =  (String) fieldMapping.get(UserStatisticPostAuthPluginConfiguration.PACKET_TYPE);
			Boolean bUseDictionaryValue = Boolean.parseBoolean((String)fieldMapping.get(UserStatisticPostAuthPluginConfiguration.USE_DICTIONARY_VALUE));
			IRadiusAttribute radiusAttribute = null;
			String strValue = null;

			for (String attribute : iAttributeID) {
				if (packetType.equals(REQUEST_PACKET)) {
					radiusAttribute = radServiceRequest.getRadiusAttribute(attribute, true);
				} else {
					radiusAttribute = radServiceResponse.getRadiusAttribute(true, attribute);
				}

				if (radiusAttribute != null) {
					break;
				}
			}

			if (radiusAttribute == null) {
				strValue = (String) fieldMapping.get(UserStatisticPostAuthPluginConfiguration.DEFAULT_VALUE);
			} else {
				strValue = radiusAttribute.getStringValue(bUseDictionaryValue);
			}
			valueList.add(strValue);
		}

		statisticsOfUsers.offer(valueList);
	} 
	
	/**
	 * @throws UnsupportedOperationException if configured in pre plugin.
	 */
	@Override
	public void handlePreRequest(RadServiceRequest serviceRequest,
			RadServiceResponse serviceResponse, String argument, PluginCallerIdentity callerID, ISession session) {
		
		if (LogManager.getLogger().isWarnLogLevel()) {
			LogManager.getLogger().warn(MODULE, "User Statistic Auth Post Plugin does not applicable for pre operation.");
		}
		throw new UnsupportedOperationException("User Statistic Auth Post Plugin does not applicable for pre operation.");
	}

	@Override
	public void init() throws InitializationFailedException {

		if (LogManager.getLogger().isInfoLogLevel()) {
			LogManager.getLogger().info(MODULE, "Initializing User Statistic Post Auth Plugin: " + this.configuration.getName());
		}


		String dbName = configuration.getDataSourceName();
		DBDataSource dataSource = this.databaseDSConfiguration.getDataSourceByName(dbName);

		try {
			connectionManager.init(dataSource, getPluginContext().getServerContext().getTaskScheduler());
			insertQuery = generateInsertQuery();
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Insert Query: " + insertQuery);
			}
			
			statisticsOfUsers = new ConcurrentLinkedQueue<ArrayList<String>>();

			getPluginContext().getServerContext().getTaskScheduler().scheduleIntervalBasedTask(new BatchUpdateScheduler());

			if (LogManager.getLogger().isInfoLogLevel()) {
				LogManager.getLogger().info(MODULE, "User Statistic Post Auth Plugin: " + configuration.getName() + " initialized successfully");
			}

			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, String.valueOf(configuration));
			}
		} catch (DatabaseInitializationException ex) {
			throw new InitializationFailedException("Connection to  datasource " + strDataSourceName + " is unavailable.", ex);
		} catch (DatabaseTypeNotSupportedException ex) {
			throw new InitializationFailedException("Database type not supported for datasource: " + strDataSourceName, ex);
			
		} finally {
			
			connectionManager.getTransactionFactory().addESIEventListener(new ESIEventListener<ESCommunicator>() {

				@Override
				public void dead(ESCommunicator esCommunicator) {
					LogManager.getLogger().warn(MODULE, "Database datasource: " + strDataSourceName + " for plugin: " + configuration.getName() + ", is dead.");
					getPluginContext().getServerContext().generateSystemAlert(AlertSeverity.WARN, Alerts.DATABASEDOWN,MODULE," Database: " + strDataSourceName + " for user-statistics plugin: " + configuration.getName() + " is down", 0,configuration.getName() + "(" + strDataSourceName + ")");
				}

				@Override
				public void alive(ESCommunicator esCommunicator) {
					LogManager.getLogger().warn(MODULE, "Database datasource: " + strDataSourceName + " for plugin: " + configuration.getName() + ", is alive now.");
					getPluginContext().getServerContext().generateSystemAlert(AlertSeverity.CLEAR, Alerts.DATABASEUP,MODULE," Database: " + strDataSourceName + " for user-statistics plugin: " + configuration.getName() + " is alive", 0,configuration.getName() + "(" + strDataSourceName + ")");
				}
			});
		}
	}

	@Override
	public void reInit() throws InitializationFailedException {
	}

	private class BatchUpdateScheduler extends BaseIntervalBasedTask{

		@Override
		public void execute(AsyncTaskContext context) {
			TransactionFactory transactionFactory = connectionManager.getTransactionFactory();
			if (transactionFactory.isAlive() == false) {
				if (LogManager.getLogger().isWarnLogLevel()) {
					LogManager.getLogger().warn(MODULE, "Database datasource: " + UserStatisticPostAuthPlugin.this.strDataSourceName + " is already dead, so "
							+ statisticsOfUsers.size() + " record(s) will not be dumped.");
				}
				statisticsOfUsers.clear();
				return;
			}

			try {
				insertUserStatistics();
			} catch (Exception e) {
				LogManager.getLogger().error(MODULE, "User Statistics insert failed, reason : " + e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
			}
		}

		@Override
		public long getInterval() {
			return configuration.getBatchUpdateIntervalInMs();
		}


		@Override
		public long getInitialDelay(){
			return configuration.getBatchUpdateIntervalInMs();
		}
		
		@Override
		public TimeUnit getTimeUnit() {
			return TimeUnit.MILLISECONDS;
			
		}
		
	}

	private void insertUserStatistics() throws SQLException{
		
		Connection connection = null;
		PreparedStatement preparedStatement = null;
		try {
			connection = connectionManager.getConnection();
			preparedStatement = connection.prepareStatement(insertQuery);
			int batchSize = 0;
			while(true){
				List<String> record = this.statisticsOfUsers.poll();
				if (batchSize >= MAX_BATCH_SIZE || record == null) {
					long queryExecutionTime = 0;
					queryExecutionTime = System.currentTimeMillis();
					preparedStatement.setQueryTimeout(queryTimeoutInSecond());
					preparedStatement.executeBatch();
					if (LogManager.getLogger().isDebugLogLevel()) {
						LogManager.getLogger().debug(MODULE, "Set NOWAIT for Batch-Insert Operation");
						LogManager.getLogger().debug(MODULE, "Set BATCH for Batch-Insert Operation");
					}

					commitBatch(connection);
					
					queryExecutionTime = System.currentTimeMillis() - queryExecutionTime;
					if (this.configuration.getDbQueryTimeoutInMs() < queryExecutionTime) {
						if (LogManager.getLogger().isWarnLogLevel()) {
							LogManager.getLogger().warn(MODULE,"Batch update time getting high, - Last Batch Update time = " + queryExecutionTime + " milliseconds.");
						}
					}
					if (LogManager.getLogger().isDebugLogLevel()) {
						LogManager.getLogger().debug(MODULE,"Batch Update time: " + queryExecutionTime);
					}
					batchSize = 0;
					if (record == null) {
						break;
					}
				}

				setValuesToPreparedStatement(preparedStatement, record);
				preparedStatement.addBatch();
				batchSize++;
			}
			transientErrorCount.set(0);
		} catch (SQLException e) {
			int errorCode = e.getErrorCode();
			if (errorCode == QUERY_TIMEOUT_ERRORCODE) {
				transientErrorCount.incrementAndGet();
				if (transientErrorCount.get() >= this.configuration.getMaxQueryTimeoutCount()) {
					if (LogManager.getLogger().isWarnLogLevel()) {
						LogManager.getLogger().warn(MODULE, "Total number of query timeouts exceeded then configured max number of query timeouts, System marked as DEAD, so " + statisticsOfUsers.size() + "record(s) will not be dumped.");
					}
					statisticsOfUsers.clear();
					connectionManager.getTransactionFactory().markDead();
					transientErrorCount.set(0);
				}

			} else if (connectionManager.isDBDownSQLException(e)) {
				statisticsOfUsers.clear();
				connectionManager.getTransactionFactory().markDead();
				if (LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
					LogManager.getLogger().warn(MODULE,"Database with Data Source name "+this.strDataSourceName+" is Down, System marked as dead, Reason: " + e.getMessage() + ", so " + statisticsOfUsers.size() + " record(s) will not be dumped.");
				}
			} else {
				transientErrorCount.incrementAndGet();
				if (transientErrorCount.get() >= this.configuration.getMaxQueryTimeoutCount()) {
					if (LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
						LogManager.getLogger().warn(MODULE, "Total number of query timeouts exceeded then configured max number of query timeouts, System marked as DEAD, so " + statisticsOfUsers.size() + " record(s) will not be dumped.");
					}
					statisticsOfUsers.clear();
					connectionManager.getTransactionFactory().markDead();
					transientErrorCount.set(0);
				}

				if(LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
					LogManager.getLogger().warn(MODULE,"Unknown DB error. Reason: " + e.getMessage() + ". Rejecting the batch of statistics.");
				}
			}
			throw new SQLException(e);
		} finally {
			DBUtility.closeQuietly(preparedStatement);
			DBUtility.closeQuietly(connection);
		}
	}


	@VisibleForTesting
	void commitBatch(Connection connection) throws SQLException {
		
		
		PreparedStatement preparedStatementForCommit = null;
		try {
			if(DBVendors.ORACLE.name().equals(connectionManager.getVendor().name())){
				preparedStatementForCommit = connection.prepareStatement("COMMIT WORK WRITE NOWAIT BATCH ");
				preparedStatementForCommit.executeUpdate();
			}else{
				connection.commit();
			}
		} finally {
			DBUtility.closeQuietly(preparedStatementForCommit);
		}
	}

	private int queryTimeoutInSecond() {
		return (int) (this.configuration.getDbQueryTimeoutInMs() / 1000);
	}

	private void setValuesToPreparedStatement(PreparedStatement ps, List<String> valueList) throws SQLException {
		int paramIndex = 1;
		ps.setTimestamp(paramIndex ,new Timestamp(new Date().getTime()));
		paramIndex++;

		ps.setString(paramIndex++, valueList.get(0));

		List<Map<String, Object>> dbFieldMapping = this.configuration.getDbFieldMapping();

		for (int i=0;i<dbFieldMapping.size();i++,paramIndex++) {

			Map<String, Object> fieldMapping = dbFieldMapping.get(i);
			String strValue = valueList.get(i+1);
			String strDataType = (String) fieldMapping.get(UserStatisticPostAuthPluginConfiguration.DATA_TYPE);
			if (UserStatisticPostAuthPluginConfiguration.DATA_TYPE_DATE.equals(strDataType)) {
				ps.setTimestamp(paramIndex, new Timestamp(Long.parseLong(strValue) * 1000 ));										
			} else {
				ps.setString(paramIndex, strValue);
			}
		}
	}

	private String generateInsertQuery() {

		StringBuffer strQueryBuffer = new StringBuffer(50).append("INSERT INTO ");
		strQueryBuffer.append(this.configuration.getTableName());
		strQueryBuffer.append(" ( ");
		String strCommaSpace=", ";
		StringBuffer strBuffieldNames= new StringBuffer();
		StringBuffer strBuffieldvalues= new StringBuffer();

		strBuffieldNames.append(USER_STATISTICS_ID_FIELD_NAME);
		strBuffieldvalues.append(connectionManager.getVendor().getVendorSpecificSequenceSyntax(USER_STATISTICS_ID_SEQUENCE_NAME));
		strBuffieldNames.append(strCommaSpace);
		strBuffieldvalues.append(strCommaSpace);
		strBuffieldNames.append(CREAT_DATE_FIELD);
		strBuffieldvalues.append('?');
		strBuffieldNames.append(strCommaSpace);
		strBuffieldvalues.append(strCommaSpace);
		strBuffieldNames.append(USER_IDENTITY);
		strBuffieldvalues.append('?');

		List<Map<String, Object>> dbFieldMapping = configuration.getDbFieldMapping();

		for (int i=0;i<dbFieldMapping.size();i++) {

			Map<String, Object> fieldMapping = dbFieldMapping.get(i);
			strBuffieldNames.append(strCommaSpace);
			strBuffieldNames.append(fieldMapping.get(UserStatisticPostAuthPluginConfiguration.DB_FIELD));
			strBuffieldvalues.append(strCommaSpace);
			strBuffieldvalues.append('?');
		}

		strQueryBuffer.append(strBuffieldNames.toString());
		strQueryBuffer.append(" ) VALUES ( ");
		strQueryBuffer.append(strBuffieldvalues.toString());
		strQueryBuffer.append(" )");
		return strQueryBuffer.toString();
	}
}
