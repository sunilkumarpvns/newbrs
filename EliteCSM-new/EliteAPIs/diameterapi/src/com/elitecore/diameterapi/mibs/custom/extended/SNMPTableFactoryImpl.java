package com.elitecore.diameterapi.mibs.custom.extended;

import com.elitecore.diameterapi.mibs.custom.autogen.TableAppStatisticsTable;
import com.sun.management.snmp.agent.SnmpMib;

import javax.management.MBeanServer;

public class SNMPTableFactoryImpl implements SNMPTableFactory{

    private MBeanServer server;

    public SNMPTableFactoryImpl(MBeanServer server) {
        this.server = server;
    }
    public TableAppStatisticsTable createAppStatisticTable(SnmpMib myMib){
       return new TableAppStatisticsTableImpl(myMib, server);
    }
}
