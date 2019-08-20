package com.elitecore.core.serverx.alert;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.elitecore.core.serverx.alert.event.SystemAlert;

public enum Alerts implements IAlertEnum{
	
	SERVERUP("SERVER UP", "AT000002", "1.3.6.1.4.1.21067.1.3.4"),
	SERVERDOWN("SERVER DOWN", "AT000003", "1.3.6.1.4.1.21067.1.3.12"),
	THREADNOTAVAILABLE("THREAD NOT AVAILABLE", "AT000069","1.3.6.1.4.1.21067.1.3.14"),
	
	DATABASEUP("DATABASE UP", "AT000012", "1.3.6.1.4.1.21067.1.3.3.1"),
	DATABASEDOWN("DATABASE DOWN", "AT000013", "1.3.6.1.4.1.21067.1.3.3.2"),
	DBQUERYTIMEOUT("DATABASE QUERY TIMEOUT", "AT000014", "1.3.6.1.4.1.21067.1.3.3.3"),
	DATABASEUNIQUECONSTRAINTS("DATABASE UNIQUE CONSTRAINTS", "AT000039", "1.3.6.1.4.1.21067.1.3.3.7"),

	LDAPUP("LDAP UP", "AT000025","1.3.6.1.4.1.21067.1.3.5.1"),
	LDAPDOWN("LDAP DOWN", "AT000026","1.3.6.1.4.1.21067.1.3.5.2"),
	
	INVALID_CLIENT("INVALID CLIENT", "AT000007","1.3.6.1.4.1.21067.1.3.6"),
	UNKNOWN_USER("UNKNOWN USER", "AT000006","1.3.6.1.4.1.21067.1.3.1.3.1"),
	
	CDR_STORAGE_PROBLEM("CDR STORAGE PROBLEM", "AT000009","1.3.6.1.4.1.21067.1.3.1.4.2"),
	
	DATABASE_GENERIC("DATABASE GENERIC", "AT000045","1.3.6.1.4.1.21067.1.3.3.8"),
	LDAP_GENERIC("LDAP GENERIC", "AT000046","1.3.6.1.4.1.21067.1.3.5.6"),
	OTHER_GENERIC("OTHER GENERIC", "AT000067","1.3.6.1.4.1.21067.1.3.10"),
	
	ALERT_MEMBER_STATUS("ALERT MEMBER STATUS", "AT000085", "1.3.6.1.4.1.21067.6.1.2"),
	CLUSTER_MEMBER("CLUSTER MEMBER","DT00001","1.3.6.1.4.1.21067.6.2.2"),
	MEMBER_STATUS("MEMBER STATUS","DT00002","1.3.6.1.4.1.21067.6.2.3"),
	
	ALERT_INSTANCE_STATUS("ALERT INSTANCE STATUS", "AT000084", "1.3.6.1.4.1.21067.6.1.1"),
	INSTANCE_STATUS("INSTANCE STATUS", "DT00003", "1.3.6.1.4.1.21067.6.2.1"),
	
	ALERT_MIGRATION_HEALTH("ALERT MIGRATION HEALTH", "AT000086", "1.3.6.1.4.1.21067.6.1.3")
	,
	MIGRATION_STATUS("MIGRATION STATUS","DT00004","1.3.6.1.4.1.21067.6.2.3")
	;
	
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
	Alerts(String name, String alertId, String oid) {
		this.name = name;
		this.alertId = alertId;
		this.oid = oid;
	}

	public String id() {
		return alertId;
	}

	public String getName(){
		return this.name;
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

	@Override
	public String aggregateAlertMessages(List<SystemAlert> alerts) {
		return alerts.size() + " occurances";
	}
}
