package com.elitecore.coreradius.commons.attributes.wimaxattribute;

import java.io.UnsupportedEncodingException;

import com.elitecore.coreradius.commons.attributes.AttributeId;
import com.elitecore.coreradius.commons.attributes.BaseRadiusWiMAXAttribute;
import com.elitecore.coreradius.commons.util.constants.CommonConstants;

public class WimaxUnknownAttribute extends BaseRadiusWiMAXAttribute{


	private static final long serialVersionUID = 1L;

	public WimaxUnknownAttribute(byte id) {
		setType(id);
	}

	public WimaxUnknownAttribute() {
	}

	public WimaxUnknownAttribute(AttributeId attributeDetail) {
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
			strData.append(Integer.toHexString(data[cnt]));
			strData.append(' ');
		}
		return strData.toString();
	}

	public String getStringValue(String charsetName) throws UnsupportedEncodingException {
		return getStringValue();
	}
	
	public String toString() {
		return " = 0x " + getStringValue();
	}

}
