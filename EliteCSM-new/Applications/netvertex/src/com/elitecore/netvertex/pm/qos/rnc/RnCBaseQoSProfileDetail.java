
package com.elitecore.netvertex.pm.qos.rnc;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.TimeSource;
import com.elitecore.commons.io.IndentingPrintWriter;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.QoSProfileAction;
import com.elitecore.corenetvertex.pm.constants.SelectionResult;
import com.elitecore.corenetvertex.pm.constants.UsageMetering;
import com.elitecore.corenetvertex.pm.pkg.ChargingRuleBaseName;
import com.elitecore.corenetvertex.pm.pkg.PCCRule;
import com.elitecore.corenetvertex.pm.pkg.datapackage.QuotaTopUp;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.IPCANQoS;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.Balance;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfileDetail;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.TotalBalance;
import com.elitecore.corenetvertex.spr.NonMonetoryBalance;
import com.elitecore.corenetvertex.spr.SubscriberMonetaryBalance;
import com.elitecore.corenetvertex.spr.SubscriptionNonMonitoryBalance;
import com.elitecore.corenetvertex.spr.data.Subscription;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.netvertex.pm.PolicyContext;
import com.elitecore.netvertex.pm.QoSInformation;
import com.elitecore.netvertex.pm.QoSProfileDetail;
import com.elitecore.netvertex.pm.quota.RnCQuotaProfileDetail;
import org.apache.commons.collections.CollectionUtils;


import static com.elitecore.commons.logging.LogManager.getLogger;

public class RnCBaseQoSProfileDetail extends com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc.RnCBaseQoSProfileDetail implements QoSProfileDetail {

    private static final long serialVersionUID = 1L;
    private static final String MODULE = "RNC-QOS-DETAIL";
    private final QoSSelectionWithoutBalance qoSSelectionWithoutBalance;
    private final QoSSelectionWithBalance qoSSelectionWithBalance;
    private final QoSSelectionWithoutQuotaProfile qoSSelectionWithoutQuotaProfile;
    private final boolean applyOnUsageUnavailability;


    /*
    used when action is reject
     */
    public RnCBaseQoSProfileDetail(@Nonnull String name,
                                   @Nonnull String packageName,
                                   @Nonnull String reason,
                                   @Nullable RnCQuotaProfileDetail allServiceQuotaProfileDetail,
                                   @Nullable Map<String, QuotaProfileDetail> serviceToQuotaProfileDetail,
                                   int fupLevel,
                                   @Nonnull Integer orderNo,
                                   boolean applyOnUsageUnavailability,
                                   @Nullable String redirectURL) {

        super(name, packageName, QoSProfileAction.REJECT, reason, allServiceQuotaProfileDetail, serviceToQuotaProfileDetail, fupLevel, orderNo, redirectURL);
        this.applyOnUsageUnavailability = applyOnUsageUnavailability;
        this.qoSSelectionWithoutBalance = new QoSSelectionWithoutBalance();
        this.qoSSelectionWithBalance = new QoSSelectionWithBalance();
        this.qoSSelectionWithoutQuotaProfile = new QoSSelectionWithoutQuotaProfile();
    }

    /*
    used when action is accept
     */
    public RnCBaseQoSProfileDetail(@Nonnull String name,
                                   @Nonnull String pkgName,
                                   int fupLevel,
                                   @Nullable RnCQuotaProfileDetail allServiceQuotaProfileDetail,
                                   @Nullable Map<String, QuotaProfileDetail> serviceToQuotaProfileDetail,
                                   @Nonnull IPCANQoS sessionQoS,
                                   @Nullable List<PCCRule> pccRules,
                                   @Nonnull Integer orderNo,
                                   boolean applyOnUsageUnavailability,
                                   @Nullable String redirectURL,
                                   @Nullable List<ChargingRuleBaseName> chargingRuleBaseNames) {

        super(name, pkgName, fupLevel, allServiceQuotaProfileDetail, serviceToQuotaProfileDetail, sessionQoS, pccRules, orderNo, redirectURL, chargingRuleBaseNames);
        this.applyOnUsageUnavailability = applyOnUsageUnavailability;
        this.qoSSelectionWithoutBalance = new QoSSelectionWithoutBalance();
        this.qoSSelectionWithBalance = new QoSSelectionWithBalance();
        this.qoSSelectionWithoutQuotaProfile = new QoSSelectionWithoutQuotaProfile();
    }

    @Override
    public @Nullable
    RnCQuotaProfileDetail getAllServiceQuotaProfileDetail() {
        return (RnCQuotaProfileDetail) super.getAllServiceQuotaProfileDetail();
    }

    private boolean isMonetaryBalanceApplicable(){

        RnCQuotaProfileDetail allServiceQuotaProfileDetail = getAllServiceQuotaProfileDetail();

        if(allServiceQuotaProfileDetail!=null && allServiceQuotaProfileDetail.isRateConfigured()){
            return true;
        }

        Map<String, QuotaProfileDetail> serviceToQuotaProfileDetail = getServiceToQuotaProfileDetail();

        if(serviceToQuotaProfileDetail!=null){
            for(QuotaProfileDetail quotaProfileDetail: serviceToQuotaProfileDetail.values()){
                RnCQuotaProfileDetail rnCQuotaProfileDetail = (RnCQuotaProfileDetail) quotaProfileDetail;
                if(rnCQuotaProfileDetail.isRateConfigured()){
                    return true;
                }
            }
        }

        return false;

    }
    /* (non-Javadoc)
         * @see com.elitecore.netvertex.pm.QoSprofileDetail#apply(com.elitecore.netvertex.pm.PolicyContext, com.elitecore.netvertex.pm.QoSInformation)
         */
    @Override
    public SelectionResult apply(PolicyContext policyContext, QoSInformation qosInformation, SelectionResult previousQoSResult) {

        if (getLogger().isInfoLogLevel()) {
            policyContext.getTraceWriter().println("Level:" + getFUPLevel());
            policyContext.getTraceWriter().incrementIndentation();
        }

        try {
            RnCQuotaProfileDetail allServiceQuotaProfileDetail = getAllServiceQuotaProfileDetail();
            if (allServiceQuotaProfileDetail == null && getServiceToQuotaProfileDetail() == null) {
                if (getLogger().isDebugLogLevel()) {
                    getLogger().debug(MODULE, "Applying qos selection without usage. Reason: Quota profile not attached to qos profile: " + getName());
                }
                return this.qoSSelectionWithoutQuotaProfile.apply(policyContext, qosInformation, null, previousQoSResult, null);
            } else {

                SubscriptionNonMonitoryBalance currentBalance = null;
                try {
                    currentBalance = policyContext.getCurrentBalance().getPackageBalance(qosInformation.getCurrentSubscriptionOrPackageId());
                } catch (OperationFailedException e) {

                    qosInformation.setUsageException(e);
                    if (isApplyOnUsageUnavailability() == false) {
                        if (getLogger().isErrorLogLevel()) {
                            getLogger().error(MODULE, "QoS profile(" + getName() + ") cannot be applied for subscriber: " + policyContext.getSPInfo().getSubscriberIdentity() + " on usage unavailability. Reason: Error while fetching usage, Cause: "
                                    + qosInformation.getUsageException().getMessage());
                        }

                        if (getLogger().isInfoLogLevel()) {
                            policyContext.getTraceWriter().println("Result:" + SelectionResult.NOT_APPLIED.displayValue + "(Error while fetching usage. Reason: " + e.getMessage() + ")");
                        }

                        return SelectionResult.NOT_APPLIED;
                    }
                }


                SubscriberMonetaryBalance monetoryBalance;
                if (isMonetaryBalanceApplicable()) {
                    try {
                        monetoryBalance = policyContext.getCurrentMonetaryBalance();
                    } catch (OperationFailedException e) {
                        getLogger().error(MODULE, "QoS profile(" + getName() + ") cannot be applied for subscriber: " + policyContext.getSPInfo().getSubscriberIdentity() + " on monetary balance unavailability. Reason: Error while fetching monetary balance, Cause: "
                                + e.getMessage());

                        if (getLogger().isInfoLogLevel()) {
                            policyContext.getTraceWriter().println("Result:" + SelectionResult.NOT_APPLIED.displayValue + "(Error while fetching monetary balance. Reason: " + e.getMessage() + ")");
                        }

                        LogManager.ignoreTrace(e);


                        return SelectionResult.NOT_APPLIED;
                    }

                    if(allServiceQuotaProfileDetail!=null && allServiceQuotaProfileDetail.isRateConfigured() && monetoryBalance.isDataBalanceExist() == false){
                        if (getLogger().isDebugLogLevel()) {
                            getLogger().debug(MODULE, "Subscribers monetary balance not exist so skipping current processing");
                        }
                        policyContext.getTraceWriter().println("Result:" + SelectionResult.NOT_APPLIED.displayValue + "(Monetary balance is exceeded)");
                        return SelectionResult.NOT_APPLIED;
                    }
                } else {
                    monetoryBalance = new SubscriberMonetaryBalance(TimeSource.systemTimeSource());
                }

                if (currentBalance == null) {
                    return handleWithoutCurrentUsage(policyContext, qosInformation, previousQoSResult, currentBalance, monetoryBalance);
                } else {
                    return this.qoSSelectionWithBalance.apply(policyContext, qosInformation, currentBalance, previousQoSResult, monetoryBalance);
                }
            }
        } finally {
            if (getLogger().isInfoLogLevel()) {
                policyContext.getTraceWriter().decrementIndentation();
            }
        }
    }

    private SelectionResult handleWithoutCurrentUsage(PolicyContext policyContext, QoSInformation qosInformation,
                                                      SelectionResult previousQoSResult, SubscriptionNonMonitoryBalance currentBalance,
                                                      SubscriberMonetaryBalance monetoryBalance) {
        if (qosInformation.getUsageException() != null) {
            if (getLogger().isWarnLogLevel()) {
                getLogger().warn(MODULE, "Applying qos profile without usage for subscriber: " + policyContext.getSPInfo().getSubscriberIdentity() + ". Reason: " + qosInformation.getUsageException().getMessage());
            }
        } else {
            if (getLogger().isErrorLogLevel()) {
                getLogger().error(MODULE, "QoS profile(" + getName() + ") cannot be applied for subscriber: "
                        + policyContext.getSPInfo().getSubscriberIdentity() + ". Reason: Usage is not found from DB");
            }

            if (getLogger().isInfoLogLevel()) {
                policyContext.getTraceWriter().println("Result:" + SelectionResult.NOT_APPLIED.displayValue
                        + ". Reason: Usage is required and it is not found from DB");
            }

            return SelectionResult.NOT_APPLIED;
        }

        return this.qoSSelectionWithoutBalance.apply(policyContext, qosInformation, currentBalance, previousQoSResult, monetoryBalance);
    }


    private String writeTrace(PCCRule pccRule) {
        return pccRule.getName() + "[Service:" + pccRule.getServiceName()
                + ",QCI:" + pccRule.getQCI().stringVal
                + ",GBRUL:" + pccRule.getGBRUL()
                + ",GBRDL:" + pccRule.getGBRDL()
                + ",MBRUL:" + pccRule.getMBRUL()
                + ",MBRDL:" + pccRule.getMBRDL()
                + "]";
    }

    public boolean isApplyOnUsageUnavailability() {
        return applyOnUsageUnavailability;
    }

    private interface QoSSelection extends Serializable {
        public SelectionResult apply(PolicyContext policyContext,
                                     QoSInformation qosInformation,
                                     SubscriptionNonMonitoryBalance currentPackageUsage, SelectionResult previousQoSResult, SubscriberMonetaryBalance subscriberMonetaryBalance);
    }

    private class QoSSelectionWithoutQuotaProfile implements QoSSelection {

        @Override
        public SelectionResult apply(PolicyContext policyContext, QoSInformation qosInformation, SubscriptionNonMonitoryBalance currentPackageUsage,
                                     SelectionResult previousQoSResult, SubscriberMonetaryBalance subscriberMonetaryBalance) {

            QoSProfileDetail previousQoSProfileDetail = qosInformation.getQoSProfileDetail();
            qosInformation.setQoSProfileDetail(RnCBaseQoSProfileDetail.this);

            if (getAction() == QoSProfileAction.REJECT) {
                if (getLogger().isInfoLogLevel()) {
                    policyContext.getTraceWriter().println("Session QoS: [Action: Reject, RejectCause: " + getRejectCause() + "]");
                }
                return SelectionResult.FULLY_APPLIED;
            }

            if (Collectionz.isNullOrEmpty(getPCCRules()) == false) {
                qosInformation.setPCCRules(getPCCRules());
            }

            if (getLogger().isInfoLogLevel()) {
                IPCANQoS sessionQoS = getSessionQoS();
                policyContext.getTraceWriter().println("Session QoS [QCI:" + sessionQoS.getQCI().stringVal
                        + ",AAMBRUL:" + sessionQoS.getAAMBRULInBytes()
                        + ",AAMBRDL:" + sessionQoS.getAAMBRDLInBytes()
                        + ",MBRUL:" + sessionQoS.getMBRULInBytes()
                        + ",MBRDL:" + sessionQoS.getMBRDLInBytes() + "]");

                writeTrace(getPCCRules(), policyContext.getTraceWriter());
            }


            if (Collectionz.isNullOrEmpty(getChargingRuleBaseNames()) == false && previousQoSProfileDetail == null) {
                qosInformation.setChargingRuleBaseNames(getChargingRuleBaseNames());
            }

            if (getLogger().isInfoLogLevel()) {
                policyContext.writeTraceForChargingRuleBaseName(getChargingRuleBaseNames());
            }

            return SelectionResult.FULLY_APPLIED;

        }

        private void writeTrace(List<PCCRule> pccRules, IndentingPrintWriter writer) {
            if (Collectionz.isNullOrEmpty(pccRules) == false) {
                for (int index = 0; index < pccRules.size(); index++) {
                    writer.println("Satisfied PCC:" + RnCBaseQoSProfileDetail.this.writeTrace(pccRules.get(index)));
                }
            }
        }
    }

    private class QoSSelectionWithBalance implements QoSSelection {



        public SelectionResult apply(PolicyContext policyContext,
                                     QoSInformation qosInformation,
                                     SubscriptionNonMonitoryBalance currentPackageUsage,
                                     SelectionResult previousQoSResult,
                                     SubscriberMonetaryBalance subscriberMonetaryBalance) {

            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Check default service usage exceeded");
            }

            QoSProfileDetail previousQoSProfileDetail  = qosInformation.getQoSProfileDetail();
            RnCQuotaProfileDetail allServiceQuotaProfileDetail = getAllServiceQuotaProfileDetail();

            if(allServiceQuotaProfileDetail != null) {

                NonMonetoryBalance packageBalance = currentPackageUsage.getBalance(getQuotaProfileId(), allServiceQuotaProfileDetail.getDataServiceType().getServiceIdentifier(), allServiceQuotaProfileDetail.getRatingGroup().getIdentifier(), getFUPLevel());
                if (getLogger().isDebugLogLevel()) {
                    getLogger().debug(MODULE, "Subscriber's total usage: " + packageBalance);
                }

                TotalBalance qosBalance = getAllServiceBalanceIfExist(policyContext, currentPackageUsage);

                SelectionResult sessionLevelQoSSelectionResult = evaluateSessionLevelQoS(qosBalance, policyContext);

                if(sessionLevelQoSSelectionResult == SelectionResult.NOT_APPLIED) {
                    return SelectionResult.NOT_APPLIED;
                }


                if(getAction() == QoSProfileAction.REJECT || (getPCCRules() == null && getChargingRuleBaseNames() == null)) {
                    qosInformation.setQoSBalance(qosBalance);
                    qosInformation.setQoSProfileDetail(RnCBaseQoSProfileDetail.this);
                    return SelectionResult.FULLY_APPLIED;
                }


                SelectionResult pccRuleResult = evaluatePCCRules(currentPackageUsage, policyContext, qosInformation, subscriberMonetaryBalance, qosBalance);

                if (Collectionz.isNullOrEmpty(getChargingRuleBaseNames()) == false /*CRBN is configured in this QoSProfileDetail(HSQ/FUP Level)*/
                        && previousQoSProfileDetail == null) { /*Previous HSQ/FUP level is not satisfied*/
                    addChargingRuleBaseName(qosInformation, policyContext);
                } else if (pccRuleResult == SelectionResult.NOT_APPLIED) {
                    return SelectionResult.NOT_APPLIED;
                }


                qosInformation.setQoSBalance(qosBalance);
                qosInformation.setQoSProfileDetail(RnCBaseQoSProfileDetail.this);


                if (pccRuleResult == SelectionResult.FULLY_APPLIED){
                    return SelectionResult.FULLY_APPLIED;
                }

                return SelectionResult.PARTIALLY_APPLIED;

            } else {
                if (getLogger().isDebugLogLevel()) {
                    getLogger().debug(MODULE, "Subscriber's All service balance not defined");
                }

                if(Collectionz.isNullOrEmpty(getPCCRules())) {
                    if(getLogger().isInfoLogLevel()) {
                        policyContext.getTraceWriter().println("Result:" + SelectionResult.NOT_APPLIED.displayValue+"(All service balance not defined and no pcc rule configured)");
                    }
                    return SelectionResult.NOT_APPLIED;
                }

                if(getLogger().isInfoLogLevel()) {
                    policyContext.getTraceWriter().println("Session QoS: Not Satisfied(All service balance not defined)");
                }

                SelectionResult pccRuleResult = evaluatePCCRules(currentPackageUsage, policyContext, qosInformation, subscriberMonetaryBalance, null);

                if (pccRuleResult == SelectionResult.NOT_APPLIED){
                    return SelectionResult.NOT_APPLIED;
                }

                qosInformation.setQoSProfileDetail(RnCBaseQoSProfileDetail.this);
                return SelectionResult.PARTIALLY_APPLIED;
            }



        }

        private SelectionResult evaluateSessionLevelQoS(TotalBalance qosBalance, PolicyContext policyContext) {

            if(qosBalance == null) {
                if(getLogger().isInfoLogLevel()) {
                    policyContext.getTraceWriter().println("Result:" + SelectionResult.NOT_APPLIED.displayValue+"(All service data exceeded)");
                }
                return SelectionResult.NOT_APPLIED;
            }

            if(getLogger().isInfoLogLevel()) {
                policyContext.getTraceWriter().print("Session QoS: ");
                if(getAction() == QoSProfileAction.REJECT) {
                    policyContext.getTraceWriter().append("[Action: Reject, RejectCause: " + getRejectCause() + "]");
                } else {
                    policyContext.getTraceWriter().append("[QCI:"+ getSessionQoS().getQCI().stringVal
                            + ", AAMBRUL:"+getSessionQoS().getAAMBRULInBytes()
                            +", AAMBRDL:"+ getSessionQoS().getAAMBRDLInBytes()
                            + ", MBRUL:"+ getSessionQoS().getMBRULInBytes()
                            +", MBRDL:"+ getSessionQoS().getMBRDLInBytes() + "]");
                    policyContext.getTraceWriter().println();
                }
            }

            return SelectionResult.FULLY_APPLIED;
        }

        private TotalBalance getAllServiceBalanceIfExist(PolicyContext policyContext,
                                                        SubscriptionNonMonitoryBalance currentPackageBalance) {

            RnCQuotaProfileDetail serviceQuotaProfileDetail;
            serviceQuotaProfileDetail = getAllServiceQuotaProfileDetail();

            Collection<Subscription> preTopUpSubscriptions = policyContext.getPreTopUpSubscriptions();
            Collection<Subscription> spareTopUpSubscriptions = policyContext.getSpareTopUpSubscriptions();

            String subscriberId = policyContext.getPCRFResponse().getAttribute(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val);
            if (CollectionUtils.isNotEmpty(preTopUpSubscriptions)) {

                TotalBalance preTopUpBalance = getTopUpBalanceIfExist(policyContext, preTopUpSubscriptions, subscriberId);

                if (Objects.nonNull(preTopUpBalance) && preTopUpBalance.isExist()) {
                    return preTopUpBalance;
                }
            }

            NonMonetoryBalance packageBalance = currentPackageBalance.getBalance(getQuotaProfileId(), serviceQuotaProfileDetail.getDataServiceType().getServiceIdentifier(), serviceQuotaProfileDetail.getRatingGroup().getIdentifier(), getFUPLevel());
            TotalBalance basePackageBalance = serviceQuotaProfileDetail.getTotalBalance(packageBalance, Balance.ZERO, policyContext.getCurrentTime());

            if (basePackageBalance.isExist()) {
                return basePackageBalance;
            }

            if(CollectionUtils.isNotEmpty(spareTopUpSubscriptions) &&
                    serviceQuotaProfileDetail.isHsqLevel()) {
                TotalBalance spareTopUpBalance = getTopUpBalanceIfExist(policyContext, spareTopUpSubscriptions, subscriberId);

                if (Objects.nonNull(spareTopUpBalance) && spareTopUpBalance.isExist()) {
                    return spareTopUpBalance;
                }
            }

            return null;
        }

        private SelectionResult evaluatePCCRules(SubscriptionNonMonitoryBalance currentPackageBalance,
                                                 PolicyContext policyContext,
                                                 QoSInformation qosInformation,
                                                 SubscriberMonetaryBalance monetaryBalance,
                                                 TotalBalance allServiceBalance) {

            SelectionResult result = SelectionResult.FULLY_APPLIED;

            List<PCCRule> pccRules = getPCCRules();

            if (Collectionz.isNullOrEmpty(pccRules)) {
                if (getLogger().isInfoLogLevel()) {
                    policyContext.getTraceWriter().println("No pcc rule Found");
                }
                return result;
            }

            boolean isAtleastOnePCCRuleSatisfied = false;

            for (int pccRuleIndex = 0; pccRuleIndex < pccRules.size(); pccRuleIndex++) {

                PCCRule pccRule = pccRules.get(pccRuleIndex);

                boolean selected = false;
                if (pccRule.getServiceTypeId().equals(CommonConstants.ALL_SERVICE_ID)) {
                    RnCQuotaProfileDetail allServiceQuotaProfileDetail = getAllServiceQuotaProfileDetail();
                    if(Objects.nonNull(allServiceQuotaProfileDetail)) {
                        selected = qosSelectionResult(allServiceQuotaProfileDetail, monetaryBalance, pccRule, policyContext, allServiceBalance, qosInformation);
                    }

                } else {

                    if (getServiceToQuotaProfileDetail() != null) {
                        RnCQuotaProfileDetail serviceQuotaProfileDetail = (RnCQuotaProfileDetail) getServiceToQuotaProfileDetail().get(pccRule.getServiceTypeId());
                        TotalBalance pccBalance = getServiceBalance(serviceQuotaProfileDetail, pccRule.getChargingKey(), currentPackageBalance, policyContext);
                        selected = qosSelectionResult(serviceQuotaProfileDetail, monetaryBalance, pccRule, policyContext, pccBalance, qosInformation);
                    }
                }

                if (selected == true) {
                    isAtleastOnePCCRuleSatisfied = true;
                } else {
                    if (getLogger().isInfoLogLevel()) {
                        policyContext.getTraceWriter().println("Not Satisfied(Quota exceeded):" + writeTrace(pccRule));
                    }

                    result = SelectionResult.PARTIALLY_APPLIED;
                }
            }

            if (isAtleastOnePCCRuleSatisfied == false) {
                if (getLogger().isInfoLogLevel()) {
                    policyContext.getTraceWriter().println("Note: No pcc rule satisfied");
                }
                result = SelectionResult.NOT_APPLIED;
            }

            return result;

        }

        private void addChargingRuleBaseName(QoSInformation qosInformation, PolicyContext policyContext) {

            List<ChargingRuleBaseName> chargingRuleBaseNames = getChargingRuleBaseNames();

            for (int chargingRuleBaseNameIndex = 0; chargingRuleBaseNameIndex < chargingRuleBaseNames.size(); chargingRuleBaseNameIndex++) {

                ChargingRuleBaseName chargingRuleBaseName = chargingRuleBaseNames.get(chargingRuleBaseNameIndex);

                if (getLogger().isInfoLogLevel()) {
                    policyContext.getTraceWriter().print("Satisfied ChargingRuleBaseName: ");
                    chargingRuleBaseName.printToQosSelectionSummary(policyContext.getTraceWriter());
                    policyContext.getTraceWriter().println();
                }

                qosInformation.add(chargingRuleBaseName);
            }
        }

        private TotalBalance getServiceBalance(RnCQuotaProfileDetail serviceQuotaProfileDetail,
                                               long ratingGroup,
                                               SubscriptionNonMonitoryBalance currentPackageUsage,
                                               PolicyContext policyContext) {

            NonMonetoryBalance packageBalance = currentPackageUsage.getBalance(getQuotaProfileId(), serviceQuotaProfileDetail.getDataServiceType().getServiceIdentifier(), ratingGroup, getFUPLevel());
            return serviceQuotaProfileDetail.getTotalBalance(packageBalance, Balance.ZERO, policyContext.getCurrentTime());

        }

        private boolean qosSelectionResult(RnCQuotaProfileDetail serviceQuotaProfileDetail,
                                           SubscriberMonetaryBalance monetaryBalance,
                                           PCCRule pccRule,
                                           PolicyContext policyContext,
                                           TotalBalance pccBalance,
                                           QoSInformation qosInformation) {

            if (serviceQuotaProfileDetail == null) {
                if (getLogger().isInfoLogLevel()) {
                    policyContext.getTraceWriter().println("Not Satisfied(balance not defined for " + pccRule.getServiceName() + " in quota profile):" + writeTrace(pccRule));
                }

                return false;
            }

            if (serviceQuotaProfileDetail.getRate() > 0 && (monetaryBalance.isDataBalanceExist() == false)) {
                if (getLogger().isInfoLogLevel()) {
                    policyContext.getTraceWriter().println("Not Satisfied(monetary balance not exist for " + pccRule.getServiceName() + "):" + writeTrace(pccRule));
                }
                return false;
            }

            if (pccBalance == null) {
                if (getLogger().isInfoLogLevel()) {
                    policyContext.getTraceWriter().println("Not Satisfied(balance not found for " + pccRule.getServiceName() + " in quota profile):" + writeTrace(pccRule));
                }
                return false;
            }

            boolean isBalanceExist = pccBalance.isExist();

            if (isBalanceExist) {
                PCCRule satisfiedPCC = pccRule;
                if (getLogger().isInfoLogLevel()) {
                    policyContext.getTraceWriter().println("Satisfied:" + writeTrace(satisfiedPCC));
                }

                qosInformation.add(satisfiedPCC);

                if (satisfiedPCC.getUsageMetering() != UsageMetering.DISABLE_QUOTA) {
                    qosInformation.getPccBalanceMap().put(satisfiedPCC.getName(), pccBalance);
                }
                return true;
            } else {
                if (getLogger().isInfoLogLevel()) {
                    policyContext.getTraceWriter().println("Not Satisfied(Quota exceeded):" + writeTrace(pccRule));
                }

                return false;
            }
        }

        private TotalBalance getTopUpBalanceIfExist(PolicyContext policyContext,
                                                    Collection<Subscription> topUpSubscriptions,
                                                    String subscriberId) {

            if (Collectionz.isNullOrEmpty(topUpSubscriptions)) {
                return null;
            }

            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Applying topup subscriptions for subscriber ID: " + subscriberId);
            }

            for (Subscription topUpSubscription : topUpSubscriptions) {

                QuotaTopUp subscriptionPackage = policyContext.getPolicyRepository().getQuotaTopUpById(topUpSubscription.getPackageId());

                if (subscriptionPackage.isTopUpIsEligibleToApply(getName()) == false) {
                	if (getLogger().isDebugLogLevel()) {
                		getLogger().debug(MODULE, "Skipping Data TopUp(" + subscriptionPackage.getName()
								+ "). Reason: Current PCC Profile(" + getName() + ") is not eligible for this TopUp");
					}
					continue;
				}

                if (topUpSubscription.getStartTime().getTime() > policyContext.getCurrentTime().getTimeInMillis()) {

                    policyContext.setRevalidationTime(topUpSubscription.getStartTime());

                    if (getLogger().isDebugLogLevel()) {
                        getLogger().debug(MODULE, "Skipping future topUp(name: " + subscriptionPackage.getName()
                                + ") subscription(id: " + topUpSubscription.getId() + "), Considering start-time(" + topUpSubscription.getStartTime()
                                + ") for session revalidation");
                    }
                    continue;
                }

                RnCQuotaProfileDetail serviceQuotaProfileDetail;
                if (getAllServiceQuotaProfileDetail().getFupLevel() == 0) {
                    serviceQuotaProfileDetail = (RnCQuotaProfileDetail) subscriptionPackage.getQuotaProfile().getHsqLevelServiceWiseQuotaProfileDetails().get(CommonConstants.ALL_SERVICE_ID);
                } else {
                    serviceQuotaProfileDetail = getAllServiceQuotaProfileDetail();
                }

                NonMonetoryBalance packageBalance = null;
                try {
                    SubscriptionNonMonitoryBalance subscriptionBalance = policyContext.getCurrentBalance().getPackageBalance(topUpSubscription.getId());
                    if(Objects.isNull(subscriptionBalance)) {
                        if (getLogger().isDebugLogLevel()) {
                            getLogger().debug(MODULE, "Skipping topUp(name: " + getName()
                                    + ") subscription(id: " + topUpSubscription.getId() + "), Considering subscription balance is not available");
                        }
                        continue;
                    }
                    packageBalance = subscriptionBalance.getBalance(subscriptionPackage.getQuotaProfile().getId()).getHsqBalance().get(0);
                } catch (OperationFailedException e) {
                    if (getLogger().isDebugLogLevel()) {
                        getLogger().debug(MODULE, "Skipping topUp(name: " + getName()
                                + ") subscription(id: " + topUpSubscription.getId() + "), Reason: " +e.getMessage());
                    }
                    LogManager.ignoreTrace(e);
                    continue;
                }
                if (getLogger().isDebugLogLevel()) {
                    getLogger().debug(MODULE, "Subscriber's total usage: " + packageBalance);
                }

                TotalBalance balance = serviceQuotaProfileDetail.getTotalBalance(packageBalance, Balance.ZERO, policyContext.getCurrentTime());

                if (balance.isExist()) {
                    return balance;
                }

            }

            return null;
        }
    }

    private class QoSSelectionWithoutBalance implements QoSSelection {

        @Override
        public SelectionResult apply(PolicyContext policyContext, QoSInformation qosInformation,
                                     SubscriptionNonMonitoryBalance currentPackageUsage, SelectionResult previousQoSResult,
                                     SubscriberMonetaryBalance monetoryBalance) {

            QoSProfileDetail previousQoSProfileDetail = qosInformation.getQoSProfileDetail();
            qosInformation.setQoSProfileDetail(RnCBaseQoSProfileDetail.this);

            if (getAction() == QoSProfileAction.REJECT) {
                if (getLogger().isInfoLogLevel()) {
                    policyContext.getTraceWriter().println("Session QoS: [Action: Reject, RejectCause: " + getRejectCause() + "]");
                }
                return SelectionResult.FULLY_APPLIED;
            }

            if (getLogger().isInfoLogLevel()) {
                IPCANQoS sessionQoS = getSessionQoS();
                policyContext.getTraceWriter().println("Session QoS [QCI:" + sessionQoS.getQCI().stringVal
                        + ",AAMBRUL:" + sessionQoS.getAAMBRULInBytes()
                        + ",AAMBRDL:" + sessionQoS.getAAMBRDLInBytes()
                        + ",MBRUL:" + sessionQoS.getMBRULInBytes()
                        + ",MBRDL:" + sessionQoS.getMBRDLInBytes() + "]");
            }

            SelectionResult result = SelectionResult.FULLY_APPLIED;
            List<PCCRule> pccRules = getPCCRules();
            if (Collectionz.isNullOrEmpty(pccRules)) {
                if (getLogger().isInfoLogLevel()) {
                    policyContext.getTraceWriter().println("No pcc rule Found");
                }
                return result;
            }

            boolean isAtleastOnePCCRuleSatisfied = false;

            for (int pccRuleIndex = 0; pccRuleIndex < pccRules.size(); pccRuleIndex++) {

                PCCRule pccRule = pccRules.get(pccRuleIndex);

                RnCQuotaProfileDetail serviceQuotaProfileDetail = null;
                if (pccRule.getServiceTypeId().equals(CommonConstants.ALL_SERVICE_ID)) {
                    serviceQuotaProfileDetail = getAllServiceQuotaProfileDetail();
                } else {
                    if (getServiceToQuotaProfileDetail() != null) {
                        serviceQuotaProfileDetail = (RnCQuotaProfileDetail) getServiceToQuotaProfileDetail().get(pccRule.getServiceTypeId());
                    }
                }

                if (serviceQuotaProfileDetail == null) {
                    if (getLogger().isInfoLogLevel()) {
                        policyContext.getTraceWriter().println("Not Satisfied(balance not defined for " + pccRule.getServiceName() + " in quota profile):" + writeTrace(pccRule));
                    }

                    result = SelectionResult.PARTIALLY_APPLIED;
                    continue;
                }

                if (serviceQuotaProfileDetail.getRate() > 0 && (monetoryBalance.isDataBalanceExist() == false)) {
                    if (getLogger().isInfoLogLevel()) {
                        policyContext.getTraceWriter().println("Not Satisfied(monetary balance not exist for " + pccRule.getServiceName() + "):" + writeTrace(pccRule));
                    }
                    result = SelectionResult.PARTIALLY_APPLIED;
                    continue;
                }

                isAtleastOnePCCRuleSatisfied = true;
                qosInformation.add(pccRule);
                if (getLogger().isInfoLogLevel()) {
                    policyContext.getTraceWriter().println("Satisfied:" + writeTrace(pccRule));
                }
            }

            if (isAtleastOnePCCRuleSatisfied == false) {
                if (getLogger().isInfoLogLevel()) {
                    policyContext.getTraceWriter().println("Note: No pcc rule satisfied");
                }
                result = SelectionResult.NOT_APPLIED;
            }

            if (Collectionz.isNullOrEmpty(getChargingRuleBaseNames()) == false && previousQoSProfileDetail == null) {
                qosInformation.setChargingRuleBaseNames(getChargingRuleBaseNames());
            }

            if (getLogger().isInfoLogLevel()) {
                policyContext.writeTraceForChargingRuleBaseName(getChargingRuleBaseNames());
            }

            return result;
        }

    }
}
