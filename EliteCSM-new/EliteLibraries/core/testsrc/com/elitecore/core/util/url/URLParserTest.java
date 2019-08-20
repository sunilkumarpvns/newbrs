package com.elitecore.core.util.url;

import static org.junit.Assert.assertEquals;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

@RunWith(JUnitParamsRunner.class)
public class URLParserTest {

	@Rule public ExpectedException expectedException = ExpectedException.none();
	
	private static UrlDataWrapper $(String host, int port) {
		return new UrlDataWrapper().h(host).p(port);
	}
	
	private static UrlDataWrapper $() {
		return new UrlDataWrapper();
	}
	
	public static Object[][] dataFor_testParse_ShouldParseBlankValuesInServiceAddress_WhenServiceAddressContainsAnyBlankValues() {
		return new Object[][] {
				{"http://:aaa@", $().pl("http").un("").pw("aaa")},
				{"10.106.1.1:86/", $("10.106.1.1", 86).r("")},
				{"aaa@:86", $("", 86).un("aaa")},
				{"aaa:aaa@:86", $("", 86).un("aaa").pw("aaa")},
				{"elitecore.com/", $().h("elitecore.com").r("")},
				{"http://aaa:aaa@:86/aaaResources", $("", 86).pl("http").un("aaa").pw("aaa").r("aaaResources")},
				{"http://:aaa@elitecore.com:86", $("elitecore.com", 86).pl("http").un("").pw("aaa")},
				{"http://aaa:aaa@:86", $("", 86).pl("http").un("aaa").pw("aaa")},
				{"http://aaa:aaa@elitecore.com:86/", $("elitecore.com", 86).pl("http").un("aaa").pw("aaa").r("")},
				{"http://:aaa@elitecore.com:86/aaaResources", $("elitecore.com", 86).pl("http").un("").pw("aaa").r("aaaResources")},
		};
	}
	
	@Test
	@Parameters(method = "dataFor_testParse_ShouldParseBlankValuesInServiceAddress_WhenServiceAddressContainsAnyBlankValues")
	public void testParse_ShouldParseBlankValuesInServiceAddress_WhenServiceAddressContainsAnyBlankValues(String serviceAddress, URLData expectedUrlData) throws InvalidURLException {
		assertEquals(expectedUrlData, URLParser.parse(serviceAddress));
	}
	
	public static Object[][] dataFor_testParse_ShouldConsiderItAsAValidHostEntry_WhenServiceAddressContainsInvalidValueAsHostEntry() {
		return new Object[][] {
				{"elitecore..com:86", $("elitecore..com", 86)},
				{"10..106.1.1:86", $("10..106.1.1", 86)},
				{"10106.1.1:86", $("10106.1.1", 86)},
				{"10.106.1:86", $("10.106.1", 86)},
		};
	}
	
	@Test
	@Parameters(method = "dataFor_testParse_ShouldConsiderItAsAValidHostEntry_WhenServiceAddressContainsInvalidValueAsHostEntry")
	public void testParse_ShouldConsiderItAsAValidHostEntry_WhenServiceAddressContainsInvalidValueAsHostEntry(String serviceAddress, URLData expectedUrlData) throws InvalidURLException {
		assertEquals(expectedUrlData, URLParser.parse(serviceAddress));
	}
	
	public static Object[][] dataFor_testParse_ShouldGenerateProperUrlData_WhenProperServiceAddressIsPassed() {
		return new Object[][] {
				//PROTOCOL
				{"http://", $().pl("http")},
				
				//USERNAME
				{"aaa@", $().un("aaa")},
				
				//PROTOCOL	USERNAME
				{"http://aaa@", $().pl("http").un("aaa")},
				
				//USERNAME	PASSWORD
				{"aaa:aaa@", $().un("aaa").pw("aaa")},
				
				//PROTOCOL	USERNAME	PASSWORD
				{"http://aaa:aaa@", $().pl("http").un("aaa").pw("aaa")},
				
				//HOST
				{"elitecore.com", $().h("elitecore.com")},

				//HOST AND PORT
				{"10.106.1.1:0", $("10.106.1.1", 0)},
				{"10.106.1.1:86", $("10.106.1.1", 86)},
				{"10.106.1.1:65535", $("10.106.1.1", 65535)},
				{"elitecore.com:86", $("elitecore.com", 86)},

				//HOST AND PORT IPv6
				{"[::0]:86", $("::0", 86)},
				
				//host	port resource
				{"10.106.1.1:86/resources", $("10.106.1.1", 86).r("resources")},

				//username	host	port
				{"aaa@elitecore.com:86", $("elitecore.com", 86).un("aaa")},
				
				//username	password	host	port
				{"aaa:aaa@elitecore.com:86", $("elitecore.com", 86).un("aaa").pw("aaa")},
				
				//protocol	username	password	host	port
				{"http://aaa:aaa@elitecore.com:86", $("elitecore.com", 86).pl("http").un("aaa").pw("aaa")},
				{"http://aaa@elitecore.com:86", $("elitecore.com", 86).pl("http").un("aaa")},
				
				//protocol	username	password	host	port	resource
				{"http://aaa:aaa@elitecore.com:86/aaaResources", $("elitecore.com", 86).pl("http").un("aaa").pw("aaa").r("aaaResources")},
		};
	}
	
	@Test
	@Parameters(method = "dataFor_testParse_ShouldGenerateProperUrlData_WhenProperServiceAddressIsPassed")
	public void testParse_ShouldGenerateProperUrlData_WhenProperServiceAddressIsPassed(String serviceAddress, URLData expectedUrlData) throws InvalidURLException {
		assertEquals(expectedUrlData, URLParser.parse(serviceAddress));
	}
	
	public static Object[][] dataFor_testParse_ShouldParseValueInServiceAddressFromHostName_ButConsideringAllValuesAsResource() {
		return new Object[][] {
				{"://elitecore.com/", $().r("elitecore.com/")}, // h
				{"://elitecore.com", $().r("elitecore.com")},	// h
				{"://aaa@elitecore.com:86", $().r("aaa@elitecore.com:86")},	//	un, h, p
		};
	}
	
	@Test
	@Parameters(method = "dataFor_testParse_ShouldParseValueInServiceAddressFromHostName_ButConsideringAllValuesAsResource")
	public void testParse_ShouldParseValueInServiceAddressFromHostName_ButConsideringAllValuesAsResource(String serviceAddress, URLData expectedUrlData) throws InvalidURLException {
		assertEquals(expectedUrlData, URLParser.parse(serviceAddress));
	}
	
	public static Object[][] dataFor_testParse_ShouldNotIgnoreSpaceAppliedInUrlData_WhenServiceAddressContainsSpace() {
		return new Object[][] {
				{"http ://", $().pl("http ")},
				{"aaa @", $().un("aaa ")},
				{"aaa : aaa @", $().un("aaa ").pw(" aaa ")},
				{"http:// :aaa@", $().pl("http").un(" ").pw("aaa")},
				{"10.106.1.1 :1000", $("10.106.1.1 ", 1000)},
		};
	}
	
	@Test
	@Parameters(method = "dataFor_testParse_ShouldNotIgnoreSpaceAppliedInUrlData_WhenServiceAddressContainsSpace")
	public void testParse_ShouldNotIgnoreSpaceAppliedInUrlData_WhenServiceAddressContainsSpace(String serviceAddress, URLData expectedUrlData) throws InvalidURLException {
		assertEquals(expectedUrlData, URLParser.parse(serviceAddress));
	}
	
	public static Object[][] dataFor_testParse_ShouldThrowInvalidURLException() {
		return new Object[][] {
				{null, "URL data can not be null"},
				{"", "URL data can not be null"},
				
				{"10.106.1.10: 1000", "Invalid port:  1000 in URL: 10.106.1.10: 1000"},
				{"10.106.1.10:abc", "Invalid port: abc in URL: 10.106.1.10:abc"},
				{"10.106.1.10:-1", "Negative port in URL: 10.106.1.10:-1"},
				{"10.106.1.10:65536", "Port out of range in URL: 10.106.1.10:65536"},
				
				{"http:", "Invalid port: "},
				{"http: //", "Invalid port:   in URL: http: //"},

				{"http:/", "Invalid Syntax for URL: http:/"},
				{"http:/ /", "Invalid Syntax for URL: http:/ /"},
				
				{"http://:@:86/aaaResources", "Invalid Syntax for URL: http://:@:86/aaaResources"},  
				{"http://aaa:@elitecore.com:86/aaaResources", "Invalid Syntax for URL: http://aaa:@elitecore.com:86/aaaResources"},
				{"http://[00:a2", "Invalid Syntax for URL: http://[00:a2"},
		};
	}
	
	@Test
	@Parameters(method = "dataFor_testParse_ShouldThrowInvalidURLException")
	public void testParse_ShouldThrowInvalidURLException(String serviceAddress, String expectedMessage) throws InvalidURLException {
		expectedException.expect(InvalidURLException.class);
		expectedException.expectMessage(expectedMessage);
		URLParser.parse(serviceAddress);
	}
	
	@Test
	public void dataFor_testParse_ShouldThrowInvalidURLException_WhenTwoPortValuesConfiguredAfterHostValue() throws InvalidURLException {
		expectedException.expect(InvalidURLException.class);
		expectedException.expectMessage("Invalid Syntax for URL: 10.106.1.1:2012:86");
		URLParser.parse("10.106.1.1:2012:86");
	}
	
	@Test
	public void testParse_ConsidersDirectStringAsAHost_ThoughItCanBeAnything() throws InvalidURLException {
		assertEquals($().h("http"), URLParser.parse("http"));
	}
	
	@Test
	public void testParse_ShouldConsiderUrlToBeValid_ButThrowsException_WhenPasswordIsBlank() throws InvalidURLException {
		expectedException.expect(InvalidURLException.class);
		expectedException.expectMessage("Invalid Syntax for URL: aaa:@elitecore.com:86");
		URLParser.parse("aaa:@elitecore.com:86");
	}
	
	@Test
	public void testParse_OverwritesUsernameWithBlankValue_WhenTwoConsecutiveAtSymbolAreProvided() throws InvalidURLException {
		assertEquals($("elitecore.com", 86).un(""), URLParser.parse("aaa@@elitecore.com:86"));
	}
	
	@Test
	public void testParse_ConsidersCompleteStringAsHostNeglectingBrackets_WhenIPv6AddressIsPassedInHost() throws InvalidURLException {
		assertEquals($("::0 1", 1000), URLParser.parse("[::0] 1:1000"));
	}
	
	static class UrlDataWrapper extends URLData{
		
		public UrlDataWrapper pl(String protocol) {
			setProtocol(protocol);
			return this;
		}
		
		public UrlDataWrapper un(String username) {
			setUserName(username);
			return this;
		}
		
		public UrlDataWrapper pw(String password) {
			setPassword(password);
			return this;
		}
		
		public UrlDataWrapper h(String host) {
			setHost(host);
			return this;
		}
		
		public UrlDataWrapper p(int port) {
			setPort(port);
			return this;
		}
		
		public UrlDataWrapper r(String resource) {
			setResource(resource);
			return this;
		}
	}
	
}