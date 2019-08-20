package com.elitecore.coreeap.packet.types.sim.attributes.types;

import com.elitecore.coreeap.packet.types.sim.attributes.BaseSIMAttribute;
import com.elitecore.coreeap.util.constants.sim.attribute.SIMAttributeTypeConstants;
import com.elitecore.coreeap.util.tls.TLSUtility;

public class AtRes extends BaseSIMAttribute {

	public AtRes() {
		super(SIMAttributeTypeConstants.AT_RES.Id);
	}

	public int getActualResLength(){
		byte[] buffer = getReservedBytes();
		int length = buffer[0] & 0xFF;
		length = length << 8;
		length = length | buffer[1]  & 0xFF;
		return (length/8);		//converting into bytes
	}
	
	public byte[] getRes(){
		int resLength = getActualResLength();
		byte[] buffer = getValueBytes();
		byte[] resBytes = new byte[resLength];
		System.arraycopy(buffer, 0, resBytes, 0, resLength);
		return resBytes;
	}

	@Override
	public String getStringValue() {
		return "[ Actual RES Length = "+ getActualResLength() +", RES = "+ TLSUtility.bytesToHex(getRes()) +"]";
	}

}
