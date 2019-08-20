package com.elitecore.aaa.core.wimax.keys;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nullable;

import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.core.wimax.WimaxRequest;
import com.elitecore.aaa.core.wimax.WimaxResponse;
import com.elitecore.aaa.core.wimax.keys.HAKeyManagerImpl.HAKeyStatistics.HAKeyCounter;

public class KeyManagerImpl implements KeyManager {
	
	private HAKeyManager haKeyManager;
	private DhcpKeyManager dhcpKeyManager;
	
	public KeyManagerImpl(AAAServerContext serverContext,
			HAKeyManager haKeyManager,
			DhcpKeyManager dhcpKeyManager) {
		
		this.haKeyManager = haKeyManager;
		this.dhcpKeyManager = dhcpKeyManager;
	}

	public static KeyManager newInstance(AAAServerContext serverContext) {
		
		HAKeyManager haInstance = HAKeyManagerImpl.newInstance(serverContext);
		DhcpKeyManager dhcpInstance = DhcpKeyManagerImpl.newInstance(serverContext);
		return new KeyManagerImpl(serverContext, haInstance, dhcpInstance);
	}
	
	@Override
	public DhcpKeys getOrCreateDhcpKeyDetails(String dhcp_Ip_address, long currentTimeInMillis) throws Exception {		
		return dhcpKeyManager.getOrCreateDhcpKeyDetails(dhcp_Ip_address, currentTimeInMillis);	
	}

	public DhcpKeys getDhcpKeyDetails(String dhcp_Ip_address, int dhcp_Key_Id, long currentTimeInMillis) {
		return dhcpKeyManager.getDhcpKeyDetails(dhcp_Ip_address, dhcp_Key_Id, currentTimeInMillis);
	}
	
	@Override
	public boolean removeDHCPKey(String string) {
		return dhcpKeyManager.removeDHCPKey(string);
	}

	@Override
	public List<DhcpKeys> getDhcpKeyDetails(String dhcpIP) throws Exception {
		return dhcpKeyManager.getDhcpKeyDetails(dhcpIP);
	}

	@Override
	public ConcurrentHashMap<String, HAKeyCounter> getHAKeyStatistics() {
		return haKeyManager.getHAKeyStatistics(); 
	}

	@Override
	public HAKeyDetails getOrCreateHaKeyDetails(String ha_Ip_address,
			WimaxRequest wimaxRequest, WimaxResponse wimaxResponse)
			throws Exception {
		return haKeyManager.getOrCreateHaKeyDetails(ha_Ip_address, 
				wimaxRequest, wimaxResponse);
	}

	@Override
	public boolean removeHAKey(String string) {
		return haKeyManager.removeHAKey(string);
	}

	@Override
	@Nullable
	public HAKeyDetails getHaKeyDetails(String haIp) throws Exception {
		return haKeyManager.getHaKeyDetails(haIp);
	}

}

