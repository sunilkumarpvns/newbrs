/**
 * 
 */
package com.elitecore.diameterapi.core.common.util;

/**
 * @author pulin
 *
 */
public class APIUtility {

	public static byte[] intToByteArray (final int integer) {
		int byteNum = (40 - Integer.numberOfLeadingZeros (integer < 0 ? ~integer : integer)) / 8;
		byte[] byteArray = new byte[4];
		
		for (int n = 0; n < byteNum; n++){
			byteArray[3 - n] = (byte) (integer >>> (n * 8));
		}
		return (byteArray);
	}
}
