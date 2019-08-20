package com.elitecore.coreradius.commons.packet;

import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.InvalidAttributeIdException;

/**
 * @author narendra.pathai
 */
public class RadiusPacketBuilder {
    private RadiusPacket radiusPacket = new RadiusPacket();

    public RadiusPacketBuilder addAttribute(int attributeId, Object value) throws InvalidAttributeIdException {
        IRadiusAttribute attribute = Dictionary.getInstance().getAttribute(attributeId);
        attribute.setStringValue(String.valueOf(value));
        radiusPacket.addAttribute(attribute);
        return this;
    }

    public RadiusPacket build() {
        radiusPacket.refreshPacketHeader();
        radiusPacket.refreshInfoPacketHeader();
        return radiusPacket;
    }

    public RadiusPacketBuilder packetType(int packetType) {
		radiusPacket.setPacketType(packetType);
		return this;
	}
	
	public RadiusPacketBuilder addAttribute(IRadiusAttribute attribute) {
		radiusPacket.addAttribute(attribute);
		return this;
	}
}
