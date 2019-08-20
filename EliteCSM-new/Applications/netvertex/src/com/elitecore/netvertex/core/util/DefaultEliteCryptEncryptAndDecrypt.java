package com.elitecore.netvertex.core.util;
import com.elitecore.passwordutil.*;

public class DefaultEliteCryptEncryptAndDecrypt implements PasswordEncryptAndDecrypt {

    public String decrypt(String encodedValue) throws NoSuchEncryptionException, DecryptionNotSupportedException, DecryptionFailedException {
        return PasswordEncryption.getInstance().decrypt(encodedValue, PasswordEncryption.ELITECRYPT);
    }

    public String crypt(String decodedValue) throws NoSuchEncryptionException,EncryptionFailedException{
        return PasswordEncryption.getInstance().crypt(decodedValue,PasswordEncryption.ELITECRYPT);
    }
}
