package com.elitecore.nvsmx.snmp.mib.webservice.autogencode;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling NETVERTEX-PCRF-MIB in standard metadata mode.
//


// jmx imports
//
import com.sun.management.snmp.SnmpStatusException;

/**
 * This interface is used for representing the remote management interface for the "SubscriberWSDeleteAlternateIdEntry" MBean.
 */
public interface SubscriberWSDeleteAlternateIdEntryMBean {

    /**
     * Getter for the "DeleteAlternateIdResultCodeCounters" variable.
     */
    public Long getDeleteAlternateIdResultCodeCounters() throws SnmpStatusException;

    /**
     * Getter for the "DeleteAlternateIdResultCodeName" variable.
     */
    public String getDeleteAlternateIdResultCodeName() throws SnmpStatusException;

    /**
     * Getter for the "DeleteAlternateIdResultCode" variable.
     */
    public Integer getDeleteAlternateIdResultCode() throws SnmpStatusException;

}
