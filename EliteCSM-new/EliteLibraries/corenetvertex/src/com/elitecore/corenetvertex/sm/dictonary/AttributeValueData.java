package com.elitecore.corenetvertex.sm.dictonary;

import com.elitecore.commons.base.Equality;
import com.elitecore.corenetvertex.util.ToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringStyle;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author saloni.shah
 *
 */
@XmlRootElement(name="value")
@Entity(name="TBLM_SUPPORTED_VALUES")
public class AttributeValueData{

    private String id;
    private Integer supportedValueId;
    private String name;
    private AttributeData attributeData;

    @Id
    @Column(name="ID")
    @GeneratedValue(generator = "eliteSequenceGenerator")
    @XmlTransient
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Column(name="SUPPORTED_VALUE_ID")
    @XmlAttribute(name="id")
    public Integer getSupportedValueId() { return supportedValueId; }

    public void setSupportedValueId(Integer supportedValueId) { this.supportedValueId = supportedValueId; }

    @Column(name="NAME")
    @XmlAttribute(name="name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @OneToOne(cascade={CascadeType.ALL})
    @JoinColumn(name="ATTRIBUTE_ID")
    @XmlTransient
    public AttributeData getAttributeData() {
        return attributeData;
    }

    public void setAttributeData(AttributeData attributeData) {
        this.attributeData = attributeData;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) {
            return true;
        }

        if(!(obj instanceof AttributeSupportedValueData)) {
            return false;
        }

        AttributeValueData other = (AttributeValueData) obj;

        return Equality.areEqual(getSupportedValueId(), other.getSupportedValueId())
                && Equality.areEqual(getName(), other.getName());
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        ToStringBuilder toStringBuilder = new ToStringBuilder(this,
                ToStringStyle.CUSTOM_TO_STRING_STYLE)
                .append("Id", supportedValueId)
                .append("Name", name);
        return toStringBuilder.toString();
    }
}

