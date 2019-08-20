package com.elitecore.netvertex.service.pcrf.servicepolicy.handler;

import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyValueConstants;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.pkg.PkgType;
import com.elitecore.corenetvertex.pm.offer.ProductOffer;
import com.elitecore.corenetvertex.pm.pkg.datapackage.SubscriptionPackage;
import com.elitecore.corenetvertex.spr.data.Subscription;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.netvertex.core.NetVertexServerContext;
import com.elitecore.netvertex.pm.BasePackage;
import com.elitecore.netvertex.pm.PolicyContext;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.PCRFServiceContext;
import org.apache.commons.collections.MapUtils;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.TreeSet;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class SubscriberPolicySelectionEngine {

    private static final String MODULE = "SUB-PLC-SELECTION-ENGINE";

    private BasePolicyHandler basePolicyHandler;
    private AddOnPolicyHandler exclusiveAddOnPolicyHandler;
    private NonExclusiveAddOnPolicyHandler nonExclusiveAddOnPolicyHandler;
    private PromotionalPolicyHandler promotionalPolicyHandler;
    private PCRFServiceContext serviceContext;

    public SubscriberPolicySelectionEngine(PCRFServiceContext serviceContext) {
        this.serviceContext = serviceContext;
        basePolicyHandler = new BasePolicyHandler();
        exclusiveAddOnPolicyHandler = new AddOnPolicyHandler(getServerContext());
        nonExclusiveAddOnPolicyHandler = new NonExclusiveAddOnPolicyHandler(getServerContext());
        promotionalPolicyHandler = new PromotionalPolicyHandler(getServerContext());
    }

    /*
     * Sequence Of Execution
     *
     * Base Package (Replaceable by Add-On == false) | | V Exclusive Add-On | |
     * V Non-Exclusive Add-On | | V Base Package (Replaceable by Add-On == true
     * && no addOn Applied)
     */

    public void applyPackage(PolicyContext policyContext) {

        PCRFRequest pcrfRequest = policyContext.getPCRFRequest();
        PCRFResponse response = policyContext.getPCRFResponse();


        BasePackage basePackage = getBasePackage(pcrfRequest);
        if (basePackage == null) {
            response.setAttribute(PCRFKeyConstants.RESULT_CODE.getVal(), PCRFKeyValueConstants.RESULT_CODE_AUTHORIZATION_REJECTED.val);
            response.setFurtherProcessingRequired(false);
            return;
        }

        policyContext.setBasePackage(basePackage);
        response.setAttribute(PCRFKeyConstants.PACKAGE_NAME.getVal(), basePackage.getName());
        if(basePackage.getParam1() != null) {
            response.setAttribute(PCRFKeyConstants.SUB_DATA_PACKAGE_PARAM1.getVal(), basePackage.getParam1());
        }
        if(basePackage.getParam2() != null) {
            response.setAttribute(PCRFKeyConstants.SUB_DATA_PACKAGE_PARAM2.getVal(), basePackage.getParam2());
        }

        String subscriberId = policyContext.getPCRFResponse().getAttribute(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val);

        Collection<Subscription> exclusiveAddOnSubscriptions = null;
        Collection<Subscription> nonExclusiveAddOnSubscriptions = null;

        try {
            LinkedHashMap<String, Subscription> subscriptions = policyContext.getSubscriptions();
            if (MapUtils.isNotEmpty(subscriptions)) {
                exclusiveAddOnSubscriptions = new TreeSet<>(AddOnSubscriptionComparator.instance());
                nonExclusiveAddOnSubscriptions = new TreeSet<>(AddOnSubscriptionComparator.instance());
                setListsForDifferentSubscriptions(subscriptions, exclusiveAddOnSubscriptions, nonExclusiveAddOnSubscriptions, subscriberId);
            }
        } catch (OperationFailedException e) {
            getLogger().warn(MODULE, "Error while fetching addon subscription for subscriber id: " + policyContext.getSPInfo().getSubscriberIdentity() + ". Reason: " + e.getMessage());
            if (ResultCode.INTERNAL_ERROR == e.getErrorCode()) {
                getLogger().trace(MODULE, e);
            }
        }


        basePolicyHandler.applyPackage(policyContext);
        exclusiveAddOnPolicyHandler.applyPackage(policyContext, exclusiveAddOnSubscriptions);
        nonExclusiveAddOnPolicyHandler.applyPackage(policyContext, nonExclusiveAddOnSubscriptions);
        promotionalPolicyHandler.applyPackage(policyContext);
    }

    private void setListsForDifferentSubscriptions(LinkedHashMap<String, Subscription> addOnSubscriptions,
                                                   Collection<Subscription> exclusiveAddOnSubscriptions,
                                                   Collection<Subscription> nonExclusiveAddOnSubscriptions, String subscriberId) {



        for (Subscription subscription : addOnSubscriptions.values()) {

            SubscriptionPackage subscriptionPackage = getServerContext().getPolicyRepository().getAddOnById(subscription.getPackageId());

            if (subscriptionPackage == null) {
                //FIXME Suggestion required
                if (getLogger().isDebugLogLevel()) {
                    getLogger().debug(MODULE, "Skipping subscription( " + subscription.getId()
                            + ") for subscriber ID: " + subscriberId
                            + ". Reason: Package found with ID: " + subscription.getPackageId() + " is not AddOn");
                }

                continue;
            }

            if(Objects.equals(PkgType.ADDON, subscriptionPackage.getPackageType()) == false) {
                continue;
            }

            if (subscriptionPackage.getStatus() == PolicyStatus.FAILURE) {
                getLogger().warn(MODULE, "Skip subscription(id: " + subscription.getId() + ") for subscriber ID: " + subscriberId
                        + ". Reason: Subscription package(name:"+subscriptionPackage.getName()+") has status FAILURE. Reason: " + subscriptionPackage.getFailReason());
                continue;
            }

            if (((com.elitecore.corenetvertex.pm.pkg.datapackage.AddOn)subscriptionPackage).isExclusive()) {
                exclusiveAddOnSubscriptions.add(subscription);
            } else {
                nonExclusiveAddOnSubscriptions.add(subscription);
            }
        }

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
        if(Objects.isNull(productOffer)){
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

    public NetVertexServerContext getServerContext() {
        return serviceContext.getServerContext();
    }
}
