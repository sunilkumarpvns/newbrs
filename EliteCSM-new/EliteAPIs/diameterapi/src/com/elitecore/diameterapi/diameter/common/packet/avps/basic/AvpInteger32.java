package com.elitecore.diameterapi.diameter.common.packet.avps.basic;

import java.nio.ByteBuffer;

import com.elitecore.diameterapi.diameter.common.packet.avps.BaseDiameterAVP;
import com.elitecore.diameterapi.diameter.common.util.DiameterDictionary;

public class AvpInteger32  extends BaseDiameterAVP{
	
	public AvpInteger32(int intAVPCode,int intVendorId ,byte bAVPFlag,String strAvpId,String strAVPEncryption) {
		super(intAVPCode,intVendorId ,bAVPFlag,strAvpId,strAVPEncryption);
	}
	
	public void setInteger(long lData) {
		byte[] valueBuffer;
		ByteBuffer bData;
		int iData = (int)lData;
		
		bData = ByteBuffer.allocate(4);
		bData.putInt(iData);
		valueBuffer = bData.array();
		this.setValueBytes(valueBuffer);
	}
	public long getInteger() {
		byte []valueBuffer;
		int data;
		ByteBuffer bData;
		
		bData = ByteBuffer.allocate(4);
		valueBuffer = this.getValueBytes();
		if(valueBuffer.length>4)
			bData.put(valueBuffer, 0, 4);
		else {
			bData.put(valueBuffer);
		}
		
		data = bData.getInt(0);

		return data;
	}
	
	public String getStringValue() {
		return String.valueOf(getInteger());
	}
	
	public void setStringValue(String data){
		setInteger(Long.parseLong(data));
	}
	
	
	@Override
	public void doPlus(String value) {
		if (value != null) {
			try {
                setInteger(Long.parseLong(value) + getInteger());
            }catch(NumberFormatException numberFormatExp) {
	            long longValue = DiameterDictionary.getInstance().getKeyFromValue(getAVPId(), value);
	            if (longValue >= 0) {
	                setInteger(getInteger() + longValue);
	            }else {
	            	throw new IllegalArgumentException("Cannot convert " + value + " to IntegerAVP32.",numberFormatExp);
	            }
            }
		} 
	}
	/*
	@Override
	public boolean equals(Object obj) {
		try{
			String strValue = (String)obj;
			if(strValue!=null){
				if(strValue.equals("*"))
					return true;
				long iValue = 0;
				try{
					iValue = Long.parseLong(strValue);
				}catch(NumberFormatException nfe){
					return false;
				}
				long iAttributeValue = getInteger();
				return iValue== iAttributeValue;
			}
			return false;
		}catch(ClassCastException cce){
			return false;
		}
	}
	*/
	
}
