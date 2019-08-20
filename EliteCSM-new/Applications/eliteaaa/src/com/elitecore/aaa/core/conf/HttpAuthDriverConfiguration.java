package com.elitecore.aaa.core.conf;

import java.text.SimpleDateFormat;
import java.util.List;

import com.elitecore.aaa.core.data.AccountDataFieldMapping;
import com.elitecore.aaa.core.drivers.DriverConfiguration;

public interface HttpAuthDriverConfiguration extends DriverConfiguration{
	
	  public String getHttpAuthDriverId();
	  
	  public AccountDataFieldMapping getAccountDataFieldMapping();
	  
	  public String getHttpURL();
	  
	  public int getStatusCheckDuration();
	  public int getMaxQueryTimeoutCount();
	  public SimpleDateFormat[] getExpiryPatterns() ;
	  public List<String> getUserIdentityAttributes();
}
