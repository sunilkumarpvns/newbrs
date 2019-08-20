package com.elitecore.aaa.core.conf;

import java.util.List;

import com.elitecore.aaa.core.data.AccountDataFieldMapping;
import com.elitecore.aaa.core.drivers.DriverConfiguration;

public interface DBAuthDriverConfiguration extends DriverConfiguration {	

	  public String getTablename();

	  public int getDbQueryTimeout();

	  public long getMaxQueryTimeoutCount();
	  
	  public AccountDataFieldMapping getAccountDataFieldMapping();

	  public String getDSName();
	  
	  public String getProfileLookUpColumnName();
	
	  public String getSequenceName();
	  
	  public String getPrimaryKeyColumn();
	  
	  public List<String> getUserIdentityAttributes();	  
}

