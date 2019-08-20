
package com.elitecore.netvertex.pm;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.io.IndentingWriter;
import com.elitecore.corenetvertex.constants.QoSProfileAction;
import com.elitecore.corenetvertex.pm.constants.SelectionResult;
import com.elitecore.corenetvertex.pm.pkg.ChargingRuleBaseName;
import com.elitecore.corenetvertex.pm.pkg.PCCRule;
import com.elitecore.corenetvertex.pm.pkg.SliceInformation;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.IPCANQoS;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.util.List;
import java.util.Map;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class SyCounterBaseQoSProfileDetail extends com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.SyCounterBaseQoSProfileDetail implements QoSProfileDetail {

    private static final String MODULE = "SY-QOS-DETAIL";

    private final QoSSelection qosSelection;

    public SyCounterBaseQoSProfileDetail(String name,
                                         String pkgName,
                                         QoSProfileAction action,
                                         String reason,
                                         int fupLevel,
                                         @Nullable com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.SyCounterBaseQuotaProfileDetail allServiceQuotaProfileDetail,
                                         @Nullable Map<String, com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfileDetail> serviceToQuotaProfileDetail,
                                         IPCANQoS sessionQoS,
                                         List<PCCRule> pccRules,
                                         boolean usageMonitoring,
                                         SliceInformation sliceInformation,
                                         int orderNo,
                                         @Nullable String redirectURL,
                                         List<ChargingRuleBaseName> chargingRuleBaseNames) {
        super(name, pkgName, action, reason, fupLevel, allServiceQuotaProfileDetail, serviceToQuotaProfileDetail, sessionQoS, pccRules, usageMonitoring, sliceInformation, orderNo, redirectURL, chargingRuleBaseNames);
        if (allServiceQuotaProfileDetail == null) {
            this.qosSelection = new QoSSelectionWithoutCounter();
        } else {
            this.qosSelection = new QoSSelectionWithCounter();
        }
    }


    public SyCounterBaseQoSProfileDetail(String name,
                                         String packageName,
                                         QoSProfileAction action,
                                         String reason,
                                         @Nullable com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.SyCounterBaseQuotaProfileDetail allServiceQuotaProfileDetail,
                                         @Nullable Map<String, com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfileDetail> serviceToQuotaProfileDetail,
                                         int fupLevel,
                                         Integer orderNo,
                                         @Nullable String redirectURL) {
        super(name, packageName, action, reason, allServiceQuotaProfileDetail, serviceToQuotaProfileDetail, fupLevel, orderNo, redirectURL);
        if (allServiceQuotaProfileDetail == null) {
            this.qosSelection = new QoSSelectionWithoutCounter();
        } else {
            this.qosSelection = new QoSSelectionWithCounter();
        }
    }

    private String writeTrace(PCCRule pccRule) {
        return pccRule.getName() + "[Service:" + pccRule.getServiceName()
                + ",QCI:" + pccRule.getQCI().stringVal
                + ",GBRUL:" + pccRule.getGBRUL()
                + ",GBRDL:" + pccRule.getGBRDL()
                + ",MBRUL:" + pccRule.getMBRUL()
                + "MBRDL:" + pccRule.getMBRDL()
                + "]";
    }



    private interface QoSSelection extends Serializable {
        public SelectionResult apply(PolicyContext policyContext,
                                     QoSInformation qosInformation, SelectionResult previousQoSResult);
    }



    private class QoSSelectionWithCounter implements QoSSelection {

        @Override
        public SelectionResult apply(PolicyContext policyContext, QoSInformation qosInformation, SelectionResult previousQoSResult) {

            if (getLogger().isDebugLogLevel())
                getLogger().debug(MODULE, "Check all service counter satisfied");

            QoSProfileDetail previousQosDetail = qosInformation.getQoSProfileDetail();
            boolean allServiceQuotaExceeded = getAllServiceQuotaProfileDetail().isUsageExceeded(policyContext);
            if (getLogger().isInfoLogLevel()) {
                policyContext.getTraceWriter().print("Session QoS: ");
            }


            if (allServiceQuotaExceeded) {
                return onAllServiceBalanceExceeded(policyContext, qosInformation);
            } else {
                return onAllServiceBalanceExist(policyContext, qosInformation, previousQosDetail);
            }



        }

        private SelectionResult onAllServiceBalanceExist(PolicyContext policyContext, QoSInformation qosInformation, QoSProfileDetail previousQosDetail) {
            if (getAction() == QoSProfileAction.REJECT) {
                qosInformation.setQoSProfileDetail(SyCounterBaseQoSProfileDetail.this);
                if (getLogger().isInfoLogLevel()) {
                    policyContext.getTraceWriter().append("[Action: Reject, RejectCause: " + getRejectCause() + "]");
                    policyContext.getTraceWriter().println();
                }
                return SelectionResult.FULLY_APPLIED;
            } else {

                if (getLogger().isInfoLogLevel()) {
                    policyContext.getTraceWriter().append("[QCI:" + SyCounterBaseQoSProfileDetail.this.getSessionQoS().getQCI().stringVal
                            + ",AAMBRUL:" + SyCounterBaseQoSProfileDetail.this.getSessionQoS().getAAMBRULInBytes()
                            + ",AAMBRDL:" + SyCounterBaseQoSProfileDetail.this.getSessionQoS().getAAMBRDLInBytes()
                            + ",MBRUL:" + SyCounterBaseQoSProfileDetail.this.getSessionQoS().getMBRULInBytes()
                            + ",MBRDL:" + SyCounterBaseQoSProfileDetail.this.getSessionQoS().getMBRDLInBytes() + "]");
                    policyContext.getTraceWriter().println();
                }
            }

            if(getPCCRules() == null && getChargingRuleBaseNames() == null) {
                qosInformation.setQoSProfileDetail(SyCounterBaseQoSProfileDetail.this);
                return SelectionResult.FULLY_APPLIED;
            }

            SelectionResult pccRuleResult =  evaluatePCCRules(qosInformation, policyContext);

            boolean applyCRBN = getChargingRuleBaseNames() != null && previousQosDetail == null;

            if (applyCRBN) {
                addChargingRuleBaseNames(qosInformation, policyContext);
            }

            if(pccRuleResult == SelectionResult.FULLY_APPLIED){
                qosInformation.setQoSProfileDetail(SyCounterBaseQoSProfileDetail.this);
                return SelectionResult.FULLY_APPLIED;
            }

            if(pccRuleResult == SelectionResult.NOT_APPLIED && applyCRBN == false){
                return SelectionResult.NOT_APPLIED;
            }

            qosInformation.setQoSProfileDetail(SyCounterBaseQoSProfileDetail.this);
            return SelectionResult.PARTIALLY_APPLIED;
        }

        private SelectionResult onAllServiceBalanceExceeded(PolicyContext policyContext, QoSInformation qosInformation) {
            if (getLogger().isInfoLogLevel()) {
                policyContext.getTraceWriter().append("Result:" + SelectionResult.NOT_APPLIED.displayValue + "(All service counter not satisfied)");
                policyContext.getTraceWriter().println();
            }

            if (getPCCRules() == null) {
                return SelectionResult.NOT_APPLIED;
            }

            SelectionResult pccRuleResult =  evaluatePCCRules(qosInformation, policyContext);

            if(pccRuleResult == SelectionResult.NOT_APPLIED){
                return SelectionResult.NOT_APPLIED;
            }

            qosInformation.setQoSProfileDetail(SyCounterBaseQoSProfileDetail.this);

            return SelectionResult.PARTIALLY_APPLIED;
        }


        public SelectionResult evaluatePCCRules(QoSInformation qosInformation, PolicyContext policyContext) {


            List<PCCRule> pccRules = getPCCRules();

            if(pccRules == null) {
                return SelectionResult.FULLY_APPLIED;
            }

            SelectionResult result = SelectionResult.FULLY_APPLIED;

                boolean isAtleastOnePCCRuleSatisfied = false;
                for (int index = 0; index < pccRules.size(); index++) {
                    PCCRule pccRule = pccRules.get(index);
                    SyCounterBaseQuotaProfileDetail quotaProfileDetail = getQuotaProfileDetail(pccRule);

                    if (quotaProfileDetail.isUsageExceeded(policyContext) == false) {
                        if (getLogger().isInfoLogLevel()) {
                            policyContext.getTraceWriter().println("Satisfied:" + writeTrace(pccRule));
                        }
                        isAtleastOnePCCRuleSatisfied = true;
                        qosInformation.add(pccRule);
                    } else {
                        if (getLogger().isInfoLogLevel()) {
                            policyContext.getTraceWriter().println("Not Satisfied(" + quotaProfileDetail.getServiceName() + " counter(s) not satisfied):" + writeTrace(pccRule));
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

        private SyCounterBaseQuotaProfileDetail getQuotaProfileDetail(PCCRule pccRule) {
            SyCounterBaseQuotaProfileDetail  quotaProfileDetail = (SyCounterBaseQuotaProfileDetail) (SyCounterBaseQoSProfileDetail.this.getServiceToQuotaProfileDetail() == null ? null : SyCounterBaseQoSProfileDetail.this.getServiceToQuotaProfileDetail().get(pccRule.getServiceTypeId()));

            if(quotaProfileDetail == null) {
                quotaProfileDetail = getAllServiceQuotaProfileDetail();
            }

            return quotaProfileDetail;
        }

        public void addChargingRuleBaseNames(QoSInformation qosInformation, PolicyContext policyContext) {

            List<ChargingRuleBaseName> chargingRuleBaseNames = getChargingRuleBaseNames();

            for (ChargingRuleBaseName chargingRuleBaseName : chargingRuleBaseNames) {

                if (getLogger().isInfoLogLevel()) {
                   policyContext.getTraceWriter().print("Satisfied: ");
                   chargingRuleBaseName.printToQosSelectionSummary(policyContext.getTraceWriter());
                }
                qosInformation.add(chargingRuleBaseName);
            }
        }

    }

    private class QoSSelectionWithoutCounter implements QoSSelection {

        @Override
        public SelectionResult apply(PolicyContext policyContext, QoSInformation qosInformation, SelectionResult previousQoSResult) {


            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Applying Qos profile. Reason: usage not found");
            }

            qosInformation.setQoSProfileDetail(SyCounterBaseQoSProfileDetail.this);

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
                policyContext.getTraceWriter().println("Session QoS(QCI:" + SyCounterBaseQoSProfileDetail.this.getSessionQoS().getQCI().stringVal
                        + ",MBRUL:" + SyCounterBaseQoSProfileDetail.this.getSessionQoS().getAAMBRULInBytes() + ",MBRDL:"
                        + SyCounterBaseQoSProfileDetail.this.getSessionQoS().getAAMBRULInBytes() + ')');

                writeTrace(getPCCRules(), policyContext.getTraceWriter());
            }

            if (Collectionz.isNullOrEmpty(getChargingRuleBaseNames()) == false && previousQoSResult == SelectionResult.NOT_APPLIED) {
                qosInformation.setChargingRuleBaseNames(getChargingRuleBaseNames());
            }

            if (getLogger().isInfoLogLevel()) {
                writeTraceForChargingRuleBaseName(getChargingRuleBaseNames(), policyContext.getTraceWriter());
            }

            return SelectionResult.FULLY_APPLIED;
        }

        private void writeTrace(List<PCCRule> pccRules, IndentingWriter writer) {
            if (Collectionz.isNullOrEmpty(pccRules) == false) {
                for (int index = 0; index < pccRules.size(); index++) {
                    writer.println("Satisfied PCC:" + SyCounterBaseQoSProfileDetail.this.writeTrace(pccRules.get(index)));
                }
            }
        }

        private void writeTraceForChargingRuleBaseName(List<ChargingRuleBaseName> chargingRuleBaseNames, IndentingWriter writer) {
            if (Collectionz.isNullOrEmpty(chargingRuleBaseNames) == false) {
                for (int index = 0; index < chargingRuleBaseNames.size(); index++) {
                    writer.print("Satisfied CharginRuleBaseName: ");
                    chargingRuleBaseNames.get(index).printToQosSelectionSummary(writer);
                    writer.println();
                }
            }
        }

    }

    @Override
    public SyCounterBaseQuotaProfileDetail getAllServiceQuotaProfileDetail() {
        return (SyCounterBaseQuotaProfileDetail) super.getAllServiceQuotaProfileDetail();
    }

    @Override
    public SelectionResult apply(PolicyContext policyContext, QoSInformation qosInformation, SelectionResult previousQoSResult) {
        if (getLogger().isInfoLogLevel()) {
            policyContext.getTraceWriter().println("Level:" + getLevel());
            policyContext.getTraceWriter().incrementIndentation();
        }

        SelectionResult result = this.qosSelection.apply(policyContext, qosInformation, previousQoSResult);
        if (getLogger().isInfoLogLevel()) {
            policyContext.getTraceWriter().decrementIndentation();
        }

        return result;
    }

}
