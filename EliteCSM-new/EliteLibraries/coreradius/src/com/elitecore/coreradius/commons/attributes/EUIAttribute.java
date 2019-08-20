package com.elitecore.coreradius.commons.attributes;

import java.io.UnsupportedEncodingException;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.coreradius.commons.util.RadiusUtility;
import com.elitecore.coreradius.commons.util.constants.CommonConstants;

public class EUIAttribute extends BaseRadiusAttribute {
	private static final long serialVersionUID = 1L;
	private static final String MODULE = "EUI Attribute";

	
    public EUIAttribute(AttributeId attributeDetail) {
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
		setStringValue(value);
	}

	
    public String getStringValue() {
    	String stringValue = null;
    	if(isPrintableString(getValueBytes())){
	    	String originalString = null;
			try {
				originalString = new String(getValueBytes(),"UTF-8");			
			} catch (UnsupportedEncodingException e) {
				originalString = new String(getValueBytes());
			}
			stringValue =  convertToPlainString(originalString);
    	}else{
    		stringValue = RadiusUtility.bytesToHex(getValueBytes()); 
    		if(stringValue.length() > 2)
    			stringValue = stringValue.substring(2);
    	}
    	return stringValue;
    }
	
   
	public String getStringValue(String charsetName) throws UnsupportedEncodingException {
		return getStringValue();
	}

	@Override
	public void doPlus(String value) {
		LogManager.getLogger().warn(MODULE, "Plus operation is not supported if EUI type value");
	}
	
	@Override
	public boolean equals(Object obj) {
		IRadiusAttribute radAttribute = (IRadiusAttribute)obj;
		String value1 = getStringValue();
		String value2 = convertToPlainString(radAttribute.getStringValue());
		return (value1.equalsIgnoreCase(value2));		
	}
	
	@Override
	public boolean patternCompare(String patternString) {
		String value = getStringValue();
		patternString = convertToPlainString(patternString);
		return RadiusUtility.matches(value, patternString.toLowerCase());
	}
	
	@Override
	public boolean stringCompare(String patternString) {
		String value = getStringValue();
		patternString = convertToPlainString(patternString);
		return value.equals(patternString.toLowerCase());
	}

	private String convertToPlainString(String strValue){		
    	String value = "";
    	if(strValue == null)
    		return "";    	    	
    	
    	for(int i=0;i< strValue.length() ; i++){
    		
    		if(isHexDigit(strValue.charAt(i))){
    			value= value + strValue.charAt(i);    			
    		}
    	}
    	return value.toLowerCase();
	}
	
	private boolean isPrintableString(byte[] valueBytes){
		if(valueBytes == null)
			return false;
		for(int i = 0 ; i < valueBytes.length ; i++){
			if(32 > valueBytes[i] || valueBytes[i] > 126){
				return false;
			}
		}
		return true;		
	}
	
	public boolean isHexDigit(int character){
		if(character >= '0' && character <= '9' ){
			return true;
		}else if(character >= 'A' && character <='F'){
			return true;
		}else if(character >= 'a' && character <='f'){
			return true;
		}else if(character == '*'){
			return true;
		}else if(character == '?'){
			return true;
		}
		return false;
	}
	public String toString() {
    	String originalString = null;
    	if(isPrintableString(getValueBytes())){
			try {
				originalString = new String(getValueBytes(),"UTF-8");			
			} catch (UnsupportedEncodingException e) {
				originalString = new String(getValueBytes());
			}
    	}else {
    		originalString = RadiusUtility.bytesToHex(getValueBytes());
    		if(originalString.length() > 2)
    			originalString = originalString.substring(2);
    	}
		if(hasTag()){
			return " = " + getTag() + ":" + originalString;
		}
		return " = " + originalString;
	}
	
}

