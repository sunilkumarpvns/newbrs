package com.elitecore.corenetvertex.pm.pkg;

import com.elitecore.acesstime.AccessTimePolicy;
import com.elitecore.corenetvertex.constants.AggregationKey;
import com.elitecore.corenetvertex.constants.BalanceLevel;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.CommonStatusValues;
import com.elitecore.corenetvertex.constants.CounterPresence;
import com.elitecore.corenetvertex.constants.IMSServiceAction;
import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.constants.PriorityLevel;
import com.elitecore.corenetvertex.constants.QCI;
import com.elitecore.corenetvertex.constants.QoSProfileAction;
import com.elitecore.corenetvertex.constants.QoSUnit;
import com.elitecore.corenetvertex.constants.QuotaProfileType;
import com.elitecore.corenetvertex.constants.RenewalIntervalUnit;
import com.elitecore.corenetvertex.constants.UsageType;
import com.elitecore.corenetvertex.core.constant.ChargingModes;
import com.elitecore.corenetvertex.pkg.PkgData;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pkg.constants.PCCAttribute;
import com.elitecore.corenetvertex.pkg.ims.PCCRuleAttributeAction;
import com.elitecore.corenetvertex.pkg.quota.QuotaProfileDetailData;
import com.elitecore.corenetvertex.pkg.rnc.QuotaUsageType;
import com.elitecore.corenetvertex.pkg.rnc.VolumeUnitType;
import com.elitecore.corenetvertex.pm.constants.FlowStatus;
import com.elitecore.corenetvertex.pm.constants.UsageMetering;
import com.elitecore.corenetvertex.pm.pkg.datapackage.AddOn;
import com.elitecore.corenetvertex.pm.pkg.datapackage.BasePackage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.EmergencyPackage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.PromotionalPackage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.conf.AddOnConf;
import com.elitecore.corenetvertex.pm.pkg.datapackage.conf.BasePackageConf;
import com.elitecore.corenetvertex.pm.pkg.datapackage.conf.DataRateCardConf;
import com.elitecore.corenetvertex.pm.pkg.datapackage.conf.GroupManageOrder;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.FailedUMBaseQoSProfileDetail;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.IPCANQoS;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.QoSProfile;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.QoSProfileDetail;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.SyCounterBaseQoSProfileDetail;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.UMBaseQoSProfileDetailImpl;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc.RateCardBasedQoSProfileDetail;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc.RnCBaseQoSProfileDetail;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc.ratecard.DataRateCard;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc.ratecard.DataRateCardVersion;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc.ratecard.FlatRating;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc.ratecard.RateCardVersion;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc.ratecard.RateSlab;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc.ratecard.VersionDetail;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.AllowedUsage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfile;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfileDetail;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.RncProfileDetail;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.SyCounterBaseQuotaProfileDetail;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.UMBaseQuotaProfile;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.UMBaseQuotaProfileDetail;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.conf.SyCounterQuotaProfileConf;
import com.elitecore.corenetvertex.pm.pkg.imspackage.IMSPackage;
import com.elitecore.corenetvertex.pm.pkg.imspackage.IMSServiceTable;
import com.elitecore.corenetvertex.pm.pkg.imspackage.MediaType;
import com.elitecore.corenetvertex.pm.pkg.imspackage.PCCAttributeTableEntry;
import com.elitecore.corenetvertex.pm.pkg.notification.QuotaLimitEvent;
import com.elitecore.corenetvertex.pm.pkg.notification.QuotaNotificationScheme;
import com.elitecore.corenetvertex.pm.pkg.notification.QuotaThresholdEvent;
import com.elitecore.corenetvertex.pm.pkg.notification.UsageNotificationScheme;
import com.elitecore.corenetvertex.pm.pkg.notification.UsageThresholdEvent;
import com.elitecore.exprlib.parser.expression.LogicalExpression;

import javax.annotation.Nullable;
import java.sql.Timestamp;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * This package factory is used to create Web Service specific policy related objects 
 * 
 * @author Jay Trivedi
 *
 */

public class PackageFactory {

	public BasePackage createBasePackage(BasePackageConf conf) {

		return new BasePackage(conf.getId(), conf.getName(), conf.getQuotaProfileType()
				, conf.getAvailabilityStatus(), conf.getQosProfiles(), conf.getUsageNotificationScheme()
				, conf.getPackageMode(), conf.getDescription(), conf.getPrice(), conf.getAvailabilityStartDate()
				, conf.getAvailabilityEndDate()
				, conf.getGroupIds()
				, conf.getStatus(), conf.getFailReason(), conf.getPartialFailReason(),conf.getParam1(), conf.getParam2(), conf.getQuotaNotificationScheme(),conf.getCurrency());
	}
	
	public AddOn createAddOn(AddOnConf conf) {
		return new com.elitecore.corenetvertex.pm.pkg.datapackage.AddOn(conf.getId(), conf.getName()
				, conf.getQuotaProfileType(), conf.getAvailabilityStatus(), conf.getQosProfiles()
				, conf.getMultipleSubscription(), conf.isExclusive()
				, conf.getValidityPeriod(), conf.getValidityPeriodUnit(), conf.getUsageNotificationScheme()
				, conf.getPackageMode(), conf.getDescription(), conf.getPrice(), conf.getAvailabilityStartDate()
				, conf.getAvailabilityEndDate()
				, conf.getGroupIds(), conf.getStatus()
				, conf.getFailReason(), conf.getPartialFailReason(),conf.getParam1(),conf.getParam2()
				, conf.getQuotaNotificationScheme(),conf.getCurrency());
	}
	
	public PromotionalPackage createPromotionalPackage(AddOnConf conf) {
		return new com.elitecore.corenetvertex.pm.pkg.datapackage.PromotionalPackage(conf.getId(), conf.getName()
				, conf.getQuotaProfileType(), conf.getAvailabilityStatus(), conf.getQosProfiles()
				, conf.getUsageNotificationScheme()
				, conf.getPackageMode(), conf.getDescription(), conf.getPrice(), conf.getAvailabilityStartDate()
				, conf.getAvailabilityEndDate()
				, conf.getGroupIds(), conf.getPreferPromotionalQoS(), conf.getStatus()
				, conf.getFailReason(), conf.getPartialFailReason(),conf.getParam1(),conf.getParam2(), conf.getPkgGroupOrderConfs());
	}

	public EmergencyPackage createEmergencyPackage(PkgData pkgData, String id, String name, PkgStatus availabilityStatus,
												   List<QoSProfile> qosProfiles
			, PkgMode packageMode
			, String description
			, Double price
			, Timestamp availabilityStartDate, Timestamp availabilityEndDate
			, List<String> groupIds, PolicyStatus status, String failReason,
			String partialFailReason,String param1, String param2, List<GroupManageOrder> pkgGroupOrderConfs) {
		
		return new EmergencyPackage(id, name, availabilityStatus, qosProfiles, packageMode, description, price, availabilityStartDate, availabilityEndDate, groupIds, status, failReason, partialFailReason,param1,param2, pkgGroupOrderConfs);
	}
	
	public SyCounterBaseQuotaProfileDetail createSyBasedQuotaProfileDetail(SyCounterQuotaProfileConf syCounterQuotaProfileConf) {
		return new SyCounterBaseQuotaProfileDetail(syCounterQuotaProfileConf.getQuotaProfileId(), syCounterQuotaProfileConf.getQuotaProfileName()
				, syCounterQuotaProfileConf.getPackageName(), syCounterQuotaProfileConf.getServiceId()
				, syCounterQuotaProfileConf.getServiceName(), syCounterQuotaProfileConf.getFupLevel());
	}

	public QuotaProfile createSyBaseQuotaProfile(String name, String pkgName, String id, QuotaProfileType quotaProfileType,
			List<Map<String, QuotaProfileDetail>> quotaProfileDetails) {
		return new QuotaProfile(name, pkgName, id, BalanceLevel.HSQ,0,RenewalIntervalUnit.MONTH, quotaProfileType, quotaProfileDetails, CommonStatusValues.DISABLE.isBooleanValue(),CommonStatusValues.DISABLE.isBooleanValue());
	}
	
	public UMBaseQuotaProfile createUMBaseQuotaProfile(String name, String pkgName, String id, int renewalInterval, RenewalIntervalUnit renewalIntervalUnit,
													   QuotaProfileType quotaProfileType,
													   List<Map<String, QuotaProfileDetail>> quotaProfileDetails, CounterPresence usagePresence, BalanceLevel balanceLevel) {
		return new UMBaseQuotaProfile(name, pkgName, id, renewalInterval, renewalIntervalUnit, quotaProfileType, quotaProfileDetails,usagePresence,balanceLevel);
	}
	
	public QuotaProfile createBalanceBasedQuotaProfile(String name, String pkgName, String id, BalanceLevel balanceLevel,
															  int renewalInterval, RenewalIntervalUnit renewalIntervalUnit,
															  QuotaProfileType quotaProfileType, List<Map<String, QuotaProfileDetail>> quotaProfileDetails,boolean proration, boolean carryForward) {
		return new QuotaProfile(name, pkgName, id, balanceLevel, renewalInterval, renewalIntervalUnit, quotaProfileType, quotaProfileDetails,proration, carryForward);
	}

	public RncProfileDetail createRnCQuotaProfileDetail(String quotaProfileId, String name, DataServiceType dataServiceType, int fupLevel,
														RatingGroup ratingGroup, Map<AggregationKey, AllowedUsage> aggregationKeyToAllowedUsage,
														long volumePulse, long timePulse, long volumePulseInBytes, long timePulseInSeconds, String volumePulseUnit, String timePulseUnit,
														double rate, UsageType usageType, QuotaUsageType quotaType, VolumeUnitType unitType, boolean isHsqLevel, String pccProfileName,
														long volumeCarryForwardLimit, long timeCarryForwardLimit, String revenueDetail) {

		return new RncProfileDetail(name, dataServiceType, fupLevel, ratingGroup, aggregationKeyToAllowedUsage, volumePulse, timePulse, volumePulseInBytes, timePulseInSeconds, volumePulseUnit, timePulseUnit
				, rate, usageType, quotaType, unitType, quotaProfileId, isHsqLevel, pccProfileName, volumeCarryForwardLimit, timeCarryForwardLimit, revenueDetail);
	}

    public RncProfileDetail createRnCTopUpQuotaProfileDetail(String quotaProfileId, String name, DataServiceType dataServiceType, Integer fupLevel,
															 RatingGroup ratingGroup, Map<AggregationKey, AllowedUsage> aggregationKeyWiseAllowedUsage,
															 Long pulseVolume, Long pulseTime, long volumePulseInBytes, long timePulseInSeconds,
															 String pulseVolumeUnit, String pulseTimeUnit, double rate, UsageType usageType,
															 QuotaUsageType quotaUsageType, VolumeUnitType volumeUnitType, boolean isHsqLevel, String pccProfileName,
															 long volumeCarryForwardLimit, long timeCarryForwardLimit, String revenueDetail) {
        return new RncProfileDetail(name, dataServiceType, fupLevel, ratingGroup, aggregationKeyWiseAllowedUsage, pulseVolume, pulseTime, volumePulseInBytes,
                timePulseInSeconds, pulseVolumeUnit, pulseTimeUnit
                , rate, usageType, quotaUsageType, volumeUnitType, quotaProfileId, isHsqLevel, pccProfileName,volumeCarryForwardLimit, timeCarryForwardLimit, revenueDetail);
    }
	
	public UMBaseQuotaProfileDetail createUMBaseQuotaProfileDetail(String quotaProfileId, String quotaProfileName, String serviceId, int fupLevel, String serviceName, List<QuotaProfileDetailData> quotaProfileDetailDatas) {
		
		Map<AggregationKey, AllowedUsage> aggregationKeyToAllowedUsage = new EnumMap<>(AggregationKey.class);
		for (QuotaProfileDetailData quotaProfileDetailData : quotaProfileDetailDatas) {
			
			AggregationKey aggregationKey = AggregationKey.valueOf(quotaProfileDetailData.getAggregationKey());
			aggregationKeyToAllowedUsage.put(aggregationKey, aggregationKey.createAllowedUsage(quotaProfileDetailData));
		}
		
		return new UMBaseQuotaProfileDetail(quotaProfileId, quotaProfileName, serviceId, fupLevel, serviceName, aggregationKeyToAllowedUsage);
	} 
	
	public QoSProfileDetail createSyCounterBaseQoSProfileDetail(String qosProfileName, 
			String pkgName,
			QoSProfileAction qosProfileAction, 
			String rejectCause, 
			SyCounterBaseQuotaProfileDetail allServiceQuotaProfileDetail,
			Map<String, QuotaProfileDetail> serviceToSyQuotaProfileDetail, 
			int fupLevel, 
			Integer orderNo,@Nullable String redirectURL) {

			if(redirectURL != null) {
				redirectURL = redirectURL.trim();
			}
		return new SyCounterBaseQoSProfileDetail(qosProfileName, pkgName, qosProfileAction, rejectCause, allServiceQuotaProfileDetail, serviceToSyQuotaProfileDetail, fupLevel, orderNo, redirectURL);
	}
	
	public QoSProfileDetail createSyCounterBaseQoSProfileDetail(String qosProfileName, 
			String pkgName, 
			QoSProfileAction qosProfileAction,
			String reason, 
			int fupLevel, 
			SyCounterBaseQuotaProfileDetail allServiceQuotaProfileDetail,
			Map<String, QuotaProfileDetail> serviceToSyQuotaProfileDetail, 
			IPCANQoS sessionQoS, 
			List<PCCRule> pccRules,
			boolean usageMonitoring, 
			SliceInformation sliceInformation, 
			Integer orderNo,@Nullable String redirectURL, List<ChargingRuleBaseName> chargingRuleBaseNames) {

			if(redirectURL != null) {
				redirectURL = redirectURL.trim();
			}
		return new SyCounterBaseQoSProfileDetail(qosProfileName, pkgName, qosProfileAction, reason, fupLevel
				, allServiceQuotaProfileDetail, serviceToSyQuotaProfileDetail, sessionQoS, pccRules, usageMonitoring, sliceInformation, orderNo, redirectURL, chargingRuleBaseNames);
	}

	public QoSProfileDetail createFailedUMBaseQoSProfileDetail(
			String qosProfileName, 
			String packageName, QoSProfileAction qosProfileAction, String rejectCause,
			int fupLevel, int orderNo,@Nullable String redirectURL) {

			if(redirectURL != null) {
				redirectURL = redirectURL.trim();
			}

		return new FailedUMBaseQoSProfileDetail(qosProfileName, packageName, qosProfileAction, rejectCause, fupLevel, orderNo, redirectURL);
	}

	public QoSProfileDetail createFailedUMBaseQoSProfileDetail(String qosProfileName, String pkgName, QoSProfileAction qosProfileAction,
			String rejectCause, int fupLevel, IPCANQoS sessionQoS, 
			List<PCCRule> pccRules, boolean usageMonitoring, SliceInformation sliceInformation,@Nullable String redirectURL,List<ChargingRuleBaseName> chargingRuleBaseNames) {
			if(redirectURL != null) {
				redirectURL = redirectURL.trim();
			}
		
		return new FailedUMBaseQoSProfileDetail(qosProfileName, pkgName, qosProfileAction, rejectCause, fupLevel, sessionQoS, pccRules, usageMonitoring, sliceInformation, redirectURL, chargingRuleBaseNames);
	}

	public QoSProfileDetail createUMBaseQoSProfileDetailForActionReject(String qosProfileName, String packageName, QoSProfileAction qosProfileAction, String reason,
			UMBaseQuotaProfileDetail allServiceQuotaProfileDetail, Map<String, QuotaProfileDetail> serviceToUMQuotaProfileDetail,
			boolean isUsageRequired, int fupLevel, Integer orderNo, boolean isApplicableOnUsageUnavailability,@Nullable String redirectURL) {

			if(redirectURL != null) {
				redirectURL = redirectURL.trim();
			}
		return new UMBaseQoSProfileDetailImpl(qosProfileName, packageName, qosProfileAction, reason, allServiceQuotaProfileDetail
				, serviceToUMQuotaProfileDetail, isUsageRequired, fupLevel, orderNo, isApplicableOnUsageUnavailability, redirectURL);
	}
	
	public QoSProfileDetail createUMBaseQoSProfileDetail(String qosProfileName, String pkgName, QoSProfileAction qosProfileAction, String reason,
														 int fupLevel, UMBaseQuotaProfileDetail allServiceQuotaProfileDetail, Map<String, QuotaProfileDetail> serviceToUMQuotaProfileDetail,
														 boolean isUsageRequired, IPCANQoS sessionQoS, List<PCCRule> pccRules, boolean usageMonitoring, SliceInformation sliceInformation,
														 Integer orderNo, boolean isApplicableOnUsageUnavailability, @Nullable String redirectURL, List<ChargingRuleBaseName> chargingRuleBaseNames) {

			if(redirectURL != null) {
				redirectURL = redirectURL.trim();
			}

		return new UMBaseQoSProfileDetailImpl(qosProfileName, pkgName, qosProfileAction, reason, fupLevel, allServiceQuotaProfileDetail
				, serviceToUMQuotaProfileDetail, isUsageRequired, sessionQoS, pccRules, usageMonitoring, sliceInformation, orderNo, isApplicableOnUsageUnavailability, redirectURL, chargingRuleBaseNames);
	}
	
	public QoSProfile createQoSProfile(String id, String name, String packageName, String packageId, @Nullable QuotaProfile quotaProfile, DataRateCard dataRateCard, List<String> accessNetwork, int duration,
									   QoSProfileDetail hsqLevelQoSDetail, List<QoSProfileDetail> fupLevelQoSDetails, LogicalExpression logicalExpression,
									   String additionalCondition, AccessTimePolicy accessTimePolicy) {
		
		return new QoSProfile(id, name, packageName, packageId, quotaProfile, dataRateCard,
				accessNetwork, duration, hsqLevelQoSDetail, fupLevelQoSDetails, logicalExpression, additionalCondition, accessTimePolicy);
	}

	public UsageNotificationScheme createUsageNotificationScheme(List<List<UsageThresholdEvent>> usageThresholdEvents) {
		
		return new UsageNotificationScheme(usageThresholdEvents);
	}

	public IMSPackage createIMSBasePackage(String id, String name, Map<Long, List<IMSServiceTable>> serviceIdentifierToServiceTables,
                                           PkgMode packageMode, PkgStatus availabilityStatus, Double price, List<String> eligibleGroupIds) {
		return new IMSPackage(id, name, serviceIdentifierToServiceTables, packageMode, availabilityStatus, price, eligibleGroupIds);
	}
	
	public IMSServiceTable createIMSServiceTable(String name, 
			MediaType serviceType, 
			String afAppId, 
			LogicalExpression logicalExpression, 
			String expressionStr,
			IMSServiceAction action,
			Map<PCCAttribute, List<PCCAttributeTableEntry>> pccAttributeToTableEntries) {
		
		return new IMSServiceTable(name, serviceType, afAppId, logicalExpression, expressionStr, action, pccAttributeToTableEntries);
	}
	
	public PCCAttributeTableEntry createPCCAttributeTableEntry(PCCAttribute attribute, PCCRuleAttributeAction action, String value, String expressionStr, LogicalExpression expression) {
		return new PCCAttributeTableEntry(attribute, action, value, expressionStr, expression);
	}

	public MediaType createMediaType(String id, long identifier, String name) {
		return new MediaType(id, identifier, name);
	}

	public PCCRule createPCCRule(String id, String name, short precedence, Long ratingGroup, String ratingGroupId, String appServiceProviderId, String sponsorIdentity,
								 UsageMetering usageMetering, QCI qci, long gbrdlInBytes, long gbrulInBytes, long mbrdlInBytes, long mbrulInBytes,
								 QoSUnit gbrdlUnit, QoSUnit gbrulUnit, QoSUnit mbrdlUnit, QoSUnit mbrulUnit,
								 long gbrdl, long gbrul, long mbrdl, long mbrul, PriorityLevel priorityLevel,
								 Boolean preCapability, Boolean preVulnerability, boolean dynamic, List<String> serviceDataFlows, ChargingModes chargingMode,
								 FlowStatus flowStatus, String monitoringKey, long serviceIdentifier, String serviceName, String serviceTypeID, SliceInformation sliceInformation,
								 int fupLevel) {
		
		return new PCCRuleImpl(id, name, precedence, ratingGroup, ratingGroupId, appServiceProviderId, sponsorIdentity, usageMetering
				, qci, gbrdlInBytes, gbrulInBytes, mbrdlInBytes, mbrulInBytes, gbrdlUnit, gbrulUnit, mbrdlUnit, mbrulUnit,
				 gbrdl, gbrul, mbrdl, mbrul,priorityLevel, preCapability, preVulnerability, dynamic, serviceDataFlows
				, chargingMode, flowStatus, monitoringKey, serviceIdentifier, serviceName, serviceTypeID, sliceInformation, fupLevel);
	}

	public ChargingRuleBaseName createChargingRuleBaseName(String id, String name, Map<String,DataServiceType> monitoringKeyServiceTypeMap, int fupLevel, Map<String,SliceInformation> monitoringKeySliceInformationMap) {
		return new ChargingRuleBaseName(id, name, monitoringKeyServiceTypeMap, fupLevel,monitoringKeySliceInformationMap);
	}

	public DataServiceType createServiceType(String id, String name, Long serviceIdentifier, List<String> serviceDataFlowList,
                                             List<RatingGroup> ratingGroupList) {
		return new DataServiceType(id, name, serviceIdentifier, serviceDataFlowList, ratingGroupList);
	}

	public RatingGroup createRatingGroup(String id, String name, String description, Long identifier) {
		return new RatingGroup(id, name, description, identifier);
	}

	public IMSPackage createIMSPackage(String id, String name, PkgMode packageMode, PkgStatus availabilityStatus,
                                       PolicyStatus policyStatus, String failReason, String partialFailReason, Double price, List<String> groupIds) {
		return new IMSPackage(id, name, packageMode, availabilityStatus, policyStatus, failReason, partialFailReason, price, groupIds);
	}


	protected long multiplyQos(long qos, Double multiplier) {

		if (qos == 0) {
			return 0;
		}
		
		if (multiplier == null) {
			return qos;
		}
		
		qos  = (long)(qos * multiplier);
		
		if (qos > CommonConstants.UNSIGNED32_MAX_VALUE) {
			qos = CommonConstants.UNSIGNED32_MAX_VALUE;
		}

		return qos;
	}

	public QoSProfileDetail createRnCBaseQoSProfileDetail(String qosProfileName,
														  String pkgName,
														  int fupLevel,
														  RncProfileDetail allServiceQuotaProfileDetail,
														  Map<String, QuotaProfileDetail> serviceToQuotaProfileDetail,
														  IPCANQoS sessionQoS,
														  List<PCCRule> pccRules,
														  Integer orderNo,
														  String redirectURL,
														  boolean applyOnUsageUnavailablity,
														  List<ChargingRuleBaseName> chargingRuleBaseNames) {
		return new RnCBaseQoSProfileDetail(qosProfileName,
				pkgName,
				fupLevel,
				allServiceQuotaProfileDetail,
				serviceToQuotaProfileDetail,
				sessionQoS,
				pccRules,
				orderNo,
				redirectURL,
				chargingRuleBaseNames);
	}

	public QoSProfileDetail createRateCardBaseQoSProfileDetail(String qosProfileName,
														  String pkgName,
														  DataRateCard dataRateCard,
														  IPCANQoS sessionQoS,
														  List<PCCRule> pccRules,
														  String redirectURL,
														  List<ChargingRuleBaseName> chargingRuleBaseNames) {
		return new RateCardBasedQoSProfileDetail(qosProfileName,
				pkgName,
				dataRateCard,
				sessionQoS,
				pccRules,
				redirectURL,
				chargingRuleBaseNames);
	}

	public QoSProfileDetail createRnCBaseQoSProfileDetailForActionReject(String qosProfileName,
																		 String packageName,
																		 QoSProfileAction qosProfileAction,
																		 String reason,
																		 RncProfileDetail allServiceQuotaProfileDetail,
																		 Map<String, QuotaProfileDetail> serviceToQuotaProfileDetail,
																		int fupLevel,
																		 Integer orderNo,
																		 boolean applyOnUsageUnavailablity,
																		 String redirectURL) {
		if(redirectURL != null) {
			redirectURL = redirectURL.trim();
		}
		return new RnCBaseQoSProfileDetail(qosProfileName,
				packageName,
				qosProfileAction,
				reason,
				allServiceQuotaProfileDetail,
				serviceToQuotaProfileDetail,
				fupLevel,
				orderNo,
				redirectURL);
	}

	public QoSProfileDetail createRateCardBaseQoSProfileDetailForActionReject(String qosProfileName,
																		 String packageName,
																		 QoSProfileAction qosProfileAction,
																		 String reason,
																		 DataRateCard dataRateCard,
																		 String redirectURL) {
		if(redirectURL != null) {
			redirectURL = redirectURL.trim();
		}
		return new RateCardBasedQoSProfileDetail(qosProfileName,
				packageName,
				qosProfileAction,
				reason,
				dataRateCard,
				redirectURL);
	}


    public QuotaNotificationScheme createQuotaNotificationScheme(List<List<QuotaThresholdEvent>> quotaThresoldEvents, List<List<QuotaLimitEvent>> quotaLimitEvents) {
        return new QuotaNotificationScheme(quotaThresoldEvents, quotaLimitEvents);
    }

	public DataRateCard createDataRateCard(DataRateCardConf conf) {
		return new DataRateCard(conf.getId(), conf.getName(), conf.getKeyOne(), conf.getKeyTwo(), conf.getDataRateCardVersions(),
				conf.getPulseUom(), conf.getRateUom());
	}

    public RateCardVersion createDataRateCardVersion(String rateCardId, String rateCardName, String versionName, List<VersionDetail> versionDetails) {
        return new DataRateCardVersion(rateCardId, rateCardName, versionName, versionDetails);
    }

    public VersionDetail createFlatRatingVersionDetail(String keyValueOne, String keyValueTwo, List<RateSlab> rateSlabs, String revenueDetail) {
        return new FlatRating(keyValueOne, keyValueTwo, rateSlabs, revenueDetail);
    }
}