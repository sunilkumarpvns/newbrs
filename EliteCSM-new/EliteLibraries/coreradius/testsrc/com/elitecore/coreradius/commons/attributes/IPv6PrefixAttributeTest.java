package com.elitecore.coreradius.commons.attributes;

import junit.framework.TestSuite;

import com.elitecore.coreradius.BaseRadiusTestCase;

public class IPv6PrefixAttributeTest extends BaseRadiusTestCase{

	public IPv6PrefixAttributeTest(String name) {
		super(name);
	}
	
	public static TestSuite suite() {
		TestSuite suite = new TestSuite();
		suite.addTest(new IPv6PrefixAttributeTest("testGetStringValue"));
	   	return suite;
	}
	
	public void testGetStringValue(){
		
		IPv6PrefixAttribute ipv6prefixAttribute  = new IPv6PrefixAttribute();
		String stringAddress;
		
		stringAddress = "fedc:ba98:7654:3210:abcd:5b8a:1ac1:acbd/32";
		ipv6prefixAttribute.setStringValue(stringAddress);
		assertEquals("fedc:ba98:0:0:0:0:0:0/32",ipv6prefixAttribute.getStringValue());
		System.out.println("Check Len : " + ipv6prefixAttribute.getLength());
		assertEquals(20,ipv6prefixAttribute.getLength());
		
		stringAddress = "fedc:ba98:7654:3210:abcd:5b8a:1ac1:acbd/64";
		ipv6prefixAttribute.setStringValue(stringAddress);
		assertEquals("fedc:ba98:7654:3210:0:0:0:0/64",ipv6prefixAttribute.getStringValue());
		System.out.println("Check Len : " + ipv6prefixAttribute.getLength());
		assertEquals(20,ipv6prefixAttribute.getLength());
		
		stringAddress = "fedc:ba98:7654:3210:abcd:5b8a:1ac1:acbd/29";
		ipv6prefixAttribute.setStringValue(stringAddress);
		assertEquals("fedc:ba90:0:0:0:0:0:0/29",ipv6prefixAttribute.getStringValue());
		System.out.println("Check Len : " + ipv6prefixAttribute.getLength());
		assertEquals(20,ipv6prefixAttribute.getLength());
		
		stringAddress = "fedc:ba98:7654:3210:abcd:5b8a:1ac1:acbd/70";
		ipv6prefixAttribute.setStringValue(stringAddress);
		assertEquals("fedc:ba98:7654:3210:a000:0:0:0/70",ipv6prefixAttribute.getStringValue());
		System.out.println("Check Len : " + ipv6prefixAttribute.getLength());
		assertEquals(20,ipv6prefixAttribute.getLength());
		
		stringAddress = "fedc:ba98:7654:3210:abcd:5b8a:1ac1:acbd";
		ipv6prefixAttribute.setStringValue(stringAddress);
		assertEquals("fedc:ba98:7654:3210:0:0:0:0/64",ipv6prefixAttribute.getStringValue());
		System.out.println("Check Len : " + ipv6prefixAttribute.getLength());
		assertEquals(20,ipv6prefixAttribute.getLength());
		
		stringAddress = "1080::8:800:200C:417A/30";
		ipv6prefixAttribute.setStringValue(stringAddress);
		assertEquals("1080:0:0:0:0:0:0:0/30",ipv6prefixAttribute.getStringValue());
		System.out.println("Check Len : " + ipv6prefixAttribute.getLength());
		assertEquals(20,ipv6prefixAttribute.getLength());
		
		stringAddress = "1080::8:800:200C:417A";
		ipv6prefixAttribute.setStringValue(stringAddress);
		assertEquals("1080:0:0:0:0:0:0:0/64",ipv6prefixAttribute.getStringValue());
		System.out.println("Check Len : " + ipv6prefixAttribute.getLength());
		assertEquals(20,ipv6prefixAttribute.getLength());
		
	}
}
