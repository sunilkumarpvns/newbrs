/**
 * 
 */
package com.elitecore.coregtp.commons.elements;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import com.elitecore.coregtp.commons.packet.ParseException;
import com.elitecore.coregtp.commons.util.constants.GTPPrimeElementTypeConstants;
import com.elitecore.coregtp.commons.util.constants.GTPPrimeCauseValuesConstants;

/**
 * @author dhaval.jobanputra
 *
 */
public class Octet extends BaseGTPPrimeElement implements Cloneable{


	public Octet() {
		super();
	}

	@Override
	public BaseGTPPrimeElement clone() throws CloneNotSupportedException{
		return super.clone();
	}

	public void setOctet(byte octet) {
		byte []value = new byte[1];
		value[0] = octet;
		setValueByte(value);
	}

	public byte getOctet() {
		return value[0];
	}
	@Override
	protected int readFrom(InputStream in) throws ParseException{
		int lengthRead=1;
		try {
			if (type > 127){
				hasLength = true;
				lengthRead += readLength(in);
				value = new byte[length];
				in.read(value, 0, length);
			}
			else{
				value = new byte[1];
				value[0] = (byte)in.read();	
			}
			return lengthRead;
		} catch (IOException e){
			throw new ParseException( "Packet parsing error in octet", e);
		}
	}

	@Override
	public int readLength(InputStream in) throws IOException{
			int firstByte = in.read() & 0xFF;
			int secondByte = in.read() & 0xFF;
			firstByte = firstByte << 8;
			length = (firstByte | secondByte) & 0xFFFF;
		return 2;
	}

	@Override
	protected void writeTo(OutputStream out) {
		byte bType = (byte)type;
		try {
			out.write((byte)bType);
			if (hasLength){
				out.write(value);
			}
			else{
				out.write(value);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/*@Override
	public void setValueByte(Object value) {
		this.value = (byte[]) value;
	}*/

	@Override
	public String toString() {
		StringWriter writer = new StringWriter();
		PrintWriter out = new PrintWriter(writer);
		out.print("{Element type, " + GTPPrimeElementTypeConstants.fromTypeID(type));
		if (hasLength){
			out.print("; Length: " + length);
			out.print("; Value,");
			for (int i=0 ; i<value.length ; i++){
				out.print(" " + value[i]);
			}
			out.println("}");

		}
		else{
			if (type == 1 ){
				out.println("; Value, " + GTPPrimeCauseValuesConstants.fromTypeID(((int)value[0]) & 0xff) + "}");
			}else{
				out.println("; Value, " + value[0] + "}");	
			}
		}
			
		return writer.toString();

	}


}
