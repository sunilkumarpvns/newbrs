package com.elitecore.aaa.core.radius.packet.jaxb;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.elitecore.aaa.core.commons.util.exception.AttributeNotFoundException;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.attributes.IRadiusGroupedAttribute;
import com.elitecore.coreradius.commons.util.Dictionary;

@XmlRootElement(name = "attribute")
public class RadiusAttributeData {
	
	private String id;
	private String value;
	private List<RadiusAttributeData> attributeDatas;

	public RadiusAttributeData() {
		attributeDatas = new ArrayList<RadiusAttributeData>();
	}
	
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
	public List<RadiusAttributeData> getAttributeDatas() {
		return attributeDatas;
	}

	public void setAttributeDatas(List<RadiusAttributeData> attributeDatas) {
		this.attributeDatas = attributeDatas;
	}

	/**
	 * @param attributeData that defines radius attributes
	 * @return IRadiusAttribute
	 * @throws AttributeNotFoundException when attribute not found in {@link Dictionary},
	 */
	public IRadiusAttribute create() throws AttributeNotFoundException {
		
		IRadiusAttribute radAttr = Dictionary.getInstance().getKnownAttribute(getId());
		
		if(radAttr == null){
			throw new AttributeNotFoundException(getId(), "AVP not found for Id: " + getId());
		}
		
		if(getValue() != null){
			radAttr.setStringValue(getValue());
			return radAttr;
		} 
		
		if(Collectionz.isNullOrEmpty(getAttributeDatas()) == true){
			throw new AttributeNotFoundException(getId(), "Child attributes(s) not found for Id: " + getId());
		}
		
		for(RadiusAttributeData tempAttributeData : getAttributeDatas()){
			try{
				
				((IRadiusGroupedAttribute)radAttr).addTLVAttribute(tempAttributeData.create());
				
			}catch(AttributeNotFoundException ex){
				throw new AttributeNotFoundException(getId()+"."+ex.getAttributeId(), ex.getMessage(),ex.getCause());
			}
		}
		return radAttr;
		
	}


}
