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
import com.elitecore.coreeap.packet.types.EAPType;
import com.elitecore.coreeap.util.constants.EapTypeConstants;

/**
*  Represents the EAP Type MD5-Challenge in EAP Packet.
*  MD5-Challenge is a Method Specific Legacy EAP Type.
*  @author Elitecore Technologies Ltd.
*/ 
public class MD5ChallengeEAPType extends EAPType {

	private int valueSize = 16; // Size of the MD-5 Challenge ( 16 ).
	private byte[] value; // The MD5-CHALLENGE of 16 bytes.
	private byte[] name; // ID or NAME bytes of the system transmitting the packet.
	
	public MD5ChallengeEAPType(){
		super(EapTypeConstants.MD5_CHALLENGE.typeId);
		//this.setData(super.getData());
	}
	
	public MD5ChallengeEAPType(byte[] byteArray) throws InvalidEAPTypeException {
		super(byteArray);
		if(super.getType() == EapTypeConstants.MD5_CHALLENGE.typeId){
			this.setType(super.getType());
			//this.setData(super.getData());
		}else{
			throw new InvalidEAPTypeException("EAP Type : not MD5-Challenge");
		}
	}
	
	public byte[] toBytes(){
		byte[] returnBytes = new byte[this.name.length + 18];
		returnBytes[0] = (byte)this.getType();
		returnBytes[1] = (byte)this.valueSize;
		System.arraycopy(this.value, 0, returnBytes, 2, this.value.length);
		System.arraycopy(this.name, 0, returnBytes, this.value.length + 2, this.name.length);
		return returnBytes;
	}
	
	public String toString(){
		StringBuilder returnString = new StringBuilder();
		returnString.append("  Type=");
		returnString.append(this.getType());
		if(this.value != null && this.name != null){
			returnString.append("  Value-Size=");
			returnString.append(this.valueSize);
			returnString.append("  Value=");
			//returnString.append(new String(this.value));
			returnString.append(bytesToHex(this.value));
			returnString.append("  Name=");

			try{
				returnString.append(new String(this.name,CommonConstants.UTF8));
			}catch(UnsupportedEncodingException e){
				returnString.append(new String(this.name));
			}
		}
		return returnString.toString();
	}
	
	private String bytesToHex(byte[] data) {
        StringBuffer buf = new StringBuffer();
        buf.append("0x");
        for (int i = 0; i < data.length; i++) {
        	buf.append(byteToHex(data[i]));
        	//buf.append("\\");
        }
        return (buf.toString());
    }

	private String byteToHex(byte data) {
		StringBuffer buf = new StringBuffer();
		buf.append(toHexChar((data >>> 4) & 0x0F));
		buf.append(toHexChar(data & 0x0F));
		return buf.toString();
	}
	
	private char toHexChar(int i) {
        if ((0 <= i) && (i <= 9)) {
            return (char) ('0' + i);
        } else {
            return (char) ('a' + (i - 10));
        }
    }
	
	public byte[] getName() {
		return this.name;
	}

	public void setName(byte[] name) {
		if(name != null){
			if(name.length >= 1){
				this.name = name;
			}else{
				throw new IllegalArgumentException("Name MUST be minimum of 1 byte.");
			}
		}else{
			throw new IllegalArgumentException("Name null.");
		}
	}

	public byte[] getValue() {
		return this.value;
	}

	public void setValue(byte[] value) {
		if(value != null){
			if(value.length == 16){
				this.value = value;
			}else{
				throw new IllegalArgumentException("Value MUST be exactly of 16 bytes for MD5-Challenge");
			}
		}else{
			throw new IllegalArgumentException("Value null.");
		}
	}

/*	
	private int getValueSize() {
		return this.valueSize;
	}

	private void setValueSize(int valueSize) {
		if(valueSize == 16){
			this.valueSize = valueSize;
		}else{
			throw new IllegalArgumentException("ValueSize MUST be 16 for MD5-Challenge");
		}
	}
*/	
	@Override
	public byte[] getData() {
		byte[] tempDataBytes = new byte[0];
		if (this.valueSize>0){
			tempDataBytes = new byte[this.value.length + this.name.length + 1];
			tempDataBytes[0] = (byte)this.valueSize;
			if (this.value!=null){
				System.arraycopy(this.value, 0, tempDataBytes, 1, this.value.length);
			}
			if (this.name!=null){
				System.arraycopy(this.name, 0, tempDataBytes, this.value.length + 1, this.name.length);
			}
		}
		return tempDataBytes;
	}

	@Override
	public void setData(byte[] data) {
		if(data != null){
			//The length of the data bytes MUST be >= 18 ( 1-ValueSize, 16-Value, 1 or More-Name)
			if(data.length >= 17){
				//For MD5-Challenge the ValueSize MUST be 16.
				if((int)(data[0] & 0xFF) == 16){
					this.valueSize = (int)(data[0] & 0xFF);
					this.value = new byte[this.valueSize];
					System.arraycopy(data, 1, this.value, 0, this.valueSize);
					this.name = new byte[data.length - (this.valueSize + 1)];
					System.arraycopy(data, this.value.length + 1, this.name, 0, data.length - (this.value.length + 1));
				}else{
					throw new IllegalArgumentException("Value Size is not 16.");
				}
			}else{
				throw new IllegalArgumentException("Length less than minimum required length.");
			}
		}else{
			throw new IllegalArgumentException("Data null.");
		}
	}

	/* (non-Javadoc)
	 * @see com.elitecore.radius.eap.packet.EAPType#clone()
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		MD5ChallengeEAPType newObject = (MD5ChallengeEAPType)super.clone();
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
