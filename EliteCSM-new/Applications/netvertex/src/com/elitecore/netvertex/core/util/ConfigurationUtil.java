package com.elitecore.netvertex.core.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.RowSet;

import com.elitecore.commons.base.DBUtility;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.util.constants.BaseConfConstant;
import com.elitecore.core.commons.util.db.DataSourceException;
import com.elitecore.netvertex.core.NetVertexDBConnectionManager;
import com.sun.rowset.CachedRowSetImpl;

/**
 * This class provides utilities for configuration.  
 * @author Jay Trivedi
 *
 */
public class ConfigurationUtil {

	private static final String MODULE = "CONF-UTIL";

	/**
	 * Provide a rowset which contains a offline result of query.
	 * 
	 * @param query : select query
	 * @return RowSet : which contains a offline result of query 
	 * @throws DataSourceException : occurs while fetching connection
	 * @throws SQLException : occurs while executing query
	 * 
	 * @see RowSet
	 */
	public static RowSet newRowSet(String query) throws DataSourceException,SQLException {
		
		Statement statement = null;
		ResultSet resultSet = null;
		Connection connection = NetVertexDBConnectionManager.getInstance().getConnection();
		try {
			
			statement = connection.createStatement();
			statement.setQueryTimeout(BaseConfConstant.QUERY_TIMEOUT_SEC);
			resultSet = statement.executeQuery(query);
			
			// creating cached row set
			CachedRowSetImpl  cachedRowSet = new CachedRowSetImpl();
			cachedRowSet.populate(resultSet);
			return cachedRowSet;
		} finally {
			DBUtility.closeQuietly(resultSet);
			DBUtility.closeQuietly(statement);
			DBUtility.closeQuietly(connection);
			
		}
		
	}
	
	/**
	 * 
	 * Provide a rowset of query which having string arguments.
	 * @param query : select query
	 * @param args : string arguments for query
	 * @return RowSet : which contains a offline result of query 
	 * @throws DataSourceException : occurs while fetching connection
	 * @throws SQLException : occurs while executing query
	 */
	public static RowSet newRowSet(String query, String...args) throws DataSourceException,SQLException {
		
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		Connection connection = NetVertexDBConnectionManager.getInstance().getConnection();
		try {
			statement = connection.prepareStatement(query);
			int index = 1;
			for (String arg : args) {
				statement.setString(index++, arg);
			}
			statement.setQueryTimeout(BaseConfConstant.QUERY_TIMEOUT_SEC);
			resultSet = statement.executeQuery();
			CachedRowSetImpl  cachedRowSet = new CachedRowSetImpl();
			cachedRowSet.populate(resultSet);
			return cachedRowSet;
		} finally {
			DBUtility.closeQuietly(resultSet);
			DBUtility.closeQuietly(statement);
			DBUtility.closeQuietly(connection);
			
		}
		
	}
	
	public static boolean stringToBoolean(String parameterName, String originalString, boolean defaultValue) {
		boolean resultValue = defaultValue;
		if(originalString == null || originalString.trim().isEmpty() == true) {
			LogManager.getLogger().warn(MODULE, "Considering default value " + defaultValue + " for " + parameterName + ". Reason: Configuration value not specified" );
			return defaultValue;
		}
			resultValue = Boolean.parseBoolean(originalString.trim());
		return resultValue;
	}
	
	public static int stringToInteger(String parameterName, String originalString, int defaultValue) {
		if(originalString == null || originalString.trim().isEmpty() == true) {
			LogManager.getLogger().warn(MODULE, "Considering default value " + defaultValue + " for " + parameterName + ". Reason: Configuration value not specified" );
			return defaultValue;
		}
		int resultValue = defaultValue;
		try{
				resultValue = Integer.parseInt(originalString.trim());
		}catch (Exception e) {
			LogManager.getLogger().trace(MODULE, e);
			LogManager.getLogger().warn(MODULE, "Considering default value " + defaultValue + " for " + parameterName + ". Reason: Invalid configured value " + originalString);
		}
		return resultValue;
		
	}
	
	public static int stringToInteger(String parameterName, String originalString, int defaultValue, int minValue, int maxValue) {
		if(originalString == null || originalString.trim().isEmpty() == true) {
			LogManager.getLogger().warn(MODULE, "Considering default value " + defaultValue + " for " + parameterName + ". Reason: Configuration value not specified" );
			return defaultValue;
		}
		int resultValue = defaultValue;
		try{
				resultValue = Integer.parseInt(originalString.trim());
			if(resultValue < minValue || resultValue > maxValue){
				LogManager.getLogger().warn(MODULE, "Considering default value " + defaultValue + " for " + parameterName + ". Reason: Invalid configured value " + originalString);
				resultValue = defaultValue ; 
			}
		}catch (Exception e) {
			LogManager.getLogger().trace(MODULE, e);
			LogManager.getLogger().warn(MODULE, "Considering default value " + defaultValue + " for " + parameterName + ". Reason: Invalid configured value " + originalString);
		}
		return resultValue;

	}
	
	public static long stringToLong(String parameterName,String originalString, long defaultValue) {
		if(originalString == null || originalString.trim().isEmpty() == true) {
			LogManager.getLogger().warn(MODULE, "Considering default value " + defaultValue + " for " + parameterName + ". Reason: Configuration value not specified" );
			return defaultValue;
		}
		long resultValue = defaultValue;
		try{
				resultValue = Long.parseLong(originalString.trim());
		}catch (Exception e) {
			LogManager.getLogger().trace(MODULE, e);
			LogManager.getLogger().warn(MODULE, "Considering default value " + defaultValue + " for " + parameterName + ". Reason: Invalid configured value " + originalString);
		}
		return resultValue;

	}
	
}
