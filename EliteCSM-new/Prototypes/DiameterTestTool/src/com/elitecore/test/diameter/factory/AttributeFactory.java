package com.elitecore.test.diameter.factory;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.exprlib.parser.expression.ValueProvider;
import com.elitecore.test.exception.AVPNotFoundException;
import com.elitecore.test.dependecy.diameter.DiameterDictionary;
import com.elitecore.test.dependecy.diameter.packet.avps.IDiameterAVP;
import com.elitecore.test.diameter.jaxb.AttributeData;

import java.util.ArrayList;

import javax.annotation.Nullable;

import sun.security.action.GetLongAction;

public class AttributeFactory {
	
	private AttributeProvider attributeProvider;
	
	public AttributeFactory(AttributeProvider attributeProvider) {
		this.attributeProvider = attributeProvider;
	}
	
	public AttributeFactory() {
		attributeProvider = new AttributeProvider() {
			
			@Override
			public IDiameterAVP getKnowAtrribute(String id) {
				IDiameterAVP diameterAVP = DiameterDictionary.getInstance().getKnownAttribute(id);
			//	Asser.assertNotNull(id + " not found from dictionary",diameterAVP);
				return diameterAVP;
			}
		};
	}

	public @Nullable IDiameterAVP createAVP(AttributeData attributeData,ValueProvider valueProvider) throws AVPNotFoundException{
		
		
		IDiameterAVP diameterAVP = attributeProvider.getKnowAtrribute(attributeData.getId());
		
		if(diameterAVP == null){
			
			LogManager.getLogger().warn("", attributeData.getId() + " not found from dictionary");
			return null;
			//throw new AVPNotFoundException(attributeData.getId(), attributeData.getId() + " AVP not found");
		}
		
		if(attributeData.getValue() != null){
			try {
				diameterAVP.setStringValue(attributeData.getExpression().getStringValue(valueProvider));
			} catch (Exception e) {
				return null;
				//throw new AVPNotFoundException(attributeData.getId(),e.getMessage(),e);
			}
			return diameterAVP;
		} 
		
		ArrayList<IDiameterAVP> childAVPs = new ArrayList<IDiameterAVP>(attributeData.getAttributeDatas().size());
		for(AttributeData tempAttributeData : attributeData.getAttributeDatas()){
			try{
				IDiameterAVP avp = createAVP(tempAttributeData,valueProvider);
				if(avp == null) {
					continue;
				}
				
				childAVPs.add(avp);
			}catch(AVPNotFoundException ex){
				//throw new AVPNotFoundException(attributeData.getId()+"."+ex.getId(), ex.getMessage(),ex.getCause());
			}
			
		}
		
		if(childAVPs.isEmpty()) {
			return null;
		}
		
		diameterAVP.setGroupedAvp(childAVPs);
		
		return diameterAVP;
		
		
		
	}


}
