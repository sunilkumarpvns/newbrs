package com.elitecore.diameterapi.diameter.common.routerx.msisdn;

import java.util.List;

import com.elitecore.commons.collections.Trie;
import com.elitecore.diameterapi.diameter.common.routerx.SubscriberBasedRoutingTableData;

public interface MsisdnBasedRoutingTableData  extends SubscriberBasedRoutingTableData {

	public String getMsisdnIdentityAttributeStr();
	
	public int getMsisdnLength();
	
	public String getMcc();
	
	public List<? extends MsisdnBasedRouteEntryData> getEntries();
	
	public List<String> getMsisdnIdentityAttributes();

	public Trie<MsisdnBasedRouteEntryData> trie();


}
