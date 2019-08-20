package com.elitecore.coreeap.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.coreeap.commons.util.constants.CommonConstants;
import com.elitecore.coreeap.util.constants.UserIdentityPrefixTypes;


public class Utility {

	public static final String MODULE = "EAP UTILITY";
	public static int readint(byte[] byteData,int startPosition,int Byte)
	{
		int number=0,iCounter=0;
		for(iCounter=0;iCounter<Byte ; iCounter++)
		{
			number = number << 8;
			number =  (int)number | (byteData[startPosition + iCounter] & 0xFF);			
		}		
		return (number);
	}

	/***
	 * This function will display byte[] in Hexadecimal form. 
	 * if the data is null then it will simply print "null"
	 * @param data[]
	 * @return Hexadecimal form of the data[] in String format.
	 */
	public static String bytesToHex(byte[] data) {		
        StringBuilder buf = new StringBuilder();
        if(data == null)
        {
        	buf.append("null");
        }
        else
        {
        	buf.append("0x");
        	for (int i = 0; i < data.length; i++) {
        		buf.append(byteToHex(data[i]));
        	}
        }
        return (buf.toString());
    }

	/***
	 * this function will never use from other class. it is only call from the
	 * TLSUtility.bytesToHex(byte[] data) function.
	 * @param data
	 * @return Hexdecimal of a given byte in a String format.
	 */
	private static String byteToHex(byte data) {
		StringBuilder buf = new StringBuilder();
		buf.append(toHexChar((data >>> 4) & 0x0F));
		buf.append(toHexChar(data & 0x0F));
		return buf.toString();
	}
	
	/***
	 * this funtion will never use from other class.it is only call from the 
	 * TLSUtility.byteToHex(byte data) function.
	 * @param iNumber
	 * @return Hexadecimal of a given number in Char format.
	 */
	private static char toHexChar(int iNumber) {
        if ((0 <= iNumber) && (iNumber <= 9)) {
            return (char) ('0' + iNumber);
        } else {
            return (char) ('a' + (iNumber - 10));
        }
    }
	
	public static MessageDigest getMessageDigest(String algorithm){
		MessageDigest msgDigest = null;
		try {
			try {
				msgDigest = (MessageDigest)MessageDigest.getInstance(algorithm,CommonConstants.SUN).clone();
			} catch (NoSuchProviderException e) {
				msgDigest = (MessageDigest)MessageDigest.getInstance(algorithm).clone();
			}
		} catch (NoSuchAlgorithmException e) {
			LogManager.getLogger().trace(MODULE, e);
		} catch (CloneNotSupportedException e) {
			LogManager.getLogger().trace(MODULE, e);
		}
		return msgDigest;
	}
	
	public static int bytesToInteger(byte[] bytes){
		if(bytes == null)
			return 0;
		
		int val = 0;
		for(byte b : bytes){
			val <<= 8;
			val |= b & 0xFF;
		}
		
		return val;
	}

	public static String getIMSIFromIdentity(String identity){
		if (identity == null){
			return identity;
		}
		String imsi = null, domain = null, msin = identity;
		try {
			if (identity.indexOf('@') != -1){
				msin = identity.substring(0,identity.indexOf('@'));
				domain = identity.substring(identity.indexOf('@')+ 1 );
			}

			if (msin.length() == 16 && UserIdentityPrefixTypes.isPrefixed(msin)){
				imsi = msin.substring(1);
			} else if (msin.length() < 11 && domain != null){
				String mcc = "";
				String mnc = "";
				int mccIndex = domain.indexOf("mcc");
				if (mccIndex!=-1){
					mcc = domain.substring(mccIndex + 3, mccIndex + 6);
				}
				int mncIndex = domain.indexOf("mnc");
				if (mncIndex != -1){
					mnc = domain.substring(mncIndex + 3, mncIndex  +6);
					if (mnc.startsWith("0")){
						mnc = mnc.substring(1);
					}
				}
				imsi = mcc + mnc + msin;
				if (imsi.length() < 15){
					int noOfZeros = 15 - imsi.length();
					imsi = mcc + mnc;
					for (int i=0 ; i<noOfZeros ; i++){
						imsi += "0";
					}
					imsi += msin;
				}
			} else {
				imsi = msin;
			}
		} catch (Exception e) {
			if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "Failed to generate IMSI from Identity: " + identity + ". Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
			return identity;
		}
		return imsi;
	}
	
	public static String formatMsisdn(String msisdn, int msisdnLength, String mcc) {
		if(msisdn == null) {
			return null;
		}
		//TODO verify with devang -monica.lulla
		int mncIndex = msisdn.length() - msisdnLength;
		if (mncIndex < 0) {
			return null;
		} 
		if (mncIndex == 0) {
			return msisdn;
		}
		if (mcc == null){
			return msisdn.substring(mncIndex);
		}
		int mccIndex = mncIndex - mcc.length();
		if (mccIndex < 0) {
			return null;
		}
		if (msisdn.regionMatches(mccIndex, mcc, 0, mcc.length())) {
			return msisdn.substring(mncIndex);
		}
		return null;
	}
}
