package com.elitecore.corenetvertex.pm.monetaryrechargeplan;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.constants.ValidityPeriodUnit;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.util.IndentingToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringable;

import java.math.BigDecimal;
import java.util.List;

public class MonetaryRechargePlan implements ToStringable {
    private final String id;
    private final String name;
    private final PkgStatus availabilityStatus;
    private BigDecimal price;
    private BigDecimal amount;
    private Integer validity;
    private ValidityPeriodUnit validityPeriodUnit = ValidityPeriodUnit.DAY;
    private String description;
    private PkgMode mode;
    private PolicyStatus status = PolicyStatus.SUCCESS;
    private List<String> groupIds;
    private String monetaryRechargePlanFailReason;
    private String monetaryRechargePlanPartialFailReason;

    public MonetaryRechargePlan(String id,
                                String name,
                                BigDecimal price,
                                BigDecimal amount,
                                Integer validity,
                                ValidityPeriodUnit validityPeriodUnit,
                                String description,
                                PkgMode mode,
                                PolicyStatus policyStatus,
                                List<String> groupIds,
                                PkgStatus availabilityStatus,
                                String monetaryRechargePlanFailReason,
                                String monetaryRechargePlanPartialFailReason) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.amount = amount;
        this.validity = validity;
        this.validityPeriodUnit = validityPeriodUnit;
        this.description = description;
        this.mode = mode;
        this.status = policyStatus;
        this.groupIds = groupIds;
        this.availabilityStatus = availabilityStatus;
        this.monetaryRechargePlanFailReason = monetaryRechargePlanFailReason;
        this.monetaryRechargePlanPartialFailReason = monetaryRechargePlanPartialFailReason;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public Integer getValidity() {
        return validity;
    }

    public ValidityPeriodUnit getValidityPeriodUnit() {
        return validityPeriodUnit;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public PkgMode getMode() {
        return mode;
    }

    public void setMode(PkgMode mode) {
        this.mode = mode;
    }

    public PolicyStatus getStatus() {
        return status;
    }

    public void setStatus(PolicyStatus status) {
        this.status = status;
    }

    public List<String> getGroupIds() {
        return groupIds;
    }

    public void setGroupIds(List<String> groupIds) {
        this.groupIds = groupIds;
    }

    public String getFailReason() {
        return monetaryRechargePlanFailReason;
    }

    public void setFailReason(String monetaryRechargePlanFailReason) {
        this.monetaryRechargePlanFailReason = monetaryRechargePlanFailReason;
    }

    public String getPartialFailReason() {
        return monetaryRechargePlanPartialFailReason;
    }

    public void setPartialFailReason(String monetaryRechargePlanPartialFailReason) {
        this.monetaryRechargePlanPartialFailReason = monetaryRechargePlanPartialFailReason;
    }

    public String getId() {
        return id;
    }

    public PkgStatus getAvailabilityStatus() {
        return availabilityStatus;
    }

    public String toString(){
        IndentingToStringBuilder builder = new IndentingToStringBuilder();
        toString(builder);
        return builder.toString();
    }

    @Override
    public void toString(IndentingToStringBuilder builder){
        builder.append("Name", name);
        builder.append("Price", price);
        builder.append("Amount", amount);
        builder.append("Validity Period", validity);
        builder.append("Validity Period Unit", validityPeriodUnit);
        if (Collectionz.isNullOrEmpty(groupIds) == false) {
            builder.append("Group Ids",groupIds.toString());
        }

        builder.append("Mode", mode.name());
        builder.append("Status", availabilityStatus.name());
        builder.append("Policy Status", status.name());
        builder.append("Description", description);


        if(PolicyStatus.FAILURE == status){
            builder.append("Fail Reason", monetaryRechargePlanFailReason);
        } else if (PolicyStatus.PARTIAL_SUCCESS == status) {
            builder.append("Partial Fail Reason", monetaryRechargePlanPartialFailReason);
        }
    }
}
