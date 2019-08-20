/**
 * 
 */
package com.elitecore.test.dependecy.diameter.packet.avps.threegpp;


import com.elitecore.test.dependecy.diameter.DiameterUtility;
import com.elitecore.test.dependecy.diameter.packet.avps.basic.AvpOctetString;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 
 * <header>3GPP-User-Location-Info</header>
 * 
 * <p>3GPP Type: 22</p>
 * 
 * <p>Length=m, where m depends on the Geographic Location Type
 * For example, m= 10 in the CGI and SAI types.</p>
 * 
 * <p>Geographic Location Type field is used to convey what type of location information is present in the 'Geographic Location' field. For GGSN, the Geographic Location Type values and coding are as defined in 3GPP TS 29.060 [24]. For P-GW, the Geographic Location Type values and coding are defined as follows:
 * <table>
 * 	<tr>
 * 		<td>0</td> <td>CGI</td>
 * 	</tr>
 * 	<tr>
 * 		<td>1</td> <td>SAI</td>
 * 	</tr>
 * 	<tr>
 * 		<td>2</td> <td>RAI</td>
 * 	</tr>
 * 	<tr>
 * 		<td>3-127</td> <td>Spare for future use</td>
 * 	</tr>
 * 	<tr>
 * 		<td>128</td> <td>TAI</td>
 * 	</tr>
 * 	<tr>
 * 		<td>129</td> <td>ECGI</td>
 * 	</tr>
 * 	<tr>
 * 		<td>130</td> <td>TAI and ECGI</td>
 * 	</tr>
 * 	<tr>
 * 		<td>131-255</td> <td>Spare for future use *</td>
 * 	</tr>
 *
 * </table>
 * (As per 3GPP TS 29.061 V10.3.0 (2011-06))
 * </p>
 * @author jatin
 *
 */
public class AvpUserLocationInfo extends AvpOctetString {
	
	/**
	 * @param intAVPCode
	 * @param intVendorId
	 * @param bAVPFlag
	 * @param strAvpId
	 * @param strAVPEncryption
	 */
	public static final String LOCATION_TYPE = "Location-Type";
	
	public static final String CGI_MCC = "CGI-MCC";
	public static final String CGI_MNC = "CGI-MNC";
	public static final String CGI_LAC = "CGI-LAC";
	public static final String CGI_CI = "CGI-CI";
	
	public static final String SAI_MCC = "SAI-MCC";
	public static final String SAI_MNC = "SAI-MNC";
	public static final String SAI_LAC = "SAI-LAC"; 
	public static final String SAI_SAC = "SAI-SAC";
	
	public static final String RAI_MCC = "RAI-MCC";
	public static final String RAI_MNC = "RAI-MNC";
	public static final String RAI_LAC = "RAI-LAC"; 
	public static final String RAI_RAC = "RAI-RAC";

	public static final String TAC_MCC = "TAC-MCC";
	public static final String TAC_MNC = "TAC-MNC";
	public static final String TAC_TAC = "TAC-TAC"; 

	public static final String ECGI_MCC = "ECGI-MCC";
	public static final String ECGI_MNC = "ECGI-MNC";
	public static final String ECGI_SPARE = "ECGI-SPARE"; 
	public static final String ECGI_ECI = "ECGI-ECI";

	private static final CgiField cgiField= new CgiField();
	private static final SaiField saiField = new SaiField();
	private static final RaiField raiField =  new RaiField();
	private static final TaiField taiField =  new TaiField();
	private static final EcgiField ecgiField =  new EcgiField();
	private static final TaiAndEcgiField taiAndEcgiField =  new TaiAndEcgiField();
	
	private int iType = 0;
	private boolean isSupportedField = true;
	private Map<String,Integer>fieldsMap;
	
	public AvpUserLocationInfo(int intAVPCode, int intVendorId, byte bAVPFlag,
			String strAvpId, String strAVPEncryption) {
		super(intAVPCode, intVendorId, bAVPFlag, strAvpId, strAVPEncryption);
		fieldsMap = new HashMap<String, Integer>();		
	}
	/**
	 *  Fields
	 *  0 CGI
	 *  1 SAI
	 *  2 RAI
     * 	3-127 Spare for future use
     *	128 TAI
     *	129 ECGI
     *	130 TAI and ECGI
     *	131-255 Spare for future use
	 */
	private void setFields(){
		byte [] valueBuffer = getValueBytes();
		
		iType = valueBuffer[0] &  0xFF;
		switch (TGPPLocationField.getField(iType)) {
		
		case CGI:	
			this.fieldsMap.putAll(cgiField.getFieldValueMap(valueBuffer));			
			break;
			
		case SAI:
			this.fieldsMap.putAll(saiField.getFieldValueMap(valueBuffer));
			break;
			
		case RAI:				
			this.fieldsMap.putAll(raiField.getFieldValueMap(valueBuffer));
			break;
			
		case TAI:
			this.fieldsMap.putAll(taiField.getFieldValueMap(valueBuffer));
			break;
			
		case ECGI:
			this.fieldsMap.putAll(ecgiField.getFieldValueMap(valueBuffer));
			break;

		case TAI_AND_ECGI:			
			this.fieldsMap.putAll(taiAndEcgiField.getFieldValueMap(valueBuffer));
			break;

		default: // Spare for future use
			isSupportedField = false;
			break;
		}
		
		this.fieldsMap.put(LOCATION_TYPE, iType);
	}
	
	@Override
	public int readFlagOnwardsFrom(InputStream sourceStream){
		int iBytes = super.readFlagOnwardsFrom(sourceStream);
		setFields();		
		return iBytes;
		 
	}
	public void setValueBytes(byte []valueBuffer) {
		super.setValueBytes(valueBuffer);
		setFields();		
	}
	
	public String getKeyStringValue(String key) {
		String value = String.valueOf(this.fieldsMap.get(key));
		if(value == null || value.equalsIgnoreCase("NULL")) {			 
			byte valueByte[] = this.getValueBytes();			
			byte[] result = new byte[valueByte.length-1];
			System.arraycopy(valueByte, 1, result, 0, valueByte.length-1);		
			return DiameterUtility.bytesToHex(result);
		}		
		return value;
	}
	@Override
	public final String getLogValue(){
		return "Location-Type = " + TGPPLocationField.fieldName(iType) + " (" + iType + ")" + ",Location-Info = " + (  this.isSupportedField ?  this.fieldsMap : getStringValue(true));
	}
	
	@Override
	public Set<String> getKeySet(){
		return this.fieldsMap.keySet();
	}
	
}
