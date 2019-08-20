package com.elitecore.license.util;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.elitecore.commons.base.Bytes;

/**
 * A utility class to perform AES encryption and decryption.
 * 
 * @author malav.desai
 *
 */
public class AES {
	
	private static final String AES_CBC_PKCS5PADDING = "AES/CBC/PKCS5PADDING";
	private static final String AES = "AES";
	private static final String PASSPHRASE = "elitecsmelitecsm";
	private static final String INITIAL_VECTOR = "csmelitecsmelite";
	
	public static String encrypt(String value) throws NoSuchAlgorithmException, 
		NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, 
		IllegalBlockSizeException, BadPaddingException {
            IvParameterSpec iv = new IvParameterSpec(INITIAL_VECTOR.getBytes());
            SecretKeySpec skeySpec = new SecretKeySpec(PASSPHRASE.getBytes(), AES);

            Cipher cipher = Cipher.getInstance(AES_CBC_PKCS5PADDING);
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            byte[] encrypted = cipher.doFinal(value.getBytes());

            return Bytes.toHex(encrypted);
    }

	public static String decrypt(String encrypted) throws NoSuchAlgorithmException, 
		NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, 
		IllegalBlockSizeException, BadPaddingException {
		IvParameterSpec iv = new IvParameterSpec(INITIAL_VECTOR.getBytes());
		SecretKeySpec skeySpec = new SecretKeySpec(PASSPHRASE.getBytes(), AES);

		Cipher cipher = Cipher.getInstance(AES_CBC_PKCS5PADDING);
		cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
		byte[] fromHex = Bytes.fromHex(encrypted);
		if (fromHex == null) throw new InvalidKeyException(encrypted + " has invalid Hex encoding, Reason: Probably the values in database has been tampered with");
		byte[] original = cipher.doFinal(fromHex);

		return new String(original);

	}

    public static void main(String[] args) throws InvalidKeyException, 
    	NoSuchAlgorithmException, NoSuchPaddingException, InvalidAlgorithmParameterException, 
    	IllegalBlockSizeException, BadPaddingException {
        System.out.println(decrypt(encrypt("malav")));
    }

}
