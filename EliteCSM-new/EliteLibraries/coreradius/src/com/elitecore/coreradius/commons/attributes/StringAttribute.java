package com.elitecore.coreradius.commons.attributes;

import java.io.UnsupportedEncodingException;

import com.elitecore.coreradius.commons.util.RadiusUtility;
import com.elitecore.coreradius.commons.util.constants.CommonConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;

public class StringAttribute extends BaseRadiusAttribute {

	private static final long serialVersionUID = 1L;

	public StringAttribute(AttributeId attributeDetail) {
		super(attributeDetail);
	}

	public StringAttribute() {
	}

	/**
	 * Sets value of this attribute to the given String value.
	 */
	public void setStringValue(String value) {
		if (value != null){
			String taglessValue = value;
			try{
				if(hasTag()){
					taglessValue = setTagAndGetTaglessValue(value);
				}
				setValueBytes(taglessValue.getBytes(CommonConstants.UTF8));
			}catch(UnsupportedEncodingException e){
				setValueBytes(taglessValue.getBytes());
			}
		}
	}

	/**
	 * Sets value of this attribute to the given String value using given 
	 * character set.
	 */
	public void setStringValue(String value, String charsetName) 
							throws UnsupportedEncodingException {
		if (value != null && charsetName != null) {
			String taglessValue = value;
			if(hasTag()){
				taglessValue = setTagAndGetTaglessValue(value);
			}
			setValueBytes(taglessValue.getBytes(charsetName));
		}else {
			setStringValue(value);
		}
	}
	
	/**
	 * @return Returns value of this attribute in String format. 
	 */
	public String getStringValue() {
		try {
			return new String(getValueBytes(),"UTF-8");
		} catch (UnsupportedEncodingException e) {
			return new String(getValueBytes());
		}
	}

	/**
	 * @return Returns value of this attribute in String format, using the 
	 * given character set.
	 */
	public String getStringValue(String charsetName) 
				throws UnsupportedEncodingException {
		if (charsetName != null)
			return new String(getValueBytes(),charsetName);
		return new String(getValueBytes());
	}
	
	/**
	 * It sets the integer <code>value<code> as the value of this attribute.
	 * @param value new integer value
	 * @see BaseRadiusAttribute#setIntValue(int)
	 */
	public void setIntValue(int value) {
		setStringValue(String.valueOf(value));
	}
	
	
	/**
	 * It returns the value of this attribute as integer, if it is parsable as 
	 * integer. If the value is not parsable as integer, it throws 
	 * NumberFormatException.
	 * @return integer parsed value of this attribute.
	 * @see BaseRadiusAttribute#getIntValue()
	 */
	public int getIntValue(){
		return Integer.parseInt(getStringValue());
	}
	
	/**
	 * It sets the long <code>value</code> as the value of this attribute.
	 * @param value new long value
	 * @see BaseRadiusAttribute#setLongValue(long)
	 */
	public void setLongValue(long value){
		setStringValue(String.valueOf(value));
	}

	/**
	 * It returns the value of this attribute as long, if it is parsable as 
	 * long. If the value is not parsable as long, it throws 
	 * NumberFormatException.
	 * @return long parsed value of this attribute.
	 * @see BaseRadiusAttribute#getLongValue()
	 */
	public long getLongValue(){
		return Long.parseLong(getStringValue());
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
	
	@Override
	public String toString() {
		if (getVendorID() == 0 && getType() == RadiusAttributeConstants.USER_PASSWORD) {
			return " = ********";	
		}else if (getEncryptStandard() != 0){
			byte data[] = getValueBytes();
			return " = " + RadiusUtility.bytesToHex(data);
		}
		
		return super.toString();
	}
	
}
