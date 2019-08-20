package com.elitecore.aaa.radius.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.coreradius.commons.attributes.BaseRadiusAttribute;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.InvalidAttributeIdException;

public class StringAttributeParser {
	private static final String MODULE = "STRING ATTRIBUTE PARSER";
	private static String strVendorID;
	private static String strAttributeID;
	private static String strAttributeValue;
	
	public static IRadiusAttribute parseString(String strVSAAttribute){
		if(strVSAAttribute.indexOf(':') != -1){
			int iVendorIdLength	= strVSAAttribute.indexOf(':');
			strVendorID = strVSAAttribute.substring(0,iVendorIdLength);
			int iVendorAttrLength 	= strVSAAttribute.indexOf('=');
			strAttributeID = strVSAAttribute.substring((iVendorIdLength)+1,iVendorAttrLength);
			strAttributeValue= strVSAAttribute.substring(iVendorAttrLength+1,strVSAAttribute.length()).trim();
		}else{
			strVendorID = "0";
			StringTokenizer strTokenizer = new StringTokenizer(strVSAAttribute, "=");
			if(strTokenizer.hasMoreTokens()){
				strAttributeID = strTokenizer.nextToken().trim();
				strAttributeValue = strTokenizer.nextToken();
			}
			String strAttribute = Dictionary.getInstance().getAttributeID(strAttributeID);
			if(strAttribute == null){
				LogManager.getLogger().debug(MODULE, "Attribute "+strVSAAttribute+" not considered. Reason: Attribute not supported by dictionary.");
			}else{
				strAttributeID = strAttribute; 
			}
		}
		long lVendorId = -1;
		try {
			lVendorId = Long.parseLong(strVendorID);
		}catch(Exception e) {
			
		}
		
		
		if(lVendorId > 0){

			
			/* change for nested attributes as reply items*/
			String mainAttributeId = strAttributeID;
			
			ArrayList<Object> subAttributes = null;
			HashMap<Object, Object> subAttributeValueMap = null;
			String subAttributeId = null;
			String subAttributeValue = null;
			String strSubAttributeList = null;
			
			if(strAttributeValue.contains("[")){
				subAttributes = new ArrayList<Object>();
				subAttributeValueMap = new HashMap<Object, Object>();
				strSubAttributeList = strAttributeValue.substring(strAttributeValue.indexOf('[')+1, strAttributeValue.lastIndexOf(']'));
				StringTokenizer st = new StringTokenizer(strSubAttributeList, ";");
				while(st.hasMoreTokens()){
					String token = st.nextToken();
					subAttributeId = token.substring(0, token.indexOf('='));
					subAttributeValue = token.substring(token.indexOf('=')+1);
					subAttributeValueMap.put(Integer.parseInt(subAttributeId), subAttributeValue);
					subAttributes.add(subAttributeId);
				}
			}

			int mainAttributeID = Integer.parseInt(strAttributeID);
			int subAttributeID = 0;
			
			IRadiusAttribute radiusAttribute1 = Dictionary.getInstance().getAttribute(lVendorId, Integer.parseInt(mainAttributeId));
			if(subAttributes != null){
				Iterator<Object> subItr = subAttributes.iterator();
				BaseRadiusAttribute tlvAttribute = (BaseRadiusAttribute)radiusAttribute1;
				while(subItr.hasNext()){
					subAttributeID = Integer.parseInt((String)subItr.next());
					BaseRadiusAttribute radiusAttribute2 = (BaseRadiusAttribute)Dictionary.getInstance().getAttribute(lVendorId, mainAttributeID, subAttributeID);						
					String subAttrValue = (String)subAttributeValueMap.get(subAttributeID);
					radiusAttribute2.setStringValue(subAttrValue);
					tlvAttribute.addTLVAttribute(radiusAttribute2);
				}
			}else{
				radiusAttribute1.setStringValue(strAttributeValue);
				
			}
			
			return radiusAttribute1;
		}else{
			IRadiusAttribute radiusAttribute = null;
			try {
				radiusAttribute = Dictionary.getInstance().getAttribute(strAttributeID);
				radiusAttribute.setStringValue(strAttributeValue);
			} catch (InvalidAttributeIdException e) {
			}
			return radiusAttribute;
		}
		
	}
	
	public static String getVendorId(){
		return strVendorID;
	}
}
