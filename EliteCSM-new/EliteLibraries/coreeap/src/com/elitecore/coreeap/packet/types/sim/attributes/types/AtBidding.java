package com.elitecore.coreeap.packet.types.sim.attributes.types;

import com.elitecore.coreeap.packet.types.sim.attributes.BaseSIMAttribute;
import com.elitecore.coreeap.util.constants.sim.attribute.SIMAttributeTypeConstants;


public class AtBidding extends BaseSIMAttribute {
	
	public AtBidding() {
		super(SIMAttributeTypeConstants.AT_BIDDING.Id);
	}
	
	public boolean isDbitSet() {
		byte[] reservedBytes = getReservedBytes();
		return (reservedBytes[0] & 0x80) == 0 ? false : true; 
	}
	
	@Override
	public String getStringValue() {
		return "[ D - bit set = " + isDbitSet() + "]";
	}
}
