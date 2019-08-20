package com.elitecore.passwordutil.base16;

import static org.junit.Assert.assertEquals;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.elitecore.passwordutil.DecryptionNotSupportedException;

@RunWith(JUnitParamsRunner.class)
public class Base16CryptEncryptionTest {
	public static Base16CryptEncryption base16CryptEncryption ;
	
	@BeforeClass
	public static void setup(){
		base16CryptEncryption = new Base16CryptEncryption();
	}
	
	/**
	 * Inputs and Outputs taken for unit testing is from RFC-4648
	 */
	public Object[][] dataFor_TestCrypt_MustReturnBase16EncodedString(){
		
		return new Object[][]{
				{"",""},
				{"f","66"},
				{"fo","666F"},
				{"foo","666F6F"},
				{"foob","666F6F62"},
				{"fooba","666F6F6261"},
				{"foobar","666F6F626172"}
		};
	}
	
	@Test
	@Parameters(method = "dataFor_TestCrypt_MustReturnBase16EncodedString")
	public void testCrypt_MustReturnBase16EncodedString(String plainText, String encryptedText){
		String cryptedStr = base16CryptEncryption.crypt(plainText);
		assertEquals(cryptedStr, encryptedText);
	}
	
	/**
	 * Inputs and Outputs taken for unit testing is from RFC-4648
	 */
	public Object[][] dataFor_TestDecrypt_MustReturnBase16DecodedString(){
		return new Object[][]{
				{"",""},
				{"66","f"},
				{"666F","fo"},
				{"666F6F","foo"},
				{"666F6F62","foob"},
				{"666F6F6261","fooba"},
				{"666F6F626172","foobar"}
		};
	}
	
	@Test
	@Parameters(method = "dataFor_TestDecrypt_MustReturnBase16DecodedString")
	public void testDecrypt_MustReturnBase16DecodedString(String encryptedText, String plainText) throws DecryptionNotSupportedException{
		String decryptedStr = base16CryptEncryption.decrypt(encryptedText);
		assertEquals(decryptedStr, plainText);
	}
	
	public Object[][] dataFor_TestMatches_MatchPlainTextAndEncryptedText(){
		return new Object[][]{
				{"f","66",true},
				{"f","68",false}
		};
	}
	
	@Test
	@Parameters(method = "dataFor_TestMatches_MatchPlainTextAndEncryptedText")
	public void testMatches_MatchPlainTextAndEncryptedText(String plainText, String encyrptedText, boolean matcheFlag){
		boolean passwordMatched = base16CryptEncryption.matches(encyrptedText, plainText);
		assertEquals(passwordMatched, matcheFlag);
	}
	
}
