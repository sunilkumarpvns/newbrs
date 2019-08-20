package com.elitecore.coreradius.commons.util;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import junitparams.JUnitParamsRunner;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.elitecore.coreradius.commons.util.RadiusUtility;

@RunWith(JUnitParamsRunner.class)
public class RadiusUtilityTest {
	
	@Test
	public void testValidNAICases(){
		String nai = null;
		String realm = null;
		nai = "joe@example.com";
		
		assertEquals(true, RadiusUtility.isValidUserAccordingToABNF(nai));
		nai = "fred@foo-9.example.com";
		realm = nai.substring(nai.indexOf('@')+1);
		assertEquals(true, RadiusUtility.isValidRealmAccordingToABNF(realm));
		assertEquals(true, RadiusUtility.isValidUserAccordingToABNF(nai));
		
		nai = "jack@3rd.depts.example.com";
		realm = nai.substring(nai.indexOf('@')+1);
		assertEquals(true, RadiusUtility.isValidRealmAccordingToABNF(realm));
		assertEquals(true, RadiusUtility.isValidUserAccordingToABNF(nai));
		
		nai = "fred.smith@example.com";
		realm = nai.substring(nai.indexOf('@')+1);
		assertEquals(true, RadiusUtility.isValidRealmAccordingToABNF(realm));
		assertEquals(true, RadiusUtility.isValidUserAccordingToABNF(nai));
		
		nai = "fred_smith@example.com";
		realm = nai.substring(nai.indexOf('@')+1);
		assertEquals(true, RadiusUtility.isValidRealmAccordingToABNF(realm));
		assertEquals(true, RadiusUtility.isValidUserAccordingToABNF(nai));
		
		nai = "fred$@example.com";
		realm = nai.substring(nai.indexOf('@')+1);
		assertEquals(true, RadiusUtility.isValidRealmAccordingToABNF(realm));
		assertEquals(true, RadiusUtility.isValidUserAccordingToABNF(nai));
		
		nai = "fred=?#$&*+-/^smith@example.com";
		realm = nai.substring(nai.indexOf('@')+1);
		assertEquals(true, RadiusUtility.isValidRealmAccordingToABNF(realm));
		assertEquals(true, RadiusUtility.isValidUserAccordingToABNF(nai));
		
		nai = "nancy@eng.example.net";
		realm = nai.substring(nai.indexOf('@')+1);
		assertEquals(true, RadiusUtility.isValidRealmAccordingToABNF(realm));
		assertEquals(true, RadiusUtility.isValidUserAccordingToABNF(nai));
		
		
		nai = "eng%nancy@example.net";
		realm = nai.substring(nai.indexOf('@')+1);
		assertEquals(true, RadiusUtility.isValidRealmAccordingToABNF(realm));
		assertEquals(true, RadiusUtility.isValidUserAccordingToABNF(nai));
		
		
		/*nai = "@privatecorp.example.net";
		assertEquals(true, RadiusUtility.isValidUserAccordingToABNF(nai));
		
		nai = "\\(user\\)@example.net";
		assertEquals(true, RadiusUtility.isValidUserAccordingToABNF(nai));*/
		
		nai = "alice@xn--tmonesimerkki-bfbb.example.net";
		realm = nai.substring(nai.indexOf('@')+1);
		assertEquals(true, RadiusUtility.isValidRealmAccordingToABNF(realm));
		assertEquals(true, RadiusUtility.isValidUserAccordingToABNF(nai));
	}
	
	@Test
	public void testInvalidNAICases(){
		String nai = "fred@example";
		String realm = null;
		assertEquals(true, RadiusUtility.isValidUserAccordingToABNF(nai));
		
		nai = "fred@example_9.com";
		realm = nai.substring(nai.indexOf('@')+1);
		assertEquals(false, RadiusUtility.isValidRealmAccordingToABNF(realm));
		assertEquals(true, RadiusUtility.isValidUserAccordingToABNF(nai));
		
		
		nai = "fred@example.net@example.net";
		realm = nai.substring(nai.indexOf('@')+1);
		assertEquals(false, RadiusUtility.isValidRealmAccordingToABNF(realm));
		assertEquals(true, RadiusUtility.isValidUserAccordingToABNF(nai));
		
		nai = "fred.@example.net";
		realm = nai.substring(nai.indexOf('@')+1);
		assertEquals(true, RadiusUtility.isValidRealmAccordingToABNF(realm));
		assertEquals(false, RadiusUtility.isValidUserAccordingToABNF(nai));
		
		nai = "eng:nancy@example.net";
		realm = nai.substring(nai.indexOf('@')+1);
		assertEquals(true, RadiusUtility.isValidRealmAccordingToABNF(realm));
		assertEquals(false, RadiusUtility.isValidUserAccordingToABNF(nai));
		
		nai = "eng;nancy@example.net";
		realm = nai.substring(nai.indexOf('@')+1);
		assertEquals(true, RadiusUtility.isValidRealmAccordingToABNF(realm));
		assertEquals(false, RadiusUtility.isValidUserAccordingToABNF(nai));
		
		nai =  "(user)@example.net";
		realm = nai.substring(nai.indexOf('@')+1);
		assertEquals(true, RadiusUtility.isValidRealmAccordingToABNF(realm));
		assertEquals(false, RadiusUtility.isValidUserAccordingToABNF(nai));
		
		nai = "<nancy>@example.net";
		realm = nai.substring(nai.indexOf('@')+1);
		assertEquals(true, RadiusUtility.isValidRealmAccordingToABNF(realm));
		assertEquals(false, RadiusUtility.isValidUserAccordingToABNF(nai));
	}
	
	@Test
	@junitparams.Parameters(method = "dataFor_ExtractKeyForSuccessfulCases")
	public void testExtractKey_ShouldGetSuccess_ForSuccessfulCases(String keyValuePair, String expectedKey) {
		try {
			String actualKey = RadiusUtility.extractKey(keyValuePair);
			assertEquals(expectedKey, actualKey);
		} catch(Throwable t) {
			fail("Unexpected exception: " + t.getClass() + " received");
		}
	}
	
	public static Object[][] dataFor_ExtractKeyForSuccessfulCases() {
		return new Object[][] {
				{"abc=123", "abc"},
				
				{"abc = 123", "abc"},
				
				{" abc = 123 ", "abc"},
				
				{"     abc    =     123     ", "abc"},
				
				{"abc==123", "abc"},
				
				{"123=abc", "123"},
				
				{"a1b2c3=a1b2c300", "a1b2c3"},
		};
	}
	
	@Test(expected = IllegalArgumentException.class)
	@junitparams.Parameters(method = "dataFor_ExtractKeyForFailureCases")
	public void testExtractKey_ShouldThrowIllegalArgumentException_ForFailureCases(String keyValuePair) {
		RadiusUtility.extractKey(keyValuePair);
	}
	
	public static Object[][] dataFor_ExtractKeyForFailureCases() {
		return new Object[][] {
				{null},
				
				{""},
				
				{"anyString"},
				
				{"1234"},
				
				{"abc="},
				
				{"=123"},
				
				{"="},
				
				{"abc:123"},
		};
	}
	
	@Test
	@junitparams.Parameters(method = "dataFor_ExtractValueForSuccessfulCases")
	public void testExtractValue_ShouldGetSuccess_ForSuccessfulCases(String keyValuePair, String expectedValue) {
		try {
			String actualValue = RadiusUtility.extractValue(keyValuePair);
			assertEquals(expectedValue, actualValue);
		} catch(Throwable t) {
			fail("Unexpected exception: " + t.getClass() + " received");
		}
	}
	
	public static Object[][] dataFor_ExtractValueForSuccessfulCases() {
		return new Object[][] {
				{"abc=123", "123"},
				
				{"abc = 123", "123"},
				
				{" abc = 123 ", "123"},
				
				{"     abc    =     123     ", "123"},
				
				{"abc==123", "=123"},
				
				{"123=abc", "abc"},
				
				{"a1b2c3=a1b2c300", "a1b2c300"},
		};
	}
	
	@Test(expected = IllegalArgumentException.class)
	@junitparams.Parameters(method = "dataFor_ExtractKeyForFailureCases")
	public void testExtractValue_ShouldThrowIllegalArgumentException_ForFailureCases(String keyValuePair) {
		RadiusUtility.extractValue(keyValuePair);
	}
	
	public static Object[][] dataFor_ExtractValueForFailureCases() {
		return new Object[][] {
				{null},
				
				{""},
				
				{"anyString"},
				
				{"1234"},
				
				{"abc="},
				
				{"=123"},
				
				{"="},
				
				{"abc:123"},
		};
	}
	
	@Test
	@junitparams.Parameters(method = "dataFor_IsHexForSuccessfulCases")
	public void testIsHex_ShouldGetSuccess_ForSuccessfulCases(String value, boolean expectedResult) {
		try {
			boolean actualResult = RadiusUtility.isHex(value);
			assertEquals(expectedResult, actualResult);
		} catch (Throwable t) {
			fail("Unexpected exception: " + t.getClass() + " received");
		}
	}
	
	public static Object[][] dataFor_IsHexForSuccessfulCases() {
		return new Object[][] {
				{"23344", false},
				
				{"0100F8", false},
				
				{"0x0100F8", true},
				
				{"0X0100F8", true},
				
				{"", false},
		};
	}
	
	@Test(expected = NullPointerException.class)
	public void testIsHex_ShouldThrowNullPointerException_WhenValueAsNullIsPassed() {
		RadiusUtility.isHex(null);
	}
	
	@Test
	@junitparams.Parameters(method = "dataFor_IsNullOrEmptyForString")
	public void testIsNullOrEmptyForString_ShouldReturnTrueOrFalse_DependOnTheValue(String value, boolean expectedResult) {
		boolean actualResult = RadiusUtility.isNullOrEmpty(value);
		assertEquals(expectedResult, actualResult);
	}
	
	public Object[][] dataFor_IsNullOrEmptyForString() {
		return new Object[][] {
				{null, true},
				
				{"" , true},
				
				{"asdf", false},
				
				{"1234" , false},
				
				{"asd123", false},
				
				{"=",false},
				
				{" ", true},
				
				{"\t",true},
				
				{"\n",true}
		};
	}
	
	@Test
	@junitparams.Parameters(method = "dataFor_IsNullOrEmptyForBytes")
	public void testIsNullOrEmptyForBytes_ShouldReturnTrueOrFalse_DependOnTheBytes(byte[] bytes, boolean expectedResult) {
		boolean actualResult = RadiusUtility.isNullOrEmpty(bytes);
		assertEquals(expectedResult, actualResult);
	}
	
	public Object[][] dataFor_IsNullOrEmptyForBytes() {
		return new Object[][] {
				{null, true},
				
				{new byte[0] , true},
				
				{new byte[] {}, true},
				
				{new byte[5], false},
				
				{new byte[] {1,2,3,4} , false},
				
				{new byte[] {1,2,(byte)'a',4} ,false},
		};
	}
	
	@Test
	@junitparams.Parameters(method="dataFor_ToByteArrayForLongForSuccessfulCases")
	public void testToByteArrayForLong_ShouldGetSuccess_ForSuccessfulCases(long value, int noOfBytes, byte[] expectedBytes) {
		try {
			byte[] byteArrayReceivced = RadiusUtility.toByteArray(value, noOfBytes);
			assertEquals("Byte Array received is not of same length",noOfBytes, byteArrayReceivced.length);
			assertArrayEquals("Unexpected bytes received", expectedBytes, byteArrayReceivced);
		} catch (Throwable t) {
			fail("Unexpected exception: " + t.getClass() + " is thrown");
		}
	}
	
	public static Object[][] dataFor_ToByteArrayForLongForSuccessfulCases() {
		return new Object[][] {
				{123, 1, new byte[] {123}},
				
				{891, 4, new byte[] {0, 0, 3, 123}},
				
				{456, 8, new byte[] {0, 0, 0, 0, 0, 0, 1, -56}},
				
				{-1, 8, new byte[] {-1, -1, -1, -1, -1, -1, -1, -1}},
				
				{65537, 8, new byte[] {0, 0, 0, 0, 0, 1, 0, 1}},
				
				{Long.MIN_VALUE, 8 , new byte[] {-128, 0, 0, 0, 0, 0, 0, 0}}
		};
	}
	
	@Test(expected = IllegalArgumentException.class)
	@junitparams.Parameters(method="dataFor_ToByteArrayForLongForFailureCases")
	public void testToByteArrayForLong_ShouldThrowIllegalArgumentException_WhenInvalidArgumentsArePassed(long value, int noOfBytes) {
		RadiusUtility.toByteArray(value, noOfBytes);
	}
	
	public static Object[][] dataFor_ToByteArrayForLongForFailureCases() {
		return new Object[][] {
				{789, 9},
				
				{489, -1},
		};
	}
	
	@Test
	@junitparams.Parameters(method="dataFor_ReadLongForSuccessfulCases")
	public void testReadLong_ShouldGetSuccess_ForSuccessfulCases(byte[] bytes, int noOfBytes, long expectedLongValue) {
		try {
			InputStream inputStream = new ByteArrayInputStream(bytes);
			long actualLongValue = RadiusUtility.readLong(inputStream, noOfBytes);
			assertEquals("Expected value: " + expectedLongValue + " is not same as actual long value: " + actualLongValue, expectedLongValue, actualLongValue);
		} catch (Throwable t) {
			fail("Unexpected exception: " + t.getClass() + " is thrown");
		}
	}
	
	public static Object[][] dataFor_ReadLongForSuccessfulCases() {
		final byte[] buffer = new byte[] {1,1,1,1,1,1,1,1};
		
		return new Object[][] {
				{buffer, 8, 72340172838076673L},
				
				{buffer, 7, 282578800148737L},
				
				{buffer, 6, 1103823438081L},
				
				{buffer, 5, 4311810305L},
				
				{buffer, 4, 16843009},
				
				{buffer, 3, 65793},
				
				{buffer, 2, 257},
				
				{buffer, 1, 1},
		};
	}
	
	@Test
	@junitparams.Parameters(method="dataFor_ReadLongForFailureCases")
	public void testReadLong_ShouldThrowSomeException_ForFailureCases(byte[] bytes, int noOfBytes, Class<? extends Exception> expectedException) {
		try {
			InputStream inputStream = new ByteArrayInputStream(bytes);
			RadiusUtility.readLong(inputStream, noOfBytes);
			fail("Expected exception is not thrown");
		} catch (Throwable t) {
			assertEquals(expectedException, t.getClass());
		}
	}
	
	public static Object[][] dataFor_ReadLongForFailureCases() {
		final byte[] buffer = new byte[] {1,1,1,1,1,1,1,1};
		
		return new Object[][] {
				{buffer, 9, IllegalArgumentException.class},
				
				{buffer, 0, IllegalArgumentException.class},
				
				{new byte[] {1,1,1,1,1}, -1, IllegalArgumentException.class},
 
				{new byte[] {1,1,1,1,1}, 6, IOException.class},
		};
	}
	
	@Test(expected = NullPointerException.class)
	public void testReadLong_ShouldReturnNullPointerException_WhenValueAsNullIsPassed() throws IOException {
		RadiusUtility.readLong(null, 5);
	}
	
	@Test
	@junitparams.Parameters(method="dataFor_ReadBytesForSuccessfulCases")
	public void testReadBytes_ShouldGetSuccess_ForSuccessfulCases(byte[] bytes, int noOfBytes, byte[] expectedBytes) {
		try {
			InputStream inputStream = new ByteArrayInputStream(bytes);
			byte[] actualBytes = RadiusUtility.readBytes(inputStream, noOfBytes);
			assertTrue("Expected value: " + expectedBytes + " is not same as actual long value: " + actualBytes, Arrays.equals(expectedBytes, actualBytes));
		} catch (Throwable t) {
			fail("Unexpected exception: " + t.getClass() + " is thrown");
		}
	}
	
	public static Object[][] dataFor_ReadBytesForSuccessfulCases() {
		return new Object[][] {
				{new byte[] {1,2,3,4,5,6,7,8}, 8, new byte[] {1,2,3,4,5,6,7,8}},
				
				{new byte[] {1,2,3,4,5,6,7,8}, 7, new byte[] {1,2,3,4,5,6,7}},
				
				{new byte[] {1,2,3,4,5,6,7,8}, 1, new byte[] {1}},
				
				{new byte[] {1,2,3,4,5,6,7,8}, 0, new byte[] {}},
		};
	}
	
	@Test
	@junitparams.Parameters(method="dataFor_ReadBytesForFailureCases")
	public void testReadBytes_ShouldThrowSomeException_ForFailureCases(byte[] bytes, int noOfBytes, byte[] expectedBytes, Class<? extends Exception> expectedException) {
		try {
			InputStream inputStream = new ByteArrayInputStream(bytes);
			byte[] actualBytes = RadiusUtility.readBytes(inputStream, noOfBytes);
			assertTrue("Expected value: " + expectedBytes + " is not same as actual long value: " + actualBytes, Arrays.equals(expectedBytes, actualBytes));
			fail("Expected exception is not thrown");
		} catch (Throwable t) {
			assertEquals(expectedException, t.getClass());
		}
	}
	
	public static Object[][] dataFor_ReadBytesForFailureCases() {
		return new Object[][] {
				{new byte[] {1,2,3,4,5,6,7,8}, 9, new byte[] {1,2,3,4,5,6,7,8}, IOException.class},
				
				{new byte[] {1,2,3,4,5,6,7,8}, -1, new byte[] {}, IllegalArgumentException.class},
		};
	}
	
	@Test(expected = NullPointerException.class)
	public void testReadBytes_ShouldReturnNullPointerException_WhenValueAsNullIsPassed() throws IOException {
		RadiusUtility.readBytes(null, 5);
	}
	
	@Test
	@junitparams.Parameters(method="dataFor_ToByteArrayForIntForSuccessfulCases")
	public void testToByteArrayForInt_ShouldGetSuccess_ForSuccessfulCases(int value, int noOfBytes, byte[] expectedBytes) {
		try {
			byte[] byteArrayReceivced = RadiusUtility.toByteArray(value, noOfBytes);
			assertEquals("Byte Array received is not of the same length",noOfBytes, byteArrayReceivced.length);
			assertArrayEquals("Unexpected bytes received", expectedBytes, byteArrayReceivced);
		} catch (Throwable t) {
			fail("Unexpected exception: " + t.getClass() + " is thrown");
		}
	}
	
	public static Object[][] dataFor_ToByteArrayForIntForSuccessfulCases() {
		return new Object[][] {
				{123, 1, new byte[] {123}},
				
				{891, 3, new byte[] {0, 3, 123}},
				
				{456, 4, new byte[] {0, 0, 1, -56}},
				
				{-1, 4, new byte[] {-1, -1, -1, -1}},
				
				{65537, 4, new byte[] {0, 1, 0, 1}},
				
				{Integer.MIN_VALUE, 4 , new byte[] {-128, 0, 0, 0}}
		};
	}
	
	@Test(expected = IllegalArgumentException.class)
	@junitparams.Parameters(method="dataFor_ToByteArrayForIntForFailureCases")
	public void testToByteArrayForInt_ShouldThrowIllegalArgumentException_WhenInvalidArgumentsArePassed(int value, int noOfBytes) {
		RadiusUtility.toByteArray(value, noOfBytes);
	}
	
	public static Object[][] dataFor_ToByteArrayForIntForFailureCases() {
		return new Object[][] {
				{789, 5},
				
				{489, -1},
		};
	}
	
	@Test(expected = NullPointerException.class)
	public void testCheckNotNull_ShouldThrowNullPointerException_WhenReferenceIsNull() {
		RadiusUtility.checkNotNull(null, "Reference cannot be null");
	}
	
	@Test
	public void testCheckNotNull_ShouldMatchMessage_WhenReferenceIsNull() {
		String expectedMessage = "Reference cannot be null";
		try {
			RadiusUtility.checkNotNull(null, "Reference cannot be null");
			fail("Expected exception: NullPointerException is not received");
		} catch (Throwable t) {
			assertEquals("Message: " + t.getMessage() + " do not matches with expected message: " + expectedMessage, expectedMessage, t.getMessage());
		}
	}
	
	@Test
	public void testCheckArgument_ShouldGetSuccess_WhenArgumentIsTrue() {
		try {
			RadiusUtility.checkArgument(true, "It must be successful");
		} catch (Throwable t) {
			fail("Unexpected exception received" + t.getClass());
		}
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testCheckArgument_ShouldThrowIllegalArgumentException_WhenArgumentIsFalse() {
		RadiusUtility.checkArgument(false, "It must fail");
	}
	
	@Test
	public void testCheckArgument_ShouldMatchMessage_WhenArgumentIsFalse() {
		String expectedMessage = "It must fail";
		try {
			RadiusUtility.checkArgument(false, "It must fail");
			fail("Expected exception: IllegalArgumentException is not thrown");	
		} catch (Throwable t) {
			assertEquals("Message: " + t.getMessage() + " do not matches with expected message: " + expectedMessage, expectedMessage, t.getMessage());
		}
	}
	
	@Test
	@junitparams.Parameters(method="dataFor_ReadIntForSuccessfulCases")
	public void testReadInt_ShouldGetSuccess_ForSuccessfulCases(byte[] bytes, int noOfBytes, int expectedIntValue) {
		try {
			InputStream inputStream = new ByteArrayInputStream(bytes);
			int actualIntValue = RadiusUtility.readInt(inputStream, noOfBytes);
			assertEquals("Expected value: " + expectedIntValue + " is not same as actual int value: " + actualIntValue, expectedIntValue, actualIntValue);
		} catch (Throwable t) {
			fail("Unexpected exception: " + t.getClass() + " is thrown");
		}
	}
	
	public static Object[][] dataFor_ReadIntForSuccessfulCases() {
		final byte[] buffer = new byte[] {127,-1,-1,-1};
		
		return new Object[][] {
				{buffer, 4, 2147483647},
				
				{buffer, 3, 8388607},
				
				{buffer, 2, 32767},
				
				{buffer, 1, 127},
		};
	}
	
	@Test
	@junitparams.Parameters(method="dataFor_ReadIntForFailureCases")
	public void testReadInt_ShouldThrowSomeException_ForFailureCases(byte[] bytes, int noOfBytes, Class<? extends Exception> expectedException) {
		try {
			InputStream inputStream = new ByteArrayInputStream(bytes);
			RadiusUtility.readInt(inputStream, noOfBytes);
			fail("Expected exception is not thrown");
		} catch (Throwable t) {
			assertEquals(expectedException, t.getClass());
		}
	}
	
	public static Object[][] dataFor_ReadIntForFailureCases() {
		final byte[] buffer = new byte[] {127,-1,-1,-1};
		
		return new Object[][] {
				{buffer, 5, IllegalArgumentException.class},
				
				{buffer, 0, IllegalArgumentException.class},
				
				{new byte[] {1,1}, -1,IllegalArgumentException.class},

				{new byte[] {1,1}, 3, IOException.class},
		};
	}
	
	@Test(expected = NullPointerException.class)
	public void testReadInt_ShouldReturnNullPointerException_WhenValueAsNullIsPassed() throws IOException {
		RadiusUtility.readInt(null, 5);
	}
}
