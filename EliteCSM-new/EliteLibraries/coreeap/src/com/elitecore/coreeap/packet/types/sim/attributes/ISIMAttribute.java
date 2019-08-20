package com.elitecore.coreeap.packet.types.sim.attributes;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface ISIMAttribute extends Cloneable{
	public int getType();
	public void setType(int type);
	public int readLengthOnwardsFrom(InputStream sourceStream) throws IOException ;
	public Object clone() throws CloneNotSupportedException;
	public byte[] getBytes();
	public int getLength();
	public byte[] getValueBytes();
	public int readFrom(InputStream sourceStream) throws IOException;
	public void setBytes(byte[] valueBytes);
	public void setValueBytes(byte[] valueBytes);
	public void writeTo(OutputStream destinationStream) throws IOException;
	public String getStringValue();
	public byte[] getReservedBytes();
	public void setReservedBytes(byte[] reservedBytes);
}
