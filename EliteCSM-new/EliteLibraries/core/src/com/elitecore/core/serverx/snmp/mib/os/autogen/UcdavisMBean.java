package com.elitecore.core.serverx.snmp.mib.os.autogen;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling UCD-SNMP-MIB in standard metadata mode.
//


// jmx imports
//
import com.sun.management.snmp.SnmpStatusException;

/**
 * This interface is used for representing the remote management interface for the "Ucdavis" MBean.
 */
public interface UcdavisMBean {

    /**
     * Access the "DskTable" variable.
     */
    public TableDskTable accessDskTable() throws SnmpStatusException;

    /**
     * Access the "ExtTable" variable.
     */
    public TableExtTable accessExtTable() throws SnmpStatusException;

    /**
     * Access the "FileTable" variable.
     */
    public TableFileTable accessFileTable() throws SnmpStatusException;

    /**
     * Access the "MrTable" variable.
     */
    public TableMrTable accessMrTable() throws SnmpStatusException;

    /**
     * Access the "PrTable" variable.
     */
    public TablePrTable accessPrTable() throws SnmpStatusException;

    /**
     * Access the "LaTable" variable.
     */
    public TableLaTable accessLaTable() throws SnmpStatusException;

}