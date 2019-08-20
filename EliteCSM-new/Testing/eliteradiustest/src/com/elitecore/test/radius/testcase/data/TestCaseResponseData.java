package com.elitecore.test.radius.testcase.data;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.List;

public class TestCaseResponseData {

	private int code;
	private int identifier;
	private int length;
	private String sharedSecret="secret";
	private byte[] requestAuthenticator;
	
	private List<AttributeData> attributes;
	private byte[] attributeBytes;
	
	public List<AttributeData> getAttributes() {
		return attributes;
	}
	public void setAttributes(List<AttributeData> attributes) {
		this.attributes = attributes;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public int getIdentifier() {
		return identifier;
	}
	public void setIdentifier(int identifier) {
		this.identifier = identifier;
	}

	public int getLength() {
		return length;
	}
	public byte[] getRequestAuthenticator() {
		return requestAuthenticator;
	}
	
	public byte[] getBytes(byte [] requestAuthenticatorBytes){
		int currentPosition = 0;
		int totalLength = 0;
		byte[] tempAttributeBytes = new byte[4096];
		Iterator iterator = getAttributes().iterator();
		
		while(iterator.hasNext()){
			AttributeData attributeData = (AttributeData)iterator.next();
			if(attributeData.getAttributeId() == 2){
				byte[] userPasswordBytes = attributeData.getAttributeBytes();
				System.arraycopy(userPasswordBytes, 0, tempAttributeBytes, currentPosition, userPasswordBytes.length);
				currentPosition = currentPosition + userPasswordBytes.length;
				totalLength = totalLength + userPasswordBytes.length;
				
			}else{
				byte[] temp = attributeData.getAttributeBytes();
				System.arraycopy(temp, 0, tempAttributeBytes, currentPosition, temp.length);
				currentPosition = currentPosition + temp.length;
				totalLength = totalLength + temp.length;
			}
		}
		
		byte[] attributeBytes = new byte[totalLength];
		System.arraycopy(tempAttributeBytes, 0, attributeBytes, 0, totalLength);
		
		setAttributeBytes(attributeBytes);
		
		/*Length = code + identifire + length + request authenticator + attributes */
		length = 1 + 1 + 2 + 16 + totalLength; 
		
		byte[] responsePacketBytes = new byte[length];
		
		/* CODE */
		responsePacketBytes[0] = (byte)getCode();
		
		/* IDENTIFIRE */
		responsePacketBytes[1] = (byte)getIdentifier();
		
		/* LENGTH */
		responsePacketBytes[3] = (byte)length;
		responsePacketBytes[2] = (byte)((length >>> 8) & 0xFF);
		
		/* Generate Request Authenticator */
		
		requestAuthenticator = generateResponseAutheticator(requestAuthenticatorBytes);
				
		/* REQUEST AUTHENTICATOR */
		System.arraycopy(requestAuthenticator, 0, responsePacketBytes, 4, 16);
		
		/* ATTRIBUTES */
		System.arraycopy(tempAttributeBytes, 0, responsePacketBytes, 20, totalLength);
		
		return responsePacketBytes;
	}
	
	private byte[] generateResponseAutheticator(byte[] requestAuthenticatorBytes){
		MessageDigest messageDigest;
		try {
			messageDigest = (MessageDigest)MessageDigest.getInstance("MD5").clone();
			messageDigest.reset();
			//CODE
			messageDigest.update((byte)getCode());
			//IDENTIFIER
			messageDigest.update((byte)getIdentifier());
			//LENGTH(2 bytes)
			byte [] lengthArray = {(byte)((getLength() >>> 8) & 0xFF),((byte)(getLength() & 0xFF))};
			messageDigest.update(lengthArray);
			//REQUEST AUTHENTICATOR			
			messageDigest.update(requestAuthenticatorBytes);
			//REQUEST-ATTRIBUTES
			messageDigest.update(getAttributeBytes());
			//SHARED-SECRET
	        messageDigest.update(sharedSecret.getBytes());
	        
	        return messageDigest.digest();
	        
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;	
	}
	
	public byte[] getAttributeBytes() {
		return attributeBytes;
	}
	public void setAttributeBytes(byte[] attributeBytes) {
		this.attributeBytes = attributeBytes;
	}
	

}
