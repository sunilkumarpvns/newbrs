/*
 *	EAP Project
 *	Elitecore Technologies Ltd.
 *	904, Silicon Tower, Law Garden
 *	Ahmedabad, India - 380009
 *
 *	Created on Nov 7, 2008
 *	Created By Devang Adeshara
 */
package com.elitecore.coreeap.packet.types.md5;

import java.io.UnsupportedEncodingException;

import com.elitecore.coreeap.commons.util.constants.CommonConstants;
import com.elitecore.coreeap.packet.InvalidEAPTypeException;
import com.elitecore.coreeap.packet.types.EAPExpandedType;
import com.elitecore.coreeap.util.constants.EapTypeConstants;
import com.elitecore.coreeap.util.constants.VendorSpecificEapTypeConstants;


/** 
*  Represents the Expanded EAP Type MD5-Challenge in EAP Packet.
*  MD5-Challenge is a Method Specific Expanded EAP Type.
*  @author Elitecore Technologies Ltd.
*/
public class MD5ChallengeExpandedEAPType extends EAPExpandedType {

	private int valueSize;
	private byte[] value;
	private byte[] name;
	
	public MD5ChallengeExpandedEAPType(){
		super();
		this.setType(EapTypeConstants.EXPANDED.typeId);
		this.setVendorID(VendorSpecificEapTypeConstants.IETF_VENDOR_ID.typeId);
		this.setVendorType(EapTypeConstants.MD5_CHALLENGE.typeId);
		this.valueSize = 0;
		this.value = new byte[0];
		this.name = new byte[0];
	}
	
	public MD5ChallengeExpandedEAPType(byte[] byteArray) throws InvalidEAPTypeException{
		parseBytes(byteArray);
	}
	
	protected void parseBytes(byte[] byteArray) throws InvalidEAPTypeException{
		
		if(byteArray.length < 8){
			throw new InvalidEAPTypeException("Invalid EAP Type : length less then minimum required length");
		}else{
			int tempType = (int)byteArray[0] & 0xff;
			if( tempType == EapTypeConstants.EXPANDED.typeId){
				this.setType(tempType);
			}else{
				throw new InvalidEAPTypeException("Invalid EAP Type Value : Not the value for Expanded EAP Type");
			}
			
			int tempVendorID = (int)byteArray[1];
			tempVendorID = tempVendorID << 8;
			tempVendorID = tempVendorID | (int)byteArray[2];
			tempVendorID = tempVendorID << 8;
			tempVendorID = tempVendorID | (int)byteArray[3];
			
			if(tempVendorID == VendorSpecificEapTypeConstants.IETF_VENDOR_ID.typeId){
				this.setVendorID(tempVendorID);
			}else{
				throw new InvalidEAPTypeException("Invalid VendorID : Not the VendorID of IETF");
			}
			
			long tempVendorType = (long)byteArray[4];
			tempVendorType = tempVendorType << 8;
			tempVendorType = tempVendorType | (long)byteArray[5];
			tempVendorType = tempVendorType << 8;
			tempVendorType = tempVendorType | (long)byteArray[6];
			tempVendorType = tempVendorType << 8;
			tempVendorType = tempVendorType | (long)byteArray[7];
			
			if(tempVendorType == EapTypeConstants.MD5_CHALLENGE.typeId){
				this.setVendorType(tempVendorType);
			}else{
				throw new InvalidEAPTypeException("Invalid VendorType : Not MD5-Challenge");
			}
			
			if(byteArray.length > 8){
				byte[] tempData = new byte[byteArray.length - 8];
				System.arraycopy(byteArray, 8, tempData, 0, byteArray.length - 8);
				this.readDataBytes(tempData);
			}
			
			/*this.valueSize = (int)byteArray[8] & 0xff;
			this.value = new byte[this.valueSize];
			//System.out.println(this.valueSize);
			System.arraycopy(byteArray, 9, this.value, 0, this.valueSize);
			//System.out.println(new String(this.value)+" length : "+this.value.length);
			this.name = new byte[byteArray.length - (this.valueSize + 9)];
			System.arraycopy(byteArray, this.valueSize + 9, this.name, 0, byteArray.length - (this.value.length + 9));
			//System.out.println(new String(this.name)+ " length : " + this.name.length);
			*/
		}
	}
	
	public byte[] toBytes(){
		
		byte[] returnBytes = new byte[this.name.length + this.value.length + 9];
		
		returnBytes[0] = (byte)this.getType();
		
		returnBytes[3] = (byte)this.getVendorID();
		returnBytes[2] = (byte)(this.getVendorID() >>> 8);
		returnBytes[1] = (byte)(this.getVendorID() >>> 16);
		
		returnBytes[7] = (byte)this.getVendorType();
		returnBytes[6] = (byte)(this.getVendorType() >>> 8);
		returnBytes[5] = (byte)(this.getVendorType() >>> 16);
		returnBytes[4] = (byte)(this.getVendorType() >>> 24);
		
		returnBytes[8] = (byte)this.valueSize;
		System.arraycopy(this.value, 0, returnBytes, 9, this.value.length);
		System.arraycopy(this.name, 0, returnBytes, this.value.length + 9, this.name.length);
		
		return returnBytes;
	}
	
	public String toString(){
		
		StringBuilder returnString = new StringBuilder();
		returnString.append("[ Type=");
		returnString.append(this.getVendorID());
		returnString.append(":");
		returnString.append(this.getVendorType());
		returnString.append(", Value-Size=");
		returnString.append(this.getValueSize());
		returnString.append(", Value=");
		if (this.getValue()!=null && this.getValue().length>0){
			try{
				returnString.append(new String(this.getValue(),CommonConstants.UTF8));
			}catch(UnsupportedEncodingException e){
				returnString.append(new String(this.getValue()));
			}
		}
		returnString.append(", Name=");
		if (this.getName()!=null && this.getName().length>0){
			try{
				returnString.append(new String(this.getName(),CommonConstants.UTF8));
			}catch(UnsupportedEncodingException e){
				returnString.append(new String(this.getName()));
			}
		}
		returnString.append(" ]");
		return returnString.toString();
	}
	
	public void readDataBytes(byte[] byteArray) throws InvalidEAPTypeException {
		if(byteArray.length >= 3){
			this.valueSize = (int)byteArray[0] & 0xff;
			this.value = new byte[this.valueSize];
			System.arraycopy(byteArray, 1, this.value, 0, this.valueSize);
			this.name = new byte[byteArray.length - (this.valueSize + 1)];
			System.arraycopy(byteArray, this.valueSize + 1, this.name, 0, byteArray.length - (this.value.length + 1));
		}
	}

	public void resetValueSize(){
		this.valueSize = this.value.length;
	}

	public byte[] getName() {
		return this.name;
	}

	public void setName(byte[] name) {
		this.name = name;
	}

	public byte[] getValue() {
		return this.value;
	}

	public void setValue(byte[] value) {
		this.value = value;
	}

	public int getValueSize() {
		return this.valueSize;
	}

	public void setValueSize(int valueSize) {
		this.valueSize = valueSize;
	}

	@Override
	public byte[] getData() {
		byte[] returnDataBytes = new byte[this.value.length + this.name.length + 1];
		
		returnDataBytes[0] = (byte)this.valueSize;
		System.arraycopy(this.value, 0, returnDataBytes, 1, this.value.length);
		System.arraycopy(this.name, 0, returnDataBytes, this.value.length + 1, this.name.length);
		return returnDataBytes;
	}

	@Override
	public void setData(byte[] data) {
		this.valueSize = data[0] & 0xff;
		System.arraycopy(data, 1, this.value, 0, this.valueSize);
		System.arraycopy(data, this.valueSize, this.name, 0, data.length - (this.value.length + 1));
	}
	
	/* (non-Javadoc)
	 * @see com.elitecore.radius.eap.packet.EAPExpandedType#clone()
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		MD5ChallengeExpandedEAPType newObject = (MD5ChallengeExpandedEAPType)super.clone();
		if (this.value!=null){
			newObject.value = new byte[this.value.length];
			System.arraycopy(this.value, 0, newObject.value, 0, this.value.length);
		}
		if (this.name!=null){
			newObject.name = new byte[this.name.length];
			System.arraycopy(this.name, 0, newObject.name, 0, this.name.length);
		}
		return newObject;
	}

}
