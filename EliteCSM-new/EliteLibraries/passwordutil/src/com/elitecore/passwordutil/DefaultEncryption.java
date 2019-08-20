/*
 *  Server Framework
 *  
 *  Elitecore Technologies Ltd.
 *  904, Silicon Tower, Law Garden
 *  Ahmedabad, India - 380009
 *  
 */


package com.elitecore.passwordutil;



// Referenced classes of package utils:
//            DecryptionNotSupportedException, IEncryption

public class DefaultEncryption implements IEncryption {

    public DefaultEncryption() {
    	
    }

    public String crypt(String s) throws EncryptionFailedException {
        return s;
    }

    public boolean matches(String s, String s1) throws EncryptionFailedException {
        return false;
    }

    public String decrypt(String s) throws DecryptionNotSupportedException, DecryptionFailedException {
        throw new DecryptionNotSupportedException();
    }

    public void init(Object ... args) throws InitializationFailedException{
    	
    }

	public String getHexFromBytes(byte[] cipherData) {
		StringBuffer buffer = new StringBuffer();
		for (int i = 0; i < cipherData.length; i++) {
			buffer.append(Integer.toHexString((cipherData[i] & 0xFF) | 0x100)
					.substring(1, 3));
		}
		return buffer.toString();
	}

}