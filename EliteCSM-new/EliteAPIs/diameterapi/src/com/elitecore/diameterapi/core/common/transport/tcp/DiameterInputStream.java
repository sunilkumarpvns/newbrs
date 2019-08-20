package com.elitecore.diameterapi.core.common.transport.tcp;

import java.io.EOFException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;

import com.elitecore.core.commons.packet.MalformedPacketException;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;

/**
 * A diameter input stream lets an application read Diameter Packet 
 * from an underlying input stream.
 *
 * @author  monica.lulla
 */
public class DiameterInputStream extends FilterInputStream {

	private static final String MODULE = "DIA-IN-STRM";

	public static final int MAX_CHUNK_SIZE = 4000;
	/**
	 * RPETrrrr  --> 1 << 7 = 128
	 */
	public static final int COMMAND_FLAG_REQUEST_BIT = 128;

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
		this(in, MAX_CHUNK_SIZE);
	}

	/**
	 * Reads input bytes for Diameter Packet and 
	 * constructs a Diameter Packet as per RFC 6733 Specification. 
	 * @return Diameter Packet
	 * 
	 * @throws IOException if unable to read bytes from Stream
	 * @throws MalformedPacketException on Malformed Diameter Packet
	 */
	public synchronized DiameterPacket readDiameterPacket() throws IOException, MalformedPacketException {

		DiameterPacket diameterPacket = null;
		
		byte[] headerBytes = getBytes(DiameterPacket.DEFAULT_DIAMETER_PACKET_LENGTH);
		try {
			diameterPacket = DiameterPacket.createPacket(headerBytes);
		} catch (MalformedPacketException e) {
			flush();
			throw e;
		}
		try {
			diameterPacket.parsePacketAVPBytes(
					getBytes(diameterPacket.getRcvdLength()
							- DiameterPacket.DEFAULT_DIAMETER_PACKET_LENGTH));
		} catch(IOException e) {
			throw new MalformedPacketException("Exception occured while parsing packet with HbH-ID=" + diameterPacket.getHop_by_hopIdentifier() + 
					" and EtE-ID=" + diameterPacket.getEnd_to_endIdentifier(), e);
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
	 * @throws IOException
	 * @throws MalformedPacketException 
	 */
	private byte[] getBytes(int length) throws IOException {

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