package com.elitecore.diameterapi.diameter.common.packet.avps.basic;

import java.nio.ByteBuffer;
import java.util.Map;

import com.elitecore.diameterapi.diameter.common.packet.avps.BaseDiameterAVP;
import com.elitecore.diameterapi.diameter.common.packet.avps.MaxValueException;
import com.elitecore.diameterapi.diameter.common.packet.avps.NegativeValueException;
import com.elitecore.diameterapi.diameter.common.util.DiameterDictionary;

public class AvpUnsigned32  extends BaseDiameterAVP{
	private Map<Integer, String> supportedValues;
	public AvpUnsigned32(int intAVPCode,int intVendorId ,byte bAVPFlag,String strAvpId,String strAVPEncryption,Map<Integer, String> supportedValues) {
		super(intAVPCode,intVendorId ,bAVPFlag,strAvpId,strAVPEncryption);
		this.supportedValues =supportedValues;
	}
	public void setInteger(long lData) {
		byte[] valueBuffer;
		ByteBuffer bData;
		ByteBuffer bLongData;
		/*
		 * Changes done by SUBHASH PUNANI
		 * Added two exceptions for checking of negative value and maximum value
		 * 
		 */
		if(lData <0)
			throw new NegativeValueException("Negative Value Found in " + getClass().getName());
		if(lData>4294967295l)
			throw new MaxValueException("Value exceeds maximum limit in " + getClass().getName());
		
		bLongData = ByteBuffer.allocate(8);
		bLongData.putLong(lData);
		bData = ByteBuffer.allocate(4);
		bLongData.position(4);
		for(int i=0;i<4;i++){
			bData.put(bLongData.get());
		}
		valueBuffer = bData.array();
		this.setValueBytes(valueBuffer);
	}
	public long getInteger() {
		byte []valueBuffer;
		long data;
		ByteBuffer bData;
		
		bData = ByteBuffer.allocate(8);
		valueBuffer = this.getValueBytes();
		bData.position(4);
		if(valueBuffer.length>4)
			bData.put(valueBuffer, 0, 4);
		else {
			bData.put(valueBuffer);
		}
		data = bData.getLong(0);
		return data;
	}

	@Override
	public String getStringValue() {		
		return getStringValue(false);	
	}
	
	public String getStringValue(boolean bUseDictionaryValue) {
		if(bUseDictionaryValue){
			String strVlue = supportedValues.get(new Integer((int)getInteger()));
			if(strVlue != null && strVlue.length() > 0)
				return strVlue; 
		}		
		return String.valueOf(getInteger());	
	}
	
	@Override
	public final String getLogValue(){
		String strVlue = supportedValues.get(new Integer((int)getInteger()));
		if(strVlue != null && strVlue.length() > 0)
			return strVlue  + " (" + getInteger() + ")";
		
		return getStringValue();
	}
	
	public void setStringValue(String data){
		if (data == null) {
			setInteger(0);
		}else {
			long longValue = DiameterDictionary.getInstance().getKeyFromValue(getAVPId(), data);
			if (longValue >= 0) {
				setInteger(longValue);
			}else {
				try {
					setInteger(Long.parseLong(data));
				}catch(NumberFormatException ex) {
					throw new IllegalArgumentException("Cannot convert " + data + " to UnsignedIntegerAttribute.",ex);
				}
			}
		}
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
	            	throw new IllegalArgumentException("Cannot convert " + value + " to AVPUnsigned32.",numberFormatExp);
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
