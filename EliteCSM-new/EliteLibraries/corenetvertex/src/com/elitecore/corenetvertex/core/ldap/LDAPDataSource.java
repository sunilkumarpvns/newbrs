package com.elitecore.corenetvertex.core.ldap;

import java.util.ArrayList;

public interface LDAPDataSource {
	public String getStrIpAddress();
	
	public String getAdministrator();
	
	public int getVersion();
	
	public String getDataSourceId();
	
	public String getDataSourceName();
	
	public String getIpAddress();
	
	public long getLdapSizeLimit();
	
	public int getMaxPoolSize();
	
	public int getMinPoolSize();
	
	public String getPassword();
	
	public String getPlainTextPassword();
	
	public int getPort();
	
	public int getTimeout();
	
	public String getUserPrefix();
	
	public ArrayList<String> getSearchBaseDnList();
}
