package com.elitecore.corenetvertex.core.validator;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.Discriminators;
import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.constants.QuotaProfileType;
import com.elitecore.corenetvertex.pkg.PkgData;
import com.elitecore.corenetvertex.pkg.PkgGroupOrderData;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pkg.PkgType;
import com.elitecore.corenetvertex.pkg.notification.QuotaNotificationData;
import com.elitecore.corenetvertex.util.MessageResult;
import com.elitecore.corenetvertex.util.SessionProvider;
import com.elitecore.corenetvertex.util.commons.ImportExportCRUDOperationUtil;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.elitecore.corenetvertex.core.validator.GlobalPkgValidations.isGlobalPlan;


public class PkgValidator implements Validator<PkgData, PkgData, PkgData> {


    private static final String MODULE = "PKG-VALIDATOR";


    @Override
    public List<String> validate(PkgData pkgDataImported, PkgData pkgData, PkgData superObject, String action, SessionProvider session) {
        List<String> subReasons = new ArrayList<String>();
        try {

            String id = pkgDataImported.getId();
            String name = pkgDataImported.getName();


            if (Strings.isNullOrBlank(id) && Strings.isNullOrBlank(name)) {
                subReasons.add("Package Id or name is mandatory");
                return subReasons;
            }
            BasicValidations.validateName(name, "Package", subReasons);

            if (Collectionz.isNullOrEmpty(subReasons) == false) {
                return subReasons;
            }

            validateParameters(pkgDataImported, subReasons);


            validateRateCardBasedOnPkgType(pkgDataImported, subReasons);

            MessageResult messageResultInfo = null;
            if (pkgDataImported.getQuotaProfileType() == QuotaProfileType.USAGE_METERING_BASED) {
                messageResultInfo = PackageImportValidator.validateUniquenessOfUsageMeteringQuotaProfileWithinPackage(pkgDataImported.getQuotaProfiles());
            } else if (pkgDataImported.getQuotaProfileType() == QuotaProfileType.SY_COUNTER_BASED) {
                messageResultInfo = PackageImportValidator.validateUniquenessOfSyQuotaProfileWithinPackage(pkgDataImported.getSyQuotaProfileDatas());
            } else if (QuotaProfileType.RnC_BASED == pkgDataImported.getQuotaProfileType()) {
                messageResultInfo = PackageImportValidator.validateUniquenessOfQuotaBalanceBasedQuotaProfileWithinPackage(pkgDataImported.getQuotaProfiles());
            }
            if (messageResultInfo.getFlag() == false) {
                String message = getMessage(pkgDataImported, Discriminators.QUOTA_PROFILE, messageResultInfo.getMessage());
                subReasons.add(message);
                return subReasons;
            }

            messageResultInfo = PackageImportValidator.validateUniquenessOfQoSProfileWithinPackage(pkgDataImported.getQosProfiles());
            if (messageResultInfo.getFlag() == false) {
                String message = getMessage(pkgDataImported, Discriminators.QOS_PROFILE, messageResultInfo.getMessage());
                subReasons.add(message);
                return subReasons;
            }

            messageResultInfo = PackageImportValidator.validateUniquenessOfPCCRuleWithinPackage(pkgDataImported.getQosProfiles());
            if (messageResultInfo.getFlag() == false) {
                String message = getMessage(pkgDataImported, Discriminators.PCCRULE, messageResultInfo.getMessage());
                subReasons.add(message);
                return subReasons;
            }

            messageResultInfo = PackageImportValidator.validateUniquenessOfMonitoringKeyWithinPackage(pkgDataImported.getQosProfiles());
            if (messageResultInfo.getFlag() == false) {
                String message = getMessage(pkgDataImported, CommonConstants.MONITORING_KEY, messageResultInfo.getMessage());
                subReasons.add(message);
                return subReasons;
            }


            //CHECK FOR PACKAGE ID OR NAME EXISTS OR NOT
            validateWithExistingPackage(pkgDataImported, action, session, subReasons);

            //CHECK FOR MAX QUOTA PROFILES
            if (QuotaProfileType.USAGE_METERING_BASED == pkgDataImported.getQuotaProfileType() && Collectionz.isNullOrEmpty(pkgDataImported.getQuotaProfiles()) == false && pkgDataImported.getQuotaProfiles().size() > 3) {
                subReasons.add("Max 3 Quota Profiles can be configured with package " + BasicValidations.printIdAndName(id, name));
            } else if (QuotaProfileType.SY_COUNTER_BASED == pkgDataImported.getQuotaProfileType() && Collectionz.isNullOrEmpty(pkgDataImported.getSyQuotaProfileDatas()) == false && pkgDataImported.getSyQuotaProfileDatas().size() > 3) {
                subReasons.add("Max 3 Sy Quota Profiles can be configured with package " + BasicValidations.printIdAndName(id, name));
            }

            //TO DO check group wise limit for  for Global Plan

            if (Collectionz.isNullOrEmpty(subReasons) && GlobalPkgValidations.isGlobalPlan(pkgDataImported)) {
                String mode = ImportExportCRUDOperationUtil.MODE_CREATE;
                if (CommonConstants.REPLACE.equalsIgnoreCase(action)) {
                    mode = ImportExportCRUDOperationUtil.MODE_UPDATE;
                }
                if (GlobalPkgValidations.isGroupWiseLimitReach(pkgDataImported.getClass(), pkgDataImported.getId(), pkgDataImported.getName(), mode, pkgDataImported.getType(), pkgDataImported.getGroups(), session, subReasons) == true) {
                    return subReasons;
                }
                if (Collectionz.isNullOrEmpty(pkgDataImported.getPkgGroupWiseOrders())) {
                    setPkgGroupOrder(pkgDataImported, null, session);
                }
            }
            validateWithExistingNotificationConfigAndThreshold(pkgDataImported.getQuotaNotificationDatas(),subReasons);

        } catch (Exception e) {
            LogManager.getLogger().error(MODULE, "Failed to validate package with " + BasicValidations.printIdAndName(pkgDataImported.getId(), pkgDataImported.getName()));
            LogManager.getLogger().trace(e);
            subReasons.add("Failed to validate package with " + BasicValidations.printIdAndName(pkgDataImported.getId(), pkgDataImported.getName()) + " . Kindly refer logs for further details");

        }


        return subReasons;
    }

    private void validateWithExistingNotificationConfigAndThreshold(List<QuotaNotificationData> quotaNotificationImported, List<String> subReasons) {
        Set<String> keys = new HashSet<>();
        for (QuotaNotificationData quotaNotificationData : quotaNotificationImported) {
            String key = quotaNotificationData.getQuotaProfileId() + quotaNotificationData.getAggregationKey().getVal() + quotaNotificationData.getFupLevel() + quotaNotificationData.getDataServiceTypeData().getId() + quotaNotificationData.getThreshold();
            if (keys.add(key) == false) {
                subReasons.add("Duplicate notification found for threshold: " + quotaNotificationData.getThreshold());
            } else {
                keys.add(key);
            }
        }
    }

    private void validateRateCardBasedOnPkgType(PkgData pkgDataImported, List<String> subReasons) {
        if ((PkgType.BASE.name().equalsIgnoreCase(pkgDataImported.getType()) &&
                QuotaProfileType.RnC_BASED == pkgDataImported.getQuotaProfileType()) == false) {
            if (Collectionz.isNullOrEmpty(pkgDataImported.getRateCards()) == false) {
                subReasons.add("Rate cards only be configured with Base type package and RnC base quota profile");
            }
        }
    }

    private String getMessage(PkgData pkgDataImported, String module, String name) {
        StringBuilder sb = new StringBuilder();
        sb.append(module);
        sb.append(" with name: ");
        sb.append(name);
        sb.append(" already exists in ");
        sb.append(Discriminators.PACKAGE);
        sb.append(BasicValidations.printIdAndName(pkgDataImported.getId(), pkgDataImported.getName()));
        return sb.toString();
    }

    private void validateParameters(PkgData pkgDataImported, List<String> subReasons) {
        //validate type
        String pkgType = pkgDataImported.getType();
        if (Strings.isNullOrEmpty(pkgType)) {
            subReasons.add("Package Type must be provided with package " + BasicValidations.printIdAndName(pkgDataImported.getId(), pkgDataImported.getName()));
        } else if (PkgType.BASE.name().equalsIgnoreCase(pkgType) == false
                && PkgType.ADDON.name().equalsIgnoreCase(pkgType) == false
                && PkgType.PROMOTIONAL.name().equalsIgnoreCase(pkgType) == false) {
            subReasons.add("Invalid Package Type: " + pkgType + " is configured with package " + BasicValidations.printIdAndName(pkgDataImported.getId(), pkgDataImported.getName()));
        } else {
            pkgDataImported.setType(pkgDataImported.getType().toUpperCase());
        }

        //validate mode
        String mode = pkgDataImported.getPackageMode();
        if (Strings.isNullOrBlank(mode)) {
            if (LogManager.getLogger().isDebugLogLevel()) {
                LogManager.getLogger().debug(MODULE, "Package Mode is not configured so taking default mode as " + PkgMode.DESIGN + " for package " + BasicValidations.printIdAndName(pkgDataImported.getId(), pkgDataImported.getName()));
            }
            pkgDataImported.setPackageMode(PkgMode.DESIGN.name());
        } else if ((PkgMode.DESIGN.name().equalsIgnoreCase(mode) == false && PkgMode.TEST.name().equalsIgnoreCase(mode) == false && PkgMode.LIVE.name().equalsIgnoreCase(mode) == false && PkgMode.LIVE2.name().equalsIgnoreCase(mode) == false)) {
            if (LogManager.getLogger().isDebugLogLevel()) {
                LogManager.getLogger().debug(MODULE, "Invalid package mode: " + mode + " is configured so taking default mode as " + PkgMode.DESIGN + " for package " + BasicValidations.printIdAndName(pkgDataImported.getId(), pkgDataImported.getName()));
            }
            pkgDataImported.setPackageMode(PkgMode.DESIGN.name());
        } else {
            pkgDataImported.setPackageMode(pkgDataImported.getPackageMode().toUpperCase());
        }

        //validate Status
        PkgStatus pkgStatus = PkgStatus.fromVal(pkgDataImported.getStatus());

        if (pkgStatus == null) {
            LogManager.getLogger().warn(MODULE, "Invalid package status " + pkgDataImported.getStatus() + " is configured so taking default status as: " + PkgStatus.ACTIVE.name() + " for package " + BasicValidations.printIdAndName(pkgDataImported.getId(), pkgDataImported.getName()));
            pkgDataImported.setStatus(PkgStatus.ACTIVE.name());
        }
        if (pkgStatus == PkgStatus.RETIRED && PkgType.PROMOTIONAL.name().equalsIgnoreCase(pkgDataImported.getType())) {
            subReasons.add("Promotional Package with status " + pkgStatus + " can not be imported.");
        }

        //Validating quota profile type
        if (pkgDataImported.getQuotaProfileType() == null) {
            subReasons.add("Invalid Quota profile type " + pkgDataImported.getQuotaProfileType() + " is configured with Package " + BasicValidations.printIdAndName(pkgDataImported.getId(), pkgDataImported.getName()));
        }

        if (PkgType.PROMOTIONAL.name().equals(pkgDataImported.getType()) == true) {
            if (pkgDataImported.getAvailabilityStartDate() == null) {
                subReasons.add("Availability Start Date is mandatory for Promotional package " + BasicValidations.printIdAndName(pkgDataImported.getId(), pkgDataImported.getName()));
                return;
            }

            //Validating Quota Profile Type for Promotional package
            if (QuotaProfileType.RnC_BASED.equals(pkgDataImported.getQuotaProfileType())) {
                subReasons.add("Promotional package " + pkgDataImported.getName() + " can not be configured with Quota Balance Based Quota Profile Type");
            }

            if (pkgDataImported.getAvailabilityEndDate() == null) {
                subReasons.add("Availability End Date is mandatory for Promotional package" + BasicValidations.printIdAndName(pkgDataImported.getId(), pkgDataImported.getName()));
                return;
            }
            if ((pkgDataImported.getAvailabilityEndDate().getTime() > System.currentTimeMillis()) == false) {
                subReasons.add("Availability End Date must be greater than current time for Promotional Package " + BasicValidations.printIdAndName(pkgDataImported.getId(), pkgDataImported.getName()));
                return;
            }
            if ((pkgDataImported.getAvailabilityEndDate().getTime() >= pkgDataImported.getAvailabilityStartDate().getTime()) == false) {
                subReasons.add("Availability End Date must be greater than Availability Start Date for Promotional Package " + BasicValidations.printIdAndName(pkgDataImported.getId(), pkgDataImported.getName()));
                return;
            }

            if ((pkgDataImported.getAvailabilityEndDate().getTime() - pkgDataImported.getAvailabilityStartDate().getTime()) < CommonConstants.TEN_MINUTES) {
                subReasons.add("Package minimum availability Duration must be at least 10 minutes for Promotional Package " + BasicValidations.printIdAndName(pkgDataImported.getId(), pkgDataImported.getName()));
                return;
            }

            if (QuotaProfileType.SY_COUNTER_BASED.equals(pkgDataImported.getQuotaProfileType())) {
                subReasons.add("Promotional package must be configured with Usage Metering Based Quota Profile Type " + BasicValidations.printIdAndName(pkgDataImported.getId(), pkgDataImported.getName()));
                return;
            }

        }

    }

    private void validatePackageMode(PkgData pkgDataImported, List<String> subReasons) {
        if (PkgMode.LIVE2.name().equalsIgnoreCase(pkgDataImported.getPackageMode()) || PkgMode.LIVE.name().equalsIgnoreCase(pkgDataImported.getPackageMode())) {
            subReasons.add("Package with mode " + pkgDataImported.getPackageMode() + " can not be replaced");
        }
    }


    private void validateWithExistingPackage(PkgData pkgDataImported, String action, SessionProvider session, List<String> subReasons) throws Exception {
        if (Strings.isNullOrBlank(pkgDataImported.getId()) == false && Strings.isNullOrBlank(pkgDataImported.getName()) == false) {
            if (validateExistingPackageWithId(pkgDataImported, action, session, subReasons) == false) {
                return;
            }
        }
        //CHECK FOR DUPLICATE NAME
        if (Strings.isNullOrBlank(pkgDataImported.getName()) == false) {

            validateExistingPackageWithName(pkgDataImported, action, session, subReasons);
        }
    }

    private void validateExistingPackageWithName(PkgData pkgDataImported, String action, SessionProvider session, List<String> subReasons) throws Exception {
        List<PkgData> pkgs = (List<PkgData>) ImportExportCRUDOperationUtil.getPackageByName(pkgDataImported.getClass(), pkgDataImported.getName(), session);

        if (Collectionz.isNullOrEmpty(pkgs) != false) {
            return;
        }

        for (PkgData existingPkgData : pkgs) {
            if (CommonConstants.STATUS_DELETED.equalsIgnoreCase(existingPkgData.getStatus())) {
                ImportExportCRUDOperationUtil.removePackages(existingPkgData, session);
                continue;
            }

            if (validateWithExistingPackage(pkgDataImported, subReasons, existingPkgData) == false) {
                return;
            }

            if (CommonConstants.FAIL.equalsIgnoreCase(action)) {
                subReasons.add("Package Name already exists: " + pkgDataImported.getName());
            } else if (CommonConstants.REPLACE.equalsIgnoreCase(action)) {
                if (Strings.isNullOrBlank(pkgDataImported.getId()) == false && pkgDataImported.getId().equalsIgnoreCase(existingPkgData.getId()) == false) {
                    subReasons.add("Imported Package with " + BasicValidations.printIdAndName(pkgDataImported.getId(), pkgDataImported.getName()) + " conflicts with Existing Package " + BasicValidations.printIdAndName(existingPkgData.getId(), existingPkgData.getName()));
                    return;
                }
                validatePackageMode(existingPkgData, subReasons);
                if(CollectionUtils.isNotEmpty(subReasons)){
                    return ;
                }
                if (isGlobalPlan(pkgDataImported)) {
                    setPkgGroupOrder(pkgDataImported, existingPkgData.getId(), session);
                }
                ImportExportCRUDOperationUtil.removePackageHierarchy(existingPkgData, session);
                pkgDataImported.setId(existingPkgData.getId());
            }

        }
    }

    private boolean validateExistingPackageWithId(PkgData pkgDataImported, String action, SessionProvider session, List<String> subReasons) throws Exception {
        PkgData existingPkgData = ImportExportCRUDOperationUtil.get(pkgDataImported.getClass(), pkgDataImported.getId(), session);
        if (existingPkgData == null) {
            return true;
        }

        if (CommonConstants.STATUS_DELETED.equalsIgnoreCase(existingPkgData.getStatus())) {
            ImportExportCRUDOperationUtil.removePackages(existingPkgData, session);
            return true;
        }

        if (validateWithExistingPackage(pkgDataImported, subReasons, existingPkgData) == false) {
            return false;
        }


        if (pkgDataImported.getName().equals(existingPkgData.getName())) {


            if (CommonConstants.FAIL.equalsIgnoreCase(action)) {
                subReasons.add("Package Id already exists: " + pkgDataImported.getId() + " for package name: " + pkgDataImported.getName());
            } else if (CommonConstants.REPLACE.equalsIgnoreCase(action)) {
                validatePackageMode(existingPkgData, subReasons);
                if(CollectionUtils.isNotEmpty(subReasons)){
                    return false;
                }
                if (isGlobalPlan(pkgDataImported)) {
                    setPkgGroupOrder(pkgDataImported, existingPkgData.getId(), session);
                }
                ImportExportCRUDOperationUtil.removePackageHierarchy(existingPkgData, session);
            }
        } else {
            subReasons.add("Imported Package with " + BasicValidations.printIdAndName(pkgDataImported.getId(), pkgDataImported.getName()) + " conflicts with Existing Package " + BasicValidations.printIdAndName(existingPkgData.getId(), existingPkgData.getName()));
        }

        return true;
    }

    private boolean validateWithExistingPackage(PkgData pkgDataImported, List<String> subReasons, PkgData existingPkgData) {

        if (existingPkgData.getType().equalsIgnoreCase(pkgDataImported.getType()) == false) {
            subReasons.add("Existing Package " + BasicValidations.printIdAndName(existingPkgData.getId(), existingPkgData.getName()) + " is of type " + existingPkgData.getType()
                    + " and Imported package " + BasicValidations.printIdAndName(pkgDataImported.getId(), pkgDataImported.getName()) + " is of type " + pkgDataImported.getType());
            return false;
        }
        if ((pkgDataImported.getPackageMode().equalsIgnoreCase(PkgMode.DESIGN.name()) || pkgDataImported.getPackageMode().equalsIgnoreCase(PkgMode.TEST.name()))
                && (existingPkgData.getPackageMode().equalsIgnoreCase(PkgMode.LIVE.name()) || existingPkgData.getPackageMode().equalsIgnoreCase(PkgMode.LIVE2.name()))) {
            subReasons.add("Existing Package " + BasicValidations.printIdAndName(existingPkgData.getId(), existingPkgData.getName()) + " is of mode " + existingPkgData.getPackageMode()
                    + " and Imported package " + BasicValidations.printIdAndName(pkgDataImported.getId(), pkgDataImported.getName()) + " is of mode " + pkgDataImported.getPackageMode());
            return false;
        }

        if (Strings.isNullOrBlank(pkgDataImported.getQuotaProfileType().name()) == false && pkgDataImported.getQuotaProfileType() != existingPkgData.getQuotaProfileType()) {
            subReasons.add("Existing Package " + BasicValidations.printIdAndName(existingPkgData.getId(), existingPkgData.getName()) + " has quota profile type " + existingPkgData.getQuotaProfileType().name()
                    + " and Imported package " + BasicValidations.printIdAndName(pkgDataImported.getId(), pkgDataImported.getName()) + " has quota profile type " + pkgDataImported.getQuotaProfileType().name());
            return false;
        }
        return true;
    }

    private void setPkgGroupOrder(PkgData importedPkg, String existingPkgId, SessionProvider sessionProvider) throws Exception {
        List<PkgGroupOrderData> pkgGroupOrderDatas = PackageImportValidator.getPkgGroupOrderDatas(importedPkg.getGroups(), importedPkg.getType(), existingPkgId, sessionProvider);
        importedPkg.getPkgGroupWiseOrders().clear();
        importedPkg.getPkgGroupWiseOrders().addAll(pkgGroupOrderDatas);
    }
}