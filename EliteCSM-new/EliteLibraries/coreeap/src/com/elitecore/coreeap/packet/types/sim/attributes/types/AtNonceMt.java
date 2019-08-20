package com.elitecore.coreeap.packet.types.sim.attributes.types;

import com.elitecore.coreeap.packet.types.sim.attributes.BaseSIMAttribute;
import com.elitecore.coreeap.util.constants.sim.attribute.SIMAttributeTypeConstants;
import com.elitecore.coreeap.util.tls.TLSUtility;

public class AtNonceMt extends BaseSIMAttribute {
	public AtNonceMt(){
		super(SIMAttributeTypeConstants.AT_NONCE_MT.Id);
	}
	public byte[] getNonceMt(){
		return getValueBytes();
	}
	@Override
	public String getStringValue() {
		return TLSUtility.bytesToHex(getNonceMt());
	}
}
