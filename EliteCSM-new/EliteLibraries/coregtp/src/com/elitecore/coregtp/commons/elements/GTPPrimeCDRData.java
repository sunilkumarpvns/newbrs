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

/**
 * @author dhaval.jobanputra
 *
 */
public class GTPPrimeCDRData extends BaseGTPPrimeElement {

	public GTPPrimeCDRData(){
		super();
	}

	@Override
	protected int readFrom(InputStream in) throws ParseException {
		int lengthRead = 0;
		try {
			lengthRead += readLength(in);
			byte[] cdrValue = new byte[length];
			lengthRead +=in.read(cdrValue);
			setValueByte(cdrValue);
			return lengthRead;
		} catch (IOException e){
			throw new ParseException( "Packet parsing error in cdr data.", e);
		}
	}

	@Override
	public int readLength(InputStream in) throws IOException {
		int firstByte = in.read() & 0xFF;
		int secondByte = in.read() & 0xFF;
		firstByte = firstByte << 8;
		length = (firstByte | secondByte) & 0xFFFF;
		return 2;
	}

	@Override
	public String toString() {
		StringWriter stringWriter = new StringWriter(); 
		PrintWriter out = new PrintWriter ( stringWriter);
		out.println("Length: " + length);
		out.println("Data: " + value.toString());
		return stringWriter.toString();
	}

	@Override
	protected void writeTo(OutputStream out) {
		// TODO Auto-generated method stub

	}

	@Override
	public BaseGTPPrimeElement clone() throws CloneNotSupportedException{
		GTPPrimeCDRData element = (GTPPrimeCDRData) super.clone();
		return element;
	}
}
