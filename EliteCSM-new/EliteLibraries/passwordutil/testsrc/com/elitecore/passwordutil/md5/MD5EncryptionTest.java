package com.elitecore.passwordutil.md5;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import com.elitecore.passwordutil.DecryptionFailedException;
import com.elitecore.passwordutil.DecryptionNotSupportedException;

@RunWith(JUnitParamsRunner.class)
public class MD5EncryptionTest {

	/**
	 * Inputs and Outputs taken for unit testing is from RFC-1321
	 */
	
	private MD5Encryption md5Encryption = new MD5Encryption();
	
	@Rule public ExpectedException exception = ExpectedException.none();
	
	public Object[][] dataFor_md5Encryption(){
		return new Object[][]{
				{"", "d41d8cd98f00b204e9800998ecf8427e"},
				{"a", "0cc175b9c0f1b6a831c399e269772661"},
				{"abc", "900150983cd24fb0d6963f7d28e17f72"},
				{"message digest", "f96b697d7cb7938d525a2f31aaf161d0"},
				{"abcdefghijklmnopqrstuvwxyz", "c3fcd3d76192e4007dfb496cca67e13b"},
				{"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789", "d174ab98d277d9f5a5611c2c9f419d9f"},
				{"12345678901234567890123456789012345678901234567890123456789012345678901234567890", "57edf4a22be3c955ac49da2e2107b67a"},
		};
	}
	
	@Test
	@Parameters(method = "dataFor_md5Encryption")
	public void returnsMD5HashValueOfGivenInputString(String plainText, String hashValue){
		assertEquals(md5Encryption.crypt(plainText), hashValue);
	}
	
	@Test
	@Parameters(method = "dataFor_md5Encryption")
	public void digestLengthIsSixteen(String plainText, String hashValue) {
		assertEquals(16, hashValue.length()/2);
	}
	
	@Test
	@Parameters(method = "dataFor_md5Encryption")
	public void matchesPlainTextWithEncryptedText(String plainText, String encyrptedText) {
		assertTrue(md5Encryption.matches(encyrptedText, plainText));
	}
	
	@Test
	public void doesNotMatchEncryptionTextIsDifferentThanGeneratedValue(){
		assertFalse(md5Encryption.matches("non-matching-input", "text"));
	}
	
	@Test
	public void decryptionNotSupported() throws DecryptionNotSupportedException, DecryptionFailedException{
		exception.expect(DecryptionNotSupportedException.class);
		md5Encryption.decrypt("d41d8cd98f00b204e9800998ecf8427e");
	}
}
