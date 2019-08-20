package com.elitecore.aaa.radius.systemx.esix.udp.impl;

import java.util.ArrayList;

import com.elitecore.core.serverx.policies.ParserUtility;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
	
  
public abstract class Vendor {

  public abstract boolean isSupportedAttribute(IRadiusAttribute radiusAttribute) ;
  
  protected ArrayList<String> getVendorAndAttributeList(String[] vendorAttrArray) {
	  ArrayList<String> vendorAttrList = new ArrayList<String>();
		if(vendorAttrArray!=null) {
			for(int i=0;i<vendorAttrArray.length;i++) {
				if(vendorAttrArray[i]!=null) {
					String currentAttr = vendorAttrArray[i].trim();
					if(currentAttr!=null && currentAttr.trim().length()!=0) {
						String [] avpAndValue = ParserUtility.splitKeyAndValue(currentAttr);
						if(avpAndValue[0]!=null && avpAndValue[0].trim().length()!=0) {
							if(avpAndValue[2]!=null && avpAndValue[2].trim().length()!=0) {
								vendorAttrList.add(avpAndValue[0]);	
							} else if(avpAndValue[1]==null) {
								vendorAttrList.add(avpAndValue[0]);
}
						}
					}	
				}
			}	
		}
		return vendorAttrList;  
  }
}
