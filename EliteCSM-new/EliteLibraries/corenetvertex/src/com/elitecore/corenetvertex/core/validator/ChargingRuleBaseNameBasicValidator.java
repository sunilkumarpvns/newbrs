package com.elitecore.corenetvertex.core.validator;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.DataUnit;
import com.elitecore.corenetvertex.constants.TimeUnit;
import com.elitecore.corenetvertex.pkg.chargingrulebasename.ChargingRuleBaseNameData;
import com.elitecore.corenetvertex.pkg.chargingrulebasename.ChargingRuleDataServiceTypeData;
import com.elitecore.corenetvertex.pkg.pccrule.PCCRuleData;
import com.elitecore.corenetvertex.pkg.dataservicetype.DataServiceTypeData;
import com.elitecore.corenetvertex.util.SessionProvider;
import com.elitecore.corenetvertex.util.commons.ImportExportCRUDOperationUtil;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by RAJ KIRPALSINH
 */
public class ChargingRuleBaseNameBasicValidator {

    private static final String MODULE = "CHARGING-RULE-BASE-NAME-BASIC-VALIDATOR";

    public static void validateMandatoryParameters(ChargingRuleBaseNameData chargingRuleImported, SessionProvider session, List<String> subReasons) throws Exception {

        //validate mandatory parameters of ChargingRuleBaseName
        if(Collectionz.isNullOrEmpty(chargingRuleImported.getChargingRuleDataServiceTypeDatas())){
            return ;
        }

        // validate monitoringKey, ServiceType, Slice Information
        validateServiceTypeMonitoringKeyAndSliceInformation(chargingRuleImported, session, subReasons);
    }

    public static void validateServiceTypeMonitoringKeyAndSliceInformation(ChargingRuleBaseNameData chargingRuleImported, SessionProvider session, List<String> subReasons) throws Exception {

        LogManager.getLogger().debug(MODULE,"Method called validateServiceTypeMonitoringKeyAndSliceInformation()");

        List<ChargingRuleDataServiceTypeData> chargingRuleDataServiceTypeDatas = chargingRuleImported.getChargingRuleDataServiceTypeDatas();

        Set<String> monitoringKeySet = new HashSet<String>();
        Set<String> serviceTypeSet = new HashSet<String>();

        for (ChargingRuleDataServiceTypeData outerChargingRuleDataServiceTypeData : chargingRuleDataServiceTypeDatas) {

            /* Checking for null monitoring key value */
            String monitoringKeyLocal = outerChargingRuleDataServiceTypeData.getMonitoringKey();
            if (Strings.isNullOrBlank(monitoringKeyLocal)) {
                StringBuilder builder = new StringBuilder();
                builder.append("MonitoringKey must be provided for ChargingRuleBaseName( " )
                        .append(chargingRuleImported.getId())
                        .append(CommonConstants.COMMA)
                        .append(chargingRuleImported.getName())
                        .append(" )");
                subReasons.add(builder.toString());
            } else {

                BasicValidations.validateName(monitoringKeyLocal, "MonitoringKey", subReasons);

                boolean flag =   monitoringKeySet.add(monitoringKeyLocal);
                if(flag==false ) {
                    StringBuilder builder = new StringBuilder();
                    builder.append("MonitoringKey ")
                            .append(monitoringKeyLocal)
                            .append(" already exist locally. ")
                            .append(" It must be unique. ( ")
                            .append(chargingRuleImported.getId())
                            .append(CommonConstants.COMMA)
                            .append(chargingRuleImported.getName())
                            .append(" )");
                    subReasons.add(builder.toString());
                }
            }

            DataServiceTypeData dataServiceTypeData = outerChargingRuleDataServiceTypeData.getDataServiceTypeData();
            if (dataServiceTypeData == null) {
                StringBuilder builder = new StringBuilder();
                builder.append("DataServiceType must be provided for ChargingRuleBaseName( " )
                        .append(chargingRuleImported.getId())
                        .append(CommonConstants.COMMA )
                        .append(chargingRuleImported.getName())
                        .append(" )");
                subReasons.add(builder.toString());
            } else {

                DataServiceTypeValidator validator = new DataServiceTypeValidator();
                List<String> serviceTypeSubReasons = validator.validate(dataServiceTypeData, null, null, null, session);
                if(serviceTypeSubReasons.isEmpty()==false) {
                    subReasons.addAll(serviceTypeSubReasons);
                }

                boolean flag = serviceTypeSet.add(dataServiceTypeData.getId());
                if(flag == false){
                    StringBuilder builder = new StringBuilder();
                    builder.append("DataServiceType ")
                            .append(dataServiceTypeData.getId())
                            .append(" already configured. ")
                            .append(" It must be unique. ( ")
                            .append(chargingRuleImported.getName())
                            .append(" )");
                    subReasons.add(builder.toString());
                }
            }

            BasicValidations.validateSliceInformation(true,
                    outerChargingRuleDataServiceTypeData.getSliceTotal(), outerChargingRuleDataServiceTypeData.getSliceTotalUnit(),
                    outerChargingRuleDataServiceTypeData.getSliceDownload(), outerChargingRuleDataServiceTypeData.getSliceDownloadUnit(),
                    outerChargingRuleDataServiceTypeData.getSliceUpload(), outerChargingRuleDataServiceTypeData.getSliceUploadUnit(),
                    outerChargingRuleDataServiceTypeData.getSliceTime(), outerChargingRuleDataServiceTypeData.getSliceTimeUnit(),
                    subReasons, chargingRuleImported.getName());


            if(Strings.isNullOrBlank(outerChargingRuleDataServiceTypeData.getSliceDownloadUnit())){
                outerChargingRuleDataServiceTypeData.setSliceDownloadUnit(DataUnit.MB.name());
            }
            if(Strings.isNullOrBlank(outerChargingRuleDataServiceTypeData.getSliceUploadUnit())){
                outerChargingRuleDataServiceTypeData.setSliceUploadUnit(DataUnit.MB.name());
            }
            if(Strings.isNullOrBlank(outerChargingRuleDataServiceTypeData.getSliceTotalUnit())){
                outerChargingRuleDataServiceTypeData.setSliceTotalUnit(DataUnit.MB.name());
            }
            if(Strings.isNullOrBlank(outerChargingRuleDataServiceTypeData.getSliceTimeUnit())){
                outerChargingRuleDataServiceTypeData.setSliceTimeUnit(TimeUnit.MINUTE.name());
            }

            /* Checking for monitoring key value already exist within the pcc rules or not */
            List<PCCRuleData> pccRules = ImportExportCRUDOperationUtil.get(PCCRuleData.class, "monitoringKey", outerChargingRuleDataServiceTypeData.getMonitoringKey(), session);
            if (Collectionz.isNullOrEmpty(pccRules) == false) {

                StringBuilder builder = new StringBuilder();

                for (PCCRuleData pcc : pccRules) {

                     builder.append("Monitoring key: " )
                            .append(outerChargingRuleDataServiceTypeData.getMonitoringKey() )
                            .append(" associated with ChargingRuleBaseName " )
                            .append(BasicValidations.printIdAndName(chargingRuleImported.getId(), chargingRuleImported.getName()))
                            .append("  already exists with existing PCC Rule ")
                            .append(BasicValidations.printIdAndName(pcc.getId(), pcc.getName()))
                            .append(" ");

                     subReasons.add(builder.toString());

                    break;
                }
            }

        }
    }
}
