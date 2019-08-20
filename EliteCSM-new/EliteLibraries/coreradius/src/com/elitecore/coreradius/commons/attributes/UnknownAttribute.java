package com.elitecore.coreradius.commons.attributes;

import java.io.UnsupportedEncodingException;

import com.elitecore.coreradius.commons.util.constants.CommonConstants;

public class UnknownAttribute extends BaseRadiusAttribute {

	private static final long serialVersionUID = 1L;

	public UnknownAttribute(byte id) {
		setType(id);
	}

	public UnknownAttribute() {
		// TODO Auto-generated constructor stub
	}

	public UnknownAttribute(AttributeId attributeDetail) {
		super(attributeDetail);
	}

	public void setStringValue(String value) {
		try{
			setValueBytes(value.getBytes(CommonConstants.UTF8));
		}catch(UnsupportedEncodingException e){
			setValueBytes(value.getBytes());
		}
	}

	public void setStringValue(String value, String charsetName) throws UnsupportedEncodingException {
		setStringValue(value);
	}
	
	public String getStringValue() {
		byte data[] = getBytes();
		StringBuffer strData = new StringBuffer();
		for(int cnt=0;cnt<data.length;cnt++) {
			strData.append(Integer.toHexString(data[cnt] & 0xFF));
			strData.append(' ');
		}
		return strData.toString();
	}

	public String getStringValue(String charsetName) throws UnsupportedEncodingException {
		return getStringValue();
	}
	
	public String toString() {
		return " = 0x "  + getStringValue();
	}

}