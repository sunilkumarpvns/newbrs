package com.elitecore.netvertex.core.snmp.mib.netvertexpcrf.autogencode;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling NETVERTEX-PCRF-MIB in standard metadata mode.
//


// jmx imports
//
import com.sun.management.snmp.SnmpStatusException;

/**
 * This interface is used for representing the remote management interface for the "SubscriptionWSChangeRnCAddOnSubscriptionEntry" MBean.
 */
public interface SubscriptionWSChangeRnCAddOnSubscriptionEntryMBean {

    /**
     * Getter for the "ChangeRnCAddOnSubscriptionResultCodeCounters" variable.
     */
    public Long getChangeRnCAddOnSubscriptionResultCodeCounters() throws SnmpStatusException;

    /**
     * Getter for the "ChangeRnCAddOnSubscriptionResultCodeName" variable.
     */
    public String getChangeRnCAddOnSubscriptionResultCodeName() throws SnmpStatusException;

    /**
     * Getter for the "ChangeRnCAddOnSubscriptionResultCode" variable.
     */
    public Integer getChangeRnCAddOnSubscriptionResultCode() throws SnmpStatusException;

}