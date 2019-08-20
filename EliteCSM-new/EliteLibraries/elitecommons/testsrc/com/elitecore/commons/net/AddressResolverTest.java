package com.elitecore.commons.net;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.junit.Test;

/**
 * 
 * @author narendra.pathai
 *
 */
public class AddressResolverTest {
	
	@Test
	public void defaultAddressResolverResolvesInetAddressByName() throws UnknownHostException {
		InetAddress localhost = AddressResolver.defaultAddressResolver().byName("localhost");
		assertThat(localhost, is(equalTo(InetAddress.getByName("localhost"))));
	}
	
	@Test
	public void defaultAddressResolverResolvesInetAddressByAddress() throws UnknownHostException {
		InetAddress localhost = AddressResolver.defaultAddressResolver().byAddress(new byte[] {127, 0, 0, 1});
		assertThat(localhost, is(equalTo(InetAddress.getByAddress(new byte[] {127, 0, 0, 1}))));
	}
	
	@Test
	public void defaultAddressResolverResolvesAllInetAddressesByName() throws UnknownHostException {
		InetAddress[] localhost = AddressResolver.defaultAddressResolver().allByName("localhost");
		assertThat(localhost, is(equalTo(InetAddress.getAllByName("localhost"))));
	}
}
