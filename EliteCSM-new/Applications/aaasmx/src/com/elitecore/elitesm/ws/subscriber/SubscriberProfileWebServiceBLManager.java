package com.elitecore.elitesm.ws.subscriber;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLTimeoutException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;

import com.elitecore.commons.base.DBUtility;
import com.elitecore.elitesm.blmanager.wsconfig.WebServiceConfigBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.datasource.database.data.DatabaseDSData;
import com.elitecore.elitesm.datamanager.wsconfig.data.IWSConfigData;
import com.elitecore.elitesm.datamanager.wsconfig.data.WSKeyMappingData;
import com.elitecore.elitesm.util.constants.AccountFieldConstants;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.web.core.system.cache.ConfigManager;
import com.elitecore.elitesm.ws.BaseWebServiceBLManager;
import com.elitecore.elitesm.ws.exception.DatabaseConnectionException;
import com.elitecore.elitesm.ws.exception.SubscriberProfileWebServiceException;
import com.elitecore.elitesm.ws.logger.Logger;

public class SubscriberProfileWebServiceBLManager extends BaseWebServiceBLManager {

	private static final String COLUMN = "Column ";
	private static final String COMMIT_WORK_WRITE_NOWAIT = "COMMIT WORK WRITE NOWAIT ";
	private static final String COMMIT = "COMMIT";
	private static final String PREPARED_STATEMENT_OBJECT_IS_NULL = "Prepared Statement Object is Null";
	private static final String PREPAREDSTATEMENT_IS_NULL_WHILE_GETTING_ACCOUNTDATA = "Preparedstatement is null.while getting accountdata";
	private static final String DATABASE_CONNECTION_OBJECT_IS_NULL = "Database Connection Object is Null";
	private static final String CONNECTION_TO_DB_FAILED_WHILE_GETTING_ACCOUNTDATA = "Connection to db Failed.while getting accountdata";
	private static SubscriberProfileDBConfiguration dbConfiguration;
	private static final String  MODULE="SubscriberProfileWebServiceBLManager";
	private static String selectQuery;
	private static String deleteQuery;
	protected static String shortDateFormat = ConfigManager.get(ConfigConstant.SHORT_DATE_FORMAT);
	protected static String longDateFormat = ConfigManager.get(ConfigConstant.DATE_FORMAT);
	private static boolean isDBFieldMapExist = false;

	static{
		setConfiguration();
	}
	public static void setConfiguration(){
		try{
			dbConfiguration = getDBConfiguration();
			dbConfiguration.setColumns(getTableColumns(dbConfiguration));
		}catch(DataManagerException e){
			Logger.logError(MODULE, "Error in initializing database configuration");
			Logger.logTrace(MODULE, e);
		}catch(Exception e){
			Logger.logError(MODULE, "Error in initializing database configuration");
			Logger.logTrace(MODULE, e);
		}
		try{
			setQuery(dbConfiguration);
		}catch(SubscriberProfileWebServiceException e){
			Logger.logError(MODULE, "Error in initializing query");
			Logger.logTrace(MODULE, e);
		}catch(Exception e){
			Logger.logError(MODULE, "Error in initializing query");
			Logger.logTrace(MODULE, e);
		}
	}

	private static SubscriberProfileDBConfiguration getDBConfiguration() throws DataManagerException, DatabaseConnectionException{


		WebServiceConfigBLManager configBLManager = new WebServiceConfigBLManager();

		IWSConfigData subscriberDBConfigData = configBLManager.getSubscriberConfiguration();
		DatabaseDSData datasource = subscriberDBConfigData.getDatasourceConfigInstance();
		String dbUrl = datasource.getConnectionUrl().trim();
		String dbUser = datasource.getUserName().trim();
		String dbPassword = datasource.getPassword().trim();
		String dbTable = subscriberDBConfigData.getTableName().trim();
		String userIdentityField = subscriberDBConfigData.getUserIdentityFieldName().trim();
		Integer recordFetchLimit = subscriberDBConfigData.getRecordFetchLimit();
		String primaryKeyColumn = subscriberDBConfigData.getPrimaryKeyColumn();
		String sequenceName = subscriberDBConfigData.getSequenceName();

		String driverClass = getDriverClass(dbUrl);
		Logger.logInfo(MODULE, "Database Url       :"+dbUrl
				+ "\n\tDatabase Class     :"+driverClass
				+ "\n\tDatabase Username  :"+dbUser
				+ "\n\tDatabase Tablename :"+dbTable
				+ "\n\tRecord Fetch Limit :"+recordFetchLimit);

		SubscriberProfileDBConfiguration dbConfiguration = new SubscriberProfileDBConfiguration(driverClass,dbUrl,dbUser,dbPassword,dbTable,userIdentityField,primaryKeyColumn,sequenceName);
		dbConfiguration.setTableName(dbTable);

		if(datasource.getMaximumPool() > 0 && datasource.getMinimumPool()>0 && datasource.getMaximumPool()> datasource.getMinimumPool()){
			dbConfiguration.setMaximumPool(datasource.getMaximumPool());
			dbConfiguration.setMaxIdle(datasource.getMinimumPool());
		}


		if(recordFetchLimit!=null){
			dbConfiguration.setRecordFetchLimit(recordFetchLimit);
		}
		Logger.logDebug(MODULE, "dbConfiguration :"+dbConfiguration);

		Set<WSKeyMappingData> wsKeyMappingSet = subscriberDBConfigData.getWsKeyMappingSet();
		Map<String, String> wsRequestFieldMap = new HashMap<String, String>();
		Map<String, String> wsResponseFieldMap = new HashMap<String, String>();
		for(WSKeyMappingData wsKeyMappingData : wsKeyMappingSet) {
			if("true".equalsIgnoreCase(wsKeyMappingData.getRequest())) {
				wsRequestFieldMap.put(wsKeyMappingData.getWsKey(), wsKeyMappingData.getDbField());
			}
			if("true".equalsIgnoreCase(wsKeyMappingData.getResponse())) {
				wsResponseFieldMap.put(wsKeyMappingData.getWsKey(), wsKeyMappingData.getDbField());
			}
		}
		dbConfiguration.setWsRequestFieldMap(wsRequestFieldMap);
		dbConfiguration.setWsResponseFieldMap(wsResponseFieldMap);

		Logger.logDebug(MODULE, "Request ADD Field Map :"+dbConfiguration.getWsRequestFieldMap());
		Logger.logDebug(MODULE, "Response ADD Field Map :"+dbConfiguration.getWsResponseFieldMap());
		return dbConfiguration;
	}

	private static void setQuery(SubscriberProfileDBConfiguration dbConfiguration) throws SubscriberProfileWebServiceException{
		if(dbConfiguration!=null){
			selectQuery = getSelectQuery();
			deleteQuery = getDeleteQuery();
		}else{
			throw new SubscriberProfileWebServiceException("Database Configuration is not found.");
		}
	}

	private static String getSelectQuery(){
		Map<String, String> wsResponseFieldMap = dbConfiguration.getWsResponseFieldMap();
		StringBuilder findQueryBuilder = new StringBuilder((wsResponseFieldMap.size()*15)+30);
		if(wsResponseFieldMap.isEmpty()){
			return null;
		}
		findQueryBuilder.append("SELECT ");
		if(wsResponseFieldMap.get("*") != null) {
			findQueryBuilder.append("*");
		}else{
			Iterator<Entry<String, String>> responseFieldMapIterator = wsResponseFieldMap.entrySet().iterator();
			while(responseFieldMapIterator.hasNext()){
				Entry<String, String> responseFieldEntry = responseFieldMapIterator.next();
				findQueryBuilder.append(responseFieldEntry.getValue()).append(" AS ").append(responseFieldEntry.getKey());
				if(responseFieldMapIterator.hasNext()){
					findQueryBuilder.append(",");
				}
			}
		}

		findQueryBuilder.append(" FROM ")
		.append(dbConfiguration.getTableName())
		.append(" WHERE ")
		.append("lower("+dbConfiguration.getUserIdentityFieldName()+")").append(" = ").append("?");
		return findQueryBuilder.toString();
	}
	private static String getDeleteQuery(){
		StringBuilder deleteQueryBuilder = new StringBuilder(dbConfiguration.getTableName().length()+30);
		deleteQueryBuilder.append("DELETE FROM ").append(dbConfiguration.getTableName());
		deleteQueryBuilder.append(" where ").append(dbConfiguration.getUserIdentityFieldName()).append(" = ").append("?");
		return deleteQueryBuilder.toString();
	}

	public Map<String,Map<String,String>> findByUserIdentity(String userIdentity)  throws SQLException, SubscriberProfileWebServiceException,DatabaseConnectionException{
		Map<String,Map<String,String>> resultData = new LinkedHashMap<String,Map<String,String>>();
		if(selectQuery == null){
			Logger.logInfo(MODULE, "No Response Key Mapped.Empty Response send");
			return resultData;
		}
		Logger.logInfo(MODULE, "SELECT Query:"+selectQuery);
		Connection  connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try{
			connection = getDBConnection(dbConfiguration);
			if(connection == null){
				Logger.logDebug(MODULE,CONNECTION_TO_DB_FAILED_WHILE_GETTING_ACCOUNTDATA);
				throw new DatabaseConnectionException(DATABASE_CONNECTION_OBJECT_IS_NULL);
			}

			preparedStatement = connection.prepareStatement(selectQuery);
			if(preparedStatement == null){
				Logger.logDebug(MODULE,PREPAREDSTATEMENT_IS_NULL_WHILE_GETTING_ACCOUNTDATA);
				throw new DatabaseConnectionException(PREPARED_STATEMENT_OBJECT_IS_NULL);
			}

			preparedStatement.setString(1, userIdentity);
			preparedStatement.setMaxRows(dbConfiguration.getRecordFetchLimit());

			preparedStatement.setQueryTimeout(getQueryTimeoutFor(SUBSCRIBER_WEBSERVICE));

			preparedStatement.setQueryTimeout(getQueryTimeoutFor(SUBSCRIBER_WEBSERVICE));

			resultSet = preparedStatement.executeQuery();

			ResultSetMetaData rsMetaData = resultSet.getMetaData();
			int count=rsMetaData.getColumnCount();
			int index=1;
			while(resultSet.next()){
				Map<String,String> map = new LinkedHashMap<String,String>();
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
				resultData.put(String.valueOf(index++), map);
			}
		} catch(SQLTimeoutException ste) {
			Logger.logWarn(MODULE, "Timeout occured for user identity: " + userIdentity);
		} finally{
			DBUtility.closeQuietly(resultSet);
			DBUtility.closeQuietly(preparedStatement);
			DBUtility.closeQuietly(connection);
		}
		return resultData;
	}

	public int addSubscriber(Map<String, Object> subscriberProfileDataMap) throws SQLException,SubscriberProfileWebServiceException,DatabaseConnectionException {
		int noOfInserts=0;

		if(subscriberProfileDataMap!=null && !subscriberProfileDataMap.isEmpty()){
			Set<Columns> columns = getValidColumnSet(subscriberProfileDataMap);
			String query = getInsertQuery(columns);
			Logger.logInfo(MODULE, "Insert Query:"+query);

			Connection  connection = null;
			PreparedStatement preparedStatement = null;
			PreparedStatement preparedStatementForCommit = null;
			try{
				connection = getDBConnection(dbConfiguration);
				if(connection == null){
					Logger.logDebug(MODULE,CONNECTION_TO_DB_FAILED_WHILE_GETTING_ACCOUNTDATA);
					throw new DatabaseConnectionException(DATABASE_CONNECTION_OBJECT_IS_NULL);
				}

				preparedStatement = connection.prepareStatement(query);
				if(preparedStatement == null){
					Logger.logDebug(MODULE,PREPAREDSTATEMENT_IS_NULL_WHILE_GETTING_ACCOUNTDATA);
					throw new DatabaseConnectionException(PREPARED_STATEMENT_OBJECT_IS_NULL);
				}
				preparedStatement.setQueryTimeout(getQueryTimeoutFor(SUBSCRIBER_WEBSERVICE));

				int parameterIndex = 1;

				for(Columns column : columns){
					String columnType = column.getColumnType().toUpperCase();
					String[] stringType = {"VARCHAR","VARCHAR2","CHAR","NCHAR","NVARCHAR2"};
					String[] numericType = {"Long", "NUMBER", "NUMERIC"};
					String value = null;
					if(column.getValue() instanceof java.util.GregorianCalendar){
						GregorianCalendar cal = (GregorianCalendar) column.getValue();
						value = String.valueOf(cal.getTimeInMillis());
					}else{
						value = (String) column.getValue();
					}
					if(ArrayUtils.contains(stringType, columnType)){
						preparedStatement.setString(parameterIndex++, value);
					}else if(ArrayUtils.contains(numericType, columnType) ){
						preparedStatement.setLong(parameterIndex++, getLongValue(value));   
					}else if("timestamp".equalsIgnoreCase(columnType) || "date".equalsIgnoreCase(columnType) || "calendar".equalsIgnoreCase(columnType)){
						preparedStatement.setTimestamp(parameterIndex++, getTimestampValue(value));
					}else {
						preparedStatement.setString(parameterIndex++, value);
					}
				}
				noOfInserts = preparedStatement.executeUpdate();
				
				if( connection.getMetaData().getURL().contains(ConfigConstant.ORACLE)) {
					preparedStatementForCommit = connection.prepareStatement(COMMIT_WORK_WRITE_NOWAIT);
					preparedStatementForCommit.executeUpdate();
				}else if( connection.getMetaData().getURL().contains(ConfigConstant.POSTGRESQL) ) {
					preparedStatementForCommit = connection.prepareStatement(COMMIT);
					preparedStatementForCommit.executeUpdate();
				}
				
			}catch (IllegalArgumentException e) {
				Logger.logWarn(MODULE, "Error in caching profile: " + e.getMessage());
			} catch (ParseException e) {
				throw new SubscriberProfileWebServiceException("Date should be in format: Epoch Time or yyyy-mm-dd hh:mm:ss.fffffffff or " + shortDateFormat + " or " + longDateFormat,"Date Parsing Exception");
			}catch(SQLTimeoutException ste) {
				Logger.logInfo(MODULE, "Timeout occured for adding subscriber");
			}finally{
				DBUtility.closeQuietly(preparedStatement);
				DBUtility.closeQuietly(preparedStatementForCommit);
				DBUtility.closeQuietly(connection);
			}
		}
		return noOfInserts;
	}	


	public int updateSubscriber(Map<String,Object> subscriberProfileDataMap, String userIdentity) throws SQLException, SubscriberProfileWebServiceException,DatabaseConnectionException {
		int noOfUpdates=0;
		if(subscriberProfileDataMap!=null && !subscriberProfileDataMap.isEmpty() && userIdentity!=null && userIdentity.trim().length()>0){
			Set<Columns> columns = getValidColumnSet(subscriberProfileDataMap);
			String query = getUpdateQuery(columns);
			Logger.logInfo(MODULE, "Update Query:"+query);

			Connection  connection = null;
			PreparedStatement preparedStatement = null;
			PreparedStatement preparedStatementForCommit = null;
			try{
				connection = getDBConnection(dbConfiguration);
				if(connection == null){
					Logger.logDebug(MODULE,CONNECTION_TO_DB_FAILED_WHILE_GETTING_ACCOUNTDATA);
					throw new DatabaseConnectionException(DATABASE_CONNECTION_OBJECT_IS_NULL);
				}
				connection.setAutoCommit(false);
				preparedStatement = connection.prepareStatement(query);
				if(preparedStatement == null){
					Logger.logDebug(MODULE,PREPAREDSTATEMENT_IS_NULL_WHILE_GETTING_ACCOUNTDATA);
					throw new DatabaseConnectionException(PREPARED_STATEMENT_OBJECT_IS_NULL);
				}
				preparedStatement.setQueryTimeout(getQueryTimeoutFor(SUBSCRIBER_WEBSERVICE));
				int parameterIndex = 1;

				for(Columns column : columns){
					String columnType = column.getColumnType().toLowerCase();
					String[] stringType = {"VARCHAR","VARCHAR2","CHAR","NCHAR","NVARCHAR2"};
					String[] numericType = {"Long", "NUMBER"};
					String value = (String) column.getValue();

					if(ArrayUtils.contains(stringType, columnType)){
						preparedStatement.setString(parameterIndex++, value);
					}else if(ArrayUtils.contains(numericType, columnType) ){
						preparedStatement.setLong(parameterIndex++, getLongValue(value));   
					}else if("timestamp".equals(columnType) || "date".equals(columnType)){
						preparedStatement.setTimestamp(parameterIndex++, getTimestampValue(value));
					}else {
						preparedStatement.setString(parameterIndex++, value);
					}
				}
				preparedStatement.setTimestamp(parameterIndex++, new Timestamp(System.currentTimeMillis()));
				preparedStatement.setString(parameterIndex++, userIdentity);
				noOfUpdates = preparedStatement.executeUpdate();
				preparedStatementForCommit = connection.prepareStatement(COMMIT_WORK_WRITE_NOWAIT);
				preparedStatementForCommit.executeUpdate();	
			}catch(SQLTimeoutException ste) {
				Logger.logInfo(MODULE, "Timeout occured for updating subscriber: " + userIdentity);
			}catch (IllegalArgumentException e) {
				Logger.logWarn(MODULE, "Error in caching profile: " + e.getMessage());
			} catch (ParseException e) {
				throw new SubscriberProfileWebServiceException("Date should be in format: Epoch Time or yyyy-mm-dd hh:mm:ss.fffffffff or " + shortDateFormat + " or " + longDateFormat,"Date Parsing Exception");
			} finally{
				DBUtility.closeQuietly(preparedStatement);
				DBUtility.closeQuietly(preparedStatementForCommit);
				DBUtility.closeQuietly(connection);
			}
			DBUtility.closeQuietly(preparedStatementForCommit);
			if(connection != null)
				try {
					connection.close();
				} catch (SQLException e) {
					Logger.logTrace(MODULE,e.toString());
				}
		}
		else{
			throw new SubscriberProfileWebServiceException("Subscriber Profile Map or User-Identity not found","Invalid Arguments");
		}	
		return noOfUpdates;
	} 



	public int delSubscriber(String userIdentity) throws SQLException, SubscriberProfileWebServiceException,DatabaseConnectionException {
		Logger.logDebug(MODULE,"queryString :"+deleteQuery);
		int noOfDeletes = 0;

		Connection  connection = null;
		PreparedStatement preparedStatement = null;
		PreparedStatement preparedStatementForCommit = null;
		try{
			connection = getDBConnection(dbConfiguration);			
			if(connection == null){
				Logger.logDebug(MODULE,CONNECTION_TO_DB_FAILED_WHILE_GETTING_ACCOUNTDATA);
				throw new DatabaseConnectionException(DATABASE_CONNECTION_OBJECT_IS_NULL);
			}

			connection.setAutoCommit(false);

			preparedStatement = connection.prepareStatement(deleteQuery);
			if(preparedStatement == null){
				Logger.logDebug(MODULE,PREPAREDSTATEMENT_IS_NULL_WHILE_GETTING_ACCOUNTDATA);
				throw new DatabaseConnectionException(PREPARED_STATEMENT_OBJECT_IS_NULL);
			}
			preparedStatement.setQueryTimeout(getQueryTimeoutFor(SUBSCRIBER_WEBSERVICE));
			preparedStatement.setString(1, userIdentity);
			noOfDeletes = preparedStatement.executeUpdate();

			preparedStatementForCommit = connection.prepareStatement(COMMIT_WORK_WRITE_NOWAIT);
			preparedStatementForCommit.executeUpdate();			

		} catch(SQLTimeoutException ste) {
			Logger.logInfo(MODULE, "Timeout occured for deleting useridentity: " + userIdentity);
		}finally{
			DBUtility.closeQuietly(preparedStatement);
			DBUtility.closeQuietly(preparedStatementForCommit);
			DBUtility.closeQuietly(connection);
		}
		return noOfDeletes;
	}


	private  String getInsertQuery(Set<Columns> columnSet) {
		StringBuilder insertQueryBuilder = new StringBuilder((columnSet.size()*15)+dbConfiguration.getTableName().length()+16);
		insertQueryBuilder.append("INSERT INTO ")
		.append(dbConfiguration.getTableName())
		.append(" ( ");
		StringBuilder valueBuilder = new StringBuilder(columnSet.size()+16);
		valueBuilder.append(" VALUES ( ");

		/*check Primary Key*/
		if(dbConfiguration.getSequenceName() != null && dbConfiguration.getSequenceName().trim().length()>0 && dbConfiguration.getPrimaryKeyColumn()!=null && dbConfiguration.getPrimaryKeyColumn().trim().length()>0){
			Map<String,String> tableColumns =  dbConfiguration.getColumns();
			String primaryKey = dbConfiguration.getPrimaryKeyColumn().toUpperCase();
			if(tableColumns.get(primaryKey)!= null){
				insertQueryBuilder.append(primaryKey).append(",");
				
				if( dbConfiguration.getConnectionUrl().contains(ConfigConstant.ORACLE)) {
					valueBuilder.append(dbConfiguration.getSequenceName().trim()).append(".nextval,");
				}else if( dbConfiguration.getConnectionUrl().contains(ConfigConstant.POSTGRESQL)) {
					valueBuilder.append("nextval('" + dbConfiguration.getSequenceName().trim()).append("'),");
				}
				
			}
		}

		Iterator<Columns> columnIterator = columnSet.iterator();
		while(columnIterator.hasNext()){
			Columns column = columnIterator.next();
			insertQueryBuilder.append(column.getColumnName());
			valueBuilder.append("?");
			if(columnIterator.hasNext()){
				insertQueryBuilder.append(",");
				valueBuilder.append(",");
			}
		}
		return insertQueryBuilder.append(" )").append(valueBuilder.append(" ) ")).toString();

	}

	private  String getUpdateQuery(Set<Columns> columnSet) {
		StringBuilder updateQueryBuilder = new StringBuilder((columnSet.size()*15)+dbConfiguration.getTableName().length()+16);
		updateQueryBuilder.append("UPDATE ")
		.append(dbConfiguration.getTableName())
		.append(" SET ");

		Iterator<Columns> columnIterator = columnSet.iterator();
		while(columnIterator.hasNext()){
			Columns column = columnIterator.next();
			updateQueryBuilder.append(column.getColumnName()).append("=?,");
		}    
		if(dbConfiguration.getColumns().get(AccountFieldConstants.LAST_MODIFIED_DATE) == null){
			throw new SubscriberProfileWebServiceException(COLUMN+AccountFieldConstants.LAST_MODIFIED_DATE+ " not exists in Database Table.");
		}
		updateQueryBuilder.append(AccountFieldConstants.LAST_MODIFIED_DATE).append("=?");
		updateQueryBuilder.append(" where ").append(dbConfiguration.getUserIdentityFieldName()).append(" = ").append("?");
		return updateQueryBuilder.toString();

	}

	protected Set<Columns> getValidColumnSet(Map<String, Object> subscriberProfileDataMap) {
		Set<Columns> columnSet = new LinkedHashSet<SubscriberProfileWebServiceBLManager.Columns>(subscriberProfileDataMap.size());
		Iterator<Entry<String, Object>> subscriberMapIterator = subscriberProfileDataMap.entrySet().iterator();

		Map<String, String> wsRequestFieldMap = dbConfiguration.getWsRequestFieldMap();
		Map<String, String> tableColumns = dbConfiguration.getColumns();
		boolean isDefaultKeyExists=  (wsRequestFieldMap.get(DEFAULT_KEY) != null ) ? true : false;

		while(subscriberMapIterator.hasNext()){
			Entry<String, Object> entry =  subscriberMapIterator.next();
			String wsKey =  entry.getKey();
			if(wsKey != null && wsKey.length() > 0){
				wsKey = wsKey.trim();
			}
			String mapDbField = wsRequestFieldMap.get(wsKey) ;
			if(mapDbField != null ){
				mapDbField = mapDbField.toUpperCase();
				String columnType = tableColumns.get(mapDbField);
				if(columnType != null) {
					Columns column = new Columns(mapDbField, columnType, entry.getValue());
					columnSet.add(column);
				} else {
					throw new SubscriberProfileWebServiceException(COLUMN+mapDbField+ " not exists in Database Table.");
				}
			}else if(isDefaultKeyExists){
				wsKey = wsKey.toUpperCase();
				String columnType = tableColumns.get(wsKey);
				if(columnType != null) {
					Columns column = new Columns(wsKey, columnType, entry.getValue());
					columnSet.add(column);	
				}else {
					throw new SubscriberProfileWebServiceException(COLUMN+wsKey+ " not exists in Database Table");
				}
			} else {
				throw new SubscriberProfileWebServiceException("Ws Key "+wsKey+ " not configured in web service mapping.");
			}
		}
		return columnSet;
	}

	private long getLongValue(String value){
		try{
			return Long.parseLong(value);
		}catch (NumberFormatException e) {
			return 0;
		}
	}
	
	protected Timestamp getTimestampValue(String value) throws ParseException {
		Date date = null;
		Timestamp dateToTimestamp = null;
		SimpleDateFormat simpleDateFormat = null;
		Long longExpDate;
		if(value!=null){				
			try{
				longExpDate = Long.parseLong(value);
				dateToTimestamp = new Timestamp(longExpDate);
			}catch(NumberFormatException e){
				try{
					dateToTimestamp = Timestamp.valueOf(value);
				}catch (IllegalArgumentException ilLegalArgExp) {
					simpleDateFormat = new SimpleDateFormat(longDateFormat);
					try{
						date = simpleDateFormat.parse(value);
						dateToTimestamp = new Timestamp(date.getTime());
					}catch(ParseException psEx){
						simpleDateFormat = new SimpleDateFormat(shortDateFormat);
						date = simpleDateFormat.parse(value);
						dateToTimestamp = new Timestamp(date.getTime());
					}
				}
			}
		}
		return dateToTimestamp;	
	}

	public static void main(String args[]){
		System.out.println("Before Created");
		/*% java org.apache.axis.wsdl.Java2WSDL -o SubscriberProfileWS.wsdl
	    -l"http://localhost:8080/axis/services/SessionManagerWS"
	    -n  "urn:SubscriberProfileWS" -p"com.elitecore.elitesm.ws" "urn:SubscriberProfileWS"
	    com.elitecore.elitesm.ws.SubscriberProfileWS*/
		org.apache.axis.wsdl.Java2WSDL.main(args);
		System.out.println("Successfully Created");

	}

	@Override
	protected boolean isDBFieldMappingExist() {
		return isDBFieldMapExist;
	}

	protected class Columns {
		private String columnName;
		private String columnType;
		private Object value;
		public Columns(String columnName, String columnType, Object object) {
			this.columnName = columnName;
			this.columnType = columnType;
			this.value = object;
		}
		public String getColumnName() {
			return columnName;
		}
		public String getColumnType() {
			return columnType;
		}
		public Object getValue() {
			return value;
		}
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result
					+ ((columnName == null) ? 0 : columnName.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			Columns other = (Columns) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (columnName == null) {
				if (other.columnName != null)
					return false;
			} else if (!columnName.equals(other.columnName))
				return false;
			return true;
		}
		private SubscriberProfileWebServiceBLManager getOuterType() {
			return SubscriberProfileWebServiceBLManager.this;
		}

	}

	public final SubscriberProfileDBConfiguration getDbConfiguration() {
		return dbConfiguration;
	}
}
