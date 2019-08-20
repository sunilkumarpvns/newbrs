package com.elitecore.aaa.radius.systemx.esix.udp.impl;

import java.util.ArrayList;

import com.elitecore.core.serverx.policies.ParserUtility;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.packet.RadiusPacket;
import com.elitecore.coreradius.commons.util.Dictionary;

public class AttributeAndValueHandler extends AttributeHandler {

	private String attribute;
	private String value;
	
	public AttributeAndValueHandler(String currentAttr, ArrayList<String> supportedVendorAttr) {
		super(supportedVendorAttr);
		parse(currentAttr);
	}
	
	private void parse(String currentAttr) {
		String [] avpAndValue = ParserUtility.splitKeyAndValue(currentAttr);
		this.attribute = avpAndValue[0];
		this.value = avpAndValue[2];	
	}

	@Override
	public ArrayList<IRadiusAttribute> getAttributes(RadiusPacket radiusPacket) {
		ArrayList<IRadiusAttribute> attributeList = new ArrayList<IRadiusAttribute>();
		ArrayList<IRadiusAttribute> receivedAttributes = (ArrayList<IRadiusAttribute>)radiusPacket.getRadiusAttributes(attribute);
		if(receivedAttributes!=null && receivedAttributes.size()>0) {
			attributeList = receivedAttributes;
		} else {
			IRadiusAttribute radiusAttribute = (IRadiusAttribute)Dictionary.getInstance().getKnownAttribute(attribute);
			radiusAttribute.setStringValue(value);
			attributeList.add(radiusAttribute);
		}
		return attributeList;
	}

}
