package com.elitecore.diameterapi.mibs.cc.autogen;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling DIAMETER-CC-APPLICATION-MIB.
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
 * The class is used for implementing the "DccaPeerCfgs" group.
 * The group is defined with the following oid: 1.3.6.1.2.1.4006.2.1.2.
 */
public class DccaPeerCfgs implements DccaPeerCfgsMBean, Serializable {

	public DccaPeerCfgs() {
	}
	
    /**
     * Variable for storing the value of "DccaPeerVendorTable".
     * The variable is identified by: "1.3.6.1.2.1.4006.2.1.2.2".
     *
     * "The table listing the Vendor IDs
     * supported by the peer."
     *
     */
    protected TableDccaPeerVendorTable DccaPeerVendorTable;

    /**
     * Variable for storing the value of "DccaPeerTable".
     * The variable is identified by: "1.3.6.1.2.1.4006.2.1.2.1".
     *
     * "The table listing information regarding
     * the discovered or configured Diameter
     * Credit Control peers."
     *
     */
    protected TableDccaPeerTable DccaPeerTable;


    /**
     * Constructor for the "DccaPeerCfgs" group.
     * If the group contains a table, the entries created through an SNMP SET will not be registered in Java DMK.
     */
    public DccaPeerCfgs(SnmpMib myMib) {
        DccaPeerVendorTable = new TableDccaPeerVendorTable (myMib);
        DccaPeerTable = new TableDccaPeerTable (myMib);
    }


    /**
     * Constructor for the "DccaPeerCfgs" group.
     * If the group contains a table, the entries created through an SNMP SET will be AUTOMATICALLY REGISTERED in Java DMK.
     */
    public DccaPeerCfgs(SnmpMib myMib, MBeanServer server) {
        DccaPeerVendorTable = new TableDccaPeerVendorTable (myMib, server);
        DccaPeerTable = new TableDccaPeerTable (myMib, server);
    }

    /**
     * Access the "DccaPeerVendorTable" variable.
     */
    public TableDccaPeerVendorTable accessDccaPeerVendorTable() throws SnmpStatusException {
        return DccaPeerVendorTable;
    }

    /**
     * Access the "DccaPeerVendorTable" variable as a bean indexed property.
     */
    public DccaPeerVendorEntryMBean[] getDccaPeerVendorTable() throws SnmpStatusException {
        return DccaPeerVendorTable.getEntries();
    }

    /**
     * Access the "DccaPeerTable" variable.
     */
    public TableDccaPeerTable accessDccaPeerTable() throws SnmpStatusException {
        return DccaPeerTable;
    }

    /**
     * Access the "DccaPeerTable" variable as a bean indexed property.
     */
    public DccaPeerEntryMBean[] getDccaPeerTable() throws SnmpStatusException {
        return DccaPeerTable.getEntries();
    }

}
