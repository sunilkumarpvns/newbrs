package com.elitecore.corenetvertex.pm;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.constants.RateCardType;
import com.elitecore.corenetvertex.constants.RenewalIntervalUnit;
import com.elitecore.corenetvertex.constants.Uom;
import com.elitecore.corenetvertex.pd.ratecard.MonetaryRateCardData;
import com.elitecore.corenetvertex.pd.ratecard.MonetaryRateCardVersion;
import com.elitecore.corenetvertex.pd.ratecard.MonetaryRateCardVersionDetail;
import com.elitecore.corenetvertex.pd.ratecard.NonMonetaryRateCardData;
import com.elitecore.corenetvertex.pd.ratecard.RateCardData;
import com.elitecore.corenetvertex.pd.ratecardgroup.RateCardGroupData;
import com.elitecore.corenetvertex.pd.ratecardgroup.TimeSlotRelationData;
import com.elitecore.corenetvertex.pd.rncpackage.RncPackageData;
import com.elitecore.corenetvertex.pd.rncpackage.notification.RncNotificationData;
import com.elitecore.corenetvertex.pkg.ChargingType;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pkg.notification.NotificationTemplateData;
import com.elitecore.corenetvertex.pkg.notification.NotificationTemplateType;

public class RnCPkgBuilder {

    public static RncPackageData rncBasePackageWithMonetaryPeakRateCard() {
        RncPackageData pkg = createBasePackageBasicInfo();
        pkg.getRateCardGroupData().add(createRateCardGroupWithPeakMonetaryRateCard(pkg));
        return pkg;
    }


    public static RncPackageData rncBasePackageWithNonMonetaryPeakRateCard() {
        RncPackageData pkg = createBasePackageBasicInfo();
        pkg.getRateCardGroupData().add(createRateCardGroupWithPeakNonMonetaryRateCard(pkg));
        return pkg;
    }


    public static RncPackageData rncBasePackageWithMonetaryOffPeakRateCard() {
        RncPackageData pkg = createBasePackageBasicInfo();
        pkg.getRateCardGroupData().add(createRateCardGroupWithOffPeakMonetaryRateCard(pkg));
        return pkg;
    }


    public static RncPackageData rncBasePackageWithNonMonetaryOffPeakRateCard() {
        RncPackageData pkg = createBasePackageBasicInfo();
        pkg.getRateCardGroupData().add(createRateCardGroupWithOffPeakNonMonetaryRateCard(pkg));
        return pkg;
    }

    public static RncPackageData rncBasePackageWithThresholdNotifications() {
        RncPackageData pkg = createBasePackageBasicInfo();
        pkg.getRateCardGroupData().add(createRateCardGroupWithPeakNonMonetaryRateCard(pkg));
        pkg.setRncNotifications(getRncNotifications(pkg));
        return pkg;
    }





    private static RncPackageData createBasePackageBasicInfo() {
        RncPackageData pkg = new RncPackageData();
        pkg.setName("RnCPackageTest");
        pkg.setGroups("1");
        pkg.setType("BASE");
        pkg.setStatus(PkgStatus.ACTIVE.name());
        pkg.setMode(PkgMode.LIVE.name());
        pkg.setChargingType(ChargingType.SESSION.name());
        return pkg;
    }

    private static RateCardGroupData createRateCardGroupWithPeakMonetaryRateCard(RncPackageData pkg){
        RateCardGroupData rateCardGroup = new RateCardGroupData();
        rateCardGroup.setName("RateCardGroupTest");
        rateCardGroup.setAdvanceCondition("\"1\"=\"1\"");
        RateCardData rateCard = createMonetaryRateCard();
        rateCard.setRncPackageData(pkg);
        rateCardGroup.setPeakRateRateCard(rateCard);
        return rateCardGroup;
    }

    private static RateCardGroupData createRateCardGroupWithPeakNonMonetaryRateCard(RncPackageData pkg){
        RateCardGroupData rateCardGroup = new RateCardGroupData();
        rateCardGroup.setName("RateCardGroupTest");
        rateCardGroup.setAdvanceCondition("\"1\"=\"1\"");
        RateCardData rateCard = createNonMonetaryRateCard();
        rateCard.setRncPackageData(pkg);
        rateCardGroup.setPeakRateRateCard(rateCard);
        return rateCardGroup;
    }

    private static RateCardGroupData createRateCardGroupWithOffPeakMonetaryRateCard(RncPackageData pkg){
        RateCardGroupData rateCardGroup = new RateCardGroupData();
        rateCardGroup.setName("RateCardGroupTest");
        rateCardGroup.setAdvanceCondition("\"1\"=\"1\"");
        RateCardData peakRateCard = createNonMonetaryRateCard();
        peakRateCard.setRncPackageData(pkg);
        rateCardGroup.setPeakRateRateCard(peakRateCard);
        RateCardData rateCard = createMonetaryRateCard();
        rateCard.setRncPackageData(pkg);
        rateCardGroup.setOffPeakRateRateCard(rateCard);
        rateCardGroup.setTimeSlotRelationData(getTimeSlotRelations());
        return rateCardGroup;
    }

    private static RateCardGroupData createRateCardGroupWithOffPeakNonMonetaryRateCard(RncPackageData pkg){
        RateCardGroupData rateCardGroup = new RateCardGroupData();
        rateCardGroup.setName("RateCardGroupTest");
        rateCardGroup.setAdvanceCondition("\"1\"=\"1\"");
        RateCardData peakRateCard = createNonMonetaryRateCard();
        peakRateCard.setRncPackageData(pkg);
        rateCardGroup.setPeakRateRateCard(peakRateCard);
        RateCardData rateCard = createNonMonetaryRateCard();
        rateCard.setRncPackageData(pkg);
        rateCardGroup.setOffPeakRateRateCard(rateCard);
        rateCardGroup.setTimeSlotRelationData(getTimeSlotRelations());
        return rateCardGroup;
    }

    private static RateCardData createMonetaryRateCard() {
        RateCardData rateCardData = new RateCardData();
        rateCardData.setType(RateCardType.MONETARY.name());
        rateCardData.setName("RateCardGroupTest");
        rateCardData.setScope("LOCAL");
        MonetaryRateCardData monetaryRateCardData = new MonetaryRateCardData();
        monetaryRateCardData.setRateUnit(Uom.SECOND.name());
        monetaryRateCardData.setPulseUnit(Uom.MINUTE.name());
        MonetaryRateCardVersion version = new MonetaryRateCardVersion();
        version.setName("Default Version");
        version.setEffectiveFromDate(new Timestamp(System.currentTimeMillis()));
        MonetaryRateCardVersionDetail versionDetail = new MonetaryRateCardVersionDetail();
        versionDetail.setPulse1(1L);
        versionDetail.setRate1(BigDecimal.ONE);
        version.getMonetaryRateCardVersionDetail().add(versionDetail);
        monetaryRateCardData.getMonetaryRateCardVersions().add(version);
        monetaryRateCardData.setRateCardData(rateCardData);
        rateCardData.setMonetaryRateCardData(monetaryRateCardData);


        return rateCardData;
    }

    private static RateCardData createNonMonetaryRateCard() {
        RateCardData rateCardData = new RateCardData();
        rateCardData.setType(RateCardType.NON_MONETARY.name());
        rateCardData.setName("RateCardGroupTest");
        rateCardData.setScope("LOCAL");
        NonMonetaryRateCardData nonMonetaryRateCardData = new NonMonetaryRateCardData();
        nonMonetaryRateCardData.setPulseUom(Uom.SECOND.name());
        nonMonetaryRateCardData.setTimeUom(Uom.MINUTE.name());
        nonMonetaryRateCardData.setTime(100L);
        nonMonetaryRateCardData.setPulse(2L);
		nonMonetaryRateCardData.setEvent(100L);
        nonMonetaryRateCardData.setRateCardData(rateCardData);
        nonMonetaryRateCardData.setRenewalInterval(1);
        nonMonetaryRateCardData.setRenewalIntervalUnit(RenewalIntervalUnit.MONTH.name());
        rateCardData.setNonMonetaryRateCardData(nonMonetaryRateCardData);

        return rateCardData;
    }

    public static List<TimeSlotRelationData> getTimeSlotRelations() {
        List<TimeSlotRelationData> timeSlotRelations = Collectionz.newArrayList();
        TimeSlotRelationData timeSlotRelationData = new TimeSlotRelationData();
        timeSlotRelationData.setDayOfWeek("1,2,3");
        timeSlotRelationData.setTimePeriod("100000");
        timeSlotRelations.add(timeSlotRelationData);
        return timeSlotRelations;
    }

    public static List<RncNotificationData> getRncNotifications(RncPackageData rncPackageData){
        List<RncNotificationData> rncNotifications = Collectionz.newArrayList();
        RncNotificationData rncNotificationData = new RncNotificationData();
        rncNotificationData.setEmailTemplateData(getEmailTemplate());
        rncNotificationData.setRateCardData(rncPackageData.getRateCardGroupData().get(0).getPeakRateRateCard());
        rncNotificationData.setThreshold(30);
        rncNotificationData.setRncPackageData(rncPackageData);
        rncNotifications.add(rncNotificationData);
        return rncNotifications;
    }

    public static NotificationTemplateData getEmailTemplate(){
        NotificationTemplateData notificationTemplateData = new NotificationTemplateData();
        notificationTemplateData.setTemplateType(NotificationTemplateType.EMAIL);
        notificationTemplateData.setSubject("50% balance exhausted");
        notificationTemplateData.setName("HalfBalanceExhausted");
        notificationTemplateData.setGroups(CommonConstants.DEFAULT_GROUP_ID);
        notificationTemplateData.setTemplateData("Dear Subscriber, Your half balance is used");
        return notificationTemplateData;
    }

    public static NotificationTemplateData getSmsTemplate(){
        NotificationTemplateData notificationTemplateData = new NotificationTemplateData();
        notificationTemplateData.setTemplateType(NotificationTemplateType.SMS);
        notificationTemplateData.setName("HalfBalanceExhausted");
        notificationTemplateData.setGroups(CommonConstants.DEFAULT_GROUP_ID);
        notificationTemplateData.setTemplateData("Dear Subscriber, Your half balance is used");
        return notificationTemplateData;
    }


	public static void addRncNotificationsWithThreshold(int threshold, RncPackageData rncPackageData) {
		List<RncNotificationData> rncNotifications = rncPackageData.getRncNotifications();
		RncNotificationData rncNotificationData = new RncNotificationData();
		rncNotificationData.setEmailTemplateData(getEmailTemplate());
		rncNotificationData.setRateCardData(rncPackageData.getRateCardGroupData().get(0).getPeakRateRateCard());
		rncNotificationData.setThreshold(threshold);
		rncNotificationData.setRncPackageData(rncPackageData);
		rncNotifications.add(rncNotificationData);
	}
}
