package com.elitecore.netvertex.core.snmp.mib.netvertexpcrf.autogencode;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling NETVERTEX-PCRF-MIB in standard metadata mode.
//


// jmx imports
//
import com.sun.management.snmp.SnmpStatusException;

/**
 * This interface is used for representing the remote management interface for the "SubscriptionWSResultCodeWiseEntry" MBean.
 */
public interface SubscriptionWSResultCodeWiseEntryMBean {

    /**
     * Getter for the "SubscriptionWSResultCodeCounters" variable.
     */
    public Long getSubscriptionWSResultCodeCounters() throws SnmpStatusException;

    /**
     * Getter for the "SubscriptionWSResultCodeName" variable.
     */
    public String getSubscriptionWSResultCodeName() throws SnmpStatusException;

    /**
     * Getter for the "SubscriptionWSResultCode" variable.
     */
    public Integer getSubscriptionWSResultCode() throws SnmpStatusException;

}
