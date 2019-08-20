package com.elitecore.corenetvertex.pd.topup;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.constants.CommonStatusValues;
import com.elitecore.corenetvertex.constants.FieldValueConstants;
import com.elitecore.corenetvertex.constants.ValidityPeriodUnit;
import com.elitecore.corenetvertex.pd.notification.TopUpNotificationData;
import com.elitecore.corenetvertex.sm.ResourceData;
import com.elitecore.corenetvertex.util.commons.jaxb.adapter.DoubleToStringAdapter;
import com.elitecore.corenetvertex.util.commons.jaxb.adapter.TimestampToStringAdapter;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonProperty;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;


@Entity(name="com.elitecore.corenetvertex.pd.topup.DataTopUpData")
@Table(name="TBLM_DATA_TOP_UP")
public class DataTopUpData extends ResourceData {
    @SerializedName(FieldValueConstants.NAME)private String name;
    @SerializedName(FieldValueConstants.DESCRIPTION)private String description;
    @SerializedName(FieldValueConstants.MODE)private String packageMode;

    @SerializedName(FieldValueConstants.PRICE) @XmlJavaTypeAdapter(DoubleToStringAdapter.class) private Double price;
    @SerializedName(FieldValueConstants.VALIDITIY_PERIOD)private Integer validityPeriod;
    @SerializedName(FieldValueConstants.VALIDITY_PERIOD_UNIT)private ValidityPeriodUnit validityPeriodUnit = ValidityPeriodUnit.DAY;
    @SerializedName(FieldValueConstants.MULTIPLE_SUBSCRIPTION)private Boolean multipleSubscription = CommonStatusValues.ENABLE.isBooleanValue();

    @XmlJavaTypeAdapter(TimestampToStringAdapter.class) private Timestamp availabilityStartDate;
    @XmlJavaTypeAdapter(TimestampToStringAdapter.class) private Timestamp availabilityEndDate;
    @SerializedName(FieldValueConstants.PARAM1) private String param1;
    @SerializedName(FieldValueConstants.PARAM2)private String param2;


    private String topupType;
    private String quotaType;
    private String unitType;
    private Long volumeBalance;
    private String volumeBalanceUnit;
    private Long timeBalance;
    private String timeBalanceUnit;
    private transient List<TopUpNotificationData> topUpNotificationList;
    private String applicablePCCProfiles;

    public DataTopUpData() {
        this.topUpNotificationList = new ArrayList<>();
    }

    @Column(name="NAME")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name="DESCRIPTION")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    @Column(name="PACKAGE_MODE")
    public String getPackageMode() {
        return packageMode;
    }

    public void setPackageMode(String packageMode) {
        this.packageMode = packageMode;
    }

    @Column(name="PRICE")
    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    @Column(name="VALIDITY_PERIOD")
    public Integer getValidityPeriod() {
        return validityPeriod;
    }

    public void setValidityPeriod(Integer validityPeriod) {
        this.validityPeriod = validityPeriod;
    }

    @Column(name="VALIDITY_PERIOD_UNIT")
    public ValidityPeriodUnit getValidityPeriodUnit() {
        return validityPeriodUnit;
    }

    public void setValidityPeriodUnit(ValidityPeriodUnit validityPeriodUnit) {
        this.validityPeriodUnit = validityPeriodUnit;
    }

    @Column(name="MULTIPLE_SUBSCRIPTION")
    public Boolean getMultipleSubscription() {
        return multipleSubscription;
    }

    public void setMultipleSubscription(Boolean multipleSubscription) {
        this.multipleSubscription = multipleSubscription;
    }

    @Column(name="AVAILABILITY_START_DATE")
    @JsonIgnore
    public Timestamp getAvailabilityStartDate() {
        return availabilityStartDate;
    }

    @JsonProperty
    public void setAvailabilityStartDate(Timestamp availabilityStartDate) {
        this.availabilityStartDate = availabilityStartDate;
    }

    @Column(name="AVAILABILITY_END_DATE")
    @JsonIgnore
    public Timestamp getAvailabilityEndDate() {
        return availabilityEndDate;
    }

    @JsonProperty
    public void setAvailabilityEndDate(Timestamp availabilityEndDate) {
        this.availabilityEndDate = availabilityEndDate;
    }

    @Column(name="TOP_UP_TYPE",updatable = false)
    public String getTopupType() {
        return topupType;
    }

    public void setTopupType(String topupType) {
        this.topupType = topupType;
    }

    @Column(name="QUOTA_TYPE")
    public String getQuotaType() {
        return quotaType;
    }

    public void setQuotaType(String quotaType) {
        this.quotaType = quotaType;
    }

    @Column(name="UNIT_TYPE")
    public String getUnitType() {
        return unitType;
    }

    public void setUnitType(String unitType) {
        this.unitType = unitType;
    }

    @Column(name="VOLUME_BALANCE")
    public Long getVolumeBalance() {
        return volumeBalance;
    }

    public void setVolumeBalance(Long volumeBalance) {
        this.volumeBalance = volumeBalance;
    }

    @Column(name="VOLUME_BALANCE_UNIT")
    public String getVolumeBalanceUnit() {
        return volumeBalanceUnit;
    }

    public void setVolumeBalanceUnit(String volumeBalanceUnit) {
        this.volumeBalanceUnit = volumeBalanceUnit;
    }

    @Column(name="TIME_BALANCE")
    public Long getTimeBalance() {
        return timeBalance;
    }

    public void setTimeBalance(Long timeBalance) {
        this.timeBalance = timeBalance;
    }

    @Column(name="TIME_BALANCE_UNIT")
    public String getTimeBalanceUnit() {
        return timeBalanceUnit;
    }

    public void setTimeBalanceUnit(String timeBalanceUnit) {
        this.timeBalanceUnit = timeBalanceUnit;
    }

    @Column(name="PARAM_1")
    public String getParam1() {
        return param1;
    }

    public void setParam1(String param1) {
        this.param1 = param1;
    }

    @Column(name="PARAM_2")
    public String getParam2() {
        return param2;
    }

    public void setParam2(String param2) {
        this.param2 = param2;
    }

    @Override
    @Column(name="GROUPS")
    public String getGroups() {
        return super.getGroups();
    }

    @Override
    @Transient
    public String getResourceName() {
        return getName();
    }


    @Override
    @Column(name = "STATUS")
    @XmlElement(name="status")
    public String getStatus() {
        return super.getStatus();
    }


    @OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.LAZY, mappedBy = "dataTopUpData", orphanRemoval = true )
    @Fetch(FetchMode.SUBSELECT)
    public List<TopUpNotificationData> getTopUpNotificationList() {
        return topUpNotificationList;
    }

    public void setTopUpNotificationList(List<TopUpNotificationData> topUpNotificationList) {
        this.topUpNotificationList = topUpNotificationList;
    }

    @Column(name="APPLICABLE_PCC_PROFILES")
    public String getApplicablePCCProfiles() {
        return applicablePCCProfiles;
    }

    public void setApplicablePCCProfiles(String applicablePCCProfiles) {
        this.applicablePCCProfiles = applicablePCCProfiles;
    }

    @Override
    public JsonObject toJson() {
        JsonObject dataTopUp = new JsonObject();
        dataTopUp.addProperty("Name",name);
        dataTopUp.addProperty("Description",description);
        dataTopUp.addProperty("Mode",packageMode);
        dataTopUp.addProperty("TopUp Type",topupType);
        dataTopUp.addProperty("Quota Type ",quotaType);
        dataTopUp.addProperty("Unit Type",unitType);
        dataTopUp.addProperty("Validity Period",validityPeriod);
        dataTopUp.addProperty("Validity Period Unit",validityPeriodUnit.displayValue);
        dataTopUp.addProperty("Price",price);
        dataTopUp.addProperty(FieldValueConstants.AVAILABILITY_START_DATE,availabilityStartDate == null ? "": availabilityStartDate.toLocalDateTime().toString());
        dataTopUp.addProperty(FieldValueConstants.AVAILABILITY_END_DATE,availabilityEndDate == null ? "":availabilityEndDate.toLocalDateTime().toString());
        dataTopUp.addProperty("Applicable PCC Profiles",applicablePCCProfiles);
        dataTopUp.addProperty("Groups",getGroups());
        dataTopUp.addProperty("Status",getStatus());
        dataTopUp.addProperty("Param1",param1);
        dataTopUp.addProperty("Param2",param2);
        dataTopUp.addProperty("Multiple Subscription",getMultipleSubscription());
        dataTopUp.addProperty("Unit Type", getUnitType());
        if (TopUpQuotaType.valueOf(getQuotaType()).equals(TopUpQuotaType.VOLUME)) {
            dataTopUp.addProperty("Volume Balance", getVolumeBalance() + " " + getVolumeBalanceUnit());
        } else {
            dataTopUp.addProperty("Time Balance", getTimeBalance() + " " + getTimeBalanceUnit());
        }

        if(Collectionz.isNullOrEmpty(topUpNotificationList) == false){
            JsonArray jsonArray = new JsonArray();
            for(TopUpNotificationData topUpNotificationData : topUpNotificationList){
                jsonArray.add(topUpNotificationData.toJson());
            }
            dataTopUp.add("Top-Up Notifications ", jsonArray);
        }
        return dataTopUp;
    }
}
