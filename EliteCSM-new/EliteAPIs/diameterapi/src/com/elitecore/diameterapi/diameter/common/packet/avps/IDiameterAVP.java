package com.elitecore.diameterapi.diameter.common.packet.avps;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;

public interface IDiameterAVP extends Cloneable{
	
	
	public Object clone() throws CloneNotSupportedException;
	/**
	 * Return length of entire AVP without padding.
	 */
	public int getLength();
	
	/**
	 * Return padding length of AVP.
	 */
	public int getPaddingLength();
	
	/**
	 * Return flag value of AVP.
	 */
	public int getFlag();
	
	/**
	 * Return vendor id of AVP if available.
	 */
	public int getVendorId();
	
	/**
	 * Retunr Code of AVP.
	 */
	public int getAVPCode();
	
	/**
	 * Return entire AVP in byte form.
	 */
	public byte[] getValueBytes();
	
	public byte[] getBytes();
	
	public void writeTo(OutputStream out) throws IOException;
	
	public boolean isGrouped();
	
	public long getInteger();

	public double getFloat();

	public java.util.ArrayList<IDiameterAVP> getGroupedAvp();
	
	public void setTime(java.util.Date date);
	
	public long getTime();

	public void setLength(int length);

	public void setValueBytes(byte [] data);
	
	public void setInteger(long data);
	
	public void setFloat(double data);
	
	public void setFlag(int flag);
	
	public void setGroupedAvp(java.util.ArrayList<IDiameterAVP> childAttr);

	public int readFlagOnwardsFrom(java.io.InputStream sourceStream);
	
	public String getStringValue();
	
	public String getStringValue(boolean bUseDictionaryValue);
	
	public String getKeyStringValue(String key);
	
	public Set<String> getKeySet();
	
	public void setKeyStringValue(String key, String value);
	
	public boolean isMandatory();
	
	public boolean isVendorSpecificAttribute();
	
	public boolean isProtected();
	
	public void setStringValue(String data);
	public void refreshAVPHeader();
	
	public boolean hasValue();
	public String getAVPId();
	
	public String getLogValue();
	public void doPlus(String stringValue);
}
