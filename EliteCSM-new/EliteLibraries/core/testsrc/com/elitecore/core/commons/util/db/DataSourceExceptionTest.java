package com.elitecore.core.commons.util.db;

import static org.junit.Assert.assertEquals;

import java.sql.SQLException;
import java.util.NoSuchElementException;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.elitecore.core.commons.util.db.errorcodes.EliteErrorCodes;
import com.elitecore.core.commons.util.db.errorcodes.OracleErrorCodes;
import com.elitecore.core.commons.util.db.errorcodes.PostgreSQLErrorCodes;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

/**
 * 
 * @author Malav Desai
 *
 */
@RunWith(JUnitParamsRunner.class)
public class DataSourceExceptionTest {
	
	public static Object[][] dataFor_testGetMessage_ShouldReturnErrorMessageInProperFormat_WhenOracleErrorCodesArePassed(){
		OracleErrorCodes[] oracleErrorCodes = OracleErrorCodes.values();
		Object[][] valueParameters = new Object[oracleErrorCodes.length][2];
		
		int i = -1;
		for (OracleErrorCodes code : oracleErrorCodes) {
			valueParameters[++i][0] = code.errorCode;
			valueParameters[i][1] = code.vendorErrorMessage;
		}
		
		return valueParameters;
	}

	@Test
	@Parameters(method = "dataFor_testGetMessage_ShouldReturnErrorMessageInProperFormat_WhenOracleErrorCodesArePassed")
	public void testGetMessage_ShouldReturnErrorMessageInProperFormat_WhenOracleErrorCodesArePassed(int errorCode, String errorMessage){
		SQLException sqlException = new SQLException(anyString(), anyString(), errorCode); 
		String expectedMessage = formatExpectedOutput(sqlException.getMessage(), errorCode, sqlException.getSQLState(), errorMessage);
		
		DataSourceException dataSourceException = DataSourceException.newException(sqlException, DBVendors.ORACLE);
		assertEquals(expectedMessage, dataSourceException.getMessage());
	}
	
	public static Object[][] dataFor_testGetMessage_ShouldReturnErrorMessageInProperFormat_WhenPostgreSqlErrorCodesArePassed(){
		PostgreSQLErrorCodes[] postgreSQLErrorCodes = PostgreSQLErrorCodes.values();
		Object[][] valueParameters = new Object[postgreSQLErrorCodes.length][2];
		
		int i = -1;
		for (PostgreSQLErrorCodes code : postgreSQLErrorCodes) {
			valueParameters[++i][0] = code.sqlState;
			valueParameters[i][1] = code.vendorErrorMessage;
		}
		
		return valueParameters;
	}

	@Test
	@Parameters(method = "dataFor_testGetMessage_ShouldReturnErrorMessageInProperFormat_WhenPostgreSqlErrorCodesArePassed")
	public void testGetMessage_ShouldReturnErrorMessageInProperFormat_WhenPostgreSqlErrorCodesArePassed(String sqlState, String vendorErrorMessage){
		SQLException sqlException = new SQLException(anyString(), sqlState, 0); 
		String expectedMessage = formatExpectedOutput(sqlException.getMessage(), 0, sqlException.getSQLState(), vendorErrorMessage);
		
		DataSourceException dataSourceException = DataSourceException.newException(sqlException, DBVendors.POSTGRESQL);
		assertEquals(expectedMessage, dataSourceException.getMessage());
	}
	
	@Test
	public void testGenerateMissingDefinesAndGetMissingDefinesErrorCode(){
		SQLException missingDefines = new SQLException(anyString(), anyString(), 17021);
		String expectedMessage = formatExpectedOutput(missingDefines.getMessage(), missingDefines.getErrorCode(), missingDefines.getSQLState());
		
		DataSourceException dataSourceException = DataSourceException.newException(missingDefines, DBVendors.ORACLE);
		assertEquals(expectedMessage, dataSourceException.getMessage());
	}
	
	@Test
	public void testGenerateNoSuchElementExceptionAndGetCustomGeneratedErrorCodeAndMessage(){
		NoSuchElementException noSuchElementException = new NoSuchElementException(anyString());
		String expectedMessage = formatExpectedOutput(EliteErrorCodes.CONNECTION_POOL_FULL_ERROR.getErrorMessage(), EliteErrorCodes.CONNECTION_POOL_FULL_ERROR.getErrorCode(), null);
		
		DataSourceException dataSourceException = DataSourceException.newException(noSuchElementException, DBVendors.ORACLE);
		assertEquals(expectedMessage, dataSourceException.getMessage());
	}
	
	@Test
	public void testGenerateExceptionAndGetCustomGeneratedErrorCodeAndExceptionMessage(){
		Exception exception = new Exception(anyString());
		String expectedMessage = formatExpectedOutput(exception.getMessage(), EliteErrorCodes.UNKNOWN_ERROR.getErrorCode(), null);
		
		DataSourceException dataSourceException = DataSourceException.newException(exception, DBVendors.ORACLE);
		assertEquals(expectedMessage, dataSourceException.getMessage());
	}
	
	private static String anyString(){
		return "AnyMessage";
	}
	
	private static String formatExpectedOutput(String exceptionMessage, int errorCode, String sqlState){
		return exceptionMessage + ", Error [Code: " + errorCode + ", SQLState: " + sqlState + "]";
	}
	
	private static String formatExpectedOutput(String exceptionMessage, int errorCode, String sqlState, String errorMessage){
		return exceptionMessage + ", Error [Code: " + errorCode + ", SQLState: " + sqlState + ", Message: " + errorMessage + "]";
	}
}
