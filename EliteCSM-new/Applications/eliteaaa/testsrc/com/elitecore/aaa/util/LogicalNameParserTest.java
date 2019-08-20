package com.elitecore.aaa.util;

import static org.junit.Assert.assertEquals;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JUnitParamsRunner.class)
public class LogicalNameParserTest {
	
	public Object[][] data_forSuccessCase() {
		return new Object[][] {
				// input		 			key						value				No of tokens
				{"0"					,	"0"					,	""					,	1},
				{"0()"					,	"0"					,	"()"				,	1},
				{"0(base)"				,	"0"					,	"(base)"			,	1},
				{"0((base)),"			,	"0"					,	"((base))"			,	1},
				{"0((zero)),1,2"		,	"1"					,	""					,	3},
				{"0((zero)),1,2"		,	"2"					,	""					,	3},
				
				/// trims white space in key
				{"0.0.0.0-0.0.0.255   (   first 256 entry)," + 
				"0.0.0.1-0.0.0.255(second 256 entry)"		,	"0.0.0.0-0.0.0.255"	,	"(   first 256 entry)",	2},
				
				{"fe80::7627:eaff:fe4a:3b05((ipv6 address))"	,	"fe80::7627:eaff:fe4a:3b05"		,	"((ipv6 address))"	,	1},
				{"[fe80::7627:eaff:fe4a:3b05]((ipv6 address))"	,	"[fe80::7627:eaff:fe4a:3b05]"	,	"((ipv6 address))"	,	1},
				
				{"https://ip:port/Services(sm webservice url)"	,	"https://ip:port/Services"		,	"(sm webservice url)",	1},
				
				{"http://ip:port(AAA Webservice)"			,	"http://ip:port"		,	"(AAA Webservice)",		1},
				
				{"hostname(localhost)"	, "hostname" 		,"(localhost)"	, "1"},
				
				{"hostname(localhost),\n" +
						"hostname1(localhost1)"	, "hostname1" 		,"(localhost1)"	, "2"},
						
				{"hostname\t( localhost)"	, "hostname" 		,"( localhost)"	, "1"},
				
				{"0 1((zero))"		,	"0 1"					,	"((zero))"					,	1},
				
				{"0\n1((zero))"		,	"0\n1"					,	"((zero))"					,	1},
				{"0(zero),(one)"	,	"0"						, 	"(zero)"						,1},
				{"0(zero),0(one)"	,	"0"						, 	"(one)"						,1}
		};
	}
	
	@Test
	@Parameters(method = "data_forSuccessCase")
	public void testParse_ShouldParseTheGivenInput_WhichIsProper(String input, String key, String expectedValue, int expectedNoOfTokens) {
		LogicalNameParser parser = new LogicalNameParser();
		parser.parse(input);
		assertEquals(expectedValue, parser.getValue(key));
		assertEquals(expectedNoOfTokens, parser.getValueToLogicalName().size());
	}
	
	
	public Object[][] data_forFailureCase() {
		return new Object[][] {
				// input		 			key						value			No of tokens
				{"0((zero),"			,	"0"					,	null			,0},
				{"0((zero)),1(one)),2"	,	"1"					,	null			,1},
				{"0((zero)),1(one)),2"	,	"2"					,	null			,1},
		};
	}
	
	@Test
	@Parameters(method = "data_forFailureCase")
	public void testParse_ShouldParseTheGivenInput_WhichTillProperInputIsFound_AndSkipRest(String input, String key, String expectedValue, int expectedNoOfTokens) {
		LogicalNameParser parser = new LogicalNameParser();
		parser.parse(input);
		assertEquals(expectedValue, parser.getValue(key));
		assertEquals(expectedNoOfTokens, parser.getValueToLogicalName().size());
	}
	
	public Object[][] data_forExceptionalCase() {
		return new Object[][] {
				// input		 				key						value			No of tokens
				{",,,"						,	"ANY"				,	null				,	0},
				{"(),,,"					,	""					,	"()"				,	1},
				{"\"\" , , , "				,	""					,	null				,	1},
				{"0 ()"						,	"0"					,	"()"				,	1},
				{"hostname  ( localhost)"	, "hostname" 			,	"( localhost)"			, "1"}
		};
	}
	
	@Test
	@Parameters(method = "data_forExceptionalCase")
	public void testParse_ShouldParseTheGivenInput_WhichIsNotProper(String input, String key, String expectedValue, int expectedNoOfTokens) {
		LogicalNameParser parser = new LogicalNameParser();
		parser.parse(input);
		assertEquals(expectedValue, parser.getValue(key));
		assertEquals(expectedNoOfTokens, parser.getValueToLogicalName().size());
	}
}
