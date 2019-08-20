package com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Maps;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.QoSProfileAction;
import com.elitecore.corenetvertex.pm.pkg.ChargingRuleBaseName;
import com.elitecore.corenetvertex.pm.pkg.PCCRule;
import com.elitecore.corenetvertex.pm.pkg.SliceInformation;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.IPCANQoS;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.QoSProfileDetail;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfileDetail;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.RncProfileDetail;
import com.elitecore.corenetvertex.util.ToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringStyle;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

public class RnCBaseQoSProfileDetail implements QoSProfileDetail {

    @Nullable
    private RncProfileDetail allServiceQuotaProfileDetail;
    @Nullable private Map<String, QuotaProfileDetail> serviceToQuotaProfileDetail;
    @Nullable private List<PCCRule> pccRules = null;
    @Nullable private List<ChargingRuleBaseName> chargingRuleBaseNames = null;
    @Nonnull private String name;
    @Nonnull private String packageName;
    @Nonnull private QoSProfileAction action = QoSProfileAction.ACCEPT;
    @Nonnull private String rejectCause = null;
    private IPCANQoS sessionQoS;
    @Nonnull private int fupLevel;
    @Nonnull private int orderNo;
    @Nullable private String redirectURL;
    @Nullable private String quotaProfileName = null;
    @Nullable private String quotaProfileId = null;

    public RnCBaseQoSProfileDetail(String name,
                                   String packageName,
                                   QoSProfileAction action,
                                   String reason,
                                   @Nullable RncProfileDetail allServiceQuotaProfileDetail,
                                   @Nullable Map<String, QuotaProfileDetail> serviceToSyQuotaProfileDetail,
                                   int fupLevel, Integer orderNo, @Nullable String redirectURL) {
        this(name, packageName, fupLevel, allServiceQuotaProfileDetail, serviceToSyQuotaProfileDetail, orderNo, redirectURL);

        this.action = action;
        this.rejectCause = reason;
        this.sessionQoS = null;
    }

    public RnCBaseQoSProfileDetail(String name,
                                   String pkgName,
                                   int fupLevel,
                                   @Nullable RncProfileDetail allServiceQuotaProfileDetail,
                                   @Nullable Map<String, QuotaProfileDetail> serviceToQuotaProfileDetail,
                                   IPCANQoS sessionQoS,
                                   List<PCCRule> pccRules,
                                   int orderNo, @Nullable String redirectURL, List<ChargingRuleBaseName> chargingRuleBaseNames) {

        this(name, pkgName, fupLevel, allServiceQuotaProfileDetail, serviceToQuotaProfileDetail, orderNo, redirectURL);

        this.sessionQoS = sessionQoS;
        this.pccRules = pccRules;
        this.chargingRuleBaseNames = chargingRuleBaseNames;
    }

    private RnCBaseQoSProfileDetail(String name,
                                    String pkgName,
                                    int fupLevel,
                                    @Nullable RncProfileDetail allServiceQuotaProfileDetail,
                                    @Nullable Map<String, QuotaProfileDetail> serviceToSyQuotaProfileDetail,
                                    int orderNo, @Nullable String redirectURL) {

        this.name = name;
        this.packageName = pkgName;
        this.fupLevel = fupLevel;
        this.allServiceQuotaProfileDetail = allServiceQuotaProfileDetail;
        this.serviceToQuotaProfileDetail = serviceToSyQuotaProfileDetail;
        this.orderNo = orderNo;
        this.redirectURL = redirectURL;

        setQuotaProfileNameAndId();

    }

    private void setQuotaProfileNameAndId() {
        if(allServiceQuotaProfileDetail != null) {
            quotaProfileName = allServiceQuotaProfileDetail.getName();
            quotaProfileId = allServiceQuotaProfileDetail.getQuotaProfileId();
        } else if(Maps.isNullOrEmpty(serviceToQuotaProfileDetail) == false){
            QuotaProfileDetail quotaProfileDetail = serviceToQuotaProfileDetail.values().stream().findAny().get();
            quotaProfileName = quotaProfileDetail.getName();
            quotaProfileId = quotaProfileDetail.getQuotaProfileId();
        }
    }

    @Override
    public RncProfileDetail getAllServiceQuotaProfileDetail() {
        return allServiceQuotaProfileDetail;
    }

    @Override
    public Map<String, QuotaProfileDetail> getServiceToQuotaProfileDetail() {
        return serviceToQuotaProfileDetail;
    }


    @Override
    public String getLevel() {
        return fupLevel == 0 ? HSQ : FUP + CommonConstants.DASH + fupLevel;
    }

    @Override
    @Nullable
    public String getRedirectURL() {
        return redirectURL;
    }

    @Override
    public @Nullable String getQuotaProfileName() {
        return quotaProfileName;
    }

    @Override
    public @Nullable String getQuotaProfileId() {
        return quotaProfileId;
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
        return fupLevel;
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
        return orderNo;
    }

    public String toString() {
        return toString(QOS_PROFILE_DETAIL_DATA_TO_STRING_STYLE);
    }

    public String toString(ToStringStyle toStringStyle) {

        ToStringBuilder toStringBuilder = new ToStringBuilder(this,
                toStringStyle).append("Level", (fupLevel == 0 ? "HSQ" : "fup level " + fupLevel))
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
}