package com.elitecore.passwordutil.base16;

import java.io.UnsupportedEncodingException;

import com.elitecore.passwordutil.DecryptionNotSupportedException;
import com.elitecore.passwordutil.commons.util.constant.CommonConstants;

public class Base16Coder {

	// Mapping table from 6-bit nibbles to Base16 characters.
	private static char[] map1 = new char[16];
	static {
		int i = 0;
		for (char c = '0'; c <= '9'; c++){
			map1[i++] = c;
		}
		for (char c = 'A'; c <= 'F'; c++){
			map1[i++] = c;
		}
	}

	// Mapping table from Base16 characters to 6-bit nibbles.
	private static byte[] map2 = new byte[128];
	static {
		for (int i = 0; i < map2.length; i++){
			map2[i] = -1;
		}
		for (int i = 0; i < 16; i++){
			map2[map1[i]] = (byte) i;
		}
	}

	/**
	 * Encodes a string into Base16 format. No blanks or line breaks are
	 * inserted.
	 * 
	 * @param s
	 *            a String to be encoded.
	 * @return A String with the Base16 encoded data.
	 */
	public static String encodeString(String s) {
		try{
			return new String(encode(s.getBytes(CommonConstants.UTF8)));
		}catch(UnsupportedEncodingException e){
			return new String(encode(s.getBytes()));
		}
	}

	/**
	 * Encodes a byte array into Base16 format. No blanks or line breaks are
	 * inserted.
	 * 
	 * @param in
	 *            an array containing the data bytes to be encoded.
	 * @return A character array with the Base16 encoded data.
	 */
	public static char[] encode(byte[] in) {
		return encode(in, in.length);
	}

	/**
	 * Encodes a byte array into Base16 format. No blanks or line breaks are
	 * inserted.
	 * 
	 * @param in
	 *            an array containing the data bytes to be encoded.
	 * @param iLen
	 *            number of bytes to process in <code>in</code>.
	 * @return A character array with the Base16 encoded data.
	 */
	public static char[] encode(byte[] in, int iLen) {
		char[] out = new char[iLen << 1];
		int pos = 0;
		for (int i = 0; i < in.length; i++) {
			out[pos++] = map1[in[i] >> 4 & 0xf];
			out[pos++] = map1[in[i] & 0xf];
		}
		return out;
	}

	/**
	 * Decodes a string from Base16 format.
	 * 
	 * @param s
	 *            a Base16 String to be decoded.
	 * @return A String containing the decoded data.
	 * @throws DecryptionNotSupportedException
	 *             if the input is not valid Base16 encoded data.
	 */
	public static String decodeString(String s)
			throws DecryptionNotSupportedException {
		try{
			return new String(decode(s),CommonConstants.UTF8);
		}catch(UnsupportedEncodingException e){
			return new String(decode(s));
		}
	}

	/**
	 * Decodes a byte array from Base16 format.
	 * 
	 * @param s
	 *            a Base16 String to be decoded.
	 * @return An array containing the decoded data bytes.
	 * @throws DecryptionNotSupportedException
	 *             if the input is not valid Base16 encoded data.
	 */
	public static byte[] decode(String s)
			throws DecryptionNotSupportedException {
		return decode(s.toCharArray());
	}

	/**
	 * Decodes a byte array from Base16 format. No blanks or line breaks are
	 * allowed within the Base16 encoded data.
	 * 
	 * @param in
	 *            a character array containing the Base16 encoded data.
	 * @return An array containing the decoded data bytes.
	 * @throws DecryptionNotSupportedException
	 *             if the input is not valid Base16 encoded data.
	 */
	public static byte[] decode(char[] in)
			throws DecryptionNotSupportedException {
		int iLen = in.length;
		if (iLen % 2 != 0)
			throw new DecryptionNotSupportedException(
					"Length of Base16 encoded input string is not a multiple of 8.");
		byte[] out = new byte[iLen >> 1];
		int pos = 0;
		int value;
		for (int i = 0; i < out.length; i++) {
			value = map2[in[pos++]];
			value = value << 4;
			value = value | map2[(in[pos++])];
			out[i] = (byte) (value & 0xFF);
		}
		return out;
	}

}
