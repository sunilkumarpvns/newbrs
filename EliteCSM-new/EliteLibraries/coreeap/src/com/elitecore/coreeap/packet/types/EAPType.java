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

/**
*  Represents the EAP Type in EAP Packet.
*  Any class that represents the Method Specific Legacy EAP Type will extend this class.
* @author Elitecore Technologies Ltd.
*
*/ 

/*	The general form of any EAP Type is shown below.
 *  
 *  Field details : 
 *    	Type : 1-byte
 *  	Type-Data : 1 or more-byte(s)
 *    
 * 	+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * 	|  Type	        |    Type-Data ...
 *	+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
 * 
 */
public class EAPType implements Cloneable {
 
	private int type;
	private byte[] data;
	
	public EAPType(int type) {
		if(type < 0 || type > 255){
			throw new IllegalArgumentException("Invalid EAP Type : " + type);
		}else{
			this.type = type;
		}
	}
	
	public EAPType(byte[] bytes) throws InvalidEAPTypeException {
		parseBytes(bytes);
	}
	
	protected void parseBytes(byte[] byteArray) throws InvalidEAPTypeException {		
		if(byteArray != null){
			if(byteArray.length < 2)
				throw new IllegalArgumentException("Invalid EAP Type Data bytes : Length less than minimum required.");
			else if((int)(byteArray[0] & 0xFF) <= 255 && (int)(byteArray[0] & 0xFF) >= 0){
				this.setType((int)(byteArray[0] & 0xFF));
				byte[] tempBytes = new byte[byteArray.length - 1];
				System.arraycopy(byteArray, 1, tempBytes, 0, byteArray.length - 1);
				this.setData(tempBytes);				
			}else{
				throw new InvalidEAPTypeException("Type value not valid.");
			}
		}else{
			throw new InvalidEAPTypeException("Argument : null");
		}
	}
	
	public int getType() {
		return this.type;
	}
	
	protected void setType(int type) {
		if(type < 0 || type > 255){
			throw new IllegalArgumentException("Invalid EAP Type : " + type);
		}else{
			this.type = type;
		}
	}
	
	public byte[] getData() {
		return this.data;
	}
	
	public void setData(byte[] data) throws IllegalArgumentException{			
		if(data != null){
			if(data.length >= 1){
				this.data = new byte[data.length];
				System.arraycopy(data, 0, this.data, 0, data.length);
				
			}else{
				throw new IllegalArgumentException("Invalid EAP Type Data bytes : Length less than minimum required.");
			}
		}else{
			throw new IllegalArgumentException("Invalid EAP Type Data bytes : null");
		}			
	}
	
	public byte[] toBytes(){
		byte[] returnBytes = new byte[this.data.length + 1];
		returnBytes[0] = (byte)this.type;
		System.arraycopy(this.data, 0, returnBytes, 1, this.data.length);
		return returnBytes;
	}
	
	public String toString(){
		String returnString = null;
		try{
			returnString = "Type="+this.type+", Type-Data="+new String(this.data,CommonConstants.UTF8);
		}catch(UnsupportedEncodingException e){
			returnString = "Type="+this.type+", Type-Data="+new String(this.data);
		}
		return returnString;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		EAPType eapType = (EAPType)super.clone();
		if (this.data!=null){
			eapType.data = new byte[this.data.length];
			System.arraycopy(this.data,0,eapType.data,0,this.data.length);
		}
		return eapType;
	}

}
