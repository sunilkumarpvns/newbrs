package com.elitecore.passwordutil;

import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author narendra.pathai
 */
@RunWith(HierarchicalContextRunner.class)
public class PasswordEncryptionTest {

    private static final String ANY_PASSWORD = "ANY-PASSWORD";
    private static final int UNKNOWN_TYPE = 100;

    @Rule public ExpectedException exception = ExpectedException.none();

    public class Encryption {

        @Test
        public void throwsNoSuchEncryptionExceptionIfEncryptionMethodIsNotSupported() throws NoSuchEncryptionException, EncryptionFailedException {
            exception.expect(NoSuchEncryptionException.class);
            int UNKNOWN_TYPE = 100;

            PasswordEncryption.getInstance().crypt(ANY_PASSWORD, UNKNOWN_TYPE);
        }

        @Test
        public void returnsEmptyStringIfPlaintextPasswordIsNull() throws NoSuchEncryptionException, EncryptionFailedException {
            assertThat(PasswordEncryption.getInstance().crypt(null, PasswordEncryption.NONE),
                    is(equalTo("")));
        }

        @Test
        public void encryptsPlaintextPasswordUsingTheEncryptionTypeProvided() throws NoSuchEncryptionException, EncryptionFailedException {
            assertThat(PasswordEncryption.getInstance().crypt("a", PasswordEncryption.MD5),
                    is(equalTo("0cc175b9c0f1b6a831c399e269772661")));
        }
    }

    public class Decryption {

        @Test
        public void throwsNoSuchEncryptionExceptionIfEncryptionMethodIsNotSupported() throws NoSuchEncryptionException, EncryptionFailedException, DecryptionFailedException, DecryptionNotSupportedException {
            exception.expect(NoSuchEncryptionException.class);

            PasswordEncryption.getInstance().decrypt(ANY_PASSWORD, UNKNOWN_TYPE);
        }

        @Test
        public void returnsEmptyStringIfEncryptedPasswordIsNull() throws NoSuchEncryptionException, EncryptionFailedException, DecryptionFailedException, DecryptionNotSupportedException {
            assertThat(PasswordEncryption.getInstance().decrypt(null, PasswordEncryption.NONE),
                    is(equalTo("")));
        }

        @Test
        public void decryptsEcryptedPasswordUsingTheEncryptionTypeProvided() throws NoSuchEncryptionException, EncryptionFailedException {
            assertThat(PasswordEncryption.getInstance().crypt("test", PasswordEncryption.ELITECRYPT),
                    is(equalTo("yjxy")));
        }

        @Test
        public void throwsDecryptionNotSupportedExceptionIfTypeDoesNotSupportDecryption() throws DecryptionNotSupportedException, DecryptionFailedException, NoSuchEncryptionException {
            exception.expect(DecryptionNotSupportedException.class);

            PasswordEncryption.getInstance().decrypt(ANY_PASSWORD, PasswordEncryption.MD5);
        }
    }

    public class MatchingPlaintextAndEncryptedPassword {

        @Test
        public void returnsFalseIfEncryptedPasswordIsNull() throws NoSuchEncryptionException, EncryptionFailedException {
            assertFalse(PasswordEncryption.getInstance().matches(null, ANY_PASSWORD, PasswordEncryption.NONE));
        }

        @Test
        public void throwsNoSuchEncryptionExceptionIfTypeIsUnsupported() throws NoSuchEncryptionException, EncryptionFailedException {
            exception.expect(NoSuchEncryptionException.class);

            PasswordEncryption.getInstance().matches(ANY_PASSWORD, ANY_PASSWORD, UNKNOWN_TYPE);
        }

        @Test
        public void matchesBasedOnEncryptionTypeProvided() throws NoSuchEncryptionException, EncryptionFailedException {
            assertTrue(PasswordEncryption.getInstance().matches("yjxy", "test", PasswordEncryption.ELITECRYPT));
        }
    }
}
