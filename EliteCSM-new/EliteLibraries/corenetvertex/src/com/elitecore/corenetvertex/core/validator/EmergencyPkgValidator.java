package com.elitecore.corenetvertex.core.validator;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.Discriminators;
import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.pkg.EmergencyPkgDataExt;
import com.elitecore.corenetvertex.pkg.PkgData;
import com.elitecore.corenetvertex.pkg.PkgGroupOrderData;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pkg.PkgType;
import com.elitecore.corenetvertex.util.MessageResult;
import com.elitecore.corenetvertex.util.SessionProvider;
import com.elitecore.corenetvertex.util.commons.ImportExportCRUDOperationUtil;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

import static com.elitecore.corenetvertex.core.validator.GlobalPkgValidations.isGroupWiseLimitReach;

/**
 * Created by Ishani on 30/11/16.
 */
public class EmergencyPkgValidator implements Validator<EmergencyPkgDataExt, EmergencyPkgDataExt, EmergencyPkgDataExt> {

    private static final String MODULE = "EMERGENCY-PKG-VALIDATOR";
    @Override
    public List<String> validate(EmergencyPkgDataExt emergencyPkgDataImported, EmergencyPkgDataExt parentObject, EmergencyPkgDataExt superObject, String action, SessionProvider session) {
        List<String> subReasons = new ArrayList<String>();
        try {

            String name = emergencyPkgDataImported.getName();
            BasicValidations.validateName(name,"Emergency Package", subReasons);

            if(Collectionz.isNullOrEmpty(subReasons) == false){
                return subReasons;
            }

            validateParameters(emergencyPkgDataImported, subReasons,session);

            MessageResult messageResultInfo = PackageImportValidator.validateUniquenessOfQoSProfileWithinPackage(emergencyPkgDataImported.getQosProfiles());
            if(messageResultInfo.getFlag() == false) {
                String message = getMessage(emergencyPkgDataImported, Discriminators.QOS_PROFILE, messageResultInfo.getMessage());
                subReasons.add(message);
                return subReasons;
            }

            messageResultInfo = PackageImportValidator.validateUniquenessOfPCCRuleWithinPackage(emergencyPkgDataImported.getQosProfiles());
            if(messageResultInfo.getFlag() == false) {
                String message = getMessage(emergencyPkgDataImported, Discriminators.PCCRULE, messageResultInfo.getMessage());
                subReasons.add(message);
                return subReasons;
            }

            messageResultInfo = PackageImportValidator.validateUniquenessOfMonitoringKeyWithinPackage(emergencyPkgDataImported.getQosProfiles());
            if(messageResultInfo.getFlag() == false) {
                String message = getMessage(emergencyPkgDataImported, CommonConstants.MONITORING_KEY, messageResultInfo.getMessage());
                subReasons.add(message);
                return subReasons;
            }

            //CHECK FOR PACKAGE ID OR NAME EXISTS OR NOT
            validateWithExistingPackage(emergencyPkgDataImported, action, session, subReasons);

            if (Collectionz.isNullOrEmpty(subReasons)) {
                String mode = ImportExportCRUDOperationUtil.MODE_CREATE;
                if (CommonConstants.REPLACE.equalsIgnoreCase(action)) {
                    mode = ImportExportCRUDOperationUtil.MODE_UPDATE;
                }

                if (isGroupWiseLimitReach(com.elitecore.corenetvertex.pkg.PkgData.class, emergencyPkgDataImported.getId(), emergencyPkgDataImported.getName(), mode, emergencyPkgDataImported.getType(),
                        emergencyPkgDataImported.getGroups(), session, subReasons) == true) {
                    return subReasons;
                }
                if (Collectionz.isNullOrEmpty(emergencyPkgDataImported.getPkgGroupWiseOrders())) {
                    setPkgGroupOrder(emergencyPkgDataImported, null, session);
                }
            }

        } catch (Exception e) {
            LogManager.getLogger().error(MODULE, "Failed to validate Emergency package with " + BasicValidations.printIdAndName(emergencyPkgDataImported.getId(), emergencyPkgDataImported.getName()));
            LogManager.getLogger().trace(e);
            subReasons.add("Failed to validate Emergency package with " + BasicValidations.printIdAndName(emergencyPkgDataImported.getId(), emergencyPkgDataImported.getName()) + " . Kindly refer logs for further details");

        }


        return subReasons;
    }

    private void validateParameters(EmergencyPkgDataExt emergencyPkgDataImported, List<String> subReasons, SessionProvider session) throws Exception {
        //Validate Availability End Date
        if(emergencyPkgDataImported.getAvailabilityStartDate() == null){
            subReasons.add("Availability Start Date must be specified with Emergency Package " + BasicValidations.printIdAndName(emergencyPkgDataImported.getId(), emergencyPkgDataImported.getName()));
        }else{
            if(emergencyPkgDataImported.getAvailabilityEndDate() != null ){
                if(emergencyPkgDataImported.getAvailabilityEndDate().getTime() < System.currentTimeMillis()){
                    subReasons.add("Availability End Date must be greater than current time configured with Emergency Package " + BasicValidations.printIdAndName(emergencyPkgDataImported.getId(), emergencyPkgDataImported.getName()));
                }else if(emergencyPkgDataImported.getAvailabilityStartDate().getTime() >= emergencyPkgDataImported.getAvailabilityEndDate().getTime()){
                    subReasons.add("Availability End Date must be greater than Availability Start Date configured with Emergency Package " + BasicValidations.printIdAndName(emergencyPkgDataImported.getId(), emergencyPkgDataImported.getName()));
                }
            }
        }

        //validate type
        String pkgType = emergencyPkgDataImported.getType();
        if (Strings.isNullOrEmpty(pkgType)) {
            subReasons.add("Package Type must be provided with Emergency package " + BasicValidations.printIdAndName(emergencyPkgDataImported.getId(), emergencyPkgDataImported.getName()));
        } else if (PkgType.EMERGENCY.name().equalsIgnoreCase(pkgType) == false) {
            subReasons.add("Invalid Package Type: " + pkgType + " is configured with Emergency Package " + BasicValidations.printIdAndName(emergencyPkgDataImported.getId(), emergencyPkgDataImported.getName()));
        } else {
            emergencyPkgDataImported.setType(emergencyPkgDataImported.getType().toUpperCase());
        }

        //validate mode
        String mode = emergencyPkgDataImported.getPackageMode();
        if (Strings.isNullOrBlank(mode)) {
            if (LogManager.getLogger().isDebugLogLevel()) {
                LogManager.getLogger().debug(MODULE, "Package Mode is not configured so taking default mode as " + PkgMode.DESIGN + " for emergency package " + BasicValidations.printIdAndName(emergencyPkgDataImported.getId(), emergencyPkgDataImported.getName()));
            }
            emergencyPkgDataImported.setPackageMode(PkgMode.DESIGN.name());
        } else if ((PkgMode.DESIGN.name().equalsIgnoreCase(mode) == false && PkgMode.TEST.name().equalsIgnoreCase(mode) == false && PkgMode.LIVE.name().equalsIgnoreCase(mode) == false && PkgMode.LIVE2.name().equalsIgnoreCase(mode) == false)) {
            if (LogManager.getLogger().isDebugLogLevel()) {
                LogManager.getLogger().debug(MODULE, "Invalid package mode: " + mode + " is configured so taking default mode as " + PkgMode.DESIGN + " for emergency package " + BasicValidations.printIdAndName(emergencyPkgDataImported.getId(), emergencyPkgDataImported.getName()));
            }
            emergencyPkgDataImported.setPackageMode(PkgMode.DESIGN.name());
        } else {
            emergencyPkgDataImported.setPackageMode(emergencyPkgDataImported.getPackageMode().toUpperCase());
        }


        //validate Status
        PkgStatus pkgStatus = PkgStatus.fromVal(emergencyPkgDataImported.getStatus());

        if (pkgStatus == null) {
            LogManager.getLogger().warn(MODULE, "Invalid package status " + emergencyPkgDataImported.getStatus() + " is configured so taking default status as: " + PkgStatus.ACTIVE.name() + " for emergency package " + BasicValidations.printIdAndName(emergencyPkgDataImported.getId(), emergencyPkgDataImported.getName()));
            emergencyPkgDataImported.setStatus(PkgStatus.ACTIVE.name());
        }
        if(pkgStatus == PkgStatus.RETIRED){
            subReasons.add("Emergency Package with status " + pkgStatus + " can not be imported.");
        }

    }

    private void validatePackageMode(EmergencyPkgDataExt emergencyPkgDataImported, EmergencyPkgDataExt existingEmergencyPkgData, List<String> subReasons) {
        if (PkgMode.LIVE2.name().equalsIgnoreCase(emergencyPkgDataImported.getPackageMode()) || PkgMode.LIVE.name().equalsIgnoreCase(emergencyPkgDataImported.getPackageMode())) {
            subReasons.add("Emergency Package with mode " + emergencyPkgDataImported.getPackageMode() + " can not be replaced");
        }
    }

    private void validateWithExistingPackage(EmergencyPkgDataExt emergencyPkgDataImported, String action, SessionProvider session, List<String> subReasons) throws Exception {
        if (Strings.isNullOrBlank(emergencyPkgDataImported.getId()) == false) {

            EmergencyPkgDataExt existingEmergencyPkgData = ImportExportCRUDOperationUtil.get(emergencyPkgDataImported.getClass(), emergencyPkgDataImported.getId(), session);
            if (existingEmergencyPkgData != null) {
                if (CommonConstants.STATUS_DELETED.equalsIgnoreCase(existingEmergencyPkgData.getStatus())) {
                    ImportExportCRUDOperationUtil.removeEmergencyPackages(existingEmergencyPkgData, session);
                } else {
                    if (existingEmergencyPkgData.getType().equalsIgnoreCase(emergencyPkgDataImported.getType()) == false) {
                        subReasons.add("Existing Emergency Package " + BasicValidations.printIdAndName(existingEmergencyPkgData.getId(), existingEmergencyPkgData.getName()) + " is of type " + existingEmergencyPkgData.getType()
                                + " and Imported Emergency package " + BasicValidations.printIdAndName(emergencyPkgDataImported.getId(), emergencyPkgDataImported.getName()) + " is of type " + emergencyPkgDataImported.getType());
                        return;
                    }
                    if ((emergencyPkgDataImported.getPackageMode().equalsIgnoreCase(PkgMode.DESIGN.name()) || emergencyPkgDataImported.getPackageMode().equalsIgnoreCase(PkgMode.TEST.name()))
                            && (existingEmergencyPkgData.getPackageMode().equalsIgnoreCase(PkgMode.LIVE.name()) || existingEmergencyPkgData.getPackageMode().equalsIgnoreCase(PkgMode.LIVE2.name()))) {
                        subReasons.add("Existing Emergency Package " + BasicValidations.printIdAndName(existingEmergencyPkgData.getId(), existingEmergencyPkgData.getName()) + " is of mode " + existingEmergencyPkgData.getPackageMode()
                                + " and Imported Emergency package " + BasicValidations.printIdAndName(emergencyPkgDataImported.getId(), emergencyPkgDataImported.getName()) + " is of mode " + emergencyPkgDataImported.getPackageMode());
                        return;
                    }
                    if (CommonConstants.FAIL.equalsIgnoreCase(action)) {
                        subReasons.add("Emergency Package Id already exists: " + emergencyPkgDataImported.getId() + " for Emergency package name: " + emergencyPkgDataImported.getName());
                    } else if (CommonConstants.REPLACE.equalsIgnoreCase(action)) {
                        validatePackageMode(emergencyPkgDataImported, existingEmergencyPkgData, subReasons);
                        setPkgGroupOrder(emergencyPkgDataImported,existingEmergencyPkgData.getId(),session);
                        ImportExportCRUDOperationUtil.removeEmergencyPackages(existingEmergencyPkgData, session);
                    }
                }
            }
        }
        //CHECK FOR DUPLICATE NAME

        List<EmergencyPkgDataExt> emergencyPkgs = (List<EmergencyPkgDataExt>) ImportExportCRUDOperationUtil.getPackageByName(emergencyPkgDataImported.getClass(), emergencyPkgDataImported.getName(), session);
        if (Collectionz.isNullOrEmpty(emergencyPkgs) == false) {
            for (EmergencyPkgDataExt existingEmergencyPkgData : emergencyPkgs) {
                if (CommonConstants.STATUS_DELETED.equalsIgnoreCase(existingEmergencyPkgData.getStatus())) {
                    ImportExportCRUDOperationUtil.removeEmergencyPackages(existingEmergencyPkgData, session);
                } else {
                    if (existingEmergencyPkgData.getType().equalsIgnoreCase(emergencyPkgDataImported.getType()) == false) {
                        subReasons.add("Existing Emergency Package " + BasicValidations.printIdAndName(existingEmergencyPkgData.getId(), existingEmergencyPkgData.getName()) + " is of type " + existingEmergencyPkgData.getName()
                                + " and Imported Emergency package " + BasicValidations.printIdAndName(emergencyPkgDataImported.getId(), emergencyPkgDataImported.getName()) + " is of type " + emergencyPkgDataImported.getType());
                        return;
                    }
                    if ((emergencyPkgDataImported.getPackageMode().equalsIgnoreCase(PkgMode.DESIGN.name()) || emergencyPkgDataImported.getPackageMode().equalsIgnoreCase(PkgMode.TEST.name()))
                            && (existingEmergencyPkgData.getPackageMode().equalsIgnoreCase(PkgMode.LIVE.name()) || existingEmergencyPkgData.getPackageMode().equalsIgnoreCase(PkgMode.LIVE2.name()))) {
                        subReasons.add("Existing Emergency Package " + BasicValidations.printIdAndName(existingEmergencyPkgData.getId(), existingEmergencyPkgData.getName()) + " is of mode " + existingEmergencyPkgData.getPackageMode()
                                + " and Imported Emergency package " + BasicValidations.printIdAndName(emergencyPkgDataImported.getId(), emergencyPkgDataImported.getName()) + " is of mode " + emergencyPkgDataImported.getPackageMode());
                        return;
                    }
                    if (CommonConstants.FAIL.equalsIgnoreCase(action)) {
                        subReasons.add("Emergency Package Name already exists: " + emergencyPkgDataImported.getName());
                    } else if (CommonConstants.REPLACE.equalsIgnoreCase(action)) {
                        if (Strings.isNullOrBlank(emergencyPkgDataImported.getId()) == false && emergencyPkgDataImported.getId().equalsIgnoreCase(existingEmergencyPkgData.getId()) == false) {
                            subReasons.add("Imported Emergency Package with " + BasicValidations.printIdAndName(emergencyPkgDataImported.getId(), emergencyPkgDataImported.getName()) + " conflicts with Existing Emergency Package " + BasicValidations.printIdAndName(existingEmergencyPkgData.getId(), existingEmergencyPkgData.getName()));
                            return;
                        }
                        validatePackageMode(emergencyPkgDataImported, existingEmergencyPkgData, subReasons);
                        setPkgGroupOrder(emergencyPkgDataImported,existingEmergencyPkgData.getId(),session);
                        ImportExportCRUDOperationUtil.removeEmergencyPackages(existingEmergencyPkgData, session);
                    }
                }

            }
        }

    }
    private String getMessage(EmergencyPkgDataExt emergencyPkgDataImported, String module, String name) {
        StringBuilder sb = new StringBuilder();
        sb.append(module);
        sb.append(" with name: ");
        sb.append(name);
        sb.append(" already exists in ");
        sb.append(Discriminators.EMERGENCY_PACKAGE);
        sb.append(BasicValidations.printIdAndName(emergencyPkgDataImported.getId(), emergencyPkgDataImported.getName()));
        return sb.toString();
    }

    private void setPkgGroupOrder(EmergencyPkgDataExt importedPkg,String existingPkgId,SessionProvider sessionProvider) throws Exception {
        List<PkgGroupOrderData> pkgGroupOrderDatas = PackageImportValidator.getPkgGroupOrderDatas(importedPkg.getGroups(), importedPkg.getType(), existingPkgId, sessionProvider);
        importedPkg.getPkgGroupWiseOrders().clear();
        importedPkg.getPkgGroupWiseOrders().addAll(pkgGroupOrderDatas);
    }
}