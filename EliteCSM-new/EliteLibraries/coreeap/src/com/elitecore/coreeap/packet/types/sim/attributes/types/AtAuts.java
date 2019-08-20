package com.elitecore.coreeap.packet.types.sim.attributes.types;

import com.elitecore.coreeap.packet.types.sim.attributes.BaseSIMAttribute;
import com.elitecore.coreeap.util.constants.sim.attribute.SIMAttributeTypeConstants;
import com.elitecore.coreeap.util.tls.TLSUtility;

public class AtAuts extends BaseSIMAttribute {

	public AtAuts() {
		super(SIMAttributeTypeConstants.AT_AUTS.Id);
	}

	public byte[] getAuts(){
		return TLSUtility.appendBytes(getReservedBytes(), getValueBytes());
	}

	@Override
	public String getStringValue() {
		return "[ MAC = "+ TLSUtility.bytesToHex(getAuts()) +"]";
	}

}
