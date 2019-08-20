package com.elitecore.coreeap.packet.types.sim.attributes.types;

import com.elitecore.coreeap.packet.types.sim.attributes.BaseSIMAttribute;
import com.elitecore.coreeap.util.constants.sim.attribute.SIMAttributeTypeConstants;
import com.elitecore.coreeap.util.tls.TLSUtility;

public class AtAutn extends BaseSIMAttribute {

	public AtAutn() {
		super(SIMAttributeTypeConstants.AT_AUTN.Id);
	}

	public byte[] getAutn(){
		return getValueBytes();
	}

	@Override
	public String getStringValue() {
		return "[ AUTN = "+ TLSUtility.bytesToHex(getAutn()) +"]";
	}

}
