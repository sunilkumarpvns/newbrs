package com.elitecore.coreradius.commons.attributes.threegpp;

import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;

	/**
	 *  
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
	 *  	<tr>
	 *  		<td>d+5</td> <td colspan="4"> MCC digit 2 </td>              <td colspan="4"> MCC digit 1 </td>
	 *  	</tr>
	 *  	<tr>
	 *  		<td>d+6</td> <td colspan="4"> MNC digit 3 </td>            <td colspan="4"> MCC digit 3 </td>
	 *  	</tr>
	 *  	<tr>
	 *  		<td>d+7</td> <td colspan="4"> MNC digit 2 </td>            <td colspan="4"> MNC digit 1 </td>
	 *  	</tr>
	 *  	<tr>
	 *  		<td>d+8</td> <td colspan="4"> spare </td>                  <td colspan="4"> ECI </td>
	 *  	</tr>
	 *		<tr>
	 *  		<td>d+9 to d+11</td> <td colspan="8"> ECI (E-UTRAN Cell Identifier)</td>
	 *  	</tr>
	 *  </table>
	 *  
	 *  <p>For TAC: Bit 8 of Octet d+3 is the most significant bit and bit 1 of Octet d+4 the least significant bit</p> 
	 *  <p>For ECI: ECI field shall start with Bit 4 of octet 8, which is the most significant bit
	 *  
	 *  and bit 1 of Octet 11 is the least significant bit.</p>
	 * 
	 * @author narendra.pathai
	 *
	 */
class TAIandECGILocationType extends GeographicLocationType{

	private static final int TYPE = 130;
	private static final int SIZE = 13;
	private static final String TAI_MNC = "taimnc";
	private static final String TAI_MCC = "taimcc";	 
	private static final String TAI_TAC = "taitac";
	private static final String ECGI_MNC = "ecgimnc";
	private static final String ECGI_MCC = "ecgimcc";	 
	private static final String ECGI_SPARE = "ecgispare";
	private static final String ECGI_ECI =  "ecgieci";
	private byte[] taiMnc = DEFAULT_VALUE_IN_BYTES;
	private byte[] taiMcc = DEFAULT_VALUE_IN_BYTES;	 
	private int taiTac = DEFAULT_VALUE;
	private byte[] ecgiMnc = DEFAULT_VALUE_IN_BYTES;
	private byte[] ecgiMcc = DEFAULT_VALUE_IN_BYTES;	 
	private int ecgiSpare = DEFAULT_VALUE;
	private int ecgiEci =  DEFAULT_VALUE;
	
	@Override
	public byte[] getBytes() {
		byte[] valueBytes = new byte[SIZE];
		
		//now adding all the fetched bytes to actual value bytes
		valueBytes[0] = (byte) TYPE;
		valueBytes[1] = taiMcc[1];
		valueBytes[1] = (byte) (((valueBytes[1] << 4) & 0xF0) | taiMcc[2]);
		valueBytes[2] = (byte) (taiMcc[0] | (taiMnc[0]<<4));
		valueBytes[3] = taiMnc[1];
		valueBytes[3] = (byte) ((valueBytes[3]<< 4) | taiMnc[2]);
		valueBytes[4] = (byte) ((taiTac >> 8) & 0xFF);
		valueBytes[5] = (byte)(taiTac & 0xFF);
		valueBytes[6] = ecgiMcc[1];
		valueBytes[6] = (byte) (((valueBytes[6] << 4) & 0xF0) | ecgiMcc[2]);
		valueBytes[7] = (byte) (ecgiMcc[0] | (ecgiMnc[0]<<4));
		valueBytes[8] = ecgiMnc[1];
		valueBytes[8] = (byte) ((valueBytes[8]<< 4) | ecgiMnc[2]);
		valueBytes[9] = (byte) (ecgiSpare & 0xFF);
		valueBytes[9] = (byte) (valueBytes[9] << 4);
		valueBytes[9] = (byte) (valueBytes[9] | ((ecgiEci >> 24) & 0xFF));
		valueBytes[10] = (byte)((ecgiEci >> 16) & 0xFF);
		valueBytes[11] = (byte)(ecgiEci >> 8 & 0xFF);
		valueBytes[12] = (byte)(ecgiEci & 0x000000FF);
		return valueBytes;
	}

	@Override
	public void setBytes(byte[] valueBytes) {
		if(valueBytes.length < SIZE)
			throw new IllegalArgumentException("TAIAndECGI-Location-Type: Value bytes are less than required length. Actual Length:" + valueBytes.length + " Required length:" + SIZE);
		taiMcc = getMCC(valueBytes);		
		taiMnc = getMNC(valueBytes);		
		taiTac  = valueBytes[4] & 0xFF; 
		taiTac  = taiTac << 8;
		taiTac  = taiTac | (valueBytes[5] & 0xFF);

		ecgiMcc = getMCC(valueBytes,5);		
		ecgiMnc = getMNC(valueBytes,5);	
		ecgiSpare = valueBytes[9] & RIGHT_DIGIT_MASK & 0xFF;
		ecgiSpare = ecgiSpare >> 4;
		
		ecgiEci = valueBytes[9] & 0xFF & RIGHT_DIGIT_MASK ;
		ecgiEci = ecgiEci << 8;		

		ecgiEci = ecgiEci | (valueBytes[10] & 0xFF); 
		ecgiEci = ecgiEci << 8;
		
		ecgiEci =  (ecgiEci | (valueBytes[11] & 0xFF));
		ecgiEci = ecgiEci << 8;
		
		ecgiEci = (ecgiEci | (valueBytes[12] & 0xFF));
	}

	@Override
	public void setStringValue(String[] fieldTokens) {
		taiMnc = getTokenValueInBytes(fieldTokens,TAI_MNC);
		taiMcc = getTokenValueInBytes(fieldTokens,TAI_MCC);
		taiTac = getTokenValue(fieldTokens,TAI_TAC);
		ecgiMnc = getTokenValueInBytes(fieldTokens,ECGI_MNC);
		ecgiMcc = getTokenValueInBytes(fieldTokens,ECGI_MCC);
		ecgiEci = getTokenValue(fieldTokens,ECGI_ECI);
		ecgiSpare = getTokenValue(fieldTokens,ECGI_SPARE);
	}

	@Override
	public String getStringValue() {
		String AVPAIR_SEPERATOR = Dictionary.getInstance().getVendorAVPairSeparator(RadiusConstants.VENDOR_3GPP_ID);
		StringBuilder builder = new StringBuilder();
		builder.append("type=" + TYPE)
		.append(AVPAIR_SEPERATOR + TAI_MNC + "=" + getStringValueForMNCandMCC(taiMnc))
		.append(AVPAIR_SEPERATOR + TAI_MCC + "=" + getStringValueForMNCandMCC(taiMcc))
		.append(AVPAIR_SEPERATOR + TAI_TAC + "=" + taiTac)
		.append(AVPAIR_SEPERATOR + ECGI_MNC + "=" + getStringValueForMNCandMCC(ecgiMnc))
		.append(AVPAIR_SEPERATOR + ECGI_MCC + "=" + getStringValueForMNCandMCC(ecgiMcc))
		.append(AVPAIR_SEPERATOR + ECGI_SPARE + "=" + ecgiSpare)
		.append(AVPAIR_SEPERATOR + ECGI_ECI + "=" + ecgiEci);
		return builder.toString();
	}

	@Override
	public String getFieldValue(String fieldName) {
		if(fieldName == null || fieldName.trim().length() == 0)
			return null;
		
		fieldName = fieldName.trim();
		if(fieldName.equalsIgnoreCase(TAI_MNC))
			return getStringValueForMNCandMCC(taiMnc);
		else if(fieldName.equalsIgnoreCase(TAI_MCC))
			return getStringValueForMNCandMCC(taiMcc);
		else if(fieldName.equalsIgnoreCase(TAI_TAC))
			return String.valueOf(taiTac);
		else if(fieldName.equalsIgnoreCase(ECGI_MNC))
			return getStringValueForMNCandMCC(ecgiMnc);
		else if(fieldName.equalsIgnoreCase(ECGI_MCC))
			return getStringValueForMNCandMCC(ecgiMcc);
		else if(fieldName.equalsIgnoreCase(ECGI_SPARE))
			return String.valueOf(ecgiSpare);
		else if(fieldName.equalsIgnoreCase(ECGI_ECI))
			return String.valueOf(ecgiEci);
		else
			return null;
	}
	
	@Override
	public String toString(){
		String AVPAIR_SEPERATOR = Dictionary.getInstance().getVendorAVPairSeparator(RadiusConstants.VENDOR_3GPP_ID);
		StringBuilder builder = new StringBuilder();
		builder.append("type=TAI_ECGI")
		.append(AVPAIR_SEPERATOR + TAI_MNC + "=" + getStringValueForMNCandMCC(taiMnc))
		.append(AVPAIR_SEPERATOR + TAI_MCC + "=" + getStringValueForMNCandMCC(taiMcc))
		.append(AVPAIR_SEPERATOR + TAI_TAC + "=" + taiTac)
		.append(AVPAIR_SEPERATOR + ECGI_MNC + "=" + getStringValueForMNCandMCC(ecgiMnc))
		.append(AVPAIR_SEPERATOR + ECGI_MCC + "=" + getStringValueForMNCandMCC(ecgiMcc))
		.append(AVPAIR_SEPERATOR + ECGI_SPARE + "=" + ecgiSpare)
		.append(AVPAIR_SEPERATOR + ECGI_ECI + "=" + ecgiEci);
		return builder.toString();
	}
}
