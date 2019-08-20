package com.elitecore.netvertex.core.util;

import com.elitecore.passwordutil.DecryptionFailedException;
import com.elitecore.passwordutil.DecryptionNotSupportedException;
import com.elitecore.passwordutil.EncryptionFailedException;
import com.elitecore.passwordutil.NoSuchEncryptionException;

public interface PasswordEncryptAndDecrypt {
    String decrypt(String value) throws NoSuchEncryptionException, DecryptionNotSupportedException, DecryptionFailedException;
    String crypt(String value) throws NoSuchEncryptionException,EncryptionFailedException;
}
