package com.elitecore.coreeap.packet.types.sim.attributes.types;

import com.elitecore.coreeap.packet.types.sim.attributes.BaseSIMAttribute;
import com.elitecore.coreeap.util.constants.sim.attribute.SIMAttributeTypeConstants;
import com.elitecore.coreeap.util.tls.TLSUtility;

public class AtNonceS extends BaseSIMAttribute {

	public AtNonceS() {
		// TODO Auto-generated constructor stub
		super(SIMAttributeTypeConstants.AT_NONCE_S.Id);
	}
	
	public byte[] getNonceS(){
		return getValueBytes();
	}
	
	@Override
	public String getStringValue() {
		return " [ NONCE_S  = " +  TLSUtility.bytesToHex(getNonceS())+ "]";
	}
}
