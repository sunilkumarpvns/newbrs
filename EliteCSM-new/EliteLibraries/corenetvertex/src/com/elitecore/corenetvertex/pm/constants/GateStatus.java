package com.elitecore.corenetvertex.pm.constants;

import java.util.HashMap;
import java.util.Map;
import com.elitecore.corenetvertex.constants.CommonConstants;

public enum GateStatus {
	OPEN("Open", CommonConstants.GATE_STATUS_OPEN),
	CLOSE("Close", CommonConstants.GATE_STATUS_CLOSE);
	
	public final String id;
	
	public final String status;
	
	private GateStatus(String id, String status) {
		this.id = id;
		this.status = status;
	}
	
	private static final Map<String,GateStatus> map;
	
	static {
		map = new HashMap<String,GateStatus>();
		for (GateStatus gateStatus : values()) {
			map.put(gateStatus.id, gateStatus);
		}
	}
	
	public static GateStatus fromValue(String id) {
	    return map.get(id);
	}
	
	public String getVal() {
		return status;
	}
	
}
