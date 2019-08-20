package com.elitecore.core.commons.util.db.errorcodes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 
 * @author Malav Desai
 *
 */
@RunWith(JUnitParamsRunner.class)
public class PostgreSQLErrorCodesTest {

	public static Object[][] dataFor_testPostgreSQLErrorCodesValues(){
		return new Object[][]{
				{PostgreSQLErrorCodes.PSQL_8000, "08000", "connection_exception", true},
				{PostgreSQLErrorCodes.PSQL_8003, "08003", "connection_does_not_exist",true},
				{PostgreSQLErrorCodes.PSQL_8006, "08006", "connection_failure", true},
				{PostgreSQLErrorCodes.PSQL_8001, "08001", "sqlclient_unable_to_establish_sqlconnection", true},
				{PostgreSQLErrorCodes.PSQL_8004, "08004", "sqlserver_rejected_establishment_of_sqlconnection", true},
				{PostgreSQLErrorCodes.PSQL_8007, "08007", "transaction_resolution_unknown", true},
				{PostgreSQLErrorCodes.PSQL_8P01, "08P01", "protocol_violation", true},	
		};
	}
	
	@Test
	@Parameters(method="dataFor_testPostgreSQLErrorCodesValues")
	public static void testPostgreSQLErrorCodesValues(PostgreSQLErrorCodes code, String expectedSqlState, String expectedMessage, boolean expectedIsDBDownError){
		assertEquals(expectedIsDBDownError, code.isDBDownError);
		assertEquals(expectedMessage, code.vendorErrorMessage);
		assertEquals(expectedSqlState, code.sqlState);
	}
	
	public static Object[][] dataFor_testIsDBDownSQLState(){
		return new Object[][]{
				{"08000", true},
				{"08003", true},
				{"08006", true},
				{"08001", true},
				{"08004", true},
				{"08007", true},
				{"08P01", true},
		};
	}
	
	@Test
	@Parameters(method="dataFor_testIsDBDownSQLState")
	public static void testIsDBDownSQLState( String sqlState, boolean expectedIsDBDownError){
		assertEquals(expectedIsDBDownError, PostgreSQLErrorCodes.isDBDownSQLState(sqlState));
	}
	
	@Test
	public static void testIsDBDownSQLState_ShouldReturnFalse_WhenUnknownSQLStateIsPassed(){
		assertFalse(PostgreSQLErrorCodes.isDBDownSQLState(unknownSqlState()));
	}
	
	public static Object[][] dataFor_testGetVendorErrorMessageFromSQLState(){
		return new Object[][]{
				{"08000", "connection_exception"},
				{"08003", "connection_does_not_exist"},
				{"08006", "connection_failure"},
				{"08001", "sqlclient_unable_to_establish_sqlconnection"},
				{"08004", "sqlserver_rejected_establishment_of_sqlconnection"},
				{"08007", "transaction_resolution_unknown"},
				{"08P01", "protocol_violation"},
				{unknownSqlState(), null},
		};
	}
	
	@Test
	@Parameters(method="dataFor_testGetVendorErrorMessageFromSQLState")
	public static void testGetVendorErrorMessageFromSQLState( String sqlState, String expectedMessage){
		assertEquals(expectedMessage, PostgreSQLErrorCodes.getVendorErrorMessageFromSQLState(sqlState));
	}
	
	@Test
	public static void testGetVendorErrorMessageFromSQLState_ShouldReturnNULL_WhenUnknownSQLStateIsPassed(){
		assertEquals(null, PostgreSQLErrorCodes.getVendorErrorMessageFromSQLState(unknownSqlState()));
	}
	
	public static Object[][] dataFor_testFromSQLState(){
		return new Object[][]{
				{PostgreSQLErrorCodes.PSQL_8000, "08000"},
				{PostgreSQLErrorCodes.PSQL_8003, "08003"},
				{PostgreSQLErrorCodes.PSQL_8006, "08006"},
				{PostgreSQLErrorCodes.PSQL_8001, "08001"},
				{PostgreSQLErrorCodes.PSQL_8004, "08004"},
				{PostgreSQLErrorCodes.PSQL_8007, "08007"},
				{PostgreSQLErrorCodes.PSQL_8P01, "08P01"},
				{null, unknownSqlState()},
		};
	}
	
	@Test
	@Parameters(method="dataFor_testFromSQLState")
	public static void testFromSQLState( PostgreSQLErrorCodes expectedCodes, String sqlState){
		assertEquals(expectedCodes, PostgreSQLErrorCodes.fromSQLState(sqlState));
	}
	
	public static String unknownSqlState(){
		return "anyString";
	}
}
