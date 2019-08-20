package com.elitecore.passwordutil.unixcrypt;

import com.elitecore.passwordutil.DefaultEncryption;


public class UnixCryptEncryption extends DefaultEncryption {

	public String crypt(String unencryptedPassword){
		return UnixCrypt.crypt(unencryptedPassword);
	}
	
	public boolean matches(String encryptedPassword, String unecyrptedPassword){
		return UnixCrypt.matches(encryptedPassword,unecyrptedPassword);
	}
	
	public static void main(String[] args) {
		UnixCryptEncryption unixCryptEncryption = new UnixCryptEncryption();
		System.out.println(unixCryptEncryption.crypt("csmbuild"));
	}
	
}
