package com.elitecore.corenetvertex.sm.dictonary;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.CascadeType;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Set;

/**
 *
 * @author saloni.shah
 *
 */
@XmlRootElement(name="supported-values")
public class AttributeSupportedValueData{

    private Set<AttributeValueData> attributeValue;

    @OneToMany(cascade={CascadeType.ALL},fetch = FetchType.LAZY, mappedBy = "attributeData", orphanRemoval = true)
    @Fetch(FetchMode.SUBSELECT)
    @XmlElement(name="value")
    public Set<AttributeValueData> getAttributeValue() {
        return attributeValue;
    }

    public void setAttributeValue(Set<AttributeValueData> attributeValue) {
        this.attributeValue = attributeValue;
    }
}

