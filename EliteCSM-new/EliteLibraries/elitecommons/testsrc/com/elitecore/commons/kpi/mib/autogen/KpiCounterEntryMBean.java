package com.elitecore.commons.kpi.mib.autogen;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling TEST-MIB in standard metadata mode.
//


// jmx imports
//
import com.sun.management.snmp.SnmpStatusException;

/**
 * This interface is used for representing the remote management interface for the "KpiCounterEntry" MBean.
 */
public interface KpiCounterEntryMBean {

    /**
     * Getter for the "TestCounter2" variable.
     */
    public Long getTestCounter2() throws SnmpStatusException;

    /**
     * Getter for the "TestCounter1" variable.
     */
    public Long getTestCounter1() throws SnmpStatusException;

    /**
     * Getter for the "KpiCounterIndex" variable.
     */
    public Integer getKpiCounterIndex() throws SnmpStatusException;

}
