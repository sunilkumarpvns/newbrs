package com.elitecore.netvertex.service.pcrf.servicepolicy.authprotocol;

import static com.elitecore.commons.logging.LogManager.ignoreTrace;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.coreradius.commons.RadiusGeneralException;
import com.elitecore.coreradius.commons.util.RadiusUtility;
import com.elitecore.coreradius.commons.util.constants.CommonConstants;
import com.elitecore.netvertex.service.pcrf.servicepolicy.authprotocol.exception.AuthenticationFailedException;
import com.elitecore.netvertex.service.pcrf.servicepolicy.authprotocol.exception.InvalidPasswordException;
import com.elitecore.passwordutil.DecryptionFailedException;
import com.elitecore.passwordutil.DecryptionNotSupportedException;
import com.elitecore.passwordutil.NoSuchEncryptionException;
import com.elitecore.passwordutil.PasswordEncryption;

public class CHAP {
	
	public static final String MODULE = "CHAP";
	private static MessageDigest initMessageDigest(){
		MessageDigest messageDigest = RadiusUtility.getMessageDigest(CommonConstants.MD5);
		return messageDigest;
	}
	
	public static void handleCHAPAuthentication(String profilePassword,byte[]chapPswdAttributeBytes,int encryptionType,byte[] challenge) throws AuthenticationFailedException, NoSuchEncryptionException, DecryptionNotSupportedException, DecryptionFailedException{
		
		byte chapId = chapPswdAttributeBytes[0];	
		handleCHAPAuthentication(profilePassword,chapPswdAttributeBytes,encryptionType,challenge,chapId);
	}
	public static void handleCHAPAuthentication(String profilePassword,byte[]chapPswdAttributeBytes,int encryptionType,byte[] challenge,byte chapId) throws AuthenticationFailedException, NoSuchEncryptionException, DecryptionNotSupportedException, DecryptionFailedException{
		byte[] chapPasswordBytes = new byte[chapPswdAttributeBytes.length - 1];
		System.arraycopy(chapPswdAttributeBytes, 1, chapPasswordBytes, 0, chapPswdAttributeBytes.length - 1);		
		String decodedPassword = PasswordEncryption.getInstance().decrypt(profilePassword, encryptionType);
		if(!validateCHAPPassword(chapPasswordBytes, chapId, decodedPassword, challenge)){
			throw new InvalidPasswordException("Invalid Password.");
		}
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
		 		ignoreTrace(e);
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
	
	public static void main(String [] args){
		
		
		try {
			byte[] authenticatorBytes = RadiusUtility.generateRequestAuthenticatorForAuthentication("secret");
			System.out.println(RadiusUtility.bytesToHex(authenticatorBytes));
			CHAP.handleCHAPAuthentication("eliteaaa",authenticatorBytes,0,authenticatorBytes) ;
		} catch (AuthenticationFailedException e) {
			e.printStackTrace();
			ignoreTrace(e);
		} catch (NoSuchEncryptionException e) {
			e.printStackTrace();
			ignoreTrace(e);
		} catch (DecryptionNotSupportedException e) {
			e.printStackTrace();
			ignoreTrace(e);
		} catch (RadiusGeneralException e) {
			e.printStackTrace();
			ignoreTrace(e);
		} catch (DecryptionFailedException e) {
			e.printStackTrace();
			ignoreTrace(e);
		}
		
	}
}
