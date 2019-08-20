package com.elitecore.corenetvertex.core.validator;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.sm.ResourceData;
import com.elitecore.corenetvertex.pkg.PkgData;
import com.elitecore.corenetvertex.pkg.PkgType;
import com.elitecore.corenetvertex.pkg.pccrule.GlobalPCCRuleData;
import com.elitecore.corenetvertex.pkg.pccrule.PCCRuleData;
import com.elitecore.corenetvertex.pkg.pccrule.PCCRuleScope;
import com.elitecore.corenetvertex.pkg.qos.QosProfileDetailData;
import com.elitecore.corenetvertex.util.SessionProvider;
import com.elitecore.corenetvertex.util.commons.ImportExportCRUDOperationUtil;

import java.util.ArrayList;
import java.util.List;

public class GlobalPCCRuleValidator implements Validator<GlobalPCCRuleData, QosProfileDetailData, ResourceData> {

    private static final String MODULE = "GLOBAL-PCC-RULE-VALIDATOR";

    @Override
    public List<String> validate(GlobalPCCRuleData globalPCCRuleImported, QosProfileDetailData qosProfileDetailData, ResourceData superObject, String action, SessionProvider session) {
        List<String> subReasons = new ArrayList<String>();
        String pccRuleName = globalPCCRuleImported.getName();
        String id = globalPCCRuleImported.getId();
        try {
            if(superObject instanceof PkgData) {
                if (Strings.isNullOrBlank(pccRuleName) && Strings.isNullOrBlank(id)) {
                    subReasons.add("Global PCC Rule Id or Name is mandatory");
                    return subReasons;
                }
                checkPackageType(globalPCCRuleImported, superObject, subReasons);

                //check whether PCC rule is exists or not
                validateWithExistingPCCRule(globalPCCRuleImported,qosProfileDetailData,session, subReasons);
            }
        } catch (Exception e) {
            LogManager.getLogger().error(MODULE, "Failed to validate pcc rule "+ BasicValidations.printIdAndName(id,pccRuleName) + ") associated with Qos Profile " + BasicValidations.printIdAndName(qosProfileDetailData.getQosProfile().getId(),qosProfileDetailData.getQosProfile().getName()));
                    LogManager.getLogger().trace(MODULE, e);
            subReasons.add("Failed to validate PCC Rule " + BasicValidations.printIdAndName(id,pccRuleName) + ") associated with Qos Profile " + BasicValidations.printIdAndName(qosProfileDetailData.getQosProfile().getId(),qosProfileDetailData.getQosProfile().getName())+". Kindly refer logs for further details");
        }
        return subReasons;
    }

    private void checkPackageType(GlobalPCCRuleData globalPCCRuleImported, ResourceData superObject, List<String> subReasons) {
        if(superObject instanceof PkgData) {
            PkgData pkgData = (PkgData) superObject;
            if (PkgType.BASE.name().equalsIgnoreCase(pkgData.getType()) == false) {
                subReasons.add("GLOBAL PCC Rule " + BasicValidations.printIdAndName(globalPCCRuleImported.getId(), globalPCCRuleImported.getName()) + " can not be configured with " + pkgData.getType() + " Package" + BasicValidations.printIdAndName(pkgData.getId(), pkgData.getName()));
            }
        }
    }


    private void validateWithExistingPCCRule(GlobalPCCRuleData pccRuleImported,QosProfileDetailData qosProfileDetailData, SessionProvider session, final List<String> subReasons) throws Exception {

        String id = pccRuleImported.getId();
        String pccRuleName = pccRuleImported.getName();
        PCCRuleData existingPCCRule = getPCCRuleData(session, id, pccRuleName);

        if (existingPCCRule == null) {
                if (LogManager.getLogger().isDebugLogLevel()) {
                    LogManager.getLogger().debug(MODULE, "Global PCC Rule with "+  BasicValidations.printIdAndName(id,pccRuleName)  + " does not exist");
                }
                subReasons.add("Global PCC Rule with " + BasicValidations.printIdAndName(id,pccRuleName) +" does not exist");
        } else {
            if(CommonConstants.STATUS_DELETED.equalsIgnoreCase(existingPCCRule.getStatus())){
                subReasons.add("Global PCC Rule with" + BasicValidations.printIdAndName(id,pccRuleName) +" does not exist");
            }else {
                if (PCCRuleScope.GLOBAL != existingPCCRule.getScope()) {
                    if (LogManager.getLogger().isDebugLogLevel()) {
                        LogManager.getLogger().debug(MODULE, "Defined pcc rule " + BasicValidations.printIdAndName(id,pccRuleName) +" is of scope:" + PCCRuleScope.GLOBAL + " and existing pcc rule is of scope: " + existingPCCRule.getScope());
                    }
                    subReasons.add("Defined PCC Rule " + BasicValidations.printIdAndName(id,pccRuleName) + " is of scope:" + PCCRuleScope.GLOBAL + " and existing PCC Rule is of scope: " + existingPCCRule.getScope());
                }
                if(Strings.isNullOrBlank(pccRuleName) == false && existingPCCRule.getName().equals(pccRuleName)){
                    if((Strings.isNullOrBlank(id) == false && existingPCCRule.getId().equalsIgnoreCase(id) == false)) {
                        subReasons.add("Global PCC Rule Id (" + id + ") and name (" + pccRuleName + ") are not related");
                    }
                }
                if(subReasons.size() == 0) {
                    qosProfileDetailData.getPccRules().add(existingPCCRule);
                }
            }
        }
    }

    private PCCRuleData getPCCRuleData(SessionProvider session, String id, String pccRuleName) throws Exception {
        PCCRuleData existingPCCRule = null;
        if (Strings.isNullOrBlank(pccRuleName) == false) {
            List<PCCRuleData> pccList = ImportExportCRUDOperationUtil.getByName(PCCRuleData.class, pccRuleName, session);
            if(Collectionz.isNullOrEmpty(pccList) == false){
                existingPCCRule = pccList.get(0);
            }
        }else if(Strings.isNullOrBlank(id) == false) {
            existingPCCRule = ImportExportCRUDOperationUtil.get(PCCRuleData.class, id, session);
        }
        return existingPCCRule;
    }

}
