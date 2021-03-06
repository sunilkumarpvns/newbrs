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
 * The class is used for implementing the "EgpNeighEntry" group.
 * The group is defined with the following oid: 1.3.6.1.2.1.8.5.1.
 */
public class EgpNeighEntry implements EgpNeighEntryMBean, Serializable {

    /**
     * Variable for storing the value of "EgpNeighOutErrMsgs".
     * The variable is identified by: "1.3.6.1.2.1.8.5.1.9".
     *
     * "The number of EGP-defined error messages sent to
     * this EGP peer."
     *
     */
    protected Long EgpNeighOutErrMsgs = new Long(1);

    /**
     * Variable for storing the value of "EgpNeighInErrMsgs".
     * The variable is identified by: "1.3.6.1.2.1.8.5.1.8".
     *
     * "The number of EGP-defined error messages received
     * from this EGP peer."
     *
     */
    protected Long EgpNeighInErrMsgs = new Long(1);

    /**
     * Variable for storing the value of "EgpNeighOutErrs".
     * The variable is identified by: "1.3.6.1.2.1.8.5.1.7".
     *
     * "The number of locally generated EGP messages not
     * sent to this EGP peer due to resource limitations
     * within an EGP entity."
     *
     */
    protected Long EgpNeighOutErrs = new Long(1);

    /**
     * Variable for storing the value of "EgpNeighEventTrigger".
     * The variable is identified by: "1.3.6.1.2.1.8.5.1.15".
     *
     * "A control variable used to trigger operator-
     * initiated Start and Stop events.  When read, this
     * variable always returns the most recent value that
     * egpNeighEventTrigger was set to.  If it has not
     * been set since the last initialization of the
     * network management subsystem on the node, it
     * returns a value of `stop'.
     * 
     * When set, this variable causes a Start or Stop
     * event on the specified neighbor, as specified on
     * pages 8-10 of RFC 904.  Briefly, a Start event
     * causes an Idle peer to begin neighbor acquisition
     * and a non-Idle peer to reinitiate neighbor
     * acquisition.  A stop event causes a non-Idle peer
     * to return to the Idle state until a Start event
     * occurs, either via egpNeighEventTrigger or
     * otherwise."
     *
     */
    protected EnumEgpNeighEventTrigger EgpNeighEventTrigger = new EnumEgpNeighEventTrigger();

    /**
     * Variable for storing the value of "EgpNeighOutMsgs".
     * The variable is identified by: "1.3.6.1.2.1.8.5.1.6".
     *
     * "The number of locally generated EGP messages to
     * this EGP peer."
     *
     */
    protected Long EgpNeighOutMsgs = new Long(1);

    /**
     * Variable for storing the value of "EgpNeighMode".
     * The variable is identified by: "1.3.6.1.2.1.8.5.1.14".
     *
     * "The polling mode of this EGP entity, either
     * passive or active."
     *
     */
    protected EnumEgpNeighMode EgpNeighMode = new EnumEgpNeighMode();

    /**
     * Variable for storing the value of "EgpNeighInErrs".
     * The variable is identified by: "1.3.6.1.2.1.8.5.1.5".
     *
     * "The number of EGP messages received from this EGP
     * peer that proved to be in error (e.g., bad EGP
     * checksum)."
     *
     */
    protected Long EgpNeighInErrs = new Long(1);

    /**
     * Variable for storing the value of "EgpNeighIntervalPoll".
     * The variable is identified by: "1.3.6.1.2.1.8.5.1.13".
     *
     * "The interval between EGP poll command
     * retransmissions (in hundredths of a second).  This
     * represents the t3 timer as defined in RFC 904."
     *
     */
    protected Integer EgpNeighIntervalPoll = new Integer(1);

    /**
     * Variable for storing the value of "EgpNeighInMsgs".
     * The variable is identified by: "1.3.6.1.2.1.8.5.1.4".
     *
     * "The number of EGP messages received without error
     * from this EGP peer."
     *
     */
    protected Long EgpNeighInMsgs = new Long(1);

    /**
     * Variable for storing the value of "EgpNeighAs".
     * The variable is identified by: "1.3.6.1.2.1.8.5.1.3".
     *
     * "The autonomous system of this EGP peer.  Zero
     * should be specified if the autonomous system
     * number of the neighbor is not yet known."
     *
     */
    protected Integer EgpNeighAs = new Integer(1);

    /**
     * Variable for storing the value of "EgpNeighIntervalHello".
     * The variable is identified by: "1.3.6.1.2.1.8.5.1.12".
     *
     * "The interval between EGP Hello command
     * retransmissions (in hundredths of a second).  This
     * represents the t1 timer as defined in RFC 904."
     *
     */
    protected Integer EgpNeighIntervalHello = new Integer(1);

    /**
     * Variable for storing the value of "EgpNeighAddr".
     * The variable is identified by: "1.3.6.1.2.1.8.5.1.2".
     *
     * "The IP address of this entry's EGP neighbor."
     *
     */
    protected String EgpNeighAddr = new String("192.9.9.100");

    /**
     * Variable for storing the value of "EgpNeighStateDowns".
     * The variable is identified by: "1.3.6.1.2.1.8.5.1.11".
     *
     * "The number of EGP state transitions from the UP
     * state to any other state with this EGP peer."
     *
     */
    protected Long EgpNeighStateDowns = new Long(1);

    /**
     * Variable for storing the value of "EgpNeighStateUps".
     * The variable is identified by: "1.3.6.1.2.1.8.5.1.10".
     *
     * "The number of EGP state transitions to the UP
     * state with this EGP peer."
     *
     */
    protected Long EgpNeighStateUps = new Long(1);

    /**
     * Variable for storing the value of "EgpNeighState".
     * The variable is identified by: "1.3.6.1.2.1.8.5.1.1".
     *
     * "The EGP state of the local system with respect to
     * this entry's EGP neighbor.  Each EGP state is
     * represented by a value that is one greater than
     * the numerical value associated with said state in
     * RFC 904."
     *
     */
    protected EnumEgpNeighState EgpNeighState = new EnumEgpNeighState();


    /**
     * Constructor for the "EgpNeighEntry" group.
     */
    public EgpNeighEntry(SnmpMib myMib) {
    }

    /**
     * Getter for the "EgpNeighOutErrMsgs" variable.
     */
    public Long getEgpNeighOutErrMsgs() throws SnmpStatusException {
        return EgpNeighOutErrMsgs;
    }

    /**
     * Getter for the "EgpNeighInErrMsgs" variable.
     */
    public Long getEgpNeighInErrMsgs() throws SnmpStatusException {
        return EgpNeighInErrMsgs;
    }

    /**
     * Getter for the "EgpNeighOutErrs" variable.
     */
    public Long getEgpNeighOutErrs() throws SnmpStatusException {
        return EgpNeighOutErrs;
    }

    /**
     * Getter for the "EgpNeighEventTrigger" variable.
     */
    public EnumEgpNeighEventTrigger getEgpNeighEventTrigger() throws SnmpStatusException {
        return EgpNeighEventTrigger;
    }

    /**
     * Setter for the "EgpNeighEventTrigger" variable.
     */
    public void setEgpNeighEventTrigger(EnumEgpNeighEventTrigger x) throws SnmpStatusException {
        EgpNeighEventTrigger = x;
    }

    /**
     * Checker for the "EgpNeighEventTrigger" variable.
     */
    public void checkEgpNeighEventTrigger(EnumEgpNeighEventTrigger x) throws SnmpStatusException {
        //
        // Add your own checking policy.
        //
    }

    /**
     * Getter for the "EgpNeighOutMsgs" variable.
     */
    public Long getEgpNeighOutMsgs() throws SnmpStatusException {
        return EgpNeighOutMsgs;
    }

    /**
     * Getter for the "EgpNeighMode" variable.
     */
    public EnumEgpNeighMode getEgpNeighMode() throws SnmpStatusException {
        return EgpNeighMode;
    }

    /**
     * Getter for the "EgpNeighInErrs" variable.
     */
    public Long getEgpNeighInErrs() throws SnmpStatusException {
        return EgpNeighInErrs;
    }

    /**
     * Getter for the "EgpNeighIntervalPoll" variable.
     */
    public Integer getEgpNeighIntervalPoll() throws SnmpStatusException {
        return EgpNeighIntervalPoll;
    }

    /**
     * Getter for the "EgpNeighInMsgs" variable.
     */
    public Long getEgpNeighInMsgs() throws SnmpStatusException {
        return EgpNeighInMsgs;
    }

    /**
     * Getter for the "EgpNeighAs" variable.
     */
    public Integer getEgpNeighAs() throws SnmpStatusException {
        return EgpNeighAs;
    }

    /**
     * Getter for the "EgpNeighIntervalHello" variable.
     */
    public Integer getEgpNeighIntervalHello() throws SnmpStatusException {
        return EgpNeighIntervalHello;
    }

    /**
     * Getter for the "EgpNeighAddr" variable.
     */
    public String getEgpNeighAddr() throws SnmpStatusException {
        return EgpNeighAddr;
    }

    /**
     * Getter for the "EgpNeighStateDowns" variable.
     */
    public Long getEgpNeighStateDowns() throws SnmpStatusException {
        return EgpNeighStateDowns;
    }

    /**
     * Getter for the "EgpNeighStateUps" variable.
     */
    public Long getEgpNeighStateUps() throws SnmpStatusException {
        return EgpNeighStateUps;
    }

    /**
     * Getter for the "EgpNeighState" variable.
     */
    public EnumEgpNeighState getEgpNeighState() throws SnmpStatusException {
        return EgpNeighState;
    }

}
