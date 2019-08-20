package com.elitecore.diameterapi.diameter.common.packet.avps.basic;

import java.io.UnsupportedEncodingException;

import com.elitecore.diameterapi.diameter.common.packet.avps.BaseDiameterAVP;
import com.elitecore.diameterapi.diameter.common.util.DiameterUtility;
import com.elitecore.diameterapi.diameter.common.util.constant.CommonConstants;

public class AvpOctetString extends BaseDiameterAVP {

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
