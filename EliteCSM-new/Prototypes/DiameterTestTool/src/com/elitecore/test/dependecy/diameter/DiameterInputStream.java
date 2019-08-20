package com.elitecore.test.dependecy.diameter;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.test.dependecy.diameter.packet.DiameterAnswer;
import com.elitecore.test.dependecy.diameter.packet.DiameterPacket;
import com.elitecore.test.dependecy.diameter.packet.DiameterRequest;
import com.elitecore.test.dependecy.diameter.packet.ParseException;

import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;

/**
 * A diameter input stream lets an application read Diameter Packet 
 * from an underlying input stream.
 *
 * @author  monica.lulla
 */
public class DiameterInputStream extends FilterInputStream {

	private static final String MODULE = "DIA-IN-STRM";

	public static final int TCP_PACKET_MAX_LEN = 65535;
	/**
	 * RPETrrrr  --> 1 << 7 = 128
	 */
	public static final int COMMAND_FLAG_REQUEST_BIT = 128;
	private static final int DIAMETER_PROTOCOL_VERSION_1 = 1;

	private static final int DIA_PCKT_MAX_EXPECTED_LENGTH = 8192;

	private static final int DIA_HEADER_LENGTH = 20;

	private int chunkSize;
	
	private byte [] readBuffer;
	private int currentIndex = 0;
	private int size = 0;

	public DiameterInputStream(InputStream in, int chunkSize) {
		super(in);
		this.chunkSize = chunkSize;
		this.readBuffer = new byte[chunkSize];
	}

	public DiameterInputStream(InputStream in) {
		this(in, TCP_PACKET_MAX_LEN);
	}

	/**
	 * Reads input bytes for Diameter Packet and 
	 * constructs a Diameter Packet as per RFC 6733 Specification. 
	 * @return Diameter Packet
	 * 
	 * @throws java.io.IOException if unable to read bytes from Stream
	 * @throws MalformedPacketException on Malformed Diameter Packet
	 */
	public synchronized DiameterPacket readDiameterPacket() throws IOException, MalformedPacketException {

		byte [] headerBytes = null;
		byte [] avpBytes = null;
		DiameterPacket diameterPacket = null;
		
		headerBytes = getBytes(DIA_HEADER_LENGTH);
		if (headerBytes[0] != DIAMETER_PROTOCOL_VERSION_1){
			LogManager.getLogger().error(MODULE, "Malformed Hex bytes recieved: " + DiameterUtility.bytesToHex(headerBytes));
			flush();
			throw new MalformedPacketException("Unsupported Diameter Version: " + headerBytes[0]);
		}
		int messageLength = headerBytes[1];
		messageLength = (messageLength << 8) | headerBytes[2] & 0xFF;
		messageLength = (messageLength << 8) | headerBytes[3] & 0xFF;

		if(messageLength < DIA_HEADER_LENGTH || messageLength > DIA_PCKT_MAX_EXPECTED_LENGTH) {
			LogManager.getLogger().error(MODULE, "Malformed Hex bytes recieved: " + DiameterUtility.bytesToHex(headerBytes));
			flush();
			throw new MalformedPacketException("Unsupported Diameter Message Length: " + messageLength);
		}
		avpBytes = getBytes(messageLength - DIA_HEADER_LENGTH);
		boolean isRequest = ((headerBytes[4] & COMMAND_FLAG_REQUEST_BIT) != 0);

		if(isRequest) {
			diameterPacket = new DiameterRequest(false);
		} else {
			diameterPacket = new DiameterAnswer();
		}
		try {
			diameterPacket.parsePacketBytes(headerBytes,avpBytes);
		} catch(ParseException e) {
			throw new MalformedPacketException(e);
		}
		return diameterPacket;
	}

	/**
	 * Try to read <code>length</code> number of bytes from bytes previously read chunk. 
	 * If <code>length</code> number of bytes are not available, 
	 * It will read from Chained Stream in Chunks of <code>chunkSize</code> 
	 * and will return the Required number of bytes
	 * 
	 * @param length the number of bytes to read.
	 * @return array of bytes
	 * @throws java.io.IOException
	 * @throws MalformedPacketException 
	 */
	private byte[] getBytes(int length) throws IOException, MalformedPacketException {

		byte[] requiredBytes = new byte[length];
		int noOfBytesFilled = 0;
		
		do{
			if(size == currentIndex){
				try {
					size = in.read(readBuffer, 0, chunkSize);
					currentIndex = 0;
					//EOS reached
					if (size < 0) {
						throw new EOFException("End of Stream reached");
					}
				} catch(SocketTimeoutException e){
					continue;
				}
			}
			int noOfAvailableBytes = size - currentIndex;
			int noOfBytesRequired = length - noOfBytesFilled;
			int noOfBytesToConsume = noOfAvailableBytes < noOfBytesRequired ? noOfAvailableBytes : noOfBytesRequired;
			
			System.arraycopy(readBuffer, currentIndex, requiredBytes, noOfBytesFilled, noOfBytesToConsume);
			currentIndex += noOfBytesToConsume;
			noOfBytesFilled += noOfBytesToConsume;
			
		}while(noOfBytesFilled < length);
		
		return requiredBytes;
	}

	/**
	 * Flushes the currently Stored Chunk of Data
	 */
	private void flush() {
		this.currentIndex = size;
	}

}