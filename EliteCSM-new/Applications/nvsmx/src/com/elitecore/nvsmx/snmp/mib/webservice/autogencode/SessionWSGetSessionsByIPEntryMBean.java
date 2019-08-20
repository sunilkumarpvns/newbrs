package com.elitecore.nvsmx.snmp.mib.webservice.autogencode;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling NETVERTEX-PCRF-MIB in standard metadata mode.
//


// jmx imports
//
import com.sun.management.snmp.SnmpStatusException;

/**
 * This interface is used for representing the remote management interface for the "SessionWSGetSessionsByIPEntry" MBean.
 */
public interface SessionWSGetSessionsByIPEntryMBean {

    /**
     * Getter for the "GetSessionsByIPResultCodeCounters" variable.
     */
    public Long getGetSessionsByIPResultCodeCounters() throws SnmpStatusException;

    /**
     * Getter for the "GetSessionsByIPResultCodeName" variable.
     */
    public String getGetSessionsByIPResultCodeName() throws SnmpStatusException;

    /**
     * Getter for the "GetSessionsByIPResultCode" variable.
     */
    public Integer getGetSessionsByIPResultCode() throws SnmpStatusException;

}
