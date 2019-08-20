/*
 *  EliteRadius
 *    
 *  Elitecore Technologies Ltd.
 *  904, Silicon Tower, Law Garden
 *  Ahmedabad, India - 380009
 *  
 *  Created on 12th June 2006
 *  Created By Ezhava Baiju
 *  
 */

package com.elitecore.coreradius.commons.attributes;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;

import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.RadiusUtility;
import com.elitecore.coreradius.commons.util.constants.CommonConstants;
import com.elitecore.coreradius.commons.util.constants.PaddingType;

/**
 * Base class for all the radius attributes.
 * 
 * @author Elitecore Technologies Ltd.
 *
 */
public abstract class BaseRadiusAttribute implements IRadiusAttribute, IRadiusTagAttribute, Cloneable{
	private static final long serialVersionUID = 1L;

	private static final int NO_ENCRYPTION = 0;
	
	private int type;
	protected int length;
	private byte[] valueBuffer;
	protected byte[] padding;
	
	protected long vendorID;
	private boolean bIgnoreCase;
	private boolean bHasTag ;
	private int tag;
	protected boolean bAvpair;
	private String parentId;
	private int iEncryptStandard;
	private int level;
	
	protected boolean isTLVLengthFormat;
	
	protected static final String[] indentStringArray = {"\t", "\t\t", "\t\t\t", "\t\t\t\t", "\t\t\t\t\t", "\t\t\t\t\t\t",
		"\t\t\t\t\t\t\t", "\t\t\t\t\t\t\t\t", "\t\t\t\t\t\t\t\t\t", "\t\t\t\t\t\t\t\t\t\t"}; 
	
	public BaseRadiusAttribute(){
        valueBuffer = new byte[0];
		length = 2;
        level = 1;
        bHasTag = false;
        bIgnoreCase = false;
        bAvpair = false;
        iEncryptStandard = NO_ENCRYPTION;
        isTLVLengthFormat = true;
        padding = new byte[0];
    }
	
	public BaseRadiusAttribute(AttributeId attributeDetail) {
		valueBuffer = new byte[0];
		length = 2;
		level = attributeDetail.getAttributeLevel();
		type = attributeDetail.getAttrId();
		vendorID = attributeDetail.getVendorId();
		bHasTag = attributeDetail.hasTag();
		bIgnoreCase = attributeDetail.isIgnoreCase();
		bAvpair = attributeDetail.isAvpair();
		iEncryptStandard = attributeDetail.getEncryptStandard();
		isTLVLengthFormat = attributeDetail.isTLVLengthFormat();
		parentId = attributeDetail.getStringID();
		
		PaddingType paddingType = attributeDetail.getPaddingType();
		if(paddingType != null){
			padding = new byte[paddingType.getLength()];
			System.arraycopy(paddingType.getPaddingBytes(), 0, padding, 0, padding.length);
		}else{
			padding = new byte[0];
		}
	}
	
	/**
	 * @return Returns the length of the attribute.
	 */
	public int getLength() {
		if(isTLVLengthFormat)
		return length;
		else
			return length + 2;
	}
	
	/**
	 * Returns the type of an attribute as per the radius dictionary,
	 * Type means attribute id as per the dictionary.
	 * 
	 * @return Returns type of the attribute.
	 */
	public int getType() {
		return type & 0xFF;
	}
	
	/**
	 * Sets the type of the attribute to the passed value.
	 * Type means attribute id as per the dictionary.
	 * 
	 */
    public void setType(int type) {
		this.type = type;
	}

	/**
	 * @return Returns only the value part of the attribute in the form of bytes array.
	 */
	public byte[] getValue() {
		return getValueBytes();
	}
	
	public byte[] getValue(String secret, byte[] authenticator){
		byte[] decryptedValue = null;
		if(getEncryptStandard() == Dictionary.RFC2868_ENCRYPT_STANDARD){
			byte[] valueBytes = getValueBytes();
			byte[] saltBytes = new byte[2];
			byte[] keyBytes = new byte[valueBytes.length-2];
			if(valueBytes != null && valueBytes.length > 2){
				System.arraycopy(valueBytes, 0, saltBytes, 0, saltBytes.length);
				System.arraycopy(valueBytes, 2, keyBytes, 0, valueBytes.length-2);
				
				decryptedValue = RadiusUtility.decryptKeyRFC2868(keyBytes, secret, authenticator, saltBytes);
			}
		}else if(getEncryptStandard()==Dictionary.RFC2865_ENCRYPT_STANDARD){			
			String plainTextValue = RadiusUtility.decryptPasswordRFC2865(getValueBytes(), authenticator, secret);
			byte[] valueBytes = null;
			try{
				valueBytes = plainTextValue.getBytes(CommonConstants.UTF8);
			}catch(UnsupportedEncodingException e){
				valueBytes = plainTextValue.getBytes();
			}
			return valueBytes;						
		}else if(getEncryptStandard()==Dictionary.RFC2865_ENCRYPT_STANDARD_FOR_CISCO){
			byte[] key = new byte[16];
			byte[] encValue = new byte[getValueBytes().length-16];			
			
			System.arraycopy(getValueBytes(), 0, key, 0, 16);
			System.arraycopy(getValueBytes(), 16, encValue,0, encValue.length);
			String plainTextValue = RadiusUtility.decryptPasswordRFC2865(encValue, key, secret);
			byte[] valueBytes = null;
			try{
				valueBytes = plainTextValue.getBytes(CommonConstants.UTF8);
			}catch(UnsupportedEncodingException e){
				valueBytes = plainTextValue.getBytes();
			}
			int len = (valueBytes[0] & 0xFF);
			byte[] out = new byte[len];
			System.arraycopy(valueBytes, 1, out, 0, len);
			return out;						
		}else{
			decryptedValue = getValueBytes();
		}
		return decryptedValue;
	}
	
	@Override
	public String getStringValue(String sharedSecret, byte[] authenticator) {
		if (getEncryptStandard() == NO_ENCRYPTION) {
    		return getStringValue();
    	}
    	
    	String value;
    	if (getEncryptStandard() == Dictionary.RFC2868_ENCRYPT_STANDARD) {
    		StringBuilder strBuilder = new StringBuilder();
    		strBuilder.append("Salt=");
    		strBuilder.append(RadiusUtility.bytesToHex(getSalt()));
    		strBuilder.append(";Value=");
    		strBuilder.append(RadiusUtility.bytesToHex(getValue(sharedSecret, authenticator)));
    		value = strBuilder.toString();
    	} else {
    		value = RadiusUtility.bytesToHex(getValue(sharedSecret, authenticator));
    	}
    	
    	return value;
	}
	
	/**
	 * Returns the salt bytes if the encryption standard is RFC 2868
	 * @throws UnsupportedOperationException if attribute is not encrypted 
	 * or encryption standard is not RFC 2868
	 */
	private byte[] getSalt() {
		if (getEncryptStandard() != Dictionary.RFC2868_ENCRYPT_STANDARD) {
			throw new UnsupportedOperationException("Attributes with standard: " + getEncryptStandard() + " do not have salt");
		}
		
		byte[] valueBytes = getValueBytes();
		byte[] saltBytes = null;
		
		if (valueBytes != null && valueBytes.length > 2) {
			saltBytes = new byte[2];
			System.arraycopy(valueBytes, 0, saltBytes, 0, saltBytes.length);
		}
		
		return saltBytes;
	}
	
	/**
	 * Sets the passed byte array as the value of the attribute.
	 * @param value Byte array to be set as a value of the attribute.
	 */
	public void setValue(byte[] value) {
		setValueBytes(value);
	}
	
	
	@Override
	public void setStringValue(String value, String sharedSecret, byte[] newAuthenticator) {
		
		if (getEncryptStandard() == 0) {
			setStringValue(value);
		} else {
			byte[] valueBytes = null;
			if (value != null) {
				String taglessValue = value;

				if (hasTag()) {
					taglessValue = setTagAndGetTaglessValue(value);
				}

				if (taglessValue.startsWith("0x") || taglessValue.startsWith("0X")) {
					valueBytes = RadiusUtility.getBytesFromHexValue(taglessValue);
				} else {
					try {
						valueBytes = taglessValue.getBytes(CommonConstants.UTF8);
					} catch (UnsupportedEncodingException e) {
						valueBytes = taglessValue.getBytes();
					}
				}
			}
			
			setValue(valueBytes, 0, sharedSecret, newAuthenticator);
		}
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
		}else if(getEncryptStandard()==Dictionary.RFC2865_ENCRYPT_STANDARD){
			byte[] encValue;
			encValue = RadiusUtility.encryptPasswordRFC2865(new String(value), authenticator, secret);
			setValueBytes(encValue);
		}else if(getEncryptStandard()==Dictionary.RFC2865_ENCRYPT_STANDARD_FOR_CISCO){
			byte[] encValue;
			byte[] plainText = new byte[value.length + 1];
			plainText[0] = (byte)(value.length & 0xFF);
			System.arraycopy(value, 0, plainText, 1, value.length);
			encValue = RadiusUtility.encryptPasswordRFC2865(new String(plainText), authenticator, secret);
			byte[] attributeValue = new byte[authenticator.length + encValue.length];
			System.arraycopy(authenticator, 0, attributeValue, 0, authenticator.length);
			System.arraycopy(encValue, 0, attributeValue, authenticator.length, encValue.length);
			setValueBytes(attributeValue);
		}
		else{
			setValueBytes(value);
		}
	}
	
	public void reencryptValue(String oldSecret, byte[] oldAuthenticator, String newSecret, byte[] newAuthenticator){
		boolean encryptOnly = (oldSecret == null && oldAuthenticator == null);
		if(getEncryptStandard() == Dictionary.RFC2868_ENCRYPT_STANDARD){
			byte[] decKey = null;
			if(encryptOnly)
				decKey = getValue();
			else
			    decKey = getValue(oldSecret, oldAuthenticator);
			setValue(decKey, RadiusUtility.getSalt(), newSecret, newAuthenticator);
		}else if(getEncryptStandard() == Dictionary.RFC2865_ENCRYPT_STANDARD){
			byte[] decKey = null;
			if(encryptOnly)
				decKey = getValue();
			else
			    decKey = getValue(oldSecret, oldAuthenticator);
			setValue(decKey, RadiusUtility.getSalt(), newSecret, newAuthenticator);
		}else if(getEncryptStandard() == Dictionary.RFC2865_ENCRYPT_STANDARD_FOR_CISCO){
			byte[] decKey = null;
			if(encryptOnly)
				decKey = getValue();
			else
			    decKey = getValue(oldSecret, oldAuthenticator);
			setValue(decKey, RadiusUtility.getSalt(), newSecret, RadiusUtility.generateRFC2865RequestAuthenticator());
		}
	}
	
	public void setValue(int value){
	}
	
	public void setValue(long value){
	}
	
	public int getID() {
		return getType();
	}
	
	public byte[] getIDBytes() {
		byte [] idBytes = {(byte)type};
		return idBytes;
	}
	
	public byte[] getBytes() {
		int extraBytes = 2;
		if(hasTag()){
			extraBytes++;
		}
		
		byte [] totalBytes = new byte[valueBuffer.length + extraBytes + padding.length];
		totalBytes [0] = (byte)type;
		totalBytes [1] = (byte)length;
		if(hasTag())
			totalBytes[2] = (byte)tag;
		
		System.arraycopy(valueBuffer,0,totalBytes,extraBytes,valueBuffer.length);
		
		if(padding.length > 0){
			System.arraycopy(padding ,0,  totalBytes , totalBytes.length - padding.length , padding.length);
		}
		
		return totalBytes;
	}

	public void setBytes(byte[] totalBytes) {
		if(totalBytes != null){
			ByteArrayInputStream in = new ByteArrayInputStream(totalBytes);
			try {
				readFrom(in);
			}catch(IOException ioExp){
				//It's not required becuase it read from always memory				
			}
		}
	}

	public byte[] getValueBytes() {
		return valueBuffer;
	}
	
	public void setValueBytes(byte[] valueBytes) {
		if (valueBytes != null) {
			if (valueBytes.length <= 253) {
				this.valueBuffer = valueBytes;
			}else {
				this.valueBuffer = new byte[253];
				System.arraycopy(valueBytes, 0, this.valueBuffer, 0, 253);
			}
		}else {
			valueBuffer = new byte[0];
		}

		int extraBytes = 0;
		if(isTLVLengthFormat)
			extraBytes = 2 ;
		
		if(bHasTag)
			extraBytes++;
		
		length = valueBuffer.length + extraBytes + padding.length;
	}
	
	/**
	 * 
	 * @throws IOException if IO error occurs.
	 * @throws NullPointerException if NULL sourceStream is passed.
	 */
	public int readFrom(InputStream sourceStream) throws IOException {
		int bytesRead = 2;
		type =  sourceStream.read();
		length = sourceStream.read();
		byte [] value;
		if(bHasTag){
			if (length < 3 && isTLVLengthFormat){
				length = 0;
				throw new InvalidRadiusAttributeLengthException("Invalid attribute length found for " + getClass().getName());
			}
			tag = sourceStream.read();
			bytesRead++;
			
			if(isTLVLengthFormat)
				value = new byte[length - bytesRead - padding.length];
			else
				value = new byte[length - padding.length];
			sourceStream.read(value);
			valueBuffer =  value;
			if(padding.length > 0 )
				sourceStream.read(padding);
		}else {
			if (length < 2 && isTLVLengthFormat){
				length = 0;
				throw new InvalidRadiusAttributeLengthException("Invalid attribute length found for " + getClass().getName());
			}
			if(isTLVLengthFormat)
				value = new byte[length - bytesRead - padding.length];
			else
				value = new byte[length - padding.length];
			sourceStream.read(value);
			valueBuffer =  value;
			if(padding.length > 0 )
				sourceStream.read(padding);
		}
		
		return bytesRead+valueBuffer.length + padding.length;
	}

	/**
	 * 
	 * @throws IOException if IO error occurs.
	 * @throws NullPointerException if NULL sourceStream is passed.
	 */
	public int readLengthOnwardsFrom(InputStream sourceStream) throws IOException {
		
		length = sourceStream.read();
		int bytesRead = 2;
		byte [] value;
		if(bHasTag){
			if (length < 3 && isTLVLengthFormat){
				length = 0;
				throw new InvalidRadiusAttributeLengthException("Invalid attribute length found for " + getClass().getName());
			}
			tag = sourceStream.read();
			bytesRead++;
			
			if(isTLVLengthFormat)
				value = new byte[length - bytesRead - padding.length];
			else
				value = new byte[length - padding.length];
			sourceStream.read(value);
			bytesRead+=value.length;
			valueBuffer =  value;
			if(padding.length > 0 ){
				sourceStream.read(padding);
				bytesRead+=padding.length;
			}
		}else {
			if (length < 2 && isTLVLengthFormat){
				length = 0;
				throw new InvalidRadiusAttributeLengthException("Invalid attribute length found for " + getClass().getName());
			}
			if(isTLVLengthFormat)
				value = new byte[length - bytesRead - padding.length];
			else
				value = new byte[length - padding.length];
			sourceStream.read(value);
			bytesRead+=value.length;
			valueBuffer =  value;
			if(padding.length > 0 ){
				sourceStream.read(padding);
				bytesRead+=padding.length;
		}
	}
	
		return bytesRead - 1;
	}
	
	/**
	 * 
	 * @throws IOException if IO error occurs.
	 * @throws NullPointerException if NULL destinationStream is passed.
	 */
	public void writeTo(OutputStream destinationStream) throws IOException {
		destinationStream.write(type);
		destinationStream.write(length);
		if(bHasTag){
			destinationStream.write(tag);
		}
		destinationStream.write(valueBuffer);
		if(padding.length > 0){
			destinationStream.write(padding);
	}
	}

	public String getIDString() {
		return String.valueOf(getID());
	}

	public int getSize() {
		return getLength();
	}

	public void setIntValue(int value) {
		
	}
	
	public int getIntValue(){
		int value = 0;
		for (int i=0;i<getValueBytes().length;i++){
			value = (value << 8) | (getValueBytes()[i] & 0xFF);
		}
		return value;
	}
	
	public long getLongValue(){
		long value = 0;
		for (int i=0;i<getValueBytes().length;i++){
			value = (value << 8) | (long)(getValueBytes()[i] & 0xFF);
		}
		return value;
	}
	
	public void setLongValue(long value){
		
	}
	
	public long getVendorID() {
		return vendorID;
	}

	public void setVendorID(long vendorID) {
		this.vendorID = vendorID;
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		BaseRadiusAttribute result = null;
		result = (BaseRadiusAttribute)super.clone();
		if (valueBuffer != null){
			result.valueBuffer = new byte[valueBuffer.length];
			System.arraycopy(valueBuffer,0,result.valueBuffer,0,valueBuffer.length);
		}
		if(padding.length > 0){
			result.padding = new byte[padding.length];
			System.arraycopy(padding, 0, result.padding,0 , padding.length);
		}
		return result;
	}
	
	@Override
	public String toString() {		
		if(bHasTag){
			return " = " + tag + ":" + getStringValue();
		}
		return " = " + getStringValue();
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
		try {
			return new String(getValueBytes(),"UTF-8");
		} catch (UnsupportedEncodingException e) {
			return new String(getValueBytes());
		}
	}
	
	/**
	 * This method will return the String value
	 * of the attribute based on vendorID.
	 * If vendorID is 0 then it will consider 
	 * standard radius attribute.
	 * 
	 * @param vendorID
	 * @param bUseDictionary
	 * @return
	 */
	public String getStringValue(long vendorID ,boolean bUseDictionary){
		return getStringValue(bUseDictionary);
	}
	
	public boolean equals(Object obj) {
		if(obj == null)
			return false;
		IRadiusAttribute radAttribute = (IRadiusAttribute)obj;
		if(hasTag()){
			if(radAttribute.getTag() != 0){
				if(radAttribute.getTag() != getTag()){
					return false;
				}
			}
		}
		if(isIgnoreCase()){
			return getStringValue().equalsIgnoreCase(radAttribute.getStringValue());
		}else{
			return Arrays.equals(getBytes(), radAttribute.getBytes());
		}
	}
	
	public int hashCode() {
		byte[] attrBytes = getBytes();
		int code = attrBytes[0];
		code = code << 8 | attrBytes[1];
		if(!isIgnoreCase() && attrBytes.length > 1){
			code = code << 8 | attrBytes[2];
			code = code << 8 | attrBytes[attrBytes.length - 1];
		}
		return code;
	}
	
	public void addTLVAttribute(IRadiusAttribute tlvAttr) {
	}
	
	public IRadiusAttribute getAttribute(int iAttributeId){
		return null;
	}
	public IRadiusAttribute getSubAttribute(int ... newAttrIds) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void refreshAttributeHeader(){
		
	}

	public ArrayList<IRadiusAttribute> getSubAttributes(int ... newAttrIds) {
		// TODO Auto-generated method stub
		return null;
	}
	public ArrayList<IRadiusAttribute> getAttributes() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public void doPlus(String value){
		setStringValue(getStringValue() + value);
	}
	
	public boolean patternCompare(String patternString){
		if(isIgnoreCase()){
			return RadiusUtility.matches(getStringValue().toLowerCase(), patternString.toLowerCase());
		}else{
			return RadiusUtility.matches(getStringValue(), patternString);
		}
	}
	
	public boolean stringCompare(String value){
		if(isIgnoreCase()){
			return getStringValue().equalsIgnoreCase(value);
		}else{
			return getStringValue().equals(value);
		}
	}
	public void setIgnoreCase(boolean b) {
		this.bIgnoreCase  = b;
	}
	public boolean isIgnoreCase() {
		return bIgnoreCase;
	}
	public boolean hasTag() {
		return bHasTag;
	}
	public void setHasTag(boolean hasTag) {
		bHasTag = hasTag;
	}
	public int getTag() {
		return tag;
	}
	public void setTag(int tag) {
		this.tag = tag;
	}
	public void setAvpair(boolean bAvpair) {
		this.bAvpair = bAvpair;
	}
	public boolean isAvpair() {
		return bAvpair;
	}
	
	public String getKeyValue(String key){
		StringTokenizer strTok = new StringTokenizer(getStringValue(), Dictionary.getInstance().getVendorAVPairSeparator(getVendorID()));
		while(strTok.hasMoreTokens()){
			String token = strTok.nextToken();
			if(token.contains("=")
					&& token.substring(0, token.indexOf('=')).equals(key)){
				return token.substring(token.indexOf('=') + 1);
			}
		}
		return null;
	}
	
	protected String setTagAndGetTaglessValue(String value){
		String taglessValue = value;
		if(value.contains(":")){
			int index = value.indexOf(':');
			if(index-1 >= 0){
				if(value.charAt(index-1) != '\\'){
		    		try{
		    			setTag(Integer.parseInt(value.substring(0,index)));
		    		} catch(NumberFormatException e){
		//        			If the value of tag is not a valid integer value, it will be set to zero.
		    		}
		    		taglessValue = value.substring(index+1);
				}else{
					taglessValue = value.substring(0, value.indexOf('\\')) + value.substring(value.indexOf('\\')+1);
				}
			}
		}
		return taglessValue;
	}
	
	public void setParentId(String stringID) {
		this.parentId = stringID;
	}
	public String getParentId(){
		return this.parentId;
	}
	
	public void setEncryptStandard(int encryptStandard) {
		iEncryptStandard = encryptStandard;
	}
	public int getEncryptStandard() {
		return iEncryptStandard;
	}
	public void setLevel(int level) {
		this.level = level;
	}
	public int getLevel() {
		return this.level;
	}
	public boolean isVendorSpecific(){
		return false;
	}
	
	public boolean isTLVLengthFormat() {
		return isTLVLengthFormat;
	}
}
