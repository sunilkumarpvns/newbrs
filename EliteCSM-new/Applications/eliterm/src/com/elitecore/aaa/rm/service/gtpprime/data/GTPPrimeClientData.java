/**
 * 
 */
package com.elitecore.aaa.rm.service.gtpprime.data;


/**
 * @author dhaval.jobanputra
 *
 */
public interface GTPPrimeClientData {
	
	public void setClientIP(String ip);
	public void setClientPort(int port);
	public void setNodeAliveRequest(boolean value );
	public void setEchoRequestInterval(int value);
	public void setRequestExpiryTime(int time);
	
	public String getClientIP();
	public int getClientPort();
	public boolean getNodeAliveRequest();
	public int getEchoRequestInterval();
	public long getRequestExpiryTime();
	public int getRequestRetry();
	public String getRedirectionIP();
	public String getFileName();
	public boolean getIsFileSequenceRequired();
	public String getFileLocation();
	public int getRollingType();
	public long getRollingUnit();
	public String toString();
	public void setRequestRetry(int requestRetry);
	public void setFileLocation(String fileLocation);
	public void setRollingUnit(int rollingUnit);
	public void setRedirectionIP(String redirectionIP);
	public void setRollingType(int rollingType);
	public void setFileName(String fileName);
	public void setIsFileSequenceRequired(boolean value);
	
	public void setMinSequenceRange(int value);
	public void setMaxSequenceRange(int value);
	public int getMinSequenceRange();
	public int getMaxSequenceRange();
}
