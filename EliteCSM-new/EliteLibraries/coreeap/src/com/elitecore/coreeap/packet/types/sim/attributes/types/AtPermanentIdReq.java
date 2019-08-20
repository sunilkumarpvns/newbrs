package com.elitecore.coreeap.packet.types.sim.attributes.types;

import com.elitecore.coreeap.packet.types.sim.attributes.BaseSIMAttribute;
import com.elitecore.coreeap.util.constants.sim.attribute.SIMAttributeTypeConstants;

public class AtPermanentIdReq extends BaseSIMAttribute {
	public AtPermanentIdReq(){
		super(SIMAttributeTypeConstants.AT_PERMANENT_ID_REQ.Id);
	}
	@Override
	public String getStringValue() {
		return "";
	}
}
