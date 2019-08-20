package com.elitecore.coreeap.packet.types.sim.attributes.types;

import com.elitecore.coreeap.packet.types.sim.attributes.BaseSIMAttribute;
import com.elitecore.coreeap.util.constants.sim.attribute.ClientErrorCodeValueConstants;
import com.elitecore.coreeap.util.constants.sim.attribute.SIMAttributeTypeConstants;

public class AtClientErrorCode extends BaseSIMAttribute {

	public AtClientErrorCode() {
		// TODO Auto-generated constructor stub
		super(SIMAttributeTypeConstants.AT_CLIENT_ERROR_CODE.Id);
	}
	
	public int getClientErrorCode(){
		byte[] buffer = getReservedBytes();
		int code = buffer[0] & 0xFF;
		code = code << 8;
		code = code | (buffer[1] & 0xFF);
		return code;
	}
	
	@Override
	public String getStringValue() {
		return ClientErrorCodeValueConstants.getmessage(getClientErrorCode());
	}
}
