package com.elitecore.coreradius.commons.attributes;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.ByteArrayInputStream;
import java.net.InetAddress;

import org.junit.Before;
import org.junit.Test;

import com.elitecore.commons.net.FakeAddressResolver;


public class IPv6AddressAttributeTest {

	private FakeAddressResolver fakeAddressResolver;

	@Before
	public void setUp() {
		fakeAddressResolver = new FakeAddressResolver();
	}
	
	@Test
	public void testGetSetStringValue(){
		try{
			AddressAttribute addressAttribute = new AddressAttribute(fakeAddressResolver);

			try{
				addressAttribute.setStringValue("TestString");
			}catch(IllegalArgumentException e){
			}

	
			String stringAdd = "0:0:0:0:0:0:0:1";
			addressAttribute.setStringValue(stringAdd);
			assertEquals("0:0:0:0:0:0:0:1",addressAttribute.getStringValue());
			assertEquals(18,addressAttribute.getLength());
			
		}catch(Exception e){
			e.printStackTrace();
			fail("testGetSetStringValue failed, reason: "+e.getMessage());
		}
	}
	
	@Test
	public void testGetSetBytes(){
		try{
			AddressAttribute addressAttribute = new AddressAttribute(fakeAddressResolver);
			String address = "0:0:0:0:0:0:0:1";
			byte [] testValue = new byte[18];
			testValue[0] = 95; //type byte
			testValue[1] = 18; //length byte
			//16-bytes value
			testValue[2] = 0;
			testValue[3] = 0;
			testValue[4] = 0; 
			testValue[5] = 0;
			testValue[6] = 0;
			testValue[7] = 0;
			testValue[8] = 0;
			testValue[9] = 0;
			testValue[10] = 0;
			testValue[11] = 0;
			testValue[12] = 0;
			testValue[13] = 0;
			testValue[14] = 0;
			testValue[15] = 0;
			testValue[16] = 0;
			testValue[17] = 1;

			addressAttribute.setBytes(testValue);
			for (int i=0; i<testValue.length; i++){
				assertEquals(testValue[i] & 0xFF, addressAttribute.getBytes()[i] & 0xFF);
			}

			assertEquals(testValue.length, addressAttribute.getBytes().length);
			assertEquals(address, addressAttribute.getStringValue());
			
			addressAttribute.setBytes(null);
			assertNotNull(addressAttribute.getBytes());
	
			for (int i=0; i<testValue.length; i++){
				assertEquals(testValue[i] & 0xFF, addressAttribute.getBytes()[i] & 0xFF);
			}
			assertEquals(testValue.length, addressAttribute.getBytes().length);
			
			byte[] testValue2Length = new byte[]{
													1, //type byte
													2, //length byte
												};
			addressAttribute.setBytes(testValue2Length);
			for (int i=0; i<testValue.length; i++){
				assertEquals(testValue[i] & 0xFF, addressAttribute.getBytes()[i] & 0xFF);
			}
			assertEquals(testValue.length, addressAttribute.getBytes().length);
			
		}catch(Exception e){
			e.printStackTrace();
			fail("testGetSetBytes failed, reason : " + e.getMessage());
		}
	}
	
	@Test
	public void testReadFrom(){
		try {
			byte []attributeDataMinimum = { 1, //type byte
									 18, //length byte
									 0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,1 //value= ::1
								   };
			ByteArrayInputStream inStream = new ByteArrayInputStream(attributeDataMinimum);
			AddressAttribute addressAttribute = new AddressAttribute(fakeAddressResolver);
			addressAttribute.readFrom(inStream);

			assertEquals(1, addressAttribute.getType());
			assertEquals(18, addressAttribute.getLength());
			assertEquals("0:0:0:0:0:0:0:1", addressAttribute.getStringValue());
						
			String address = "0:0:0:0:0:0:0:1";
			byte [] attributeDataNormal = new byte[18];
			attributeDataNormal[0] = 1; //type byte
			attributeDataNormal[1] = 18; //length byte
			//16-bytes value
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
			attributeDataNormal[6] = temp[4];
			attributeDataNormal[7] = temp[5];
			attributeDataNormal[8] = temp[6];
			attributeDataNormal[9] = temp[7];
			attributeDataNormal[10] = temp[8];
			attributeDataNormal[11] = temp[9];
			attributeDataNormal[12] = temp[10];
			attributeDataNormal[13] = temp[11];
			attributeDataNormal[14] = temp[12];
			attributeDataNormal[15] = temp[13];
			attributeDataNormal[16] = temp[14];
			attributeDataNormal[17] = temp[15];

			inStream = new ByteArrayInputStream(attributeDataNormal);
			
			addressAttribute = new AddressAttribute(fakeAddressResolver);
			addressAttribute.readFrom(inStream);
			
			assertEquals(1, addressAttribute.getType());
			assertEquals(18, addressAttribute.getLength());
			assertEquals(address, addressAttribute.getStringValue());
						
		}catch(Exception e){
			e.printStackTrace();
			fail("readFrom for IntegerAttribute failed, reason : " + e.getMessage());
		}
	}
	
}
