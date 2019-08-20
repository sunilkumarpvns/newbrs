package com.elitecore.corenetvertex.core.validator;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.Discriminators;
import com.elitecore.corenetvertex.pkg.EmergencyPkgDataExt;
import com.elitecore.corenetvertex.pkg.qos.QosProfileData;
import com.elitecore.corenetvertex.pkg.qos.QosProfileDetailData;
import com.elitecore.corenetvertex.util.SessionProvider;
import com.elitecore.corenetvertex.util.commons.ImportExportCRUDOperationUtil;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ishani on 30/11/16.
 */
public class QosProfileEmergencyPackageValidator implements Validator<QosProfileData,EmergencyPkgDataExt,EmergencyPkgDataExt>{
    private static final String MODULE = "EMERGENCY-QOS-PROFILE-VALIDATOR";
    @Override
    public List<String> validate(QosProfileData qosProfileImported, EmergencyPkgDataExt emergencyPkgData, EmergencyPkgDataExt superObject, String action, SessionProvider session) {

        List<String> subReasons = new ArrayList<String>();
        try {

            String id = qosProfileImported.getId();
            String name = qosProfileImported.getName();

            if(Strings.isNullOrBlank(id) && Strings.isNullOrBlank(name)){
                subReasons.add("Qos Profile Id or Name is mandatory");
            }

            validateQosProfileName(qosProfileImported, emergencyPkgData, session, action, subReasons);
            qosProfileImported.setCreatedByStaff(emergencyPkgData.getCreatedByStaff());
            qosProfileImported.setCreatedDate(new Timestamp(System.currentTimeMillis()));
            qosProfileImported.setModifiedByStaff(qosProfileImported.getCreatedByStaff());
            qosProfileImported.setModifiedDate(qosProfileImported.getCreatedDate());
            QosProfileData existingQosProfile = null;
            if (Strings.isNullOrBlank(id) == false) {
                existingQosProfile = ImportExportCRUDOperationUtil.get(QosProfileData.class, id, session);
                if (existingQosProfile != null){
                    if(CommonConstants.STATUS_DELETED.equalsIgnoreCase(existingQosProfile.getStatus())){
                        ImportExportCRUDOperationUtil.deleteQosProfile(existingQosProfile.getId(), session);
                    } else {
                        if (Strings.isNullOrBlank(emergencyPkgData.getId()) == false
                                && emergencyPkgData.getId().equalsIgnoreCase(existingQosProfile.getPkgData().getId()) == false
                                && CommonConstants.REPLACE.equalsIgnoreCase(action)) {
                            subReasons.add(Discriminators.QOS_PROFILE + " " + BasicValidations.printIdAndName(qosProfileImported.getId(), qosProfileImported.getName()) + " already exists in different emergency package " + BasicValidations.printIdAndName(existingQosProfile.getPkgData().getId(), existingQosProfile.getPkgData().getName()));
                            return subReasons;
                        }
                    }
                }

            } else {
                List<QosProfileData> qosProfileList = ImportExportCRUDOperationUtil.getNameBasedOnParentId(QosProfileData.class, name, emergencyPkgData.getId(), "pkgData.id", session);
                if (Collectionz.isNullOrEmpty(qosProfileList) == false) {
                    existingQosProfile = qosProfileList.get(0);
                }
            }

            if(existingQosProfile != null){
                if (CommonConstants.STATUS_DELETED.equalsIgnoreCase(existingQosProfile.getStatus()) == false) {
                    validateQosProfileBasedOnAction(qosProfileImported, existingQosProfile, emergencyPkgData, action, subReasons);
                }
            }
            if(Collectionz.isNullOrEmpty(qosProfileImported.getQosProfileDetailDataList()) == false && qosProfileImported.getQosProfileDetailDataList().size() > 1){
                subReasons.add("Only HSQ Level can be configured with Qos Profile " + BasicValidations.printIdAndName(id,name));
            }

            //Validations on HSQ
            List<QosProfileDetailData> qosProfileDetails = qosProfileImported.getQosProfileDetailDataList();
            boolean isHSQPresent = false;
            if(Collectionz.isNullOrEmpty(qosProfileDetails) == false) {
                for (QosProfileDetailData qosProfileDetail : qosProfileDetails) {
                    if(qosProfileDetail.getFupLevel() == null || (qosProfileDetail.getFupLevel() != 0 )){
                        subReasons.add("Invalid FUP Level: "+qosProfileDetail.getFupLevel() +" is configured with QoS Profile Detail id: " + id + " associated with QoS Profile " + BasicValidations.printIdAndName(id,name));
                    }else {
                        if (qosProfileDetail.getFupLevel() == 0) {
                            isHSQPresent = true;
                        }
                    }
                }
                if(isHSQPresent == false ){
                    subReasons.add("No Level is configured with Qos Profile "+ BasicValidations.printIdAndName(id,name) +" associated with Package " + BasicValidations.printIdAndName(emergencyPkgData.getId(), emergencyPkgData.getName()));
                }
            }
        }catch (Exception e) {
            LogManager.getLogger().error(MODULE, "Failed to validate qos profile "+ BasicValidations.printIdAndName(qosProfileImported.getId(), qosProfileImported.getName()) +" associated with package " + BasicValidations.printIdAndName(emergencyPkgData.getId(), emergencyPkgData.getName()));
            LogManager.getLogger().trace(MODULE,e);
            subReasons.add("Failed to validate Qos Profile  "+ BasicValidations.printIdAndName(qosProfileImported.getId(),qosProfileImported.getName()) +" associated with Package " + BasicValidations.printIdAndName(emergencyPkgData.getId(), emergencyPkgData.getName()) +". Kindly refer logs for further details");

        }
        return subReasons;
    }

    private void validateQosProfileBasedOnAction(QosProfileData qosProfileImported, QosProfileData existingQosProfile,EmergencyPkgDataExt emergencyPkgData, String action, List<String> subReasons){
        if(CommonConstants.FAIL.equalsIgnoreCase(action) && emergencyPkgData.getName().equalsIgnoreCase(existingQosProfile.getPkgData().getName())){
            subReasons.add("Qos Profile "+ BasicValidations.printIdAndName(qosProfileImported.getId(),qosProfileImported.getName()) +" already exists in Package " + BasicValidations.printIdAndName(emergencyPkgData.getId(), emergencyPkgData.getName()));
        }else if(CommonConstants.REPLACE.equalsIgnoreCase(action)) {
           qosProfileImported.setId(existingQosProfile.getId());
        }
    }

    private void validateQosProfileName(QosProfileData qosProfileImported, EmergencyPkgDataExt emergencyPkgData, SessionProvider session, String action, List<String> subReasons) throws Exception {
        BasicValidations.validateName(qosProfileImported.getName(),"Qos Profile",subReasons);
    }

}

