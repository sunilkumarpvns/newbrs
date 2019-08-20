package com.elitecore.nvsmx.snmp.mib.webservice.autogencode;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling NETVERTEX-PCRF-MIB in standard metadata mode.
//


// jmx imports
//
import com.sun.management.snmp.SnmpStatusException;

/**
 * This interface is used for representing the remote management interface for the "SubscriberWSPurgeSubscriberEntry" MBean.
 */
public interface SubscriberWSPurgeSubscriberEntryMBean {

    /**
     * Getter for the "PurgeSubscriberResultCodeCounters" variable.
     */
    public Long getPurgeSubscriberResultCodeCounters() throws SnmpStatusException;

    /**
     * Getter for the "PurgeSubscriberResultCodeName" variable.
     */
    public String getPurgeSubscriberResultCodeName() throws SnmpStatusException;

    /**
     * Getter for the "PurgeSubscriberResultCode" variable.
     */
    public Integer getPurgeSubscriberResultCode() throws SnmpStatusException;

}