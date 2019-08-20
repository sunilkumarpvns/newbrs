package com.elitecore.netvertex.service.pcrf.servicepolicy.handler;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Maps;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyValueConstants;
import com.elitecore.corenetvertex.pm.constants.UsageMetering;
import com.elitecore.corenetvertex.pm.pkg.ChargingRuleBaseName;
import com.elitecore.corenetvertex.pm.pkg.PCCRule;
import com.elitecore.corenetvertex.pm.pkg.SliceInformation;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.TotalBalance;
import com.elitecore.netvertex.core.NetVertexServerContext;
import com.elitecore.netvertex.pm.FinalQoSSelectionData;
import com.elitecore.netvertex.pm.QoSInformation;
import com.elitecore.netvertex.pm.QoSProfileDetail;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.usagemetering.UMLevel;
import com.elitecore.netvertex.usagemetering.UsageMonitoringInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class UMSliceProcessor {

    private static final String MODULE = "UM-SLICE-FACTORY";

    private NetVertexServerContext context;

    public UMSliceProcessor(NetVertexServerContext context) {
        this.context = context;
    }

    private void setPCCRuleLevelUMInfoList(PCRFResponse pcrfResponse, QoSInformation qoSInformation) {

        if (PCRFKeyValueConstants.PCC_LEVEL_MONITORING_NOT_SUPPORTED.val.equals(pcrfResponse.getAttribute(PCRFKeyConstants.PCC_LEVEL_MONITORING.val))) {
            if (getLogger().isDebugLogLevel())
                getLogger().debug(MODULE, "Skipping creation of PCC Rule Level usage metering. "
                        + "Reason: PCC level monitoring is not supported by gateway:"
                        + pcrfResponse.getAttribute(PCRFKeyConstants.CS_GATEWAY_NAME.val));
            return;
        }

        String resultCode = pcrfResponse.getAttribute(PCRFKeyConstants.RESULT_CODE.getVal());
        if (PCRFKeyValueConstants.RESULT_CODE_SUCCESS.val.equalsIgnoreCase(resultCode) == false) {
            if (getLogger().isDebugLogLevel())
                getLogger().debug(MODULE, "Skipping creation of PCC rule level usage metering. Reason: Result code is: " + resultCode);
            return;
        }

        Map<String, String> pccRules = pcrfResponse.getActivePccRules();
        if (Maps.isNullOrEmpty(pccRules)) {
            if (getLogger().isDebugLogLevel())
                getLogger().debug(MODULE, "Skipping creation of PCC Rule Level usage metering. Reason: No active pcc rule found");
            return;
        }

        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Creating pcc rule level usage information from Active PCCRules"
                    + " for PCRFResponse with CoreSessionId: " + pcrfResponse.getAttribute(PCRFKeyConstants.CS_CORESESSION_ID.getVal()));
        }

        List<UsageMonitoringInfo> pccRuleLevelUMInfos = new ArrayList<UsageMonitoringInfo>(pccRules.size());

        for (String pccRuleId : pccRules.keySet()) {

            PCCRule pccRule = context.getPolicyRepository().getPCCRuleById(pccRuleId);
            if (pccRule == null) {
                getLogger().warn(MODULE, "Skipping pcc rule(" + pccRuleId
                        + ")for creating pcc rule level usage information. Reason: pcc rule not found ");
                continue;
            }

            if (UsageMetering.DISABLE_QUOTA == pccRule.getUsageMetering() || pccRule.getSliceInformation() == null) {
                if (getLogger().isDebugLogLevel())
                    getLogger().debug(MODULE, "Skipping usage metering for PCCRule: " + pccRule.getName()
                            + ". Reason: Usage Metering is disabled or slice information is not available for PCCRule");
                continue;
            }

            UsageMonitoringInfo monitoringInfo = new UsageMonitoringInfo.UsageMonitoringInfoBuilder(pccRule.getMonitoringKey(), UMLevel.PCC_RULE_LEVEL, pccRule.getSliceInformation())
                    .withBalance(qoSInformation.getPccBalanceMap().get(pccRule.getName()))
                    .build();

            pccRuleLevelUMInfos.add(monitoringInfo);
        }

        if (Collectionz.isNullOrEmpty(pccRuleLevelUMInfos) == false) {
            pcrfResponse.setUsageMonitoringInfoList(pccRuleLevelUMInfos);
        }
    }


    private void setChargingRuleBaseNameUMInfoList(PCRFResponse pcrfResponse, QoSInformation qoSInformation) {

        if (PCRFKeyValueConstants.PCC_LEVEL_MONITORING_NOT_SUPPORTED.val.equals(pcrfResponse.getAttribute(PCRFKeyConstants.PCC_LEVEL_MONITORING.val))) {
            if (getLogger().isDebugLogLevel())
                getLogger().debug(MODULE, "Skipping creation of PCC Rule Level usage metering. "
                        + "Reason: PCC level monitoring is not supported by gateway:"
                        + pcrfResponse.getAttribute(PCRFKeyConstants.CS_GATEWAY_NAME.val));
            return;
        }

        String resultCode = pcrfResponse.getAttribute(PCRFKeyConstants.RESULT_CODE.getVal());
        if (PCRFKeyValueConstants.RESULT_CODE_SUCCESS.val.equalsIgnoreCase(resultCode) == false) {
            if (getLogger().isDebugLogLevel())
                getLogger().debug(MODULE, "Skipping creation of PCC rule level usage metering. Reason: Result code is: " + resultCode);
            return;
        }

        Map<String, String> activeChargingRuleBaseNames = pcrfResponse.getActiveChargingRuleBaseNames();
        if (Maps.isNullOrEmpty(activeChargingRuleBaseNames)) {
            if (getLogger().isDebugLogLevel())
                getLogger().debug(MODULE, "Skipping creation of PCC Rule Level usage metering. Reason: No active ChargingRuleBaseName found");
            return;
        }

        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Creating pcc rule level usage information from Active ChargingRuleBaseNames"
                    + " for PCRFResponse with CoreSessionId: " + pcrfResponse.getAttribute(PCRFKeyConstants.CS_CORESESSION_ID.getVal()));
        }

        for (String chargingRuleBaseNameId : activeChargingRuleBaseNames.keySet()) {


            ChargingRuleBaseName chargingRuleBaseName = context.getPolicyRepository().getChargingRuleBaseNameById(chargingRuleBaseNameId);
            if (chargingRuleBaseName == null) {
                getLogger().warn(MODULE, "Skipping ChargingRuleBaseName( " + chargingRuleBaseNameId
                        + ")for creating pcc rule level usage information. Reason: ChargingRuleBaseName not found ");
                continue;
            }

            Map<String, TotalBalance> chargingRuleBaseNameToBalanceMap = qoSInformation.getChargingRuleBaseNameToBalanceMap().get(chargingRuleBaseName.getName());

            if( chargingRuleBaseName.getMonitoringKeySliceInformationMap() != null ) {

                for(Map.Entry<String,SliceInformation> monitoringKeyToSliceInformation : chargingRuleBaseName.getMonitoringKeySliceInformationMap().entrySet() ) {

                    UsageMonitoringInfo.UsageMonitoringInfoBuilder monitoringInfoBuilder = new UsageMonitoringInfo.UsageMonitoringInfoBuilder(monitoringKeyToSliceInformation.getKey(), UMLevel.PCC_RULE_LEVEL, monitoringKeyToSliceInformation.getValue());

                    if( Maps.isNullOrEmpty(chargingRuleBaseNameToBalanceMap) == false ) {
                        monitoringInfoBuilder.withBalance(chargingRuleBaseNameToBalanceMap.get(monitoringKeyToSliceInformation.getKey()));
                    }

                    pcrfResponse.addUsageMonitoringInfo(monitoringInfoBuilder.build());
                }
            }
        }
    }

    private void setSessionLevelUMInfo(PCRFResponse pcrfResponse, FinalQoSSelectionData finalQoSSelectionData) {

        String resultCode = pcrfResponse.getAttribute(PCRFKeyConstants.RESULT_CODE.getVal());
        if (PCRFKeyValueConstants.RESULT_CODE_SUCCESS.val.equalsIgnoreCase(resultCode) == false) {
            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(MODULE, "Skipping creation of session level usage metering. Reason: Result code is: " + resultCode);
            }
            return;
        }

        QoSProfileDetail qoSProfileDetail = finalQoSSelectionData.getQosProfileDetail();

        if (qoSProfileDetail.getUsageMonitoring() == false || qoSProfileDetail.getSliceInformation() == null) {
            if (getLogger().isLogLevel(LogLevel.DEBUG))
                getLogger().debug(MODULE, "Skipping session level usage metering for Qos Profile: " + qoSProfileDetail.getUniqueName()
                        + ". Reason: Usage Metering is disabled or slice information is not available");
            return;
        }

        if (getLogger().isLogLevel(LogLevel.DEBUG)) {
            getLogger().debug(MODULE, "Creating session level Usage Information from selected qos profile : "
                    + qoSProfileDetail.getUniqueName());
        }



        // Create UM Info from selected QoS
        UsageMonitoringInfo monitoringInfo = new UsageMonitoringInfo.UsageMonitoringInfoBuilder(
                pcrfResponse.getAttribute(PCRFKeyConstants.CS_SESSION_ID.val),
                UMLevel.SESSION_LEVEL, qoSProfileDetail.getSliceInformation())
                .withBalance(finalQoSSelectionData.getQoSBalance())
                .build();

        if(monitoringInfo.getGrantedServiceUnit().getTotalOctets() > 0) {
            pcrfResponse.setAttribute(PCRFKeyConstants.SESS_GSU_TOTAL.val, String.valueOf(monitoringInfo.getGrantedServiceUnit().getTotalOctets()));
        }

        if(monitoringInfo.getGrantedServiceUnit().getOutputOctets() > 0) {
            pcrfResponse.setAttribute(PCRFKeyConstants.SESS_GSU_DOWNLOAD.val, String.valueOf(monitoringInfo.getGrantedServiceUnit().getOutputOctets()));
        }

        if(monitoringInfo.getGrantedServiceUnit().getInputOctets() > 0) {
            pcrfResponse.setAttribute(PCRFKeyConstants.SESS_GSU_UPLOAD.val, String.valueOf(monitoringInfo.getGrantedServiceUnit().getInputOctets()));
        }

        if(monitoringInfo.getGrantedServiceUnit().getTime() > 0) {
            pcrfResponse.setAttribute(PCRFKeyConstants.SESS_GSU_TIME.val, String.valueOf(monitoringInfo.getGrantedServiceUnit().getTime()));
        }

        pcrfResponse.addUsageMonitoringInfo(monitoringInfo);
    }

    public void process(PCRFResponse response, QoSInformation qosInformation, FinalQoSSelectionData finalQoSInfomationData) {

        if (PCRFKeyValueConstants.REQUEST_TYPE_TERMINATION_REQUEST.val.equals(response.getAttribute(PCRFKeyConstants.REQUEST_TYPE.getVal())) == false) {
            setPCCRuleLevelUMInfoList(response, qosInformation);
            setSessionLevelUMInfo(response, finalQoSInfomationData);
            setChargingRuleBaseNameUMInfoList(response, qosInformation);
        } else {
            if (getLogger().isDebugLogLevel())
                getLogger().debug(MODULE, "Skipping creation of granted service unit for PCRFResponse with CoreSessionId: "
                        + response.getAttribute(PCRFKeyConstants.CS_CORESESSION_ID.val) + ". Reason: Request-Type is termination request");
        }

    }
}
