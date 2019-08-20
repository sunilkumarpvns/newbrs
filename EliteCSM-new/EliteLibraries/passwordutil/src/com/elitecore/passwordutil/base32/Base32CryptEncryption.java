package com.elitecore.passwordutil.base32;

import com.elitecore.passwordutil.DecryptionNotSupportedException;
import com.elitecore.passwordutil.DefaultEncryption;

public class Base32CryptEncryption extends DefaultEncryption {
	  public String crypt(String enteredPassword){		 
		  return Base32Coder.encodeString(enteredPassword);
	  }
	  
	  /**
	   * arg1 encryptedPassword
	   * arg2 enteredPassword
	   * returns true if the encryptedPassword and the enteredPassword matches 
	   *		 false if the encryptedPassword and the enteredPassword does not match
	   */
		
	  public boolean matches(String encryptedPassword, String enteredPassword){
		return crypt(enteredPassword).equals(encryptedPassword);		
	  }
		
	  public String decrypt(String encryptedPassword)
			throws DecryptionNotSupportedException {		  
		  return Base32Coder.decodeString(encryptedPassword);
	  }
}
