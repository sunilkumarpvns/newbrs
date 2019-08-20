package com.elitecore.diameterapi.diameter.common.packet.avps.threegpp;

import java.util.HashMap;
import java.util.Map;

public class LaiField extends BaseField{

	private static final String NAME = "LAI";
	private int mnc = 0;
	private int mcc = 0;	 
	private int lac = 0;	
	public LaiField(){
	}

	@Override
	public String getName() {
		return NAME;
	}

	/**
	 * <p>LAI field</p>
	 * The coding of LAI (Location Area Identifier) is depicted in Figure 8.21.6-1.
	 *  <table border="2">
	 *  	<tr>
	 *  		<td></td> <td colspan="8"> Bits </td>
	 *  	</tr>
	 *  	<tr>
	 *  		<td>Octets</td> <td>8</td><td>7</td><td>6</td><td>5</td>   <td>4</td><td>3</td><td>2</td><td>1</td>
	 *  	</tr>
	 *  	<tr>
	 *  		<td>f</td> <td colspan="4"> MCC digit 2 </td>              <td colspan="4"> MCC digit 1 </td>
	 *  	</tr>
	 *  	<tr>
	 *  		<td>f+1</td> <td colspan="4"> MNC digit 3 </td>            <td colspan="4"> MCC digit 3 </td>
	 *  	</tr>
	 *  	<tr>
	 *  		<td>f+2</td> <td colspan="4"> MNC digit 2 </td>            <td colspan="4"> MNC digit 1 </td>
	 *  	</tr>
	 *  	<tr>
	 *  		<td>f+3 to f+4</td> <td colspan="8"> Location Area Code (LAC) </td>
	 *  	</tr>		
	 *  </table>
	 *  <p>Figure 8.21.6-1: LAI field</p>
	 *  
	 *  The Location Area Code (LAC) consists of 2 octets. 
	 *  Bit 8 of Octet f+3 is the most significant bit and bit 1 of Octet f+4 the least significant bit. 
	 *  The coding of the location area code is the responsibility of each administration. 
	 *  Coding using full hexadecimal representation shall be used.  
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
		
		Map<String, Integer> fieldValueMap = new HashMap<String, Integer>();
		
		fieldValueMap.put("LAI_MCC", mcc);
		fieldValueMap.put("LAI_MNC", mnc);
		fieldValueMap.put("LAI_LAC", lac);
		
		return fieldValueMap;	
	}

}
