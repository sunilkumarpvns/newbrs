package com.elitecore.coreeap.packet.types.sim.attributes.types;

import com.elitecore.commons.base.Bytes;
import com.elitecore.commons.base.Numbers;
import com.elitecore.coreeap.packet.types.sim.attributes.BaseSIMAttribute;
import com.elitecore.coreeap.util.constants.sim.attribute.SIMAttributeTypeConstants;

public class AtKdf extends BaseSIMAttribute {

	public AtKdf() {
		super(SIMAttributeTypeConstants.AT_KDF.Id);
	}
	
	public void setKdf(int kdfValue) {
		setReservedBytes(Numbers.toByteArray(kdfValue, 2));
	}
	
	public int getKeyDerivationFunction() {
		return Bytes.toInt(getReservedBytes());
	}
	
	@Override
	public String getStringValue() {
		return "[ key derivation function = " + getKeyDerivationFunction() + " ]";
	}
}
