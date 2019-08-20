package com.elitecore.aaa.radius.plugins.quotamgmt.conf;

import java.util.ArrayList;

import com.elitecore.aaa.radius.service.acct.plugins.DS;
import com.elitecore.core.commons.plugins.PluginConfiguration;

public interface QuotaManagementPluginConf extends PluginConfiguration  {

	public static final String CONTINUE_FURTHER_PROCESSING = "CONTINUE_FURTHER_PROCESSING";
	public static final String ACTION = "ACTION";
	public static final String TYPE_OF_PACKET = "TYPE_OF_PACKET";
	public static final String CLASS_ATTRIBUTE_KEY_FOR_VOLUME = "CLASS_ATTRIBUTE_KEY_FOR_VOLUME";
	public static final String CLASS_ATTRIBUTE_KEY_FOR_TIME = "CLASS_ATTRIBUTE_KEY_FOR_TIME";
	public static final String LIST_OF_ATTRIBUTES = "LIST_OF_ATTRIBUTES";
	public static final String QUOTA_TYPE = "QUOTA_TYPE";
	public static final int ACCEPT = 1;
	public static final int DROP = 3;
	
	public Object getValue(String key);
	public Integer getAction(String key);
	public ArrayList<DS> getListOfAttributes();
}
