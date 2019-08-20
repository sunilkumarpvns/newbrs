package com.elitecore.test.dependecy.diameter.packet.avps.basic;

import com.elitecore.test.dependecy.diameter.CommonConstants;
import com.elitecore.test.dependecy.diameter.DiameterUtility;
import com.elitecore.test.dependecy.diameter.packet.avps.BaseDiameterAVP;

import java.io.UnsupportedEncodingException;

public class AvpOctetString extends BaseDiameterAVP {

	private static final String MODULE = "AvpOctetString";
	
	public AvpOctetString(int intAVPCode,int intVendorId ,byte bAVPFlag,String strAvpId,String strAVPEncryption) {
		super(intAVPCode,intVendorId ,bAVPFlag,strAvpId,strAVPEncryption);
	}
	
	public void setStringValue(String data){
		if(data!=null){
			if(data.startsWith("0x") || data.startsWith("0X")){
				setValueBytes(DiameterUtility.getBytesFromHexValue(data));
	        } else{
	        	try{
	        		setValueBytes(data.getBytes(CommonConstants.UTF8));
	        	}catch(UnsupportedEncodingException e){
	        		setValueBytes(data.getBytes());
	        	}
	        }
		}
	}
	
	public String getStringValue(){		
		return "0x"+DiameterUtility.bytesToHex(this.getValueBytes());
	}
	
	@Override
	public void doPlus(String value){
		if(value!=null){
			String hexValue = DiameterUtility.bytesToHex(value.getBytes());
	    	setValueBytes(DiameterUtility.appendBytes(getStringValue().getBytes(),hexValue.getBytes()));
		}	
    }
	
	public static void main(String args[]) {
		AvpOctetString avpOctetString = new AvpOctetString(1, 0, (byte)128, "1", "true");
		avpOctetString.setStringValue("0000000001");
		
		System.out.println(DiameterUtility.getBytesAsString("MODULE", avpOctetString.getValueBytes()));
	}
}
