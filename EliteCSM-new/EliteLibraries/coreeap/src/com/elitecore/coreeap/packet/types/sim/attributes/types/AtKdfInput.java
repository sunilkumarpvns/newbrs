package com.elitecore.coreeap.packet.types.sim.attributes.types;

import java.io.UnsupportedEncodingException;

import com.elitecore.commons.base.Numbers;
import com.elitecore.coreeap.commons.util.constants.CommonConstants;
import com.elitecore.coreeap.packet.types.sim.attributes.BaseSIMAttribute;
import com.elitecore.coreeap.util.constants.sim.attribute.SIMAttributeTypeConstants;


/*
 * Do not forget to set actualNetworkNameLength when this attribute is used 
 * as this will be the actual network name length. 
 */
public class AtKdfInput extends BaseSIMAttribute {
	
	public AtKdfInput() {
		super(SIMAttributeTypeConstants.AT_KDF_INPUT.Id);
	}
	
	public int getActualNetworkNameLength() {
		byte[] buffer = getReservedBytes();
		int length = buffer[0] & 0xFF;
		length = length << 8;
		length = length | buffer[1]  & 0xFF;
		return length;	
	}
	
	public void setActualNetworkNameLength(int length) {
		setReservedBytes(Numbers.toByteArray(length, 2));
	}
	
	public byte[] getNetworkName() {
		int actualNetworkNameLength = getActualNetworkNameLength();
		byte[] valueBytes = getValueBytes();
		byte[] networkName = new byte[actualNetworkNameLength];
		System.arraycopy(valueBytes, 0, networkName, 0, actualNetworkNameLength);
		return networkName;
	}
	
	@Override
	public String getStringValue() {
		try {
			return new String(getValueBytes(),CommonConstants.UTF8);
		} catch (UnsupportedEncodingException e) {
			return new String(getValueBytes());
		}
	}
}
