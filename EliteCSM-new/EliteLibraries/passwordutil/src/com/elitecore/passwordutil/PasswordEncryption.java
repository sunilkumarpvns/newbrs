/*
 *  Elitecore Technologies Ltd.
 *  904, Silicon Tower, Law Garden
 *  Ahmedabad, India - 380009
 *  
 */

package com.elitecore.passwordutil;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.elitecore.passwordutil.aes.EliteAESEncryption;
import com.elitecore.passwordutil.base16.Base16CryptEncryption;
import com.elitecore.passwordutil.base32.Base32CryptEncryption;
import com.elitecore.passwordutil.base64.Base64CryptEncryption;
import com.elitecore.passwordutil.elitecrypt.EliteCryptEncryption;
import com.elitecore.passwordutil.elitecrypt.ElitePasswordCrypt;
import com.elitecore.passwordutil.ldap.LDAPEncryption;
import com.elitecore.passwordutil.md5.MD5Encryption;
import com.elitecore.passwordutil.plaintext.PlainTextEncryption;
import com.elitecore.passwordutil.threedes.Elite3DESEncryption;
import com.elitecore.passwordutil.unixcrypt.UnixCryptEncryption;


public class PasswordEncryption{
	
	public static final int NONE = 0;	  
	public static final int UNIX_CRYPT = 1;
	public static final int MD5 = 2;
	public static final int ELITECRYPT = 3;
	public static final int ASCIICRYPT = 4;
	public static final int LDAP = 5;
	public static final int ELITE_PASSWORD_CRYPT = 6;
	public static final int AES_CRYPT = 7;
	public static final int THREE_DES_CRYPT = 8;
	public static final int BASE16 = 16;
	public static final int BASE32 = 32;
	public static final int BASE64 = 64;
	
	
	public static final String PREFIX_NONE = "{none}";
	public static final String PREFIX_CRYPT = "{crypt}";
	public static final String PREFIX_MD5 = "{md5}";
	public static final String PREFIX_ELITECRYPT = "{elitecrypt}";
	public static final String PREFIX_ASCII = "{ascii}";
	
	private static PasswordEncryption passwordEncryption;
	
	private Map<Integer, IEncryption> cryptMap;
	private PasswordEncryption(){
		loadEncryptionInstances();
	}
	
	private void loadEncryptionInstances(){
		cryptMap = new ConcurrentHashMap<Integer, IEncryption>();
		
		IEncryption encryption = new PlainTextEncryption();
		cryptMap.put(NONE, encryption);
		
		encryption = new UnixCryptEncryption();
		cryptMap.put(UNIX_CRYPT, encryption);
		
		encryption = new MD5Encryption();
		cryptMap.put(MD5,encryption);
		
		encryption = new EliteCryptEncryption();
		cryptMap.put(ELITECRYPT, encryption);
		
		encryption = new Base16CryptEncryption();
		cryptMap.put(BASE16,encryption);
		
		encryption = new Base32CryptEncryption();
		cryptMap.put(BASE32,encryption);
		
		encryption = new Base64CryptEncryption();
		cryptMap.put(BASE64, encryption);
		
		encryption = new LDAPEncryption();
		cryptMap.put(LDAP, encryption);

		encryption = new ElitePasswordCrypt(cryptMap.get(BASE64));
		cryptMap.put(ELITE_PASSWORD_CRYPT, encryption);

		encryption = new EliteAESEncryption();
		cryptMap.put(AES_CRYPT, encryption);

		encryption = new Elite3DESEncryption();
		cryptMap.put(THREE_DES_CRYPT, encryption);
	}
	
	static {
		passwordEncryption = new PasswordEncryption();
		passwordEncryption.init();
	}
	
	/**
	 * this method will return the instance of PasswordEncryption
	 * @return
	 */
	public static PasswordEncryption getInstance() {
		return passwordEncryption;
	}

	/**
	 * this method will return new instance of PasswordEncryption
	 * @return
	 */
	public static PasswordEncryption newInstance() {
		return new PasswordEncryption();
	}

	/**
	 *	Returns encrypted string
	 *	arg1 unecrypted string
	 *	arg2 type of encryption
	 */	  
	public final String crypt(String unencryptedPassword,int type) 
			throws NoSuchEncryptionException, EncryptionFailedException {
		IEncryption icrypt = cryptMap.get(type);
		if(icrypt == null)
			throw new NoSuchEncryptionException();
		if(unencryptedPassword == null)
			return new String();
		return icrypt.crypt(unencryptedPassword);		
	}
	
	/** 
	 *	Returns encrypted string
	 *    arg1 encryptedPassword String
	 *	arg2 unencryptedPassword string
	 *	arg3 type of encryption
	 */
	public final boolean matches(String encryptedPassword, String unencryptedPassword,int type) 
	throws NoSuchEncryptionException, EncryptionFailedException {
		if(encryptedPassword == null)
			return false;
//		return PasswordEncryption.getCrypt(type).matches(encryptedPassword,unencryptedPassword);
		IEncryption icrypt = cryptMap.get(type);
		if(icrypt == null)
			throw new NoSuchEncryptionException();
		return icrypt.matches(encryptedPassword, unencryptedPassword);
	}
	
	public final String decrypt(String encryptedPassword, int nEncType) 
	throws NoSuchEncryptionException, DecryptionNotSupportedException, DecryptionFailedException {

		IEncryption icrypt = cryptMap.get(nEncType);
		if(icrypt == null)
			throw new NoSuchEncryptionException();
		if(encryptedPassword == null){
			return new String();
		}
//		return PasswordEncryption.getCrypt(nEncType).decrypt(encryptedPassword);
		return icrypt.decrypt(encryptedPassword);
	}

	/**
	 * This method will initialize all the encryption technic with its
	 * default value. Hence encryption and decryption will be done using
	 * its respective default key value.
	 * 
	 * @throws NoSuchEncryptionException
	 * @throws InitializationFailedException
	 */
	public void init() {
		for (Entry<Integer, IEncryption> encryptionType : cryptMap.entrySet()) {
			try {
				init(encryptionType.getKey(), new String());
			} catch (NoSuchEncryptionException e) { //NOSONAR - Reason: Exception handlers should preserve the original exceptions 
				e.printStackTrace();
			} catch (InitializationFailedException e) { //NOSONAR - Reason: Exception handlers should preserve the original exceptions
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * This method will initialize the encryption technic with provided value in parameter
	 * @param type specify the type of encryption mechanism
	 * @param args specify the value which will be used for encryption and decryption.
	 * @throws NoSuchEncryptionException
	 * @throws InitializationFailedException
	 */
	public void init(int type, Object ... args) throws NoSuchEncryptionException, InitializationFailedException {
		IEncryption icrypt = cryptMap.get(type);
		if(icrypt == null)
			throw new NoSuchEncryptionException();
		icrypt.init(args);
	}

/*	private static IEncryption getCrypt(int type) throws NoSuchEncryptionException{
		
		String class_name= null;
		if ( type == UNIX_CRYPT)
			class_name = "com.elitecore.passwordutil.unixcrypt.UnixCryptEncryption";
		else if ( type == MD5)
			class_name = "com.elitecore.passwordutil.md5.MD5Encryption";
		else if ( type == NONE ) 
			class_name = "com.elitecore.passwordutil.plaintext.PlainTextEncryption";
		else if ( type == ELITECRYPT ) 
			class_name = "com.elitecore.passwordutil.elitecrypt.EliteCryptEncryption";
		else if ( type == ASCIICRYPT ) 
			class_name = "asciicrypt.AsciiEncryption";
		else if ( type == BASE16)
			class_name = "com.elitecore.passwordutil.base16.Base16CryptEncryption";
		else if ( type == BASE32)
			class_name = "com.elitecore.passwordutil.base32.Base32CryptEncryption";
		else if ( type == BASE64)
			class_name = "com.elitecore.passwordutil.base64.Base64CryptEncryption";
		else
			throw new NoSuchEncryptionException();
		IEncryption  icrypt;
		try {
			icrypt = (IEncryption)Class.forName(class_name).newInstance();
		}catch(java.lang.ClassNotFoundException e){
			throw new NoSuchEncryptionException();
		}catch(java.lang.InstantiationException e1){
			throw new NoSuchEncryptionException();
		}catch(java.lang.IllegalAccessException e1){
			throw new NoSuchEncryptionException();
		}
		return icrypt;
	}
*/
}