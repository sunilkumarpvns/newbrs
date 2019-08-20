package com.elitecore.commons.net;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Fake implementation of {@link AddressResolver} which aids in unit testing address resolution code.
 *
 * <p>
 * Hosts can be added to the resolver using {@link #addHost(String, byte[])}, which will then be found
 * when host lookup is done.
 * 
 * <p>
 * Hosts {@code 127.0.0.1}, {@code 0.0.0.0} and {@code 0:0:0:0:0:0:0:1} are added by default so no need to add them manually.
 * 
 * @author narendra.pathai
 *
 */
public class FakeAddressResolver extends AddressResolver {

	private static final String DEFAULT_IPv6_HOST = "0:0:0:0:0:0:0:1";
	private static final String DEFAULT_HOST = "0.0.0.0";
	private static final String LOCALHOST = "127.0.0.1";
	
	private List<InetAddress> addresses = new ArrayList<InetAddress>();
	
	public FakeAddressResolver() {
		addHost(LOCALHOST, new byte[] {127, 0, 0, 1})
		.addHost(DEFAULT_HOST, new byte[] {0, 0, 0, 0})
		.addHost(DEFAULT_IPv6_HOST, new byte[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1});
	}
	
	@Override
	public InetAddress byAddress(byte[] address) throws UnknownHostException {
		for (InetAddress it : addresses) {
			if (Arrays.equals(it.getAddress(), address)) {
				return it;
			}
		}
		throw new UnknownHostException(Arrays.toString(address));
	}

	@Override
	public InetAddress byName(String name) throws UnknownHostException {
		for (InetAddress it : addresses) {
			if (it.getHostAddress().equals(name)) {
				return it;
			}
		}
		throw new UnknownHostException(name);
	}

	@Override
	public InetAddress[] allByName(String name) throws UnknownHostException {
		for (InetAddress it : addresses) {
			if (it.getHostAddress().equals(name)) {
				return new InetAddress[] {it};
			}
		}
		throw new UnknownHostException(name);
	}

	/**
	 * Adds host to registry which can the be found by name and address 
	 */
	public FakeAddressResolver addHost(String name, byte[] addr) {
		InetAddress address = mock(InetAddress.class);
		when(address.getAddress()).thenReturn(addr);
		when(address.getHostAddress()).thenReturn(name);
		addresses.add(address);
		return this;
	}
}
