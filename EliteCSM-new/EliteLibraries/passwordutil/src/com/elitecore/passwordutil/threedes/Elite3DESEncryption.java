package com.elitecore.passwordutil.threedes;

import java.security.NoSuchAlgorithmException;
import java.security.spec.KeySpec;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;
import javax.xml.bind.DatatypeConverter;

import com.elitecore.passwordutil.DecryptionFailedException;
import com.elitecore.passwordutil.DecryptionNotSupportedException;
import com.elitecore.passwordutil.DefaultEncryption;
import com.elitecore.passwordutil.EncryptionFailedException;
import com.elitecore.passwordutil.InitializationFailedException;

public class Elite3DESEncryption extends DefaultEncryption {

	private Cipher cipher;
	private SecretKey secretKey;
	private static String CIPHER_ALGO = "DESede";
	private static String DEFAULT_KEY = "El!T3C0re+c1p#3R+1lG0r!t#m";

	public String crypt(String enteredPassword) throws EncryptionFailedException {
		String encryptedData = null;
		try {
			if (cipher != null && secretKey != null) {
				// Initialize the cipher
				cipher.init(Cipher.ENCRYPT_MODE, secretKey);
				// Encrypt the data
				byte[] encryptedByteArray = cipher.doFinal(enteredPassword.getBytes("UTF8"));
				// Convert to Hex
				encryptedData = getHexFromBytes(encryptedByteArray);
			} else {
				throw new EncryptionFailedException("Cipher object or Key is not initialized.");
			}
		} catch (Exception e) {
			throw new EncryptionFailedException("Error while encrypting the data", e);
		}
		return encryptedData;
	}

	public boolean matches(String encryptedPassword, String enteredPassword) throws EncryptionFailedException {
		try {
			if (cipher != null && secretKey != null) {
				cipher.init(Cipher.ENCRYPT_MODE, secretKey);
				byte[] encryptedByteArray = cipher.doFinal(enteredPassword.getBytes());
				String encryptedData = getHexFromBytes(encryptedByteArray);
				if (encryptedData.equals(encryptedPassword)) {
					return true;
				}
			} else {
				throw new EncryptionFailedException("Cipher object or Key is not initialized.");
			}
		} catch (Exception e) {
			throw new EncryptionFailedException("Error while matching the data", e);
		}
		return false;
	}

	public String decrypt(String encryptedPassword)
			throws DecryptionNotSupportedException, DecryptionFailedException {
		String decryptedData = null;
		try {
			if (cipher != null && secretKey != null) {
				// Initialize the cipher
				cipher.init(Cipher.DECRYPT_MODE, secretKey);
				// Convert from HEX mode
				byte[] encryptedByteArray = DatatypeConverter.parseHexBinary(encryptedPassword);
				// Decrypt the data
				byte[] decryptedByteArray = cipher.doFinal(encryptedByteArray);
				decryptedData = new String(decryptedByteArray, "UTF8");
			} else {
				throw new DecryptionFailedException("Cipher object or Key is not initialized.");
			}
		} catch (Exception e) {
			throw new DecryptionFailedException("Error while decrypting the data", e);
		}
		return decryptedData;
	}

	public void init(Object ... args) throws InitializationFailedException {
		String key = DEFAULT_KEY;
		if((args != null && args.length > 0)){
			Object obj = args[0];
			if(obj instanceof String && ((String) obj).trim().length() > 0){
				key = (String) obj;
			} 
		}

		try {
			if(!isValidKey(key))
				throw new InitializationFailedException("Invalid Key Length");

			KeySpec skeySpec = new DESedeKeySpec(key.getBytes());
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance(CIPHER_ALGO);
			secretKey = keyFactory.generateSecret(skeySpec);
			cipher = Cipher.getInstance(CIPHER_ALGO);
		} catch (Exception e) {
			throw new InitializationFailedException("Error while initialization", e);
		}
	}

	private boolean isValidKey(String key) throws NoSuchAlgorithmException{
		boolean validKey = false;
		int keyLength = key.length();
		if((keyLength > 23) && (keyLength%2 == 0) && (key.getBytes().length <= Cipher.getMaxAllowedKeyLength(CIPHER_ALGO))){
			validKey = true;
		}
		return validKey;
	}

}
