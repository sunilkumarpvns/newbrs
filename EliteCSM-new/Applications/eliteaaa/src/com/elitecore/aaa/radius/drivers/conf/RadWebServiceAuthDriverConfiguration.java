package com.elitecore.aaa.radius.drivers.conf;

import java.text.SimpleDateFormat;
import java.util.List;

import com.elitecore.aaa.core.data.AccountDataFieldMapping;
import com.elitecore.aaa.core.drivers.DriverConfiguration;
import com.elitecore.core.commons.configuration.LoadConfigurationException;

public interface RadWebServiceAuthDriverConfiguration extends DriverConfiguration {

	public void readConfiguration() throws LoadConfigurationException;
	public String getServiceAddress();
	public AccountDataFieldMapping getAccountDataFieldMapping();
	public String getIMSIAttribute();
	public int getMaxQueryTimeoutCount();
	public int getStatusCheckDuration();
	public SimpleDateFormat[] getExpiryDatePatterns();
	public List<String> getUserIdentityAttributes();
	
}
