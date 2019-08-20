package com.elitecore.aaa.radius.util.converters;

import java.util.Collection;
import java.util.ArrayList;
import java.util.StringTokenizer;

import com.elitecore.aaa.radius.service.auth.RadAuthRequest;
import com.elitecore.aaa.radius.service.auth.RadAuthResponse;
import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.constants.Constants3GPP2;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;
import com.elitecore.coreradius.commons.attributes.BaseRadiusAttribute;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;


public class ThreeGPP2PrepaidConverter implements IPrepaidConverter{
	
	@Override
	public void convertFromStandard(RadAuthRequest request) throws Exception {
		Collection<IRadiusAttribute> prepaidAttributes = request.getRadiusAttributes(RadiusConstants.VENDOR_3GPP2_ID,Constants3GPP2.PPAQ);
		if(prepaidAttributes != null){
			final int prepaidAttributesSize = prepaidAttributes.size();
			for(int i=0; i<prepaidAttributesSize; i++){
				BaseRadiusAttribute prepaidAttr = (BaseRadiusAttribute) ((ArrayList<IRadiusAttribute>)prepaidAttributes).get(i);
				
				Collection<IRadiusAttribute> subAttributes = prepaidAttr.getAttributes();
				if(subAttributes != null){
					StringBuffer strPrepaidValue = new StringBuffer(30);
					final int subAttrSize = subAttributes.size();
					for(int j=0; j<subAttrSize; j++){
						IRadiusAttribute subAttr = (BaseRadiusAttribute) ((ArrayList<IRadiusAttribute>)subAttributes).get(j);
						
						String key = ThreeGPP2PrepaidConstants.getKey(subAttr.getType());
						if(key != null){
							if(strPrepaidValue.length() > 0){								
								strPrepaidValue.append(',');
							}
							strPrepaidValue.append(key);
							strPrepaidValue.append('=');
							strPrepaidValue.append(subAttr.getStringValue(false));
						}
					}
					
					if(strPrepaidValue.length() > 0){
						IRadiusAttribute elitePrepaidAttribute = Dictionary.getInstance().getAttribute(RadiusConstants.ELITECORE_VENDOR_ID, RadiusAttributeConstants.ELITE_PPAQ_AVPAIR);
						elitePrepaidAttribute.setStringValue(strPrepaidValue.toString());
						request.addInfoAttribute(elitePrepaidAttribute);						
					}
				}
			}
		}
		
	}

	@Override
	public void convertToStandard(RadAuthResponse response) throws Exception {
		
		Collection<IRadiusAttribute> elitePrepaidAttrColl = response.getRadiusAttributes(RadiusConstants.ELITECORE_VENDOR_ID, RadiusAttributeConstants.ELITE_PPAQ_AVPAIR);
		if(elitePrepaidAttrColl != null){
			final int elitePrepaidAttrCollSize = elitePrepaidAttrColl.size();
			for(int i=0; i<elitePrepaidAttrCollSize; i++){
				IRadiusAttribute elitePrepaidAttr = ((ArrayList<IRadiusAttribute>)elitePrepaidAttrColl).get(i);
				String strPrepaidValue = elitePrepaidAttr.getStringValue();
				
				BaseRadiusAttribute ppaqAttr = (BaseRadiusAttribute)Dictionary.getInstance().getAttribute(RadiusConstants.VENDOR_3GPP2_ID, Constants3GPP2.PPAQ);
				
				StringTokenizer strTokenizer = new StringTokenizer(strPrepaidValue, ",;");
				
				while(strTokenizer.hasMoreTokens()){
					String avpair = strTokenizer.nextToken();
					int index = avpair.indexOf('=');
					if(index != -1){
						String key = avpair.substring(0, index).trim();
						String value = avpair.substring(index+1).trim();
						
						int subAttrId = ThreeGPP2PrepaidConstants.getId(key);
						
						if(subAttrId != ThreeGPP2PrepaidConstants.NONE.id && value.length() > 0){
							IRadiusAttribute subAttr = Dictionary.getInstance().getAttribute(RadiusConstants.VENDOR_3GPP2_ID, Constants3GPP2.PPAQ, subAttrId);
							subAttr.setStringValue(value);
							ppaqAttr.addTLVAttribute(subAttr);
						}
					}
				}				
				if(ppaqAttr.getAttributes()!= null && ppaqAttr.getAttributes().size() > 0){
					((RadAuthResponse)response).addAttribute(ppaqAttr);					
				}
			}
		}
		
	}
	
	
}
