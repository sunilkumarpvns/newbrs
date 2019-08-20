package com.elitecore.diameterapi.mibs.custom.extended;

import com.elitecore.diameterapi.mibs.custom.autogen.TableAppStatisticsTable;
import com.sun.management.snmp.SnmpStatusException;
import com.sun.management.snmp.agent.SnmpMib;

public class DummySNMPTableFactoryImpl implements SNMPTableFactory{
    private TableAppStatisticsTableImpl tableAppStatisticsTable;

    public static DummySNMPTableFactoryImpl create() throws SnmpStatusException {

        DummySNMPTableFactoryImpl dummySNMPTableFactory = new DummySNMPTableFactoryImpl();
        TableAppStatisticsTableImpl tableAppStatisticsTable = MockTableAppStatisticsTableImpl.create();
        dummySNMPTableFactory.setTableAppStatisticsTable(tableAppStatisticsTable);

        return dummySNMPTableFactory;
    }

    @Override
    public TableAppStatisticsTable createAppStatisticTable(SnmpMib myMib) {
        return tableAppStatisticsTable;
    }

    public TableAppStatisticsTableImpl getTableAppStatisticsTable() {
        return tableAppStatisticsTable;
    }

    public void setTableAppStatisticsTable(TableAppStatisticsTableImpl tableAppStatisticsTable) {
        this.tableAppStatisticsTable = tableAppStatisticsTable;
    }
}
