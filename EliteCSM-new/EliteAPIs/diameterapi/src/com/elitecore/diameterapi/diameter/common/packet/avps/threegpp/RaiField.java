package com.elitecore.diameterapi.diameter.common.packet.avps.threegpp;

import java.util.HashMap;
import java.util.Map;

public class RaiField extends BaseField{

	private static final String NAME = "RAI";
	private int mnc = 0;
	private int mcc = 0;	 
	private int lac = 0;
	private int rac = 0;
	public RaiField(){
	}

	@Override
	public String getName() {
		return NAME;
	}


	/**
	 * <p>RAI field</p>
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
	 */
	@Override
	public Map<String, Integer> getFieldValueMap(byte[] valueBuffer) {
		mcc = getMCC(valueBuffer);		
		mnc = getMNC(valueBuffer);	
		
		// Location Area Code (LAC) : c+3 to c+4
		lac  = valueBuffer[4] & 0xFF; 
		lac  = lac << 8;
		lac  = lac | (valueBuffer[5] & 0xFF);

		//Routing Area Code (RAC) : c+5 to c+6
		rac  = valueBuffer[6] & 0xFF; 
		rac  = rac << 8;
		rac  = rac | (valueBuffer[7] & 0xFF);
		
		Map<String, Integer> fieldValueMap = new HashMap<String, Integer>();		
		fieldValueMap.put(AvpUserLocationInfo.RAI_MCC, mcc);
		fieldValueMap.put(AvpUserLocationInfo.RAI_MNC, mnc);
		fieldValueMap.put(AvpUserLocationInfo.RAI_LAC, lac);
		fieldValueMap.put(AvpUserLocationInfo.RAI_RAC, rac);
		return fieldValueMap;
	}
}
