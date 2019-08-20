package com.elitecore.corenetvertex.core.validator;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.Discriminators;
import com.elitecore.corenetvertex.pkg.chargingrulebasename.ChargingRuleBaseNameData;
import com.elitecore.corenetvertex.pkg.chargingrulebasename.ChargingRuleDataServiceTypeData;
import com.elitecore.corenetvertex.pkg.qos.QosProfileDetailData;
import com.elitecore.corenetvertex.util.SessionProvider;
import com.elitecore.corenetvertex.util.commons.ImportExportCRUDOperationUtil;

import java.util.ArrayList;
import java.util.List;

import static com.elitecore.corenetvertex.core.validator.BasicValidations.printIdAndName;
import static com.elitecore.corenetvertex.core.validator.ChargingRuleBaseNameBasicValidator.validateMandatoryParameters;

/**
 * Created by RAJ KIRPALSINH on 10/14/16.
 */
public class ChargingRuleBaseNameImportValidator implements Validator<ChargingRuleBaseNameData,QosProfileDetailData,Object> {

    private static final String MODULE ="CHARGING-RULE-BASE-NAME-IMPORT-VALIDATOR" ;

    @Override
    public List<String> validate(ChargingRuleBaseNameData chargingRuleBaseNameImported, QosProfileDetailData parentObject, Object superObject, String action, SessionProvider session) {
        if(LogManager.getLogger().isDebugLogLevel()){
            LogManager.getLogger().debug(MODULE,"Inside validate() method validating ChargingRuleBaseName");
        }
        List<String> subReasons = new ArrayList<String>();
        String chargingRuleName = chargingRuleBaseNameImported.getName();
        String id = chargingRuleBaseNameImported.getId();
        try {
            if (Strings.isNullOrBlank(chargingRuleName) && Strings.isNullOrBlank(id)) {
                subReasons.add("ChargingRuleBaseName Id or Name is mandatory");
                return subReasons;
            }

            BasicValidations.validateName(chargingRuleName,"ChargingRuleBaseName",subReasons);

            if(Collectionz.isNullOrEmpty(subReasons) == false){
                return subReasons;
            }

            validateMandatoryParameters(chargingRuleBaseNameImported, session, subReasons);

            /* Verify monitoringkey with all existing ChargingRuleBaseName. */
            validateMonitoringKeyWithExistingChargingRuleBaseNames(chargingRuleBaseNameImported,session, subReasons);

            //check whether ChargingRuleBaseName is exists or not
            validateWithExistingChargingRuleBaseNames(chargingRuleBaseNameImported, parentObject, superObject, action, session, subReasons);

        } catch (Exception e) {
            LogManager.getLogger().error(MODULE, "Failed to validate ChargingRuleBaseName "+ printIdAndName(id, chargingRuleName));
            LogManager.getLogger().trace(MODULE, e);
            subReasons.add("Failed to validate ChargingRuleBaseName " + printIdAndName(id, chargingRuleName));
        }
        return subReasons;
    }


    /***
     * validation with existing ChargingRuleBaseName works in following way
     * (1). If Id of existing ChargingRuleBaseName different then Id of imported ChargingRuleBaseName than fail
     * (2). Id not provided but ChargingRuleBaseName exist with provided name than update existing ChargingRuleBaseName with imported one
     *
     *
     * @param chargingRuleImported
     * @param parentObject
     * @param superObject
     * @param action
     * @param session
     * @param subReasons
     * @throws Exception
     */


    private void validateWithExistingChargingRuleBaseNames(ChargingRuleBaseNameData chargingRuleImported,Object parentObject, Object superObject, String action, SessionProvider session, final List<String> subReasons) throws Exception {
        if(LogManager.getLogger().isDebugLogLevel()) {
            LogManager.getLogger().debug(MODULE,"Method called validateWithExistingChargingRuleBaseNames()");
        }

        String id = chargingRuleImported.getId();
        String chargingRuleImportedName = chargingRuleImported.getName();

        if(Strings.isNullOrBlank(id)==false){
            ChargingRuleBaseNameData existingChargingRule = ImportExportCRUDOperationUtil.get(ChargingRuleBaseNameData.class,id,session);
            if(existingChargingRule != null  ) {
                validateExistingChargingRule(existingChargingRule, session, id, chargingRuleImported, action, subReasons);
            }
        }

        //Check for duplicate name
        if (Strings.isNullOrBlank(chargingRuleImportedName) == false){
            List<ChargingRuleBaseNameData> existingChargingRules = ImportExportCRUDOperationUtil.getAll(ChargingRuleBaseNameData.class, "name", chargingRuleImportedName, session);
            if(Collectionz.isNullOrEmpty(existingChargingRules) == false){
                for(ChargingRuleBaseNameData existingChargingRule : existingChargingRules) {
                    if (validateExistingChargingRule(existingChargingRule, session, id, chargingRuleImported,action,subReasons) == false) {
                        return;
                    }
                }
            }
        }
    }

    private boolean validateExistingChargingRule(ChargingRuleBaseNameData existingChargingRule, SessionProvider session, String id,ChargingRuleBaseNameData chargingRuleImported,String action, List<String> subReasons ) throws Exception {

        if(LogManager.getLogger().isDebugLogLevel()) {
            LogManager.getLogger().debug(MODULE,"Method called validateExistingChargingRule()");
        }

        if(CommonConstants.STATUS_DELETED.equalsIgnoreCase(existingChargingRule.getStatus())){
            ImportExportCRUDOperationUtil.deleteChargingRuleBaseName(existingChargingRule.getId(), session);
            return true;
        } else {
           return validateChargingRuleBaseNameBasedOnAction(chargingRuleImported, existingChargingRule, session, action, subReasons);
        }

    }

    private void validateMonitoringKeyWithExistingChargingRuleBaseNames(ChargingRuleBaseNameData chargingRuleImported, SessionProvider session, final List<String> subReasons) throws Exception {

        /* Checking for monitoring key value already exist within the ChargingRuleBaseNames or not */

        List<ChargingRuleDataServiceTypeData> chargingRuleDataServiceTypeDatas = chargingRuleImported.getChargingRuleDataServiceTypeDatas();

        for (ChargingRuleDataServiceTypeData outerChargingRuleDataServiceTypeData : chargingRuleDataServiceTypeDatas)
        {
            List<ChargingRuleDataServiceTypeData> chargingRuleDataServiceTypeDatasLocal = ImportExportCRUDOperationUtil.getAll(ChargingRuleDataServiceTypeData.class, "monitoringKey", outerChargingRuleDataServiceTypeData.getMonitoringKey(), session);

            if (Collectionz.isNullOrEmpty(chargingRuleDataServiceTypeDatasLocal) == false)
            {
                StringBuilder builder = new StringBuilder();
                for (ChargingRuleDataServiceTypeData dbCrbnServiceType : chargingRuleDataServiceTypeDatasLocal) {

                    if( dbCrbnServiceType.getChargingRuleBaseName().getId().equals(chargingRuleImported.getId()) ||
                            dbCrbnServiceType.getChargingRuleBaseName().getName().equals(chargingRuleImported.getName()) ) {
                        continue;
                    }

                    builder.append("Monitoring key: ")
                            .append(outerChargingRuleDataServiceTypeData.getMonitoringKey())
                            .append(" associated with ChargingRuleBaseName ")
                            .append(BasicValidations.printIdAndName(chargingRuleImported.getId(), chargingRuleImported.getName()))
                            .append("  already exists with existing ChargingRuleBaseName ")
                            .append(BasicValidations.printIdAndName(dbCrbnServiceType .getChargingRuleBaseName().getId(), dbCrbnServiceType .getChargingRuleBaseName().getName()));

                    subReasons.add(builder.toString());
                }
            }
        }
    }

    private boolean validateChargingRuleBaseNameBasedOnAction(ChargingRuleBaseNameData chargingRuleImported, ChargingRuleBaseNameData existingChargingRuleBaseName, SessionProvider session, String action, List<String> subReasons) throws Exception {

        LogManager.getLogger().debug(MODULE,"Method called validateChargingRuleBaseNameBasedOnAction()");

        if (CommonConstants.FAIL.equalsIgnoreCase(action)) {
            subReasons.add("ChargingRuleBaseName with name: " + chargingRuleImported.getName() + " already exists with Id : "+existingChargingRuleBaseName.getId());
            return false;
        }
        if (CommonConstants.REPLACE.equalsIgnoreCase(action)) {

            if (Strings.isNullOrBlank(chargingRuleImported.getId()) == false && existingChargingRuleBaseName.getId().equalsIgnoreCase(chargingRuleImported.getId()) == false) {
                if (LogManager.getLogger().isDebugLogLevel()) {
                    LogManager.getLogger().debug(MODULE, "ChargingRuleBaseName " + existingChargingRuleBaseName.getName() + " already exist  with different Id : " + existingChargingRuleBaseName.getId());
                }
                subReasons.add(Discriminators.CHARGING_RULE_BASE_NAME + existingChargingRuleBaseName.getName() + " already exist  with different Id: " + existingChargingRuleBaseName.getId());
                return false;
            }
            setQosProfileDetail(chargingRuleImported, existingChargingRuleBaseName);
            ImportExportCRUDOperationUtil.deleteChargingRuleBaseName(existingChargingRuleBaseName.getId(),session);
            chargingRuleImported.setId(existingChargingRuleBaseName.getId());

        }
        return true;
    }

    private void setQosProfileDetail(ChargingRuleBaseNameData chargingRuleImported, ChargingRuleBaseNameData existingChargingRule) {
        LogManager.getLogger().debug(MODULE,"Method called setQosProfileDetail()");

        if ( Collectionz.isNullOrEmpty(existingChargingRule.getQosProfileDetails()) == false ) {
            chargingRuleImported.getQosProfileDetails().clear();
            chargingRuleImported.getQosProfileDetails().addAll(existingChargingRule.getQosProfileDetails());
        }
    }
}