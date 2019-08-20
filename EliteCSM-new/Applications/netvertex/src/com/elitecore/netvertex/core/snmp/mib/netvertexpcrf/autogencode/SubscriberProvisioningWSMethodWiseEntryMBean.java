package com.elitecore.netvertex.core.snmp.mib.netvertexpcrf.autogencode;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling NETVERTEX-PCRF-MIB in standard metadata mode.
//


// jmx imports
//
import com.sun.management.snmp.SnmpStatusException;

/**
 * This interface is used for representing the remote management interface for the "SubscriberProvisioningWSMethodWiseEntry" MBean.
 */
public interface SubscriberProvisioningWSMethodWiseEntryMBean {

    /**
     * Getter for the "SubscriberWSMethodAvgTPS" variable.
     */
    public Long getSubscriberWSMethodAvgTPS() throws SnmpStatusException;

    /**
     * Getter for the "SubscriberWSMethodLastMinuteTotalRequests" variable.
     */
    public Long getSubscriberWSMethodLastMinuteTotalRequests() throws SnmpStatusException;

    /**
     * Getter for the "SubscriberWSMethodTotalResponses" variable.
     */
    public Long getSubscriberWSMethodTotalResponses() throws SnmpStatusException;

    /**
     * Getter for the "SubscriberWSMethodTotalRequests" variable.
     */
    public Long getSubscriberWSMethodTotalRequests() throws SnmpStatusException;

    /**
     * Getter for the "SubscriberWSMethodName" variable.
     */
    public String getSubscriberWSMethodName() throws SnmpStatusException;

    /**
     * Getter for the "SubscriberWSMethodIndex" variable.
     */
    public Integer getSubscriberWSMethodIndex() throws SnmpStatusException;

}