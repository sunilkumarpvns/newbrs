package com.elitecore.netvertex.gateway.diameter.gy;

import java.util.HashMap;
import java.util.Map;

public enum ReportingReason {

	THRESHOLD(0),
	QHT(1),
	FINAL(2),
	QUOTA_EXHAUSTED(3),
	VALIDITY_TIME(4),
	OTHER_QUOTA_TYPE(5),
	RATING_CONDITION_CHANGE(6),
	FORCED_REAUTHORISATION(7),
	POOL_EXHAUSTED(8);

	public final int val;

	public static final Map<Integer, ReportingReason> map;
	ReportingReason(int val) {
		this.val = val;
	}
	
	static {
		map = new HashMap<>();
		
		for (ReportingReason e : ReportingReason.values()) {
			map.put(e.val, e);
		}
	}

	public static ReportingReason fromVal(int val) {
		return map.get(val);
	}
}
