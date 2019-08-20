package com.elitecore.passwordutil.elitecrypt;

import com.elitecore.passwordutil.CaesarCipher;
import com.elitecore.passwordutil.DecryptionFailedException;
import com.elitecore.passwordutil.DecryptionNotSupportedException;
import com.elitecore.passwordutil.DefaultEncryption;
import com.elitecore.passwordutil.EncryptionFailedException;
import com.elitecore.passwordutil.IEncryption;

public class ElitePasswordCrypt extends DefaultEncryption{

	private static final int SHIFT_VALUE = 4;
	
	private IEncryption firstStepEncryption;
	private CaesarCipher caesarCipher;
	
	public ElitePasswordCrypt(IEncryption firstStepEncryption) {
		this.firstStepEncryption = firstStepEncryption;
		this.caesarCipher = new CaesarCipher(SHIFT_VALUE);
	}
	
	@Override
	public boolean matches(String encryptedPassword, String enteredPassword) throws EncryptionFailedException {
		return encryptedPassword.equals(crypt(enteredPassword));
	}
	
	@Override
	public String crypt(String s) throws EncryptionFailedException {
		//doing the step 1 encryption 
		String encryptedStringAfterStep1 = firstStepEncryption.crypt(s);
		
		//Applying the step 2 encryption
		return caesarCipher.encryptcrc(encryptedStringAfterStep1);
	}
	
	@Override
	public String decrypt(String s) throws DecryptionNotSupportedException, DecryptionFailedException {
		//decrypt the ceaser cipher
		String decryptedStringAfterStep1 = caesarCipher.decryptcrc(s);
		
		//applying the step 2 decryption
		return firstStepEncryption.decrypt(decryptedStringAfterStep1);
	}
}
