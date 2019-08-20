package com.elitecore.coreeap.packet.types.sim.attributes.types;

import com.elitecore.coreeap.packet.types.sim.attributes.BaseSIMAttribute;
import com.elitecore.coreeap.util.constants.sim.attribute.SIMAttributeTypeConstants;
import com.elitecore.coreeap.util.tls.TLSUtility;

public class AtMac extends BaseSIMAttribute {

	public AtMac() {
		// TODO Auto-generated constructor stub
		super(SIMAttributeTypeConstants.AT_MAC.Id);
	}
	
	public byte[] getMAC(){
		return getValueBytes();
	}
	
	@Override
	public String getStringValue() {
		return "[ MAC = "+ TLSUtility.bytesToHex(getMAC()) +"]";
	}
}
