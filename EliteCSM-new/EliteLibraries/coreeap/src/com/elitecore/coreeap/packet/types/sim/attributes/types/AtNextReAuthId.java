package com.elitecore.coreeap.packet.types.sim.attributes.types;

import com.elitecore.coreeap.packet.types.sim.attributes.BaseSIMAttribute;
import com.elitecore.coreeap.util.constants.sim.attribute.SIMAttributeTypeConstants;
import com.elitecore.coreeap.util.tls.TLSUtility;

public class AtNextReAuthId extends BaseSIMAttribute {

	public AtNextReAuthId() {
		// TODO Auto-generated constructor stub
		super(SIMAttributeTypeConstants.AT_NEXT_REAUTH_ID.Id);
	}
	public int getActualReAuthIdentityLength(){
		byte[] buffer = getReservedBytes();
		int length = buffer[0] & 0xFF;
		length = length << 8;
		length = length | buffer[1]  & 0xFF;
		return length;		
	}
	
	public byte[] getNextFastReAuthUsername(){
		int reAuthIdentityLength = getActualReAuthIdentityLength();
		byte[] buffer = getValueBytes();
		byte[] fastReAuthUsernameBytes = new byte[reAuthIdentityLength];
		System.arraycopy(buffer, 0, fastReAuthUsernameBytes, 0, reAuthIdentityLength);
		return fastReAuthUsernameBytes;
	}

	public void setNextReauthID(String reauthId){
		if (reauthId != null) {
			byte[] reauthIdentityBytes = reauthId.getBytes();
			setActualFastReauthLength(reauthIdentityBytes.length);
			setValueBytes(reauthIdentityBytes);
		}
	}
	
	public void setActualFastReauthLength(int length ){
		byte[] lengthBytes = new byte[2];
		lengthBytes[1] = (byte) (length & 0xFF);
		length = length >>> 8;
		lengthBytes[0] = (byte) (length & 0xFF);
		setReservedBytes(lengthBytes);
	}
	
	@Override
	public String getStringValue() {
		StringBuilder returnString = new StringBuilder();
		returnString.append("[ Actual Re-Auth Identity Length = " + getActualReAuthIdentityLength() + ", Next Fast Re-authentication Username = " + TLSUtility.bytesToHex(getNextFastReAuthUsername()) + "]");
		return returnString.toString();
	}

}
