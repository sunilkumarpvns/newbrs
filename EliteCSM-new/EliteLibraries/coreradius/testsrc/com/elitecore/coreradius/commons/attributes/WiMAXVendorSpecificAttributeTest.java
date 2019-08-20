package com.elitecore.coreradius.commons.attributes;

import java.io.ByteArrayInputStream;
import java.util.Arrays;

import junit.framework.TestSuite;

import com.elitecore.coreradius.BaseRadiusTestCase;
import com.elitecore.coreradius.commons.packet.RadiusPacket;
import com.elitecore.coreradius.commons.util.RadiusUtility;

public class WiMAXVendorSpecificAttributeTest extends BaseRadiusTestCase {

	public WiMAXVendorSpecificAttributeTest(String name) {
		super(name);
	}

	public static TestSuite suite() {
		TestSuite suite = new TestSuite();
		//suite.addTest(new WiMAXVendorSpecificAttributeTest("testReadFrom"));
		suite.addTest(new WiMAXVendorSpecificAttributeTest("testWimaxAttribute"));
	   	return suite;
	}
	public void testWimaxAttribute(){
		RadiusPacket radiusPacket = new RadiusPacket();
		try{
			//vendorSpecificAttribute = new VendorSpecificAttribute();			
			byte[] WIMAX_RADIUS_PACKET = {
					1,					// radius packet type - access request
					1, 					// identifier
					0, 60,				//length of radius packet
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,	// authenticator
					//RADIUS VENDOR SPECIFIC ATTRIBUTE
					26,					// radius attribute type - vendor specific
					25,					// length of radius attribute
					0, 0, 96, (byte)181, //Wimax Vendor ID 
					1, 				//attribute type - wimax capability attribute
					23,					// length 
					0,					// continuation byte
					//sub attribute
					1,					// wimax release
					4,					// length of sub attribute
					0, 1,				// value
					// sub attribute
					2,					// accounting capabilities
					4,					//length
					0, 3,				// value
					//sub attribute
					3,					// hotlining capabilities
					4,					// length
					0, 0,				// value
					//sub attribute
					4,					// idle mode notification capabilities
					4,					// length
					0,1,					// value		

					//RADIUS VENDOR SPECIFIC ATTRIBUTE
					26,					// radius attribute type - vendor specific
					0x0F,					// length of radius attribute
					0, 0, 96, (byte)181, //Wimax Vendor ID 
					0x23, 				//attribute type - wimax capability attribute
					0x09,					// length 
					0x00,					// continuation byte
					//sub attribute
					1,					// wimax release
					0x06,					// length of sub attribute
					0x00,0x00,0x00, 0x63,				// value										
			};
			
			System.out.println("total length: " + WIMAX_RADIUS_PACKET.length);
			ByteArrayInputStream in = new ByteArrayInputStream(WIMAX_RADIUS_PACKET);
			radiusPacket.readFrom(in);
			assertTrue(Arrays.equals(WIMAX_RADIUS_PACKET, radiusPacket.getBytes()));
			System.out.println("radius packet: " + RadiusUtility.bytesToHex(radiusPacket.getBytes()));
		}catch(Exception e ){
			e.printStackTrace();
		}
	}
	public void testReadFrom(){
		VendorSpecificAttribute vendorSpecificAttribute = null;
		try{
			vendorSpecificAttribute = new VendorSpecificAttribute();
			byte[] bWimaxVendorAttribute = new byte[]{14, 0, 0, 96, (byte)178, 40, 6, 0 & 0xFF, 1, 1, 'j', 'a', 'y'};
			ByteArrayInputStream inStream = new ByteArrayInputStream(bWimaxVendorAttribute);
			int i = vendorSpecificAttribute.readLengthOnwardsFrom(inStream);
			System.out.println("Read From :: " + i);
			System.out.println("Check  : "  + (vendorSpecificAttribute.getAttribute(40)).getClass());
			System.out.println("Check  : "  + ((WiMAXVendorSpecificAttribute)(vendorSpecificAttribute.getAttribute(40))).getStringValue());
//			byte bVSAByte = 26;
//			byte bLength = 0;
//			byte[] bVendorIDBytes = new byte[]{0, 0, 95, -128};
//			byte bWimaxType = 40;
//			byte bWimaxLength = 6;
//			byte bContinuationByte = 0 & 0xFF;
//			byte[] bSalt = new byte[]{1,1};
//			byte[] bKey = new byte[]{'j','a','y'};
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
}
