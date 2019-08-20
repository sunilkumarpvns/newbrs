package com.elitecore.netvertex.service.pcrf.servicepolicy.handler;

import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyValueConstants;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.pm.offer.ProductOffer;
import com.elitecore.corenetvertex.pm.pkg.datapackage.SubscriptionPackage;
import com.elitecore.corenetvertex.spr.data.Subscription;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.netvertex.pm.BasePackage;
import com.elitecore.netvertex.pm.FinalQoSSelectionData;
import com.elitecore.netvertex.pm.PolicyContext;
import com.elitecore.netvertex.pm.QoSInformation;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.PCRFServiceContext;

import java.util.Objects;

/**
 * @author Jay Trivedi
 */
public class SubscriberPolicyHandler extends DataPolicyHandler {

    private static final String MODULE = "SUB-PLC-HDLR";
    private final SubscriberPolicySelectionEngine subscriberPolicySelectionEngine;
    private final BoDPackageHandler boDPackageHandler;

    public SubscriberPolicyHandler(PCRFServiceContext serviceContext, SubscriberPolicySelectionEngine subscriberPolicySelectionEngine, BoDPackageHandler boDPackageHandler) {

        super(serviceContext);
        this.subscriberPolicySelectionEngine = subscriberPolicySelectionEngine;
        this.boDPackageHandler = boDPackageHandler;
    }

	/*
     * Sequence Of Execution
	 * 
	 * Base Package (Replaceable by Add-On == false) | | V Exclusive Add-On | |
	 * V Non-Exclusive Add-On | | V Base Package (Replaceable by Add-On == true
	 * && no addOn Applied)
	 */

    @Override
    public FinalQoSSelectionData applyPackage(PolicyContext policyContext, QoSInformation qosInformation) {

        PCRFRequest pcrfRequest = policyContext.getPCRFRequest();
        PCRFResponse response = policyContext.getPCRFResponse();

        String subscriberIdentity = pcrfRequest.getAttribute(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val);

        BasePackage basePackage = getBasePackage(pcrfRequest);
        if (basePackage == null) {
            response.setAttribute(PCRFKeyConstants.RESULT_CODE.getVal(), PCRFKeyValueConstants.RESULT_CODE_AUTHORIZATION_REJECTED.val);
            response.setFurtherProcessingRequired(false);
            return null;
        }

        subscriberPolicySelectionEngine.applyPackage(policyContext);

        FinalQoSSelectionData finalQoSSelectionData = qosInformation.endProcess();

        boDPackageHandler.applyBoDPackage(policyContext, finalQoSSelectionData);

        postProcess(policyContext, pcrfRequest, response, subscriberIdentity, basePackage, finalQoSSelectionData);

        return finalQoSSelectionData;
    }

    private void postProcess(PolicyContext policyContext, PCRFRequest pcrfRequest, PCRFResponse response,
                             String subscriberIdentity, BasePackage basePackage, FinalQoSSelectionData finalQoSInfomationData) {
        if (finalQoSInfomationData == null) {
            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Skipping further processing for subscriber ID: " + subscriberIdentity + ". Reason: No QoS satisfied");
            }
            response.setAttribute(PCRFKeyConstants.RESULT_CODE.val, PCRFKeyValueConstants.RESULT_CODE_AUTHORIZATION_REJECTED.val);
            response.setFurtherProcessingRequired(false);
            response.setAttribute(PCRFKeyConstants.EXPIRED_QOS_PROFILE.val, pcrfRequest.getAttribute(PCRFKeyConstants.QOS_PROFILE_NAME.val));
            response.setPolicyChanged(true);

        } else {
            setSelectedPackageParameter(policyContext, response, subscriberIdentity, basePackage, finalQoSInfomationData);
        }
    }

    private void setSelectedPackageParameter(PolicyContext policyContext, PCRFResponse response,
                                             String subscriberIdentity, BasePackage basePackage, FinalQoSSelectionData finalQoSInfomationData) {
        if (basePackage.getId().equals(finalQoSInfomationData.getSelectedSubscriptionIdOrPkgId())) {
            response.setAttribute(PCRFKeyConstants.SUB_DATA_PACKAGE_PARAM1.val, basePackage.getParam1());
            response.setAttribute(PCRFKeyConstants.SUB_DATA_PACKAGE_PARAM2.val, basePackage.getParam2());
        } else {

            try {
                if (policyContext.getSubscriptions() != null) {

                    Subscription subscription = policyContext.getSubscriptions().get(finalQoSInfomationData.getSelectedSubscriptionIdOrPkgId());
                    if (subscription != null) {
                        SubscriptionPackage subscriptionPackage = getServerContext()
                                .getPolicyRepository().getAddOnById(subscription.getPackageId());
                        if (subscriptionPackage != null) {
                            response.setAttribute(PCRFKeyConstants.SUB_DATA_PACKAGE_PARAM1.val, subscriptionPackage.getParam1());
                            response.setAttribute(PCRFKeyConstants.SUB_DATA_PACKAGE_PARAM2.val, subscriptionPackage.getParam2());
                        } else {
                            getLogger().warn(MODULE, "Unable to set param1 and param2 for subscriber: " + subscriberIdentity + " and core session Id: "
                                    + response.getAttribute(PCRFKeyConstants.CS_CORESESSION_ID.val) + ". Reason: Package not found for subscription Id: "
                                    + finalQoSInfomationData.getSelectedSubscriptionIdOrPkgId() + ", package id:" + subscription.getPackageId());
                        }
                    }
                }
            } catch (OperationFailedException e) {
                getLogger().error(MODULE, "Unable to set param1 and param2 for subscriber: " + subscriberIdentity + " and core session Id: "
                        + response.getAttribute(PCRFKeyConstants.CS_CORESESSION_ID.val) + ". Reason: " + e.getMessage());
                if (ResultCode.INTERNAL_ERROR == e.getErrorCode()) {
                    getLogger().trace(MODULE, e);
                }
            }

        }
        response.setAttribute(PCRFKeyConstants.IPCAN_REDIRECT_URL.val, finalQoSInfomationData.getQosProfileDetail().getRedirectURL());
    }

    private BasePackage getBasePackage(PCRFRequest pcrfRequest) {
        String subscriberPackage = pcrfRequest.getSPRInfo().getProductOffer();
        String subscriberId = pcrfRequest.getAttribute(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val);

        if (subscriberPackage == null) {
            if (getLogger().isErrorLogLevel())
                getLogger().error(MODULE, "Rejecting request for subscriber ID: " + subscriberId + ". Reason: package information not found in subscriber profile");
            return null;
        }

        ProductOffer productOffer = getServerContext().getPolicyRepository().getProductOffer().byName(subscriberPackage);
        if (Objects.isNull(productOffer)) {
            if (getLogger().isErrorLogLevel())
                getLogger().error(MODULE, "Rejecting request for subscriber ID: " + subscriberId + ". Reason: product offer(" + subscriberPackage + ") not found in policy repository");
            return null;
        }

        BasePackage basePackage = (BasePackage) productOffer.getDataServicePkgData();

        if (basePackage == null) {
            if (getLogger().isErrorLogLevel())
                getLogger().error(MODULE, "Rejecting request for subscriber ID: " + subscriberId + ". Reason: subscriber package(" + subscriberPackage + ") not found in policy repository");
            return null;
        }

        if (basePackage.getStatus() == PolicyStatus.FAILURE) {
            if (getLogger().isErrorLogLevel())
                getLogger().error(MODULE, "Rejecting request for subscriber ID: " + subscriberId
                        + ". Reason: subscriber package(" + subscriberPackage + ") has status FAILURE. Reason: "
                        + basePackage.getFailReason());
            return null;
        }

        return basePackage;
    }

}
