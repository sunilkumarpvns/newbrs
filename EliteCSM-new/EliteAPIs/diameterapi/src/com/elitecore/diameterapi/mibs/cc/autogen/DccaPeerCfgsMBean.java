package com.elitecore.diameterapi.mibs.cc.autogen;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling DIAMETER-CC-APPLICATION-MIB in standard metadata mode.
//


// jmx imports
//
import com.sun.management.snmp.SnmpStatusException;

/**
 * This interface is used for representing the remote management interface for the "DccaPeerCfgs" MBean.
 */
public interface DccaPeerCfgsMBean {

    /**
     * Access the "DccaPeerVendorTable" variable.
     */
    public TableDccaPeerVendorTable accessDccaPeerVendorTable() throws SnmpStatusException;

    /**
     * Access the "DccaPeerTable" variable.
     */
    public TableDccaPeerTable accessDccaPeerTable() throws SnmpStatusException;

}