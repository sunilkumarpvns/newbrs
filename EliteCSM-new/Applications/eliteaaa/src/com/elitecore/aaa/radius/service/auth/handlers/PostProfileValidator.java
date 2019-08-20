package com.elitecore.aaa.radius.service.auth.handlers;

import java.util.ArrayList;
import java.util.List;

import com.elitecore.aaa.core.data.AttributeDetails;
import com.elitecore.aaa.radius.conf.impl.RadBWListConfigurable;
import com.elitecore.aaa.radius.service.auth.RadAuthRequest;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;

public class PostProfileValidator extends BlackListValidator{
	private static final String MODULE = "Profile_Black_Handler";

	@Override
	public void init(List<AttributeDetails> configuredAttrList) {
		
		List<AttributeDetails> tempWhiteList = new ArrayList<AttributeDetails>();
		List<AttributeDetails> tempBlackList = new ArrayList<AttributeDetails>();
		String attrId = "";
		
		int numOfConfiguredAttributes = configuredAttrList.size();
		
		for(int i=0;i<numOfConfiguredAttributes;i++) {
			AttributeDetails attributeDetails = configuredAttrList.get(i);
			attrId = attributeDetails.getId();
			
			if(attrId.startsWith("21067:") == false && 
					attrId.equalsIgnoreCase(RadiusConstants.STANDARD_VENDOR_ID+":"+RadiusAttributeConstants.CUI) == false){
				continue;
			}
			if(RadBWListConfigurable.WHITE.equalsIgnoreCase(attributeDetails.getType())){
				tempWhiteList.add(attributeDetails);
			}else {
				tempBlackList.add(attributeDetails);
			}
		}
		this.blackAttributeList = tempBlackList;
		this.whiteAttributeList = tempWhiteList;
	
	}

	@Override
	public String getModule() {
		return MODULE;
	}
	
	@Override
	public boolean isBlockedUser(RadAuthRequest request){
		return super.isBlockedUser(request);
	}

}