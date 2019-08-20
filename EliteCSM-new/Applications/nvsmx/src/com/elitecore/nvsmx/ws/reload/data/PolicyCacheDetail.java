package com.elitecore.nvsmx.ws.reload.data;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Function;
import com.elitecore.corenetvertex.constants.PolicyStatus;

import javax.annotation.Nonnull;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@XmlRootElement
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

    private static final Function<com.elitecore.corenetvertex.data.PolicyDetail, PolicyDetail> POLICY_DETAIL_MAPPER = policyDetailOfCore -> PolicyDetail.from(policyDetailOfCore);

    public PolicyCacheDetail() {
        this.date = new Date();

    }

    public PolicyStatus getStatus() {
        return status;
    }

    public String getRemark() {
        return remark;
    }

    public Date getDate() {
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

    public List<PolicyDetail> getPartialSuccessPolicyList() {
        return partialSuccessPolicyList;
    }

    public List<PolicyDetail> getSuccessPolicyList() {
        return successPolicyList;
    }

    public void setStatus(PolicyStatus status) {
        this.status = status;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public void setFailurePolicyList(List<PolicyDetail> failurePolicyList) {
        this.failurePolicyList = failurePolicyList;
    }

    public void setPartialSuccessPolicyList(List<PolicyDetail> partialSuccessPolicyList) {
        this.partialSuccessPolicyList = partialSuccessPolicyList;
    }

    public void setSuccessPolicyList(List<PolicyDetail> successPolicyList) {
        this.successPolicyList = successPolicyList;
    }

    public void setSuccessCounter(int successCounter) {
        this.successCounter = successCounter;
    }

    public void setPartialSuccessCounter(int partialSuccessCounter) {
        this.partialSuccessCounter = partialSuccessCounter;
    }

    public void setFailureCounter(int failureCounter) {
        this.failureCounter = failureCounter;
    }

    public List<PolicyDetail> getLastKnownGoodPolicyList() {
        return lastKnownGoodPolicyList;
    }

    public void setLastKnownGoodPolicyList(List<PolicyDetail> lastKnownGoodPolicyList) {
        this.lastKnownGoodPolicyList = lastKnownGoodPolicyList;
    }

    public int getLastKnownGoodCounter() {
        return lastKnownGoodCounter;
    }

    public void setLastKnownGoodCounter(int lastKnownGoodCounter) {
        this.lastKnownGoodCounter = lastKnownGoodCounter;
    }

    public static PolicyCacheDetail from(@Nonnull com.elitecore.corenetvertex.data.PolicyCacheDetail policyCacheDetail) {

        PolicyCacheDetail policyCacheDetailForWS = new PolicyCacheDetail();
        policyCacheDetailForWS.setSuccessCounter(policyCacheDetail.getSuccessCounter());
        policyCacheDetailForWS.setPartialSuccessCounter(policyCacheDetail.getPartialSuccessCounter());
        policyCacheDetailForWS.setFailureCounter(policyCacheDetail.getFailureCounter());
        policyCacheDetailForWS.setLastKnownGoodCounter(policyCacheDetail.getLastKnownGoodCounter());
        policyCacheDetailForWS.setPartialSuccessPolicyList(Collectionz.map(policyCacheDetail.getPartialSuccessPolicyList(), POLICY_DETAIL_MAPPER));
        policyCacheDetailForWS.setSuccessPolicyList(Collectionz.map(policyCacheDetail.getSuccessPolicyList(), POLICY_DETAIL_MAPPER));
        policyCacheDetailForWS.setFailurePolicyList(Collectionz.map(policyCacheDetail.getFailurePolicyList(), POLICY_DETAIL_MAPPER));
        policyCacheDetailForWS.setLastKnownGoodPolicyList(Collectionz.map(policyCacheDetail.getLastKnownGoodPolicyList(), POLICY_DETAIL_MAPPER));
        policyCacheDetailForWS.setRemark(policyCacheDetail.getRemark());
        policyCacheDetailForWS.setStatus(policyCacheDetail.getStatus());
        return policyCacheDetailForWS;

    }





}
