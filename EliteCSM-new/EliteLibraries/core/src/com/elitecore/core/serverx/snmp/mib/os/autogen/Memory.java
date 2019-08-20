package com.elitecore.core.serverx.snmp.mib.os.autogen;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling UCD-SNMP-MIB.
//

// java imports
//
import java.io.Serializable;

import javax.management.MBeanServer;

import com.sun.management.snmp.SnmpStatusException;
import com.sun.management.snmp.agent.SnmpMib;

/**
 * The class is used for implementing the "Memory" group.
 * The group is defined with the following oid: 1.3.6.1.4.1.2021.4.
 */
public class Memory implements MemoryMBean, Serializable {

    /**
     * Variable for storing the value of "MemSwapErrorMsg".
     * The variable is identified by: "1.3.6.1.4.1.2021.4.101".
     */
    protected String MemSwapErrorMsg = new String("JDMK 5.1");

    /**
     * Variable for storing the value of "MemSwapError".
     * The variable is identified by: "1.3.6.1.4.1.2021.4.100".
     */
    protected EnumMemSwapError MemSwapError = new EnumMemSwapError();

    /**
     * Variable for storing the value of "MemUsedRealTXT".
     * The variable is identified by: "1.3.6.1.4.1.2021.4.17".
     */
    protected Integer MemUsedRealTXT = new Integer(1);

    /**
     * Variable for storing the value of "MemUsedSwapTXT".
     * The variable is identified by: "1.3.6.1.4.1.2021.4.16".
     */
    protected Integer MemUsedSwapTXT = new Integer(1);

    /**
     * Variable for storing the value of "MemCached".
     * The variable is identified by: "1.3.6.1.4.1.2021.4.15".
     */
    protected Integer MemCached = new Integer(1);

    /**
     * Variable for storing the value of "MemBuffer".
     * The variable is identified by: "1.3.6.1.4.1.2021.4.14".
     */
    protected Integer MemBuffer = new Integer(1);

    /**
     * Variable for storing the value of "MemShared".
     * The variable is identified by: "1.3.6.1.4.1.2021.4.13".
     */
    protected Integer MemShared = new Integer(1);

    /**
     * Variable for storing the value of "MemMinimumSwap".
     * The variable is identified by: "1.3.6.1.4.1.2021.4.12".
     */
    protected Integer MemMinimumSwap = new Integer(1);

    /**
     * Variable for storing the value of "MemTotalFree".
     * The variable is identified by: "1.3.6.1.4.1.2021.4.11".
     */
    protected Integer MemTotalFree = new Integer(1);

    /**
     * Variable for storing the value of "MemAvailRealTXT".
     * The variable is identified by: "1.3.6.1.4.1.2021.4.10".
     */
    protected Integer MemAvailRealTXT = new Integer(1);

    /**
     * Variable for storing the value of "MemTotalRealTXT".
     * The variable is identified by: "1.3.6.1.4.1.2021.4.9".
     */
    protected Integer MemTotalRealTXT = new Integer(1);

    /**
     * Variable for storing the value of "MemAvailSwapTXT".
     * The variable is identified by: "1.3.6.1.4.1.2021.4.8".
     */
    protected Integer MemAvailSwapTXT = new Integer(1);

    /**
     * Variable for storing the value of "MemTotalSwapTXT".
     * The variable is identified by: "1.3.6.1.4.1.2021.4.7".
     */
    protected Integer MemTotalSwapTXT = new Integer(1);

    /**
     * Variable for storing the value of "MemAvailReal".
     * The variable is identified by: "1.3.6.1.4.1.2021.4.6".
     */
    protected Integer MemAvailReal = new Integer(1);

    /**
     * Variable for storing the value of "MemTotalReal".
     * The variable is identified by: "1.3.6.1.4.1.2021.4.5".
     */
    protected Integer MemTotalReal = new Integer(1);

    /**
     * Variable for storing the value of "MemAvailSwap".
     * The variable is identified by: "1.3.6.1.4.1.2021.4.4".
     */
    protected Integer MemAvailSwap = new Integer(1);

    /**
     * Variable for storing the value of "MemTotalSwap".
     * The variable is identified by: "1.3.6.1.4.1.2021.4.3".
     */
    protected Integer MemTotalSwap = new Integer(1);

    /**
     * Variable for storing the value of "MemErrorName".
     * The variable is identified by: "1.3.6.1.4.1.2021.4.2".
     */
    protected String MemErrorName = new String("JDMK 5.1");

    /**
     * Variable for storing the value of "MemIndex".
     * The variable is identified by: "1.3.6.1.4.1.2021.4.1".
     */
    protected Integer MemIndex = new Integer(1);


    /**
     * Constructor for the "Memory" group.
     * If the group contains a table, the entries created through an SNMP SET will not be registered in Java DMK.
     */
    public Memory(SnmpMib myMib) {
    }


    /**
     * Constructor for the "Memory" group.
     * If the group contains a table, the entries created through an SNMP SET will be AUTOMATICALLY REGISTERED in Java DMK.
     */
    public Memory(SnmpMib myMib, MBeanServer server) {
    }

    public Memory() {

    }
    
    /**
     * Getter for the "MemSwapErrorMsg" variable.
     */
    public String getMemSwapErrorMsg() throws SnmpStatusException {
        return MemSwapErrorMsg;
    }

    /**
     * Getter for the "MemSwapError" variable.
     */
    public EnumMemSwapError getMemSwapError() throws SnmpStatusException {
        return MemSwapError;
    }

    /**
     * Getter for the "MemUsedRealTXT" variable.
     */
    public Integer getMemUsedRealTXT() throws SnmpStatusException {
        return MemUsedRealTXT;
    }

    /**
     * Getter for the "MemUsedSwapTXT" variable.
     */
    public Integer getMemUsedSwapTXT() throws SnmpStatusException {
        return MemUsedSwapTXT;
    }

    /**
     * Getter for the "MemCached" variable.
     */
    public Integer getMemCached() throws SnmpStatusException {
        return MemCached;
    }

    /**
     * Getter for the "MemBuffer" variable.
     */
    public Integer getMemBuffer() throws SnmpStatusException {
        return MemBuffer;
    }

    /**
     * Getter for the "MemShared" variable.
     */
    public Integer getMemShared() throws SnmpStatusException {
        return MemShared;
    }

    /**
     * Getter for the "MemMinimumSwap" variable.
     */
    public Integer getMemMinimumSwap() throws SnmpStatusException {
        return MemMinimumSwap;
    }

    /**
     * Getter for the "MemTotalFree" variable.
     */
    public Integer getMemTotalFree() throws SnmpStatusException {
        return MemTotalFree;
    }

    /**
     * Getter for the "MemAvailRealTXT" variable.
     */
    public Integer getMemAvailRealTXT() throws SnmpStatusException {
        return MemAvailRealTXT;
    }

    /**
     * Getter for the "MemTotalRealTXT" variable.
     */
    public Integer getMemTotalRealTXT() throws SnmpStatusException {
        return MemTotalRealTXT;
    }

    /**
     * Getter for the "MemAvailSwapTXT" variable.
     */
    public Integer getMemAvailSwapTXT() throws SnmpStatusException {
        return MemAvailSwapTXT;
    }

    /**
     * Getter for the "MemTotalSwapTXT" variable.
     */
    public Integer getMemTotalSwapTXT() throws SnmpStatusException {
        return MemTotalSwapTXT;
    }

    /**
     * Getter for the "MemAvailReal" variable.
     */
    public Integer getMemAvailReal() throws SnmpStatusException {
        return MemAvailReal;
    }

    /**
     * Getter for the "MemTotalReal" variable.
     */
    public Integer getMemTotalReal() throws SnmpStatusException {
        return MemTotalReal;
    }

    /**
     * Getter for the "MemAvailSwap" variable.
     */
    public Integer getMemAvailSwap() throws SnmpStatusException {
        return MemAvailSwap;
    }

    /**
     * Getter for the "MemTotalSwap" variable.
     */
    public Integer getMemTotalSwap() throws SnmpStatusException {
        return MemTotalSwap;
    }

    /**
     * Getter for the "MemErrorName" variable.
     */
    public String getMemErrorName() throws SnmpStatusException {
        return MemErrorName;
    }

    /**
     * Getter for the "MemIndex" variable.
     */
    public Integer getMemIndex() throws SnmpStatusException {
        return MemIndex;
    }

}