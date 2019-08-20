package com.elitecore.netvertex.core.snmp.mib.netvertexpcrf.autogencode;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling NETVERTEX-PCRF-MIB in standard metadata mode.
//


// jmx imports
//
import com.sun.management.snmp.SnmpStatusException;

/**
 * This interface is used for representing the remote management interface for the "SessionWSGetSessionsBySubscriberIDEntry" MBean.
 */
public interface SessionWSGetSessionsBySubscriberIDEntryMBean {

    /**
     * Getter for the "GetSessionsBySubscriberIDResultCodeCounters" variable.
     */
    public Long getGetSessionsBySubscriberIDResultCodeCounters() throws SnmpStatusException;

    /**
     * Getter for the "GetSessionsBySubscriberIDResultCodeName" variable.
     */
    public String getGetSessionsBySubscriberIDResultCodeName() throws SnmpStatusException;

    /**
     * Getter for the "GetSessionsBySubscriberIDResultCode" variable.
     */
    public Integer getGetSessionsBySubscriberIDResultCode() throws SnmpStatusException;

}