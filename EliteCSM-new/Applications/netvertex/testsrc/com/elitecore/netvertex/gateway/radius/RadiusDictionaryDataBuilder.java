package com.elitecore.netvertex.gateway.radius;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.constants.CommunicationProtocol;
import com.elitecore.corenetvertex.sm.dictonary.AttributeData;
import com.elitecore.corenetvertex.sm.dictonary.AttributeSupportedValueData;
import com.elitecore.corenetvertex.sm.dictonary.AttributeValueData;
import com.elitecore.corenetvertex.sm.dictonary.VendorInformation;
import com.elitecore.coreradius.commons.util.dictionary.AttributeModel;
import com.elitecore.coreradius.commons.util.dictionary.AttributeSupportedValueModel;
import com.elitecore.diameterapi.diameter.common.util.constant.AVPType;

import java.util.*;

public class RadiusDictionaryDataBuilder {

    private static final String OTHER_TYPE = "OCTETS";
    private static final String DICTIONARY_TYPE = "RADIUS";
    public static final String VENDOR_ID = "1";
    public static final String OTHER_ATTRIBUTE_ID = "33";

    public static VendorInformation getVendorInformation(){
        VendorInformation vendorInformation = new VendorInformation();
        vendorInformation.setName("test");
        vendorInformation.setVendorId(VENDOR_ID);
        vendorInformation.setStatus("Active");
        vendorInformation.setId(VENDOR_ID);

        return vendorInformation;
    }

    public static VendorInformation getOtherVendorInformation(){
        VendorInformation vendorInformation = new VendorInformation();
        vendorInformation.setName("test3");
        vendorInformation.setVendorId("3");
        vendorInformation.setStatus("Active");
        vendorInformation.setId("3");

        return vendorInformation;
    }

    public static AttributeData getAttributeDataForOtherVendor(){
        AttributeData attributeData = getBasicAttributeInformation();
        attributeData.setType(AVPType.UNSIGNED64.name());
        attributeData.setDictionaryType(CommunicationProtocol.RADIUS.name());
        attributeData.setAttributeId("33");
        attributeData.setName("Attrib33");
        attributeData.setVendorInformation(getOtherVendorInformation());
        attributeData.setAttributeVendorId(getOtherVendorInformation().getVendorId());

        return attributeData;
    }

    public static AttributeData getNullAttribute() {
        AttributeData attributeData = getBasicAttributeInformation();
        attributeData.setType(null);
        attributeData.setDictionaryType(CommunicationProtocol.RADIUS.name());
        attributeData.setAttributeId("2");
        attributeData.setName("Attrib2");
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
        attributeData.setDictionaryType(CommunicationProtocol.RADIUS.name());
        attributeData.setAttributeId("3");
        attributeData.setName("Attrib3");

        List<AttributeData> childAttributes = getChildAttribute();
        childAttributes.forEach(childAttribute -> childAttribute.setParentAttributeData(attributeData));
        attributeData.setChildAttributes(childAttributes);

        return attributeData;
    }

    public static List<AttributeData> getChildAttribute() {
        List<AttributeData> childAttributes = new ArrayList<>();
        AttributeData subAttributeData = getBasicAttributeInformation();
        subAttributeData.setType(AVPType.GROUPED.name());
        subAttributeData.setDictionaryType(CommunicationProtocol.RADIUS.name());
        subAttributeData.setAttributeId("5");
        subAttributeData.setName("Attrib5");

        List<AttributeData> subChildAttributes = getSubChildAttribute();
        subChildAttributes.forEach(subAttribute -> subAttribute.setParentAttributeData(subAttributeData));
        subAttributeData.setChildAttributes(subChildAttributes);

        childAttributes.add(subAttributeData);

        return childAttributes;
    }

    public static List<AttributeData> getSubChildAttribute() {
        List<AttributeData> childAttributes = new ArrayList<>();
        AttributeData subAttributeData = getBasicAttributeInformation();
        subAttributeData.setType(AVPType.UTF8STRING.name());
        subAttributeData.setDictionaryType(CommunicationProtocol.RADIUS.name());
        subAttributeData.setAttributeId("6");
        subAttributeData.setName("Attrib6");
        childAttributes.add(subAttributeData);

        return childAttributes;
    }

    public static AttributeData getStringAttribute() {
        AttributeData attributeData = getBasicAttributeInformation();
        attributeData.setType(AVPType.UTF8STRING.name());
        attributeData.setDictionaryType(CommunicationProtocol.RADIUS.name());
        attributeData.setAttributeId("7");
        attributeData.setName("Attrib7");
        return attributeData;
    }

    public static AttributeData getSupportedValues() {
        AttributeData attributeData = getBasicAttributeInformation();
        attributeData.setType(AVPType.UTF8STRING.name());
        attributeData.setDictionaryType(CommunicationProtocol.RADIUS.name());
        attributeData.setAttributeId("9");
        attributeData.setName("Attrib9");

        AttributeSupportedValueData attributeSupportedValueData = new AttributeSupportedValueData();
        AttributeValueData attributeValueData = new AttributeValueData();
        attributeValueData.setId("1");
        attributeValueData.setSupportedValueId(11);
        attributeValueData.setName("test");
        HashSet<AttributeValueData> values = new HashSet<>();
        values.add(attributeValueData);
        attributeSupportedValueData.setAttributeValue(values);
        attributeData.setAttributeSupportedValues(values);
        attributeData.setSupportedValues(attributeSupportedValueData);

        return attributeData;
    }

    public static AttributeModel buildAttributeDetailsForRadiusAPI(AttributeData attributeData) {
        return createRadiusAttrData(attributeData);
    }

    public static AttributeModel createRadiusAttrData(AttributeData attributeData) {

        Set<AttributeSupportedValueModel> supportedValues = new HashSet<>();
        Set<AttributeValueData> attributeSupportedValues = attributeData.getAttributeSupportedValues();
        if(Collectionz.isNullOrEmpty(attributeSupportedValues) == false) {
            for (AttributeValueData supportedValue : attributeSupportedValues) {
                AttributeSupportedValueModel attributeSupportedValueModel = new AttributeSupportedValueModel(supportedValue.getSupportedValueId(), supportedValue.getName());
                supportedValues.add(attributeSupportedValueModel);
        }
        }

        AttributeModel attributeModel = new AttributeModel(Integer.parseInt(attributeData.getAttributeId().trim()), attributeData.getName(), attributeData.getType(), supportedValues);

        List<AttributeData> childAttributes = attributeData.getChildAttributes();

        if(Objects.nonNull(childAttributes)) {
            Set<AttributeModel> newChildAttributes = new HashSet<>();
            for (AttributeData currentChild : childAttributes) {
                newChildAttributes.add(createRadiusAttrData(currentChild));
            }
            attributeModel.setSubAttributes(newChildAttributes);
        }else {
            attributeModel.setSubAttributes(Collections.emptySet());
        }

        return attributeModel;
    }

    public static Map<Long,com.elitecore.coreradius.commons.util.dictionary.VendorInformation> buildIdToVendorInformation(List<AttributeData> attributeDatas) {
        HashMap<Long, com.elitecore.coreradius.commons.util.dictionary.VendorInformation> idToVendorInformation = new HashMap<>();

        for (AttributeData attributeData : attributeDatas) {

            if(Objects.isNull(attributeData.getType())) {
                continue;
            }

            long vendorId = Long.parseLong(attributeData.getVendorInformation().getVendorId());

            com.elitecore.coreradius.commons.util.dictionary.VendorInformation vendorInformation1 = idToVendorInformation.computeIfAbsent(vendorId, aLong -> new com.elitecore.coreradius.commons.util.dictionary.VendorInformation(vendorId, attributeData.getVendorInformation().getName(), new HashSet<>()));

            AttributeModel attributeModel = buildAttributeDetailsForRadiusAPI(attributeData);

            vendorInformation1.getAttributeModels().add(attributeModel);

        }

        return idToVendorInformation;
    }
}
