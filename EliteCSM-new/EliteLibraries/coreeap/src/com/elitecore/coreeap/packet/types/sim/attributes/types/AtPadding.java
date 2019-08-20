package com.elitecore.coreeap.packet.types.sim.attributes.types;

import com.elitecore.coreeap.packet.types.sim.attributes.BaseSIMAttribute;
import com.elitecore.coreeap.util.constants.sim.attribute.SIMAttributeTypeConstants;

public class AtPadding extends BaseSIMAttribute {
	public AtPadding(){
		super(SIMAttributeTypeConstants.AT_PADDING.Id);
	}
}
