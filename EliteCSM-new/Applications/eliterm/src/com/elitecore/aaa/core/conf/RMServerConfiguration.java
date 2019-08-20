package com.elitecore.aaa.core.conf;

import com.elitecore.aaa.rm.conf.GTPPrimeConfiguration;
import com.elitecore.aaa.rm.conf.RDRConfiguration;
import com.elitecore.aaa.rm.conf.RMChargingServiceConfiguration;
import com.elitecore.aaa.rm.conf.RMConcurrentLoginServiceConfiguration;
import com.elitecore.aaa.rm.conf.RMIPPoolConfiguration;
import com.elitecore.aaa.rm.conf.RMPrepaidChargingServiceConfiguration;
import com.elitecore.aaa.rm.conf.RdrDetailLocalConfiguration;

public interface RMServerConfiguration extends AAAServerConfiguration{
	
	public RMChargingServiceConfiguration getRmChargingServiceConfiguration();
	public RMIPPoolConfiguration getIPPoolConfiguration();
	public GTPPrimeConfiguration getGTPPrimeConfiguration();
	public RMConcurrentLoginServiceConfiguration getRMConcurrentLoginServiceConfiguration();
	public RMPrepaidChargingServiceConfiguration getRMPrepaidChargingServiceConfiguration();
	public RDRConfiguration getRDRConfiguration();
	public RdrDetailLocalConfiguration getRdrDetailLocalConfiguration();

}
