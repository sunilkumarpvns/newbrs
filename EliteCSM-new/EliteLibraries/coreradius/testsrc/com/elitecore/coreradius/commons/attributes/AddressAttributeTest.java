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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.net.InetAddress;

import org.junit.Before;
import org.junit.Test;

import com.elitecore.commons.net.FakeAddressResolver;

public class AddressAttributeTest {

	private FakeAddressResolver fakeAddressResolver;

	@Before
	public void setUp() {
		fakeAddressResolver = new FakeAddressResolver();
	}
	
	@Test
	public void testGetSetStringValue(){
		System.out.println("TEST ADD");
		try{
			AddressAttribute addressAttribute = createAttribute();
			
			//Test Case: "" value
/*			try{
				addressAttribute.setStringValue("");
				fail("AddressAttribute.setStringValue(String) is not throwing " +
						"IllegalArgumentException if the paramater is zero length string");
			}catch(IllegalArgumentException e){
			}
		*/
			try{
				addressAttribute.setStringValue("TestString");
			}catch(IllegalArgumentException e){
			}


			String stringAddress = "127.0.0.1";
			addressAttribute.setStringValue(stringAddress);
			assertEquals(stringAddress,addressAttribute.getStringValue());
			System.out.println("Check Len : " + addressAttribute.getLength());
			assertEquals(6,addressAttribute.getLength());
			
			stringAddress = "127.0.0.1/24";
			addressAttribute.setStringValue(stringAddress);
			assertEquals(stringAddress.substring(0, stringAddress.indexOf("/")), addressAttribute.getStringValue());
			
		}catch(Exception e){
			e.printStackTrace();
			fail("testGetSetStringValue failed, reason: "+e.getMessage());
		}
	}

	private AddressAttribute createAttribute() {
		AddressAttribute addressAttribute = new AddressAttribute(fakeAddressResolver);
		return addressAttribute;
	}
	
	@Test
	public void testGetSetBytes(){
		try{
			AddressAttribute addressAttribute = createAttribute();
			String address = "127.0.0.1";
			byte [] testValue = new byte[6];
			testValue[0] = 4; //type byte
			testValue[1] = 6; //length byte
			//4-bytes value
			testValue[2] = 127;
			testValue[3] = 0;
			testValue[4] = 0; 
			testValue[5] = 1;
			
			addressAttribute.setBytes(testValue);
			for (int i=0; i<testValue.length; i++){
				assertEquals(testValue[i], addressAttribute.getBytes()[i]);
			}
			assertEquals(testValue.length, addressAttribute.getBytes().length);
			assertEquals(address, addressAttribute.getStringValue());
			
			addressAttribute.setBytes(null);
			assertNotNull(addressAttribute.getBytes());
			for (int i=0; i<testValue.length; i++){
				assertEquals(testValue[i], addressAttribute.getBytes()[i]);
			}
			assertEquals(testValue.length, addressAttribute.getBytes().length);
			
			byte[] testValue2Length = new byte[]{
													1, //type byte
													2, //length byte
												};
			addressAttribute.setBytes(testValue2Length);
			for (int i=0; i<testValue.length; i++){
				assertEquals(testValue[i], addressAttribute.getBytes()[i]);
			}
			assertEquals(testValue.length, addressAttribute.getBytes().length);
			
		}catch(Exception e){
			e.printStackTrace();
			fail("testGetSetBytes failed, reason : " + e.getMessage());
		}
	}
	
	@Test
	public void testReadFrom(){
		System.out.println("In Read From");
		try {
			byte []attributeDataMinimum = { 1, //type byte
									 6, //length byte
									 127, 0, 0, 1 //value=127.0.0.1
								   };
			ByteArrayInputStream inStream = new ByteArrayInputStream(attributeDataMinimum);
			AddressAttribute addressAttribute = createAttribute();
			addressAttribute.readFrom(inStream);

			assertEquals(1, addressAttribute.getType());
			assertEquals(6, addressAttribute.getLength());
			assertEquals("127.0.0.1", addressAttribute.getStringValue());
						
			String address = "127.0.0.1";
			byte [] attributeDataNormal = new byte[6];
			attributeDataNormal[0] = 1; //type byte
			attributeDataNormal[1] = 6; //length byte
			//4-bytes value
			byte temp[] = null;
			try {
				temp = InetAddress.getByName(address).getAddress();
			}catch(Exception e) {
				e.printStackTrace();
			}
			
			attributeDataNormal[2] = temp[0];
			attributeDataNormal[3] = temp[1];
			attributeDataNormal[4] = temp[2];
			attributeDataNormal[5] = temp[3];
			inStream = new ByteArrayInputStream(attributeDataNormal);
			
			addressAttribute = createAttribute();
			addressAttribute.readFrom(inStream);
			
			assertEquals(1, addressAttribute.getType());
			assertEquals(6, addressAttribute.getLength());
			assertEquals(address, addressAttribute.getStringValue());
						
		}catch(Exception e){
			e.printStackTrace();
			fail("readFrom for IntegerAttribute failed, reason : " + e.getMessage());
		}
	}
}
