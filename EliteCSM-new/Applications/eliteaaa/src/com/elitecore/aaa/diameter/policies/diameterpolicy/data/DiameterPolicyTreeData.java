package com.elitecore.aaa.diameter.policies.diameterpolicy.data;

import com.elitecore.core.serverx.policies.data.PolicyTreeData;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;

public class DiameterPolicyTreeData extends PolicyTreeData {

	private DiameterRequest diameterRequest;
	private DiameterAnswer diameterAnswer;	
	private boolean rejectOnCheckItemNotFound;
	private boolean rejectOnRejectItemNotFound;
	private boolean continueOnPolicyNotFound;
	private boolean bApplyCheckItemForPortalRequest;
	private int clientType;
	public DiameterPolicyTreeData(DiameterRequest request, DiameterAnswer response){
		this.diameterRequest = request;
		this.diameterAnswer = response;
	}

	public DiameterRequest getDiameterRequest() {
		return diameterRequest;
	}
	
	public DiameterAnswer getDiameterAnswer() {
		return diameterAnswer;
	}

	public void setRejectOnCheckItemNotFound(boolean rejectOnCheckItemNotFound) {
		this.rejectOnCheckItemNotFound = rejectOnCheckItemNotFound;
	}

	public boolean isRejectOnCheckItemNotFound() {
		return rejectOnCheckItemNotFound;
	}

	public void setRejectOnRejectItemNotFound(boolean rejectOnRejectItemNotFound) {
		this.rejectOnRejectItemNotFound = rejectOnRejectItemNotFound;
	}

	public boolean isRejectOnRejectItemNotFound() {
		return rejectOnRejectItemNotFound;
	}

	public void setContinueOnPolicyNotFound(boolean continueOnPolicyNotFound) {
		this.continueOnPolicyNotFound = continueOnPolicyNotFound;
	}

	public boolean isContinueOnPolicyNotFound() {
		return continueOnPolicyNotFound;
	}

	public void setClientType(int clientType) {
		this.clientType = clientType;
	}

	public int getClientType() {
		return clientType;
	}
	
	
	public boolean isbApplyCheckItemForPortalRequest() {
		return bApplyCheckItemForPortalRequest;
	}

	public void setbApplyCheckItemForPortalRequest(
			boolean bApplyCheckItemForPortalRequest) {
		this.bApplyCheckItemForPortalRequest = bApplyCheckItemForPortalRequest;
	}
}
