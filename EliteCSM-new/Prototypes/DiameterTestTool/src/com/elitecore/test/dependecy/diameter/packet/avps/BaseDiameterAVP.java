package com.elitecore.test.dependecy.diameter.packet.avps;

import com.elitecore.test.dependecy.diameter.DiameterAVPConstants;
import com.elitecore.test.dependecy.diameter.DiameterDictionary;
import com.elitecore.test.dependecy.diameter.DiameterUtility;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Set;

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
public abstract class BaseDiameterAVP implements IDiameterAVP,Cloneable{
	private int intAVPCode;
	private byte bAVPFlag;
	private int intAVPLength;
	
	// Represents attribute id in the form of "vendor-id:avpcode"
	private String strAvpId="";
	
	private int intVendorId ;
	private byte[] bValueBuffer = new byte[0];
	private byte[] bPaddingBuffer = new byte[0];
	private String strAVPEncryption;
	public static final int STANDARD_VENDOR_ID = 0 ;
	
	private static final String MODULE = "Base Diameter AVP";
	
	public BaseDiameterAVP() {
		// TODO Auto-generated constructor stub
	}
	
	public BaseDiameterAVP(int intAVPCode,int intVendorId ,byte bAVPFlag,String strAvpId,String strAVPEncryption) {
		this.intAVPCode = intAVPCode;
		this.intVendorId = intVendorId;
		this.bAVPFlag  = bAVPFlag;
		this.strAvpId = strAvpId;
		this.strAVPEncryption = strAVPEncryption;
	}
	/**
	 * Set the byte array argument to the member of byte array.
	 * @param valueBuffer a value buffer to set byte buffer.
	 */
	public void setValueBytes(byte []valueBuffer) {
		this.bValueBuffer = valueBuffer;
		
		int remender,restByte;
		remender = valueBuffer.length % 4;
		if(remender != 0){
			restByte = 4 - remender;
			bPaddingBuffer = new byte[restByte];
			for(int cnt=0;cnt<restByte;cnt++) {
				bPaddingBuffer[cnt]=0;
			}
		}else {
			bPaddingBuffer = new byte[0];
		}
		
/*		if(isVendorSpecificAttribute()) {
			setLength(valueBuffer.length + 12);
		}else {
			setLength(valueBuffer.length + 8);
		}
*/
	}

	public byte[] getValueBytes(){
		return bValueBuffer;
	}

	/**
	 * Set padding data.
	 * @param paddingBuffer a byte buffer for padding data.
	 */
	public void setPaddingBytes(byte [] paddingBuffer){
		this.bPaddingBuffer = paddingBuffer;
	}
	
	/**
	 * Return padding data as byte array.
	 */
	public byte[] getPaddingBytes(){
		return bPaddingBuffer;
	}
	
	/**
	 * Return Code of Specified AVP.
	 */
	public int getAVPCode() {
		return intAVPCode;
	}
	
	/**
	 * Retunr length of Specified AVP.
	 */
	public int getLength() {
		intAVPLength = 8;
		if(intVendorId > 0 ) {
			intAVPLength += 4;
		}
		intAVPLength += bValueBuffer.length;
		return intAVPLength;
	}
	
	/**
	 * Return padding length of specified AVP
	 */
	public int getPaddingLength() {
		if(bPaddingBuffer != null)
			return bPaddingBuffer.length;
		else
			return 0;
	}
	
	
	/**
	 * Set Length of Specified AVP.
	 * @param length Length for Specified AVP.
	 */
	public void setLength(int length) {
		intAVPLength = length;
	}
	
	
	/**
	 * Return flag for specified AVP.
	 */
	public int getFlag() {
		return bAVPFlag;
	}
	
	/**
	 * Set flag for Speficied AVP.
	 */
	public void setFlag(int flag) {
		bAVPFlag = (byte)flag;
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
	
	public void setGroupedAvp(ArrayList childAttr) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * This method is called by diameter packet class.
	 * When diameter finished reading of header it will give call to this method,
	 * to read bytes onward related to AVP.
	 */
	public int readFlagOnwardsFrom(InputStream sourceStream){
		int restByte=0;
		int totalByte=0;
		int remender = 0;
		byte []paddingBuffer;

		try {
			if(intVendorId != 0)
				restByte = intAVPLength - (totalByte + 12);
			else
				restByte = intAVPLength - (totalByte + 8);

			bValueBuffer = new byte[restByte]; 
			sourceStream.read(bValueBuffer);
			
			totalByte = totalByte + restByte;
			if((intAVPLength % 4) != 0) {
				remender = intAVPLength % 4;
				restByte = 4 - remender;
				paddingBuffer = new byte[restByte];
				for(int cnt=0; cnt < paddingBuffer.length;cnt++)
					paddingBuffer[cnt] = 0;
				this.setPaddingBytes(paddingBuffer);
				sourceStream.read(paddingBuffer);
				totalByte = totalByte + restByte;
			}
			return totalByte;
		}catch(Exception e) {
			return totalByte;
		}
	}
	
	public Object clone() throws CloneNotSupportedException {
		BaseDiameterAVP result = null;
		result = (BaseDiameterAVP)super.clone();
		if (bValueBuffer != null){
			result.bValueBuffer = new byte[bValueBuffer.length];
			System.arraycopy(bValueBuffer,0,result.bValueBuffer,0,bValueBuffer.length);
		}
		
		if(bPaddingBuffer != null) {
			result.bPaddingBuffer = new byte[bPaddingBuffer.length];
			System.arraycopy(bPaddingBuffer,0,result.bPaddingBuffer,0,bPaddingBuffer.length);
		}
		return result;
	}
	
	public String getAVPEncryption() {
		return strAVPEncryption;
	}

	
	public byte[] getBytes(){
		int currentBytePosition = 0;
		byte[] totalBytes;
		if(getVendorId() != STANDARD_VENDOR_ID ){
			totalBytes = new byte[8 + 4 + bValueBuffer.length + bPaddingBuffer.length];
		}else{
			totalBytes = new byte[8 + bPaddingBuffer.length + bValueBuffer.length];
		}		
		
		byte []bAVPCode = DiameterUtility.intToByteArray(intAVPCode);
		
		for(int i=0 ; i < bAVPCode.length; i++) {
			totalBytes[currentBytePosition] = bAVPCode[i];
			currentBytePosition++;
		}

		totalBytes[currentBytePosition] = (byte)(getFlag() & 0xFF);
		currentBytePosition++;
		
		byte []bAVPLength = DiameterUtility.intToByteArray(this.getLength(),3);

		for(int i=0 ; i < bAVPLength.length; i++) {
			totalBytes[currentBytePosition] = bAVPLength[i];
			currentBytePosition++;
		}

		if(intVendorId != 0){
			byte []bVendorId = DiameterUtility.intToByteArray(intVendorId);

			for(int i=0 ; i < bVendorId.length; i++) {
				totalBytes[currentBytePosition] = bVendorId[i];
				currentBytePosition++;
			}
		}
		System.arraycopy(bValueBuffer,0,totalBytes,currentBytePosition,bValueBuffer.length);
		currentBytePosition += bValueBuffer.length;
		
		if(bPaddingBuffer != null) {
			System.arraycopy(bPaddingBuffer,0,totalBytes,currentBytePosition,bPaddingBuffer.length);
			currentBytePosition += bPaddingBuffer.length;
		}
		
		return totalBytes; 
	}
	
	public boolean isMandatory() {
		return (((bAVPFlag & 0xFF) & DiameterUtility.BIT_01000000) > 0);
	}
	
	public boolean isVendorSpecificAttribute() {
		return (((bAVPFlag & 0xFF) & DiameterUtility.BIT_10000000) > 0);
	}
	
	public boolean isProtected() {
		return (((bAVPFlag & 0xFF) & DiameterUtility.BIT_00100000) > 0);
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
		if(intVendorId == 0){
			if(intAVPCode == DiameterAVPConstants.USER_PASSWORD_INT)
				return "\t\t" + DiameterDictionary.getInstance().getAttributeName(intAVPCode) + "(" + getAVPCode()+ ")" + (isMandatory() ? " [M]" : "") + (isVendorSpecificAttribute() ? "[V-" + getVendorId() + "]" : "" ) + (isProtected() ? "[P]" : "") + " = *******";
			else
				return "\t\t" + DiameterDictionary.getInstance().getAttributeName(intAVPCode) + "(" + getAVPCode()+ ")" + (isMandatory() ? " [M]" : "") + (isVendorSpecificAttribute() ? "[V-" + getVendorId() + "]" : "" ) + (isProtected() ? "[P]" : "") + " = " + getLogValue();
		}
		else
			return "\t\t" + DiameterDictionary.getInstance().getAttributeName(intVendorId, intAVPCode) + "(" + getAVPCode()+ ")" + (isMandatory() ? " [M]" : "") + (isVendorSpecificAttribute() ? "[V-" + getVendorId() + "]" : "" ) + (isProtected() ? "[P]" : "") +  " = " + getLogValue();
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
			return false;
		}
	}
	
	public void refreshAVPHeader() {
	}
	
	public boolean hasValue() {
		if(getLength() < 8)
			return false;			
		return true;
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
