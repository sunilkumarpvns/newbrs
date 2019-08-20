/**
 *
 */
package com.elitecore.test.dependecy.diameter;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.test.dependecy.diameter.packet.DiameterAvpId;
import com.elitecore.test.dependecy.diameter.packet.avps.BaseAVPBuilder;
import com.elitecore.test.dependecy.diameter.packet.avps.BaseGroupAvpBuilder;
import com.elitecore.test.dependecy.diameter.packet.avps.IDiameterAVP;
import com.elitecore.test.dependecy.diameter.packet.avps.UnknownAttribute;
import com.elitecore.test.dependecy.diameter.packet.avps.basic.AvpFloat32;
import com.elitecore.test.dependecy.diameter.packet.avps.basic.AvpFloat64;
import com.elitecore.test.dependecy.diameter.packet.avps.basic.AvpInteger32;
import com.elitecore.test.dependecy.diameter.packet.avps.basic.AvpInteger64;
import com.elitecore.test.dependecy.diameter.packet.avps.basic.AvpOctetString;
import com.elitecore.test.dependecy.diameter.packet.avps.basic.AvpUnsigned32;
import com.elitecore.test.dependecy.diameter.packet.avps.basic.AvpUnsigned64;
import com.elitecore.test.dependecy.diameter.packet.avps.derived.AvpAddress;
import com.elitecore.test.dependecy.diameter.packet.avps.derived.AvpDiameterIdentity;
import com.elitecore.test.dependecy.diameter.packet.avps.derived.AvpDiameterURI;
import com.elitecore.test.dependecy.diameter.packet.avps.derived.AvpEnumerated;
import com.elitecore.test.dependecy.diameter.packet.avps.derived.AvpIPv4Address;
import com.elitecore.test.dependecy.diameter.packet.avps.derived.AvpTime;
import com.elitecore.test.dependecy.diameter.packet.avps.derived.AvpUTF8String;
import com.elitecore.test.dependecy.diameter.packet.avps.derived.AvpUserEquipmentInfoValue;
import com.elitecore.test.dependecy.diameter.packet.avps.grouped.AvpGrouped;
import com.elitecore.test.dependecy.diameter.packet.avps.grouped.AvpRule;
import com.elitecore.test.dependecy.diameter.packet.avps.grouped.AvpUserEquipmentInfo;
import com.elitecore.test.dependecy.diameter.packet.avps.threegpp.AvpUserLocationInfo;

/**
 * @author dhaval.jobanputra
 */

public class DiameterDictionary {
    public static final String MODULE = "Dictionary Manager";
    public static final String ATTRIBUTE_LIST = "attribute-list";
    public static final String ATTRIBUTE = "attribute";
    public static final String SUPPORTED_VALUES = "supported-values";
    public static final String VALUE = "value";
    public static final String ID = "id";
    public static final String TYPE = "type";
    public static final String MANDATORY = "mandatory";
    public static final String PROTECTED = "protected";
    public static final String ENCRYPTION = "encryption";
    public static final String FIXED = "fixed";
    public static final String REQUIRED = "required";
    public static final String OPTIONAL = "optional";
    public static final String VENDOR_ID = "vendorid";
    public static final String VENDOR_NAME = "vendor-name";
    public static final String APPLICATION_ID = "applicationid";
    public static final String APPLICATION_NAME = "application-name";
    public static final int STANDARD_VENDOR_ID = 0;
    public static final int STANDARD_APPLICATION_ID = 0;
    public static final long VALUE_NOT_FOUND = -1;
    private static final String NAME = "name";
    private static final String GROUPED = "grouped";
    private static final String ATTRIBUTE_RULE = "attributerule";
    private static final String MINIMUM = "minimum";
    private static final String MAXIMUM = "maximum";
    private static DiameterDictionary dictionaryInstance;
    private Map<String, DiameterVendor> vendorMap = new HashMap<String, DiameterVendor>();
    private Map<DiameterAvpId, BaseAVPBuilder> attributeMap = new HashMap<DiameterAvpId, BaseAVPBuilder>();
    private Map<String, DiameterAvpId> attributeIdMap = new HashMap<String, DiameterAvpId>();
    private List<String> vandorIdList = new ArrayList<String>();
    private List<String> xmlReaded = new ArrayList<String>();

    /**
     * this method will return the instance of Dictionary
     *
     * @return
     */
    public static DiameterDictionary getInstance() {

        if (dictionaryInstance == null) {
            synchronized (DiameterDictionary.class) {
                if (dictionaryInstance == null) {
                    dictionaryInstance = new DiameterDictionary();
                    dictionaryInstance.setDefaultAttributes();
                }
            }
        }
        return dictionaryInstance;
    }

    public static void main(String a[]) throws Exception {
        File file = new File("/media/opt/EliteAAA/EliteAAA/Trunk/Applications/eliteaaa/default/dictionary/diameter/temp");
        if (!file.exists()) {
            System.out.println("File does not exist");
            return;
        }
        DiameterDictionary dict = DiameterDictionary.getInstance();
        dict.load(file);
        System.out.println("Completed");
    }

    public String getAttributeName(int[] idArray) {
        String strAttrName = "";
        for (int id : idArray) {
            strAttrName += getAttributeName(id);
        }
        if (strAttrName != null) {
            return strAttrName;
        } else {
            return "Unknown-Attribute - " + Arrays.toString(idArray);
        }
    }

    public void load(File file) throws Exception {
        Map<String, DiameterAvpId> tempAttributeIdMap = new HashMap<String, DiameterAvpId>();
        readDictionary(file, tempAttributeIdMap);

        DiameterAvpId diameterAvpId;

        for (Map.Entry<String, DiameterAvpId> entry : tempAttributeIdMap.entrySet()) {
            diameterAvpId = entry.getValue();
            if (diameterAvpId.isGrouped())
                parseGroupedAvp(diameterAvpId, entry.getKey());
        }

    }

    private void readDictionary(File file, Map<String, DiameterAvpId> tempAttributeIdMap) throws Exception {
        if (file.isDirectory()) {
            File[] fileList = file.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                readDictionary(fileList[i], tempAttributeIdMap);
            }
        } else {
            if (file.getName().endsWith(".xml")) {
                if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
                    LogManager.getLogger().debug(MODULE, "Reading dictionary file [" + file.getName() + "]");
                }
                Reader inStream = new FileReader(file);
                readDictionary(inStream, tempAttributeIdMap, file.getName());
                inStream.close();
            }
        }
    }

    private void setDefaultAttributes() {

        // USER_EQUIPMENT_INFO_TYPE
        BaseAVPBuilder usrEqpmntInfoTypeBuilder = new AvpEnumeratedBuilder();
        usrEqpmntInfoTypeBuilder.setAVPCode(DiameterAVPConstants.USER_EQUIPMENT_INFO_TYPE_INT);
        usrEqpmntInfoTypeBuilder.setVendorId(STANDARD_VENDOR_ID);
        usrEqpmntInfoTypeBuilder.setAVPId(STANDARD_VENDOR_ID, DiameterAVPConstants.USER_EQUIPMENT_INFO_TYPE_INT);
        usrEqpmntInfoTypeBuilder.setAVPEncryption("yes");
        usrEqpmntInfoTypeBuilder.setMandatoryBit();
        Map<Integer, String> supportedValueMap = new HashMap<Integer, String>();
        supportedValueMap.put(0, "IMEISV");
        supportedValueMap.put(1, "MAC");
        supportedValueMap.put(2, "EUI64");
        supportedValueMap.put(3, "MODIFIED_EUI64");
        ((AvpEnumeratedBuilder) usrEqpmntInfoTypeBuilder).setSupportedValuesMap(supportedValueMap);
        DiameterAvpId usrEqpmntInfoTypeAvpId = new DiameterAvpId(STANDARD_VENDOR_ID, "STANDARD", DiameterAVPConstants.USER_EQUIPMENT_INFO_TYPE_INT, "User-Equipment-Info-Type", "yes", "no", "yes", "Enumerated", null, null, null, null, false, "default-avp");

        attributeMap.put(usrEqpmntInfoTypeAvpId, usrEqpmntInfoTypeBuilder);
        attributeIdMap.put(STANDARD_VENDOR_ID + ":" + DiameterAVPConstants.USER_EQUIPMENT_INFO_TYPE_INT, usrEqpmntInfoTypeAvpId);


        // USER_EQUIPMENT_INFO
        BaseAVPBuilder usrEqpmntInfoBuilder = new UserEquipmentInfoAvpBuilder();
        ArrayList<AvpRule> requiredAttrList = new ArrayList<AvpRule>();
        AvpRule usrEqpmntInfoTypeAvpRule = new AvpRule();
        usrEqpmntInfoTypeAvpRule.setVendorId(0);
        usrEqpmntInfoTypeAvpRule.setVendorId(0);
        usrEqpmntInfoTypeAvpRule.setAttrId(DiameterAVPConstants.USER_EQUIPMENT_INFO_TYPE_INT);
        usrEqpmntInfoTypeAvpRule.setMaximum("1");
        usrEqpmntInfoTypeAvpRule.setMinimum("1");
        usrEqpmntInfoTypeAvpRule.setName("User-Equipment-Info-Type");
        requiredAttrList.add(usrEqpmntInfoTypeAvpRule);

        AvpRule usrEqpmntInfoValueAvpRule = new AvpRule();
        usrEqpmntInfoTypeAvpRule.setVendorId(0);
        usrEqpmntInfoTypeAvpRule.setVendorId(0);
        usrEqpmntInfoTypeAvpRule.setAttrId(DiameterAVPConstants.USER_EQUIPMENT_INFO_VALUE_INT);
        usrEqpmntInfoTypeAvpRule.setMaximum("1");
        usrEqpmntInfoTypeAvpRule.setMinimum("1");
        usrEqpmntInfoTypeAvpRule.setName("User-Equipment-Info-Value");
        requiredAttrList.add(usrEqpmntInfoValueAvpRule);

        usrEqpmntInfoBuilder.setAVPCode(DiameterAVPConstants.USER_EQUIPMENT_INFO_INT);
        usrEqpmntInfoBuilder.setVendorId(STANDARD_VENDOR_ID);
        usrEqpmntInfoBuilder.setAVPId(STANDARD_VENDOR_ID, DiameterAVPConstants.USER_EQUIPMENT_INFO_INT);
        usrEqpmntInfoBuilder.setAVPEncryption("yes");
        usrEqpmntInfoBuilder.setMandatoryBit();
        ((BaseGroupAvpBuilder) usrEqpmntInfoBuilder).setRequiredAttrList(requiredAttrList);
        ((BaseGroupAvpBuilder) usrEqpmntInfoBuilder).setFixedAttrList(new ArrayList<AvpRule>());
        ((BaseGroupAvpBuilder) usrEqpmntInfoBuilder).setOptionalAttrList(new ArrayList<AvpRule>());

        DiameterAvpId usrEqpmntInfoAvpId = new DiameterAvpId(STANDARD_VENDOR_ID, "STANDARD", DiameterAVPConstants.USER_EQUIPMENT_INFO_INT, "User-Equipment-Info", "yes", "no", "yes", "UserEquipmentInfo", null, null, null, null, false, "default-avp");

        attributeMap.put(usrEqpmntInfoAvpId, usrEqpmntInfoBuilder);
        attributeIdMap.put(STANDARD_VENDOR_ID + ":" + DiameterAVPConstants.USER_EQUIPMENT_INFO_INT, usrEqpmntInfoAvpId);


        // 3GPP-User-Location-Info
        BaseAVPBuilder tgppUsrLocationInfoBuilder = new AvpUserLocationInfoAvpBuilder();
        tgppUsrLocationInfoBuilder.setAVPCode(22);
        tgppUsrLocationInfoBuilder.setVendorId(10415);
        tgppUsrLocationInfoBuilder.setAVPId(10415, 22);
        tgppUsrLocationInfoBuilder.setAVPEncryption("yes");
        tgppUsrLocationInfoBuilder.setMandatoryBit();
        DiameterAvpId tgppUsrLocationInfoAvpId = new DiameterAvpId(10415, "3GPP", 22, "3GPP-User-Location-Info", "yes", "no", "yes", "UserLocationInfo", null, null, null, null, false, "default-avp");
        attributeMap.put(tgppUsrLocationInfoAvpId, tgppUsrLocationInfoBuilder);
        attributeIdMap.put("10415:22", tgppUsrLocationInfoAvpId);

    }

    protected void readDictionary(Reader inStream, Map<String, DiameterAvpId> tempAttributeIdMap, String xmlFileName) throws DictionaryParseException {

        String strVendorID = null;
        String strVendorName = null;
        String strApplicationId = null;
        String strApplicationName = null;

        String strEncryption = null;
        String strMandatory = null;
        String strProtected = null;

        String strAttrType = null;
        DiameterVendor vendor = null;

        int iVendorId = 0;
        int iApplicationId = 0;

        Map<String, DiameterVendor> tmpVendorMap = new HashMap<String, DiameterVendor>();
        Map<DiameterAvpId, BaseAVPBuilder> tmpAttributeMap = new HashMap<DiameterAvpId, BaseAVPBuilder>();
        Map<String, DiameterAvpId> tmpAttributeIdMap = new HashMap<String, DiameterAvpId>();

        DocumentBuilderFactory factory = null;
        DocumentBuilder documentBuilder = null;
        Document document = null;
        NodeList nodeList = null;

        try {
            factory = DocumentBuilderFactory.newInstance();
            factory.setIgnoringComments(true);
            factory.setIgnoringElementContentWhitespace(true);
            factory.setValidating(false);
            documentBuilder = factory.newDocumentBuilder();

            InputSource inputSource = new InputSource(inStream);
            document = documentBuilder.parse(inputSource);

            Node vendorNode = document.getElementsByTagName(ATTRIBUTE_LIST).item(0);
            nodeList = vendorNode.getChildNodes();

            if (vendorNode.getNodeName().equals(ATTRIBUTE_LIST)) {

                if (vendorNode.getAttributes().getNamedItem(VENDOR_ID).getTextContent() != null) {
                    strVendorID = vendorNode.getAttributes().getNamedItem(VENDOR_ID).getTextContent().trim();
                    try {
                        iVendorId = Integer.parseInt(strVendorID);
                    } catch (NumberFormatException e) {
                        throw new DictionaryParseException(strVendorID + " is not a valid Vendor-Id");
                    }
                } else {
                    throw new DictionaryParseException("Vendor-Id not defined for the dictionary.");
                }

                if (vendorNode.getAttributes().getNamedItem(VENDOR_NAME).getTextContent() != null) {
                    strVendorName = vendorNode.getAttributes().getNamedItem(VENDOR_NAME).getTextContent().trim();

                } else {
                    throw new DictionaryParseException("Vendor-Name not defined for the dictionary.");
                }

                if (vendorNode.getAttributes().getNamedItem(APPLICATION_ID).getTextContent() != null) {
                    strApplicationId = vendorNode.getAttributes().getNamedItem(APPLICATION_ID).getTextContent().trim();
                    try {
                        iApplicationId = Integer.parseInt(strApplicationId);
                    } catch (NumberFormatException e) {
                        throw new DictionaryParseException(strVendorID + " is not a valid Application-Id");
                    }
                } else {
                    throw new DictionaryParseException("Application-Id not defined for the dictionary.");
                }

                if (vendorNode.getAttributes().getNamedItem(APPLICATION_NAME).getTextContent() != null) {
                    strApplicationName = vendorNode.getAttributes().getNamedItem(APPLICATION_NAME).getTextContent().trim();
                } else {
                    throw new DictionaryParseException("Application-Name not defined for the dictionary.");
                }

                if (!this.vendorMap.containsKey(strVendorID)) {
                    vendor = new DiameterVendor(iVendorId, strVendorName, iApplicationId, strApplicationName);
                    tmpVendorMap.put(strVendorID, vendor);
                    tmpVendorMap.put(strVendorName, vendor);
                    this.vandorIdList.add(strVendorID);
                } else {
                    vendorMap.get(strVendorID).addApplication(iApplicationId, strApplicationName);
                }

            }

            for (int i = 0; i < nodeList.getLength(); i++) {

                Node node = nodeList.item(i);

                String attrID = null;
                int iAttrID = -1;
                String attrName = null;

                BaseAVPBuilder baseAVPBuilder = null;
                ArrayList<AvpRule> fixedAttrList = null;
                ArrayList<AvpRule> requiredAttrList = null;
                ArrayList<AvpRule> optionalAttrList = null;
                Map<Integer, String> supportedValueMap = null;
                boolean isGrouped = false;
                NamedNodeMap namedNodeMap;

                if (node.getNodeName().equals(ATTRIBUTE)) {
                    isGrouped = false;
                    namedNodeMap = node.getAttributes();
                    if (namedNodeMap.getNamedItem(ID).getTextContent() != null) {
                        attrID = namedNodeMap.getNamedItem(ID).getTextContent().trim();
                        try {
                            iAttrID = Integer.parseInt(attrID);
                        } catch (NumberFormatException e) {
                            if (LogManager.getLogger().isLogLevel(LogLevel.TRACE))
                                LogManager.getLogger().trace(MODULE, "Attribute id " + attrID + " is not in proper format for " + strVendorID + ":" + strVendorName);
                            throw new DictionaryParseException("Attribute id " + attrID + " is not in proper format for " + strVendorID + ":" + strVendorName);
                        }
                    }
                    if (namedNodeMap.getNamedItem(NAME).getTextContent() != null) {
                        attrName = namedNodeMap.getNamedItem(NAME).getTextContent().trim();
                    } else {
                        throw new DictionaryParseException("Attribute name not specified for " + strVendorID + ":" + strVendorName + ":" + attrID);
                    }
                    if (namedNodeMap.getNamedItem(MANDATORY).getTextContent() != null) {
                        strMandatory = namedNodeMap.getNamedItem(MANDATORY).getTextContent();
                    }
                    if (namedNodeMap.getNamedItem(PROTECTED).getTextContent() != null) {
                        strProtected = namedNodeMap.getNamedItem(PROTECTED).getTextContent();
                    }
                    if (namedNodeMap.getNamedItem(ENCRYPTION).getTextContent() != null) {
                        strEncryption = namedNodeMap.getNamedItem(ENCRYPTION).getTextContent();
                    }
                    if (namedNodeMap.getNamedItem(TYPE).getTextContent() != null) {
                        strAttrType = namedNodeMap.getNamedItem(TYPE).getTextContent().trim();
                    } else {
                        throw new DictionaryParseException("Attribute type not specified for " + strVendorID + ":" + strVendorName + ":" + attrID);
                    }


                    if (strAttrType.equalsIgnoreCase("Unsigned32")) {
                        baseAVPBuilder = new AvpUnsigned32Builder();
                    } else if (strAttrType.equalsIgnoreCase("Unsigned64")) {
                        baseAVPBuilder = new AvpUnsigned64Builder();
                    } else if (strAttrType.equalsIgnoreCase("Integer32")) {
                        baseAVPBuilder = new AvpInteger32Builder();
                    } else if (strAttrType.equalsIgnoreCase("Integer64")) {
                        baseAVPBuilder = new AvpInteger64Builder();
                    } else if (strAttrType.equalsIgnoreCase("Float32")) {
                        baseAVPBuilder = new AvpFloat32Builder();
                    } else if (strAttrType.equalsIgnoreCase("Float64")) {
                        baseAVPBuilder = new AvpFloat64Builder();
                    } else if (strAttrType.equalsIgnoreCase("Grouped")) {
                        isGrouped = true;
                        baseAVPBuilder = new AvpGroupedBuilder();
                    } else if (strAttrType.equalsIgnoreCase("DiameterIdentity")) {
                        baseAVPBuilder = new AvpDiameterIdentityBuilder();
                    } else if (strAttrType.equalsIgnoreCase("DiameterURI")) {
                        baseAVPBuilder = new AvpDiameterURIBuilder();
                    } else if (strAttrType.equalsIgnoreCase("Time")) {
                        baseAVPBuilder = new AvpTimeBuilder();
                    } else if (strAttrType.equalsIgnoreCase("UTF8String")) {
                        baseAVPBuilder = new AvpUTF8StringBuilder();
                    } else if (strAttrType.equalsIgnoreCase("IPAddress")) {
                        baseAVPBuilder = new AvpAddressBuilder();
                    } else if (strAttrType.equalsIgnoreCase("IPv4Address")) {
                        baseAVPBuilder = new AvpIpv4AddressBuilder();
                    } else if (strAttrType.equalsIgnoreCase("Enumerated")) {
                        baseAVPBuilder = new AvpEnumeratedBuilder();
                    } else if (strAttrType.equalsIgnoreCase("UserLocationInfo")) {
                        baseAVPBuilder = new AvpUserLocationInfoAvpBuilder();
                    } else if (strAttrType.equalsIgnoreCase("UserEquipmentInfoValue")) {
                        baseAVPBuilder = new UserEquipmentInfoValueAvpBuilder();
                    } else if (strAttrType.equalsIgnoreCase("UserEquipmentInfo")) {
                        baseAVPBuilder = new UserEquipmentInfoAvpBuilder();
                    } else {
                        baseAVPBuilder = new AvpOctetStringBuilder();
                    }


                    baseAVPBuilder.setAVPCode(iAttrID);
                    baseAVPBuilder.setVendorId(iVendorId);
                    baseAVPBuilder.setAVPId(iVendorId, iAttrID);
                    baseAVPBuilder.setAVPEncryption(strEncryption);

                    if (iVendorId != STANDARD_VENDOR_ID) {
                        baseAVPBuilder.setVendorBit();
                    }
                    if (strMandatory.equalsIgnoreCase("yes")) {
                        baseAVPBuilder.setMandatoryBit();
                    }
                    if (strProtected.equalsIgnoreCase("yes")) {
                        baseAVPBuilder.setProtectedBit();
                    }

                    NodeList subNodeList = node.getChildNodes();

                    for (int k = 0; k < subNodeList.getLength(); k++) {

                        String valueID = null;
                        int ivalueID;
                        String valueName = null;
                        Node DictNameNode = subNodeList.item(k);

                        if (DictNameNode.getNodeName().equals(SUPPORTED_VALUES)) {
                            supportedValueMap = new HashMap<Integer, String>();

                            NodeList valueNodeList = DictNameNode.getChildNodes();

                            for (int m = 0; m < valueNodeList.getLength(); m++) {
                                ivalueID = -1;

                                Node valueNameNode = valueNodeList.item(m);

                                if (valueNameNode.getNodeName().equals(VALUE)) {

                                    if (valueNameNode.getAttributes().getNamedItem(ID).getTextContent() != null) {
                                        valueID = valueNameNode.getAttributes().getNamedItem(ID).getTextContent().trim();

                                        try {
                                            ivalueID = Integer.parseInt(valueID);
                                        } catch (NumberFormatException e) {
                                            if (LogManager.getLogger().isLogLevel(LogLevel.TRACE))
                                                LogManager.getLogger().trace(MODULE, "Not a valid attribute value id " + valueID + " for " + strVendorID + ":" + strVendorName + ":" + attrID);
                                            throw new DictionaryParseException("Not a valid attribute value id " + valueID + " for " + strVendorID + ":" + strVendorName + ":" + attrID);
                                        }
                                    } else {
                                        throw new DictionaryParseException("Attribute value id not found for " + strVendorID + ":" + strVendorName + ":" + attrID);
                                    }

                                    if (valueNameNode.getAttributes().getNamedItem(NAME).getTextContent() != null) {
                                        valueName = valueNameNode.getAttributes().getNamedItem(NAME).getTextContent().trim();
                                    } else {
                                        throw new DictionaryParseException("Attribute value name not found for " + strVendorID + ":" + strVendorName + ":" + attrID);
                                    }

                                }
                                if (ivalueID != -1)
                                    supportedValueMap.put(ivalueID, valueName);

                            }
                            if (strAttrType.equalsIgnoreCase("Enumerated")) {
                                ((AvpEnumeratedBuilder) baseAVPBuilder).setSupportedValuesMap(supportedValueMap);
                            }
                            if (strAttrType.equalsIgnoreCase("Unsigned32")) {
                                ((AvpUnsigned32Builder) baseAVPBuilder).setSupportedValuesMap(supportedValueMap);
                            }


                        } else if (DictNameNode.getNodeName().equals(GROUPED)) {

                            NodeList groupeNodeList = DictNameNode.getChildNodes();

                            fixedAttrList = new ArrayList<AvpRule>();
                            requiredAttrList = new ArrayList<AvpRule>();
                            optionalAttrList = new ArrayList<AvpRule>();

                            Node groupChildNode;
                            String groupChildNodeName;
                            NodeList groupChildNodeList;
                            int groupChildNodeListSize;
                            for (int n = 0; n < groupeNodeList.getLength(); n++) {
                                groupChildNode = groupeNodeList.item(n);
                                groupChildNodeName = groupChildNode.getNodeName();
                                if (groupChildNodeName.equalsIgnoreCase(FIXED) || groupChildNodeName.equalsIgnoreCase(OPTIONAL) || groupChildNodeName.equalsIgnoreCase(REQUIRED)) {
                                    groupChildNodeList = groupChildNode.getChildNodes();
                                    groupChildNodeListSize = groupChildNodeList.getLength();
                                    Node attributeRuleNode;
                                    NamedNodeMap ruleNamedNodeMap;
                                    AvpRule avpRule;
                                    for (int m = 0; m < groupChildNodeListSize; m++) {
                                        attributeRuleNode = groupChildNodeList.item(m);
                                        if (ATTRIBUTE_RULE.equalsIgnoreCase(attributeRuleNode.getNodeName())) {

                                            ruleNamedNodeMap = attributeRuleNode.getAttributes();
                                            avpRule = new AvpRule();
                                            if (ruleNamedNodeMap.getNamedItem("vendor-id").getTextContent() != null) {
                                                try {
                                                    avpRule.setVendorId(Integer.parseInt(ruleNamedNodeMap.getNamedItem("vendor-id").getTextContent()));
                                                } catch (NumberFormatException e) {
                                                    if ("*".equalsIgnoreCase(ruleNamedNodeMap.getNamedItem("vendor-id").getTextContent())) {
                                                        avpRule.setVendorId(-1);
                                                    } else {
                                                        throw new DictionaryParseException("Invalid vendor id found");
                                                    }
                                                }
                                            } else {
                                                throw new DictionaryParseException("Attribute name not found in grouped avp.");
                                            }
                                            if (ruleNamedNodeMap.getNamedItem(ID).getTextContent() != null) {
                                                try {
                                                    avpRule.setAttrId(Integer.parseInt(ruleNamedNodeMap.getNamedItem(ID).getTextContent()));
                                                } catch (NumberFormatException e) {
                                                    if ("*".equalsIgnoreCase(ruleNamedNodeMap.getNamedItem(ID).getTextContent())) {
                                                        avpRule.setAttrId(-1);
                                                    } else {
                                                        throw new DictionaryParseException("Invalid Attribute id found");
                                                    }
                                                }
                                            } else {
                                                throw new DictionaryParseException("Attribute name not found in grouped avp.");
                                            }
                                            if (ruleNamedNodeMap.getNamedItem(NAME).getTextContent() != null) {
                                                avpRule.setName(ruleNamedNodeMap.getNamedItem(NAME).getTextContent());
                                            } else {
                                                throw new DictionaryParseException("Attribute name not found in grouped avp.");
                                            }
                                            if (ruleNamedNodeMap.getNamedItem(MINIMUM).getTextContent() != null) {
                                                avpRule.setMinimum(ruleNamedNodeMap.getNamedItem(MINIMUM).getTextContent());
                                            } else {
                                                throw new DictionaryParseException("Attribute minimum not found in grouped avp.");
                                            }
                                            if (ruleNamedNodeMap.getNamedItem(MAXIMUM).getTextContent() != null) {
                                                avpRule.setMaximum(ruleNamedNodeMap.getNamedItem(MAXIMUM).getTextContent());
                                            } else {
                                                throw new DictionaryParseException("Attribute maximum not found in grouped avp.");
                                            }
                                            if (groupChildNodeName.equalsIgnoreCase(FIXED)) {
                                                fixedAttrList.add(avpRule);
                                            } else if (groupChildNodeName.equalsIgnoreCase(OPTIONAL)) {
                                                optionalAttrList.add(avpRule);
                                            } else {
                                                requiredAttrList.add(avpRule);
                                            }
                                        }
                                    }
                                }
                            }
                            ((BaseGroupAvpBuilder) baseAVPBuilder).setOptionalAttrList(optionalAttrList);
                            ((BaseGroupAvpBuilder) baseAVPBuilder).setFixedAttrList(fixedAttrList);
                            ((BaseGroupAvpBuilder) baseAVPBuilder).setRequiredAttrList(requiredAttrList);
                        }

                    }
                    DiameterAvpId diameterAvpId = new DiameterAvpId(iVendorId, strVendorName, iAttrID, attrName, strMandatory, strProtected, strEncryption, strAttrType, supportedValueMap, requiredAttrList, optionalAttrList, fixedAttrList, isGrouped, xmlFileName);

                    tmpAttributeMap.put(diameterAvpId, baseAVPBuilder);
                    tmpAttributeIdMap.put(strVendorID + ":" + attrID, diameterAvpId);
                    tmpAttributeIdMap.put(attrName, diameterAvpId);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new DictionaryParseException();
        }

        this.xmlReaded.add(xmlFileName);
        vendorMap.putAll(tmpVendorMap);
        attributeMap.putAll(tmpAttributeMap);
        attributeIdMap.putAll(tmpAttributeIdMap);
        tempAttributeIdMap.putAll(tmpAttributeIdMap);

    }


    private void parseGroupedAvp(DiameterAvpId avpId, String parentKey) {

        AvpRule avpRule;
        DiameterAvpId tempAvpId;
        ArrayList<List<AvpRule>> list = new ArrayList<List<AvpRule>>();

        list.add(avpId.getFixedAVPRuleList());
        list.add(avpId.getRequiredAVPRuleList());
        list.add(avpId.getOptionalAVPRuleList());

        for (int i = 0; i < 3; i++) {
            List<AvpRule> avpRuleList = list.get(i);
            if (avpRuleList != null && avpRuleList.size() > 0) {
                for (int j = 0; j < avpRuleList.size(); j++) {
                    avpRule = avpRuleList.get(j);

                    if (!(avpRule.getVendorId() == -1 && avpRule.getAttrId() == -1)) {
                        tempAvpId = attributeIdMap.get(avpRule.getVendorId() + ":" + avpRule.getAttrId());
                        attributeIdMap.put(parentKey + "." + tempAvpId.getAVPId(), tempAvpId);
                        if (tempAvpId.isGrouped()) {
                            parseGroupedAvp(tempAvpId, parentKey + "." + tempAvpId.getAVPId());
                        }
                    }
                }
            }
        }
    }


    public IDiameterAVP getAttribute(int iAttrId) {

        return getAttribute("0:" + iAttrId);
    }

    public List<String> getDictionaryNames() {
        return (ArrayList<String>) ((ArrayList<String>) this.xmlReaded).clone();
    }

    public IDiameterAVP getAttribute(long iVendorId, int iAttrId) {
        return getAttribute(iVendorId + ":" + iAttrId);
    }

    public IDiameterAVP getAttribute(String attrID) {

        IDiameterAVP diameterAVP = null;
        if (attrID != null) {
            DiameterAvpId avpId = getAttributeId(attrID);
            if (avpId != null)
                diameterAVP = (IDiameterAVP) attributeMap.get(avpId).createAVP();
            else {
                LogManager.getLogger().warn(MODULE, "Unknown Attribute: " + attrID);
                diameterAVP = getUnknownAttribute(attrID);
            }

        } else {
            LogManager.getLogger().warn(MODULE, "Unknown Attribute: " + attrID);
            diameterAVP = new UnknownAttribute();
        }
        return diameterAVP;
    }

    public IDiameterAVP getKnownAttribute(String attrID) {

        IDiameterAVP diameterAVP = null;
        if (attrID != null) {
            DiameterAvpId avpId = getAttributeId(attrID);
            if (avpId != null)
                diameterAVP = (IDiameterAVP) attributeMap.get(avpId).createAVP();
        }
        return diameterAVP;
    }

    private UnknownAttribute getUnknownAttribute(String attrID) {
        StringTokenizer tokenizer = new StringTokenizer(attrID, ":");
        String vendorId = null;
        String attrId = null;
        if (tokenizer.hasMoreTokens()) {
            vendorId = tokenizer.nextToken();
            if (tokenizer.hasMoreTokens()) {
                attrId = tokenizer.nextToken();
            }
        }

        if (vendorId != null && attrId != null) {
            try {
                int intVendorId = Integer.parseInt(vendorId);
                int intAttrId = Integer.parseInt(attrId);
                byte bFlag = 0;
                if (intVendorId > 0) {
                    bFlag = (byte) (bFlag | DiameterUtility.BIT_10000000);
                }
                return new UnknownAttribute(intAttrId, intVendorId, bFlag, attrID, "false");
            } catch (Exception e) {
                return new UnknownAttribute();
            }
        } else {
            return new UnknownAttribute();
        }

    }

    public DiameterAvpId getAttributeId(String attrID) {
        DiameterAvpId diameterAvpId = null;
        diameterAvpId = attributeIdMap.get(attrID);


        if (diameterAvpId == null) {
            try {
                StringTokenizer attributeTokens = new StringTokenizer(attrID, ".");
                if (!(attributeTokens.countTokens() < 2)) {
                    String parentId = attributeTokens.nextToken();
                    String childId = attributeTokens.nextToken();
                    DiameterAvpId parentAvpId = attributeIdMap.get(parentId);
                    DiameterAvpId childAvpId = attributeIdMap.get(childId);
                    if (parentAvpId != null && childAvpId != null) {
                        if (childAvpId.isGrouped()) {
                            parseGroupedAvp(childAvpId, parentAvpId.getAVPId() + "." + childAvpId.getAVPId());
                        } else {
                            attributeIdMap.put(attrID, childAvpId);
                        }
                        diameterAvpId = attributeIdMap.get(attrID);
                    }
                }
            } catch (Exception e) {

            }

        }
        return diameterAvpId;
    }

    /**
     * @param intAVPCode argument specifies: code of Avp
     * @return returns the standard vendor's attribute name of Avp-code passed
     */
    public String getAttributeName(int intAVPCode) {
        return getAttributeName("0:" + intAVPCode);
    }

    public String getAttributeName(int intVendorId, int intAVPCode) {
        return getAttributeName(intVendorId + ":" + intAVPCode);
    }

    /**
     * @param strId argument specifies: Vendorid:Avpcode
     * @return returns name of Attribute, if not found then returns
     * <p/>
     * "unknown-attribute".
     */
    public String getAttributeName(String strId) {
        DiameterAvpId avpId = attributeIdMap.get(strId);
        if (avpId != null) {
            return avpId.getAttributeName();
        }
        return "unknown-attribute";
    }

    public String getAttributeID(String name) {
        return String.valueOf(attributeIdMap.get(name).getAttrbuteId());
    }

    public String getStrAVPId(String name) {
        if (name != null && attributeIdMap.get(name) != null)
            return String.valueOf(attributeIdMap.get(name).getAVPId());
        else
            return null;
    }

    public List<String> getVendorIDs() {
        List<String> vendorIds = new ArrayList<String>();
        vendorIds.addAll(this.vandorIdList);
        return vendorIds;
    }

    public String findDictionaryName(File file, long vendorId, long applicationId) {
        if (!file.exists()) {
            return null;
        }
        if (file.isDirectory()) {
            File[] fileList = file.listFiles();
            for (int i = 0; i < fileList.length; i++) {
                return findDictionaryName(fileList[i], vendorId, applicationId);
            }
        } else {
            if (file.getName().endsWith(".xml")) {
                if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
                    LogManager.getLogger().debug(MODULE, "Reading dictionary file [" + file.getName() + "]");
                Reader inStream = null;
                try {
                    inStream = new FileReader(file);
                    if (matchDictionary(inStream, vendorId, applicationId)) {
                        return file.getName();
                    }
                    inStream.close();
                } catch (IOException e) {
                    LogManager.getLogger().trace(MODULE, e);
                    LogManager.getLogger().error(MODULE, e.getMessage());
                }

            }
        }
        return null;
    }

    private boolean matchDictionary(Reader inStream, long vendorId2, long applicationId) {
        DocumentBuilderFactory factory = null;
        DocumentBuilder documentBuilder = null;
        Document docDictionaryXMLParse = null;
        NodeList nodeList = null;


        try {

            String strVendorID = null;
            String strApplicationId = null;

            long lVendorId = 0;
            long lApplicationId = 0;

            factory = DocumentBuilderFactory.newInstance();
            factory.setIgnoringComments(true);
            factory.setIgnoringElementContentWhitespace(true);
            factory.setValidating(false);
            documentBuilder = factory.newDocumentBuilder();
            //			documentBuilder.setErrorHandler(this);

            InputSource inputSource = new InputSource(inStream);
            docDictionaryXMLParse = documentBuilder.parse(inputSource);

            nodeList = docDictionaryXMLParse.getElementsByTagName("attribute-list").item(0).getChildNodes();

            Node vendorNode = docDictionaryXMLParse.getElementsByTagName("attribute-list").item(0);

            if (vendorNode.getNodeName().equals("attribute-list")) {

                if (vendorNode.getAttributes().getNamedItem("vendorid").getTextContent() != null) {
                    strVendorID = vendorNode.getAttributes().getNamedItem("vendorid").getTextContent().trim();
                    try {
                        lVendorId = Integer.parseInt(strVendorID);
                    } catch (NumberFormatException e) {
                        return false;
                    }
                }

                if (vendorNode.getAttributes().getNamedItem("applicationid").getTextContent() != null) {
                    strApplicationId = vendorNode.getAttributes().getNamedItem("applicationid").getTextContent().trim();
                    try {
                        lApplicationId = Integer.parseInt(strApplicationId);
                    } catch (NumberFormatException e) {
                        return false;
                    }
                }
                return lVendorId == vendorId2 && lApplicationId == applicationId;
            }
        } catch (Exception exp) {
        }
        return false;
    }


    public Map<Object, Object> readDictionaryForServerManager(Reader inStream) throws DictionaryParseException {


        Map<Object, Object> resultMap = new HashMap<Object, Object>();
        Map<String, Object> attributeMap = new LinkedHashMap<String, Object>();
        Map<String, Object> attribute = null;

        final String VENDOR_ID = "vendor_id";
        final String VENDOR_NAME = "vendor_name";
        final String APPLICATION_ID = "application_id";
        final String APPLICATION_NAME = "application_name";
        final String ATTRIBUTE_ID = "attribute_id";
        final String ATTRIBUTE_NAME = "attribute_name";
        final String ATTRIBUTE_MANDATORY = "attribute_mandatory";
        final String ATTRIBUTE_PROTECTED = "attribute_protected";
        final String ATTRIBUTE_ENCRYPTION = "attribute_encryption";
        final String ATTRIBUTE_TYPE = "attribute_type";
        final String SUPPORTED_VALUE = "supported_value";
        final String FIXED_GROUP_ATTRIBUTE_LIST = "fixed_group_attribute_list";
        final String REQUIRED_GROUP_ATTRIBUTE_LIST = "required_group_attribute_list";
        final String OPTIONAL_GROUP_ATTRIBUTE_LIST = "optional_group_attribute_list";
        final String ATTRIBUTE_LIST = "attribute_list";


        String strVendorID = null;
        String strVendorName = null;
        String strApplicationId = null;
        String strApplicationName = null;
        String strEncryption = null;
        String strMandatory = null;
        String strProtected = null;
        String strAttrType = null;

        int iVendorId = 0;
        int iApplicationId = 0;

        DocumentBuilderFactory factory = null;
        DocumentBuilder documentBuilder = null;
        Document docDictionaryXMLParse = null;
        NodeList nodeList = null;

        try {
            factory = DocumentBuilderFactory.newInstance();
            factory.setIgnoringComments(true);
            factory.setIgnoringElementContentWhitespace(true);
            factory.setValidating(false);
            documentBuilder = factory.newDocumentBuilder();


            InputSource inputSource = new InputSource(inStream);
            docDictionaryXMLParse = documentBuilder.parse(inputSource);

            nodeList = docDictionaryXMLParse.getElementsByTagName("attribute-list").item(0).getChildNodes();

            Node vendorNode = docDictionaryXMLParse.getElementsByTagName("attribute-list").item(0);
            if (vendorNode.getNodeName().equals("attribute-list")) {

                if (vendorNode.getAttributes().getNamedItem("vendorid").getTextContent() != null) {
                    strVendorID = vendorNode.getAttributes().getNamedItem("vendorid").getTextContent().trim();

                    try {
                        iVendorId = Integer.parseInt(strVendorID);
                        resultMap.put(VENDOR_ID, iVendorId);
                    } catch (NumberFormatException e) {
                        throw new DictionaryParseException(strVendorID + " is not a valid Vendor-Id");
                    }
                } else {
                    throw new DictionaryParseException("Vendor-Id not defined for the dictionary.");
                }

                if (vendorNode.getAttributes().getNamedItem("vendor-name").getTextContent() != null) {
                    strVendorName = vendorNode.getAttributes().getNamedItem("vendor-name").getTextContent().trim();
                    resultMap.put(VENDOR_NAME, strVendorName);

                } else {
                    throw new DictionaryParseException("Vendor-Name not defined for the dictionary.");
                }

                if (vendorNode.getAttributes().getNamedItem("applicationid").getTextContent() != null) {
                    strApplicationId = vendorNode.getAttributes().getNamedItem("applicationid").getTextContent().trim();
                    try {
                        iApplicationId = Integer.parseInt(strApplicationId);
                        resultMap.put(APPLICATION_ID, iApplicationId);
                    } catch (NumberFormatException e) {
                        throw new DictionaryParseException(strVendorID + " is not a valid Application-Id");
                    }
                } else {
                    throw new DictionaryParseException("Application-Id not defined for the dictionary.");
                }

                if (vendorNode.getAttributes().getNamedItem("application-name").getTextContent() != null) {
                    strApplicationName = vendorNode.getAttributes().getNamedItem("application-name").getTextContent().trim();
                    resultMap.put(APPLICATION_NAME, strApplicationName);
                } else {
                    throw new DictionaryParseException("Application-Name not defined for the dictionary.");
                }

                //				Logger.logInfo(MODULE, "Parsing dictionary for " + strVendorID + "-"+ strVendorName );

                for (int i = 0; i < nodeList.getLength(); i++) {
                    attribute = new HashMap<String, Object>();
                    Node node = nodeList.item(i);

                    String attrID = null;
                    int iAttrID = -1;
                    String attrName = null;

                    //BaseDiameterAVP bDiameterAttribute = null;

                    if (node.getNodeName().equals("attribute")) {

                        if (node.getAttributes().getNamedItem("id").getTextContent() != null) {
                            attrID = node.getAttributes().getNamedItem("id").getTextContent().trim();
                            try {
                                iAttrID = Integer.parseInt(attrID);
                                attribute.put(ATTRIBUTE_ID, iAttrID);
                            } catch (NumberFormatException e) {
                                if (LogManager.getLogger().isLogLevel(LogLevel.TRACE))
                                    LogManager.getLogger().trace(MODULE, "Attribute id " + attrID + " is not in proper format for " + strVendorID + ":" + strVendorName);
                                throw new DictionaryParseException("Attribute id " + attrID + " is not in proper format for " + strVendorID + ":" + strVendorName);
                            }
                        }

                        if (node.getAttributes().getNamedItem("name").getTextContent() != null) {
                            attrName = node.getAttributes().getNamedItem("name").getTextContent().trim();
                            attribute.put(ATTRIBUTE_NAME, attrName);
                        } else {
                            throw new DictionaryParseException("Attribute name not specified for " + strVendorID + ":" + strVendorName + ":" + attrID);
                        }

                        if (node.getAttributes().getNamedItem("type").getTextContent() != null) {
                            strAttrType = node.getAttributes().getNamedItem("type").getTextContent().trim();
                            attribute.put(ATTRIBUTE_TYPE, strAttrType);
                        } else {
                            throw new DictionaryParseException("Attribute type not specified for " + strVendorID + ":" + strVendorName + ":" + attrID);
                        }


                        if (node.getAttributes().getNamedItem("protected").getTextContent() != null) {
                            strProtected = node.getAttributes().getNamedItem("protected").getTextContent();
                            attribute.put(ATTRIBUTE_PROTECTED, strProtected);
                        }

                        if (node.getAttributes().getNamedItem("mandatory").getTextContent() != null) {
                            strMandatory = node.getAttributes().getNamedItem("mandatory").getTextContent();
                            attribute.put(ATTRIBUTE_MANDATORY, strMandatory);
                        }

                        if (node.getAttributes().getNamedItem("encryption").getTextContent() != null) {
                            strEncryption = node.getAttributes().getNamedItem("encryption").getTextContent();
                            attribute.put(ATTRIBUTE_ENCRYPTION, strEncryption);
                        }


                        // Now search for supported values for the attribute.
                        StringBuffer strSupportedValue = new StringBuffer("");
                        attribute.put(SUPPORTED_VALUE, strSupportedValue.toString());
                        NodeList subNodeList = node.getChildNodes();


                        for (int k = 0; k < subNodeList.getLength(); k++) {

                            String valueID = null;
                            String valueName = null;
                            Node DictNameNode = subNodeList.item(k);

                            if (DictNameNode.getNodeName().equals("supported-values")) {

                                NodeList valueNodeList = DictNameNode.getChildNodes();

                                for (int m = 0; m < valueNodeList.getLength(); m++) {

                                    Node valueNameNode = valueNodeList.item(m);

                                    if (valueNameNode.getNodeName().equals("value")) {

                                        if (valueNameNode.getAttributes().getNamedItem("id").getTextContent() != null) {
                                            valueID = valueNameNode.getAttributes().getNamedItem("id").getTextContent().trim();

                                            try {
                                                Integer.parseInt(valueID);
                                            } catch (NumberFormatException e) {
                                                if (LogManager.getLogger().isLogLevel(LogLevel.TRACE))
                                                    LogManager.getLogger().trace(MODULE, "Not a valid attribute value id " + valueID + " for " + strVendorID + ":" + strVendorName + ":" + attrID);
                                                throw new DictionaryParseException("Not a valid attribute value id " + valueID + " for " + strVendorID + ":" + strVendorName + ":" + attrID);
                                            }
                                        } else {
                                            throw new DictionaryParseException("Attribute value id not found for " + strVendorID + ":" + strVendorName + ":" + attrID);
                                        }

                                        if (valueNameNode.getAttributes().getNamedItem("name").getTextContent() != null) {
                                            valueName = valueNameNode.getAttributes().getNamedItem("name").getTextContent().trim();
                                        } else {
                                            throw new DictionaryParseException("Attribute value name not found for " + strVendorID + ":" + strVendorName + ":" + attrID);
                                        }

                                        if (valueID != null && valueName != null) {

                                            if (strSupportedValue.toString().equals("")) {
                                                strSupportedValue.append(valueName);
                                                strSupportedValue.append(':');
                                                strSupportedValue.append(valueID);
                                            } else strSupportedValue.append("," + valueName + ":" + valueID);
                                        }


                                    }

                                } // end of for loop
                                attribute.put(SUPPORTED_VALUE, strSupportedValue.toString());

                            } else if (DictNameNode.getNodeName().equalsIgnoreCase("Grouped")) {

                                NodeList groupeNodeList = DictNameNode.getChildNodes();

                                HashMap<String, Object> fixedAttrMap = new HashMap<String, Object>();
                                HashMap<String, Object> requiredAttrMap = new HashMap<String, Object>();
                                HashMap<String, Object> optionalAttrMap = new HashMap<String, Object>();


                                for (int n = 0; n < groupeNodeList.getLength(); n++) {
                                    Node valueNameNode = groupeNodeList.item(n);

                                    if (valueNameNode.getNodeName().equals("fixed")) {

                                        NodeList fixedNodeList = valueNameNode.getChildNodes();

                                        for (int j = 0; j < fixedNodeList.getLength(); j++) {

                                            Node valNameNode = fixedNodeList.item(j);

                                            if (valNameNode.getNodeName().equals("attributerule")) {
                                                AvpRule avpRule = new AvpRule();

                                                if (valNameNode.getAttributes().getNamedItem("vendor-id").getTextContent() != null) {
                                                    try {
                                                        avpRule.setVendorId(Integer.parseInt(valNameNode.getAttributes().getNamedItem("vendor-id").getTextContent()));
                                                    } catch (NumberFormatException e) {
                                                        if ("*".equalsIgnoreCase(valNameNode.getAttributes().getNamedItem("vendor-id").getTextContent())) {
                                                            avpRule.setVendorId(-1);
                                                        } else {
                                                            throw new DictionaryParseException("Invalid vendor id found");
                                                        }
                                                    }
                                                } else {
                                                    throw new DictionaryParseException("Attribute name not found in grouped avp.");
                                                }
                                                if (valNameNode.getAttributes().getNamedItem(ID).getTextContent() != null) {
                                                    try {
                                                        avpRule.setAttrId(Integer.parseInt(valNameNode.getAttributes().getNamedItem(ID).getTextContent()));
                                                    } catch (NumberFormatException e) {
                                                        if ("*".equalsIgnoreCase(valNameNode.getAttributes().getNamedItem(ID).getTextContent())) {
                                                            avpRule.setAttrId(-1);
                                                        } else {
                                                            throw new DictionaryParseException("Invalid Attribute id found");
                                                        }
                                                    }
                                                } else {
                                                    throw new DictionaryParseException("Attribute name not found in grouped avp.");
                                                }

                                                if (valNameNode.getAttributes().getNamedItem("name").getTextContent() != null) {
                                                    avpRule.setName(valNameNode.getAttributes().getNamedItem("name").getTextContent());
                                                } else {
                                                    throw new DictionaryParseException("Attribute name not found in grouped avp.");
                                                }
                                                if (valNameNode.getAttributes().getNamedItem("minimum").getTextContent() != null) {
                                                    avpRule.setMinimum(valNameNode.getAttributes().getNamedItem("minimum").getTextContent());
                                                } else {
                                                    throw new DictionaryParseException("Attribute minimum not found in grouped avp.");
                                                }
                                                if (valNameNode.getAttributes().getNamedItem("maximum").getTextContent() != null) {
                                                    avpRule.setMaximum(valNameNode.getAttributes().getNamedItem("maximum").getTextContent());
                                                } else {
                                                    throw new DictionaryParseException("Attribute maximum not found in grouped avp.");
                                                }
                                                fixedAttrMap.put(avpRule.getName(), avpRule);

                                            }
                                        }
                                        attribute.put(FIXED_GROUP_ATTRIBUTE_LIST, fixedAttrMap);

                                    }
                                    if (valueNameNode.getNodeName().equals("required")) {
                                        NodeList requiredNodeList = valueNameNode.getChildNodes();
                                        for (int j = 0; j < requiredNodeList.getLength(); j++) {

                                            Node valNameNode = requiredNodeList.item(j);
                                            if (valNameNode.getNodeName().equals("attributerule")) {
                                                AvpRule avpRule = new AvpRule();

                                                if (valNameNode.getAttributes().getNamedItem("vendor-id").getTextContent() != null) {
                                                    try {
                                                        avpRule.setVendorId(Integer.parseInt(valNameNode.getAttributes().getNamedItem("vendor-id").getTextContent()));
                                                    } catch (NumberFormatException e) {
                                                        if ("*".equalsIgnoreCase(valNameNode.getAttributes().getNamedItem("vendor-id").getTextContent())) {
                                                            avpRule.setVendorId(-1);
                                                        } else {
                                                            throw new DictionaryParseException("Invalid vendor id found");
                                                        }
                                                    }
                                                } else {
                                                    throw new DictionaryParseException("Attribute name not found in grouped avp.");
                                                }
                                                if (valNameNode.getAttributes().getNamedItem(ID).getTextContent() != null) {
                                                    try {
                                                        avpRule.setAttrId(Integer.parseInt(valNameNode.getAttributes().getNamedItem(ID).getTextContent()));
                                                    } catch (NumberFormatException e) {
                                                        if ("*".equalsIgnoreCase(valNameNode.getAttributes().getNamedItem(ID).getTextContent())) {
                                                            avpRule.setAttrId(-1);
                                                        } else {
                                                            throw new DictionaryParseException("Invalid Attribute id found");
                                                        }
                                                    }
                                                } else {
                                                    throw new DictionaryParseException("Attribute name not found in grouped avp.");
                                                }
                                                if (valNameNode.getAttributes().getNamedItem("name").getTextContent() != null) {
                                                    avpRule.setName(valNameNode.getAttributes().getNamedItem("name").getTextContent());
                                                } else {
                                                    throw new DictionaryParseException("Attribute name not found in grouped avp.");
                                                }
                                                if (valNameNode.getAttributes().getNamedItem("minimum").getTextContent() != null) {
                                                    avpRule.setMinimum(valNameNode.getAttributes().getNamedItem("minimum").getTextContent());
                                                } else {
                                                    throw new DictionaryParseException("Attribute minimum not found in grouped avp.");
                                                }
                                                if (valNameNode.getAttributes().getNamedItem("maximum").getTextContent() != null) {
                                                    avpRule.setMaximum(valNameNode.getAttributes().getNamedItem("maximum").getTextContent());
                                                } else {
                                                    throw new DictionaryParseException("Attribute maximum not found in grouped avp.");
                                                }
                                                requiredAttrMap.put(avpRule.getName(), avpRule);


                                            }
                                        }
                                        attribute.put(REQUIRED_GROUP_ATTRIBUTE_LIST, requiredAttrMap);
                                    }
                                    if (valueNameNode.getNodeName().equals("optional")) {
                                        NodeList optionalNodeList = valueNameNode.getChildNodes();
                                        for (int j = 0; j < optionalNodeList.getLength(); j++) {

                                            Node valNameNode = optionalNodeList.item(j);
                                            if (valNameNode.getNodeName().equals("attributerule")) {
                                                AvpRule avpRule = new AvpRule();

                                                if (valNameNode.getAttributes().getNamedItem("vendor-id").getTextContent() != null) {
                                                    try {
                                                        avpRule.setVendorId(Integer.parseInt(valNameNode.getAttributes().getNamedItem("vendor-id").getTextContent()));
                                                    } catch (NumberFormatException e) {
                                                        if ("*".equalsIgnoreCase(valNameNode.getAttributes().getNamedItem("vendor-id").getTextContent())) {
                                                            avpRule.setVendorId(-1);
                                                        } else {
                                                            throw new DictionaryParseException("Invalid vendor id found");
                                                        }
                                                    }
                                                } else {
                                                    throw new DictionaryParseException("Attribute name not found in grouped avp.");
                                                }
                                                if (valNameNode.getAttributes().getNamedItem(ID).getTextContent() != null) {
                                                    try {
                                                        avpRule.setAttrId(Integer.parseInt(valNameNode.getAttributes().getNamedItem(ID).getTextContent()));
                                                    } catch (NumberFormatException e) {
                                                        if ("*".equalsIgnoreCase(valNameNode.getAttributes().getNamedItem(ID).getTextContent())) {
                                                            avpRule.setAttrId(-1);
                                                        } else {
                                                            throw new DictionaryParseException("Invalid Attribute id found");
                                                        }
                                                    }
                                                } else {
                                                    throw new DictionaryParseException("Attribute name not found in grouped avp.");
                                                }
                                                if (valNameNode.getAttributes().getNamedItem("name").getTextContent() != null) {
                                                    avpRule.setName(valNameNode.getAttributes().getNamedItem("name").getTextContent());
                                                } else {
                                                    throw new DictionaryParseException("Attribute name not found in grouped avp.");
                                                }
                                                if (valNameNode.getAttributes().getNamedItem("minimum").getTextContent() != null) {
                                                    avpRule.setMinimum(valNameNode.getAttributes().getNamedItem("minimum").getTextContent());
                                                } else {
                                                    throw new DictionaryParseException("Attribute minimum not found in grouped avp.");
                                                }
                                                if (valNameNode.getAttributes().getNamedItem("maximum").getTextContent() != null) {
                                                    avpRule.setMaximum(valNameNode.getAttributes().getNamedItem("maximum").getTextContent());
                                                } else {
                                                    throw new DictionaryParseException("Attribute maximum not found in grouped avp.");
                                                }

                                                optionalAttrMap.put(avpRule.getName(), avpRule);
                                            }
                                        }
                                        attribute.put(OPTIONAL_GROUP_ATTRIBUTE_LIST, optionalAttrMap);
                                    }
                                }

                            }
                        }
                        attributeMap.put("" + iAttrID + "", attribute);
                    }


                }

                resultMap.put(ATTRIBUTE_LIST, attributeMap);
            }


        } catch (SAXParseException sax) {
            LogManager.getLogger().error(MODULE, sax.getMessage());
            LogManager.getLogger().trace(MODULE, "Unexpected error while parsing dictionary " + sax);
            throw new DictionaryParseException("Unexpected error while parsing dictionary at Line Number: " + sax.getLineNumber() +
                    " Column Number: " + sax.getColumnNumber());
        } catch (IOException io) {
            LogManager.getLogger().error(MODULE, io.getMessage());
            LogManager.getLogger().trace(MODULE, "Unexpected error while parsing dictionary " + io);
            throw new DictionaryParseException("Unexpected error while parsing dictionary, Reason: " + io.getMessage());
        } catch (Exception ex) {
            LogManager.getLogger().error(MODULE, ex.getMessage());
            LogManager.getLogger().trace(MODULE, "Unexpected error while parsing dictionary " + ex);
            throw new DictionaryParseException("Unexpected error while parsing dictionary ");
        }
        return resultMap;
    }

/*	private abstract class BaseAVPBuilder {

		protected int intAVPCode;
		protected int intVendorId;
		protected String strAvpId="";
		protected String strAVPEncryption;
		protected byte bAVPFlag;

		public abstract IDiameterAVP createAVP() ;

		*//**
     * Set Code for specified AVP
     * @param code Code related to AVP.
     *//*
		public void setAVPCode(int code) {
			intAVPCode = code;
		}
		*//**
     * Set vendor id for Specified AVP.
     *//*
		public void setVendorId(int vendorId){
			this.intVendorId = vendorId;
		}

		public void setVendorBit(){
			bAVPFlag = (byte)(bAVPFlag | DiameterUtility.BIT_10000000);
		}

		public void setMandatoryBit(){
			bAVPFlag = (byte)(bAVPFlag | DiameterUtility.BIT_01000000);
		}

		public void setProtectedBit(){
			bAVPFlag = (byte)(bAVPFlag | DiameterUtility.BIT_00100000);
		}

		public void setAVPEncryption(String encryption) {
			strAVPEncryption = encryption;
		}
		*/

    /**
     * Sets attribute id in the form of "Vendor-id : AvpCode"
     *//*
		public void setAVPId(int vendorId,int AVPCode){
			this.strAvpId = vendorId+":"+AVPCode;
		}

	}
*/    private class AvpUnsigned32Builder extends BaseAVPBuilder {
        Map<Integer, String> supportedValues = new HashMap<Integer, String>();

        public void setSupportedValuesMap(Map<Integer, String> supportedValueMap) {
            this.supportedValues = supportedValueMap;
        }

        @Override
        public IDiameterAVP createAVP() {
            return new AvpUnsigned32(intAVPCode, intVendorId, bAVPFlag, strAvpId, strAVPEncryption, supportedValues);
        }

    }

    private class AvpAddressBuilder extends BaseAVPBuilder {

        @Override
        public IDiameterAVP createAVP() {
            return new AvpAddress(intAVPCode, intVendorId, bAVPFlag, strAvpId, strAVPEncryption);
        }

    }

    private class AvpDiameterIdentityBuilder extends BaseAVPBuilder {

        @Override
        public IDiameterAVP createAVP() {
            return new AvpDiameterIdentity(intAVPCode, intVendorId, bAVPFlag, strAvpId, strAVPEncryption);
        }

    }

    private class AvpDiameterURIBuilder extends BaseAVPBuilder {

        @Override
        public IDiameterAVP createAVP() {
            return new AvpDiameterURI(intAVPCode, intVendorId, bAVPFlag, strAvpId, strAVPEncryption);
        }

    }

    private class AvpEnumeratedBuilder extends BaseAVPBuilder {

        Map<Integer, String> supportedValues = new HashMap<Integer, String>();

        @Override
        public IDiameterAVP createAVP() {
            return new AvpEnumerated(intAVPCode, intVendorId, bAVPFlag, strAvpId, strAVPEncryption, supportedValues);
        }

        public void setSupportedValuesMap(Map<Integer, String> supportedValueMap) {
            this.supportedValues = supportedValueMap;
        }

    }

    private class AvpUserLocationInfoAvpBuilder extends BaseAVPBuilder {

        @Override
        public IDiameterAVP createAVP() {
            return new AvpUserLocationInfo(intAVPCode, intVendorId, bAVPFlag, strAvpId, strAVPEncryption);
        }
    }

    private class UserEquipmentInfoValueAvpBuilder extends BaseAVPBuilder {

        @Override
        public IDiameterAVP createAVP() {
            return new AvpUserEquipmentInfoValue(intAVPCode, intVendorId, bAVPFlag, strAvpId, strAVPEncryption);
        }

    }

    private class UserEquipmentInfoAvpBuilder extends BaseGroupAvpBuilder {
        @Override
        public IDiameterAVP createAVP() {
            return new AvpUserEquipmentInfo(intAVPCode, intVendorId, bAVPFlag, strAvpId, strAVPEncryption, fixedAttrList, requiredAttrList, optionalAttrList);
        }

    }

    private class AvpFloat32Builder extends BaseAVPBuilder {

        @Override
        public IDiameterAVP createAVP() {
            return new AvpFloat32(intAVPCode, intVendorId, bAVPFlag, strAvpId, strAVPEncryption);
        }

    }

    private class AvpFloat64Builder extends BaseAVPBuilder {

        @Override
        public IDiameterAVP createAVP() {
            return new AvpFloat64(intAVPCode, intVendorId, bAVPFlag, strAvpId, strAVPEncryption);
        }

    }

    private class AvpIpv4AddressBuilder extends BaseAVPBuilder {

        @Override
        public IDiameterAVP createAVP() {
            return new AvpIPv4Address(intAVPCode, intVendorId, bAVPFlag, strAvpId, strAVPEncryption);
        }

    }

    public class AvpGroupedBuilder extends BaseGroupAvpBuilder {
        @Override
        public IDiameterAVP createAVP() {
            return new AvpGrouped(intAVPCode, intVendorId, bAVPFlag, strAvpId, strAVPEncryption, fixedAttrList, requiredAttrList, optionalAttrList);
        }


    }

    private class AvpInteger32Builder extends BaseAVPBuilder {

        @Override
        public IDiameterAVP createAVP() {
            return new AvpInteger32(intAVPCode, intVendorId, bAVPFlag, strAvpId, strAVPEncryption);
        }

    }

    private class AvpInteger64Builder extends BaseAVPBuilder {

        @Override
        public IDiameterAVP createAVP() {
            return new AvpInteger64(intAVPCode, intVendorId, bAVPFlag, strAvpId, strAVPEncryption);
        }

    }

    private class AvpOctetStringBuilder extends BaseAVPBuilder {

        @Override
        public IDiameterAVP createAVP() {
            return new AvpOctetString(intAVPCode, intVendorId, bAVPFlag, strAvpId, strAVPEncryption);
        }

    }

    private class AvpTimeBuilder extends BaseAVPBuilder {

        @Override
        public IDiameterAVP createAVP() {
            return new AvpTime(intAVPCode, intVendorId, bAVPFlag, strAvpId, strAVPEncryption);
        }

    }

    private class AvpUnsigned64Builder extends BaseAVPBuilder {

        @Override
        public IDiameterAVP createAVP() {
            return new AvpUnsigned64(intAVPCode, intVendorId, bAVPFlag, strAvpId, strAVPEncryption);
        }

    }

    private class AvpUTF8StringBuilder extends BaseAVPBuilder {

        @Override
        public IDiameterAVP createAVP() {
            return new AvpUTF8String(intAVPCode, intVendorId, bAVPFlag, strAvpId, strAVPEncryption);
        }

    }

    public long getKeyFromValue(String attribute, String Val) {
        DiameterAvpId diameterAvpId = attributeIdMap.get(attribute);
        if (diameterAvpId != null) {

            Long val = diameterAvpId.getKeyForValue(Val);
            if (val != null) {
                return val.longValue();
            }
        }
        return VALUE_NOT_FOUND;
    }

}
