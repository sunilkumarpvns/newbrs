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

/**
 * @author dhaval.jobanputra
 *
 */
public class Sequence extends BaseGTPPrimeElement implements Cloneable {

	public Sequence(){
		super();
	}
	@Override
	public BaseGTPPrimeElement clone() throws CloneNotSupportedException {
		return super.clone();
	}

	@Override
	protected int readFrom(InputStream in) throws ParseException{
		int lengthRead = 0;
		int tempLength = length;
		try {
			lengthRead = readLength (in);
			for (int i=0 ; tempLength>0 ; i+=2){
				value[i] = (byte) (in.read() & 0xFF);
				value[i+1] = (byte) (in.read() & 0xFF);
				lengthRead += 2;
				tempLength -= 2;
			}
			return lengthRead;
		} catch (IOException e){
			throw new ParseException ( "Packet parsing error in sequence", e);
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
		try {
			out.write((byte)type);
			int tempLength = length;
			byte byte1 = (byte) (tempLength & 0xFF);
			tempLength >>= 8;
		byte byte0 = (byte) (tempLength & 0xFF);
		out.write(byte0);
		out.write(byte1);
		if (length > 0){
		out.write(getValueByte());
		}
		/*			for (int i=0 ; i<length ; i+=2){

				byte1 = (byte) (seqNumbers[i] & 0xFF);
				seqNumbers[0] >>= 8;
				byte0 = (byte) (seqNumbers[i] & 0xFF);
				out.write(byte0);
				out.write(byte1);

			}
		 */		} catch (IOException e) {
			 e.printStackTrace();
		 }
	}

	public int[] getSequence() {
		int[] valueInt = new int[value.length/2];
		int intCnt = 0;
		for(int cnt = 0; cnt < value.length ;  cnt++) {
			int intvalue = 0;
			intvalue = intvalue << 8 | value[cnt] & 0xff;
			intvalue = intvalue << 8 | value[cnt++] & 0xff;
			valueInt[intCnt] = intvalue;
			intCnt++;
		}

		return valueInt;
	}


	@Override
	public String toString() {
		StringWriter stringWriter = new StringWriter(); 
		PrintWriter out = new PrintWriter ( stringWriter);
		out.print("{Element Type, " + GTPPrimeElementTypeConstants.fromTypeID(type));
		out.print(" ;Length, " + length);
		out.print(" ;Value, ");
		for (int i=0 ; i<length/2 ; i+=2){
			int seq = value[i];
			seq <<= 8;
			seq = seq | value[i+1];
			seq = seq & 0xffff;
			out.print(seq + " ");
		}
		out.println("}");
		return stringWriter.toString();
	}

	public void setLength(int len) {
		length = len;
	}

}
