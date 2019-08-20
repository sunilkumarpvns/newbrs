package com.elitecore.diameterapi.diameter.common.util.dictionary;

import com.elitecore.commons.base.Equality;
import com.elitecore.commons.io.IndentingPrintWriter;
import com.elitecore.commons.io.IndentingWriter;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;
import java.io.StringWriter;

public class AttributeSupportedValueModel{
    private int id;
    private String name;

    /* Transient Fields */
    private AttributeData parentAttributeModel;

    public AttributeSupportedValueModel() { }

    public AttributeSupportedValueModel(int id, String name) {
        this.id = id;
        this.name = name;
    }

    @XmlAttribute(name = "id")
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @XmlAttribute(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj)
            return true;

        if(obj instanceof AttributeSupportedValueModel == false)
            return false;

        AttributeSupportedValueModel other = (AttributeSupportedValueModel) obj;

        return Equality.areEqual(getId(), other.getId())
                && Equality.areEqual(getName(), other.getName());
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        StringWriter stringWriter = new StringWriter();
        IndentingWriter out = new IndentingPrintWriter(stringWriter);
        out.println("Supported Value Id: " + id);
        out.println("Name: " + name);
        out.close();

        return stringWriter.toString();
    }

    @XmlTransient
    public AttributeData getParent() {
        return parentAttributeModel;
    }

    public void setParent(AttributeData parentAttributeModel) {
        this.parentAttributeModel = parentAttributeModel;
    }
}
