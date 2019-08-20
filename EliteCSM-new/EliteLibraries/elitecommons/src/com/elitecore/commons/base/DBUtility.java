package com.elitecore.commons.base;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.elitecore.commons.logging.LogManager;

/**
 * A set of DB related utilities such as closing resources quietly.
 * 
 * @author narendra.pathai
 *
 */
public class DBUtility {

	/**
	 * Closes the result set, ignoring (by logging) any exception that occurs while closing.
	 * <p>Does nothing when {@code resultSet} is {@code null}.
	 * 
	 * @param resultSet result-set to be closed
	 */
	public static void closeQuietly(ResultSet resultSet) {
		if(resultSet == null){
			return;
		}

		try {
			resultSet.close();
		} catch (SQLException e) {
			LogManager.getLogger().trace(e);
		}
	}
	
	/**
	 * Closes all result sets, ignoring (by logging) any exceptions that occur while closing.
	 * <p>Skips any {@code null ResultSet}s encountered. 
	 * 
	 * @param resultSets all the result-sets to be closed, does nothing if {@code null}
	 */
	public static void closeQuietly(ResultSet... resultSets) {
		if(resultSets == null){
			return;
		}

		for (ResultSet resultSet : resultSets) {
			closeQuietly(resultSet);
		}
	}

	/**
	 * Closes the statement, ignoring (by logging) any exception that occurs while closing.
	 * <p>Does nothing when {@code statement} is {@code null}.
	 * 
	 * @param statement statement to be closed
	 */
	public static void closeQuietly(Statement statement) {
		if (statement == null) {
			return;
		}

		try {
			statement.close();
		} catch (SQLException e) {
			LogManager.getLogger().trace(e);
		}
	}
	
	/**
	 * Closes all statements, ignoring (by logging) any exceptions that occur while closing.
	 * <p>Skips any {@code null Statement}s encountered.
	 * 
	 * @param statements all the statements to be closed, does nothing if {@code null}
	 */
	public static void closeQuietly(Statement... statements) {
		if (statements == null) {
			return;
		}
		
		for (Statement statement : statements) {
			closeQuietly(statement);
		}
	}

	/**
	 * Closes the connection, ignoring (by logging) any exception that occurs while closing.
	 * <p>Does nothing when {@code connection} is {@code null}.
	 * 
	 * @param connection connection to be closed
	 */
	public static void closeQuietly(Connection connection) {
		if (connection == null) {
			return;
		}

		try {
			connection.close();
		} catch (SQLException e) {
			LogManager.getLogger().trace(e);
		}
	}

	/**
	 * Closes all the connections, ignoring (by logging) any exceptions that occur while closing.
	 * <p>Skips any {@code null Connection}s encountered.
	 * 
	 * @param connections all the connections to be closed, does nothing if {@code null}
	 */
	public static void closeQuietly(Connection... connections) {
		if (connections == null) {
			return;
		}

		for (Connection connection : connections) {
			closeQuietly(connection);
		}
	}

	

	/**
	 * Checks if the result set contains value for the column provided and returns 
	 * false if it is a <i>non-blank</i> value and returns true if value is {@code null} or
	 * blank. 
	 * 
	 * <p>Uses {@link ResultSet#getString(String)} to fetch the value of column.
	 * 
	 * <p>NOTE: Trims the column value retrieved from result set to check if it is blank.
	 * 
	 * @see Strings#isNullOrBlank(String)
	 *  
	 * @param resultSet non-null result set from which the value of column is to be retrieved
	 * @param column non-null name of the column
	 * @return true if value is null or blank, false if some value is present
	 * @throws SQLException if there is issue in fetching value of column from result set
	 */
	public static boolean isValueUnavailable(ResultSet resultSet, String column) throws SQLException {
		Preconditions.checkNotNull(resultSet, "resultSet is null");
		Preconditions.checkNotNull(column, "column is null");

		return Strings.isNullOrBlank(resultSet.getString(column));
	}

	/**
	 * Checks if the result set contains value for the column provided and returns 
	 * true if it is a <i>non-blank</i> value and returns false if value is {@code null} or
	 * blank. 
	 * 
	 * <p>Uses {@link ResultSet#getString(String)} to fetch the value of column.
	 * 
	 * <p>NOTE: Trims the column value retrieved from result set to check if it is blank.
	 * 
	 * @see Strings#isNullOrBlank(String)
	 *  
	 * @param resultSet non-null result set from which the value of column is to be retrieved
	 * @param column non-null name of the column
	 * @return true if value is available, false otherwise
	 * @throws SQLException if there is issue in fetching value of column from result set
	 */
	public static boolean isValueAvailable(ResultSet resultSet, String column) throws SQLException{
		return !isValueUnavailable(resultSet, column);
	}
}
