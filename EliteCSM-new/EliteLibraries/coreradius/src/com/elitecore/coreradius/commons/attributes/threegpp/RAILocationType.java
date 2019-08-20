package com.elitecore.coreradius.commons.attributes.threegpp;

import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;

/**
 *  <p><b>RAI Location type</b></p>
	 * The coding of RAI (Routing Area Identity) is depicted in Figure 8.21.3-1. Only zero or one RAI field shall be present in ULI IE.
	 *  <table border="2">
	 *  	<tr>
	 *  		<td></td> <td colspan="8"> Bits </td>
	 *  	</tr>
	 *  	<tr>
	 *  		<td>Octets</td> <td>8</td><td>7</td><td>6</td><td>5</td>   <td>4</td><td>3</td><td>2</td><td>1</td>
	 *  	</tr>
	 *  	<tr>
	 *  		<td>c</td> <td colspan="4"> MCC digit 2 </td>              <td colspan="4"> MCC digit 1 </td>
	 *  	</tr>
	 *  	<tr>
	 *  		<td>c+1</td> <td colspan="4"> MNC digit 3 </td>            <td colspan="4"> MCC digit 3 </td>
	 *  	</tr>
	 *  	<tr>
	 *  		<td>c+2</td> <td colspan="4"> MNC digit 2 </td>            <td colspan="4"> MNC digit 1 </td>
	 *  	</tr>
	 *  	<tr>
	 *  		<td>c+3 to c+4</td> <td colspan="8"> Location Area Code (LAC) </td>
	 *  	</tr>
	 *		<tr>
	 *  		<td>c+5 to c+6</td> <td colspan="8"> Routing Area Code (RAC) </td>
	 *  	</tr>
	 *  </table>
	 *  <p>Figure 8.21.3-1: RAI field</p>
	 *  
	 *  The Location Area Code (LAC) consists of 2 octets. 
	 *  Bit 8 of Octet c+3 is the most significant bit and bit 1 of Octet c+4 the least significant bit. 
	 *  Coding using full hexadecimal representation shall be used.
	 *	The Routing Area Code (RAC) consists of 2 octets. Only Octet c+5 contains the RAC. 
	 *  Octet c+6 is coded as all 1's (11111111). The RAC is defined by the operator.  
	 *  <p></p> 
 * @author narendra.pathai
 *
 */
class RAILocationType extends GeographicLocationType{

	private static final int TYPE = 2;
	private static final int SIZE = 8;
	private static final String MNC = "mnc";
	private static final String MCC = "mcc";	 
	private static final String LAC = "lac";
	private static final String RAC = "rac";
	private byte[] mnc = DEFAULT_VALUE_IN_BYTES;
	private byte[] mcc = DEFAULT_VALUE_IN_BYTES;	 
	private int lac = DEFAULT_VALUE;
	//                               c+5     c+6
	//the default value for RAC is 0000000011111111
	private int rac =  255;
	
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
		valueBytes[6] = (byte)((rac >> 8) & 0xFF);
		valueBytes[7] = (byte)(rac & 0xFF);
		return valueBytes;
	}

	@Override
	public void setBytes(byte[] valueBytes) {
		if(valueBytes.length < SIZE)
			throw new IllegalArgumentException("RAI-Location-Type: Value bytes are less than required length. Actual Length:" + valueBytes.length + " Required length:" + SIZE);
		mcc = getMCC(valueBytes);		
		mnc = getMNC(valueBytes);	
		
		// Location Area Code (LAC) : c+3 to c+4
		lac  = valueBytes[4] & 0xFF; 
		lac  = lac << 8;
		lac  = lac | (valueBytes[5] & 0xFF);

		//Routing Area Code (RAC) : c+5 to c+6
		rac  = valueBytes[6] & 0xFF; 
		rac  = rac << 8;
		rac  = rac | (valueBytes[7] & 0xFF);
	}

	@Override
	public void setStringValue(String[] fieldTokens) {
		mnc = getTokenValueInBytes(fieldTokens,MNC);
		mcc = getTokenValueInBytes(fieldTokens,MCC);
		rac = getTokenValue(fieldTokens,RAC);
		if(rac > 255){
			throw new IllegalArgumentException("RAI-Location-Type: Value of RAC cannot be more than 255");
		}
		//as the Least significant byte is always FF
		rac = rac << 8 | 0xFF;
		
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
		.append(AVPAIR_SEPERATOR + RAC + "=" + (getActualValueOfRACField(rac)));
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
		else if(fieldName.equalsIgnoreCase(RAC))
			return String.valueOf(getActualValueOfRACField(rac));
		else
			return null;
	}
	
	@Override
	public String toString(){
		String AVPAIR_SEPERATOR = Dictionary.getInstance().getVendorAVPairSeparator(RadiusConstants.VENDOR_3GPP_ID);
		StringBuilder builder = new StringBuilder();
		builder.append("type=RAI")
		.append(AVPAIR_SEPERATOR + MNC + "=" + getStringValueForMNCandMCC(mnc))
		.append(AVPAIR_SEPERATOR + MCC + "=" + getStringValueForMNCandMCC(mcc))
		.append(AVPAIR_SEPERATOR + LAC + "=" + lac)
		.append(AVPAIR_SEPERATOR + RAC + "=" + getActualValueOfRACField(rac));
		return builder.toString();
	}
	
	private int getActualValueOfRACField(int value){
		return (rac >> 8) & 0xFF;
}
}
