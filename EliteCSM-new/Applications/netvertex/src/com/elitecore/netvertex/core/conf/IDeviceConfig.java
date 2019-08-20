/**
 * 
 */
package com.elitecore.netvertex.core.conf;

/**
 * @author krunalh.patel
 *
 */
public interface IDeviceConfig {
	public long getDeviceId();
	public long getProfileId();
	public long getDeviceTypeId();
	public String getDescription();
	public long getLocationId();
	public String getAreaName();
	public String getConnectionURL();
}
