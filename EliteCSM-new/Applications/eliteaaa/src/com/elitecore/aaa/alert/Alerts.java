/**
 * 
 */
package com.elitecore.aaa.alert;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.elitecore.core.serverx.alert.IAlertEnum;
import com.elitecore.core.serverx.alert.event.SystemAlert;
import com.elitecore.core.util.string.BoundedStringBuilder;

/**
 * @author pulin
 *
 */
public enum Alerts implements IAlertEnum{
	SERVERUP("SERVER UP", "AT000002", "1.3.6.1.4.1.21067.1.3.4","aaaServerUP"),
	SERVERDOWN("SERVER DOWN", "AT000003", "1.3.6.1.4.1.21067.1.3.12","aaaServerDown"),
	
	ALERT_MEMBER_STATUS("ALERT MEMBER STATUS", "AT000085", "1.3.6.1.4.1.21067.6.1.2","alertMemberStatus"),
	CLUSTER_MEMBER("CLUSTER MEMBER","DT00001","1.3.6.1.4.1.21067.6.2.2","member"),
	MEMBER_STATUS("MEMBER STATUS","DT00002","1.3.6.1.4.1.21067.6.2.3","memberStatus"),
	
	ALERT_INSTANCE_STATUS("ALERT INSTANCE STATUS", "AT000084", "1.3.6.1.4.1.21067.6.1.1","alertInstanceStatus"),
	INSTANCE_STATUS("INSTANCE STATUS", "DT00003", "1.3.6.1.4.1.21067.6.2.1","instanceStatus"),
	
	ALERT_MIGRATION_HEALTH("ALERT MIGRATION HEALTH", "AT000086", "1.3.6.1.4.1.21067.6.1.3","alertMigrationHealth") {
		@Override
		public String aggregateAlertMessages(List<SystemAlert> alerts) {
			return generateGenericAggregatedAlertMessage(alerts, "Migration health is");
		}
	} ,
	MIGRATION_STATUS("MIGRATION STATUS","DT00004","1.3.6.1.4.1.21067.6.2.3","migrationStatus"),
	
	
	HIGHAAARESPONSETIME("HIGH AAA RESPONSE TIME", "AT000070","1.3.6.1.4.1.21067.1.3.13","highAAAResponseTime"){
		@Override
		public String aggregateAlertMessages(List<SystemAlert> alerts) {
			int totalHighRespTime = 0;
			for (SystemAlert systemAlert : alerts) {
				totalHighRespTime += systemAlert.getAlertIntValue();
			}
			return alerts.size() + " occ. Average for AAA server instance: " + alerts.get(0).getAlertStringValue() + " is " + (totalHighRespTime / alerts.size());
		}
	},
	THREADNOTAVAILABLE("THREAD NOT AVAILABLE", "AT000069","1.3.6.1.4.1.21067.1.3.14","threadNotAvailable") {
		@Override
		public String aggregateAlertMessages(List<SystemAlert> alerts) {
			BoundedStringBuilder aggregatedAlertMessage = new BoundedStringBuilder(250);
			aggregatedAlertMessage.append("Worker Thread not available. Total occ: " + alerts.size());
			Map<String, Integer> generatorToCount = new HashMap<String, Integer>();
			for (SystemAlert alert : alerts) {
				if (generatorToCount.get(alert.getAlertGeneratorIdentity()) == null) {
					generatorToCount.put(alert.getAlertGeneratorIdentity(), 1);
				} else {
					generatorToCount.put(alert.getAlertGeneratorIdentity(), generatorToCount.get(alert.getAlertGeneratorIdentity()) + 1);
				}
			}
			
			for (Entry<String, Integer> entry : generatorToCount.entrySet()) {
				String message = ", " +  entry.getKey() + "(occ=" + entry.getValue() + ")";
				if (aggregatedAlertMessage.isAccomodable(message)) {
					aggregatedAlertMessage.append(message);
				}
			}
			return aggregatedAlertMessage.toString();
		}
	},
	
	DATABASEUP("DATABASE UP", "AT000012", "1.3.6.1.4.1.21067.1.3.3.1","databaseUP") {
		@Override
		public String aggregateAlertMessages(List<SystemAlert> alerts) {
			return generateGenericAggregatedAlertMessage(alerts, "Connection is up for");
		}
	},
	DATABASEDOWN("DATABASE DOWN", "AT000013", "1.3.6.1.4.1.21067.1.3.3.2","databaseDOWN") {
		@Override
		public String aggregateAlertMessages(List<SystemAlert> alerts) {
			return generateGenericAggregatedAlertMessage(alerts, "Connection is down for");
		}
	},
	DBQUERYTIMEOUT("DATABASE QUERY TIMEOUT", "AT000014", "1.3.6.1.4.1.21067.1.3.3.3","queryTimeout") {
		@Override
		public String aggregateAlertMessages(List<SystemAlert> alerts) {
			BoundedStringBuilder aggregatedAlertMessage = new BoundedStringBuilder(250);
			aggregatedAlertMessage.append(alerts.size() + " occ. Average DB Query timeouts are");
			Map<String, Average> generatorToTimeouts = new HashMap<String, Average>();
			for (SystemAlert alert : alerts) {
				if (generatorToTimeouts.containsKey(alert.getAlertGeneratorIdentity()) == false) {
					Average average = new Average();
					average.record(alert.getAlertIntValue());
					generatorToTimeouts.put(alert.getAlertGeneratorIdentity(), average);
				} else {
					generatorToTimeouts.get(alert.getAlertGeneratorIdentity()).record(alert.getAlertIntValue());
				}
			}
			
			for (Entry<String, Average> entry : generatorToTimeouts.entrySet()) {
				String message = ", " + entry.getKey()+ "(occ=" + entry.getValue().getCount() + ", avg=" + entry.getValue().get() + ")";
				if (aggregatedAlertMessage.indexOf(message) == -1 &&
					aggregatedAlertMessage.isAccomodable(message)) {
					aggregatedAlertMessage.append(message);
				}
			}
			return aggregatedAlertMessage.toString();
		}
	},
	DATABASEUNIQUECONSTRAINTS("DATABASE UNIQUE CONSTRAINTS", "AT000039", "1.3.6.1.4.1.21067.1.3.3.7","uniqueConstraint") {
		@Override
		public String aggregateAlertMessages(List<SystemAlert> alerts) {
			return generateGenericAggregatedAlertMessage(alerts, "Unique constraint violated in database");
		}
	},
	
	RADIUSALIVE("RADIUS ALIVE","AT000019", "1.3.6.1.4.1.21067.1.3.1.1.2","radiusESIUP") {
		@Override
		public String aggregateAlertMessages(List<SystemAlert> alerts) {
			return generateGenericAggregatedAlertMessage(alerts, "Marked Alive ESIs are");
		}
	},
	RADIUSDEAD("RADIUS DEAD", "AT000020", "1.3.6.1.4.1.21067.1.3.1.1.1","radiusESIDown") {
		@Override
		public String aggregateAlertMessages(List<SystemAlert> alerts) {
			return generateGenericAggregatedAlertMessage(alerts, "Marked Dead ESIs are");
		}
	},
	RADIUS_REQUEST_TIMEOUT("RADIUS REQUEST TIMEOUT", "AT000021","1.3.6.1.4.1.21067.1.3.1.1.3","radiusESIRequestTimeout") {
		@Override
		public String aggregateAlertMessages(List<SystemAlert> alerts) {
			return generateGenericAggregatedAlertMessage(alerts, "Timeout response received for servers");
		}
	},

	RADIUS_SERVICE_POLICY_NOT_SATISFIED("RADIUS SERVICE POLICY NOT SATISFIED", "AT000038", "1.3.6.1.4.1.21067.1.3.1.5","radiusServicePolicyNotSatisfied") {
		@Override
		public String aggregateAlertMessages(List<SystemAlert> alerts) {
			return generateGenericAggregatedAlertMessage(alerts, "No service policy satisfied for");
		}
	},

	LDAPUP("LDAP UP", "AT000025","1.3.6.1.4.1.21067.1.3.5.1","ldapUP") {
		@Override
		public String aggregateAlertMessages(List<SystemAlert> alerts) {
			return generateGenericAggregatedAlertMessage(alerts, "Connection is UP for Driver");
		}
	},
	LDAPDOWN("LDAP DOWN", "AT000026","1.3.6.1.4.1.21067.1.3.5.2","ldapDown") {
		@Override
		public String aggregateAlertMessages(List<SystemAlert> alerts) {
			return generateGenericAggregatedAlertMessage(alerts, "They are");
		}
	},
	
	INVALID_CLIENT("INVALID CLIENT", "AT000007","1.3.6.1.4.1.21067.1.3.6","invalidAAAClient") {
		@Override
		public String aggregateAlertMessages(List<SystemAlert> alerts) {
			return generateGenericAggregatedAlertMessage(alerts, "Dropping request from client(s)");
		}
	},

	UNKNOWN_USER("UNKNOWN USER", "AT000006","1.3.6.1.4.1.21067.1.3.1.3.1","unknownUser"){
		@Override
		public String aggregateAlertMessages(List<SystemAlert> alerts) {
			BoundedStringBuilder aggregateAlertMessage = new BoundedStringBuilder(250);
			aggregateAlertMessage.append(alerts.size() + " occ. Users are");
			appendUsernamesToAlertMessage(alerts, aggregateAlertMessage);
			return aggregateAlertMessage.toString();
		}

		private void appendUsernamesToAlertMessage(List<SystemAlert> alerts, BoundedStringBuilder aggregateAlertMessage) {
			for(int index=0 ; index < alerts.size() ; index++) {
				SystemAlert systemAlert = alerts.get(index);
				if(systemAlert == null) {
					continue;
				}
				
				if(aggregateAlertMessage.indexOf(systemAlert.getAlertStringValue()) == -1 && 
						aggregateAlertMessage.isAccomodable(systemAlert.getAlertStringValue())) {
					aggregateAlertMessage.append(", " + systemAlert.getAlertStringValue());
				}
			}
		}
	},
	
	CDR_STORAGE_PROBLEM("CDR STORAGE PROBLEM", "AT000009","1.3.6.1.4.1.21067.1.3.1.4.2","cdrStorageProblem") {
		@Override
		public String aggregateAlertMessages(List<SystemAlert> alerts) {
			return generateGenericAggregatedAlertMessage(alerts, "They are");
		}
	},
	
	LICENSE_EXPIRED("LICENSE EXPIRED", "AT000032", "1.3.6.1.4.1.21067.1.3.7.2","licenseExpired") {
		@Override
		public String aggregateAlertMessages(List<SystemAlert> alerts) {
			return generateGenericAggregatedAlertMessage(alerts, "License expired for");
		}
	},
	LICENSED_CPU_EXCEEDED("LICENSE CPU EXCEEDED", "AT000035", "1.3.6.1.4.1.21067.1.3.7.5","licensedCPUExceeded"),
	LICENSED_TPS_EXCEEDED("LICENSE TPS EXCEEDED", "AT000034", "1.3.6.1.4.1.21067.1.3.7.4","licensedTPSExceeded") {
		@Override
		public String aggregateAlertMessages(List<SystemAlert> alerts) {
			BoundedStringBuilder aggregatedAlertMessage = new BoundedStringBuilder(250);
			
			int min = alerts.get(0).getAlertIntValue();
			int max = min;
			int sum = 0;
			
			for (SystemAlert alert : alerts) {
				sum += alert.getAlertIntValue();
				
				if (alert.getAlertIntValue() < min) {
					min = alert.getAlertIntValue();
				}
				
				if (alert.getAlertIntValue() > max) {
					max = alert.getAlertIntValue();
				}
			}

			double avg = sum / alerts.size();
			
			aggregatedAlertMessage.append(alerts.size() + " occ. " + "Invalid License for Max TPS Supported " 
					+ alerts.get(0).getAlertStringValue() + ", min=" + min + ", max=" + max + ", avg=" + avg);
			
			return aggregatedAlertMessage.toString();
		}
	},
	LICENSED_CLIENT_EXCEEDED("LICENSE CLIENT EXCEEDED", "AT000036", "1.3.6.1.4.1.21067.1.3.7.6","licensedClientsExceeded") {
		@Override
		public String aggregateAlertMessages(List<SystemAlert> alerts) {
			return generateGenericAggregatedAlertMessage(alerts, "Invalid License for Max Supported Client");
		}
	},
	LICENSED_CONCURRENT_USER_EXCEEDED("LICENSE CONCURRENT USER EXCEEDED", "AT000037", "1.3.6.1.4.1.21067.1.3.7.7","licensedConcurrentUserExceeded") {
		@Override
		public String aggregateAlertMessages(List<SystemAlert> alerts) {
			return generateGenericAggregatedAlertMessage(alerts, "Invalid License for Max Councurrent User Supported");
		}
	},
	
	NOT_LICENSED_VENDOR("NOT LICENSE VENDOR", "AT000031","1.3.6.1.4.1.21067.1.3.7.1","notLicensedVendor") {
		@Override
		public String aggregateAlertMessages(List<SystemAlert> alerts) {
			return generateGenericAggregatedAlertMessage(alerts, "Invalid License for Vendor Client");
		}
		
	},
	NOT_LICENSED_SUPPORTED_VENDOR("NOT LICENSE SUPPORTED VENDOR", "AT000033","1.3.6.1.4.1.21067.1.3.7.3","notLicensedSupportedVendor") {
		@Override
		public String aggregateAlertMessages(List<SystemAlert> alerts) {
			return generateGenericAggregatedAlertMessage(alerts, "Invalid License for Supported Vendor");
		}
		
	},
	NFV_LICENSE_VALIDATION_FAILED("NFV LICENSE VALIDATION FAILED", "AT000079", "1.3.6.1.4.1.21067.1.3.7.8", "nfvLicenseValidationFailed") {
		@Override
		public String aggregateAlertMessages(List<SystemAlert> alerts) {
			return generateGenericAggregatedAlertMessage(alerts, "Error while validating NFV License");
		}
	},
	NFV_LICENSE_RECEIVED("NFV LICENSE RECEIVED", "AT000080", "1.3.6.1.4.1.21067.1.3.7.9", "nfvLicenseReceived") {
		@Override
		public String aggregateAlertMessages(List<SystemAlert> alerts) {
			return generateGenericAggregatedAlertMessage(alerts, "NFV license received");
		}
	},
	NFV_LICENSE_DENIED("NFV LICENSE DENIED", "AT000081", "1.3.6.1.4.1.21067.1.3.7.10", "nfvLicenseDenied") {
		@Override
		public String aggregateAlertMessages(List<SystemAlert> alerts) {
			return generateGenericAggregatedAlertMessage(alerts, "NFV license denied");
		}
	},
	NFV_LICENSE_DEREGISTERED("NFV LICENSE DEREGISTERED", "AT000082", "1.3.6.1.4.1.21067.1.3.7.11", "nfvLicenseDeregistered") {
		@Override
		public String aggregateAlertMessages(List<SystemAlert> alerts) {
			return generateGenericAggregatedAlertMessage(alerts, "NFV license deregistered");
		}
	},
	
	RADIUS_AUTH_GENERIC("RADIUS AUTH GENERIC", "AT000040"," 1.3.6.1.4.1.21067.1.3.1.3.2","authGeneric") {
		@Override
		public String aggregateAlertMessages(List<SystemAlert> alerts) {
			return generateGenericAggregatedAlertMessage(alerts, "They are");
		}
	},
	RADIUS_ACCT_GENERIC("RADIUS ACCT GENERIC", "AT000041","1.3.6.1.4.1.21067.1.3.1.4.3","acctGeneric") {
		@Override
		public String aggregateAlertMessages(List<SystemAlert> alerts) {
			return generateGenericAggregatedAlertMessage(alerts, "They are");
		}
	},
	RADIUS_DA_GENERIC("RADIUS DA GENERIC", "AT000043","1.3.6.1.4.1.21067.1.3.1.6.1","dynaAuthGeneric") {
		@Override
		public String aggregateAlertMessages(List<SystemAlert> alerts) {
			return generateGenericAggregatedAlertMessage(alerts, "They are");
		}
	},
	RADIUS_WS_GENERIC("RADIUS WS GENERIC", "AT000068","1.3.6.1.4.1.21067.1.3.1.7.1","radiusWebServiceGeneric") {
		@Override
		public String aggregateAlertMessages(List<SystemAlert> alerts) {
			return generateGenericAggregatedAlertMessage(alerts, "They are");
		}
	},
	RM_IPPOOL_GENERIC("RM IPPOOL GENERIC", "AT000052","1.3.6.1.4.1.21067.1.3.9.1.1","rmIPPoolGeneric") {
		@Override
		public String aggregateAlertMessages(List<SystemAlert> alerts) {
			return generateGenericAggregatedAlertMessage(alerts, "They are");
		}
	},
	
	RM_PPS_GENERIC("RM PPS GENERIC", "AT000054","1.3.6.1.4.1.21067.1.3.9.3.1","rmPrepaidGeneric") {
		@Override
		public String aggregateAlertMessages(List<SystemAlert> alerts) {
			return generateGenericAggregatedAlertMessage(alerts, "They are");
		}
	},
	RM_SESSION_GENERIC("RM SESSION GENERIC", "AT000053","1.3.6.1.4.1.21067.1.3.9.2.1","rmConcurrencyGeneric") {
		@Override
		public String aggregateAlertMessages(List<SystemAlert> alerts) {
			return generateGenericAggregatedAlertMessage(alerts, "They are");
		}
	},
	DATABASE_GENERIC("DATABASE GENERIC", "AT000045","1.3.6.1.4.1.21067.1.3.3.8","databaseGeneric") {
		@Override
		public String aggregateAlertMessages(List<SystemAlert> alerts) {
			return generateGenericAggregatedAlertMessage(alerts, "They are");
		}
	},
	LDAP_GENERIC("LDAP GENERIC", "AT000046","1.3.6.1.4.1.21067.1.3.5.6","ldapGeneric") {
		@Override
		public String aggregateAlertMessages(List<SystemAlert> alerts) {
			return generateGenericAggregatedAlertMessage(alerts, "They are");
		}
	},
	RADIUS_ESI_GENERIC("RADIUS ESI GENERIC", "AT000047","1.3.6.1.4.1.21067.1.3.1.1.6","radiusESIGeneric") {
		@Override
		public String aggregateAlertMessages(List<SystemAlert> alerts) {
			return generateGenericAggregatedAlertMessage(alerts, "They are");
		}
	},
	DM_NAS_GENERIC("DM NAS GENERIC", "AT000061","1.3.6.1.4.1.21067.1.3.2.1.1","diameterNASGeneric") {
		@Override
		public String aggregateAlertMessages(List<SystemAlert> alerts) {
			return generateGenericAggregatedAlertMessage(alerts, "They are");
		}
	},
	DM_DCCA_GENERIC("DM DCCA GENERIC", "AT000062","1.3.6.1.4.1.21067.1.3.2.4.1","diameterCCGeneric") {
		@Override
		public String aggregateAlertMessages(List<SystemAlert> alerts) {
			return generateGenericAggregatedAlertMessage(alerts, "They are");
		}
	},
	DM_EAP_GENERIC("DM EAP GENERIC", "AT000063","1.3.6.1.4.1.21067.1.3.2.2.1","diameterEAPGeneric") {
		@Override
		public String aggregateAlertMessages(List<SystemAlert> alerts) {
			return generateGenericAggregatedAlertMessage(alerts, "They are");
		}
	},
	DM_MIP_GENERIC("DM MIP GENERIC", "AT000064","1.3.6.1.4.1.21067.1.3.2.3.1","diameterMIPGeneric") {
		@Override
		public String aggregateAlertMessages(List<SystemAlert> alerts) {
			return generateGenericAggregatedAlertMessage(alerts, "They are");
		}
	},
	DM_WS_GENERIC("DM WS GENERIC", "AT000065","1.3.6.1.4.1.21067.1.3.2.5.1","diameterWebServiceGeneric") {
		@Override
		public String aggregateAlertMessages(List<SystemAlert> alerts) {
			return generateGenericAggregatedAlertMessage(alerts, "They are");
		}
	},
	
	DIAMETER_STACK_UP("DIAMETER STACK UP", "AT000071","1.3.6.1.4.1.21067.1.3.2.6","diameterStackUp"),
	DIAMETER_STACK_DOWN("DIAMETER STACK DOWN", "AT000072","1.3.6.1.4.1.21067.1.3.2.7","diameterStackDown"),
	DIAMETER_PEER_UP("DIAMETER PEER UP", "AT000073","1.3.6.1.4.1.21067.1.3.2.8","diameterPeerUp") {
		@Override
		public String aggregateAlertMessages(List<SystemAlert> alerts) {
			return generateGenericAggregatedAlertMessage(alerts, "Diameter Peers are");
		}
	},
	DIAMETER_PEER_DOWN("DIAMETER PEER DOWN", "AT000074","1.3.6.1.4.1.21067.1.3.2.9","diameterPeerDown") {
		@Override
		public String aggregateAlertMessages(List<SystemAlert> alerts) {
			return generateGenericAggregatedAlertMessage(alerts, "Diameter peers are");
		}
	},
	DIA_PEER_CONNECTION_REJECTED("DIA PEER CONNECTION REJECTED", "AT000076","1.3.6.1.4.1.21067.1.3.2.10","diaPeerConnectionRejected") {
		@Override
		public String aggregateAlertMessages(List<SystemAlert> alerts) {
			return generateGenericAggregatedAlertMessage(alerts, "Multiple Connection Request rejected for Peer");
		}
	},
	
	DIAMETER_STACK_HIGH_RESPONSE_TIME("DIA STACK HIGH RESPONSE TIME", "AT000077","1.3.6.1.4.1.21067.1.3.2.11","diaStackHighResponseTime"){
		/**
		 * Aggregated message for this alert will be generated as below, 
		 * N occurrence(s). Response time to peer(s): peer1(Xms),
		 * peer2(Yms), ... peerN(Zms).
		 */
		@Override
		public String aggregateAlertMessages(List<SystemAlert> alerts) {

			BoundedStringBuilder aggregateAlertMessage = new BoundedStringBuilder(1000);
			aggregateAlertMessage.append(alerts.size() + " occ.");

			Map<String, Average> peersToHRTDetail = createPeersAndHRTDetailMap(alerts);

			if (peersToHRTDetail.isEmpty() == false) {
				aggregateAlertMessage.append(" Response time to peer(s): ");
				appendkeyAndAvgResponseTimeToMessage(peersToHRTDetail, aggregateAlertMessage);
			}

			return aggregateAlertMessage.toString();
		}
		
		
	},
	
	DIAMETER_PEER_HIGH_RESPONSE_TIME("DIA PEER HIGH RESPONSE TIME", "AT000078","1.3.6.1.4.1.21067.1.3.2.12","diaPeerHighResponseTime"){
		/**
		 * Aggregated message for this alert will be generated as below, 
		 * N occurrence(s). Response time from peer(s): peer1(Xms),
		 * peer2(Yms), ... peerN(Zms).
		 */
		@Override
		public String aggregateAlertMessages(List<SystemAlert> alerts) {

			BoundedStringBuilder aggregateAlertMessage = new BoundedStringBuilder(1000);
			aggregateAlertMessage.append(alerts.size() + " occ.");

			Map<String, Average> peersToHRTDetail = createPeersAndHRTDetailMap(alerts);

			if (peersToHRTDetail.isEmpty() == false) {
				aggregateAlertMessage.append(" Response time from peer(s): ");
				appendkeyAndAvgResponseTimeToMessage(peersToHRTDetail, aggregateAlertMessage);
			}

			return aggregateAlertMessage.toString();
		}
		
	},
	
	OTHER_GENERIC("OTHER GENERIC", "AT000067","1.3.6.1.4.1.21067.1.3.10","otherGeneric") {
		@Override
		public String aggregateAlertMessages(List<SystemAlert> alerts) {
			return generateGenericAggregatedAlertMessage(alerts, "They are");
		}
	},

	AAA_ALERT_SEVERITY("AAA ALERT SEVERITY", "","1.3.6.1.4.1.21067.1.3.11","alertSeverity"),
	AAA_SERVER_INSTANCE_ID("AAA SERVER INSTANCE ID", "","1.3.6.1.4.1.21067.1.4","aaaServerInstanceId"),
	IDLE_COMMUNICATION("IDLE COMMUNICATION", "AT000075", "1.3.6.1.4.1.21067.1.3.15","idleCommunication"),
	RADIUS_ESI_HIGH_RESPONSE_TIME("RADIUS ESI HIGH RESPONSE TIME", "AT000023","1.3.6.1.4.1.21067.1.3.1.1.5","radiusESIHighResponseTime") {
		@Override
		public String aggregateAlertMessages(List<SystemAlert> alerts) {
			BoundedStringBuilder aggregateAlertMessage = new BoundedStringBuilder(250);
			
			aggregateAlertMessage.append(alerts.size() + " occ. Average for each ESI: ");
			
			Map<String, Integer> esiToAvgHighResp = calculateAverageForEachESI(alerts);
			
			appendMessageFromMap(esiToAvgHighResp, aggregateAlertMessage);
			
			return aggregateAlertMessage.toString();
		}

		private Map<String, Integer> calculateAverageForEachESI(List<SystemAlert> alerts) {
			Map<String,Integer> esiToAvgHighResp = new HashMap<String, Integer>();
			Map<String, List<SystemAlert>> esiToAlerts = new HashMap<String, List<SystemAlert>>();

			for (SystemAlert systemAlert : alerts) {
				if(!esiToAlerts.containsKey(systemAlert.getAlertStringValue())) {
					esiToAlerts.put(systemAlert.getAlertStringValue(), new ArrayList<SystemAlert>());
				}
				esiToAlerts.get(systemAlert.getAlertStringValue()).add(systemAlert);
			}
			
			for (Entry<String, List<SystemAlert>> entry : esiToAlerts.entrySet()) {
				int totalHighResp = 0;
				List<SystemAlert> alertList = entry.getValue();
				for (SystemAlert alert : alertList) {
					totalHighResp += alert.getAlertIntValue();
				}
				esiToAvgHighResp.put(entry.getKey(), (totalHighResp/alertList.size()));
			}
			return esiToAvgHighResp;
		}
	};

	private String displayName;
	public final String alertId;
	public final String oid;
	public final String name;
	
	private static final Map<String,Alerts> map;
	
	public static final Alerts[] VALUES = values();
	
	static {
		map = new HashMap<String,Alerts>();
		for (Alerts type : VALUES) {
			map.put(type.alertId, type);
		}
	}
	Alerts(String displayName, String alertId, String oid,String name) {
		this.displayName = displayName;
		this.alertId = alertId;
		this.oid = oid;
		this.name = name;
	}

	public String id() {
		return alertId;
	}

	public String getName(){
		return this.name;
	}

	public String getDisplayName() {
		return this.displayName;
	}
	public static Alerts fromAlertId(String alertId) {
		return map.get(alertId);
	}
	
	public String oid() {
		return oid;
	}
	
	public static boolean isValid(String value) {
		return map.containsKey(value);

	}
	private static String generateGenericAggregatedAlertMessage(List<SystemAlert> alerts, String explicitMessage) {
		BoundedStringBuilder aggregatedAlertMessage = new BoundedStringBuilder(250);
		aggregatedAlertMessage.append(alerts.size() + " occ. " + explicitMessage);
		int cnt = 1;
		for (SystemAlert alert : alerts) {
			if (aggregatedAlertMessage.indexOf(alert.getAlertStringValue()) == -1 &&
				aggregatedAlertMessage.isAccomodable(alert.getAlertStringValue())) {
				aggregatedAlertMessage.append(", " + cnt + "# " + alert.getAlertStringValue());
				cnt++;
			}
		}
		return aggregatedAlertMessage.toString();
	}

	@Override
	public String aggregateAlertMessages(List<SystemAlert> alerts) {
		return alerts.size() + " occ";
	}
	
	protected void appendMessageFromMap(Map<String, Integer> map, BoundedStringBuilder aggregateAlertMessage) {
		Iterator<Entry<String, Integer>> iterator = map.entrySet().iterator();
		while (iterator.hasNext()) {
			Entry<String,Integer> entry = iterator.next();
			String msg = ", " + entry.getKey() + "(" + entry.getValue() + ")";
			if(!aggregateAlertMessage.isAccomodable(msg)) {
				return;
			}
			aggregateAlertMessage.append(msg);
		}
	}
	
	/*
	 * TODO Need to move this class in elitecommons library once test cases are written for this class. 
	 * 		And also take care of the Divide by zero exception when it is moved to the elitecommons library 
	 */
	final class Average {

		private double value = 0;
		private int totalOccurrence = 0;

		public double get() {
			return value / totalOccurrence;
		}

		public void record(int value) {
			this.value += value;
			totalOccurrence++;
		}

		public int getCount() {
			return totalOccurrence;
		}
	}
	
	Map<String, Average> createPeersAndHRTDetailMap(List<SystemAlert> alerts) {

		Map<String, Average> keyToHRTDetail = new HashMap<String, Average>();
		for (SystemAlert alert : alerts) {
			if (alert.getAlertStringValue() == null) {
				continue;
			}

			if (keyToHRTDetail.containsKey(alert.getAlertStringValue()) == false) {
				keyToHRTDetail.put(alert.getAlertStringValue(), new Average());
			}

			keyToHRTDetail.get(alert.getAlertStringValue()).record(alert.getAlertIntValue());
		}
		return keyToHRTDetail;
	}

	void appendkeyAndAvgResponseTimeToMessage(Map<String, Average> peerToAvgHighResponseTime,
			BoundedStringBuilder aggregateAlertMessage) {

		int index = 0;
		for (Entry<String, Average> entry : peerToAvgHighResponseTime.entrySet()) {

			String token = entry.getKey() + "(" + entry.getValue().get() + "ms)";

			if (aggregateAlertMessage.isAccomodable(token)) {

				aggregateAlertMessage.append(token);
				if (index != peerToAvgHighResponseTime.size() - 1) {
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

}