package com.elitecore.netvertex.gateway.diameter;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.constants.CommunicationProtocol;
import com.elitecore.corenetvertex.sm.dictonary.AttributeData;
import com.elitecore.corenetvertex.sm.dictonary.AttributeSupportedValueData;
import com.elitecore.corenetvertex.sm.dictonary.AttributeValueData;
import com.elitecore.corenetvertex.sm.dictonary.VendorInformation;
import com.elitecore.diameterapi.diameter.common.util.constant.AVPType;
import com.elitecore.diameterapi.diameter.common.util.dictionary.DiameterDictionaryModel;

import java.util.*;

public class DiameterDictionaryDataBuilder {

    private static final String OTHER_TYPE = "OCTETS";
    public static final String VENDOR_ID = "1";

    public static VendorInformation getVendorInformation(){
        VendorInformation vendorInformation = new VendorInformation();
        vendorInformation.setName("test");
        vendorInformation.setVendorId(VENDOR_ID);
        vendorInformation.setStatus("Active");
        vendorInformation.setId(VENDOR_ID);

        return vendorInformation;
    }

    public static AttributeData getNullAttribute() {
        AttributeData attributeData = getBasicAttributeInformation();
        attributeData.setType(null);
        attributeData.setDictionaryType(CommunicationProtocol.DIAMETER.name());
        attributeData.setAttributeId("1");
        attributeData.setName("Attrib1");
        return attributeData;
    }

    private static AttributeData getBasicAttributeInformation() {
        AttributeData attributeData = new AttributeData();
        attributeData.setId(UUID.randomUUID().toString());
        attributeData.setStatus("Active");
        attributeData.setVendorInformation(getVendorInformation());
        attributeData.setAttributeVendorId(getVendorInformation().getVendorId());
        attributeData.setChildAttributes(Collections.emptyList());

        return attributeData;
    }

    public static AttributeData getGroupedAttribute() {
        AttributeData attributeData = getBasicAttributeInformation();
        attributeData.setType(AVPType.GROUPED.name());
        attributeData.setDictionaryType(CommunicationProtocol.DIAMETER.name());
        attributeData.setAttributeId("4");
        attributeData.setName("Attrib4");

        return attributeData;
    }

    public static AttributeData getStringAttribute() {
        AttributeData attributeData = getBasicAttributeInformation();
        attributeData.setType(AVPType.UTF8STRING.name());
        attributeData.setDictionaryType(CommunicationProtocol.DIAMETER.name());
        attributeData.setAttributeId("8");
        attributeData.setName("Attrib8");
        return attributeData;
    }

    public static AttributeData getAttributeSupportedValues() {
        AttributeData attributeData = getBasicAttributeInformation();
        attributeData.setType(AVPType.UTF8STRING.name());
        attributeData.setDictionaryType(CommunicationProtocol.DIAMETER.name());
        attributeData.setAttributeId("10");
        attributeData.setName("Attrib10");

        AttributeSupportedValueData attributeSupportedValueData = new AttributeSupportedValueData();
        AttributeValueData attributeValueData = new AttributeValueData();
        attributeValueData.setId("2");
        attributeValueData.setSupportedValueId(22);
        attributeValueData.setName("test");
        HashSet<AttributeValueData> values = new HashSet<>();
        values.add(attributeValueData);
        attributeSupportedValueData.setAttributeValue(values);
        attributeData.setAttributeSupportedValues(values);
        attributeData.setSupportedValues(attributeSupportedValueData);

        return attributeData;
    }

    private static boolean contains(String type) {

        for (AVPType avpType : AVPType.values()) {
            if (avpType.name().equalsIgnoreCase(type)) {
                return true;
            }
        }

        return false;
    }

    public static DiameterDictionaryModel buildDiameterDictionaryModel(List<AttributeData> attributeDatas){

        Map<String, com.elitecore.diameterapi.diameter.common.util.dictionary.VendorInformation> idToVendorInformation = new HashMap<>();
        Set<String> vendorIds = new HashSet<>();
        for (AttributeData attributeData : attributeDatas) {

            if(Objects.isNull(attributeData.getType())){
                continue;
            }

            com.elitecore.diameterapi.diameter.common.util.dictionary.VendorInformation vendorInformation = idToVendorInformation.computeIfAbsent(attributeData.getVendorInformation().getId(), s -> buildVendorInformationForDiameterAPI(attributeData.getVendorInformation()));

            vendorIds.add(attributeData.getVendorInformation().getVendorId());

            com.elitecore.diameterapi.diameter.common.util.dictionary.AttributeData attribute = buildAttributeDetailsForDiameterAPI(attributeData, vendorInformation);
            vendorInformation.addAttribute(attribute);

        }

        return new DiameterDictionaryModel(idToVendorInformation, new ArrayList<>(vendorIds));
    }

    private static com.elitecore.diameterapi.diameter.common.util.dictionary.AttributeData buildAttributeDetailsForDiameterAPI(AttributeData attributeData, com.elitecore.diameterapi.diameter.common.util.dictionary.VendorInformation vendorInformation) {

        if (contains(attributeData.getType()) == false) {
            attributeData.setType(OTHER_TYPE);
        }

        Map<Integer, String> supportedValues = new HashMap<>();
        Set<AttributeValueData> attributeSupportedValues = attributeData.getAttributeSupportedValues();
        if(Collectionz.isNullOrEmpty(attributeSupportedValues) == false) {
            for (AttributeValueData supportedValue : attributeSupportedValues) {
                com.elitecore.diameterapi.diameter.common.util.dictionary.AttributeSupportedValueModel attributeSupportedValueData = new com.elitecore.diameterapi.diameter.common.util.dictionary.AttributeSupportedValueModel(supportedValue.getSupportedValueId(), supportedValue.getName());
                supportedValues.put(attributeSupportedValueData.getId(), attributeSupportedValueData.getName());
            }
        }

        return new com.elitecore.diameterapi.diameter.common.util.dictionary.AttributeData(
                vendorInformation.getVendorId(), attributeData.getAttributeId(), attributeData.getName(), attributeData.getMandatory(), attributeData.getProtectedValue(),
                attributeData.getEncryption(), AVPType.valueOf(attributeData.getType().toUpperCase()), attributeData.getStatus(), attributeData.getDictionaryType(),
                attributeData.getMinimum(), attributeData.getMaximum(), attributeData.getAttributeVendorId(), supportedValues);

    }

    private static com.elitecore.diameterapi.diameter.common.util.dictionary.VendorInformation buildVendorInformationForDiameterAPI(VendorInformation vendorInformation) {
        return new com.elitecore.diameterapi.diameter.common.util.dictionary.VendorInformation(
                vendorInformation.getVendorId(), vendorInformation.getName(), vendorInformation.getStatus());
    }
}
