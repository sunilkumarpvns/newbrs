package com.elitecore.passwordutil.base16;

import static org.junit.Assert.assertEquals;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import com.elitecore.passwordutil.DecryptionNotSupportedException;

@RunWith(JUnitParamsRunner.class)
public class Base16CoderTest {
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	/**
	 * Inputs and Outputs taken for unit testing is from RFC-4648
	 */
	public Object[][] dataFor_TestEncodeString_MustReturnEncryptedText(){
		
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
	@Parameters(method = "dataFor_TestEncodeString_MustReturnEncryptedText")
	public void testEncodeString_MustReturnEncryptedText(String plainText, String encryptedText){
		String encodeString = Base16Coder.encodeString(plainText);
		assertEquals(encodeString, encryptedText);
	}
	
	@Test
	public void testEncodeString_ShouldThrowNullPointerException_WhenArgumentIsNull(){
		exception.expect(NullPointerException.class);
		Base16Coder.encodeString(null);
	}
	
	/**
	 * Inputs and Outputs taken for unit testing is from RFC-4648
	 */
	public Object[][] dataFor_TestDecodeString_MustReturnDecryptedText(){
		
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
	@Parameters(method = "dataFor_TestDecodeString_MustReturnDecryptedText")
	public void testDecodeString_MustReturnDecryptedText(String encryptedText, String plainText) throws DecryptionNotSupportedException{
		String decodeString = Base16Coder.decodeString(encryptedText);
		assertEquals(decodeString,plainText);
	}
	
	@Test
	public void testDecodeString_ShouldThrowDecryptionNotSupportedException_WhenEncryptedTextLengthIsNotBaseOf8() throws DecryptionNotSupportedException{
		exception.expect(DecryptionNotSupportedException.class);
		exception.expectMessage("Length of Base16 encoded input string is not a multiple of 8.");
		Base16Coder.decodeString("666F6F6");
	}
	
	@Test
	public void testDecodeString_ShouldThrowNullPointerException_WhenArgumentIsNull() throws DecryptionNotSupportedException{
		exception.expect(NullPointerException.class);
		Base16Coder.decodeString(null);
	}
	
}
