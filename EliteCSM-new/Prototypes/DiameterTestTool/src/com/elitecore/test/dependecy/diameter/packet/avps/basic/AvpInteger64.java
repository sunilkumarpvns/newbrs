package com.elitecore.test.dependecy.diameter.packet.avps.basic;

import com.elitecore.test.dependecy.diameter.DiameterDictionary;
import com.elitecore.test.dependecy.diameter.packet.avps.BaseDiameterAVP;

import java.nio.ByteBuffer;

public class AvpInteger64  extends BaseDiameterAVP{
	
	public AvpInteger64(int intAVPCode,int intVendorId ,byte bAVPFlag,String strAvpId,String strAVPEncryption) {
		super(intAVPCode,intVendorId ,bAVPFlag,strAvpId,strAVPEncryption);
	}
	
	public void setInteger(long iData) {
		byte[] valueBuffer;
		ByteBuffer bData;
		
		bData = ByteBuffer.allocate(8);
		bData.putLong(iData);
		valueBuffer = bData.array();
		this.setValueBytes(valueBuffer);
	}
	public long getInteger() {
		byte []valueBuffer;
		long data;
		ByteBuffer bData;
		
		bData = ByteBuffer.allocate(8);
		valueBuffer = this.getValueBytes();
		if(valueBuffer.length>8)
			bData.put(valueBuffer, 0, 8);
		else {
			bData.put(valueBuffer);
		}
		/*
		 * Changes done by SUBHASH PUNANI
		 * 
		 * Changed	 :  bData.getInt(0) ==> bData.getLong()
		 * 				
		 */
		data = bData.getLong(0);
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
	            	throw new IllegalArgumentException("Cannot convert " + value + " to IntegerAVP64.",numberFormatExp);
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
