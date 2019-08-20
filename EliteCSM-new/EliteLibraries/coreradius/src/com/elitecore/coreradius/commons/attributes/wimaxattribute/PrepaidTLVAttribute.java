package com.elitecore.coreradius.commons.attributes.wimaxattribute;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;

import com.elitecore.coreradius.commons.attributes.AttributeId;
import com.elitecore.coreradius.commons.attributes.BaseRadiusAttribute;

public class PrepaidTLVAttribute extends BaseRadiusAttribute{
	private static final long serialVersionUID = 1L;
	private long valueDigits;
	
	private int exponent;
	
	public PrepaidTLVAttribute(AttributeId attributeDetail) {
		super(attributeDetail);
	}

	public int readLengthOnwardsFrom(InputStream in) throws IOException {
		int bytesRead=0;
		
		length = in.read();
		bytesRead++;
		int length = getLength();
		
		if(length == 10){
//			only value-digits is present
			for (int counter=0; counter<length-2; counter++) {
				long tempInt = in.read() & 0xFF;
				bytesRead++;
				valueDigits |= (tempInt << (8*(((length-2)-1)-counter)));
			}
		}else if(length == 14){
//		value-digits and exponent both are present
			for (int counter=0; counter<length-2; counter++) {
				if(counter < 8){
					long tempInt = in.read() & 0xFF;
					bytesRead++;
					valueDigits |= (tempInt << (8*(((length-6)-1)-counter)));
				} else{
					int tempInt = in.read() & 0xFF;
					bytesRead++;
					exponent |= (tempInt << (8*(((length-2)-1)-counter)));
				}
			}
		}
		
		setValue(valueDigits, exponent);
		return bytesRead;
	}
	
	public void setValue(long valueDigits, int exponent) {
		byte[] valueBytes = new byte[8];
		this.valueDigits = valueDigits;
		this.exponent = exponent;
		if(exponent != 0){
			valueBytes = new byte[12];
			valueBytes[11] =  (byte)(exponent);
			valueBytes[10] =  (byte)(exponent >>> 8);
			valueBytes[9] =  (byte)(exponent >>> 16);
			valueBytes[8] =  (byte)(exponent >>> 24);
		}
		
		valueBytes[7] =  (byte)(valueDigits);
		valueBytes[6] =  (byte)(valueDigits >>> 8);
		valueBytes[5] =  (byte)(valueDigits >>> 16);
		valueBytes[4] =  (byte)(valueDigits >>> 24);
		valueBytes[3] =  (byte)(valueDigits >>> 32);
		valueBytes[2] =  (byte)(valueDigits >>> 40);
		valueBytes[1] =  (byte)(valueDigits >>> 48);
		valueBytes[0] =  (byte)(valueDigits >>> 56);
		
		setValueBytes(valueBytes);
	}

	public int getExponent() {
		return exponent;
	}
	public void setExponent(int exponent) {
		this.exponent = exponent;
	}
	public long getValueDigits() {
		return valueDigits;
	}
	public void setValueDigits(long valueDigits) {
		this.valueDigits = valueDigits;
	}
	
	public String getPrepaidValue(){
		if(exponent == 0)
			return String.valueOf(valueDigits);
		else {
			return String.valueOf(valueDigits * Math.pow(10, exponent));
		}
	}
	
	public String getStringValue(){
		return getPrepaidValue();
	}
	
	public void setStringValue(String value){
		try{
			if(value.contains(".")){
				BigDecimal dvalue = new BigDecimal(value);
				exponent = dvalue.scale() * -1;
				valueDigits = dvalue.unscaledValue().longValue();
			} else {
				valueDigits = Long.parseLong(value);
				exponent = 0;
			}
			setValue(valueDigits, exponent);
		}catch(NumberFormatException e){
			setValueDigits(0);
			setExponent(0);			
		}
	}
	
	public String getStringValue(String charsetName) throws UnsupportedEncodingException {
		return getStringValue();
	}
	public void setStringValue(String value, String charsetName) throws UnsupportedEncodingException {
		setStringValue(value);
	}
	
	public String toString(){
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append("= {Value-Digits = ");
		strBuilder.append(valueDigits);
		strBuilder.append(" ,Exponent = ");
		strBuilder.append(exponent).append("}");
		return strBuilder.toString();
	}
	
	public void doPlus(String value){
		try{
			double dVal = Double.parseDouble(value);
			double finalVal = Double.parseDouble(getPrepaidValue()) + dVal;
			setStringValue(String.valueOf(finalVal));
		}catch (Exception e) {
		}
	}
	
}
