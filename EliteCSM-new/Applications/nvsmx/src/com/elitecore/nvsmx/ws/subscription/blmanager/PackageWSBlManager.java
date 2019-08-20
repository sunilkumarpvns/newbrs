
package com.elitecore.nvsmx.ws.subscription.blmanager;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Maps;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.AggregationKey;
import com.elitecore.corenetvertex.constants.BalanceLevel;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.EntityType;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.constants.QuotaProfileType;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pkg.PkgType;
import com.elitecore.corenetvertex.pkg.RnCPkgType;
import com.elitecore.corenetvertex.pm.PolicyManager;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.pm.pkg.datapackage.AddOn;
import com.elitecore.corenetvertex.pm.pkg.datapackage.BasePackage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.EmergencyPackage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.Package;
import com.elitecore.corenetvertex.pm.pkg.datapackage.PromotionalPackage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.UserPackage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.AllowedUsage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfile;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfileDetail;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.UMBaseQuotaProfile;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.UMBaseQuotaProfileDetail;
import com.elitecore.corenetvertex.pm.rnc.pkg.RnCPackage;
import com.elitecore.corenetvertex.sm.acl.GroupData;
import com.elitecore.corenetvertex.sm.systemparameter.SystemParameter;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.nvsmx.policydesigner.controller.util.ImportExportUtil;
import com.elitecore.nvsmx.sm.model.systemparameter.SystemParameterDAO;
import com.elitecore.nvsmx.system.groovy.GroovyManager;
import com.elitecore.nvsmx.system.groovy.SubscriptionWsScript;
import com.elitecore.nvsmx.system.hibernate.SessionProviderImpl;
import com.elitecore.nvsmx.system.listeners.DefaultNVSMXContext;
import com.elitecore.nvsmx.ws.interceptor.DiagnosticContextWebServiceInterceptor;
import com.elitecore.nvsmx.ws.interceptor.WebServiceInterceptor;
import com.elitecore.nvsmx.ws.interceptor.WebServiceRequest;
import com.elitecore.nvsmx.ws.interceptor.WebServiceResponse;
import com.elitecore.nvsmx.ws.interceptor.WebServiceStatisticsManager;
import com.elitecore.nvsmx.ws.subscription.data.PackageInfo;
import com.elitecore.nvsmx.ws.subscription.data.QuotaProfileBalanceInformation;
import com.elitecore.nvsmx.ws.subscription.data.QuotaProfileData;
import com.elitecore.nvsmx.ws.subscription.data.QuotaProfileDetailData;
import com.elitecore.nvsmx.ws.subscription.data.RnCPackageInfo;
import com.elitecore.nvsmx.ws.subscription.data.Usage;
import com.elitecore.nvsmx.ws.subscription.data.UsageInfo;
import com.elitecore.nvsmx.ws.subscription.request.ListPackageRequest;
import com.elitecore.nvsmx.ws.subscription.request.ListPackagesRequest;
import com.elitecore.nvsmx.ws.subscription.response.ListPackagesResponse;
import com.elitecore.nvsmx.ws.subscription.response.PackageQueryResponse;
import com.elitecore.nvsmx.ws.subscription.response.RnCPackageQueryResponse;
import com.elitecore.nvsmx.ws.util.DataPackageCurrencyPredicate;
import com.elitecore.nvsmx.ws.util.DataPackageGroupPredicate;
import com.elitecore.nvsmx.ws.util.DataPackageModePredicate;
import com.elitecore.nvsmx.ws.util.RncPackageCurrencyPredicate;
import com.elitecore.nvsmx.ws.util.RncPackageGroupPredicate;
import com.elitecore.nvsmx.ws.util.RncPackageModePredicate;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class PackageWSBlManager {

    private static final String MODULE = "PKG-WSBL-MNGR";
    private static final long UNLIMITED_USAGE = -1;
    private List<WebServiceInterceptor> interceptors = new ArrayList<WebServiceInterceptor>();
    private static final Predicate ALWAYS_TRUE = o -> true;
    private static ImportExportUtil importExportUtil = new ImportExportUtil(new SessionProviderImpl());


    public PackageWSBlManager() {
        interceptors.add(WebServiceStatisticsManager.getInstance());
        interceptors.add(DiagnosticContextWebServiceInterceptor.getInstance());
    }

	public PackageQueryResponse getListDataPackages(ListPackageRequest request) {

		applyRequestInterceptors(request);

		List<SubscriptionWsScript> groovyScripts = GroovyManager.getInstance().getSubscriptionGroovyScripts();
		for (SubscriptionWsScript groovyScript : groovyScripts) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Executing script: " + groovyScript.getName());
			}

			try {
				groovyScript.preListDataPackages(request);
			} catch (Exception e) {
				getLogger().error(MODULE, "Error in executing script: " + groovyScript.getName() + " . Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
			}
		}

		PackageQueryResponse response = doGetAllDataPackages(request);

		for (SubscriptionWsScript groovyScript : groovyScripts) {
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Executing script: " + groovyScript.getName());
			}

			try {
				groovyScript.postListDataPackages(request, response);
			} catch (Exception e) {
				getLogger().error(MODULE, "Error in executing script: " + groovyScript.getName() + " . Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
			}
		}

		applyResponseInterceptors(response);
		return response;

	}
	private PackageQueryResponse doGetAllDataPackages(ListPackageRequest request) {
		String pkgId = request.getPackageId();
		String pkgName = request.getPackageName();

		try{
			List<PackageInfo> userPackages=null;
			PkgType pkgType=null;

			if(Strings.isNullOrBlank(request.getPackageType()) == false){
				pkgType = getPkgType(request);
				if(pkgType == null){
					getLogger().error(MODULE, "Invalid Package Type received " +request.getPackageType());
					return new PackageQueryResponse(ResultCode.INVALID_INPUT_PARAMETER.code,"Error while fetching data packages.Reason: Invalid Package Type received :"+request.getPackageType(),null,null,null, request.getWebServiceName(), request.getWebServiceMethodName());

				}
			}

            if(StringUtils.isNotBlank(request.getCurrency())){
                if (isCurrencyNotInSync(request.getCurrency())) {
                    getLogger().error(MODULE, "Currency other than system currency is not supported");
                    return new PackageQueryResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "Error while fetching data packages.Reason: Currency other than system currency is not supported",null,null,null, request.getWebServiceName(), request.getWebServiceMethodName());
                }else if (SystemParameter.CURRENCY.validate(request.getCurrency()) == false) {
                    getLogger().error(MODULE, "Invalid Currency received " + request.getCurrency());
                    return new PackageQueryResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "Error while fetching data packages.Reason: Invalid Currency received :" + request.getCurrency(),null,null,null, request.getWebServiceName(), request.getWebServiceMethodName());
                }
            }

			userPackages = getUserPackageDataList(pkgType, pkgId, pkgName, DataPackageCurrencyPredicate.create(request.getCurrency()));

			if(Collectionz.isNullOrEmpty(userPackages)){
				return new PackageQueryResponse(ResultCode.NOT_FOUND.code, "No Package Found", null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());

			}
			return new PackageQueryResponse(ResultCode.SUCCESS.code, ResultCode.SUCCESS.name, null, null, userPackages, request.getWebServiceName(), request.getWebServiceMethodName());
		}catch(Exception e){
			getLogger().error(MODULE, "Internal error occured while fetching packages. Reason :"+e.getMessage());
			getLogger().trace(e);
			return new PackageQueryResponse(ResultCode.INTERNAL_ERROR.code,"Reason:"+ e.getMessage(),null,null,null, request.getWebServiceName(), request.getWebServiceMethodName());
		}
	}

    private List<PackageInfo> getUserPackageDataList(PkgType pkgType, String pkgId, String pkgName, Predicate predicate) {

        if (pkgType == null) {
            if (Strings.isNullOrBlank(pkgId) && Strings.isNullOrBlank(pkgName)) {
                return getAllPackages(predicate);
            }

            List<PackageInfo> packageInfos = null;

            packageInfos = getBasePackages(pkgId, pkgName, predicate);

            if (Collectionz.isNullOrEmpty(packageInfos)) {
                packageInfos = getAddOnPackages(pkgId, pkgName, predicate);
            }

            if (Collectionz.isNullOrEmpty(packageInfos)) {
                packageInfos = getEmergencyPackages(pkgId, pkgName, predicate);
            }

            if (Collectionz.isNullOrEmpty(packageInfos)) {
                packageInfos = getPromotionalPackages(pkgId, pkgName, predicate);
            }
            return packageInfos;
        }

        switch (pkgType) {
            case BASE:
                return getBasePackages(pkgId, pkgName, predicate);
            case ADDON:
                return getAddOnPackages(pkgId, pkgName, predicate);
            case EMERGENCY:
                return getEmergencyPackages(pkgId, pkgName, predicate);
            case PROMOTIONAL:
                return getPromotionalPackages(pkgId, pkgName, predicate);
            default:
                return null;
        }

    }

    private List<PackageInfo> getPromotionalPackages(String pkgId, String pkgName, Predicate predicate) {

        List<UserPackage> userPackageList = Collectionz.newArrayList();
        List<PackageInfo> userPackageDataList = null;
        UserPackage userPackage = null;

        if (Strings.isNullOrBlank(pkgId) == false) {
            userPackage = getNVSMXPolicyRepository().getPromotionalPackageById(pkgId);

            if (userPackage == null) {
                return null;
            }
            userPackageList.add(userPackage);

        } else if (Strings.isNullOrBlank(pkgName) == false) {
            userPackage = getNVSMXPolicyRepository().getPromotionalPackageByName(pkgName);

            if (userPackage == null) {
                return null;
            }
            userPackageList.add(userPackage);

        } else {
            List<PromotionalPackage> promotionalPackages = getNVSMXPolicyRepository().getPromotionalPackages();

            if (Collectionz.isNullOrEmpty(promotionalPackages) == true) {
                return null;
            }
            userPackageList.addAll(promotionalPackages);
        }
        if (Collectionz.isNullOrEmpty(userPackageList) == true) {
            return null;
        }
        userPackageDataList = Collectionz.newArrayList();
        for (UserPackage pkg : userPackageList) {
            if (PolicyStatus.FAILURE == pkg.getStatus()) {
                continue;
            }
            if (predicate.test(pkg) == false) {
                continue;
            }

            PackageInfo userPackageData = new PackageInfo(pkg.getId(), pkg.getName(), pkg.getDescription(), pkg.getPrice(), pkg.getType(),
                    pkg.getAvailabilityStartDate() == null ? null : pkg.getAvailabilityStartDate().getTime(),
                    pkg.getAvailabilityEndDate() == null ? null : pkg.getAvailabilityEndDate().getTime(), pkg.getMode().name(), pkg.getAvailabilityStatus().name(),pkg.getGroupIds(), pkg.getParam1(), pkg.getParam2(), pkg.getCurrency()
            );
            userPackageDataList.add(createUserPackageData(pkg, userPackageData));
        }
        return userPackageDataList;
    }

    private List<PackageInfo> getAllPackages(Predicate predicate) {
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "No Package Type received so fetching all the packages");
        }

        List<Package> allPackages = getNVSMXPolicyRepository().getAllPackageDatas();
        if (Collectionz.isNullOrEmpty(allPackages)) {
            return null;
        }

        List<PackageInfo> userPackageDataList = Collectionz.newArrayList();
        for (UserPackage pkg : allPackages) {
            if (PolicyStatus.FAILURE == pkg.getStatus())
                continue;
            if (predicate.test(pkg) == false) {
                continue;
            }

            PackageInfo userPackageData = new PackageInfo(pkg.getId(), pkg.getName(), pkg.getDescription(), pkg.getPrice(), pkg.getType(),
                    pkg.getAvailabilityStartDate() == null ? null : pkg.getAvailabilityStartDate().getTime(),
                    pkg.getAvailabilityEndDate() == null ? null : pkg.getAvailabilityEndDate().getTime(), pkg.getMode().name(), pkg.getAvailabilityStatus().name(), pkg.getGroupIds(),pkg.getParam1(), pkg.getParam2(), pkg.getCurrency()
            );
            if (pkg.getPackageType().equals(PkgType.ADDON)) {
                userPackageData.setValidityPeriod(((AddOn) pkg).getValidity());
                userPackageData.setValidityPeriodUnit(((AddOn) pkg).getValidityPeriodUnit());
            }

            userPackageDataList.add(createUserPackageData(pkg, userPackageData));
        }

        return userPackageDataList;

    }


    private List<PackageInfo> getEmergencyPackages(String pkgId, String pkgName, Predicate predicate) {

        List<UserPackage> userPackageList = Collectionz.newArrayList();
        List<PackageInfo> userPackageDataList = null;
        UserPackage userPackage = null;

        if (Strings.isNullOrBlank(pkgId) == false) {
            userPackage = getNVSMXPolicyRepository().getEmergencyPackagebyId(pkgId);

            if (userPackage == null) {
                return null;
            }
            userPackageList.add(userPackage);

        } else if (Strings.isNullOrBlank(pkgName) == false) {
            userPackage = getNVSMXPolicyRepository().getEmergencyPackageByName(pkgName);

            if (userPackage == null) {
                return null;
            }
            userPackageList.add(userPackage);

        } else {
            List<EmergencyPackage> emergencyPackages = getNVSMXPolicyRepository().getEmergencyPackages();

            if (Collectionz.isNullOrEmpty(emergencyPackages) == true) {
                return null;
            }
            userPackageList.addAll(emergencyPackages);
        }
        if (Collectionz.isNullOrEmpty(userPackageList) == true) {
            return null;
        }
        userPackageDataList = Collectionz.newArrayList();
        for (UserPackage pkg : userPackageList) {
            if (PolicyStatus.FAILURE == pkg.getStatus()) {
                continue;
            }
            if (predicate.test(pkg) == false) {
                continue;
            }

            PackageInfo userPackageData = new PackageInfo(pkg.getId(), pkg.getName(), pkg.getDescription(), pkg.getPrice(), pkg.getType(),
                    pkg.getAvailabilityStartDate() == null ? null : pkg.getAvailabilityStartDate().getTime(),
                    pkg.getAvailabilityEndDate() == null ? null : pkg.getAvailabilityEndDate().getTime(), pkg.getMode().name(), pkg.getAvailabilityStatus().name(),pkg.getGroupIds(), pkg.getParam1(), pkg.getParam2(), pkg.getCurrency()
            );
            userPackageDataList.add(createUserPackageData(pkg, userPackageData));
        }
        return userPackageDataList;
    }


    private List<PackageInfo> getAddOnPackages(String pkgId, String pkgName, Predicate predicate) {
        List<AddOn> userPackageList = Collectionz.newArrayList();
        List<PackageInfo> userPackageDataList = null;
        AddOn userPackage = null;

        if (Strings.isNullOrBlank(pkgId) == false) {
            userPackage = getNVSMXPolicyRepository().getAddOnById(pkgId);

            if (userPackage == null) {
                return null;
            }
            userPackageList.add(userPackage);
        } else if (Strings.isNullOrBlank(pkgName) == false) {
            userPackage = getNVSMXPolicyRepository().getAddOnByName(pkgName);

            if (userPackage == null) {
                return null;
            }
            userPackageList.add(userPackage);
        } else {
            List<AddOn> addOnOrTopUpPkgDatas = getNVSMXPolicyRepository().getActiveAllAddOnPkgDatas(false);
            if (Collectionz.isNullOrEmpty(addOnOrTopUpPkgDatas) == true) {
                return null;
            }
            userPackageList.addAll(addOnOrTopUpPkgDatas);
        }
        if (Collectionz.isNullOrEmpty(userPackageList) == true) {
            return userPackageDataList;
        }
        userPackageDataList = Collectionz.newArrayList();

        for (AddOn pkg : userPackageList) {
            if (PolicyStatus.FAILURE == pkg.getStatus()) {
                continue;
            }
            if (predicate.test(pkg) == false) {
                continue;
            }

            PackageInfo userPackageData = new PackageInfo(pkg.getId(), pkg.getName(), pkg.getDescription(), pkg.getPrice(), pkg.getType(),
                    pkg.getAvailabilityStartDate() == null ? null : pkg.getAvailabilityStartDate().getTime(),
                    pkg.getAvailabilityEndDate() == null ? null : pkg.getAvailabilityEndDate().getTime(), pkg.getMode().name(), pkg.getAvailabilityStatus().name(),pkg.getGroupIds(), pkg.getParam1(), pkg.getParam2(), pkg.getCurrency()
            );
            userPackageData.setValidityPeriod(pkg.getValidity());
            userPackageData.setValidityPeriodUnit(pkg.getValidityPeriodUnit());
            userPackageDataList.add(createUserPackageData(pkg, userPackageData));
        }
        return userPackageDataList;
    }

    private PkgType getPkgType(ListPackageRequest request) {
        try {
            return PkgType.valueOf(request.getPackageType());
        } catch (Exception e) {
            getLogger().error(MODULE, "Invalid package Type :" + request.getPackageType() + " Configured");
            LogManager.ignoreTrace(e);
        }
        return null;
    }



    private List<PackageInfo> getBasePackages(String pkgId, String pkgName, Predicate predicate) {
        List<BasePackage> userPackageList = Collectionz.newArrayList();
        List<PackageInfo> userPackageDataList = Collectionz.newArrayList();
        BasePackage userPackage = null;

        if (Strings.isNullOrBlank(pkgId) == false) {
            userPackage = getNVSMXPolicyRepository().getActiveBasePackageById(pkgId);

            if (userPackage == null) {
                return null;
            }

            userPackageList.add(userPackage);

        } else if (Strings.isNullOrBlank(pkgName) == false) {
            userPackage = getNVSMXPolicyRepository().getActiveBasePackageByName(pkgName);

            if (userPackage == null) {
                return null;
            }
            userPackageList.add(userPackage);

        } else {

            List<BasePackage> basePackages = getNVSMXPolicyRepository().getActiveAllBasePkgDatas();
            if (Collectionz.isNullOrEmpty(basePackages) == true) {
                return null;
            }
            userPackageList.addAll(basePackages);
        }
        for (BasePackage pkg : userPackageList) {
            if (PolicyStatus.FAILURE == pkg.getStatus()) {
                continue;
            }
            if (predicate.test(pkg) == false) {
                continue;
            }

            PackageInfo userPackageData = new PackageInfo(pkg.getId(), pkg.getName(), pkg.getDescription(), pkg.getPrice(), pkg.getType(),
                    pkg.getAvailabilityStartDate() == null ? null : pkg.getAvailabilityStartDate().getTime(),
                    pkg.getAvailabilityEndDate() == null ? null : pkg.getAvailabilityEndDate().getTime(), pkg.getMode().name(), pkg.getAvailabilityStatus().name(), pkg.getGroupIds(),pkg.getParam1(), pkg.getParam2(), pkg.getCurrency()
            );
            userPackageDataList.add(createUserPackageData(pkg, userPackageData));
        }
        return userPackageDataList;
    }


    private PackageInfo createUserPackageData(UserPackage pkg, PackageInfo userPackageData) {
        if (Collectionz.isNullOrEmpty(pkg.getQuotaProfiles()) == true) {
            return userPackageData;
        }
        List<QuotaProfileData> quotaProfileList = Collectionz.newArrayList();
        for (QuotaProfile quotaProfile : pkg.getQuotaProfiles()) {
            quotaProfileList.add(getQuotaProfileWs(quotaProfile));
        }
        userPackageData.setQuotaProfiles(quotaProfileList);
        return userPackageData;
    }


    private PolicyRepository getNVSMXPolicyRepository() {
        return DefaultNVSMXContext.getContext().getPolicyRepository();
    }


    private QuotaProfileBalanceInformation createQuotaProfileBalance(QuotaProfile quotaProfile) {

        QuotaProfileBalanceInformation quotaProfileBalance = new QuotaProfileBalanceInformation();
        BalanceLevel balanceLevel = ((UMBaseQuotaProfile) quotaProfile).getBalanceLevel();

        Map<String, QuotaProfileDetail> fupLevelServiceWiseQuotaProfileDetails = balanceLevel.getBalanceLevelQuotaProfileDetail(quotaProfile);
        if (Maps.isNullOrEmpty(fupLevelServiceWiseQuotaProfileDetails)) {
            getLogger().warn(MODULE, "Skipping creating balance for quota profile: " + quotaProfile.getName() + ". Reason: FUP quota detail not found for configured balance Level: " + balanceLevel.getDisplayVal());
            return null;
        }

        for (QuotaProfileDetail quotaProfileDetail : fupLevelServiceWiseQuotaProfileDetails.values()) {

            createBalance(quotaProfileBalance, quotaProfileDetail);
        }
        return quotaProfileBalance;
    }

    private void createBalance(QuotaProfileBalanceInformation quotaProfileBalance, QuotaProfileDetail quotaProfileDetail) {
        if (((UMBaseQuotaProfileDetail) quotaProfileDetail).getBillingCycleAllowedUsage() != null) {
            UsageInfo usageInfo = createUsageInformationDetails(quotaProfileDetail, ((UMBaseQuotaProfileDetail) quotaProfileDetail).getBillingCycleAllowedUsage()
                    , AggregationKey.BILLING_CYCLE);
            quotaProfileBalance.setTotalOctets(usageInfo.getBalance().getTotalOctets());
            quotaProfileBalance.setDownloadOctets(usageInfo.getBalance().getDownloadOctets());
            quotaProfileBalance.setUploadOctets(usageInfo.getBalance().getUploadOctets());
            quotaProfileBalance.setTime(usageInfo.getBalance().getTime());
        }

    }


    private UsageInfo createUsageInformationDetails(QuotaProfileDetail quotaProfileDetail, AllowedUsage allowedUsage, AggregationKey aggregationKey) {
        return new UsageInfo(quotaProfileDetail.getServiceId(), ((UMBaseQuotaProfileDetail) quotaProfileDetail).getServiceName(),
                aggregationKey.getVal(), null, null, getAllowedUsage(allowedUsage));
    }


    private Usage getAllowedUsage(AllowedUsage allowedUsage) {

        long uploadInBytes = allowedUsage.getUploadInBytes() == 0 ? UNLIMITED_USAGE : allowedUsage.getUploadInBytes();
        long downloadInBytes = allowedUsage.getDownloadInBytes() == 0 ? UNLIMITED_USAGE : allowedUsage.getDownloadInBytes();
        long totalInBytes = allowedUsage.getTotalInBytes() == 0 ? UNLIMITED_USAGE : allowedUsage.getTotalInBytes();
        long timeInSeconds = allowedUsage.getTimeInSeconds() == 0 ? UNLIMITED_USAGE : allowedUsage.getTimeInSeconds();

        return new Usage(uploadInBytes, downloadInBytes, totalInBytes
                , timeInSeconds, null);
    }

    private QuotaProfileData getQuotaProfileWs(QuotaProfile quotaProfile) {
        QuotaProfileData quotaProfileData = new QuotaProfileData();

        quotaProfileData.setId(quotaProfile.getId());
        quotaProfileData.setName(quotaProfile.getName());
        quotaProfileData.setQuotaProfileType(quotaProfile.getType());

        if (QuotaProfileType.USAGE_METERING_BASED == quotaProfile.getType()) {
            UMBaseQuotaProfile umBaseQuotaProfile = (UMBaseQuotaProfile) quotaProfile;
            quotaProfileData.setBalanceLevel(umBaseQuotaProfile.getBalanceLevel());
            quotaProfileData.setUsagePresence(umBaseQuotaProfile.getUsagePresence());
            quotaProfileData.setQuotaProfileDetails(getQuotaProfileDetails(umBaseQuotaProfile.getAllLevelServiceWiseQuotaProfileDetails(), QuotaProfileType.USAGE_METERING_BASED));
            quotaProfileData.setQuotaProfileBalance(createQuotaProfileBalance(umBaseQuotaProfile));
        } else if (QuotaProfileType.SY_COUNTER_BASED == quotaProfile.getType()) {
            quotaProfileData.setQuotaProfileDetails(getQuotaProfileDetails(quotaProfile.getAllLevelServiceWiseQuotaProfileDetails(), QuotaProfileType.SY_COUNTER_BASED));
        }
        return quotaProfileData;

    }



    private List<QuotaProfileDetailData> getQuotaProfileDetails(List<Map<String, QuotaProfileDetail>> fupLevelserviceWiseQuotaProfileDetails, QuotaProfileType quotaProfileType) {
        if (Collectionz.isNullOrEmpty(fupLevelserviceWiseQuotaProfileDetails) == true) {
            return null;
        }
        List<QuotaProfileDetailData> quotaProfileDetails = Collectionz.newArrayList();
        for (Map<String, QuotaProfileDetail> serviceWiseFupLevelMap : fupLevelserviceWiseQuotaProfileDetails) {
            for (QuotaProfileDetail quotaProfileDetail : serviceWiseFupLevelMap.values()) {
                quotaProfileDetails.addAll(getQuotaProfileDetailDataList(quotaProfileDetail, quotaProfileType));
            }
        }
        return quotaProfileDetails;

    }


    private List<QuotaProfileDetailData> getQuotaProfileDetailDataList(QuotaProfileDetail quotaProfileDetail, QuotaProfileType quotaProfileType) {
        List<QuotaProfileDetailData> quotaProfileDetails = Collectionz.newArrayList();
        if (QuotaProfileType.SY_COUNTER_BASED == quotaProfileType) {
            quotaProfileDetails.add(new QuotaProfileDetailData(quotaProfileDetail.getQuotaProfileId(), quotaProfileDetail.getName(), quotaProfileDetail.getServiceId(), quotaProfileDetail.getFupLevel(), quotaProfileDetail.getServiceName()));
            return quotaProfileDetails;
        }

        UMBaseQuotaProfileDetail umQuotaProifleDetail = (UMBaseQuotaProfileDetail) quotaProfileDetail;

        if (umQuotaProifleDetail.getWeeklyAllowedUsage() != null) {
            quotaProfileDetails.add(getAggrgationKeyWiseQuotaProfileDetailData(quotaProfileDetail, AggregationKey.WEEKLY.name(), umQuotaProifleDetail.getWeeklyAllowedUsage()));
        }
        if (umQuotaProifleDetail.getDailyAllowedUsage() != null) {
            quotaProfileDetails.add(getAggrgationKeyWiseQuotaProfileDetailData(quotaProfileDetail, AggregationKey.DAILY.name(), umQuotaProifleDetail.getDailyAllowedUsage()));
        }
        if (umQuotaProifleDetail.getBillingCycleAllowedUsage() != null) {
            quotaProfileDetails.add(getAggrgationKeyWiseQuotaProfileDetailData(quotaProfileDetail, AggregationKey.BILLING_CYCLE.name(), umQuotaProifleDetail.getBillingCycleAllowedUsage()));
        }
        if (umQuotaProifleDetail.getCustomAllowedUsage() != null) {
            quotaProfileDetails.add(getAggrgationKeyWiseQuotaProfileDetailData(quotaProfileDetail, AggregationKey.CUSTOM.name(), umQuotaProifleDetail.getCustomAllowedUsage()));
        }
        return quotaProfileDetails;
    }

    private QuotaProfileDetailData getAggrgationKeyWiseQuotaProfileDetailData(QuotaProfileDetail quotaProfileDetail, String aggregationKey, AllowedUsage allowedUsage) {

        QuotaProfileDetailData quotaProfileDetailData = new QuotaProfileDetailData(quotaProfileDetail.getQuotaProfileId(), quotaProfileDetail.getName(), quotaProfileDetail.getServiceId(), quotaProfileDetail.getFupLevel(), quotaProfileDetail.getServiceName());

        quotaProfileDetailData.setAggregationKey(aggregationKey);
        quotaProfileDetailData.setTotal(allowedUsage.getTotal());
        quotaProfileDetailData.setTotalUnit(allowedUsage.getTotalUnit().name());

        quotaProfileDetailData.setDownload(allowedUsage.getDownload());
        quotaProfileDetailData.setDownloadUnit(allowedUsage.getDownloadUnit().name());

        quotaProfileDetailData.setUpload(allowedUsage.getUpload());
        quotaProfileDetailData.setUploadUnit(allowedUsage.getUploadUnit().name());

        quotaProfileDetailData.setTime(allowedUsage.getTime());
        quotaProfileDetailData.setTimeUnit(allowedUsage.getTimeUnit().name());
        return quotaProfileDetailData;


    }

    private void applyRequestInterceptors(WebServiceRequest request) {
        for (WebServiceInterceptor interceptor : interceptors) {
            interceptor.requestReceived(request);
        }
    }

    private void applyResponseInterceptors(WebServiceResponse response) {
        for (WebServiceInterceptor interceptor : interceptors) {
            interceptor.responseReceived(response);
        }
    }

    public RnCPackageQueryResponse getRnCPackages(ListPackageRequest request) {
        applyRequestInterceptors(request);

        List<SubscriptionWsScript> groovyScripts = GroovyManager.getInstance().getSubscriptionGroovyScripts();
        for (SubscriptionWsScript groovyScript : groovyScripts) {
            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Executing script: " + groovyScript.getName());
            }

            try {
                groovyScript.preListRnCPackages(request);
            } catch (Exception e) {
                getLogger().error(MODULE, "Error in executing script: " + groovyScript.getName() + " . Reason: " + e.getMessage());
                getLogger().trace(MODULE, e);
            }
        }

        RnCPackageQueryResponse response = doGetAllRnCPackages(request);

        for (SubscriptionWsScript groovyScript : groovyScripts) {
            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Executing script: " + groovyScript.getName());
            }

            try {
                groovyScript.postListRnCPackages(request, response);
            } catch (Exception e) {
                getLogger().error(MODULE, "Error in executing script: " + groovyScript.getName() + " . Reason: " + e.getMessage());
                getLogger().trace(MODULE, e);
            }
        }

        applyResponseInterceptors(response);
        return response;
    }

    private RnCPackageQueryResponse doGetAllRnCPackages(ListPackageRequest request) {
        String pkgId = request.getPackageId();
        String pkgName = request.getPackageName();

        try {
            List<RnCPackageInfo> userRnCPackages = null;
            RnCPkgType pkgType = null;

            if (Strings.isNullOrBlank(request.getPackageType()) == false) {
                pkgType = RnCPkgType.valueOf(request.getPackageType());
                if (pkgType == null) {
                    getLogger().error(MODULE, "Invalid Package Type received " + request.getPackageType());
                    return new RnCPackageQueryResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "Error while fetching RnC packages.Reason: Invalid Package Type received :" + request.getPackageType(), null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());

                }
            }

            if(StringUtils.isNotBlank(request.getCurrency())){
                if (isCurrencyNotInSync(request.getCurrency())) {
                    getLogger().error(MODULE, "Currency other than system currency is not supported");
                    return new RnCPackageQueryResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "Error while fetching RnC packages.Reason: Currency other than system currency is not supported",null,null,null, request.getWebServiceName(), request.getWebServiceMethodName());
                }else if (SystemParameter.CURRENCY.validate(request.getCurrency()) == false) {
                    getLogger().error(MODULE, "Invalid Currency received " + request.getCurrency());
                    return new RnCPackageQueryResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "Error while fetching RnC packages.Reason: Invalid Currency received :" + request.getCurrency(),null,null,null, request.getWebServiceName(), request.getWebServiceMethodName());
                }
            }

            userRnCPackages = getRnCPackageDataList(pkgType, pkgId, pkgName, RncPackageCurrencyPredicate.create(request.getCurrency()));

            if (Collectionz.isNullOrEmpty(userRnCPackages)) {
                return new RnCPackageQueryResponse(ResultCode.NOT_FOUND.code, "No Package Found", null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
            }
            return new RnCPackageQueryResponse(ResultCode.SUCCESS.code, ResultCode.SUCCESS.name, null, null, userRnCPackages, request.getWebServiceName(), request.getWebServiceMethodName());
        } catch (Exception e) {
            getLogger().error(MODULE, "Internal error occured while fetching RnC packages. Reason :" + e.getMessage());
            getLogger().trace(e);
            return new RnCPackageQueryResponse(ResultCode.INTERNAL_ERROR.code, "Reason:" + e.getMessage(), null, null, null, request.getWebServiceName(), request.getWebServiceMethodName());
        }
    }

    private List<RnCPackageInfo> getRnCPackageDataList(RnCPkgType pkgType, String pkgId, String pkgName, Predicate predicate) {
        if (pkgType == null) {
            if (Strings.isNullOrBlank(pkgId) && Strings.isNullOrBlank(pkgName)) {
                return getAllRnCPackages(predicate);
            }
        }

        if (!Strings.isNullOrBlank(pkgId)) {
            return getRnCPackageById(pkgId, predicate);
        }

        if (!Strings.isNullOrBlank(pkgName)) {
            return getRnCPackageByName(pkgName, predicate);
        }

        if (pkgType != null) {
            return getRnCPackagesByPackageType(pkgType, predicate);
        }
        return null;
    }




    private List<RnCPackageInfo> getRnCPackagesByPackageType(RnCPkgType pkgType, Predicate predicate) {
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "No Package Type received so fetching all the RnC packages");
        }

        List<RnCPackage> rncPackages = PolicyManager.getInstance().getRnCPackage().all().stream()
                .filter(rncPkg -> rncPkg.getPackageMode().getOrder() >= PkgMode.TEST.getOrder()
                        && rncPkg.getPolicyStatus() != PolicyStatus.FAILURE
                        && (rncPkg.getPkgType() == pkgType)).collect(Collectors.toList());

        List<RnCPackageInfo> userRnCPackageDataList = Collectionz.newArrayList();
        for (RnCPackage pkg : rncPackages) {
            if (predicate.test(pkg) == false) {
                continue;
            }

            RnCPackageInfo userRnCPackageData = RnCPackageInfo.create(pkg);
            userRnCPackageDataList.add(userRnCPackageData);
        }
        return userRnCPackageDataList;
    }



    private List<RnCPackageInfo> getAllRnCPackages(Predicate predicate) {
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "No Package Type received so fetching all the RnC packages");
        }

        List<RnCPackage> allRnCPackages = PolicyManager.getInstance().getRnCPackage().all();

        List<RnCPackageInfo> userRnCPackageDataList = Collectionz.newArrayList();
        for (RnCPackage pkg : allRnCPackages) {
            if (PolicyStatus.FAILURE == pkg.getPolicyStatus()) {
                continue;
            }
            if (predicate.test(pkg) == false) {
                continue;
            }

            RnCPackageInfo userRnCPackageData = RnCPackageInfo.create(pkg);
            userRnCPackageDataList.add(userRnCPackageData);
        }
        return userRnCPackageDataList;
    }




    private List<RnCPackageInfo> getRnCPackageById(String pkgId, Predicate predicate) {

        List<RnCPackageInfo> userRnCPackageDataList = Collectionz.newArrayList();

        Optional<RnCPackage> rnCPackage = PolicyManager.getInstance().getRnCPackage().all().stream()
                .filter(rncPkg -> rncPkg.getPackageMode().getOrder() >= PkgMode.TEST.getOrder()
                        && rncPkg.getPolicyStatus() != PolicyStatus.FAILURE
                        && (rncPkg.getId().equals(pkgId))).findFirst();
        if (rnCPackage.isPresent() == false) {
            return userRnCPackageDataList;
        }
        RnCPackage pkg = rnCPackage.get();

        if (Objects.isNull(pkg)) {
            return userRnCPackageDataList;
        }

        if (PolicyStatus.FAILURE != pkg.getPolicyStatus() && (predicate.test(pkg))) {
            RnCPackageInfo userRnCPackageData = RnCPackageInfo.create(pkg);
            userRnCPackageDataList.add(userRnCPackageData);
        }


        return userRnCPackageDataList;

    }


    private List<RnCPackageInfo> getRnCPackageByName(String pkgName, Predicate predicate) {

        List<RnCPackageInfo> userRnCPackageDataList = Collectionz.newArrayList();
        Optional<RnCPackage> rnCPackage = PolicyManager.getInstance().getRnCPackage().all().stream()
                .filter(rncPkg -> rncPkg.getPackageMode().getOrder() >= PkgMode.TEST.getOrder()
                        && rncPkg.getPolicyStatus() != PolicyStatus.FAILURE
                        && (rncPkg.getName().equals(pkgName))).findFirst();
        if (rnCPackage.isPresent() == false) {
            return userRnCPackageDataList;
        }
        RnCPackage pkg = rnCPackage.get();
        if (Objects.isNull(pkg)) {
            return userRnCPackageDataList;
        }

        if (PolicyStatus.FAILURE != pkg.getPolicyStatus() && (predicate.test(pkg))) {
            RnCPackageInfo userRnCPackageData = RnCPackageInfo.create(pkg);
            userRnCPackageDataList.add(userRnCPackageData);
        }
        return userRnCPackageDataList;
    }


    public ListPackagesResponse getListPackages(ListPackagesRequest request) {
        applyRequestInterceptors(request);

        List<SubscriptionWsScript> groovyScripts = GroovyManager.getInstance().getSubscriptionGroovyScripts();
        for (SubscriptionWsScript groovyScript : groovyScripts) {
            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Executing script: " + groovyScript.getName());
            }

            try {
                groovyScript.preListPackages(request);
            } catch (Exception e) {
                getLogger().error(MODULE, "Error in executing script: " + groovyScript.getName() + " . Reason: " + e.getMessage());
                getLogger().trace(MODULE, e);
            }
        }

        ListPackagesResponse response = doGetPackages(request);

        for (SubscriptionWsScript groovyScript : groovyScripts) {
            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Executing script: " + groovyScript.getName());
            }

            try {
                groovyScript.postListPackages(request, response);
            } catch (Exception e) {
                getLogger().error(MODULE, "Error in executing script: " + groovyScript.getName() + " . Reason: " + e.getMessage());
                getLogger().trace(MODULE, e);
            }
        }

        applyResponseInterceptors(response);
        return response;
    }

    private ListPackagesResponse doGetPackages(ListPackagesRequest request) {
        String pkgId = request.getPackageId();
        String pkgName = request.getPackageName();

        try {
            List<RnCPackageInfo> rnCPackages = null;
            List<PackageInfo> dataPackages = null;
            EntityType type = null;
            PkgType pkgType = null;
            PkgMode mode = null;
            RnCPkgType rnCPkgType = null;
            Predicate rncPackagePredicate = ALWAYS_TRUE;
            Predicate dataPackagePredicate = ALWAYS_TRUE;
            Map<String,String> idWiseGroupNameMap=null;

            if (StringUtils.isNotBlank(request.getType())) {
                type = EntityType.fromName(request.getType());
                if (type == null || (EntityType.DATA != type && EntityType.RNC != type)) {
                    getLogger().warn(MODULE, "Invalid Type received " + request.getType());
                    return new ListPackagesResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "Error while listing packages.Reason: Invalid Type received :" + request.getType(), null, null, request.getWebServiceName(), request.getWebServiceMethodName(), rnCPackages, dataPackages);
                }
            }

            if (Strings.isNullOrBlank(request.getPackageType()) == false) {
                pkgType = PkgType.fromName(request.getPackageType());
                rnCPkgType = RnCPkgType.fromName(request.getPackageType());
                if (pkgType == null && rnCPkgType == null) {
                    getLogger().error(MODULE, "Invalid Package-Type received " + request.getPackageType());
                    return new ListPackagesResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "Error while listing packages.Reason: Invalid Package-Type received :" + request.getPackageType(), null, null, request.getWebServiceName(), request.getWebServiceMethodName(), rnCPackages, dataPackages);
                }

            }

            if (Strings.isNullOrBlank(request.getMode()) == false) {
                mode = PkgMode.getMode(request.getMode());
                if (mode == null || (PkgMode.TEST != mode && PkgMode.LIVE != mode && PkgMode.LIVE2 != mode)) {
                    getLogger().error(MODULE, "Invalid Mode received " + request.getMode());
                    return new ListPackagesResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "Error while listing packages.Reason: Invalid Package-Mode received :" + request.getMode(), null, null, request.getWebServiceName(), request.getWebServiceMethodName(), rnCPackages, dataPackages);
                }

            }

            if(Strings.isNullOrBlank(request.getGroup()) == false){
                List<String> groupNameList = CommonConstants.COMMA_SPLITTER.split(request.getGroup());
                idWiseGroupNameMap = getGroupMapByName(groupNameList);
                if(idWiseGroupNameMap.isEmpty()){
                    getLogger().error(MODULE, "Invalid Group received " + request.getGroup());
                    return new ListPackagesResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "Error while listing packages.Reason: Invalid Group received :" + request.getGroup(), null, null, request.getWebServiceName(), request.getWebServiceMethodName(), rnCPackages, dataPackages);
                }
            }

            if(StringUtils.isNotBlank(request.getCurrency())){
                if (isCurrencyNotInSync(request.getCurrency())) {
                    getLogger().error(MODULE, "Currency other than system currency is not supported");
                    return new ListPackagesResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "Error while listing packages.Reason: Currency other than system currency is not supported", null, null, request.getWebServiceName(), request.getWebServiceMethodName(), rnCPackages, dataPackages);
                }else if (SystemParameter.CURRENCY.validate(request.getCurrency()) == false) {
                    getLogger().error(MODULE, "Invalid Currency received " + request.getCurrency());
                    return new ListPackagesResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "Error while listing packages.Reason: Invalid Currency received :" + request.getCurrency(), null, null, request.getWebServiceName(), request.getWebServiceMethodName(), rnCPackages, dataPackages);
                }
            }


            if (mode != null) {
                rncPackagePredicate = RncPackageModePredicate.create(mode);
                dataPackagePredicate = DataPackageModePredicate.create(mode);
            }

            rncPackagePredicate=rncPackagePredicate.and(RncPackageGroupPredicate.create(idWiseGroupNameMap)).and(RncPackageCurrencyPredicate.create(request.getCurrency()));
            dataPackagePredicate=dataPackagePredicate.and(DataPackageGroupPredicate.create(idWiseGroupNameMap)).and(DataPackageCurrencyPredicate.create(request.getCurrency()));

            if (type != null && type == EntityType.DATA) {
                dataPackages = getUserPackageDataList(pkgType, pkgId, pkgName, dataPackagePredicate);
            } else if (type != null && type == EntityType.RNC) {
                rnCPackages = getRnCPackageDataList(rnCPkgType, pkgId, pkgName, rncPackagePredicate);
            } else {
                dataPackages = getUserPackageDataList(pkgType, pkgId, pkgName, dataPackagePredicate);
                rnCPackages = getRnCPackageDataList(rnCPkgType, pkgId, pkgName, rncPackagePredicate);
            }


            if (Collectionz.isNullOrEmpty(rnCPackages) && Collectionz.isNullOrEmpty(dataPackages)) {
                return new ListPackagesResponse(ResultCode.NOT_FOUND.code, "No Packages Found", null, null, request.getWebServiceName(), request.getWebServiceMethodName(), rnCPackages, dataPackages);
            }

            return new ListPackagesResponse(ResultCode.SUCCESS.code, ResultCode.SUCCESS.name, null, null, request.getWebServiceName(), request.getWebServiceMethodName(), rnCPackages, dataPackages);
        } catch (Exception e) {
            getLogger().error(MODULE, "Internal error occured while listing packages. Reason :" + e.getMessage());
            getLogger().trace(e);
            return new ListPackagesResponse(ResultCode.INTERNAL_ERROR.code, "Reason:" + e.getMessage(), null, null, request.getWebServiceName(), request.getWebServiceMethodName(), null, null);
        }
    }

    private boolean isCurrencyNotInSync(String currency) {
        return (SystemParameterDAO.isMultiCurrencyEnable() == false) && (SystemParameterDAO.getCurrency().equals(currency) == false);
    }

    private Map<String,String> getGroupMapByName(List<String> groupNameList) throws Exception{
        List<GroupData> groupDatas= importExportUtil.getGroups();
        Map<String,String> idWiseGroupNameMap = Maps.newHashMap();

        for(String groupName:groupNameList) {
            GroupData groupData = groupDatas.stream().filter(name -> name.getName().equalsIgnoreCase(groupName)).findAny().orElse(null);
            if(groupData == null){
                idWiseGroupNameMap.clear();
                return idWiseGroupNameMap;
            }
            idWiseGroupNameMap.put(groupData.getId(),groupData.getName());
        }
        return idWiseGroupNameMap;
    }

    private boolean isValidGroupId(Map<String,String> idWiseGroupNameMap, List<String> groupIds){
        List<String> groupDataList = Collectionz.newArrayList();
        if(idWiseGroupNameMap == null){
            return true;
        }
        for (Map.Entry<String, String> groupData : idWiseGroupNameMap.entrySet()) {
            if (groupIds.stream().anyMatch(s -> groupData.getKey().contains(s))){
               groupDataList.add(groupData.getValue());
            }
        }
        return !(groupDataList.isEmpty());
    }

}