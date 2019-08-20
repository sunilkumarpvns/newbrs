package com.elitecore.netvertex.pm;

import com.elitecore.corenetvertex.constants.BalanceLevel;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyValueConstants;
import com.elitecore.corenetvertex.constants.QuotaProfileType;
import com.elitecore.corenetvertex.constants.RenewalIntervalUnit;
import com.elitecore.corenetvertex.pm.constants.SelectionResult;
import com.elitecore.corenetvertex.pm.pkg.datapackage.UserPackage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfileDetail;
import com.elitecore.corenetvertex.spr.NonMonetoryBalance;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.corenetvertex.spr.data.Subscription;
import com.elitecore.netvertex.gateway.diameter.gy.MSCC;
import com.elitecore.netvertex.pm.quota.QuotaReservation;
import com.elitecore.netvertex.pm.quota.RnCQuotaProfileDetail;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class QuotaProfile extends com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfile {

    private static final String MODULE = "QUOTA-PROFILE";
    public QuotaProfile(String name, String pkgName, String id, BalanceLevel balanceLevel, int renewalInterval, RenewalIntervalUnit renewalIntervalUnit, QuotaProfileType quotaProfileType, List<Map<String, QuotaProfileDetail>> fupLevelserviceWiseQuotaProfileDetais,Boolean proration, Boolean carryForward) {
        super(name, pkgName, id, balanceLevel, renewalInterval, renewalIntervalUnit, quotaProfileType, fupLevelserviceWiseQuotaProfileDetais,proration,carryForward);
    }

    public boolean apply(PolicyContext policyContext, UserPackage userPackage, Subscription subscription, QuotaReservation quotaReservation) {

        if(getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Quota Profile " + getName() + " processing started");
        }

        Map<String, QuotaProfileDetail> serviceWiseHSQQuotaProfileDetail = getHsqLevelServiceWiseQuotaProfileDetails();

        if(Objects.equals(QuotaProfileType.RnC_BASED, getType()) && isLevelApplicable(policyContext, serviceWiseHSQQuotaProfileDetail) == false){
            return false;
        }

        SelectionResult result = applyLevel(policyContext, userPackage.getId(), subscription, quotaReservation, serviceWiseHSQQuotaProfileDetail);

        if(result != SelectionResult.FULLY_APPLIED){
            List<Map<String, QuotaProfileDetail>> allLevelServiceWiseQuotaProfileDetails = getAllLevelServiceWiseQuotaProfileDetails();

            if (allLevelServiceWiseQuotaProfileDetails != null) {

                for(int i = 1; i < allLevelServiceWiseQuotaProfileDetails.size(); i++) {

                    SelectionResult previousResult = result;
                    if(Objects.equals(QuotaProfileType.RnC_BASED, getType()) && isLevelApplicable(policyContext, allLevelServiceWiseQuotaProfileDetails.get(i)) == false){
                        return result != SelectionResult.NOT_APPLIED;
                    }

                    result = applyLevel(policyContext, userPackage.getId(), subscription, quotaReservation, allLevelServiceWiseQuotaProfileDetails.get(i));
                    if(result == SelectionResult.FULLY_APPLIED){
                        break;
                    }

                    result = previousResult.and(result);
                }
            }
        }

        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Quota Profile " + getName() + " processing completed");
        }

        return result != SelectionResult.NOT_APPLIED;
    }

    private boolean isLevelApplicable(PolicyContext policyContext, Map<String, QuotaProfileDetail> serviceWiseQuotaProfileDetails){
        RnCQuotaProfileDetail quotaProfileDetail = (RnCQuotaProfileDetail)getAllServiceQuotaProfileDetails(serviceWiseQuotaProfileDetails);

        if(quotaProfileDetail==null){
            return true;
        }

        SPRInfo sprInfo = policyContext.getPCRFRequest().getSPRInfo();
        if (quotaProfileDetail.isRateConfigured()
                && PCRFKeyValueConstants.SUBSCRIBER_INTERNATIONAL_ROAMING_TRUE.val.equals(policyContext.getPCRFResponse().getAttribute(PCRFKeyConstants.SUB_INTERNATIONAL_ROAMING.val))
                && sprInfo.getPaygInternationalDataRoaming() == false) {
            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Quota Profile " + getName() + " for "+getBalanceLevel().getDisplayVal()+" processing skipped. Reason: Subscriber is roaming internationally and has opted for not applying PAYG International Data Roaming");
            }
            return false;
        }

        return true;
    }

    private QuotaProfileDetail getAllServiceQuotaProfileDetails(Map<String, QuotaProfileDetail> serviceWiseQuotaProfileDetails) {
        for (Map.Entry<String, QuotaProfileDetail> serviceIdToQuotaProfileDetailEntry : serviceWiseQuotaProfileDetails.entrySet()) {
            QuotaProfileDetail quotaProfileDetail = serviceIdToQuotaProfileDetailEntry.getValue();
            if (quotaProfileDetail.getServiceId().equals(CommonConstants.ALL_SERVICE_ID)) {
                return quotaProfileDetail;
            }
        }

        return null;
    }

    private SelectionResult applyLevel(PolicyContext policyContext, String packageId,
                                       Subscription subscription, QuotaReservation quotaReservation, Map<String, QuotaProfileDetail> hsqLevelServiceWiseQuotaProfileDetails) {


        SelectionResult qoSSelectionResult = SelectionResult.NOT_APPLIED;

        for (Map.Entry<String, QuotaProfileDetail> serviceIdToQuotaProfileDetailEntry : hsqLevelServiceWiseQuotaProfileDetails.entrySet()) {
            RnCQuotaProfileDetail quotaProfileDetail = (RnCQuotaProfileDetail) serviceIdToQuotaProfileDetailEntry.getValue();


            MSCC existingMSCC = quotaReservation.get(quotaProfileDetail.getRatingGroup().getIdentifier());

            boolean result;
            if (hasSamePackage(packageId, existingMSCC)) {
                if (hasSameSubscription(subscription, existingMSCC) || hasHigherFUPLevel(existingMSCC, policyContext.getPCRFResponse(), quotaProfileDetail.getFupLevel())) {
                    result = true;
                } else {
                    result = quotaProfileDetail.apply(policyContext, packageId, subscription, quotaReservation);
                }
            } else {

                boolean evaluate = true;

                if(Objects.nonNull(existingMSCC)) {
                    if(Objects.nonNull(subscription)) {
                        if(isExistingMSCCIsFromAddOnSubscription(existingMSCC, policyContext.getPCRFResponse()) && hasHigherFUPLevel(existingMSCC, policyContext.getPCRFResponse(), quotaProfileDetail.getFupLevel())) {
                            evaluate = false;
                        }
                    } else if(policyContext.getBasePackage().getId().equals(packageId) == false) {

                        if(isExistingMSCCIsFromBasePackage(existingMSCC, policyContext) == false && hasHigherFUPLevel(existingMSCC, policyContext.getPCRFResponse(), quotaProfileDetail.getFupLevel())) {
                            evaluate = false;
                        }
                    }
                }

                if(evaluate) {
                    result = quotaProfileDetail.apply(policyContext, packageId, subscription, quotaReservation);
                } else {
                    result = true;
                }
            }


            if (result == false) {
                qoSSelectionResult = qoSSelectionResult == SelectionResult.NOT_APPLIED ? SelectionResult.NOT_APPLIED : SelectionResult.PARTIALLY_APPLIED;
            } else {
                qoSSelectionResult = qoSSelectionResult == SelectionResult.PARTIALLY_APPLIED ? SelectionResult.PARTIALLY_APPLIED : SelectionResult.FULLY_APPLIED;
            }
        }
        return qoSSelectionResult;
    }

    private boolean isExistingMSCCIsFromBasePackage(MSCC existingMSCC, PolicyContext response) {
        NonMonetoryBalance balance = response.getPCRFResponse().getCurrentNonMonetoryBalance().getBalanceById(existingMSCC.getGrantedServiceUnits().getBalanceId());
        return Objects.equals(balance.getPackageId(), response.getBasePackage().getId());
    }

    private boolean isExistingMSCCIsFromAddOnSubscription(MSCC existingMSCC, PCRFResponse response) {
        NonMonetoryBalance balance = response.getCurrentNonMonetoryBalance().getBalanceById(existingMSCC.getGrantedServiceUnits().getBalanceId());
        return Objects.nonNull(balance.getSubscriptionId());
    }

    private boolean hasHigherFUPLevel(MSCC existingMSCC, PCRFResponse response, int level) {

        NonMonetoryBalance balance = response.getCurrentNonMonetoryBalance().getBalanceById(existingMSCC.getGrantedServiceUnits().getBalanceId());

        return balance.getLevel() < level;

    }

    private boolean hasSameSubscription(Subscription subscription, MSCC existingMSCC) {
        return Objects.isNull(subscription) || Objects.equals(existingMSCC.getGrantedServiceUnits().getSubscriptionId(), subscription.getId());
    }

    private boolean hasSamePackage(String packageId, MSCC existingMSCC) {
        return Objects.nonNull(existingMSCC) && Objects.equals(existingMSCC.getGrantedServiceUnits().getPackageId(), packageId);
    }

    public boolean applyWithRG(PolicyContext policyContext, String packageId, Subscription subscription, QuotaReservation quotaReservation, int level, long ratingGroup) {



        Map<String, QuotaProfileDetail> serviceWiseQuotaProfileDetails = getServiceWiseQuotaProfileDetails(level);

        if (serviceWiseQuotaProfileDetails == null) {
            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "FUP quota detail not found for level: " + level);
            }
            return false;
        }

        do {

            for (Map.Entry<String, QuotaProfileDetail> serviceIdToQuotaProfileDetailEntry : serviceWiseQuotaProfileDetails.entrySet()) {

                if (((com.elitecore.netvertex.pm.QuotaProfileDetail) serviceIdToQuotaProfileDetailEntry.getValue())
                        .applyRG(policyContext, packageId, subscription, quotaReservation, ratingGroup)) {
                    return true;
                }
            }

            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "FUP quota detail not found with RG: " + ratingGroup + " in level: " + level);
            }
            serviceWiseQuotaProfileDetails = getServiceWiseQuotaProfileDetails(++level);

        }while(Objects.nonNull(serviceWiseQuotaProfileDetails));

        return false;

    }
}
