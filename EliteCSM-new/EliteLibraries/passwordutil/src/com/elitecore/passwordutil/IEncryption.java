package com.elitecore.passwordutil;


public interface IEncryption {
	
	/**
	 * arg plainString
	 * returns the crypted String
	 */
    public String crypt(String enteredPassword) throws EncryptionFailedException;

    /**
     * arg1 encryptedPassword
     * arg2 enteredPassword
     * returns true if the encryptedPassword and the enteredPassword matches 
     *		 false if the encryptedPassword and the enteredPassword does not match
     */
	public boolean matches(String encryptedPassword, String enteredPassword)
		throws EncryptionFailedException;
	
	public String decrypt(String encryptedPassword) 
		throws DecryptionNotSupportedException, DecryptionFailedException;

	public void init(Object ... args) throws InitializationFailedException;
}

