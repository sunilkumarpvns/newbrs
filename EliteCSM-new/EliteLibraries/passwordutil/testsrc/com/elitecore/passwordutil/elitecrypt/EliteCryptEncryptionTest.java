package com.elitecore.passwordutil.elitecrypt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.elitecore.passwordutil.DecryptionNotSupportedException;

@RunWith(JUnitParamsRunner.class)
public class EliteCryptEncryptionTest {
	
	private EliteCryptEncryption cryptEncryption = new EliteCryptEncryption();
	
	public static Object[][] dataFor_cryptEncryption(){
		
		return new Object[][]{
				{"test"},
				{"!@#$%^&*()_+|\\=-"},
				{"~~securePassWORD{}[]:;,<.>/?"},
				{"9081740982173049jshdfljksahdfk"},
				{"ABCDabcd1234!@#$"},
				{"A"},
				{"0"},
				{"a"},
				{""},
				{"ELITEAAA644"},
				{"user555"},
				{"checkout2"},
				{"eliteaaa6600"},
		};
	}
	
	@Test
	@Parameters(method = "dataFor_cryptEncryption")
	public void convertsPlainTextToEncyptedTextAndViceVersa(String plainText) throws DecryptionNotSupportedException {
		String encrypted = cryptEncryption.crypt(plainText);
		String decrypted = cryptEncryption.decrypt(encrypted);
		
		assertEquals(plainText, decrypted);
	}
	
	@Test
	@Parameters(method = "dataFor_cryptEncryption")
	public void matchesPlainTextAndEncryptedText(String plainText) {
		String encrypted = cryptEncryption.crypt(plainText);
		boolean result = cryptEncryption.matches(encrypted, plainText);
		assertTrue("Matches was unable to match encrypted and plain text password", result);
	}
	
	@Test
	public void doesNotMatchEncryptionTextIsDifferentThanGeneratedValue() {
		assertFalse(cryptEncryption.matches("non-matching-input", "text"));
	}
}
