package com.elitecore.coreradius.commons.attributes;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.elitecore.coreradius.commons.util.Dictionary;

public class TimeAttribute extends BaseRadiusAttribute {

	private static final long serialVersionUID = 1L;

	public TimeAttribute(AttributeId attributeDetail) {
		super(attributeDetail);
	}

	/**
	 * String date passed as parameter
	 * should be of following format
	 * EEE MMM dd HH:mm:ss zzz yyyy
	 * eg. Fri Jun 15 16:26:55 GMT+05:30 2007
	 * 
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
                	Date valueDate = null;
                	SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
                	try {
						valueDate = sdf.parse(taglessValue);
					} catch (ParseException e) {
						throw new IllegalArgumentException("Cannot convert " + taglessValue + " to TimeAttribute.",e);
					}
                    setLongValue(valueDate.getTime()/1000);
                }catch(NumberFormatException numberFormatExp) {
                    throw new IllegalArgumentException("Cannot convert " + taglessValue + " to TimeAttribute.",numberFormatExp);
                }
            }
        }
	}

	public void setStringValue(String value, String charsetName) throws UnsupportedEncodingException {
		setStringValue(value);
		
	}
	
	/**
	 * this method will return string value of date
	 * in following format
	 * EEE MMM dd HH:mm:ss zzz yyyy
	 * eg. Fri Jun 15 16:26:55 GMT+05:30 2007
	 * 
	 */
	public String getStringValue() {
		Date date = new Date(getLongValue() * 1000);
		return date.toString();
	}

	public String getStringValue(String charsetName) throws UnsupportedEncodingException {
		return getStringValue();
	}

	/**
	 * This method return the string value of date 
	 * in specified pattern depending of the strPattern
	 * 
	 * @param strPattern
	 * @return
	 */
	public String getFormatedStringValue(String strPattern){
		Date date = new Date(getLongValue() * 1000);
		String strDateValue = null;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat(strPattern);
			if (date != null){
				strDateValue=  sdf.format(date);
			}
		} catch (Exception e) {
			throw new IllegalArgumentException("Cannot convert using specified date pattern.",e);
		}
		return strDateValue;
	}
	
	
	/**
	 * Set the given value for this attribute.
	 */
	public void setIntValue(int value) {
		byte [] valueBytes = new byte[4];
		valueBytes[3] =  (byte)(value);
		valueBytes[2] =  (byte)(value >>> 8);
		valueBytes[1] =  (byte)(value >>> 16);
		valueBytes[0] =  (byte)(value >>> 24);
		setValueBytes(valueBytes);
	}
	
	/**
	 * It sets the integer <code>value<code> as the value of this attribute.
	 * @param value new integer value
	 * @see #setIntValue(int)
	 */
	public void setValue(int value) {
		setIntValue(value);
	}

	/**
	 * It sets the long <code>value<code> as the value of this attribute.
	 * @param value new long value
	 * @see #setLongValue(long)
	 */
	public void setValue(long value) {
		setLongValue(value);
	}

	public void setLongValue(long value) {
		setIntValue((int)value);
	}
	
	@Override
	public void doPlus(String value) {
		//Adds no of milliseconds to current time
		try{
			int iVal = Integer.parseInt(value);
			setIntValue(getIntValue() + iVal);
		}catch(NumberFormatException nfe){
		}
	}
}