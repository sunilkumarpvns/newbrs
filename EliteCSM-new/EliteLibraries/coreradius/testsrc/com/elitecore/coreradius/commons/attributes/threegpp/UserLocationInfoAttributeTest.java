package com.elitecore.coreradius.commons.attributes.threegpp;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.StringReader;
import java.util.Arrays;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.RadiusUtility;

public class UserLocationInfoAttributeTest {

	static String tgppUserlocationInfoAttrDict = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><attribute-list vendorid=\"10415\" vendor-name=\"3GPP\" avpair-separator=\",\"><attribute id=\"22\" name=\"3GPP-User-Location-Info\" type=\"UserLocationInfo\" ignore-case=\"true\"/></attribute-list>";

	@BeforeClass
	public static void setUpBeforeClass() {
		try {
			StringReader reader = new StringReader(tgppUserlocationInfoAttrDict);
			Dictionary.getInstance().load(reader);
		} catch (Exception e) {
			Assert.fail();
			e.printStackTrace();
		}
	}
	
	@Test
	public void testSetStringValue(){
		
		//POSITIVE TEST CASES
		byte[] CGItestValue = {(byte) 0x00,(byte) 0xF0,(byte) 0xFF,(byte) 0xF1,(byte) 0x01,(byte) 0x01,(byte)0x01,(byte)0x01};
		UserLocationInfoAttribute attr1 = new UserLocationInfoAttribute();
		UserLocationInfoAttribute attr2 = new UserLocationInfoAttribute();
		attr2.setStringValue("type = 0,type = 1,,,,, mcc = 0 , mnc = 1 ,	lac	=	257	,	ci	=	257	");
		attr1.setValueBytes(CGItestValue);
		
		assertEquals(RadiusUtility.bytesToHex(CGItestValue),RadiusUtility.bytesToHex(attr2.getValueBytes()));
		
		//System.out.println(attr2);
		
		byte[] SAItestValue = {(byte) 0x01,0x21,(byte) 0x63,0x42,(byte) 0x01,(byte) 0x01,(byte)0x01,(byte)0x01};
		UserLocationInfoAttribute attr3 = new UserLocationInfoAttribute();
		attr3.setValueBytes(SAItestValue);
		UserLocationInfoAttribute attr4 = new UserLocationInfoAttribute();
		attr4.setStringValue("type=1,mcc=123,mnc=246,lac=257,sac=257");
		assertEquals(RadiusUtility.bytesToHex(SAItestValue),RadiusUtility.bytesToHex(attr4.getValueBytes()));
		
		
		
		
		byte[] RAItestValue = {(byte) 0x02,0x21,(byte) 0x63,0x42,(byte) 0x27,(byte) 0x0F,(byte)0x01,(byte)0xFF};
		UserLocationInfoAttribute attr5 = new UserLocationInfoAttribute();
		attr5.setValueBytes(RAItestValue);
		UserLocationInfoAttribute attr6 = new UserLocationInfoAttribute();
		attr6.setStringValue("type=2,mcc=123,mnc=246,lac=9999,rac=0001");
		assertEquals(RadiusUtility.bytesToHex(RAItestValue),RadiusUtility.bytesToHex(attr6.getValueBytes()));
		
		//System.out.println(attr6.getStringValue());
		
		
		byte[] TAItestValue = {(byte) 0x80,0x21,(byte) 0x63,0x42,(byte) 0x01,(byte) 0x01};
		UserLocationInfoAttribute attr7 = new UserLocationInfoAttribute();
		attr7.setValueBytes(TAItestValue);
		UserLocationInfoAttribute attr8 = new UserLocationInfoAttribute();
		attr8.setStringValue("type=128,mcc=123,mnc=246,tac=257");
		assertEquals(RadiusUtility.bytesToHex(TAItestValue),RadiusUtility.bytesToHex(attr8.getValueBytes()));
		
		
		
		byte[] ECGItestValue = {(byte) 0x81,0x21,(byte) 0x63,0x42,(byte) 0x01,(byte) 0x01,(byte)0x01};
		UserLocationInfoAttribute attr9 = new UserLocationInfoAttribute();
		attr9.setValueBytes(ECGItestValue);
		UserLocationInfoAttribute attr10 = new UserLocationInfoAttribute();
		attr10.setStringValue("type=129,mcc=123,mnc=246,spare=0,eci=65793");
		assertEquals(RadiusUtility.bytesToHex(ECGItestValue),RadiusUtility.bytesToHex(attr10.getValueBytes()));
		
		
		byte[] ECGI_TAItestValue = {(byte) 0x82,0x21,(byte) 0x63,0x42,(byte) 0x01,(byte) 0x01,0x21,(byte) 0x63,0x42,(byte)0x00, (byte) 0x01,(byte) 0x01,(byte)0x01};
		UserLocationInfoAttribute attr11 = new UserLocationInfoAttribute();
		attr11.setValueBytes(ECGI_TAItestValue);
		UserLocationInfoAttribute attr12 = new UserLocationInfoAttribute();
		attr12.setStringValue("type=130,ecgimcc=123,ecgimnc=246,ecgispare=0,ecgieci=65793,taimcc=123,taimnc=246,taitac=257");
		assertEquals(RadiusUtility.bytesToHex(ECGI_TAItestValue),RadiusUtility.bytesToHex(attr12.getValueBytes()));
		
		
		//NEGETIVE TEST CASES
		//CGI Type
		
		//Test case 1 : The value of MCC is 3
		try{
			byte[] CGItestValue1 = {(byte) 0x00,(byte) 0x00,(byte) 0x63,0x42,(byte) 0x01,(byte) 0x01,(byte)0x01,(byte)0x01};
			attr1 = new UserLocationInfoAttribute();
			attr1.setValueBytes(CGItestValue1);
		}catch(IllegalArgumentException ex){
			fail();
		}
		
		/*
		 * This test case is now invalidated
		 * //Test case 2: The value of MCC is 1000
		try{
			byte[] CGItestValue1 = {(byte) 0x00,(byte) 0xFF,(byte) 0x6F,0x42,(byte) 0x01,(byte) 0x01,(byte)0x01,(byte)0x01};
			attr1 = new UserLocationInfoAttribute();
			attr1.setValueBytes(CGItestValue1);
			fail("The value of mcc is greater than 999. But the test case passed");
		}catch(IllegalArgumentException ex){

		}*/
		
		//Test case 3: The value of MCC is 0
		try{
			byte[] CGItestValue1 = {(byte) 0x00,(byte) 0x00,(byte) 0x60,0x42,(byte) 0x01,(byte) 0x01,(byte)0x01,(byte)0x01};
			attr1 = new UserLocationInfoAttribute();
			attr1.setValueBytes(CGItestValue1);
		}catch(IllegalArgumentException ex){}
		
		//Test case 4: The no of bytes are smaller than required
		try{
			byte[] CGItestValue1 = {(byte) 0x00,(byte) 0x00,(byte) 0x60,0x42,(byte) 0x01,(byte) 0x01,(byte)0x01};
			attr1 = new UserLocationInfoAttribute();
			attr1.setValueBytes(CGItestValue1);
			fail("The no of bytes are less than required for CGI Location type.");
		}catch(IllegalArgumentException ex){}
		
		try{
			byte[] CGItestValue1 = RadiusUtility.getBytesFromHexValue("0x82042420123404242001234567");
			attr1 = new UserLocationInfoAttribute();
			attr1.setValueBytes(CGItestValue1);		
			
			System.out.println("Success:" + attr1.getStringValue());
			System.out.println(RadiusUtility.bytesToHex(attr1.getValueBytes()));
			Assert.assertEquals(true, Arrays.equals(attr1.getValueBytes(), CGItestValue1));
		}catch(IllegalArgumentException ex){}
		
		//Test for UNKNOWN attribute
		byte[] UnknowntestValue = {(byte) 0xFF,(byte) 0x00,(byte) 0x60,0x42,(byte) 0x01,(byte) 0x01,(byte)0x01};
		attr1 = new UserLocationInfoAttribute();
		attr1.setValueBytes(UnknowntestValue);
	}
	
	@Test
	public void testGetKeyValue(){
		//Positive test cases
		UserLocationInfoAttribute attr2 = new UserLocationInfoAttribute();
		attr2.setStringValue("type=0,mcc=123,mnc=246,lac=257,ci=257");
		assertEquals("123", attr2.getKeyValue("mcc"));
		assertEquals("246", attr2.getKeyValue("mnc"));
		assertEquals("257", attr2.getKeyValue("lac"));
		assertEquals("257", attr2.getKeyValue("ci"));
		
		
		UserLocationInfoAttribute attr4 = new UserLocationInfoAttribute();
		attr4.setStringValue("type=1,mcc=123,mnc=246,lac=257,sac=257");
		assertEquals("123", attr4.getKeyValue("mcc"));
		assertEquals("246", attr4.getKeyValue("mnc"));
		assertEquals("257", attr4.getKeyValue("lac"));
		assertEquals("257", attr4.getKeyValue("sac"));
		
		
		UserLocationInfoAttribute attr6 = new UserLocationInfoAttribute();
		attr6.setStringValue("type=2,mcc=123,mnc=246,lac=257,rac=255");
		assertEquals("123", attr6.getKeyValue("mcc"));
		assertEquals("246", attr6.getKeyValue("mnc"));
		assertEquals("257", attr6.getKeyValue("lac"));
		assertEquals("255", attr6.getKeyValue("rac"));
		
		
		UserLocationInfoAttribute attr8 = new UserLocationInfoAttribute();
		attr8.setStringValue("type=128,mcc=123,mnc=246,tac=257");
		assertEquals("123", attr8.getKeyValue("mcc"));
		assertEquals("246", attr8.getKeyValue("mnc"));
		assertEquals("257", attr8.getKeyValue("tac"));
		
		
		UserLocationInfoAttribute attr10 = new UserLocationInfoAttribute();
		attr10.setStringValue("type=129,mcc=123,mnc=246,spare=0,eci=65793");
		assertEquals("123", attr10.getKeyValue("mcc"));
		assertEquals("246", attr10.getKeyValue("mnc"));
		assertEquals("0", attr10.getKeyValue("spare"));
		assertEquals("65793", attr10.getKeyValue("eci"));
		
		
		UserLocationInfoAttribute attr12 = new UserLocationInfoAttribute();
		attr12.setStringValue("type=130,ecgimcc=123,ecgimnc=246,ecgispare=0,ecgieci=65793,taimcc=123,taimnc=246,taitac=257");
		assertEquals("123", attr12.getKeyValue("taimcc"));
		assertEquals("246", attr12.getKeyValue("taimnc"));
		assertEquals("257", attr12.getKeyValue("taitac"));
		assertEquals("123", attr12.getKeyValue("ecgimcc"));
		assertEquals("246", attr12.getKeyValue("ecgimnc"));
		assertEquals("0", attr12.getKeyValue("ecgispare"));
		assertEquals("65793", attr12.getKeyValue("ecgieci"));
		
		//Negative test cases
		//TODO here if the value of MCC and MNC is not available then the 
		//value previously sent was 0 but now it is sending ""
		attr2 = new UserLocationInfoAttribute();
		attr2.setStringValue("type=0,mnc=246,lac=257,ci=257");
		assertEquals("", attr2.getKeyValue("mcc"));
		assertEquals("246", attr2.getKeyValue("mnc"));
		assertEquals("257", attr2.getKeyValue("lac"));
		assertEquals("257", attr2.getKeyValue("ci"));
		
		attr2 = new UserLocationInfoAttribute();
		attr2.setStringValue("type=0,mcc=123,lac=257,ci=257");
		assertEquals("123", attr2.getKeyValue("mcc"));
		assertEquals("", attr2.getKeyValue("mnc"));
		assertEquals("257", attr2.getKeyValue("lac"));
		assertEquals("257", attr2.getKeyValue("ci"));
		
		attr2 = new UserLocationInfoAttribute();
		attr2.setStringValue("type=0");
		assertEquals("", attr2.getKeyValue("mcc"));
		assertEquals("", attr2.getKeyValue("mnc"));
		assertEquals("0", attr2.getKeyValue("lac"));
		assertEquals("0", attr2.getKeyValue("ci"));
	}
	
	@Test
	public void testWithIgnoreValuesForMNCandMCC(){
		byte[] cgiBytes = {0x00,0x15,(byte) 0xf0,0x11,0x5f,0x29,(byte) 0xea,(byte) 0xb2};
		UserLocationInfoAttribute attribute = new UserLocationInfoAttribute();
		attribute.setValueBytes(cgiBytes);
		byte[] valueBytes = attribute.getValueBytes();
		assertEquals(true, Arrays.equals(cgiBytes, valueBytes));
	}
	
	@Test
	public void testWiresharkValue(){
		byte[] SAItestValueFromWireshark = RadiusUtility.getBytesFromHexValue("0x0182f620281fdaaf");
		UserLocationInfoAttribute attr31 = new UserLocationInfoAttribute();
		attr31.setValueBytes(SAItestValueFromWireshark);
		//System.out.println(RadiusUtility.bytesToHex(attr31.getValueBytes()));
		UserLocationInfoAttribute attr41 = new UserLocationInfoAttribute();
		attr41.setStringValue("type=1,mcc=286,mnc=02,lac=10271,sac=55983");
		//System.out.println(RadiusUtility.bytesToHex(attr41.getValueBytes()));
		assertEquals(RadiusUtility.bytesToHex(SAItestValueFromWireshark),RadiusUtility.bytesToHex(attr41.getValueBytes()));
}
	
	@Test
	public void testSingleDigitMNCValue(){
		byte[] SAItestValueFromWireshark = RadiusUtility.getBytesFromHexValue("0x0182f6f2281fdaaf");
		UserLocationInfoAttribute attr31 = new UserLocationInfoAttribute();
		attr31.setValueBytes(SAItestValueFromWireshark);
		//System.out.println(RadiusUtility.bytesToHex(attr31.getValueBytes()));
		UserLocationInfoAttribute attr41 = new UserLocationInfoAttribute();
		attr41.setStringValue("type=1,mcc=286,mnc=2,lac=10271,sac=55983");
		//System.out.println(RadiusUtility.bytesToHex(attr41.getValueBytes()));
		assertEquals(RadiusUtility.bytesToHex(SAItestValueFromWireshark),RadiusUtility.bytesToHex(attr41.getValueBytes()));
	}
	
	@Test
	public void testRACValueWithTrailingAllOnesAccordingToStandardWithinLimit(){
		UserLocationInfoAttribute attr41 = new UserLocationInfoAttribute();
		attr41.setStringValue("type=2,mcc=123,mnc=246,lac=257,rac=1");
		//System.out.println(RadiusUtility.bytesToHex(attr41.getValueBytes()));
		assertEquals("0x02216342010101ff", RadiusUtility.bytesToHex(attr41.getValueBytes()));
	}
	
	@Test
	public void testRACValueWithTrailingAllOnesAccordingToStandardOutsideLimit(){
		UserLocationInfoAttribute attr41 = new UserLocationInfoAttribute();
		try{
			attr41.setStringValue("type=2,mcc=123,mnc=246,lac=257,rac=257");
			fail("The value of RAC is greater than allowed based on standard, but test case is passing!!!!");
		}catch (IllegalArgumentException e) {
			//proper
		}
	}
	
	@Test
	public void testECGI_TAI_JIRA_ELITEAAA2646() {
		byte[] attributeBytes = RadiusUtility.getBytesFromHexValue("0x160f82042420123404242001234567");
		UserLocationInfoAttribute attribute = new UserLocationInfoAttribute();
		attribute.setBytes(attributeBytes);

		byte[] returnBytes = attribute.getBytes();
		org.junit.Assert.assertArrayEquals(attributeBytes, returnBytes);
	}
}
