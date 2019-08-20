package com.elitecore.netvertex.gateway.file.util;

import java.io.Closeable;
import java.io.IOException;

import com.elitecore.commons.logging.LogManager;
import com.googlecode.ipv6.IPv6Address;
import com.googlecode.ipv6.IPv6Network;

public class InetAddressUtil {

	private static final String MESSAGE = "Problem occured while closing stream, Reason : ";

	/**
	 * Bytes to ip v6.
	 *
	 * @param b the b
	 * @return the string
	 */
	public static String bytesToIPV6UniversalFormat(byte[] b) {
		IPv6Address iPv6Address = IPv6Address.fromByteArray(b);
		return iPv6Address.toString();
	}
	/**
	 * Bytes to ip v6.
	 *
	 * @param b the b
	 * @return the string
	 */
	public static String bytesToIPV6UniversalFormat(String ipv6InString) {
		String[] ipv6WithNetMask = ipv6InString.split("/");

		if (ipv6WithNetMask.length > 1) {
			IPv6Network.fromString(ipv6InString); //Validate IPV6
			String tempIpv6 = IPv6Address.fromString(ipv6WithNetMask[0]).toString();
			return tempIpv6 + "/" + ipv6WithNetMask[1];
		} else {
			IPv6Address iPv6Address = IPv6Address.fromString(ipv6InString);
			return iPv6Address.toString();
		}
	}

	public static String stringToIPV6LongFormat(String ipv6InString) {
		IPv6Address iPv6Address = IPv6Address.fromString(ipv6InString);
		return iPv6Address.toLongString();
	}

	public static final void closeStream(Closeable closeable, String module) {
		if (closeable != null){
			try {
				closeable.close();
			} catch (IOException e) {
				LogManager.getLogger().error(module, MESSAGE + e.getMessage());
				LogManager.getLogger().trace(module, e);
			}
		}
	}

	public static String convertIPv6ToIPv4(String value){

		String tempIpv6 = IPv6Address.fromString(value).toLongString();
		String[] ipv6 = tempIpv6.split(":");
		int[] ipv4Arr = new int[4];
		ipv4Arr[0] = Integer.parseInt(ipv6[6].substring(0, 2), 16);
		ipv4Arr[1] = Integer.parseInt(ipv6[6].substring(2, 4), 16);
		ipv4Arr[2] = Integer.parseInt(ipv6[7].substring(0, 2), 16);
		ipv4Arr[3] = Integer.parseInt(ipv6[7].substring(2, 4), 16);
		StringBuilder ipv4 = new StringBuilder();
		for (int k = 0; k < ipv4Arr.length; k++) {
			ipv4.append(ipv4Arr[k]);
			if(k!=3)
				ipv4.append(".");
		}
		return ipv4.toString();
	}
}

