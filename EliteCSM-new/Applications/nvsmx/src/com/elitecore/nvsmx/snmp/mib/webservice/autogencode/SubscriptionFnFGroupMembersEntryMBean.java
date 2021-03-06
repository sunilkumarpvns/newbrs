package com.elitecore.nvsmx.snmp.mib.webservice.autogencode;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling NETVERTEX-PCRF-MIB in standard metadata mode.
//


// jmx imports
//
import com.sun.management.snmp.SnmpStatusException;

/**
 * This interface is used for representing the remote management interface for the "SubscriptionFnFGroupMembersEntry" MBean.
 */
public interface SubscriptionFnFGroupMembersEntryMBean {

    /**
     * Getter for the "FnFGroupMembersResultCodeCounters" variable.
     */
    public Long getFnFGroupMembersResultCodeCounters() throws SnmpStatusException;

    /**
     * Getter for the "FnFGroupMembersResultCodeName" variable.
     */
    public String getFnFGroupMembersResultCodeName() throws SnmpStatusException;

    /**
     * Getter for the "FnFGroupMembersResultCode" variable.
     */
    public Integer getFnFGroupMembersResultCode() throws SnmpStatusException;

}
