package com.elitecore.coreradius.commons.packet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.elitecore.coreradius.commons.util.RadiusDictionaryTestHarness;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import com.elitecore.coreradius.commons.attributes.GroupedAttribute;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.packet.RadiusPacket;
import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.InvalidAttributeIdException;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;

@RunWith(JUnitParamsRunner.class)
public class NewRadiusPacketTest {

    @Rule
    public ExpectedException exception = ExpectedException.none();

    private RadiusPacket radiusPacket;

    @Before
    public void setUp() throws Exception {
        radiusPacket = createAndGetRadiusPacket();
    }

    @BeforeClass
    public static void setUpBeforeClass() {
        // FIXME change the dictionary implementation in Version 6.6 using RadiusHarnessTest
        String standard_dictionary = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"

                + "<attribute-list vendorid=\"0\" vendor-name=\"standard\">"

                + "<attribute id=\"1\" name=\"User-Name\" type=\"string\"/>"
                + "<attribute id=\"2\" name=\"User-Password\" type=\"octets\"/>"
                + "<attribute id=\"4\" name=\"NAS-IP-Address\" type=\"ipaddr\"/>"
                + "<attribute id=\"30\" name=\"Called-Station-Id\"  type=\"string\"/>"
                + "<attribute id=\"31\" name=\"Calling-Station-Id\" type=\"string\"/>"
                + "<attribute id=\"44\" name=\"Acct-Session-Id\" type=\"string\"/>"
                + "<attribute id=\"26\" name=\"Vendor-Specific\" type=\"octets\"/>"

                + "</attribute-list>";

        String vendor_dictionary = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"

                + "<attribute-list vendorid=\"21067\" vendor-name=\"elitecore\">"
                + "<attribute id=\"0\" name=\"24Online-AVPair\" type=\"string\"/>"
                + "<attribute id=\"1\" name=\"Tekelec-AVPair\" type=\"string\"/>"
                + "<attribute id=\"2\" name=\"Cyberoam-AVPair\" type=\"string\"/>"
                + "<attribute id=\"3\" name=\"IP-Pool-AVPair\" type=\"string\"/>"
                + "<attribute id=\"4\" name=\"Rating-AVPair\" type=\"string\"/>"
                + "<attribute id=\"111\" name=\"Profile-Account-Status\" type=\"string\"/>"
                + "<attribute id=\"112\" name=\"Profile-Account-Type\" type=\"string\"/>"
                + "<attribute id=\"113\" name=\"Profile-Credit-Limit\" type=\"integer\"/>"
                + "<attribute id=\"114\" name=\"Profile-User-Group\" type=\"string\"/>"
                + "<attribute id=\"115\" name=\"Profile-Expiry-Date\" type=\"date\"/>"
                + "<attribute id=\"116\" name=\"Profile-Identity\" type=\"string\" />"
                + "<attribute id=\"117\" name=\"Profile-AVPair\"  type=\"grouped\">"
                + "  <attribute id=\"1\" name=\"Param1\"  type=\"string\"/>"
                + "  <attribute id=\"2\" name=\"Param2\"  type=\"string\"/>"
                + "  <attribute id=\"3\" name=\"Param3\"  type=\"string\"/>"
                + "  <attribute id=\"4\" name=\"Param4\"  type=\"string\"/>"
                + "  <attribute id=\"5\" name=\"Param5\"  type=\"string\"/>"
                + "</attribute>"
                + "</attribute-list>";

        StringReader standard_dict = new StringReader(standard_dictionary);
        try {
            Dictionary.getInstance().load(standard_dict);
        } catch (Exception e) {
            e.printStackTrace();
        }

        StringReader vendor_dict = new StringReader(vendor_dictionary);
        try {
            Dictionary.getInstance().load(vendor_dict);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private RadiusPacket createAndGetRadiusPacket() throws Exception {
        RadiusPacket radiusPacket = new RadiusPacket();
        URL currentPath = RadiusDictionaryTestHarness.class.getClassLoader().getResource("dictionary");
        File file = new File(URLDecoder.decode(currentPath.getPath(), "UTF-8"));
        Dictionary.getInstance().loadDictionary(file);
        return radiusPacket;
    }


    @Test
    @Parameters(value = {
            "11-1a-2b-cd-3e",
            "11:1a:2b:cd:3e",
            "1122:1a2b:cd3e"
    })
    public void testAddAttribute_ShouldAddAttributeAsMainAttribute(String attributeValue) {

        IRadiusAttribute calledStationId = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.CALLED_STATION_ID);
        calledStationId.setStringValue(attributeValue);
        radiusPacket.addAttribute(calledStationId);

        Assert.assertEquals(calledStationId, radiusPacket.getRadiusAttribute(RadiusAttributeConstants.CALLED_STATION_ID));
    }

    @Test
    @Parameters(value = {
            "11-1a-2b-cd-3e",
            "11:1a:2b:cd:3e",
            "1122:1a2b:cd3e"
    })
    public void testRemoveAttribute_ShouldRemoveAttributeFromMainAttribute(String attributeValue) throws InvalidAttributeIdException {

        IRadiusAttribute callingStationId = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.CALLING_STATION_ID);
        callingStationId.setStringValue(attributeValue);

        radiusPacket.addAttribute(callingStationId);

        radiusPacket.removeAttribute(callingStationId);
        Assert.assertNull(radiusPacket.getRadiusAttribute(RadiusAttributeConstants.CALLING_STATION_ID));
    }

    @Test
    @Parameters(value = {
            "11-1a-2b-cd-3e",
            "11:1a:2b:cd:3e",
            "1122:1a2b:cd3e"
    })
    public void testAddInfoAttribute_ShouldAddAttributeAsInfoAttribute(String attributeValue) {

        IRadiusAttribute calledStationId = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.CALLED_STATION_ID);
        calledStationId.setStringValue("012456789");
        radiusPacket.addInfoAttribute(calledStationId);

        Assert.assertEquals(calledStationId, radiusPacket.getRadiusAttribute(RadiusAttributeConstants.CALLED_STATION_ID, true));
    }

    @Test
    @Parameters(value = {
            "11-1a-2b-cd-3e",
            "11:1a:2b:cd:3e",
            "1122:1a2b:cd3e"
    })
    public void testRemoveInfoAttribute_ShouldRemoveAttributeFromInfoLevel(String attributeValue) throws InvalidAttributeIdException {

        IRadiusAttribute calledStationId = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.CALLED_STATION_ID);
        calledStationId.setStringValue(attributeValue);
        radiusPacket.addInfoAttribute(calledStationId);

        radiusPacket.removeInfoAttribute(calledStationId);

        Assert.assertNull(radiusPacket.getRadiusAttribute(RadiusAttributeConstants.CALLED_STATION_ID));
    }

    @Test
    public void testAddAttibute_ShouldAddCollectionsOfAttributeAsMainAttribute() {

        List<IRadiusAttribute> listOfAttribute = new ArrayList<IRadiusAttribute>();

        IRadiusAttribute nas_ip_address = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.NAS_IP_ADDRESS);
        nas_ip_address.setStringValue("10.106.1.24");
        listOfAttribute.add(nas_ip_address);

        IRadiusAttribute nas_ip_address2 = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.NAS_IP_ADDRESS);
        nas_ip_address2.setStringValue("0.0.0.0");
        listOfAttribute.add(nas_ip_address2);

        radiusPacket.addAttributes(listOfAttribute);
        Assert.assertEquals(listOfAttribute, radiusPacket.getRadiusAttributes(RadiusAttributeConstants.NAS_IP_ADDRESS));
    }

    @Test
    public void testAddInfoAttibute_ShouldAddCollectionsOfAttributeAsInfoAttribute() {

        List<IRadiusAttribute> listOfAttribute = new ArrayList<IRadiusAttribute>();

        IRadiusAttribute nas_ip_address = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.NAS_IP_ADDRESS);
        nas_ip_address.setStringValue("10.106.1.24");
        listOfAttribute.add(nas_ip_address);

        IRadiusAttribute nas_ip_address2 = Dictionary.getInstance().getAttribute(RadiusAttributeConstants.NAS_IP_ADDRESS);
        nas_ip_address2.setStringValue("0.0.0.0");
        listOfAttribute.add(nas_ip_address2);

        radiusPacket.addInfoAttributes(listOfAttribute);
        Assert.assertEquals(listOfAttribute, radiusPacket.getRadiusAttributes(RadiusAttributeConstants.NAS_IP_ADDRESS, true));
    }

    @Test
    @Parameters(value = {
            "param1",
            "EliteCoreAvpair",
            "any value"
    })
    public void testAddAttribute_ShouldAddVendorSpecificGroupedAttributeAsMainAttribute(String attributeValue) {

        GroupedAttribute profileAVPairGroupedAttr = (GroupedAttribute) Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID, RadiusAttributeConstants.ELITE_PROFILE_AVPAIR);

        IRadiusAttribute param1Attr = Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID, RadiusAttributeConstants.ELITE_PROFILE_AVPAIR, RadiusAttributeConstants.ELITE_PARAM1);
        param1Attr.setStringValue(attributeValue);
        profileAVPairGroupedAttr.addTLVAttribute(param1Attr);

        radiusPacket.addAttribute(profileAVPairGroupedAttr);
        Assert.assertEquals(param1Attr, radiusPacket.getRadiusAttribute(RadiusConstants.ELITECORE_VENDOR_ID, RadiusAttributeConstants.ELITE_PROFILE_AVPAIR, RadiusAttributeConstants.ELITE_PARAM1));
    }

    @Test
    @Parameters(value = {
            "param1",
            "EliteCoreAvpair",
            "123"
    })
    public void testRemoveAttribute_ShouldRemoveVendorSpecificGroupedAttribute(String attributeValue) {
        GroupedAttribute profileAVPairGroupedAttr = (GroupedAttribute) Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID, RadiusAttributeConstants.ELITE_PROFILE_AVPAIR);

        IRadiusAttribute param1Attr = Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID, RadiusAttributeConstants.ELITE_PROFILE_AVPAIR, RadiusAttributeConstants.ELITE_PARAM1);
        param1Attr.setStringValue(attributeValue);
        profileAVPairGroupedAttr.addTLVAttribute(param1Attr);

        radiusPacket.addAttribute(profileAVPairGroupedAttr);

        radiusPacket.removeAttribute(param1Attr);

        Assert.assertNull(radiusPacket.getRadiusAttribute(RadiusConstants.ELITECORE_VENDOR_ID, RadiusAttributeConstants.ELITE_PROFILE_AVPAIR));
    }

    @Test
    @Parameters(value = {
            "param1",
            "EliteCoreAvpair",
            "123"
    })
    public void testAddInfoAttribute_ShouldAddVendorSpecificGroupedAttributeAsInfoAttribute(String parameterValue) {

        GroupedAttribute profileAVPairGroupedAttr = (GroupedAttribute) Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID, RadiusAttributeConstants.ELITE_PROFILE_AVPAIR);

        IRadiusAttribute param1Attr = Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID, RadiusAttributeConstants.ELITE_PROFILE_AVPAIR, RadiusAttributeConstants.ELITE_PARAM1);
        param1Attr.setStringValue(parameterValue);
        profileAVPairGroupedAttr.addTLVAttribute(param1Attr);

        radiusPacket.addInfoAttribute(profileAVPairGroupedAttr);
        Assert.assertEquals(param1Attr, radiusPacket.getRadiusInfoAttribute(RadiusConstants.ELITECORE_VENDOR_ID, RadiusAttributeConstants.ELITE_PROFILE_AVPAIR, RadiusAttributeConstants.ELITE_PARAM1));
    }

    @Test
    @Parameters(value = {
            "param1",
            "EliteCoreAvpair",
            "123"
    })
    public void testRemoveInfoAttribute_ShouldRemoveVendorSpecificGroupedInfoAttribute(String parameterValue) {
        GroupedAttribute profileAVPairGroupedAttr = (GroupedAttribute) Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID, RadiusAttributeConstants.ELITE_PROFILE_AVPAIR);

        IRadiusAttribute param1Attr = Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID, RadiusAttributeConstants.ELITE_PROFILE_AVPAIR, RadiusAttributeConstants.ELITE_PARAM1);
        param1Attr.setStringValue(parameterValue);
        profileAVPairGroupedAttr.addTLVAttribute(param1Attr);
        radiusPacket.addInfoAttribute(profileAVPairGroupedAttr);

        radiusPacket.removeInfoAttribute(profileAVPairGroupedAttr);

        Assert.assertNull(radiusPacket.getRadiusAttribute(RadiusConstants.ELITECORE_VENDOR_ID, RadiusAttributeConstants.ELITE_PROFILE_AVPAIR));
    }

    @Test
    public void testAddAttribute_ShouldAddVendorSpecificAsMainAttribute() {

        IRadiusAttribute ipPoolAttr = Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID, RadiusAttributeConstants.ELITE_IP_POOL_AVPAIR);
        ipPoolAttr.setStringValue("IPPOOL");
        radiusPacket.addAttribute(ipPoolAttr);
        Assert.assertEquals(ipPoolAttr, radiusPacket.getRadiusAttribute(RadiusConstants.ELITECORE_VENDOR_ID, RadiusAttributeConstants.ELITE_IP_POOL_AVPAIR));
    }

    @Test
    public void testRemoveAttribute_ShouldRemoveVendorSpecificAttribute() {

        IRadiusAttribute ipPoolAttr = Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID, RadiusAttributeConstants.ELITE_IP_POOL_AVPAIR);
        ipPoolAttr.setStringValue("IPPOOL");
        radiusPacket.addAttribute(ipPoolAttr);
        radiusPacket.removeAttribute(ipPoolAttr);
        Assert.assertNull(radiusPacket.getRadiusAttribute(RadiusConstants.ELITECORE_VENDOR_ID, RadiusAttributeConstants.ELITE_IP_POOL_AVPAIR));
    }

    @Test
    public void testAddInfoAttribute_ShouldAddVendorSpecificAsInfoAttribute() {

        IRadiusAttribute ipPoolAttr = Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID, RadiusAttributeConstants.ELITE_IP_POOL_AVPAIR);
        ipPoolAttr.setStringValue("IPPOOL");
        radiusPacket.addInfoAttribute(ipPoolAttr);
        Assert.assertEquals(ipPoolAttr, radiusPacket.getRadiusInfoAttribute(RadiusConstants.ELITECORE_VENDOR_ID, RadiusAttributeConstants.ELITE_IP_POOL_AVPAIR));
    }

    @Test
    public void testRemoveInfoAttribute_ShouldRemoveVendorSpecificInfoAttribute() {

        IRadiusAttribute ipPoolAttr = Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID, RadiusAttributeConstants.ELITE_IP_POOL_AVPAIR);
        ipPoolAttr.setStringValue("IPPOOL");
        radiusPacket.addInfoAttribute(ipPoolAttr);
        radiusPacket.removeInfoAttribute(ipPoolAttr);
        Assert.assertNull(radiusPacket.getRadiusInfoAttribute(RadiusConstants.ELITECORE_VENDOR_ID, RadiusAttributeConstants.ELITE_IP_POOL_AVPAIR));
    }

    @Test
    public void testAddAttribute_ShouldAddParentGroupedAttribute_WithMultipleSubAttributes() {

        GroupedAttribute profileAVPairGroupedAttr = (GroupedAttribute) Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID, RadiusAttributeConstants.ELITE_PROFILE_AVPAIR);

        IRadiusAttribute param1 = Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID, RadiusAttributeConstants.ELITE_PROFILE_AVPAIR, RadiusAttributeConstants.ELITE_PARAM1);
        param1.setStringValue("param1");
        profileAVPairGroupedAttr.addTLVAttribute(param1);

        IRadiusAttribute param2 = Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID, RadiusAttributeConstants.ELITE_PROFILE_AVPAIR, RadiusAttributeConstants.ELITE_PARAM2);
        param2.setStringValue("param2");
        profileAVPairGroupedAttr.addTLVAttribute(param2);

        radiusPacket.addAttribute(profileAVPairGroupedAttr);
        Assert.assertEquals(profileAVPairGroupedAttr, radiusPacket.getRadiusAttribute(RadiusConstants.ELITECORE_VENDOR_ID, RadiusAttributeConstants.ELITE_PROFILE_AVPAIR));
    }

    @Test
    public void testRemoveAttribute_ShouldRemoveParentGroupedAttributeIncludingAllAttributes_UsingParentAttributeID() {

        GroupedAttribute profileAVPairGroupedAttr = (GroupedAttribute) Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID, RadiusAttributeConstants.ELITE_PROFILE_AVPAIR);

        IRadiusAttribute param1 = Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID, RadiusAttributeConstants.ELITE_PROFILE_AVPAIR, RadiusAttributeConstants.ELITE_PARAM1);
        param1.setStringValue("param1");
        profileAVPairGroupedAttr.addTLVAttribute(param1);

        IRadiusAttribute param2 = Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID, RadiusAttributeConstants.ELITE_PROFILE_AVPAIR, RadiusAttributeConstants.ELITE_PARAM2);
        param2.setStringValue("param2");
        profileAVPairGroupedAttr.addTLVAttribute(param2);

        radiusPacket.addAttribute(profileAVPairGroupedAttr);
        radiusPacket.removeAttribute(profileAVPairGroupedAttr);

        Assert.assertNull(radiusPacket.getRadiusAttribute(RadiusConstants.ELITECORE_VENDOR_ID, RadiusAttributeConstants.ELITE_PROFILE_AVPAIR));
    }

    @Test
    public void testAddAttribute_ShouldAddMultipleSubAttibutesOfSameParentAsDifferentAttributeAsMainAttribute() {

        IRadiusAttribute param1 = Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID, RadiusAttributeConstants.ELITE_PROFILE_AVPAIR, RadiusAttributeConstants.ELITE_PARAM1);
        param1.setStringValue("param1");
        radiusPacket.addAttribute(param1);

        IRadiusAttribute param2 = Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID, RadiusAttributeConstants.ELITE_PROFILE_AVPAIR, RadiusAttributeConstants.ELITE_PARAM2);
        param2.setStringValue("param2");
        radiusPacket.addAttribute(param2);
        Assert.assertEquals(param1, radiusPacket.getRadiusAttribute(RadiusConstants.ELITECORE_VENDOR_ID, RadiusAttributeConstants.ELITE_PROFILE_AVPAIR, RadiusAttributeConstants.ELITE_PARAM1));
        Assert.assertEquals(param2, radiusPacket.getRadiusAttribute(RadiusConstants.ELITECORE_VENDOR_ID, RadiusAttributeConstants.ELITE_PROFILE_AVPAIR, RadiusAttributeConstants.ELITE_PARAM2));
    }

    @Test
    public void testRemoveAttribute_ShouldRemoveIndividualGroupedAttributeWhenNotIncludeWithinSameParentAttribute() {

        IRadiusAttribute param1 = Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID, RadiusAttributeConstants.ELITE_PROFILE_AVPAIR, RadiusAttributeConstants.ELITE_PARAM1);
        param1.setStringValue("param1");
        radiusPacket.addAttribute(param1);

        IRadiusAttribute param2 = Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID, RadiusAttributeConstants.ELITE_PROFILE_AVPAIR, RadiusAttributeConstants.ELITE_PARAM2);
        param2.setStringValue("param2");
        radiusPacket.addAttribute(param2);

        radiusPacket.removeAttribute(param1);

        radiusPacket.removeAttribute(param2);

        Assert.assertNull(radiusPacket.getRadiusAttribute(RadiusConstants.ELITECORE_VENDOR_ID, RadiusAttributeConstants.ELITE_PROFILE_AVPAIR, RadiusAttributeConstants.ELITE_PARAM1));
        Assert.assertNull(radiusPacket.getRadiusAttribute(RadiusConstants.ELITECORE_VENDOR_ID, RadiusAttributeConstants.ELITE_PROFILE_AVPAIR, RadiusAttributeConstants.ELITE_PARAM2));
    }

    @Test
    public void testRemoveAttribute_ShouldRemoveSingleGroupedAttributeWhenNotIncludeWithinSameParentAttribute() {

        IRadiusAttribute param1 = Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID, RadiusAttributeConstants.ELITE_PROFILE_AVPAIR, RadiusAttributeConstants.ELITE_PARAM1);
        param1.setStringValue("param1");
        radiusPacket.addAttribute(param1);

        IRadiusAttribute param2 = Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID, RadiusAttributeConstants.ELITE_PROFILE_AVPAIR, RadiusAttributeConstants.ELITE_PARAM2);
        param2.setStringValue("param2");
        radiusPacket.addAttribute(param2);

        radiusPacket.removeAttribute(param1);


        Assert.assertNull(radiusPacket.getRadiusAttribute(RadiusConstants.ELITECORE_VENDOR_ID, RadiusAttributeConstants.ELITE_PROFILE_AVPAIR, RadiusAttributeConstants.ELITE_PARAM1));
        Assert.assertEquals(param2, radiusPacket.getRadiusAttribute(RadiusConstants.ELITECORE_VENDOR_ID, RadiusAttributeConstants.ELITE_PROFILE_AVPAIR, RadiusAttributeConstants.ELITE_PARAM2));
    }


    @Test
    public void testRemoveAttribute_ShouldRemoveSingleGroupedAttributeWhenNotIncludeWithinSameParentAttribute1() {

        IRadiusAttribute param1 = Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID, RadiusAttributeConstants.ELITE_PROFILE_AVPAIR, RadiusAttributeConstants.ELITE_PARAM1);
        param1.setStringValue("param1");
        radiusPacket.addAttribute(param1);

        IRadiusAttribute param2 = Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID, RadiusAttributeConstants.ELITE_PROFILE_AVPAIR, RadiusAttributeConstants.ELITE_PARAM2);
        param2.setStringValue("param2");
        radiusPacket.addAttribute(param2);

        radiusPacket.removeAttribute(param1);


        Assert.assertNull(radiusPacket.getRadiusAttribute(RadiusConstants.ELITECORE_VENDOR_ID, RadiusAttributeConstants.ELITE_PROFILE_AVPAIR, RadiusAttributeConstants.ELITE_PARAM1));
        Assert.assertEquals(param2, radiusPacket.getRadiusAttribute(RadiusConstants.ELITECORE_VENDOR_ID, RadiusAttributeConstants.ELITE_PROFILE_AVPAIR, RadiusAttributeConstants.ELITE_PARAM2));
    }

    @Test
    public void testRemoveAttribute_ShouldNotRemoveSubAttributesInSameParentAttribute_UsingSubAttribute() {

        GroupedAttribute profileAVPairGroupedAttr = (GroupedAttribute) Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID, RadiusAttributeConstants.ELITE_PROFILE_AVPAIR);

        IRadiusAttribute param1 = Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID, RadiusAttributeConstants.ELITE_PROFILE_AVPAIR, RadiusAttributeConstants.ELITE_PARAM1);
        param1.setStringValue("param1");
        profileAVPairGroupedAttr.addTLVAttribute(param1);

        IRadiusAttribute param2 = Dictionary.getInstance().getKnownAttribute(RadiusConstants.ELITECORE_VENDOR_ID, RadiusAttributeConstants.ELITE_PROFILE_AVPAIR, RadiusAttributeConstants.ELITE_PARAM2);
        param2.setStringValue("param2");
        profileAVPairGroupedAttr.addTLVAttribute(param2);

        radiusPacket.addAttribute(profileAVPairGroupedAttr);

        radiusPacket.removeAttribute(param1);
        radiusPacket.removeAttribute(param2);

        Assert.assertNotNull(radiusPacket.getRadiusAttribute(RadiusConstants.ELITECORE_VENDOR_ID, RadiusAttributeConstants.ELITE_PROFILE_AVPAIR, RadiusAttributeConstants.ELITE_PARAM1));
        Assert.assertNotNull(radiusPacket.getRadiusAttribute(RadiusConstants.ELITECORE_VENDOR_ID, RadiusAttributeConstants.ELITE_PROFILE_AVPAIR, RadiusAttributeConstants.ELITE_PARAM2));
    }

    @Test
    public void testParsePacketType_ShouldThrowIllegalArgumentException_IfPassedBytesAreInsufficientInSize() {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("Insufficient bytes to parse packet type");
        RadiusPacket.parsePacketType(new byte[]{});
    }

    @Test
    public void testParsePacketType_ShouldThrowNPE_IfPassedBytesAreNull() {
        exception.expect(NullPointerException.class);
        exception.expectMessage("packetBytes are null");
        RadiusPacket.parsePacketType(null);
    }

    @Test
    @Parameters(method = "packetTypes")
    public void testParsePacketType_ShouldProperlyParsePacketTypeFromBytesPassed(int packetType) {
        RadiusPacket packet = new RadiusPacket();
        packet.setPacketType(packetType);
        packet.refreshPacketHeader();
        packet.refreshInfoPacketHeader();

        assertEquals(packetType, RadiusPacket.parsePacketType(packet.getBytes()));
    }

    public Object[] packetTypes() {
        return new Object[]{
                RadiusConstants.ACCESS_ACCEPT_MESSAGE,
                RadiusConstants.ACCESS_CHALLENGE_MESSAGE,
                RadiusConstants.ACCESS_REJECT_MESSAGE,
                RadiusConstants.ACCESS_REQUEST_MESSAGE,
                RadiusConstants.ACCOUNTING_REQUEST_MESSAGE,
                RadiusConstants.ACCOUNTING_RESPONSE_MESSAGE,
                255
        };
    }

    @Test
    public void testAddAttributeforStringValueWorks() throws Exception {
        radiusPacket.addAttribute("4", "10.121.21.24");
        IRadiusAttribute radiusAttribute = radiusPacket.getRadiusAttribute("4");
        assertNotNull(radiusAttribute);
        Assert.assertEquals("10.121.21.24", radiusAttribute.getStringValue());
    }

    @Test
    public void testAddAttributeForIntegerValueWorks() throws Exception {
        radiusPacket.addAttribute("12", Integer.parseInt("1"));
        IRadiusAttribute radiusAttribute = radiusPacket.getRadiusAttribute("12");
        assertNotNull(radiusAttribute);
        Assert.assertEquals("1", radiusAttribute.getStringValue() );
    }

    @Test
    public void testAddAttributeForLongValueWorks() throws Exception {
        radiusPacket.addAttribute("5", 1l);
        IRadiusAttribute radiusAttribute = radiusPacket.getRadiusAttribute("5");
        assertNotNull(radiusAttribute);
        Assert.assertEquals(1, radiusAttribute.getLongValue());
    }


}