/**
 *
 */
package com.elitecore.netvertex.core.alerts;

import com.elitecore.core.serverx.alert.IAlertEnum;
import com.elitecore.core.serverx.alert.event.SystemAlert;
import com.elitecore.core.util.string.BoundedStringBuilder;
import com.elitecore.diameterapi.core.stack.alert.IStackAlertEnum;
import com.elitecore.diameterapi.diameter.stack.alert.DiameterStackAlerts;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


/**
 * @author jatin
 */
public enum Alerts implements IAlertEnum {

    // Server type of Alerts

    SERVER_UP("nvServerUp", "INFO", "SERVER UP", com.elitecore.core.serverx.alert.Alerts.SERVERUP, com.elitecore.corenetvertex.core.alerts.Alerts.SERVERUP, null),
    SERVER_DOWN("nvServerDown", "INFO", "SERVER DOWN", com.elitecore.core.serverx.alert.Alerts.SERVERDOWN, com.elitecore.corenetvertex.core.alerts.Alerts.SERVERDOWN, null),

    LICENSE_EXPIRED("nvLicenseExpired", "CRITICAL", "LICENSE EXPIRED", null, com.elitecore.corenetvertex.core.alerts.Alerts.LICENSE_EXPIRED, null),
    LICENSE_EXCEEDED("nvLicenseExceed", "CRITICAL", "LICENSE EXCEEDED", null, com.elitecore.corenetvertex.core.alerts.Alerts.LICENSE_EXCEEDED, null),
    LICENSE_REQUEST_FAILED("nvLicenseRequestFailed", "CRITICAL", "LICENSE REQUEST FAILED", null, com.elitecore.corenetvertex.core.alerts.Alerts.LICENSE_REQUEST_FAILED, null),

    SERVICE_POLICY_INIT_FAILS("nvServicePolicyInitFails", "ERROR", "SERVICE POLICY INIT FAILS", null, com.elitecore.corenetvertex.core.alerts.Alerts.SERVICE_POLICY_INIT_FAILS, null),

    // PCRF Service specific Alerts\
    SPR_UP("nvSPRUp", "INFO", "SPR UP", null, com.elitecore.corenetvertex.core.alerts.Alerts.SPRUP, null) {
        @Override
        public String aggregateAlertMessages(List<SystemAlert> alerts) {
            BoundedStringBuilder aggregateAlertMessage = new BoundedStringBuilder(1000);
            aggregateAlertMessage.append((alerts.size()) + " occurrence(s). SPR are ");
            appendSPRToAlertMessage(alerts, aggregateAlertMessage);
            return aggregateAlertMessage.toString();
        }

        private void appendSPRToAlertMessage(List<SystemAlert> alerts, BoundedStringBuilder aggregateAlertMessage) {
            if (alerts.isEmpty())
                return;

            for (int index = 0, length = alerts.size(); index < length; index++) {
                SystemAlert systemAlert = alerts.get(index);

                if (aggregateAlertMessage.indexOf(systemAlert.getAlertStringValue()) == -1 &&
                        aggregateAlertMessage.isAccomodable(systemAlert.getAlertStringValue())) {
                    aggregateAlertMessage.append(systemAlert.getAlertStringValue());
                    // For not appending "," with last value
                    if (index != (alerts.size() - 1)) {
                        aggregateAlertMessage.append(",");
                    }

                }
            }
        }

    },
    SPR_DOWN("nvSPRDown", "CRITICAL", "SPR DOWN", null, com.elitecore.corenetvertex.core.alerts.Alerts.SPRDOWN, null) {
        @Override
        public String aggregateAlertMessages(List<SystemAlert> alerts) {
            BoundedStringBuilder aggregateAlertMessage = new BoundedStringBuilder(1000);
            aggregateAlertMessage.append((alerts.size()) + " occurrence(s). SPR are ");
            appendSPRToAlertMessage(alerts, aggregateAlertMessage);
            return aggregateAlertMessage.toString();
        }

        private void appendSPRToAlertMessage(List<SystemAlert> alerts, BoundedStringBuilder aggregateAlertMessage) {
            if (alerts.isEmpty())
                return;

            for (int index = 0, length = alerts.size(); index < length; index++) {
                SystemAlert systemAlert = alerts.get(index);

                if (aggregateAlertMessage.indexOf(systemAlert.getAlertStringValue()) == -1 &&
                        aggregateAlertMessage.isAccomodable(systemAlert.getAlertStringValue())) {
                    aggregateAlertMessage.append(systemAlert.getAlertStringValue());
                    if (index != (alerts.size() - 1)) {
                        aggregateAlertMessage.append(",");
                    }

                }
            }
        }

    },
    QUERY_TIMEOUT("nvQueryTimeout", "CRITICAL", "QUERY TIMEOUT", null, com.elitecore.corenetvertex.core.alerts.Alerts.QUERY_TIME_OUT, null),
    UNKNOWN_USER("nvUnknownUser", "INFO", "UNKNOWN USER", null, com.elitecore.corenetvertex.core.alerts.Alerts.UNKNOWN_USER, null) {
        @Override
        public String aggregateAlertMessages(List<SystemAlert> alerts) {
            BoundedStringBuilder aggregateAlertMessage = new BoundedStringBuilder(1000);
            aggregateAlertMessage.append((alerts.size()) + " occurrence(s). Users are ");
            appendUsernamesToAlertMessage(alerts, aggregateAlertMessage);
            return aggregateAlertMessage.toString();
        }

        private void appendUsernamesToAlertMessage(List<SystemAlert> alerts, BoundedStringBuilder aggregateAlertMessage) {
            if (alerts.isEmpty())
                return;

            for (int index = 0, length = alerts.size(); index < length; index++) {
                SystemAlert systemAlert = alerts.get(index);

                if (aggregateAlertMessage.indexOf(systemAlert.getAlertStringValue()) == -1 &&
                        aggregateAlertMessage.isAccomodable(systemAlert.getAlertStringValue())) {
                    aggregateAlertMessage.append(systemAlert.getAlertStringValue());
                    if (index != (alerts.size() - 1)) {
                        aggregateAlertMessage.append(",");
                    }

                }
            }
        }

    },
    PCRF_STARTUP_FAILED("nvPCRFServiceStartUpFailed", "ERROR", "PCRF STARTUP FAILED", null, com.elitecore.corenetvertex.core.alerts.Alerts.PCRF_STARTUP_FAILED, null),

    // Radius Gateway specific of Alerts
    RADIUS_ALIVE("nvRadiusGwAlive", "INFO", "RADIUS ALIVE", null, com.elitecore.corenetvertex.core.alerts.Alerts.RADIUS_GATEWAY_ALIVE, null),
    RADIUS_DEAD("nvRadiusGwDead", "CRITICAL", "RADIUS DEAD", null, com.elitecore.corenetvertex.core.alerts.Alerts.RADIUS_GATEWAY_DEAD, null),

    // Diameter Gateway specific of Alerts
    // See DiameterNetVertexAlertRelation.java
    DIAMETER_STACK_UP("nvDiameterStackUp", "INFO", "DIAMETER STACK UP", null, com.elitecore.corenetvertex.core.alerts.Alerts.DIAMETER_STACK_UP, DiameterStackAlerts.DIAMETER_STACK_UP),
    DIAMETER_STACK_DOWN("nvDiameterStackDown", "CRITICAL", "DIAMETER STACK DOWN", null, com.elitecore.corenetvertex.core.alerts.Alerts.DIAMETER_STACK_DOWN, DiameterStackAlerts.DIAMETER_STACK_DOWN),

    DIAMETER_PEER_UP("nvDiameterPeerUp", "INFO", "DIAMETER PEER UP", null, com.elitecore.corenetvertex.core.alerts.Alerts.DIAMETER_PEER_UP, DiameterStackAlerts.DIAMETER_PEER_UP),
    DIAMETER_PEER_DOWN("nvDiameterPeerDown", "CRITICAL", "DIAMETER PEER DOWN", null, com.elitecore.corenetvertex.core.alerts.Alerts.DIAMETER_PEER_DOWN, DiameterStackAlerts.DIAMETER_PEER_DOWN),

    DIAMETER_HIGH_RESPONSE_TIME("nvDiameterHighResponseTime", "WARN", "DIAMETER HIGH RESPONSE TIME", null, com.elitecore.corenetvertex.core.alerts.Alerts.DIAMETER_HIGH_RESPONSE_TIME, DiameterStackAlerts.DIAMETER_HIGH_RESPONSE_TIME) {
        /**
         * Aggregated message for this alert will be generated as below, N
         * occurrences. Average high response time for peers: peer1[N ms],
         * peer2[N ms] ... peerN[N ms]. where N is any positive integer.
         */
        @Override
        public String aggregateAlertMessages(List<SystemAlert> alerts) {

            BoundedStringBuilder aggregateAlertMessage = new BoundedStringBuilder(1000);
            aggregateAlertMessage.append(alerts.size() + " occurrence(s).");

            Map<String, HighResponseMessageHelper> peersToHRTDetail = createPeersAndHRTDetailMap(alerts);

            if (peersToHRTDetail.isEmpty() == false) {
                aggregateAlertMessage.append(" Response time to peer(s): ");
                appendPeersAndAvgResponseTimeToMessage(peersToHRTDetail, aggregateAlertMessage);
            }

            return aggregateAlertMessage.toString();
        }

    },
    DIAMETER_PEER_CONNECTION_REJECTED("nvDiameterPeerConnectionRejected", "ERROR", "DIAMETER PEER CONNECTION REJECTED", null, com.elitecore.corenetvertex.core.alerts.Alerts.DIAMETER_PEER_CONNECTION_REJECTED,DiameterStackAlerts.PEER_CONNECTION_REJECTED),

    DIAMETER_PEER_HIGH_RESPONSE_TIME("nvDiameterPeerHighResponseTime", "WARN", "DIAMETER PEER HIGH RESPONSE TIME", null, com.elitecore.corenetvertex.core.alerts.Alerts.DIAMETER_PEER_HIGH_RESPONSE_TIME, DiameterStackAlerts.DIAMETER_PEER_HIGH_RESPONSE_TIME) {
        /**
         * Aggregated message for this alert will be generated as below, N
         * occurrences. Average high response time for peers: peer1[N ms],
         * peer2[N ms] ... peerN[N ms]. where N is any positive integer.
         */
        @Override
        public String aggregateAlertMessages(List<SystemAlert> alerts) {

            BoundedStringBuilder aggregateAlertMessage = new BoundedStringBuilder(1000);
            aggregateAlertMessage.append(alerts.size() + " occurrence(s).");

            Map<String, HighResponseMessageHelper> peersToHRTDetail = createPeersAndHRTDetailMap(alerts);

            if (peersToHRTDetail.isEmpty() == false) {
                aggregateAlertMessage.append(" Response time from peer(s): ");
                appendPeersAndAvgResponseTimeToMessage(peersToHRTDetail, aggregateAlertMessage);
            }

            return aggregateAlertMessage.toString();
        }

    },

    DIAMETER_CC_STATISTICS_NOT_FOUND("nvDiameterCCStatisticsNotFound", "WARN", "DIAMETER CC STATISTICS NOT FOUND", null, com.elitecore.corenetvertex.core.alerts.Alerts.DIAMETER_CC_STATISTICS_NOT_FOUND, DiameterStackAlerts.CCSTATISTICSNOTFOUND),
    DIAMETER_BASE_STATISTICS_NOT_FOUND("nvDiameterBaseStatisticsNotFound", "WARN", "DIAMETER BASE STATISTICS NOT FOUND",null, com.elitecore.corenetvertex.core.alerts.Alerts.DIAMETER_BASE_STATISTICS_NOT_FOUND, DiameterStackAlerts.BASESTATISTICSNOTFOUND),


    // Notification Service specific Alerts
    EMAIL_SENDING_FAILED("nvSMSNotificationFailure", "WARN", "EMAIL SENDING FAILED", null, com.elitecore.corenetvertex.core.alerts.Alerts.EMAIL_SENDING_FAILED, null),
    SMS_SENDING_FAILED("nvEmailNotificationFailure", "WARN", "SMS SENDING FAILED", null, com.elitecore.corenetvertex.core.alerts.Alerts.SMS_SENDING_FAILED, null),
    NOTIFICATION_STARTUP_FAILED("nvNotificationServiceStartUpFailed", "ERROR", "NOTIFICATION STARTUP FAILED", null, com.elitecore.corenetvertex.core.alerts.Alerts.NOTIFICATION_STARTUP_FAILED, null),


    // Database Alerts
    DB_NO_CONNECTION("nvNoConnection", "ERROR", "DB NO CONNECTION", null, com.elitecore.corenetvertex.core.alerts.Alerts.DATABASE_CONNECTION_NOT_AVAILABLE, null),

    DB_HIGH_QUERY_RESPONSE_TIME("nvHighQueryResponseTime", "WARN", "DB HIGH QUERY RESPONSE TIME", null, com.elitecore.corenetvertex.core.alerts.Alerts.HIGH_QUERY_RESPONSE_TIME, null),

    DS_UP("nvDataSourceUp", "INFO", "DATASOURCE UP", null, com.elitecore.corenetvertex.core.alerts.Alerts.DATASOURCE_UP, null),
    DS_DOWN("nvDataSourceDown", "ERROR", "DATASOURCE DOWN", null, com.elitecore.corenetvertex.core.alerts.Alerts.DATASOURCE_DOWN, null),

    DATA_PACKAGES_CACHING_FAILS("nvDataPackageCachingFail","WARN", "DATA PACKAGE CACHING FAIL", null, com.elitecore.corenetvertex.core.alerts.Alerts.DATA_PACKAGES_CACHING_FAILS, null),
    IMS_PACKAGES_CACHING_FAILS("nvIMSPackageCachingFail","WARN", "IMS PACKAGE CACHING FAIL", null, com.elitecore.corenetvertex.core.alerts.Alerts.IMS_PACKAGES_CACHING_FAILS, null),
	
	//File Gateway alerts 
	FILE_GATEWAY_DISK_SPACE_RESOLVED ("nvFileGatewayDiskSpaceResolved",
			"INFO",
			"FILE GATEWAY SPACE RESOLVED",
			null,
			com.elitecore.corenetvertex.core.alerts.Alerts.FILE_GATEWAY_DISK_SPACE_RESOLVED,
			null),
	FILE_GATEWAY_NO_SPACE_LEFT_ON_DEVICE("nvFileGatewayNoSpaceLeftOnDevice",
			"ERROR", // verify whether it should be Critical or error level.
			"FILE GATEWAY NO SPACE LEFT ON DEVICE",
			null,
			com.elitecore.corenetvertex.core.alerts.Alerts.FILE_GATEWAY_NO_SPACE_LEFT_ON_DEVICE,
			null), 
	FILE_GATEWAY_PARSING_ERROR("nvFileGatewayParsingError",
			"WARN",
			"FILE GATEWAY PARSING ERROR",
			null,
			com.elitecore.corenetvertex.core.alerts.Alerts.FILE_GATEWAY_PARSING_ERROR,
			null), 
	FILE_GATEWAY_NO_FILE_RECEIVED("nvFileGatewayNoFileReceived",
			"WARN",
			"FILE GATEWAY NO FILE RECEIVED",
			null,
			com.elitecore.corenetvertex.core.alerts.Alerts.FILE_GATEWAY_NO_FILE_RECEIVED,
			null), 
	FILE_GATEWAY_MAX_FILES_LIMIT_REACHED("nvFileGatewayMaxFilesLimitReached",
			"WARN",
			"FILE GATEWAY MAX FILES LIMIT REACHED",
			null,
			com.elitecore.corenetvertex.core.alerts.Alerts.FILE_GATEWAY_MAX_FILES_LIMIT_REACHED,
			null);

    private static final Map<String, Alerts> map;
    private static final Map<com.elitecore.core.serverx.alert.Alerts, Alerts> coreAlertToNetVertexAlert;
    private static final Map<com.elitecore.corenetvertex.core.alerts.Alerts, Alerts> coreNetVertexAlertToNetVertexAlert;
    private static final Map<IStackAlertEnum, Alerts> stckAlertToNetVertexAlert = new HashMap<>();


    private final String alertName;
    private final String severity;
    private final com.elitecore.core.serverx.alert.Alerts coreAlert;
    private final String displayName;



    private final com.elitecore.corenetvertex.core.alerts.Alerts alert;
    private IStackAlertEnum stackAlert;

    private Alerts(String name,
                   String severity,
                   String displayName, com.elitecore.core.serverx.alert.Alerts coreAlert,
                   com.elitecore.corenetvertex.core.alerts.Alerts alert, IStackAlertEnum stackAlert) {
        this.alertName = name;
        this.severity = severity;
        this.coreAlert = coreAlert;
        this.displayName = displayName;
        this.alert = alert;
        this.stackAlert = stackAlert;
    }

    static {
        map = new HashMap<>();
        coreNetVertexAlertToNetVertexAlert = new EnumMap<>(com.elitecore.corenetvertex.core.alerts.Alerts.class);
        coreAlertToNetVertexAlert = new EnumMap<>(com.elitecore.core.serverx.alert.Alerts.class);
        for (Alerts alerts : values()) {
            map.put(alerts.id(), alerts);

            if (alerts.coreAlert != null) {
                coreAlertToNetVertexAlert.put(alerts.coreAlert, alerts);
            }

            if (alerts.stackAlert!= null) {
                stckAlertToNetVertexAlert.put(alerts.stackAlert, alerts);
            }
            coreNetVertexAlertToNetVertexAlert.put(alerts.alert, alerts);

        }

    }

    public String getSeverity() {
        return severity;

    }

    public String id() {
        return this.name();
    }

    public static Alerts fromAlertId(String alertId) {
        return map.get(alertId);
    }

    @Override
    public String getName() {
        return this.alertName;
    }

    @Override
    public String oid() {
        return "";
    }

    public static Alerts fromCoreAlert(com.elitecore.core.serverx.alert.Alerts coreAlert) {
        return coreAlertToNetVertexAlert.get(coreAlert);
    }

    public static Alerts fromCoreNetvertexAlert(com.elitecore.corenetvertex.core.alerts.Alerts coreNetvertexAlert) {
        return coreNetVertexAlertToNetVertexAlert.get(coreNetvertexAlert);
    }

    public static Alerts fromStackAlert(IStackAlertEnum stackAlert) {
        return stckAlertToNetVertexAlert.get(stackAlert);
    }

    @Override
    public String aggregateAlertMessages(List<SystemAlert> alerts) {
        return alerts.size() + " occurrence(s)";
    }

    protected Map<String, HighResponseMessageHelper> createPeersAndHRTDetailMap(List<SystemAlert> alerts) {

        Map<String, HighResponseMessageHelper> peerToHRTDetail = new HashMap<String, Alerts.HighResponseMessageHelper>();
        for (SystemAlert alert : alerts) {
            if (alert.getAlertStringValue() == null) {
                continue;
            }

            if (peerToHRTDetail.containsKey(alert.getAlertStringValue()) == false) {

                peerToHRTDetail.put(alert.getAlertStringValue(), new HighResponseMessageHelper());
            }

            peerToHRTDetail.get(alert.getAlertStringValue()).record(alert.getAlertIntValue());
        }
        return peerToHRTDetail;
    }

    protected void appendPeersAndAvgResponseTimeToMessage(Map<String, HighResponseMessageHelper> peersToHRTDetail,
                                                          BoundedStringBuilder aggregateAlertMessage) {

        int index = 0;
        for (Entry<String, HighResponseMessageHelper> entry : peersToHRTDetail.entrySet()) {

            String token = entry.getKey() + "[" + entry.getValue().average() + "ms]";

            if (aggregateAlertMessage.isAccomodable(token)) {

                aggregateAlertMessage.append(token);
                if (index != peersToHRTDetail.size() - 1) {
                    aggregateAlertMessage.append(", ");
                }
            } else {
                // This line below is to remove appended extra "," at last
                aggregateAlertMessage.delete(aggregateAlertMessage.length() - 2, aggregateAlertMessage.length());
                return;
            }
            index++;
        }

    }
    public com.elitecore.corenetvertex.core.alerts.Alerts getCoreNetvertexAlert() {
        return alert;
    }


    public String getDisplayName() {
        return displayName;
    }



    private class HighResponseMessageHelper {

        private int totalHighResponseTime = 0;
        private int totalOccurrence = 0;

        public int average() {
            return totalHighResponseTime / totalOccurrence;
        }

        public void record(int totalHighResponseTime) {
            this.totalHighResponseTime += totalHighResponseTime;
            totalOccurrence++;
        }

    }
}
