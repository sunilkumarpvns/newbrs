package com.elitecore.test.diameter.jaxb;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.gson.annotations.SerializedName;

@XmlRootElement(name = "attribute")
public class DiameterAttributeData {

	@SerializedName("id") @Nonnull private String id;
	@SerializedName("val") @Nullable private String value;
	@SerializedName("sub-attribut")@Nullable private List<DiameterAttributeData> attributeDatas;

	@XmlAttribute(name = "id",required=true)
	public String getId() { 
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@XmlAttribute(name = "value")
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@XmlElement(name="attribute")
	public List<DiameterAttributeData> getAttributeDatas() {
		return attributeDatas;
	}

	public void setAttributeDatas(List<DiameterAttributeData> attributeDatas) {
		this.attributeDatas = attributeDatas;
	}
	
}
