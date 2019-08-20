package com.elitecore.nvsmx.snmp.mib.webservice.autogencode;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling NETVERTEX-PCRF-MIB in standard metadata mode.
//


// jmx imports
//
import com.sun.management.snmp.SnmpStatusException;

/**
 * This interface is used for representing the remote management interface for the "SubscriptionWSListAddOnPackagesEntry" MBean.
 */
public interface SubscriptionWSListAddOnPackagesEntryMBean {

    /**
     * Getter for the "ListAddOnPackagesResultCodeCounters" variable.
     */
    public Long getListAddOnPackagesResultCodeCounters() throws SnmpStatusException;

    /**
     * Getter for the "ListAddOnPackagesResultCodeName" variable.
     */
    public String getListAddOnPackagesResultCodeName() throws SnmpStatusException;

    /**
     * Getter for the "ListAddOnPackagesResultCode" variable.
     */
    public Integer getListAddOnPackagesResultCode() throws SnmpStatusException;

}