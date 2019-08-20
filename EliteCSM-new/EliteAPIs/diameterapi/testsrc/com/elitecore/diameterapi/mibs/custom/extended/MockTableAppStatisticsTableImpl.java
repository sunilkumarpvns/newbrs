package com.elitecore.diameterapi.mibs.custom.extended;

import com.sun.management.snmp.SnmpOid;
import com.sun.management.snmp.SnmpStatusException;
import com.sun.management.snmp.agent.SnmpMib;
import org.mockito.Mockito;

import javax.management.MBeanServer;

import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.mock;

public class MockTableAppStatisticsTableImpl extends  TableAppStatisticsTableImpl{

    public MockTableAppStatisticsTableImpl(SnmpMib myMib, MBeanServer server) {
        super(myMib, server);
    }

    public static TableAppStatisticsTableImpl create() throws SnmpStatusException {
        TableAppStatisticsTableImpl tableAppStatisticsTable = mock(TableAppStatisticsTableImpl.class);
        Mockito.doReturn(mock(SnmpOid.class)).when(tableAppStatisticsTable).buildOidFromIndexVal(anyLong());

        return tableAppStatisticsTable;
    }

}
