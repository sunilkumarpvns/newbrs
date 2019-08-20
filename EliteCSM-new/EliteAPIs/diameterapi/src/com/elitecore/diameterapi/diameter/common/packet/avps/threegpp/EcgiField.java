package com.elitecore.diameterapi.diameter.common.packet.avps.threegpp;

import java.util.HashMap;
import java.util.Map;

public class EcgiField extends BaseField{

	private static final String NAME = "ECGI";
	private int mnc = 0;
	private int mcc = 0;	 
	private int eci = 0;
	private int spare = 0;
	public EcgiField(){
	}

	@Override
	public String getName() {
		return NAME;
	}

	/**
	 * <p>ECGI field</p>
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
	 */
	@Override
	public Map<String, Integer> getFieldValueMap(byte[] valueBuffer) {
		int index = 0;
		mcc = getMCC(valueBuffer);		
		mnc = getMNC(valueBuffer);	
		
		int tmp = 0;
		spare = valueBuffer[index+4] & LEFT_DIGIT_MASK & 0xFF;
		spare = spare >> 4;
		
		tmp=0;
		eci = valueBuffer[index+4] & 0xFF & RIGHT_DIGIT_MASK ;
		eci = eci << 24;		

		tmp = valueBuffer[index+5] & 0xFF; 
		tmp = tmp << 16;
		eci = eci | tmp;
		
		tmp = 0;
		tmp = valueBuffer[index+6] & 0xFF; 
		eci = eci | (tmp << 8);
		eci = eci | (valueBuffer[index+7] & 0xFF);
		
		Map<String, Integer> fieldValueMap = new HashMap<String, Integer>();
		
		fieldValueMap.put(AvpUserLocationInfo.ECGI_MCC, mcc);
		fieldValueMap.put(AvpUserLocationInfo.ECGI_MNC, mnc);
		fieldValueMap.put(AvpUserLocationInfo.ECGI_SPARE, spare);
		fieldValueMap.put(AvpUserLocationInfo.ECGI_ECI, eci);
		
		return fieldValueMap;
	}
}
