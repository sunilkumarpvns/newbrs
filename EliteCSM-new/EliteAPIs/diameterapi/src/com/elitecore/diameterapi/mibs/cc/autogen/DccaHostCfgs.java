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
 * The class is used for implementing the "DccaHostCfgs" group.
 * The group is defined with the following oid: 1.3.6.1.2.1.4006.2.1.1.
 */
public class DccaHostCfgs implements DccaHostCfgsMBean, Serializable {

	public DccaHostCfgs() {
	}
	
    /**
     * Variable for storing the value of "DccaHostIpAddrTable".
     * The variable is identified by: "1.3.6.1.2.1.4006.2.1.1.2".
     *
     * "The table listing the Diameter
     * Credit Control host's IP Addresses."
     *
     */
    protected TableDccaHostIpAddrTable DccaHostIpAddrTable;

    /**
     * Variable for storing the value of "DccaHostID".
     * The variable is identified by: "1.3.6.1.2.1.4006.2.1.1.1".
     *
     * "The implementation identification string for
     * the Diameter software in use on the system,
     * for example; 'diameterd'"
     *
     */
    protected String DccaHostID = new String("JDMK 5.1");


    /**
     * Constructor for the "DccaHostCfgs" group.
     * If the group contains a table, the entries created through an SNMP SET will not be registered in Java DMK.
     */
    public DccaHostCfgs(SnmpMib myMib) {
        DccaHostIpAddrTable = new TableDccaHostIpAddrTable (myMib);
    }


    /**
     * Constructor for the "DccaHostCfgs" group.
     * If the group contains a table, the entries created through an SNMP SET will be AUTOMATICALLY REGISTERED in Java DMK.
     */
    public DccaHostCfgs(SnmpMib myMib, MBeanServer server) {
        DccaHostIpAddrTable = new TableDccaHostIpAddrTable (myMib, server);
    }

    /**
     * Access the "DccaHostIpAddrTable" variable.
     */
    public TableDccaHostIpAddrTable accessDccaHostIpAddrTable() throws SnmpStatusException {
        return DccaHostIpAddrTable;
    }

    /**
     * Access the "DccaHostIpAddrTable" variable as a bean indexed property.
     */
    public DccaHostIpAddrEntryMBean[] getDccaHostIpAddrTable() throws SnmpStatusException {
        return DccaHostIpAddrTable.getEntries();
    }

    /**
     * Getter for the "DccaHostID" variable.
     */
    public String getDccaHostID() throws SnmpStatusException {
        return DccaHostID;
    }

}
