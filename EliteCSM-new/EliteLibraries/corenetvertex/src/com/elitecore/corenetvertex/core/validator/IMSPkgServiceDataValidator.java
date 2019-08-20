package com.elitecore.corenetvertex.core.validator;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.Discriminators;
import com.elitecore.corenetvertex.constants.IMSServiceAction;
import com.elitecore.corenetvertex.pkg.ims.IMSPkgData;
import com.elitecore.corenetvertex.pkg.ims.IMSPkgServiceData;
import com.elitecore.corenetvertex.util.SessionProvider;
import com.elitecore.corenetvertex.util.commons.ImportExportCRUDOperationUtil;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * A validator class that validated Service Data associated with IMS Package
 */
public class IMSPkgServiceDataValidator implements Validator<IMSPkgServiceData, IMSPkgData, IMSPkgData> {

    private static final String MODULE = "IMS-PKG-SERVICE-VALIDATOR";

    @Override
    public List<String> validate(IMSPkgServiceData imsPkgServiceDataImported, final IMSPkgData imsPkgData, IMSPkgData superObject, String action, SessionProvider session) {
        List<String> subReasons = new ArrayList<String>();
        try {

            String id = imsPkgServiceDataImported.getId();
            String name = imsPkgServiceDataImported.getName();

            if (Strings.isNullOrBlank(id) && Strings.isNullOrBlank(name)) {
                subReasons.add("IMS Package Service Data Id or Name is mandatory");
                return subReasons;
            }

            if(imsPkgServiceDataImported.getMediaTypeData() == null){
                subReasons.add("Media Type must be configured with IMS Package Service Data " + BasicValidations.printIdAndName(imsPkgServiceDataImported.getId(),imsPkgServiceDataImported.getName()));
            }

            validateIMSPkgServiceDataName(imsPkgServiceDataImported, imsPkgData, session, action, subReasons);
            if(Collectionz.isNullOrEmpty(subReasons) == false){
                return subReasons;
            }
            setDefaultValueForMandatoryParameters(imsPkgServiceDataImported, subReasons);
            imsPkgServiceDataImported.setCreatedByStaff(imsPkgData.getCreatedByStaff());
            imsPkgServiceDataImported.setCreatedDate(new Timestamp(System.currentTimeMillis()));
            imsPkgServiceDataImported.setModifiedByStaff(imsPkgServiceDataImported.getCreatedByStaff());
            imsPkgServiceDataImported.setModifiedDate(imsPkgServiceDataImported.getCreatedDate());
            IMSPkgServiceData existingImsServiceData = null;
            if (Strings.isNullOrBlank(id) == false) {
                existingImsServiceData = ImportExportCRUDOperationUtil.get(IMSPkgServiceData.class, id, session);
                if (existingImsServiceData != null){
                    if(CommonConstants.STATUS_DELETED.equalsIgnoreCase(existingImsServiceData.getStatus())){
                        ImportExportCRUDOperationUtil.deleteIMSServiceData(existingImsServiceData.getId(), session);
                    } else {
                        if (Strings.isNullOrBlank(imsPkgData.getId()) == false
                                && imsPkgData.getId().equalsIgnoreCase(existingImsServiceData.getImsPkgData().getId()) == false
                                && CommonConstants.REPLACE.equalsIgnoreCase(action)) {
                            subReasons.add(Discriminators.IMS_PKG_SERVICE + " " + BasicValidations.printIdAndName(imsPkgServiceDataImported.getId(), imsPkgServiceDataImported.getName()) + " already exists in different ims package " + BasicValidations.printIdAndName(existingImsServiceData.getImsPkgData().getId(), existingImsServiceData.getImsPkgData().getName()));
                            return subReasons;
                        }
                    }
                }
            } else if((Strings.isNullOrBlank(name) == false)) {
                List<IMSPkgServiceData> imsPkgServiceList = ImportExportCRUDOperationUtil.getNameBasedOnParentId(IMSPkgServiceData.class, name, imsPkgData.getId(), "imsPkgData.id", session);
                if (Collectionz.isNullOrEmpty(imsPkgServiceList) == false) {
                    existingImsServiceData = imsPkgServiceList.get(0);
                }
            }
            if (existingImsServiceData != null) {
                if (CommonConstants.STATUS_DELETED.equalsIgnoreCase(existingImsServiceData.getStatus()) == false) {
                    validateImsPkgServiceDataBasedOnAction(imsPkgServiceDataImported, existingImsServiceData, imsPkgData, action, subReasons);
                }
            }

        } catch (Exception e) {
            LogManager.getLogger().error(MODULE, "Failed to validate IMS Package Service Data " + BasicValidations.printIdAndName(imsPkgServiceDataImported.getId(), imsPkgServiceDataImported.getName()) + " associated with ims package " + BasicValidations.printIdAndName(imsPkgData.getId(), imsPkgData.getName()));
            LogManager.getLogger().trace(MODULE, e);
            subReasons.add("Failed to validate IMS Package Service Data " + BasicValidations.printIdAndName(imsPkgServiceDataImported.getId(), imsPkgServiceDataImported.getName()) + " associated with IMS Package " + BasicValidations.printIdAndName(imsPkgData.getId(), imsPkgData.getName()) + ". Kindly refer logs for further details");

        }
        return subReasons;
    }

    private void setDefaultValueForMandatoryParameters(IMSPkgServiceData imsPkgServiceDataImported, List<String> subReasons) {
        IMSServiceAction serviceAction = imsPkgServiceDataImported.getAction();
        if(serviceAction == null){
            if(LogManager.getLogger().isDebugLogLevel()){
                LogManager.getLogger().debug(MODULE,"Invalid Action is configured with IMS Package Service Data " + BasicValidations.printIdAndName(imsPkgServiceDataImported.getId(),imsPkgServiceDataImported.getName()) +" so taking default value as REJECT(1)" );
            }
            imsPkgServiceDataImported.setAction(IMSServiceAction.REJECT);
        }
    }

    private void validateImsPkgServiceDataBasedOnAction(IMSPkgServiceData imsPkgServiceDataImported, IMSPkgServiceData existingImsPkgServiceData, IMSPkgData imsPkgData, String action, List<String> subReasons) {
        if (CommonConstants.FAIL.equalsIgnoreCase(action) && imsPkgData.getName().equals(existingImsPkgServiceData.getImsPkgData().getName())) {
            subReasons.add(Discriminators.IMS_PKG_SERVICE + " " + BasicValidations.printIdAndName(imsPkgServiceDataImported.getId(), imsPkgServiceDataImported.getName()) + " already exists in IMS Package " + BasicValidations.printIdAndName(imsPkgData.getId(), imsPkgData.getName()));
        } else if (CommonConstants.REPLACE.equalsIgnoreCase(action)) {
            imsPkgServiceDataImported.setId(existingImsPkgServiceData.getId());
        }
    }

    private void validateIMSPkgServiceDataName(IMSPkgServiceData imsPkgServiceDataImported, IMSPkgData pkgData, SessionProvider session, String action, List<String> subReasons) throws Exception {
        BasicValidations.validateName(imsPkgServiceDataImported.getName(),Discriminators.IMS_PKG_SERVICE, subReasons);
    }
}
