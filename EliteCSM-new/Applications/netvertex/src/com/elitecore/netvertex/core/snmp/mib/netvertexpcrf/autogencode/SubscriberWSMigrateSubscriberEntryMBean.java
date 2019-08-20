package com.elitecore.netvertex.core.snmp.mib.netvertexpcrf.autogencode;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling NETVERTEX-PCRF-MIB in standard metadata mode.
//


// jmx imports
//
import com.sun.management.snmp.SnmpStatusException;

/**
 * This interface is used for representing the remote management interface for the "SubscriberWSMigrateSubscriberEntry" MBean.
 */
public interface SubscriberWSMigrateSubscriberEntryMBean {

    /**
     * Getter for the "MigrateSubscriberResultCodeCounters" variable.
     */
    public Long getMigrateSubscriberResultCodeCounters() throws SnmpStatusException;

    /**
     * Getter for the "MigrateSubscriberResultCodeName" variable.
     */
    public String getMigrateSubscriberResultCodeName() throws SnmpStatusException;

    /**
     * Getter for the "MigrateSubscriberResultCode" variable.
     */
    public Integer getMigrateSubscriberResultCode() throws SnmpStatusException;

}