package com.elitecore.aaa.radius.sessionx.conf;

import javax.annotation.Nullable;

import com.elitecore.aaa.radius.sessionx.conf.impl.ConcurrentLoginPolicyData;

public interface ConcurrentLoginPolicyConfiguration {
	public @Nullable ConcurrentLoginPolicyData getConcurrentLoginPolicy(String policyName);
}
