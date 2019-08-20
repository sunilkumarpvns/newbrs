package com.elitecore.diameterapi.diameter.common.routerx.imsi;

import java.util.List;

import com.elitecore.commons.collections.Trie;
import com.elitecore.diameterapi.diameter.common.routerx.SubscriberBasedRoutingTableData;

public interface ImsiBasedRoutingTableData extends SubscriberBasedRoutingTableData {

	public String getImsiIdentityAttributeStr();
	
	public List<String> getImsiIdentityAttributes();

	public List<? extends ImsiBasedRouteEntryData> getEntries();

	Trie<ImsiBasedRouteEntryData> trie();
}
