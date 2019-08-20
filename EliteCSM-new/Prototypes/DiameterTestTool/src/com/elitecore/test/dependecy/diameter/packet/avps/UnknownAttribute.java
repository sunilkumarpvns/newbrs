package com.elitecore.test.dependecy.diameter.packet.avps;

import com.elitecore.test.dependecy.diameter.DiameterUtility;

import java.io.UnsupportedEncodingException;

public class UnknownAttribute extends BaseDiameterAVP {
	
	public UnknownAttribute(){
		
	}
	
	public UnknownAttribute(int intAVPCode,int intVendorId ,byte bAVPFlag,String strAvpId,String strAVPEncryption) {
		super(intAVPCode,intVendorId ,bAVPFlag,strAvpId,strAVPEncryption);
	}
	
	public void setStringValue(String value) {
		setValueBytes(value.getBytes());
	}

	public void setStringValue(String value, String charsetName) throws UnsupportedEncodingException {
		// TODO Auto-generated method stub
	}
	
	public String getValueString() {
		byte data[] = getValueBytes();
		String strData = new String("0x");
		for(int cnt=2;cnt<data.length;cnt++) {
			strData = strData + Integer.toHexString(data[cnt]);
		}
		return strData;
	}
	
	public String getStringValue() {
		return "0x"+ DiameterUtility.bytesToHex(getBytes());
	}

	public String getStringValue(String charsetName) throws UnsupportedEncodingException {
		// TODO Auto-generated method stub
		return null;
	}
	
	/*public String toString() {
		return "0x "  + getStringValue();
	}*/
}