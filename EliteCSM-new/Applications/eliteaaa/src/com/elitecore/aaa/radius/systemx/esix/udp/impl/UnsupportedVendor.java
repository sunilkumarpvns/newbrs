package com.elitecore.aaa.radius.systemx.esix.udp.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.elitecore.core.serverx.policies.ParserUtility;
import com.elitecore.coreradius.commons.attributes.BaseRadiusAttribute;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.util.Dictionary;

public class UnsupportedVendor extends Vendor{
	public static final char SEPERATOR = ',';
	private ArrayList<String> unsupportedVendorAttr;
	private Map<String, String> unSupportedValues;    // key = parentid   and   value = value of that id
	private String unsupportedAttrsStr;
	private String[] unsupportedAttributeArray;
	
	public UnsupportedVendor(String unsupportedAttrsStr) {
		this.unsupportedAttrsStr = unsupportedAttrsStr;
		this.unSupportedValues = new HashMap<String, String>();
	}
	
	public void init() {
		this.unsupportedAttributeArray = ParserUtility.splitString(unsupportedAttrsStr, SEPERATOR);
		this.unsupportedVendorAttr = getVendorAndAttributeList(unsupportedAttributeArray);
		if(unsupportedAttributeArray != null) {
			for(int attributeIndex=0 ; attributeIndex < unsupportedAttributeArray.length ; attributeIndex++) {
				String [] avpAndValue = ParserUtility.splitKeyAndValue(unsupportedAttributeArray[attributeIndex]);
				if(isValidAttributeWithValue(avpAndValue)) {
					unSupportedValues.put(avpAndValue[0], avpAndValue[2]);
			}
		}
	}
	}

	private boolean isValidAttributeWithValue(String[] avpAndValue) {
		if((avpAndValue[2] != null && avpAndValue[2].trim().length()!=0) 
				&& (avpAndValue[0]!=null && avpAndValue[0].trim().length()!=0)) {
			return true;
		} else { 
			return false;
	}
	}

	@Override
	public boolean isSupportedAttribute(IRadiusAttribute currentAttribute) {
		long vendorId = currentAttribute.getVendorID();
		if(currentAttribute.isVendorSpecific()) {
			if(Dictionary.getInstance().isUnknownVendor(vendorId)) {
				return true;
}
			List<IRadiusAttribute> childAttrib = (ArrayList<IRadiusAttribute>) ((BaseRadiusAttribute)currentAttribute).getAttributes();
			if(childAttrib != null) {
				for(int attributeIndex=0 ; attributeIndex < childAttrib.size() ; attributeIndex++) {
					if(unsupportedVendorAttr.contains(childAttrib.get(attributeIndex).getParentId()) || unsupportedVendorAttr.contains(String.valueOf(vendorId))) {
						if(unSupportedValues.get(childAttrib.get(attributeIndex).getParentId())!=null 
								&& !childAttrib.get(attributeIndex).getStringValue().equalsIgnoreCase(unSupportedValues.get(childAttrib.get(attributeIndex).getParentId()))) {
							return true;
						} 
						return false;
					}
					if(unsupportedVendorAttr.contains(String.valueOf(currentAttribute.getID()))) {
						return false;
					}
				}
			}
		} else {
			if(unsupportedVendorAttr.contains(currentAttribute.getParentId()) || unsupportedVendorAttr.contains(String.valueOf(vendorId))) {
				if(unSupportedValues.get(currentAttribute.getParentId())!=null && !currentAttribute.getStringValue().equalsIgnoreCase(unSupportedValues.get(currentAttribute.getParentId()))) {
					return true;
				} 
				return  false;
			}	
		}
		return true;
	}
}
