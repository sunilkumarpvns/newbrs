package com.elitecore.coreeap.packet.types.tls.record.types.handshakemessages;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface ITLSHandshakeMessage extends Cloneable{

	public byte[] getBytes();
	public String toString();	
	public void setBytes(byte[] handshakeMessageBytes);
	public int readFrom(InputStream sourceStream) throws IOException;
	public void writeTo(OutputStream out) throws IOException;
	public Object clone() throws CloneNotSupportedException;
	public int getType();
}
