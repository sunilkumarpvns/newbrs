package com.elitecore.test.dependecy.diameter.packet.avps.basic;

import java.nio.ByteBuffer;




import com.elitecore.test.dependecy.diameter.DiameterDictionary;
import com.elitecore.test.dependecy.diameter.packet.avps.BaseDiameterAVP;

public class AvpFloat32 extends BaseDiameterAVP{
	
	public AvpFloat32(int intAVPCode,int intVendorId ,byte bAVPFlag,String strAvpId,String strAVPEncryption) {
		super(intAVPCode,intVendorId ,bAVPFlag,strAvpId,strAVPEncryption);
	}
	public void setFloat(double iData) {
		byte[] valueBuffer;
		ByteBuffer bData;
		float fData = (float)iData;
		
		bData = ByteBuffer.allocate(4);
		bData.putFloat(fData);
		valueBuffer = bData.array();
		this.setValueBytes(valueBuffer);
	}
	
	public double getFloat() {
		byte []valueBuffer;
		float data;
		ByteBuffer bData;
		
		bData = ByteBuffer.allocate(4);
		valueBuffer = this.getValueBytes();
		
		if(valueBuffer.length>4)
			bData.put(valueBuffer, 0, 4);
		else {
			bData.put(valueBuffer);
		}
		
		data = bData.getFloat(0);

		return data;
	}
	
	public String getStringValue() {
		return String.valueOf(getFloat());
	}
	
	public void setStringValue(String data){
		setFloat(Double.parseDouble(data));
	}
	
	@Override
	public void doPlus(String value) {
		if (value != null) {
			try {
                setFloat(Float.parseFloat(value) + getFloat());
            }catch(NumberFormatException numberFormatExp) {
            	float floatValue = DiameterDictionary.getInstance().getKeyFromValue(getAVPId(),value);
	            if (floatValue >= 0) {
	                setFloat(getFloat() + floatValue);
	            }else {
	            	throw new IllegalArgumentException("Cannot convert " + value + " to FloatAVP32.",numberFormatExp);
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
				double dValue = 0;
				try{
					dValue = Double.parseDouble(strValue);
				}catch(NumberFormatException nfe){
					return false;
				}
				double dAttributeValue = getFloat();
				return dValue==dAttributeValue;
			}
			return false;
		}catch(ClassCastException cce){
			return false;
		}
	}
	*/
	
}