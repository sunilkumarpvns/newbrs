package com.elitecore.corenetvertex.pm;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.constants.AggregationKey;
import com.elitecore.corenetvertex.constants.BalanceLevel;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.DataUnit;
import com.elitecore.corenetvertex.constants.IMSServiceAction;
import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.constants.QoSProfileAction;
import com.elitecore.corenetvertex.constants.QoSUnit;
import com.elitecore.corenetvertex.constants.QuotaProfileType;
import com.elitecore.corenetvertex.constants.RenewalIntervalUnit;
import com.elitecore.corenetvertex.constants.TierRateType;
import com.elitecore.corenetvertex.constants.TimeUnit;
import com.elitecore.corenetvertex.constants.Uom;
import com.elitecore.corenetvertex.constants.UsageType;
import com.elitecore.corenetvertex.constants.ValidityPeriodUnit;
import com.elitecore.corenetvertex.pd.revenuedetail.RevenueDetailData;
import com.elitecore.corenetvertex.pd.topup.DataTopUpData;
import com.elitecore.corenetvertex.pkg.PkgData;
import com.elitecore.corenetvertex.pkg.PkgGroupOrderData;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pkg.PkgType;
import com.elitecore.corenetvertex.pkg.chargingrulebasename.ChargingRuleBaseNameData;
import com.elitecore.corenetvertex.pkg.constants.PCCAttribute;
import com.elitecore.corenetvertex.pkg.dataservicetype.DataServiceTypeData;
import com.elitecore.corenetvertex.pkg.ims.IMSPkgData;
import com.elitecore.corenetvertex.pkg.ims.IMSPkgPCCAttributeData;
import com.elitecore.corenetvertex.pkg.ims.IMSPkgServiceData;
import com.elitecore.corenetvertex.pkg.ims.MediaTypeData;
import com.elitecore.corenetvertex.pkg.ims.PCCRuleAttributeAction;
import com.elitecore.corenetvertex.pkg.pccrule.PCCRuleData;
import com.elitecore.corenetvertex.pkg.qos.QosProfileData;
import com.elitecore.corenetvertex.pkg.qos.QosProfileDetailData;
import com.elitecore.corenetvertex.pkg.quota.QuotaProfileDetailData;
import com.elitecore.corenetvertex.pkg.ratecard.DataRateCardData;
import com.elitecore.corenetvertex.pkg.ratecard.DataRateCardVersionDetailData;
import com.elitecore.corenetvertex.pkg.ratecard.DataRateCardVersionRelationData;
import com.elitecore.corenetvertex.pkg.ratinggroup.RatingGroupData;
import com.elitecore.corenetvertex.pkg.rnc.QuotaUsageType;
import com.elitecore.corenetvertex.pkg.rnc.RncProfileData;
import com.elitecore.corenetvertex.pkg.rnc.RncProfileDetailData;
import com.elitecore.corenetvertex.pkg.rnc.VolumeUnitType;
import com.elitecore.corenetvertex.pm.PolicyManagerTestSuite.ManageOrderData;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc.ratecard.DataRateCard;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc.ratecard.DataRateCardVersion;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc.ratecard.FlatRating;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc.ratecard.FlatSlab;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc.ratecard.RateCardVersion;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc.ratecard.RateSlab;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc.ratecard.VersionDetail;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * Created by Ishani on 2/5/17.
 */
public class PkgDataBuilder {

	public static Random random = new Random();
	private static final String currency="INR";

    public static PkgData newBasePackageWithDefaultValues(boolean isPCCRequired) {
		PkgData pkg = createBasePackageBasicInfo();
		List<QosProfileData> qosProfiles = getQosProfiles(pkg, isPCCRequired);
		pkg.setQosProfiles(qosProfiles);
		return pkg;
	}

	public static PkgData newBasePackageWithRnCProfileAndPCCProfile(boolean isPCCRequired) {
		PkgData pkg = createBasePackageBasicInfo();
		RncProfileData rncProfileData = createRnCQuotaProfileData();
		rncProfileData.setPkgData(pkg);
		pkg.setRncProfileDatas(Arrays.asList(rncProfileData));
		List<QosProfileData> qosProfiles = getQosProfiles(pkg, rncProfileData, isPCCRequired);
		pkg.setQosProfiles(qosProfiles);
		return pkg;
	}

	public static PkgData newAddOnWithDefaultValues() {

		PkgData pkg = createAddOnBasicInfo();
		List<QosProfileData> qosProfiles = getQosProfiles(pkg, true);
		pkg.setQosProfiles(qosProfiles);
		pkg.setValidityPeriod(1);
		pkg.setMultipleSubscription(false);
		pkg.setValidityPeriodUnit(ValidityPeriodUnit.DAY);
		return pkg;
	}

    public static IMSPkgData newImsPackageWithDefaultValue(){
        IMSPkgData imsPkgData = createIMSBasicInfo();
        imsPkgData.setImsPkgServiceDatas(createImsPkgServiceDatas(imsPkgData));
        return imsPkgData;
    }

	public static DataTopUpData newDataTopUpDataWithDefaultValue(){
		DataTopUpData dataTopUpData = createDataTopUpBasicInfo();
		return dataTopUpData;
	}

    private static List<QosProfileData> getQosProfiles(PkgData pkg, boolean isPCCRequired) {
        List<QosProfileData> qosProfiles = Collectionz.newArrayList();
        QosProfileData qosProfile = new QosProfileData();
        qosProfile.setName("QosProfileTest");
        qosProfile.setGroups("1");
        qosProfile.setPkgData(pkg);
        List<QosProfileDetailData> qosProfileDetailDatas = getQosProfileDetails(qosProfile, isPCCRequired);
        qosProfile.setQosProfileDetailDataList(qosProfileDetailDatas);
        qosProfiles.add(qosProfile);
        return qosProfiles;
    }

	private static List<QosProfileData> getQosProfiles(PkgData pkg, RncProfileData rncProfileData, boolean isPCCRequired) {
		List<QosProfileData> qosProfiles = Collectionz.newArrayList();
		QosProfileData qosProfile = new QosProfileData();
		qosProfile.setName("QosProfileTest");
		qosProfile.setGroups("1");
		qosProfile.setPkgData(pkg);
		qosProfile.setRncProfileData(rncProfileData);
		List<QosProfileDetailData> qosProfileDetailDatas = getQosProfileDetails(qosProfile, isPCCRequired);
		qosProfile.setQosProfileDetailDataList(qosProfileDetailDatas);
		qosProfiles.add(qosProfile);
		return qosProfiles;
	}

	private static List<QosProfileData> getQosProfiles(PkgData pkg, DataRateCardData dataRateCardData, boolean isPCCRequired) {
		List<QosProfileData> qosProfiles = Collectionz.newArrayList();
		QosProfileData qosProfile = new QosProfileData();
		qosProfile.setName("QosProfileTest");
		qosProfile.setGroups("1");
		qosProfile.setPkgData(pkg);
		qosProfile.setRateCardData(dataRateCardData);
		List<QosProfileDetailData> qosProfileDetailDatas = getQosProfileDetails(qosProfile, isPCCRequired);
		qosProfile.setQosProfileDetailDataList(qosProfileDetailDatas);
		qosProfiles.add(qosProfile);
		return qosProfiles;
	}

    private static List<IMSPkgServiceData> createImsPkgServiceDatas(IMSPkgData imsPkgData){
        List<IMSPkgServiceData> imsPkgServiceDatas = Collectionz.newArrayList();
        IMSPkgServiceData imsPkgServiceData = new IMSPkgServiceData();
        imsPkgServiceData.setName("IMSPkgServiceTest");
        imsPkgServiceData.setMediaTypeData(getMediaTypeData());
        imsPkgServiceData.setAfApplicationId("0");
        imsPkgServiceData.setAction(IMSServiceAction.ACCEPT);
        imsPkgServiceData.setImsPkgPCCAttributeDatas(getIMSPccAttributeDatas(imsPkgServiceData));
        imsPkgServiceData.setImsPkgData(imsPkgData);
        imsPkgServiceDatas.add(imsPkgServiceData);
        return imsPkgServiceDatas;
    }

    private static List<IMSPkgPCCAttributeData> getIMSPccAttributeDatas(IMSPkgServiceData imsPkgServiceData){
        List<IMSPkgPCCAttributeData> imsPkgPCCAttributeDatas = Collectionz.newArrayList();
        IMSPkgPCCAttributeData imsPkgPCCAttributeData = new IMSPkgPCCAttributeData();
        imsPkgPCCAttributeData.setAttribute(PCCAttribute.CHARGING_KEY);
        imsPkgPCCAttributeData.setAction(PCCRuleAttributeAction.OVERRIDE);
        imsPkgPCCAttributeData.setValue("0");
        imsPkgPCCAttributeData.setImsPkgServiceData(imsPkgServiceData);
        imsPkgPCCAttributeDatas.add(imsPkgPCCAttributeData);
        return imsPkgPCCAttributeDatas;
    }

    private static MediaTypeData getMediaTypeData(){
        MediaTypeData mediaTypeData = new MediaTypeData();
        mediaTypeData.setName("TestType");
        mediaTypeData.setMediaIdentifier(101L);
        mediaTypeData.setStatus(PkgStatus.ACTIVE.name());
        return mediaTypeData;
    }

	private static List<QosProfileDetailData> getQosProfileDetails(QosProfileData qosProfileData, boolean isPCCRequired) {
		List<QosProfileDetailData> qosProfileDetailDatas = Collectionz.newArrayList();

		QosProfileDetailData qosProfileDetailData = createQosProfileDetailData(UUID.randomUUID().toString(), qosProfileData.getName());
		qosProfileDetailData.setQosProfile(qosProfileData);
		List<PCCRuleData> pccRules = new ArrayList<>();
		if (isPCCRequired) {
			pccRules.add(createPCCRule("PCCTest", "1"));
		}
		qosProfileDetailData.setPccRules(pccRules);
		qosProfileDetailDatas.add(qosProfileDetailData);
		return qosProfileDetailDatas;
	}

	public static PCCRuleData createPCCRule(String id, String name) {
		PCCRuleData pcc = new PCCRuleData();
		pcc.setName(id);
		pcc.setGroups(name);
		pcc.setMonitoringKey("PCCTest");
		pcc.setChargingMode((byte)1);
		pcc.setChargingKey(createRatingGroupData().getId());
		pcc.setDataServiceTypeData(createServiceTypeWithRatingGroup());
        pcc.setCreatedDate(new Timestamp(100));
        pcc.setFlowStatus((byte)1);
        pcc.setType("DYNAMIC");
        pcc.setUsageMonitoring(false);
        pcc.setSliceUpload(1l);
        pcc.setSliceDownload(1l);
        pcc.setSliceTime(1l);
        pcc.setSliceTotal(1l);
        pcc.setMbrdl(1l);
		return pcc;
	}


	public static ChargingRuleBaseNameData createCRBN(String id, String name){
		ChargingRuleBaseNameData crbn = new ChargingRuleBaseNameData();
		crbn.setName(id);
		crbn.setGroups(name);
		return crbn;
	}

	public static RatingGroupData createRatingGroupData(String id, String name){
		RatingGroupData ratingGroupData = new RatingGroupData();
		ratingGroupData.setId(id);
		ratingGroupData.setName(name);
		ratingGroupData.setGroups(name);
		ratingGroupData.setIdentifier(1l);
		ratingGroupData.setDescription("dummy");
		return ratingGroupData;
	}

	public static List<RatingGroupData> createRatingGroupDatas(String id, String name){
		List<RatingGroupData> ratingGroups = new ArrayList<>();
		ratingGroups.add(createRatingGroupData(id, name));
		return ratingGroups;
	}

	public static QosProfileDetailData createQosProfileDetailData(String id, String name) {
		QosProfileDetailData qosProfileData = new QosProfileDetailData();
        List<PCCRuleData> pccRules = new ArrayList<>();
        pccRules.add(createPCCRule("PCCTest", "1"));
		qosProfileData.setPccRules(pccRules);
		List<ChargingRuleBaseNameData> chargingRuleBaseNames = new ArrayList<>();
		chargingRuleBaseNames.add(createCRBN("CRBNTest","1"));
		qosProfileData.setChargingRuleBaseNames(chargingRuleBaseNames);
		List<RatingGroupData> ratingGroups = new ArrayList<>();
		ratingGroups.add(createRatingGroupData("ratingGroupTest","1"));
        qosProfileData.setQci(9);
        qosProfileData.setFupLevel(0);
		qosProfileData.setUsageMonitoring(false);
		qosProfileData.setAction(QoSProfileAction.ACCEPT.getId());
		qosProfileData.setAambrdl(1l);
		qosProfileData.setAambrul(1l);
		qosProfileData.setMbrdl(1l);
		qosProfileData.setMbrul(1l);
		qosProfileData.setAambrdlUnit(QoSUnit.Kbps.name());
		qosProfileData.setAambrulUnit(QoSUnit.Kbps.name());
		qosProfileData.setMbrdlUnit(QoSUnit.Kbps.name());
		qosProfileData.setMbrulUnit(QoSUnit.Kbps.name());
		qosProfileData.setPreCapability(true);
		qosProfileData.setPreVulnerability(true);
		qosProfileData.setPriorityLevel((byte)1);
        qosProfileData.setRejectCause("QoS prfoile detail creation is rejected.");
        return qosProfileData;
	}

	public static RatingGroupData createRatingGroupData() {
		RatingGroupData ratingGroup = new RatingGroupData();
		ratingGroup.setId("RATING_TYPE_1");
		ratingGroup.setName("DefaultRatingGroup");
		ratingGroup.setGroups("1");
		ratingGroup.setIdentifier(1L);
		return ratingGroup;
	}

	public static DataServiceTypeData createServiceTypeWithRatingGroup() {
		return createServiceTypeWithRatingGroup(CommonConstants.ALL_SERVICE_ID);
	}

    public static DataServiceTypeData createServiceTypeWithRatingGroup(String serviceId) {
        DataServiceTypeData dataServiceTypeData = new DataServiceTypeData();
        dataServiceTypeData.setId(serviceId);
        dataServiceTypeData.setGroups("1");
        dataServiceTypeData.setName("ServiceTypeTest");
        dataServiceTypeData.setServiceIdentifier(1L);
        dataServiceTypeData.setRatingGroupDatas(Arrays.asList(createRatingGroupData()));
        return dataServiceTypeData;
    }

	private static PkgData createBasePackageBasicInfo() {
		PkgData pkg = new PkgData();
		pkg.setName("PackageTest");
		pkg.setGroups("1");
		pkg.setType("BASE");
		pkg.setQuotaProfileType(QuotaProfileType.USAGE_METERING_BASED);
		pkg.setStatus(PkgStatus.ACTIVE.name());
		pkg.setPackageMode(PkgMode.LIVE.name());
		pkg.setCurrency(currency);
		return pkg;
	}

	private static PkgData getEmergencyPackageBasicInfo() {
		PkgData pkg = createPackages();
		pkg.setType(PkgType.EMERGENCY.name());
		return pkg;
	}
	
	private static PkgData getPromotionalPackageBasicInfo() {
		PkgData pkg = createPackages();
		pkg.setType(PkgType.PROMOTIONAL.name());
		return pkg;
	}

	private static PkgData createPackages() {
		PkgData pkg = new PkgData();
		pkg.setId(UUID.randomUUID().toString());
		pkg.setName("PackageTest");
		pkg.setGroups("1");
		pkg.setQuotaProfileType(QuotaProfileType.USAGE_METERING_BASED);
		pkg.setAvailabilityStartDate(new Timestamp(Calendar.getInstance().getTimeInMillis()));
		pkg.setStatus(PkgStatus.ACTIVE.name());
		pkg.setPackageMode(PkgMode.LIVE.name());
		pkg.setCurrency(currency);
		return pkg;
	}

    private static IMSPkgData createIMSBasicInfo(){
        IMSPkgData imsPkgData = new IMSPkgData();
        imsPkgData.setName("IMSPackage");
        imsPkgData.setGroups("1");
        imsPkgData.setStatus(PkgStatus.ACTIVE.name());
        imsPkgData.setPackageMode(PkgMode.LIVE.name());
        return imsPkgData;
    }

	public static DataTopUpData createDataTopUpBasicInfo(){
		DataTopUpData dataTopUpData = new DataTopUpData();
		dataTopUpData.setName("QuotaTopUp");
		dataTopUpData.setGroups("1");
		dataTopUpData.setStatus(PkgStatus.ACTIVE.name());
		dataTopUpData.setPackageMode(PkgMode.LIVE.name());
		dataTopUpData.setValidityPeriod(1);
		dataTopUpData.setValidityPeriodUnit(ValidityPeriodUnit.DAY);
		dataTopUpData.setAvailabilityStartDate(new Timestamp(System.currentTimeMillis()-1000));
		dataTopUpData.setAvailabilityEndDate(new Timestamp(System.currentTimeMillis()));
		dataTopUpData.setTopupType("TOP_UP");
		dataTopUpData.setVolumeBalanceUnit("BYTE");
		dataTopUpData.setTimeBalanceUnit("SECOND");
		dataTopUpData.setQuotaType("VOLUME");
		dataTopUpData.setUnitType("TOTAL");
		dataTopUpData.setVolumeBalance(1000l);
		dataTopUpData.setPackageMode("LIVE");

		return dataTopUpData;
	}

	public static PkgData newEmergencyPackageWithDefaultValues() {
		PkgData pkg = getEmergencyPackageBasicInfo();
		List<QosProfileData> qosProfiles = getQosProfiles(pkg, true);
		pkg.setQosProfiles(qosProfiles);
		return pkg;
	}
	
	public static PkgData newPromotionalWithDefaultValues() {

		PkgData pkg = getPromotionalPackageBasicInfo();
		List<QosProfileData> qosProfiles = getQosProfiles(pkg, true);
		pkg.setQosProfiles(qosProfiles);
		return pkg;
	}

	private static PkgData createAddOnBasicInfo() {
		PkgData pkg = new PkgData();
		pkg.setName("AddOnTestPackage");
		pkg.setGroups("1");
		pkg.setQuotaProfileType(QuotaProfileType.USAGE_METERING_BASED);
		pkg.setType(PkgType.ADDON.name());
		pkg.setStatus(PkgStatus.ACTIVE.name());
		pkg.setPackageMode(PkgMode.LIVE.name());
		return pkg;
	}

	public static List<PkgGroupOrderData> newPkgGroupOrderWithDefaultValue(PkgData pkgData) {

		List<PkgGroupOrderData> pkgGroupOrderDatas = Collectionz.newArrayList(); 
		
		String groupIdsStr = pkgData.getGroups();
		List<String> groupIds = Strings.splitter(CommonConstants.COMMA).split(groupIdsStr);

		for (int i = 0; i < groupIds.size(); i++) {

			PkgGroupOrderData groupOrderData =  new PkgGroupOrderData();
			groupOrderData.setId(UUID.randomUUID().toString());
			groupOrderData.setGroupId(groupIds.get(i));
			groupOrderData.setOrderNumber(i);
			pkgGroupOrderDatas.add(groupOrderData);
			groupOrderData.setPkgData(pkgData);
			groupOrderData.setType(pkgData.getType());
			
		}
		return pkgGroupOrderDatas;
	}

	public static List<PkgGroupOrderData> newPkgGroupOrdersWithDefaultValue(PkgData... pkg) {
		List<PkgGroupOrderData> pkgManageOrders = new ArrayList<PkgGroupOrderData>();

		for (PkgData pkgData : pkg) {
			pkgManageOrders.addAll(newPkgGroupOrderWithDefaultValue(pkgData));
		}

		return pkgManageOrders;
	}

	public static void setOrderNumber(List<PkgGroupOrderData> pkgGroupOrderDatas, ManageOrderData... manageOrderDatas) {
		for (PkgGroupOrderData pkgGroupOrderData : pkgGroupOrderDatas) {

			for (ManageOrderData manageOrderData : manageOrderDatas) {
				if (pkgGroupOrderData.getPkgData().getId().equals(manageOrderData.getPkgId()) &&
						pkgGroupOrderData.getGroupId().equals(manageOrderData.getGroupId())) {
					pkgGroupOrderData.setOrderNumber(manageOrderData.getOrderNo());
				}
			}
		}
	}

	public static QuotaProfileDetailData createUMBaseQuotaDetailData(String id) {
		return createUMBaseQuotaDetailData(id, "1");
	}

    public static QuotaProfileDetailData createUMBaseQuotaDetailData(String id, String serviceId) {
        QuotaProfileDetailData quotaProfileDetailData = new QuotaProfileDetailData();
        quotaProfileDetailData.setId(id);
        quotaProfileDetailData.setFupLevel(0);
        quotaProfileDetailData.setTotalUnit(DataUnit.BYTE.name());
        quotaProfileDetailData.setDownloadUnit(DataUnit.BYTE.name());
        quotaProfileDetailData.setUploadUnit(DataUnit.BYTE.name());
        quotaProfileDetailData.setTimeUnit(TimeUnit.SECOND.name());
        quotaProfileDetailData.setTotal(new Long(random.nextInt(Integer.MAX_VALUE)));
        quotaProfileDetailData.setDownload(new Long(random.nextInt(Integer.MAX_VALUE)));
        quotaProfileDetailData.setUpload(new Long(random.nextInt(Integer.MAX_VALUE)));
        quotaProfileDetailData.setTime(new Long(random.nextInt(Integer.MAX_VALUE)));
        quotaProfileDetailData.setAggregationKey(AggregationKey.BILLING_CYCLE.name());
        quotaProfileDetailData.setDataServiceTypeData(createServiceTypeWithRatingGroup(serviceId));

        return quotaProfileDetailData;
    }
	
	public static RncProfileData createRnCQuotaProfileData() {
		
		RncProfileData rncProfileData = new RncProfileData();
		rncProfileData.setId("1");
		rncProfileData.setName("Default_Balance_Quota");
		
		rncProfileData.setPkgData(newBasePackageWithDefaultValues(false));
		List<RncProfileDetailData> balanceQuotaProfileDetails = getBalanceQuotaProfileDetails();
		balanceQuotaProfileDetails.forEach(data -> data.setRncProfileData(rncProfileData));
		rncProfileData.setRncProfileDetailDatas(balanceQuotaProfileDetails);
		rncProfileData.setQuotaType(QuotaUsageType.HYBRID.name());
		rncProfileData.setUnitType(VolumeUnitType.TOTAL.name());
		rncProfileData.setBalanceLevel(BalanceLevel.HSQ.name());
		rncProfileData.setRenewalInterval(2);
		rncProfileData.setRenewalIntervalUnit(RenewalIntervalUnit.MONTH.name());
		
		return rncProfileData;
	}

	public static DataRateCardData createRateCardData() {
		DataRateCardData rateCardData = new DataRateCardData();
		rateCardData.setName("RateCard");
		rateCardData.setDescription("Description");
		rateCardData.setLabelKey1("LabelKey1");
		rateCardData.setLabelKey2("LabelKey2");
		rateCardData.setPulseUnit(Uom.BYTE.getValue());
		rateCardData.setRateUnit(Uom.BYTE.getValue());
		rateCardData.setDataRateCardVersionRelationData(Arrays.asList(createDataRateCardVersionRelationData()));
		return rateCardData;
	}

	public static DataRateCardData createRateCardDataWithRateAndPulse(BigDecimal rate, long pulse) {
		DataRateCardData rateCardData = new DataRateCardData();
		rateCardData.setName("RateCard");
		rateCardData.setDescription("Description");
		rateCardData.setLabelKey1("LabelKey1");
		rateCardData.setLabelKey2("LabelKey2");
		rateCardData.setPulseUnit(Uom.BYTE.getValue());
		rateCardData.setRateUnit(Uom.BYTE.getValue());
		rateCardData.setDataRateCardVersionRelationData(Arrays.asList(createDataRateCardVersionRelationData(rate, pulse)));
		return rateCardData;
	}

	public static DataRateCardVersionRelationData createDataRateCardVersionRelationData(BigDecimal rate, long pulse) {
		DataRateCardVersionRelationData dataRateCardVersionRelationData = new DataRateCardVersionRelationData();
		dataRateCardVersionRelationData.setId(UUID.randomUUID().toString());
		dataRateCardVersionRelationData.setVersionName("Version");
		dataRateCardVersionRelationData.setEffectiveFromDate(new Timestamp(System.currentTimeMillis() - 15000));
		dataRateCardVersionRelationData.setDataRateCardVersionDetailDataList(Arrays.asList(createDataRateCardVersionDetailData(rate, pulse)));
		return dataRateCardVersionRelationData;
	}

	public static DataRateCardVersionRelationData createDataRateCardVersionRelationData() {
		return createDataRateCardVersionRelationData(new BigDecimal(1), 1l);
	}

	public static DataRateCardVersionDetailData createDataRateCardVersionDetailData() {
		return createDataRateCardVersionDetailData(new BigDecimal(1), 1l);
	}

	public static DataRateCardVersionDetailData createDataRateCardVersionDetailData(BigDecimal rate, long pulse) {
		DataRateCardVersionDetailData dataRateCardVersionDetailData = new DataRateCardVersionDetailData();
		dataRateCardVersionDetailData.setId(UUID.randomUUID().toString());
		dataRateCardVersionDetailData.setOrderNumber(new Integer(1));
		dataRateCardVersionDetailData.setLabelKey1("Key1");
		dataRateCardVersionDetailData.setLabelKey2("Key2");
		dataRateCardVersionDetailData.setPulse1(new Long(pulse));
		dataRateCardVersionDetailData.setPulse2(null);
		dataRateCardVersionDetailData.setPulse3(null);
		dataRateCardVersionDetailData.setRate1(rate);
		dataRateCardVersionDetailData.setRate2(null);
		dataRateCardVersionDetailData.setRate3(null);
		dataRateCardVersionDetailData.setRateType(TierRateType.FLAT.name());
		dataRateCardVersionDetailData.setSlab1(new Long(1));
		dataRateCardVersionDetailData.setSlab2(null);
		dataRateCardVersionDetailData.setSlab3(null);
		dataRateCardVersionDetailData.setRevenueDetail(null);
		return dataRateCardVersionDetailData;
	}

	private static List<RncProfileDetailData> getBalanceQuotaProfileDetails() {
		return createBalanceQuotaProfileDetails();
	}

	private static List<RncProfileDetailData> createBalanceQuotaProfileDetails() {
		
		List<RncProfileDetailData> rncProfileDetailDatas = new ArrayList<RncProfileDetailData>();
		
		rncProfileDetailDatas.add(createQuotaDetail("1", 0));

		
		return rncProfileDetailDatas;
	}

	public static RncProfileData createThreeFUPLevelBalanceBasedQuotaProfileData() {
		
		RncProfileData balanceBasedQuotaProfileData = createRnCQuotaProfileData();
		List<RncProfileDetailData> threeLevelQuotaDetails = createThreeLevelQuotaDetails();
		threeLevelQuotaDetails.forEach(rncProfileDetailData -> rncProfileDetailData.setRncProfileData(balanceBasedQuotaProfileData));
		balanceBasedQuotaProfileData.setRncProfileDetailDatas(threeLevelQuotaDetails);
		
		return balanceBasedQuotaProfileData;
	}

	private static List<RncProfileDetailData> createThreeLevelQuotaDetails() {

		List<RncProfileDetailData> rncProfileDetailDatas = new ArrayList<RncProfileDetailData>();
		
		rncProfileDetailDatas.add(createQuotaDetail("1", 0));
		rncProfileDetailDatas.add(createQuotaDetail("2", 1));
		rncProfileDetailDatas.add(createQuotaDetail("3", 2));
		
		return rncProfileDetailDatas;
	}

	private static RncProfileDetailData createQuotaDetail(String id, int fupLevel) {
		
		Random random = new Random();
		RncProfileDetailData rncProfileDetailData = new RncProfileDetailData();
		
		rncProfileDetailData.setId(id);
		
		rncProfileDetailData.setBalance(100l);
		rncProfileDetailData.setTimeBalance(3600l);
		rncProfileDetailData.setTimeBalanceUnit("HOUR");
		rncProfileDetailData.setBalanceUnit("GB");

		rncProfileDetailData.setUsageLimitUnit("MB");
		rncProfileDetailData.setTimeLimitUnit("HOUR");
		rncProfileDetailData.setDailyUsageLimit(1000l);
		rncProfileDetailData.setDailyTimeLimit(10l);

		rncProfileDetailData.setWeeklyUsageLimit(5000l);
		rncProfileDetailData.setWeeklyTimeLimit(60l);
		
		rncProfileDetailData.setPulseVolume(new Long(random.nextInt()));
		rncProfileDetailData.setPulseVolumeUnit(DataUnit.BYTE.name());
		
		rncProfileDetailData.setPulseTime(new Long(random.nextInt()));
		rncProfileDetailData.setPulseTimeUnit(TimeUnit.SECOND.name());

		rncProfileDetailData.setRateUnit(UsageType.VOLUME.name());

		rncProfileDetailData.setVolumeCarryForwardLimit(1024l);
		rncProfileDetailData.setTimeCarryForwardLimit(1000l);

		DataServiceTypeData serviceType = createServiceTypeWithRatingGroup();
		rncProfileDetailData.setDataServiceTypeData(serviceType);
		rncProfileDetailData.setRatingGroupData(createRatingGroupData());
		rncProfileDetailData.setFupLevel(fupLevel);

		RevenueDetailData revenueDetailData= new RevenueDetailData();
		revenueDetailData.setName("RevenueDetail");
		revenueDetailData.setDescription("Revenue Detail");
		revenueDetailData.setRevenueDetailId("Revenue detail Id");
		rncProfileDetailData.setRevenueDetail(revenueDetailData);
		return rncProfileDetailData;
	}

	public static DataRateCard createExpectedDataMonetaryRateCard(DataRateCardData dataRateCardData) {
		DataRateCardVersionDetailData dataRateCardVersionDetailData = dataRateCardData.getDataRateCardVersionRelationData().get(0).getDataRateCardVersionDetailDataList().get(0);
		RateSlab rateSlab1 = new FlatSlab(dataRateCardVersionDetailData.getSlab1()
				, dataRateCardVersionDetailData.getPulse1(),
				dataRateCardVersionDetailData.getRate1(),
				Uom.fromVaue(dataRateCardData.getPulseUnit()),
				Uom.fromVaue(dataRateCardData.getRateUnit()));

		VersionDetail versionDetail = new FlatRating(dataRateCardVersionDetailData.getLabelKey1(),
				dataRateCardVersionDetailData.getLabelKey2(),
				Arrays.asList(rateSlab1), "");

		RateCardVersion rateCardVersion = new DataRateCardVersion(dataRateCardData.getId(), dataRateCardData.getName(), dataRateCardData.getDataRateCardVersionRelationData().get(0).getVersionName(), Arrays.asList(versionDetail));

		DataRateCard expectedDataRateCard = new DataRateCard(dataRateCardData.getId(),
				dataRateCardData.getName(),
				dataRateCardData.getLabelKey1(),
				dataRateCardData.getLabelKey2(),
				Arrays.asList(rateCardVersion),
				Uom.fromVaue(dataRateCardData.getPulseUnit()),
				Uom.fromVaue(dataRateCardData.getRateUnit()));
		return expectedDataRateCard;
	}

	public static QosProfileData createQoSProfileData() {
		QosProfileData qosProfileData = new QosProfileData();
		qosProfileData.setName("QosProfile");
		qosProfileData.setDescription("Description");
		PkgData pkgData = new PkgData();
		qosProfileData.setPkgData(pkgData);
		qosProfileData.setRncProfileData(null);
		qosProfileData.setRateCardData(createRateCardData());
		return qosProfileData;
	}

	public static PkgData newBasePackageWithRateCardAndPCCProfile(boolean isPCCRequired) {
		PkgData pkg = createBasePackageBasicInfo();
		DataRateCardData dataRateCardData = createRateCardData();
		dataRateCardData.setPkgData(pkg);
		List<QosProfileData> qosProfiles = getQosProfiles(pkg, dataRateCardData, isPCCRequired);
		pkg.setQosProfiles(qosProfiles);
		return pkg;
	}
}
