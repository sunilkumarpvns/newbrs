/**
 * 
 */
package com.elitecore.aaa.rm.data;

/**
 * @author dhaval.jobanputra
 *
 */
public interface GTPPrimeClientData {
	
	public void setClientIP(String ip);
	public void setClientPort(int port);
	public void setNodeAliveRequest(boolean value );
	public void setEchoRequest(boolean value);
	public void setRequestExpiryTime(int time);
	public void setRequestRetry(int value);
	public void setRedirectionIP(String ip);
	public void setFileLocation(String loc);
	public void setRollingType(int type);
	public void setRollingUnit(long unit);
	public void setMaxRolledUnit(int unit);
	public void setCompressRolledUnit(boolean value);
	
	public String getClientIP();
	public int getClientPort();
	public boolean getNodeAliveRequest();
	public boolean getEchoRequest();
	public int getRequestExpiryTime();
	public int getRequestRetry();
	public String getRedirectionIP();
	public String getFileLocation();
	public int getRollingType();
	public long getRollingUnit();
	public int getMaxRolledUnit();
	public boolean getCompressRolledUnit();
	public String toString();
}
