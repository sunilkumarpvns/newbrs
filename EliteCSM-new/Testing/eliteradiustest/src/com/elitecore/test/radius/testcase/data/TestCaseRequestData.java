package com.elitecore.test.radius.testcase.data;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class TestCaseRequestData {
	
	private int code;
	private int identifier;
	private int length;
	private String sharedSecret="secret";
	private byte[] requestAuthenticator;
	private List<AttributeData> attributes;
	private byte[] attributeBytes;
	private final static int USERPASSWORD = 2;
	
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
	public void setRequestAuthenticator(byte[] requestAuthenticator) {
		if(requestAuthenticator != null){
			this.requestAuthenticator = new byte[16];
			System.arraycopy(requestAuthenticator, 0, this.requestAuthenticator, 0, 16);
		}			
	}
	public byte[] getRequestAuthenticator() {
		return requestAuthenticator;
	}
	
	public byte[] getBytes(){
		int currentPosition = 0;
		int totalLength = 0;
		byte[] attributeBytes = new byte[4096];
		Iterator iterator = getAttributes().iterator();
		/* Generate Request Authenticator */
		if(this.requestAuthenticator == null)
			requestAuthenticator = generateRequestAutheticatorForAuthentication();
		
		while(iterator.hasNext()){
			AttributeData attributeData = (AttributeData)iterator.next();
			
			if(attributeData.getAttributeId() == USERPASSWORD){
				
				byte[] temp = new byte[attributeData.getAttributeBytes().length - 2];
				
				// get userPassword from byte array
				System.arraycopy(attributeData.getAttributeBytes(), 2, temp, 0, temp.length);
				
				String userPassword = new String(temp);
				
				byte[] bEncryptedUserPassword = encodePassword(userPassword, sharedSecret, requestAuthenticator);
				
				byte[] attributeUserPassword = new byte[bEncryptedUserPassword.length + 2];
				
				attributeUserPassword[0] = (byte)attributeData.getAttributeId();
				attributeUserPassword[1] = (byte)(bEncryptedUserPassword.length + 2);
				
				System.arraycopy(bEncryptedUserPassword, 0, attributeUserPassword, 2, bEncryptedUserPassword.length);
				
				System.arraycopy(attributeUserPassword, 0, attributeBytes, currentPosition, attributeUserPassword.length);
				
				currentPosition = currentPosition + attributeUserPassword.length;
				totalLength = totalLength + attributeUserPassword.length;
				
			}else{
				byte[] temp = attributeData.getAttributeBytes();
				System.arraycopy(temp, 0, attributeBytes, currentPosition, temp.length);
				currentPosition = currentPosition + temp.length;
				totalLength = totalLength + temp.length;
				
			}
		}
		
		setAttributeBytes(attributeBytes);
		
		/*Length = code + identifire + length + request authenticator + attributes */
		length = 1 + 1 + 2 + 16 + totalLength; 
		
		byte[] requestPacketBytes = new byte[length];
		
		/* CODE */
		requestPacketBytes[0] = (byte)getCode();
		
		/* IDENTIFIRE */
		requestPacketBytes[1] = (byte)getIdentifier();
		
		/* LENGTH */
		requestPacketBytes[3] = (byte)length;
		requestPacketBytes[2] = (byte)((length >>> 8) & 0xFF);
		
		/* REQUEST AUTHENTICATOR */
		System.arraycopy(requestAuthenticator, 0, requestPacketBytes, 4, 16);
		
		/* ATTRIBUTES */
		System.arraycopy(attributeBytes, 0, requestPacketBytes, 20, totalLength);
		
		return requestPacketBytes;
	}
	
	public byte[] getAttributeBytes() {
		return attributeBytes;
	}
	public void setAttributeBytes(byte[] attributeBytes) {
		this.attributeBytes = attributeBytes;
	}
	
	private byte[] generateRequestAutheticatorForAuthentication(){
		
		byte[] requestAuthenticator = new byte[16];
		
		Random randomNumberGenerator  = new Random(System.currentTimeMillis());
		
		for(int i=15; i<=0; i--){
		    requestAuthenticator[i] = (byte) randomNumberGenerator.nextInt() ;
        }
		
        try {
			MessageDigest messageDigest = (MessageDigest) MessageDigest.getInstance("MD5").clone();
			messageDigest.reset();
	        messageDigest.update(sharedSecret.getBytes());
	        messageDigest.update(requestAuthenticator);
	        return messageDigest.digest();
		}catch (NoSuchAlgorithmException e) {
		}catch (Exception e){
        }
		
		return requestAuthenticator;
	}
	
	public static byte[] encodePassword( String userPassword, 
			String sharedSecret, byte[] requestAuthenticator) {
        
		if (userPassword==null)
            userPassword = "";
        // encrypt the password.
        byte[] finalUserPassBytes = null;
        //the password must be a multiple of 16 bytes and less than or equal
        //to 128 bytes. If it isn't a multiple of 16 bytes fill it out with
        // zeroes
        //to make it a multiple of 16 bytes. If it is greater than 128 bytes
        //truncate it at 128
        byte [] userPasswordBytes = null;
        try {
            userPasswordBytes = userPassword.getBytes("UTF-8");
        }catch(Exception e) {
            userPasswordBytes = userPassword.getBytes();
        }
        
        if (userPasswordBytes.length > 128) {
            finalUserPassBytes = new byte[128];
            System.arraycopy(userPasswordBytes, 0, finalUserPassBytes, 0, 128);
        } else {
            finalUserPassBytes = userPasswordBytes;
        }
        
        // declare the byte array to hold the final product
        byte[] encryptedPass = null;

        if (finalUserPassBytes.length < 128) {
            if (finalUserPassBytes.length % 16 == 0) {
                // It is already a multiple of 16 bytes
                encryptedPass = new byte[finalUserPassBytes.length];
            } else {
                // Make it a multiple of 16 bytes
                encryptedPass = new byte[((finalUserPassBytes.length / 16) * 16) + 16];
            }
        } else {
            // the encrypted password must be between 16 and 128 bytes
            encryptedPass = new byte[128];
        }

        // copy the userPass into the encrypted pass and then fill it out with
        // zeroes
        System.arraycopy(finalUserPassBytes, 0, encryptedPass, 0,
                finalUserPassBytes.length);
        //Arrays.fill(encryptedPass, userPassBytes.length, encryptedPass.length, (byte)0);

        MessageDigest messageDigest = null;
        try {
			messageDigest = (MessageDigest)MessageDigest.getInstance("MD5").clone();
        }catch(NoSuchAlgorithmException e) {
			
		}catch (Exception e){
            
        }
        
        messageDigest.reset();
        // add the shared secret
        try {
			messageDigest.update(sharedSecret.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			messageDigest.update(sharedSecret.getBytes());
		}
        // add the Request Authenticator.
        messageDigest.update(requestAuthenticator);
        // get the md5 hash( b1 = MD5(S + RA) ).
        byte bn[] = messageDigest.digest();

        for (int i = 0; i < 16; i++) {
            // perform the XOR as specified by RFC 2865.
            encryptedPass[i] = (byte) ( (bn[i] ) ^ (encryptedPass[i]));
        }

        if (encryptedPass.length > 16) {
            for (int i = 16; i < encryptedPass.length; i += 16) {
                messageDigest.reset();
                // add the shared secret
                try {
					messageDigest.update(sharedSecret.getBytes("UTF-8"));
				} catch (UnsupportedEncodingException e) {
					messageDigest.update(sharedSecret.getBytes());
				}
                //add the previous(encrypted) 16 bytes of the user password
                messageDigest.update(encryptedPass, i - 16, 16);
                // get the md5 hash( bn = MD5(S + c(i-1)) ).
                bn = messageDigest.digest();
                for (int j = 0; j < 16; j++) {
                    // perform the XOR as specified by RFC 2865.
                    encryptedPass[i + j] = (byte) ( (bn[j]) ^ (encryptedPass[i + j]));
                }
            }
        }
        return encryptedPass;
    }

	
}
