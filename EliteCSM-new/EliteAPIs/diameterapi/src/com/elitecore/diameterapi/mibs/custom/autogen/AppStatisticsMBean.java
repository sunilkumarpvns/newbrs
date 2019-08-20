package com.elitecore.diameterapi.mibs.custom.autogen;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling DIAMETER-STACK-MIB in standard metadata mode.
//


// jmx imports
//
import com.sun.management.snmp.SnmpStatusException;

/**
 * This interface is used for representing the remote management interface for the "AppStatistics" MBean.
 */
public interface AppStatisticsMBean {

    /**
     * Access the "AppStatisticsTable" variable.
     */
    public TableAppStatisticsTable accessAppStatisticsTable() throws SnmpStatusException;

}
