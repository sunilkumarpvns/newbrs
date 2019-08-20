package com.elitecore.diameterapi.core.common.packet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public abstract class Packet {

	private byte[] buffer;
	
	protected Packet(byte[] buffer) {
		this.buffer = buffer;
	}
	
	public Packet() {
	}

	public byte[] getPacketBytes() {
		return this.buffer;
	}
	
	public void setPacketByte(byte[] buffer) {
		this.buffer = buffer;
	}
	
	public abstract int readFrom(InputStream ips) throws IOException;
	
	public abstract void writeTo(ByteArrayOutputStream destinationStream) throws IOException;

	public abstract int getLength();
}
