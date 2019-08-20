package com.elitecore.coreradius.commons.util.dictionary;

import java.io.StringWriter;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlTransient;

import com.elitecore.commons.base.Equality;
import com.elitecore.coreradius.commons.util.RadiusUtility.TabbedPrintWriter;

/**
 * 
 * @author narendra.pathai
 *
 */
public class AttributeSupportedValueModel{
	private long id;
	private String name;
	
	/* Transient Fields */
	private AttributeModel parentAttributeModel;
	
	public AttributeSupportedValueModel() {
	}
	
	public AttributeSupportedValueModel(int id, String name) {
		this.id = id;
		this.name = name;
	}

	@XmlAttribute(name = "id")
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
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
		
		if(!(obj instanceof AttributeSupportedValueModel))
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
		StringWriter stringBuffer = new StringWriter();
		TabbedPrintWriter out = new TabbedPrintWriter(stringBuffer);
		appendTo(out);
		out.close();
		return stringBuffer.toString();
	}
	
	public void appendTo(TabbedPrintWriter out) {
		out.println("Value Id: " + id);
		out.println("Value Name: " + name);
	}

	@XmlTransient
	public AttributeModel getParent() {
		return parentAttributeModel;
	}

	public void setParent(AttributeModel parentAttributeModel) {
		this.parentAttributeModel = parentAttributeModel;
	}
}
