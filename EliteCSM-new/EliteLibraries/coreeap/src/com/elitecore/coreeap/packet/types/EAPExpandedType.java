/*
 *	EAP Project
 *	Elitecore Technologies Ltd.
 *	904, Silicon Tower, Law Garden
 *	Ahmedabad, India - 380009
 *
 *	Created on Nov 7, 2008
 *	Created By Devang Adeshara
 */
package com.elitecore.coreeap.packet.types;
/**
*  Represents the Expanded EAP Type in EAP Packet.
*  Any class that represents the Method Specific Expanded EAP Type will extend this class.
* @author Elitecore Technologies Ltd.
*/
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import com.elitecore.coreeap.dictionary.EAPTypeDictionary;
import com.elitecore.coreeap.packet.InvalidEAPTypeException;
import com.elitecore.coreeap.util.constants.EapTypeConstants;
import com.elitecore.coreeap.util.constants.VendorSpecificEapTypeConstants;

public class EAPExpandedType extends EAPType { 

	private int vendorId;
	private long vendorType;
	private Collection<EAPExpandedType> otherEAPTypes;
	
	public EAPExpandedType(){
		super(EapTypeConstants.EXPANDED.typeId);
		this.vendorId = VendorSpecificEapTypeConstants.IETF_VENDOR_ID.typeId;
		this.vendorType = 0;
		this.otherEAPTypes = new ArrayList<EAPExpandedType>();
	}
	
	public EAPExpandedType(byte[] byteArray) throws InvalidEAPTypeException {
		super(byteArray);
		parseBytes(byteArray);
	}
	
	protected void parseBytes(byte[] byteArray) throws InvalidEAPTypeException{

		// The number of bytes in the Expanded EAP Type MUST be 8 or more than 8.
		if(byteArray.length < 8){
			throw new InvalidEAPTypeException("Invalid EAP Type : length less then minimum required length");
		}else{
			this.setType((int)byteArray[0] & 0xff);
			
			this.vendorId = byteArray[1];
			this.vendorId = this.vendorId << 8;
			this.vendorId = this.vendorId | (int)byteArray[2];
			this.vendorId = this.vendorId << 8;
			this.vendorId = this.vendorId | (int)byteArray[3];
			
			this.vendorType = byteArray[4];
			this.vendorType = this.vendorType << 8;
			this.vendorType = this.vendorType | (long)byteArray[5];
			this.vendorType = this.vendorType << 8;
			this.vendorType = this.vendorType | (long)byteArray[6];
			this.vendorType = this.vendorType << 8;
			this.vendorType = this.vendorType | (long)byteArray[7];
			
			if (byteArray.length>8){
				byte[] tempData = new byte[byteArray.length - 8];
				System.arraycopy(byteArray, 8, tempData, 0, byteArray.length - 8);
				this.readDataBytes(tempData);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see com.elitecore.radius.eap.packet.EAPType#readDataBytes(byte[])
	 */
	
	//@Override
	public void readDataBytes(byte[] byteArray) throws InvalidEAPTypeException {
		if (byteArray.length>0){
			
			/* 
			 * Parse each alternate method and put it in collecion.
			 * In this case, the data-field will not contain anything.
			 */
			if(this.vendorId == VendorSpecificEapTypeConstants.IETF_VENDOR_ID.typeId 
					&& this.vendorType == EapTypeConstants.NAK.typeId){ 
				if (byteArray.length % 8 != 0){
					throw new InvalidEAPTypeException(
							"Invalid EAP-Expanded NAK Type");
				}
				
				otherEAPTypes = new ArrayList<EAPExpandedType>();
				
				byte[] eachNAKAltData = new byte[8];
				EAPExpandedType eachNAKAltType = null;
				EAPTypeDictionary typeFactory = EAPTypeDictionary.getInstance();
				for(int i=0; i<byteArray.length; i+=8){
					System.arraycopy(byteArray, i, eachNAKAltData, 0, 8);
					int type = eachNAKAltData[0] & 0xff;
					if (type==EapTypeConstants.EXPANDED.typeId){
						int vendorId = eachNAKAltData[1];
						vendorId = vendorId << 8;
						vendorId = vendorId | (int)eachNAKAltData[2];
						vendorId = vendorId << 8;
						vendorId = vendorId | (int)eachNAKAltData[3];
						
						long vendorType = eachNAKAltData[4];
						vendorType = vendorType << 8;
						vendorType = vendorType | (long)eachNAKAltData[5];
						vendorType = vendorType << 8;
						vendorType = vendorType | (long)eachNAKAltData[6];
						vendorType = vendorType << 8;
						vendorType = vendorType | (long)eachNAKAltData[7];
						eachNAKAltType = typeFactory.createEAPExpandedType(vendorId, vendorType);
						if (eachNAKAltType!=null){
							this.otherEAPTypes.add(eachNAKAltType);
						}
					}else{
						throw new InvalidEAPTypeException("Invalid EAP-NAK-Alternative method");
					}
				}
			}else{
				this.setData(byteArray);
			}
		}else{
			this.setData(new byte[0]);
		}
	}
	
	public byte[] toBytes(){
		byte[] headerBytes = new byte[8];
		
		headerBytes[0] = (byte)this.getType();
		
		headerBytes[3] = (byte)this.vendorId;
		headerBytes[2] = (byte)(this.vendorId >>> 8);
		headerBytes[1] = (byte)(this.vendorId >>> 16);
		
		headerBytes[7] = (byte)this.vendorType;
		headerBytes[6] = (byte)(this.vendorType >>> 8);
		headerBytes[5] = (byte)(this.vendorType >>> 16);
		headerBytes[4] = (byte)(this.vendorType >>> 24);
		
		byte[] data = new byte[0];
		
		if (this.vendorId==VendorSpecificEapTypeConstants.IETF_VENDOR_ID.typeId && this.vendorType==EapTypeConstants.NAK.typeId){
			if (this.otherEAPTypes!=null && !this.otherEAPTypes.isEmpty()){
				data = new byte[this.otherEAPTypes.size()*8];
				Iterator<EAPExpandedType> itrOtherEAPTypes = this.otherEAPTypes.iterator();
				EAPExpandedType eachType = null;
				int counter = 0;
				while(itrOtherEAPTypes.hasNext()){
					eachType = itrOtherEAPTypes.next();
					System.arraycopy(eachType.toBytes(), 0, data, counter*8, 8);
					counter++;
				}
			}
		}else if (this.getData()!=null && this.getData().length>0){
			data = this.getData();
		}
		
		byte[] typeBytes = new byte[8+data.length];
		System.arraycopy(headerBytes, 0, typeBytes, 0, 8);
		if (data.length>0){
			System.arraycopy(data, 0, typeBytes, 8, data.length);
		}
		
		return typeBytes;
	}
	
	public String toString(){
		
		StringBuilder returnStringBuilder = new StringBuilder();
		returnStringBuilder.append("[ Type=");
		returnStringBuilder.append(this.vendorId);
		returnStringBuilder.append(":");
		returnStringBuilder.append(this.vendorType);
		returnStringBuilder.append(", Data=");
		returnStringBuilder.append(new String(this.getData()));
		if((this.vendorId == VendorSpecificEapTypeConstants.IETF_VENDOR_ID.typeId && this.vendorType == EapTypeConstants.NAK.typeId) && (otherEAPTypes != null && !otherEAPTypes.isEmpty())){
			returnStringBuilder.append(", Alternate-Methods=");
			returnStringBuilder.append(otherEAPTypes.toString());
		}
		returnStringBuilder.append(" ]");
		return returnStringBuilder.toString();
	}
	
	public int getVendorID() {
		return this.vendorId;
	}
	
	public long getVendorType() {
		return this.vendorType;
	}
	
	public Collection<EAPExpandedType> getOtherEAPTypes() {
		return this.otherEAPTypes;
	}
	
	public void setVendorID(int vendorID) {
		this.vendorId = vendorID;
	}
	
	public void setVendorType(long vendorType) {
		this.vendorType = vendorType;
	}
	
	public boolean addOtherEAPType(EAPExpandedType eapType) {
		if (this.otherEAPTypes==null){
			this.otherEAPTypes = new ArrayList<EAPExpandedType>();
		}
		return this.otherEAPTypes.add(eapType);
	}
	
	public boolean addOtherEAPTypes(Collection<EAPExpandedType> eapTypes) {
		if (this.otherEAPTypes==null){
			this.otherEAPTypes = new ArrayList<EAPExpandedType>();
		}
		return this.otherEAPTypes.addAll(eapTypes);
	}
	
	public void clearOtherEAPTypes(){
		this.otherEAPTypes.clear();
	}

	/* (non-Javadoc)
	 * @see com.elitecore.radius.eap.packet.EAPType#clone()
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		EAPExpandedType eapExpandedType = (EAPExpandedType)super.clone();
		if (this.otherEAPTypes==null){
			eapExpandedType.otherEAPTypes = null;
		}else{
			eapExpandedType.otherEAPTypes = new ArrayList<EAPExpandedType>(this.otherEAPTypes);
		}
		return super.clone();
	}
}
