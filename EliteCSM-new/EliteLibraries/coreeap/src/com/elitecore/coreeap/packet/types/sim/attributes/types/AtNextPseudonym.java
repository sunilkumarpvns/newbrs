package com.elitecore.coreeap.packet.types.sim.attributes.types;

import com.elitecore.coreeap.packet.types.sim.attributes.BaseSIMAttribute;
import com.elitecore.coreeap.util.constants.sim.attribute.SIMAttributeTypeConstants;
import com.elitecore.coreeap.util.tls.TLSUtility;

public class AtNextPseudonym extends BaseSIMAttribute {

	public AtNextPseudonym() {
		// TODO Auto-generated constructor stub
		super(SIMAttributeTypeConstants.AT_NEXT_PSEUDONYM.Id);
	}
	
	public int getActualPseudonymLength(){
		byte[] buffer = getReservedBytes();
		int length = buffer[0] & 0xFF;
		length = length << 8;
		length = length | buffer[1]  & 0xFF;
		return length;		
	}
	
	public void setActualPseudonymLength(int length ){
		byte[] lengthBytes = new byte[2];
		lengthBytes[1] = (byte) (length & 0xFF);
		length = length >>> 8;
		lengthBytes[0] = (byte) (length & 0xFF);
		setReservedBytes(lengthBytes);
	}
	
	public byte[] getNextPseudonym(){
		int pseudonymLength = getActualPseudonymLength();
		byte[] buffer = getValueBytes();
		byte[] pseudonymBytes = new byte[pseudonymLength];
		System.arraycopy(buffer, 0, pseudonymBytes, 0, pseudonymLength);
		return pseudonymBytes;
	}

	public void setNextPseudonym(String pseudonym){
		if (pseudonym != null) {
			byte[] pseudonymIdentityBytes = pseudonym.getBytes();
			setActualPseudonymLength(pseudonymIdentityBytes.length);
			setValueBytes(pseudonymIdentityBytes);
		}
	}

	@Override
	public String getStringValue() {
		StringBuilder returnString = new StringBuilder();
		returnString.append("[ Actual Pseudonym Length = " + getActualPseudonymLength() + ", Next Pseudonym = " + TLSUtility.bytesToHex(getNextPseudonym()));
		return returnString.toString();
	}
}
