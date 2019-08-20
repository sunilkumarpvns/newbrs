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
 * The class is used for implementing the "DbpAppAdvFromPeerEntry" group.
 * The group is defined with the following oid: 1.3.6.1.2.1.119.1.3.3.1.
 */
public class DbpAppAdvFromPeerEntry implements DbpAppAdvFromPeerEntryMBean, Serializable {

	public DbpAppAdvFromPeerEntry() {
	}
    /**
     * Variable for storing the value of "DbpAppAdvFromPeerType".
     * The variable is identified by: "1.3.6.1.2.1.119.1.3.3.1.3".
     *
     * "The type of services supported for each application,
     * accounting, authentication or both."
     *
     */
    protected EnumDbpAppAdvFromPeerType DbpAppAdvFromPeerType = new EnumDbpAppAdvFromPeerType();

    /**
     * Variable for storing the value of "DbpAppAdvFromPeerIndex".
     * The variable is identified by: "1.3.6.1.2.1.119.1.3.3.1.2".
     *
     * "A number uniquely identifying the applications
     * advertised as supported from each Diameter peer."
     *
     */
    protected Long DbpAppAdvFromPeerIndex = new Long(1);

    /**
     * Variable for storing the value of "DbpAppAdvFromPeerVendorId".
     * The variable is identified by: "1.3.6.1.2.1.119.1.3.3.1.1".
     *
     * "The IANA Enterprise Code value assigned to
     * the vendor of the Diameter application."
     *
     */
    protected Long DbpAppAdvFromPeerVendorId = new Long(1);

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
     * Constructor for the "DbpAppAdvFromPeerEntry" group.
     */
    public DbpAppAdvFromPeerEntry(SnmpMib myMib) {
    }

    /**
     * Getter for the "DbpAppAdvFromPeerType" variable.
     */
    public EnumDbpAppAdvFromPeerType getDbpAppAdvFromPeerType() throws SnmpStatusException {
        return DbpAppAdvFromPeerType;
    }

    /**
     * Getter for the "DbpAppAdvFromPeerIndex" variable.
     */
    public Long getDbpAppAdvFromPeerIndex() throws SnmpStatusException {
        return DbpAppAdvFromPeerIndex;
    }

    /**
     * Getter for the "DbpAppAdvFromPeerVendorId" variable.
     */
    public Long getDbpAppAdvFromPeerVendorId() throws SnmpStatusException {
        return DbpAppAdvFromPeerVendorId;
    }

    /**
     * Getter for the "DbpPeerIndex" variable.
     */
    public Long getDbpPeerIndex() throws SnmpStatusException {
        return DbpPeerIndex;
    }

}