package com.elitecore.nvsmx.snmp.mib.webservice.extended.subscriptionws;

import javax.management.MBeanServer;
import javax.management.ObjectName;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.nvsmx.snmp.mib.webservice.autogencode.SubscriptionWSSubscribeAddOnProductOfferEntryMBean;
import com.elitecore.nvsmx.snmp.mib.webservice.autogencode.TableSubscriptionWSSubscribeAddOnProductOfferTable;
import com.elitecore.nvsmx.ws.subscription.ISubscriptionWS;
import com.sun.management.snmp.SnmpStatusException;
import com.sun.management.snmp.agent.SnmpMib;

public class TableSubscriptionWSSubscribeAddOnProductOfferTableImpl extends TableSubscriptionWSSubscribeAddOnProductOfferTable {
    private static final long serialVersionUID = 1L;
    private static final String MODULE = "SUBSCRIPTION-WS-wsSubscribeAddOnProductOffer-STAT-TABLE";

    public TableSubscriptionWSSubscribeAddOnProductOfferTableImpl(SnmpMib myMib, MBeanServer server) {
        super(myMib, server);
    }

    @Override
    public synchronized void addEntry(SubscriptionWSSubscribeAddOnProductOfferEntryMBean entry, ObjectName name) throws SnmpStatusException {
        super.addEntry(entry, name);

        if(server != null){
            try {
                server.registerMBean(entry, name);
            } catch (Exception e) {
                LogManager.getLogger().error(MODULE, "SubscriptionWS method " + ISubscriptionWS.WS_SUBSCRIBE_ADD_ON
                        + " ResultCode statistics entry for ( "
                        + entry.getSubscribeAddOnProductOfferResultCodeName() + "," + entry.getSubscribeAddOnProductOfferResultCode() + " ) " +
                        " not added in TableSubscriptionWSChangeDataAddOnSubscriptionTable. Reason: " + e.getMessage());
                LogManager.getLogger().trace(MODULE, e);
            }
        }
    }
}
