/**
 * 
 */
package com.elitecore.aaa.rm.data;

/**
 * @author nitul.kukadia
 *
 */
public interface RDRClientData {
	public String getClientIP();
	public int getClientPort();
	
	public String getFileName();
	public String getFileLocation();
	
	public int getRollingType();
	public long getRollingUnit();

	public String toString();
}
