package com.elitecore.aaa.diameter.policies.diameterpolicy;

import java.util.ArrayList;
import java.util.List;

import com.elitecore.core.commons.configuration.LoadCacheFailedException;
import com.elitecore.core.serverx.policies.ParserException;
import com.elitecore.core.serverx.policies.PolicyFailedException;
import com.elitecore.core.serverx.policies.data.IPolicyData;
import com.elitecore.core.serverx.policies.data.PolicyGroupData;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;

public class InMemoryDiameterPolicyManager extends DiameterPolicyManager {


	private List<IPolicyData> policyDataList = new ArrayList<IPolicyData>();

	public InMemoryDiameterPolicyManager(String key) {
		super(key);
	}

	public InMemoryDiameterPolicyManager(String key, List<IPolicyData> policyDataList) {
		super(key);
		this.policyDataList = policyDataList;
	}

	@Override
	protected List<IPolicyData> readFromDB()
			throws LoadCacheFailedException {
		return this.policyDataList;
	}

	@Override
	protected List<PolicyGroupData> readPolicyGroupFromDB()
			throws LoadCacheFailedException {
		return new ArrayList<PolicyGroupData>();
	}

	@Override
	protected void serializePolicies() {
		// No ops as the data is in memory
	}

	@Override
	protected void serializePolicyGroup() {
		// No ops as the data is in memory
	}

	@Override
	public List<String> applyPolicies(DiameterRequest request,
			DiameterAnswer response, String strPoliciesToBeApplied,
			String strOverridedCheckItems, String strOverridedRejectItems,
			String strOverridedReplyItems,
			boolean bRejectOnCheckItemNotFound,
			boolean bRejectOnRejectItemNotFound,
			boolean bContinueOnPolicyNotFound) throws ParserException,
	PolicyFailedException {

		return super.applyPolicies(request, response, strPoliciesToBeApplied,
				strOverridedCheckItems, strOverridedRejectItems,
				strOverridedReplyItems, bRejectOnCheckItemNotFound,
				bRejectOnRejectItemNotFound, bContinueOnPolicyNotFound);
	}

	public void setPolicyDataList(List<IPolicyData> policyDataList) {
		this.policyDataList = policyDataList;
	}
}