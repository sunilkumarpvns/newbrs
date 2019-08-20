/**
 * 
 */
package com.elitecore.diameterapi.diameter.stack.alert;

import java.util.HashMap;
import java.util.Map;

import com.elitecore.diameterapi.core.stack.alert.IStackAlertEnum;

/**
 * @author pulin
 *
 */
public enum DiameterStackAlerts implements IStackAlertEnum{

	DIAMETER_STACK_UP("DMS000001"),
	DIAMETER_STACK_DOWN("DMS000002"),
	DIAMETER_PEER_UP("DMS000003"),
	DIAMETER_PEER_DOWN("DMS000004"),
	DIAMETER_HIGH_RESPONSE_TIME("DMS000005"), 
	CCSTATISTICSNOTFOUND("DMS000006"),
	BASESTATISTICSNOTFOUND("DMS000007"),
	PEER_CONNECTION_REJECTED("DMS000008"),
	DIAMETER_PEER_HIGH_RESPONSE_TIME("DMS000009"),
	;
	
	public final String alertId;
	private static final Map<String,DiameterStackAlerts> map;
	
	public static final DiameterStackAlerts[] VALUES = values();
	
	static {
		map = new HashMap<String,DiameterStackAlerts>();
		for (DiameterStackAlerts type : VALUES) {
			map.put(type.alertId, type);
		}
	}

	DiameterStackAlerts(String oid) {
		this.alertId = oid;
	}

	public static DiameterStackAlerts fromAlertId(String alertId) {
		return map.get(alertId);
	}
	
	public static boolean isValid(int value) {
		return map.containsKey(value);
	}

	@Override
	public String id() {
		return alertId;
	}
}
