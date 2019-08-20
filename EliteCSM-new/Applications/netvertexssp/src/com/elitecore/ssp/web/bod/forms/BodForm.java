package com.elitecore.ssp.web.bod.forms;

import java.util.List;

import com.elitecore.netvertexsm.ws.cxfws.ssp.subscription.BoDPackage;
import com.elitecore.netvertexsm.ws.cxfws.ssp.subscription.BoDSubscriptionData;
import com.elitecore.ssp.web.core.base.forms.BaseWebForm;

public class BodForm extends BaseWebForm {
	private static final long serialVersionUID = 1L;
	
	private String policyname;
	private Long policyId;
	private List<BoDPackage> availableBodPackages;
	private List<BoDSubscriptionData> activeBodPackages;
	private List<BoDSubscriptionData> bodSubscriptionHistories;
	private String startTime="2/5/2014";
	private List<BoDSubscriptionData> pendingBodReqData;
	private BoDSubscriptionData pendingBodSubReqData;
	
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}
	public List<BoDPackage> getAvailableBodPackages() {
		return availableBodPackages;
	}
	public void setAvailableBodPackages(List<BoDPackage> availableBodPackages) {
		this.availableBodPackages = availableBodPackages;
	}
	public List<BoDSubscriptionData> getActiveBodPackages() {
		return activeBodPackages;
	}
	public void setActiveBodPackages(List<BoDSubscriptionData> activeBodPackages) {
		this.activeBodPackages = activeBodPackages;
	}
	public List<BoDSubscriptionData> getBodSubscriptionHistories() {
		return bodSubscriptionHistories;
	}
	public void setBodSubscriptionHistories(
			List<BoDSubscriptionData> bodSubscriptionHistories) {
		this.bodSubscriptionHistories = bodSubscriptionHistories;
	}
	public Long getPolicyId() {
		return policyId;
	}
	public void setPolicyId(Long policyId) {
		this.policyId = policyId;
	}
	public String getPolicyname() {
		return policyname;
	}
	public void setPolicyname(String policyname) {
		this.policyname = policyname;
	}
	public List<BoDSubscriptionData> getPendingBodReqData() {
		return pendingBodReqData;
	}
	public void setPendingBodReqData(List<BoDSubscriptionData> pendingBodReqData) {
		this.pendingBodReqData = pendingBodReqData;
	}
	public BoDSubscriptionData getPendingBodSubReqData() {
		return pendingBodSubReqData;
	}
	public void setPendingBodSubReqData(BoDSubscriptionData pendingBodSubReqData) {
		this.pendingBodSubReqData = pendingBodSubReqData;
	}	
}