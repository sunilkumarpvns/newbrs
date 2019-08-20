package com.elitecore.netvertex.pm;

import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyValueConstants;
import com.elitecore.corenetvertex.constants.QuotaProfileType;
import com.elitecore.corenetvertex.pm.constants.SelectionResult;
import com.elitecore.corenetvertex.pm.pkg.datapackage.UserPackage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfileDetail;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.corenetvertex.spr.data.Subscription;
import com.elitecore.netvertex.pm.quota.RnCQuotaProfileDetail;

import java.util.List;
import java.util.Objects;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class PCRFQoSProcessor implements QoSProcessor {

    private static final String MODULE = "PCRF-QOS-PROCESSOR";

    private QoSInformation qosInformation;

    public PCRFQoSProcessor(QoSInformation qosInformation) {
        this.qosInformation = qosInformation;
    }

    @Override
    public boolean process(QoSProfile qoSProfile, BasePolicyContext policyContext, UserPackage userPackage, Subscription subscription) {

        if(Objects.nonNull(subscription)) {
            qosInformation.startAddOnQoSSelection(userPackage, subscription.getId());
        } else {
            qosInformation.startPackageQoSSelection(userPackage);
        }

        SelectionResult result = processPCCProfile(qoSProfile, policyContext);
        qosInformation.endQoSSelectionProcess();
        return result != SelectionResult.NOT_APPLIED;

    }

    private SelectionResult processPCCProfile(QoSProfile qoSProfile, PolicyContext policyContext) {
        qosInformation.startProcessingQoSProfile(qoSProfile);

        if (Objects.nonNull(qoSProfile.getDataRateCard())) {
            if (isLevelApplicable(qoSProfile, policyContext)) {
                return SelectionResult.NOT_APPLIED;
            }
        } else if(Objects.nonNull(qoSProfile.getQuotaProfile()) && Objects.equals(QuotaProfileType.RnC_BASED, qoSProfile.getQuotaProfile().getType()) &&
                isLevelApplicable(policyContext, qoSProfile.getHSQLevelQoSDetail().getAllServiceQuotaProfileDetail())==false){
            return SelectionResult.NOT_APPLIED;
        }

        SelectionResult result = qoSProfile.getHSQLevelQoSDetail().apply(policyContext, qosInformation, SelectionResult.NOT_APPLIED);

        if(result != SelectionResult.FULLY_APPLIED){
            List<com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.QoSProfileDetail> fupLevelQoSDetails = qoSProfile.getFupLevelQoSDetails();
            if(fupLevelQoSDetails  != null){
                for(int index = 0; index < fupLevelQoSDetails.size(); index++) {
                    SelectionResult previousResult = result;
                    if(Objects.equals(QuotaProfileType.RnC_BASED, qoSProfile.getQuotaProfile().getType()) &&
                        isLevelApplicable(policyContext, fupLevelQoSDetails.get(index).getAllServiceQuotaProfileDetail())==false){
                            return result;
                    }

                    result = ((QoSProfileDetail)fupLevelQoSDetails.get(index)).apply(policyContext, qosInformation, result);
                    if(result == SelectionResult.FULLY_APPLIED){
                        break;
                    }

                    result = previousResult.and(result);
                }
            }
        }

        qosInformation.endProcessingPCCProfile();
        return result;
    }

    private boolean isLevelApplicable(QoSProfile qoSProfile, PolicyContext policyContext) {
        if (Objects.equals(PCRFKeyValueConstants.SUBSCRIBER_INTERNATIONAL_ROAMING_TRUE.val, policyContext.getPCRFResponse().getAttribute(PCRFKeyConstants.SUB_INTERNATIONAL_ROAMING.val))
                && policyContext.getPCRFRequest().getSPRInfo().getPaygInternationalDataRoaming() == false) {
            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "QOS Profile " + qoSProfile.getName() + " processing skipped. Reason: Subscriber is roaming internationally and has opted for not applying PAYG International Data Roaming");
            }
            return true;
        }
        return false;
    }

    private boolean isLevelApplicable(PolicyContext policyContext, QuotaProfileDetail quotaProfileDetail){

        if(Objects.isNull(quotaProfileDetail)){
            return true;
        }

        SPRInfo sprInfo = policyContext.getPCRFRequest().getSPRInfo();
        if (((RnCQuotaProfileDetail)quotaProfileDetail).isRateConfigured()
                && Objects.equals(PCRFKeyValueConstants.SUBSCRIBER_INTERNATIONAL_ROAMING_TRUE.val, policyContext.getPCRFResponse().getAttribute(PCRFKeyConstants.SUB_INTERNATIONAL_ROAMING.val))
                && sprInfo.getPaygInternationalDataRoaming() == false) {
            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Quota Profile " + quotaProfileDetail.getName() + " for " + quotaProfileDetail.getFupLevel() + " level processing skipped. Reason: Subscriber is roaming internationally and has opted for not applying PAYG International Data Roaming");
            }
            return false;
        }

        return true;
    }
}
