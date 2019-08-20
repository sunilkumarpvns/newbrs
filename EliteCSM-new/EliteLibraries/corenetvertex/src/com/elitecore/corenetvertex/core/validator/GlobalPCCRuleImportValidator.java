package com.elitecore.corenetvertex.core.validator;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.Discriminators;
import com.elitecore.corenetvertex.pkg.pccrule.PCCRuleData;
import com.elitecore.corenetvertex.pkg.pccrule.PCCRuleScope;
import com.elitecore.corenetvertex.util.SessionProvider;
import com.elitecore.corenetvertex.util.commons.ImportExportCRUDOperationUtil;

import java.util.ArrayList;
import java.util.List;

import static com.elitecore.corenetvertex.core.validator.BasicValidations.printIdAndName;
import static com.elitecore.corenetvertex.core.validator.PCCRuleBasicValidator.setDefaultValuesForQosParameter;
import static com.elitecore.corenetvertex.core.validator.PCCRuleBasicValidator.validateMandatoryParameters;
import static com.elitecore.corenetvertex.core.validator.PCCRuleBasicValidator.validateQuotaAndQosRelatedParameters;

/**
 * Created by aditya on 10/14/16.
 */
public class GlobalPCCRuleImportValidator implements Validator<PCCRuleData,Object,Object> {

    private static final String MODULE ="GLOBAL-PCC-VALIDATOR" ;

    @Override
    public List<String> validate(PCCRuleData pccRuleImported, Object parentObject, Object superObject, String action, SessionProvider session) {
        if(LogManager.getLogger().isDebugLogLevel()){
            LogManager.getLogger().debug(MODULE,"Inside validate method validating pcc rule of name: "+ pccRuleImported);
        }
        List<String> subReasons = new ArrayList<String>();
        String pccRuleName = pccRuleImported.getName();
        String id = pccRuleImported.getId();
        try {

            //validate pcc rule name
            BasicValidations.validateName(pccRuleName,"PCC Rule",subReasons);
            if(Collectionz.isNullOrEmpty(subReasons) == false){
                return subReasons;
            }

            pccRuleImported.setScope(PCCRuleScope.GLOBAL);

            //validate mandatory parameters of pcc rule
            validateMandatoryParameters(pccRuleImported, session, subReasons);

            //Validate Quota and qos related parameters
            validateQuotaAndQosRelatedParameters(pccRuleImported, subReasons);

            //check whether PCC rule is exists or not
            validateWithExistingPCCRuleDatas(pccRuleImported, parentObject, superObject, action, session, subReasons);

            setDefaultValuesForQosParameter(pccRuleImported);

        } catch (Exception e) {
            LogManager.getLogger().error(MODULE, "Failed to validate pcc rule "+ printIdAndName(id, pccRuleName));
            LogManager.getLogger().trace(MODULE, e);
            subReasons.add("Failed to validate PCC Rule " + printIdAndName(id, pccRuleName));
        }
        return subReasons;
    }


    /***
     * validation with existing PCC rule works in following way
     * (1). If existing PCC rule exist with LOCAL scope then fail
     * (2). If Id of existing PCC rule different then Id of imported pcc rule than fail
     * (3). Id not provided but PCC rule exist with provided name than update existing PCC rule with imported one
     *
     *
     * @param pccRuleImported
     * @param parentObject
     * @param superObject
     * @param action
     * @param session
     * @param subReasons
     * @throws Exception
     */


    private void validateWithExistingPCCRuleDatas(PCCRuleData pccRuleImported, Object parentObject, Object superObject, String action, SessionProvider session, final List<String> subReasons) throws Exception {

        String id = pccRuleImported.getId();
        String pccRuleName = pccRuleImported.getName();

        //Validate existing PCC Rule by id
        if (Strings.isNullOrBlank(id) == false) {
            PCCRuleData existingPCCRule = ImportExportCRUDOperationUtil.get(PCCRuleData.class, id, session);
            if (existingPCCRule != null) {
                if (validateWithExistingPCCRule(pccRuleImported, existingPCCRule, action, session, subReasons) == false) {
                    return;
                }
            }
        }
        //Validate existing PCC Rule by name
        List<PCCRuleData> existingPccRuleDatas = ImportExportCRUDOperationUtil.getAll(PCCRuleData.class, "name", pccRuleName, session);
        if (Collectionz.isNullOrEmpty(existingPccRuleDatas) == false) {
            for (PCCRuleData existingPCCRule : existingPccRuleDatas) {
                if (validateWithExistingPCCRule(pccRuleImported, existingPCCRule, action, session, subReasons) == false) {
                    return;
                }
            }
        }

    }

    private boolean validateWithExistingPCCRule(PCCRuleData pccRuleImported, PCCRuleData existingPCCRule, String action, SessionProvider session, List<String> subReasons) throws Exception {
        if (CommonConstants.STATUS_DELETED.equalsIgnoreCase(existingPCCRule.getStatus())) {
            ImportExportCRUDOperationUtil.deleteGlobalPCCRule(existingPCCRule.getId(), session);
            return true;
        }

        if (PCCRuleScope.LOCAL == existingPCCRule.getScope()) {
            if (LogManager.getLogger().isDebugLogLevel()) {
                LogManager.getLogger().debug(MODULE, Discriminators.PCCRULE + " " + pccRuleImported.getName() + " already exist  with SCOPE: " + PCCRuleScope.LOCAL);
            }
            subReasons.add(Discriminators.PCCRULE + " " + printIdAndName(pccRuleImported.getId(), pccRuleImported.getName()) + " already exists with SCOPE: " + PCCRuleScope.LOCAL);
            return false;
        }

        return validatePCCRuleBasedOnAction(pccRuleImported, existingPCCRule, session, action, subReasons);
    }

    private boolean validatePCCRuleBasedOnAction(PCCRuleData pccRuleImported, PCCRuleData existingPCCRule, SessionProvider session, String action, List<String> subReasons) throws Exception {

        if (CommonConstants.FAIL.equalsIgnoreCase(action)) {
            subReasons.add(Discriminators.PCCRULE + " with name: " + pccRuleImported.getName() + " already exists with id : " + existingPCCRule.getId());
            return false;
        }
        if (CommonConstants.REPLACE.equalsIgnoreCase(action)) {
            if (Strings.isNullOrBlank(pccRuleImported.getId()) == false && existingPCCRule.getId().equalsIgnoreCase(pccRuleImported.getId()) == false) {
                if (LogManager.getLogger().isDebugLogLevel()) {
                    LogManager.getLogger().debug(MODULE, Discriminators.PCCRULE + " " + pccRuleImported.getName() + " already exist  with different Id : " + existingPCCRule.getId());
                }
                subReasons.add(Discriminators.PCCRULE + " " + pccRuleImported.getName() + " already exist  with different Id: " + existingPCCRule.getId());
                return false;
            }
            setQosProfileDetail(pccRuleImported, existingPCCRule);
            ImportExportCRUDOperationUtil.deleteGlobalPCCRule(existingPCCRule.getId(), session);
            pccRuleImported.setId(existingPCCRule.getId());
        }
        return true;
    }

    private void setQosProfileDetail(PCCRuleData pccRuleImported, PCCRuleData existingPCCRule) {
        if (Collectionz.isNullOrEmpty(existingPCCRule.getQosProfileDetails()) == false && pccRuleImported.getScope() == PCCRuleScope.GLOBAL) {
            pccRuleImported.getQosProfileDetails().clear();
            pccRuleImported.getQosProfileDetails().addAll(existingPCCRule.getQosProfileDetails());
        }
    }
}