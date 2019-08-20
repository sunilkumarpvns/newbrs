package com.elitecore.coreeap.packet.types.sim.attributes.types;

import com.elitecore.coreeap.packet.types.sim.attributes.BaseSIMAttribute;
import com.elitecore.coreeap.util.constants.sim.attribute.SIMAttributeTypeConstants;

public class AtResultInd extends BaseSIMAttribute {

	public AtResultInd() {
		// TODO Auto-generated constructor stub
		super(SIMAttributeTypeConstants.AT_RESULT_IND.Id);
	}
	
	@Override
	public String getStringValue() {
		return "";
	}
}
