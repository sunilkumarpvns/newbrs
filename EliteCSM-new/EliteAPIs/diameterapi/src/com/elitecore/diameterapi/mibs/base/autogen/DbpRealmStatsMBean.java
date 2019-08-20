package com.elitecore.diameterapi.mibs.base.autogen;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling DIAMETER-BASE-PROTOCOL-MIB in standard metadata mode.
//


// jmx imports
//
import com.sun.management.snmp.SnmpStatusException;

/**
 * This interface is used for representing the remote management interface for the "DbpRealmStats" MBean.
 */
public interface DbpRealmStatsMBean {

    /**
     * Access the "DbpRealmMessageRouteTable" variable.
     */
    public TableDbpRealmMessageRouteTable accessDbpRealmMessageRouteTable() throws SnmpStatusException;

}