package com.elitecore.diameterapi.diameter.common.routerx;

import com.elitecore.diameterapi.diameter.common.routerx.selector.PeerCommunicatorGroupSelector;

public interface SubscriberBasedRoutingTableData {

	public String getName();
	
	public PeerCommunicatorGroupSelector createSelector(RouterContext diameterRouterContext);
}
