/**
 * 
 */
package com.elitecore.coregtp.commons.elements;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import com.elitecore.coregtp.commons.packet.ParseException;
import com.elitecore.coregtp.commons.util.constants.GTPPrimeElementTypeConstants;

/**
 * @author dhaval.jobanputra
 *
 */
public class GTPPrimeDataRecordPacket extends BaseGTPPrimeElement implements Cloneable{

	private int noOfDataRecords=0;
	private byte dataRecordFormat;
	private int dataRecordFormatVersion;
	private List<GTPPrimeCDRData> cdrDataList;

	public GTPPrimeDataRecordPacket (){
		super();
		cdrDataList = new ArrayList<GTPPrimeCDRData>();
	}

	@Override
	public BaseGTPPrimeElement clone() throws CloneNotSupportedException{
		GTPPrimeDataRecordPacket element = (GTPPrimeDataRecordPacket) super.clone();
		List<GTPPrimeCDRData> tmpList = new ArrayList<GTPPrimeCDRData>();
		tmpList.addAll(cdrDataList);
		element.cdrDataList = tmpList;
		return element;
	}

	public List<GTPPrimeCDRData> getCDRList(){
		return cdrDataList;
	}

	@Override
	protected int readFrom(InputStream in) throws ParseException  {
		int lengthRead=0;
		int noOfDataRecordRead = 0;
		GTPPrimeCDRData cdrDataObject;
		try{
			readLength(in);
			noOfDataRecords = in.read() & 0xFF;
			lengthRead++;
			dataRecordFormat = (byte)(in.read() & 0xFF);
			lengthRead++;
			int temp = (byte)(in.read() & 0xFF);
			temp <<= 8;
			dataRecordFormatVersion = (temp | (in.read() & 0xFF)) & 0xFFFF;
			lengthRead += 2;

			while (lengthRead < length && noOfDataRecordRead < noOfDataRecords){
				cdrDataObject = new GTPPrimeCDRData();
				lengthRead += cdrDataObject.readFrom(in);
				cdrDataList.add(cdrDataObject);
				noOfDataRecordRead++;
			}
		} catch (IOException e){
			throw new ParseException( "Packet parsing error in data record packet.", e);
		}
		if (lengthRead != length || noOfDataRecordRead != noOfDataRecords){
			throw new ParseException("Error In parsing data record packet. Length or No. of data record not matched");
		}
		return lengthRead + 2;
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
	public String toString() {
		StringWriter stringWriter = new StringWriter(); 
		PrintWriter out = new PrintWriter ( stringWriter);
		out.print("{Element type, " + GTPPrimeElementTypeConstants.fromTypeID(type));
		out.print("; Length, " + length);
		out.print("; No of CDR, " + noOfDataRecords);
		out.print("; Format, " + dataRecordFormat);
		out.println("; Format Version, " + dataRecordFormatVersion  + "}");

		for (int i=0; i<noOfDataRecords ; i++){
			out.println("CDR: " + (i+1));
			cdrDataList.get(i).toString();
		}
		return stringWriter.toString();
	}

	@Override
	protected void writeTo(OutputStream out) {
		int tempLength;
		byte byte1;
		byte byte0;
		try {
			out.write((byte)noOfDataRecords);
			out.write(dataRecordFormat);
			out.write(dataRecordFormatVersion);
			for (int i=0 ; i<noOfDataRecords ; i++){
				tempLength = cdrDataList.get(i).getLength();
				byte1 = (byte) (tempLength & 0xFF);
				tempLength >>= 8;
			byte0 = (byte) (tempLength & 0xFF);
			out.write(byte0);
			out.write(byte1);
			out.write(cdrDataList.get(i).getValueByte());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public int getNoOfRecord(){
		return noOfDataRecords;
	}

	@Override
	public byte[] getValueByte(){
		try {
			ByteArrayOutputStream buffer = new ByteArrayOutputStream();
			int listSize = cdrDataList.size();
			for (int i=0 ; i<listSize ; i++){
				buffer.write(cdrDataList.get(i).getValueByte());
			}
			return buffer.toByteArray(); 
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String bytesToHex(byte buf[]){
		if(buf == null)
			return "";
		else
			return bytesToHex(buf, 0, buf.length);
	}

	public static String bytesToHex(byte buf[], int offset, int limit){
		char[] hexbuf = new char[(((limit - offset)*2) + 2)];
		hexbuf[0]='0';
		hexbuf[1]='x';
		for(int i = offset,k=2; i < limit; i++){
			hexbuf[k++] = (HEX[buf[i] >> 4 & 0xf]);
			hexbuf[k++] = (HEX[buf[i] & 0xf]);
		}
		return String.valueOf(hexbuf);
	}
	private static final char HEX[] = {
		'0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
		'a', 'b', 'c', 'd', 'e', 'f'
	};
}
