package com.elitecore.coreeap.packet.types.sim.attributes.types;

import com.elitecore.coreeap.packet.types.sim.attributes.BaseSIMAttribute;
import com.elitecore.coreeap.util.constants.sim.attribute.SIMAttributeTypeConstants;

public class AtCounterTooSmall extends BaseSIMAttribute {

	public AtCounterTooSmall() {
	// TODO Auto-generated constructor stub
		super(SIMAttributeTypeConstants.AT_COUNTER_TOO_SMALL.Id);
	}
	
	@Override
	public String getStringValue() {
		return "";
	}
}
