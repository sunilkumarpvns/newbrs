package com.elitecore.corenetvertex.pm.pkg;

import com.elitecore.corenetvertex.constants.QoSProfileAction;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfileDetail;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.UMBaseQuotaProfileDetail;

import javax.annotation.Nullable;
import java.util.Map;

public class UMQuotaProfileConf {
    private final String qosProfileName;
    private final String packageName;
    private final QoSProfileAction qosProfileAction;
    private final String reason;
    private final UMBaseQuotaProfileDetail allServiceQuotaProfileDetail;
    private final Map<String, QuotaProfileDetail> serviceToUMQuotaProfileDetail;
    private final boolean isUsageRequired;
    private final int fupLevel;
    private final Integer orderNo;
    private final boolean isApplicableOnUsageUnavailability;
    @Nullable
    private String redirectURL;

    public UMQuotaProfileConf(String qosProfileName, String packageName, QoSProfileAction qosProfileAction, String reason, UMBaseQuotaProfileDetail allServiceQuotaProfileDetail, Map<String, QuotaProfileDetail> serviceToUMQuotaProfileDetail, boolean isUsageRequired, int fupLevel, Integer orderNo, boolean isApplicableOnUsageUnavailability, @Nullable String redirectURL) {
        this.qosProfileName = qosProfileName;
        this.packageName = packageName;
        this.qosProfileAction = qosProfileAction;
        this.reason = reason;
        this.allServiceQuotaProfileDetail = allServiceQuotaProfileDetail;
        this.serviceToUMQuotaProfileDetail = serviceToUMQuotaProfileDetail;
        this.isUsageRequired = isUsageRequired;
        this.fupLevel = fupLevel;
        this.orderNo = orderNo;
        this.isApplicableOnUsageUnavailability = isApplicableOnUsageUnavailability;
        this.redirectURL = redirectURL;
    }

    public String getQosProfileName() {
        return qosProfileName;
    }

    public String getPackageName() {
        return packageName;
    }

    public QoSProfileAction getQosProfileAction() {
        return qosProfileAction;
    }

    public String getReason() {
        return reason;
    }

    public UMBaseQuotaProfileDetail getAllServiceQuotaProfileDetail() {
        return allServiceQuotaProfileDetail;
    }

    public Map<String, QuotaProfileDetail> getServiceToUMQuotaProfileDetail() {
        return serviceToUMQuotaProfileDetail;
    }

    public boolean isUsageRequired() {
        return isUsageRequired;
    }

    public int getFupLevel() {
        return fupLevel;
    }

    public Integer getOrderNo() {
        return orderNo;
    }

    public boolean isApplicableOnUsageUnavailability() {
        return isApplicableOnUsageUnavailability;
    }

    public String getRedirectURL() {
        return redirectURL;
    }

    public void setRedirectURL(String redirectURL) {
        this.redirectURL = redirectURL;
    }
}
