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
 * The class is used for implementing the "IfEntry" group.
 * The group is defined with the following oid: 1.3.6.1.2.1.2.2.1.
 */
public class IfEntry implements IfEntryMBean, Serializable {

    /**
     * Variable for storing the value of "IfOutDiscards".
     * The variable is identified by: "1.3.6.1.2.1.2.2.1.19".
     *
     * "The number of outbound packets which were chosen
     * to be discarded even though no errors had been
     * detected to prevent their being transmitted.  One
     * possible reason for discarding such a packet could
     * be to free up buffer space."
     *
     */
    protected Long IfOutDiscards = new Long(1);

    /**
     * Variable for storing the value of "IfOutNUcastPkts".
     * The variable is identified by: "1.3.6.1.2.1.2.2.1.18".
     *
     * "The total number of packets that higher-level
     * protocols requested be transmitted to a non-
     * unicast (i.e., a subnetwork-broadcast or
     * subnetwork-multicast) address, including those
     * that were discarded or not sent."
     *
     */
    protected Long IfOutNUcastPkts = new Long(1);

    /**
     * Variable for storing the value of "IfOutUcastPkts".
     * The variable is identified by: "1.3.6.1.2.1.2.2.1.17".
     *
     * "The total number of packets that higher-level
     * protocols requested be transmitted to a
     * subnetwork-unicast address, including those that
     * were discarded or not sent."
     *
     */
    protected Long IfOutUcastPkts = new Long(1);

    /**
     * Variable for storing the value of "IfOutOctets".
     * The variable is identified by: "1.3.6.1.2.1.2.2.1.16".
     *
     * "The total number of octets transmitted out of the
     * interface, including framing characters."
     *
     */
    protected Long IfOutOctets = new Long(1);

    /**
     * Variable for storing the value of "IfInUnknownProtos".
     * The variable is identified by: "1.3.6.1.2.1.2.2.1.15".
     *
     * "The number of packets received via the interface
     * which were discarded because of an unknown or
     * unsupported protocol."
     *
     */
    protected Long IfInUnknownProtos = new Long(1);

    /**
     * Variable for storing the value of "IfInErrors".
     * The variable is identified by: "1.3.6.1.2.1.2.2.1.14".
     *
     * "The number of inbound packets that contained
     * errors preventing them from being deliverable to a
     * higher-layer protocol."
     *
     */
    protected Long IfInErrors = new Long(1);

    /**
     * Variable for storing the value of "IfInDiscards".
     * The variable is identified by: "1.3.6.1.2.1.2.2.1.13".
     *
     * "The number of inbound packets which were chosen
     * to be discarded even though no errors had been
     * detected to prevent their being deliverable to a
     * higher-layer protocol.  One possible reason for
     * discarding such a packet could be to free up
     * buffer space."
     *
     */
    protected Long IfInDiscards = new Long(1);

    /**
     * Variable for storing the value of "IfInNUcastPkts".
     * The variable is identified by: "1.3.6.1.2.1.2.2.1.12".
     *
     * "The number of non-unicast (i.e., subnetwork-
     * broadcast or subnetwork-multicast) packets
     * delivered to a higher-layer protocol."
     *
     */
    protected Long IfInNUcastPkts = new Long(1);

    /**
     * Variable for storing the value of "IfInUcastPkts".
     * The variable is identified by: "1.3.6.1.2.1.2.2.1.11".
     *
     * "The number of subnetwork-unicast packets
     * delivered to a higher-layer protocol."
     *
     */
    protected Long IfInUcastPkts = new Long(1);

    /**
     * Variable for storing the value of "IfInOctets".
     * The variable is identified by: "1.3.6.1.2.1.2.2.1.10".
     *
     * "The total number of octets received on the
     * interface, including framing characters."
     *
     */
    protected Long IfInOctets = new Long(1);

    /**
     * Variable for storing the value of "IfLastChange".
     * The variable is identified by: "1.3.6.1.2.1.2.2.1.9".
     *
     * "The value of sysUpTime at the time the interface
     * entered its current operational state.  If the
     * current state was entered prior to the last re-
     * initialization of the local network management
     * subsystem, then this object contains a zero
     * value."
     *
     */
    protected Long IfLastChange = new Long(1);

    /**
     * Variable for storing the value of "IfOperStatus".
     * The variable is identified by: "1.3.6.1.2.1.2.2.1.8".
     *
     * "The current operational state of the interface.
     * The testing(3) state indicates that no operational
     * packets can be passed."
     *
     */
    protected EnumIfOperStatus IfOperStatus = new EnumIfOperStatus();

    /**
     * Variable for storing the value of "IfAdminStatus".
     * The variable is identified by: "1.3.6.1.2.1.2.2.1.7".
     *
     * "The desired state of the interface.  The
     * testing(3) state indicates that no operational
     * packets can be passed."
     *
     */
    protected EnumIfAdminStatus IfAdminStatus = new EnumIfAdminStatus();

    /**
     * Variable for storing the value of "IfPhysAddress".
     * The variable is identified by: "1.3.6.1.2.1.2.2.1.6".
     *
     * "The interface's address at the protocol layer
     * immediately `below' the network layer in the
     * protocol stack.  For interfaces which do not have
     * such an address (e.g., a serial line), this object
     * should contain an octet string of zero length."
     *
     */
    protected Byte[] IfPhysAddress = { new Byte("74"), new Byte("68"), new Byte("77"), new Byte("75")};

    /**
     * Variable for storing the value of "IfSpeed".
     * The variable is identified by: "1.3.6.1.2.1.2.2.1.5".
     *
     * "An estimate of the interface's current bandwidth
     * in bits per second.  For interfaces which do not
     * vary in bandwidth or for those where no accurate
     * estimation can be made, this object should contain
     * the nominal bandwidth."
     *
     */
    protected Long IfSpeed = new Long(1);

    /**
     * Variable for storing the value of "IfMtu".
     * The variable is identified by: "1.3.6.1.2.1.2.2.1.4".
     *
     * "The size of the largest datagram which can be
     * sent/received on the interface, specified in
     * octets.  For interfaces that are used for
     * transmitting network datagrams, this is the size
     * of the largest network datagram that can be sent
     * on the interface."
     *
     */
    protected Integer IfMtu = new Integer(1);

    /**
     * Variable for storing the value of "IfType".
     * The variable is identified by: "1.3.6.1.2.1.2.2.1.3".
     *
     * "The type of interface, distinguished according to
     * the physical/link protocol(s) immediately `below'
     * the network layer in the protocol stack."
     *
     */
    protected EnumIfType IfType = new EnumIfType();

    /**
     * Variable for storing the value of "IfDescr".
     * The variable is identified by: "1.3.6.1.2.1.2.2.1.2".
     *
     * "A textual string containing information about the
     * interface.  This string should include the name of
     * the manufacturer, the product name and the version
     * of the hardware interface."
     *
     */
    protected String IfDescr = new String("JDMK 5.1");

    /**
     * Variable for storing the value of "IfIndex".
     * The variable is identified by: "1.3.6.1.2.1.2.2.1.1".
     *
     * "A unique value for each interface.  Its value
     * ranges between 1 and the value of ifNumber.  The
     * value for each interface must remain constant at
     * least from one re-initialization of the entity's
     * network management system to the next re-
     * initialization."
     *
     */
    protected Integer IfIndex = new Integer(1);

    /**
     * Variable for storing the value of "IfSpecific".
     * The variable is identified by: "1.3.6.1.2.1.2.2.1.22".
     *
     * "A reference to MIB definitions specific to the
     * particular media being used to realize the
     * interface.  For example, if the interface is
     * realized by an ethernet, then the value of this
     * object refers to a document defining objects
     * specific to ethernet.  If this information is not
     * present, its value should be set to the OBJECT
     * IDENTIFIER { 0 0 }, which is a syntatically valid
     * object identifier, and any conformant
     * implementation of ASN.1 and BER must be able to
     * generate and recognize this value."
     *
     */
    protected String IfSpecific = new String("1.3.6.1.4.1.42");

    /**
     * Variable for storing the value of "IfOutQLen".
     * The variable is identified by: "1.3.6.1.2.1.2.2.1.21".
     *
     * "The length of the output packet queue (in
     * packets)."
     *
     */
    protected Long IfOutQLen = new Long(1);

    /**
     * Variable for storing the value of "IfOutErrors".
     * The variable is identified by: "1.3.6.1.2.1.2.2.1.20".
     *
     * "The number of outbound packets that could not be
     * transmitted because of errors."
     *
     */
    protected Long IfOutErrors = new Long(1);


    /**
     * Constructor for the "IfEntry" group.
     */
    public IfEntry(SnmpMib myMib) {
    }

    /**
     * Getter for the "IfOutDiscards" variable.
     */
    public Long getIfOutDiscards() throws SnmpStatusException {
        return IfOutDiscards;
    }

    /**
     * Getter for the "IfOutNUcastPkts" variable.
     */
    public Long getIfOutNUcastPkts() throws SnmpStatusException {
        return IfOutNUcastPkts;
    }

    /**
     * Getter for the "IfOutUcastPkts" variable.
     */
    public Long getIfOutUcastPkts() throws SnmpStatusException {
        return IfOutUcastPkts;
    }

    /**
     * Getter for the "IfOutOctets" variable.
     */
    public Long getIfOutOctets() throws SnmpStatusException {
        return IfOutOctets;
    }

    /**
     * Getter for the "IfInUnknownProtos" variable.
     */
    public Long getIfInUnknownProtos() throws SnmpStatusException {
        return IfInUnknownProtos;
    }

    /**
     * Getter for the "IfInErrors" variable.
     */
    public Long getIfInErrors() throws SnmpStatusException {
        return IfInErrors;
    }

    /**
     * Getter for the "IfInDiscards" variable.
     */
    public Long getIfInDiscards() throws SnmpStatusException {
        return IfInDiscards;
    }

    /**
     * Getter for the "IfInNUcastPkts" variable.
     */
    public Long getIfInNUcastPkts() throws SnmpStatusException {
        return IfInNUcastPkts;
    }

    /**
     * Getter for the "IfInUcastPkts" variable.
     */
    public Long getIfInUcastPkts() throws SnmpStatusException {
        return IfInUcastPkts;
    }

    /**
     * Getter for the "IfInOctets" variable.
     */
    public Long getIfInOctets() throws SnmpStatusException {
        return IfInOctets;
    }

    /**
     * Getter for the "IfLastChange" variable.
     */
    public Long getIfLastChange() throws SnmpStatusException {
        return IfLastChange;
    }

    /**
     * Getter for the "IfOperStatus" variable.
     */
    public EnumIfOperStatus getIfOperStatus() throws SnmpStatusException {
        return IfOperStatus;
    }

    /**
     * Getter for the "IfAdminStatus" variable.
     */
    public EnumIfAdminStatus getIfAdminStatus() throws SnmpStatusException {
        return IfAdminStatus;
    }

    /**
     * Setter for the "IfAdminStatus" variable.
     */
    public void setIfAdminStatus(EnumIfAdminStatus x) throws SnmpStatusException {
        IfAdminStatus = x;
    }

    /**
     * Checker for the "IfAdminStatus" variable.
     */
    public void checkIfAdminStatus(EnumIfAdminStatus x) throws SnmpStatusException {
        //
        // Add your own checking policy.
        //
    }

    /**
     * Getter for the "IfPhysAddress" variable.
     */
    public Byte[] getIfPhysAddress() throws SnmpStatusException {
        return IfPhysAddress;
    }

    /**
     * Getter for the "IfSpeed" variable.
     */
    public Long getIfSpeed() throws SnmpStatusException {
        return IfSpeed;
    }

    /**
     * Getter for the "IfMtu" variable.
     */
    public Integer getIfMtu() throws SnmpStatusException {
        return IfMtu;
    }

    /**
     * Getter for the "IfType" variable.
     */
    public EnumIfType getIfType() throws SnmpStatusException {
        return IfType;
    }

    /**
     * Getter for the "IfDescr" variable.
     */
    public String getIfDescr() throws SnmpStatusException {
        return IfDescr;
    }

    /**
     * Getter for the "IfIndex" variable.
     */
    public Integer getIfIndex() throws SnmpStatusException {
        return IfIndex;
    }

    /**
     * Getter for the "IfSpecific" variable.
     */
    public String getIfSpecific() throws SnmpStatusException {
        return IfSpecific;
    }

    /**
     * Getter for the "IfOutQLen" variable.
     */
    public Long getIfOutQLen() throws SnmpStatusException {
        return IfOutQLen;
    }

    /**
     * Getter for the "IfOutErrors" variable.
     */
    public Long getIfOutErrors() throws SnmpStatusException {
        return IfOutErrors;
    }

}