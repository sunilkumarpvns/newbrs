package com.elitecore.commons.base;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import com.elitecore.commons.logging.ILogger;
import com.elitecore.commons.logging.LogManager;

@RunWith(JUnitParamsRunner.class)
public class DBUtilityTest {

	@Rule public ExpectedException exception = ExpectedException.none();
	
	@Test
	public void testCloseQuietyWithResultSet_ShouldNotThrowException_WhenResultSetIsNull(){
		DBUtility.closeQuietly((ResultSet)null);
	}
	
	@Test
	public void testCloseQuietyWithMultipleResultSets_ShouldNotThrowException_WhenNullIsPassed(){
		DBUtility.closeQuietly((ResultSet[])null);
	}
	
	@Test
	public void testCloseQuietyWithMultipleResultSets_ShouldSkipAllNullResultSets(){
		DBUtility.closeQuietly((ResultSet)null,(ResultSet)null);
	}
	
	@Test
	public void testCloseQuietlyWithResultSet_ShouldCloseResultSet_IfResultSetIsNonNull() throws SQLException{
		ResultSet mockResultSet = mock(ResultSet.class);
		
		DBUtility.closeQuietly(mockResultSet);
		
		verify(mockResultSet).close();
	}
	
	@Test
	public void testCloseQuietlyWithMultipleResultSets_ShouldCloseAllResultSets() throws SQLException{
		ResultSet mockResultSet1 = mock(ResultSet.class);
		ResultSet mockResultSet2 = mock(ResultSet.class);
		
		DBUtility.closeQuietly(mockResultSet1,mockResultSet2);
		
		verify(mockResultSet1).close();
		verify(mockResultSet2).close();
	}
	
	@Test
	public void testCloseQuietlyWithResultSet_ShouldEatSQLException_WhenClosingThrowsException() throws SQLException{
		ResultSet mockResultSet = mock(ResultSet.class);
		doThrow(SQLException.class).when(mockResultSet).close();
		
		DBUtility.closeQuietly(mockResultSet);
	}
	
	@Test
	public void testCloseQuietlyWithMultipleResultSets_ShouldSwallowAnySQLExceptions_WhenClosingThrowsException() throws SQLException{
		ResultSet mockResultSet1 = mock(ResultSet.class);
		ResultSet mockResultSet2 = mock(ResultSet.class);
		doThrow(SQLException.class).when(mockResultSet1).close();
		doThrow(SQLException.class).when(mockResultSet2).close();
		
		DBUtility.closeQuietly(mockResultSet1,mockResultSet2);
	}
	
	@Test
	public void testCloseQuietlyWithResultSet_ShouldLogSQLException_WhenAnyIssueInClosing() throws SQLException{
		ILogger mockLogger = setMockLogger();
		
		ResultSet mockResultSet = mock(ResultSet.class);
		doThrow(SQLException.class).when(mockResultSet).close();
		
		DBUtility.closeQuietly(mockResultSet);
		
		verify(mockLogger).trace((Throwable) anyObject());
	}
	
	@Test
	public void testCloseQuietlyWithMultipleResultSets_ShouldLogAllSQLExceptions_WhenAnyIssueInClosing() throws SQLException{
		ILogger mockLogger = setMockLogger();
		
		ResultSet mockResultSet1 = mock(ResultSet.class);
		doThrow(SQLException.class).when(mockResultSet1).close();
		ResultSet mockResultSet2 = mock(ResultSet.class);
		doThrow(SQLException.class).when(mockResultSet2).close();
		
		DBUtility.closeQuietly(mockResultSet1,mockResultSet2);
		
		verify(mockLogger, times(2)).trace((Throwable) anyObject());
	}
	
	@Test
	public void testCloseQuietlyWithMultipleResultSets_ShouldContinueClosingOtherResultSets_WhenAnyIssueInClosingOneOfThem() throws SQLException{
		ResultSet mockResultSet1 = mock(ResultSet.class);
		doThrow(SQLException.class).when(mockResultSet1).close();
		ResultSet mockResultSet2 = mock(ResultSet.class);
		
		DBUtility.closeQuietly(mockResultSet1,mockResultSet2);
		
		verify(mockResultSet1).close();
		verify(mockResultSet2).close();
	}
	
	@Test
	public void testCloseQuietyWithStatement_ShouldNotThrowException_WhenStatementIsNull(){
		DBUtility.closeQuietly((PreparedStatement)null);
	}
	
	@Test
	public void testCloseQuietyWithMultipleStatements_ShouldNotThrowException_WhenNullIsPassed(){
		DBUtility.closeQuietly((PreparedStatement[])null);
	}
	
	@Test
	public void testCloseQuietyWithMultipleStatements_ShouldSkipAllNullStatements(){
		DBUtility.closeQuietly((Statement)null,(Statement)null);
	}
	
	@Test
	public void testCloseQuietlyWithStatement_ShouldClosePreparedStatement_IfStatementIsNonNull() throws SQLException{
		PreparedStatement mockPreparedStatement = mock(PreparedStatement.class);
		
		DBUtility.closeQuietly(mockPreparedStatement);
		
		verify(mockPreparedStatement).close();
	}
	
	@Test
	public void testCloseQuietlyWithMultipleStatements_ShouldCloseAllStatements() throws SQLException{
		PreparedStatement mockPreparedStatement1 = mock(PreparedStatement.class);
		PreparedStatement mockPreparedStatement2 = mock(PreparedStatement.class);
		
		DBUtility.closeQuietly(mockPreparedStatement1,mockPreparedStatement2);
		
		verify(mockPreparedStatement1).close();
		verify(mockPreparedStatement2).close();
	}
	
	@Test
	public void testCloseQuietlyWithStatement_ShouldSwallowSQLException_WhenClosingThrowsException() throws SQLException{
		PreparedStatement mockPreparedStatement = mock(PreparedStatement.class);
		doThrow(SQLException.class).when(mockPreparedStatement).close();
		
		DBUtility.closeQuietly(mockPreparedStatement);
	}
	
	@Test
	public void testCloseQuietlyWithMultipleStatements_ShouldSwallowAnySQLException_WhenClosingThrowsException() throws SQLException{
		PreparedStatement mockPreparedStatement1 = mock(PreparedStatement.class);
		doThrow(SQLException.class).when(mockPreparedStatement1).close();
		PreparedStatement mockPreparedStatement2 = mock(PreparedStatement.class);
		doThrow(SQLException.class).when(mockPreparedStatement2).close();
		
		DBUtility.closeQuietly(mockPreparedStatement1,mockPreparedStatement2);
	}
	
	@Test
	public void testCloseQuietlyWithStatement_ShouldLogSQLException_WhenAnyIssueInClosing() throws SQLException{
		ILogger mockLogger = setMockLogger();
		
		PreparedStatement mockPreparedStatement = mock(PreparedStatement.class);
		doThrow(SQLException.class).when(mockPreparedStatement).close();
		
		DBUtility.closeQuietly(mockPreparedStatement);
		
		verify(mockLogger).trace((Throwable) anyObject());
	}
	
	@Test
	public void testCloseQuietlyWithMultipleStatements_ShouldLogAllSQLExceptions_WhenAnyIssueInClosing() throws SQLException{
		ILogger mockLogger = setMockLogger();
		
		PreparedStatement mockPreparedStatement1 = mock(PreparedStatement.class);
		doThrow(SQLException.class).when(mockPreparedStatement1).close();
		PreparedStatement mockPreparedStatement2 = mock(PreparedStatement.class);
		doThrow(SQLException.class).when(mockPreparedStatement2).close();
		
		DBUtility.closeQuietly(mockPreparedStatement1, mockPreparedStatement2);
		
		verify(mockLogger, times(2)).trace((Throwable) anyObject());
	}
	
	@Test
	public void testCloseQuietlyWithMultipleStatements_ShouldContinueClosingOtherStatements_WhenIssueInClosingAnyOfThem() throws SQLException{
		PreparedStatement mockPreparedStatement1 = mock(PreparedStatement.class);
		doThrow(SQLException.class).when(mockPreparedStatement1).close();
		PreparedStatement mockPreparedStatement2 = mock(PreparedStatement.class);
		
		DBUtility.closeQuietly(mockPreparedStatement1, mockPreparedStatement2);
		
		verify(mockPreparedStatement1).close();
		verify(mockPreparedStatement2).close();
	}
	
	
	@Test
	public void testCloseQuietyWithConnection_ShouldNotThrowException_WhenConnectionIsNull(){
		DBUtility.closeQuietly((Connection)null);
	}
	
	@Test
	public void testCloseQuietyWithMultipleConnections_ShouldNotThrowException_WhenNullIsPassed(){
		DBUtility.closeQuietly((Connection[])null);
	}
	
	@Test
	public void testCloseQuietyWithMultipleConnections_ShouldSkipAllNullConnections(){
		DBUtility.closeQuietly((Connection)null, (Connection)null);
	}
	
	@Test
	public void testCloseQuietlyWithConnection_ShouldCloseConnection_IfConnectionIsNonNull() throws SQLException{
		Connection mockConnection = mock(Connection.class);
		
		DBUtility.closeQuietly(mockConnection);
		
		verify(mockConnection).close();
	}
	
	@Test
	public void testCloseQuietlyWithMultipleConnections_ShouldCloseAllConnections() throws SQLException{
		Connection mockConnection1 = mock(Connection.class);
		Connection mockConnection2 = mock(Connection.class);
		
		DBUtility.closeQuietly(mockConnection1,mockConnection2);
		
		verify(mockConnection1).close();
		verify(mockConnection2).close();
	}
	
	@Test
	public void testCloseQuietlyWithConnection_ShouldSwallowSQLException_WhenClosingThrowsException() throws SQLException{
		Connection mockConnection = mock(Connection.class);
		doThrow(SQLException.class).when(mockConnection).close();
		
		DBUtility.closeQuietly(mockConnection);
	}
	
	@Test
	public void testCloseQuietlyWithMultipleConnections_ShouldSwallowAllSQLExceptions_WhenClosingThrowsException() throws SQLException{
		Connection mockConnection1 = mock(Connection.class);
		doThrow(SQLException.class).when(mockConnection1).close();
		Connection mockConnection2 = mock(Connection.class);
		doThrow(SQLException.class).when(mockConnection2).close();
		
		DBUtility.closeQuietly(mockConnection1, mockConnection2);
	}
	
	@Test
	public void testCloseQuietlyWithConnection_ShouldLogSQLException_WhenAnyIssueInClosing() throws SQLException{
		ILogger mockLogger = setMockLogger();
		
		Connection mockConnection = mock(Connection.class);
		doThrow(SQLException.class).when(mockConnection).close();
		
		DBUtility.closeQuietly(mockConnection);
		
		verify(mockLogger).trace((Throwable) anyObject());
	}
	
	@Test
	public void testCloseQuietlyWithMultipleConnections_ShouldLogAllSQLExceptions_WhenAnyIssueInClosing() throws SQLException{
		ILogger mockLogger = setMockLogger();
		
		Connection mockConnection1 = mock(Connection.class);
		doThrow(SQLException.class).when(mockConnection1).close();
		Connection mockConnection2 = mock(Connection.class);
		doThrow(SQLException.class).when(mockConnection2).close();
		
		DBUtility.closeQuietly(mockConnection1, mockConnection2);
		
		verify(mockLogger, times(2)).trace((Throwable) anyObject());
	}
	
	@Test
	public void testCloseQuietlyWithMultipleConnections_ShouldContinueClosingOtherConnections_IfAnyIssueInClosingOneOfThem() throws SQLException{
		Connection mockConnection1 = mock(Connection.class);
		doThrow(SQLException.class).when(mockConnection1).close();
		Connection mockConnection2 = mock(Connection.class);
		
		DBUtility.closeQuietly(mockConnection1, mockConnection2);
		
		verify(mockConnection1).close();
		verify(mockConnection2).close();
	}
	
	@Test
	public void testIsValueAvailable_ShouldThrowNullPointerException_IfColumnNameIsNull() throws SQLException{
		exception.expect(NullPointerException.class);
		exception.expectMessage("column is null");
		
		DBUtility.isValueAvailable(mock(ResultSet.class), (String)null);
	}
	
	@Test
	public void testIsValueAvailable_ShouldThrowNullPointerException_IfResultSetIsNull() throws SQLException{
		exception.expect(NullPointerException.class);
		exception.expectMessage("resultSet is null");
		
		DBUtility.isValueAvailable(null, "");
	}
	
	@Test
	@Parameters(method = "dataFor_testIsValueAvailable")
	public void testIsValueAvailable(String columnName, String columnValue, boolean expectedResult) throws SQLException{
		ResultSet mockResultSet = mock(ResultSet.class);
		when(mockResultSet.getString(columnName)).thenReturn(columnValue);
		
		boolean actualResult = DBUtility.isValueAvailable(mockResultSet, columnName);
		
		assertEquals(expectedResult, actualResult);
	}
	
	public Object[][] dataFor_testIsValueAvailable() {
		return new Object[][] {
				{"any", 	null, 	false},
				{"any",		"", 	false},
				{"any", 	" ",	false},
				{"any",		"\t",	false},
				{"any",		"any",	true}
		};
	}
	
	@Test
	public void testIsValueUnavailable_ShouldThrowNullPointerException_IfColumnNameIsNull() throws SQLException{
		exception.expect(NullPointerException.class);
		exception.expectMessage("column is null");
		
		DBUtility.isValueUnavailable(mock(ResultSet.class), (String)null);
	}
	
	@Test
	public void testIsValueUnavailable_ShouldThrowNullPointerException_IfResultSetIsNull() throws SQLException{
		exception.expect(NullPointerException.class);
		exception.expectMessage("resultSet is null");
		
		DBUtility.isValueUnavailable(null, "");
	}
	
	@Test
	@Parameters(method = "dataFor_testIsValueUnavailable")
	public void testIsValueUnavailable(String columnName, String columnValue, boolean expectedResult) throws SQLException{
		ResultSet mockResultSet = mock(ResultSet.class);
		when(mockResultSet.getString(columnName)).thenReturn(columnValue);
		
		boolean actualResult = DBUtility.isValueUnavailable(mockResultSet, columnName);
		
		assertEquals(expectedResult, actualResult);
	}
	
	public Object[][] dataFor_testIsValueUnavailable() {
		return new Object[][] {
				{"any", 	null, 	true},
				{"any",		"", 	true},
				{"any", 	" ",	true},
				{"any",		"\t",	true},
				{"any",		"any",	false}
		};
	}
	
	private ILogger setMockLogger(){
		ILogger mockLogger = mock(ILogger.class);
		LogManager.setDefaultLogger(mockLogger);
		return mockLogger;
	}
}
