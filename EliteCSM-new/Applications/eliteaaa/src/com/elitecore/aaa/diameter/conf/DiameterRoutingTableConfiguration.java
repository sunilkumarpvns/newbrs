package com.elitecore.aaa.diameter.conf;

import java.util.List;

import com.elitecore.aaa.diameter.conf.impl.RoutingEntryDataImpl;
import com.elitecore.diameterapi.core.stack.constant.OverloadAction;

public interface DiameterRoutingTableConfiguration {
	public List<RoutingEntryDataImpl> getRoutingEntryDataList();
	public String getTableName();
	public OverloadAction getOverloadAction();
	public int getResultCode();
}
