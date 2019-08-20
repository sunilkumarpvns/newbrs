package com.elitecore.coreradius.commons.attributes;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.RadiusUtility;

public class IntegerAttribute extends BaseRadiusAttribute {

	private static final long serialVersionUID = 1L;

	public IntegerAttribute() {
	}
	
	public IntegerAttribute(AttributeId attributeDetail) {
		super(attributeDetail);
	}

	public void setBytes(byte[] totalBytes) {
		if(totalBytes != null && totalBytes.length >= 4){
			ByteArrayInputStream in = new ByteArrayInputStream(totalBytes);
			try {
				readFrom(in);
			}catch(IOException ioExp){
				//It's not required becuase it read from always memory				
			}
		}
	}

	/**
	 * 
     * Sets numeric equivalent value for this attribute.
     * First tries to get the numeric equivalent for the given value from the 
     * dictionary available in the memory. If no value can be located from the
     * available dictionaries, it will try to convert the given value to 
     * integer and set it. If all of these operation fails, it will throw @see IllegalArgumentException.
     * 
     * If the parameter passed is NULL, the value will be set to 0.
     *  
	 * @param The new attribute value to be set.
	 * @throws IllegalArgumentException if the value passed cannot be converted to IntegerAttribute.
	 */
	public void setStringValue(String value) {
	    
        if (value == null) {
            setIntValue(0);
        }else {
        	String taglessValue = value;
        	if(hasTag()){
        		taglessValue = setTagAndGetTaglessValue(value);
        	}
            long longValue = Dictionary.getInstance().getKeyFromValue(getParentId(), taglessValue);
            if (longValue >= 0) {
                setLongValue(longValue);
            }else {
                try {
                    setLongValue(Long.parseLong(taglessValue));
                }catch(NumberFormatException numberFormatExp) {
                    throw new IllegalArgumentException("Cannot convert " + taglessValue + " to IntegerAttribute.",numberFormatExp);
                }
            }
        }
	}

	/**
	 * Sets the given value for this attribute.
	 * 
	 * @param The new attribute value to be set.
	 * @throws IllegalArgumentException if the value passed cannot be converted to IntegerAttribute.
	 * @throws UnsupportedEncodingException 
	 */
	public void setStringValue(String value, String charsetName) throws UnsupportedEncodingException {
		setStringValue(value);
	}

	/**
	 * Set the given value for this attribute.
	 */
	public void setIntValue(int value) {
		byte [] valueBytes = new byte[4];
		if(hasTag()){
			valueBytes = new byte[3];
			valueBytes[2] =  (byte)(value);
			valueBytes[1] =  (byte)(value >>> 8);
			valueBytes[0] =  (byte)(value >>> 16);
		}else{
			valueBytes[3] =  (byte)(value);
			valueBytes[2] =  (byte)(value >>> 8);
			valueBytes[1] =  (byte)(value >>> 16);
			valueBytes[0] =  (byte)(value >>> 24);
		}
		setValueBytes(valueBytes);
	}
	
	/**
	 * It sets the integer <code>value<code> as the value of this attribute.
	 * @param value new integer value
	 * @see #setIntValue(int)
	 */
	@Override
	public void setValue(int value) {
		setIntValue(value);
	}

	/**
	 * It sets the long <code>value<code> as the value of this attribute.
	 * @param value new long value
	 * @see #setLongValue(long)
	 */
	@Override
	public void setValue(long value) {
		setLongValue(value);
	}

	public void setLongValue(long value) {
		setIntValue((int)value);
	}
	
	/**
	 * Returns string equivalent value of this attribute from the dictionary. 
	 * If the value cannot be located from the available dictionary, the numeric
	 * value will be returned.
	 * 
	 * @return Returns string equivalent value.
	 */
	
	public String getStringValue() {
		return getStringValue(true);
	}
	
	/**
	 * Here if useDictionary boolean parameter 
	 * is true , then it will try to find String equivalent 
	 * value of attribute in dictionary and return the same.
	 * if useDictionary boolean parameter is false  
	 * then the numeric value will be returned.
	 * @param useDictionary
	 * @return
	 */
	public String getStringValue(boolean bUseDictionary) {
		String value = null;
		if(bUseDictionary){
			value =	Dictionary.getInstance().getValueFromKey(getParentId(),getLongValue());
		}
		if (value != null)
			return value;
		return String.valueOf(getLongValue());
	}
	
	/**
	 * Returns string equivalent value of this attribute from the dictionary. 
	 * If the value cannot be located from the available dictionary, the numeric
	 * value will be returned. Same as @see IntegerAttribute#getStringValue().
	 * 
	 * @return Returns string equivalent value.
	 * 
	 */
	public String getStringValue(String charsetName) throws UnsupportedEncodingException {
		return getStringValue();
	}
	
	 /*
	  * author narendra.pathai
	  * 
	  * This method converts the value received in parameter into integer or long equivalent value
	  * and updates the value by adding the converted value to the present value.
	  * This attribute tries to convert any format from which it can extract integer value
	  * When the value is in IP Address format then it fetches the equivalent long value and adds to 
	  * present value.
	  * 
	  * Three formats are convertible to integer here:
	  * 1) Plain Long value
	  * 2) Key if the attribute is of enum type
	  * 3) IP address attribute 
	  */
	@Override
	public void doPlus(String value) {
		if (value == null) {
            setIntValue(0);
        }else {
            try {
                setLongValue(Long.parseLong(value) + getLongValue());
            }catch(NumberFormatException numberFormatExp) {
	            long longValue = Dictionary.getInstance().getKeyFromValue(getParentId(), value);
	            if (longValue >= 0) {
	                setLongValue(getLongValue() + longValue);
	            }else {
	            	//check if the value is of IP address type
	            	if(value.indexOf('.') != -1){
	            		try {
							long longEquivalent = RadiusUtility.bytesToLong(InetAddress.getByName(value).getAddress());
							setLongValue(getLongValue() + longEquivalent); 
						} catch (UnknownHostException e) {
							throw new IllegalArgumentException("Cannot convert " + value + " to Integer value.");
						}
	            	}else{
	            		throw new IllegalArgumentException("Cannot convert " + value + " to IntegerAttribute.",numberFormatExp);
	            	}
	            }
            }
        }
	}


}
