package com.elitecore.commons.net;

import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.elitecore.commons.annotations.VisibleForTesting;
import com.elitecore.commons.base.Bytes;
import com.elitecore.commons.base.Preconditions;
import com.elitecore.commons.base.Splitter;
import com.elitecore.commons.base.Strings;

public class Inet4CIDRUtil {

	private static final long ALL_ONES = 0xFFFFFFFFL;

	private static final String IP_ADDRESS = "(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})\\.(\\d{1,3})";
	
	private static final Pattern addressPattern = Pattern.compile(IP_ADDRESS);
	
	private static final String DOTTED_DECIMAL_FORMAT = "%d.%d.%d.%d";

	private static Map<String, Long> possibleSubnets = new HashMap<String, Long>();
	
	static {
		long subnet = ALL_ONES;
		for (int i = 0; i <= 32; i++) {
			subnet = (ALL_ONES << i) & 0xFFFFFFFFL;
			possibleSubnets.put(String.valueOf(subnet), subnet);
		}
	}
	
	private byte[] addressBytes;
	private byte[] netmaskAddress;
	private int netmask;
	private final String input;
	private byte[] startAddress;
	private byte[] endAddress;

	private boolean isCIDR = false;

	private AddressResolver addressResolver;

	private Inet4CIDRUtil(String input, AddressResolver addressResolver) {
		this.input = input;
		this.addressResolver = addressResolver;
	}

	public static Inet4CIDRUtil from(String address) throws NullPointerException, IllegalArgumentException {
		return from(address, AddressResolver.defaultAddressResolver());
	}
	
	@VisibleForTesting
	static Inet4CIDRUtil from(String address, AddressResolver addressResolver) throws NullPointerException, IllegalArgumentException {
		Preconditions.checkNotNull(address, "address is null");
		
		return new Inet4CIDRUtil(address, addressResolver).initialize();
	}
	
	private Inet4CIDRUtil initialize() {
		parse();
		if (isCIDR) {
			calculateStartAddress();
			calculateEndAddress();
		}
		return this;
	}

	private void parse() {
		if (input.contains("/")) {
			String[] tokens = Splitter.on('/').preserveTokens().splitToArray(input);
			
			if (tokens.length != 2) {
				throw new IllegalArgumentException("Invalid format: " + input);
			}
			
			String strIpAddress = tokens[0];
			if (Strings.isNullOrBlank(strIpAddress) == false) {
				parseAddress(strIpAddress);
			}
			
			if (Strings.isNullOrBlank(tokens[1])) {
				throw new IllegalArgumentException("Invalid format: " + input);
			}
			
			parseNetmask(tokens);
		} else {
			parseAddress(input);
		}
		
		isCIDR = addressBytes != null && netmaskAddress != null;
	}

	private void parseAddress(String strIpAddress) {
		Matcher addressMatcher = addressPattern.matcher(strIpAddress);
		if (addressMatcher.matches() == false) {
			throw new IllegalArgumentException("Invalid format: " + input);
		}
		
		try {
			addressBytes = addressResolver.byName(strIpAddress).getAddress();
		} catch (UnknownHostException e) {
			throw new IllegalArgumentException("Invalid format: " + input, e);
		}
	}

	private void parseNetmask(String[] tokens) {
		try {
			netmask = Integer.parseInt(tokens[1]);
			if (netmask < 0 || netmask > 32) {
				throw new IllegalArgumentException("invalid netmask:" + netmask + ", it must be in range [0-32]");
			}
			calculateNetmaskAddress();
		} catch (NumberFormatException ex) {
			if (addressPattern.matcher(tokens[1]).matches() == false) {
				throw new IllegalArgumentException("Invalid netmask [" + tokens[1] + "] in address " + input);
			}
			
			try {
				netmaskAddress = addressResolver.byName(tokens[1]).getAddress();
				validateNetmask();
			} catch (UnknownHostException e) {
				throw new IllegalArgumentException("Invalid format: " + input);
			}
		}
	}

	private void validateNetmask() {
		long netmaskAddressValue = Bytes.toLong(netmaskAddress);
		if (possibleSubnets.get(String.valueOf(netmaskAddressValue)) == null) {
			throw new IllegalArgumentException("Invalid netmask value: " + dottedDecimal(netmaskAddress));
		}
	}

	private void calculateEndAddress() {
		byte[] endAddress = new byte[4];
		endAddress[0] = (byte) (addressBytes[0] | ~netmaskAddress[0]);
		endAddress[1] = (byte) (addressBytes[1] | ~netmaskAddress[1]);
		endAddress[2] = (byte) (addressBytes[2] | ~netmaskAddress[2]);
		endAddress[3] = (byte) (addressBytes[3] | ~netmaskAddress[3]);
		this.endAddress = endAddress;
	}

	private void calculateStartAddress() {
		byte[] startAddress = new byte[4];
		startAddress[0] = (byte) (addressBytes[0] & netmaskAddress[0]);
		startAddress[1] = (byte) (addressBytes[1] & netmaskAddress[1]);
		startAddress[2] = (byte) (addressBytes[2] & netmaskAddress[2]);
		startAddress[3] = (byte) (addressBytes[3] & netmaskAddress[3]);
		this.startAddress = startAddress;
	}

	private void calculateNetmaskAddress() {
		long ALLONES = ALL_ONES;
		int MAX_CIDR = 32;
		long subnet = ALLONES << MAX_CIDR - netmask & 0xFFFFFFFFL; 
		byte[] netmaskAddress = new byte[4];
		netmaskAddress[0] = (byte) (subnet >> 24 & 0xFF);
		netmaskAddress[1] = (byte) (subnet >> 16 & 0xFF);
		netmaskAddress[2] = (byte) (subnet >> 8 & 0xFF);
		netmaskAddress[3] = (byte) (subnet & 0xFF); 
		this.netmaskAddress = netmaskAddress;
	}

	public String getAddress() {
		if (addressBytes == null) {
			throw new IllegalStateException("No address available");
		}
		
		return dottedDecimal(addressBytes);
	}
	
	private String dottedDecimal(byte[] octets) {
		return String.format(DOTTED_DECIMAL_FORMAT, octets[0] & 0xFF, octets[1] & 0xFF, octets[2] & 0xFF, octets[3] & 0xFF);
	}
	
	public String getNetmask() {
		return dottedDecimal(netmaskAddress);
	}

	public byte[] getNetmaskAddress() {
		return netmaskAddress;
	}

	public byte[] getIPAddress() {
		return addressBytes;
	}

	public byte[] getStartAddress() {
		return startAddress;
	}

	public byte[] getEndAddress() {
		return endAddress;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("CIDR Address: ").append(dottedDecimal(addressBytes)).append("/")
			.append(dottedDecimal(netmaskAddress)).append("\n")
			.append("Start Address: ").append(dottedDecimal(startAddress)).append("\n")
			.append("End Address: ").append(dottedDecimal(endAddress));
		return builder.toString();
	}
}
