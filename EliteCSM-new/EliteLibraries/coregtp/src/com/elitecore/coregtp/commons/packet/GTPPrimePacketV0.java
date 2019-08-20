/**
 * 
 */
package com.elitecore.coregtp.commons.packet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import com.elitecore.coregtp.commons.elements.BaseGTPPrimeElement;
import com.elitecore.coregtp.commons.util.Dictionary;
import com.elitecore.coregtp.commons.util.constants.GTPPrimeConstants;

/**
 * @author dhaval.jobanputra
 *
 */
public class GTPPrimePacketV0 extends BaseGTPPrimePacket {

	private int flowLabel;
	private int SNDCPN;
	private int spare1;
	private int spare2;
	private int spare3;
	private long TID;

	public GTPPrimePacketV0(){
		super();
		flowLabel=0;
		SNDCPN=0;
		spare1=0;
		spare2=0;
		spare3=0;
		TID=0;
	}

	public void setHeaderLength(int gtpPrimeVersion) {
		bGTPVersion = (byte) gtpPrimeVersion;
	}
	public void writeTo(OutputStream destinationStream) throws IOException{
		destinationStream.write(firstByte);
		destinationStream.write(new Byte((new Integer (messageType)).byteValue()));
		destinationStream.write(intToByteArray(payloadLength, 2));
		destinationStream.write(intToByteArray(seqNumber, 2));
		if (bGTPVersion==0){
			destinationStream.write(intToByteArray(flowLabel, 2));
			destinationStream.write(new Byte((new Integer ( SNDCPN)).byteValue()));
			destinationStream.write(new Byte((new Integer (spare1)).byteValue()));
			destinationStream.write(new Byte((new Integer (spare2)).byteValue()));
			destinationStream.write(new Byte((new Integer (spare3)).byteValue()));
			destinationStream.write(longToByteArray(TID, 8));		
		}
		if (elementList != null && elementList.size() > 0){
			for (int i=0 ; i<elementList.size() ; i++){
				destinationStream.write(elementList.get(i).getBytes());

			}
		}
	}



	public int readFrom(InputStream sourceStream) throws ParseException {
		int elementType;
		BaseGTPPrimeElement element = null;
		int totalLengthRead=0;
		try{
		parseFirstByte (sourceStream.read());
		messageType = sourceStream.read() & 0xFF;
		payloadLength = sourceStream.read() & 0xFF;
		payloadLength = (payloadLength << 8) | (sourceStream.read() & 0xFF);
		seqNumber = sourceStream.read();
		seqNumber = (seqNumber << 8 ) | (sourceStream.read() & 0xFF);
		if (bHeaderType == 0){
			flowLabel = sourceStream.read();
			flowLabel = (flowLabel << 8 ) | (sourceStream.read() & 0xFF);
			SNDCPN = sourceStream.read();
			spare1 = sourceStream.read();
			spare2 = sourceStream.read();
			spare3 = sourceStream.read();
			TID = sourceStream.read();
			TID = ( TID << 8 ) | (sourceStream.read() & 0xFF);
			TID = ( TID << 8 ) | (sourceStream.read() & 0xFF);
			TID = ( TID << 8 ) | (sourceStream.read() & 0xFF);
			TID = ( TID << 8 ) | (sourceStream.read() & 0xFF);
			TID = ( TID << 8 ) | (sourceStream.read() & 0xFF);
			TID = ( TID << 8 ) | (sourceStream.read() & 0xFF);
			TID = ( TID << 8 ) | (sourceStream.read() & 0xFF);		
		}
		while (( elementType = sourceStream.read()) != -1 &&  totalLengthRead < payloadLength){
			totalLengthRead++;
			element = Dictionary.getInstance().getElement(elementType);
			element.setType(elementType);
			totalLengthRead += element.setBytes(sourceStream);
			if (payloadLength<totalLengthRead){
				throw new ParseException("Error in reading elements");
			}
			elementList.add(element);
		}
		
		} catch (CloneNotSupportedException e){
			throw new ParseException("Clone not supported exception in Parsing Packet.", e);
		} catch (IOException e) {
			throw new ParseException("IOException in Parsing Packet.", e);
		}
		
		if (payloadLength!=totalLengthRead){
			throw new ParseException("Malformed Packet. Length not matched");
		}
		
		if (bHeaderType == 0){
			return totalLengthRead + 20;
		}
		return totalLengthRead + 6;
	}
	public void setFirstByte(byte fByte) {
		firstByte = fByte;

	}


	public void setFlowLabel() {
		flowLabel = 0;
	}

	public void setSNDCPN() {
		SNDCPN = 0;
	}

	public void setSpares() {
		spare1 = -1;
		spare2 = -1;
		spare3 = -1;
	}

	public void setTID() {
		TID = 0;
	}

	public String toString() {
		StringWriter stringWriter = new StringWriter(); 
		PrintWriter out = new PrintWriter ( stringWriter);
		out.println("\n\t\t--Packet Header-- " );
		out.print("{Protocol, " + ((bGTPVersion == 2)? "V2" : ((bGTPVersion==1)?"V1" : "V0")));
		out.print((( bProtocolType == 0 ) ? "P" : "G"));
		out.print("...");
		out.println(((bHeaderType)==0 ? "0" : "1")+ "}");
		out.println("{Message Type, " + GTPPrimeConstants.fromTypeID(messageType) +"}");
		out.println("{Payload length, " + payloadLength + "}");
		out.println("{Sequence no, " + seqNumber+ "}");
		out.println("{Flow label, " + flowLabel+ "}");
		out.println("{SNDCP, " + SNDCPN+ "}");
		out.println("{Spare octet 1, " + spare1 + "; Spare octet 2, " + spare2 + "; Spare octet 3, " + spare3 + "}");
		out.println("{Tunnel ID, " + TID+ "}");
		out.println("\t\t--Elements--");
		for (int i=0 ; i<elementList.size() ; i++){
			out.println(elementList.get(i).toString());
		}
		return stringWriter.toString();
	}
	@Override
	public List<BaseGTPPrimeElement> getElementList() {
		return elementList;
	}

	@Override
	public void setElementList(List<BaseGTPPrimeElement> list) {
		elementList = list;
	}

}
