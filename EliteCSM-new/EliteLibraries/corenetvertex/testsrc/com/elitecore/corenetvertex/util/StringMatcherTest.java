package com.elitecore.corenetvertex.util;

import static org.junit.Assert.*;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 
 * @author Chetan.Sankhala
 */
@RunWith(JUnitParamsRunner.class)
@Ignore
public class StringMatcherTest {

	

	public Object[][] dataProviderFor_test_matches_should_return_expected_result_when_pattern_matches() {
		
		final String SUFFIX_VALUE = "*195";

		final String PREFIX_VALUE = "195*";
		final String ASTERISK = "*";
		
		
		final String string = "*797";
		return new Object[][] {
				
				{
					"195", SUFFIX_VALUE, true
				},
				{
					"21951123195", SUFFIX_VALUE, true 
				},
				{
					"ABC1195", SUFFIX_VALUE, true
				},
				{
					"%^&**195", SUFFIX_VALUE, true
				},
				{
					" 195", SUFFIX_VALUE, true
				},
				{
					"abc 195", SUFFIX_VALUE, true
				},
				{
					"", SUFFIX_VALUE, false
				},
				{
					"195 ", SUFFIX_VALUE, false
				},
				{
					"195\t", SUFFIX_VALUE, false
				},

				//prefix based
				{
					"195", PREFIX_VALUE, true
				},
				{
					"195ABC", PREFIX_VALUE, true
				},
				{
					"195%^&", PREFIX_VALUE, true
				},
				{
					"195 ", PREFIX_VALUE, true
				},
				{
					"195 abc", PREFIX_VALUE, true
				},
				{
					"", PREFIX_VALUE, false
				},
				{
					" 195", PREFIX_VALUE, false
				},
				{
					"\t195", PREFIX_VALUE, false
				},
				
				// *
				
				{
					"195", ASTERISK, true
				},
				{
					"", ASTERISK, true
				},
				{
					"\\*\\*", ASTERISK, true 
				},
				{
					"**^*&^&", ASTERISK, true  
				},
				{
					"*abcd", ASTERISK, true 
				},
				{
					"abcd*", ASTERISK, true  
				},
				{
					"?*", ASTERISK, true 
				},
				
				// **
				{
					"195", "**", true
				},
				{
					"", "**", true
				},
				{
					"**", "**", true 
				},
				{
					"**^*&^&", "**", true  
				},
				{
					"*abcd", "**", true 
				},
				{
					"abcd*", "**", true 
				},
				{
					"?*", "**", true
				},
				{
					"97979797", "*97", true
				},
				{
					"97979797", "*797", true
				},
				{
					"9797979797", "*797", true
				},
				{
					"789789789789", "*789", true
				},
				{
					"chetan@elite.com", "*@elite.com", true
				},
				{
					"chetan@elite.com", "?*@elite.com", true
				},
				{
					"chetan.sir@elite.com", "chetan.???@elite.com", true
				},
				{
					"@elite.com", "?*@elite.com", false
				},
				{
					"@ElitE.com", "*@elite.com", false
				},
				{
					"79797979", "797*", true
				},
				{
					"7979797979797", "79*", true
				},
				{
					"9797979797", "979*", false
				}
		};
	}
	
	
	@Test
	@Parameters(method="dataProviderFor_test_matches_should_return_expected_result_when_pattern_matches")
	public void test_matches_should_return_expected_result_when_pattern_matches(String sourceString, String pattern, boolean result) {
		assertEquals(result, StringMatcher.matches(sourceString, pattern));
	}
}
