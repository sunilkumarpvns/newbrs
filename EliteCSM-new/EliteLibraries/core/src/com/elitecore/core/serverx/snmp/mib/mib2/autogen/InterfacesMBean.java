package com.elitecore.core.serverx.snmp.mib.mib2.autogen;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling RFC1213-MIB in standard metadata mode.
//


// jmx imports
//
import com.sun.management.snmp.SnmpStatusException;

/**
 * This interface is used for representing the remote management interface for the "Interfaces" MBean.
 */
public interface InterfacesMBean {

    /**
     * Access the "IfTable" variable.
     */
    public TableIfTable accessIfTable() throws SnmpStatusException;

    /**
     * Getter for the "IfNumber" variable.
     */
    public Integer getIfNumber() throws SnmpStatusException;

}
