package com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriberws;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.nvsmx.snmp.mib.webservice.autogencode.SubscriberWSAddSubscribersEntryMBean;
import com.elitecore.nvsmx.snmp.mib.webservice.autogencode.SubscriberWSChangeImsPackageEntryMBean;
import com.elitecore.nvsmx.snmp.mib.webservice.autogencode.TableSubscriberProvisioningWSChangeImsPackageTable;
import com.sun.management.snmp.SnmpStatusException;
import com.sun.management.snmp.agent.SnmpMib;

public class TableSubscriberProvisioningWSChangeImsPackageTableImpl extends TableSubscriberProvisioningWSChangeImsPackageTable {
    private static final long serialVersionUID = 1L;
    private static final String MODULE = "SUBSCRIBER-WS-wsChangeImsPackage-STAT-TABLE";

    public TableSubscriberProvisioningWSChangeImsPackageTableImpl(SnmpMib myMib, MBeanServer server) {
        super(myMib, server);
    }

    @Override
    public synchronized void addEntry(SubscriberWSChangeImsPackageEntryMBean entry, ObjectName name) throws SnmpStatusException {
        super.addEntry(entry, name);

        if (server != null) {
            try {
                server.registerMBean(entry, name);
            } catch (Exception e) {
                LogManager.getLogger().error(MODULE, "SubscriberProvisioningWS method wsChangeImsPackage ResultCode statistics entry for ( "
                        + entry.getChangeImsPackageResultCodeName() + "," + entry.getChangeImsPackageResultCode() + " ) " +
                        " not added in TableSubscriberProvisioningWSChangeImsPackageTable. Reason: " + e.getMessage());
                LogManager.getLogger().trace(MODULE, e);
            }
        }
    }
}
