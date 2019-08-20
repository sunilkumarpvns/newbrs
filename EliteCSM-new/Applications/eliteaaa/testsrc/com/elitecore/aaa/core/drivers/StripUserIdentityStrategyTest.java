package com.elitecore.aaa.core.drivers;

import static junitparams.JUnitParamsRunner.$;
import static org.junit.Assert.assertEquals;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.elitecore.aaa.core.drivers.StripUserIdentityStrategy.NoneStrategy;
import com.elitecore.aaa.core.drivers.StripUserIdentityStrategy.PrefixStrategy;
import com.elitecore.aaa.core.drivers.StripUserIdentityStrategy.SuffixStrategy;

@RunWith(JUnitParamsRunner.class)
public class StripUserIdentityStrategyTest {
	private static final String SOME_NON_NULL_STRING = "abc";
	
	@Test
	public void testGet_ShouldReturnNoneStrategy_IfIdentifierPassedIsNull() {
		assertEquals("None strategy should be returned if identifier is null.", 
				NoneStrategy.class, StripUserIdentityStrategy.get(null).getClass());
	}
	
	@Test
	@Parameters(value = {"", "abc", "unknown"})
	public void testGet_ShouldReturnNoneStrategy_IfIdentifierIsUnknown(String unknownIdentifier) {
		assertEquals("None Strategy should be returned if identifier is unknown.", 
				NoneStrategy.class, StripUserIdentityStrategy.get(unknownIdentifier).getClass());
	}
	
	@Test
	@Parameters(method = "dataFor_testGet_ShouldReturnStrategyBasedOnTheIdentifierProvided")
	public void testGet_ShouldReturnStrategyBasedOnTheIdentifierProvided(String identifier, Class<?> expectedStrategyClass) {
		assertEquals(expectedStrategyClass, StripUserIdentityStrategy.get(identifier).getClass());
	}
	
	public Object[] dataFor_testGet_ShouldReturnStrategyBasedOnTheIdentifierProvided() {
		return $(
				$("none", 	NoneStrategy.class),
				$("prefix", PrefixStrategy.class),
				$("suffix", SuffixStrategy.class)
		);
	}
	
	@Test
	public void testNone_ShouldReturnNoneStrategy() {
		assertEquals(NoneStrategy.class, StripUserIdentityStrategy.none().getClass());
	}
	
	@Test
	@Parameters(method = "dataFor_testNoneStrategy_ShouldRetrunTheIdentifierUnchanged")
	public void testNoneStrategy_ShouldRetrunTheIdentifierUnchanged(String identifier, String separator, String expectedOutput) {
		assertEquals(expectedOutput, new NoneStrategy().apply(identifier, separator));
	}
	
	public Object[] dataFor_testNoneStrategy_ShouldRetrunTheIdentifierUnchanged() {
		return $(
				//identity				separator				expectedOutput
				$("",					null,					""),
				$("",					"",						""),
				$("",					"@",					""),
				$("",					"dont care",			""),
				$(SOME_NON_NULL_STRING,	SOME_NON_NULL_STRING,	SOME_NON_NULL_STRING)
		);
	}
	
	@Test
	@Parameters(method = "dataFor_testPrefixStrategy_ShouldStripPrefix_IfSeparatorFoundInIdentifier")
	public void testPrefixStrategy_ShouldStripPrefix_IfSeparatorFoundInIdentifier(String identifier, String separator, String expectedOutput) {
		assertEquals(expectedOutput, new PrefixStrategy().apply(identifier, separator));
	}
	
	public Object[] dataFor_testPrefixStrategy_ShouldStripPrefix_IfSeparatorFoundInIdentifier() {
		return $(
				//identity				separator				expectedOutput
				$("",					"@",					""),
				$("abc",				"@",					"abc"),
				$("abc@",				"@",					""),
				$("abc@cde",			"@",					"cde"),
				$("abc@cde@xyz",		"@",					"cde@xyz")
		);
	}
	
	@Test
	@Parameters(method = "dataFor_testSuffixStrategy_ShouldStripSuffix_IfSeparatorFoundInIdentifier")
	public void testSuffixStrategy_ShouldStripSuffix_IfSeparatorFoundInIdentifier(String identifier, String separator, String expectedOutput) {
		assertEquals(expectedOutput, new SuffixStrategy().apply(identifier, separator));
	}
	
	public Object[] dataFor_testSuffixStrategy_ShouldStripSuffix_IfSeparatorFoundInIdentifier() {
		return $(
				//identity				separator				expectedOutput
				$("",					"@",					""),
				$("abc",				"@",					"abc"),
				$("abc@",				"@",					"abc"),
				$("abc@cde",			"@",					"abc"),
				$("abc@cde@xyz",		"@",					"abc")
		);
	}
}
