package com.elitecore.nvsmx.snmp.mib.webservice.autogencode;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling NETVERTEX-PCRF-MIB in standard metadata mode.
//


// jmx imports
//
import com.sun.management.snmp.SnmpStatusException;

/**
 * This interface is used for representing the remote management interface for the "SubscriberWSDeleteSubscriberProfilesEntry" MBean.
 */
public interface SubscriberWSDeleteSubscriberProfilesEntryMBean {

    /**
     * Getter for the "DeleteSubscriberProfilesResultCodeCounters" variable.
     */
    public Long getDeleteSubscriberProfilesResultCodeCounters() throws SnmpStatusException;

    /**
     * Getter for the "DeleteSubscriberProfilesResultCodeName" variable.
     */
    public String getDeleteSubscriberProfilesResultCodeName() throws SnmpStatusException;

    /**
     * Getter for the "DeleteSubscriberProfilesResultCode" variable.
     */
    public Integer getDeleteSubscriberProfilesResultCode() throws SnmpStatusException;

}