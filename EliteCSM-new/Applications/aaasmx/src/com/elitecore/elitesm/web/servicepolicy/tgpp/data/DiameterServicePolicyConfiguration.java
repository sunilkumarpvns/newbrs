package com.elitecore.elitesm.web.servicepolicy.tgpp.data;

import java.util.List;

import com.elitecore.aaa.diameter.policies.applicationpolicy.defaultresponsebehavior.DefaultResponseBehavior.DefaultResponseBehaviorType;


public interface DiameterServicePolicyConfiguration {

	public static final int AUTHENTICATE_ONLY = 1;
	public static final int AUTHORIZE_ONLY = 2;
	public static final int AUTHENTICATE_AND_AUTHORIZE = 3;
	
	public String getId();
	
	public String getName();
	
	public String getRuleSet();
	
	public String getSessionManagementEnabled();
	
	public List<CommandCodeResponseAttribute> getCommandCodeResponseAttributesList();
	
	public  DefaultResponseBehaviorType getDefaultResponseBehaviorType();
	
	public  String getDefaultResponseBehaviorParameter();
}
