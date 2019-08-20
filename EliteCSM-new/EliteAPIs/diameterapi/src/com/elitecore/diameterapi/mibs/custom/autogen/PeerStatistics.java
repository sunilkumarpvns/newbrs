package com.elitecore.diameterapi.mibs.custom.autogen;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling DIAMETER-STACK-MIB.
//

// java imports
//
import java.io.Serializable;

import javax.management.MBeanServer;

import com.sun.management.snmp.SnmpStatusException;
import com.sun.management.snmp.agent.SnmpMib;
// jmx imports
//
// jdmk imports
//

/**
 * The class is used for implementing the "PeerStatistics" group.
 * The group is defined with the following oid: 1.3.6.1.4.1.21067.5.102.
 */
public class PeerStatistics implements PeerStatisticsMBean, Serializable {

    /**
     * Variable for storing the value of "PeerInfoTable".
     * The variable is identified by: "1.3.6.1.4.1.21067.5.102.1".
     *
     * " Table listing information of the discovered and
     * configured Diameter peers "
     *
     */
    protected TablePeerInfoTable PeerInfoTable;

    public PeerStatistics() {
        
    }

    /**
     * Constructor for the "PeerStatistics" group.
     * If the group contains a table, the entries created through an SNMP SET will not be registered in Java DMK.
     */
    public PeerStatistics(SnmpMib myMib) {
        PeerInfoTable = new TablePeerInfoTable (myMib);
    }


    /**
     * Constructor for the "PeerStatistics" group.
     * If the group contains a table, the entries created through an SNMP SET will be AUTOMATICALLY REGISTERED in Java DMK.
     */
    public PeerStatistics(SnmpMib myMib, MBeanServer server) {
        PeerInfoTable = new TablePeerInfoTable (myMib, server);
    }

    /**
     * Access the "PeerInfoTable" variable.
     */
    public TablePeerInfoTable accessPeerInfoTable() throws SnmpStatusException {
        return PeerInfoTable;
    }

    /**
     * Access the "PeerInfoTable" variable as a bean indexed property.
     */
    public PeerInfoEntryMBean[] getPeerInfoTable() throws SnmpStatusException {
        return PeerInfoTable.getEntries();
    }

}
