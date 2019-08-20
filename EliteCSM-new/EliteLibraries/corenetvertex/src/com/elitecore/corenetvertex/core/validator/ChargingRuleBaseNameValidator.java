package com.elitecore.corenetvertex.core.validator;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.pkg.PkgData;
import com.elitecore.corenetvertex.pkg.PkgType;
import com.elitecore.corenetvertex.sm.ResourceData;
import com.elitecore.corenetvertex.pkg.chargingrulebasename.ChargingRuleBaseNameData;
import com.elitecore.corenetvertex.pkg.chargingrulebasename.ChargingRuleData;
import com.elitecore.corenetvertex.pkg.qos.QosProfileDetailData;
import com.elitecore.corenetvertex.util.SessionProvider;
import com.elitecore.corenetvertex.util.commons.ImportExportCRUDOperationUtil;

import java.util.ArrayList;
import java.util.List;

/*
    Created by RAJ KIRPALSINH
    This class is used to validate ChargingRuleBaseName which are import with the QosProfile attached with Package
*/

public class ChargingRuleBaseNameValidator implements Validator<ChargingRuleData, QosProfileDetailData, ResourceData> {

    private static final String MODULE = "CHARGING-RULE-VALIDATOR";

    @Override
    public List<String> validate(ChargingRuleData chargingRuleImported, QosProfileDetailData qosProfileDetailData, ResourceData superObject, String action, SessionProvider session) {
        List<String> subReasons = new ArrayList<String>();
        String chargingRuleImportedName = chargingRuleImported.getName();
        String id = chargingRuleImported.getId();

        try {

            //if(superObject.getClass().isInstance(PkgData.class)) {
            if(superObject instanceof  PkgData) {
                if (Strings.isNullOrBlank(chargingRuleImportedName) && Strings.isNullOrBlank(id)) {
                    subReasons.add("ChargingRuleBaseName Id or Name is mandatory");
                    return subReasons;
                }

                checkPackageType(chargingRuleImported, superObject, subReasons);

                //check ChargingRuleBaseName is exists or not
                validateWithExistingChargingRule(chargingRuleImported, qosProfileDetailData, session, subReasons);
            }
        } catch (Exception e) {
            LogManager.getLogger().error(MODULE, "Failed to validate ChargingRuleBaseName "+ BasicValidations.printIdAndName(id,chargingRuleImportedName) + ") associated with Qos Profile " + BasicValidations.printIdAndName(qosProfileDetailData.getQosProfile().getId(),qosProfileDetailData.getQosProfile().getName()));
                    LogManager.getLogger().trace(MODULE, e);
            subReasons.add("Failed to validate ChargingRuleBaseName " + BasicValidations.printIdAndName(id,chargingRuleImportedName) + ") associated with Qos Profile " + BasicValidations.printIdAndName(qosProfileDetailData.getQosProfile().getId(),qosProfileDetailData.getQosProfile().getName())+". Kindly refer logs for further details");
        }
        return subReasons;
    }

    private void checkPackageType(ChargingRuleData chargingRuleImported, ResourceData superObject, List<String> subReasons) {
        if(superObject instanceof PkgData) {
            PkgData pkgData = (PkgData) superObject;

            /* ChargingRuleBaseName is configurable with Exclusive AddOn and Base Package */
            if(( PkgType.ADDON.name().equalsIgnoreCase(pkgData.getType()) && pkgData.isExclusiveAddOn()==true )) {
                return;
            }

            if(( PkgType.ADDON.name().equalsIgnoreCase(pkgData.getType()) && pkgData.isExclusiveAddOn()==false )) {
                subReasons.add(" " + BasicValidations.printIdAndName(chargingRuleImported.getId(), chargingRuleImported.getName()) + " can not be configured with NonExclusive " + pkgData.getType() + " Package" + BasicValidations.printIdAndName(pkgData.getId(), pkgData.getName()));
                return;
            }

            if(PkgType.BASE.name().equalsIgnoreCase(pkgData.getType()) == false ){
                subReasons.add(" " + BasicValidations.printIdAndName(chargingRuleImported.getId(), chargingRuleImported.getName()) + " can not be configured with " + pkgData.getType() + " Package" + BasicValidations.printIdAndName(pkgData.getId(), pkgData.getName()));
            }
        }
    }


    private void validateWithExistingChargingRule(ChargingRuleData chargingRuleImported,QosProfileDetailData qosProfileDetailData, SessionProvider session, final List<String> subReasons) throws Exception {

        String id = chargingRuleImported.getId();
        String chargingRuleImportedName = chargingRuleImported.getName();
        ChargingRuleBaseNameData existingChargingRule = getChargingRuleData(session, id, chargingRuleImportedName);

        if (existingChargingRule == null) {
                if (LogManager.getLogger().isDebugLogLevel()) {
                    LogManager.getLogger().debug(MODULE, "ChargingRuleBaseName with "+  BasicValidations.printIdAndName(id,chargingRuleImportedName)  + " does not exist");
                }
                subReasons.add("ChargingRuleBaseName with " + BasicValidations.printIdAndName(id,chargingRuleImportedName) +" does not exist");

        } else {

            if(CommonConstants.STATUS_DELETED.equalsIgnoreCase(existingChargingRule.getStatus())){
                subReasons.add("ChargingRuleBaseName with" + BasicValidations.printIdAndName(id,chargingRuleImportedName) +" does not exist");
            }else {

                if(Strings.isNullOrBlank(chargingRuleImportedName) == false && existingChargingRule.getName().equals(chargingRuleImportedName)){
                    if((Strings.isNullOrBlank(id) == false && existingChargingRule.getId().equalsIgnoreCase(id) == false)) {
                        subReasons.add("ChargingRuleBaseName Id (" + id + ") and name (" + chargingRuleImportedName + ") are not related");
                    }
                }
                if(subReasons.size() == 0) {
                    qosProfileDetailData.getChargingRuleBaseNames().add(existingChargingRule);
                }
            }
        }
    }

    private ChargingRuleBaseNameData getChargingRuleData(SessionProvider session, String id, String chargingRuleName) throws Exception {
        ChargingRuleBaseNameData existingChargingRule = null;
        if (Strings.isNullOrBlank(chargingRuleName) == false) {
            List<ChargingRuleBaseNameData> chargingRules = ImportExportCRUDOperationUtil.getByName(ChargingRuleBaseNameData.class, chargingRuleName, session);
            if(Collectionz.isNullOrEmpty(chargingRules) == false){
                existingChargingRule = chargingRules.get(0);
            }
        }else if(Strings.isNullOrBlank(id) == false) {
            existingChargingRule = ImportExportCRUDOperationUtil.get(ChargingRuleBaseNameData.class, id, session);
        }
        return existingChargingRule;
    }

}