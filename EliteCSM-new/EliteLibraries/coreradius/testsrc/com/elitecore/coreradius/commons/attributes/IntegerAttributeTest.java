/*
 *	Radius Client API
 *	Elitecore Technologies Ltd.
 *	904, Silicon Tower, Law Garden
 *	Ahmedabad, India - 380009
 *
 *	Created on Sep 18, 2006
 *	Created By Dhirendra Kumar Singh
 */


package com.elitecore.coreradius.commons.attributes;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;

import junit.framework.TestSuite;

import com.elitecore.coreradius.BaseRadiusTestCase;


public class IntegerAttributeTest extends BaseRadiusTestCase {

	public IntegerAttributeTest(String name){
		super(name);
	}
	
	public static TestSuite suite() {
		TestSuite suite = new TestSuite();
		suite.addTest(new IntegerAttributeTest("testGetSetStringValue"));
		suite.addTest(new IntegerAttributeTest("testGetSetStringValueWithCharacterSet"));
		suite.addTest(new IntegerAttributeTest("testGetSetBytes"));
		suite.addTest(new IntegerAttributeTest("testGetSetTypeAndGetIDAndGetIDStringAndGetIDBytes"));
		suite.addTest(new IntegerAttributeTest("testGetSetValue"));
		suite.addTest(new IntegerAttributeTest("testGetSetValueBytes"));
		suite.addTest(new IntegerAttributeTest("testGetSetIntValue"));
		suite.addTest(new IntegerAttributeTest("testGetSetLongValue"));
		suite.addTest(new IntegerAttributeTest("testSetValueInt"));
		suite.addTest(new IntegerAttributeTest("testSetValueLong"));
		suite.addTest(new IntegerAttributeTest("testReadFrom"));
		suite.addTest(new IntegerAttributeTest("testReadLengthOnwardsFrom"));
	   	return suite;
	}

	//-------------------------------------------------------------------------
	// Test get/setStringValue method of StringAttribute
	// Test cases:
	//   1. "" value
	//   2. 10 character long String
	//   3. null value
	//   4. "0" value
	//   5. "4294967296" value
	//-------------------------------------------------------------------------
	public void testGetSetStringValue(){
		try{
			IntegerAttribute integerAttribute = new IntegerAttribute();
			
			//Test Case: "" value
			try{
				integerAttribute.setStringValue("");
				fail("IntegerAttribute.setStringValue(String) is not throwing " +
						"IllegalArgumentException if the paramater is zero length string");
			}catch(IllegalArgumentException e){
			}

			//Test Case: 10 character long String
			try{
				integerAttribute.setStringValue("TestString");
				fail("IntegerAttribute.setStringValue(String) is not throwing " +
						"IllegalArgumentException if the paramater is non-integer111 string");
			}catch(IllegalArgumentException e){
			}

			//Test Case: null value
			integerAttribute.setStringValue(null);
			assertEquals(0,integerAttribute.getIntValue());				
	
			///Test Case: "0" value
			String stringZero = "0";
			integerAttribute.setStringValue(stringZero);
			assertEquals(stringZero,integerAttribute.getStringValue());
			assertEquals(6,integerAttribute.getLength());
			
			//Test Case: "4294967295" value
			String stringMaxUnsigned = "4294967295";
			integerAttribute.setStringValue(stringMaxUnsigned);
			assertEquals(stringMaxUnsigned,integerAttribute.getStringValue());
			assertEquals(6,integerAttribute.getLength());
			
		}catch(Exception e){
			e.printStackTrace();
			fail("testGetSetStringValue failed, reason: "+e.getMessage());
		}
	}
	
	//-------------------------------------------------------------------------
	// Test testGetSetStringValueWithCharacterSet method of StringAttribute
	// Test Cases:
	//   1. value = "10", charsetName = "US-ASCII"
	//   2. value = null, charsetName = "US-ASCII"
	//   3. value = 10 character long String, charsetName = null
	//   4. value = 10 character long String, charsetName = "invalidCharset"
	//   5. value = null, charsetName = null
	//-------------------------------------------------------------------------
	public void testGetSetStringValueWithCharacterSet(){
		try{
			IntegerAttribute integerAttribute = new IntegerAttribute();
			String charsetName = "US-ASCII";
			String testString = "10";
			
			//Test Case: value = "10", charsetName = "US-ASCII"
			try {
				integerAttribute.setStringValue(testString, charsetName);
			} catch (UnsupportedEncodingException e) {
				fail("setStringValue(String,String) is not accepting "
					+charsetName+" as valid character-set");
			}
			try {
				assertEquals(testString,integerAttribute.getStringValue(charsetName));
				assertEquals(6,integerAttribute.getLength());
			} catch (UnsupportedEncodingException e) {
				fail("getStringValue(String) is not accepting "
					+charsetName+" as valid character-set");
			}
			
			//Test Case: value = null, charsetName = "US-ASCII"
			integerAttribute.setStringValue(null, charsetName);
			assertEquals(0,integerAttribute.getIntValue());
			
			//Test Case: value = 10 character long String, charsetName = "null"
			try {
				integerAttribute.setStringValue(testString, null);
			} catch (UnsupportedEncodingException e) {
				fail("setStringValue(String,String) is throwing UnsupportedEncodingException " +
						"even if charsetName=null, it must use the default charset");
			} catch (NullPointerException e) {
				fail("setStringValue(String,String) must not throw NullPointerException " +
						"even if charsetName=null, it must use the default charset");
			}
			
			//Test Case: value = 10 character long String, charsetName = "invalidCharset"
			String invalidCharsetName = "invalidCharset";
			try {
				integerAttribute.setStringValue(testString, invalidCharsetName);
			} catch (UnsupportedEncodingException e) {
				fail("UnsupportedEncodingException was thrown for invalid charsetName " +
						"for setStringValue, it must use the default charset");
			}
			try {
				integerAttribute.getStringValue(invalidCharsetName);
			} catch (UnsupportedEncodingException e) {
				fail("UnsupportedEncodingException was thrown for invalid charsetName " +
						"for getStringValue, it must use the default charset");
			}
			
			//Test Case: value = null, charsetName = null
			integerAttribute.setStringValue(null, null);
			assertEquals(0,integerAttribute.getIntValue());
			
		}catch(Exception e){
			e.printStackTrace();
			fail("testGetSetStringValueWithCharacterSet failed, reason: "+e.getMessage());
		}
	}

	//-------------------------------------------------------------------------
	// Test setBytes(byte[]) and getBytes() methods
	// Test Cases:
	//   1. value = 2 Header-bytes + 4-byte representation of 12345
	//   2. value = null
	//   3. value = 2 Header-bytes only
	//   4. value = 2 Header-bytes + 4-byte representation of 0
	//   5. value = 2 Header-bytes + 4-byte representation of 4294967295 (Maximum)
	//-------------------------------------------------------------------------
	public void testGetSetBytes(){
		try{
			IntegerAttribute integerAttribute = new IntegerAttribute();
			int value = 12345;
			byte [] testValue = new byte[6];
			testValue[0] = 1; //type byte
			testValue[1] = 6; //length byte
			//4-bytes value
			testValue[5] = (byte)(value);
			testValue[4] = (byte)(value >>> 8);
			testValue[3] = (byte)(value >>> 16);
			testValue[2] = (byte)(value >>> 24);
			
			//Test Case: value = 2 Header-bytes + 4-byte representation of 12345
			integerAttribute.setBytes(testValue);
			for (int i=0; i<testValue.length; i++){
				assertEquals(testValue[i], integerAttribute.getBytes()[i]);
			}
			assertEquals(testValue.length, integerAttribute.getBytes().length);
			assertEquals(value, integerAttribute.getIntValue());
			assertEquals(value, integerAttribute.getLongValue());
			
			//Test Case: value = null
			integerAttribute.setBytes(null);
			assertNotNull(integerAttribute.getBytes());
			for (int i=0; i<testValue.length; i++){
				assertEquals(testValue[i], integerAttribute.getBytes()[i]);
			}
			assertEquals(testValue.length, integerAttribute.getBytes().length);
			assertEquals(value, integerAttribute.getLongValue());
			
			//Test Case: value = 2 Header-bytes only
			byte[] testValue2Length = new byte[]{
													1, //type byte
													2, //length byte
												};
			integerAttribute.setBytes(testValue2Length);
			for (int i=0; i<testValue.length; i++){
				assertEquals(testValue[i], integerAttribute.getBytes()[i]);
			}
			assertEquals(testValue.length, integerAttribute.getBytes().length);
			assertEquals(value, integerAttribute.getLongValue());
			
			//Test Case: value = 2 Header-bytes + 4-byte representation of 0
			byte[] testValueZero = new byte[]{
													1, //type byte
													6, //length byte
													0, 0, 0, 0  //byte representation of 0
												};
			integerAttribute.setBytes(testValueZero);
			for (int i=0; i<testValueZero.length; i++){
				assertEquals(testValueZero[i], integerAttribute.getBytes()[i]);
			}
			assertEquals(testValueZero.length, integerAttribute.getBytes().length);
			assertEquals(0, integerAttribute.getIntValue());
			assertEquals(0, integerAttribute.getLongValue());
			
			//Test Case: value = 2 Header-bytes + 4-byte representation of 4294967295 (Maximum)
			long valueMax = 4294967295L;
			byte[] testValueMax = new byte[6];
			testValueMax[0] = 1;
			testValueMax[1] = 6;
			for (int i=2; i<6; i++){
				testValueMax[i] = (byte)255;
			}
			integerAttribute.setBytes(testValueMax);
			for (int i=0; i<testValueMax.length; i++){
				assertEquals(testValueMax[i], integerAttribute.getBytes()[i]);
			}
			assertEquals(testValueMax.length, integerAttribute.getBytes().length);
			assertEquals(valueMax, integerAttribute.getLongValue());
		}catch(Exception e){
			e.printStackTrace();
			fail("testGetSetBytes failed, reason : " + e.getMessage());
		}
	}
	
			
	//-------------------------------------------------------------------------
	// Test getType(), getID(), getIDString(), getIDBytes and setType(int) methods
	// Test Cases:
	//   1. type = 15
	//   2. type = 1
	//   3. type = 255
	//-------------------------------------------------------------------------
	public void testGetSetTypeAndGetIDAndGetIDStringAndGetIDBytes(){
		try{
			IntegerAttribute integerAttribute = new IntegerAttribute();
			
			//Test Case: type = 15
			integerAttribute.setType(15);
			assertEquals(15, integerAttribute.getType());
			assertEquals(15, integerAttribute.getID());
			assertEquals((byte)15, integerAttribute.getIDBytes()[0]);
			assertEquals("15", integerAttribute.getIDString());
			
			//Test Case: type = 1
			integerAttribute.setType(1);
			assertEquals(1, integerAttribute.getType());
			assertEquals(1, integerAttribute.getID());
			assertEquals((byte)1, integerAttribute.getIDBytes()[0]);
			assertEquals("1", integerAttribute.getIDString());
			
			//Test Case: type = 255
			integerAttribute.setType(255);
			assertEquals(255, integerAttribute.getType());
			assertEquals(255, integerAttribute.getID());
			assertEquals((byte)255, integerAttribute.getIDBytes()[0]);
			assertEquals("255", integerAttribute.getIDString());
			
		}catch(Exception e){
			e.printStackTrace();
			fail("testGetSetLength failed, reason : " + e.getMessage());
		}
	}
	
	//-------------------------------------------------------------------------
	// Test getValue() and setValue(byte[]) methods
	// Test Cases:
	//   1. value = null
	//   2. value = byte[0]
	//   3. value = 12345
	//   4. value = 0 (Minimum)
	//   5. value = 1234567890 (32-bit value less than Maximum)
	//   6. value = 4294967295 (Maximum)
	//-------------------------------------------------------------------------
	public void testGetSetValue(){
		try{
			IntegerAttribute integerAttribute = new IntegerAttribute();
			
			//Test Case: value = null
			integerAttribute.setValue(null);
			assertNotNull(integerAttribute.getValue());
			assertEquals(0, integerAttribute.getValue().length);
			assertEquals(2, integerAttribute.getLength());
			
			//Test Case: value = byte[0]
			integerAttribute.setValue(new byte[0]);
			assertEquals(0, integerAttribute.getValue().length);
			assertEquals(2, integerAttribute.getLength());
			
			//Test Case: value = 12345
			int valueLessThan32Bit = 12345;
			byte [] testValueLessThan32Bit = new byte[4];
			//4-bytes value
			testValueLessThan32Bit[3] = (byte)(valueLessThan32Bit);
			testValueLessThan32Bit[2] = (byte)(valueLessThan32Bit >>> 8);
			testValueLessThan32Bit[1] = (byte)(valueLessThan32Bit >>> 16);
			testValueLessThan32Bit[0] = (byte)(valueLessThan32Bit >>> 24);
			integerAttribute.setValue(testValueLessThan32Bit);
			for (int i=0; i<testValueLessThan32Bit.length; i++){
				assertEquals(testValueLessThan32Bit[i], integerAttribute.getValue()[i]);
			}
			assertEquals(testValueLessThan32Bit.length, integerAttribute.getValue().length);
			assertEquals(valueLessThan32Bit, integerAttribute.getIntValue());
			assertEquals(valueLessThan32Bit, integerAttribute.getLongValue());
			assertEquals(6, integerAttribute.getLength());
			
			//Test Case: value = 0 (Minimum)
			byte [] testValueZero = new byte[4];
			//4-bytes value
			for (int i=0; i<4; i++){
				testValueZero[i] = 0;
			}
			integerAttribute.setValue(testValueZero);
			for (int i=0; i<testValueZero.length; i++){
				assertEquals(testValueZero[i], integerAttribute.getValue()[i]);
			}
			assertEquals(testValueZero.length, integerAttribute.getValue().length);
			assertEquals(0, integerAttribute.getIntValue());
			assertEquals(0, integerAttribute.getLongValue());
			assertEquals(6, integerAttribute.getLength());
			
			//Test Case: value = 1234567890 (32-bit value less than Maximum)
			int value32Bit = 1234567890;
			byte [] testValue32Bit = new byte[4];
			//4-bytes value
			testValue32Bit[3] = (byte)(value32Bit);
			testValue32Bit[2] = (byte)(value32Bit >>> 8);
			testValue32Bit[1] = (byte)(value32Bit >>> 16);
			testValue32Bit[0] = (byte)(value32Bit >>> 24);
			integerAttribute.setValue(testValue32Bit);
			for (int i=0; i<testValue32Bit.length; i++){
				assertEquals(testValue32Bit[i], integerAttribute.getValue()[i]);
			}
			assertEquals(testValue32Bit.length, integerAttribute.getValue().length);
			assertEquals(value32Bit, integerAttribute.getLongValue());
			assertEquals(testValue32Bit.length+2, integerAttribute.getLength());
			
			//Test Case: value = 4294967295 (Maximum)
			long valueMax = 4294967295L;
			byte [] testValueMax = new byte[4];
			//4-bytes value
			for (int i=0; i<4; i++){
				testValueMax[i] = (byte)255;
			}
			integerAttribute.setValue(testValueMax);
			for (int i=0; i<testValueMax.length; i++){
				assertEquals(testValueMax[i], integerAttribute.getValue()[i]);
			}
			assertEquals(testValueMax.length, integerAttribute.getValue().length);
			assertEquals(testValueMax.length+2, integerAttribute.getLength());
			assertEquals(valueMax, integerAttribute.getLongValue());
			
		}catch(Exception e){
			e.printStackTrace();
			fail("testGetSetValue failed, reason : " + e.getMessage());
		}
	}
	
	//-------------------------------------------------------------------------
	// Test getValueBytes() and setValueBytes(byte[]) methods
	// Test Cases:
	//   1. value = 4-byte representation of 1234567890
	//   2. value = null
	//   3. value = new byte[0]
	//   4. value = 4-byte representation of 0 (Minumum)
	//   5. value = 4-byte representation of 4294967295 (Maximum)
	//-------------------------------------------------------------------------
	public void testGetSetValueBytes(){
		try{
			IntegerAttribute integerAttribute = new IntegerAttribute();
			int value = 1234567890;
			byte [] testValue = new byte[4];
			testValue[3] = (byte)(value);
			testValue[2] = (byte)(value >>> 8);
			testValue[1] = (byte)(value >>> 16);
			testValue[0] = (byte)(value >>> 24);
			
			//Test Case: value = 4-byte representation of 1234567890
			integerAttribute.setValueBytes(testValue);
			for (int i=0; i<testValue.length; i++){
				assertEquals(testValue[i], integerAttribute.getValueBytes()[i]);
			}
			assertEquals(testValue.length, integerAttribute.getValueBytes().length);
			assertEquals(testValue.length+2, integerAttribute.getLength());
			assertEquals(value, integerAttribute.getLongValue());
			
			//Test Case: value = null
			integerAttribute.setValueBytes(null);
			assertNotNull(integerAttribute.getValueBytes());
			assertEquals(0, integerAttribute.getValueBytes().length);
			assertEquals(2, integerAttribute.getBytes().length);
			
			//Test Case: value = new byte[0]
			integerAttribute.setValueBytes(new byte[0]);
			assertEquals(0, integerAttribute.getValueBytes().length);
			assertEquals(2, integerAttribute.getBytes().length);
			
			//Test Case: value = 4-byte representation of 0
			byte[] testValueZero = new byte[]{0,0,0,0};
			integerAttribute.setValueBytes(testValueZero);
			for (int i=0; i<testValueZero.length; i++){
				assertEquals(testValueZero[i], integerAttribute.getValueBytes()[i]);
			}
			assertEquals(4, integerAttribute.getValueBytes().length);
			assertEquals(6, integerAttribute.getLength());
			assertEquals(0, integerAttribute.getIntValue());
			assertEquals(0, integerAttribute.getLongValue());
			
			//Test Case: value = 4-byte representation of 4294967295 (Maximum)
			long valueMax = 4294967295L;
			byte[] testValueMax = new byte[4];
			for (int i=0; i<4; i++){
				testValueMax[i] = (byte)255;
			}
			integerAttribute.setValueBytes(testValueMax);
			for (int i=0; i<testValueMax.length; i++){
				assertEquals(testValueMax[i], integerAttribute.getValueBytes()[i]);
			}
			assertEquals(4, integerAttribute.getValueBytes().length);
			assertEquals(6, integerAttribute.getLength());
			assertEquals(valueMax, integerAttribute.getLongValue());
			
		}catch(Exception e){
			e.printStackTrace();
			fail("testGetSetValue failed, reason : " + e.getMessage());
		}
	}
	
	//-------------------------------------------------------------------------
	// Test readFrom(), getType(), getLength() and getLong() methods
	// Test Cases:
	//   1. value = 2 Header-bytes + 4-byte representation of 0
	//   2. value = 2 Header-bytes + 4-byte representation of 12345
	//   3. value = 2 Header-bytes + 4-byte representation of 4294967295
	//-------------------------------------------------------------------------
	public void testReadFrom(){
		try {
			//Test Case: value = 2 Header-bytes + 4-byte representation of 0
			byte []attributeDataMinimum = { 1, //type byte
									 6, //length byte
									 0, 0, 0, 0 //value=0
								   };
			ByteArrayInputStream inStream = new ByteArrayInputStream(attributeDataMinimum);
			IntegerAttribute integerAttribute = new IntegerAttribute();
			integerAttribute.readFrom(inStream);

			assertEquals(1, integerAttribute.getType());
			assertEquals(6, integerAttribute.getLength());
			assertEquals(0, integerAttribute.getIntValue());
			assertEquals(0, integerAttribute.getLongValue());
			
			//Test Case: value = 2 Header-bytes + 4-byte representation of 12345
			int value = 12345;
			byte [] attributeDataNormal = new byte[6];
			attributeDataNormal[0] = 1; //type byte
			attributeDataNormal[1] = 6; //length byte
			//4-bytes value
			attributeDataNormal[5] = (byte)(value);
			attributeDataNormal[4] = (byte)(value >>> 8);
			attributeDataNormal[3] = (byte)(value >>> 16);
			attributeDataNormal[2] = (byte)(value >>> 24);
			inStream = new ByteArrayInputStream(attributeDataNormal);
			
			integerAttribute = new IntegerAttribute();
			integerAttribute.readFrom(inStream);
			
			assertEquals(1, integerAttribute.getType());
			assertEquals(6, integerAttribute.getLength());
			assertEquals(value, integerAttribute.getIntValue());
			assertEquals(value, integerAttribute.getLongValue());
			
			//Test Case: 2 Header-bytes + 4-byte representation of 4294967295
			long valueMax = 4294967295L;
			byte[] attributeDataMaximum = new byte[6];
			attributeDataMaximum[0] = 1;
			attributeDataMaximum[1] = 6;
			for (int i=2; i<6; i++){
				attributeDataMaximum[i] = (byte)255;
			}
			inStream = new ByteArrayInputStream(attributeDataMaximum);
			
			integerAttribute = new IntegerAttribute();
			integerAttribute.readFrom(inStream);
			
			assertEquals(1, integerAttribute.getType());
			assertEquals(6, integerAttribute.getLength());
			assertEquals(valueMax, integerAttribute.getLongValue());
		}catch(Exception e){
			e.printStackTrace();
			fail("readFrom for IntegerAttribute failed, reason : " + e.getMessage());
		}
	}
	
	//-------------------------------------------------------------------------
	// Test getIntValue() and setIntValue(int) methods
	// Test Cases:
	//   1. value = 12345
	//   2. value = 0
	//   3. value = 2147483647
	//-------------------------------------------------------------------------
	public void testGetSetIntValue(){
		try{
			IntegerAttribute integerAttribute = new IntegerAttribute();
			
			//Test Case: value = 12345
			integerAttribute.setIntValue(12345);
			assertEquals(12345, integerAttribute.getIntValue());
			
			//Test Case: value = 0
			integerAttribute.setIntValue(0);
			assertEquals(0, integerAttribute.getIntValue());
			
			//Test Case: value = 2147483647
			integerAttribute.setIntValue(2147483647);
			assertEquals(2147483647, integerAttribute.getLongValue());
			
		}catch(Exception e){
			e.printStackTrace();
			fail("get/setIntValue for IntegerAttribute failed, reason : " + e.getMessage());
		}
	}
	
	//-------------------------------------------------------------------------
	// Test getLongValue() and setLongValue(long) methods
	// Test Cases:
	//   1. value = 12345
	//   2. value = 0
	//   3. value = 4294967295
	//-------------------------------------------------------------------------
	public void testGetSetLongValue(){
		try{
			IntegerAttribute integerAttribute = new IntegerAttribute();
			
			//Test Case: value = 12345
			integerAttribute.setLongValue(12345L);
			assertEquals(12345L, integerAttribute.getLongValue());
			
			//Test Case: value = 0
			integerAttribute.setLongValue(0L);
			assertEquals(0L, integerAttribute.getLongValue());
			
			//Test Case: value = 4294967295
			integerAttribute.setLongValue(4294967295L);
			assertEquals(4294967295L, integerAttribute.getLongValue());
			
		}catch(Exception e){
			e.printStackTrace();
			fail("get/setIntValue for IntegerAttribute failed, reason : " + e.getMessage());
		}
	}
	
	//-------------------------------------------------------------------------
	// Test getLongValue() and setValue(long) methods
	// Test Cases:
	//   1. value = 12345
	//   3. value = 0
	//   4. value = 4294967295
	//-------------------------------------------------------------------------
	public void testSetValueLong(){
		try{
			IntegerAttribute integerAttribute = new IntegerAttribute();
			
			//Test Case: value = 12345
			integerAttribute.setValue(12345L);
			assertEquals(12345L, integerAttribute.getLongValue());
			
			//Test Case: value = 0
			integerAttribute.setValue(0L);
			assertEquals(0L, integerAttribute.getLongValue());
			
			//Test Case: value = 4294967295
			integerAttribute.setValue(4294967295L);
			assertEquals(4294967295L, integerAttribute.getLongValue());
		}catch(Exception e){
			e.printStackTrace();
			fail("get/setValue(long) for IntegerAttribute failed, reason : " + e.getMessage());
		}
	}
	
	//-------------------------------------------------------------------------
	// Test getIntValue() and setValue(int) methods
	// Test Cases:
	//   1. value = 12345
	//   2. value = 0
	//   3. value = 2147483647
	//-------------------------------------------------------------------------
	public void testSetValueInt(){
		try{
			IntegerAttribute integerAttribute = new IntegerAttribute();
			
			//Test Case: value = 12345
			integerAttribute.setValue(12345);
			assertEquals(12345, integerAttribute.getIntValue());
			
			//Test Case: value = 0
			integerAttribute.setValue(0);
			assertEquals(0, integerAttribute.getIntValue());
			
			//Test Case: value = 2147483647
			integerAttribute.setValue(2147483647);
			assertEquals(2147483647, integerAttribute.getLongValue());
		}catch(Exception e){
			e.printStackTrace();
			fail("get/setIntValue for IntegerAttribute failed, reason : " + e.getMessage());
		}
	}
	
	//-------------------------------------------------------------------------
	// Test readLengthOnwordsFrom(), getType(), getLength() and getLong() methods
	// Test Cases:
	//   1. value = 1 length-byte + 4-byte representation of 0
	//   2. value = 1 length-byte + 4-byte representation of 12345
	//   3. value = 1 length-byte + 4-byte representation of 4294967295
	//-------------------------------------------------------------------------
	public void testReadLengthOnwardsFrom(){
		try{
			//Test Case: value = 1 length-byte + 4-byte representation of 0
			byte []attributeDataMinimum = {
											 6, //length byte
											 0, 0, 0, 0 //value=0
										  };
			ByteArrayInputStream inStream = new ByteArrayInputStream(attributeDataMinimum);
			IntegerAttribute integerAttribute = new IntegerAttribute();
			integerAttribute.readLengthOnwardsFrom(inStream);

			assertEquals(6, integerAttribute.getLength());
			assertEquals(0, integerAttribute.getIntValue());
			assertEquals(0, integerAttribute.getLongValue());
			
			//Test Case: value = 1 length-byte + 4-byte representation of 12345
			int value = 12345;
			byte [] attributeDataNormal = new byte[5];
			attributeDataNormal[0] = 6; //length byte
			//4-bytes value
			attributeDataNormal[4] = (byte)(value);
			attributeDataNormal[3] = (byte)(value >>> 8);
			attributeDataNormal[2] = (byte)(value >>> 16);
			attributeDataNormal[1] = (byte)(value >>> 24);
			inStream = new ByteArrayInputStream(attributeDataNormal);
			
			integerAttribute = new IntegerAttribute();
			integerAttribute.readLengthOnwardsFrom(inStream);
			
			assertEquals(6, integerAttribute.getLength());
			assertEquals(value, integerAttribute.getIntValue());
			assertEquals(value, integerAttribute.getLongValue());
			
			//Test Case: 1 length-byte + 4-byte representation of 4294967295
			long valueMax = 4294967295L;
			byte[] attributeDataMaximum = new byte[5];
			attributeDataMaximum[0] = 6;
			for (int i=1; i<5; i++){
				attributeDataMaximum[i] = (byte)255;
			}
			inStream = new ByteArrayInputStream(attributeDataMaximum);
			
			integerAttribute = new IntegerAttribute();
			integerAttribute.readLengthOnwardsFrom(inStream);
			
			assertEquals(6, integerAttribute.getLength());
			assertEquals(valueMax, integerAttribute.getLongValue());
		}catch(Exception e){
			e.printStackTrace();
			fail("readLengthOnwardsFrom for IntegerAttribute failed, reason : " + e.getMessage());
		}
	}
}
