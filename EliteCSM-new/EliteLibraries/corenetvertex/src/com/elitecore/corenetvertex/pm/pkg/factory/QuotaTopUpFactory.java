package com.elitecore.corenetvertex.pm.pkg.factory;

import com.elitecore.commons.base.Splitter;
import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.constants.AggregationKey;
import com.elitecore.corenetvertex.constants.BalanceLevel;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.CommonStatusValues;
import com.elitecore.corenetvertex.constants.DataUnit;
import com.elitecore.corenetvertex.constants.DeploymentMode;
import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.constants.QuotaProfileType;
import com.elitecore.corenetvertex.constants.RenewalIntervalUnit;
import com.elitecore.corenetvertex.constants.TimeUnit;
import com.elitecore.corenetvertex.constants.UsageType;
import com.elitecore.corenetvertex.constants.ValidityPeriodUnit;
import com.elitecore.corenetvertex.pd.topup.DataTopUpData;
import com.elitecore.corenetvertex.pd.topup.TopUpQuotaType;
import com.elitecore.corenetvertex.pd.topup.TopUpType;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pkg.rnc.QuotaUsageType;
import com.elitecore.corenetvertex.pkg.rnc.VolumeUnitType;
import com.elitecore.corenetvertex.pm.pkg.DataServiceType;
import com.elitecore.corenetvertex.pm.pkg.PackageFactory;
import com.elitecore.corenetvertex.pm.pkg.RatingGroup;
import com.elitecore.corenetvertex.pm.pkg.datapackage.QuotaTopUp;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.AllowedUsage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.BillingCycleAllowedUsage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfile;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfileDetail;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.RncProfileDetail;
import com.elitecore.corenetvertex.pm.pkg.notification.QuotaNotificationScheme;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class QuotaTopUpFactory {
    private static final String MODULE = "QUOTA-TOPUP-FCTRY";
    private static final Splitter COMMA_BASE_SPLITTER = Splitter.on(CommonConstants.COMMA).trimTokens();
    private static final long SERVICE_IDENTIFIER = 1;
    private static final long RATING_GROUP_IDENTIFIER = 1;
    private QuotaNotificationSchemeFactory quotaNotificationSchemeFactory;
    private PackageFactory packageFactory;
    private DeploymentMode deploymentMode;

    public QuotaTopUpFactory(QuotaNotificationSchemeFactory quotaNotificationSchemeFactory, PackageFactory packageFactory, DeploymentMode deploymentMode) {
        this.quotaNotificationSchemeFactory = quotaNotificationSchemeFactory;
        this.packageFactory = packageFactory;
        this.deploymentMode = deploymentMode;
    }


    public List<QuotaTopUp> create(List<DataTopUpData> dataTopUpDatas) {

        if (getLogger().isInfoLogLevel()) {
            getLogger().info(MODULE, "Creating Quota TopUp from Quota TopUp package data started");
        }

        List<QuotaTopUp> tempQuotaTopUps = new ArrayList<>();

        for (DataTopUpData dataTopUpData : dataTopUpDatas) {
            List<String> quotaTopUpFailReasons = new ArrayList<>();
            List<String> quotaTopUpPartialFailReasons = new ArrayList<>();

            String name = dataTopUpData.getName();
            boolean multipleSubscription = dataTopUpData.getMultipleSubscription();

            PkgMode mode = PkgMode.getMode(dataTopUpData.getPackageMode());
            if (mode == null) {
                quotaTopUpFailReasons.add("Invalid package mode(" + dataTopUpData.getPackageMode() + ") given for package: " + dataTopUpData.getName());
            }

            if(deploymentMode == DeploymentMode.PCRF){
                quotaTopUpFailReasons.add("Top-Up is not compatible with deployment mode: "+deploymentMode.getValue());
            }


            int validityPeriod = dataTopUpData.getValidityPeriod();
            ValidityPeriodUnit validityPeriodUnit = dataTopUpData.getValidityPeriodUnit();

            if (validityPeriodUnit == null) {
                validityPeriodUnit = ValidityPeriodUnit.MID_NIGHT;
            }

            String description = dataTopUpData.getDescription();
            Double price = dataTopUpData.getPrice();
            Timestamp availabilityStartDate = dataTopUpData.getAvailabilityStartDate();
            Timestamp availabilityEndDate = dataTopUpData.getAvailabilityEndDate();

            PkgStatus availabilityStatus = PkgStatus.valueOf(dataTopUpData.getStatus());

            String groups = dataTopUpData.getGroups();
            String param1 = dataTopUpData.getParam1();
            String param2 = dataTopUpData.getParam2();

            TopUpType topUpType = TopUpType.valueOf(dataTopUpData.getTopupType());

            List<String> quotaProfileFailReasons = new ArrayList<String>();

            QuotaProfile quotaProfile = createQuotaProfileForQuotaTopUp(dataTopUpData, quotaProfileFailReasons);

            if (quotaProfileFailReasons.isEmpty() == false) {
                quotaTopUpFailReasons.add("Quota profile (" + dataTopUpData.getName() + "-QuotaProfile" + ") parsing fail. Cause by:" +
                        FactoryUtils.format(quotaProfileFailReasons));
            }

            QuotaNotificationScheme quotaNotificationScheme = quotaNotificationSchemeFactory.createTopUpQuotaNotificationScheme(dataTopUpData.getTopUpNotificationList(),
                    quotaProfile, quotaTopUpPartialFailReasons);

            PolicyStatus policyStatus = PolicyStatus.SUCCESS;
            String quotaTopUpFailReason = null;
            if (quotaTopUpFailReasons.isEmpty() == false) {
                policyStatus = PolicyStatus.FAILURE;
                quotaTopUpFailReason = quotaTopUpFailReasons.toString();
            }

            String quotaTopUpPartialFailReason = null;
            if (quotaTopUpPartialFailReasons.isEmpty() == false) {
                quotaTopUpPartialFailReason = quotaTopUpPartialFailReasons.toString();
                policyStatus = PolicyStatus.PARTIAL_SUCCESS;
            }

            List<String> applicablePCCProfiles = new ArrayList<>();
            if (Strings.isNullOrBlank(dataTopUpData.getApplicablePCCProfiles()) == false) {
				applicablePCCProfiles = CommonConstants.COMMA_SPLITTER.split(dataTopUpData.getApplicablePCCProfiles());
			}

            QuotaTopUp quotaTopUp = new QuotaTopUp(dataTopUpData.getId(),
                    name,
                    multipleSubscription,
                    validityPeriod,
                    validityPeriodUnit,
                    mode,
                    topUpType,
                    description,
                    price,
                    availabilityStartDate,
                    availabilityEndDate,
                    availabilityStatus,
                    COMMA_BASE_SPLITTER.split(groups),
                    quotaProfile,
                    quotaTopUpFailReason,
                    quotaTopUpPartialFailReason,
                    policyStatus,
                    param1,
                    param2,
                    quotaNotificationScheme,
                    dataTopUpData.getQuotaType(),
                    dataTopUpData.getUnitType(),
                    dataTopUpData.getVolumeBalance(),
                    dataTopUpData.getVolumeBalanceUnit(),
                    dataTopUpData.getTimeBalance(),
                    dataTopUpData.getTimeBalanceUnit(), applicablePCCProfiles);

            if (quotaTopUpFailReasons.isEmpty()) {

                if (getLogger().isInfoLogLevel()) {
                    getLogger().info(MODULE, "Quota TopUp package(" + name + ") parsed successfully");
                }
            }

            tempQuotaTopUps.add(quotaTopUp);
        }

        getLogger().info(MODULE, "Creating Quota TopUp package from package data completed");
        return tempQuotaTopUps;
    }

    private QuotaProfile createQuotaProfileForQuotaTopUp(DataTopUpData dataTopUpData,
                                                         List<String> quotaProfileFailReasons) {
        String quotaName = dataTopUpData.getName() + "-QuotaProfile";
        String pkgName = dataTopUpData.getName();
        String id = dataTopUpData.getId();
        QuotaProfileType quotaProfileType = QuotaProfileType.RnC_BASED;
        Map<String, QuotaProfileDetail> hsqLevelServiceWiseQuotaProfileDetais = createHsqLevelServiceWiseQuotaProfileDetais(dataTopUpData, quotaProfileFailReasons);
        return new QuotaProfile(quotaName, pkgName, id, BalanceLevel.HSQ,0, RenewalIntervalUnit.MONTH, quotaProfileType, Arrays.asList(hsqLevelServiceWiseQuotaProfileDetais), CommonStatusValues.DISABLE.isBooleanValue(),CommonStatusValues.DISABLE.isBooleanValue());
    }

    private Map<String, QuotaProfileDetail> createHsqLevelServiceWiseQuotaProfileDetais(DataTopUpData dataTopUpData,
                                                                                        List<String> quotaProfileFailReasons) {
        Map<String, QuotaProfileDetail> hsqLevelServiceWiseQuotaProfileDetails = new HashMap<>();
        String quotaProfileId = dataTopUpData.getId();
        String name = dataTopUpData.getName();
        RatingGroup ratingGroup = createRatingGroup();

        DataServiceType dataServiceType = new DataServiceType(
                CommonConstants.ALL_SERVICE_ID,
                CommonConstants.ALL_SERVICE_NAME,
                SERVICE_IDENTIFIER,
                Collections.emptyList(),
                Arrays.asList(ratingGroup));

        int fupLevel = 0;
        Map<AggregationKey, AllowedUsage> aggregationKeyToAllowedUsage = createAggregationKeyToAllowedUsageForQuotaTopUp(dataTopUpData,
                quotaProfileFailReasons);
        long volumePulse = 1;
        long timePulse = 1;
        long volumePulseInBytes = 1;
        long timePulseInSeconds = 1;
        String volumePulseUnit = DataUnit.BYTE.name();
        String timePulseUnit = TimeUnit.SECOND.name();
        double rate = 0;
        UsageType rateUnit = UsageType.VOLUME;
        QuotaUsageType quotaType = QuotaUsageType.valueOf(dataTopUpData.getQuotaType());
        VolumeUnitType unitType = VolumeUnitType.valueOf(dataTopUpData.getUnitType());

        RncProfileDetail rncProfileDetail = packageFactory.createRnCQuotaProfileDetail(quotaProfileId,
                name, dataServiceType, fupLevel,
                ratingGroup, aggregationKeyToAllowedUsage,
                volumePulse, timePulse, volumePulseInBytes, timePulseInSeconds, volumePulseUnit,
                timePulseUnit, rate, rateUnit, quotaType, unitType, true,  null, 0l,0l, null);

        hsqLevelServiceWiseQuotaProfileDetails.put(CommonConstants.ALL_SERVICE_ID, rncProfileDetail);

        return hsqLevelServiceWiseQuotaProfileDetails;
    }

    private Map<AggregationKey, AllowedUsage> createAggregationKeyToAllowedUsageForQuotaTopUp(DataTopUpData dataTopUpData,
                                                                                              List<String> quotaProfileFailReasons) {
        Map<AggregationKey, AllowedUsage> aggregationKeyToAllowedUsage = new EnumMap<>(AggregationKey.class);
        AggregationKey aggregationKey = AggregationKey.BILLING_CYCLE;
        AllowedUsage billingCycleAllowedUsage = createAllowedUsage(dataTopUpData, quotaProfileFailReasons);
        aggregationKeyToAllowedUsage.put(aggregationKey, billingCycleAllowedUsage);
        return aggregationKeyToAllowedUsage;
    }


    private RatingGroup createRatingGroup() {
        String ratingGroupId = CommonConstants.RATING_GROUP_QUOTA_TOPUP_ID_1;
        String name = CommonConstants.QUOTA_TOPUP_RATING_GROUP_NAME;
        String description = "Description";
        long identifier = RATING_GROUP_IDENTIFIER;
        return new RatingGroup(ratingGroupId, name, description, identifier);
    }

    public AllowedUsage createAllowedUsage(DataTopUpData dataTopUpData,
                                           List<String> quotaProfileFailReasons) {
        DataUnit volumeUnit = DataUnit.valueOf(dataTopUpData.getVolumeBalanceUnit());

        long total = CommonConstants.QUOTA_UNDEFINED;
        long upload = CommonConstants.QUOTA_UNDEFINED;
        long download = CommonConstants.QUOTA_UNDEFINED;
        long time = CommonConstants.QUOTA_UNDEFINED;


        TopUpQuotaType quotaType = TopUpQuotaType.valueOf(dataTopUpData.getQuotaType());

        boolean isVolumeDefined = quotaType == TopUpQuotaType.VOLUME;
        boolean isTimeDefined = quotaType == TopUpQuotaType.TIME;

        String id = dataTopUpData.getId();

        if (isVolumeDefined) {
            VolumeUnitType unitType = VolumeUnitType.valueOf(dataTopUpData.getUnitType());
            if (unitType == VolumeUnitType.TOTAL) {
                if (dataTopUpData.getVolumeBalance() == null) {
                    quotaProfileFailReasons.add(createFailMessage(id) + "Volume Balance not defined for defined Volume Unit Type: " + unitType);
                } else {
                    total = dataTopUpData.getVolumeBalance();
                }
            } else if (unitType == VolumeUnitType.UPLOAD) {
                if (dataTopUpData.getVolumeBalance() == null) {
                    quotaProfileFailReasons.add(createFailMessage(id)+ "Volume Balance not defined for defined Volume Unit Type: " + unitType);
                } else {
                    upload = dataTopUpData.getVolumeBalance();
                }
            } else if (unitType == VolumeUnitType.DOWNLOAD) {
                if (dataTopUpData.getVolumeBalance() == null) {
                    quotaProfileFailReasons.add(createFailMessage(id) + "Volume Balance not defined for defined Volume Unit Type: " + unitType);
                } else {
                    download = dataTopUpData.getVolumeBalance();
                }
            }
        }

        if (isTimeDefined) {
            if (dataTopUpData.getTimeBalance() == null) {
                quotaProfileFailReasons.add(createFailMessage(id) + "Time Balance not defined for defined Time Unit Type: " + dataTopUpData.getTimeBalanceUnit());
            } else {
                time = dataTopUpData.getTimeBalance();
            }
        }

        return new BillingCycleAllowedUsage(total, download, upload
                , time, volumeUnit, volumeUnit, volumeUnit
                , TimeUnit.fromVal(dataTopUpData.getTimeBalanceUnit()));
    }

    private String createFailMessage(String id) {
        return "Quota Profile Detail(" + id + ") parsing fail. Cause by:";
    }
}
