/*
 *  EliteRadius
 *    
 *  Elitecore Technologies Ltd.
 *  904, Silicon Tower, Law Garden
 *  Ahmedabad, India - 380009
 *  
 *  Created on 18th June 2007
 *    
 */
package com.elitecore.coreradius.commons.attributes;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.RadiusUtility;
import com.elitecore.coreradius.commons.util.constants.CommonConstants;
 
public abstract class BaseRadiusWiMAXAttribute extends BaseRadiusAttribute{

	private static final long serialVersionUID = 1L;
	private int continuationByte;
	
	public BaseRadiusWiMAXAttribute() {
	}
	
	public BaseRadiusWiMAXAttribute(AttributeId attributeDetail){
		super(attributeDetail);
	}
	
	public int getContinuationByte() {
		return continuationByte;
	}

	public void setContinuationByte(int continuationByte) {
		this.continuationByte = continuationByte;
	}
	
	public boolean isFragmented(){
		return ((continuationByte & 0xFF) & 128 ) == 128; 
	}
	
	public byte[] getBytes() {
		byte [] totalBytes = new byte[getValueBytes().length + 3];
		totalBytes [0] = (byte)getType();
		totalBytes [1] = (byte)(getLength());
		totalBytes [2] = (byte)getContinuationByte();
		System.arraycopy(getValueBytes(),0,totalBytes,3,getValueBytes().length);
		return totalBytes;
	}

	public void setValueBytes(byte[] valueBytes) {
		if (valueBytes != null) {
			if (valueBytes.length <= 252) {
				super.setValueBytes(valueBytes);
			}else {
				byte[] bNewArray = new byte[252];
				System.arraycopy(valueBytes,0,bNewArray,0,252);
				super.setValueBytes(bNewArray);
			}
		}else {
			super.setValueBytes(new byte[0]);
		}
		length = getValueBytes().length + 3;
	}
	
	public void setValue(byte[] value, int salt, String secret, byte[] authenticator){
		if(getEncryptStandard() == Dictionary.RFC2868_ENCRYPT_STANDARD){
			byte[] saltBytes = new byte[2];
			saltBytes[1] = (byte)salt;
			saltBytes[0] = (byte)(salt >>> 8);
			byte[] encValue = RadiusUtility.encryptKeyRFC2868(value, secret, authenticator, saltBytes);
			byte[] attributeValue = new byte[saltBytes.length + encValue.length];
			System.arraycopy(saltBytes, 0, attributeValue, 0, saltBytes.length);
			System.arraycopy(encValue, 0, attributeValue, saltBytes.length, encValue.length);
			
			setValueBytes(attributeValue);
		}else {
			setValueBytes(value);
		}
	}
	
	/**
	 * 
	 * @throws IOException if IO error occurs.
	 * @throws NullPointerException if NULL sourceStream is passed.
	 */
	public int readFrom(InputStream sourceStream) throws IOException {
		int totalByte = 0;
		setType(sourceStream.read());
		totalByte++;
		length = sourceStream.read();
		totalByte++;
		setContinuationByte(sourceStream.read());
		totalByte++;
		if (getLength() < 3){
			length = 0;
			throw new InvalidRadiusAttributeLengthException("Invalid attribute length found for " + getClass().getName());
		}
		byte []value = new byte[getLength() - 3];
		sourceStream.read(value);
		setValueBytes(value);
		return totalByte + value.length;
	}
	
	/**
	 * 
	 * @throws IOException if IO error occurs.
	 * @throws NullPointerException if NULL sourceStream is passed.
	 */
	public int readLengthOnwardsFrom(InputStream sourceStream) throws IOException {
		int totalByte = 0;
		length = sourceStream.read();
		totalByte++;
		setContinuationByte(sourceStream.read());
		totalByte++;
		if (getLength() < 3){
			length = 0;
			throw new InvalidRadiusAttributeLengthException("Invalid attribute length found for " + getClass().getName());
		}
		byte []value = new byte[getLength() - 3];
		sourceStream.read(value);
		setValueBytes(value);
		return totalByte + value.length;
	}
	
	/**
	 * 
	 * @throws IOException if IO error occurs.
	 * @throws NullPointerException if NULL destinationStream is passed.
	 */
	public void writeTo(OutputStream destinationStream) throws IOException {
		destinationStream.write(getType());
		destinationStream.write(getLength());
		destinationStream.write(getContinuationByte());
		destinationStream.write(getValueBytes());
	}

	public Object clone() throws CloneNotSupportedException {
		BaseRadiusWiMAXAttribute result = null;
		result = (BaseRadiusWiMAXAttribute)super.clone();
		result.continuationByte = continuationByte;
		return result;
	}
	
	/**
	 * Here if useDictionary boolean parameter 
	 * is true , then it will try to find String equivalent 
	 * value of attribute in dictionary and return the same.
	 * 
	 * @param bUseDictionary
	 * @return
	 * 
	 */
	public  String getStringValue(boolean bUseDictionary) {
		return getStringValue();
	} 
	
	public String getStringValue() {
		try{
			return new String(getValueBytes(),CommonConstants.UTF8);
		}catch(UnsupportedEncodingException e){
			return new String(getValueBytes());
		}
	}
	
	public ArrayList<IRadiusAttribute> getAttributes() {
		return null;
	}
	
	public String toString(){
		return " = " + getStringValue();
	}
}
