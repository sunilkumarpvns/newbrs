package com.elitecore.corenetvertex.pm.pkg.datapackage;

import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.constants.QuotaProfileType;
import com.elitecore.corenetvertex.constants.ValidityPeriodUnit;
import com.elitecore.corenetvertex.pd.topup.TopUpType;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfile;
import com.elitecore.corenetvertex.pm.pkg.notification.QuotaNotificationScheme;
import com.elitecore.corenetvertex.util.ToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringStyle;

import javax.annotation.Nullable;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

public class QuotaTopUp implements Serializable {

    private final String name;
    private final String id;
    private static final QuotaProfileType quotaProfileType = QuotaProfileType.RnC_BASED;
    private final PkgStatus availabilityStatus;
    private String description;
    private PkgMode mode;
    private Timestamp availabilityStartDate;
    private Timestamp availabilityEndDate;
    private Double price;
    private String failReason;
    private String partialFailReason;
    private PolicyStatus status = PolicyStatus.SUCCESS;
    private List<String> groupIds;
    private String param1;
    private String param2;
    private QuotaNotificationScheme quotaNotificationScheme;

    private final ValidityPeriodUnit validityPeriodUnit;
    private final int validityPeriod;
    private boolean multipleSubscription;
    private QuotaProfile quotaProfile;

    private TopUpType topUpType;
    private String quotaType;
    private String unitType;
    private Long volumeBalance;
    private String volumeBalanceUnit;
    private Long timeBalance;
    private String timeBalanceUnit;

    private List<String> applicablePCCProfiles;

    public QuotaTopUp(String id,
                      String name,
                      boolean multipleSubscription,
                      int validityPeriod,
                      ValidityPeriodUnit validityPeriodUnit,
                      PkgMode mode,
                      TopUpType topUpType,
                      String description,
                      Double price,
                      Timestamp availabilityStartDate,
                      Timestamp availabilityEndDate,
                      PkgStatus availabilityStatus,
                      List<String> groupIds,
                      QuotaProfile quotaProfile,
                      String failReason,
                      String partialFailReason,
                      PolicyStatus policyStatus, @Nullable String param1,
                      @Nullable String param2,
                      QuotaNotificationScheme quotaNotificationScheme,
                      String quotaType,
                      String unitType,
                      Long volumeBalance,
                      String volumeBalanceUnit,
                      Long timeBalance,
                      String timeBalanceUnit,
					  List<String> applicablePCCProfiles) {
        this.id = id;
        this.name = name;
        this.mode = mode;
        this.topUpType = topUpType;
        this.description = description;
        this.price = price;
        this.availabilityStartDate = availabilityStartDate;
        this.availabilityEndDate = availabilityEndDate;
        this.availabilityStatus = availabilityStatus;
        this.groupIds = groupIds;
        this.failReason = failReason;
        this.partialFailReason = partialFailReason;
        this.multipleSubscription = multipleSubscription;
        this.validityPeriod = validityPeriod;
        this.validityPeriodUnit = validityPeriodUnit;
        this.quotaProfile = quotaProfile;
        this.status = policyStatus;
        this.param1 = param1;
        this.param2 = param2;
        this.quotaNotificationScheme = quotaNotificationScheme;

        this.quotaType = quotaType;
        this.unitType = unitType;
        this.volumeBalance = volumeBalance;
        this.volumeBalanceUnit = volumeBalanceUnit;
        this.timeBalance = timeBalance;
        this.timeBalanceUnit = timeBalanceUnit;
        this.applicablePCCProfiles = applicablePCCProfiles;
    }

    public ValidityPeriodUnit getValidityPeriodUnit() {
        return validityPeriodUnit;
    }

    public int getValidity() {
        return validityPeriod;
    }

    public boolean isMultipleSubscription() {
        return multipleSubscription;
    }

    public String getName() {
        return name;
    }

    public QuotaProfile getQuotaProfile() {
        return quotaProfile;
    }

    public List<String> getGroupIds() {
        return groupIds;
    }

    public String getParam1() {
        return param1;
    }

    public String getParam2() {
        return param2;
    }

    public PkgMode getMode() {
        return mode;
    }

    public void setPolicyStatus(PolicyStatus status) {
        this.status = status;
    }

    public void setPartialFailReason(String partialFailReason) {
        this.partialFailReason = partialFailReason;
    }

    public void setFailReason(String failReason) {
        this.failReason = failReason;
    }

    public String getDescription() {
        return description;
    }

    public Double getPrice() {
        return price;
    }

    public Timestamp getAvailabilityStartDate() {
        return availabilityStartDate;
    }

    public Timestamp getAvailabilityEndDate() {
        return availabilityEndDate;
    }

    public QuotaProfile getQuotaProfile(String id) {

        if (quotaProfile.getId().equalsIgnoreCase(id)) {
            return quotaProfile;
        }

        return null;
    }

    public QuotaProfileType getQuotaProfileType() {
        return quotaProfileType;
    }

    public String getId() {
        return id;
    }

    public PolicyStatus getStatus() {
        return status;
    }

    public String getFailReason() {
        return failReason;
    }

    public String getPartialFailReason() {
        return partialFailReason;
    }

    public PkgStatus getAvailabilityStatus() {
        return availabilityStatus;
    }

    public TopUpType getPackageType() {
        return topUpType;
    }

    public @Nullable QuotaNotificationScheme getQuotaNotificationScheme() {
        return quotaNotificationScheme;
    }

    public String getUnitType() {
        return unitType;
    }

    public void setUnitType(String unitType) {
        this.unitType = unitType;
    }

    public Long getVolumeBalance() {
        return volumeBalance;
    }

    public void setVolumeBalance(Long volumeBalance) {
        this.volumeBalance = volumeBalance;
    }

    public String getVolumeBalanceUnit() {
        return volumeBalanceUnit;
    }

    public void setVolumeBalanceUnit(String volumeBalanceUnit) {
        this.volumeBalanceUnit = volumeBalanceUnit;
    }

    public Long getTimeBalance() {
        return timeBalance;
    }

    public void setTimeBalance(Long timeBalance) {
        this.timeBalance = timeBalance;
    }

    public String getTimeBalanceUnit() {
        return timeBalanceUnit;
    }

    public void setTimeBalanceUnit(String timeBalanceUnit) {
        this.timeBalanceUnit = timeBalanceUnit;
    }

    public String getQuotaType() {
        return quotaType;
    }

	public List<String> getApplicablePCCProfiles() {
		return applicablePCCProfiles;
	}

	public void setQuotaType(String quotaType) {
        this.quotaType = quotaType;
    }

    @Override
    public String toString() {

        ToStringBuilder toStringBuilder = new ToStringBuilder(this,
                ToStringStyle.CUSTOM_TO_STRING_STYLE).append("Name", name)
                .append("Type", topUpType)
                .append("Quota Profile Type", quotaProfileType)
                .append("Availability Status", availabilityStatus)
                .append("Package Mode", mode)
                .append("Availability Start Date", availabilityStartDate)
                .append("Availability End Date", availabilityEndDate)
                .append("Price", price == null ? "N/A" : price)
                .append("Status", status)
                .append("Quota Type",quotaType)
                .append("Unit Type",unitType)
                .append("Volume Balance",volumeBalance)
                .append("Volume Balance Unit",volumeBalanceUnit)
                .append("Time Balance",timeBalance)
                .append("Time Balance Unit",timeBalanceUnit);


        toStringBuilder.append("\t");
        if (quotaProfile != null) {
            toStringBuilder.append("Quota Profile", quotaProfile.toString());
        }

		if (quotaProfile != null) {
			toStringBuilder.append("Applicable PCC Profiles", applicablePCCProfiles);
		}

        if (failReason != null) {
            toStringBuilder.append("Fail Reasons", failReason);
        }
        if (partialFailReason != null) {
            toStringBuilder.append("Partial Fail Reasons", partialFailReason);
        }

        return toStringBuilder.toString();

    }

	/*
	  if ApplicablePCCProfiles are not configured then allow topup to execute, It means this topup is eligible for all base packages
	  if ApplicablePCCProfiles are configured then allow only matching quota details.
	 */
	public boolean isTopUpIsEligibleToApply(String pccProfileName) {
        return applicablePCCProfiles.isEmpty() || applicablePCCProfiles.contains(pccProfileName);
	}


}