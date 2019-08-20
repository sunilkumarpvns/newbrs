package com.elitecore.aaa.radius.plugins.pmqm.conf;


import com.elitecore.core.commons.plugins.PluginConfiguration;

public interface PMQMAcctWSPluginConfiguration extends PluginConfiguration{
	public String getConfigFileName();
	public Object getConfigValue(String key);
	
	public static final String EVENT="EVENT";
	public static final String URL="URL";
	public static final String USER_NAME="USER_NAME";
	public static final String PASSWORD="PASSWORD"; //NOSONAR - Reason: Credentials should not be hard-coded
	public static final String PMQM_PLUGIN_LIST		  = "PMQM_PLUGIN_LIST";
	public static final String ACCT_FIELD_MAPPING	  = "ACCT_FIELD_MAPPING";
	public static final String ATTRIBUTE	          = "ATTRIBUTE";
	public static final String ATTRIBUTE_ID	          = "ATTRIBUTE_ID";
	public static final String DEFAULT_VALUE	      = "DEFAULT_VALUE";
	public static final String ACCT_ATTRIBUTE_ID  	  = "ACCT_ATTRIBUTE_ID";
	public static final String VENDOR_ID	          = "VENDOR_ID";	
	public static final String VALUE_MAPPING	      ="VALUE_MAPPING";
	public static final String MULTIPLE_ATTRIBUTE_ID  ="MULTIPLE_ATTRIBUTE_ID";
	public static final String PM_FIELD	          	  ="PM_FIELD";

}
