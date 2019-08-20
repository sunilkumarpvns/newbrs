package com.elitecore.diameterapi.diameter.common.util.dictionary;

import com.elitecore.commons.base.Equality;
import com.elitecore.commons.io.IndentingPrintWriter;
import com.elitecore.commons.io.IndentingWriter;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

/*
* To maintain vendor information
* */
@XmlRootElement(name = "attribute-list")
public class VendorInformation {

    private String vendorId;
    private String name;
    private String status;
    private List<AttributeData> attributeData;

    public VendorInformation() {
        attributeData = new ArrayList<>();
    }

    public VendorInformation(String vendorId, String name, String status) {
        this.vendorId = vendorId;
        this.name = name;
        this.status = status;
        attributeData = new ArrayList<>();
    }

    @XmlAttribute(name = "vendorid", required = true)
    public String getVendorId() {
        return vendorId;
    }

    public void setVendorId(String vendorId) {
        this.vendorId = vendorId;
    }

    @XmlAttribute(name = "vendor-name", required = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getStatus() {
        return status;
    }

    @XmlElement(name = "attribute")
    public List<AttributeData> getAttributeData() {
        return attributeData;
    }

    public void setAttributeData(List<AttributeData> attributeData) {
        this.attributeData = attributeData;
    }

    @Override
    public String toString() {
        StringWriter stringWriter = new StringWriter();
        IndentingWriter out = new IndentingPrintWriter(stringWriter);
        out.println("Vendor: " + vendorId);
        out.println("Name: " + name);
        out.println("Status: " + status);
        out.println("AttributeData: ");
        out.incrementIndentation();
        for (AttributeData attributeDetails : attributeData) {
            out.println(attributeDetails.toString());
        }
        out.decrementIndentation();
        out.println("Application Map: ");
        out.incrementIndentation();
        out.decrementIndentation();
        out.close();

        return stringWriter.toString();
    }

    public void addAttribute(AttributeData attribute) {
        attributeData.add(attribute);
    }

    @Override
    public int hashCode() {
        return getName().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;

        if (!(obj instanceof VendorInformation))
            return false;

        VendorInformation that = (VendorInformation) obj;
        return Equality.areEqual(getVendorId(), that.getVendorId())
                && Equality.areEqual(getName(), that.getName());
    }
}
