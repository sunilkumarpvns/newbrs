package com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriptionws;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.nvsmx.snmp.mib.webservice.autogencode.SubscriptionWSChangeDataAddOnSubscriptionEntryMBean;
import com.elitecore.nvsmx.snmp.mib.webservice.autogencode.TableSubscriptionWSChangeDataAddOnSubscriptionTable;
import com.elitecore.nvsmx.ws.subscription.ISubscriptionWS;
import com.sun.management.snmp.SnmpStatusException;
import com.sun.management.snmp.agent.SnmpMib;

public class TableSubscriptionWSChangeDataAddOnSubscriptionTableImpl extends TableSubscriptionWSChangeDataAddOnSubscriptionTable {
    private static final long serialVersionUID = 1L;
    private static final String MODULE = "SUBSCRIPTION-WS-wsChangeDataAddOnSubscription-STAT-TABLE";

    public TableSubscriptionWSChangeDataAddOnSubscriptionTableImpl(SnmpMib myMib, MBeanServer server) {
        super(myMib, server);
    }

    @Override
    public synchronized void addEntry(SubscriptionWSChangeDataAddOnSubscriptionEntryMBean entry, ObjectName name) throws SnmpStatusException {
        super.addEntry(entry, name);

        if(server != null){
            try {
                server.registerMBean(entry, name);
            } catch (Exception e) {
                LogManager.getLogger().error(MODULE, "SubscriptionWS method " + ISubscriptionWS.WS_CHANGE_DATA_ADD_ON_SUBSCRIPTION
                        + " ResultCode statistics entry for ( "
                        + entry.getChangeDataAddOnSubscriptionResultCodeName() + "," + entry.getChangeDataAddOnSubscriptionResultCode() + " ) " +
                        " not added in TableSubscriptionWSChangeDataAddOnSubscriptionTable. Reason: " + e.getMessage());
                LogManager.getLogger().trace(MODULE, e);
            }
        }
    }
}
