package com.elitecore.coreeap.packet.types.sim.attributes.types;

import com.elitecore.coreeap.packet.types.sim.attributes.BaseSIMAttribute;
import com.elitecore.coreeap.util.constants.sim.attribute.SIMAttributeTypeConstants;

public class AtAnyIdReq extends BaseSIMAttribute {

	public AtAnyIdReq() {
		// TODO Auto-generated constructor stub
		super(SIMAttributeTypeConstants.AT_ANY_ID_REQ.Id);
	}
	
	@Override
	public String getStringValue() {
		return "";
	}
	
}
