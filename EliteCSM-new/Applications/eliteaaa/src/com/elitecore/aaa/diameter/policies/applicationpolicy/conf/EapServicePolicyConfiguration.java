package com.elitecore.aaa.diameter.policies.applicationpolicy.conf;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.elitecore.aaa.diameter.service.application.handlers.conf.DiameterAuthorizationHandlerData;
import com.elitecore.aaa.radius.policies.servicepolicy.conf.ChargeableUserIdentityConfiguration;
import com.elitecore.core.commons.plugins.data.PluginEntryDetail;

public interface EapServicePolicyConfiguration extends DiameterServicePolicyConfiguration {
	DiameterAuthorizationHandlerData getAuthorizationHandlerData();
	public Map<String,Integer> getAuthDriverInstanceIdsMap();
	public String getDriverScript();
	
	public ArrayList<String> getUserIdentities();
	public int getCaseSensitivity();	
	public String getStripUserIdentity();
	public boolean getTrimUserIdentity();
	public String getRealmSeparator();
	public boolean isBTrimPassword();
	public String getEapConfId();
	public List<String> getAdditionalDrivers();
	public int getRequestType();
	public ChargeableUserIdentityConfiguration getCuiConfiguration();
	String getDiameterConcurrencyConfigId();
	String getAdditionalDiameterConcuurencyConfigId();
	public List<PluginEntryDetail> getPrePlugins();
	public List<PluginEntryDetail> getPostPlugins();
}
