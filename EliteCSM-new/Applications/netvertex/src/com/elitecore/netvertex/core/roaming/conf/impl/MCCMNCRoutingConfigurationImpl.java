package com.elitecore.netvertex.core.roaming.conf.impl;

import com.elitecore.netvertex.core.roaming.MCCMNCEntry;
import com.elitecore.netvertex.core.roaming.MCCMNCRoutingEntry;
import com.elitecore.netvertex.core.roaming.RoutingEntry;
import com.elitecore.netvertex.core.roaming.conf.MCCMNCRoutingConfiguration;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public class MCCMNCRoutingConfigurationImpl implements MCCMNCRoutingConfiguration {

    private Map<String, MCCMNCRoutingEntry> routingEntriesByMCCMNC;
    private Map<String, RoutingEntry> routingEntriesByName;
    private Map<String, MCCMNCEntry> mccmncEntries;
    private List<RoutingEntry> routingEntries;

    public MCCMNCRoutingConfigurationImpl(Map<String, MCCMNCRoutingEntry> routingEntriesByMCCMNC,
                                          Map<String, RoutingEntry> routingEntriesByName,
                                          Map<String, MCCMNCEntry> mccmncEntries,
                                          List<RoutingEntry> routingEntries) {
        this.routingEntriesByMCCMNC = routingEntriesByMCCMNC;
        this.routingEntriesByName = routingEntriesByName;
        this.mccmncEntries = mccmncEntries;
        this.routingEntries = routingEntries;
    }

    @Override
    public Collection<String> getMccMncs() {
        return routingEntriesByMCCMNC.keySet();
    }

    @Override
    public Collection<String> getRoutingEntryNames() {
        return routingEntriesByName.keySet();
    }

    @Override
    public MCCMNCRoutingEntry getRoutingEntryByMCCMNC(String mccmnc) {
        return routingEntriesByMCCMNC.get(mccmnc);
    }

    @Override
    public List<RoutingEntry> getRoutingEntries() {
        return routingEntries;
    }

    @Override
    public MCCMNCEntry getMCCMNCEntry(String mccmnc) {
        return mccmncEntries.get(mccmnc);
    }

    @Override
    public RoutingEntry getRoutingEntryByName(String name) {
        return routingEntriesByName.get(name);
    }
}