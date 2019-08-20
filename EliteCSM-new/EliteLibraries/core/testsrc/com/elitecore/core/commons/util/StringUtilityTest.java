package com.elitecore.core.commons.util;

import static org.junit.Assert.assertEquals;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * @author Manjil Purohit
 *
 */
@RunWith(JUnitParamsRunner.class)
public class StringUtilityTest {

	@Test
	public void escapeSpecialCharsTest() {
		
		String result = StringUtility.escapeSpecialChars(null, '"');
		assertEquals(null, result);
		
		result = StringUtility.escapeSpecialChars("", '"');
		assertEquals(null, result);
		
		result = StringUtility.escapeSpecialChars("  ", '"');
		assertEquals(null, result);
		
		result = StringUtility.escapeSpecialChars(" escapeSpecialCharacter ", null);
		assertEquals(" escapeSpecialCharacter ", result);
		
		result = StringUtility.escapeSpecialChars("This is a escapeSpecialCharacter method");
		assertEquals("This is a escapeSpecialCharacter method", result);
		
		result = StringUtility.escapeSpecialChars("This is a \"escapeSpecialCharacter\" method");
		assertEquals("This is a \"escapeSpecialCharacter\" method", result);
		
	}
	
	@Test
	@Parameters(method = "dataFor_testMatches")
	public void testMatches(boolean expectedOutput, String sourceString, String pattern){
		boolean result = StringUtility.matchesElitePattern(sourceString, pattern);
		assertEquals("Unable to match  " + "\"" + sourceString + "\"" + " with pattern \"" + pattern + "\"", expectedOutput, result);
	}
	
	
	public static Object[][] dataFor_testMatches(){
		
		return new Object[][]{
				{true, "", ""},
				{false, "", "*c*e"},
				{false, "a\\*", ""},
				
				{false, "a\\*", "*c*e"},
				{false, "\\*c", "*c*e"},
				{false, "c\\*", "*c*e"},
				{false, "\\*e", "*c*e"},
				{false, "a\\*c", "*c*e"},
				{false, "\\*c\\*", "*c*e"},
				{true, "c\\*e", "*c*e"},
				{false, "a\\*e", "*c*e"},
				{false, "\\*a\\*", "*c*e"},
				{false, "a\\*c*", "*c*e"},
				{true, "\\*c\\*e", "*c*e"},
				{true, "a\\*c\\*e", "*c*e"},
				
				{true, "a\\*", "a*"},
				{false, "\\*c", "a*"},
				{false, "c\\*", "a*"},
				{false, "\\*e", "a*"},
				{true, "a\\*c", "a*"},
				{false, "\\*c\\*", "a*"},
				{false, "c\\*e", "a*"},
				{true, "a\\*e", "a*"},
				{false, "\\*a\\*", "a*"},
				{true, "a\\*c*", "a*"},
				{false, "\\*c\\*e", "a*"},
				{true, "a\\*c\\*e", "a*"},
				
				{false, "a\\*", "a*c"},
				{false, "\\*c", "a*c"},
				{false, "c\\*", "a*c"},
				{false, "\\*e", "a*c"},
				{true, "a\\*c", "a*c"},
				{false, "\\*c\\*", "a*c"},
				{false, "c\\*e", "a*c"},
				{false, "a\\*e", "a*c"},
				{false, "\\*a\\*", "a*c"},
				{false, "a\\*c*", "a*c"},
				{false, "\\*c\\*e", "a*c"},
				{false, "a\\*c\\*e", "a*c"},
				
				{false, "a\\*", "*c"},
				{true, "\\*c", "*c"},
				{false, "c\\*", "*c"},
				{false, "\\*e", "*c"},
				{true, "a\\*c", "*c"},
				{false, "\\*c\\*", "*c"},
				{false, "c\\*e", "*c"},
				{false, "a\\*e", "*c"},
				{false, "\\*a\\*", "*c"},
				{false, "a\\*c*", "*c"},
				{false, "\\*c\\*e", "*c"},
				{false, "a\\*c\\*e", "*c"},
				
				{false, "a\\*", "*c*"},
				{true, "\\*c", "*c*"},
				{true, "c\\*", "*c*"},
				{false, "\\*e", "*c*"},
				{true, "a\\*c", "*c*"},
				{true, "\\*c\\*", "*c*"},
				{true, "c\\*e", "*c*"},
				{false, "a\\*e", "*c*"},
				{false, "\\*a\\*", "*c*"},
				{true, "a\\*c*", "*c*"},
				{true, "\\*c\\*e", "*c*"},
				{true, "a\\*c\\*e", "*c*"},
				
				{false, "a\\*", "c*"},
				{false, "\\*c", "c*"},
				{true, "c\\*", "c*"},
				{false, "\\*e", "c*"},
				{false, "a\\*c", "c*"},
				{false, "\\*c\\*", "c*"},
				{true, "c\\*e", "c*"},
				{false, "a\\*e", "c*"},
				{false, "\\*a\\*", "c*"},
				{false, "a\\*c*", "c*"},
				{false, "\\*c\\*e", "c*"},
				{false, "a\\*c\\*e", "c*"},
				
				{false, "a\\*", "c*e"},
				{false, "\\*c", "c*e"},
				{false, "c\\*", "c*e"},
				{false, "\\*e", "c*e"},
				{false, "a\\*c", "c*e"},
				{false, "\\*c\\*", "c*e"},
				{true, "c\\*e", "c*e"},
				{false, "a\\*e", "c*e"},
				{false, "\\*a\\*", "c*e"},
				{false, "a\\*c*", "c*e"},
				{false, "\\*c\\*e", "c*e"},
				{false, "a\\*c\\*e", "c*e"},
				
				{false, "a\\*", "*e"},
				{false, "\\*c", "*e"},
				{false, "c\\*", "*e"},
				{true, "\\*e", "*e"},
				{false, "a\\*c", "*e"},
				{false, "\\*c\\*", "*e"},
				{true, "c\\*e", "*e"},
				{true, "a\\*e", "*e"},
				{false, "\\*a\\*", "*e"},
				{false, "a\\*c*", "*e"},
				{true, "\\*c\\*e", "*e"},
				{true, "a\\*c\\*e", "*e"},
				
				{false, "a\\*", "a*e"},
				{false, "\\*c", "a*e"},
				{false, "c\\*", "a*e"},
				{false, "\\*e", "a*e"},
				{false, "a\\*c", "a*e"},
				{false, "\\*c\\*", "a*e"},
				{false, "c\\*e", "a*e"},
				{true, "a\\*e", "a*e"},
				{false, "\\*a\\*", "a*e"},
				{false, "a\\*c*", "a*e"},
				{false, "\\*c\\*e", "a*e"},
				{true, "a\\*c\\*e", "a*e"},
				
				{true, "a\\*", "*a*"},
				{false, "\\*c", "*a*"},
				{false, "c\\*", "*a*"},
				{false, "\\*e", "*a*"},
				{true, "a\\*c", "*a*"},
				{false, "\\*c\\*", "*a*"},
				{false, "c\\*e", "*a*"},
				{true, "a\\*e", "*a*"},
				{true, "\\*a\\*", "*a*"},
				{true, "a\\*c*", "*a*"},
				{false, "\\*c\\*e", "*a*"},
				{true, "a\\*c\\*e", "*a*"},
				
				{false, "a\\*", "a*c*"},
				{false, "\\*c", "a*c*"},
				{false, "c\\*", "a*c*"},
				{false, "\\*e", "a*c*"},
				{true, "a\\*c", "a*c*"},
				{false, "\\*c\\*", "a*c*"},
				{false, "c\\*e", "a*c*"},
				{false, "a\\*e", "a*c*"},
				{false, "\\*a\\*", "a*c*"},
				{true, "a\\*c*", "a*c*"},
				{false, "\\*c\\*e", "a*c*"},
				{true, "a\\*c\\*e", "a*c*"},
				
				{false, "a\\*", "*c*e"},
				{false, "\\*c", "*c*e"},
				{false, "c\\*", "*c*e"},
				{false, "\\*e", "*c*e"},
				{false, "a\\*c", "*c*e"},
				{false, "\\*c\\*", "*c*e"},
				{true, "c\\*e", "*c*e"},
				{false, "a\\*e", "*c*e"},
				{false, "\\*a\\*", "*c*e"},
				{false, "a\\*c*", "*c*e"},
				{true, "\\*c\\*e", "*c*e"},
				{true, "a\\*c\\*e", "*c*e"},
				
				{false, "a\\*", "a*c*e"},
				{false, "\\*c", "a*c*e"},
				{false, "c\\*", "a*c*e"},
				{false, "\\*e", "a*c*e"},
				{false, "a\\*c", "a*c*e"},
				{false, "\\*c\\*", "a*c*e"},
				{false, "c\\*e", "a*c*e"},
				{false, "a\\*e", "a*c*e"},
				{false, "\\*a\\*", "a*c*e"},
				{false, "a\\*c*", "a*c*e"},
				{false, "\\*c\\*e", "a*c*e"},
				{true, "a\\*c\\*e", "a*c*e"},
				
		};
	}

}
