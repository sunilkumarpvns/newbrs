package com.elitecore.test.dependecy.diameter.packet.avps.derived;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.test.dependecy.diameter.CommonConstants;
import com.elitecore.test.dependecy.diameter.packet.avps.basic.AvpOctetString;

import java.io.UnsupportedEncodingException;

public class AvpUTF8String extends AvpOctetString {
	public static final String MODULE = "UTF8 String";

	public AvpUTF8String(int intAVPCode,int intVendorId ,byte bAVPFlag,String strAvpId,String strAVPEncryption) {
		super(intAVPCode,intVendorId ,bAVPFlag,strAvpId,strAVPEncryption);
	}
	
	public void setStringValue(String data)  {
		byte []valueBuffer = null;

		if(data!=null){
			try{
				valueBuffer = data.getBytes(CommonConstants.UTF8);
			}catch(UnsupportedEncodingException ueExce){
				LogManager.getLogger().trace(MODULE, ueExce);
			}
			this.setValueBytes(valueBuffer);
		}
	}
	public String getStringValue(){
		byte []valueBuffer = null;
		valueBuffer = this.getValueBytes();
		if(valueBuffer != null) {
			try {
				return new String(valueBuffer,"UTF-8");
			}catch(UnsupportedEncodingException ueExce) {
				LogManager.getLogger().trace(MODULE, ueExce);
			}
		}
		return null;
	}
	@Override
	public void doPlus(String value){
		if(value!=null)
			setStringValue(getStringValue() + value);
	}

}
