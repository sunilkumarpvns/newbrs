package com.elitecore.coreeap.packet.types.sim.attributes.types;

import com.elitecore.coreeap.packet.types.sim.attributes.BaseSIMAttribute;
import com.elitecore.coreeap.util.constants.sim.attribute.SIMAttributeTypeConstants;

public class AtCounter extends BaseSIMAttribute {

	public AtCounter() {
		// TODO Auto-generated constructor stub
		super(SIMAttributeTypeConstants.AT_COUNTER.Id);
	}
	
	public int getCounter(){
		byte[] buffer = getReservedBytes();
		int counter = buffer[0] & 0xFF;
		counter = counter << 8;
		counter = counter | (byte) (buffer[1] & 0xFF);
		return counter;
	}
	
	public void setCounter(int counter){
		setReservedBytes(new byte[]{(byte)(counter>>8),(byte)counter});		
	}
	
	@Override
	public String getStringValue() {
		return "[ Counter = "+ getCounter() +"]";
	}
}
