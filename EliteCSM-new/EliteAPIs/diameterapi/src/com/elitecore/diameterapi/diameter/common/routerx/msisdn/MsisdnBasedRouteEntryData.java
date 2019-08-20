package com.elitecore.diameterapi.diameter.common.routerx.msisdn;


/**
 * Configuration properties for 
 * Msisdn Based Routing Entry
 * 
 * @author monica.lulla
 *
 */
public interface MsisdnBasedRouteEntryData {

	/**
	 * 
	 * @return MSISDN range
	 */
	public String getMsisdnRange();
	
	/**
	 * 
	 * @return Primary Peer Name
	 */
	public String getPrimaryPeer();
	
	/**
	 * 
	 * @return Secondary Peer Name
	 */
	public String getSecondaryPeer();

	public String getTag();
}
