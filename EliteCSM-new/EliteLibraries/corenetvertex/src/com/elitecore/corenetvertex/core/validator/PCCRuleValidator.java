package com.elitecore.corenetvertex.core.validator;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.pkg.EmergencyPkgDataExt;
import com.elitecore.corenetvertex.pkg.PkgData;
import com.elitecore.corenetvertex.pkg.pccrule.PCCRuleData;
import com.elitecore.corenetvertex.pkg.pccrule.PCCRuleScope;
import com.elitecore.corenetvertex.pkg.qos.QosProfileDetailData;
import com.elitecore.corenetvertex.sm.ResourceData;
import com.elitecore.corenetvertex.util.SessionProvider;
import com.elitecore.corenetvertex.util.commons.ImportExportCRUDOperationUtil;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static com.elitecore.corenetvertex.core.validator.PCCRuleBasicValidator.*;

public class PCCRuleValidator implements Validator<PCCRuleData, QosProfileDetailData, ResourceData> {

    private static final String MODULE = "PCC-RULE-VALIDATOR";




    @Override
    public List<String> validate(PCCRuleData pccRuleImported, QosProfileDetailData qosProfileDetailData, ResourceData superObject, String action, SessionProvider session) {

		List<String> subReasons = new ArrayList<String>();
		if (PCCRuleScope.GLOBAL == pccRuleImported.getScope()) {
			return subReasons;
		}

        String pccRuleName = pccRuleImported.getName();
        String id = pccRuleImported.getId();
        try {
            if (Strings.isNullOrBlank(pccRuleName) && Strings.isNullOrBlank(id)) {
                subReasons.add("PCC Rule Id or PCC Rule name is mandatory");
            }
            pccRuleImported.setScope(PCCRuleScope.LOCAL);
            pccRuleImported.setCreatedByStaff(superObject.getCreatedByStaff());
            pccRuleImported.setCreatedDate(new Timestamp(System.currentTimeMillis()));
            pccRuleImported.setModifiedByStaff(pccRuleImported.getCreatedByStaff());
            pccRuleImported.setModifiedDate(pccRuleImported.getCreatedDate());
            //validate pcc rule name
            BasicValidations.validateName(pccRuleName,"PCC Rule",subReasons);

            //validate mandatory parameters of pcc rule
            validateMandatoryParameters(pccRuleImported, session, subReasons);

            //check whether PCC rule is exists or not
            validateWithExistingPCCRule(pccRuleImported,qosProfileDetailData,superObject, action, session, subReasons);


            //Validate Quota and qos related parameters
            validateQuotaAndQosRelatedParameters(pccRuleImported,subReasons);

            setDefaultValuesForQosParameter(pccRuleImported);


        } catch (Exception e) {
            LogManager.getLogger().error(MODULE, "Failed to validate pcc rule "+ BasicValidations.printIdAndName(id,pccRuleName) + ") associated with QoS Profile " + BasicValidations.printIdAndName(qosProfileDetailData.getQosProfile().getId(),qosProfileDetailData.getQosProfile().getName()));
                    LogManager.getLogger().trace(MODULE, e);
            subReasons.add("Failed to validate PCC Rule " + BasicValidations.printIdAndName(id,pccRuleName) + ") associated with QoS Profile " + BasicValidations.printIdAndName(qosProfileDetailData.getQosProfile().getId(),qosProfileDetailData.getQosProfile().getName())+". Kindly refer logs for further details");
        }
        return subReasons;
    }

    private void validateWithExistingPCCRule(PCCRuleData pccRuleImported, QosProfileDetailData parentObject, final ResourceData superObject, String action, SessionProvider session, final List<String> subReasons) throws Exception {

        String id = pccRuleImported.getId();
        String pccRuleName = pccRuleImported.getName();
        PCCRuleData existingPCCRule = getPCCRuleData(pccRuleImported, superObject, session, id, pccRuleName);

        if  (existingPCCRule != null)  {
            String name = null;
            if(superObject instanceof PkgData){
                PkgData pkg = (PkgData) superObject;
                name = pkg.getName();
            }else if(superObject instanceof EmergencyPkgDataExt){
                EmergencyPkgDataExt emergencyPkg = (EmergencyPkgDataExt) superObject;
                name = emergencyPkg.getName();
            }
            if (PCCRuleScope.LOCAL == existingPCCRule.getScope() && CommonConstants.STATUS_DELETED.equalsIgnoreCase(existingPCCRule.getStatus()) == false) {
                List<QosProfileDetailData> qosProfileDetailDatas = existingPCCRule.getQosProfileDetails();
                if (Collectionz.isNullOrEmpty(qosProfileDetailDatas) == false) {
                    QosProfileDetailData detailData = qosProfileDetailDatas.get(0);
                    PkgData existingPkg = detailData.getQosProfile().getPkgData();
                    if ((Strings.isNullOrBlank(superObject.getId()) == false && existingPkg.getId().equalsIgnoreCase(superObject.getId()) == false) || existingPkg.getName().equals(name) == false) {
                        subReasons.add("PCC Rule " + BasicValidations.printIdAndName(id,pccRuleName) + " already exists in different Package " + BasicValidations.printIdAndName(existingPkg.getId(),existingPkg.getName()));
                        existingPCCRule = null;
                    }
                }
            }


            if (existingPCCRule != null) {
                if (existingPCCRule.getScope() != pccRuleImported.getScope()) {
                    if (LogManager.getLogger().isDebugLogLevel()) {
                        LogManager.getLogger().debug(MODULE, "Defined pcc rule " + BasicValidations.printIdAndName(id,pccRuleName) +" is of scope:" + pccRuleImported.getScope() + " and existing pcc rule is of scope: " + existingPCCRule.getScope());
                    }
                    subReasons.add("Defined PCC Rule " + BasicValidations.printIdAndName(id,pccRuleName) + " is of scope:" + pccRuleImported.getScope() + " and existing PCC Rule is of scope: " + existingPCCRule.getScope());
                }
                if (CommonConstants.STATUS_DELETED.equalsIgnoreCase(existingPCCRule.getStatus()) == false) {
                    validatePCCRuleBasedOnAction(pccRuleImported, existingPCCRule, session, action, subReasons);
                } else {
                       existingPCCRule.setQosProfileDetails(null);
                }
            }
        }
    }

    private PCCRuleData getPCCRuleData(PCCRuleData pccRuleImported, ResourceData superObject, SessionProvider session, String id, String pccRuleName) throws Exception {
        PCCRuleData existingPCCRule = null;
        if (Strings.isNullOrBlank(pccRuleName) == false) {
            List<PCCRuleData> pccList = ImportExportCRUDOperationUtil.getByName(PCCRuleData.class, pccRuleName, session);
            if (Collectionz.isNullOrEmpty(pccList)) {
                if (PCCRuleScope.LOCAL == pccRuleImported.getScope()) {
                    if(Strings.isNullOrBlank(id) == false) {
                        existingPCCRule = ImportExportCRUDOperationUtil.get(PCCRuleData.class, id, session);

                    }else{
                        //TODO WHAT TO DO
                    }
                }
            } else {
                existingPCCRule = pccList.get(0);

            }
        }else if(Strings.isNullOrBlank(id) == false) {
            existingPCCRule = ImportExportCRUDOperationUtil.get(PCCRuleData.class, id, session);
        }
        if (existingPCCRule != null && CommonConstants.STATUS_DELETED.equalsIgnoreCase(existingPCCRule.getStatus()) && PCCRuleScope.LOCAL == existingPCCRule.getScope()) {
            ImportExportCRUDOperationUtil.deletePCCRule(existingPCCRule.getId(), session);
        }
        return existingPCCRule;
    }

    private void validatePCCRuleBasedOnAction(PCCRuleData pccRuleImported, PCCRuleData existingPCCRule, SessionProvider session, String action, List<String> subReasons) throws Exception {
            if (CommonConstants.FAIL.equalsIgnoreCase(action)) {
                validatePCCRuleName(pccRuleImported, session, subReasons);
            } else if (CommonConstants.REPLACE.equalsIgnoreCase(action)) {
                if(Strings.isNullOrBlank(pccRuleImported.getId()) || PCCRuleScope.GLOBAL == pccRuleImported.getScope()) {
                    pccRuleImported.setId(existingPCCRule.getId());
                }
            }
            setQosProfileDetail(pccRuleImported, existingPCCRule);
    }

    private void validatePCCRuleName(PCCRuleData pccRuleImported, SessionProvider session, List<String> subReasons) throws Exception {
        String pccRuleName = pccRuleImported.getName();
        List<PCCRuleData> pccList = ImportExportCRUDOperationUtil.getByName(PCCRuleData.class, pccRuleName , session);

        if (Collectionz.isNullOrEmpty(pccList) == false){
            if (PCCRuleScope.GLOBAL != pccRuleImported.getScope()) {
                subReasons.add("PCC Rule with name: " + pccRuleImported.getName() + " already exists");
            }
        }
    }


    private void setQosProfileDetail(PCCRuleData pccRuleImported, PCCRuleData existingPCCRule) {
        if (Collectionz.isNullOrEmpty(existingPCCRule.getQosProfileDetails()) == false && pccRuleImported.getScope() == PCCRuleScope.GLOBAL) {
            for(QosProfileDetailData qosProfileDetailData : existingPCCRule.getQosProfileDetails()) {
                boolean found = false;
            for(QosProfileDetailData qosProfileDetailData1 : pccRuleImported.getQosProfileDetails()) {
                if(qosProfileDetailData.getId().equals(qosProfileDetailData1.getId())){
                    found = true;
                    break;
                 }

            }
            if(found == false){
                pccRuleImported.getQosProfileDetails().add(qosProfileDetailData);
            }


        }
        } else {
            pccRuleImported.setQosProfileDetails(existingPCCRule.getQosProfileDetails());
        }
    }





}
