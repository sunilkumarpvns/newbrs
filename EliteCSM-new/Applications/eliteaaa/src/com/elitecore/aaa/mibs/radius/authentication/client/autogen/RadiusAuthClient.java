package com.elitecore.aaa.mibs.radius.authentication.client.autogen;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling RADIUS-AUTH-CLIENT-MIB.
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
 * The class is used for implementing the "RadiusAuthClient" group.
 * The group is defined with the following oid: 1.3.6.1.2.1.67.1.2.1.1.
 */
public class RadiusAuthClient implements RadiusAuthClientMBean, Serializable {

    /**
     * Variable for storing the value of "RadiusAuthServerTable".
     * The variable is identified by: "1.3.6.1.2.1.67.1.2.1.1.3".
     *
     * "The (conceptual) table listing the RADIUS authentication
     * servers with which the client shares a secret."
     *
     */
    protected TableRadiusAuthServerTable RadiusAuthServerTable;

    /**
     * Variable for storing the value of "RadiusAuthClientIdentifier".
     * The variable is identified by: "1.3.6.1.2.1.67.1.2.1.1.2".
     *
     * "The NAS-Identifier of the RADIUS authentication client.
     * This is not necessarily the same as sysName in MIB II."
     *
     */
    protected String RadiusAuthClientIdentifier = new String("JDMK 5.1");

    /**
     * Variable for storing the value of "RadiusAuthClientInvalidServerAddresses".
     * The variable is identified by: "1.3.6.1.2.1.67.1.2.1.1.1".
     *
     * "The number of RADIUS Access-Response packets
     * received from unknown addresses."
     *
     */
    protected Long RadiusAuthClientInvalidServerAddresses = new Long(1);


    /**
     * Constructor for the "RadiusAuthClient" group.
     * If the group contains a table, the entries created through an SNMP SET will not be registered in Java DMK.
     */
    public RadiusAuthClient(SnmpMib myMib) {
        RadiusAuthServerTable = new TableRadiusAuthServerTable (myMib);
    }


    /**
     * Constructor for the "RadiusAuthClient" group.
     * If the group contains a table, the entries created through an SNMP SET will be AUTOMATICALLY REGISTERED in Java DMK.
     */
    public RadiusAuthClient(SnmpMib myMib, MBeanServer server) {
        RadiusAuthServerTable = new TableRadiusAuthServerTable (myMib, server);
    }

    public RadiusAuthClient() {
	}
    
    /**
     * Access the "RadiusAuthServerTable" variable.
     */
    public TableRadiusAuthServerTable accessRadiusAuthServerTable() throws SnmpStatusException {
        return RadiusAuthServerTable;
    }

    /**
     * Access the "RadiusAuthServerTable" variable as a bean indexed property.
     */
    public RadiusAuthServerEntryMBean[] getRadiusAuthServerTable() throws SnmpStatusException {
        return RadiusAuthServerTable.getEntries();
    }

    /**
     * Getter for the "RadiusAuthClientIdentifier" variable.
     */
    public String getRadiusAuthClientIdentifier() throws SnmpStatusException {
        return RadiusAuthClientIdentifier;
    }

    /**
     * Getter for the "RadiusAuthClientInvalidServerAddresses" variable.
     */
    public Long getRadiusAuthClientInvalidServerAddresses() throws SnmpStatusException {
        return RadiusAuthClientInvalidServerAddresses;
    }

}
