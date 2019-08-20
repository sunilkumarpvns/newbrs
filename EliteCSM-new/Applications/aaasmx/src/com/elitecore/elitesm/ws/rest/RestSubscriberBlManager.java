package com.elitecore.elitesm.ws.rest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;

import com.elitecore.commons.base.DBUtility;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.elitesm.util.constants.AccountFieldConstants;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.ws.exception.DatabaseConnectionException;
import com.elitecore.elitesm.ws.logger.Logger;
import com.elitecore.elitesm.ws.rest.constant.RestWSConstants;
import com.elitecore.elitesm.ws.rest.utility.DBFieldList;
import com.elitecore.elitesm.ws.rest.utility.ListWrapper;
import com.elitecore.elitesm.ws.subscriber.SubscriberProfileWebServiceBLManager;

public class RestSubscriberBlManager extends SubscriberProfileWebServiceBLManager {

	private static final String MODULE = "REST-SUBSCRIBER-BL-MGR";

	@Override
	public final int updateSubscriber(Map<String, Object> logicalFieldNameToValue, String whereClause)throws SQLException, DatabaseConnectionException {

		int noOfUpdates=0;
		
		if(logicalFieldNameToValue == null || logicalFieldNameToValue.isEmpty()) {
			if(Logger.getLogger().isLogLevel(LogLevel.WARN)) {
				Logger.logInfo(MODULE, "Subscriber updation failed. Reason: Subscriber detail is not provided.");
			}
			return noOfUpdates;
		}
		
		if(Strings.isNullOrBlank(whereClause)) {
			if(Logger.getLogger().isLogLevel(LogLevel.INFO)) {
				Logger.logInfo(MODULE, "Subscriber updataion failed. Reason: where clause is not provided.");
			}
			return noOfUpdates;
		}

		Set<Columns> columns = new HashSet<SubscriberProfileWebServiceBLManager.Columns>();
		
		for (Columns column : getValidColumnSet(logicalFieldNameToValue)) {
			if(column.getColumnName().equalsIgnoreCase(AccountFieldConstants.LAST_MODIFIED_DATE) == false){
				columns.add(column);
			}
		}
		
		String query = generateUpdateQuery(columns, whereClause);
		
		if(Logger.getLogger().isLogLevel(LogLevel.INFO)) {
			Logger.logInfo(MODULE, "Update Query: "+query);
		}

		Connection  connection = null;
		PreparedStatement preparedStatement = null;
		PreparedStatement preparedStatementForCommit = null;
		try {
			connection = getDBConnection(getDbConfiguration());
			connection.setAutoCommit(false);
			preparedStatement = connection.prepareStatement(query);
			if(preparedStatement == null){
				throw new DatabaseConnectionException("Prepared statement is null for connection.");
			}
			int parameterIndex = 1;

			final String[] stringType = {"varchar","varchar2","char","nchar","nvarchar2"};
			final String[] numericType = {"long", "number"};
			for(Columns column : columns) {
				String columnType = column.getColumnType().toLowerCase();
				String value = (String) column.getValue();
				
				if(ArrayUtils.contains(stringType, columnType)) {
					preparedStatement.setString(parameterIndex++, value);
				}else if(ArrayUtils.contains(numericType, columnType) ) {
					preparedStatement.setLong(parameterIndex++, Long.parseLong(value));   
				}else if("timestamp".equals(columnType) || "date".equals(columnType)) {
					preparedStatement.setTimestamp(parameterIndex++, getTimestampValue(value));
				}else {
					preparedStatement.setString(parameterIndex++, value);
				}
			}
			preparedStatement.setTimestamp(parameterIndex++, new Timestamp(System.currentTimeMillis()));
			noOfUpdates = preparedStatement.executeUpdate();

			String connectionUrl = connection.getMetaData().getURL();
			
			if(connectionUrl.contains(ConfigConstant.ORACLE)){
				preparedStatementForCommit = connection.prepareStatement("COMMIT WORK WRITE NOWAIT ");
			}else if(connectionUrl.contains(ConfigConstant.POSTGRESQL)){
				preparedStatementForCommit = connection.prepareStatement("COMMIT");
			}
			
			preparedStatementForCommit.executeUpdate();	

		}catch (IllegalArgumentException e) {
			if(Logger.getLogger().isLogLevel(LogLevel.DEBUG)) {
				Logger.logDebug(MODULE, "Error in updating subscriber profile: " + e.getMessage());
			}
		} catch (ParseException e) {
			if(Logger.getLogger().isLogLevel(LogLevel.DEBUG)) {
				Logger.logDebug(MODULE, "Date should be in format: Epoch Time or yyyy-mm-dd hh:mm:ss.fffffffff or " + shortDateFormat + " or " + longDateFormat +" ,Reason: Date Parsing Exception");
			}
		} finally {
			DBUtility.closeQuietly(preparedStatement);
			DBUtility.closeQuietly(preparedStatementForCommit);
			DBUtility.closeQuietly(connection);
		}
		return noOfUpdates;
	}

	public String generateUpdateQuery(Set<Columns> columnSet, String whereClause) throws SQLException {
		StringBuilder updateQueryBuilder = new StringBuilder(RestWSConstants.STR_BUILDER_INIT_CAPACITY);

		updateQueryBuilder.append("UPDATE ").append(getDbConfiguration().getTableName()).append(" SET ");

		if(getDbConfiguration().getColumns().get(AccountFieldConstants.LAST_MODIFIED_DATE) == null) {
			throw new SQLException("Column "+AccountFieldConstants.LAST_MODIFIED_DATE+ " not exists in Database Table.");
		}
		
		Iterator<Columns> columnIterator = columnSet.iterator();
		while(columnIterator.hasNext()) {
			Columns column = columnIterator.next();
			updateQueryBuilder.append(column.getColumnName()).append("=?,");
		}

		updateQueryBuilder.append(AccountFieldConstants.LAST_MODIFIED_DATE).append("=?");
		updateQueryBuilder.append(" where ").append(whereClause);
		return updateQueryBuilder.toString();

	}

	@Override
	public final int delSubscriber(String deleteQuery) throws SQLException,DatabaseConnectionException {
		
		int noOfDeletes = 0;

		Connection  connection = null;
		PreparedStatement preparedStatement = null;
		PreparedStatement preparedStatementForCommit = null;

		try {
			connection = getDBConnection(getDbConfiguration());			
			connection.setAutoCommit(false);

			Logger.logDebug(MODULE,"Delete Query: "+deleteQuery);
			
			preparedStatement = connection.prepareStatement(deleteQuery);
		
			if(preparedStatement == null) {
				throw new DatabaseConnectionException("Prepared statement is null for connection.");
			}
		
			noOfDeletes = preparedStatement.executeUpdate();

			String connectionUrl = connection.getMetaData().getURL();
			
			if(connectionUrl.contains(ConfigConstant.ORACLE)){
				preparedStatementForCommit = connection.prepareStatement("COMMIT WORK WRITE NOWAIT ");
			}else if(connectionUrl.contains(ConfigConstant.POSTGRESQL)){
				preparedStatementForCommit = connection.prepareStatement("COMMIT");
			}
		
			preparedStatementForCommit.executeUpdate();
			
		}finally {
			DBUtility.closeQuietly(preparedStatement);
			DBUtility.closeQuietly(preparedStatementForCommit);
			DBUtility.closeQuietly(connection);
		}
		return noOfDeletes;
	}

	public ListWrapper<DBFieldList> getResult(String query, int limit, int offset) throws SQLException, DatabaseConnectionException {
		return getResult(getDbConfiguration(), query, limit, offset);
	}
	
	public String getDeleteQuery(String whereClause) {
		
		if(Strings.isNullOrBlank(whereClause)) {
			if(Logger.getLogger().isLogLevel(LogLevel.DEBUG)) {
				Logger.logDebug(MODULE, "No where clause is provided from request, so returning null.");
			}
			return null;
		}
		
		StringBuilder deleteQueryBuilder = new StringBuilder(RestWSConstants.STR_BUILDER_INIT_CAPACITY);
		deleteQueryBuilder.append("DELETE FROM ").append(getDbConfiguration().getTableName());
		deleteQueryBuilder.append(" where ").append(whereClause);
		return deleteQueryBuilder.toString();
	}
	
	public String generateSelectQuery(String whereClause) {
		
		Map<String, String> wsResponseFieldMap = getDbConfiguration().getWsResponseFieldMap();
		
		if(wsResponseFieldMap.isEmpty()) {
			if(Logger.getLogger().isLogLevel(LogLevel.DEBUG)) {
				Logger.logDebug(MODULE, "Response mapping for subscriber is empty, so returning null.");
			}
			return null;
		}
		
		if(Strings.isNullOrBlank(whereClause)) {
			if(Logger.getLogger().isLogLevel(LogLevel.DEBUG)) {
				Logger.logDebug(MODULE, "No where clause is provided from request, so returning null.");
			}
			return null;
		}
		
		StringBuilder findQueryBuilder = new StringBuilder(RestWSConstants.STR_BUILDER_INIT_CAPACITY);
		
		findQueryBuilder.append("SELECT ");
		
		if(wsResponseFieldMap.get("*") != null) {
			findQueryBuilder.append("*");
		}else {
			Iterator<Entry<String, String>> responseFieldMapIterator = wsResponseFieldMap.entrySet().iterator();
			
			while(responseFieldMapIterator.hasNext()) {
				Entry<String, String> responseFieldEntry = responseFieldMapIterator.next();
				findQueryBuilder.append(responseFieldEntry.getValue()).append(" AS ").append(responseFieldEntry.getKey());
				if(responseFieldMapIterator.hasNext()) {
					findQueryBuilder.append(",");
				}
			}
		}
	
		findQueryBuilder.append(" FROM ").append(getDbConfiguration().getTableName());
		findQueryBuilder.append(" where " + whereClause);
		return findQueryBuilder.toString();
	}
}