package com.elitecore.commons.net;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * An address resolution utility that hides system address resolution dependency behind an abstraction.
 * This is useful for unit testing address resolution code. Depending on actual hostname lookup during
 * unit tests proves costly and time consuming. That's when you can utilize this abstraction to test
 * all branches of your code.
 *   
 * @author narendra.pathai
 *
 */
public abstract class AddressResolver {
	
	private static final AddressResolver DEFAULT = new AddressResolver() {
		
		@Override
		public InetAddress byAddress(byte[] address) throws UnknownHostException {
			return InetAddress.getByAddress(address);
		}

		@Override
		public InetAddress byName(String name) throws UnknownHostException {
			return InetAddress.getByName(name);
		}

		@Override
		public InetAddress[] allByName(String name) throws UnknownHostException {
			return InetAddress.getAllByName(name);
		}
	};
	
	/**
	 * An abstract call representing {@link InetAddress} lookup based on address.
	 * @return InetAddress
	 * @throws UnknownHostException if host is unknown
	 */
	public abstract InetAddress byAddress(byte[] address) throws UnknownHostException;
	
	/**
	 * An abstract call representing {@link InetAddress} lookup based on name.
	 * @return InetAddress
	 * @throws UnknownHostException if host is unknown
	 */
	public abstract InetAddress byName(String name) throws UnknownHostException;
	
	/**
	 * An abstract call representing {@link InetAddress} lookup based on name.
	 * @return InetAddress
	 * @throws UnknownHostException if host is unknown
	 */
	public abstract InetAddress[] allByName(String name) throws UnknownHostException;

	/**
	 * @return default address resolver that uses Java API {@link InetAddress}.
	 */
	public static AddressResolver defaultAddressResolver() {
		return DEFAULT;
	}

}
