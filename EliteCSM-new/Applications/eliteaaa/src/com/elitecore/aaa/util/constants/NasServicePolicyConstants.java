package com.elitecore.aaa.util.constants;

import com.elitecore.aaa.diameter.policies.applicationpolicy.defaultresponsebehavior.DefaultResponseBehavior;

public class NasServicePolicyConstants {

	public static final Long DEFAULT_SESSION_TIMEOUT = 600L;
	public static final boolean TRIM_USER_IDENTITY = true; 
	public static final Integer REQUEST_TYPE = 1;
	public static final String DEFAULT_RESPONSE_BEHAVIOUR = DefaultResponseBehavior.DefaultResponseBehaviorType.REJECT.name();
	public static final String REALM_PATTERN = "Suffix";
	public static final Integer CASE_SENSITIVITY = 1;
	public static final String CUI = "NONE";

}
