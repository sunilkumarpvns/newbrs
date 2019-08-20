package com.elitecore.core.serverx.snmp.mib.os.autogen;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling UCD-SNMP-MIB in standard metadata mode.
//


// jmx imports
//
import com.sun.management.snmp.SnmpStatusException;

/**
 * This interface is used for representing the remote management interface for the "LaEntry" MBean.
 */
public interface LaEntryMBean {

    /**
     * Getter for the "LaLoadFloat" variable.
     */
    public Byte[] getLaLoadFloat() throws SnmpStatusException;

    /**
     * Getter for the "LaLoadInt" variable.
     */
    public Integer getLaLoadInt() throws SnmpStatusException;

    /**
     * Getter for the "LaConfig" variable.
     */
    public String getLaConfig() throws SnmpStatusException;

    /**
     * Setter for the "LaConfig" variable.
     */
    public void setLaConfig(String x) throws SnmpStatusException;

    /**
     * Checker for the "LaConfig" variable.
     */
    public void checkLaConfig(String x) throws SnmpStatusException;

    /**
     * Getter for the "LaLoad" variable.
     */
    public String getLaLoad() throws SnmpStatusException;

    /**
     * Getter for the "LaErrMessage" variable.
     */
    public String getLaErrMessage() throws SnmpStatusException;

    /**
     * Getter for the "LaNames" variable.
     */
    public String getLaNames() throws SnmpStatusException;

    /**
     * Getter for the "LaIndex" variable.
     */
    public Integer getLaIndex() throws SnmpStatusException;

    /**
     * Getter for the "LaErrorFlag" variable.
     */
    public EnumLaErrorFlag getLaErrorFlag() throws SnmpStatusException;

}