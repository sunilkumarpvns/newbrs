package com.elitecore.aaa.radius.drivers.conf;

import com.elitecore.aaa.core.data.AccountDataFieldMapping;
import com.elitecore.aaa.core.drivers.DriverConfiguration;

public interface RadMAPGWDriverConfiguration extends DriverConfiguration {
	public String getRemoteHost();
	public String getLocalHost();
	public AccountDataFieldMapping getAccountDataFieldMapping();
	public int getNoOfMAPGWConnections();
	public long getRequestTimeout();
	public int getMaxQueryTimeoutCount();

	public int getStatusCheckDuration() ;

	
}
