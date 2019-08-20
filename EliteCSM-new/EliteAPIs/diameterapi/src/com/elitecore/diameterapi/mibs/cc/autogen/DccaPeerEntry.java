package com.elitecore.diameterapi.mibs.cc.autogen;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling DIAMETER-CC-APPLICATION-MIB.
//

// java imports
//
import java.io.Serializable;

import com.sun.management.snmp.SnmpStatusException;
import com.sun.management.snmp.agent.SnmpMib;
// jmx imports
//
// jdmk imports
//

/**
 * The class is used for implementing the "DccaPeerEntry" group.
 * The group is defined with the following oid: 1.3.6.1.2.1.4006.2.1.2.1.1.
 */
public class DccaPeerEntry implements DccaPeerEntryMBean, Serializable {

	public DccaPeerEntry() {
	}
	
    /**
     * Variable for storing the value of "DccaPeerRowStatus".
     * The variable is identified by: "1.3.6.1.2.1.4006.2.1.2.1.1.5".
     *
     * "The status of this conceptual row.
     * To create a row in this table, a manager must
     * set this object to either createAndGo(4) or
     * createAndWait(5).
     * 
     * Until instances of all corresponding columns
     * are appropriately configured, the value of the
     * corresponding instance of the
     * dccaPeerRowStatus column is 'notReady'.
     * 
     * In particular, a newly created row cannot be
     * made active until the corresponding dccaPeerId
     * has been set.
     * 
     * dccaPeerId may not be modified while the
     * value of this object is active(1):
     * An attempt to set these objects while the value
     * of dccaPeerRowStatus is active(1) will result
     * in an inconsistentValue error.
     * 
     * Entries in this table with dccaPeerRowStatus
     * equal to active(1) remain in the table until
     * destroyed.
     * 
     * Entries in this table with dccaPeerRowStatus
     * equal to values other than active(1) will be
     * destroyed after timeout (5 minutes).
     * 
     * If a dccaPeerId being created via SNMP already
     * exists in another active dccaPeerEntry, then a
     * newly created row
     * cannot be made active until the original row
     * with the dccaPeerId value is destroyed.
     * 
     * Upon reload, dccaPeerIndex values may be
     * changed."
     *
     */
    protected EnumDccaPeerRowStatus DccaPeerRowStatus = new EnumDccaPeerRowStatus();

    /**
     * Variable for storing the value of "DccaPeerStorageType".
     * The variable is identified by: "1.3.6.1.2.1.4006.2.1.2.1.1.4".
     *
     * "The storage type for this conceptual row. None
     * of the columnar objects is writable when the
     * conceptual row is permanent."
     *
     */
    protected EnumDccaPeerStorageType DccaPeerStorageType = new EnumDccaPeerStorageType("nonVolatile");

    /**
     * Variable for storing the value of "DccaPeerFirmwareRevision".
     * The variable is identified by: "1.3.6.1.2.1.4006.2.1.2.1.1.3".
     *
     * "Firmware revision of peer.  If no firmware
     * revision, the revision of the Diameter
     * Credit Control software
     * module may be reported instead."
     *
     */
    protected Long DccaPeerFirmwareRevision = new Long(1);

    /**
     * Variable for storing the value of "DccaPeerId".
     * The variable is identified by: "1.3.6.1.2.1.4006.2.1.2.1.1.2".
     *
     * "The server identifier for the Diameter
     * Credit Control peer."
     *
     */
    protected String DccaPeerId = new String("JDMK 5.1");

    /**
     * Variable for storing the value of "DccaPeerIndex".
     * The variable is identified by: "1.3.6.1.2.1.4006.2.1.2.1.1.1".
     *
     * "A number uniquely identifying each Diameter
     * Credit Control peer with which this host
     * communicates."
     *
     */
    protected Long DccaPeerIndex = new Long(1);


    /**
     * Constructor for the "DccaPeerEntry" group.
     */
    public DccaPeerEntry(SnmpMib myMib) {
    }

    /**
     * Getter for the "DccaPeerRowStatus" variable.
     */
    public EnumDccaPeerRowStatus getDccaPeerRowStatus() throws SnmpStatusException {
        return DccaPeerRowStatus;
    }

    /**
     * Setter for the "DccaPeerRowStatus" variable.
     * NB: There is no check method generated for RowStatus.
     *      Override checkRowStatusChange on SnmpMibTable if needed.
     */
    public void setDccaPeerRowStatus(EnumDccaPeerRowStatus x) throws SnmpStatusException {
        DccaPeerRowStatus = x;
    }

    /**
     * Getter for the "DccaPeerStorageType" variable.
     */
    public EnumDccaPeerStorageType getDccaPeerStorageType() throws SnmpStatusException {
        return DccaPeerStorageType;
    }

    /**
     * Setter for the "DccaPeerStorageType" variable.
     */
    public void setDccaPeerStorageType(EnumDccaPeerStorageType x) throws SnmpStatusException {
        DccaPeerStorageType = x;
    }

    /**
     * Checker for the "DccaPeerStorageType" variable.
     */
    public void checkDccaPeerStorageType(EnumDccaPeerStorageType x) throws SnmpStatusException {
        //
        // Add your own checking policy.
        //
    }

    /**
     * Getter for the "DccaPeerFirmwareRevision" variable.
     */
    public Long getDccaPeerFirmwareRevision() throws SnmpStatusException {
        return DccaPeerFirmwareRevision;
    }

    /**
     * Setter for the "DccaPeerFirmwareRevision" variable.
     */
    public void setDccaPeerFirmwareRevision(Long x) throws SnmpStatusException {
        DccaPeerFirmwareRevision = x;
    }

    /**
     * Checker for the "DccaPeerFirmwareRevision" variable.
     */
    public void checkDccaPeerFirmwareRevision(Long x) throws SnmpStatusException {
        //
        // Add your own checking policy.
        //
    }

    /**
     * Getter for the "DccaPeerId" variable.
     */
    public String getDccaPeerId() throws SnmpStatusException {
        return DccaPeerId;
    }

    /**
     * Setter for the "DccaPeerId" variable.
     */
    public void setDccaPeerId(String x) throws SnmpStatusException {
        DccaPeerId = x;
    }

    /**
     * Checker for the "DccaPeerId" variable.
     */
    public void checkDccaPeerId(String x) throws SnmpStatusException {
        //
        // Add your own checking policy.
        //
    }

    /**
     * Getter for the "DccaPeerIndex" variable.
     */
    public Long getDccaPeerIndex() throws SnmpStatusException {
        return DccaPeerIndex;
    }

}