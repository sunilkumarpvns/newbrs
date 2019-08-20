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

import java.util.ArrayList;
import java.util.Collection;

import com.elitecore.coreeap.dictionary.EAPTypeDictionary;
import com.elitecore.coreeap.packet.InvalidEAPTypeException;
import com.elitecore.coreeap.util.constants.EapTypeConstants;

public class NAKEAPType extends EAPType {

	private byte[] alternateMethods;
	
	public NAKEAPType(){
		super(EapTypeConstants.NAK.typeId);
		//this.setData(super.getData());
	}
	
	public NAKEAPType(byte[] byteArray) throws InvalidEAPTypeException {
		super(byteArray);
		if(super.getType() == EapTypeConstants.NAK.typeId){
			this.setType(EapTypeConstants.NAK.typeId);
			//this.setData(super.getData());
		}else{
			throw new IllegalArgumentException("EAP Type : not NAK");
		}
	}
	
	public byte[] getAlternateMethods() {
		return this.alternateMethods;
	}

	public void setAlternateMethods(byte[] alternateMethods) throws IllegalArgumentException{
		if(alternateMethods != null){
			this.alternateMethods = new byte[alternateMethods.length];
			if(alternateMethods.length >= 1){
				System.arraycopy(alternateMethods, 0, this.alternateMethods, 0, alternateMethods.length);
			}else{
				throw new IllegalArgumentException("Invalid number of bytes "+alternateMethods.length);
			}
		}else{
			throw new IllegalArgumentException("Data bytes null.");
		}
	}
		
	@Override
	public byte[] getData() {
		return this.getAlternateMethods();
	}

	@Override
	public void setData(byte[] data) {
		try{
			this.setAlternateMethods(data);
		}catch(IllegalArgumentException ille){
			throw ille;
		}
	}

	public byte[] toBytes(){
		byte[] returnBytes = new byte[this.alternateMethods.length + 1];
		returnBytes[0] = (byte)this.getType();
		System.arraycopy(this.alternateMethods, 0, returnBytes, 1, this.alternateMethods.length);
		return returnBytes;
	}
	
	public String toString(){
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append("  Type=");
		strBuilder.append(this.getType());
		strBuilder.append("  Alternate-Methods=");
		byte[] alternateMethods = getAlternateMethods();
		for(int i=0; i<alternateMethods.length; i++){
			strBuilder.append(" " + alternateMethods[i]);
		}
		return strBuilder.toString();
	}
		
	@Override
	public Object clone() throws CloneNotSupportedException {
		NAKEAPType newObject = (NAKEAPType)super.clone();
		if(this.alternateMethods != null){
			newObject.alternateMethods = new byte[this.alternateMethods.length];
			System.arraycopy(this.alternateMethods, 0, newObject.alternateMethods, 0, this.alternateMethods.length);
		}
		return newObject;
	}

	//TODO verify this method - baiju
	public Collection<EAPType> getAlternateEAPTypes(byte[] alternateMethodBytes){
		Collection<EAPType> alternateEAPMethods = new ArrayList<EAPType>();
		for (int i = 0; i < alternateMethodBytes.length; i++) {
			////try{
				EAPType eapType = EAPTypeDictionary.getInstance().createEAPType((int)(alternateMethodBytes[i] & 0xFF));
				alternateEAPMethods.add(eapType);
			//}catch(UnsupportedEAPTypeException ueaptype){
			//	ueaptype.getMessage();
			//}
		}
		return alternateEAPMethods;
	}
	
}
