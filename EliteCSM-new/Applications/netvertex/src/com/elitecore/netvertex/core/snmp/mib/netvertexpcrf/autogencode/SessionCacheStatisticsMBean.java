package com.elitecore.netvertex.core.snmp.mib.netvertexpcrf.autogencode;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling NETVERTEX-PCRF-MIB in standard metadata mode.
//


// jmx imports
//
import com.sun.management.snmp.SnmpStatusException;

/**
 * This interface is used for representing the remote management interface for the "SessionCacheStatistics" MBean.
 */
public interface SessionCacheStatisticsMBean {

    /**
     * Getter for the "TotalAverageLoadPanelty" variable.
     */
    public Long getTotalAverageLoadPanelty() throws SnmpStatusException;

    /**
     * Getter for the "TotalLoadCount" variable.
     */
    public Long getTotalLoadCount() throws SnmpStatusException;

    /**
     * Getter for the "TotalRequestCount" variable.
     */
    public Long getTotalRequestCount() throws SnmpStatusException;

    /**
     * Getter for the "TotalMissedCount" variable.
     */
    public Long getTotalMissedCount() throws SnmpStatusException;

    /**
     * Getter for the "TotalHitCount" variable.
     */
    public Long getTotalHitCount() throws SnmpStatusException;

    /**
     * Getter for the "TotalCacheCount" variable.
     */
    public Long getTotalCacheCount() throws SnmpStatusException;

    /**
     * Getter for the "TotalEvictionCount" variable.
     */
    public Long getTotalEvictionCount() throws SnmpStatusException;

}