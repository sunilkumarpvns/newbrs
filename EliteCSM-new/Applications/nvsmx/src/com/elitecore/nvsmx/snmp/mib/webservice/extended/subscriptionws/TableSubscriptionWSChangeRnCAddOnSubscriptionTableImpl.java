package com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriptionws;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.nvsmx.snmp.mib.webservice.autogencode.SubscriptionWSChangeRnCAddOnSubscriptionEntryMBean;
import com.elitecore.nvsmx.snmp.mib.webservice.autogencode.TableSubscriptionWSChangeRnCAddOnSubscriptionTable;
import com.elitecore.nvsmx.ws.subscription.ISubscriptionWS;
import com.sun.management.snmp.SnmpStatusException;
import com.sun.management.snmp.agent.SnmpMib;

public class TableSubscriptionWSChangeRnCAddOnSubscriptionTableImpl extends TableSubscriptionWSChangeRnCAddOnSubscriptionTable {
    private static final long serialVersionUID = 1L;
    private static final String MODULE = "SUBSCRIPTION-WS-wsChangeRnCAddOnSubscription-STAT-TABLE";

    public TableSubscriptionWSChangeRnCAddOnSubscriptionTableImpl(SnmpMib myMib, MBeanServer server) {
        super(myMib, server);
    }

    @Override
    public synchronized void addEntry(SubscriptionWSChangeRnCAddOnSubscriptionEntryMBean entry, ObjectName name) throws SnmpStatusException {
        super.addEntry(entry, name);

        if(server != null){
            try {
                server.registerMBean(entry, name);
            } catch (Exception e) {
                LogManager.getLogger().error(MODULE, "SubscriptionWS method " + ISubscriptionWS.WS_CHANGE_RNC_ADD_ON_SUBSCRIPTION
                        + " ResultCode statistics entry for ( "
                        + entry.getChangeRnCAddOnSubscriptionResultCodeName() + "," + entry.getChangeRnCAddOnSubscriptionResultCode() + " ) " +
                        " not added in TableSubscriptionWSChangeRnCAddOnSubscriptionTableImpl. Reason: " + e.getMessage());
                LogManager.getLogger().trace(MODULE, e);
            }
        }
    }
}
