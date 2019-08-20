/**
 * 
 */
package com.elitecore.coregtp.commons.elements;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;

import com.elitecore.coregtp.commons.packet.ParseException;
import com.elitecore.coregtp.commons.util.constants.GTPPrimeElementTypeConstants;

/**
 * @author dhaval.jobanputra
 *
 */
public class IpAddress extends BaseGTPPrimeElement implements Cloneable {

	public IpAddress(){
		super();
	}

	public InetAddress getAddress() {
		try {
			return InetAddress.getByAddress(getValueByte());
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public BaseGTPPrimeElement clone() throws CloneNotSupportedException{
		return super.clone();
	}

	public int readLength(InputStream in) throws IOException{
		int firstByte = in.read() & 0xFF;
		int secondByte = in.read() & 0xFF;
		firstByte = firstByte << 8;
		length = (firstByte | secondByte) & 0xFFFF;
		return 2;
	}

	@Override
	public int readFrom(InputStream in) throws ParseException{
		int lengthRead = 0;
		try {
			lengthRead += readLength (in);

			byte[] bytes=null;
			if (length == 4){
				bytes = new byte[4];
				in.read(bytes, 0 , bytes.length);
				setValueByte(bytes);
				lengthRead += bytes.length;
			}else if (length==16){
				bytes = new byte[16];
				in.read(bytes, 0 , bytes.length);
				setValueByte(bytes);
				lengthRead += bytes.length;
			}
		} catch (IOException e) {
			throw new ParseException( "Packet parsing error in IP address", e);
		}
		return lengthRead;
	}

	@Override
	protected void writeTo(OutputStream out) {
		try {
			out.write((byte)type);
			int tempLength = length;
			byte byte1 = (byte) (tempLength & 0xFF);
			tempLength >>= 8;
		byte byte0 = (byte) (tempLength & 0xFF);
		out.write(byte0);
		out.write(byte1);
		out.write(getValueByte());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void setLength(int i) {
		length = i;
	}


	public void setAddress (InetAddress addr){
		setValueByte(addr.getAddress());
	}

	@Override
	public String toString() {
		StringWriter stringWriter = new StringWriter(); 
		PrintWriter out = new PrintWriter ( stringWriter);
		out.print("{Element Type," + GTPPrimeElementTypeConstants.fromTypeID(type));
		out.print("; Length, " + length);
		try {
			out.println("; Value, " + InetAddress.getByAddress(getValueByte()) + "}");
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return stringWriter.toString();
	}
}