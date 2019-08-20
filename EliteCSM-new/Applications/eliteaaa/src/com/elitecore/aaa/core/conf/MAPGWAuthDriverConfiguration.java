package com.elitecore.aaa.core.conf;

import java.util.List;

import com.elitecore.aaa.core.data.AccountDataFieldMapping;
import com.elitecore.aaa.core.drivers.DriverConfiguration;

public interface MAPGWAuthDriverConfiguration extends DriverConfiguration{
	public String getRemoteHost();
	public String getLocalHost();
	public AccountDataFieldMapping getAccountDataFieldMapping();
	public int getNoOfMAPGWConnections();
	public long getRequestTimeout();
	public int getMaxQueryTimeoutCount();
	public int getStatusCheckDuration() ;
	public List<String> getUserIdentityAttributes();
	public boolean getIsSAIEnabled();
	public boolean isRestoreDataEnabled();
	public int getNumberOfSIMTriplets();
}
