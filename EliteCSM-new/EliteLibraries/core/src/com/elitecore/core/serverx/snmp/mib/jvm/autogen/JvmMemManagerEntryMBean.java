package com.elitecore.core.serverx.snmp.mib.jvm.autogen;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling JVM-MANAGEMENT-MIB in standard metadata mode.
//


// jmx imports
//
import com.sun.management.snmp.SnmpStatusException;

/**
 * This interface is used for representing the remote management interface for the "JvmMemManagerEntry" MBean.
 */
public interface JvmMemManagerEntryMBean {

    /**
     * Getter for the "JvmMemManagerState" variable.
     */
    public EnumJvmMemManagerState getJvmMemManagerState() throws SnmpStatusException;

    /**
     * Getter for the "JvmMemManagerName" variable.
     */
    public Byte[] getJvmMemManagerName() throws SnmpStatusException;

    /**
     * Getter for the "JvmMemManagerIndex" variable.
     */
    public Integer getJvmMemManagerIndex() throws SnmpStatusException;

}