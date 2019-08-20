package com.elitecore.passwordutil.unixcrypt;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import com.elitecore.passwordutil.DecryptionFailedException;
import com.elitecore.passwordutil.DecryptionNotSupportedException;

@RunWith(JUnitParamsRunner.class)
public class UnixCryptEncryptionTest {
	
	private UnixCryptEncryption unixCryptEncryption = new UnixCryptEncryption();
	
	@Rule public ExpectedException exception = ExpectedException.none();
	
	public static Object[][] dataFor_UnixCryptEncryption() {
		
		return new Object[][] {
				{"test", "tW9JZ/FKvxuuc"},
				{"!@#$%^&*()_+|\\=-", "nnf/PZlVzer2c"},
				{"~~securePassWORD{}[]:;,<.>/?", "CZlGwpkNpEYWc"},
				{"9081740982173049jshdfljksahdfk", "bqltKzI8GXTQI"},
				{"ABCDabcd1234!@#$", "glDkCgjI4Be56"},
				{"A", "z0MwC8lUzVpTQ"},
				{"0", "0UNNjaGFjA71Y"},
				{"a", "74hxhABldWDZ2"},
				{"", "OzrEhjrLVLQLM"},
				{"ELITEAAA644", "T/moVLSUqyXlU"},
				{"eliteaaa6600", ".GLnwqEVpNaIw"}
		};
	}
	
	@Test
	@Parameters(method = "dataFor_UnixCryptEncryption")
	public void convertsPlainTextToEncyptedText(String plainText, String encryptedText) {
		unixCryptEncryption.crypt(plainText);
	}
	
	@Test
	@Parameters(method = "dataFor_UnixCryptEncryption")
	public void matchesPlainTextWithEncryptedText(String plainText, String encryptedText) {
		assertTrue(unixCryptEncryption.matches(encryptedText, plainText));
	}
	
	@Test
	public void doesNotMatchEncryptionTextIsDifferentThanGeneratedValue() {
		assertFalse(unixCryptEncryption.matches("non-matching-input", "text"));
	}
	
	@Test
	public void decryptionNotSupported() throws DecryptionNotSupportedException, DecryptionFailedException {
		exception.expect(DecryptionNotSupportedException.class);
		unixCryptEncryption.decrypt("GLnwqEVpNaIw");
	}
}
