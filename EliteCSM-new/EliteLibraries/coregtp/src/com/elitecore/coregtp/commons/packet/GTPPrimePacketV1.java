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
public class GTPPrimePacketV1 extends BaseGTPPrimePacket{

	public GTPPrimePacketV1(){
		super();
	}

	public void writeTo(OutputStream destinationStream) throws IOException{
		destinationStream.write(firstByte);
		destinationStream.write(new Byte((new Integer (messageType)).byteValue()));
		destinationStream.write(intToByteArray(payloadLength, 2));
		destinationStream.write(intToByteArray(seqNumber, 2));
		if (elementList != null && elementList.size()>0){
			for (int i=0 ; i<elementList.size() ; i++){
				destinationStream.write(elementList.get(i).getBytes());

			}
		}
	}

	public int readFrom(InputStream sourceStream) throws ParseException{
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

			while (( elementType = sourceStream.read()) != -1 && totalLengthRead < payloadLength ){
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
		return totalLengthRead + 6;
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
		out.println("{Sequence no, " + seqNumber +"}");
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
