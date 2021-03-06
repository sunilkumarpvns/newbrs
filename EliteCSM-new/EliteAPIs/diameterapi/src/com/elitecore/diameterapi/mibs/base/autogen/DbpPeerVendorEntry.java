package com.elitecore.diameterapi.mibs.base.autogen;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling DIAMETER-BASE-PROTOCOL-MIB.
//

// java imports
//
import java.io.Serializable;

// jmx imports
//
import com.sun.management.snmp.SnmpStatusException;

// jdmk imports
//
import com.sun.management.snmp.agent.SnmpMib;

/**
 * The class is used for implementing the "DbpPeerVendorEntry" group.
 * The group is defined with the following oid: 1.3.6.1.2.1.119.1.3.4.1.
 */
public class DbpPeerVendorEntry implements DbpPeerVendorEntryMBean, Serializable {

    /**
     * Variable for storing the value of "DbpPeerVendorRowStatus".
     * The variable is identified by: "1.3.6.1.2.1.119.1.3.4.1.4".
     *
     * "The status of this conceptual row.
     * 
     * To create a row in this table, a manager must
     * set this object to either createAndGo(4) or
     * createAndWait(5).
     * 
     * Until instances of all corresponding columns are
     * appropriately configured, the value of the
     * corresponding instance of the dbpPeerVendorRowStatus
     * column is 'notReady'.
     * 
     * In particular, a newly created row cannot be made
     * active until the corresponding dbpPeerVendorId has been
     * set. Also, a newly created row cannot be made active
     * until the corresponding 'dbpPeerIndex' has been set.
     * 
     * dbpPeerVendorId may not be modified while the
     * value of this object is active(1):
     * An attempt to set these objects while the value of
     * dbpPeerVendorRowStatus is active(1) will result in
     * an inconsistentValue error.
     * 
     * Entries in this table with dbpPeerVendorRowStatus equal
     * to active(1) remain in the table until destroyed.
     * 
     * Entries in this table with dbpPeerVendorRowStatus equal
     * to values other than active(1) will be destroyed after
     * timeout (5 minutes)."
     *
     */
    protected EnumDbpPeerVendorRowStatus DbpPeerVendorRowStatus = new EnumDbpPeerVendorRowStatus();

    /**
     * Variable for storing the value of "DbpPeerVendorStorageType".
     * The variable is identified by: "1.3.6.1.2.1.119.1.3.4.1.3".
     *
     * "The storage type for this conceptual row.
     * None of the objects are writable when the
     * conceptual row is permanent."
     *
     */
    protected EnumDbpPeerVendorStorageType DbpPeerVendorStorageType = new EnumDbpPeerVendorStorageType("nonVolatile");

    /**
     * Variable for storing the value of "DbpPeerVendorId".
     * The variable is identified by: "1.3.6.1.2.1.119.1.3.4.1.2".
     *
     * "The active vendor ID used for peer connections.
     * diameterVendorIetf (0)           -- IETF
     * diameterVendorCisco (9)         -- Cisco Systems
     * diameterVendor3gpp (10415)       --  3GPP
     * diameterVendorVodafone (12645)     --- Vodafone"
     *
     */
    protected EnumDbpPeerVendorId DbpPeerVendorId = new EnumDbpPeerVendorId("diameterVendorIetf");

    /**
     * Variable for storing the value of "DbpPeerVendorIndex".
     * The variable is identified by: "1.3.6.1.2.1.119.1.3.4.1.1".
     *
     * "A number uniquely identifying the Vendor
     * ID supported by the peer. Upon reload,
     * dbpPeerVendorIndex values may be changed."
     *
     */
    protected Long DbpPeerVendorIndex = new Long(1);

    /**
     * Variable for storing the value of "DbpPeerIndex".
     * The variable is identified by: "1.3.6.1.2.1.119.1.3.1.1.1".
     *
     * "A number uniquely identifying each Diameter peer
     * with which the local peer communicates.
     * Upon reload, dbpPeerIndex values may be changed."
     *
     */
    protected Long DbpPeerIndex = new Long(1);


    /**
     * Constructor for the "DbpPeerVendorEntry" group.
     */
    public DbpPeerVendorEntry(SnmpMib myMib) {
    }

    /**
     * Getter for the "DbpPeerVendorRowStatus" variable.
     */
    public EnumDbpPeerVendorRowStatus getDbpPeerVendorRowStatus() throws SnmpStatusException {
        return DbpPeerVendorRowStatus;
    }

    /**
     * Setter for the "DbpPeerVendorRowStatus" variable.
     * NB: There is no check method generated for RowStatus.
     *      Override checkRowStatusChange on SnmpMibTable if needed.
     */
    public void setDbpPeerVendorRowStatus(EnumDbpPeerVendorRowStatus x) throws SnmpStatusException {
        DbpPeerVendorRowStatus = x;
    }

    /**
     * Getter for the "DbpPeerVendorStorageType" variable.
     */
    public EnumDbpPeerVendorStorageType getDbpPeerVendorStorageType() throws SnmpStatusException {
        return DbpPeerVendorStorageType;
    }

    /**
     * Setter for the "DbpPeerVendorStorageType" variable.
     */
    public void setDbpPeerVendorStorageType(EnumDbpPeerVendorStorageType x) throws SnmpStatusException {
        DbpPeerVendorStorageType = x;
    }

    /**
     * Checker for the "DbpPeerVendorStorageType" variable.
     */
    public void checkDbpPeerVendorStorageType(EnumDbpPeerVendorStorageType x) throws SnmpStatusException {
        //
        // Add your own checking policy.
        //
    }

    /**
     * Getter for the "DbpPeerVendorId" variable.
     */
    public EnumDbpPeerVendorId getDbpPeerVendorId() throws SnmpStatusException {
        return DbpPeerVendorId;
    }

    /**
     * Setter for the "DbpPeerVendorId" variable.
     */
    public void setDbpPeerVendorId(EnumDbpPeerVendorId x) throws SnmpStatusException {
        DbpPeerVendorId = x;
    }

    /**
     * Checker for the "DbpPeerVendorId" variable.
     */
    public void checkDbpPeerVendorId(EnumDbpPeerVendorId x) throws SnmpStatusException {
        //
        // Add your own checking policy.
        //
    }

    /**
     * Getter for the "DbpPeerVendorIndex" variable.
     */
    public Long getDbpPeerVendorIndex() throws SnmpStatusException {
        return DbpPeerVendorIndex;
    }

    /**
     * Getter for the "DbpPeerIndex" variable.
     */
    public Long getDbpPeerIndex() throws SnmpStatusException {
        return DbpPeerIndex;
    }

}
