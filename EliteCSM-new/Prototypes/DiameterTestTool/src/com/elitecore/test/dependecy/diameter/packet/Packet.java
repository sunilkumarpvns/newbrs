package com.elitecore.test.dependecy.diameter.packet;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
	
	public abstract void writeTo(OutputStream destinationStream) throws IOException;
}
