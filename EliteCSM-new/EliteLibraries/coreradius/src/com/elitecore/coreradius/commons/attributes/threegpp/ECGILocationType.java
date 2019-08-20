package com.elitecore.coreradius.commons.attributes.threegpp;

import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;

/**
 * <p><b>ECGI Location Type</b></p>
	 * The coding of ECGI (E-UTRAN Cell Global Identifier) is depicted in Figure 8.21.5-1. Only zero or one ECGI field shall be present in ULI IE.
	 *  <table border="2">
	 *  	<tr>
	 *  		<td></td> <td colspan="8"> Bits </td>
	 *  	</tr>
	 *  	<tr>
	 *  		<td>Octets</td> <td>8</td><td>7</td><td>6</td><td>5</td>   <td>4</td><td>3</td><td>2</td><td>1</td>
	 *  	</tr>
	 *  	<tr>
	 *  		<td>e</td> <td colspan="4"> MCC digit 2 </td>              <td colspan="4"> MCC digit 1 </td>
	 *  	</tr>
	 *  	<tr>
	 *  		<td>e+1</td> <td colspan="4"> MNC digit 3 </td>            <td colspan="4"> MCC digit 3 </td>
	 *  	</tr>
	 *  	<tr>
	 *  		<td>e+2</td> <td colspan="4"> MNC digit 2 </td>            <td colspan="4"> MNC digit 1 </td>
	 *  	</tr>
	 *  	<tr>
	 *  		<td>e+3</td> <td colspan="4"> spare </td>                  <td colspan="4"> ECI </td>
	 *  	</tr>
	 *		<tr>
	 *  		<td>e+4 to e+6</td> <td colspan="8"> ECI (E-UTRAN Cell Identifier)</td>
	 *  	</tr>
	 *  </table>
	 *  <p>Figure 8.21.5-1: ECGI field</p>
	 *  
	 *  The E-UTRAN Cell Identifier (ECI) consists of 28 bits. 
	 *  The ECI field shall start with Bit 4 of octet e+3, which is the most significant bit. 
	 *  Bit 1 of Octet e+6 is the least significant bit.
	 *  The coding of the E-UTRAN cell identifier is the responsibility of each administration. 
	 *  Coding using full hexadecimal representation shall be used.  
	 *  <p></p> 
 * @author narendra.pathai
 *
 */
class ECGILocationType extends GeographicLocationType{

	private static final int TYPE = 129;
	private static final int SIZE = 7;
	private static final String MNC = "mnc";
	private static final String MCC = "mcc";	 
	private static final String SPARE = "spare";
	private static final String ECI =  "eci";
	private byte[] mnc = DEFAULT_VALUE_IN_BYTES;
	private byte[] mcc = DEFAULT_VALUE_IN_BYTES;	 
	private int spare = DEFAULT_VALUE;
	private int eci =  DEFAULT_VALUE;
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
		valueBytes[4] = (byte) (spare & 0xFF);
		valueBytes[4] = (byte) (valueBytes[4] << 4);
		valueBytes[4] = (byte) (valueBytes[4] | ((eci >> 16) & 0xFF));
		valueBytes[5] = (byte)((eci >> 8) & 0xFF);
		valueBytes[6] = (byte)(eci & 0x000000FF);
		return valueBytes;
	}

	@Override
	public void setBytes(byte[] valueBytes) {
		if(valueBytes.length < SIZE)
			throw new IllegalArgumentException("ECGI-Location-Type: Value bytes are less than required length. Actual Length:" + valueBytes.length + " Required length:" + SIZE);
		
		mcc = getMCC(valueBytes);		
		mnc = getMNC(valueBytes);	
		
		
		spare = valueBytes[4] & LEFT_DIGIT_MASK & 0xFF;
		spare = spare >> 4;
		
		
		eci = valueBytes[4] & 0xFF & RIGHT_DIGIT_MASK ;
		eci = eci << 8;		

		eci = eci | (valueBytes[5] & 0xFF); 
		eci = eci << 8;
		
		eci =  (eci | (valueBytes[6] & 0xFF)); 
	}

	@Override
	public void setStringValue(String[] fieldTokens) {
		mnc = getTokenValueInBytes(fieldTokens,MNC);
		mcc = getTokenValueInBytes(fieldTokens,MCC);
		eci = getTokenValue(fieldTokens,ECI);
		spare = getTokenValue(fieldTokens,SPARE);
	}

	@Override
	public String getStringValue() {
		String AVPAIR_SEPERATOR = Dictionary.getInstance().getVendorAVPairSeparator(RadiusConstants.VENDOR_3GPP_ID);
		StringBuilder builder = new StringBuilder();
		builder.append("type=" + TYPE)
		.append(AVPAIR_SEPERATOR + MNC + "=" + getStringValueForMNCandMCC(mnc))
		.append(AVPAIR_SEPERATOR + MCC + "=" + getStringValueForMNCandMCC(mcc))
		.append(AVPAIR_SEPERATOR + SPARE + "=" + spare)
		.append(AVPAIR_SEPERATOR + ECI + "=" + eci);
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
		else if(fieldName.equalsIgnoreCase(SPARE))
			return String.valueOf(spare);
		else if(fieldName.equalsIgnoreCase(ECI))
			return String.valueOf(eci);
		else
			return null;
	}
	
	@Override
	public String toString(){
		String AVPAIR_SEPERATOR = Dictionary.getInstance().getVendorAVPairSeparator(RadiusConstants.VENDOR_3GPP_ID);
		StringBuilder builder = new StringBuilder();
		builder.append("type=ECGI")
		.append(AVPAIR_SEPERATOR + MNC + "=" + getStringValueForMNCandMCC(mnc))
		.append(AVPAIR_SEPERATOR + MCC + "=" + getStringValueForMNCandMCC(mcc))
		.append(AVPAIR_SEPERATOR + SPARE + "=" + spare)
		.append(AVPAIR_SEPERATOR + ECI + "=" + eci);
		return builder.toString();
	}
}
