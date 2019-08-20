package com.elitecore.core.serverx.snmp.mib.jvm.autogen;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling JVM-MANAGEMENT-MIB in standard metadata mode.
//


// jmx imports
//
import com.sun.management.snmp.SnmpStatusException;

/**
 * This interface is used for representing the remote management interface for the "JvmRTLibraryPathEntry" MBean.
 */
public interface JvmRTLibraryPathEntryMBean {

    /**
     * Getter for the "JvmRTLibraryPathItem" variable.
     */
    public Byte[] getJvmRTLibraryPathItem() throws SnmpStatusException;

    /**
     * Getter for the "JvmRTLibraryPathIndex" variable.
     */
    public Integer getJvmRTLibraryPathIndex() throws SnmpStatusException;

}
