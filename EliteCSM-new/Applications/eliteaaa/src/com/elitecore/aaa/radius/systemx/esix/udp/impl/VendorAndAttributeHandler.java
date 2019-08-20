package com.elitecore.aaa.radius.systemx.esix.udp.impl;

import java.util.ArrayList;

import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.packet.RadiusPacket;

public class VendorAndAttributeHandler extends AttributeHandler {

	private String attribute;
	
	public VendorAndAttributeHandler(String currentAttr, ArrayList<String> supportedVendorAttr) {
		super(supportedVendorAttr);
		this.attribute = currentAttr;
	}

	@Override
	public ArrayList<IRadiusAttribute> getAttributes(RadiusPacket radiusPacket) {
		return (ArrayList<IRadiusAttribute>)radiusPacket.getRadiusAttributes(attribute);
	}
}
