/**
 * 
 */
package com.elitecore.coregtp.commons.elements;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.elitecore.coregtp.commons.packet.ParseException;

/**
 * @author dhaval.jobanputra
 *
 */
public abstract class BaseGTPPrimeElement implements IGTPElement, Cloneable {

	protected int type;
	protected int length;
	byte value[];
	boolean hasLength=false;

	public BaseGTPPrimeElement(){
		super();
	}

	public int getElementType(){
		return type;
	}

	public int getLength(){
		return length;
	}

	public void setType(int elementType) {
		type = elementType;
	}

	public int setBytes (InputStream sourceStream) throws ParseException{
		int lengthRead = 0;
		
		lengthRead += readFrom (sourceStream);
		return lengthRead;
	}

	public byte[] getBytes (){
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		try{
			writeTo (buffer);
		}
		catch(Exception exp){
			exp.printStackTrace();
		}
		return buffer.toByteArray();
	}

	abstract protected  int readFrom(InputStream in) throws ParseException;
	abstract protected void writeTo(OutputStream out);
	abstract public int readLength(InputStream in) throws IOException;
	abstract public String toString();

	public void setLength(int i) {}

	public BaseGTPPrimeElement clone() throws CloneNotSupportedException {
		BaseGTPPrimeElement baseGTPPrimeElement =  (BaseGTPPrimeElement)super.clone();
		if (value != null){
			baseGTPPrimeElement.value = new byte[value.length];
			System.arraycopy(value,0,baseGTPPrimeElement.value,0,value.length);
		}
		return baseGTPPrimeElement;
	}

	public void setValueByte(byte[] value) {
		this.value = value; 
	}

	public byte[] getValueByte() {
		return value;
	}
}
