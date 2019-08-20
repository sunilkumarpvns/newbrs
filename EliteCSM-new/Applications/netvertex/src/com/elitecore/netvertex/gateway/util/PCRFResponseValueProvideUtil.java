package com.elitecore.netvertex.gateway.util;

import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyValueConstants;
import com.elitecore.corenetvertex.pm.pkg.PCCRule;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.IPCANQoS;

public class PCRFResponseValueProvideUtil {

    public static String fromPCCRule(String identifier, PCCRule pccRule) {

        String value = null;
        switch (PCRFKeyConstants.fromKeyConstants(identifier)) {
            case PCC_RULE_NAME:
                value = pccRule.getName();
                break;
            case PCC_RULE_PRECEDENCE:
                int precedence = pccRule.getPrecedence();
                value = precedence == 0 ? null : Integer.toString(precedence);
                break;
            case PCC_RULE_CHARGING_KEY:
                long chargingKey = pccRule.getChargingKey();
                if (chargingKey >= 0) {
                    value = Long.toString(chargingKey);
                }
                break;
            case PCC_RULE_APP_SERVICE_PROVIDER_ID:
                value = pccRule.getAppServiceProviderId();
                break;
            case PCC_RULE_SPONSOR_ID:

                value = pccRule.getSponsorIdentity();
                break;
            case PCC_RULE_SERVICE_IDENTIFIER:

                value = String.valueOf(pccRule.getServiceIdentifier());
                break;
            case PCC_RULE_USAGE_METERING:

                value = pccRule.getUsageMetering().getVal();
                break;

            case PCC_RULE_MONITORING_KEY:

                value = pccRule.getMonitoringKey();
                break;
            case PCC_RULE_QCI:

                value = pccRule.getQCI() == null ? null : pccRule.getQCI().stringVal;
                break;
            case PCC_RULE_GBRDL:

                long gbrdl = pccRule.getGBRDL();
                value = gbrdl == 0 ? null : Long.toString(gbrdl);

                break;
            case PCC_RULE_GBRUL:

                long gbrul = pccRule.getGBRUL();
                value = gbrul == 0 ? null : Long.toString(gbrul);

                break;
            case PCC_RULE_MBRDL:

                long mbrdl = pccRule.getMBRDL();
                value = mbrdl == 0 ? null : Long.toString(mbrdl);

                break;
            case PCC_RULE_MBRUL:

                long mbrul = pccRule.getMBRUL();
                value = mbrul == 0 ? null : Long.toString(mbrul);

                break;
            case PCC_RULE_FLOW_STATUS:
                value = Integer.toString(pccRule.getFlowStatus().getVal());
                break;
            case PCC_RULE_ONLINE_CHARGING:
                value = pccRule.getChargingMode().isOnlineEnabled()?  "1" : "0";
                break;

            case PCC_RULE_OFFLINE_CHARGING:
                value = pccRule.getChargingMode().isOfflineEnabled() ? "1" : "0";
                break;
            case PCC_RULE_PRIORITY_LEVEL:
                value = pccRule.getPriorityLevel() == null ? null : pccRule.getPriorityLevel().stringVal;
                break;
            case PCC_RULE_PREEMPTION_CAPABILITY:
                value = pccRule.getPeCapability() == true ?
                        PCRFKeyValueConstants.REQ_PREEMPTION_CAPABILITY_ENABLE.val
                        : PCRFKeyValueConstants.REQ_PREEMPTION_CAPABILITY_DISABLE.val;
                break;
            case PCC_RULE_PREEMPTION_VULNERABILITY:
                value = pccRule.getPeVulnerability() == true ?
                        PCRFKeyValueConstants.REQ_PREEMPTION_VULNERABILITY_ENABLE.val
                        : PCRFKeyValueConstants.REQ_PREEMPTION_VULNERABILITY_DISABLE.val;
                break;

            default:
                value = null;

        }

        return value;

    }

    public static String valueFromIpCanQoS(String identifier, IPCANQoS ipcQoS) {

        String value = null;
        switch (PCRFKeyConstants.fromKeyConstants(identifier)) {
            case IPCAN_AAMBRDL:
                if (ipcQoS != null) {
                    long aambrdl = ipcQoS.getAAMBRDLInBytes();
                    value = aambrdl == 0 ? null : Long.toString(aambrdl);
                }
                break;
            case IPCAN_AAMBRUL:
                if (ipcQoS != null) {
                    long aambrul = ipcQoS.getAAMBRULInBytes();
                    value = aambrul == 0 ? null : Long.toString(aambrul);
                }
                break;

            case IPCAN_MBRDL:
                if (ipcQoS != null) {
                    long mbrdl = ipcQoS.getMBRDLInBytes();
                    value = mbrdl == 0 ? null : Long.toString(mbrdl);
                }
                break;
            case IPCAN_MBRUL:
                if (ipcQoS != null) {
                    long mbrul = ipcQoS.getMBRULInBytes();
                    value = mbrul == 0 ? null : Long.toString(mbrul);
                }
                break;
            case IPCAN_QCI:
                if (ipcQoS != null)
                    value = ipcQoS.getQCI() == null ? null
                            : ipcQoS.getQCI().stringVal;
                break;
            case IPCAN_PRIORITY_LEVEL:
                if (ipcQoS != null) {
                    value = ipcQoS.getPriorityLevel() == null ? null : ipcQoS
                            .getPriorityLevel().stringVal;
                }
                break;
            case IPCAN_PREEMPTION_CAPABILITY:
                if (ipcQoS != null)
                    value = ipcQoS.getPreEmptionCapability() == true ? PCRFKeyValueConstants.REQ_PREEMPTION_CAPABILITY_ENABLE.val
                            : PCRFKeyValueConstants.REQ_PREEMPTION_CAPABILITY_DISABLE.val;
                break;
            case IPCAN_PREEMPTION_VULNERABILITY:
                if (ipcQoS != null)
                    value = ipcQoS.getPreEmptionVulnerability() == true ? PCRFKeyValueConstants.REQ_PREEMPTION_VULNERABILITY_ENABLE.val
                            : PCRFKeyValueConstants.REQ_PREEMPTION_VULNERABILITY_DISABLE.val;
                break;
            default:
                value = null;

        }
        return value;
    }
}
