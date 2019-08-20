package com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriptionws;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.nvsmx.snmp.mib.webservice.autogencode.SubscriptionWSListBoDPackagesEntryMBean;
import com.elitecore.nvsmx.snmp.mib.webservice.autogencode.TableSubscriptionWSListBoDPackagesTable;
import com.sun.management.snmp.SnmpStatusException;
import com.sun.management.snmp.agent.SnmpMib;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.io.Serializable;

public class TableSubscriptionWSListBoDPackagesTableImpl extends TableSubscriptionWSListBoDPackagesTable implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String MODULE = "SUBSCRIPTION-WS-wsListBoDPackages-STAT-TABLE";

    public TableSubscriptionWSListBoDPackagesTableImpl(SnmpMib myMib, MBeanServer server) {
        super(myMib, server);
    }

    @Override
    public synchronized void addEntry(SubscriptionWSListBoDPackagesEntryMBean entry, ObjectName name) throws SnmpStatusException {
        super.addEntry(entry, name);

        if(server != null){
            try {
                server.registerMBean(entry, name);
            } catch (Exception e) {
                LogManager.getLogger().error(MODULE, "SubscriptionWS method wsListBoDPackages ResultCode statistics entry for ( " +entry.getListBoDPackagesResultCodeName()+","+entry.getListBoDPackagesResultCode()+" ) " +
                        " not added in TableSubscriptionWSListBoDPackagesTable. Reason: " + e.getMessage());
                LogManager.getLogger().trace(MODULE, e);
            }
        }
    }
}
