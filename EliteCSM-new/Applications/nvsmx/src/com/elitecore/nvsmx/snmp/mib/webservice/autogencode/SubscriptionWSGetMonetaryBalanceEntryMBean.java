package com.elitecore.nvsmx.snmp.mib.webservice.autogencode;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling NETVERTEX-PCRF-MIB in standard metadata mode.
//


// jmx imports
//
import com.sun.management.snmp.SnmpStatusException;

/**
 * This interface is used for representing the remote management interface for the "SubscriptionWSGetMonetaryBalanceEntry" MBean.
 */
public interface SubscriptionWSGetMonetaryBalanceEntryMBean {

    /**
     * Getter for the "GetMonetaryBalanceResultCodeCounters" variable.
     */
    public Long getGetMonetaryBalanceResultCodeCounters() throws SnmpStatusException;

    /**
     * Getter for the "GetMonetaryBalanceResultCodeName" variable.
     */
    public String getGetMonetaryBalanceResultCodeName() throws SnmpStatusException;

    /**
     * Getter for the "GetMonetaryBalanceResultCode" variable.
     */
    public Integer getGetMonetaryBalanceResultCode() throws SnmpStatusException;

}
