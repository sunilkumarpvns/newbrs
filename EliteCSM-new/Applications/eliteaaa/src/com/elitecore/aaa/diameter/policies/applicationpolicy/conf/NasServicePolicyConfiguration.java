package com.elitecore.aaa.diameter.policies.applicationpolicy.conf;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.elitecore.aaa.diameter.policies.applicationpolicy.conf.impl.NasAcctDetail;
import com.elitecore.aaa.diameter.policies.applicationpolicy.conf.impl.NasAuthDetail;
import com.elitecore.aaa.diameter.service.application.handlers.conf.DiameterAuthenticationHandlerData;
import com.elitecore.aaa.diameter.service.application.handlers.conf.DiameterAuthorizationHandlerData;
import com.elitecore.aaa.radius.policies.servicepolicy.conf.ChargeableUserIdentityConfiguration;

public interface NasServicePolicyConfiguration extends DiameterServicePolicyConfiguration {
	public DiameterAuthenticationHandlerData getAuthenticationHandlerData();
	public DiameterAuthorizationHandlerData getAuthorizationHandlerData();
	public ArrayList<String> getUserIdentities();
	public int getCaseSensitivity();	
	public String getStripUserIdentity();
	public boolean getTrimUserIdentity();
	public String getRealmSeparator();
	public String getUserNameConfiguration();
	public ArrayList<String> getUserNameRespAttrList();
	
	public Map<String,Integer>getAuthDriverInstanceIdsMap();	
	public Map<String,Integer> getAcctDriverInstanceIdsMap();
	public boolean isBTrimPassword();
	public Map<String,String> getSecondaryAndCacheDriverRelMap();
	public int getRequestType();
	public List<String> getAdditionalDrivers();
	public NasAuthDetail getNasAuthDetail();
	public NasAcctDetail getNasAcctDetail();
	public String getAuthDriverScript();
	public String getAcctDriverScript();
	public String getAnonymousProfileIdentity();
	public ChargeableUserIdentityConfiguration getCuiConfiguration();
	public String getAdditionalDiameterConcuurencyConfigId();
	public String getDiameterConcurrencyConfigId();
	
}
