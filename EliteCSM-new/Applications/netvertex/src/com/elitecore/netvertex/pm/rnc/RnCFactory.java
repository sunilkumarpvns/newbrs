package com.elitecore.netvertex.pm.rnc;

import com.elitecore.acesstime.AccessTimePolicy;
import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.constants.RenewalIntervalUnit;
import com.elitecore.corenetvertex.constants.Uom;
import com.elitecore.corenetvertex.pd.ratecardgroup.TimeSlotRelationData;
import com.elitecore.corenetvertex.pkg.ChargingType;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pkg.RnCPkgType;
import com.elitecore.corenetvertex.pm.rnc.notification.ThresholdEvent;
import com.elitecore.corenetvertex.pm.rnc.notification.ThresholdNotificationScheme;
import com.elitecore.corenetvertex.pm.rnc.pkg.RnCPackage;
import com.elitecore.corenetvertex.pm.rnc.ratecard.MonetaryRateCard;
import com.elitecore.corenetvertex.pm.rnc.ratecard.MonetaryRateCardVeresionDetail;
import com.elitecore.corenetvertex.pm.rnc.ratecard.MonetaryRateCardVersion;
import com.elitecore.corenetvertex.pm.rnc.ratecard.NonMonetaryRateCard;
import com.elitecore.corenetvertex.pm.rnc.ratecard.RateCard;
import com.elitecore.corenetvertex.pm.rnc.rcgroup.RateCardGroup;
import com.elitecore.exprlib.parser.expression.LogicalExpression;
import com.elitecore.netvertex.pm.rnc.ratecard.RateCardVersion;

import java.util.List;

public class RnCFactory extends com.elitecore.corenetvertex.pm.rnc.RnCFactory {

    @Override
    public MonetaryRateCardVersion createMonetaryRateCardVersion(String id, String name, List<MonetaryRateCardVeresionDetail> ratingBehaviors,
                                                                 String rncPackageId, String rncPackageName, String rateCardGroupId, String rateCardGroupName,
                                                                 String rateCardId, String rateCardName){
        return new RateCardVersion(id,name, ratingBehaviors,
                rncPackageId, rncPackageName, rateCardGroupId, rateCardGroupName,
                rateCardId, rateCardName);
    }

    @Override
    public NonMonetaryRateCard createNonMonetaryRateCard(String id, String name, String description, Uom timeUom, long time, long timeMinorUnit,
                                                         long event, Uom pulseUom, long pulse, long pulseMinorUnit, String rncPackageId, String rncPackageName,
                                                         String rateCardGroupId, String rateCardGroupName, int renewalInterval, RenewalIntervalUnit renewalIntervalUnit,boolean proration){
        return new com.elitecore.netvertex.pm.rnc.ratecard.NonMonetaryRateCard(id,name, description,
                timeUom,
                time,
                timeMinorUnit,
                event,
                pulseUom,
                pulse,
                pulseMinorUnit,
                rncPackageId, rncPackageName, rateCardGroupId, rateCardGroupName,renewalInterval, renewalIntervalUnit,proration);
    }

    @Override
    public MonetaryRateCard createMonetaryRateCard(String id, String name, String description, String keyOne, String keyTwo,
                                                   List<MonetaryRateCardVersion> rateCardVersions, Uom rateUnit, Uom pulseUnit,
                                                   String rncPackageId, String rncPackageName, String rateCardGroupId, String rateCardGroupName){
        return new com.elitecore.netvertex.pm.rnc.ratecard.MonetaryRateCard(id, name, description, keyOne, keyTwo, rateCardVersions, rateUnit, pulseUnit,
                rncPackageId, rncPackageName, rateCardGroupId, rateCardGroupName);
    }

    @Override
    public RateCardGroup createRateCardGroup(String id, String name, String description, LogicalExpression advancedCondition, RateCard peakDaysRateCard, RateCard offPeakRateCard, String rncPackageId, String rncPackageName, int order, AccessTimePolicy accessTimePolicy, List<TimeSlotRelationData> timeSlotRelationDatas){
        return new com.elitecore.netvertex.pm.rnc.rcgroup.RateCardGroup(id, name, description, advancedCondition, peakDaysRateCard, offPeakRateCard, rncPackageId,
                rncPackageName, order, accessTimePolicy, timeSlotRelationDatas);
    }

    @Override
    public RnCPackage createRncPackage(String id, String name, String description,
                                       List<String> groupIds, List<RateCardGroup> rateCardGroups, ThresholdNotificationScheme thresholdNotificationScheme,
                                       String tag, RnCPkgType pkgType,
                                       PkgMode packageMode, PkgStatus pkgStatus, PolicyStatus policyStatus,
                                       String failReason, String partialFailReason, ChargingType chargingType,String currency){
        return new com.elitecore.netvertex.pm.rnc.pkg.RnCPackage(id, name, description, groupIds, rateCardGroups,thresholdNotificationScheme, tag, pkgType, packageMode, pkgStatus,
                policyStatus, failReason, partialFailReason, chargingType,currency);
    }

    @Override
    public ThresholdNotificationScheme createThresholdNotificationScheme(List<ThresholdEvent> thresholdEvents) {
        return new com.elitecore.netvertex.service.notification.ThresholdNotificationScheme(thresholdEvents);
    }
}
