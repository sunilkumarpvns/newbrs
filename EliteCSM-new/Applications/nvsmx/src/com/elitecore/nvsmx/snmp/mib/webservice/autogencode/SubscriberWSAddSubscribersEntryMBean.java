package com.elitecore.nvsmx.snmp.mib.webservice.autogencode;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling NETVERTEX-PCRF-MIB in standard metadata mode.
//


// jmx imports
//
import com.sun.management.snmp.SnmpStatusException;

/**
 * This interface is used for representing the remote management interface for the "SubscriberWSAddSubscribersEntry" MBean.
 */
public interface SubscriberWSAddSubscribersEntryMBean {

    /**
     * Getter for the "AddSubscribersResultCodeCounters" variable.
     */
    public Long getAddSubscribersResultCodeCounters() throws SnmpStatusException;

    /**
     * Getter for the "AddSubscribersResultCodeName" variable.
     */
    public String getAddSubscribersResultCodeName() throws SnmpStatusException;

    /**
     * Getter for the "AddSubscribersResultCode" variable.
     */
    public Integer getAddSubscribersResultCode() throws SnmpStatusException;

}
