package com.elitecore.nvsmx.snmp.mib.webservice.autogencode;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling NETVERTEX-PCRF-MIB in standard metadata mode.
//


// jmx imports
//
import com.sun.management.snmp.SnmpStatusException;

/**
 * This interface is used for representing the remote management interface for the "SubscriptionWSUpdateMonetaryBalanceEntry" MBean.
 */
public interface SubscriptionWSUpdateMonetaryBalanceEntryMBean {

    /**
     * Getter for the "UpdateMonetaryBalanceResultCodeCounters" variable.
     */
    public Long getUpdateMonetaryBalanceResultCodeCounters() throws SnmpStatusException;

    /**
     * Getter for the "UpdateMonetaryBalanceResultCodeName" variable.
     */
    public String getUpdateMonetaryBalanceResultCodeName() throws SnmpStatusException;

    /**
     * Getter for the "UpdateMonetaryBalanceResultCode" variable.
     */
    public Integer getUpdateMonetaryBalanceResultCode() throws SnmpStatusException;

}
