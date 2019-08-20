package com.elitecore.aaa.radius.policies;

import com.elitecore.aaa.radius.policies.radiuspolicy.RadiusParseTreeNode;
import com.elitecore.core.serverx.policies.ParserUtility;
import com.elitecore.core.serverx.policies.PolicyType;
import com.elitecore.core.serverx.policies.parsetree.PolicyParseTreeNode;
import com.elitecore.core.util.data.BaseRuleSet;

public class RadiusRuleSet extends BaseRuleSet {
	
	public RadiusRuleSet(String ruleSetName, String checkItem, String rejectItem) {
		super(ruleSetName,checkItem,rejectItem);
	}
	
	@Override
	protected PolicyParseTreeNode getAttributeParseTreeNode(
			String strPolicyToken, PolicyType policyType, String policyName) {
		String[] strSplitedExpression = ParserUtility.splitKeyAndValue(strPolicyToken);
		RadiusParseTreeNode radiusParseTreeNode = new RadiusParseTreeNode(strSplitedExpression,true,policyType, policyName);
		return radiusParseTreeNode;
	}
	
}
