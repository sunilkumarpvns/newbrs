package com.elitecore.netvertex.pm;

import com.elitecore.acesstime.AccessTimePolicy;
import com.elitecore.corenetvertex.constants.AggregationKey;
import com.elitecore.corenetvertex.constants.BalanceLevel;
import com.elitecore.corenetvertex.constants.IMSServiceAction;
import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.constants.QoSProfileAction;
import com.elitecore.corenetvertex.constants.QuotaProfileType;
import com.elitecore.corenetvertex.constants.RenewalIntervalUnit;
import com.elitecore.corenetvertex.constants.UsageType;
import com.elitecore.corenetvertex.pkg.PkgData;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pkg.constants.PCCAttribute;
import com.elitecore.corenetvertex.pkg.ims.PCCRuleAttributeAction;
import com.elitecore.corenetvertex.pkg.quota.QuotaProfileDetailData;
import com.elitecore.corenetvertex.pkg.rnc.QuotaUsageType;
import com.elitecore.corenetvertex.pkg.rnc.VolumeUnitType;
import com.elitecore.corenetvertex.pm.pkg.ChargingRuleBaseName;
import com.elitecore.corenetvertex.pm.pkg.DataServiceType;
import com.elitecore.corenetvertex.pm.pkg.PCCRule;
import com.elitecore.corenetvertex.pm.pkg.RatingGroup;
import com.elitecore.corenetvertex.pm.pkg.SliceInformation;
import com.elitecore.corenetvertex.pm.pkg.datapackage.conf.AddOnConf;
import com.elitecore.corenetvertex.pm.pkg.datapackage.conf.BasePackageConf;
import com.elitecore.corenetvertex.pm.pkg.datapackage.conf.DataRateCardConf;
import com.elitecore.corenetvertex.pm.pkg.datapackage.conf.GroupManageOrder;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.IPCANQoS;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.QoSProfile;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc.ratecard.DataRateCard;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc.ratecard.RateCardVersion;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc.ratecard.RateSlab;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc.ratecard.VersionDetail;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.AllowedUsage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfileDetail;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.RncProfileDetail;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.SyCounterBaseQuotaProfileDetail;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.UMBaseQuotaProfileDetail;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.conf.SyCounterQuotaProfileConf;
import com.elitecore.corenetvertex.pm.pkg.imspackage.IMSServiceTable;
import com.elitecore.corenetvertex.pm.pkg.imspackage.MediaType;
import com.elitecore.corenetvertex.pm.pkg.imspackage.PCCAttributeTableEntry;
import com.elitecore.corenetvertex.pm.pkg.notification.QuotaLimitEvent;
import com.elitecore.corenetvertex.pm.pkg.notification.QuotaThresholdEvent;
import com.elitecore.corenetvertex.pm.pkg.notification.UsageThresholdEvent;
import com.elitecore.exprlib.parser.expression.LogicalExpression;
import com.elitecore.netvertex.pm.QoSProfileDetail.UsageProvider;
import com.elitecore.netvertex.pm.qos.rnc.ratecard.DataRateCardVersion;
import com.elitecore.netvertex.pm.qos.rnc.ratecard.FlatRating;
import com.elitecore.netvertex.pm.quota.RnCQuotaProfileDetail;
import com.elitecore.netvertex.pm.quota.RnCTopUpQuotaProfileDetail;
import com.elitecore.netvertex.service.notification.QuotaNotificationScheme;

import javax.annotation.Nullable;
import java.sql.Timestamp;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * This package factory is used to create Netvertex engine specific objects that will be used for policy hunting
 *
 * @author Jay Trivedi
 */

public class PackageFactory extends com.elitecore.corenetvertex.pm.pkg.PackageFactory {

    @Override
    public com.elitecore.netvertex.pm.BasePackage createBasePackage(BasePackageConf conf) {

        return new com.elitecore.netvertex.pm.BasePackage(conf.getId(), conf.getName(), conf.getQuotaProfileType()
                , conf.getAvailabilityStatus(), conf.getQosProfiles(), conf.getUsageNotificationScheme()
                , conf.getPackageMode(), conf.getDescription(), conf.getPrice(), conf.getAvailabilityStartDate()
                , conf.getAvailabilityEndDate(), conf.getGroupIds()
                , conf.getStatus(), conf.getFailReason(), conf.getPartialFailReason(), conf.getParam1(), conf.getParam2(), conf.getQuotaNotificationScheme(),conf.getCurrency());
    }

    @Override
    public com.elitecore.netvertex.pm.AddOn createAddOn(AddOnConf conf) {
        return new com.elitecore.netvertex.pm.AddOn(conf.getId(), conf.getName()
                , conf.getQuotaProfileType(), conf.getAvailabilityStatus(), conf.getQosProfiles()
                , conf.getMultipleSubscription(), conf.isExclusive()
                , conf.getValidityPeriod(), conf.getValidityPeriodUnit(), conf.getUsageNotificationScheme()
                , conf.getPackageMode(), conf.getDescription(), conf.getPrice(), conf.getAvailabilityStartDate()
                , conf.getAvailabilityEndDate(), conf.getGroupIds(), conf.getStatus()
                , conf.getFailReason(), conf.getPartialFailReason(), conf.getParam1(), conf.getParam2(), conf.getQuotaNotificationScheme(),conf.getCurrency());
    }

    @Override
    public com.elitecore.netvertex.pm.PromotionalPackage createPromotionalPackage(AddOnConf conf) {
        return new com.elitecore.netvertex.pm.PromotionalPackage(conf.getId(), conf.getName()
                , conf.getQuotaProfileType(), conf.getAvailabilityStatus(), conf.getQosProfiles()
                , conf.getUsageNotificationScheme()
                , conf.getPackageMode(), conf.getDescription(), conf.getPrice(), conf.getAvailabilityStartDate()
                , conf.getAvailabilityEndDate()
                , conf.getGroupIds(), conf.getPreferPromotionalQoS(), conf.getStatus()
                , conf.getFailReason(), conf.getPartialFailReason(), conf.getParam1(), conf.getParam2(), conf.getPkgGroupOrderConfs());
    }

    @Override
    public com.elitecore.netvertex.pm.EmergencyPackage createEmergencyPackage(PkgData pkgData, String id, String name,
                                                                              PkgStatus availabilityStatus,
                                                                              List<QoSProfile> qosProfiles
            , PkgMode packageMode
            , String description
            , Double price
            , Timestamp availabilityStartDate, Timestamp availabilityEndDate, List<String> groupIds, PolicyStatus status, String failReason,
                                                                              String partialFailReason, String param1, String param2, List<GroupManageOrder> pkgGroupOrderConfs) {

        return new com.elitecore.netvertex.pm.EmergencyPackage(id,
                name, availabilityStatus, qosProfiles, packageMode,
                description, price, availabilityStartDate, availabilityEndDate,
                groupIds, status, failReason, partialFailReason, param1,
                param2, pkgGroupOrderConfs);
    }

    @Override
    public QoSProfileDetail createSyCounterBaseQoSProfileDetail(String qosProfileName, String pkgName,
                                                                QoSProfileAction qosProfileAction, String rejectCause, SyCounterBaseQuotaProfileDetail allServiceQuotaProfileDetail,
                                                                Map<String, com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfileDetail> serviceToSyQuotaProfileDetail, int fupLevel, Integer orderNo, @Nullable String redirectURL) {

        return new com.elitecore.netvertex.pm.SyCounterBaseQoSProfileDetail(qosProfileName, pkgName, qosProfileAction, rejectCause, allServiceQuotaProfileDetail, serviceToSyQuotaProfileDetail, fupLevel, orderNo, redirectURL);
    }

    @Override
    public QoSProfileDetail createFailedUMBaseQoSProfileDetail(String qosProfileName, String packageName, QoSProfileAction qosProfileAction, String rejectCause,
                                                               int fupLevel, int orderNo, @Nullable String redirectURL) {

        return new com.elitecore.netvertex.pm.FailedUMBaseQoSProfileDetail(qosProfileName, packageName, qosProfileAction, rejectCause, fupLevel, orderNo, redirectURL);
    }

    @Override
    public com.elitecore.netvertex.pm.QoSProfileDetail createUMBaseQoSProfileDetailForActionReject(String qosProfileName, String packageName, QoSProfileAction qosProfileAction, String reason,
                                                                                                   UMBaseQuotaProfileDetail allServiceQuotaProfileDetail, Map<String, com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfileDetail> serviceToUMQuotaProfileDetail,
                                                                                                   boolean isUsageRequired, int fupLevel, Integer orderNo, boolean isApplicableOnUsageUnavailability, @Nullable String redirectURL) {


        return new com.elitecore.netvertex.pm.UMBaseQoSProfileDetail(qosProfileName, packageName, qosProfileAction
                , reason, allServiceQuotaProfileDetail, serviceToUMQuotaProfileDetail, isUsageRequired,
                fupLevel, orderNo, isApplicableOnUsageUnavailability, UsageProvider.CURRENT_EXECUTING_PACKAGE_USAGE_PROVIDER, redirectURL);
    }

    @Override
    public QoSProfileDetail createSyCounterBaseQoSProfileDetail(String qosProfileName,
                                                                String pkgName,
                                                                QoSProfileAction qosProfileAction,
                                                                String reason,
                                                                int fupLevel,
                                                                SyCounterBaseQuotaProfileDetail allServiceQuotaProfileDetail,
                                                                Map<String, com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfileDetail> serviceToSyQuotaProfileDetail,
                                                                IPCANQoS sessionQoS,
                                                                List<PCCRule> pccRules,
                                                                boolean usageMonitoring,
                                                                SliceInformation sliceInformation,
                                                                Integer orderNo, @Nullable String redirectURL, List<ChargingRuleBaseName> chargingRuleBaseNames) {
        return new com.elitecore.netvertex.pm.SyCounterBaseQoSProfileDetail(qosProfileName, pkgName, qosProfileAction, reason, fupLevel
                , allServiceQuotaProfileDetail, serviceToSyQuotaProfileDetail, sessionQoS, pccRules, usageMonitoring, sliceInformation, orderNo, redirectURL, chargingRuleBaseNames);
    }

    @Override
    public QoSProfileDetail createFailedUMBaseQoSProfileDetail(String qosProfileName, String pkgName, QoSProfileAction qosProfileAction,
                                                               String rejectCause, int fupLevel, IPCANQoS sessionQoS,
                                                               List<PCCRule> pccRules, boolean usageMonitoring, SliceInformation sliceInformation, @Nullable String redirectURL, List<ChargingRuleBaseName> chargingRuleBaseNames) {

        return new com.elitecore.netvertex.pm.FailedUMBaseQoSProfileDetail(qosProfileName, pkgName, qosProfileAction, rejectCause, fupLevel, sessionQoS, pccRules, usageMonitoring, sliceInformation, redirectURL, chargingRuleBaseNames);
    }

    @Override
    public QoSProfileDetail createUMBaseQoSProfileDetail(String qosProfileName, String pkgName, QoSProfileAction qosProfileAction, String reason,
                                                         int fupLevel, UMBaseQuotaProfileDetail allServiceQuotaProfileDetail, Map<String, com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfileDetail> serviceToUMQuotaProfileDetail,
                                                         boolean isUsageRequired, IPCANQoS sessionQoS, List<PCCRule> pccRules, boolean usageMonitoring, SliceInformation sliceInformation,
                                                         Integer orderNo, boolean isApplicableOnUsageUnavailability, @Nullable String redirectURL, List<ChargingRuleBaseName> chargingRuleBaseNames) {

        return new com.elitecore.netvertex.pm.UMBaseQoSProfileDetail(qosProfileName, pkgName, qosProfileAction, reason, fupLevel, allServiceQuotaProfileDetail
                , serviceToUMQuotaProfileDetail, isUsageRequired, sessionQoS, pccRules, usageMonitoring, sliceInformation, orderNo, isApplicableOnUsageUnavailability, com.elitecore.netvertex.pm.QoSProfileDetail.UsageProvider.CURRENT_EXECUTING_PACKAGE_USAGE_PROVIDER, redirectURL, chargingRuleBaseNames);
    }

    @Override
    public com.elitecore.netvertex.pm.QoSProfile createQoSProfile(String id, String name, String packageName, String packageId,
                                                                  @Nullable com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfile quotaProfile,
                                                                  com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc.ratecard.DataRateCard dataRateCard, List<String> accessNetwork, int duration,
                                                                  com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.QoSProfileDetail hsqLevelQoSDetail,
                                                                  List<com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.QoSProfileDetail> fupLevelQoSDetails,
                                                                  LogicalExpression logicalExpression,
                                                                  String additionalCondition, AccessTimePolicy accessTimePolicy) {

        return new com.elitecore.netvertex.pm.QoSProfile(id, name, packageName, packageId, quotaProfile, dataRateCard, accessNetwork, duration, hsqLevelQoSDetail, fupLevelQoSDetails, logicalExpression, additionalCondition, accessTimePolicy);
    }

    @Override
    public com.elitecore.netvertex.pm.SyCounterBaseQuotaProfileDetail createSyBasedQuotaProfileDetail(SyCounterQuotaProfileConf syCounterQuotaProfileConf) {
        return new com.elitecore.netvertex.pm.SyCounterBaseQuotaProfileDetail(syCounterQuotaProfileConf.getQuotaProfileId(), syCounterQuotaProfileConf.getQuotaProfileName()
                , syCounterQuotaProfileConf.getPackageName(), syCounterQuotaProfileConf.getServiceId()
                , syCounterQuotaProfileConf.getServiceName(), syCounterQuotaProfileConf.getFupLevel(), syCounterQuotaProfileConf.getSyCounters());
    }

    @Override
    public QuotaProfile createBalanceBasedQuotaProfile(String name, String pkgName, String id, BalanceLevel balanceLevel, int renewalInterval, RenewalIntervalUnit renewalIntervalUnit, QuotaProfileType quotaProfileType,
                                                              List<Map<String, QuotaProfileDetail>> quotaProfileDetails, boolean proration, boolean carryForward) {
        return new QuotaProfile(name, pkgName, id, balanceLevel, renewalInterval, renewalIntervalUnit, quotaProfileType, quotaProfileDetails,proration,carryForward);
    }

    @Override
    public RnCQuotaProfileDetail createRnCQuotaProfileDetail(String quotaProfileId, String name, DataServiceType dataServiceType, int fupLevel, RatingGroup ratingGroup, Map<AggregationKey, AllowedUsage> aggregationKeyToAllowedUsage, long volumePulse, long timePulse, long volumePulseInBytes, long timePulseInSeconds, String volumePulseUnit, String timePulseUnit, double rate, UsageType rateUnit,
															 QuotaUsageType quotaType, VolumeUnitType unitType, boolean isHsqLevel, String pccProfileName, long volumeCarryForwardLimit, long timeCarryForwardLimit, String revenueDetail) {

        return new RnCQuotaProfileDetail(quotaProfileId, name, dataServiceType, fupLevel, ratingGroup, aggregationKeyToAllowedUsage, volumePulse, timePulse, volumePulseInBytes, timePulseInSeconds, volumePulseUnit, timePulseUnit, rate, rateUnit,
                quotaType, unitType, isHsqLevel, pccProfileName, volumeCarryForwardLimit, timeCarryForwardLimit,revenueDetail);
    }

    @Override
    public RncProfileDetail createRnCTopUpQuotaProfileDetail(String quotaProfileId, String name, DataServiceType dataServiceType, Integer fupLevel,
															 RatingGroup ratingGroup, Map<AggregationKey, AllowedUsage> aggregationKeyWiseAllowedUsage,
															 Long pulseVolume, Long pulseTime, long volumePulseInBytes, long timePulseInSeconds,
															 String pulseVolumeUnit, String pulseTimeUnit, double rate, UsageType usageType,
															 QuotaUsageType quotaUsageType, VolumeUnitType volumeUnitType, boolean isHsqLevel, String pccProfileName, long volumeCarryForwardLimit, long timeCarryForwardLimit, String revenueDetail) {
        return new RnCTopUpQuotaProfileDetail(quotaProfileId, name, dataServiceType, fupLevel, ratingGroup, aggregationKeyWiseAllowedUsage, pulseVolume, pulseTime, volumePulseInBytes,
                timePulseInSeconds, pulseVolumeUnit, pulseTimeUnit
                , rate, usageType, quotaUsageType, volumeUnitType, isHsqLevel, pccProfileName, volumeCarryForwardLimit, timeCarryForwardLimit, revenueDetail);
    }

    @Override
    public com.elitecore.netvertex.pm.UMBaseQuotaProfileDetail createUMBaseQuotaProfileDetail(String quotaProfileId, String quotaProfileName, String serviceId, int fupLevel, String serviceName, List<QuotaProfileDetailData> quotaProfileDetailDatas) {

        Map<AggregationKey, AllowedUsage> aggregationKeyToAllowedUsage = new EnumMap<>(AggregationKey.class);
        for (QuotaProfileDetailData quotaProfileDetailData : quotaProfileDetailDatas) {

            AggregationKey aggregationKey = AggregationKey.valueOf(quotaProfileDetailData.getAggregationKey());
            aggregationKeyToAllowedUsage.put(aggregationKey, aggregationKey.createAllowedUsage(quotaProfileDetailData));
        }

        return new com.elitecore.netvertex.pm.UMBaseQuotaProfileDetail(quotaProfileId, quotaProfileName, serviceId, fupLevel, serviceName, aggregationKeyToAllowedUsage);
    }

    @Override
    public com.elitecore.netvertex.service.notification.UsageNotificationScheme createUsageNotificationScheme(List<List<UsageThresholdEvent>> usageThresholdEvents) {
        return new com.elitecore.netvertex.service.notification.UsageNotificationScheme(usageThresholdEvents);
    }

    @Override
    public com.elitecore.netvertex.pm.IMSPackage createIMSBasePackage(String id, String name, Map<Long, List<IMSServiceTable>> serviceIdentifierToServiceTables,
                                                                      PkgMode packageMode, PkgStatus availabilityStatus, Double price, List<String> groupIds) {
        return new com.elitecore.netvertex.pm.IMSPackage(id, name, serviceIdentifierToServiceTables, packageMode, availabilityStatus, price, groupIds);
    }

    @Override
    public com.elitecore.netvertex.pm.IMSServiceTable createIMSServiceTable(String name,
                                                                            MediaType serviceType,
                                                                            String afAppId,
                                                                            LogicalExpression logicalExpression,
                                                                            String expressionStr,
                                                                            IMSServiceAction action,
                                                                            Map<PCCAttribute, List<PCCAttributeTableEntry>> pccAttributeToTableEntries) {

        return new com.elitecore.netvertex.pm.IMSServiceTable(name, serviceType, afAppId, logicalExpression, expressionStr, action, pccAttributeToTableEntries);
    }

    @Override
    public com.elitecore.netvertex.pm.PCCAttributeTableEntry createPCCAttributeTableEntry(PCCAttribute attribute, PCCRuleAttributeAction action, String value, String expressionStr, LogicalExpression expression) {
        return new com.elitecore.netvertex.pm.PCCAttributeTableEntry(attribute, action, value, expressionStr, expression);
    }

    @Override
    public com.elitecore.netvertex.pm.IMSPackage createIMSPackage(String id, String name, PkgMode packageMode, PkgStatus availabilityStatus,
                                                                  PolicyStatus policyStatus, String failReason, String partialFailReason, Double price, List<String> groupIds) {
        return new com.elitecore.netvertex.pm.IMSPackage(id, name, packageMode, availabilityStatus, policyStatus, failReason, partialFailReason, price, groupIds);
    }

    @Override
    public QoSProfileDetail createRnCBaseQoSProfileDetail(String qosProfileName,
                                                          String pkgName,
                                                          int fupLevel,
                                                          RncProfileDetail allServiceQuotaProfileDetail,
                                                          Map<String, com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfileDetail> serviceToQuotaProfileDetail,
                                                          IPCANQoS sessionQoS,
                                                          List<PCCRule> pccRules,
                                                          Integer orderNo,
                                                          String redirectURL,
                                                          boolean applyOnUsageUnavailablity,
                                                          List<ChargingRuleBaseName> chargingRuleBaseNames) {
        return new com.elitecore.netvertex.pm.qos.rnc.RnCBaseQoSProfileDetail(qosProfileName,
                pkgName,
                fupLevel,
                (RnCQuotaProfileDetail) allServiceQuotaProfileDetail,
                serviceToQuotaProfileDetail,
                sessionQoS,
                pccRules,
                orderNo,
                applyOnUsageUnavailablity,
                redirectURL,
                chargingRuleBaseNames);
    }

    @Override
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

        if (redirectURL != null) {
            redirectURL = redirectURL.trim();
        }
        return new com.elitecore.netvertex.pm.qos.rnc.RnCBaseQoSProfileDetail(qosProfileName,
                packageName,
                reason,
                (RnCQuotaProfileDetail) allServiceQuotaProfileDetail,
                serviceToQuotaProfileDetail,
                fupLevel,
                orderNo,
                applyOnUsageUnavailablity,
                redirectURL);
    }

    @Override
    public QuotaNotificationScheme createQuotaNotificationScheme(List<List<QuotaThresholdEvent>> quotaThresoldEvents, List<List<QuotaLimitEvent>> quotaLimitEvents) {
        return new QuotaNotificationScheme(quotaThresoldEvents, quotaLimitEvents);
    }

    @Override
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

    @Override
    public QoSProfileDetail createRateCardBaseQoSProfileDetailForActionReject(String qosProfileName,
                                                                              String packageName,
                                                                              QoSProfileAction qosProfileAction,
                                                                              String reason,
                                                                              DataRateCard dataRateCard,
                                                                              String redirectURL) {
        if (redirectURL != null) {
            redirectURL = redirectURL.trim();
        }
        return new RateCardBasedQoSProfileDetail(qosProfileName,
                packageName,
                qosProfileAction,
                reason,
                dataRateCard,
                redirectURL);
    }

    @Override
    public com.elitecore.netvertex.pm.qos.rnc.ratecard.DataRateCard createDataRateCard(DataRateCardConf conf) {
        return new com.elitecore.netvertex.pm.qos.rnc.ratecard.DataRateCard(conf.getId(), conf.getName(), conf.getKeyOne(), conf.getKeyTwo(), conf.getDataRateCardVersions(),
                conf.getPulseUom(), conf.getRateUom());
    }

    @Override
    public RateCardVersion createDataRateCardVersion(String rateCardId, String rateCardName, String versionName, List<VersionDetail> versionDetails) {
        return new DataRateCardVersion(rateCardId, rateCardName, versionName, versionDetails);
    }

    @Override
    public com.elitecore.netvertex.pm.qos.rnc.ratecard.VersionDetail createFlatRatingVersionDetail(String keyValueOne, String keyValueTwo, List<RateSlab> rateSlabs, String revenueDetail) {
        return new FlatRating(keyValueOne, keyValueTwo, rateSlabs, revenueDetail);
    }
}
