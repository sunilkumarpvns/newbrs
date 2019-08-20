package com.elitecore.coreradius.commons.attributes.threegpp;

import java.io.IOException;
import java.io.InputStream;

import com.elitecore.coreradius.commons.attributes.AttributeId;
import com.elitecore.coreradius.commons.attributes.TextAttribute;

/**
 * This attribute is of AV-Pair type. The total size in bytes of this attribute depends on 
 * the Location Type which is stored as a member <code>GeographicLocationType</code>
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
 * 
 * @author narendra.pathai
 *
 */
public class UserLocationInfoAttribute extends TextAttribute{
	private static final long serialVersionUID = 1L;
	transient private GeographicLocationType locationType;

	public UserLocationInfoAttribute() {
		locationType = GeographicLocationType.UNKNOWN;
		bAvpair = true;
	}
	
	public UserLocationInfoAttribute(AttributeId radiusAttributeDetail) {
		super(radiusAttributeDetail);
		locationType = GeographicLocationType.UNKNOWN;
		bAvpair = true;
	}

	@Override
	public void setValueBytes(byte[] buffer){
		locationType = GeographicLocationType.getInstanceByBytes(buffer);
		super.setValueBytes(locationType.getBytes());
	}
	
	@Override
	public String toString(){
		return String.valueOf(" " + locationType);
	}
	
	@Override
	public String getKeyValue(String key) {
		return locationType.getFieldValue(key) + "";
	}
	
	@Override
	public void setStringValue(String data) {
		locationType = GeographicLocationType.getInstanceByAVPair(data);
		super.setValueBytes(locationType.getBytes());
	}
	
	@Override
	public int readLengthOnwardsFrom(InputStream sourceStream)
			throws IOException {
		int read = super.readLengthOnwardsFrom(sourceStream);
		setValueBytes(super.getValueBytes());
		return read;
	}
	
	@Override
	public void doPlus(String value) {
		//This operation is not supported for this attribute
	}
	
	@Override
	public int readFrom(InputStream sourceStream) throws IOException {
		int read = super.readFrom(sourceStream);
		setValueBytes(super.getValueBytes());
		return read;
	}
	
	@Override
	public String getStringValue() {
		return locationType.getStringValue();
	}
}
