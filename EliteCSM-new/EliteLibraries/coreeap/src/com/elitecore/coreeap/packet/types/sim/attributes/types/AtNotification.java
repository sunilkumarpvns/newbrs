package com.elitecore.coreeap.packet.types.sim.attributes.types;

import com.elitecore.coreeap.packet.types.sim.attributes.BaseSIMAttribute;
import com.elitecore.coreeap.util.constants.sim.attribute.SIMAttributeTypeConstants;

public class AtNotification extends BaseSIMAttribute {

	public AtNotification() {
		// TODO Auto-generated constructor stub
		super(SIMAttributeTypeConstants.AT_NOTIFICATION.Id);
	}
	
	public boolean isSbitSet(){
		byte[] buffer = getReservedBytes();
		int code = buffer[0] & 0xFF;
		code = code << 8;
		code = code | (buffer[1] & 0xFF);
		return (code==32768); 
	}
	public boolean isPbitSet(){
		byte[] buffer = getReservedBytes();
		int code = buffer[0] & 0xFF;
		code = code << 8;
		code = code | (buffer[1] & 0xFF);
		return (code==16384);		
	}
	public int getNotificationCode(){
		byte[] buffer = getReservedBytes();
		int code = buffer[0] & 0xFF;
		code = code << 8;
		code = code | (buffer[1] & 0xFF);
		return code;
	}
	
	@Override
	public String getStringValue() {
		return "[ S = "+isSbitSet()+", P = "+isPbitSet()+", Notification Code = " + getNotificationCode() +"]";
	}
}
