package com.elitecore.aaa.core.drivers;

import com.elitecore.aaa.core.data.AccountData;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.systemx.esix.ESCommunicatorGroupImpl;


public class CacheCommunicatorGroupImpl extends ESCommunicatorGroupImpl<IEliteAuthDriver> implements CacheCommunicatorGroup {
	private static final String MODULE= "CACHE-COMM-GROUP";
	
	@Override
	public void saveAccountData(AccountData accountData) {
		
			IEliteAuthDriver authDriver = getCommunicator();
			if(authDriver!=null && authDriver.isAlive()){
				authDriver.saveAccountData(accountData);
			}else if(authDriver!=null && !authDriver.isAlive()){
					LogManager.getLogger().warn(MODULE, "Driver is not alive, caching a profile is failed.");
			}
		
	}

}
