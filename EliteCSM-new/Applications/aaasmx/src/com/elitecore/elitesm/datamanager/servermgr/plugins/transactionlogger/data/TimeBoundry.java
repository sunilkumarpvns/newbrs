package com.elitecore.elitesm.datamanager.servermgr.plugins.transactionlogger.data;

import java.util.HashMap;
import java.util.Map;

public enum TimeBoundry {

	_1_MINUTE(1l,"1 Min"), 
	_2_MINUTE(2l,"2 Min"), 
	_3_MINUTE(3l,"3 Min"), 
	_5_MINUTE(5l,"5 Min"),
	_10_MINUTE(10l,"10 Min"),
	_15_MINUTE(15l,"15 Min"),
	_20_MINUTE(20l,"20 Min"),
	_30_MINUTE(30l,"30 Min"),
	HOURLY(60l,"Hourly"),
	DAILY(1440l,"Daily");
	
	public String name;
	public Long id;
	
	private static final Map<Long,TimeBoundry> map;
	public static final TimeBoundry[] VALUES = values();
	
	static {
		map = new HashMap<Long,TimeBoundry>();
		for (TimeBoundry type : VALUES) {
			map.put(type.id, type);
		}
	}
	
	private TimeBoundry(Long id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public static TimeBoundry fromPacketTypeMethods(Long id) {
		return map.get(id);
	}
}
