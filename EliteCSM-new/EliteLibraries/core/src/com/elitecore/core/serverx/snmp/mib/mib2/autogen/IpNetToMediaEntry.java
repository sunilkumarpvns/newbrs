package com.elitecore.core.serverx.snmp.mib.mib2.autogen;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling RFC1213-MIB.
//

// java imports
//
import java.io.Serializable;

import com.sun.management.snmp.SnmpStatusException;
import com.sun.management.snmp.agent.SnmpMib;

/**
 * The class is used for implementing the "IpNetToMediaEntry" group.
 * The group is defined with the following oid: 1.3.6.1.2.1.4.22.1.
 */
public class IpNetToMediaEntry implements IpNetToMediaEntryMBean, Serializable {

    /**
     * Variable for storing the value of "IpNetToMediaType".
     * The variable is identified by: "1.3.6.1.2.1.4.22.1.4".
     *
     * "The type of mapping.
     * 
     * Setting this object to the value invalid(2) has
     * the effect of invalidating the corresponding entry
     * in the ipNetToMediaTable.  That is, it effectively
     * dissasociates the interface identified with said
     * entry from the mapping identified with said entry.
     * It is an implementation-specific matter as to
     * whether the agent removes an invalidated entry
     * from the table.  Accordingly, management stations
     * must be prepared to receive tabular information
     * from agents that corresponds to entries not
     * currently in use.  Proper interpretation of such
     * entries requires examination of the relevant
     * ipNetToMediaType object."
     *
     */
    protected EnumIpNetToMediaType IpNetToMediaType = new EnumIpNetToMediaType();

    /**
     * Variable for storing the value of "IpNetToMediaNetAddress".
     * The variable is identified by: "1.3.6.1.2.1.4.22.1.3".
     *
     * "The IpAddress corresponding to the media-
     * dependent `physical' address."
     *
     */
    protected String IpNetToMediaNetAddress = new String("192.9.9.100");

    /**
     * Variable for storing the value of "IpNetToMediaPhysAddress".
     * The variable is identified by: "1.3.6.1.2.1.4.22.1.2".
     *
     * "The media-dependent `physical' address."
     *
     */
    protected Byte[] IpNetToMediaPhysAddress = { new Byte("74"), new Byte("68"), new Byte("77"), new Byte("75")};

    /**
     * Variable for storing the value of "IpNetToMediaIfIndex".
     * The variable is identified by: "1.3.6.1.2.1.4.22.1.1".
     *
     * "The interface on which this entry's equivalence
     * is effective.  The interface identified by a
     * particular value of this index is the same
     * interface as identified by the same value of
     * ifIndex."
     *
     */
    protected Integer IpNetToMediaIfIndex = new Integer(1);


    /**
     * Constructor for the "IpNetToMediaEntry" group.
     */
    public IpNetToMediaEntry(SnmpMib myMib) {
    }

    /**
     * Getter for the "IpNetToMediaType" variable.
     */
    public EnumIpNetToMediaType getIpNetToMediaType() throws SnmpStatusException {
        return IpNetToMediaType;
    }

    /**
     * Setter for the "IpNetToMediaType" variable.
     */
    public void setIpNetToMediaType(EnumIpNetToMediaType x) throws SnmpStatusException {
        IpNetToMediaType = x;
    }

    /**
     * Checker for the "IpNetToMediaType" variable.
     */
    public void checkIpNetToMediaType(EnumIpNetToMediaType x) throws SnmpStatusException {
        //
        // Add your own checking policy.
        //
    }

    /**
     * Getter for the "IpNetToMediaNetAddress" variable.
     */
    public String getIpNetToMediaNetAddress() throws SnmpStatusException {
        return IpNetToMediaNetAddress;
    }

    /**
     * Setter for the "IpNetToMediaNetAddress" variable.
     */
    public void setIpNetToMediaNetAddress(String x) throws SnmpStatusException {
        IpNetToMediaNetAddress = x;
    }

    /**
     * Checker for the "IpNetToMediaNetAddress" variable.
     */
    public void checkIpNetToMediaNetAddress(String x) throws SnmpStatusException {
        //
        // Add your own checking policy.
        //
    }

    /**
     * Getter for the "IpNetToMediaPhysAddress" variable.
     */
    public Byte[] getIpNetToMediaPhysAddress() throws SnmpStatusException {
        return IpNetToMediaPhysAddress;
    }

    /**
     * Setter for the "IpNetToMediaPhysAddress" variable.
     */
    public void setIpNetToMediaPhysAddress(Byte[] x) throws SnmpStatusException {
        IpNetToMediaPhysAddress = x;
    }

    /**
     * Checker for the "IpNetToMediaPhysAddress" variable.
     */
    public void checkIpNetToMediaPhysAddress(Byte[] x) throws SnmpStatusException {
        //
        // Add your own checking policy.
        //
    }

    /**
     * Getter for the "IpNetToMediaIfIndex" variable.
     */
    public Integer getIpNetToMediaIfIndex() throws SnmpStatusException {
        return IpNetToMediaIfIndex;
    }

    /**
     * Setter for the "IpNetToMediaIfIndex" variable.
     */
    public void setIpNetToMediaIfIndex(Integer x) throws SnmpStatusException {
        IpNetToMediaIfIndex = x;
    }

    /**
     * Checker for the "IpNetToMediaIfIndex" variable.
     */
    public void checkIpNetToMediaIfIndex(Integer x) throws SnmpStatusException {
        //
        // Add your own checking policy.
        //
    }

}