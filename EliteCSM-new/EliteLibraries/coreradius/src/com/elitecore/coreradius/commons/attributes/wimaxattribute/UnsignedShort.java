package com.elitecore.coreradius.commons.attributes.wimaxattribute;

import java.io.UnsupportedEncodingException;

import com.elitecore.coreradius.commons.attributes.AttributeId;
import com.elitecore.coreradius.commons.attributes.BaseRadiusWiMAXAttribute;
import com.elitecore.coreradius.commons.util.Dictionary;

public class UnsignedShort extends BaseRadiusWiMAXAttribute {

	private static final long serialVersionUID = 1L;

	public UnsignedShort(AttributeId attributeDetail) {
		super(attributeDetail);
	}

	public void setIntValue(int value) {
		byte [] valueBytes = new byte[2];
		valueBytes[1] =  (byte)(value);
		valueBytes[0] =  (byte)(value >>> 8);
		setValueBytes(valueBytes);
	}
	
	public int getIntValue() {
		byte [] valueByte = getValueBytes();
		int intValue = 0;
		for (int counter=0; counter<valueByte.length; counter++) {
			int tempInt = valueByte[counter] & 0xFF;
			//integerValue = (integerValue << 8) | l;
			intValue |= (tempInt << (8*((1)-counter)));
		}
		
		return intValue;
	}
	
	public void setStringValue(String value) {
		 if (value == null) {
	            setIntValue(0);
        }else {
            long longValue = Dictionary.getInstance().getKeyFromValue(getParentId(), value);
            if (longValue >= 0) {
                setIntValue((int)longValue);
            }else {
                try {
                    setIntValue(Integer.parseInt(value));
                }catch(NumberFormatException numberFormatExp) {
                    throw new IllegalArgumentException("Cannot convert " + value + " to UnsignedShortAttribute.",numberFormatExp);
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
			value =	Dictionary.getInstance().getValueFromKey(getParentId(),getIntValue());
		}
		if (value != null)
			return value;
		return String.valueOf(getIntValue());
	}

	public String getStringValue(){
		return getStringValue(true);
	}
	
	@Override
	public void doPlus(String value) {
		try{
			int iVal = Integer.parseInt(value);
			setIntValue(getIntValue() + iVal);
		}catch(NumberFormatException nfe){
		}
	}
}
