package com.elitecore.netvertex.pm;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.constants.QoSProfileAction;
import com.elitecore.corenetvertex.pm.constants.SelectionResult;
import com.elitecore.corenetvertex.pm.pkg.ChargingRuleBaseName;
import com.elitecore.corenetvertex.pm.pkg.PCCRule;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.IPCANQoS;
import com.elitecore.netvertex.pm.qos.rnc.ratecard.DataRateCard;

import java.util.List;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class RateCardBasedQoSProfileDetail extends com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc.RateCardBasedQoSProfileDetail
        implements com.elitecore.netvertex.pm.QoSProfileDetail {

    public RateCardBasedQoSProfileDetail(String name,
                                         String packageName,
                                         QoSProfileAction action,
                                         String reason,
                                         com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc.ratecard.DataRateCard dataRateCard,
                                         String redirectURL) {
        super(name, packageName, action, reason, dataRateCard, redirectURL);
    }

    public RateCardBasedQoSProfileDetail(String name,
                                         String pkgName,
                                         com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc.ratecard.DataRateCard dataRateCard,
                                         IPCANQoS sessionQoS,
                                         List<PCCRule> pccRules,
                                         String redirectURL, List<ChargingRuleBaseName> chargingRuleBaseNames) {

        super(name, pkgName, dataRateCard, sessionQoS, pccRules, redirectURL, chargingRuleBaseNames);

    }

    @Override
    public SelectionResult apply(PolicyContext policyContext, QoSInformation qosInformation, SelectionResult previousQoSResult) {

        DataRateCard dataRateCard = (DataRateCard) getDataRateCard();

        if (getLogger().isInfoLogLevel()) {
            policyContext.getTraceWriter().println();
            policyContext.getTraceWriter().println("Applying Rate Card: " + getName());
            policyContext.getTraceWriter().incrementIndentation();
        }

        if (dataRateCard.isApplicable(policyContext, qosInformation) == false) {
            return SelectionResult.NOT_APPLIED;
        }

        qosInformation.setQoSProfileDetail(RateCardBasedQoSProfileDetail.this);

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

        if (Collectionz.isNullOrEmpty(getPCCRules()) == false) {
            qosInformation.setPCCRules(getPCCRules());
            if (getLogger().isInfoLogLevel()) {
                policyContext.writeTrace(getPCCRules());
            }
        }

        if (Collectionz.isNullOrEmpty(getChargingRuleBaseNames()) == false) {
            qosInformation.setChargingRuleBaseNames(getChargingRuleBaseNames());
            if (getLogger().isInfoLogLevel()) {
                policyContext.writeTraceForChargingRuleBaseName(getChargingRuleBaseNames());
            }
        }

        if (getLogger().isInfoLogLevel()) {
            policyContext.getTraceWriter().decrementIndentation();
        }

        return SelectionResult.FULLY_APPLIED;
    }
}
