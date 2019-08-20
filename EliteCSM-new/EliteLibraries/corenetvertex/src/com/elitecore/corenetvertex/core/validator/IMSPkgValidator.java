package com.elitecore.corenetvertex.core.validator;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Predicate;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pkg.ims.IMSPkgData;
import com.elitecore.corenetvertex.pkg.ims.IMSPkgServiceData;
import com.elitecore.corenetvertex.util.SessionProvider;
import com.elitecore.corenetvertex.util.commons.ImportExportCRUDOperationUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * A validator class that validates IMS Package fields
 */
public class IMSPkgValidator implements Validator<IMSPkgData, IMSPkgData, IMSPkgData> {


    private static final String MODULE = "IMS-PKG-VALIDATOR";


    @Override
    public List<String> validate(IMSPkgData imsPkgDataImported, IMSPkgData imsPkgData, IMSPkgData superObject, String action, SessionProvider session) {
        List<String> subReasons = new ArrayList<String>();
        try {

            String name = imsPkgDataImported.getName();

            BasicValidations.validateName(name,"IMS Package",subReasons);
            if(Collectionz.isNullOrEmpty(subReasons) == false){
                return subReasons;
            }

            validateParameters(imsPkgDataImported,subReasons);

            validateUniquenessOfServiceNameWithinImsPackage(imsPkgDataImported, subReasons);

            if(Collectionz.isNullOrEmpty(subReasons) == false){
                return subReasons;
            }
            //CHECK FOR PACKAGE ID OR NAME EXISTS OR NOT
            validateWithExistingIMSPackage(imsPkgDataImported, action, session, subReasons);

        } catch (Exception e) {
            LogManager.getLogger().error(MODULE, "Failed to validate ims package with "+BasicValidations.printIdAndName(imsPkgDataImported.getId(), imsPkgDataImported.getName()));
            LogManager.getLogger().trace(e);
            subReasons.add("Failed to validate ims package with "+BasicValidations.printIdAndName(imsPkgDataImported.getId(), imsPkgDataImported.getName())+" . Kindly refer logs for further details");

        }


        return subReasons;
    }

    private void validateUniquenessOfServiceNameWithinImsPackage(IMSPkgData imsPkgDataImported, List<String> subReasons) {
        List<IMSPkgServiceData> imsPkgServiceDatas = imsPkgDataImported.getImsPkgServiceDatas();
        if(Collectionz.isNullOrEmpty(imsPkgServiceDatas) == false){
            if(imsPkgServiceDatas.size() == 1){
                return;
            }
            for(int i=0;i<imsPkgServiceDatas.size();i++){
                for(int j=i+1;j<imsPkgServiceDatas.size();j++){
                    if(imsPkgServiceDatas.get(i).getName().equalsIgnoreCase(imsPkgServiceDatas.get(j).getName())){
                        subReasons.add("IMS Package Service Data with name: " + imsPkgServiceDatas.get(i).getName() + " already exists in IMS Package " + BasicValidations.printIdAndName(imsPkgDataImported.getId(), imsPkgDataImported.getName()));
                        return;
                    }
                }
            }
        }
    }

    private void validateParameters(IMSPkgData imsPkgDataImported, List<String> subReasons) {

        //validate mode
        String mode = imsPkgDataImported.getPackageMode();
        if(Strings.isNullOrBlank(mode)){
            if(LogManager.getLogger().isDebugLogLevel()){
                LogManager.getLogger().debug(MODULE,"Package Mode is not configured so taking default mode as "+ PkgMode.DESIGN +" for ims package "+BasicValidations.printIdAndName(imsPkgDataImported.getId(), imsPkgDataImported.getName()));
            }
            imsPkgDataImported.setPackageMode(PkgMode.DESIGN.name());
        }else if(PkgMode.getMode(mode) == null){
            if(LogManager.getLogger().isDebugLogLevel()){
                LogManager.getLogger().debug(MODULE,"Invalid package mode: "+mode +" is configured so taking default mode as "+PkgMode.DESIGN +" for ims package "+BasicValidations.printIdAndName(imsPkgDataImported.getId(), imsPkgDataImported.getName()));
            }
            imsPkgDataImported.setPackageMode(PkgMode.DESIGN.name());
        }else{
            imsPkgDataImported.setPackageMode(imsPkgDataImported.getPackageMode().toUpperCase());
        }

        //validate Status
        String pkgStatus =  imsPkgDataImported.getStatus();
        if(Strings.isNullOrBlank(pkgStatus)){
            if(LogManager.getLogger().isDebugLogLevel()){
                LogManager.getLogger().debug(MODULE,"Status is not configured so taking default status as: "+ PkgStatus.ACTIVE.name() +" for ims package "+BasicValidations.printIdAndName(imsPkgDataImported.getId(), imsPkgDataImported.getName()));
            }
            imsPkgDataImported.setStatus(PkgStatus.ACTIVE.name());
        }else if(pkgStatus.equals(PkgStatus.ACTIVE.name()) == false && pkgStatus.equals(PkgStatus.INACTIVE.name()) == false && pkgStatus.equals(PkgStatus.RETIRED.name()) == false){
            if(LogManager.getLogger().isDebugLogLevel()){
                LogManager.getLogger().debug(MODULE,"Invalid Status is configured so taking default status as: "+ PkgStatus.ACTIVE.name() +" for ims package "+BasicValidations.printIdAndName(imsPkgDataImported.getId(), imsPkgDataImported.getName()));
            }
            imsPkgDataImported.setStatus(PkgStatus.ACTIVE.name());
        }

    }

    private void validateWithExistingIMSPackage(IMSPkgData imsPkgDataImported, String action, SessionProvider session, List<String> subReasons) throws Exception {
        if (Strings.isNullOrBlank(imsPkgDataImported.getId()) == false) {

            IMSPkgData existingIMSPkgData = ImportExportCRUDOperationUtil.get(imsPkgDataImported.getClass(), imsPkgDataImported.getId(), session);
            if (existingIMSPkgData != null) {
                if (CommonConstants.STATUS_DELETED.equalsIgnoreCase(existingIMSPkgData.getStatus())) {
                    ImportExportCRUDOperationUtil.removeIMSPackages(existingIMSPkgData, session);
                } else {
                    if((imsPkgDataImported.getPackageMode().equalsIgnoreCase(PkgMode.DESIGN.name()) || imsPkgDataImported.getPackageMode().equalsIgnoreCase(PkgMode.TEST.name()))
                            && (existingIMSPkgData.getPackageMode().equalsIgnoreCase(PkgMode.LIVE.name()) || existingIMSPkgData.getPackageMode().equalsIgnoreCase(PkgMode.LIVE2.name()))){
                        subReasons.add("Existing IMS Package " + BasicValidations.printIdAndName(existingIMSPkgData.getId(),existingIMSPkgData.getName()) +" is of mode " +existingIMSPkgData.getPackageMode()
                                + " and Imported IMS Package " + BasicValidations.printIdAndName(imsPkgDataImported.getId(), imsPkgDataImported.getName()) + " is of mode " + imsPkgDataImported.getPackageMode());
                        return;
                    }
                    if (CommonConstants.FAIL.equalsIgnoreCase(action)) {
                        subReasons.add("IMS Package Id already exists: " + imsPkgDataImported.getId() + " for ims package name: " + imsPkgDataImported.getName());
                    } else if (CommonConstants.REPLACE.equalsIgnoreCase(action)) {
                        validatePackageMode(imsPkgDataImported, existingIMSPkgData, subReasons);
                        ImportExportCRUDOperationUtil.removeIMSPackages(existingIMSPkgData, session);
                    }

                }

            }
        }
        //CHECK FOR DUPLICATE NAME

        List<IMSPkgData> pkgs = (List<IMSPkgData>) ImportExportCRUDOperationUtil.getPackageByName(imsPkgDataImported.getClass(), imsPkgDataImported.getName(), session);
        if (Collectionz.isNullOrEmpty(pkgs) == false) {
            for (IMSPkgData existingPkgData : pkgs) {
                if (CommonConstants.STATUS_DELETED.equalsIgnoreCase(existingPkgData.getStatus())) {
                    ImportExportCRUDOperationUtil.removeIMSPackages(existingPkgData, session);
                } else {
                    if ((imsPkgDataImported.getPackageMode().equalsIgnoreCase(PkgMode.DESIGN.name()) || imsPkgDataImported.getPackageMode().equalsIgnoreCase(PkgMode.TEST.name()))
                            && (existingPkgData.getPackageMode().equalsIgnoreCase(PkgMode.LIVE.name()) || existingPkgData.getPackageMode().equalsIgnoreCase(PkgMode.LIVE2.name()))) {
                        subReasons.add("Existing IMS Package " + BasicValidations.printIdAndName(existingPkgData.getId(), existingPkgData.getName()) + " is of mode " + existingPkgData.getPackageMode()
                                + " and Imported IMS Package " + BasicValidations.printIdAndName(imsPkgDataImported.getId(), imsPkgDataImported.getName()) + " is of mode " + imsPkgDataImported.getPackageMode());
                        return;
                    }
                    if (CommonConstants.FAIL.equalsIgnoreCase(action)) {
                        subReasons.add("IMS Package Name already exists: " + imsPkgDataImported.getName());
                    } else if (CommonConstants.REPLACE.equalsIgnoreCase(action)) {
                        validatePackageMode(imsPkgDataImported, existingPkgData, subReasons);
                        ImportExportCRUDOperationUtil.removeIMSPackages(existingPkgData, session);
                        if (Strings.isNullOrBlank(imsPkgDataImported.getId()) == false && imsPkgDataImported.getId().equalsIgnoreCase(existingPkgData.getId()) == false) {
                            subReasons.add("Imported IMS Package with " + BasicValidations.printIdAndName(imsPkgDataImported.getId(), imsPkgDataImported.getName()) + " conflicts with Existing IMS Package " + BasicValidations.printIdAndName(existingPkgData.getId(), existingPkgData.getName()));
                        }
                    }
                }

            }
        }

    }
    private void validatePackageMode(IMSPkgData imsPkgDataImported,IMSPkgData existingImsPkgData, List<String> subReasons) {
        PkgMode pkgMode = PkgMode.getMode(imsPkgDataImported.getPackageMode());
        if(PkgMode.LIVE == pkgMode || PkgMode.LIVE2 == pkgMode){
            subReasons.add("IMS Package with mode " + imsPkgDataImported.getPackageMode() + " can not be replaced");
        }
    }
}