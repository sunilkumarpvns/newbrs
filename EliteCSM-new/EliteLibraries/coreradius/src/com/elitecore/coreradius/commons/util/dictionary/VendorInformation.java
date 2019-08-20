package com.elitecore.coreradius.commons.util.dictionary;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.elitecore.commons.base.Equality;
import com.elitecore.coreradius.commons.util.DictionaryParseException;
import com.elitecore.coreradius.commons.util.RadiusUtility.TabbedPrintWriter;

/**
 *
 * @author narendra.pathai
 *
 */
@XmlRootElement(name = "attribute-list")
public class VendorInformation {

    private static final String DEFAULT_AVPAIR_SEPARATOR = " ";
    private static final String DEFAULT_FORMAT = "1,1";
    private static final int DEFAULT_BYTECOUNT_FOR_TYPE_STANDARD_RADIUS = 1;
    private static final int DEFAULT_BYTECOUNT_FOR_LENGTH_STANDARD_RADIUS = 1;

    private long vendorId;
    private String vendorName;
    private Set<AttributeModel> attributeModels;
    private String avPairSeparator;
    private String format;

    /* ----- Transient Fields ------*/
    private int formatBytesCountForIdentifier;

    private int formatBytesCountForLength;

    private Map<Integer, AttributeModel> attributeIDToAttributeModel;

    public VendorInformation() {
        attributeModels = new HashSet<>();
        attributeIDToAttributeModel = new HashMap<>();
        avPairSeparator = DEFAULT_AVPAIR_SEPARATOR;
        format = DEFAULT_FORMAT;
        formatBytesCountForIdentifier = DEFAULT_BYTECOUNT_FOR_TYPE_STANDARD_RADIUS;
        formatBytesCountForLength = DEFAULT_BYTECOUNT_FOR_LENGTH_STANDARD_RADIUS;
    }

    public VendorInformation(long vendorId, String vendorName, Set<AttributeModel> attributeModels) {
        this.vendorId = vendorId;
        this.vendorName = vendorName;
        this.attributeModels = attributeModels;
    }

    @XmlElement(name = "attribute", required = false)
    public Set<AttributeModel> getAttributeModels() {
        return attributeModels;
    }

    public void setAttributeModels(Set<AttributeModel> attributeModels) {
        this.attributeModels = attributeModels;
    }

    @XmlAttribute(name = "vendorid", required = true)
    public long getVendorId() {
        return vendorId;
    }

    public void setVendorId(long vendorId) {
        this.vendorId = vendorId;
    }

    @XmlAttribute(name = "vendor-name", required = true)
    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    @XmlAttribute(name = "avpair-separator", required = false)
    public String getAvPairSeparator() {
        return avPairSeparator;
    }

    public void setAvPairSeparator(String avPairSeparator) {
        this.avPairSeparator = avPairSeparator;
    }

    @XmlAttribute(name = "format", required = false)
    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public int getFormatBytesCountForIdentifier() {
        return formatBytesCountForIdentifier;
    }

    private void setFormatBytesCountForIdentifier(int formatBytesCountForIdentifier) {
        this.formatBytesCountForIdentifier = formatBytesCountForIdentifier;
    }

    public int getFormatBytesCountForLength() {
        return formatBytesCountForLength;
    }

    private void setFormatBytesCountForLength(int formatBytesCountForLength) {
        this.formatBytesCountForLength = formatBytesCountForLength;
    }

    public AttributeModel getAttributeModelForId(int id){
        return attributeIDToAttributeModel.get(id);
    }

    @Override
    public int hashCode() {
        return getVendorName().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == this)
            return true;

        if(!(obj instanceof VendorInformation))
            return false;

        VendorInformation that = (VendorInformation) obj;
        return Equality.areEqual(getVendorId(), that.getVendorId())
                && Equality.areEqual(getVendorName(), that.getVendorName());
    }

    @Override
    public String toString() {
        StringWriter stringBuffer = new StringWriter();
        TabbedPrintWriter out = new TabbedPrintWriter(stringBuffer);
        out.println("--------Dictionary:" + vendorName + "(" + vendorId + ")--Format:(" + getFormat() + ")--------");

        out.incrementIndentation();
        out.println("-----Attributes-----");
        for(AttributeModel attributeModel : attributeModels){
            attributeModel.appendTo(out);
        }
        out.decrementIndentation();

        out.println("-----------------------------------");
        out.close();
        return stringBuffer.toString();
    }

    public void postRead() throws DictionaryParseException {
        postReadForAttributeFormat();
        postReadForAttributeModels();
    }

    private void postReadForAttributeModels() {
        for(AttributeModel attributeModel : getAttributeModels()){
            attributeModel.postRead();
            attributeIDToAttributeModel.put(attributeModel.getId(), attributeModel);
        }
    }

    private void postReadForAttributeFormat() throws DictionaryParseException {
        String[] formatElements = getFormat().trim().split(",");
        if(formatElements.length != 2){
            throw new DictionaryParseException("Improper attribute format: " + getFormat()
                    + " for vendor:[id:" + getVendorId() + ", name: " + getVendorName() + "]");
        }

        try{
            setFormatBytesCountForIdentifier(
                    assertGreaterThan(Integer.parseInt(formatElements[0]), 0,
                            "Improper attribute format: " + getFormat()
                                    + " for vendor:[id:" + getVendorId() + ", name: " + getVendorName() + "], Reason: Negative values not allowed"
                    )
            );
            setFormatBytesCountForLength(
                    assertGreaterThan(Integer.parseInt(formatElements[1]), 0,
                            "Improper attribute format: " + getFormat()
                                    + " for vendor:[id:" + getVendorId() + ", name: " + getVendorName() + "], Reason: Negative values not allowed"
                    )
            );
        }catch (NumberFormatException e) {
            throw new DictionaryParseException("Improper attribute format: " + getFormat()
                    + " for vendor:[id:" + getVendorId() + ", name: " + getVendorName() + "], Reason: Only numeric values are allowed");
        }
    }

    private int assertGreaterThan(int actualValue, int baseValue, String message) throws DictionaryParseException{
        if(actualValue <= baseValue){
            throw new DictionaryParseException(message);
        }
        return actualValue;
    }
}
