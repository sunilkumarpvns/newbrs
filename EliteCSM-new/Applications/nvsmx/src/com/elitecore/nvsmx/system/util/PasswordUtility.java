package com.elitecore.nvsmx.system.util;

import com.elitecore.passwordutil.*;

/**
 * This class will be used to encrypt password using "PasswordEncryption.ELITE_PASSWORD_CRYPT".
 * User can fetch encrypted or decrypted password
 * Created by dhyani on 14/12/16.
 */
public class PasswordUtility {

    public static String getEncryptedPassword(String password) throws NoSuchEncryptionException, EncryptionFailedException {
        return PasswordEncryption.getInstance().crypt(password,PasswordEncryption.ELITE_PASSWORD_CRYPT);
    }

    public static String getDecryptedPassword(String password) throws DecryptionNotSupportedException, DecryptionFailedException, NoSuchEncryptionException {
        return PasswordEncryption.getInstance().decrypt(password,PasswordEncryption.ELITE_PASSWORD_CRYPT);
    }
}
