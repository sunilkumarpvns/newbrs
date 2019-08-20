package com.elitecore.corenetvertex.pm.pkg.factory;

import com.elitecore.corenetvertex.constants.AggregationKey;
import com.elitecore.corenetvertex.constants.BalanceLevel;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.DataUnit;
import com.elitecore.corenetvertex.constants.QuotaProfileType;
import com.elitecore.corenetvertex.constants.RenewalIntervalUnit;
import com.elitecore.corenetvertex.constants.TimeUnit;
import com.elitecore.corenetvertex.constants.UsageType;
import com.elitecore.corenetvertex.pkg.PkgType;
import com.elitecore.corenetvertex.pkg.dataservicetype.DataServiceTypeData;
import com.elitecore.corenetvertex.pkg.ratinggroup.RatingGroupData;
import com.elitecore.corenetvertex.pkg.rnc.QuotaUsageType;
import com.elitecore.corenetvertex.pkg.rnc.RncProfileData;
import com.elitecore.corenetvertex.pkg.rnc.RncProfileDetailData;
import com.elitecore.corenetvertex.pkg.rnc.VolumeUnitType;
import com.elitecore.corenetvertex.pm.pkg.PackageFactory;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.AllowedUsage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfile;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfileDetail;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.RncProfileDetail;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

import static com.elitecore.commons.base.Collectionz.isNullOrEmpty;
import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.elitecore.corenetvertex.constants.CommonConstants.QUOTA_UNDEFINED;
import static com.elitecore.corenetvertex.constants.CommonConstants.UNLIMITED_QCF_QUOTA;

/**
 * 
 * @author Jay Trivedi
 *
 */
public class RncProfileFactory {

	private static final String MODULE = "RnC-FCTRY";
	public static final String TOTAL = "Total";
	public static final String DOWNLOAD = "Download";
	public static final String UPLOAD = "Upload";
	public static final String TIME = "Time";
	public static final String FAILMESSAGE_DAILYCOUNTERLIMIT = "Daily %s quota defined but Billing Cycle %s quota is undefined";
	public static final String FAILMEESAGE_WEEKLYCOUNTERLIMIT = "Weekly %s quota defined but Billing Cycle %s quota is undefined";

	private PackageFactory packageFactory;
	private RatingGroupFactory ratingGroupFactory;
	private DataServiceTypeFactory dataServiceTypeFactory;

	public RncProfileFactory(PackageFactory packageFactory, RatingGroupFactory ratingGroupFactory, DataServiceTypeFactory dataServiceTypeFactory) {
		
		this.packageFactory = packageFactory;
		this.ratingGroupFactory = ratingGroupFactory;
		this.dataServiceTypeFactory = dataServiceTypeFactory;
	}

	public QuotaProfile create(RncProfileData rncProfileData, String pccProfileName, List<String> quotaProfileFailReasons) {

		if (isNullOrEmpty(rncProfileData.getRncProfileDetailDatas())) {
			quotaProfileFailReasons.add("No quota profile details configured for quota profile: " + rncProfileData.getName());
			return null;
		}

        boolean isBasePackageQuotaProfile = PkgType.BASE.name().equalsIgnoreCase(rncProfileData.getPkgData().getType());

		TreeMap<Integer,Map<String, QuotaProfileDetail>> fupLevelServiceWiseQuotaProfileDetailsMap = new TreeMap<Integer, Map<String,QuotaProfileDetail>>();
		
		for (RncProfileDetailData quotaProfileDetailData : rncProfileData.getRncProfileDetailDatas()) {
			
			List<String> failReasons = new ArrayList<String>();
			validateQuotaProfileDetailData(quotaProfileDetailData, failReasons);
			
			if (failReasons.isEmpty() == false) {
				quotaProfileFailReasons.add("Quota Profile Detail(" + quotaProfileDetailData.getId() + ") parsing fail. Cause by: "
						+ FactoryUtils.format(failReasons));
				continue;
			}
			
			Integer fupLevel = quotaProfileDetailData.getFupLevel();
			Map<String, QuotaProfileDetail> serviceWiseMap = fupLevelServiceWiseQuotaProfileDetailsMap.get(fupLevel);
			
			if (serviceWiseMap == null) {
				serviceWiseMap = new HashMap<String, QuotaProfileDetail>();
			}
			
			String serviceTypeId = quotaProfileDetailData.getDataServiceTypeData().getId();
			
			if (serviceWiseMap.get(serviceTypeId) != null) {
				quotaProfileFailReasons.add("Quota Profile Detail(" + quotaProfileDetailData.getId() + " parsing fail. Cause by: Same serviceType: " + quotaProfileDetailData.getDataServiceTypeData().getName()
						+ " cannot be configured more than one time at same fup level: " + fupLevel);
				continue;
			}

			Map<AggregationKey, AllowedUsage> aggregationKeyWiseAllowedUsage = createAggregationKeyWiseAllowedUsage(quotaProfileDetailData);
			
			long volumePulseInBytes = 0;
			long timePulseInSeconds = 0;
			if (quotaProfileDetailData.getPulseVolume() != null) {
				volumePulseInBytes = DataUnit.fromName(quotaProfileDetailData.getPulseVolumeUnit()).toBytes(quotaProfileDetailData.getPulseVolume());
			}
			
			if (quotaProfileDetailData.getPulseTime() != null) {
				timePulseInSeconds = TimeUnit.fromVal(quotaProfileDetailData.getPulseTimeUnit()).toSeconds(quotaProfileDetailData.getPulseTime());
			}

			double rate = quotaProfileDetailData.getRate() == null ? 0  : quotaProfileDetailData.getRate().doubleValue();

            boolean isValid = validateRateUnit(rate, quotaProfileDetailData.getRateUnit(), quotaProfileDetailData.getRncProfileData().getQuotaType());
            if (isValid == false) {
				quotaProfileFailReasons.add("Quota Profile Detail(" + quotaProfileDetailData.getId() + " parsing fail. Cause by: Rate unit: " + quotaProfileDetailData.getRateUnit()
						+ " does not match with Quota type: " + quotaProfileDetailData.getRncProfileData().getQuotaType());
				continue;
			}

			BalanceLevel balanceLevel = BalanceLevel.valueOf(quotaProfileDetailData.getRncProfileData().getBalanceLevel());

            boolean isHSQLevel = balanceLevel.fupLevel == quotaProfileDetailData.getFupLevel();

            validateCarryForwardLimit(quotaProfileDetailData);

			RncProfileDetail rnCQuotaProfileDetail;
            if (isBasePackageQuotaProfile) {
                rnCQuotaProfileDetail = packageFactory.createRnCTopUpQuotaProfileDetail(rncProfileData.getId()
                        , rncProfileData.getName(), dataServiceTypeFactory.createServiceType(quotaProfileDetailData.getDataServiceTypeData()), fupLevel
                        , ratingGroupFactory.createRatingGroup(quotaProfileDetailData.getRatingGroupData()), aggregationKeyWiseAllowedUsage
                        , quotaProfileDetailData.getPulseVolume(), quotaProfileDetailData.getPulseTime(), volumePulseInBytes, timePulseInSeconds
                        , quotaProfileDetailData.getPulseVolumeUnit()
                        , quotaProfileDetailData.getPulseTimeUnit(), rate, UsageType.valueOf(quotaProfileDetailData.getRateUnit()),
                        QuotaUsageType.valueOf(quotaProfileDetailData.getRncProfileData().getQuotaType()),
                        VolumeUnitType.valueOf(quotaProfileDetailData.getRncProfileData().getUnitType()), isHSQLevel, pccProfileName,
						quotaProfileDetailData.getVolumeCarryForwardLimit(),
						quotaProfileDetailData.getTimeCarryForwardLimit(),Objects.nonNull(quotaProfileDetailData.getRevenueDetail())?quotaProfileDetailData.getRevenueDetail().getName():null);
            } else {
                rnCQuotaProfileDetail = packageFactory.createRnCQuotaProfileDetail(rncProfileData.getId()
                        , rncProfileData.getName(), dataServiceTypeFactory.createServiceType(quotaProfileDetailData.getDataServiceTypeData()), fupLevel
                        , ratingGroupFactory.createRatingGroup(quotaProfileDetailData.getRatingGroupData()), aggregationKeyWiseAllowedUsage
                        , quotaProfileDetailData.getPulseVolume(), quotaProfileDetailData.getPulseTime(), volumePulseInBytes, timePulseInSeconds
                        , quotaProfileDetailData.getPulseVolumeUnit()
                        , quotaProfileDetailData.getPulseTimeUnit(), rate, UsageType.valueOf(quotaProfileDetailData.getRateUnit()),
                        QuotaUsageType.valueOf(quotaProfileDetailData.getRncProfileData().getQuotaType()),
                        VolumeUnitType.valueOf(quotaProfileDetailData.getRncProfileData().getUnitType()), isHSQLevel, pccProfileName,
						quotaProfileDetailData.getVolumeCarryForwardLimit(),
						quotaProfileDetailData.getTimeCarryForwardLimit(),Objects.nonNull(quotaProfileDetailData.getRevenueDetail())?quotaProfileDetailData.getRevenueDetail().getName():null);
            }

            serviceWiseMap.put(serviceTypeId, rnCQuotaProfileDetail);
			fupLevelServiceWiseQuotaProfileDetailsMap.put(fupLevel, serviceWiseMap);
		}
		
		if (fupLevelServiceWiseQuotaProfileDetailsMap.isEmpty()) {
			return null;
		}
		
		List<Map<String, QuotaProfileDetail>> fupLevelWiseQuotaProfileDetail = createFUPLevelWiseQuotaProfileDetail(rncProfileData, quotaProfileFailReasons, fupLevelServiceWiseQuotaProfileDetailsMap);

		if (RenewalIntervalUnit.fromRenewalIntervalUnit(rncProfileData.getRenewalIntervalUnit())==null) {
			quotaProfileFailReasons.add("Invalid Renewal Interval Value set for Quota profile: " + rncProfileData.getName());
		}

		if (quotaProfileFailReasons.isEmpty() == false) {
			getLogger().info(MODULE, "Skip to create Quota profie for :" + rncProfileData.getName() + ". Reason: "
					+ FactoryUtils.format(quotaProfileFailReasons));
			return null;
		} 

		return packageFactory.createBalanceBasedQuotaProfile(rncProfileData.getName(), rncProfileData.getPkgData().getName()
				, rncProfileData.getId(), BalanceLevel.fromName(rncProfileData.getBalanceLevel())
				,rncProfileData.getRenewalInterval()==null?0:rncProfileData.getRenewalInterval()
				, RenewalIntervalUnit.fromRenewalIntervalUnit(rncProfileData.getRenewalIntervalUnit()), QuotaProfileType.RnC_BASED
				, fupLevelWiseQuotaProfileDetail, rncProfileData.getProration() == null ? false : rncProfileData.getProration()
				, rncProfileData.getCarryForward() == null ? false : rncProfileData.getCarryForward());
	}

	private void validateCarryForwardLimit(RncProfileDetailData quotaProfileDetailData) {
		if(Objects.isNull(quotaProfileDetailData.getVolumeCarryForwardLimit())){
			quotaProfileDetailData.setVolumeCarryForwardLimit(UNLIMITED_QCF_QUOTA);
		}
		if(Objects.isNull(quotaProfileDetailData.getTimeCarryForwardLimit())){
			quotaProfileDetailData.setTimeCarryForwardLimit(UNLIMITED_QCF_QUOTA);
		}
	}

	private boolean validateRateUnit(double rate, String rateUnit, String quotaType) {

		if(rate <= 0) {
			return true;
		}

		if(Objects.equals(quotaType, QuotaUsageType.HYBRID.name())) {
			return true;
		}

		if(Objects.equals(quotaType, QuotaUsageType.VOLUME.name())) {
			return Objects.equals(rateUnit, UsageType.VOLUME.name());
		} else {
			return Objects.equals(rateUnit, UsageType.TIME.name());
		}
	}


	private static void validateQuotaProfileDetailData(RncProfileDetailData quotaProfileDetailData, List<String> failReasons) {

		DataServiceTypeData serviceType = quotaProfileDetailData.getDataServiceTypeData();
		if (serviceType == null) {
			failReasons.add("No data service type configured for quota profile detail: " + quotaProfileDetailData.getId());
		}
		
		RatingGroupData ratingGroupData = quotaProfileDetailData.getRatingGroupData();
		
		if (ratingGroupData == null) {
			failReasons.add("Rating group is not configured in quota profile detail: " + quotaProfileDetailData.getId());
		}

		validateQuota(quotaProfileDetailData, failReasons);


	}


	private static void validateQuota(RncProfileDetailData quotaProfileDetailData, List<String> failReasons) {

		boolean isBillingCycleTotalUndefined = false;
		boolean isBillingCycleTimeUndefined = false;

		boolean isCarryForwardVolumeUndefined = false;
		boolean isCarryForwardTimeUndefined = false;

		if(Objects.equals(QuotaUsageType.TIME.name(), quotaProfileDetailData.getRncProfileData().getQuotaType())) {
			isBillingCycleTotalUndefined = true;
			isCarryForwardVolumeUndefined = true;
		}

		if(Objects.equals(QuotaUsageType.VOLUME.name(), quotaProfileDetailData.getRncProfileData().getQuotaType())) {
			isBillingCycleTimeUndefined = true;
			isCarryForwardTimeUndefined = true;
		}

		if(Objects.isNull(quotaProfileDetailData.getPulseVolume())) {
			quotaProfileDetailData.setPulseVolume(1l);
			quotaProfileDetailData.setPulseVolumeUnit(DataUnit.BYTE.name());
		}

		if(Objects.isNull(quotaProfileDetailData.getPulseTime())) {
			quotaProfileDetailData.setPulseTime(1l);
			quotaProfileDetailData.setPulseTimeUnit(TimeUnit.SECOND.name());
		}

		if (isBillingCycleTotalUndefined) {
			quotaProfileDetailData.setBalance(QUOTA_UNDEFINED);
		} else {
			if (quotaProfileDetailData.getBalance() == null) {
				quotaProfileDetailData.setBalance(CommonConstants.QUOTA_UNLIMITED);
			}
		}

		if(isBillingCycleTimeUndefined) {
			quotaProfileDetailData.setTimeBalance(QUOTA_UNDEFINED);
		} else {
			if (quotaProfileDetailData.getTimeBalance() == null) {
				quotaProfileDetailData.setTimeBalance(CommonConstants.QUOTA_UNLIMITED);
			}
		}

		if(isCarryForwardVolumeUndefined){
			quotaProfileDetailData.setVolumeCarryForwardLimit(QUOTA_UNDEFINED);
		} else {
			if (quotaProfileDetailData.getTimeCarryForwardLimit() == null) {
				quotaProfileDetailData.setTimeCarryForwardLimit(CommonConstants.QUOTA_UNLIMITED);
			}
		}

		if(isCarryForwardTimeUndefined){
			quotaProfileDetailData.setTimeCarryForwardLimit(QUOTA_UNDEFINED);
		} else {
			if (quotaProfileDetailData.getVolumeCarryForwardLimit() == null) {
				quotaProfileDetailData.setVolumeCarryForwardLimit(CommonConstants.QUOTA_UNLIMITED);
			}
		}

		if (isBillingCycleTotalUndefined && isBillingCycleTimeUndefined) {
			failReasons.add("Billing cycle Total/Download/Upload/Time quota are undefined");
		}

		validateCounterLimits(quotaProfileDetailData, isBillingCycleTotalUndefined, isBillingCycleTimeUndefined);

	}


	private static void validateCounterLimits(RncProfileDetailData quotaProfileDetailData, boolean isBillingCycleUploadUndefined, boolean isBillingCycleTimeUndefined) {
    /*
        If Counter limit is defined, Respective bifurcation value should be defined in Billing Cycle Quotas
     */
		validateDailyCounterLimits(quotaProfileDetailData, isBillingCycleUploadUndefined, isBillingCycleTimeUndefined);

		validateWeeklyCounterLimits(quotaProfileDetailData, isBillingCycleUploadUndefined, isBillingCycleTimeUndefined);
	}

	private static void validateWeeklyCounterLimits(RncProfileDetailData quotaProfileDetailData, boolean isBillingCycleTotalUndefined, boolean isBillingCycleTimeUndefined) {
		if (isBillingCycleTotalUndefined) {
			quotaProfileDetailData.setWeeklyUsageLimit(QUOTA_UNDEFINED);
		} else {
			if (quotaProfileDetailData.getWeeklyUsageLimit() == null) {
				quotaProfileDetailData.setWeeklyUsageLimit(CommonConstants.QUOTA_UNLIMITED);
			}
		}

		if(isBillingCycleTimeUndefined) {
			quotaProfileDetailData.setWeeklyTimeLimit(QUOTA_UNDEFINED);
		} else {
			if (quotaProfileDetailData.getWeeklyTimeLimit() == null) {
				quotaProfileDetailData.setWeeklyTimeLimit(CommonConstants.QUOTA_UNLIMITED);
			}
		}
	}

	private static void validateDailyCounterLimits(RncProfileDetailData quotaProfileDetailData, boolean isBillingCycleTotalUndefined, boolean isBillingCycleTimeUndefined) {

		if (isBillingCycleTotalUndefined) {
			quotaProfileDetailData.setDailyUsageLimit(QUOTA_UNDEFINED);
		} else {
			if (quotaProfileDetailData.getDailyUsageLimit() == null) {
				quotaProfileDetailData.setDailyUsageLimit(CommonConstants.QUOTA_UNLIMITED);
			}
		}

		if(isBillingCycleTimeUndefined) {
			quotaProfileDetailData.setDailyTimeLimit(QUOTA_UNDEFINED);
		} else {
			if (quotaProfileDetailData.getDailyTimeLimit() == null) {
				quotaProfileDetailData.setDailyTimeLimit(CommonConstants.QUOTA_UNLIMITED);
			}
		}
	}

	private List<Map<String, QuotaProfileDetail>> createFUPLevelWiseQuotaProfileDetail(RncProfileData rncProfileData,
			List<String> quotaProfileFailReasons, TreeMap<Integer, Map<String, QuotaProfileDetail>> fupLevelServiceWiseQuotaProfileDetailsMap) {
		
		List<Map<String, QuotaProfileDetail>> fupLevelserviceWiseQuotaProfileDetails = new ArrayList<Map<String,QuotaProfileDetail>>();
		
		for (int fupLevel = 0; fupLevel <= fupLevelServiceWiseQuotaProfileDetailsMap.lastKey(); fupLevel++) {
			
			Map<String, QuotaProfileDetail> serviceWiseQuota = fupLevelServiceWiseQuotaProfileDetailsMap.get(fupLevel);
			if (serviceWiseQuota == null) {
				quotaProfileFailReasons.add("Level " + fupLevel + " not configured in quota profile: " +  rncProfileData.getName());
				continue;
			} else { 
				fupLevelserviceWiseQuotaProfileDetails.add(serviceWiseQuota);
			}
		}
		return fupLevelserviceWiseQuotaProfileDetails;
	}

	private Map<AggregationKey, AllowedUsage> createAggregationKeyWiseAllowedUsage(RncProfileDetailData quotaProfileDetailData) {
		Map<AggregationKey, AllowedUsage> aggregationKeyWiseAllowedUsage = new EnumMap<>(AggregationKey.class);
		
		for (AggregationKey aggregationKey : AggregationKey.values()) {
			
			if (aggregationKey == AggregationKey.CUSTOM) {
				continue;
			}
			
			aggregationKeyWiseAllowedUsage.put(aggregationKey, aggregationKey.createAllowedUsage(quotaProfileDetailData));
		}
		return aggregationKeyWiseAllowedUsage;
	}
}
