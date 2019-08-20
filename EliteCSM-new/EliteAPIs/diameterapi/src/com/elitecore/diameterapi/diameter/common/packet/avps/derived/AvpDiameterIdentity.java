package com.elitecore.diameterapi.diameter.common.packet.avps.derived;

import java.io.UnsupportedEncodingException;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.diameterapi.diameter.common.packet.avps.InvalidDiameterIdentityException;
import com.elitecore.diameterapi.diameter.common.packet.avps.basic.AvpOctetString;

public class AvpDiameterIdentity extends AvpOctetString{
	private static final String MODULE = "AVP DIAMETER IDENTITY";
	
	public AvpDiameterIdentity(int intAVPCode,int intVendorId ,byte bAVPFlag,String strAvpId,String strAVPEncryption) {
		super(intAVPCode,intVendorId ,bAVPFlag,strAvpId,strAVPEncryption);
	}
	
	public void setStringValue(String data) {
		if(data == null) {
			if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "NULL Diameter Identity");
			return;
		}
		super.setStringValue(data);
	}
	
	public String getStringValue() throws InvalidDiameterIdentityException{
		String identity;
		byte []valueBuffer = null;
		valueBuffer = this.getValueBytes();
		try {
			identity=new String(valueBuffer,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			LogManager.getLogger().trace(MODULE, e);
			identity=new String(valueBuffer);
		}
		
		return identity;
	}	
	@Override
	public void doPlus(String value){
		if(value!=null)
			setStringValue(getStringValue() + value);	
    }

}
