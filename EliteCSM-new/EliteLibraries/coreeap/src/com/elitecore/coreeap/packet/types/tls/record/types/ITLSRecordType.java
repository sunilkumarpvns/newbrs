package com.elitecore.coreeap.packet.types.tls.record.types;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface ITLSRecordType extends Cloneable{

	public void setBytes(byte[] messageData);
	public int readFrom(InputStream sourceStream) throws IOException;
	public byte[] getBytes();
	public void writeTo(OutputStream out) throws IOException;	
	public String toString();
	public int getType();
	public Object clone() throws CloneNotSupportedException;

}
