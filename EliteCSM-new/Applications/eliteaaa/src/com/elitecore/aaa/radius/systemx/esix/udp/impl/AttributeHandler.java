package com.elitecore.aaa.radius.systemx.esix.udp.impl;

import java.util.ArrayList;

import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.packet.RadiusPacket;

public abstract class AttributeHandler {
	protected ArrayList<String> supportedVendorAttr;
	public AttributeHandler(ArrayList<String> supportedVendorAttr) {
		this.supportedVendorAttr = supportedVendorAttr;
	}

	public abstract ArrayList<IRadiusAttribute> getAttributes(RadiusPacket radiusPacket);
}
