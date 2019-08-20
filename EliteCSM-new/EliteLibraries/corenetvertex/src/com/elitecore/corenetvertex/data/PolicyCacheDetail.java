package com.elitecore.corenetvertex.data;


import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.constants.PolicyStatus;



public class PolicyCacheDetail implements Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private PolicyStatus status;
	private String remark;
	private Date date;
	private List<PolicyDetail> failurePolicyList;
	private List<PolicyDetail> partialSuccessPolicyList;
	private List<PolicyDetail> successPolicyList;
	private List<PolicyDetail> lastKnownGoodPolicyList;
	private int successCounter;
	private int partialSuccessCounter;
	private int failureCounter;
	private int lastKnownGoodCounter;

	private PolicyCacheDetail(){
		this.date = new Date();

	}

	public PolicyStatus getStatus() {
		return status;
	}
	public String getRemark() {
		return remark;
	}
	public Date getDate(){
		return date;
	}
	public List<PolicyDetail> getFailurePolicyList() {
		return failurePolicyList;
	}

	public int getSuccessCounter() {
		return successCounter;
	}

	public int getPartialSuccessCounter() {
		return partialSuccessCounter;
	}

	public int getFailureCounter() {
		return failureCounter;
	}

	public int getLastKnownGoodCounter() { return lastKnownGoodCounter; }

	public List<PolicyDetail> getPartialSuccessPolicyList() {
		return partialSuccessPolicyList;
	}

	public List<PolicyDetail> getSuccessPolicyList() {
		return successPolicyList;
	}

	public List<PolicyDetail> getLastKnownGoodPolicyList() { return lastKnownGoodPolicyList; }

	public static class PolicyCacheDetailBuilder{
		private PolicyCacheDetail policyCacheDetail;

		public PolicyCacheDetailBuilder(){
			policyCacheDetail=new PolicyCacheDetail();
		}
		//Policy Counter Check
		public PolicyCacheDetail build(){


			if (policyCacheDetail.remark != null) {
				policyCacheDetail.status = PolicyStatus.FAILURE;
				return policyCacheDetail;
			}


			/*
			 * First checked partial success counters greater than zero, policy status will be PARTIAL_SUCCESS
			 *
			 * If failure counter is zero means policy status is SUCCESS
			 * 	 because partial success counter is also zero (success counter may be zero or more)
			 *
			 * if success counter is greater than zero means policy status is PARTIAL_SUCCESS
			 * 		because failure counter is also greater than zero and partial success counter is zero
			 *
			 * else
			 * 		policy status FAILURE
			 * 			because success/partial success counter is zero and failure counter is greater than zero
			 *
			 */
			if (policyCacheDetail.partialSuccessCounter > 0) {
				policyCacheDetail.status = PolicyStatus.PARTIAL_SUCCESS;
			} else if (policyCacheDetail.failureCounter == 0) {
				policyCacheDetail.status = PolicyStatus.SUCCESS;
			} else if (policyCacheDetail.successCounter > 0) {
				policyCacheDetail.status = PolicyStatus.PARTIAL_SUCCESS;
			} else if (policyCacheDetail.lastKnownGoodCounter > 0) {
				policyCacheDetail.status = PolicyStatus.LAST_KNOWN_GOOD;
			} else {
				if (policyCacheDetail.remark == null) {
					policyCacheDetail.remark = "All policies are failed due to wrong configuration";
				}
				policyCacheDetail.status = PolicyStatus.FAILURE;
			}

			return policyCacheDetail;
		}

		public PolicyCacheDetailBuilder withRemark(String remark){
			policyCacheDetail.remark=remark;
			return this;
		}
		public PolicyCacheDetailBuilder withFailurePolicyList(List<PolicyDetail> failurePolicyList){
			policyCacheDetail.failurePolicyList=failurePolicyList;
			return this;
		}
		public PolicyCacheDetailBuilder withPartialSuccessPolicyList(List<PolicyDetail> partialSuccessPolicyList){
			policyCacheDetail.partialSuccessPolicyList=partialSuccessPolicyList;
			return this;
		}
		public PolicyCacheDetailBuilder withSuccessPolicyList(List<PolicyDetail> successPolicyList){
			policyCacheDetail.successPolicyList=successPolicyList;
			return this;
		}
		public PolicyCacheDetailBuilder withLastKnownGoodPolicyList(List<PolicyDetail> lastKnownGoodPolicyList){
			policyCacheDetail.lastKnownGoodPolicyList=lastKnownGoodPolicyList;
			return this;
		}
		public PolicyCacheDetailBuilder withSuccessCounter(int successCounter){
			policyCacheDetail.successCounter=successCounter;
			return this;
		}
		public PolicyCacheDetailBuilder withPartialSuccessCounter(int partialSuccessCounter){
			policyCacheDetail.partialSuccessCounter=partialSuccessCounter;
			return this;
		}
		public PolicyCacheDetailBuilder withFailureCounter(int failureCounter){
			policyCacheDetail.failureCounter=failureCounter;
			return this;
		}
		public PolicyCacheDetailBuilder withLastKnownGoodCounter(int lastKnownGoodCounter){
			policyCacheDetail.lastKnownGoodCounter=lastKnownGoodCounter;
			return this;
		}

	}

	public static PolicyCacheDetail merge(PolicyCacheDetail... policyCacheDetails){

		int failureCounter = 0;
		int successCounter =0;
		int partialSuccessCounter=0;
		int lastKnownGoodCounter=0;
		List<PolicyDetail> successPolicyList = Collectionz.newArrayList();
		List<PolicyDetail> partialSuccessPolicyList = Collectionz.newArrayList();
		List<PolicyDetail> failurePolicyList  = Collectionz.newArrayList();
		List<PolicyDetail> lastKnownGoodPolicyList  = Collectionz.newArrayList();

		for (PolicyCacheDetail sourcePolicyCacheDetail : policyCacheDetails) {
			if(sourcePolicyCacheDetail.getFailureCounter() > 0 ){
				failureCounter += sourcePolicyCacheDetail.getFailureCounter();
				failurePolicyList.addAll(sourcePolicyCacheDetail.getFailurePolicyList());
			}
			if(sourcePolicyCacheDetail.getPartialSuccessCounter() > 0){
				partialSuccessCounter += sourcePolicyCacheDetail.getPartialSuccessCounter();
				partialSuccessPolicyList.addAll(sourcePolicyCacheDetail.getPartialSuccessPolicyList());
			}
			if(sourcePolicyCacheDetail.getSuccessCounter() > 0) {
				successPolicyList.addAll(sourcePolicyCacheDetail.getSuccessPolicyList());
				successCounter += sourcePolicyCacheDetail.getSuccessCounter();
			}
			if(sourcePolicyCacheDetail.getLastKnownGoodCounter() > 0) {
				lastKnownGoodPolicyList.addAll(sourcePolicyCacheDetail.getLastKnownGoodPolicyList());
				lastKnownGoodCounter += sourcePolicyCacheDetail.getLastKnownGoodCounter();
			}
		}
		return new PolicyCacheDetail.PolicyCacheDetailBuilder().withFailureCounter(failureCounter).withFailurePolicyList(failurePolicyList)
				.withPartialSuccessCounter(partialSuccessCounter).withPartialSuccessPolicyList(partialSuccessPolicyList)
				.withSuccessCounter(successCounter).withSuccessPolicyList(successPolicyList)
				.withLastKnownGoodCounter(lastKnownGoodCounter).withLastKnownGoodPolicyList(lastKnownGoodPolicyList).build();
	}

}
