package com.elitecore.elitesm.ws;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.elitecore.commons.base.DBUtility;
import com.elitecore.commons.logging.ILogger;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.core.commons.util.db.DBVendors;
import com.elitecore.core.commons.util.db.DatabaseTypeNotSupportedException;
import com.elitecore.elitesm.util.EliteUtility;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.ws.exception.DatabaseConnectionException;
import com.elitecore.elitesm.ws.logger.Logger;
import com.elitecore.elitesm.ws.rest.constant.RestWSConstants;
import com.elitecore.elitesm.ws.rest.utility.DBField;
import com.elitecore.elitesm.ws.rest.utility.DBFieldList;
import com.elitecore.elitesm.ws.rest.utility.ListWrapper;
import com.elitecore.elitesm.ws.subscriber.SubscriberProfileDBConfiguration;

public abstract class BaseWebServiceBLManager {
	private static final String SO_TAKING_DEFAULT_TIMEOUT = "So taking default timeout: ";
	private static final String MODULE = "BaseWebServiceBLManager";
	protected static final String DEFAULT_KEY = "*";
	private static boolean connectionPoolingMode = true;
	private static final int DEFAULT_QUERY_TIMEOUT = 2;
	protected static final String SUBSCRIBER_WEBSERVICE = "subscriber.webservice.query.timeout.seconds";
	protected static final String SESSION_WEBSERVICE = "session.webservice.query.timeout.seconds";
	
	protected Map<String,Map<String,String>> getResult(String query,BaseDBConfiguration dbConfiguration,String inParameter) throws SQLException,DatabaseConnectionException{
		Map<String,Map<String,String>> resultData = new LinkedHashMap<String,Map<String,String>>();
		Connection conn=null;
		PreparedStatement psmt = null;
		ResultSet resultSet = null;

		try{
			long startTime = System.currentTimeMillis();
			conn = getDBConnection(dbConfiguration);
			psmt = conn.prepareStatement(query);
			
			psmt.setQueryTimeout(getQueryTimeoutFor(SESSION_WEBSERVICE));
			
			psmt.setMaxRows(dbConfiguration.getRecordFetchLimit());
			Logger.logDebug(MODULE,"queryString :"+query);
			psmt.setString(1, inParameter);
			resultSet  = psmt.executeQuery();
			ResultSetMetaData rsMetaData = resultSet.getMetaData();
			int count=rsMetaData.getColumnCount();
			int index=0;

			while(resultSet.next()){

				Map<String,String> map = new LinkedHashMap<String,String>();
				if(isDBFieldMappingExist()){
					for (int i = 1; i<=count; i++) {

						Object obj = resultSet.getObject(i);
						String strVal = null;
						if(obj!=null){
							strVal = obj.toString();

						}
						String columnName = rsMetaData.getColumnLabel(i);
						if(columnName==null){
							columnName = rsMetaData.getColumnName(i);
						}
						map.put(columnName, strVal);
					}
				}
				index++;
				resultData.put(String.valueOf(index), map);

			}
			if(!isDBFieldMappingExist()){
				Logger.logWarn(MODULE, "DB Field Mapping is not found, return empty result.");
			}
			long endTime = System.currentTimeMillis();
			Logger.logDebug(MODULE,"Total Records :"+index);
			Logger.logDebug(MODULE,"Total time :"+(endTime-startTime) + " ms.");
		}catch(SQLTimeoutException ste) {
			Logger.logInfo(MODULE, "Timeout occured for session manager web service");
		}finally{
			DBUtility.closeQuietly(resultSet);
			DBUtility.closeQuietly(psmt);
			DBUtility.closeQuietly(conn);
		}
		return resultData;
	}
	
	/**
	 * Use to get the inParameters for query.
	 * @param value
	 * @return the value to be set as in parameter for query
	 */
	protected String getCriteria(String value){
		String criteria;
		if(value!=null){
			String tempValue = value.replace('*', '%');
			criteria = tempValue;
		}else{
			criteria = " NULL";
		}
		return criteria;
	}
	
	protected static String getDriverClass(String dbUrl) throws DatabaseConnectionException {
		DBVendors dbVendors = null;
        try {
            dbVendors = DBVendors.fromUrl(dbUrl);
            return dbVendors.driverClassName;
        } catch (DatabaseTypeNotSupportedException e) {
            throw new DatabaseConnectionException("Error while fetching driver class from db url : " + dbUrl );
        }
	}
	protected static Connection getDBConnection(BaseDBConfiguration dbConfiguration) throws DatabaseConnectionException{
		if(connectionPoolingMode){
			return com.elitecore.elitesm.web.dashboard.db.DBConnectionManager.getInstance().getWebServiceDatabaseConnection(dbConfiguration);
		}else{
			try{
				Class.forName(dbConfiguration.getDriverClass()).newInstance();
				Logger.logInfo(MODULE, "Get Connection From Configuration");
				return com.elitecore.elitesm.web.dashboard.db.DBConnectionManager.getInstance().getWebServiceDatabaseConnection(dbConfiguration);
				
			}catch(ClassNotFoundException e){
				throw new DatabaseConnectionException("Driver class is not found["+dbConfiguration.getDriverClass()+"]",e);
			} catch (InstantiationException e) {
				throw new DatabaseConnectionException("Error while instantiation of driver class ["+dbConfiguration.getDriverClass()+"]",e);
			} catch (IllegalAccessException e) {
				throw new DatabaseConnectionException("Error while getting connection,"+e.getMessage(),e);
			}
		}
		
	}
	
	protected static  Map<String, String> getTableColumns(SubscriberProfileDBConfiguration dbConfiguration) throws DatabaseConnectionException{
			Connection connection = null;
			PreparedStatement prepareStatement = null;
			ResultSet resultSet = null;
			ResultSetMetaData rsMetaData = null;
			String query = new StringBuilder(26+dbConfiguration.getTableName().length()).append("SELECT * FROM ")
								.append(dbConfiguration.getTableName())
								.append(" where 1=0").toString();
			Map<String,String> columns = new HashMap<String, String>();
			try {
				connection = getDBConnection(dbConfiguration);
				prepareStatement = connection.prepareStatement(query);
				prepareStatement.setQueryTimeout(getQueryTimeoutFor(SUBSCRIBER_WEBSERVICE));
				resultSet = prepareStatement.executeQuery();
				rsMetaData = resultSet.getMetaData();
	
				for(int i=1;i<=rsMetaData.getColumnCount();i++) {
					columns.put(rsMetaData.getColumnName(i).toUpperCase(), rsMetaData.getColumnTypeName(i));
				}
				
			}catch(SQLTimeoutException ste) {
				Logger.logInfo(MODULE, "Timeout occured for fetching field mappings");
			}catch (SQLException e) {
			throw new DatabaseConnectionException(e.getMessage(),e);
			}finally {
					if(resultSet != null) {
						try {
							resultSet.close();
						} catch (SQLException e) {
							Logger.logTrace(MODULE,e.toString());
						}
					}
					if(prepareStatement != null) {
						try {
							prepareStatement.close();
						} catch (SQLException e) {
							Logger.logTrace(MODULE,e.toString());
						}
					}
					if(connection != null) {
						try {
							connection.close();
						} catch (SQLException e) {
							Logger.logTrace(MODULE,e.toString());
						}
					}
			}
			return columns;
	}
	
	protected static int getQueryTimeoutFor(String paramName) {
		int queryTimeout = DEFAULT_QUERY_TIMEOUT;
		String queryTimeoutStr = null;
		try {
			FileInputStream fileInputStream = null;

			String miscPropsFileLocation = ConfigConstant.MISC_CONFIG_FILE_LOCATION;

			Properties properties = new Properties();
			File misConfigPropsFile = new File(EliteUtility.getSMHome()+File.separator+miscPropsFileLocation);
			fileInputStream = new FileInputStream(misConfigPropsFile);
			properties.load(fileInputStream);

			queryTimeoutStr = (String)properties.get(paramName);

			if (queryTimeoutStr == null) {
				Logger.logDebug(MODULE, "query timeout is not specified for web service, " +
						SO_TAKING_DEFAULT_TIMEOUT + DEFAULT_QUERY_TIMEOUT);
				return queryTimeout;
			}

			queryTimeout = Integer.parseInt(queryTimeoutStr);
			
		} catch (NumberFormatException nfe) {
			Logger.logTrace(MODULE, nfe);
			Logger.logDebug(MODULE, "Invalid query timeout(sec) " + queryTimeoutStr + 
					" is configured for web service, " +
					SO_TAKING_DEFAULT_TIMEOUT + DEFAULT_QUERY_TIMEOUT );
		} catch(IOException e){
			Logger.logError(MODULE, "Error in getting URL from property file :"+e.getMessage()+
									SO_TAKING_DEFAULT_TIMEOUT + DEFAULT_QUERY_TIMEOUT );
		}
		return queryTimeout;
	}
	
	private static class SMLogger implements ILogger{

		@Override
		public void error(String module, String strMessage) {
			Logger.logError(module, strMessage);
		}

		@Override
		public void debug(String module, String strMessage) {
			Logger.logDebug(module, strMessage);
		}

		@Override
		public void info(String module, String strMessage) {
			Logger.logInfo(module, strMessage);
		}

		@Override
		public void warn(String module, String strMessage) {
			Logger.logWarn(module, strMessage);
		}

		@Override
		public void trace(String module, String strMessage) {
			Logger.logTrace(module, strMessage);
		}

		@Override
		public void trace(Throwable exception) {
			Logger.logTrace(MODULE, exception);
		}

		@Override
		public void trace(String module, Throwable exception) {
			Logger.logTrace(MODULE, exception);
		}

		@Override
		public int getCurrentLogLevel() {
			return Logger.getLogger().getCurrentLogLevel();
		}

		@Override
		public boolean isLogLevel(LogLevel level) {
			return Logger.getLogger().isLogLevel(level);
		}

		@Override
		public void addThreadName(String threadName) {
		}

		@Override
		public void removeThreadName(String threadName) {
		}

		@Override
		public boolean isErrorLogLevel() {
			return true;
		}

		@Override
		public boolean isWarnLogLevel() {
			return true;
		}

		@Override
		public boolean isInfoLogLevel() {
			return true;
		}

		@Override
		public boolean isDebugLogLevel() {
			return true;
		}
		
	}
	
	protected abstract boolean isDBFieldMappingExist();
	
	protected final ListWrapper<DBFieldList> getResult(BaseDBConfiguration dbConfiguration, String query, int limit, int offset)  throws SQLException, DatabaseConnectionException {
		Map<String, Map<String, String>> indexToRecordsMap = new LinkedHashMap<String, Map<String, String>>();

		if(Logger.getLogger().isLogLevel(LogLevel.DEBUG)) {
			Logger.logDebug(MODULE, "DB Query: "+query);
		}
		
		Connection  connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		
		try {
			connection = getDBConnection(dbConfiguration);
			preparedStatement = connection.prepareStatement(query);

			if(preparedStatement == null) {
				throw new DatabaseConnectionException("Prepared statement is null for connection.");
			}
			
			if(limit > 0) {
				preparedStatement.setInt(RestWSConstants.LIMIT_PARAM_INDEX, limit);
				if(offset > 0) {
					preparedStatement.setInt(RestWSConstants.OFFSET_PARAM_INDEX, offset);	
				}
			}
			
			preparedStatement.setMaxRows(dbConfiguration.getRecordFetchLimit());

			resultSet = preparedStatement.executeQuery();
			ResultSetMetaData rsMetaData = resultSet.getMetaData();
			
			int count=rsMetaData.getColumnCount();
			
			for(int recordIndex = 1; resultSet.next() ; recordIndex++) {
				
				Map<String,String> dbColumnToValueMap = new LinkedHashMap<String,String>();
			
				for (int i = 1; i<=count; i++) {
					String value = resultSet.getString(i);
					String columnName = rsMetaData.getColumnLabel(i);
					dbColumnToValueMap.put(columnName, value);
				}
				
				indexToRecordsMap.put(String.valueOf(recordIndex), dbColumnToValueMap);
			}
			
		} finally {
			DBUtility.closeQuietly(resultSet);
			DBUtility.closeQuietly(preparedStatement);
			DBUtility.closeQuietly(connection);
		}
		return getDBFieldsListWrapper(indexToRecordsMap);
	}
	
	protected final ListWrapper<DBFieldList> getDBFieldsListWrapper(Map<String, Map<String, String>> resultMap) {
		
		ListWrapper<DBFieldList> dbFieldsListWrapper = null;
		
		if (resultMap.isEmpty() == false) {
			
			dbFieldsListWrapper = new ListWrapper<DBFieldList>();
			
			List<DBFieldList> dbFieldLists = new ArrayList<DBFieldList>();
			
			for(Map<String, String> map : resultMap.values()) {
				
				List<DBField> dbFields = new ArrayList<DBField>();
				
				for(Map.Entry<String,String> entry : map.entrySet()) {
					DBField dbField = new DBField();
					dbField.setColumn(entry.getKey());
					dbField.setValue(entry.getValue());
					dbFields.add(dbField);
				}
				
				DBFieldList dbFieldList = new DBFieldList();
				dbFieldList.setDbFieldList(dbFields);
				dbFieldLists.add(dbFieldList);
			}
			dbFieldsListWrapper.setDatalist(dbFieldLists);
		}
		
		return dbFieldsListWrapper;
	}
}