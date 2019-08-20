package com.elitecore.aaa.core.wimax.keys;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class DhcpInMemoryAccessObject {
	
	private ConcurrentHashMap<String, List<DhcpKeys>> dhcpKeys = new ConcurrentHashMap<String, List<DhcpKeys>>();
	
	public List<DhcpKeys> get(String dhcpAddress) {
		return dhcpKeys.get(dhcpAddress);
	}
	
	public boolean delete(String dhcpIpAddress) {
		return dhcpKeys.remove(dhcpIpAddress) != null;
	}

	public void save(String dhcpIpAddress, List<DhcpKeys> dhcpKeyDetailsList) {
		dhcpKeys.put(dhcpIpAddress, dhcpKeyDetailsList);
	}
	
	public void save(String dhcpIpAddress, DhcpKeys details) {
		List<DhcpKeys> dhcpKeyDetailsList = get(dhcpIpAddress);
		if(dhcpKeyDetailsList == null){
			dhcpKeyDetailsList = new ArrayList<DhcpKeys>();
			save(dhcpIpAddress, dhcpKeyDetailsList);
		}
		synchronized (dhcpKeyDetailsList) {
			dhcpKeyDetailsList.add(details);		
		}
	}

	public void deleteAllExpiredKeys() {
		long currentTimeInMillis = System.currentTimeMillis();

		for (Entry<String, List<DhcpKeys>> entry: dhcpKeys.entrySet()){ 
			List<DhcpKeys> dhcpKeyDetailsList = entry.getValue();					

			int j = 0;
			while (j < dhcpKeyDetailsList.size()) {
				DhcpKeys tempDetails = dhcpKeyDetailsList.get(j);
				if (tempDetails.isExpired(currentTimeInMillis)) {
					synchronized (dhcpKeyDetailsList) {
						dhcpKeyDetailsList.remove(j);
						continue;
					}
				}						
				j++;						
			}
		}
		
	}

}
