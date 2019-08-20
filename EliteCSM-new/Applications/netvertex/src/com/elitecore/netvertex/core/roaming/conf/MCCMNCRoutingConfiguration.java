package com.elitecore.netvertex.core.roaming.conf;

import java.util.Collection;
import java.util.List;

import com.elitecore.netvertex.core.roaming.MCCMNCEntry;
import com.elitecore.netvertex.core.roaming.MCCMNCRoutingEntry;
import com.elitecore.netvertex.core.roaming.RoutingEntry;

public interface  MCCMNCRoutingConfiguration{


	Collection<String> getMccMncs();
	Collection<String> getRoutingEntryNames();
	MCCMNCRoutingEntry getRoutingEntryByMCCMNC(String mccmnc);
	RoutingEntry getRoutingEntryByName(String name);
	List<RoutingEntry> getRoutingEntries();
	MCCMNCEntry getMCCMNCEntry(String mccmnc);
}