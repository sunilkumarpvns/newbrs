package com.elitecore.coreradius.commons.attributes.wimaxattribute;

import java.io.UnsupportedEncodingException;

import com.elitecore.coreradius.commons.attributes.AttributeId;
import com.elitecore.coreradius.commons.attributes.BaseRadiusWiMAXAttribute;
import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.RadiusUtility;
import com.elitecore.coreradius.commons.util.constants.CommonConstants;

public class OctetString extends BaseRadiusWiMAXAttribute{
	

	private static final long serialVersionUID = 1L;

	public OctetString(AttributeId attributeDetail) {
		super(attributeDetail);
	}

	public void setStringValue(String value) {
		if(value != null){
			if(value.startsWith("0x") || value.startsWith("0X")){
				setValueBytes(RadiusUtility.getBytesFromHexValue(value));
	        } else{
	        	try{
	        		setValueBytes(value.getBytes(CommonConstants.UTF8));
	        	}catch(UnsupportedEncodingException e){
	        		setValueBytes(value.getBytes());
	        	}
			}
		}
	}

	public void setStringValue(String value, String charsetName) throws UnsupportedEncodingException {
		if(value != null){
			setValueBytes(value.getBytes(charsetName));
		}
	}

	public String getStringValue(String charsetName) throws UnsupportedEncodingException {
		return new String(getValueBytes(),charsetName);
	}

	public String toString() {
		StringBuilder strBuilder = new StringBuilder();
		byte valueByte[] = getValueBytes();
		if(getEncryptStandard() == Dictionary.RFC2868_ENCRYPT_STANDARD){
			byte[] saltBytes = new byte[2];
			byte[] keyBytes = new byte[valueByte.length-2];
			if(valueByte != null && valueByte.length > 2){
				System.arraycopy(valueByte, 0, saltBytes, 0, saltBytes.length);
				System.arraycopy(valueByte, 2, keyBytes, 0, valueByte.length-2);
			}
			strBuilder.append("Salt = ");
			strBuilder.append(RadiusUtility.bytesToHex(saltBytes));
			strBuilder.append(" Value = ");
			strBuilder.append(RadiusUtility.bytesToHex(keyBytes));	
		}else{
			strBuilder.append(RadiusUtility.bytesToHex(valueByte));
		}
		return " = " + strBuilder.toString();
	}

	public String getStringValue(){
		return RadiusUtility.bytesToHex(getValue());
	}
	
	public void doPlus(String value){
		String hexValue = value;
    	if(!value.startsWith("0x") && !value.startsWith("0X")){
    		hexValue = RadiusUtility.bytesToHex(value.getBytes());
        }
    	hexValue = hexValue.substring(2);
    	setStringValue(getStringValue() + hexValue);
    }
}
