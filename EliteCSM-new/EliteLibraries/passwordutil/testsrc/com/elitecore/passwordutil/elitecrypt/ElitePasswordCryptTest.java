package com.elitecore.passwordutil.elitecrypt;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.elitecore.passwordutil.DecryptionFailedException;
import com.elitecore.passwordutil.DecryptionNotSupportedException;
import com.elitecore.passwordutil.EncryptionFailedException;
import com.elitecore.passwordutil.NoSuchEncryptionException;
import com.elitecore.passwordutil.PasswordEncryption;

/**
 * 
 * @author narendra.pathai
 *
 */
@RunWith(JUnitParamsRunner.class)
public class ElitePasswordCryptTest {

	private static final char ASCII_START = '\0';
	private static final char ASCII_END = '\u007f';
	
	
	public static Collection<Object[]> data(){
		return Arrays.asList(new Object[][]{
				{"narendra~!@#$%^&*()_+|<>?:"},
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
		});
	}
	
	@Test
	@Parameters(method = "data")
	public void testFromPlainTextToEncryptedToPlainText(String plainText) throws NoSuchEncryptionException, DecryptionNotSupportedException, EncryptionFailedException, DecryptionFailedException{
		String encrypted = PasswordEncryption.getInstance().crypt(plainText, PasswordEncryption.ELITE_PASSWORD_CRYPT);
		String decrypted = PasswordEncryption.getInstance().decrypt(encrypted, PasswordEncryption.ELITE_PASSWORD_CRYPT);
		assertEquals("The plaintext formed after decrypting the encrypted text is not same as original plaintext", plainText, decrypted);
		System.out.println(decrypted + "=" + encrypted);
	}

	
	@Test
	@Parameters(method = "data")
	public void testMatches(String plainText) throws NoSuchEncryptionException, EncryptionFailedException{
		String encrypted = PasswordEncryption.getInstance().crypt(plainText, PasswordEncryption.ELITE_PASSWORD_CRYPT);
		
		//applying stimulus
		boolean result = PasswordEncryption.getInstance().matches(encrypted, plainText, PasswordEncryption.ELITE_PASSWORD_CRYPT);
		
		//assert
		assertTrue("Matches was unable to match encrypted and plain text password", result);
	}
	
	@Test
	@Parameters(method = "data")
	public void testEncryptedDataIsAllAscii(String plainText) throws NoSuchEncryptionException, EncryptionFailedException{
		//apply stimulus
		String encrypted = PasswordEncryption.getInstance().crypt(plainText, PasswordEncryption.ELITE_PASSWORD_CRYPT);
		
		//assert
		assertTrue("The encrypted text for plaintext: " + plainText + " contians some non-ascii characters", isAllAscii(encrypted));
	}
	
	
	@Test
	@Parameters(method = "data")
	public void testLengthOfEncryptedStringGreaterThanPlainText(String plainText) throws NoSuchEncryptionException, EncryptionFailedException{
		//apply stimulus
		String encryptedString = PasswordEncryption.getInstance().crypt(plainText, PasswordEncryption.ELITE_PASSWORD_CRYPT);
		
		//assert
		assertTrue("The length of encrypted string is lower than plaintext", encryptedString.length() >= plainText.length());
	}
	
	public static boolean isAllAscii(String string){
		if(string == null)
			return false;
		
		boolean result = true;
		for(Character c : string.toCharArray()){
			 result &= isInAsciiRange(c);
		}
		
		return result;
	}
	
	private static boolean isInAsciiRange(Character c){
		return ASCII_START <= c && c <= ASCII_END;
	}
	
}
