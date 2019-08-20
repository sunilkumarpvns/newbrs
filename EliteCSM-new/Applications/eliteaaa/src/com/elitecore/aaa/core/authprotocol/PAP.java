package com.elitecore.aaa.core.authprotocol;

import com.elitecore.aaa.core.authprotocol.exception.AuthenticationFailedException;
import com.elitecore.aaa.core.authprotocol.exception.InvalidPasswordException;
import com.elitecore.passwordutil.EncryptionFailedException;
import com.elitecore.passwordutil.NoSuchEncryptionException;
import com.elitecore.passwordutil.PasswordEncryption;

public class PAP {
	
	public static void handlePAPAuthentication(String userPassword, String profilePassword,int encryptionType ) throws AuthenticationFailedException, NoSuchEncryptionException, EncryptionFailedException {
			
		if(!PasswordEncryption.getInstance().matches(profilePassword, userPassword, encryptionType)){
			throw new InvalidPasswordException("Invalid Password.");
		}
	}
}

