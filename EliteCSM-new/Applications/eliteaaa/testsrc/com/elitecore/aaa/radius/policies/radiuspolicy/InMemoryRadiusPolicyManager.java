package com.elitecore.aaa.radius.policies.radiuspolicy;

import java.util.ArrayList;
import java.util.List;

import com.elitecore.aaa.radius.policies.radiuspolicy.RadiusPolicyManager;
import com.elitecore.aaa.radius.service.auth.RadAuthRequest;
import com.elitecore.aaa.radius.service.auth.RadAuthResponse;
import com.elitecore.core.commons.configuration.LoadCacheFailedException;
import com.elitecore.core.serverx.policies.ParserException;
import com.elitecore.core.serverx.policies.PolicyFailedException;
import com.elitecore.core.serverx.policies.data.IPolicyData;
import com.elitecore.core.serverx.policies.data.PolicyGroupData;

public class InMemoryRadiusPolicyManager extends RadiusPolicyManager {

	public InMemoryRadiusPolicyManager(String key) {
		super(key);
	}
	
	public InMemoryRadiusPolicyManager(String key, List<IPolicyData> policyDataList) {
		super(key);
		this.policyDataList = policyDataList;
	}
	
	@Override
	protected List<IPolicyData> readFromDB() throws LoadCacheFailedException {
		return this.policyDataList;
	}
	
	@Override
	protected List<PolicyGroupData> readPolicyGroupFromDB() throws LoadCacheFailedException {
		return new ArrayList<PolicyGroupData>();
	}
	
	@Override
	public List<String> applyPolicies(RadAuthRequest request, RadAuthResponse response,
			String strPoliciesToBeApplied, int iVendorType, String strOverridedCheckItems,
			String strOverridedRejectItems, String strOverridedReplyItems, boolean bRejectOnCheckItemNotFound,
			boolean bRejectOnRejectItemNotFound, boolean bContinueOnPolicyNotFound)
			throws ParserException, PolicyFailedException {
		
		return super.applyPolicies(request, response, strPoliciesToBeApplied, iVendorType, strOverridedCheckItems,
				strOverridedRejectItems, strOverridedReplyItems, bRejectOnCheckItemNotFound, bRejectOnRejectItemNotFound,
				bContinueOnPolicyNotFound);
	}
	
	@Override
	public List<String> applyPolicies(RadAuthRequest request, RadAuthResponse response, String strPoliciesToBeApplied,
			int iVendorType, String strOverridedCheckItems, String strOverridedRejectItems,
			String strOverridedReplyItems, boolean bRejectOnCheckItemNotFound, boolean bRejectOnRejectItemNotFound,
			boolean bContinueOnPolicyNotFound, boolean bApplyCheckItemForPortalRequest)
			throws ParserException, PolicyFailedException {
		
		return super.applyPolicies(request, response, strPoliciesToBeApplied, iVendorType, strOverridedCheckItems,
				strOverridedRejectItems, strOverridedReplyItems, bRejectOnCheckItemNotFound, bRejectOnRejectItemNotFound,
				bContinueOnPolicyNotFound, bApplyCheckItemForPortalRequest);
	}
}