package com.elitecore.aaa.rm.translator.data;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.elitecore.aaa.core.commons.util.exception.AttributeNotFoundException;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.util.DiameterDictionary;

@XmlRootElement(name = "attribute")
public class DiameterAttributeData {

	@Nonnull private String id;
	@Nullable private String value;
	@Nullable private List<DiameterAttributeData> attributeDatas;

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
	
	/**
	 * @param attributeData that defines Diameter AVPs
	 * @return IDiameterAVP
	 * @throws AttributeNotFoundException when attribute not found in {@link DiameterDictionary},
	 */
	public IDiameterAVP create() throws AttributeNotFoundException {
		
		IDiameterAVP diameterAVP = DiameterDictionary.getInstance().getKnownAttribute(getId());
		
		if(diameterAVP == null){
			throw new AttributeNotFoundException(getId(), 
					"AVP not found for Id: " + getId());
		}
		
		if(getValue() != null){
			diameterAVP.setStringValue(getValue());
			return diameterAVP;
		} 
		
		if(Collectionz.isNullOrEmpty(getAttributeDatas())){
			throw new AttributeNotFoundException(getId(), 
					"Child AVP(s) not found for Id: " + getId());
		}
		
		ArrayList<IDiameterAVP> childAVPs = new ArrayList<IDiameterAVP>(getAttributeDatas().size());
		for(DiameterAttributeData tempAttributeData : getAttributeDatas()){
			try{
				childAVPs.add(tempAttributeData.create());
			}catch(AttributeNotFoundException ex){
				throw new AttributeNotFoundException(getId()+"."+ex.getAttributeId(), ex.getMessage(),ex.getCause());
			}
		}
		diameterAVP.setGroupedAvp(childAVPs);
		
		return diameterAVP;
	}
}
