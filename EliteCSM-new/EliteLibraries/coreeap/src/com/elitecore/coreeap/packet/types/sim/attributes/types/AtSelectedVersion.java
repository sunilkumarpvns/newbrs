package com.elitecore.coreeap.packet.types.sim.attributes.types;

import com.elitecore.coreeap.packet.types.sim.attributes.BaseSIMAttribute;
import com.elitecore.coreeap.util.constants.sim.attribute.SIMAttributeTypeConstants;

public class AtSelectedVersion extends BaseSIMAttribute {

	public AtSelectedVersion() {
		// TODO Auto-generated constructor stub
		super(SIMAttributeTypeConstants.AT_SELECTED_VERSION.Id);
	}
	
	public int getSelectedVersion(){
		byte[] buffer = getReservedBytes();
		int version = buffer[0] & 0xFF;
		version = version << 8;
		version = version | (byte)(buffer[1]& 0xFF);
		return version;
	}
	
	public String getStringValue() {		
		return String.valueOf(getSelectedVersion());		
	}
}
