package com.elitecore.coreradius.commons.attributes.wimaxattribute;

import java.io.UnsupportedEncodingException;

import com.elitecore.coreradius.commons.attributes.AttributeId;
import com.elitecore.coreradius.commons.attributes.BaseRadiusWiMAXAttribute;
import com.elitecore.coreradius.commons.util.Dictionary;

public class UnsignedLong extends BaseRadiusWiMAXAttribute{

	private static final long serialVersionUID = 1L;

	public UnsignedLong(AttributeId attributeDetail) {
		super(attributeDetail);
	}
	public void setIntValue(int value) {
		setLongValue(value);
	}
	public int getIntValue() {
		byte [] valueByte = getValueBytes();
		int intValue = 0;
		for (int counter=0; counter<valueByte.length; counter++) {
			int tempInt = valueByte[counter] & 0xFF;
			//integerValue = (integerValue << 8) | l;
			intValue |= (tempInt << (8*((valueByte.length-1)-counter)));
		}
		
		return intValue;
	}
	
	public void setLongValue(long value) {
		byte [] valueBytes = new byte[8];
		valueBytes[7] =  (byte)(value);
		valueBytes[6] =  (byte)(value >>> 8);
		valueBytes[5] =  (byte)(value >>> 16);
		valueBytes[4] =  (byte)(value >>> 24);
		valueBytes[3] =  (byte)(value >>> 32);
		valueBytes[2] =  (byte)(value >>> 40);
		valueBytes[1] =  (byte)(value >>> 48);
		valueBytes[0] =  (byte)(value >>> 56);
		setValueBytes(valueBytes);
	}
	
	public long getLongValue(){
		byte [] valueByte = getValueBytes();
		long longValue = 0;
		for (int counter=0; counter<valueByte.length; counter++) {
			long tempInt = valueByte[counter] & 0xFF;		
			longValue |= (tempInt << (8*((valueByte.length-1)-counter)));
		}
		
		return longValue;
		
	}

	public void setStringValue(String value) {
		if (value == null) {
			setIntValue(0);
		}else {
			long longValue = Dictionary.getInstance().getKeyFromValue(getParentId(), value);
			if (longValue >= 0) {
				setLongValue(longValue);
			}else {
				try {
					setLongValue(Long.parseLong(value));
				}catch(NumberFormatException numberFormatExp) {
					throw new IllegalArgumentException("Cannot convert " + value + " to UnsignedLongrAttribute.",numberFormatExp);
				}
			}
		}
	}
	public void setStringValue(String value, String charsetName) throws UnsupportedEncodingException {
		setStringValue(value);
	}
	public String getStringValue(String charsetName) throws UnsupportedEncodingException {
		return getStringValue();
	}
	
	
	@Override
	public String getStringValue(boolean useDictionary) {
		String value = null;
		if(useDictionary){
			value =	Dictionary.getInstance().getValueFromKey(getParentId(),getLongValue());
		}
		if (value != null)
			return value;
		return String.valueOf(getLongValue());
	}

	public String getStringValue(){
		return getStringValue(true);
	}
	
	@Override
	public void doPlus(String value) {
		try{
			long lVal = Long.parseLong(value);
			setLongValue(getLongValue() + lVal);
		}catch(NumberFormatException nfe){
		}
	}
}
