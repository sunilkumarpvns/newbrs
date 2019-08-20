package com.elitecore.core.serverx.snmp.mib.os.autogen;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling UCD-SNMP-MIB in standard metadata mode.
//


// jmx imports
//
import com.sun.management.snmp.SnmpStatusException;

/**
 * This interface is used for representing the remote management interface for the "PrEntry" MBean.
 */
public interface PrEntryMBean {

    /**
     * Getter for the "PrCount" variable.
     */
    public Integer getPrCount() throws SnmpStatusException;

    /**
     * Getter for the "PrMax" variable.
     */
    public Integer getPrMax() throws SnmpStatusException;

    /**
     * Getter for the "PrErrFixCmd" variable.
     */
    public String getPrErrFixCmd() throws SnmpStatusException;

    /**
     * Getter for the "PrErrFix" variable.
     */
    public EnumPrErrFix getPrErrFix() throws SnmpStatusException;

    /**
     * Setter for the "PrErrFix" variable.
     */
    public void setPrErrFix(EnumPrErrFix x) throws SnmpStatusException;

    /**
     * Checker for the "PrErrFix" variable.
     */
    public void checkPrErrFix(EnumPrErrFix x) throws SnmpStatusException;

    /**
     * Getter for the "PrMin" variable.
     */
    public Integer getPrMin() throws SnmpStatusException;

    /**
     * Getter for the "PrErrMessage" variable.
     */
    public String getPrErrMessage() throws SnmpStatusException;

    /**
     * Getter for the "PrNames" variable.
     */
    public String getPrNames() throws SnmpStatusException;

    /**
     * Getter for the "PrErrorFlag" variable.
     */
    public EnumPrErrorFlag getPrErrorFlag() throws SnmpStatusException;

    /**
     * Getter for the "PrIndex" variable.
     */
    public Integer getPrIndex() throws SnmpStatusException;

}