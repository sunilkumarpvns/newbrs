package com.elitecore.coreradius.commons.attributes.wimaxattribute;

import java.io.UnsupportedEncodingException;

import com.elitecore.coreradius.commons.attributes.AttributeId;
import com.elitecore.coreradius.commons.attributes.BaseRadiusWiMAXAttribute;


public class WiMAXStringAttribute extends BaseRadiusWiMAXAttribute{
	
	private static final long serialVersionUID = 1L;

	public WiMAXStringAttribute(AttributeId attributeDetail) {
		super(attributeDetail);
	}

	public WiMAXStringAttribute() {
	}

	public void setStringValue(String value) {
		if (value != null){
			try{
			setValueBytes(value.getBytes("UTF-8"));
			}catch (UnsupportedEncodingException e) {
				setValueBytes(value.getBytes());
			}
		}
	}
	
	public void setStringValue(String value, String charsetName) throws UnsupportedEncodingException {
		if (value != null && charsetName != null) {
			setValueBytes(value.getBytes(charsetName));
		}else {
			setStringValue(value);
		}
	}
	
	public String getStringValue() {
		try {
			return new String(getValueBytes(),"UTF-8");
		} catch (UnsupportedEncodingException e) {
			return new String(getValueBytes());
		}
	}
	
	public String getStringValue(String charsetName) 
	throws UnsupportedEncodingException {
		if (charsetName != null)
			return new String(getValueBytes(),charsetName);
		return new String(getValueBytes());
	}
	
	public void setIntValue(int value) {
		setStringValue(String.valueOf(value));
	}
	
	public int getIntValue(){
		return Integer.parseInt(getStringValue());
	}
	
	public void setLongValue(long value){
		setStringValue(String.valueOf(value));
	}
	
	public long getLongValue(){
		return Long.parseLong(getStringValue());
	}
	
	@Override
	public void setValue(int value) {
		setIntValue(value);
	}

	@Override
	public void setValue(long value) {
		setLongValue(value);
	}
	
}
