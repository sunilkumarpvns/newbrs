package com.elitecore.coreeap.packet.types.sim.attributes;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.elitecore.coreeap.util.constants.sim.attribute.SIMAttributeTypeConstants;
import com.elitecore.coreeap.util.tls.TLSUtility;

public abstract class BaseSIMAttribute implements ISIMAttribute {
	private int type;
	private int length;
	private byte[] reservedBytes;
	private byte[] valueBuffer;
	
    public BaseSIMAttribute(){
        valueBuffer = new byte[0];
        reservedBytes = new byte[2];
        this.length = 1;
    }
    
    public BaseSIMAttribute(byte[] attributeBytes){    	
    	
    }
    
    public BaseSIMAttribute(int type){
        valueBuffer = new byte[0];
        reservedBytes = new byte[2];
        this.type = type;
        this.length = 1;
    }

	/**
	 * @return Returns the length of the attribute.
	 */
	public int getLength() {
		return length & 0xFFF;
	}
	
	/**
	 * Sets the length of the attribute.
	 * @param length
	 */
	public void setLength(int length) {
		if(length%4 != 0){
			//TODO - throw new exception Invalid Length
		}
		this.length = length;
	}
	
	/**
	 * Returns the type of an attribute as per the radius dictionary,
	 * Type means attribute id as per the dictionary.
	 * 
	 * @return Returns type of the attribute.
	 */
	public int getType() {
		return type;
	}
	
	/**
	 * Sets the type of the attribute to the passed value.
	 * Type means attribute id as per the dictionary.
	 * 
	 */
    public void setType(int type) {
		this.type = type;
	}

    
    public byte[] getReservedBytes(){
    	return this.reservedBytes;
    }
    
	public void setReservedBytes(byte[] reservedBytes) {
		// TODO Auto-generated method stub
		this.reservedBytes = new byte[2];
		if(reservedBytes!=null){			
			if(reservedBytes.length!=2){
				//TODO - throw Exception = invalid length of reserved bytes
			}
			this.reservedBytes[0] = reservedBytes[0];
			this.reservedBytes[1] = reservedBytes[1];
		}
	}
	/**
	 * @return Returns only the value part of the attribute in the form of bytes array.
	 */
	public byte[] getValue() {
		return getValueBytes();
	}
	
	/**
	 * Sets the passed byte array as the value of the attribute.
	 * @param value Byte array to be set as a value of the attribute.
	 */
	public void setValue(byte[] value) {
		setValueBytes(value);
	}	
	
	public int getID() {
		return getType();
	}		
	
	public byte[] getBytes() {
		int remainder = 4 - valueBuffer.length%4;
		byte [] totalBytes = new byte[4 + valueBuffer.length + ( remainder<4 ? remainder : 0 )];
		totalBytes [0] = (byte)type;
		totalBytes [1] = (byte)length;
		totalBytes [2] = (byte)this.reservedBytes[0];
		totalBytes [3] = (byte)this.reservedBytes[1];
		if(valueBuffer.length > 0)
			System.arraycopy(valueBuffer, 0, totalBytes, 4, valueBuffer.length);
		return totalBytes;
	}

	public void setBytes(byte[] totalBytes) {
		if (totalBytes != null && totalBytes.length > 2) {
			this.type = totalBytes[0] & 0xFF;
			this.length = (totalBytes[1]>255?255:totalBytes[1] & 0xFF);
//			this.length = this.length * 4;
			this.reservedBytes = new byte[2];
			this.reservedBytes[0] = totalBytes[2];
			this.reservedBytes[1] = totalBytes[3];			                   
			this.valueBuffer = new byte[(this.length*4) - 4];			
			System.arraycopy(totalBytes,4,this.valueBuffer,0,((this.length*4) - 4));
		}
	}

	public byte[] getValueBytes() {
		return valueBuffer;
	}
	
	public void setValueBytes(byte[] valueBytes) {
		if (valueBytes != null) {
			if (valueBytes.length <= 1018) {
				this.valueBuffer = valueBytes;
			}else {
				this.valueBuffer = new byte[1018];
				System.arraycopy(valueBytes, 0, this.valueBuffer, 0, 1018);
			}
		}else {
			valueBuffer = new byte[0];
		}
		if(valueBuffer.length % 4 != 0)
			setLength((valueBuffer.length/4) + 2);
		else
			setLength((valueBuffer.length/4) + 1);
	}	
	
	/**
	 * 
	 * @throws IOException if IO error occurs.
	 * @throws NullPointerException if NULL sourceStream is passed.
	 */
	public int readFrom(InputStream sourceStream) throws IOException {
		
		type =  sourceStream.read();
		length = sourceStream.read();
		length = length  * 4 ;
		if (length < 4){
			length = 0;			
			//TODO- Throw new Exeption "Invalid attribute length found for " + getClass().getName()
		}
		reservedBytes = new byte[2];
		sourceStream.read(reservedBytes);		
		byte []value = new byte[getLength() - 4];
		sourceStream.read(value);
		valueBuffer =  value;
		return value.length + 4;		
	}

	/**
	 * 
	 * @throws IOException if IO error occurs.
	 * @throws NullPointerException if NULL sourceStream is passed.
	 */
	public int readLengthOnwardsFrom(InputStream sourceStream) throws IOException {
		
		length = sourceStream.read();
		length = length  * 4 ;
		if (length < 2){
			length = 0;
			//TODO- Throw new Exeption "Invalid attribute length found for " + getClass().getName()
		}
		//length-=2;
		byte []value = new byte[getLength() - 2];
		sourceStream.read(value);
		valueBuffer =  value;
		return value.length + 1;
		
	}
	
	/**
	 * 
	 * @throws IOException if IO error occurs.
	 * @throws NullPointerException if NULL destinationStream is passed.
	 */
	public void writeTo(OutputStream destinationStream) throws IOException {
		destinationStream.write(type);
		destinationStream.write(length);
		destinationStream.write(valueBuffer);
	}

	public int getSize() {
		return getLength();
	}

	public Object clone() throws CloneNotSupportedException {
		BaseSIMAttribute result = null;
		result = (BaseSIMAttribute)super.clone();
		if (reservedBytes != null){
			result.reservedBytes = new byte[reservedBytes.length];
			System.arraycopy(reservedBytes,0,result.reservedBytes,0,reservedBytes.length);
		}		
		if (valueBuffer != null){
			result.valueBuffer = new byte[valueBuffer.length];
			System.arraycopy(valueBuffer,0,result.valueBuffer,0,valueBuffer.length);
		}
		return result;
	}
	
	public String getStringValue() {		
		return TLSUtility.bytesToHex(getValueBytes());		
	}

	public String toString() {
		return "\t" + SIMAttributeTypeConstants.getName(type) + " = " + getStringValue();
	}
	
	
	
	public boolean equals(Object obj) {
		if (obj instanceof BaseSIMAttribute) {
			return this.type == ((BaseSIMAttribute)obj).type;
		}
		return false;
	}
	
	public int hashCode() {
		return type;
	}	
}

