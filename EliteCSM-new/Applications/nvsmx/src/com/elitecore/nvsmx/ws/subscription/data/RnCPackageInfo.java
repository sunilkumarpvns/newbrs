package com.elitecore.nvsmx.ws.subscription.data;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.constants.RateCardType;
import com.elitecore.corenetvertex.pd.ratecardgroup.TimeSlotRelationData;
import com.elitecore.corenetvertex.pm.rnc.pkg.RnCPackage;
import com.elitecore.corenetvertex.pm.rnc.ratecard.MonetaryRateCard;
import com.elitecore.corenetvertex.pm.rnc.ratecard.MonetaryRateCardVeresionDetail;
import com.elitecore.corenetvertex.pm.rnc.ratecard.MonetaryRateCardVersion;
import com.elitecore.corenetvertex.pm.rnc.ratecard.NonMonetaryRateCard;
import com.elitecore.corenetvertex.pm.rnc.ratecard.RateCard;
import com.elitecore.corenetvertex.pm.rnc.ratecard.RateSlab;
import com.elitecore.corenetvertex.pm.rnc.rcgroup.RateCardGroup;

import javax.xml.bind.annotation.XmlType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@XmlType(propOrder={"rncPackageId","rncPackageName","rncPackageDescription","rncPackageType", "packageMode", "packageStatus","currency", "groupNames","monetaryRateCards","nonMonetaryRateCards","rateCardGroup"})
public class RnCPackageInfo {
    private String rncPackageId;
    private String rncPackageName;
    private String rncPackageDescription;
    private String rncPackageType;
    private String packageMode;
    private String packageStatus;
    private List<String> groupNames;
    private String currency;
    private List<MonetaryRateCardInfo> monetaryRateCards;
    private List<NonMonetaryRateCardInfo> nonMonetaryRateCards;
    private List<RateCardGroupInfo> rateCardGroup;

    public RnCPackageInfo(){
        //required by method to have a default constructor
    }

    public RnCPackageInfo(String rncPackageId, String rncPackageName, String rncPackageDescription, String rncPackageType, String packageMode, String packageStatus, List<String> groupNames,String currency) {
        super();
        this.rncPackageId = rncPackageId;
        this.rncPackageName = rncPackageName;
        this.rncPackageDescription = rncPackageDescription;
        this.rncPackageType = rncPackageType;
        this.packageMode = packageMode;
        this.packageStatus = packageStatus;
        this.groupNames = groupNames;
        this.currency=currency;
        rateCardGroup = Collectionz.newArrayList();
        monetaryRateCards = Collectionz.newArrayList();
        nonMonetaryRateCards = Collectionz.newArrayList();
    }

    public String getRncPackageId() {
        return rncPackageId;
    }

    public void setRncPackageId(String rncPackageId) {
        this.rncPackageId = rncPackageId;
    }

    public String getRncPackageName() {
        return rncPackageName;
    }

    public void setRncPackageName(String rncPackageName) {
        this.rncPackageName = rncPackageName;
    }

    public String getRncPackageDescription() {
        return rncPackageDescription;
    }

    public void setRncPackageDescription(String rncPackageDescription) {
        this.rncPackageDescription = rncPackageDescription;
    }

    public String getRncPackageType() {
        return rncPackageType;
    }

    public void setRncPackageType(String rncPackageType) {
        this.rncPackageType = rncPackageType;
    }

    public String getPackageMode() {
        return packageMode;
    }


    public void setPackageMode(String packageMode) {
        this.packageMode = packageMode;
    }

    public String getPackageStatus() {
        return packageStatus;
    }

    public void setPackageStatus(String packageStatus) {
        this.packageStatus = packageStatus;
    }

    public List<String> getGroupNames() {
        return groupNames;
    }

    public void setGroupNames(List<String> groupNames) {
        this.groupNames = groupNames;
    }


    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public List<RateCardGroupInfo> getRateCardGroup() {
        return rateCardGroup;
    }

    public void setRateCardGroup(List<RateCardGroupInfo> rateCardGroupInfo) {
        this.rateCardGroup = rateCardGroupInfo;
    }

    public List<MonetaryRateCardInfo> getMonetaryRateCards() {
        return monetaryRateCards;
    }

    public void setMonetaryRateCards(List<MonetaryRateCardInfo> monetaryRateCards) {
        this.monetaryRateCards = monetaryRateCards;
    }

    public List<NonMonetaryRateCardInfo> getNonMonetaryRateCards() {
        return nonMonetaryRateCards;
    }

    public void setNonMonetaryRateCards(List<NonMonetaryRateCardInfo> nonMonetaryRateCards) {
        this.nonMonetaryRateCards = nonMonetaryRateCards;
    }

    public static RnCPackageInfo create(RnCPackage rncPackage){
        RnCPackageInfo rncPackageInfo = new  RnCPackageInfo(rncPackage.getId(), rncPackage.getName(),rncPackage.getDescription(),  rncPackage.getPkgType().name(), rncPackage.getPackageMode().name(), rncPackage.getPkgStatus().name(),rncPackage.getGroupIds(),rncPackage.getCurrency());
        Map<String,MonetaryRateCardInfo> monetaryRateCardMap = new HashMap<>();
        Map<String,NonMonetaryRateCardInfo> nonMonetaryRateCardMap = new HashMap<>();

        for(RateCardGroup rateCardGroup : rncPackage.getRateCardGroups()){
            RateCardInfo peakRateCardInfo = createRateCardInfo(monetaryRateCardMap, nonMonetaryRateCardMap, rateCardGroup.getPeakRateCard());
            RateCardInfo offPeakRateCardInfo = createRateCardInfo(monetaryRateCardMap, nonMonetaryRateCardMap, rateCardGroup.getOffPeakRateCard());
            RateCardGroupInfo rateCardGroupInfo = new RateCardGroupInfo(rateCardGroup.getId(), rateCardGroup.getName(), rateCardGroup.getDescription(), Objects.nonNull(rateCardGroup.getAdvancedCondition())?rateCardGroup.getAdvancedCondition().toString():null,peakRateCardInfo,offPeakRateCardInfo);

            List<TimeSlotInformation> timeSlotInformations = Collectionz.newArrayList();
            List<TimeSlotRelationData> timeSlotRelationDatas = rateCardGroup.getTimeSlotRelationDatas();
            if(Collectionz.isNullOrEmpty(timeSlotRelationDatas) == false){
                for(TimeSlotRelationData timeSlotRelationData : timeSlotRelationDatas){
                    timeSlotInformations.add(new TimeSlotInformation(timeSlotRelationData.getDayOfWeek(),timeSlotRelationData.getTimePeriod(),timeSlotRelationData.getType(),timeSlotRelationData.getOrderNo()));
                }
            }
            rateCardGroupInfo.setOffPeakTimeSlot(timeSlotInformations);
            rncPackageInfo.getRateCardGroup().add(rateCardGroupInfo);
        }
        rncPackageInfo.getNonMonetaryRateCards().addAll(nonMonetaryRateCardMap.values());
        rncPackageInfo.getMonetaryRateCards().addAll(monetaryRateCardMap.values());
        return rncPackageInfo;
    }

    private static RateCardInfo createRateCardInfo(Map<String, MonetaryRateCardInfo> monetaryRateCardMap, Map<String, NonMonetaryRateCardInfo> nonMonetaryRateCardMap, RateCard rateCard) {
        if(rateCard != null) {
            MonetaryRateCard monetaryRateCard = getMonetaryRateCard(rateCard);
            if (monetaryRateCard != null) {
                addMonetaryRateCardIfAbsent(monetaryRateCardMap, monetaryRateCard);
                return new RateCardInfo(monetaryRateCard.getId(), monetaryRateCard.getName());
            } else {
                NonMonetaryRateCard nonMonetaryRateCard = getNonMonetaryRateCard(rateCard);
                if (nonMonetaryRateCard != null) {
                    addNonMonetaryRateCardIfAbsent(nonMonetaryRateCardMap, nonMonetaryRateCard);
                    return new RateCardInfo(nonMonetaryRateCard.getId(), nonMonetaryRateCard.getName());
                }
            }
        }
        return null;
    }


    private static MonetaryRateCard getMonetaryRateCard(RateCard rateCard){
        if(RateCardType.MONETARY == rateCard.getType()) {
            return (MonetaryRateCard) rateCard;
        }
        return null;
    }

    private static NonMonetaryRateCard getNonMonetaryRateCard(RateCard rateCard){
        if(RateCardType.NON_MONETARY == rateCard.getType()) {
            return (NonMonetaryRateCard) rateCard;
        }
        return null;
    }

    private  static void addMonetaryRateCardIfAbsent(Map<String, MonetaryRateCardInfo> monetaryRateCardMap, MonetaryRateCard monetaryRateCard) {
        if(monetaryRateCardMap.containsKey(monetaryRateCard.getId()) == false){
            monetaryRateCardMap.put(monetaryRateCard.getId(),getMonetaryRateCard(monetaryRateCard));

        }
    }
    private static void addNonMonetaryRateCardIfAbsent(Map<String, NonMonetaryRateCardInfo> nonMonetaryRateCardMap, NonMonetaryRateCard nonMonetaryRateCard) {
        if(nonMonetaryRateCardMap.containsKey(nonMonetaryRateCard.getId()) == false){
            nonMonetaryRateCardMap.put(nonMonetaryRateCard.getId(),getNonMonetaryRateCard(nonMonetaryRateCard));

        }
    }

    private static MonetaryRateCardInfo getMonetaryRateCard(MonetaryRateCard rncRateCard) {
        MonetaryRateCardInfo monetaryRateCardInfo = new MonetaryRateCardInfo(rncRateCard.getId(), rncRateCard.getName(), rncRateCard.getDescription(), rncRateCard.getKeyOne(), rncRateCard.getKeyTwo(), rncRateCard.getRateUnit(), rncRateCard.getPulseUnit());

        List<RateCardVersionDetailInfo> rateCardVersionInfoList = Collectionz.newArrayList();

        for(MonetaryRateCardVersion rateCardVersion : rncRateCard.getRateCardVersions()){
            rateCardVersionInfoList.add(createVersionDetailInfo(rateCardVersion));
        }

        monetaryRateCardInfo.setRateCardVersionDetails(rateCardVersionInfoList);
        return monetaryRateCardInfo;
    }

    private static NonMonetaryRateCardInfo getNonMonetaryRateCard(NonMonetaryRateCard rncRateCard) {
        return new NonMonetaryRateCardInfo(rncRateCard.getId(), rncRateCard.getName(), rncRateCard.getDescription(), rncRateCard.getTime(), rncRateCard.getTimeUom(), rncRateCard.getPulse(), rncRateCard.getPulseUom(),rncRateCard.getEvent());
    }


    private static RateCardVersionDetailInfo createVersionDetailInfo(MonetaryRateCardVersion rateCardVersion) {

        RateCardVersionDetailInfo rateCardVersionDetailInfo = new RateCardVersionDetailInfo();
        for (MonetaryRateCardVeresionDetail ratingBehavior : rateCardVersion.getRatingBehaviors()){
            int slabCount = 1;
            rateCardVersionDetailInfo.setRateType(ratingBehavior.getRatingType().name());
            for (RateSlab rateSlab : ratingBehavior.getSlabs()){
                setSlabInformation(rateCardVersionDetailInfo, rateSlab, slabCount);
                ++slabCount;
            }
        }
        return rateCardVersionDetailInfo;
    }

    private static void setSlabInformation(RateCardVersionDetailInfo rateCardVersionDetailInfo, RateSlab rateSlab, int slabCount) {
        if(slabCount==1){
            rateCardVersionDetailInfo.setSlab1(rateSlab.getSlabValue());
            rateCardVersionDetailInfo.setRate1(rateSlab.getRate());
            rateCardVersionDetailInfo.setPulse1(rateSlab.getPulse());
        }else if(slabCount==2){
            rateCardVersionDetailInfo.setSlab2(rateSlab.getSlabValue());
            rateCardVersionDetailInfo.setRate2(rateSlab.getRate());
            rateCardVersionDetailInfo.setPulse2(rateSlab.getPulse());
        }else if(slabCount==3){
            rateCardVersionDetailInfo.setSlab3(rateSlab.getSlabValue());
            rateCardVersionDetailInfo.setRate3(rateSlab.getRate());
            rateCardVersionDetailInfo.setPulse3(rateSlab.getPulse());
        }
    }
}
