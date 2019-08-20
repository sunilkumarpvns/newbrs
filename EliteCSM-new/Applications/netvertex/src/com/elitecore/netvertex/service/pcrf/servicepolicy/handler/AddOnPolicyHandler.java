package com.elitecore.netvertex.service.pcrf.servicepolicy.handler;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.pm.pkg.datapackage.SubscriptionPackage;
import com.elitecore.corenetvertex.spr.data.Subscription;
import com.elitecore.netvertex.core.NetVertexServerContext;
import com.elitecore.netvertex.pm.PackageProcessor;
import com.elitecore.netvertex.pm.PolicyContext;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;

import java.util.Collection;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class AddOnPolicyHandler {

    private static final String MODULE = "ADDON-PLC-HDLR";

    private NetVertexServerContext serverContext;

    public AddOnPolicyHandler(NetVertexServerContext serverContext) {
        this.serverContext = serverContext;
    }

    public void applyPackage(PolicyContext policyContext, Collection<Subscription> exclusiveAddOnSubscriptions) {

        PCRFResponse response = policyContext.getPCRFResponse();
        PCRFRequest request = policyContext.getPCRFRequest();
        String subscriberIdentity = request.getAttribute(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val);

        if (Collectionz.isNullOrEmpty(exclusiveAddOnSubscriptions)) {
            if (getLogger().isLogLevel(LogLevel.DEBUG))
                getLogger().debug(MODULE, "No addOn subscription found for subscriber ID: " + subscriberIdentity);
            return;
        }

        String subscriberId = response.getAttribute(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val);

        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Applying addon subscriptions for subscriber ID: " + subscriberId);
        }

        /*
         * Applying exclusive addOn, For All exclusive addOn subscription, we
         * apply only one whose have better QoS also set pccRule from that addOn
         * only
         */
        for (Subscription addonSubscription : exclusiveAddOnSubscriptions) {

            SubscriptionPackage addOn = serverContext.getPolicyRepository().getAddOnById(addonSubscription.getPackageId());

            if (getLogger().isLogLevel(LogLevel.DEBUG))
                getLogger().debug(MODULE, "Applying addOn package(" + addOn.getName() + ")");

            apply(policyContext, addonSubscription, addOn);
        }

    }

    private void apply(PolicyContext policyContext, Subscription subscription, SubscriptionPackage addOn) {
        if(getLogger().isInfoLogLevel())
            getLogger().info(MODULE, "Applying AddOn : "+ addOn.getName() + '-' + addOn.getName() + " Subscription ID:"
                    + CommonConstants.OPENING_PARENTHESES + subscription.getId()
                    + CommonConstants.CLOSING_PARENTHESES);

        if (subscription.getStartTime().getTime() > policyContext.getCurrentTime().getTimeInMillis()) {

            policyContext.setRevalidationTime(subscription.getStartTime());

            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Skipping future addon(name: " + addOn.getName()
                        + ") subscription(id: " + subscription.getId() + "), Considering start-time(" + subscription.getStartTime()
                        + ") for session revalidation");
            }
            return;
        }

        PackageProcessor.apply(policyContext, addOn, subscription);

        if(getLogger().isInfoLogLevel()) {
            getLogger().info(MODULE, "AddOn:" + addOn.getName() + '-' + addOn.getId() + " Subscription ID:"
                    + CommonConstants.OPENING_PARENTHESES + subscription.getId()
                    + CommonConstants.CLOSING_PARENTHESES + " completed");
        }
    }
}
