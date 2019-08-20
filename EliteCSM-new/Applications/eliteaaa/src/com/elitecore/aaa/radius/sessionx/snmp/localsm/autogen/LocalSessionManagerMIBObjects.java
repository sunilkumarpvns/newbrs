package com.elitecore.aaa.radius.sessionx.snmp.localsm.autogen;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling LOCAL-SESSION-MANAGER-MIB.
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
 * The class is used for implementing the "LocalSessionManagerMIBObjects" group.
 * The group is defined with the following oid: 1.3.6.1.4.1.21067.1.6.1.
 */
public class LocalSessionManagerMIBObjects implements LocalSessionManagerMIBObjectsMBean, Serializable {

	public LocalSessionManagerMIBObjects() {
	}
	
    /**
     * Variable for storing the value of "LocalSessionManagerStatsTable".
     * The variable is identified by: "1.3.6.1.4.1.21067.1.6.1.1".
     *
     * "Table defines number of Session Manager"
     *
     */
    protected TableLocalSessionManagerStatsTable LocalSessionManagerStatsTable;


    /**
     * Constructor for the "LocalSessionManagerMIBObjects" group.
     * If the group contains a table, the entries created through an SNMP SET will not be registered in Java DMK.
     */
    public LocalSessionManagerMIBObjects(SnmpMib myMib) {
        LocalSessionManagerStatsTable = new TableLocalSessionManagerStatsTable (myMib);
    }


    /**
     * Constructor for the "LocalSessionManagerMIBObjects" group.
     * If the group contains a table, the entries created through an SNMP SET will be AUTOMATICALLY REGISTERED in Java DMK.
     */
    public LocalSessionManagerMIBObjects(SnmpMib myMib, MBeanServer server) {
        LocalSessionManagerStatsTable = new TableLocalSessionManagerStatsTable (myMib, server);
    }

    /**
     * Access the "LocalSessionManagerStatsTable" variable.
     */
    public TableLocalSessionManagerStatsTable accessLocalSessionManagerStatsTable() throws SnmpStatusException {
        return LocalSessionManagerStatsTable;
    }

    /**
     * Access the "LocalSessionManagerStatsTable" variable as a bean indexed property.
     */
    public LocalSessionManagerEntryMBean[] getLocalSessionManagerStatsTable() throws SnmpStatusException {
        return LocalSessionManagerStatsTable.getEntries();
    }

}
