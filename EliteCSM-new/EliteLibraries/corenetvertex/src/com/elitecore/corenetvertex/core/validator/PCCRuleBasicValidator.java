package com.elitecore.corenetvertex.core.validator;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.CommonStatusValues;
import com.elitecore.corenetvertex.constants.DataUnit;
import com.elitecore.corenetvertex.constants.FieldValueConstants;
import com.elitecore.corenetvertex.constants.QCI;
import com.elitecore.corenetvertex.constants.QoSUnit;
import com.elitecore.corenetvertex.constants.TimeUnit;
import com.elitecore.corenetvertex.core.constant.ChargingModes;
import com.elitecore.corenetvertex.pkg.chargingrulebasename.ChargingRuleDataServiceTypeData;
import com.elitecore.corenetvertex.pkg.pccrule.PCCRuleData;
import com.elitecore.corenetvertex.pkg.pccrule.PCCRuleType;
import com.elitecore.corenetvertex.pkg.ratinggroup.RatingGroupData;
import com.elitecore.corenetvertex.pm.constants.FlowStatus;
import com.elitecore.corenetvertex.util.SessionProvider;
import com.elitecore.corenetvertex.util.commons.ImportExportCRUDOperationUtil;

import java.sql.Timestamp;
import java.util.List;

/**
 * Created by aditya on 12/5/16.
 */
public class PCCRuleBasicValidator {

    private static final String MODULE="PCC-BASIC-VALIDATOR";



    public static void validateMandatoryParameters(PCCRuleData pccRuleImported, SessionProvider session, List<String> subReasons) throws Exception {
        String pccRuleName = pccRuleImported.getName() ;
        String id = pccRuleImported.getId();

        if(pccRuleImported.getDataServiceTypeData() == null){
            subReasons.add("Data Service Type must be configured with PCC Rule " + BasicValidations.printIdAndName(pccRuleImported.getId(),pccRuleImported.getName()));
            return;
        }

        //validate monitoring key
        validateMonitoringKey(pccRuleImported, session, subReasons);

        //validate rating group based on Charging key provided
        validateRatingGroup(pccRuleImported, session, subReasons);

        // validate Charging mode
        validateChargingMode(pccRuleImported, pccRuleName, id);

        //validate type
        validateType(pccRuleImported, pccRuleName, id);

        //validatePrecedence
        validatePrecedence(pccRuleImported, pccRuleName, id);

        //validate qci
        validateQci(pccRuleImported, pccRuleName, id);

        //validate flow status
        validateFlowStatus(pccRuleImported, pccRuleName, id);

        //validate usage monitoring
        validateUsageMonitoring(pccRuleImported, pccRuleName, id);

        //validate pre-emption capability and Vulnerability
        validatePreEmptionCapability(pccRuleImported, pccRuleName, id);


        //validate pre-emption  Vulnerability
        validatePreEmptionVulnerablity(pccRuleImported, pccRuleName, id);

        //validating priority level
        validatePriority(pccRuleImported, pccRuleName, id);


    }




    public static void validatePriority(PCCRuleData pccRuleImported, String pccRuleName, String id) {
        if(pccRuleImported.getArp() == null){
            LogManager.getLogger().warn(MODULE, "Priority is not configure with PCC Rule " + BasicValidations.printIdAndName(id, pccRuleName) +". So taking default priority as 15");
            pccRuleImported.setArp((byte) 15);
        }
        if(pccRuleImported.getArp() < 1 || pccRuleImported.getArp() > 15){
            LogManager.getLogger().warn(MODULE, "Invalid Priority is configure with PCC Rule " + BasicValidations.printIdAndName(id, pccRuleName) + ". So taking default priority as 15");
            pccRuleImported.setArp((byte) 15);
        }
    }

    public static void validatePreEmptionVulnerablity(PCCRuleData pccRuleImported, String pccRuleName, String id) {
        if (pccRuleImported.getPreVulnerability() == null || CommonStatusValues.fromBooleanValue(pccRuleImported.getPreVulnerability()) == null) {
            if(LogManager.getLogger().isDebugLogLevel()){
                LogManager.getLogger().debug(MODULE, "Invalid Pre-Vulnerability : " + pccRuleImported.getPreVulnerability() + " configured with PCC Rule " + BasicValidations.printIdAndName(id, pccRuleName) +".  So taking default Preemption vulnerability as ENABLE(1)");
            }
            pccRuleImported.setPreVulnerability(CommonStatusValues.ENABLE.isBooleanValue());
        }
    }

    public static void validatePreEmptionCapability(PCCRuleData pccRuleImported, String pccRuleName, String id) {
        if (pccRuleImported.getPreCapability() == null || CommonStatusValues.fromBooleanValue(pccRuleImported.getPreCapability()) == null) {
            if(LogManager.getLogger().isDebugLogLevel()){
                LogManager.getLogger().debug(MODULE, "Invalid Preemption capability: " + pccRuleImported.getPreCapability() + " configured with PCC Rule " + BasicValidations.printIdAndName(id, pccRuleName) +".  So taking default Preemption capability as DISABLE(0)");
            }
            pccRuleImported.setPreCapability(CommonStatusValues.DISABLE.isBooleanValue());
        }
    }

    public static void validateUsageMonitoring(PCCRuleData pccRuleImported, String pccRuleName, String id) {
        if (pccRuleImported.getUsageMonitoring() == null || (pccRuleImported.getUsageMonitoring() != true  && pccRuleImported.getUsageMonitoring() != false )
                || CommonStatusValues.fromBooleanValue(pccRuleImported.getUsageMonitoring()) == null) {
            if(LogManager.getLogger().isDebugLogLevel()){
                LogManager.getLogger().debug(MODULE, "Invalid Usage Monitoring: " + pccRuleImported.getUsageMonitoring() + " configured with PCC Rule " + BasicValidations.printIdAndName(id, pccRuleName) + ".  So taking default usage metering type as DISABLE(0)");
            }
            pccRuleImported.setUsageMonitoring(CommonStatusValues.DISABLE.isBooleanValue());
        }
    }

    public static void validateFlowStatus(PCCRuleData pccRuleImported, String pccRuleName, String id) {
        if(pccRuleImported.getFlowStatus() == null || FlowStatus.fromValue(pccRuleImported.getFlowStatus()) == null){
            if(LogManager.getLogger().isDebugLogLevel()){
                LogManager.getLogger().debug(MODULE, "Invalid flow status: " + pccRuleImported.getFlowStatus() + " configured with PCC Rule " + BasicValidations.printIdAndName(id, pccRuleName) +".  So taking default flow status as ENABLED(2)");
            }
            pccRuleImported.setFlowStatus((byte)FlowStatus.ENABLED.val);
        }
    }

    public static void validateQci(PCCRuleData pccRuleImported, String pccRuleName, String id) {
        int defaultQci = QCI.getDefault().getQci();
        if(pccRuleImported.getQci() == null){
            LogManager.getLogger().warn(MODULE, "QCI is not configured with PCC Rule " + BasicValidations.printIdAndName(id, pccRuleName) + ".  So taking default value as " + defaultQci);
            pccRuleImported.setQci((byte)defaultQci);
        }else if(QCI.fromId(pccRuleImported.getQci().intValue()) == null){
            LogManager.getLogger().warn(MODULE, "Invalid QCI: "+ pccRuleImported.getQci() +" is configured with PCC Rule " + BasicValidations.printIdAndName(id, pccRuleName) + ".  So taking default value as " + defaultQci);
            pccRuleImported.setQci((byte)defaultQci);
            }
        }

    public static void validatePrecedence(PCCRuleData pccRuleImported, String pccRuleName, String id) {
        if(pccRuleImported.getPrecedence() == null){
            if(LogManager.getLogger().isDebugLogLevel()){
                LogManager.getLogger().debug(MODULE, "Precedence is not configured with PCC Rule " + BasicValidations.printIdAndName(id, pccRuleName) +".  So taking default value as 999");
            }
            pccRuleImported.setPrecedence((short)999);
        } else if(pccRuleImported.getPrecedence()<0 || pccRuleImported.getPrecedence()>999){
            if(LogManager.getLogger().isDebugLogLevel()){
                LogManager.getLogger().debug(MODULE, "Invalid Precedence value "+pccRuleImported.getPrecedence()+" configured with Pcc Rule " + BasicValidations.printIdAndName(id, pccRuleName) +".  Precedence value must be in range from 0 to 999. So taking default value as 999.");
            }
            pccRuleImported.setPrecedence((short)999);
        }
    }

    public static void validateType(PCCRuleData pccRuleImported, String pccRuleName, String id) {
        if(Strings.isNullOrBlank(pccRuleImported.getType()) || (PCCRuleType.STATIC.getValue().equalsIgnoreCase(pccRuleImported.getType())== false && PCCRuleType.DYNAMIC.getValue().equalsIgnoreCase(pccRuleImported.getType()) == false)){
            if(LogManager.getLogger().isDebugLogLevel()){
                LogManager.getLogger().debug(MODULE, "PCC Rule type is not configured with PCC Rule " + BasicValidations.printIdAndName(id, pccRuleName) +".  So taking default type as Dynamic");
            }
            pccRuleImported.setType("Dynamic");
        }
    }

    public static void validateChargingMode(PCCRuleData pccRuleImported, String pccRuleName, String id) {
        if(pccRuleImported.getChargingMode() == null || ChargingModes.fromValue(pccRuleImported.getChargingMode()) == null){
            if(LogManager.getLogger().isDebugLogLevel()){
                LogManager.getLogger().debug(MODULE, "Invalid charging mode: " + pccRuleImported.getChargingMode() + " configured with PCC Rule " + BasicValidations.printIdAndName(id, pccRuleName) +". So taking default mode as Online(0)");
            }
            pccRuleImported.setChargingMode((byte)ChargingModes.ONLINE.val);
        }
    }

    public static void validateMonitoringKey(PCCRuleData pccRuleImported, SessionProvider session, List<String> subReasons) throws Exception {
        String monitoringKey = pccRuleImported.getMonitoringKey();
        if (Strings.isNullOrBlank(monitoringKey)) {
            subReasons.add("Monitoring Key must be provided for PCC Rule(" + pccRuleImported.getId() + CommonConstants.COMMA + pccRuleImported.getName() + ")");
            return;
        }

        List<PCCRuleData> pccRules = ImportExportCRUDOperationUtil.get(PCCRuleData.class, "monitoringKey", monitoringKey, session);
        if (Collectionz.isNullOrEmpty(pccRules) == false) {
            for (PCCRuleData pcc : pccRules) {
                if (monitoringKey.equalsIgnoreCase(pcc.getMonitoringKey()) && pccRuleImported.getName().equals(pcc.getName()) == false && CommonConstants.STATUS_DELETED.equalsIgnoreCase(pcc.getStatus()) == false) {
                    subReasons.add("Monitoring key: " + monitoringKey + " associated with PCC Rule " + BasicValidations.printIdAndName(pccRuleImported.getId(), pccRuleImported.getName()) + "  already exists with existing PCC Rule " + BasicValidations.printIdAndName(pcc.getId(), pcc.getName()));
                    break;
                }
            }
        }

        List<ChargingRuleDataServiceTypeData> chargingRuleDataServiceTypeDatas = ImportExportCRUDOperationUtil.getAll(ChargingRuleDataServiceTypeData.class, "monitoringKey", monitoringKey, session);
        if (Collectionz.isNullOrEmpty(chargingRuleDataServiceTypeDatas) == false) {
            for (ChargingRuleDataServiceTypeData chargingRuleDataServiceTypeData : chargingRuleDataServiceTypeDatas) {
                if (monitoringKey.equals(chargingRuleDataServiceTypeData.getMonitoringKey())) {
                    subReasons.add("Monitoring key: " + monitoringKey + " associated with PCC Rule " + BasicValidations.printIdAndName(pccRuleImported.getId(), pccRuleImported.getName()) + "  already exists with existing ChargingRuleBaseName " + BasicValidations.printIdAndName(chargingRuleDataServiceTypeData.getChargingRuleBaseName().getId(), chargingRuleDataServiceTypeData.getChargingRuleBaseName().getName()));
                    break;
                }
            }
        }

    }


  public static void validateRatingGroup(PCCRuleData pccRuleImported, SessionProvider session, List<String> subReasons) throws Exception {
        String pccRuleName = pccRuleImported.getName();
        String id = pccRuleImported.getId();

        String chargingKey = pccRuleImported.getChargingKey();
        String chargingKeyName = pccRuleImported.getChargingKeyName();

        if( Strings.isNullOrBlank(chargingKey) && Strings.isNullOrBlank(chargingKeyName) ){
            subReasons.add("ChargingKey or ChargingKeyName must be provided for PCC Rule " + BasicValidations.printIdAndName(id,pccRuleName));

        }else if ( Strings.isNullOrBlank(chargingKey)==false && Strings.isNullOrBlank(chargingKeyName)==false ){

            RatingGroupData ratingGroupData = ImportExportCRUDOperationUtil.getNotDeleted(RatingGroupData.class, chargingKey, session);

            if ( ratingGroupData==null || (ratingGroupData.getName().equals(chargingKeyName)==false) ) {
                 subReasons.add("ChargingKey: " + chargingKey + " and ChargingKeyName: " + chargingKeyName + " are not related");
            }

        }else if ( Strings.isNullOrBlank(chargingKey)==false ) {

            RatingGroupData ratingGroupData = ImportExportCRUDOperationUtil.getNotDeleted(RatingGroupData.class, chargingKey, session);
            if(ratingGroupData == null){
                subReasons.add("ChargingKey: " + chargingKey +" does not exists");
            }
            //FIXME add code for rating group exists but is not related to service type code --ishani.bhatt
        }else if ( Strings.isNullOrBlank(chargingKeyName)==false ) {

            List<RatingGroupData> ratingGroupDatas = ImportExportCRUDOperationUtil.getByName(RatingGroupData.class, chargingKeyName, session);
            if ( Collectionz.isNullOrEmpty(ratingGroupDatas) ) {

                subReasons.add("ChargingKeyName: " + chargingKeyName +" does not exists");
            }else{
                RatingGroupData ratingGroupData = ratingGroupDatas.get(0);
                pccRuleImported.setChargingKey(ratingGroupData.getId());
            }
        }
    }

    //utility methods
    public static void setDateAndStaff(PCCRuleData pccRuleImported) {
        pccRuleImported.setCreatedByStaff(pccRuleImported.getCreatedByStaff());
        pccRuleImported.setCreatedDate(new Timestamp(System.currentTimeMillis()));
        pccRuleImported.setModifiedByStaff(pccRuleImported.getCreatedByStaff());
        pccRuleImported.setModifiedDate(pccRuleImported.getCreatedDate());
    }


    public static void setDefaultValuesForQosParameter(PCCRuleData pccRuleImported) {
        if(Strings.isNullOrBlank(pccRuleImported.getMbrdlUnit())){
            pccRuleImported.setMbrdlUnit(QoSUnit.Kbps.name());
        }
        if(Strings.isNullOrBlank(pccRuleImported.getMbrulUnit())){
            pccRuleImported.setMbrulUnit(QoSUnit.Kbps.name());
        }
        if(Strings.isNullOrBlank(pccRuleImported.getGbrdlUnit())){
            pccRuleImported.setGbrdlUnit(QoSUnit.Kbps.name());
        }
        if(Strings.isNullOrBlank(pccRuleImported.getGbrulUnit())){
            pccRuleImported.setGbrdlUnit(QoSUnit.Kbps.name());
        }

        // set usage metering parameter
        if(Strings.isNullOrBlank(pccRuleImported.getSliceDownloadUnit())){
            pccRuleImported.setSliceDownloadUnit(DataUnit.MB.name());
        }
        if(Strings.isNullOrBlank(pccRuleImported.getSliceUploadUnit())){
            pccRuleImported.setSliceUploadUnit(DataUnit.MB.name());
        }
        if(Strings.isNullOrBlank(pccRuleImported.getSliceTotalUnit())){
            pccRuleImported.setSliceTotalUnit(DataUnit.MB.name());
        }
        if(Strings.isNullOrBlank(pccRuleImported.getSliceTimeUnit())){
            pccRuleImported.setSliceTimeUnit(TimeUnit.MINUTE.name());
        }

    }


    public static void validateQuotaAndQosRelatedParameters(PCCRuleData pccRuleImported, List<String> subReasons) {
        if (pccRuleImported.getMbrdl() == null && pccRuleImported.getGbrdl() == null) {
            if (pccRuleImported.getQci() < 5) {
                subReasons.add("MBRDL/GBRDL is mandatory for PCC Rule: " + pccRuleImported.getName());
            } else {
                subReasons.add(FieldValueConstants.MBRDL + " is mandatory for PCC Rule: " + pccRuleImported.getName());
            }
        }

        validateQosParameter(pccRuleImported.getMbrdl(), pccRuleImported.getMbrdlUnit(), pccRuleImported.getMbrul(), pccRuleImported.getMbrulUnit(), pccRuleImported.getGbrdl(), pccRuleImported.getGbrdlUnit(), pccRuleImported.getGbrul(), pccRuleImported.getGbrulUnit(), subReasons, pccRuleImported.getName());


        BasicValidations.validateSliceInformation(pccRuleImported.getUsageMonitoring(), pccRuleImported.getSliceTotal(), pccRuleImported.getSliceTotalUnit(), pccRuleImported.getSliceDownload(), pccRuleImported.getSliceDownloadUnit(),
                pccRuleImported.getSliceUpload(), pccRuleImported.getSliceUploadUnit(), pccRuleImported.getSliceTime(), pccRuleImported.getSliceTimeUnit(), subReasons, pccRuleImported.getName());

    }

    public static void validateQosParameter(Long mbrdl,String mbrdlUnit, Long mbrul, String mbrulUnit,Long gbrdl, String gbrdlUnit, Long gbrul,String gbrulUnit, List<String> subReasons,String entityName){
        if (mbrdl != null && BasicValidations.isPositiveNumber(mbrdl) == false) {
            subReasons.add(FieldValueConstants.MBRDL + " must be positive numeric");
        }
        BasicValidations.checkForValidQos(FieldValueConstants.MBRDL, mbrdl, mbrdlUnit, subReasons, entityName);


        if (mbrul != null && BasicValidations.isPositiveNumber(mbrul) == false) {
            subReasons.add(FieldValueConstants.MBRDL + " must be positive numeric associated with PCC Rule: " + entityName);
        }
        BasicValidations.checkForValidQos(FieldValueConstants.MBRUL, mbrul, mbrulUnit, subReasons, entityName);

        if (gbrdl != null && BasicValidations.isPositiveNumber(gbrdl) == false) {
            subReasons.add(FieldValueConstants.GBRDL + " must be positive numeric associated with PCC Rule: " + entityName);
        }
        BasicValidations.checkForValidQos(FieldValueConstants.GBRDL, gbrdl, gbrdlUnit, subReasons, entityName);

        if (gbrul != null && BasicValidations.isPositiveNumber(gbrul) == false) {
            subReasons.add(FieldValueConstants.GBRUL + " must be positive numeric associated with PCC Rule: " + entityName);
        }
        BasicValidations.checkForValidQos(FieldValueConstants.GBRUL, gbrul, gbrulUnit, subReasons, entityName);
    }


}
