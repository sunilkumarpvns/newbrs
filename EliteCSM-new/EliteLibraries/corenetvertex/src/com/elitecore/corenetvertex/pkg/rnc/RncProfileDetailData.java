package com.elitecore.corenetvertex.pkg.rnc;

import com.elitecore.corenetvertex.pd.revenuedetail.RevenueDetailData;
import com.elitecore.corenetvertex.pkg.dataservicetype.DataServiceTypeData;
import com.elitecore.corenetvertex.pkg.ratinggroup.RatingGroupData;
import com.elitecore.corenetvertex.util.commons.jaxb.adapter.DoubleToStringAdapter;
import com.google.gson.JsonObject;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.util.Objects;

/**
 * Rating and Charging Based Configuration Detail
 * Created by dhyani on 19/7/17.
 */
@Entity
@Table(name = "TBLM_RnC_QUOTA_PROFILE_DETAIL")
public class RncProfileDetailData implements Serializable {

    private String id;
    private Integer fupLevel;
    private Long pulseVolume;
    private String pulseVolumeUnit;
    private Long pulseTime;
    private String pulseTimeUnit;
    private DataServiceTypeData dataServiceTypeData;
    private RatingGroupData ratingGroupData;
    private transient RncProfileData rncProfileData;
    private String serviceTypeId;
    private String ratingGroupId;
    private Double rate;
    private String rateUnit;
    private String balanceUnit;
    private Long balance;
    private Long timeBalance;
    private String timeBalanceUnit;
    private Long dailyUsageLimit;
    private String usageLimitUnit;
    private Long weeklyUsageLimit;
    private Long dailyTimeLimit;
    private Long weeklyTimeLimit;
    private String timeLimitUnit;
    private Long volumeCarryForwardLimit;
    private Long timeCarryForwardLimit;
    private RevenueDetailData revenueDetail;

    public RncProfileDetailData() {
        revenueDetail = new RevenueDetailData();
    }

    @Id
    @GeneratedValue(generator = "eliteSequenceGenerator")
    @Column(name =  "ID")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Column(name =  "FUP_LEVEL")
    public Integer getFupLevel() {
        return fupLevel;
    }

    public void setFupLevel(Integer fupLevel) {
        this.fupLevel = fupLevel;
    }

    @Column(name =  "PULSE_VOLUME")
    public Long getPulseVolume() {
        return pulseVolume;
    }

    public void setPulseVolume(Long pulseVolume) {
        this.pulseVolume = pulseVolume;
    }


    @Column(name =  "PULSE_VOLUME_UNIT")
    public String getPulseVolumeUnit() {
        return pulseVolumeUnit;
    }

    public void setPulseVolumeUnit(String pulseVolumeUnit) {
        this.pulseVolumeUnit = pulseVolumeUnit;
    }

    @Column(name =  "PULSE_TIME_UNIT")
    public String getPulseTimeUnit() {
        return pulseTimeUnit;
    }

    public void setPulseTimeUnit(String pulseTimeUnit) {
        this.pulseTimeUnit = pulseTimeUnit;
    }

    @Column(name =  "PULSE_TIME")
    public Long getPulseTime() {
        return pulseTime;
    }

    public void setPulseTime(Long pulseTime) {
        this.pulseTime = pulseTime;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name =  "DATA_SERVICE_TYPE_ID")
    @XmlElement(name = "dataServiceType")
    public DataServiceTypeData getDataServiceTypeData() {
        return dataServiceTypeData;
    }

    public void setDataServiceTypeData(DataServiceTypeData dataServiceTypeData) {
        this.dataServiceTypeData = dataServiceTypeData;
    }

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name =  "RATING_GROUP_ID")
    public RatingGroupData getRatingGroupData() {
        return ratingGroupData;
    }

    public void setRatingGroupData(RatingGroupData ratingGroupData) {
        this.ratingGroupData = ratingGroupData;
    }


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name =  "QUOTA_PROFILE_ID")
    @XmlTransient
    public RncProfileData getRncProfileData() {
        return rncProfileData;
    }

    public void setRncProfileData(RncProfileData rncProfileData) {
        this.rncProfileData = rncProfileData;
    }

    @Transient
    public String getServiceTypeId() {
        return serviceTypeId;
    }

    public void setServiceTypeId(String serviceTypeId) {
        this.serviceTypeId = serviceTypeId;
    }

    @Transient
    public String getRatingGroupId() {
        return ratingGroupId;
    }

    public void setRatingGroupId(String ratingGroupId) {
        this.ratingGroupId = ratingGroupId;
    }

    @Column(name = "RATE")
    @XmlJavaTypeAdapter(DoubleToStringAdapter.class)
    public Double getRate() {
        return rate;
    }

    public void setRate(Double rateVolume) {
        this.rate = rateVolume;
    }

    @Column(name = "RATE_UNIT")
    public String getRateUnit() {
        return rateUnit;
    }

    public void setRateUnit(String rateTime) {
        this.rateUnit = rateTime;
    }

    @Column(name =  "BALANCE")
    public Long getBalance() {
        return balance;
    }

    public void setBalance(Long balance) {
        this.balance = balance;
    }

    @Column(name =  "BALANCE_UNIT")
    public String getBalanceUnit() {
        return balanceUnit;
    }

    public void setBalanceUnit(String balanceUnit) {
        this.balanceUnit = balanceUnit;
    }

    @Column(name =  "TIME_BALANCE")
    public Long getTimeBalance() {
        return timeBalance;
    }

    public void setTimeBalance(Long timeBalance) {
        this.timeBalance = timeBalance;
    }

    @Column(name =  "TIME_BALANCE_UNIT")
    public String getTimeBalanceUnit() {
        return timeBalanceUnit;
    }

    public void setTimeBalanceUnit(String timeBalanceUnit) {
        this.timeBalanceUnit = timeBalanceUnit;
    }

    @Column(name = "DAILY_USAGE_LIMIT")
    public Long getDailyUsageLimit() {
        return dailyUsageLimit;
    }

    public void setDailyUsageLimit(Long dailyTotal) {
        this.dailyUsageLimit = dailyTotal;
    }

    @Column(name = "USAGE_LIMIT_UNIT")
    public String getUsageLimitUnit() {
        return usageLimitUnit;
    }

    public void setUsageLimitUnit(String usageLimitUnit) {
        this.usageLimitUnit = usageLimitUnit;
    }

    @Column(name = "WEEKLY_USAGE_LIMIT")
    public Long getWeeklyUsageLimit() {
        return weeklyUsageLimit;
    }

    public void setWeeklyUsageLimit(Long weeklyTotal) {
        this.weeklyUsageLimit = weeklyTotal;
    }

    @Column(name = "DAILY_TIME_LIMIT")
    public Long getDailyTimeLimit() {
        return dailyTimeLimit;
    }

    public void setDailyTimeLimit(Long dailyTimeLimit) {
        this.dailyTimeLimit = dailyTimeLimit;
    }

    @Column(name = "WEEKLY_TIME_LIMIT")
    public Long getWeeklyTimeLimit() {
        return weeklyTimeLimit;
    }

    public void setWeeklyTimeLimit(Long weeklyTimeLimit) {
        this.weeklyTimeLimit = weeklyTimeLimit;
    }

    @Column(name = "TIME_LIMIT_UNIT")
    public String getTimeLimitUnit() {
        return timeLimitUnit;
    }

    public void setTimeLimitUnit(String timeLimitUnit) {
        this.timeLimitUnit = timeLimitUnit;
    }

    @Column(name = "VOLUME_CARRY_FORWARD_LIMIT")
    public Long getVolumeCarryForwardLimit() {
        return volumeCarryForwardLimit;
    }

    public void setVolumeCarryForwardLimit(Long volumeCarryForwardLimit) {
        this.volumeCarryForwardLimit = volumeCarryForwardLimit;
    }

    @Column(name = "TIME_CARRY_FORWARD_LIMIT")
    public Long getTimeCarryForwardLimit() {
        return timeCarryForwardLimit;
    }

    public void setTimeCarryForwardLimit(Long timeCarryForwardLimit) {
        this.timeCarryForwardLimit= timeCarryForwardLimit;
    }

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "REVENUE_DETAIL")
    public RevenueDetailData getRevenueDetail() {
        return revenueDetail;
    }

    public void setRevenueDetail(RevenueDetailData revenueDetail) {
        this.revenueDetail = revenueDetail;
    }

    public JsonObject toJson(){
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("Rating Group", ratingGroupData.getName());
        jsonObject.addProperty("Balance", balance);
        jsonObject.addProperty("Balance Unit", balanceUnit);
        jsonObject.addProperty("Time Balance", timeBalance);
        jsonObject.addProperty("Pulse Volume ", pulseVolume);
        jsonObject.addProperty("Pulse Volume Unit", pulseVolumeUnit);
        jsonObject.addProperty("Rate", rateUnit);
        jsonObject.addProperty("Daily Usage Limit", dailyUsageLimit);
        jsonObject.addProperty("Weekly Usage Limit", weeklyUsageLimit);
        jsonObject.addProperty("Usage Limit Unit", usageLimitUnit);
        jsonObject.addProperty("Daily Time Limit", dailyTimeLimit);
        jsonObject.addProperty("Weekly Time Limit", weeklyTimeLimit);
        jsonObject.addProperty("Time Limit Unit", timeLimitUnit);
        jsonObject.addProperty("Volume Carry Forward Limit", volumeCarryForwardLimit);
        jsonObject.addProperty("Time Carry Forward Limit", timeCarryForwardLimit);
        if(Objects.nonNull(revenueDetail)){
            jsonObject.addProperty("Revenue Detail", revenueDetail.getName());
        }
        JsonObject serviceTypeJson = new JsonObject();
        serviceTypeJson.add(dataServiceTypeData.getName(), jsonObject);
        return serviceTypeJson;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        RncProfileDetailData other = (RncProfileDetailData) obj;
        if (getId() == null) {
            if (other.getId() != null)
                return false;
        } else if (!getId().equals(other.getId()))
            return false;
        return true;
    }

    public RncProfileDetailData copyModel() {
        RncProfileDetailData newData = new RncProfileDetailData();
        newData.fupLevel = this.fupLevel;
        newData.pulseVolume = this.pulseVolume;
        newData.pulseVolumeUnit = this.pulseVolumeUnit;
        newData.pulseTime = this.pulseTime;
        newData.pulseTimeUnit = this.pulseTimeUnit;
        newData.dataServiceTypeData = this.dataServiceTypeData;
        newData.ratingGroupData = this.ratingGroupData;
        newData.serviceTypeId = this.serviceTypeId;
        newData.ratingGroupId = this.ratingGroupId;
        newData.rate = this.rate;
        newData.rateUnit = this.rateUnit;
        newData.balanceUnit = this.balanceUnit;
        newData.balance = this.balance;
        newData.timeBalance = this.timeBalance;
        newData.timeBalanceUnit = this.timeBalanceUnit;
        newData.dailyUsageLimit = this.dailyUsageLimit;
        newData.usageLimitUnit = this.usageLimitUnit;
        newData.weeklyUsageLimit = this.weeklyUsageLimit;
        newData.dailyTimeLimit = this.dailyTimeLimit;
        newData.weeklyTimeLimit = this.weeklyTimeLimit;
        newData.timeLimitUnit = this.timeLimitUnit;
        newData.volumeCarryForwardLimit = this.volumeCarryForwardLimit;
        newData.timeCarryForwardLimit = this.timeCarryForwardLimit;
        newData.revenueDetail = this.revenueDetail;
        return  newData;
    }
}

