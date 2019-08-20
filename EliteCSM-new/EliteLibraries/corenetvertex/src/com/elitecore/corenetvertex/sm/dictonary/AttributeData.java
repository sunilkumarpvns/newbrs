package com.elitecore.corenetvertex.sm.dictonary;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.util.ToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringStyle;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import java.util.List;
import java.util.Set;

@XmlRootElement(name="attribute")
@Entity(name="TBLM_ATTRIBUTE")
public class AttributeData {

    private String id;
    private String attributeId;
    private String name;
    private String mandatory;
    private String protectedValue;
    private String encryption;
    private String type;
    private VendorInformation vendorInformation;
    private String status;
    private String dictionaryType;
    private AttributeData parentAttributeData;
    private List<AttributeData> childAttributes;
    private String minimum;
    private String maximum;
    private String attributeVendorId;
    private AttributeSupportedValueData supportedValues;
    private Set<AttributeValueData> attributeSupportedValues;

    @Id
    @Column(name = "ID")
    @GeneratedValue(generator = "eliteSequenceGenerator")
    @XmlTransient
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @XmlAttribute(name="id")
    @Column(name="ATTRIBUTE_ID")
    public String getAttributeId() {
        return attributeId;
    }

    public void setAttributeId(String attributeId) {
        this.attributeId = attributeId;
    }

    @XmlAttribute(name="name")
    @Column(name="NAME")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @XmlAttribute(name="mandatory")
    @Column(name="MANDATORY")
    public String getMandatory() {
        return mandatory;
    }

    public void setMandatory(String mandatory) {
        this.mandatory = mandatory;
    }

    @XmlAttribute(name="protected")
    @Column(name="PROTECTED")
    public String getProtectedValue() {
        return protectedValue;
    }

    public void setProtectedValue(String protectedValue) {
        this.protectedValue = protectedValue;
    }

    @XmlAttribute(name="encryption")
    @Column(name="ENCRYPTION")
    public String getEncryption() {
        return encryption;
    }

    public void setEncryption(String encryption) {
        this.encryption = encryption;
    }

    @XmlAttribute(name="type")
    @Column(name="TYPE")
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @XmlTransient
    @ManyToOne(fetch= FetchType.EAGER)
    @JoinColumn(name="VENDOR_ID")
    public VendorInformation getVendorInformation() {
        return vendorInformation;
    }

    public void setVendorInformation(VendorInformation vendorInformation) {
        this.vendorInformation = vendorInformation;
    }

    @Column(name="STATUS")
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        ToStringBuilder toStringBuilder = new ToStringBuilder(this,
                ToStringStyle.CUSTOM_TO_STRING_STYLE).append("Id", attributeId)
                .append("Name", name)
                .append("Mandatory", mandatory)
                .append("Protected", protectedValue)
                .append("Encryption", encryption)
                .append("Type", type)
                .append("Dictionary Type", dictionaryType);
        if(Collectionz.isNullOrEmpty(childAttributes) == false){
            for(AttributeData attributeData : childAttributes){
                toStringBuilder.append(attributeData.toString());
            }
        }
        if(Collectionz.isNullOrEmpty(attributeSupportedValues) == false){
            toStringBuilder.append("Attribute Supported Value");
            for(AttributeValueData supportedValue: attributeSupportedValues) {
                toStringBuilder.append(supportedValue.toString());
            }
        }
        return toStringBuilder.toString();
    }


    @OneToMany(cascade={CascadeType.ALL},fetch = FetchType.LAZY, mappedBy = "parentAttributeData",orphanRemoval = true)
    @Fetch(FetchMode.SUBSELECT)
    @XmlElement(name="attribute")
    public List<AttributeData> getChildAttributes() {
        return childAttributes;
    }

    public void setChildAttributes(List<AttributeData> childAttributes) {
        this.childAttributes = childAttributes;
    }

    @Column(name="DICTIONARY_TYPE")
    public String getDictionaryType() {
        return dictionaryType;
    }

    public void setDictionaryType(String dictionaryType) {
        this.dictionaryType = dictionaryType;
    }

    @ManyToOne(cascade={CascadeType.ALL})
    @JoinColumn(name="PARENT_ATTRIBUTE_ID")
    public AttributeData getParentAttributeData() {
        return parentAttributeData;
    }

    public void setParentAttributeData(AttributeData parentAttributeData) {
        this.parentAttributeData = parentAttributeData;
    }

    @Column(name="MINIMUM")
    @XmlAttribute(name="minimum")
    public String getMinimum() {
        return minimum;
    }

    public void setMinimum(String minimum) {
        this.minimum = minimum;
    }

    @Column(name="MAXIMUM")
    @XmlAttribute(name="maximum")
    public String getMaximum() {
        return maximum;
    }

    public void setMaximum(String maximum) {
        this.maximum = maximum;
    }

    @Column(name="ATTRIBUTE_VENDOR_ID")
    @XmlAttribute(name="vendor-id")
    public String getAttributeVendorId() {
        return attributeVendorId;
    }

    public void setAttributeVendorId(String attributeVendorId) {
        this.attributeVendorId = attributeVendorId;
    }

    //FIXME:SALONI SHAH: REMOVE IT AFTER DB DUMP IS SUCCESSFUL
    @Transient
    @XmlElement(name="supported-values")
    public AttributeSupportedValueData getSupportedValues() { return supportedValues; }

    public void setSupportedValues(AttributeSupportedValueData supportedValues) { this.supportedValues = supportedValues; }

    @OneToMany(cascade={CascadeType.ALL},fetch = FetchType.LAZY, mappedBy = "attributeData", orphanRemoval = true)
    @Fetch(FetchMode.SUBSELECT)
    @XmlElement(name="value")
    public Set<AttributeValueData> getAttributeSupportedValues() {
        return attributeSupportedValues;
    }

    public void setAttributeSupportedValues(Set<AttributeValueData> attributeSupportedValues) {
        this.attributeSupportedValues = attributeSupportedValues;
    }


}