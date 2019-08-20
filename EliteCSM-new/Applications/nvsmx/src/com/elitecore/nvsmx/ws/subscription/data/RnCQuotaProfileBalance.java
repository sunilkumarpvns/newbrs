package com.elitecore.nvsmx.ws.subscription.data;

import javax.xml.bind.annotation.XmlType;
import java.util.List;

@XmlType(propOrder={"quotaProfileId", "quotaProfileName", "volumeUnitType", "quotaUsageType","balanceLevel","proration","availableHSQVolumeBalance","availableHSQTimeBalance","dataServiceTypeBalance"})
public class RnCQuotaProfileBalance {
    private String quotaProfileId;
    private String quotaProfileName;
    private String volumeUnitType;
    private String quotaUsageType;
    private String balanceLevel;
    private long availableHSQVolumeBalance;
    private long availableHSQTimeBalance;
    private Boolean proration;

    private List<DataServiceTypeBalance> dataServiceTypeBalance;

    public String getQuotaProfileId() {
        return quotaProfileId;
    }

    public void setQuotaProfileId(String quotaProfileId) {
        this.quotaProfileId = quotaProfileId;
    }

    public String getQuotaProfileName() {
        return quotaProfileName;
    }

    public void setQuotaProfileName(String quotaProfileName) {
        this.quotaProfileName = quotaProfileName;
    }

    public String getVolumeUnitType() {
        return volumeUnitType;
    }

    public void setVolumeUnitType(String volumeUnitType) {
        this.volumeUnitType = volumeUnitType;
    }

    public String getQuotaUsageType() {
        return quotaUsageType;
    }

    public void setQuotaUsageType(String quotaUsageType) {
        this.quotaUsageType = quotaUsageType;
    }

    public List<DataServiceTypeBalance> getDataServiceTypeBalance() {
        return dataServiceTypeBalance;
    }

    public void setDataServiceTypeBalance(List<DataServiceTypeBalance> balanceInfos) {
        this.dataServiceTypeBalance = balanceInfos;
    }

    public String getBalanceLevel() {
        return balanceLevel;
    }

    public void setBalanceLevel(String balanceLevel) {
        this.balanceLevel = balanceLevel;
    }

    public long getAvailableHSQVolumeBalance() {
        return availableHSQVolumeBalance;
    }

    public void setAvailableHSQVolumeBalance(long availableHSQVolumeBalance) {
        this.availableHSQVolumeBalance = availableHSQVolumeBalance;
    }

    public long getAvailableHSQTimeBalance() {
        return availableHSQTimeBalance;
    }

    public void setAvailableHSQTimeBalance(long availableHSQTimeBalance) {
        this.availableHSQTimeBalance = availableHSQTimeBalance;
    }

    public Boolean getProration() {
        return proration;
    }

    public void setProration(Boolean proration) {
        this.proration = proration;
    }
}
