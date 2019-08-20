package com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.QoSProfileAction;
import com.elitecore.corenetvertex.pm.pkg.ChargingRuleBaseName;
import com.elitecore.corenetvertex.pm.pkg.PCCRule;
import com.elitecore.corenetvertex.pm.pkg.SliceInformation;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.IPCANQoS;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.QoSProfileDetail;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc.ratecard.DataRateCard;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfileDetail;
import com.elitecore.corenetvertex.util.ToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringStyle;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public class RateCardBasedQoSProfileDetail implements QoSProfileDetail {
    private DataRateCard dataRateCard;
    private transient List<PCCRule> pccRules = null;
    private transient List<ChargingRuleBaseName> chargingRuleBaseNames = null;
    private String name;
    private String packageName;
    private QoSProfileAction action = QoSProfileAction.ACCEPT;
    private String rejectCause = null;
    private IPCANQoS sessionQoS;
    private String redirectURL;

    public RateCardBasedQoSProfileDetail(String name,
                                         String packageName,
                                         QoSProfileAction action,
                                         String reason,
                                         DataRateCard dataRateCard,
                                         String redirectURL) {
        this(name, packageName, dataRateCard, redirectURL);

        this.action = action;
        this.rejectCause = reason;
        this.sessionQoS = null;
    }

    public RateCardBasedQoSProfileDetail(String name,
                                   String pkgName,
                                   DataRateCard dataRateCard,
                                   IPCANQoS sessionQoS,
                                   List<PCCRule> pccRules,
                                   String redirectURL, List<ChargingRuleBaseName> chargingRuleBaseNames) {

        this(name, pkgName, dataRateCard, redirectURL);

        this.sessionQoS = sessionQoS;
        this.pccRules = pccRules;
        this.chargingRuleBaseNames = chargingRuleBaseNames;
    }

    private RateCardBasedQoSProfileDetail(String name,
                                    String pkgName,
                                    DataRateCard dataRateCard, String redirectURL) {

        this.name = name;
        this.packageName = pkgName;
        this.dataRateCard = dataRateCard;
        this.redirectURL = redirectURL;

    }

    @Override
    public String getLevel() {
        return HSQ;
    }

    @Override
    @Nullable
    public String getRedirectURL() {
        return redirectURL;
    }

    @Override
    public String getQuotaProfileName() {
        return null;
    }

    @Override
    public String getQuotaProfileId() {
        return null;
    }

    @Override
    public IPCANQoS getSessionQoS() {
        return sessionQoS;
    }

    @Override
    public QoSProfileAction getAction() {
        return action;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getFUPLevel() {
        return 0;
    }

    @Override
    public SliceInformation getSliceInformation() {
        return null;
    }

    @Override
    public String getRejectCause() {
        return rejectCause;
    }

    @Override
    public String getPackageName() {
        return packageName;
    }

    @Override
    public boolean getUsageMonitoring() {
        return false;
    }

    @Override
    public List<PCCRule> getPCCRules() {
        return pccRules;
    }

    @Override
    public List<ChargingRuleBaseName> getChargingRuleBaseNames() {
        return chargingRuleBaseNames;
    }


    @Override
    public String getUniqueName() {
        return packageName + CommonConstants.HASH + name;
    }

    @Override
    public int getOrderNo() {
        return 0;
    }

    @Override
    public QuotaProfileDetail getAllServiceQuotaProfileDetail() {
        return null;
    }

    @Override
    public Map<String, QuotaProfileDetail> getServiceToQuotaProfileDetail() {
        return null;
    }

    public String toString() {
        return toString(QOS_PROFILE_DETAIL_DATA_TO_STRING_STYLE);
    }

    public String toString(ToStringStyle toStringStyle) {

        ToStringBuilder toStringBuilder = new ToStringBuilder(this,
                toStringStyle).append("Level", "HSQ")
                .append("Action", getAction());

        if (sessionQoS != null) {
            sessionQoS.toString(toStringBuilder);
        }
        toStringBuilder.append("Redirect URL", redirectURL);
        if(getAction() == QoSProfileAction.ACCEPT) {
            toStringBuilder.append("");
            if (Collectionz.isNullOrEmpty(pccRules) == false) {
                for (PCCRule pccRule : pccRules) {
                    toStringBuilder.append("PCC Rule", pccRule);
                }
            }
        }

        toStringBuilder.append("");
        if (Collectionz.isNullOrEmpty(chargingRuleBaseNames) == false) {
            for (ChargingRuleBaseName chargingRuleBaseName : chargingRuleBaseNames) {
                toStringBuilder.append("Charging Rule Base Name", chargingRuleBaseName);
            }
        }

        return toStringBuilder.toString();
    }

    public DataRateCard getDataRateCard() {
        return dataRateCard;
    }
}
