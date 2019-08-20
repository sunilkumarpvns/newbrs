package com.elitecore.passwordutil.base32;

import static org.junit.Assert.assertEquals;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import com.elitecore.passwordutil.DecryptionNotSupportedException;

@RunWith(JUnitParamsRunner.class)
public class Base32CryptEncryptionTest {
	
	public static Base32CryptEncryption base32CryptEncryption ;
	
	@BeforeClass
	public static void setup(){
		base32CryptEncryption = new Base32CryptEncryption();
	}
	
	/**
	 * Following inputs and outputs are taken RFC-4648
	 */
	public Object[][] dataFor_TestCrypt_MustReturnBase32EncodedString(){
		return new Object[][]{
				{"",""},
				{"f","MY======"},
				{"fo","MZXQ===="},
				{"foo","MZXW6==="},
				{"foob","MZXW6YQ="},
				{"fooba","MZXW6YTB"},
				{"foobar","MZXW6YTBOI======"}
		};
	}
	
	@Test
	@Parameters(method = "dataFor_TestCrypt_MustReturnBase32EncodedString")
	public void testCrypt_MustReturnBase32EncodedString(String plainText, String encryptedText){
		String cryptedStr = base32CryptEncryption.crypt(plainText);
		assertEquals(cryptedStr, encryptedText);
	}
	
	/**
	 * Following inputs and outputs are taken RFC-4648
	 */
	public Object[][] dataFor_MustReturnBase32DecodedString(){
		return new Object[][]{
				{"",""},
				{"MY======","f"},
				{"MZXQ====","fo"},
				{"MZXW6===","foo"},
				{"MZXW6YQ=","foob"},
				{"MZXW6YTB","fooba"},
				{"MZXW6YTBOI======","foobar"}
		};
	}
	
	@Test
	@Parameters(method = "dataFor_MustReturnBase32DecodedString")
	public void testDecrypt_MustReturnBase32DecodedString(String encryptedText, String plainText) throws DecryptionNotSupportedException{
		String decryptedStr = base32CryptEncryption.decrypt(encryptedText);
		assertEquals(decryptedStr, plainText);
	}
	
	public Object[][] dataFor_TestMatches_MatchPlainTextAndEncryptedText(){
		return new Object[][]{
				{"f","MY======",true},
				{"f","MY====",false}
		};
	}
	
	@Test
	@Parameters(method = "dataFor_TestMatches_MatchPlainTextAndEncryptedText")
	public void testMatches_MatchPlainTextAndEncryptedText(String plainText, String encyrptedText, boolean matcheFlag){
		boolean passwordMatched = base32CryptEncryption.matches(encyrptedText, plainText);
		assertEquals(passwordMatched, matcheFlag);
	}
	
}
