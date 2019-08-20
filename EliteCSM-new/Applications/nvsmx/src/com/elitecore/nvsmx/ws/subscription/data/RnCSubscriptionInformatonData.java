package com.elitecore.nvsmx.ws.subscription.data;

import com.elitecore.corenetvertex.constants.SubscriptionState;

import javax.xml.bind.annotation.XmlType;
import java.util.List;

@XmlType(propOrder={"packageId", "packageName","packageType","productOfferId","productOfferName","packageDescription","addonSubscriptionId","startTime","endTime","addOnStatus","quotaprofileBalance"})
public class RnCSubscriptionInformatonData {
    private String addonSubscriptionId;
    private String packageName;
    private String packageId;
    private String productOfferId;
    private String productOfferName;
    private String packageDescription;
    private String packageType;
    private Long startTime;
    private Long endTime;
    private SubscriptionState addOnStatus;

    private List<RnCQuotaProfileBalance> quotaprofileBalance;

    public String getAddonSubscriptionId() {
        return addonSubscriptionId;
    }

    public void setAddonSubscriptionId(String addonSubscriptionId) {
        this.addonSubscriptionId = addonSubscriptionId;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getPackageId() {
        return packageId;
    }

    public void setPackageId(String packageId) {
        this.packageId = packageId;
    }

    public String getPackageDescription() {
        return packageDescription;
    }

    public void setPackageDescription(String packageDescription) {
        this.packageDescription = packageDescription;
    }

    public String getPackageType() {
        return packageType;
    }

    public void setPackageType(String packageType) {
        this.packageType = packageType;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public SubscriptionState getAddOnStatus() {
        return addOnStatus;
    }

    public void setAddOnStatus(SubscriptionState addOnStatus) {
        this.addOnStatus = addOnStatus;
    }

    public List<RnCQuotaProfileBalance> getQuotaprofileBalance() {
        return quotaprofileBalance;
    }

    public void setQuotaprofileBalance(List<RnCQuotaProfileBalance> quotaprofileBalance) {
        this.quotaprofileBalance = quotaprofileBalance;
    }

    public String getProductOfferId() {
        return productOfferId;
    }

    public void setProductOfferId(String productOfferId) {
        this.productOfferId = productOfferId;
    }

    public String getProductOfferName() {
        return productOfferName;
    }

    public void setProductOfferName(String productOfferName) {
        this.productOfferName = productOfferName;
    }
}
