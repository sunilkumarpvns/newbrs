package com.elitecore.test.dependecy.diameter.packet.avps.derived;

import com.elitecore.test.dependecy.diameter.packet.avps.basic.AvpInteger32;

import java.util.Map;

public class AvpEnumerated extends AvpInteger32{
	
	// When entries into below map are added/removed after object creation, clone method into this class should be implemented
	private Map<Integer, String> supportedValues;
	public AvpEnumerated(int intAVPCode,int intVendorId ,byte bAVPFlag,String strAvpId,String strAVPEncryption,Map<Integer, String> supportedValues) {
		super(intAVPCode,intVendorId ,bAVPFlag,strAvpId,strAVPEncryption);
		this.supportedValues = supportedValues;
	}
	
	@Override
	public String getStringValue() {		
		return getStringValue(false);	
	}
	
	@Override
	public final String getLogValue(){
		String strVlue = supportedValues.get(new Integer((int)getInteger()));
		if(strVlue != null && strVlue.length() > 0)
			return strVlue + " (" + getInteger() + ")";
		
		return getStringValue();
	}
	
	public String getStringValue(boolean bUseDictionaryValue) {
		if(bUseDictionaryValue){
			String strVlue = supportedValues.get(new Integer((int)getInteger()));
			if(strVlue != null && strVlue.length() > 0)
				return strVlue;
		}		
		return String.valueOf(getInteger());	
	}
}

