package com.elitecore.aaa.core.wimax.keys;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface DhcpKeyManager {
	
	@Nonnull
	DhcpKeys getOrCreateDhcpKeyDetails(String dhcp_Ip_address,long currentTimeInMillis) throws Exception;
	DhcpKeys getDhcpKeyDetails(String dhcp_Ip_address,int dhcp_Key_Id,long currentTimeInMillis);
	
	boolean removeDHCPKey(String string);
	
	@Nullable 
	List<DhcpKeys> getDhcpKeyDetails(String dhcpIP) throws Exception;
	
}
