package com.elitecore.aaa.radius.systemx.esix.udp.impl;

import javax.annotation.Nonnull;

import com.elitecore.aaa.diameter.commons.AAATranslatorConstants;
import com.elitecore.aaa.radius.systemx.esix.udp.RadUDPRequest;
import com.elitecore.core.commons.data.ValueProvider;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;

public class RadiusSessionDataTranslatedValueProvider implements ValueProvider {

	private RadUDPRequest request;

	public RadiusSessionDataTranslatedValueProvider(@Nonnull RadUDPRequest request) {
		this.request = request;
	}

	@Override
	public String getStringValue(String identifier) {
		IRadiusAttribute attribute = null;
		if (AAATranslatorConstants.PACKET_TYPE.equalsIgnoreCase(identifier)) {
			return String.valueOf(request.getRadiusPacket().getPacketType());
		} else if(identifier.startsWith("$REQ:")) {
			attribute = request.getRadiusPacket().getRadiusAttribute(identifier.substring(5));
		} else {
			attribute = request.getRadiusPacket().getRadiusAttribute(identifier);
		}
		if(attribute != null) {
			return attribute.getStringValue();
		}
		return null;
	}

}
