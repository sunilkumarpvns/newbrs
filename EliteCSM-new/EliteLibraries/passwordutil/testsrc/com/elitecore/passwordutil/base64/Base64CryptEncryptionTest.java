package com.elitecore.passwordutil.base64;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.elitecore.passwordutil.DecryptionNotSupportedException;

@RunWith(JUnitParamsRunner.class)
public class Base64CryptEncryptionTest {

	/**
	 * Inputs and Outputs taken for unit testing is from RFC-4648
	 */
	
	private Base64CryptEncryption base64CryptEncryption = new Base64CryptEncryption();
	
	public Object[][] dataFor_Base64Encryption(){
		return new Object[][]{
				{"", ""},
				{"f", "Zg=="},
				{"fo", "Zm8="},
				{"foo", "Zm9v"},
				{"foob", "Zm9vYg=="},
				{"fooba", "Zm9vYmE="},
				{"foobar", "Zm9vYmFy"},
		};
	}
	
	@Test
	@Parameters(method = "dataFor_Base64Encryption")
	public void returnsEncodedStringOfGivenInputString(String plainText, String base64Value){
		assertEquals(base64CryptEncryption.crypt(plainText), base64Value);
	}
	
	@Test
	@Parameters(method = "dataFor_Base64Encryption")
	public void returnsTheDecodedStringOfGivenInputString(String expected,String decodedText) throws DecryptionNotSupportedException{
		assertEquals(base64CryptEncryption.decrypt(decodedText), expected);
	}
	
	@Test
	@Parameters(method = "dataFor_Base64Encryption")
	public void matchesPlainTextWithEncryptedText(String plainText, String encyrptedText){
		assertTrue(base64CryptEncryption.matches(encyrptedText, plainText));
	}
	
	@Test
	public void doesNotMatchEncryptionTextIsDifferentThanGeneratedValue(){
		assertFalse(base64CryptEncryption.matches("non-matching-input", "text"));
	}
}
