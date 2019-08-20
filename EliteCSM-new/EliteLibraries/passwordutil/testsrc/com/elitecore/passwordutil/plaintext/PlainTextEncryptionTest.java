package com.elitecore.passwordutil.plaintext;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.elitecore.passwordutil.DecryptionNotSupportedException;

@RunWith(JUnitParamsRunner.class)
public class PlainTextEncryptionTest {
	
	private PlainTextEncryption plainEncryption = new PlainTextEncryption();
		
	public static Object[][] dataFor_plainTextEncryption(){
		
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
				{"eliteaaa6600"}
		};
	}
	
	@Test
	@Parameters(method = "dataFor_plainTextEncryption")
	public void encrytedTextIsSameAsInputText(String plainPassword) {
		assertEquals(plainEncryption.crypt(plainPassword), plainPassword);
	}
	
	@Test
	@Parameters(method = "dataFor_plainTextEncryption")
	public void decryptedTextIsSameAsEncryptedText(String encryptedPassword) throws DecryptionNotSupportedException {
		assertEquals(plainEncryption.decrypt(encryptedPassword), encryptedPassword);
	}
	
	@Test
	@Parameters(method = "dataFor_plainTextEncryption")
	public void matchesPlainTextWithEncryptedText(String plainText) {
		assertTrue(plainEncryption.matches(plainText, plainText));
	}
	
	@Test
	public void doesNotMatchEncryptionTextIsDifferentThanGeneratedValue() {
		assertFalse(plainEncryption.matches("non-matching-input", "text"));
	}
}
