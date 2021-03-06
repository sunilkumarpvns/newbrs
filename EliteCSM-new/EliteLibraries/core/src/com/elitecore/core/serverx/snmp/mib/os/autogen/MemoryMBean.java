package com.elitecore.core.serverx.snmp.mib.os.autogen;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling UCD-SNMP-MIB in standard metadata mode.
//


// jmx imports
//
import com.sun.management.snmp.SnmpStatusException;

/**
 * This interface is used for representing the remote management interface for the "Memory" MBean.
 */
public interface MemoryMBean {

    /**
     * Getter for the "MemSwapErrorMsg" variable.
     */
    public String getMemSwapErrorMsg() throws SnmpStatusException;

    /**
     * Getter for the "MemSwapError" variable.
     */
    public EnumMemSwapError getMemSwapError() throws SnmpStatusException;

    /**
     * Getter for the "MemUsedRealTXT" variable.
     */
    public Integer getMemUsedRealTXT() throws SnmpStatusException;

    /**
     * Getter for the "MemUsedSwapTXT" variable.
     */
    public Integer getMemUsedSwapTXT() throws SnmpStatusException;

    /**
     * Getter for the "MemCached" variable.
     */
    public Integer getMemCached() throws SnmpStatusException;

    /**
     * Getter for the "MemBuffer" variable.
     */
    public Integer getMemBuffer() throws SnmpStatusException;

    /**
     * Getter for the "MemShared" variable.
     */
    public Integer getMemShared() throws SnmpStatusException;

    /**
     * Getter for the "MemMinimumSwap" variable.
     */
    public Integer getMemMinimumSwap() throws SnmpStatusException;

    /**
     * Getter for the "MemTotalFree" variable.
     */
    public Integer getMemTotalFree() throws SnmpStatusException;

    /**
     * Getter for the "MemAvailRealTXT" variable.
     */
    public Integer getMemAvailRealTXT() throws SnmpStatusException;

    /**
     * Getter for the "MemTotalRealTXT" variable.
     */
    public Integer getMemTotalRealTXT() throws SnmpStatusException;

    /**
     * Getter for the "MemAvailSwapTXT" variable.
     */
    public Integer getMemAvailSwapTXT() throws SnmpStatusException;

    /**
     * Getter for the "MemTotalSwapTXT" variable.
     */
    public Integer getMemTotalSwapTXT() throws SnmpStatusException;

    /**
     * Getter for the "MemAvailReal" variable.
     */
    public Integer getMemAvailReal() throws SnmpStatusException;

    /**
     * Getter for the "MemTotalReal" variable.
     */
    public Integer getMemTotalReal() throws SnmpStatusException;

    /**
     * Getter for the "MemAvailSwap" variable.
     */
    public Integer getMemAvailSwap() throws SnmpStatusException;

    /**
     * Getter for the "MemTotalSwap" variable.
     */
    public Integer getMemTotalSwap() throws SnmpStatusException;

    /**
     * Getter for the "MemErrorName" variable.
     */
    public String getMemErrorName() throws SnmpStatusException;

    /**
     * Getter for the "MemIndex" variable.
     */
    public Integer getMemIndex() throws SnmpStatusException;

}
