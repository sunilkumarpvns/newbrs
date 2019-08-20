package com.elitecore.aaa.diameter.conf;

import java.util.List;

import com.elitecore.diameterapi.diameter.common.routerx.imsi.ImsiBasedRoutingTableData;

public interface ImsiBasedRoutingTableConfiguration {
	
	List<? extends ImsiBasedRoutingTableData> getImsiTables();
	
	ImsiBasedRoutingTableData getImsiBasedRoutingTableDataByName(String name);
}
