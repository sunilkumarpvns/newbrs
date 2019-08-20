package com.elitecore.passwordutil.ldap;

import com.elitecore.passwordutil.DefaultEncryption;

/**
 * 
 * @author narendra.pathai
 *
 */
public class LDAPEncryption extends DefaultEncryption{

	public static final String CRYPT =	"crypt";
	public static final String MD5   =	"md5"; 
	public static final String SMD5  =	"smd5"; 
	public static final String SHA   =	"sha"; 
	public static final String SSHA  =	"ssha"; 
	
	@Override
	public boolean matches(String encryptedPassword, String unEncryptedPassword) {
		if(encryptedPassword == null || unEncryptedPassword == null)
			return false;
		
		String method = getMethod(encryptedPassword);
		if(method == null)
			return false;
		
		try {
			String encryptedPasswdWithoutMethod = encryptedPassword.substring(encryptedPassword.indexOf('}') + 1);
			return encryptedPasswdWithoutMethod.equals(LDAPPassword.crypt(method, unEncryptedPassword, encryptedPasswdWithoutMethod));
		} catch (LDAPPasswordVerificationException ex) {
			ex.printStackTrace();
			return false;
		}
	}
	
	
	public static String getMethod(String encryptedPassword){
		int indexOfOpeningBrace = encryptedPassword.indexOf('{');
		int indexOfClosingBrace = encryptedPassword.indexOf('}');
		if(indexOfOpeningBrace != -1 && indexOfClosingBrace != -1 && indexOfOpeningBrace < indexOfClosingBrace && indexOfClosingBrace != encryptedPassword.length()){
			return encryptedPassword.substring(indexOfOpeningBrace + 1,indexOfClosingBrace).trim();
		}
		return null;
	}
	
}
