package com.elitecore.diameterapi.diameter.common.util.dictionary;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.io.IndentingPrintWriter;
import com.elitecore.commons.io.IndentingWriter;
import com.elitecore.diameterapi.diameter.common.util.constant.AVPType;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/*
* To maintain attribute information of vendor
* */
public class AttributeData {

    private String attributeId;
    private String name;
    private String mandatory;
    private String protectedValue;
    private String encryption;
    private AVPType type;
    private String status;
    private String dictionaryType;
    private AttributeData childAttributeData;
    private String minimum;
    private String maximum;
    private String attributeVendorId;
    private String strAvpId="";

    private Map<String,Integer> supportedValueToValue;
    private Map<Integer,String> idToSupportedValue;
    private Set<AttributeSupportedValueModel> supportedValues = Collectionz.newHashSet();

    private static final String ATTRIBUTE_ID = "id";
    private static final String ATTRIBUTE_NAME = "name";
    private static final String ATTRIBUTE_TYPE = "type";
    private static final String ATTRIBUTE_SUPPORTED_VALUES = "supported-values";
    private static final String ATTRIBUTE_SUPPORTED_VALUE = "value";

    public AttributeData(){}
    public AttributeData(String vendorId,
                         String attributeId,
                         String name,
                         String mandatory,
                         String protectedValue,
                         String encryption,
                         AVPType type,
                         String status,
                         String dictionaryType,
                         String minimum,
                         String maximum,
                         String attributeVendorId,
                         Map<Integer, String> valueToSupportedValue) {
        this.attributeId = attributeId;
        this.name = name;
        this.mandatory = mandatory;
        this.protectedValue = protectedValue;
        this.encryption = encryption;
        this.type = type;
        this.status = status;
        this.dictionaryType = dictionaryType;
        this.minimum = minimum;
        this.maximum = maximum;
        this.attributeVendorId = attributeVendorId;
        this.strAvpId = vendorId + ":" + attributeId;

        if (valueToSupportedValue != null){
            this.idToSupportedValue = valueToSupportedValue;
            this.supportedValueToValue = new HashMap<>();
            Iterator<Integer> iterator = valueToSupportedValue.keySet().iterator();
            while(iterator.hasNext()){
                Integer key = iterator.next();
                String strVal = valueToSupportedValue.get(key);
                supportedValueToValue.put(strVal, key);


                AttributeSupportedValueModel attributeSupportedValueModel = new AttributeSupportedValueModel(key, strVal);
                supportedValues.add(attributeSupportedValueModel);
            }
        }
    }

    @XmlAttribute(name = ATTRIBUTE_ID)
    public String getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(String attributeId) {
        this.attributeId = attributeId;
    }

    @XmlAttribute(name = ATTRIBUTE_NAME)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlAttribute(name="mandatory")
    public String getMandatory() {
        return mandatory;
    }

    public void setMandatory(String mandatory) {
        this.mandatory = mandatory;
    }

    @XmlAttribute(name="protected")
    public String getProtectedValue() {
        return protectedValue;
    }

    public void setProtectedValue(String protectedValue) {
        this.protectedValue = protectedValue;
    }

    @XmlAttribute(name="encryption")
    public String getEncryption() {
        return encryption;
    }

    public void setEncryption(String encryption) {
        this.encryption = encryption;
    }

    @XmlAttribute(name=ATTRIBUTE_TYPE)
    public AVPType getType() {
        return type;
    }

    public void setType(AVPType type) {
        if(type != null){
            this.type = type;
        }
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDictionaryType() {
        return dictionaryType;
    }

    public void setDictionaryType(String dictionaryType) {
        this.dictionaryType = dictionaryType;
    }

    public AttributeData getAttributeData() {
        return childAttributeData;
    }

    public void setAttributeData(AttributeData attributeData) {
        this.childAttributeData = attributeData;
    }

    @XmlAttribute(name="minimum")
    public String getMinimum() {
        return minimum;
    }

    public void setMinimum(String minimum) {
        this.minimum = minimum;
    }

    @XmlAttribute(name="maximum")
    public String getMaximum() {
        return maximum;
    }

    public void setMaximum(String maximum) {
        this.maximum = maximum;
    }

    @XmlAttribute(name="vendor-id")
    public String getAttributeVendorId() {
        return attributeVendorId;
    }

    public void setAttributeVendorId(String attributeVendorId) {
        this.attributeVendorId = attributeVendorId;
    }

    /**
     * Returns attribute id in the form of "Vendor-id : AvpCode"
     */
    public String getAVPId(){
        return this.strAvpId;
    }

    public void setAVPId(String vendorId, String attributeId){
        this.strAvpId = vendorId + ":" + attributeId;
    }

    public Map<Integer, String> getIdToSupportedValues() {
        return idToSupportedValue;
    }

    @Override
    public String toString() {
        StringWriter stringWriter = new StringWriter();
        IndentingWriter out = new IndentingPrintWriter(stringWriter);
        out.println("Attribute Id: " + attributeId );
        out.println("Name: " + name);
        out.println("Mandatory: " + mandatory);
        out.println("Protected Value: " + protectedValue);
        out.println("Type: " + type);
        out.println("Status: " + status);
        out.println("DictionaryType: " + dictionaryType);
        if(type == AVPType.GROUPED && Objects.nonNull(childAttributeData)) {
            out.println("AttributeData: ");
            out.incrementIndentation();
            out.println(childAttributeData.toString());
            out.decrementIndentation();
        }
        out.println("Minimum: " + minimum);
        out.println("Maximum: " + maximum);
        out.println("Attribute Vendor Id: " + attributeVendorId);
        out.println("AVP Id: " + strAvpId);
        out.close();

        return stringWriter.toString();
    }

    public boolean isGrouped() {
        return false;
    }

    public long getKeyForValue(String val){
        if (supportedValueToValue == null ) {
            return -1;
        }

        Integer key = supportedValueToValue.get(val);
        if (key == null)
            return -1;
        return key.longValue();

    }

    @XmlElementWrapper(name = ATTRIBUTE_SUPPORTED_VALUES)
    @XmlElement(name = ATTRIBUTE_SUPPORTED_VALUE)
    public Set<AttributeSupportedValueModel> getSupportedValues() {
        return supportedValues;
    }
}
