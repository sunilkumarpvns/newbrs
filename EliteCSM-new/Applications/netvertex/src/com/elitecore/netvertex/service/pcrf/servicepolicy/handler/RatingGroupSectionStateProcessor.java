package com.elitecore.netvertex.service.pcrf.servicepolicy.handler;

import java.util.LinkedHashMap;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.QuotaProfileType;
import com.elitecore.corenetvertex.pm.pkg.PCCRule;
import com.elitecore.corenetvertex.pm.pkg.datapackage.Package;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc.RnCBaseQoSProfileDetail;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.RncProfileDetail;
import com.elitecore.corenetvertex.spr.data.Subscription;
import com.elitecore.netvertex.core.NetVertexServerContext;
import com.elitecore.netvertex.pm.FinalQoSSelectionData;
import com.elitecore.netvertex.pm.QoSProfile;
import com.elitecore.netvertex.pm.QoSProfileDetail;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import org.apache.commons.collections.MapUtils;

public class RatingGroupSectionStateProcessor {

    private NetVertexServerContext serverContext;

    public RatingGroupSectionStateProcessor(NetVertexServerContext serverContext) {

        this.serverContext = serverContext;
    }

    public void process(PCRFResponse response, FinalQoSSelectionData finalQoSInfomationData) {

        RatingGroupSelectionState pccProfileSelectionState = new RatingGroupSelectionState();
        addPackageSelectionState(response, finalQoSInfomationData.getSelectedSubscriptionIdOrPkgId(), finalQoSInfomationData.getQoSProfile(),
                finalQoSInfomationData.getQosProfileDetail(), pccProfileSelectionState);

        if(MapUtils.isNotEmpty(finalQoSInfomationData.getPccRules())) {
            for(PCCRule pccRule : finalQoSInfomationData.getPccRules().values()) {
                QoSProfile qoSProfile = finalQoSInfomationData.getQoSProfile(pccRule);
                String subscriptionIdOrPackageId = finalQoSInfomationData.getPccRuleIdToSubscriptionOrPackageId().get(pccRule.getId());
                addPackageSelectionState(response, subscriptionIdOrPackageId, qoSProfile, pccRule.getFupLevel(), pccRule.getChargingKey(), pccRule.getServiceIdentifier(), pccProfileSelectionState);
            }
        }


        response.setPccProfileSelectionState(pccProfileSelectionState);
    }

    private void addPackageSelectionState(PCRFResponse response,
                                          String subscriptionIdOrPackageId,
                                          QoSProfile qoSProfile,
                                          int fupLevel,
                                          long ratingGroup,
                                          long serviceIdentifier,
                                          RatingGroupSelectionState pccProfileSelectionState) {
        LinkedHashMap<String, Subscription> subscriptions = response.getSubscriptions();

        Subscription subscription = null;
        if(MapUtils.isNotEmpty(subscriptions)) {
            subscription = subscriptions.get(subscriptionIdOrPackageId);
        }

        String subscriptionId = null;
        String packageId;
        Package selectedPackage;

        if(subscription == null) {
            packageId = subscriptionIdOrPackageId;
            selectedPackage = serverContext.getPolicyRepository().getBasePackageDataById(packageId);

            if(selectedPackage == null) {
                selectedPackage = serverContext.getPolicyRepository().getPromotionalPackageById(packageId);

                if(selectedPackage == null) {
                    selectedPackage = serverContext.getPolicyRepository().getEmergencyPackagebyId(packageId);
                }
            }


        } else {
            subscriptionId = subscription.getId();
            packageId = subscription.getPackageId();
            selectedPackage = serverContext.getPolicyRepository().getAddOnById(packageId);
        }



        if(selectedPackage != null) {
            pccProfileSelectionState.add(subscriptionId, selectedPackage, qoSProfile, fupLevel, ratingGroup, serviceIdentifier);
        }
    }

    private void addPackageSelectionState(PCRFResponse response,
                                          String subscriptionIdOrPackageId,
                                          QoSProfile qoSProfile,
                                          QoSProfileDetail selectedQoSProfileDetail,
                                          RatingGroupSelectionState pccProfileSelectionState) {
        LinkedHashMap<String, Subscription> subscriptions = response.getSubscriptions();

        Subscription subscription = null;
        if(MapUtils.isNotEmpty(subscriptions)) {
            subscription = subscriptions.get(subscriptionIdOrPackageId);
        }

        String subscriptionId = null;
        String packageId;
        Package selectedPackage;

        if(subscription == null) {
            packageId = subscriptionIdOrPackageId;
            selectedPackage = serverContext.getPolicyRepository().getBasePackageDataById(packageId);

            if(selectedPackage == null) {
                selectedPackage = serverContext.getPolicyRepository().getPromotionalPackageById(packageId);

                if(selectedPackage == null) {
                    selectedPackage = serverContext.getPolicyRepository().getEmergencyPackagebyId(packageId);
                }
            }


        } else {
            subscriptionId = subscription.getId();
            packageId = subscription.getPackageId();
            selectedPackage = serverContext.getPolicyRepository().getAddOnById(packageId);
        }

        if(selectedPackage.getQuotaProfileType() != QuotaProfileType.RnC_BASED) {
            return;
        }

        long ratingGroup;
        long serviceIdentifier;
        if (qoSProfile.getDataRateCard() != null) {
            ratingGroup = CommonConstants.DEFAULT_RATING_GROUP_IDENTIFIER;
            serviceIdentifier = CommonConstants.ALL_SERVICE_IDENTIFIER;
        } else {
            RnCBaseQoSProfileDetail allServiceQuotaProfileDetail = (RnCBaseQoSProfileDetail) selectedQoSProfileDetail;
            RncProfileDetail serviceQuotaProfileDetail = allServiceQuotaProfileDetail.getAllServiceQuotaProfileDetail();
            ratingGroup = serviceQuotaProfileDetail.getRatingGroup().getIdentifier();
            serviceIdentifier = serviceQuotaProfileDetail.getDataServiceType().getServiceIdentifier();
        }

        if(selectedPackage != null) {
            pccProfileSelectionState.add(subscriptionId, selectedPackage, qoSProfile, selectedQoSProfileDetail.getFUPLevel(), ratingGroup, serviceIdentifier);
        }

    }

}
