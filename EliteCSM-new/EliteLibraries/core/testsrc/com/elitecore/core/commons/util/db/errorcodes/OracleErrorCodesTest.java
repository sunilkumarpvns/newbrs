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
public class OracleErrorCodesTest {
	
	public static Object[][] dataFor_testOracleErrorCodesValues(){
		return new Object[][]{
				{OracleErrorCodes.ORA_1033, 1033, "ORACLE initialization or shutdown in progress", true},
				{OracleErrorCodes.ORA_1034, 1034, "ORACLE not available", true},
				{OracleErrorCodes.ORA_1089, 1089, "immediate shutdown in progress - no operations are permitted", true},
				{OracleErrorCodes.ORA_12514, 12514, "TNS:listener does not currently know of service requested in connect descriptor", true},
				{OracleErrorCodes.ORA_12528, 12528, "TNS:listener: all appropriate instances are blocking new connections", true},
				{OracleErrorCodes.ORA_17002, 17002, "Io exception", true},
				{OracleErrorCodes.ORA_17410, 17410, "No more data to read from socket",true},
				{OracleErrorCodes.ORA_17447, 17447, "OALL8 is in an inconsistent state", false},
				{OracleErrorCodes.ORA_3113, 3113, "end-of-file on communication channel", true},
		};
	}

	@Test
	@Parameters(method="dataFor_testOracleErrorCodesValues")
	public void testOracleErrorCodesValues(OracleErrorCodes code, int expectedErrorCode, String expectedMessage, boolean expectedIsDBDownError){
		assertEquals(expectedErrorCode, code.errorCode);
		assertEquals(expectedIsDBDownError, code.isDBDownError);
		assertEquals(expectedMessage, code.vendorErrorMessage);
	}
	
	public static Object[][] dataFor_testIsDBDownErrorCode(){
		return new Object[][]{
				{1033, true},
				{1034, true},
				{1089, true},
				{12514, true},
				{12528, true},
				{17002, true},
				{17410, true},
				{17447, false},
				{3113, true},
		};
	}
	
	@Test
	@Parameters(method="dataFor_testIsDBDownErrorCode")
	public void testIsDBDownErrorCode(int errorCode, boolean expectedIsDBDownError){
		assertEquals(expectedIsDBDownError, OracleErrorCodes.isDBDownErrorCode(errorCode));
	}
	
	@Test
	public void testIsDBDownErrorCode_ShouldReturnFalse_WhenUnknownErrorCodeIsPassed(){
		assertFalse(OracleErrorCodes.isDBDownErrorCode(unknownErrorCode()));
	}
	
	public static Object[][] dataFor_testGetVendorErrorMessageFromErrorCode(){
		return new Object[][]{
				{1033, "ORACLE initialization or shutdown in progress"},
				{1034, "ORACLE not available"},
				{1089, "immediate shutdown in progress - no operations are permitted"},
				{12514, "TNS:listener does not currently know of service requested in connect descriptor"},
				{12528, "TNS:listener: all appropriate instances are blocking new connections"},
				{17002, "Io exception"},
				{17410, "No more data to read from socket"},
				{17447, "OALL8 is in an inconsistent state"},
				{3113, "end-of-file on communication channel"},
		};
	}
	
	@Test
	@Parameters(method="dataFor_testGetVendorErrorMessageFromErrorCode")
	public void testGetVendorErrorMessageFromErrorCode(int errorCode, String expectedMessage){
		assertEquals(expectedMessage, OracleErrorCodes.getVendorErrorMessageFromErrorCode(errorCode));
	}
	
	@Test
	public void testGetVendorErrorMessageFromErrorCode_ShouldReturnNULL_WhenUnknownErrorCodeIsPassed(){
		assertEquals(null, OracleErrorCodes.getVendorErrorMessageFromErrorCode(unknownErrorCode()));
	}
	
	public static Object[][] dataFor_testFromErrorCode(){
		return new Object[][]{
				{OracleErrorCodes.ORA_1033, 1033},
				{OracleErrorCodes.ORA_1034, 1034},
				{OracleErrorCodes.ORA_1089, 1089},
				{OracleErrorCodes.ORA_12514, 12514},
				{OracleErrorCodes.ORA_12528, 12528},
				{OracleErrorCodes.ORA_17002, 17002},
				{OracleErrorCodes.ORA_17410, 17410},
				{OracleErrorCodes.ORA_17447, 17447},
				{OracleErrorCodes.ORA_3113, 3113},
		};
	}
	
	@Test
	@Parameters(method="dataFor_testFromErrorCode")
	public void testFromErrorCode(OracleErrorCodes expectedCode, int errorCode){
		assertEquals(expectedCode, OracleErrorCodes.fromErrorCode(errorCode));
	}
	
	@Test
	public void testFromErrorCode_ShouldReturnNULL_WhenUnknownErrorCodeIsPassed(){
		assertEquals(null, OracleErrorCodes.fromErrorCode(unknownErrorCode()));
	}
	
	private static int unknownErrorCode(){
		return -255;
	}
}
