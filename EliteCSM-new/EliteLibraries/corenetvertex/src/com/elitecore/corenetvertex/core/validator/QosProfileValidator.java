package com.elitecore.corenetvertex.core.validator;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.Discriminators;
import com.elitecore.corenetvertex.constants.QuotaProfileType;
import com.elitecore.corenetvertex.pkg.PkgData;
import com.elitecore.corenetvertex.pkg.qos.QosProfileData;
import com.elitecore.corenetvertex.pkg.qos.QosProfileDetailData;
import com.elitecore.corenetvertex.pkg.quota.QuotaProfileData;
import com.elitecore.corenetvertex.pkg.syquota.SyQuotaProfileData;
import com.elitecore.corenetvertex.util.SessionProvider;
import com.elitecore.corenetvertex.util.commons.ImportExportCRUDOperationUtil;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class QosProfileValidator implements Validator<QosProfileData, PkgData, PkgData> {

    private static final String MODULE = "QOS-PROFILE-VALIDATOR";

    @Override
    public List<String> validate(QosProfileData qosProfileImported, PkgData pkgData, PkgData superObject, String action, SessionProvider session) {
        List<String> subReasons = new ArrayList<String>();
        try {

            String id = qosProfileImported.getId();
            String name = qosProfileImported.getName();

            if(Strings.isNullOrBlank(id) && Strings.isNullOrBlank(name)){
                subReasons.add("Qos Profile Id or Name is mandatory");
            }

            validateQosProfileName(qosProfileImported, pkgData, session, action, subReasons);
            qosProfileImported.setCreatedByStaff(pkgData.getCreatedByStaff());
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
                        if (Strings.isNullOrBlank(pkgData.getId()) == false
                                && pkgData.getId().equalsIgnoreCase(existingQosProfile.getPkgData().getId()) == false
                                && CommonConstants.REPLACE.equalsIgnoreCase(action)) {
                            subReasons.add(Discriminators.QOS_PROFILE + " " + BasicValidations.printIdAndName(qosProfileImported.getId(), qosProfileImported.getName()) + " already exists in different package " + BasicValidations.printIdAndName(existingQosProfile.getPkgData().getId(), existingQosProfile.getPkgData().getName()));
                            return subReasons;
                        }
                    }
                }

            } else {
                List<QosProfileData> qosProfileList = ImportExportCRUDOperationUtil.getNameBasedOnParentId(QosProfileData.class, name, pkgData.getId(), "pkgData.id", session);
                if (Collectionz.isNullOrEmpty(qosProfileList) == false) {
                    existingQosProfile = qosProfileList.get(0);
                }
            }

            if(existingQosProfile != null){
                if (CommonConstants.STATUS_DELETED.equalsIgnoreCase(existingQosProfile.getStatus()) == false) {
                    validateQosProfileBasedOnAction(qosProfileImported, existingQosProfile, pkgData, action, subReasons);
                }
            }
            validateQuotaProfileIdAndNameForQosProfile(qosProfileImported,pkgData,subReasons,session);

            validateRnCQuotaProfileAndRateConfiguration(qosProfileImported,pkgData,subReasons);



            if(Collectionz.isNullOrEmpty(qosProfileImported.getQosProfileDetailDataList()) == false && qosProfileImported.getQosProfileDetailDataList().size() > 3){
                subReasons.add("Maximum 3 levels(HSQ,FUP1,FUP2) can be configured with Qos Profile " + BasicValidations.printIdAndName(id,name));
            }
            //Validations on HSQ , FUP1 and FUP2. Can not configure FUP1 or FUP2 without configuring HSQ
            List<QosProfileDetailData> qosProfileDetails = qosProfileImported.getQosProfileDetailDataList();
            boolean isHSQPresent = false;
            boolean isFUP1Present = false;
            boolean isFUP2Present = false;
            if(Collectionz.isNullOrEmpty(qosProfileDetails) == false) {
                for (QosProfileDetailData qosProfileDetail : qosProfileDetails) {
                    if(qosProfileDetail.getFupLevel() == null || (qosProfileDetail.getFupLevel() != 0 && qosProfileDetail.getFupLevel() != 1 && qosProfileDetail.getFupLevel() != 2)){
                        subReasons.add("Invalid FUP Level: "+qosProfileDetail.getFupLevel() +" is configured with QoS Profile Detail id: " + id + " associated with QoS Profile " + BasicValidations.printIdAndName(id,name));
                    }else {
                        if (qosProfileDetail.getFupLevel() == 0) {
                            isHSQPresent = true;
                        } else if (qosProfileDetail.getFupLevel() == 1) {
                            isFUP1Present = true;
                        } else if (qosProfileDetail.getFupLevel() == 2) {
                            isFUP2Present = true;
                        }
                    }
                }
                if(isHSQPresent == false && isFUP1Present == false && isFUP2Present == false){
                    subReasons.add("No Level is configured with Qos Profile "+ BasicValidations.printIdAndName(id,name) +" associated with Package " + BasicValidations.printIdAndName(pkgData.getId(), pkgData.getName()));
                }else {
                    if (isHSQPresent == false) {
                        subReasons.add("HSQ Level must be configured Qos Profile "+ BasicValidations.printIdAndName(id,name) +" associated with Package " + BasicValidations.printIdAndName(pkgData.getId(), pkgData.getName()));
                    } else if (isFUP1Present == false && isFUP2Present == true) {
                        subReasons.add("FUP1 Level must be configured Qos Profile "+ BasicValidations.printIdAndName(id,name) +" associated with Package " + BasicValidations.printIdAndName(pkgData.getId(), pkgData.getName()) +" in presence of FUP2");
                    }
                }
            }
        }catch (Exception e) {
            LogManager.getLogger().error(MODULE, "Failed to validate qos profile "+ BasicValidations.printIdAndName(qosProfileImported.getId(), qosProfileImported.getName()) +" associated with package " + BasicValidations.printIdAndName(pkgData.getId(), pkgData.getName()));
            LogManager.getLogger().trace(MODULE,e);
            subReasons.add("Failed to validate Qos Profile  "+ BasicValidations.printIdAndName(qosProfileImported.getId(),qosProfileImported.getName()) +" associated with Package " + BasicValidations.printIdAndName(pkgData.getId(), pkgData.getName()) +". Kindly refer logs for further details");

        }
        return subReasons;
    }

    private void validateRnCQuotaProfileAndRateConfiguration(QosProfileData qosProfileImported, PkgData pkgData, List<String> subReasons) {
        if(QuotaProfileType.RnC_BASED != pkgData.getQuotaProfileType()){
            return;
        }
        boolean rateCardExist = isRateCardExist(qosProfileImported);
        if(isRnCQuotaProfileExist(qosProfileImported) && rateCardExist){
            subReasons.add("Either of RnC Quota Profile or Rate Card can be configured with PCC Profile");
            return;
        }
        if(isRnCQuotaProfileExist(qosProfileImported) == false && rateCardExist == false){
            subReasons.add("Either of RnC Quota Profile or Rate Card must be configured with PCC Profile");
            return;
        }
        if(rateCardExist && Collectionz.isNullOrEmpty(qosProfileImported.getQosProfileDetailDataList()) == false){
            if(qosProfileImported.getQosProfileDetailDataList().size() > 1){
                subReasons.add("Only HSQ level can be configured with PCC Profile when Rate Card is configured");
                return;
            }
            if(qosProfileImported.getQosProfileDetailDataList().get(0).getFupLevel() != 0){
                subReasons.add("Only HSQ level can be configured with PCC Profile when Rate Card is configured");
                return;
            }

        }
    }

    private boolean isRateCardExist(QosProfileData qosProfileImported) {
        return Strings.isNullOrBlank(qosProfileImported.getRateCardId()) == false || Strings.isNullOrBlank(qosProfileImported.getRateCardName()) == false;
    }

    private boolean isRnCQuotaProfileExist(QosProfileData qosProfileImported) {
        return Strings.isNullOrBlank(qosProfileImported.getQuotaProfileId()) == false || Strings.isNullOrBlank(qosProfileImported.getQuotaProfileName()) == false;
    }

    private void validateQuotaProfileIdAndNameForQosProfile(QosProfileData qosProfileImported, PkgData pkgData, List<String> subReasons, SessionProvider session) throws Exception {
        String quotaProfileId = qosProfileImported.getQuotaProfileId();
        String quotaProfileName = qosProfileImported.getQuotaProfileName();
        boolean isQuotaProfileExists = false;
        if(QuotaProfileType.USAGE_METERING_BASED == pkgData.getQuotaProfileType()) {
            List<QuotaProfileData> quotaProfilesImported = pkgData.getQuotaProfiles();
            if (Strings.isNullOrBlank(quotaProfileId) == false) {
                for (QuotaProfileData quotaProfile : quotaProfilesImported) {
                    if (Strings.isNullOrBlank(quotaProfile.getId()) == false && quotaProfileId.equals(quotaProfile.getId())) {
                        isQuotaProfileExists = true;
                        break;
                    }
                }
                if (isQuotaProfileExists == false) {
                    subReasons.add("Quota Profile" + BasicValidations.printIdAndName(quotaProfileId, quotaProfileName) + " associated with Qos Profile " + BasicValidations.printIdAndName(qosProfileImported.getId(), qosProfileImported.getName()) + " does not exists");
                }
            } else if (Strings.isNullOrBlank(quotaProfileName) == false) {
                for (QuotaProfileData quotaProfile : quotaProfilesImported) {
                    if (Strings.isNullOrBlank(quotaProfile.getName()) == false && quotaProfileName.equals(quotaProfile.getName())) {
                        isQuotaProfileExists = true;
                        break;
                    }
                }
                if (isQuotaProfileExists == false) {
                    subReasons.add("Quota Profile" + BasicValidations.printIdAndName(quotaProfileId, quotaProfileName) + " associated with Qos Profile " + BasicValidations.printIdAndName(qosProfileImported.getId(), qosProfileImported.getName()) + " does not exists");
                }
            }
        }else if(QuotaProfileType.SY_COUNTER_BASED == pkgData.getQuotaProfileType()){
            List<SyQuotaProfileData> quotaProfilesImported = pkgData.getSyQuotaProfileDatas();
            if (Strings.isNullOrBlank(quotaProfileId) == false) {
                for (SyQuotaProfileData quotaProfile : quotaProfilesImported) {
                    if (Strings.isNullOrBlank(quotaProfile.getId()) == false && quotaProfileId.equals(quotaProfile.getId())) {
                        isQuotaProfileExists = true;
                        break;
                    }
                }
                if (isQuotaProfileExists == false) {
                    subReasons.add("Quota Profile" + BasicValidations.printIdAndName(quotaProfileId, quotaProfileName) + " associated with Qos Profile " + BasicValidations.printIdAndName(qosProfileImported.getId(), qosProfileImported.getName()) + " does not exists");
                }
            }else if(Strings.isNullOrBlank(quotaProfileName) ==false){
                for (SyQuotaProfileData quotaProfile : quotaProfilesImported) {
                    if (Strings.isNullOrBlank(quotaProfile.getName()) == false && quotaProfileName.equals(quotaProfile.getName())) {
                        isQuotaProfileExists = true;
                        break;
                    }
                }
                if (isQuotaProfileExists == false) {
                    subReasons.add("Quota Profile" + BasicValidations.printIdAndName(quotaProfileId, quotaProfileName) + " associated with Qos Profile " + BasicValidations.printIdAndName(qosProfileImported.getId(), qosProfileImported.getName()) + " does not exists");
                }
            }
        }
    }

    private void validateQosProfileBasedOnAction(QosProfileData qosProfileImported, QosProfileData existingQosProfile,PkgData pkgData, String action, List<String> subReasons){
        if(CommonConstants.FAIL.equalsIgnoreCase(action) && pkgData.getName().equalsIgnoreCase(existingQosProfile.getPkgData().getName())){
            subReasons.add("Qos Profile "+ BasicValidations.printIdAndName(qosProfileImported.getId(),qosProfileImported.getName()) +" already exists in Package " + BasicValidations.printIdAndName(pkgData.getId(), pkgData.getName()));
        }else if(CommonConstants.REPLACE.equalsIgnoreCase(action)) {
                qosProfileImported.setId(existingQosProfile.getId());
        }
    }

    private void validateQosProfileName(QosProfileData qosProfileImported, PkgData pkgData, SessionProvider session, String action, List<String> subReasons) throws Exception {
        BasicValidations.validateName(qosProfileImported.getName(),"Qos Profile",subReasons);
    }

}
