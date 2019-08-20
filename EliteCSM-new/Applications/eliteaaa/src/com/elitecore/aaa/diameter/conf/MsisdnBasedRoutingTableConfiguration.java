package com.elitecore.aaa.diameter.conf;

import java.util.List;

import com.elitecore.diameterapi.diameter.common.routerx.msisdn.MsisdnBasedRoutingTableData;

public interface MsisdnBasedRoutingTableConfiguration {
	
	public List<? extends MsisdnBasedRoutingTableData> getMsisdnTables();
	
	public MsisdnBasedRoutingTableData getMsisdnBasedRoutingTableDataByName(String name);
}
