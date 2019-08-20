package com.elitecore.coreeap.packet.types.sim.attributes.types;

import com.elitecore.coreeap.packet.types.sim.attributes.BaseSIMAttribute;
import com.elitecore.coreeap.util.constants.sim.attribute.SIMAttributeTypeConstants;
import com.elitecore.coreeap.util.tls.TLSUtility;

public class AtIV extends BaseSIMAttribute {

	public AtIV() {
	// TODO Auto-generated constructor stub
		super(SIMAttributeTypeConstants.AT_IV.Id);
	}
	
	public byte[] getInitializationVector(){
		return getValueBytes();		
	}
	
	@Override
	public String getStringValue() {
		return "[ Initialization Vector = " + TLSUtility.bytesToHex(getInitializationVector()) + "]";
	}
}
