package com.elitecore.test.dependecy.diameter.packet.avps.threegpp;

import java.util.HashMap;
import java.util.Map;

public class SaiField extends BaseField{

	private static final String NAME = "SAI";
	private int mnc = 0;
	private int mcc = 0;	 
	private int lac = 0;
	private int sac = 0;
	public SaiField(){
	}
	
	@Override
	public String getName() {
		return NAME;
	}

	/**
	 * <p>SAI field</p>
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
	 */
	@Override
	public Map<String, Integer> getFieldValueMap(byte[] valueBuffer) {
		mcc = getMCC(valueBuffer);		
		mnc = getMNC(valueBuffer);		
		
		// Location Area Code (LAC) : a+3 to a+4
		lac  = valueBuffer[4] & 0xFF; 
		lac  = lac << 8;
		lac  = lac | (valueBuffer[5] & 0xFF);

		//Service Area Code (SAC) : a+5 to a+6
		sac  = valueBuffer[6] & 0xFF; 
		sac  = sac << 8;
		sac  = sac | (valueBuffer[7] & 0xFF);
		
		Map<String, Integer> fieldValueMap = new HashMap<String, Integer>();		
		fieldValueMap.put(AvpUserLocationInfo.SAI_MCC, mcc);
		fieldValueMap.put(AvpUserLocationInfo.SAI_MNC, mnc);
		fieldValueMap.put(AvpUserLocationInfo.SAI_LAC, lac);
		fieldValueMap.put(AvpUserLocationInfo.SAI_SAC, sac);
		return fieldValueMap;
	}

	
}
