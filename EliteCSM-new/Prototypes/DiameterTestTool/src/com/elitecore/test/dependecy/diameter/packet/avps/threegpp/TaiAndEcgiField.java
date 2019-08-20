package com.elitecore.test.dependecy.diameter.packet.avps.threegpp;

import java.util.HashMap;
import java.util.Map;

public class TaiAndEcgiField extends BaseField {

	private static final String NAME = "TAI_ECGI";
	private int taiMnc = 0;
	private int taiMcc = 0;
	private int taiTac = 0;

	private int ecgiMnc = 0;
	private int ecgiMcc = 0;
	private int ecgiSpare = 0;
	private int ecgiEci = 0;


	/**
	 * <p>TAI And ECGI field</p>
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
	 *  	and bit 1 of Octet 11 is the least significant bit.</p>
	 */
	@Override
	public Map<String, Integer> getFieldValueMap(byte[] valueBuffer) {		
		taiMcc = getMCC(valueBuffer);		
		taiMnc = getMNC(valueBuffer);		
		taiTac  = valueBuffer[4] & 0xFF; 
		taiTac  = taiTac << 8;
		taiTac  = taiTac | (valueBuffer[5] & 0xFF);

		ecgiMcc = getMCC(valueBuffer,5);		
		ecgiMnc = getMNC(valueBuffer,5);	
		ecgiSpare = valueBuffer[9] & RIGHT_DIGIT_MASK & 0xFF;
		ecgiSpare = ecgiSpare >> 4;

		int tmp=0;
		ecgiEci = valueBuffer[9] & LEFT_DIGIT_MASK & 0xFF;
		ecgiEci = ecgiEci << 24;		

		tmp = valueBuffer[10] & 0xFF; 
		tmp = tmp << 16;
		ecgiEci = ecgiEci | tmp;

		tmp = 0;
		tmp = valueBuffer[11] & 0xFF; 
		ecgiEci = ecgiEci | (tmp << 8);
		ecgiEci = ecgiEci | (valueBuffer[12] & 0xFF);

		Map<String, Integer> fieldValueMap = new HashMap<String, Integer>();

		fieldValueMap.put(AvpUserLocationInfo.TAC_MCC, taiMcc);
		fieldValueMap.put(AvpUserLocationInfo.TAC_MNC, taiMnc);
		fieldValueMap.put(AvpUserLocationInfo.TAC_TAC, taiTac);

		fieldValueMap.put(AvpUserLocationInfo.ECGI_MCC, ecgiMcc);
		fieldValueMap.put(AvpUserLocationInfo.ECGI_MNC, ecgiMnc);
		fieldValueMap.put(AvpUserLocationInfo.ECGI_SPARE, ecgiSpare);
		fieldValueMap.put(AvpUserLocationInfo.ECGI_ECI, ecgiEci);

		return fieldValueMap;	
	}

	@Override
	public String getName() {
		return NAME;
	}

}
