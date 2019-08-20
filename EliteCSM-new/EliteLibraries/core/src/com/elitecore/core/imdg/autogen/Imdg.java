package com.elitecore.core.imdg.autogen;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling IMDG-MIB.
//

// java imports
//
import java.io.Serializable;

// jmx imports
//
import javax.management.MBeanServer;

import com.sun.management.snmp.SnmpStatusException;
// jdmk imports
//
import com.sun.management.snmp.agent.SnmpMib;

/**
 * The class is used for implementing the "Imdg" group.
 * The group is defined with the following oid: 1.3.6.1.4.1.21067.6.
 */
public class Imdg implements ImdgMBean, Serializable {

    /**
     * Variable for storing the value of "OwnState".
     * The variable is identified by: "1.3.6.1.4.1.21067.6.9".
     */
    protected String OwnState = new String("JDMK 5.1");

    /**
     * Variable for storing the value of "OwnPort".
     * The variable is identified by: "1.3.6.1.4.1.21067.6.8".
     */
    protected Integer OwnPort = new Integer(1);

    /**
     * Variable for storing the value of "OwnIpAddress".
     * The variable is identified by: "1.3.6.1.4.1.21067.6.7".
     */
    protected String OwnIpAddress = new String("JDMK 5.1");

    /**
     * Variable for storing the value of "ClusterState".
     * The variable is identified by: "1.3.6.1.4.1.21067.6.6".
     */
    protected String ClusterState = new String("JDMK 5.1");

    /**
     * Variable for storing the value of "ClusterMembersCount".
     * The variable is identified by: "1.3.6.1.4.1.21067.6.5".
     */
    protected Long ClusterMembersCount = new Long(1);

    /**
     * Variable for storing the value of "ClusterName".
     * The variable is identified by: "1.3.6.1.4.1.21067.6.4".
     */
    protected String ClusterName = new String("JDMK 5.1");

    /**
     * Variable for storing the value of "ClusterVersion".
     * The variable is identified by: "1.3.6.1.4.1.21067.6.3".
     */
    protected String ClusterVersion = new String("JDMK 5.1");


    public Imdg() {
	}

    /**
     * Constructor for the "Imdg" group.
     * If the group contains a table, the entries created through an SNMP SET will not be registered in Java DMK.
     */
    public Imdg(SnmpMib myMib) {
    }


    /**
     * Constructor for the "Imdg" group.
     * If the group contains a table, the entries created through an SNMP SET will be AUTOMATICALLY REGISTERED in Java DMK.
     */
    public Imdg(SnmpMib myMib, MBeanServer server) {
    }

    /**
     * Getter for the "OwnState" variable.
     */
    public String getOwnState() throws SnmpStatusException {
        return OwnState;
    }

    /**
     * Getter for the "OwnPort" variable.
     */
    public Integer getOwnPort() throws SnmpStatusException {
        return OwnPort;
    }

    /**
     * Getter for the "OwnIpAddress" variable.
     */
    public String getOwnIpAddress() throws SnmpStatusException {
        return OwnIpAddress;
    }

    /**
     * Getter for the "ClusterState" variable.
     */
    public String getClusterState() throws SnmpStatusException {
        return ClusterState;
    }

    /**
     * Getter for the "ClusterMembersCount" variable.
     */
    public Long getClusterMembersCount() throws SnmpStatusException {
        return ClusterMembersCount;
    }

    /**
     * Getter for the "ClusterName" variable.
     */
    public String getClusterName() throws SnmpStatusException {
        return ClusterName;
    }

    /**
     * Getter for the "ClusterVersion" variable.
     */
    public String getClusterVersion() throws SnmpStatusException {
        return ClusterVersion;
    }

}
