package com.elitecore.coreeap.packet.types.sim.attributes.types;

import com.elitecore.coreeap.packet.types.sim.attributes.BaseSIMAttribute;
import com.elitecore.coreeap.util.constants.sim.attribute.SIMAttributeTypeConstants;
import com.elitecore.coreeap.util.tls.TLSUtility;

public class AtCheckCode extends BaseSIMAttribute {

	public AtCheckCode() {
		super(SIMAttributeTypeConstants.AT_CHECKCODE.Id);
	}

	public byte[] getCheckCode(){
		return getValueBytes();
	}
	
	@Override
	public String getStringValue() {
		return "[ CHECKCODE = "+ TLSUtility.bytesToHex(getCheckCode()) +"]";
	}

}
