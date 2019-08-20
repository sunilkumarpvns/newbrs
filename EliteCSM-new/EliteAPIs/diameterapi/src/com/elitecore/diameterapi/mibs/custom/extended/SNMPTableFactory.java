package com.elitecore.diameterapi.mibs.custom.extended;

import com.elitecore.diameterapi.mibs.custom.autogen.TableAppStatisticsTable;
import com.sun.management.snmp.agent.SnmpMib;

public interface SNMPTableFactory {
    public TableAppStatisticsTable createAppStatisticTable(SnmpMib myMib);
}
