package com.elitecore.aaa.radius.translators.copypacket;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.coreradius.commons.packet.RadiusPacket;
import com.elitecore.diameterapi.diameter.translator.operations.data.HeaderFields;

public enum RadiusHeaderFields implements HeaderFields<RadiusPacket> {

	PACKET_TYPE("PacketType") {
		
		@Override
		public boolean apply(RadiusPacket packet, int value) {
			if (value < 0) {
				if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
					LogManager.getLogger().debug(MODULE, "Not Updating Header-Field: " + this.name + 
							", Reason: Parsed Negative Value: " + value +  
							" for provided value: " + value);
				}
				return false;
			} 
			
			if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
				LogManager.getLogger().debug(MODULE, "Updating Header-Field: " + this.name + 
						" with Value: " + value);
			}
			packet.setPacketType(value);
			return true;
		}
	};
	
	public static final String MODULE = "RAD-PKT-HDR-FIELDS";
	public final String name;
	
	private RadiusHeaderFields(String name) {
		this.name = name;
	}
	
	@Override
	public String key() {
		return name;
	}

	public static RadiusHeaderFields getHeaderField(String name) {
		if (PACKET_TYPE.name.equalsIgnoreCase(name)) {
			return PACKET_TYPE;
		}
		return null;
	}
	
	@Override
	public final boolean apply(RadiusPacket packet, String value) throws NumberFormatException {
		return apply(packet, Integer.parseInt(value));
	}

	protected abstract boolean apply(RadiusPacket packet, int value);
	
}
