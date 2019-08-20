package com.elitecore.diameterapi.diameter.common.packet.avps.threegpp;

import java.util.HashMap;
import java.util.Map;



public class TaiField extends BaseField{

	private static final String NAME = "TAI";	
	private int mnc = 0;
	private int mcc = 0;	 
	private int tac = 0;
	public TaiField(){
	}
	
	@Override
	public String getName() {
		return NAME;
	}

	/**
	 * <p>TAI field</p>
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
	 */
	@Override
	public Map<String, Integer> getFieldValueMap(byte[] valueBuffer) {
		mcc = getMCC(valueBuffer);		
		mnc = getMNC(valueBuffer);		
		
		// Tracking Area Code (LAC) : a+3 to a+4
		tac  = valueBuffer[4] & 0xFF; 
		tac  = tac << 8;
		tac  = tac | (valueBuffer[5] & 0xFF);
		
		Map<String, Integer> fieldValueMap = new HashMap<String, Integer>();		
		fieldValueMap.put(AvpUserLocationInfo.TAC_MCC, mcc);
		fieldValueMap.put(AvpUserLocationInfo.TAC_MNC, mnc);
		fieldValueMap.put(AvpUserLocationInfo.TAC_TAC, tac);
		return fieldValueMap;
	}
}
