package com.elitecore.coreradius.commons.attributes;

import java.io.ByteArrayInputStream;
import java.io.UnsupportedEncodingException;

import junit.framework.TestSuite;

import com.elitecore.coreradius.BaseRadiusTestCase;
import com.elitecore.coreradius.commons.packet.RadiusPacket;
import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.RadiusDictionaryTestHarness;
import com.elitecore.coreradius.commons.util.RadiusUtility;

public class StringAttributeTest extends BaseRadiusTestCase {
	
	public StringAttributeTest(String name){
		super(name);
	}
	
	public static TestSuite suite() {
		
		RadiusDictionaryTestHarness.getInstance();
		
		TestSuite suite = new TestSuite();
		suite.addTest(new StringAttributeTest("testGetSetStringValue"));
		suite.addTest(new StringAttributeTest("testGetSetStringValueWithCharacterSet"));
		suite.addTest(new StringAttributeTest("testGetSetBytes"));
		suite.addTest(new StringAttributeTest("testGetSetTypeAndGetIDAndGetIDStringAndGetIDBytes"));
		suite.addTest(new StringAttributeTest("testGetSetValue"));
		suite.addTest(new StringAttributeTest("testGetSetValueBytes"));
		suite.addTest(new StringAttributeTest("testGetSetIntValue"));
		suite.addTest(new StringAttributeTest("testGetSetLongValue"));
		suite.addTest(new StringAttributeTest("testSetValueInt"));
		suite.addTest(new StringAttributeTest("testSetValueLong"));
		suite.addTest(new StringAttributeTest("testReadFrom"));
		suite.addTest(new StringAttributeTest("testReadFromWithOneCharacterValue"));
		suite.addTest(new StringAttributeTest("testReadFromWithZeroLengthValue"));
		suite.addTest(new StringAttributeTest("testReadFromWithMaximumLengthValue"));
		suite.addTest(new StringAttributeTest("testReadLengthOnwardsFrom"));
		suite.addTest(new StringAttributeTest("test_setStringValueWith_sharedSecret_packetAuthenticator"));
	   	return suite;
	}
	
	//-------------------------------------------------------------------------
	// Test get/setStringValue method of StringAttribute
	// Test cases:
	//   1. "" value
	//   2. 10 character long String
	//   3. null value
	//   4. 253 charecters long String
	//   5. 276 characters long String
	//-------------------------------------------------------------------------
	public void testGetSetStringValue(){
		try{
			StringAttribute	stringAttribute = new StringAttribute();
			
			//Test Case: "" value
			stringAttribute.setStringValue("");
			assertEquals("",stringAttribute.getStringValue());
			assertEquals(2,stringAttribute.getLength());
			
			//Test Case: 10 character long String
			stringAttribute.setStringValue("TestString");
			assertEquals("TestString",stringAttribute.getStringValue());
			assertEquals(12,stringAttribute.getLength());

			//Test Case: null value
			stringAttribute.setStringValue(null);
			assertNotNull("setStringValue(String) with null parameter not ignoring null value",
					stringAttribute.getStringValue());
			assertEquals("TestString", stringAttribute.getStringValue());
			
			//Test Case: 253 character long String
			String string253Chars = 
				"EliteRadius is a powerful RADIUS/AAA server that meets the access control,  "+ 
				"service delivery, and  accounting  requirements of carriers, service providers, "+ 
				"and wireless operators.  EliteRadius  centrally manages the authentication of all"+
				" your customers.";
			stringAttribute.setStringValue(string253Chars);
			assertEquals(string253Chars,stringAttribute.getStringValue());
			assertEquals(255,stringAttribute.getLength());
			
			//Test Case: 276 character long value
			String string276Chars = "Elitecore Technologies " + string253Chars ;
			stringAttribute.setStringValue(string276Chars);
			assertEquals(string276Chars.substring(0,253),stringAttribute.getStringValue());
			assertEquals(255,stringAttribute.getLength());
		}catch(Exception e){
			e.printStackTrace();
			fail("testGetSetStringValue failed, reason: "+e.getMessage());
		}
	}
	
	
	
	public void test_setStringValueWith_sharedSecret_packetAuthenticator() {
		
		String sharedSecret = "secret";
		RadiusPacket radiusPacket = new RadiusPacket();
		radiusPacket.setPacketType(1);
		radiusPacket.setAuthenticator(RadiusUtility.generateRFC2865RequestAuthenticator());
		radiusPacket.setIdentifier(1);
		
		// Test 1 : check setStringValue(with plain text value, no encryption standard will apply here) 
		StringAttribute stringAttribute = new StringAttribute();
		stringAttribute.setStringValue("testString", sharedSecret, radiusPacket.getAuthenticator());
		assertEquals("testString",stringAttribute.getStringValue(sharedSecret,radiusPacket.getAuthenticator()));
		
		//Test 2 : check setStringValue(with plain value but attribute which has encryption standard like- cisco password(9:249))
		String ciscoPasswordAvpId = "9:249";
		IRadiusAttribute ciscoPasswordAttribute = Dictionary.getInstance().getKnownAttribute(ciscoPasswordAvpId);
		if (ciscoPasswordAttribute != null) {
			ciscoPasswordAttribute.setStringValue("example",sharedSecret,radiusPacket.getAuthenticator());
			radiusPacket.addAttribute(ciscoPasswordAttribute);
		} else {
			System.out.println("Cisco password not found in dictionary");
		}
		
		System.out.println("Radius Packet :" + radiusPacket.toString());
		IRadiusAttribute ciscoPasswordAvpFromPacket = radiusPacket.getRadiusAttribute(ciscoPasswordAvpId,true);
		String ciscoPasswordDecryptedValue = "";
		if(ciscoPasswordAvpFromPacket != null){
			ciscoPasswordDecryptedValue = ciscoPasswordAvpFromPacket.getStringValue(sharedSecret, radiusPacket.getAuthenticator());
			// remove already cisco password avp
			radiusPacket.removeAttribute(ciscoPasswordAvpFromPacket);
		} else {
			System.out.println("Cisco password not found in radius packet");
		}
		
		String ciscoPasswordHexValue = "0x6578616d706c65";
		assertEquals(ciscoPasswordHexValue, ciscoPasswordDecryptedValue);
		
		//Test 3 : check setStringValue(with hex value but attribute which has encryption standard like- cisco password(9:249))
		IRadiusAttribute ciscoPasswordAttributeWithHexValue = Dictionary.getInstance().getKnownAttribute(ciscoPasswordAvpId);
		if (ciscoPasswordAttributeWithHexValue != null) {
			ciscoPasswordAttributeWithHexValue.setStringValue(ciscoPasswordHexValue,sharedSecret,radiusPacket.getAuthenticator());
			radiusPacket.addAttribute(ciscoPasswordAttribute);
		} else {
			System.out.println("Cisco password not found in dictionary");
		}
		
		ciscoPasswordAvpFromPacket = radiusPacket.getRadiusAttribute(ciscoPasswordAvpId,true);
		ciscoPasswordDecryptedValue = "";
		if(ciscoPasswordAvpFromPacket != null){
			ciscoPasswordDecryptedValue = ciscoPasswordAvpFromPacket.getStringValue(sharedSecret, radiusPacket.getAuthenticator());
		} else {
			
			System.out.println("Cisco password not found in radius packet");
		}
		assertEquals(ciscoPasswordHexValue, ciscoPasswordDecryptedValue);
		
	}
	
	//-------------------------------------------------------------------------
	// Test testGetSetStringValueWithCharacterSet method of StringAttribute
	// Test Cases:
	//   1. value = 10 character long String, charsetName = "US-ASCII"
	//   2. value = null, charsetName = "US-ASCII"
	//   3. value = 10 character long String, charsetName = null
	//   4. value = 10 character long String, charsetName = "invalidCharset"
	//   5. value = null, charsetName = null
	//-------------------------------------------------------------------------
	public void testGetSetStringValueWithCharacterSet(){
		try{
			StringAttribute stringAttribute = new StringAttribute();
			String charsetName = "US-ASCII";
			String testString = "TestString";
			
			
			//Test Case: value = 10 character long String, charsetName = "US-ASCII"
			try {
				stringAttribute.setStringValue(testString, charsetName);
			} catch (UnsupportedEncodingException e) {
				fail("setStringValue(String,String) is not accepting "
					+charsetName+" as valid character-set");
			}
			try {
				assertEquals(testString,stringAttribute.getStringValue(charsetName));
			} catch (UnsupportedEncodingException e) {
				fail("getStringValue(String) is not accepting "
					+charsetName+" as valid character-set");
			}
			
			//Test Case: value = null, charsetName = "US-ASCII"
			try {
				stringAttribute.setStringValue(null, charsetName);
			} catch (UnsupportedEncodingException e) {
				fail("setStringValue(String,String) with value=null is throwing " +
					"UnsupportedEncodingException exception");
			}
			try {
				assertNotNull("setStringValue(null) with null parameter is not ignoring null value",
						stringAttribute.getStringValue(charsetName));
				assertEquals(testString, stringAttribute.getStringValue(charsetName));
			} catch (UnsupportedEncodingException e) {
				fail("getStringValue(String) does not accept "
					+charsetName+"as valid character-set");
			}
			
			//Test Case: value = 10 character long String, charsetName = "null"
			try {
				stringAttribute.setStringValue(testString, null);
			} catch (UnsupportedEncodingException e) {
				fail("setStringValue(String,String) is throwing UnsupportedEncodingException " +
						"even if charsetName=null, it must use the default charset");
			} catch (NullPointerException e) {
				fail("setStringValue(String,String) must not throw NullPointerException " +
						"even if charsetName=null, it must use the default charset");
			}
			try {
				@SuppressWarnings("unused") String testValue = stringAttribute.getStringValue(null);
			} catch (UnsupportedEncodingException e) {
				fail("getStringValue(String)  must not throw UnsupportedEncodingException " +
						"even if charsetName=null, it must use the default charset");
			} catch (NullPointerException e) {
				fail("getStringValue(String)  must not throw NullPointerException " +
						"even if charsetName=null, it must use the default charset");
			}
			
			//Test Case: value = 10 character long String, charsetName = "invalidCharset"
			String invalidCharsetName = "invalidCharset";
			try {
				stringAttribute.setStringValue(testString, invalidCharsetName);
				fail("UnsupportedEncodingException not thrown for invalid charsetName " +
					"for setStringValue");
			} catch (UnsupportedEncodingException e) {
			}
			try {
				@SuppressWarnings("unused") String testValue = stringAttribute.getStringValue(invalidCharsetName);
				fail("UnsupportedEncodingException not thrown for invalid charsetName " +
					"for getStringValue");
			} catch (UnsupportedEncodingException e) {
			}
			
			//Test Case: value = null, charsetName = null
			try {
				stringAttribute.setStringValue(null, null);
			} catch (UnsupportedEncodingException e) {
				fail("setStringValue(String,String) with value=null and charsetName=null" +
					" is throwing UnsupportedEncodingException exception");
			}
			try {
				assertNotNull("setStringValue(null) with null parameter is not ignoring null value",
						stringAttribute.getStringValue(charsetName));
				assertEquals(testString, stringAttribute.getStringValue(charsetName));
			} catch (UnsupportedEncodingException e) {
				fail("getStringValue(String) does not accept "
					+charsetName+"as valid character-set");
			}
		}catch(Exception e){
			e.printStackTrace();
			fail("testGetSetStringValueWithCharacterSet failed, reason: "+e.getMessage());
		}
	}

	//-------------------------------------------------------------------------
	// Test setBytes(byte[]) and getBytes() methods
	// Test Cases:
	//   1. value = 12 byte long
	//   2. value = null
	//   3. value = 2 byte long
	//   4. value = 3 byte long
	//   5. value = 255 byte long
	//   6. value = 256 byte long
	//-------------------------------------------------------------------------
	public void testGetSetBytes(){
		try{
			StringAttribute stringAttribute = new StringAttribute();
			byte[] testValue = new byte[]{1, 12,
							'T', 'e', 's', 't', 'S', 't', 'r', 'i', 'n', 'g'};
			
			//Test Case: value = 12 byte long
			stringAttribute.setBytes(testValue);
			for (int i=0; i<testValue.length; i++){
				assertEquals(testValue[i], stringAttribute.getBytes()[i]);
			}
			assertEquals(testValue.length, stringAttribute.getBytes().length);
			
			//Test Case: value = null
			stringAttribute.setBytes(null);
			assertNotNull(stringAttribute.getBytes());
			for (int i=0; i<testValue.length; i++){
				assertEquals(testValue[i], stringAttribute.getBytes()[i]);
			}
			assertEquals(testValue.length, stringAttribute.getBytes().length);
			
			//Test Case: value = 2 byte long
//			byte[] testValue2Length = new byte[]{1,2};
//			stringAttribute.setBytes(testValue2Length);
//			for (int i=0; i<testValue.length; i++){
//				assertEquals(testValue[i], stringAttribute.getBytes()[i]);
//			}
//			assertEquals(testValue.length, stringAttribute.getBytes().length);
			
			//Test Case: value = 3 byte long
			byte[] testValue3Length = new byte[]{1,3,'T'};
			stringAttribute.setBytes(testValue3Length);
			for (int i=0; i<testValue3Length.length; i++){
				assertEquals(testValue3Length[i], stringAttribute.getBytes()[i]);
			}
			assertEquals(testValue3Length.length, stringAttribute.getBytes().length);
			
			//Test Case: value = 255 byte long
			byte[] testValue255Length = new byte[255];
			testValue255Length[0] = (byte)1;
			testValue255Length[1] = (byte)255;
			for (int i=2; i<testValue255Length.length; i++){
				testValue255Length[i] = 't';
			}
			stringAttribute.setBytes(testValue255Length);
			for (int i=0; i<testValue255Length.length; i++){
				assertEquals(testValue255Length[i], stringAttribute.getBytes()[i]);
			}
			assertEquals(255, stringAttribute.getBytes().length);
			
			//Test Case: value = 256 byte long
			byte[] testValue256Length = new byte[256];
			testValue256Length[0] = (byte)1;
			testValue256Length[1] = (byte)255;
			testValue256Length[2] = 'a';
			System.arraycopy(testValue255Length,2,testValue256Length,3,testValue255Length.length-2);
			stringAttribute.setBytes(testValue256Length);
			for (int i=0; i<255; i++){
				assertEquals(testValue256Length[i], stringAttribute.getBytes()[i]);
			}
			assertEquals(255, stringAttribute.getBytes().length);
			
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
			StringAttribute stringAttribute = new StringAttribute();
			
			//Test Case: type = 15
			stringAttribute.setType(15);
			assertEquals(15, stringAttribute.getType());
			assertEquals(15, stringAttribute.getID());
			assertEquals((byte)15, stringAttribute.getIDBytes()[0]);
			assertEquals("15", stringAttribute.getIDString());
			
			//Test Case: type = 1
			stringAttribute.setType(1);
			assertEquals(1, stringAttribute.getType());
			assertEquals(1, stringAttribute.getID());
			assertEquals((byte)1, stringAttribute.getIDBytes()[0]);
			assertEquals("1", stringAttribute.getIDString());
			
			//Test Case: type = 255
			stringAttribute.setType(255);
			assertEquals(255, stringAttribute.getType());
			assertEquals(255, stringAttribute.getID());
			assertEquals((byte)255, stringAttribute.getIDBytes()[0]);
			assertEquals("255", stringAttribute.getIDString());
			
		}catch(Exception e){
			e.printStackTrace();
			fail("testGetSetLength failed, reason : " + e.getMessage());
		}
	}
	
	//-------------------------------------------------------------------------
	// Test getValue() and setValue(byte[]) methods
	// Test Cases:
	//   1. value = null
	//   2. value = byte[10]
	//   3. value = byte[0]
	//   4. value = byte[253]
	//   5. value = byte[254]
	//-------------------------------------------------------------------------
	public void testGetSetValue(){
		try{
			StringAttribute stringAttribute = new StringAttribute();
			
			//Test Case: value = null
			stringAttribute.setValue(null);
			assertNotNull(stringAttribute.getValue());
			assertEquals(0, stringAttribute.getValue().length);
			assertEquals(2, stringAttribute.getLength());
			
			//Test Case: value = byte[10]
			byte[] testValue10Length = {'T', 'e', 's', 't', 'S', 't', 'r', 'i', 'n', 'g'};
			stringAttribute.setValue(testValue10Length);
			for (int i=0; i<testValue10Length.length; i++){
				assertEquals(testValue10Length[i], stringAttribute.getValue()[i]);
			}
			assertEquals(testValue10Length.length, stringAttribute.getValue().length);
			assertEquals(testValue10Length.length+2, stringAttribute.getLength());
			
			//Test Case: value = byte[0]
			stringAttribute.setValue(new byte[0]);
			assertEquals(0, stringAttribute.getValue().length);
			assertEquals(2, stringAttribute.getLength());
			
			//Test Case: value = byte[253]
			byte[] testValue253Length = {
						'E','l','i','t','e','R','a','d','i','u','s',' ','i','s',' ','a',
						' ','p','o','w','e','r','f','u','l',' ','R','A','D','I','U','S',
						'/','A','A','A',' ','s','e','r','v','e','r',' ','t','h','a','t',
						' ','m','e','e','t','s',' ','t','h','e',' ','a','c','c','e','s','s',
						' ','c','o','n','t','r','o','l',',',' ',' ','s','e','r','v','i','c','e',
						' ','d','e','l','i','v','e','r','y',',',' ','a','n','d',' ',' ',
						'a','c','c','o','u','n','t','i','n','g',' ',' ',
						'r','e','q','u','i','r','e','m','e','n','t','s',' ','o','f',' ',
						'c','a','r','r','i','e','r','s',',',' ','s','e','r','v','i','c','e',
						' ','p','r','o','v','i','d','e','r','s',',',' ','a','n','d',' ',
						'w','i','r','e','l','e','s','s',' ','o','p','e','r','a','t','o','r','s','.',
						' ',' ','E','l','i','t','e','R','a','d','i','u','s',' ',' ',
						'c','e','n','t','r','a','l','l','y',' ','m','a','n','a','g','e','s',' ',
						't','h','e',' ','a','u','t','h','e','n','t','i','c','a','t','i','o','n',
						' ','o','f',' ','a','l','l',' ','y','o','u','r',' ',
						'c','u','s','t','o','m','e','r','s','.'
					};
			stringAttribute.setValue(testValue253Length);
			for (int i=0; i<testValue253Length.length; i++){
				assertEquals(testValue253Length[i], stringAttribute.getValue()[i]);
			}
			assertEquals(testValue253Length.length, stringAttribute.getValue().length);
			assertEquals(255, stringAttribute.getLength());
			
			//Test Case: value = byte[254]
			byte[] testValue254Length = new byte[256];
			testValue254Length[0] = '1';
			System.arraycopy(testValue253Length, 0, testValue254Length, 1, 253);
			stringAttribute.setValue(testValue254Length);
			for (int i=0; i<253; i++){
				assertEquals(testValue254Length[i], stringAttribute.getValue()[i]);
			}
			assertEquals(253, stringAttribute.getValue().length);
			assertEquals(255, stringAttribute.getLength());
			
		}catch(Exception e){
			e.printStackTrace();
			fail("testGetSetValue failed, reason : " + e.getMessage());
		}
	}
	
	//-------------------------------------------------------------------------
	// Test getValueBytes() and setValueBytes(byte[]) methods
	// Test Cases:
	//   1. value = null
	//   2. value = byte[10]
	//   3. value = byte[0]
	//   4. value = byte[253]
	//   5. value = byte[254]
	//-------------------------------------------------------------------------
	public void testGetSetValueBytes(){
		try{
			StringAttribute stringAttribute = new StringAttribute();
			
			//Test Case: value = null
			stringAttribute.setValue(null);
			assertNotNull(stringAttribute.getValueBytes());
			assertEquals(0, stringAttribute.getValueBytes().length);
			assertEquals(2, stringAttribute.getLength());
			
			//Test Case: value = byte[10]
			byte[] testValue10Length = {'T', 'e', 's', 't', 'S', 't', 'r', 'i', 'n', 'g'};
			stringAttribute.setValue(testValue10Length);
			for (int i=0; i<testValue10Length.length; i++){
				assertEquals(testValue10Length[i], stringAttribute.getValueBytes()[i]);
			}
			assertEquals(testValue10Length.length, stringAttribute.getValueBytes().length);
			assertEquals(testValue10Length.length+2, stringAttribute.getLength());
			
			//Test Case: value = byte[0]
			stringAttribute.setValue(new byte[0]);
			assertEquals(0, stringAttribute.getValueBytes().length);
			assertEquals(2, stringAttribute.getLength());
			
			//Test Case: value = byte[253]
			byte[] testValue253Length = {
						'E','l','i','t','e','R','a','d','i','u','s',' ','i','s',' ','a',
						' ','p','o','w','e','r','f','u','l',' ','R','A','D','I','U','S',
						'/','A','A','A',' ','s','e','r','v','e','r',' ','t','h','a','t',
						' ','m','e','e','t','s',' ','t','h','e',' ','a','c','c','e','s','s',
						' ','c','o','n','t','r','o','l',',',' ',' ','s','e','r','v','i','c','e',
						' ','d','e','l','i','v','e','r','y',',',' ','a','n','d',' ',' ',
						'a','c','c','o','u','n','t','i','n','g',' ',' ',
						'r','e','q','u','i','r','e','m','e','n','t','s',' ','o','f',' ',
						'c','a','r','r','i','e','r','s',',',' ','s','e','r','v','i','c','e',
						' ','p','r','o','v','i','d','e','r','s',',',' ','a','n','d',' ',
						'w','i','r','e','l','e','s','s',' ','o','p','e','r','a','t','o','r','s','.',
						' ',' ','E','l','i','t','e','R','a','d','i','u','s',' ',' ',
						'c','e','n','t','r','a','l','l','y',' ','m','a','n','a','g','e','s',' ',
						't','h','e',' ','a','u','t','h','e','n','t','i','c','a','t','i','o','n',
						' ','o','f',' ','a','l','l',' ','y','o','u','r',' ',
						'c','u','s','t','o','m','e','r','s','.'
					};
			stringAttribute.setValue(testValue253Length);
			for (int i=0; i<testValue253Length.length; i++){
				assertEquals(testValue253Length[i], stringAttribute.getValueBytes()[i]);
			}
			assertEquals(testValue253Length.length, stringAttribute.getValueBytes().length);
			assertEquals(255, stringAttribute.getLength());
			
			//Test Case: value = byte[254]
			byte[] testValue254Length = new byte[256];
			testValue254Length[0] = '1';
			System.arraycopy(testValue253Length, 0, testValue254Length, 1, 253);
			stringAttribute.setValue(testValue254Length);
			for (int i=0; i<253; i++){
				assertEquals(testValue254Length[i], stringAttribute.getValueBytes()[i]);
			}
			assertEquals(253, stringAttribute.getValueBytes().length);
			assertEquals(255, stringAttribute.getLength());
			
		}catch(Exception e){
			e.printStackTrace();
			fail("testGetSetValue failed, reason : " + e.getMessage());
		}
	}
	
	//-------------------------------------------------------------------------
	// Test readFrom method, normal value
	//-------------------------------------------------------------------------
	public void testReadFrom(){
		try {
			byte []attributeData = {  1, 23,
									  'E','l','i','t','e','c','o','r','e',' ',
									  'T','e','c','h','o','l','o','g','i','e','s'
									};
			ByteArrayInputStream inStream = new ByteArrayInputStream(attributeData);
			
			StringAttribute stringAttribute = new StringAttribute();
			stringAttribute.readFrom(inStream);

			assertEquals(1, stringAttribute.getType());
			assertEquals(23, stringAttribute.getLength());
			assertEquals("Elitecore Techologies", stringAttribute.getStringValue());
			
		}catch(Exception e){
			e.printStackTrace();
			fail("readFrom for StringAttribute failed, reason : " + e.getMessage());
		}
	}

	//-------------------------------------------------------------------------
	// Test readFrom method, near boundry - one character in value
	//-------------------------------------------------------------------------
	public void testReadFromWithOneCharacterValue(){
		try {
			byte []attributeData = {  1, 3,
									  'E'
									};
			ByteArrayInputStream inStream = new ByteArrayInputStream(attributeData);
			
			StringAttribute stringAttribute = new StringAttribute();
			stringAttribute.readFrom(inStream);

			assertEquals(1, stringAttribute.getType());
			assertEquals(3, stringAttribute.getLength());
			assertEquals("E", stringAttribute.getStringValue());
			
		}catch(Exception e){
			e.printStackTrace();
			fail("readFrom for StringAttribute failed, reason : " + e.getMessage());
		}
	}

	//-------------------------------------------------------------------------
	// Test readFrom method, boundry - no character in value
	//-------------------------------------------------------------------------
	public void testReadFromWithZeroLengthValue(){
		try {
			byte []attributeData = {  1, 2
									};
			ByteArrayInputStream inStream = new ByteArrayInputStream(attributeData);
			
			StringAttribute stringAttribute = new StringAttribute();
			stringAttribute.readFrom(inStream);

			assertEquals(1, stringAttribute.getType());
			assertEquals(2, stringAttribute.getLength());
			assertEquals("", stringAttribute.getStringValue());
			
		}catch(Exception e){
			e.printStackTrace();
			fail("readFrom for StringAttribute failed, reason : " + e.getMessage());
		}
	}

	//-------------------------------------------------------------------------
	// Test readFrom method, boundry - maximum characters in value
	//-------------------------------------------------------------------------
	public void testReadFromWithMaximumLengthValue(){
		try {
			byte []attributeData = {  1, (byte)255,
									'E','l','i','t','e','R','a','d','i','u','s',' ','i','s',' ','a',
									' ','p','o','w','e','r','f','u','l',' ','R','A','D','I','U','S',
									'/','A','A','A',' ','s','e','r','v','e','r',' ','t','h','a','t',
									' ','m','e','e','t','s',' ','t','h','e',' ','a','c','c','e','s','s',
									' ','c','o','n','t','r','o','l',',',' ',' ','s','e','r','v','i','c','e',
									' ','d','e','l','i','v','e','r','y',',',' ','a','n','d',' ',' ',
									'a','c','c','o','u','n','t','i','n','g',' ',' ',
									'r','e','q','u','i','r','e','m','e','n','t','s',' ','o','f',' ',
									'c','a','r','r','i','e','r','s',',',' ','s','e','r','v','i','c','e',
									' ','p','r','o','v','i','d','e','r','s',',',' ','a','n','d',' ',
									'w','i','r','e','l','e','s','s',' ','o','p','e','r','a','t','o','r','s','.',
									' ',' ','E','l','i','t','e','R','a','d','i','u','s',' ',' ',
									'c','e','n','t','r','a','l','l','y',' ','m','a','n','a','g','e','s',' ',
									't','h','e',' ','a','u','t','h','e','n','t','i','c','a','t','i','o','n',
									' ','o','f',' ','a','l','l',' ','y','o','u','r',' ',
									'c','u','s','t','o','m','e','r','s','.'		
									};
			ByteArrayInputStream inStream = new ByteArrayInputStream(attributeData);
			
			StringAttribute stringAttribute = new StringAttribute();
			stringAttribute.readFrom(inStream);

			assertEquals(1, stringAttribute.getType());
			assertEquals(255, stringAttribute.getLength());
			assertEquals("EliteRadius is a powerful RADIUS/AAA server that meets the access control,  "+ 
					"service delivery, and  accounting  requirements of carriers, service providers, "+ 
					"and wireless operators.  EliteRadius  centrally manages the authentication of all"+
					" your customers.", stringAttribute.getStringValue());
			
		}catch(Exception e){
			e.printStackTrace();
			fail("readFrom for StringAttribute failed, reason : " + e.getMessage());
		}
	}
	
	//-------------------------------------------------------------------------
	// Test getIntValue() and setIntValue(int) methods
	// Test Cases:
	//   1. value = 12345
	//   2. value = -12345
	//   3. value = 0
	//   4. value = 2147483647
	//   5. value = -2147483648
	//   6. value = "abcd" and try to access through getIntValue()
	//-------------------------------------------------------------------------
	public void testGetSetIntValue(){
		try{
			StringAttribute stringAttribute = new StringAttribute();
			
			//Test Case: value = 12345
			stringAttribute.setIntValue(12345);
			assertEquals(12345, stringAttribute.getIntValue());
			
			//Test Case: value = -12345
			stringAttribute.setIntValue(-12345);
			assertEquals(-12345, stringAttribute.getIntValue());
			
			//Test Case: value = 0
			stringAttribute.setIntValue(0);
			assertEquals(0, stringAttribute.getIntValue());
			
			//Test Case: value = 2147483647
			stringAttribute.setIntValue(2147483647);
			assertEquals(2147483647, stringAttribute.getIntValue());
			
			//Test Case: value = -2147483648
			stringAttribute.setIntValue(-2147483648);
			assertEquals(-2147483648, stringAttribute.getIntValue());
			
			//Test Case: value = "abcd" and try to access through getIntValue()
			stringAttribute.setStringValue("abcd");
			try{
				@SuppressWarnings("unused") long tmp = stringAttribute.getIntValue();
				fail("getIntValue() method is not throwing NumberFormatException" +
						"when the value is not parsable to int value");
			}catch(NumberFormatException e){
				
			}
		}catch(Exception e){
			e.printStackTrace();
			fail("get/setIntValue for StringAttribute failed, reason : " + e.getMessage());
		}
	}
	
	//-------------------------------------------------------------------------
	// Test getLongValue() and setValue(long) methods
	// Test Cases:
	//   1. value = 12345
	//   2. value = -12345
	//   3. value = 0
	//   4. value = 9223372036854775807
	//   5. value = -9223372036854775808
	//   6. value = "abcd" and try to access through getLongValue()
	//-------------------------------------------------------------------------
	public void testGetSetLongValue(){
		try{
			StringAttribute stringAttribute = new StringAttribute();
			
			//Test Case: value = 12345
			stringAttribute.setLongValue(12345L);
			assertEquals(12345L, stringAttribute.getLongValue());
			
			//Test Case: value = -12345
			stringAttribute.setLongValue(-12345L);
			assertEquals(-12345L, stringAttribute.getLongValue());
			
			//Test Case: value = 0
			stringAttribute.setLongValue(0L);
			assertEquals(0L, stringAttribute.getLongValue());
			
			//Test Case: value = 9223372036854775807
			stringAttribute.setLongValue(9223372036854775807L);
			assertEquals(9223372036854775807L, stringAttribute.getLongValue());
			
			//Test Case: value = -9223372036854775808
			stringAttribute.setLongValue(-9223372036854775808L);
			assertEquals(-9223372036854775808L, stringAttribute.getLongValue());
			
			//Test Case: value = "abcd" and try to access through getLongValue()
			stringAttribute.setStringValue("abcd");
			try{
				stringAttribute.getLongValue();
				fail("getLongValue() method is not throwing NumberFormatException" +
						"when the value is not parsable to long value");
			}catch(NumberFormatException e){
				
			}
		}catch(Exception e){
			e.printStackTrace();
			fail("get/setIntValue for StringAttribute failed, reason : " + e.getMessage());
		}
	}
	
	//-------------------------------------------------------------------------
	// Test getLongValue() and setValue(long) methods
	// Test Cases:
	//   1. value = 12345
	//   2. value = -12345
	//   3. value = 0
	//   4. value = 9223372036854775807
	//   5. value = -9223372036854775808
	//-------------------------------------------------------------------------
	public void testSetValueLong(){
		try{
			StringAttribute stringAttribute = new StringAttribute();
			
			//Test Case: value = 12345
			stringAttribute.setValue(12345L);
			assertEquals(12345L, stringAttribute.getLongValue());
			
			//Test Case: value = -12345
			stringAttribute.setValue(-12345L);
			assertEquals(-12345L, stringAttribute.getLongValue());
			
			//Test Case: value = 0
			stringAttribute.setValue(0L);
			assertEquals(0L, stringAttribute.getLongValue());
			
			//Test Case: value = 9223372036854775807
			stringAttribute.setValue(9223372036854775807L);
			assertEquals(9223372036854775807L, stringAttribute.getLongValue());
			
			//Test Case: value = -9223372036854775808
			stringAttribute.setValue(-9223372036854775808L);
			assertEquals(-9223372036854775808L, stringAttribute.getLongValue());
		}catch(Exception e){
			e.printStackTrace();
			fail("get/setValue(long) for StringAttribute failed, reason : " + e.getMessage());
		}
	}
	
	//-------------------------------------------------------------------------
	// Test getIntValue() and setValue(int) methods
	// Test Cases:
	//   1. value = 12345
	//   2. value = -12345
	//   3. value = 0
	//   4. value = 2147483647
	//   5. value = -2147483648
	//-------------------------------------------------------------------------
	public void testSetValueInt(){
		try{
			StringAttribute stringAttribute = new StringAttribute();
			
			//Test Case: value = 12345
			stringAttribute.setValue(12345);
			assertEquals(12345, stringAttribute.getIntValue());
			
			//Test Case: value = -12345
			stringAttribute.setValue(-12345);
			assertEquals(-12345, stringAttribute.getIntValue());
			
			//Test Case: value = 0
			stringAttribute.setValue(0);
			assertEquals(0, stringAttribute.getIntValue());
			
			//Test Case: value = 2147483647
			stringAttribute.setValue(2147483647);
			assertEquals(2147483647, stringAttribute.getIntValue());
			
			//Test Case: value = -2147483648
			stringAttribute.setValue(-2147483648);
			assertEquals(-2147483648, stringAttribute.getIntValue());
		}catch(Exception e){
			e.printStackTrace();
			fail("get/setIntValue for StringAttribute failed, reason : " + e.getMessage());
		}
	}
	
	//-------------------------------------------------------------------------
	// Test readLengthOnwardsFrom() method
	// Test Cases:
	//   1. source-byte size = 22
	//   2. value = -12345
	//   3. value = 0
	//   4. value = 2147483647
	//   5. value = -2147483648
	//-------------------------------------------------------------------------
	public void testReadLengthOnwardsFrom(){
		try{
			
			//Test Case: source-byte size = 22
			byte []attributeData22Size = {23,
					  'E','l','i','t','e','c','o','r','e',' ',
					  'T','e','c','h','o','l','o','g','i','e','s'
					};
			ByteArrayInputStream inStream = new ByteArrayInputStream(attributeData22Size);
			
			StringAttribute stringAttribute = new StringAttribute();
			stringAttribute.readLengthOnwardsFrom(inStream);
			
			assertEquals(23, stringAttribute.getLength());
			assertEquals("Elitecore Techologies", stringAttribute.getStringValue());
			
			//Test Case: source-byte size = 22
			byte []attributeData2Size = {3,  'E'};
			inStream = new ByteArrayInputStream(attributeData2Size);
			
			stringAttribute = new StringAttribute();
			stringAttribute.readLengthOnwardsFrom(inStream);
			
			assertEquals(3, stringAttribute.getLength());
			assertEquals("E", stringAttribute.getStringValue());
			
			//Test Case: source-byte size = 1
			byte []attributeData1Size = {2};
			inStream = new ByteArrayInputStream(attributeData1Size);
			
			stringAttribute = new StringAttribute();
			stringAttribute.readLengthOnwardsFrom(inStream);
			
			assertEquals(2, stringAttribute.getLength());
			assertEquals("", stringAttribute.getStringValue());
			
			//Test Case: source-byte size = 254
			byte []attributeData254Size = { (byte)255,
						'E','l','i','t','e','R','a','d','i','u','s',' ','i','s',' ','a',
						' ','p','o','w','e','r','f','u','l',' ','R','A','D','I','U','S',
						'/','A','A','A',' ','s','e','r','v','e','r',' ','t','h','a','t',
						' ','m','e','e','t','s',' ','t','h','e',' ','a','c','c','e','s','s',
						' ','c','o','n','t','r','o','l',',',' ',' ','s','e','r','v','i','c','e',
						' ','d','e','l','i','v','e','r','y',',',' ','a','n','d',' ',' ',
						'a','c','c','o','u','n','t','i','n','g',' ',' ',
						'r','e','q','u','i','r','e','m','e','n','t','s',' ','o','f',' ',
						'c','a','r','r','i','e','r','s',',',' ','s','e','r','v','i','c','e',
						' ','p','r','o','v','i','d','e','r','s',',',' ','a','n','d',' ',
						'w','i','r','e','l','e','s','s',' ','o','p','e','r','a','t','o','r','s','.',
						' ',' ','E','l','i','t','e','R','a','d','i','u','s',' ',' ',
						'c','e','n','t','r','a','l','l','y',' ','m','a','n','a','g','e','s',' ',
						't','h','e',' ','a','u','t','h','e','n','t','i','c','a','t','i','o','n',
						' ','o','f',' ','a','l','l',' ','y','o','u','r',' ',
						'c','u','s','t','o','m','e','r','s','.'		
					};
			inStream = new ByteArrayInputStream(attributeData254Size);
			
			stringAttribute = new StringAttribute();
			stringAttribute.readLengthOnwardsFrom(inStream);
			
			assertEquals(255, stringAttribute.getLength());
			assertEquals(
					"EliteRadius is a powerful RADIUS/AAA server that meets the access control,  "+ 
					"service delivery, and  accounting  requirements of carriers, service " +
					"providers, and wireless operators.  EliteRadius  centrally manages the " +
					"authentication of all your customers.", stringAttribute.getStringValue());
		}catch(Exception e){
			e.printStackTrace();
			fail("readLengthOnwardsFrom for StringAttribute failed, reason : " + e.getMessage());
		}
	}
}