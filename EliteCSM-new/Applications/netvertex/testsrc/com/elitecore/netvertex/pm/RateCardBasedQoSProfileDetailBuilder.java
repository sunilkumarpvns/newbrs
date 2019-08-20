package com.elitecore.netvertex.pm;

import java.util.List;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.constants.QoSProfileAction;
import com.elitecore.corenetvertex.pm.IPCanQoSFactory;
import com.elitecore.corenetvertex.pm.pkg.ChargingRuleBaseName;
import com.elitecore.corenetvertex.pm.pkg.PCCRule;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.IPCANQoS;
import com.elitecore.netvertex.pm.qos.rnc.ratecard.DataRateCard;

public class RateCardBasedQoSProfileDetailBuilder {
    private String name = "qosName1";
    private IPCANQoS ipcanQoS;
    private String reason;
    private List<PCCRule> pccRules;
    private List<ChargingRuleBaseName> chargingRuleBaseNames;
    private String pkgName = "packageName1";
    private DataRateCard rateCard;
    private QoSProfileAction action;

    public RateCardBasedQoSProfileDetailBuilder() {
        ipcanQoS = IPCanQoSFactory.randomQoS();
        pccRules = Collectionz.newArrayList();
        chargingRuleBaseNames = Collectionz.newArrayList();
    }

    public RateCardBasedQoSProfileDetailBuilder withDataRateCard(DataRateCard rateCard) {
        this.rateCard = rateCard;
        return this;
    }

    public RateCardBasedQoSProfileDetailBuilder withRejectAction(String reason) {
        this.reason = reason;
        return this;
    }

    public RateCardBasedQoSProfileDetailBuilder withAction(QoSProfileAction action) {
        this.action = action;
        return this;
    }

    public RateCardBasedQoSProfileDetailBuilder withRandomPCCRule() {
        pccRules.add(PCCRuleFactory.createPCCRuleWithRandomQoS().build());
        return this;
    }

    public RateCardBasedQoSProfileDetailBuilder withPCCRule(PCCRule pccRule) {
        pccRules.add(pccRule);
        return this;
    }

    public RateCardBasedQoSProfileDetailBuilder withPCCRules(List<PCCRule> pccRules) {
        this.pccRules.addAll(pccRules);
        return this;
    }

    public RateCardBasedQoSProfileDetail build() {
        return new com.elitecore.netvertex.pm.RateCardBasedQoSProfileDetail(name,
                pkgName,
                rateCard,
                ipcanQoS,
                pccRules,
                null,
                chargingRuleBaseNames);
    }

    public RateCardBasedQoSProfileDetail buildWithAction() {
        return new com.elitecore.netvertex.pm.RateCardBasedQoSProfileDetail(name,
                pkgName,
                action,
                reason,
                rateCard,
                null);
    }

    public RateCardBasedQoSProfileDetailBuilder withChargingRuleBaseNames(List<ChargingRuleBaseName> chargingRuleBaseNames) {
        this.chargingRuleBaseNames = chargingRuleBaseNames;
        return this;
    }
}
