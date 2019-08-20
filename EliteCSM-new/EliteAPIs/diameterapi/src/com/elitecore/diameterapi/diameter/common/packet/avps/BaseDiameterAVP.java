package com.elitecore.diameterapi.diameter.common.packet.avps;

import static com.elitecore.commons.logging.LogManager.ignoreTrace;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Set;

import javax.annotation.concurrent.NotThreadSafe;

import com.elitecore.diameterapi.diameter.common.util.DiameterDictionary;
import com.elitecore.diameterapi.diameter.common.util.DiameterUtility;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;

/**
 * </b> Functionality Description: </b>
 * 		- Base Class for all AVP type.
 * 		- This class has all the functionality which are common for
 * 		  all AVP types.
 * </b> Start Date: </b>
 * 
 * </b> Modification Log: </b>
 * 
 * @author pulindani
 *
 */
@NotThreadSafe
public abstract class BaseDiameterAVP implements IDiameterAVP,Cloneable{
	
	public static final int STANDARD_AVP_HEADER_LENGTH = 8;
	public static final int VS_AVP_HEADER_LENGTH = 12;

	// Represents attribute id in the form of "vendor-id:avpcode"
	private String strAvpId="";
	
	private byte[] bValueBuffer = new byte[0];
	private int paddingSize = 0;
	private String strAVPEncryption;
	private ByteBuffer header;
	private int intAVPLength;
	private int intVendorId;
	public static final int STANDARD_VENDOR_ID = 0 ;
	
	public BaseDiameterAVP() {
	}
	
	public BaseDiameterAVP(int intAVPCode,int intVendorId ,byte bAVPFlag,String strAvpId,String strAVPEncryption) {
		this.strAvpId = strAvpId;
		this.strAVPEncryption = strAVPEncryption;
		this.intVendorId = intVendorId;
		if (intVendorId != STANDARD_VENDOR_ID ) {
			header = ByteBuffer.allocate(VS_AVP_HEADER_LENGTH);
			header.putInt(8, intVendorId);
			setLength(VS_AVP_HEADER_LENGTH);
		} else {
			header = ByteBuffer.allocate(STANDARD_AVP_HEADER_LENGTH);
			setLength(STANDARD_AVP_HEADER_LENGTH);
		}
		header.putInt(0,intAVPCode);
		header.put(4,(byte)(bAVPFlag & 0xFF));		
		
	}
	/**
	 * Set the byte array argument to the member of byte array.
	 * @param valueBuffer a value buffer to set byte buffer.
	 */
	public void setValueBytes(byte []valueBuffer) {
		this.bValueBuffer = valueBuffer;
		int remainder = 0;
		if(valueBuffer != null) {
			remainder = valueBuffer.length % 4;
		} 
		paddingSize = remainder == 0 ? 0 : 4 - remainder;			
		if(bValueBuffer != null)
			setLength(header.limit() + bValueBuffer.length);
	}

	public byte[] getValueBytes(){
		return bValueBuffer;
	}

	/**
	 * Return Code of Specified AVP.
	 */
	public int getAVPCode() {
		return header.getInt(0);
	}
	
	/**
	 * Retunr length of Specified AVP.
	 */
	public int getLength() {		
		return intAVPLength;
	}
	
	/**
	 * Return padding length of specified AVP
	 */
	public int getPaddingLength() {
		return paddingSize;
	}
	
	
	/**
	 * Set Length of Specified AVP.
	 * @param length Length for Specified AVP.
	 */
	public void setLength(int length) {
		intAVPLength = length;
		DiameterUtility.intToByteArray(header, 5, length, 3);
	}
	
	
	/**
	 * Return flag for specified AVP.
	 */
	public int getFlag() {
		return header.get(4);
	}
	
	/**
	 * Set flag for Speficied AVP.
	 */
	public void setFlag(int flag) {		
		header.put(4,(byte)(flag & 0xFF));
	}
	
	/**
	 * Retunr vendor id of AVP, if vendor id field is available.
	 */
	public int getVendorId() {
		return intVendorId;
	}
	
	public void setTime(Date date) {
	}
	
	public long getTime(){
		return -1;
	}

	public long getInteger(){
		return -1;
	}
	
	public void setInteger(long lValue) {
		
	}
	
	public double getFloat(){
		return -1;
	}
	
	public void setFloat(double lValue) {
	}

	public ArrayList<IDiameterAVP> getGroupedAvp() {
		return null;
	}
	
	public void setGroupedAvp(ArrayList<IDiameterAVP> childAttr) {
		
	}
	
	/**
	 * This method is called by diameter packet class.
	 * When diameter finished reading of header it will give call to this method,
	 * to read bytes onward related to AVP.
	 */
	public int readFlagOnwardsFrom(InputStream sourceStream){
		int valueBytes = 0;
		int totalByte = 0;

		try {
			if (isVendorSpecificAttribute()) {
				valueBytes = intAVPLength - VS_AVP_HEADER_LENGTH;
			} else {
				valueBytes = intAVPLength - STANDARD_AVP_HEADER_LENGTH;
			}

			byte[] bValueBuffer = new byte[valueBytes]; 
			sourceStream.read(bValueBuffer);
			totalByte = totalByte + valueBytes;
			setValueBytes(bValueBuffer);
			
			int remainder = intAVPLength % 4;
			if(remainder > 0) {
				remainder = 4 - remainder;
				sourceStream.skip(remainder);
				totalByte = totalByte + remainder;
			}
			return totalByte;
		}catch(Exception e) {
			return totalByte;
		}
	}
	
	public Object clone() throws CloneNotSupportedException {
		BaseDiameterAVP result = null;
		result = (BaseDiameterAVP)super.clone();
		if( header != null){
			
			byte[] headerBytes = header.array();
			result.header = ByteBuffer.wrap(Arrays.copyOf(headerBytes, headerBytes.length));
		}
		if (bValueBuffer != null){
			result.bValueBuffer = new byte[bValueBuffer.length];
			System.arraycopy(bValueBuffer,0,result.bValueBuffer,0,bValueBuffer.length);
		}
		
		return result;
	}
	
	public String getAVPEncryption() {
		return strAVPEncryption;
	}

	
	public byte[] getBytes(){		
		
		ByteArrayOutputStream temp = new ByteArrayOutputStream(getLength()); 
		try {
			writeTo(temp);
		} catch (IOException e) { 
			ignoreTrace(e);
			throw new AssertionError(e.getMessage());
		}
		
		return temp.toByteArray(); 
	}
	
	@Override
	public void writeTo(OutputStream out) throws IOException {
		out.write(header.array());
		out.write(getValueBytes());
		if (getPaddingLength() > 0) {
			out.write(new byte[getPaddingLength()]);
		}
	}

	public boolean isMandatory() {
		return (((getFlag() & 0xFF) & DiameterUtility.BIT_01000000) > 0);
	}
	
	public boolean isVendorSpecificAttribute() {
		return (((getFlag() & 0xFF) & DiameterUtility.BIT_10000000) > 0);
	}
	
	public boolean isProtected() {
		return (((getFlag() & 0xFF) & DiameterUtility.BIT_00100000) > 0);
	}
	
	public boolean isGrouped() {
		return false;
	}
	public String getStringValue(boolean bUseDictionaryValue) {
		return getStringValue();
	}
	public String getStringValue() {
		try {
			return new String(getValueBytes(),"UTF-8");
		} catch (UnsupportedEncodingException e) {
			return new String(getValueBytes());
		}
	}
	
	@Override
	public String getLogValue(){
		return getStringValue(true);
	}
	public String toString() {
		if(!isVendorSpecificAttribute()){
			if(getAVPCode() == DiameterAVPConstants.USER_PASSWORD_INT)
				return "\t\t" + DiameterDictionary.getInstance().getAttributeName(getAVPCode()) + "(" + getAVPCode()+ ")" + (isMandatory() ? " [M]" : "") + (isVendorSpecificAttribute() ? "[V-" + getVendorId() + "]" : "" ) + (isProtected() ? "[P]" : "") + " = *******";
			else
				return "\t\t" + DiameterDictionary.getInstance().getAttributeName(getAVPCode()) + "(" + getAVPCode()+ ")" + (isMandatory() ? " [M]" : "") + (isVendorSpecificAttribute() ? "[V-" + getVendorId() + "]" : "" ) + (isProtected() ? "[P]" : "") + " = " + getLogValue();
		}
		else
			return "\t\t" + DiameterDictionary.getInstance().getAttributeName(getVendorId(), getAVPCode()) + "(" + getAVPCode()+ ")" + (isMandatory() ? " [M]" : "") + (isVendorSpecificAttribute() ? "[V-" + getVendorId() + "]" : "" ) + (isProtected() ? "[P]" : "") +  " = " + getLogValue();
	}
	
	public boolean equals(Object obj) {
		if(obj == null)
			return false;
		try{
			BaseDiameterAVP avp = (BaseDiameterAVP)obj;
			return  getAVPCode() == avp.getAVPCode() && 
					getVendorId() == avp.getVendorId() &&
					getLength() == avp.getLength() &&
					Arrays.equals(getValueBytes(), avp.getValueBytes());
		} catch (ClassCastException e){ 
			ignoreTrace(e);
			return false;
		}
	}
	
	public void refreshAVPHeader() {
	}
	
	public boolean hasValue() {
		
		if(isVendorSpecificAttribute()) {
			return getLength() > VS_AVP_HEADER_LENGTH;
		}
		return getLength() > STANDARD_AVP_HEADER_LENGTH;
	}
	/**
	 * Sets attribute id in the form of "Vendor-id : AvpCode"
	 */
	public void setAVPId(int vendorId,int AVPCode){
		this.strAvpId = vendorId+":"+AVPCode;
	}
	/**
	 * Returns attribute id in the form of "Vendor-id : AvpCode"
	 */
	public String getAVPId(){
		return this.strAvpId;
	}
	
	public String getKeyStringValue(String key) {
		return null;
	}
	
	public void setKeyStringValue(String key, String value) {
		
	}

	public Set<String> getKeySet() {
		return null;
	}
	
	@Override
	public void doPlus(String value){
		if(value!=null)
			setStringValue(getStringValue() + value);
	}
}
