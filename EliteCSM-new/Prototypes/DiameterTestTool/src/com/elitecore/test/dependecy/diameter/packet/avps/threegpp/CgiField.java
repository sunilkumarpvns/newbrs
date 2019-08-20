package com.elitecore.test.dependecy.diameter.packet.avps.threegpp;

import java.util.HashMap;
import java.util.Map;

public class CgiField extends BaseField{

	private static final String NAME = "CGI";
	private int mnc = 0;
	private int mcc = 0;	 
	private int lac = 0;
	private int ci = 0;
	public CgiField(){
	}

	/**
	 * <p>CGI field</p>
	 * The coding of CGI (Cell Global Identifier) is depicted in Figure 8.21.1-1. Only zero or one CGI field shall be present in ULI IE.
	 *  <table border="2">
	 *  	<tr>
	 *  		<td></td> <td colspan="8"> Bits </td>
	 *  	</tr>
	 *  	<tr>
	 *  		<td>Octets</td> <td>8</td><td>7</td><td>6</td><td>5</td>   <td>4</td><td>3</td><td>2</td><td>1</td>
	 *  	</tr>
	 *  	<tr>
	 *  		<td>a</td> <td colspan="4"> MCC digit 2 </td>              <td colspan="4"> MCC digit 1 </td>
	 *  	</tr>
	 *  	<tr>
	 *  		<td>a+1</td> <td colspan="4"> MNC digit 3 </td>            <td colspan="4"> MCC digit 3 </td>
	 *  	</tr>
	 *  	<tr>
	 *  		<td>a+2</td> <td colspan="4"> MNC digit 2 </td>            <td colspan="4"> MNC digit 1 </td>
	 *  	</tr>
	 *  	<tr>
	 *  		<td>a+3 to a+4</td> <td colspan="8"> Location Area Code (LAC) </td>
	 *  	</tr>
	 *		<tr>
	 *  		<td>a+5 to a+6</td> <td colspan="8"> Cell Identity (CI) </td>
	 *  	</tr>
	 *  </table>
	 *  <p>Figure 8.21.1-1: CGI field</p>
	 *  
	 *  The Location Area Code (LAC) consists of 2 octets. 
	 *  Bit 8 of Octet a+3 is the most significant bit and bit 1 of Octet a+4 the least significant bit. 
	 *  The coding of the location area code is the responsibility of each administration. 
	 *  Coding using full hexadecimal representation shall be used. The Cell Identity (CI) consists of 2 octets. 
	 *  Bit 8 of Octet a+5 is the most significant bit and bit 1 of Octet a+6 the least significant bit. 
	 *  <p></p> 
	 */
	@Override
	public Map<String, Integer> getFieldValueMap(byte []valueBuffer){
		// valueBuffer[0]; Specifies the type of a Field	
		mcc = getMCC(valueBuffer);
		
		mnc = getMNC(valueBuffer);
		
		// Location Area Code (LAC) : a+3 to a+4
		lac  = valueBuffer[4] & 0xFF; 
		lac  = lac << 8;
		lac  = lac | (valueBuffer[5] & 0xFF);
		
		
		//Cell Identity (CI) : a+5 to a+6
		ci  = valueBuffer[6] & 0xFF; 
		ci  = ci << 8;
		ci  = ci | (valueBuffer[7] & 0xFF);
		
		Map<String, Integer> fieldValueMap = new HashMap<String, Integer>();
		fieldValueMap.put(AvpUserLocationInfo.CGI_MCC, mcc);
		fieldValueMap.put(AvpUserLocationInfo.CGI_MNC, mnc);
		fieldValueMap.put(AvpUserLocationInfo.CGI_LAC, lac);
		fieldValueMap.put(AvpUserLocationInfo.CGI_CI, ci);
		
		return fieldValueMap;
	}
	
	@Override
	public String getName() {
		return NAME;
	}

}
