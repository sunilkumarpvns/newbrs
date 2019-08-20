package com.elitecore.netvertex.core.roaming;

import com.elitecore.netvertex.core.roaming.conf.MCCMNCRoutingConfiguration;
import org.mockito.Mockito;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import static org.mockito.Mockito.mock;

public class DummyMCCMNCRoutingConfigurationImpl implements MCCMNCRoutingConfiguration{
    private HashMap<String, MCCMNCRoutingEntry> routingEntriesByMCCMNC;
    private HashMap<String, RoutingEntry> routingEntriesByName;
    private HashMap<String, MCCMNCEntry> mccmncEntries;

    public static DummyMCCMNCRoutingConfigurationImpl spy() {
        return Mockito.spy(new DummyMCCMNCRoutingConfigurationImpl());
    }

    public DummyMCCMNCRoutingConfigurationImpl() {
        routingEntriesByMCCMNC = new HashMap<>();
        routingEntriesByName = new HashMap<>();
        mccmncEntries = new HashMap<>();
    }


    public MCCMNCRoutingEntry spyRoutingTableByMCCMNCconf(String mccmnc) {
        MCCMNCRoutingEntry mccmncRoutingEntry = mock(MCCMNCRoutingEntry.class);
        this.routingEntriesByMCCMNC.put(mccmnc, mccmncRoutingEntry);
        return mccmncRoutingEntry;
    }

    public RoutingEntry spyRoutingTableByNameconf(String mccmnc) {
        RoutingEntry routingEntry = mock(RoutingEntry.class);
        this.routingEntriesByName.put(mccmnc, routingEntry);
        return routingEntry;
    }

    public MCCMNCEntry spyMCCMNCgroupByMCCMNCconf(String mccmnc) {
        MCCMNCEntry mccmncEntry = mock(MCCMNCEntry.class);
        this.mccmncEntries.put(mccmnc, mccmncEntry);
        return mccmncEntry;
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
    public RoutingEntry getRoutingEntryByName(String name) {
        return routingEntriesByName.get(name);
    }

    @Override
    public List<RoutingEntry> getRoutingEntries() {
        return null;
    }

    @Override
    public MCCMNCEntry getMCCMNCEntry(String mccmnc) {
        return mccmncEntries.get(mccmnc);
    }
}
