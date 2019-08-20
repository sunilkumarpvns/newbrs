/**
 * 
 */
package com.elitecore.coregtp.commons.packet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.elitecore.coregtp.commons.elements.BaseGTPPrimeElement;

/**
 * @author dhaval.jobanputra
 *
 */
public abstract class BaseGTPPrimePacket implements IGTPPrimePacket {

	protected String clientIP;
	protected int clientPort;

	protected byte bGTPVersion;
	protected byte bProtocolType;
	protected byte bSpaerBits;
	protected byte bHeaderType;
	protected byte firstByte;
	protected int messageType;
	protected int payloadLength;
	protected int seqNumber;

	protected List<BaseGTPPrimeElement> elementList;
	protected List<BaseGTPPrimeElement> toSendIEList;

	public BaseGTPPrimePacket() {
		super();
		elementList = new ArrayList<BaseGTPPrimeElement>();
		toSendIEList = new ArrayList<BaseGTPPrimeElement>();
	}


	@Override
	public byte[] getBytes() {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		try {
			writeTo(buffer);
		}catch(Exception e){
			e.printStackTrace();
		}
		return buffer.toByteArray();
	}

	@Override
	public void setBytes(byte[] data) throws ParseException{
		ByteArrayInputStream in = new ByteArrayInputStream(data);
		readFrom(in);
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


	public static byte[] intToByteArray (int integer) {
		int byteNum = (40 - Integer.numberOfLeadingZeros (integer < 0 ? ~integer : integer)) / 8;
		byte[] byteArray = new byte[4];

		for (int n = 0; n < byteNum; n++)
			byteArray[3 - n] = (byte) (integer >>> (n * 8));

		return (byteArray);
	}

	public static byte[] intToByteArray (int integer, int noOfBytes) {
		byte[] byteArray = new byte[noOfBytes];

		for (int n = 0; n < noOfBytes; n++)
			byteArray[(noOfBytes - 1) - n] = (byte) (integer >>> (n * 8));

		return (byteArray);
	}

	public static byte[] longToByteArray (long longValue, int noOfBytes) {
		byte[] byteArray = new byte[noOfBytes];

		for (int n = 0; n < noOfBytes; n++)
			byteArray[(noOfBytes - 1) - n] = (byte) (longValue >>> (n * 8));

		return (byteArray);
	}

	public void setClientIP(String hostAddress) {
		this.clientIP=hostAddress;		
	}

	public void setClientPort(int sourcePort) {
		this.clientPort=sourcePort;		
	}

	public int getMessageType() {
		return messageType;
	}

	public int getSeqNumber() {
		return seqNumber;
	}

	public void setMessageType(int type){
		messageType = type;
	}

	public void setFirstByte(byte fByte) {
		firstByte = fByte;

	}

	public void setPayloadLength(int length) {
		payloadLength = length;

	}

	public void setSeqNumber(int sequence) {
		seqNumber = sequence;
	}

	public byte getVersion() {
		return bGTPVersion;
	}

	public byte getProtocolType() {
		return bProtocolType;
	}

	public byte getSpareBits() {
		return bSpaerBits;
	}

	public byte getHeaderType() {
		return bHeaderType;
	}

	protected void parseFirstByte(int firstByte) {
		int temp=firstByte;
		bGTPVersion = (byte) (temp >>> 5);
		temp=firstByte;
		bProtocolType = (byte) ( (temp >>> 4 ) & 1);
		temp=firstByte;
		bSpaerBits = (byte) ((temp >>> 1) & 7);
		temp= firstByte;
		bHeaderType = (byte) (temp & 1);
	}

	public abstract void writeTo(OutputStream destinationStream)throws IOException;
	public abstract int readFrom(InputStream sourceStream) throws ParseException;
	public abstract List<BaseGTPPrimeElement> getElementList();
	public abstract void setElementList(List<BaseGTPPrimeElement> list);

	//this methods are just needed in PacketV0
	public void setFlowLabel() {}
	public void setSNDCPN() {}
	public void setSpares() {}
	public void setTID() {}
	public void setHeaderLength(int gtpPrimeVersion) {}
}
