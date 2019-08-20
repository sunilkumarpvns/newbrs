package com.elitecore.coreradius.commons.attributes.threegpp;

import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;

/**
 * <p><b>TAI Location type</b></p>
	 * The coding of TAI (Tracking Area Identity) is depicted in Figure 8.21.4-1. Only zero or one TAI field shall be present in ULI IE.
	 *  <table border="2">
	 *  	<tr>
	 *  		<td></td> <td colspan="8"> Bits </td>
	 *  	</tr>
	 *  	<tr>
	 *  		<td>Octets</td> <td>8</td><td>7</td><td>6</td><td>5</td>   <td>4</td><td>3</td><td>2</td><td>1</td>
	 *  	</tr>
	 *  	<tr>
	 *  		<td>d</td> <td colspan="4"> MCC digit 2 </td>              <td colspan="4"> MCC digit 1 </td>
	 *  	</tr>
	 *  	<tr>
	 *  		<td>d+1</td> <td colspan="4"> MNC digit 3 </td>            <td colspan="4"> MCC digit 3 </td>
	 *  	</tr>
	 *  	<tr>
	 *  		<td>d+2</td> <td colspan="4"> MNC digit 2 </td>            <td colspan="4"> MNC digit 1 </td>
	 *  	</tr>
	 *  	<tr>
	 *  		<td>d+3 to d+4</td> <td colspan="8"> Tracking Area Code (TAC) </td>
	 *  	</tr>
	 *  </table>
	 *  <p>Figure 8.21.4-1: TAI</p>
	 *  
	 *  The Tracking Area Code (TAC) consists of 2 octets. 
	 *  Bit 8 of Octet d+3 is the most significant bit and bit 1 of Octet d+4 the least significant bit.
	 *  The coding of the tracking area code is the responsibility of each administration. 
	 *  Coding using full hexadecimal representation shall be used.
	 *  <p></p> 
 * @author narendra.pathai
 *
 */
class TAILocationType extends GeographicLocationType{

	private static final int TYPE = 128;
	private static final int SIZE = 6;
	private static final String MNC = "mnc";
	private static final String MCC = "mcc";	 
	private static final String TAC = "tac";
	private byte[] mnc = DEFAULT_VALUE_IN_BYTES;
	private byte[] mcc = DEFAULT_VALUE_IN_BYTES;	 
	private int tac = DEFAULT_VALUE;
	
	@Override
	public byte[] getBytes() {
		byte[] valueBytes = new byte[SIZE];

		//now adding all the fetched bytes to actual value bytes
		valueBytes[0] = (byte) TYPE;
		valueBytes[1] = mcc[1];
		valueBytes[1] = (byte) (((valueBytes[1] << 4) & 0xF0) | mcc[2]);
		valueBytes[2] = (byte) (mcc[0] | (mnc[0]<<4));
		valueBytes[3] = mnc[1];
		valueBytes[3] = (byte) ((valueBytes[3]<< 4) | mnc[2]);
		valueBytes[4] = (byte) ((tac >> 8) & 0xFF);
		valueBytes[5] = (byte)(tac & 0xFF);
		return valueBytes;
	}

	@Override
	public void setBytes(byte[] valueBytes) {
		if(valueBytes.length < SIZE)
			throw new IllegalArgumentException("TAI-Location-Type: Value bytes are less than required length. Actual Length:" + valueBytes.length + " Required length:" + SIZE);
		
		mcc = getMCC(valueBytes);		
		mnc = getMNC(valueBytes);		
		
		// Tracking Area Code (LAC) : a+3 to a+4
		tac  = valueBytes[4] & 0xFF; 
		tac  = tac << 8;
		tac  = tac | (valueBytes[5] & 0xFF);
	}

	@Override
	public void setStringValue(String[] fieldTokens) {
		mnc = getTokenValueInBytes(fieldTokens,MNC);
		mcc = getTokenValueInBytes(fieldTokens,MCC);
		tac = getTokenValue(fieldTokens,TAC);
	}

	@Override
	public String getStringValue() {
		String AVPAIR_SEPERATOR = Dictionary.getInstance().getVendorAVPairSeparator(RadiusConstants.VENDOR_3GPP_ID);
		StringBuilder builder = new StringBuilder();
		builder.append("type=" + TYPE)
		.append(AVPAIR_SEPERATOR + MNC + "=" + getStringValueForMNCandMCC(mnc))
		.append(AVPAIR_SEPERATOR + MCC + "=" + getStringValueForMNCandMCC(mcc))
		.append(AVPAIR_SEPERATOR + TAC + "=" + tac);
		return builder.toString();
	}

	@Override
	public String getFieldValue(String fieldName) {
		if(fieldName == null || fieldName.trim().length() == 0)
			return null;
		
		fieldName = fieldName.trim();
		if(fieldName.equalsIgnoreCase(MNC))
			return getStringValueForMNCandMCC(mnc);
		else if(fieldName.equalsIgnoreCase(MCC))
			return getStringValueForMNCandMCC(mcc);
		else if(fieldName.equalsIgnoreCase(TAC))
			return String.valueOf(tac);
		else
			return null;
	}
	
	@Override
	public String toString(){
		String AVPAIR_SEPERATOR = Dictionary.getInstance().getVendorAVPairSeparator(RadiusConstants.VENDOR_3GPP_ID);
		StringBuilder builder = new StringBuilder();
		builder.append("type=TAI")
		.append(AVPAIR_SEPERATOR + MNC + "=" + getStringValueForMNCandMCC(mnc))
		.append(AVPAIR_SEPERATOR + MCC + "=" + getStringValueForMNCandMCC(mcc))
		.append(AVPAIR_SEPERATOR + TAC + "=" + tac);
		return builder.toString();
	}
}
