package com.elitecore.commons.net;

import static junitparams.JUnitParamsRunner.$;

import java.net.UnknownHostException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

/// TODO out of range IP
@RunWith(JUnitParamsRunner.class)
public class Inet4CIDRUtilTest {
	
	@Rule public ExpectedException exception = ExpectedException.none();

	private FakeAddressResolver fakeAddressResolver;
	
	@Before
	public void setUp() throws UnknownHostException {
		fakeAddressResolver = new FakeAddressResolver();
		fakeAddressResolver.addHost("255.255.255.0", new byte[] {(byte) 255, (byte) 255, (byte) 255, 0});
	}
	
	@Test
	public void testConstructor_ShouldThrowNPE_WhenValueIsNotProvided() {
		exception.expect(NullPointerException.class);
		exception.expectMessage("address is null");
		Inet4CIDRUtil.from(null, fakeAddressResolver);
	}
	
	@Test
	@Parameters({
		"",
		"127.0.0.1/24/24",
		"/"
	})
	public void testFrom_ShouldThrowIllegalArgumentException_IfAddressIsInvalid(String address) {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Invalid format: " + address);
		Inet4CIDRUtil.from(address, fakeAddressResolver);
	}
	
	public Object[] dataFor_testFrom_invalid_cidr() {
		return $(
				$("127.0.0.1/ab",	 "ab"),
				$("127.0.0.1/cd",   "cd")
		);
	}
	
	@Test
	@Parameters(method = "dataFor_testFrom_invalid_cidr")
	public void testFrom_ShouldThrowNumberFormatException_IfCIDRValueIsInvalid(String address, String cidrValue) {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Invalid netmask [" + cidrValue + "] in address " + address);
		Inet4CIDRUtil.from(address, fakeAddressResolver);
	}
	
	public Object[] dataFor_testFrom_invalid_cidr_range() {
		return $(
				$("127.0.0.1/-1",	 "-1"),
				$("127.0.0.1/33",  "33"),
				$("127.0.0.1/333",  "333")
		);
	}
	
	@Test
	@Parameters(method = "dataFor_testFrom_invalid_cidr_range")
	public void testFrom_ShouldThrowIllegalArgumentException_IfCIDRValueWithInvalidRange(String address, String cidrValue) {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("invalid netmask:" + cidrValue + ", it must be in range [0-32]");
		Inet4CIDRUtil.from(address, fakeAddressResolver);
	}
	
	public Object[] dataFor_testFrom_invalid_address() {
		return $(
				$("127.0..0.1/-1",  "127.0..0.1"),
				$("127.0.1/32",  "127.0.1"),
				$("127.0.a.1/32",  "127.0.a.1")
		);
	}
	
	@Test
	@Parameters(method = "dataFor_testFrom_invalid_address")
	public void testFrom_ShouldThrowIllegalArgumentException_IfAddressIsInvalid(String address, String invalidAddress) {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Invalid format: " + invalidAddress);
		Inet4CIDRUtil.from(address, fakeAddressResolver);
	}
	
	public Object[] dataFor_testFrom_valid_ipaddress() {
		return $(
				$("127.0.0.1/0",	 "127.0.0.1"),
				$("127.0.0.1/32",  "127.0.0.1"),
				$("127.0.0.1/9",  "127.0.0.1")
		);
	}
	
	@Test
	@Parameters(method = "dataFor_testFrom_valid_ipaddress")
	public void testFrom_ShouldReturnIpAddress_IfValidValueIsGiven(String address, String expectedAddressValue) {
		Inet4CIDRUtil util = Inet4CIDRUtil.from(address, fakeAddressResolver);
		Assert.assertEquals(expectedAddressValue, util.getAddress());
	}
	
	
	public Object[] dataFor_testFrom() {
		return $(
				$("127.0.0.1/24",	 "255.255.255.0"),
				$("127.0.0.1/9",	 "255.128.0.0")
		);
	}
	
	@Test
	@Parameters(method = "dataFor_testFrom")
	public void testFrom_ShouldReturnNetmaskAddress_IfValidValueIsGiven(String address, String expectedAddressValue) {
		Inet4CIDRUtil util = Inet4CIDRUtil.from(address, fakeAddressResolver);
		Assert.assertEquals(expectedAddressValue, util.getNetmask());
	}
	
	@Test
	public void testFrom_ShouldThrowIllegalArgumentException_IfAddressResolutionFails() throws UnknownHostException {
		String address = "127.0.0";

		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Invalid format: "+ address);
		Inet4CIDRUtil.from(address, fakeAddressResolver);
	}
	
	@Test
	@Parameters(method = "dataFor_testGetNetmaskAddress_ShouldReturnNetmaskAddressOctets")
	public void testGetNetmaskAddress_ShouldReturnNetmaskAddressOctets(String address, byte[] expectedNetmaskAddress) {
		Inet4CIDRUtil util = Inet4CIDRUtil.from(address, fakeAddressResolver);
		Assert.assertArrayEquals(expectedNetmaskAddress, util.getNetmaskAddress());
	}
	
	Object[] dataFor_testGetNetmaskAddress_ShouldReturnNetmaskAddressOctets() {
		return $(
				$("127.0.0.1/8", new byte[] {(byte) 255, 0, 0, 0})
		);
	}
	
	@Test
	@Parameters(method = "dataFor_testGetIPAddress_ShouldReturnIPAddressOctets")
	public void testGetIPAddress_ShouldReturnIPAddressOctets(String address, byte[] expectedIPAddress) {
		Inet4CIDRUtil util = Inet4CIDRUtil.from(address, fakeAddressResolver);
		Assert.assertArrayEquals(expectedIPAddress, util.getIPAddress());
	}
	
	Object[] dataFor_testGetIPAddress_ShouldReturnIPAddressOctets() {
		return $(
				$("127.0.0.1/8", new byte[] {127, 0, 0, 1})
		);
	}
	
	@Test
	@Parameters(method = "dataFor_testGetStartAddress_ShouldReturnStartAddressOctets")
	public void testGetStartAddress_ShouldReturnStartAddressOctets(String address, byte[] expectedStartAddress) {
		Inet4CIDRUtil util = Inet4CIDRUtil.from(address, fakeAddressResolver);
		Assert.assertArrayEquals(expectedStartAddress, util.getStartAddress());
	}
	
	Object[] dataFor_testGetStartAddress_ShouldReturnStartAddressOctets() {
		return $(
				$("127.0.0.1/8", new byte[] {127, 0, 0, 0})
		);
	}
	
	@Test
	@Parameters(method = "dataFor_testGetEndAddress_ShouldReturnEndAddressOctets")
	public void testGetEndAddress_ShouldReturnEndAddressOctets(String address, byte[] expectedEndAddress) {
		Inet4CIDRUtil util = Inet4CIDRUtil.from(address, fakeAddressResolver);
		Assert.assertArrayEquals(expectedEndAddress, util.getEndAddress());
	}
	
	Object[] dataFor_testGetEndAddress_ShouldReturnEndAddressOctets() {
		return $(
				$("127.0.0.1/8", new byte[] {127, (byte) 255, (byte) 255, (byte) 255}),
				$("127.0.0.1/9", new byte[] {127, (byte) 127, (byte) 255, (byte) 255}),
				$("127.0.0.1/16", new byte[]  {127, 0, (byte) 255, (byte) 255})
		);
	}
	
	@Test
	public void testToString_ShouldPrintAllDetails() {
		Inet4CIDRUtil util = Inet4CIDRUtil.from("127.0.0.1/23", fakeAddressResolver);
		Assert.assertTrue(util.toString().contains("CIDR Address: 127.0.0.1/255.255.254.0"));
		Assert.assertTrue(util.toString().contains("Start Address: 127.0.0.0"));
		Assert.assertTrue(util.toString().contains("End Address: 127.0.1.255"));
	}
	
	@Test
	public void testGetNetmaskAddress_ShouldReturnNetmaskOctets_IfJustNetmaskIsProvided() {
		Inet4CIDRUtil util = Inet4CIDRUtil.from("/24", fakeAddressResolver);
		Assert.assertArrayEquals(new byte[] {(byte) 255, (byte) 255, (byte) 255, 0}, util.getNetmaskAddress());
	}
	
	@Test
	public void testGetNetmaskAddress_ShouldReturnNetmaskOctets_IfJustNetmaskInDottedDecimalIsProvided() {
		Inet4CIDRUtil util = Inet4CIDRUtil.from("/255.255.255.0", fakeAddressResolver);
		Assert.assertArrayEquals(new byte[] {(byte) 255, (byte) 255, (byte) 255, 0}, util.getNetmaskAddress());
	}
	
	@Test
	public void testFrom_ShouldThrowIllegalArgumentException_IfJustNetmaskInDottedDecimalIsProvidedAndItIsNotValid() throws UnknownHostException {
		fakeAddressResolver.addHost("255.255.245.0", new byte[] {(byte) 255, (byte) 255, (byte) 245, 0});

		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Invalid netmask value: 255.255.245.0");
		Inet4CIDRUtil.from("/255.255.245.0", fakeAddressResolver);
	}
	
	@Test
	public void testFrom_ShouldThrowIllegalArgumentException_IfJustNetmaskInDottedDecimalIsProvidedAndItIsOutOfRange() throws UnknownHostException {
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Invalid format: /255.255.500.0");
		Inet4CIDRUtil.from("/255.255.500.0", fakeAddressResolver);
	}
	
	@Test
	public void testGetIPAddress_ShouldReturnIPAddressOctets_IfJustIPInDottedDecimalIsProvided() {
		Inet4CIDRUtil util = Inet4CIDRUtil.from("127.0.0.1", fakeAddressResolver);
		Assert.assertArrayEquals(new byte[] {127, (byte) 0, (byte) 0, 1}, util.getIPAddress());
	}
}
