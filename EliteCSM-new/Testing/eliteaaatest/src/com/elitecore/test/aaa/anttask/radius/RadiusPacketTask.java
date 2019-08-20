package com.elitecore.test.aaa.anttask.radius;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

import com.elitecore.test.aaa.anttask.core.BaseAttributeTask;
import com.elitecore.test.aaa.anttask.core.AttributesTask;

public class RadiusPacketTask extends Task {
	private int code;
	private int identifier;
	private byte[] authenticator;
	private AttributesTask attributesTask;
	private static final String sharedSecret = "secret";
	private final static int USERPASSWORD = 2;
	
	public RadiusPacketTask() {
		setAuthenticator(generateRequestAutheticatorForAuthentication());	
	}
	
	@Override
	public void execute() throws BuildException {
		// TODO Auto-generated method stub
	
	}

	public void addAttributes(AttributesTask attributesTask){
		this.attributesTask = attributesTask;
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
	
	public byte[] getRequestBytes(){
		int currentPosition = 0;
		int length = 0;
		int totalLength = 0;
		byte[] attributeBytes = new byte[4096];	
		
		
		for(BaseAttributeTask attributeTask : this.attributesTask.getAttributes()){
				
			if(attributeTask.getId() == USERPASSWORD){			
				
				byte[] temp = new byte[attributeTask.getBytes().length - 2];
				
				// get userPassword from byte array
				System.arraycopy(attributeTask.getBytes(), 2, temp, 0, temp.length);
				
				String userPassword = new String(temp);
				
				byte[] bEncryptedUserPassword = encodePassword(userPassword, sharedSecret, getAuthenticator());
				
				byte[] attributeUserPassword = new byte[bEncryptedUserPassword.length + 2];
				
				attributeUserPassword[0] = (byte)attributeTask.getId();
				attributeUserPassword[1] = (byte)(bEncryptedUserPassword.length + 2);
				
				System.arraycopy(bEncryptedUserPassword, 0, attributeUserPassword, 2, bEncryptedUserPassword.length);
				
				System.arraycopy(attributeUserPassword, 0, attributeBytes, currentPosition, attributeUserPassword.length);
				
				currentPosition = currentPosition + attributeUserPassword.length;
				totalLength = totalLength + attributeUserPassword.length;
				
			}else{
				byte[] temp = attributeTask.getBytes();
				System.arraycopy(temp, 0, attributeBytes, currentPosition, temp.length);
				currentPosition = currentPosition + temp.length;
				totalLength = totalLength + temp.length;
				
			}
		}				
		
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
		System.arraycopy(getAuthenticator(), 0, requestPacketBytes, 4, 16);
		
		/* ATTRIBUTES */
		System.arraycopy(attributeBytes, 0, requestPacketBytes, 20, totalLength);
		
		return requestPacketBytes;
	}

	public byte[] getResponseBytes(){
		byte[] packetBytes = getRequestBytes();
		byte[] responseAuthenticator = generateResponseAutheticator(packetBytes);
		System.arraycopy(responseAuthenticator, 0, packetBytes, 4, 16);
		return packetBytes;
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

	private byte[] generateResponseAutheticator(byte[] requestPacketBytes){
		
		MessageDigest messageDigest;
		try {
			messageDigest = (MessageDigest)MessageDigest.getInstance("MD5").clone();
			messageDigest.reset();
			messageDigest.update(requestPacketBytes);
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

	public byte[] getAuthenticator() {
		return authenticator;
	}

	public void setAuthenticator(byte[] authenticator) {
		this.authenticator = authenticator;
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
