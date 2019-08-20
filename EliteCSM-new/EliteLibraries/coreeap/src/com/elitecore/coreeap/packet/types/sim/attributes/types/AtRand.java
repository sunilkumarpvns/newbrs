package com.elitecore.coreeap.packet.types.sim.attributes.types;

import com.elitecore.coreeap.packet.types.sim.attributes.BaseSIMAttribute;
import com.elitecore.coreeap.util.constants.sim.attribute.SIMAttributeTypeConstants;
import com.elitecore.coreeap.util.tls.TLSUtility;

public class AtRand extends BaseSIMAttribute {

	public AtRand() {
		// TODO Auto-generated constructor stub
		super(SIMAttributeTypeConstants.AT_RAND.Id);
	}
	
	public byte[][] getRands(){
		byte[] buffer = getValueBytes();
		
		int length = buffer.length;
		if(length%4 != 0){
			//throw exception invalid length 
		}				 
		byte[][] rands = new byte[length/16][16];
		int pos =0;
		int offset = 0;
		while(offset < length){
			System.arraycopy(buffer, offset, rands[pos], 0, 16);			
			offset = offset + 16;
			pos ++;
		}
		return rands;
	}
	
	@Override
	public String getStringValue() {
		StringBuilder returnString = new StringBuilder();
		byte[][] rands = getRands();
		returnString.append("[");
		for(int i = 0 ; i < rands.length ; i++){
			returnString.append(" Rand = " + TLSUtility.bytesToHex(rands[i]));
		}
		returnString.append("]");
		return returnString.toString();
	}
}
