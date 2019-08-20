package com.elitecore.aaa.core.wimax;

import java.io.Serializable;

public interface WimaxSessionData extends Cloneable, Serializable{
	
	public long getLastAccessTime();
	public long getSessionLifetime() ;
	public long getDefaultSessionTimeoutInSeconds(); 
	public String getStringAAASessionId();
	public String getUsername() ;
	public String getAAASessionId() ;
	public String getStringAaaSessionId() ;
	public byte[] getHA_RK_KEY() ;
	public long getHA_RK_Lifetime_In_Seconds();
	public long getHA_RK_Generation_Time_In_Millis();
	public int getHA_RK_SPI() ;
	public int getSalt() ;
	public byte[] getHa_salt() ;
	public byte[] getMN_HA_MIP4_KEY() ;
	public long getMN_HA_MIP4_SPI() ;
	public byte[] getMn_salt() ;
	public byte[] getMSK() ;
	public byte[] getEMSK() ;
	public String getHA_IP_MIP4() ;
	public byte[] getHaIpAddressInBytes();
	public byte[] getFA_RK_KEY() ;
	public long getFA_RK_SPI() ;
	public String getCallingStationId();
	public String getNasIP();
	public String getNasIdentifier();
	public String getNasPort();
	public String getNasPortType();
	public String getNasPortId();
	public String toString();
	public String getFramedIPAddress() ;
	public byte[] getDhcp_rk();
	public int getDhcp_rk_id() ;
	public long getDhcp_rk_lifetime_in_seconds() ;
	public String getDHCPV4_SERVER() ;
	public void setDHCPV4_SERVER(String dhcpv4_server) ;
	public long getDhcp_rk_generation_time() ;
	public byte[] getDhcp_salt() ;
	public Object clone() throws CloneNotSupportedException;

	public Integer getAccountingCapabilities();
	public Integer getHotliningCapabilities();
	public byte[] getWimaxRelease();
	public Integer getIdleModeNotificationCapabilities();
	
	//adding the method to add the CUI in case of the HA request
	public String getCUI();
}
