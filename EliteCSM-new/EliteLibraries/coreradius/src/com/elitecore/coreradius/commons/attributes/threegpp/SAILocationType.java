package com.elitecore.coreradius.commons.attributes.threegpp;

import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;

/**
 * <p><b>SAI Location type</b></p>
	 * The coding of SAI (Service Area Identifier) is depicted in Figure 8.21.2-1. Only zero or one SAI field shall be present in ULI IE.
	 *  <table border="2">
	 *  	<tr>
	 *  		<td></td> <td colspan="8"> Bits </td>
	 *  	</tr>
	 *  	<tr>
	 *  		<td>Octets</td> <td>8</td><td>7</td><td>6</td><td>5</td>   <td>4</td><td>3</td><td>2</td><td>1</td>
	 *  	</tr>
	 *  	<tr>
	 *  		<td>b</td> <td colspan="4"> MCC digit 2 </td>              <td colspan="4"> MCC digit 1 </td>
	 *  	</tr>
	 *  	<tr>
	 *  		<td>b+1</td> <td colspan="4"> MNC digit 3 </td>            <td colspan="4"> MCC digit 3 </td>
	 *  	</tr>
	 *  	<tr>
	 *  		<td>b+2</td> <td colspan="4"> MNC digit 2 </td>            <td colspan="4"> MNC digit 1 </td>
	 *  	</tr>
	 *  	<tr>
	 *  		<td>b+3 to b+4</td> <td colspan="8"> Location Area Code (LAC) </td>
	 *  	</tr>
	 *		<tr>
	 *  		<td>b+5 to b+6</td> <td colspan="8"> Service Area Code (SAC) </td>
	 *  	</tr>
	 *  </table>
	 *  <p>Figure 8.21.2-1: SAI field</p>
	 *  
	 *  The Location Area Code (LAC) consists of 2 octets. 
	 *  Bit 8 of Octet b+3 is the most significant bit and bit 1 of Octet b+4 the least significant bit. 
	 *  The coding of the location area code is the responsibility of each administration. 
	 *  Coding using full hexadecimal representation shall be used.
	 *  The Service Area Code (SAC) consists of 2 octets. 
	 *  Bit 8 of Octet b+5 is the most significant bit and bit 1 of Octet b+6 the least significant bit. 
	 *  The SAC is defined by the operator. See 3GPP TS 23.003 [2] section 12.5 for more information.  
	 *  <p></p> 
 * @author narendra.pathai
 *
 */
class SAILocationType extends GeographicLocationType{

	private static final int TYPE = 1;
	private static final int SIZE = 8;
	private static final String MNC = "mnc";
	private static final String MCC = "mcc";	 
	private static final String LAC = "lac";
	private static final String SAC = "sac";
	private byte[] mnc = DEFAULT_VALUE_IN_BYTES;
	private byte[] mcc = DEFAULT_VALUE_IN_BYTES;	 
	private int lac = DEFAULT_VALUE;
	private int sac =  DEFAULT_VALUE;
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
		valueBytes[4] = (byte) ((lac >> 8) & 0xFF);
		valueBytes[5] = (byte)(lac & 0xFF);
		valueBytes[6] = (byte)((sac >> 8) & 0xFF);
		valueBytes[7] = (byte)(sac & 0xFF);
		return valueBytes;
	}

	@Override
	public void setBytes(byte[] valueBytes) {
		if(valueBytes.length < SIZE)
			throw new IllegalArgumentException("SAI-Location-Type: Value bytes are less than required length. Actual Length:" + valueBytes.length + " Required length:" + SIZE);
		
		mcc = getMCC(valueBytes);		
		mnc = getMNC(valueBytes);		
		
		// Location Area Code (LAC) : a+3 to a+4
		lac  = valueBytes[4] & 0xFF; 
		lac  = lac << 8;
		lac  = lac | (valueBytes[5] & 0xFF);

		//Service Area Code (SAC) : a+5 to a+6
		sac  = valueBytes[6] & 0xFF; 
		sac  = sac << 8;
		sac  = sac | (valueBytes[7] & 0xFF);
	}

	@Override
	public void setStringValue(String[] fieldTokens) {
		mnc = getTokenValueInBytes(fieldTokens,MNC);
		mcc = getTokenValueInBytes(fieldTokens,MCC);
		sac = getTokenValue(fieldTokens,SAC);
		lac = getTokenValue(fieldTokens,LAC);
	}

	@Override
	public String getStringValue() {
		String AVPAIR_SEPERATOR = Dictionary.getInstance().getVendorAVPairSeparator(RadiusConstants.VENDOR_3GPP_ID);
		StringBuilder builder = new StringBuilder();
		builder.append("type=" + TYPE)
		.append(AVPAIR_SEPERATOR + MNC + "=" + getStringValueForMNCandMCC(mnc))
		.append(AVPAIR_SEPERATOR + MCC + "=" + getStringValueForMNCandMCC(mcc))
		.append(AVPAIR_SEPERATOR + LAC + "=" + lac)
		.append(AVPAIR_SEPERATOR + SAC + "=" + sac);
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
		else if(fieldName.equalsIgnoreCase(LAC))
			return String.valueOf(lac);
		else if(fieldName.equalsIgnoreCase(SAC))
			return String.valueOf(sac);
		else
			return null;
	}
	
	@Override
	public String toString(){
		String AVPAIR_SEPERATOR = Dictionary.getInstance().getVendorAVPairSeparator(RadiusConstants.VENDOR_3GPP_ID);
		StringBuilder builder = new StringBuilder();
		builder.append("type=SAI")
		.append(AVPAIR_SEPERATOR + MNC + "=" + getStringValueForMNCandMCC(mnc))
		.append(AVPAIR_SEPERATOR + MCC + "=" + getStringValueForMNCandMCC(mcc))
		.append(AVPAIR_SEPERATOR + LAC + "=" + lac)
		.append(AVPAIR_SEPERATOR + SAC + "=" + sac);
		return builder.toString();
	}
}
