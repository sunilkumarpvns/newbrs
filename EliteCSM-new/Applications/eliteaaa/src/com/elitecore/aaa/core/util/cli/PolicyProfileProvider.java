package com.elitecore.aaa.core.util.cli;

import com.elitecore.core.commons.utilx.mbean.MBeanConstants;

public class PolicyProfileProvider extends ProfileProvider{
	
	private static PolicyProfileProvider policyProfileDetailProvider;
	
	private PolicyProfileProvider() {
		super(MBeanConstants.POLICY_PROFILE);

	}

	public static PolicyProfileProvider getInstance() {
		if (policyProfileDetailProvider == null) {
			synchronized (PolicyProfileProvider.class) {
				if (policyProfileDetailProvider == null) {
					policyProfileDetailProvider = new PolicyProfileProvider();
				}
			}
		}
		return policyProfileDetailProvider;
	}

	@Override
	public String getKey() {
		return "policy";
	}

}

