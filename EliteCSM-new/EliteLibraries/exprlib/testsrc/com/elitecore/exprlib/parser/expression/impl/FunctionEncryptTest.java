package com.elitecore.exprlib.parser.expression.impl;


import static org.junit.Assert.assertEquals;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.elitecore.exprlib.compiler.Compiler;
import com.elitecore.exprlib.parser.exception.IllegalArgumentException;
import com.elitecore.exprlib.parser.exception.InvalidExpressionException;
import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;
import com.elitecore.exprlib.parser.expression.Expression;
import com.elitecore.exprlib.parser.expression.ValueProvider;

@RunWith(JUnitParamsRunner.class)
public class FunctionEncryptTest {

	private Compiler compiler;

	@Mock
	private ValueProvider valueProvider;
	
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	
	@Before
	public void setup() {
		MockitoAnnotations.initMocks(this);
		this.compiler = Compiler.getDefaultCompiler();
	}

	public Object[][] dataFor_TestGetStringValue_ShouldReturnMd5Digest_WhenFirstParameterIsMd5AndSecondParameterIsAnyString(){
		
		return new Object[][]{
				{"encrypt(\"Md5\",\"\")","d41d8cd98f00b204e9800998ecf8427e"},
				{"encrypt(\"MD5\",\"a\")","0cc175b9c0f1b6a831c399e269772661"},
				{"encrypt(\"md5\",\"abc\")","900150983cd24fb0d6963f7d28e17f72"},
				{"encrypt(\"md5\",\"message digest\")","f96b697d7cb7938d525a2f31aaf161d0"},
				{"encrypt(\"md5\",\"abcdefghijklmnopqrstuvwxyz\")","c3fcd3d76192e4007dfb496cca67e13b"},
				{"encrypt(\"md5\",\"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789\")","d174ab98d277d9f5a5611c2c9f419d9f"},
				{"encrypt(\"md5\",\"12345678901234567890123456789012345678901234567890123456789012345678901234567890\")",
					"57edf4a22be3c955ac49da2e2107b67a"}
				
		};
	}
	
	@Test
	@Parameters(method="dataFor_TestGetStringValue_ShouldReturnMd5Digest_WhenFirstParameterIsMd5AndSecondParameterIsAnyString")
	public void testGetStringValue_ShouldReturnMd5Digest_WhenFirstParameterIsMd5AndSecondParameterIsAnyString(
			String function, String expected) throws InvalidExpressionException,
			InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException{
		Expression  expression = compiler.parseExpression(function);
		String value = expression.getStringValue(valueProvider);
		assertEquals(expected,value);
	}
	
	
	public Object[][] dataFor_TestGetStringValue_ShouldReturnShaDigest_WhenFirstParameterIsShaAndSecondParameterIsAnyString(){
		
		return new Object[][]{
				{"encrypt(\"sha\",\"abc\")","a9993e364706816aba3e25717850c26c9cd0d89d"},
				{"encrypt(\"sha\",\"abcdbcdecdefdefgefghfghighijhijkijkljklmklmnlmnomnopnopq\")","84983e441c3bd26ebaae4aa1f95129e5e54670f1"},
				
				{"encrypt(\"sha\",\"" + getStringFromChar("0123456701234567012345670123456701234567012345670123456701234567", 10) + "\")",
					"dea356a2cddd90c7a7ecedc5ebb563934f460452"},

				//{"encrypt(\"sha\",\"" + getStringFromChar("a", 1000000) + "\")","34aa973cd4c4daa4f61eeb2bdbad27316534016f"},

				{"encrypt(\"sha\",\"\u005e\")","5e6f80a34a9798cafc6a5db96cc57ba4c4db59c2"}, 
				
//				Have Tested for characters whose hex representation exceed 7f, But that 
//				requires converting the input to a byte array and passing it as such which isn't possible here. 
		};
	}
	private String getStringFromChar(String a, int times) {
		StringBuilder builder = new StringBuilder();
		for (int i=0; i<times; i++) {
			builder.append(a);
		}
		return builder.toString();
	}

	
	@Test
	@Parameters(method="dataFor_TestGetStringValue_ShouldReturnShaDigest_WhenFirstParameterIsShaAndSecondParameterIsAnyString")
	public void testGetStringValue_ShouldReturnShaDigest_WhenFirstParameterIsShaAndSecondParameterIsAnyString(
			String function, String expected) throws InvalidExpressionException,
			InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException{
		Expression  expression = compiler.parseExpression(function);
		String value = expression.getStringValue(valueProvider);
		assertEquals(expected,value);
	}

	
	public Object[][] dataFor_TestGetStringValue_ShouldReturnMessageDigest_WhenAnIdentifierIsPassedInPlaceOfPlainTextMessage(){
		return new Object[][]{
				{"encrypt(\"sha\",message)","a9993e364706816aba3e25717850c26c9cd0d89d"},
				{"encrypt(\"md5\",message)","900150983cd24fb0d6963f7d28e17f72"}
		};
	}
	
	@Test
	@Parameters(method="dataFor_TestGetStringValue_ShouldReturnMessageDigest_WhenAnIdentifierIsPassedInPlaceOfPlainTextMessage")
	public void testGetStringValue_ShouldReturnMessageDigest_WhenAnIdentifierIsPassedInPlaceOfPlainTextMessage(
			String function, String expected) throws InvalidExpressionException, InvalidTypeCastException, 
			IllegalArgumentException, MissingIdentifierException{
		Expression expression = compiler.parseExpression(function);
		Mockito.when(valueProvider.getStringValue("message")).thenReturn("abc");
		String value = expression.getStringValue(valueProvider);
		assertEquals(expected, value);
		
	}
	
	
	public Object[][] getDataFor_TestGetStringValue_ShouldThrowIllegalArgumentException_WhenNumberOfArgumentsIsLessThanTwoOrMoreThanThree(){
		return new Object[][]{
				{"encrypt(\"md5\")"},
				{"encrypt(\"md5\",\"randomString\",\"key\",\"extraString\")"}
		};
	}

	@Test
	@Parameters(method="getDataFor_TestGetStringValue_ShouldThrowIllegalArgumentException_WhenNumberOfArgumentsIsLessThanTwoOrMoreThanThree")
	public void testGetStringValue_ShouldThrowIllegalArgumentException_WhenNumberOfArgumentsIsLessThanTwoOrMoreThanThree(
			String function) throws InvalidExpressionException, InvalidTypeCastException,
			IllegalArgumentException, MissingIdentifierException{
		Expression expression = compiler.parseExpression(function);
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Number of parameters mismatch, ENCRYPT function has 2 or 3 arguments" +
				" 1)Encryption Algorithm Name   2)Encryption Key   3)String to be encrypted");
		expression.getStringValue(valueProvider);
	}
	
	
	@Test
	public void testGetLongValue_ShouldThrowInvalidTypeCastException_WhenAnyArgumentIsPassed() throws InvalidExpressionException,
			InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException{
		Expression expression = compiler.parseExpression("encrypt(\"md5\",\"abc\")");
		exception.expect(InvalidTypeCastException.class);
		exception.expectMessage("Cannot cast a String to Integer");
		expression.getLongValue(valueProvider);
	}
	
	
	@Test
	public void testGetStringValue_ShouldThrowIllegalArgument_WhenFirstArgumentIsNottMd5OrRsaOrSha(
			) throws InvalidExpressionException, InvalidTypeCastException, IllegalArgumentException, MissingIdentifierException{
		Expression expression = compiler.parseExpression("encrypt(\"abc\",\"md5\")");
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Invalid encryption algorithm specified");
		expression.getStringValue(valueProvider);
	}
}

