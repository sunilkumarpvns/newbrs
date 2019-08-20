package com.elitecore.coreeap.util.tls.keyexchange;

import junit.framework.TestCase;

import org.junit.Test;

import com.elitecore.coreeap.util.tls.TLSUtility;
import com.elitecore.coreeap.util.tls.keyexchange.DHKeyExchange;

public class DHKeyExchangeTest extends TestCase {
	
	@Test
	public static void test64BytesPandG(){
		
		final byte[] P_64 = { 
			-65, 36, 77, -110, -106, -37, -10, -119,
			-83, -121, 16, -115, -5, 68, -56, 127, -121,
			4, 76, 62, -92, -63, 46, 120, 122,
			-32, -46, -86, -117, -120, 34, 86,
			-62, 17, -29, 103, 97, -104, 80, -96,
			81, -68, 82, 50, -72, -27, -47, 109,
			-44, -30, -108, 94, 69, 54, -69, 6,
			79, -18, 37, 16, -49, 127, 2, 99
		};
		
		final byte[] G_64 = {
			-65, 2, -53, -87, 89, -74, -69, -22,
			-28, -54, -14, -111, 90, -100, -103, -79,
			-95, 102, -118, 107, -91, 21, -128, 15,
			11, -106, 124, 119, -10, 125, 44, -64,
			-37, -20, -60, 101, -82, 14, 87, 95,
			3, -78, -50, -90, 90, 78, -47, -26,
			-52, 10, 12, -94, 56, 18, -122, 72,
			59, 65, 74, -53, 77, -14, -76, -63
		};
		
		DHKeyExchange dhKeyExchange = new DHKeyExchange(P_64, G_64);
		
		byte[] serverParam = dhKeyExchange.generateParameters();
		int serverParamIndex = 0;
		int length = ((serverParam[serverParamIndex++] & 0xFF)  << 8) + (serverParam[serverParamIndex++] & 0xFF);
		byte[] p_test = new byte[length];
		System.arraycopy(serverParam, serverParamIndex, p_test, 0, length);
		serverParamIndex += length;
		length = ((serverParam[serverParamIndex++] & 0xFF)  << 8) + (serverParam[serverParamIndex++] & 0xFF);
		byte[] g_test = new byte[length];
		System.arraycopy(serverParam, serverParamIndex, g_test, 0, length);
		serverParamIndex += length;
		length = ((serverParam[serverParamIndex++] & 0xFF)  << 8) + (serverParam[serverParamIndex++] & 0xFF);
		byte[] y_test = new byte[length];
		System.arraycopy(serverParam, serverParamIndex, y_test, 0, length);
		
		assertEquals("P bytes mismatch", TLSUtility.bytesToHex(P_64), TLSUtility.bytesToHex(p_test));
		assertEquals("G bytes mismatch", TLSUtility.bytesToHex(G_64), TLSUtility.bytesToHex(g_test));
		assertEquals("Y length field and Y size mismatch", length, y_test.length);
	}
	
	@Test
	public static void test128BytesPandG(){
		final byte[] P_128 = { 
				-65, 36, 77, -110, -106, -37, -10, -119,
				-83, -121, 16, -115, -5, 68, -56, 127, -121,
				4, 76, 62, -92, -63, 46, 120, 122,
				-32, -46, -86, -117, -120, 34, 86,
				-62, 17, -29, 103, 97, -104, 80, -96,
				81, -68, 82, 50, -72, -27, -47, 109,
				-44, -30, -108, 94, 69, 54, -69, 6,
				79, -18, 37, 16, -49, 127, 2, 99,
				-65, 36, 77, -110, -106, -37, -10, -119,
				-83, -121, 16, -115, -5, 68, -56, 127, -121,
				4, 76, 62, -92, -63, 46, 120, 122,
				-32, -46, -86, -117, -120, 34, 86,
				-62, 17, -29, 103, 97, -104, 80, -96,
				81, -68, 82, 50, -72, -27, -47, 109,
				-44, -30, -108, 94, 69, 54, -69, 6,
				79, -18, 37, 16, -49, 127, 2, 99
			};
			
			final byte[] G_128 = {
				-65, 2, -53, -87, 89, -74, -69, -22,
				-28, -54, -14, -111, 90, -100, -103, -79,
				-95, 102, -118, 107, -91, 21, -128, 15,
				11, -106, 124, 119, -10, 125, 44, -64,
				-37, -20, -60, 101, -82, 14, 87, 95,
				3, -78, -50, -90, 90, 78, -47, -26,
				-52, 10, 12, -94, 56, 18, -122, 72,
				59, 65, 74, -53, 77, -14, -76, -63,
				-65, 2, -53, -87, 89, -74, -69, -22,
				-28, -54, -14, -111, 90, -100, -103, -79,
				-95, 102, -118, 107, -91, 21, -128, 15,
				11, -106, 124, 119, -10, 125, 44, -64,
				-37, -20, -60, 101, -82, 14, 87, 95,
				3, -78, -50, -90, 90, 78, -47, -26,
				-52, 10, 12, -94, 56, 18, -122, 72,
				59, 65, 74, -53, 77, -14, -76, -63
			};
			
			DHKeyExchange dhKeyExchange = new DHKeyExchange(P_128, G_128);
			
			byte[] serverParam = dhKeyExchange.generateParameters();
			int serverParamIndex = 0;
			int length = ((serverParam[serverParamIndex++] & 0xFF)  << 8) + (serverParam[serverParamIndex++] & 0xFF);
			byte[] p_test = new byte[length];
			System.arraycopy(serverParam, serverParamIndex, p_test, 0, length);
			serverParamIndex += length;
			length = ((serverParam[serverParamIndex++] & 0xFF)  << 8) + (serverParam[serverParamIndex++] & 0xFF);
			byte[] g_test = new byte[length];
			System.arraycopy(serverParam, serverParamIndex, g_test, 0, length);
			serverParamIndex += length;
			length = ((serverParam[serverParamIndex++] & 0xFF)  << 8) + (serverParam[serverParamIndex++] & 0xFF);
			byte[] y_test = new byte[length];
			System.arraycopy(serverParam, serverParamIndex, y_test, 0, length);
			
			assertEquals("P bytes mismatch", TLSUtility.bytesToHex(P_128), TLSUtility.bytesToHex(p_test));
			assertEquals("G bytes mismatch", TLSUtility.bytesToHex(G_128), TLSUtility.bytesToHex(g_test));
			assertEquals("Y length field and Y size mismatch", length, y_test.length);
	}
}