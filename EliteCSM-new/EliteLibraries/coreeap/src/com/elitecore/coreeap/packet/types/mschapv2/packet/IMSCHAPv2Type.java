package com.elitecore.coreeap.packet.types.mschapv2.packet;


public interface IMSCHAPv2Type extends Cloneable{
	public int getIdentifier();
	public void setIdentifier(int identifier);
	public int getMsLength();
	public void setMsLength(int msLength);
	public byte[] getValueBuffer();	
	public void setValueBuffer(byte[] data) throws IllegalArgumentException;
	public byte[] toBytes();	
	public String toString();	
	public Object clone() throws CloneNotSupportedException;
}
