package com.elitecore.passwordutil.base32;

import static org.junit.Assert.assertEquals;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import com.elitecore.passwordutil.DecryptionNotSupportedException;

@RunWith(JUnitParamsRunner.class)
public class Base32CoderTest {
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	/**
	 * Inputs and Outputs taken for unit testing is from RFC-4648
	 */
	public Object[][] dataFor_TestEncodeString_MustReturnEncryptedText(){
		
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
	@Parameters(method = "dataFor_TestEncodeString_MustReturnEncryptedText")
	public void testEncodeString_MustReturnEncryptedText(String plainText, String encryptedText){
		String encodeString = Base32Coder.encodeString(plainText);
		assertEquals(encodeString, encryptedText);
	}
	
	@Test
	public void testEncodeString_ShouldThrowNullPointerException_WhenArgumentIsNull(){
		exception.expect(NullPointerException.class);
		Base32Coder.encodeString(null);
	}
	
	/**
	 * Following inputs and outputs are taken RFC-4648
	 */
	public Object[][] dataFor_TestDecodeString_MustReturnDecryptedText(){
		
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
	@Parameters(method = "dataFor_TestDecodeString_MustReturnDecryptedText")
	public void testDecodeString_MustReturnDecryptedText(String encryptedText, String plainText) throws DecryptionNotSupportedException{
		String decodeString = Base32Coder.decodeString(encryptedText);
		assertEquals(decodeString,plainText);
	}
	
	@Test
	public void testDecodeString_ShouldThrowDecryptionNotSupportedException_WhenEncryptedTextLengthIsNotBaseOf8() throws DecryptionNotSupportedException{
		exception.expect(DecryptionNotSupportedException.class);
		exception.expectMessage("Length of Base32 encoded input string is not a multiple of 8.");
		Base32Coder.decodeString("MZXW6YQ");
	}
	
	@Test
	public void testDecodeString_ShouldThrowNullPointerException_WhenArgumentIsNull() throws DecryptionNotSupportedException{
		exception.expect(NullPointerException.class);
		Base32Coder.decodeString(null);
	}
	
}
