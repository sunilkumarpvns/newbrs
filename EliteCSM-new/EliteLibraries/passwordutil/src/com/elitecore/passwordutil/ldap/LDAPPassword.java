package com.elitecore.passwordutil.ldap;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import com.elitecore.passwordutil.base64.Base64;
import com.elitecore.passwordutil.base64.InvalidBase64Encoding;
import com.elitecore.passwordutil.unixcrypt.UnixCrypt;

/**
 * 
 * @author narendra.pathai
 *
 */
public class LDAPPassword {

	public static String crypt(String method, String unEncryptedPassword, String encryptedPassword) throws LDAPPasswordVerificationException{
		if(method.equalsIgnoreCase(LDAPEncryption.MD5)){
			return cryptMD5(unEncryptedPassword);
		}else if(method.equalsIgnoreCase(LDAPEncryption.CRYPT)){
			return cryptCRYPT(unEncryptedPassword,getSeedFromEncryptedPassword(encryptedPassword,0,2));
		}else if(method.equalsIgnoreCase(LDAPEncryption.SHA)){
			return cryptSHA(unEncryptedPassword);
		}else if(method.equalsIgnoreCase(LDAPEncryption.SSHA)){
			return cryptSSHA(unEncryptedPassword,getSeedFromEncryptedPassword(encryptedPassword,20));
		}else if(method.equalsIgnoreCase(LDAPEncryption.SMD5)){
			return cryptSMD5(unEncryptedPassword,getSeedFromEncryptedPassword(encryptedPassword, 16));
		}else{
			throw new LDAPPasswordVerificationException("Encryption method: " + method + " is not supported");
		}
	}
	
	public static String cryptMD5(String unEncryptedPassword) throws LDAPPasswordVerificationException{
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] digest = md.digest(unEncryptedPassword.getBytes());
			return Base64.encode(digest);
		} catch (NoSuchAlgorithmException e) {
			//will never be thrown
			throw new LDAPPasswordVerificationException("Error in calculating hash with MD5. Reason: No such algorithm");
		} 
	}
	
	public static String cryptSMD5(String unEncryptedPassword , byte[] seed) throws LDAPPasswordVerificationException{
		try {
			
			//creating the digest with unencrypted password and salt
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(unEncryptedPassword.getBytes());
			md.update(seed);
			byte[] digest = md.digest();
			
			//appending the salt bytes in digest bytes and encoding with base 64
			byte[] newBytes = new byte[digest.length + seed.length];
			System.arraycopy(digest, 0, newBytes, 0, digest.length);
			System.arraycopy(seed, 0, newBytes, digest.length, seed.length);
			return Base64.encode(newBytes);
			
		} catch (NoSuchAlgorithmException e) {
			throw new LDAPPasswordVerificationException("Error in calculating hash with MD5. Reason: No such algorithm");
		}
	}
	
	public static String cryptSHA(String unEncryptedPassword) throws LDAPPasswordVerificationException{
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			byte[] digest = md.digest(unEncryptedPassword.getBytes());
			return Base64.encode(digest);
		} catch (NoSuchAlgorithmException e) {
			throw new LDAPPasswordVerificationException("Error in calculating hash with SHA-1. Reason: No such algorithm");
		} 
	}
	
	public static String cryptSSHA(String unEncryptedPassword, byte[] seed) throws LDAPPasswordVerificationException{
		try {
			
			//calculating the new digest with the entered password and salt calculated
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			md.update(unEncryptedPassword.getBytes());
			md.update(seed);
			byte[] digest = md.digest();
			
			//appending the salt bytes in digest bytes and encoding with base 64
			byte[] newBytes = new byte[digest.length + seed.length];
			System.arraycopy(digest, 0, newBytes, 0, digest.length);
			System.arraycopy(seed, 0, newBytes, digest.length, seed.length);
			return Base64.encode(newBytes);
			
		} catch (NoSuchAlgorithmException e) {
			throw new LDAPPasswordVerificationException("Error in calculating hash with SHA-1. Reason: No such algorithm");
		}
	}
	
	public static String cryptCRYPT(String unEncryptedPassword, byte[] seed){
		//calculating the new digest with the entered password and salt calculated
		String cryptedPasswd = UnixCrypt.crypt(byteArrayToString(seed), unEncryptedPassword);
		return Base64.encode(cryptedPasswd.getBytes());
	}
	
	public static String byteArrayToString(byte[] input){
		try {
			return new String(input, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			return new String(input);
		}
	}
	
	/**
	 * Decodes the encrypted password with Base 64 and returns the seed bytes
	 * @param encryptedPassword is the password encoded in base 64 and contains salt bytes
	 * @param startPosition is the start position of the salt bytes in decoded password
	 * @param endPosition is the end position of the salt bytes in decoded password
	 * @return Seed bytes
	 * @throws LDAPPasswordVerificationException 
	 */
	public static byte[] getSeedFromEncryptedPassword(String encryptedPassword, int startPosition, int endPosition) throws LDAPPasswordVerificationException{
		int lengthOfSalt = endPosition - startPosition;

		//decoding the encrypted password to retrive the salt bytes
		try {
			byte[] decodedBase64 = Base64.decode(encryptedPassword);
			if(decodedBase64.length < lengthOfSalt)
				throw new LDAPPasswordVerificationException("Decoded base 64 password length is less than seed length");
			
			return Arrays.copyOfRange(decodedBase64, startPosition, endPosition);
		} catch (InvalidBase64Encoding e) {
			throw new LDAPPasswordVerificationException("Invalid base 64 encoding of password");
		}
		
	}
	
	/**
	 * Decodes the encrypted password with Base 64 and returns the seed bytes
	 * @param encryptedPassword is the password encoded in base 64 and contains salt bytes
	 * @param startPosition is the start position of the salt bytes in decoded password
	 * @return Seed bytes starting from start position to the end of decoded bytes
	 * @throws LDAPPasswordVerificationException 
	 */
	public static byte[] getSeedFromEncryptedPassword(String encryptedPassword, int startPosition) throws LDAPPasswordVerificationException{
		//decoding the encrypted password to retrive the salt bytes
		try {
			byte[] decodedBase64 = Base64.decode(encryptedPassword);
			return Arrays.copyOfRange(decodedBase64, startPosition, decodedBase64.length);
		} catch (InvalidBase64Encoding e) {
			throw new LDAPPasswordVerificationException("Invalid base 64 encoding of password");
		}
		
	}
}
