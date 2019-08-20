/*
 * Created on Mar 24, 2006
 */
package com.elitecore.coreradius.util.chap ;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.coreradius.commons.util.RadiusUtility;
import com.elitecore.coreradius.commons.util.constants.CommonConstants;

/**
 * @author pratik
 */
public class CHAPUtil {

	//private MessageDigest messageDigest = initMessageDigest();
	public static final String MODULE = "CHAPUTIL";
	private static MessageDigest initMessageDigest(){
			MessageDigest messageDigest = RadiusUtility.getMessageDigest(CommonConstants.MD5);
			return messageDigest;
	}
	
	/**
	 * This method compares CHAPPassword with MD5(strUserPassword,strChapID,strRequestAuthenticator).
	 * If comparison is successful then returns true, false otherwise.
	 * @param bCHAPPassword
	 * @param bChapID
	 * @param strUserPassword
	 * @param bRequestAuthenticator
	 * @return
	 */	
	public static final boolean validateCHAPPassword(byte[] bCHAPPassword,byte bChapID,String strUserPassword,byte[] bRequestAuthenticator){
		
		//final int PASSWORD_LENGTH = 15;
		 boolean bAccept = false;
//		 if(messageDigest==null){
		 	MessageDigest messageDigest = initMessageDigest();
//		 }
		 if(messageDigest!=null && bCHAPPassword!=null && strUserPassword!=null){
		 	messageDigest.reset();
		 	messageDigest.update(bChapID);
		 	try{
		 		messageDigest.update(strUserPassword.getBytes(CommonConstants.UTF8));
		 	}catch(UnsupportedEncodingException e){
		 		messageDigest.update(strUserPassword.getBytes());
		 	}
		 	messageDigest.update(bRequestAuthenticator);
		 	byte bDigestedMessage[] = messageDigest.digest();
			if(bDigestedMessage.length != bCHAPPassword.length){
				if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
					LogManager.getLogger().warn(MODULE, "Invalid length of CHAP-Password.");
				
				bAccept = false;
			}else{
				bAccept = true;
				for(int iCount=0;iCount<bDigestedMessage.length;iCount++){
					if(bDigestedMessage[iCount]!=bCHAPPassword[iCount]){
					   bAccept = false;
					   if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
						LogManager.getLogger().trace(MODULE, "received Chap-Password : "+ RadiusUtility.bytesToHex(bCHAPPassword) + ", expected Chap-Password : " + RadiusUtility.bytesToHex(bDigestedMessage));
					   if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
						LogManager.getLogger().warn(MODULE, "Invalid CHAP-Password.");
					   break;	
					}
				}
			}
		 }else{
		 	if(messageDigest==null){
		 		if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
					LogManager.getLogger().error(MODULE, "MessageDigest is not set, so ignoring the request");
		 	}else if(bCHAPPassword==null){
		 		if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
					LogManager.getLogger().error(MODULE, "CHAP-Password is Not Found, so ignoring the request");
		 	}else if(strUserPassword==null){
		 		if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
					LogManager.getLogger().error(MODULE, "User-Password is Not Found, so ignoring the request");
		 	}
		 }		 
		 return bAccept;
	}
	/**
	 * This method generates CHAPPassword with MD5(byte bChapID,String strUserPassword,byte[] bRequestAuthenticator).
	 * if Chap-Challenge is present use Chap-Challenge instead of Request Authenticator. 
    */
	public static final byte[] generateCHAPPassword(byte bChapID,String strUserPassword,byte[] bRequestAuthenticator){
		
	 	MessageDigest messageDigest = initMessageDigest();
	 	
		 if(messageDigest!=null && strUserPassword!=null){
		 	messageDigest.reset();
		 	messageDigest.update(bChapID);
		 	try{
		 		messageDigest.update(strUserPassword.getBytes(CommonConstants.UTF8));
		 	}catch(UnsupportedEncodingException e){
		 		messageDigest.update(strUserPassword.getBytes());
		 	}
		 	messageDigest.update(bRequestAuthenticator);
		 	byte bDigestedMessage[] = messageDigest.digest();
		 	return bDigestedMessage;
		 }	 		 
		 return null;
	}

}
