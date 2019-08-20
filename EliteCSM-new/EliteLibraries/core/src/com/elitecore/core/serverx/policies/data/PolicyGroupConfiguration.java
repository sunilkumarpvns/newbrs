package com.elitecore.core.serverx.policies.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "groups")
public class PolicyGroupConfiguration {

	private List<PolicyGroupData> policyGroupData = new ArrayList<PolicyGroupData>();
	private Map<String, String> policyGroupNameToExpression = new HashMap<String, String>();
	
	@XmlElement(name = "data")
	public List<PolicyGroupData> getPolicyGroupDataList() {
		return policyGroupData;
	}
	
	public String getExpressionByGroupName(String groupName) {
		return policyGroupNameToExpression.get(groupName);
	}
	
	public void postRead() {
		for (PolicyGroupData groupData : getPolicyGroupDataList()) {
			this.policyGroupNameToExpression.put(groupData.getName(), groupData.getExpression());
		}
	}
}
