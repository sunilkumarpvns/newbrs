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

import java.io.UnsupportedEncodingException;

import com.elitecore.coreeap.commons.util.constants.CommonConstants;
import com.elitecore.coreeap.packet.InvalidEAPTypeException;
import com.elitecore.coreeap.util.constants.EapTypeConstants;



public class IdentityEAPType extends EAPType{
	
	private byte[] identity; 
 
	public IdentityEAPType(){
		super(EapTypeConstants.IDENTITY.typeId);
		//this.setData(super.getData());
	}
	
	public IdentityEAPType(byte[] byteArray) throws InvalidEAPTypeException {
		super(byteArray);
		if(super.getType() == EapTypeConstants.IDENTITY.typeId){
			this.setType(EapTypeConstants.IDENTITY.typeId);
			//this.setData(super.getData());
		}else{
			throw new IllegalArgumentException("EAP Type : not IDENTITY");
		}
	}
	
	public byte[] getIdentity() {
		return this.identity;
	}

	public void setIdentity(byte[] identity) throws IllegalArgumentException {
		if(identity != null){
			this.identity = new byte[identity.length];
			if(identity.length >=1 && identity.length <= 1015){
				System.arraycopy(identity, 0, this.identity, 0, identity.length);
			}else{
				throw new IllegalArgumentException("Invalid number of bytes " + identity.length);
			}
		}else{
			throw new IllegalArgumentException("Data bytes : null");
		}
	}
	
	@Override
	public byte[] getData() {
		return this.getIdentity();
	}

	@Override
	public void setData(byte[] data) {
		try{
			this.setIdentity(data);
		}catch(IllegalArgumentException ille){
			throw ille;
		}
	}

	public byte[] toBytes(){
		byte[] returnBytes = new byte[this.identity.length + 1];
		returnBytes[0] = (byte)this.getType();
		System.arraycopy(this.identity, 0, returnBytes, 1, this.identity.length);
		return returnBytes;
	}
	
	public String toString(){
		StringBuilder strBuilder = new StringBuilder();
		strBuilder.append("  Type=");
		strBuilder.append(this.getType());
		strBuilder.append("  Identity=");
		try{
			strBuilder.append(new String(this.getIdentity(),CommonConstants.UTF8));
		}catch(UnsupportedEncodingException e){
			strBuilder.append(new String(this.getIdentity()));
		}
		return strBuilder.toString();
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		IdentityEAPType newObject = (IdentityEAPType)super.clone();
		if(this.identity != null){
			newObject.identity = new byte[this.identity.length];
			System.arraycopy(this.identity, 0, newObject.identity, 0, this.identity.length);
		}
		return newObject;
	}

}
