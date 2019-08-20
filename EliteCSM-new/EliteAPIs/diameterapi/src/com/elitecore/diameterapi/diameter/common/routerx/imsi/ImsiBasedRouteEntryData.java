package com.elitecore.diameterapi.diameter.common.routerx.imsi;


/**
 * Configuration properties for 
 * Imsi Based Routing Entry
 * 
 * @author monica.lulla
 *
 */
public interface ImsiBasedRouteEntryData {

	/**
	 * 
	 * @return IMSI Prefix
	 */
	String getImsiRange();
	
	/**
	 * 
	 * @return Primary Peer Name
	 */
	String getPrimaryPeer();
	
	/**
	 * 
	 * @return Secondary Peer Name
	 */
	String getSecondaryPeer();

	/**
	 * 
	 * @return Tag
	 */
	String getTag();
}
