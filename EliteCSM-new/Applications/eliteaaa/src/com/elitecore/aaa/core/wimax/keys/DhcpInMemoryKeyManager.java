package com.elitecore.aaa.core.wimax.keys;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.elitecore.aaa.core.conf.DHCPKeysConfiguration;
import com.elitecore.aaa.core.conf.WimaxConfiguration;
import com.elitecore.commons.logging.LogManager;

public class DhcpInMemoryKeyManager extends DhcpKeyManagerImpl {

	private static final String MODULE = "DHCP-IN-MEM-KEY-MGR";
	private final DhcpInMemoryAccessObject inMemoryCache = new DhcpInMemoryAccessObject();

	public DhcpInMemoryKeyManager(DHCPKeysConfiguration dhcpKeysConfiguration, WimaxConfiguration wimaxConfiguration) {
		super(dhcpKeysConfiguration, wimaxConfiguration);
	}

	@Override
	@Nonnull
	public DhcpKeys getOrCreateDhcpKeyDetails(String dhcpIpAddress,
			long currentTimeInMillis) {
		DhcpKeys dhcpKeys =  getDhcpKeyDetailsWithHighestLifetime(
				currentTimeInMillis, inMemoryCache.get(dhcpIpAddress));

		if (dhcpKeys != null) {
			return dhcpKeys;
		}
		dhcpKeys = createAndSaveKeysExclusively(dhcpIpAddress,
				currentTimeInMillis);					
		
		return dhcpKeys;	
	}

	private synchronized DhcpKeys createAndSaveKeysExclusively(String dhcpIpAddress,
			long currentTimeInMillis) {
		
		// check if already cached by another thread
		DhcpKeys dhcpKeys = getDhcpKeyDetailsWithHighestLifetime(
				currentTimeInMillis, inMemoryCache.get(dhcpIpAddress));
		
		// found cached key
		if (dhcpKeys != null) {
			return dhcpKeys;
		}
		
		// not found --> creating new key.
		dhcpKeys = generateDhcpKey(dhcpIpAddress, currentTimeInMillis);		
		
		// saving generated key
		inMemoryCache.save(dhcpIpAddress, dhcpKeys);
		return dhcpKeys;
	}
	
	@Override
	public DhcpKeys getDhcpKeyDetails(String dhcpIpAddress, int dhcpRkKeyId,
			long currentTimeInMillis) {
		return getDhcpKeyForRKId(dhcpRkKeyId, currentTimeInMillis,
				inMemoryCache.get(dhcpIpAddress));
	}

	@Override
	public boolean removeDHCPKey(String dhcpIpAddress) {
		return inMemoryCache.delete(dhcpIpAddress);
	}

	@Override
	@Nullable
	public List<DhcpKeys> getDhcpKeyDetails(String dhcpIP) {
		return inMemoryCache.get(dhcpIP);
	}

	@Override
	protected void removeExpiredKeys() {
		if (LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(MODULE, "Starting the removal of expired DHCP keys");
		}	
		inMemoryCache.deleteAllExpiredKeys();
	}	

}
