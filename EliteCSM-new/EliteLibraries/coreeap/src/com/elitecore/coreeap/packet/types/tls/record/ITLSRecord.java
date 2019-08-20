package com.elitecore.coreeap.packet.types.tls.record;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface ITLSRecord {
	
	public void setBytes(byte[] tlsRecordBytes);
	public int readFrom(InputStream sourceStream) throws IOException;
	public byte[] getBytes();
	public void writeTo(OutputStream out) throws IOException;
	public String toString();
	public Object clone() throws CloneNotSupportedException;
	
}
