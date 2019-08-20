package com.elitecore.exprlib.encryptutil;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

import javax.crypto.Cipher;
import javax.xml.bind.DatatypeConverter;

public class PasswordUtil {

	public static String cryptMD5(String unEncryptedString) throws PasswordEncryptionException{
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			byte[] digest = md.digest(unEncryptedString.getBytes());
			return getHexFromBytes(digest);
		} catch (NoSuchAlgorithmException e) {
			throw new PasswordEncryptionException("Error while calculating hash with MD5. Reason: No such algorithm");
		}
	}
	
	public static String cryptSHA(String unEncryptedString) throws PasswordEncryptionException{
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			byte[] digest = md.digest(unEncryptedString.getBytes());
			return getHexFromBytes(digest);
		} catch (NoSuchAlgorithmException e) {
			throw new PasswordEncryptionException("Error while calculating hash with SHA-1. Reason: No such algorithm");
		}
	}
	
	public static String cryptRSA(String unEncryptedString, PublicKey pubKey) throws PasswordEncryptionException{
		try{
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.ENCRYPT_MODE, pubKey);
			byte[] cipherData = cipher.doFinal(unEncryptedString.getBytes());
			return getHexFromBytes(cipherData);
		}catch(Exception e){
			throw new PasswordEncryptionException("Error while encrypting with RSA. Reason: " + e.getMessage(), e);
		}
	}
	
	public static String decryptRSA(String encryptedString, PrivateKey priKey) throws PasswordEncryptionException{
		try{
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.DECRYPT_MODE, priKey);
			byte[] cipherData = cipher.doFinal(DatatypeConverter.parseHexBinary(encryptedString));
			return new String(cipherData);
		}catch(Exception e){
			throw new PasswordEncryptionException("Error while decrypting with RSA. Reason: " + e.getMessage(), e);
		}
	}
	
	public static PublicKey readPublicKeyFromFile(File keyFile) throws Exception {
		FileInputStream fis = new FileInputStream(keyFile);
		ObjectInputStream ois = new ObjectInputStream(fis);
		try {
			BigInteger m = (BigInteger) ois.readObject();
			BigInteger e = (BigInteger) ois.readObject();
			RSAPublicKeySpec keySpec = new RSAPublicKeySpec(m, e);
			KeyFactory fact = KeyFactory.getInstance("RSA");
			PublicKey pubKey = fact.generatePublic(keySpec);
			return pubKey;
		} catch (Exception e) {
			throw e;
		} finally {
			ois.close();
		}
	}
	
	public static PrivateKey readPrivateKeyFromFile(File keyFile) throws Exception {
		FileInputStream fis = new FileInputStream(keyFile);
		ObjectInputStream ois = new ObjectInputStream(fis);
		try {
			BigInteger m = (BigInteger) ois.readObject();
			BigInteger e = (BigInteger) ois.readObject();
			RSAPrivateKeySpec keySpec = new RSAPrivateKeySpec(m, e);
			KeyFactory fact = KeyFactory.getInstance("RSA");
			PrivateKey privateKey = fact.generatePrivate(keySpec);
			return privateKey;
		} catch (Exception e) {
			throw e;
		} finally {
			ois.close();
		}
	}
	
	private static String getHexFromBytes(byte[] cipherData){
		StringBuffer buffer = new StringBuffer();
		for(int i=0; i<cipherData.length; i++){
			buffer.append(Integer.toHexString((cipherData[i] & 0xFF) | 0x100).substring(1,3));
		}
		return buffer.toString();
	}
}
