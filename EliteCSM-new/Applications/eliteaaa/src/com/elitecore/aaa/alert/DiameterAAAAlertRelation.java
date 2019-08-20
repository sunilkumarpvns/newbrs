/**
 * 
 */
package com.elitecore.aaa.alert;

import java.util.HashMap;
import java.util.Map;

import com.elitecore.diameterapi.diameter.stack.alert.DiameterStackAlerts;

/**
 * @author pulin
 *
 */
public enum DiameterAAAAlertRelation {
	DIAMETER_STACK_UP("DMS000001","AT000071"),
	DIAMETER_STACK_DOWN("DMS000002","AT000072"),
	DIAMETER_PEER_UP("DMS000003","AT000073"),
	DIAMETER_PEER_DOWN("DMS000004","AT000074"),
	DIAMETER_HIGH_RESPONSE_TIME(DiameterStackAlerts.DIAMETER_HIGH_RESPONSE_TIME.alertId,
								Alerts.DIAMETER_STACK_HIGH_RESPONSE_TIME.alertId),
	DIA_CC_STATISTICS_NOT_FOUND("DMS000006", "AT000062"),
	DIA_BASE_STATISTICS_NOT_FOUND("DMS000007", "AT000067"),
	DIA_PEER_CONNECTION_REJECTED("DMS000008","AT000076"),
	DIAMETER_PEER_HIGH_RESPONSE_TIME(DiameterStackAlerts.DIAMETER_PEER_HIGH_RESPONSE_TIME.alertId, 
									Alerts.DIAMETER_PEER_HIGH_RESPONSE_TIME.alertId)
	;
	
	public final String diameterAPIAlertId;
	public final String aaaDiameterAlertId;
	private static final Map<String,DiameterAAAAlertRelation> map;
	
	public static final DiameterAAAAlertRelation[] VALUES = values();
	
	static {
		map = new HashMap<String,DiameterAAAAlertRelation>();
		for (DiameterAAAAlertRelation type : VALUES) {
			map.put(type.diameterAPIAlertId, type);
		}
	}
	DiameterAAAAlertRelation(String diameterAPIAlertId, String aaaDiameterAlertId) {
		this.diameterAPIAlertId = diameterAPIAlertId;
		this.aaaDiameterAlertId = aaaDiameterAlertId;
	}

	public String diameterAPIId() {
		return diameterAPIAlertId;
	}

	public static DiameterAAAAlertRelation fromAlertId(String alertId) {
		return map.get(alertId);
	}
	
	public String diameterAAAAlertId() {
		return aaaDiameterAlertId;
	}
	
	public static boolean isValid(int value) {
		return map.containsKey(value);
	}

}
