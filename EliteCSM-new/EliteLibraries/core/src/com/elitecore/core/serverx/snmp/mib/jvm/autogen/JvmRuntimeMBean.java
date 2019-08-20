package com.elitecore.core.serverx.snmp.mib.jvm.autogen;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling JVM-MANAGEMENT-MIB in standard metadata mode.
//


// jmx imports
//
import com.sun.management.snmp.SnmpStatusException;

/**
 * This interface is used for representing the remote management interface for the "JvmRuntime" MBean.
 */
public interface JvmRuntimeMBean {

    /**
     * Access the "JvmRTLibraryPathTable" variable.
     */
    public TableJvmRTLibraryPathTable accessJvmRTLibraryPathTable() throws SnmpStatusException;

    /**
     * Access the "JvmRTClassPathTable" variable.
     */
    public TableJvmRTClassPathTable accessJvmRTClassPathTable() throws SnmpStatusException;

    /**
     * Access the "JvmRTBootClassPathTable" variable.
     */
    public TableJvmRTBootClassPathTable accessJvmRTBootClassPathTable() throws SnmpStatusException;

    /**
     * Getter for the "JvmRTBootClassPathSupport" variable.
     */
    public EnumJvmRTBootClassPathSupport getJvmRTBootClassPathSupport() throws SnmpStatusException;

    /**
     * Access the "JvmRTInputArgsTable" variable.
     */
    public TableJvmRTInputArgsTable accessJvmRTInputArgsTable() throws SnmpStatusException;

    /**
     * Getter for the "JvmRTManagementSpecVersion" variable.
     */
    public String getJvmRTManagementSpecVersion() throws SnmpStatusException;

    /**
     * Getter for the "JvmRTSpecVersion" variable.
     */
    public String getJvmRTSpecVersion() throws SnmpStatusException;

    /**
     * Getter for the "JvmRTSpecVendor" variable.
     */
    public String getJvmRTSpecVendor() throws SnmpStatusException;

    /**
     * Getter for the "JvmRTSpecName" variable.
     */
    public String getJvmRTSpecName() throws SnmpStatusException;

    /**
     * Getter for the "JvmRTVMVersion" variable.
     */
    public String getJvmRTVMVersion() throws SnmpStatusException;

    /**
     * Getter for the "JvmRTVMVendor" variable.
     */
    public String getJvmRTVMVendor() throws SnmpStatusException;

    /**
     * Getter for the "JvmRTStartTimeMs" variable.
     */
    public Long getJvmRTStartTimeMs() throws SnmpStatusException;

    /**
     * Getter for the "JvmRTUptimeMs" variable.
     */
    public Long getJvmRTUptimeMs() throws SnmpStatusException;

    /**
     * Getter for the "JvmRTVMName" variable.
     */
    public Byte[] getJvmRTVMName() throws SnmpStatusException;

    /**
     * Getter for the "JvmRTName" variable.
     */
    public String getJvmRTName() throws SnmpStatusException;

    /**
     * Getter for the "JvmRTInputArgsCount" variable.
     */
    public Integer getJvmRTInputArgsCount() throws SnmpStatusException;

}