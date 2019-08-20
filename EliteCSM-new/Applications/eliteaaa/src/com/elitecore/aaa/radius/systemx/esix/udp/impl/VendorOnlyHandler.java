package com.elitecore.aaa.radius.systemx.esix.udp.impl;

import java.util.ArrayList;
import java.util.List;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.serverx.policies.ParserUtility;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.attributes.VendorSpecificAttribute;
import com.elitecore.coreradius.commons.packet.RadiusPacket;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;

public class VendorOnlyHandler extends AttributeHandler {
	
	private long vendorId;
	
	public VendorOnlyHandler(String currentAttr, ArrayList<String> supportedVendorAttr) {
		super(supportedVendorAttr);
		parse(currentAttr);
	}
	
	private void parse(String currentAttr) {
		String [] avpAndValue = ParserUtility.splitKeyAndValue(currentAttr);
		try {
			this.vendorId = Long.parseLong(avpAndValue[0]);	
		} catch(NumberFormatException e) {
			if(LogManager.getLogger().isLogLevel(LogLevel.TRACE)) {
				LogManager.getLogger().trace("Invalid VendorId", e.getLocalizedMessage());
			}
		}
	}

	@Override
	public ArrayList<IRadiusAttribute> getAttributes(RadiusPacket radiusPacket) {
		ArrayList<IRadiusAttribute> attributeList = new ArrayList<IRadiusAttribute>();
		ArrayList<IRadiusAttribute> receivedAttributes = (ArrayList<IRadiusAttribute>)radiusPacket.getRadiusAttributes(true);
		if(receivedAttributes != null) {
			for(int attributeIndex=0 ; attributeIndex < receivedAttributes.size() ; attributeIndex++) {
				if(RadiusConstants.STANDARD_VENDOR_ID == vendorId) {
					if(!receivedAttributes.get(attributeIndex).isVendorSpecific() && receivedAttributes.get(attributeIndex).getVendorID()==RadiusConstants.STANDARD_VENDOR_ID) {
						if(!supportedVendorAttr.contains(receivedAttributes.get(attributeIndex).getParentId())) {
							attributeList.add(receivedAttributes.get(attributeIndex));
						}
					}
				} else {
					if(receivedAttributes.get(attributeIndex).isVendorSpecific()) {
						List<IRadiusAttribute> childAttrib = (ArrayList<IRadiusAttribute>) ((VendorSpecificAttribute)receivedAttributes.get(attributeIndex)).getAttributes();
						if(childAttrib != null) {
							for(int childIndex=0 ; childIndex < childAttrib.size() ; childIndex++) {
								if(!supportedVendorAttr.contains(childAttrib.get(childIndex).getParentId()) && childAttrib.get(childIndex).getVendorID() == vendorId) {
									attributeList.add(childAttrib.get(childIndex));
								}
							}
						}
					}
				}
			}	
		}
		return attributeList;
	}
}
