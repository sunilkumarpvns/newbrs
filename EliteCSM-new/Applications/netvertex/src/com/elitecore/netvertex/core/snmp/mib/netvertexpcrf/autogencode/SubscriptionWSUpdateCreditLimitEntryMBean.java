package com.elitecore.netvertex.core.snmp.mib.netvertexpcrf.autogencode;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling NETVERTEX-PCRF-MIB in standard metadata mode.
//


// jmx imports
//
import com.sun.management.snmp.SnmpStatusException;

/**
 * This interface is used for representing the remote management interface for the "SubscriptionWSUpdateCreditLimitEntry" MBean.
 */
public interface SubscriptionWSUpdateCreditLimitEntryMBean {

    /**
     * Getter for the "UpdateCreditLimitResultCodeCounters" variable.
     */
    public Long getUpdateCreditLimitResultCodeCounters() throws SnmpStatusException;

    /**
     * Getter for the "UpdateCreditLimitResultCodeName" variable.
     */
    public String getUpdateCreditLimitResultCodeName() throws SnmpStatusException;

    /**
     * Getter for the "UpdateCreditLimitResultCode" variable.
     */
    public Integer getUpdateCreditLimitResultCode() throws SnmpStatusException;

}