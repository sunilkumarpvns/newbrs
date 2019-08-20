package com.elitecore.aaa.radius.systemx.esix.udp.impl;

import java.util.ArrayList;
import java.util.List;

import com.elitecore.core.serverx.policies.ParserUtility;
import com.elitecore.coreradius.commons.attributes.BaseRadiusAttribute;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;

public class SupportedVendor extends Vendor{
	public static final char SEPERATOR = ',';
	private ArrayList<String> supportedVendorAttr;
	private String[] supportedAttributeArray;
	private String supportedAttrsStr;
	public SupportedVendor(String supportedAttrsStr) {
		this.supportedAttrsStr = supportedAttrsStr;
		this.supportedVendorAttr = new ArrayList<String>();
	}
	
	public void init() {
		this.supportedAttributeArray = ParserUtility.splitString(supportedAttrsStr, SEPERATOR);
		this.supportedVendorAttr = getVendorAndAttributeList(supportedAttributeArray);
		boolean isStandardExist = false;
		if(supportedAttributeArray != null) {
		for(int attributeIndex=0 ; attributeIndex < supportedAttributeArray.length ; attributeIndex++) {
			String vendorAttr = supportedAttributeArray[attributeIndex];
			String vendorId = null;
			if(vendorAttr!=null && vendorAttr.trim().length()!=0) {
				if(vendorAttr.contains(":")) {
					vendorId = getVendorId(vendorAttr);	
				} else {
					vendorId = vendorAttr;
				}
				if(vendorId!=null && String.valueOf(RadiusConstants.STANDARD_VENDOR_ID).equals(vendorId)) {
					isStandardExist = true;
					break;
				}	
			}
		}
		}
		if(!isStandardExist) {
			this.supportedVendorAttr.add(String.valueOf(RadiusConstants.STANDARD_VENDOR_ID));
		}
	}
	
	private String getVendorId(String vendorAttr) {
		String[] vendorAndAttr = vendorAttr.trim().split(":");
		if((vendorAndAttr.length==1 && !vendorAttr.contains(":") )|| vendorAndAttr.length==2) {
			return vendorAndAttr[0];
		} else {
			return null;	
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
					if(!supportedVendorAttr.contains(childAttrib.get(attributeIndex).getParentId())&& !supportedVendorAttr.contains(String.valueOf(vendorId))) {
						return false;
		}
					if(supportedVendorAttr.contains(String.valueOf(currentAttribute.getID()))) {
						return false;
	}
				}	
			}
		} else {
			if(!supportedVendorAttr.contains(currentAttribute.getParentId()) && !supportedVendorAttr.contains(String.valueOf(vendorId))) {
			return false;
			}	
		}
		return true;
	}
}
