package com.elitecore.coreradius.commons.attributes.wimaxattribute;

import java.io.UnsupportedEncodingException;

import com.elitecore.coreradius.commons.attributes.AttributeId;
import com.elitecore.coreradius.commons.attributes.BaseRadiusWiMAXAttribute;
import com.elitecore.coreradius.commons.util.Dictionary;

public class UnsignedOctet extends BaseRadiusWiMAXAttribute {

	private static final long serialVersionUID = 1L;

	public UnsignedOctet(AttributeId attributeDetail) {
		super(attributeDetail);
	}
	public void setIntValue(int value) {
		byte [] valueBytes = new byte[1];
		valueBytes[0] =  (byte)(value);
		setValueBytes(valueBytes);
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
                    throw new IllegalArgumentException("Cannot convert " + value + " to UnsignedOctetAttribute.",numberFormatExp);
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
}
