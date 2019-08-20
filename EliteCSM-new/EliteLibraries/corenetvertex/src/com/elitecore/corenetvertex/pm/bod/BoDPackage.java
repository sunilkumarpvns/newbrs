package com.elitecore.corenetvertex.pm.bod;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Maps;
import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.constants.ValidityPeriodUnit;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.util.ToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringStyle;
import org.apache.commons.lang.SystemUtils;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

public class BoDPackage implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private String description;
    private PkgMode packageMode;
    private PkgStatus pkgStatus;
    private Integer validityPeriod;
    private ValidityPeriodUnit validityPeriodUnit;
    private List<String> applicableQosProfiles;
    private Map<Integer, BoDQosMultiplier> fupLevelToBoDQosMultipliers;
    private List<String> groupIds;
    private String failReason;
    private PolicyStatus policyStatus;
    private Double price;
    private Timestamp availabilityStartDate;
    private Timestamp availabilityEndDate;
    private String param1;
    private String param2;

    public BoDPackage(String id, String name, String description, PkgMode packageMode, PkgStatus pkgStatus,
                      Integer validityPeriod, ValidityPeriodUnit validityPeriodUnit, List<String> applicableQosProfiles,
                      Map<Integer, BoDQosMultiplier> fupLevelToBoDQosMultipliers, List<String> groupIds,
                      String failReason, PolicyStatus policyStatus, Double price, Timestamp availabilityStartDate,
                      Timestamp availabilityEndDate, String param1, String param2) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.packageMode = packageMode;
        this.pkgStatus = pkgStatus;
        this.validityPeriod = validityPeriod;
        this.validityPeriodUnit = validityPeriodUnit;
        this.applicableQosProfiles = applicableQosProfiles;
        this.fupLevelToBoDQosMultipliers = fupLevelToBoDQosMultipliers;
        this.groupIds = groupIds;
        this.failReason = failReason;
        this.policyStatus = policyStatus;
        this.price = price;
        this.availabilityStartDate = availabilityStartDate;
        this.availabilityEndDate = availabilityEndDate;
        this.param1 = param1;
        this.param2 = param2;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public PkgMode getPackageMode() {
        return packageMode;
    }

    public PkgStatus getPkgStatus() {
        return pkgStatus;
    }

    public Integer getValidityPeriod() {
        return validityPeriod;
    }

    public ValidityPeriodUnit getValidityPeriodUnit() {
        return validityPeriodUnit;
    }

    public List<String> getApplicableQosProfiles() {
        return applicableQosProfiles;
    }

    public Map<Integer, BoDQosMultiplier> getFupLevelToBoDQosMultipliers() {
        return fupLevelToBoDQosMultipliers;
    }

    public List<String> getGroupIds() {
        return groupIds;
    }

    public String getFailReason() {
        return failReason;
    }

    public PolicyStatus getPolicyStatus() {
        return policyStatus;
    }

    public void setFailReason(String failReason) {
        this.failReason = failReason;
    }

    public void setPolicyStatus(PolicyStatus policyStatus) {
        this.policyStatus = policyStatus;
    }

    public Double getPrice() { return price; }

    public void setPrice(Double price) { this.price = price; }

    public Timestamp getAvailabilityStartDate() { return availabilityStartDate;    }

    public void setAvailabilityStartDate(Timestamp availabilityStartDate) { this.availabilityStartDate = availabilityStartDate; }

    public Timestamp getAvailabilityEndDate() { return availabilityEndDate; }

    public void setAvailabilityEndDate(Timestamp availabilityEndDate) { this.availabilityEndDate = availabilityEndDate; }

    public String getParam1() { return param1; }

    public void setParam1(String param1) { this.param1 = param1; }

    public String getParam2() { return param2; }

    public void setParam2(String param2) { this.param2 = param2; }


    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj){
        if (obj == null) {
            return false;
        }
        if (!BoDPackage.class.isAssignableFrom(obj.getClass())) {
            return false;
        }
        final BoDPackage other = (BoDPackage) obj;
        return (this.id == null) ? (other.getId() == null) : this.id.equals(other.id);
    }

    @Override
    public String toString(){
        ToStringBuilder builder = new ToStringBuilder(this,
                ToStringStyle.CUSTOM_TO_STRING_STYLE);
        builder.append("Id", id);
        builder.append("Name", name);
        builder.append("Description", description);
        builder.append("Package Mode", packageMode);
        builder.append("Package Status", pkgStatus);
        builder.append("Validity Period", validityPeriod);
        builder.append("Validity Period Unit", validityPeriodUnit);
        builder.append("Price", price);
        builder.append("Availability Start Date", availabilityStartDate);
        builder.append("Availability End Date", availabilityEndDate);
        builder.append("Param 1",param1);
        builder.append("Param 2",param2);

        builder.append(SystemUtils.LINE_SEPARATOR);
        builder.append("Applicable Qos:");
        if (!Collectionz.isNullOrEmpty(applicableQosProfiles)) {
            for (String qosName : applicableQosProfiles) {
                builder.append("\t"+qosName);
            }
        }
        else{
            builder.append("\tApplicable to all Base QoS.");
        }

        builder.append(SystemUtils.LINE_SEPARATOR);
        builder.append("BoD QoS Multipliers:");

        if(Maps.isNullOrEmpty(fupLevelToBoDQosMultipliers)){
            builder.append("\tNo BoD QoS Multiplier found.");
        } else {
            for(Map.Entry<Integer, BoDQosMultiplier> entry : this.fupLevelToBoDQosMultipliers.entrySet()){
                builder.append("\tFUP Level", entry.getKey());
                builder.append(entry.getValue().toString());
            }
        }

        if (!Collectionz.isNullOrEmpty(groupIds)) {
            builder.append("Group Ids",groupIds.toString());
        }

        builder.append("Policy Status", getPolicyStatus());

        if(PolicyStatus.FAILURE == policyStatus){
            builder.append("Fail Reason", failReason);
        }

        return builder.toString();
    }
}