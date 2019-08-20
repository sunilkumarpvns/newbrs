package com.elitecore.passwordutil.base32;

import java.io.UnsupportedEncodingException;

import com.elitecore.passwordutil.DecryptionNotSupportedException;
import com.elitecore.passwordutil.commons.util.constant.CommonConstants;

public class Base32Coder {

	// Mapping table from 6-bit nibbles to Base32 characters.
	private static char[] map1 = new char[32];
	static {
		int i = 0;
		for (char c = 'A'; c <= 'Z'; c++){
			map1[i++] = c;
		}
		for (char c = '2'; c <= '7'; c++){
			map1[i++] = c;
		}
	}

	// Mapping table from Base32 characters to 6-bit nibbles.
	private static byte[] map2 = new byte[128];
	static {
		for (int i = 0; i < map2.length; i++){
			map2[i] = -1;
		}
		for (int i = 0; i < 32; i++){
			map2[map1[i]] = (byte) i;
		}
	}

	/**
	 * Encodes a string into Base32 format.
	 * No blanks or line breaks are inserted.
	 * @param s  a String to be encoded.
	 * @return   A String with the Base32 encoded data.
	 */
	public static String encodeString(String s) {
		try{
			return new String(encode(s.getBytes(CommonConstants.UTF8)));
		}catch(UnsupportedEncodingException e){
			return new String(encode(s.getBytes()));
		}
	}

	/**
	 * Encodes a byte array into Base32 format.
	 * No blanks or line breaks are inserted.
	 * @param in  an array containing the data bytes to be encoded.
	 * @return    A character array with the Base32 encoded data.
	 */
	public static char[] encode(byte[] in) {
		int iLen = in.length;
		int oDataLen = (iLen * 8 + 4) / 5; // output length without padding
		int oLen = ((iLen + 4) / 5) * 8; // output length including padding
		char[] out = new char[oLen];
		int ip = 0;
		int op = 0;
		while (ip < iLen) {
			int i0 = in[ip++] & 0xff;
			int i1 = ip < iLen ? in[ip++] & 0xff : 0;
			int i2 = ip < iLen ? in[ip++] & 0xff : 0;
			int i3 = ip < iLen ? in[ip++] & 0xff : 0;
			int i4 = ip < iLen ? in[ip++] & 0xff : 0;
			int o0 = i0 >>> 3;
			int o1 = ((i0 & 7) << 2) | (i1 >>> 6);
			int o2 = (i1 & 62) >>> 1;
			int o3 = ((i1 & 1) << 4) | (i2 >>> 4);
			int o4 = ((i2 & 0xF) << 1) | ((i3 & 128) >>> 7);
			int o5 = ((i3 & 124) >>> 2);
			int o6 = ((i3 & 3) << 3) | (i4 >>> 5);
			int o7 = i4 & 31;
			out[op++] = map1[o0];
			out[op++] = map1[o1];
			out[op] = op < oDataLen ? map1[o2] : '=';
			op++;
			out[op] = op < oDataLen ? map1[o3] : '=';
			op++;
			out[op] = op < oDataLen ? map1[o4] : '=';
			op++;
			out[op] = op < oDataLen ? map1[o5] : '=';
			op++;
			out[op] = op < oDataLen ? map1[o6] : '=';
			op++;
			out[op] = op < oDataLen ? map1[o7] : '=';
			op++;
		}
		return out;
	}

	/**
	 * Decodes a string from Base32 format.
	 * @param s  a Base32 String to be decoded.
	 * @return   A String containing the decoded data. 
	 * @throws   DecryptionNotSupportedException if the input is not valid Base32 encoded data.
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
	 * Decodes a byte array from Base32 format.
	 * @param s  a Base32 String to be decoded.
	 * @return   An array containing the decoded data bytes. 
	 * @throws   DecryptionNotSupportedException if the input is not valid Base32 encoded data.
	 */
	public static byte[] decode(String s)
			throws DecryptionNotSupportedException {
		return decode(s.toCharArray());
	}

	/**
	 * Decodes a byte array from Base32 format.
	 * No blanks or line breaks are allowed within the Base32 encoded data.
	 * @param in  a character array containing the Base32 encoded data.
	 * @return    An array containing the decoded data bytes. 
	 * @throws    DecryptionNotSupportedException if the input is not valid Base32 encoded data.
	 */
	public static byte[] decode(char[] in)
			throws DecryptionNotSupportedException {
		int iLen = in.length;
		if (iLen % 8 != 0)
			throw new DecryptionNotSupportedException(
					"Length of Base32 encoded input string is not a multiple of 8.");
		while (iLen > 0 && in[iLen - 1] == '='){
			iLen--;
		}	
		int oLen = (iLen * 5) / 8;
		byte[] out = new byte[oLen];
		int ip = 0;
		int op = 0;
		while (ip < iLen) {
			int i0 = in[ip++];
			int i1 = in[ip++];
			int i2 = ip < iLen ? in[ip++] : 'A';
			int i3 = ip < iLen ? in[ip++] : 'A';
			int i4 = ip < iLen ? in[ip++] : 'A';
			int i5 = ip < iLen ? in[ip++] : 'A';
			int i6 = ip < iLen ? in[ip++] : 'A';
			int i7 = ip < iLen ? in[ip++] : 'A';
			if (i0 > 128 || i1 > 128 || i2 > 128 || i3 > 128 || i4 > 128
					|| i5 > 128 || i6 > 128 || i7 > 128)
				throw new DecryptionNotSupportedException(
						"Illegal character in Base32 encoded data.");
			int b0 = map2[i0];
			int b1 = map2[i1];
			int b2 = map2[i2];
			int b3 = map2[i3];
			int b4 = map2[i4];
			int b5 = map2[i5];
			int b6 = map2[i6];
			int b7 = map2[i7];

			if (b0 < 0 || b1 < 0 || b2 < 0 || b3 < 0 || b4 < 0 || b5 < 0
					|| b6 < 0 || b7 < 0)
				throw new DecryptionNotSupportedException(
						"Illegal character in Base32 encoded data.");
			int o0 = (b0 << 3) | (b1 >>> 2);
			int o1 = ((b1 & 3) << 6) | (b2 << 1) | ((b3 & 16) >>> 4);
			int o2 = ((b3 & 0xF) << 4) | (b4 >>> 1);
			int o3 = ((b4 & 1) << 7) | (b5 << 2) | ((b6 & 24) >>> 3);
			int o4 = ((b6 & 7) << 5) | b7;
			out[op++] = (byte) o0;
			if (op < oLen)
				out[op++] = (byte) o1;
			if (op < oLen)
				out[op++] = (byte) o2;
			if (op < oLen)
				out[op++] = (byte) o3;
			if (op < oLen)
				out[op++] = (byte) o4;
		}
		return out;
	}

	// Dummy constructor.
	private Base32Coder() {
	}

}
