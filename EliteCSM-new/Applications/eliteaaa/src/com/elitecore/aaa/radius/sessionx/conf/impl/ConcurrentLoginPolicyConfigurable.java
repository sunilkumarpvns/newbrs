package com.elitecore.aaa.radius.sessionx.conf.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "concurrent-login-policies")
public class ConcurrentLoginPolicyConfigurable {

	private List<ConcurrentLoginPolicyData> concurrentLoginPolicies;
	private Map<String, ConcurrentLoginPolicyData> concurrentLoginPolicyMap;

	public ConcurrentLoginPolicyConfigurable() {
		this.concurrentLoginPolicies = new ArrayList<ConcurrentLoginPolicyData>();
		this.concurrentLoginPolicyMap = new HashMap<String, ConcurrentLoginPolicyData>();
	}

	@XmlElement(name = "concurrent-policy")
	public List<ConcurrentLoginPolicyData> getConcurrentLoginPolicies() {
		return concurrentLoginPolicies;
	}

	public void postRead() {
		Map<String, ConcurrentLoginPolicyData> tmpCurrentLoginPolicyMap = new HashMap<String, ConcurrentLoginPolicyData>();
		for (ConcurrentLoginPolicyData data : this.concurrentLoginPolicies) {
			tmpCurrentLoginPolicyMap.put(data.getName(), data);
		}
		this.concurrentLoginPolicyMap = tmpCurrentLoginPolicyMap;
	}

	public ConcurrentLoginPolicyData getConcurrentPolicyData(String policyName) {
		return this.concurrentLoginPolicyMap.get(policyName);
	}
}
