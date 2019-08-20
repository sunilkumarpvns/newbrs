package com.elitecore.coreeap.packet.types.sim.attributes.types;

import java.io.UnsupportedEncodingException;

import com.elitecore.coreeap.commons.util.constants.CommonConstants;
import com.elitecore.coreeap.packet.types.sim.attributes.BaseSIMAttribute;
import com.elitecore.coreeap.util.constants.sim.attribute.SIMAttributeTypeConstants;

public class AtIdentity extends BaseSIMAttribute {

	public AtIdentity() {
		super(SIMAttributeTypeConstants.AT_IDENTITY.Id);
	}
	
	public int getActualIdentityLength(){
		byte[] buffer = getReservedBytes();
		int length = buffer[0] & 0xFF;
		length = length << 8;
		length = length | buffer[1]  & 0xFF;
		return length;		
	}
	
	public byte[] getIdentity(){
		int identityLength = getActualIdentityLength();
		byte[] buffer = getValueBytes();
		byte[] identityBytes = new byte[identityLength];
		System.arraycopy(buffer, 0, identityBytes, 0, identityLength);
		return identityBytes;
	}
	
	public String getStringValue() {
		try {
			return new String(getValueBytes(),CommonConstants.UTF8);
		} catch (UnsupportedEncodingException e) {
			return new String(getValueBytes());
		}
	}

}
