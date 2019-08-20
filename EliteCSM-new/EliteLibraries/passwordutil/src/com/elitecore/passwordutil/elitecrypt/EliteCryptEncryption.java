package com.elitecore.passwordutil.elitecrypt;

import com.elitecore.passwordutil.DecryptionNotSupportedException;
import com.elitecore.passwordutil.DefaultEncryption;
import com.elitecore.passwordutil.CaesarCipher;



public class EliteCryptEncryption extends DefaultEncryption { 

	private CaesarCipher caesarCipher;
	
	public EliteCryptEncryption() {
		caesarCipher = new CaesarCipher();
	}
	
	public String crypt(String enteredPassword){
		return caesarCipher.encryptcrc(enteredPassword);
	}

	/**
	 * arg1 encryptedPassword
	 * arg2 enteredPassword
	 * returns true if the encryptedPassword and the enteredPassword matches 
	 *		 false if the encryptedPassword and the enteredPassword does not match
	 */

	public boolean matches(String encryptedPassword, String enteredPassword){
		return caesarCipher.checkEncrypted(enteredPassword , encryptedPassword);
	}

	public String decrypt(String encryptedPassword)
	throws DecryptionNotSupportedException {

		return caesarCipher.decryptcrc(encryptedPassword);
	}

}