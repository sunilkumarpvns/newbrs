package com.elitecore.aaa.diameter.policies.applicationpolicy.conf;

import java.util.List;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.elitecore.aaa.core.data.AdditionalResponseAttributes;
import com.elitecore.aaa.diameter.policies.applicationpolicy.conf.impl.CommandCodeResponseAttribute;
import com.elitecore.aaa.diameter.policies.applicationpolicy.defaultresponsebehavior.DefaultResponseBehavior.DefaultResponseBehaviorType;


public interface DiameterServicePolicyConfiguration {

	public static final int AUTHENTICATE_ONLY = 1;
	public static final int AUTHORIZE_ONLY = 2;
	public static final int AUTHENTICATE_AND_AUTHORIZE = 3;
	
	public String getId();
	
	public String getName();
	
	public String getRuleSet();
	
	Boolean isSessionManagementEnabled();
	
	public List<CommandCodeResponseAttribute> getCommandCodeResponseAttributesList();
	
	public Map<Integer,AdditionalResponseAttributes> getCommandCodeResponseAttributesMap();
	
	public @Nonnull DefaultResponseBehaviorType getDefaultResponseBehaviorType();
	
	public @Nullable String getDefaultResponseBehaviorParameter();

	
}
