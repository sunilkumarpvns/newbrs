package com.elitecore.test.diameter.factory;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.test.dependecy.diameter.DiameterDictionary;
import com.elitecore.test.dependecy.diameter.packet.avps.IDiameterAVP;
import com.elitecore.test.diameter.DiameterAttributeProvider;
import com.elitecore.test.diameter.jaxb.DiameterAttributeData;

import java.util.ArrayList;

public class DiameterAttributeFactory {
	
	private DiameterAttributeProvider attributeProvider;
	
	public DiameterAttributeFactory(DiameterAttributeProvider attributeProvider) {
		this.attributeProvider = attributeProvider;
	}
	
	public DiameterAttributeFactory() {
		attributeProvider = new DiameterAttributeProvider() {
			
			@Override
			public IDiameterAVP getKnowAtrribute(String id) {
				return DiameterDictionary.getInstance().getKnownAttribute(id);
			}
		};
	}

	/**
	 * @param attributeData that defines Diameter AVPs
	 * @return IDiameterAVP
	 * @throws AttributeNotFoundException when attribute not found in DiameterDictionary,
	 * Null Pointer exception if attributeData is NULL
	 */
	public IDiameterAVP createAVP(DiameterAttributeData attributeData) throws AttributeNotFoundException {
		
		if(attributeData == null){
			throw new NullPointerException("Diameter Attribute data can not be NULL");
		}
		IDiameterAVP diameterAVP = attributeProvider.getKnowAtrribute(attributeData.getId());
		
		if(diameterAVP == null){
			throw new AttributeNotFoundException(attributeData.getId(), "AVP not found for Id: " + attributeData.getId());
		}
		
		if(attributeData.getValue() != null){
			diameterAVP.setStringValue(attributeData.getValue());
			return diameterAVP;
		} 
		
		if(Collectionz.isNullOrEmpty(attributeData.getAttributeDatas()) == true){
			throw new AttributeNotFoundException(attributeData.getId(), "Child AVP(s) not found for Id: " + attributeData.getId());
		}
		
		ArrayList<IDiameterAVP> childAVPs = new ArrayList<IDiameterAVP>(attributeData.getAttributeDatas().size());
		for(DiameterAttributeData tempAttributeData : attributeData.getAttributeDatas()){
			try{
				childAVPs.add(createAVP(tempAttributeData));
			}catch(AttributeNotFoundException ex){
				throw new AttributeNotFoundException(attributeData.getId()+"."+ex.getAttributeId(), ex.getMessage(),ex.getCause());
			}
			
		}
		
		diameterAVP.setGroupedAvp(childAVPs);
		
		return diameterAVP;
		
	}


}
