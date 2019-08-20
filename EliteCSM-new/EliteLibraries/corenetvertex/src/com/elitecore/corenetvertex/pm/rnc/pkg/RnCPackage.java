package com.elitecore.corenetvertex.pm.rnc.pkg;

import com.elitecore.acesstime.AccessTimePolicy;
import com.elitecore.acesstime.TimePeriod;
import com.elitecore.acesstime.TimeSlot;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.pkg.ChargingType;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pkg.RnCPkgType;
import com.elitecore.corenetvertex.pm.rnc.notification.ThresholdNotificationScheme;
import com.elitecore.corenetvertex.pm.rnc.rcgroup.RateCardGroup;
import com.elitecore.corenetvertex.pm.util.TimeSlotUtil;
import com.elitecore.corenetvertex.util.IndentingToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RnCPackage implements Serializable, ToStringable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private String description;
    private List<String> groupIds;
    private List<RateCardGroup> rateCardGroups;
    private String failReason;
    private String partialFailReason;
    private PkgMode packageMode;
    private PkgStatus pkgStatus;
    private PolicyStatus policyStatus;
    private String tag;
    private RnCPkgType pkgType;
    private List<TimePeriod> timeSlots;
    private ThresholdNotificationScheme thresholdNotificationScheme;
    private ChargingType chargingType;
    private String currency;

    public RnCPackage(String id, String name, String description,
                      List<String> groupIds, List<RateCardGroup> rateCardGroups,ThresholdNotificationScheme thresholdNotificationSchemes,
                      String tag, RnCPkgType pkgType,
                      PkgMode packageMode, PkgStatus pkgStatus, PolicyStatus policyStatus,
                      String failReason, String partialFailReason, ChargingType chargingType,String currency) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.groupIds = groupIds;
        this.rateCardGroups = rateCardGroups;
        this.thresholdNotificationScheme = thresholdNotificationSchemes;
        this.packageMode = packageMode;
        this.pkgStatus = pkgStatus;
        this.policyStatus = policyStatus;
        this.failReason = failReason;
        this.partialFailReason = partialFailReason;
        this.tag = tag;
        this.pkgType = pkgType;
        this.chargingType = chargingType;
        this.currency=currency;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCurrency() { return currency; }

    public String getDescription() {
        return description;
    }

    public List<String> getGroupIds() {
        return groupIds;
    }

    public List<RateCardGroup> getRateCardGroups() {
        return rateCardGroups;
    }

    public String getFailReason() {
        return failReason;
    }

    public String getPartialFailReason() {
        return partialFailReason;
    }

    public PkgMode getPackageMode() {
        return packageMode;
    }

    public PkgStatus getPkgStatus() {
        return pkgStatus;
    }

    public PolicyStatus getPolicyStatus() {
        return policyStatus;
    }

    public String getTag() {
        return tag;
    }

    public RnCPkgType getPkgType() {
        return pkgType;
    }

    public ChargingType getChargingType() { return chargingType; }

    public void setFailReason(String failReason) {
        this.failReason = failReason;
    }

    public void setPartialFailReason(String partialFailReason) {
        this.partialFailReason = partialFailReason;
    }

    public void setPolicyStatus(PolicyStatus policyStatus) {
        this.policyStatus = policyStatus;
    }

    public long getNextSessionTimeOut(TimePeriod currTimePeriod) {
        return TimeSlotUtil.getNextSessionTimeOut(timeSlots, currTimePeriod);
    }



    public void init() {
        List<TimePeriod> timePeriods = new ArrayList<>();

        if(policyStatus == PolicyStatus.FAILURE){
            return;
        }

        for(RateCardGroup rateCardGroup : getRateCardGroups()) {
            AccessTimePolicy accessTimePolicy = rateCardGroup.getAccessTimePolicy();
            if(accessTimePolicy != null) {
                for(TimeSlot timSlot : accessTimePolicy.getListTimeSlot()){
                    for(TimePeriod period : timSlot.getTimePeriods()) {
                        timePeriods.add(period);
                    }
                }
            }
        }

        this.timeSlots = TimeSlotUtil.createTimeSlots(timePeriods);
    }

    public List<TimePeriod> getTimeSlots() {
        return timeSlots;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj){
        if (obj == null) {
            return false;
        }
        if (!RnCPackage.class.isAssignableFrom(obj.getClass())) {
            return false;
        }
        final RnCPackage other = (RnCPackage) obj;
        return (this.id == null) ? (other.getId() == null) : this.id.equals(other.id);
    }

    public String toString(){
        IndentingToStringBuilder builder = new IndentingToStringBuilder();
        toString(builder);
        return builder.toString();
    }

    @Override
    public void toString(IndentingToStringBuilder builder){
        builder.append("Name", name);
        builder.append("Description", description);

        if (Collectionz.isNullOrEmpty(groupIds) == false) {
            builder.append("Group Ids:",groupIds.toString());
        }

        builder.append("Mode", packageMode.name());
        builder.append("Status", pkgStatus.name());
        builder.append("Policy Status", policyStatus.name());
        builder.append("Tag", tag);
        builder.append("Type", pkgType.getVal());
        builder.append("Charging Type", chargingType.name());
        builder.append("Currency", currency);

        builder.newline();
        builder.appendChildObject("Rate Card Groups",rateCardGroups);

        if(PolicyStatus.FAILURE == policyStatus){
            builder.append("Fail Reason", failReason);
        } else if (PolicyStatus.PARTIAL_SUCCESS == policyStatus) {
            builder.append("Partial Fail Reason", partialFailReason);
        }
    }

    public ThresholdNotificationScheme getThresholdNotificationScheme() {
        return thresholdNotificationScheme;
    }

}
