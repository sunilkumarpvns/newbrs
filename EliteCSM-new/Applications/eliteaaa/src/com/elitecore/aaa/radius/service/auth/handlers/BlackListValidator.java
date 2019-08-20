package com.elitecore.aaa.radius.service.auth.handlers;

import java.util.ArrayList;
import java.util.List;

import com.elitecore.aaa.core.data.AttributeDetails;
import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.radius.service.auth.RadAuthRequest;
import com.elitecore.aaa.util.constants.AAAServerConstants;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;

public abstract class BlackListValidator{
	
	protected List<AttributeDetails> blackAttributeList = new ArrayList<AttributeDetails>();
	protected List<AttributeDetails> whiteAttributeList = new ArrayList<AttributeDetails>();
	
	
	public boolean isBlockedUser(RadAuthRequest request){
		
		boolean isBlockedUser = false;
		
		if(blackAttributeList.isEmpty() && whiteAttributeList.isEmpty())
			return isBlockedUser;
		
		if(blackAttributeList.size()>0){
			if(isBlackUser(request)){
				String identity = (String)request.getParameter(AAAServerConstants.CUI_KEY);
				if(identity==null){
					identity="";
				}
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(getModule(), "subscriber: "+identity+" is eligible for BlackList, checking for WhiteList");
				isBlockedUser = !isWhiteUser(request); 
				if(!isBlockedUser){
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
						LogManager.getLogger().debug(getModule(), "subscriber: "+identity+" is eligible for WhiteList");
					}
				}else {
					if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
						LogManager.getLogger().debug(getModule(), "subscriber: "+identity+" is not eligible for WhiteList");
					}
				}
				return isBlockedUser;
			}
		}else {
			return !isWhiteUser(request);
		}
		return isBlockedUser;
	}
	
	private boolean isBlackUser(RadServiceRequest request) {
		return compareAttributes(request,blackAttributeList);
	}
	private boolean isWhiteUser(RadServiceRequest request) {
		return compareAttributes(request,whiteAttributeList);
	}
	private boolean compareAttributes(RadServiceRequest request,List<AttributeDetails> blackOrWhiteAttributes) {
		int numOfBlackOrWhiteAttributes = blackOrWhiteAttributes.size();
		for(int i=0;i<numOfBlackOrWhiteAttributes;i++){
			AttributeDetails attributeDetails = blackOrWhiteAttributes.get(i);
			if(isValidAttributeDetail(attributeDetails)){
				List<IRadiusAttribute> radiusAttributes = (ArrayList<IRadiusAttribute>)request.getRadiusAttributes(attributeDetails.getId(),true);
				if(radiusAttributes==null){
					continue;
				}else{
					int numOfAttributes = radiusAttributes.size();
					for(int j=0;j<numOfAttributes;j++){
						IRadiusAttribute compareAttribute = radiusAttributes.get(j);
						if(compareAttribute.patternCompare(attributeDetails.getValue())){
							return true;
						}
					}
				}
			}
		}
		return false;

	}
	
	private boolean isValidAttributeDetail(AttributeDetails attributeDetails) {
		if(!attributeDetails.isValid()){
			return false;
		}else {
			
			if (System.currentTimeMillis()<=attributeDetails.getValidity().getTime()) {
				return true;
			}else {
				attributeDetails.setValid(false);
				return false;
			}
		}
	}
	
	public abstract void init(List<AttributeDetails> allConfiguredAttributes);
	
	public abstract String getModule();
	
	
}
