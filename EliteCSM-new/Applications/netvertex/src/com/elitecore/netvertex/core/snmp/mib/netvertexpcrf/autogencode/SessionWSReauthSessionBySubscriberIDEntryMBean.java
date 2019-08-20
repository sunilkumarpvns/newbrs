package com.elitecore.netvertex.core.snmp.mib.netvertexpcrf.autogencode;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling NETVERTEX-PCRF-MIB in standard metadata mode.
//


// jmx imports
//
import com.sun.management.snmp.SnmpStatusException;

/**
 * This interface is used for representing the remote management interface for the "SessionWSReauthSessionBySubscriberIDEntry" MBean.
 */
public interface SessionWSReauthSessionBySubscriberIDEntryMBean {

    /**
     * Getter for the "ReauthSessionBySubscriberIDResultCodeCounters" variable.
     */
    public Long getReauthSessionBySubscriberIDResultCodeCounters() throws SnmpStatusException;

    /**
     * Getter for the "ReauthSessionBySubscriberIDResultCodeName" variable.
     */
    public String getReauthSessionBySubscriberIDResultCodeName() throws SnmpStatusException;

    /**
     * Getter for the "ReauthSessionBySubscriberIDResultCode" variable.
     */
    public Integer getReauthSessionBySubscriberIDResultCode() throws SnmpStatusException;

}
