package com.elitecore.coreradius.commons.packet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.BeforeClass;
import org.junit.Test;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.commons.logging.NullLogger;
import com.elitecore.coreradius.commons.attributes.BaseRadiusAttribute;
import com.elitecore.coreradius.commons.attributes.GeneralVendorSpecificAttribute;
import com.elitecore.coreradius.commons.attributes.GroupedAttribute;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.attributes.IVendorSpecificAttribute;
import com.elitecore.coreradius.commons.attributes.StringAttribute;
import com.elitecore.coreradius.commons.attributes.VendorSpecificAttribute;
import com.elitecore.coreradius.commons.attributes.WiMAXVendorSpecificAttribute;
import com.elitecore.coreradius.commons.attributes.wimaxattribute.WiMAXStringAttribute;
import com.elitecore.coreradius.commons.attributes.wimaxattribute.WimaxGroupedAttribute;
import com.elitecore.coreradius.commons.packet.RadiusPacket;
import com.elitecore.coreradius.commons.util.RadiusUtility;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;

public class OldRadiusPacketTest {

	@BeforeClass
	public static void setUpBeforeClass() {
		LogManager.setDefaultLogger(new NullLogger());
	}

	@Test
	public void testZeroLengthRadiusPacket(){
		RadiusPacket radiusPacket = new RadiusPacket();
		radiusPacket.refreshPacketHeader();
		radiusPacket.refreshInfoPacketHeader();
		assertEquals("Radius Packet default length MUST be 20",
				RadiusPacket.DEFAULT_RADIUS_PACKET_LENGTH, radiusPacket.getLength());
		assertEquals("Radius Packet info length MUST be 0", 0, radiusPacket.getInfoLength());
	}

	@Test
	public void testGetSetAttribute(){
		final String DEFAULT_USER_NAME = "eliteaaa";
		RadiusPacket radiusPacket = new RadiusPacket();
		StringAttribute strAttribute = new StringAttribute();
		strAttribute.setType(RadiusAttributeConstants.USER_NAME);
		strAttribute.setStringValue(DEFAULT_USER_NAME);
		radiusPacket.addAttribute(strAttribute);
		radiusPacket.refreshPacketHeader();
		IRadiusAttribute radAttr = radiusPacket.getRadiusAttribute(RadiusAttributeConstants.USER_NAME);
		assertNotNull(radAttr);
		assertEquals("Radius Packet resultant attribute value must be " + DEFAULT_USER_NAME,DEFAULT_USER_NAME,radAttr.getStringValue());
	}

	@Test
	public void testGetSetVSA(){
		final String DEFAULT_USER_NAME = "eliteaaa";			
		RadiusPacket radiusPacket = new RadiusPacket();
		VendorSpecificAttribute vsaAttribute = new VendorSpecificAttribute();
		vsaAttribute.setVendorID(RadiusConstants.ELITECORE_VENDOR_ID);
		IVendorSpecificAttribute vsaType = new GeneralVendorSpecificAttribute();
		IRadiusAttribute radiusAttribute = new StringAttribute();
		radiusAttribute.setType(RadiusAttributeConstants.USER_NAME);
		radiusAttribute.setStringValue(DEFAULT_USER_NAME);
		vsaType.addAttribute(radiusAttribute);
		vsaType.refreshAttributeHeader();
		vsaAttribute.setVendorTypeAttribute(vsaType);
		vsaAttribute.refreshAttributeHeader();
		radiusPacket.addAttribute(vsaAttribute);

		IRadiusAttribute radAttr = radiusPacket.getRadiusAttribute(RadiusConstants.ELITECORE_VENDOR_ID, RadiusAttributeConstants.USER_NAME);
		assertNotNull(radAttr);
		assertEquals("Radius Packet resultant attribute value must be " + DEFAULT_USER_NAME,DEFAULT_USER_NAME,radAttr.getStringValue());
	}

	@Test
	public void testGetSetInfoAttr(){
		final String DEFAULT_USER_NAME = "eliteaaa";
		RadiusPacket radiusPacket = new RadiusPacket();
		StringAttribute strAttribute = new StringAttribute();
		strAttribute.setType(RadiusAttributeConstants.USER_NAME);
		strAttribute.setStringValue(DEFAULT_USER_NAME);
		radiusPacket.addInfoAttribute(strAttribute);
		radiusPacket.refreshPacketHeader();
		IRadiusAttribute radAttr = radiusPacket.getRadiusAttribute(RadiusAttributeConstants.USER_NAME);

		assertNull(radAttr);

		radAttr = radiusPacket.getRadiusAttribute(RadiusAttributeConstants.USER_NAME, false);

		assertNull(radAttr);

		radAttr = radiusPacket.getRadiusAttribute(RadiusAttributeConstants.USER_NAME,true);

		assertNotNull(radAttr);
		assertEquals("Radius Packet resultant attribute value must be " + DEFAULT_USER_NAME,DEFAULT_USER_NAME,radAttr.getStringValue());			
	}

	@Test
	public void testGetSetInfoVSA(){
		final String DEFAULT_USER_NAME = "eliteaaa";			
		RadiusPacket radiusPacket = new RadiusPacket();
		VendorSpecificAttribute vsaAttribute = new VendorSpecificAttribute();
		vsaAttribute.setVendorID(RadiusConstants.ELITECORE_VENDOR_ID);
		IVendorSpecificAttribute vsaType = new GeneralVendorSpecificAttribute();
		IRadiusAttribute radiusAttribute = new StringAttribute();
		radiusAttribute.setType(RadiusAttributeConstants.USER_NAME);
		radiusAttribute.setStringValue(DEFAULT_USER_NAME);
		vsaType.addAttribute(radiusAttribute);
		vsaType.refreshAttributeHeader();
		vsaAttribute.setVendorTypeAttribute(vsaType);
		vsaAttribute.refreshAttributeHeader();
		radiusPacket.addInfoAttribute(vsaAttribute);

		IRadiusAttribute radAttr = radiusPacket.getRadiusAttribute(RadiusConstants.ELITECORE_VENDOR_ID, RadiusAttributeConstants.USER_NAME);
		assertNull(radAttr);

		radAttr = radiusPacket.getRadiusAttribute(RadiusConstants.ELITECORE_VENDOR_ID, false, RadiusAttributeConstants.USER_NAME);
		assertNull(radAttr);

		radAttr = radiusPacket.getRadiusAttribute(RadiusConstants.ELITECORE_VENDOR_ID, true, RadiusAttributeConstants.USER_NAME);
		assertNotNull(radAttr);
		assertEquals("Radius Packet resultant attribute value must be " + DEFAULT_USER_NAME,DEFAULT_USER_NAME,radAttr.getStringValue());			

	}

	@Test
	public void testRefreshPacketHeader(){
		final String DEFAULT_USER_NAME = "eliteaaa";
		final int EXPECTED_LENGTH = 30;
		RadiusPacket radiusPacket = new RadiusPacket();
		radiusPacket.refreshPacketHeader();
		radiusPacket.refreshInfoPacketHeader();
		assertEquals("Radius Packet default length MUST be 20",RadiusPacket.DEFAULT_RADIUS_PACKET_LENGTH,radiusPacket.getLength());
		assertEquals("Radius Packet info length MUST be 0",0,radiusPacket.getInfoLength());
		StringAttribute strAttribute = new StringAttribute();
		strAttribute.setType(RadiusAttributeConstants.USER_NAME);
		strAttribute.setStringValue(DEFAULT_USER_NAME);
		radiusPacket.addAttribute(strAttribute);
		radiusPacket.refreshPacketHeader();
		assertEquals("Radius Packet default length MUST be "+EXPECTED_LENGTH,EXPECTED_LENGTH,radiusPacket.getLength());
		assertEquals("Radius Packet info length MUST be 0",0,radiusPacket.getInfoLength());
	}

	@Test
	public void testRefreshInfoPacketHeader(){
		final String DEFAULT_USER_NAME = "eliteaaa";
		final int EXPECTED_LENGTH = 10;
		RadiusPacket radiusPacket = new RadiusPacket();
		radiusPacket.refreshPacketHeader();
		radiusPacket.refreshInfoPacketHeader();
		assertEquals("Radius Packet default length MUST be 20",RadiusPacket.DEFAULT_RADIUS_PACKET_LENGTH,radiusPacket.getLength());
		assertEquals("Radius Packet info length MUST be 0",0,radiusPacket.getInfoLength());
		StringAttribute strAttribute = new StringAttribute();
		strAttribute.setType(RadiusAttributeConstants.USER_NAME);
		strAttribute.setStringValue(DEFAULT_USER_NAME);
		radiusPacket.addInfoAttribute(strAttribute);
		radiusPacket.refreshInfoPacketHeader();
		assertEquals("Radius Packet default length MUST be "+RadiusPacket.DEFAULT_RADIUS_PACKET_LENGTH,RadiusPacket.DEFAULT_RADIUS_PACKET_LENGTH,radiusPacket.getLength());
		assertEquals("Radius Packet info length MUST be "+EXPECTED_LENGTH,EXPECTED_LENGTH,radiusPacket.getInfoLength());
	}

	@Test
	public void testGetSetClientIP(){
		final String VALID_IP = "127.0.0.1";
		final String INVALID_IP = "270.70.70.027";			
		final String ALPHABETIC_IP = "a.b.c.d";
		RadiusPacket radiusPacket = new RadiusPacket();
		radiusPacket.setClientIP(VALID_IP);
		assertEquals("Radius Packet IP MUST be "+ VALID_IP,VALID_IP,radiusPacket.getClientIP());
		radiusPacket.setClientIP(INVALID_IP);
		assertEquals("Radius Packet IP MUST be "+INVALID_IP,INVALID_IP,radiusPacket.getClientIP());
		radiusPacket.setClientIP(ALPHABETIC_IP);
		assertEquals("Radius Packet IP MUST be "+ALPHABETIC_IP,ALPHABETIC_IP,radiusPacket.getClientIP());					
	}

	@Test
	public void testGetSetClientPort(){
		final int VALID_PORT = 8080;
		final int NEGATIVE_PORT = -1;			
		RadiusPacket radiusPacket = new RadiusPacket();
		radiusPacket.setClientPort(VALID_PORT);
		assertEquals("Radius Packet Port MUST be "+ VALID_PORT,VALID_PORT,radiusPacket.getClientPort());
		radiusPacket.setClientPort(NEGATIVE_PORT);
		assertEquals("Radius Packet Port MUST be "+NEGATIVE_PORT,NEGATIVE_PORT,radiusPacket.getClientPort());			
	}

	@Test
	public void testGetSetPacketType(){
		final int NEGATIVE_TYPE = -1;
		RadiusPacket radiusPacket = new RadiusPacket();
		radiusPacket.setPacketType(RadiusConstants.ACCESS_ACCEPT_MESSAGE);
		assertEquals("Radius Packet Packet Type MUST be "+ RadiusConstants.ACCESS_ACCEPT_MESSAGE,RadiusConstants.ACCESS_ACCEPT_MESSAGE,radiusPacket.getPacketType());
		radiusPacket.setPacketType(NEGATIVE_TYPE);
		assertEquals("Radius Packet Packet Type MUST be "+NEGATIVE_TYPE,NEGATIVE_TYPE,radiusPacket.getPacketType());
	}

	@Test
	public void testGetSetIdentifier(){
		final int DEFAULT_IDENTIFIER = 1;
		final int NEGATIVE_IDENTIFIER = -1;
		RadiusPacket radiusPacket = new RadiusPacket();
		radiusPacket.setIdentifier(DEFAULT_IDENTIFIER);
		assertEquals("Radius Packet identifier MUST be "+ DEFAULT_IDENTIFIER,DEFAULT_IDENTIFIER,radiusPacket.getIdentifier());
		radiusPacket.setIdentifier(NEGATIVE_IDENTIFIER);
		assertEquals("Radius Packet identifier MUST be "+ NEGATIVE_IDENTIFIER,NEGATIVE_IDENTIFIER,radiusPacket.getIdentifier());
	}

	@Test
	public void testGetSetAuthenticator(){
		final byte[] DEFUALT_AUTHENTICATOR = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15};
		final byte[] NEGATIVE_AUTHENTICATOR = {0,-1,-2,-3,-4,-5,-6,-7,-8,-9,-10,-11,-12,-13,-14,-15};
		final byte[] INVALID_PARTIAL_AUTHENTICATOR = {0,1,2,3,4,5,6,7,8,9,10};
		final byte[] EXPECTED_PADDED_AUTHENTICATOR = {0,1,2,3,4,5,6,7,8,9,10,0,0,0,0,0};
		final byte[] INVALID_LARGE_AUTHENTICATOR = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18,19,20};
		final byte[] EXPECTED_TRUNCATED_AUTHENTICATOR = {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15};

		RadiusPacket radiusPacket = new RadiusPacket();
		radiusPacket.setAuthenticator(DEFUALT_AUTHENTICATOR);
		assertEquals("Radius Packet authenticator MUST be {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15}",true,Arrays.equals(DEFUALT_AUTHENTICATOR, radiusPacket.getAuthenticator()));
		radiusPacket.setAuthenticator(NEGATIVE_AUTHENTICATOR);
		assertEquals("Radius Packet authenticator MUST be {0,-1,-2,-3,-4,-5,-6,-7,-8,-9,-10,-11,-12,-13,-14,-15}",RadiusUtility.bytesToHex(NEGATIVE_AUTHENTICATOR),RadiusUtility.bytesToHex(radiusPacket.getAuthenticator()));
		radiusPacket.setAuthenticator(INVALID_PARTIAL_AUTHENTICATOR);
		assertEquals("Radius Packet authenticator MUST be {0,1,2,3,4,5,6,7,8,9,10,0,0,0,0,0}",RadiusUtility.bytesToHex(EXPECTED_PADDED_AUTHENTICATOR),RadiusUtility.bytesToHex(radiusPacket.getAuthenticator()));
		radiusPacket.setAuthenticator(INVALID_LARGE_AUTHENTICATOR);
		assertEquals("Radius Packet authenticator MUST be {0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15}",RadiusUtility.bytesToHex(EXPECTED_TRUNCATED_AUTHENTICATOR),RadiusUtility.bytesToHex(radiusPacket.getAuthenticator()));					
	}

	@Test
	public void testClone() throws CloneNotSupportedException{
		final String DEFAULT_USER_NAME = "eliteaaa";
		final String DEFAULT_INFO_USER_NAME = "eliteaaaInfo";
		RadiusPacket radiusPacket = new RadiusPacket();

		StringAttribute strAttribute = new StringAttribute();
		strAttribute.setType(RadiusAttributeConstants.USER_NAME);
		strAttribute.setStringValue(DEFAULT_USER_NAME);
		radiusPacket.addAttribute(strAttribute);
		radiusPacket.refreshPacketHeader();

		StringAttribute strInfoAttribute = new StringAttribute();
		strInfoAttribute.setType(RadiusAttributeConstants.CUI);
		strInfoAttribute.setStringValue(DEFAULT_INFO_USER_NAME);
		radiusPacket.addInfoAttribute(strInfoAttribute);
		radiusPacket.refreshInfoPacketHeader();

		RadiusPacket clonePacket = (RadiusPacket) radiusPacket.clone();
		IRadiusAttribute radAttr = clonePacket.getRadiusAttribute(RadiusAttributeConstants.USER_NAME);
		assertNotNull(radAttr);
		assertEquals("Radius Packet resultant attribute value must be " + DEFAULT_USER_NAME,DEFAULT_USER_NAME,radAttr.getStringValue());

		IRadiusAttribute radInfoAttr = clonePacket.getRadiusAttribute(RadiusAttributeConstants.CUI,true);
		assertNotNull(radInfoAttr);
		assertEquals("Radius Packet resultant attribute value must be " + DEFAULT_INFO_USER_NAME,DEFAULT_INFO_USER_NAME,radInfoAttr.getStringValue());			
	}

	@Test
	public void testRemoveAttribute(){
		RadiusPacket radiusPacket = new RadiusPacket();

		//1. To remove String Type Attribute .
		IRadiusAttribute nonGroupedAttrToRemove = new StringAttribute();
		nonGroupedAttrToRemove.setType(RadiusAttributeConstants.USER_NAME);
		nonGroupedAttrToRemove.setStringValue("User1");

		radiusPacket.addAttribute(nonGroupedAttrToRemove);
		radiusPacket.removeAttribute(nonGroupedAttrToRemove);

		assertFalse(radiusPacket.getRadiusAttributes().contains(nonGroupedAttrToRemove));

		//2. To Remove General Vendor Specific Grouped Attribute .
		IVendorSpecificAttribute generalVSGAttrToRemove = new GeneralVendorSpecificAttribute();
		generalVSGAttrToRemove.addAttribute(nonGroupedAttrToRemove);
		generalVSGAttrToRemove.refreshAttributeHeader();

		radiusPacket.addAttribute(generalVSGAttrToRemove);
		radiusPacket.removeAttribute(generalVSGAttrToRemove);

		assertFalse(radiusPacket.getRadiusAttributes().contains(generalVSGAttrToRemove));

		//3. To remove String Type Attribute from packet with different reference of same value .
		radiusPacket.addAttribute(nonGroupedAttrToRemove);

		IRadiusAttribute nonGroupedAttrWithSameValue = new StringAttribute();
		nonGroupedAttrWithSameValue.setType(RadiusAttributeConstants.USER_NAME);
		nonGroupedAttrWithSameValue.setStringValue("User1");


		radiusPacket.removeAttribute(nonGroupedAttrWithSameValue);

		assertFalse(radiusPacket.getRadiusAttributes().contains(nonGroupedAttrToRemove));

		//4. To Remove General Vendor Specific Grouped Attribute from packet with different reference of same value.
		radiusPacket.addAttribute(generalVSGAttrToRemove);
		IVendorSpecificAttribute generalVSGAttrWithSameValue = new GeneralVendorSpecificAttribute();
		generalVSGAttrWithSameValue.addAttribute(nonGroupedAttrToRemove);
		generalVSGAttrWithSameValue.refreshAttributeHeader();


		radiusPacket.removeAttribute(generalVSGAttrWithSameValue);

		assertFalse(radiusPacket.getRadiusAttributes().contains(generalVSGAttrToRemove));

		//5. To Remove General Vendor Specific Grouped Attribute from packet with different reference with different value.
		radiusPacket.addAttribute(generalVSGAttrToRemove);

		IRadiusAttribute nonGroupedAttrWithDifferentValue = new StringAttribute();
		nonGroupedAttrWithDifferentValue.setType(RadiusAttributeConstants.USER_NAME);
		nonGroupedAttrWithDifferentValue.setStringValue("User5");

		IVendorSpecificAttribute generalVSGAttrWithDifferentValue = new GeneralVendorSpecificAttribute();
		generalVSGAttrWithDifferentValue.addAttribute(nonGroupedAttrWithDifferentValue);
		generalVSGAttrWithDifferentValue.refreshAttributeHeader();

		radiusPacket.removeAttribute(generalVSGAttrWithDifferentValue);

		assertTrue(radiusPacket.getRadiusAttributes().contains(generalVSGAttrToRemove));

		//6. To Remove WiMAX Vendor Specific Grouped Attribute .
		IVendorSpecificAttribute wimaxVSGAttrToRemove = new WiMAXVendorSpecificAttribute();

		BaseRadiusAttribute wimaxStrAttribute = new WiMAXStringAttribute();
		wimaxStrAttribute.setStringValue("wimaxStrAttribute");
		wimaxVSGAttrToRemove.addAttribute(wimaxStrAttribute);
		wimaxVSGAttrToRemove.refreshAttributeHeader();

		radiusPacket.addAttribute(wimaxVSGAttrToRemove);
		radiusPacket.removeAttribute(wimaxVSGAttrToRemove);

		assertFalse(radiusPacket.getRadiusAttributes().contains(wimaxVSGAttrToRemove));

		//7. To Remove WiMAX Vendor Specific Grouped Attribute from packet with different reference with same value.
		radiusPacket.addAttribute(wimaxVSGAttrToRemove);
		IVendorSpecificAttribute wimaxVSGAttrWithSameValue= new WiMAXVendorSpecificAttribute();
		wimaxVSGAttrWithSameValue.addAttribute(wimaxStrAttribute);
		wimaxVSGAttrWithSameValue.refreshAttributeHeader();
		radiusPacket.removeAttribute(wimaxVSGAttrWithSameValue);

		assertFalse(radiusPacket.getRadiusAttributes().contains(wimaxVSGAttrToRemove));

		//8. To remove Group Type Attribute .
		BaseRadiusAttribute groupAttribute = new GroupedAttribute();
		groupAttribute.addTLVAttribute(nonGroupedAttrWithDifferentValue);
		groupAttribute.refreshAttributeHeader();

		radiusPacket.addAttribute(groupAttribute);
		radiusPacket.removeAttribute(groupAttribute);

		assertFalse(radiusPacket.getRadiusAttributes().contains(groupAttribute));


		//9. To remove Group Type Attribute from packet with different reference with same attribute .

		radiusPacket.addAttribute(groupAttribute);
		BaseRadiusAttribute groupAttributeWithSameValue = new GroupedAttribute();
		groupAttributeWithSameValue.addTLVAttribute(nonGroupedAttrWithDifferentValue);
		groupAttributeWithSameValue.refreshAttributeHeader();

		radiusPacket.removeAttribute(groupAttributeWithSameValue);

		assertFalse(radiusPacket.getRadiusAttributes().contains(groupAttribute));

		//10. To remove Wimax Group Type Attribute from packet.
		BaseRadiusAttribute wimaxGroupedAtribute = new WimaxGroupedAttribute();
		wimaxGroupedAtribute.addTLVAttribute(nonGroupedAttrWithDifferentValue);
		wimaxGroupedAtribute.refreshAttributeHeader();

		radiusPacket.addAttribute(wimaxGroupedAtribute);
		radiusPacket.removeAttribute(wimaxGroupedAtribute);

		assertFalse(radiusPacket.getRadiusAttributes().contains(wimaxGroupedAtribute));

		//11. To remove Wimax Group Type Attribute from packet with different reference with same value.

		radiusPacket.addAttribute(wimaxGroupedAtribute);
		BaseRadiusAttribute wimaxGroupedAttrWithSameValue = new WimaxGroupedAttribute();
		wimaxGroupedAttrWithSameValue.addTLVAttribute(nonGroupedAttrWithDifferentValue);
		wimaxGroupedAttrWithSameValue.refreshAttributeHeader();

		radiusPacket.removeAttribute(wimaxGroupedAttrWithSameValue);

		assertFalse(radiusPacket.getRadiusAttributes().contains(wimaxGroupedAtribute));

		//12. To remove Wimax Group Type Attribute from packet with different reference with different value.
		BaseRadiusAttribute wimaxGroupedAttrWithDifferentValue = new WimaxGroupedAttribute();
		nonGroupedAttrToRemove.setStringValue("different value");
		wimaxGroupedAttrWithDifferentValue.addTLVAttribute(nonGroupedAttrWithSameValue);
		wimaxGroupedAttrWithDifferentValue.refreshAttributeHeader();

		radiusPacket.addAttribute(wimaxGroupedAtribute);
		radiusPacket.removeAttribute(wimaxGroupedAttrWithDifferentValue);

		assertTrue(radiusPacket.getRadiusAttributes().contains(wimaxGroupedAtribute));
	}
}
