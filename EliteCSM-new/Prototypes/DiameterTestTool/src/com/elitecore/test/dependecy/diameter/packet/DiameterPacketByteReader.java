package com.elitecore.test.dependecy.diameter.packet;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class DiameterPacketByteReader {

	public int readPacketBytes(InputStream ips) throws IOException{
		DataInputStream sourceStream = (DataInputStream) ips;
		
		byte [] first4Bytes = new byte[4];
		sourceStream.read(first4Bytes);
		
		int length = 0;
		
		length = first4Bytes[1];
		length = (length << 8) | first4Bytes[2] & 0xFF;
		length = (length << 8) | first4Bytes[3] & 0xFF;
		
		byte [] restBytes = new byte[(length - 4)];
		
		sourceStream.readFully(restBytes);
		
		return length;

	}

}
