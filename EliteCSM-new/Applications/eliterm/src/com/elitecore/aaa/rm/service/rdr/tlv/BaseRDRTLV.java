package com.elitecore.aaa.rm.service.rdr.tlv;

public abstract class BaseRDRTLV {
	
	protected int type;
	protected long length;
	protected byte[] value;
	
	public void setType(int type) {
		this.type = type;
	}
	public int getType() {
		return type;
	}
	public void setLength(long length) {
		this.length = length;
	}
	public long getLength() {
		return length;
	}
	public void setBytes(byte[] valuebytes) {
		this.value=valuebytes;
	}
	public byte[] getBytes() {
		return this.value;
	}
}
