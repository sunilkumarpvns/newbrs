package com.elitecore.commons.base;

import static com.elitecore.commons.base.Preconditions.checkArgument;
import static com.elitecore.commons.base.Preconditions.checkNotNull;

/**
 * A set of byte related utilities, best used as static imports.
 * 
 * @author narendra.pathai
 *
 */
public class Bytes {

	/**
	 * Converts {@code bytes} to integer. 
	 * 
	 * <p>Usage:<br/>
	 * <code>Bytes.toInt(new byte[]{0x00, 0x01, 0x00})</code> will return {@code 256}
	 * @param bytes a non-null byte array with length less than or equal to 4
	 * @return converted integer value
	 * @throws IllegalArgumentException if {@code bytes.length} is greater than 4
	 */
	public static int toInt(byte[] bytes) {
		checkNotNull(bytes, "bytes are null");
		checkArgument(bytes.length <= 4, "byteCount should be in closed-range [0:4], found: " + bytes.length);
		
		return toIntInternal(bytes, 0, bytes.length);
	}

	private static int toIntInternal(byte[] bytes, int startIndex, int endIndex) {
		int value = 0;
		for (int i = startIndex; i < endIndex; i++) {
			value = value << 8 | (bytes[i] & 0xFF);
		}
		
		return value;
	}

	/**
	 * Converts {@code bytes} to long. 
	 * 
	 * <p>Usage:<br/>
	 * <code>Bytes.toLong(new byte[]{0x00, 0x01, 0x00, 0x01})</code> will return {@code 65537}
	 * @param bytes a non-null byte array with length less than or equal to 8
	 * @return converted long value
	 * @throws IllegalArgumentException if {@code bytes.length} is greater than 8
	 */
	public static long toLong(byte[] bytes) {
		checkNotNull(bytes, "bytes are null");
		checkArgument(bytes.length <= 8, "byteCount should be in closed-range [0:8], found: " + bytes.length);
		
		return toLongInternal(bytes, 0, bytes.length);
	}
	
	private static long toLongInternal(byte[] bytes, int startIndex, int endIndex) {
		long value = 0;
		for (int i = startIndex; i < endIndex; i++) {
			value = value << 8 | (bytes[i] & 0xFF);
		}
		
		return value;
	}

	/**
	 * Returns the values from each provided array combined into a single array.
	 * For example,<br/> 
	 * <code>concat(new byte[] {a, b}, new byte[] {}, new
	 * byte[] {c}</code> returns the array <code> {a, b, c}</code>.
	 *
	 * @param arrays zero or more {@code byte} arrays
	 * @return a single array containing all the values from the source arrays, in
	 * order
	 */
	public static byte[] concat(byte[]... byteArrays) {
		int length = 0;
		for (byte[] byteArray : byteArrays) {
			length += byteArray.length;
		}
		
		byte[] result = new byte[length];
		int position = 0;
		for (byte[] byteArray : byteArrays) {
			System.arraycopy(byteArray, 0, result, position, byteArray.length);
			position += byteArray.length;
		}
		return result;
	}

	/**
	 * Converts each byte to corresponding hexadecimal character by reading a nibble at 
	 * a time. 
	 * 
	 * @param bytes A non null {@code byte} array
	 * @return A hexadecimal character String
	 */
	
	public static String toHex(byte[] bytes) {
		char[] hexArray = "0123456789ABCDEF".toCharArray();
		char[] hexChars = new char[bytes.length*2];
		int i=-1;
		
		for (byte b : bytes) {
			hexChars[++i] = hexArray[(b & 0xFF) >>> 4];
			hexChars[++i] = hexArray[b & 0x0F];
		}
		
		return String.valueOf(hexChars);
	}

	/**
	 * Converts the hexadecimal characters present in 
	 * the parameter string to bytes. 
	 * 
	 * Returns null in case the hexString passed has invalid
	 * hexadecimal characters or if the length of string is 
	 * not even.
	 * 
	 * @param hexString a string made up of hexadecimal characters
	 * @return resultant byte array 
	 */
	
	public static byte[] fromHex(String hexString) {
		if (hexString.length()%2 != 0)  return null; 
		char[] hexchars = hexString.toCharArray();
		byte[] ans = new byte[hexchars.length/2];
		int j = -1;
		for (int i=0; i< ans.length; i++) {
			byte h = fromHex(hexchars[++j]);
			byte l = fromHex(hexchars[++j]);
			if ( h == -1 || l == -1) return null;
			ans [i] = (byte) ((h << 4) | l);
		}
		return ans;
	}
	
	/**
	 * Returns -1, if {@code ch}  is not a valid hexadecimal character 
	 * 
	 * @param ch hexadecimal character
	 * @return byte value of hexadecimal character
	 */
	public static byte fromHex(char ch) {
		if (ch >= '0' && ch <= '9') return (byte) (ch - '0') ;
		if (ch >= 'A' && ch <= 'F') return (byte) (ch - 'A' + 10);
		if (ch >= 'a' && ch <= 'f') return (byte) (ch - 'a' + 10);
		return -1;
	}
}
