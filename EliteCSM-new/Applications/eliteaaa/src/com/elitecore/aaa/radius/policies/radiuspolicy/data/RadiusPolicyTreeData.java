package com.elitecore.aaa.radius.policies.radiuspolicy.data;

import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.radius.service.RadServiceResponse;
import com.elitecore.core.serverx.policies.data.PolicyTreeData;

public class RadiusPolicyTreeData extends PolicyTreeData {
	private RadServiceRequest radiusServiceReqeust;
	private RadServiceResponse radiusServiceResponse;
	private boolean rejectOnCheckItemNotFound;
	private boolean rejectOnRejectItemNotFound;
	private boolean continueOnPolicyNotFound;
	private boolean bApplyCheckItemForPortalRequest;

	private int clientType;
	public RadiusPolicyTreeData(RadServiceRequest radServiceRequest, RadServiceResponse radServiceResponse){
		radiusServiceReqeust = radServiceRequest;
		radiusServiceResponse = radServiceResponse;
	}

	public RadServiceRequest getRadiusServiceRequest() {
		return radiusServiceReqeust;
	}

	public RadServiceResponse getRadiusServiceResponse() {
		return radiusServiceResponse;
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
