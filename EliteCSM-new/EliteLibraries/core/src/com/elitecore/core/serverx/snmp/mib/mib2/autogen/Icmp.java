package com.elitecore.core.serverx.snmp.mib.mib2.autogen;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling RFC1213-MIB.
//

// java imports
//
import java.io.Serializable;

import javax.management.MBeanServer;

import com.sun.management.snmp.SnmpStatusException;
import com.sun.management.snmp.agent.SnmpMib;

/**
 * The class is used for implementing the "Icmp" group.
 * The group is defined with the following oid: 1.3.6.1.2.1.5.
 */
public class Icmp implements IcmpMBean, Serializable {

    /**
     * Variable for storing the value of "IcmpOutSrcQuenchs".
     * The variable is identified by: "1.3.6.1.2.1.5.19".
     *
     * "The number of ICMP Source Quench messages sent."
     *
     */
    protected Long IcmpOutSrcQuenchs = new Long(1);

    /**
     * Variable for storing the value of "IcmpOutParmProbs".
     * The variable is identified by: "1.3.6.1.2.1.5.18".
     *
     * "The number of ICMP Parameter Problem messages
     * sent."
     *
     */
    protected Long IcmpOutParmProbs = new Long(1);

    /**
     * Variable for storing the value of "IcmpOutTimeExcds".
     * The variable is identified by: "1.3.6.1.2.1.5.17".
     *
     * "The number of ICMP Time Exceeded messages sent."
     *
     */
    protected Long IcmpOutTimeExcds = new Long(1);

    /**
     * Variable for storing the value of "IcmpOutDestUnreachs".
     * The variable is identified by: "1.3.6.1.2.1.5.16".
     *
     * "The number of ICMP Destination Unreachable
     * messages sent."
     *
     */
    protected Long IcmpOutDestUnreachs = new Long(1);

    /**
     * Variable for storing the value of "IcmpOutErrors".
     * The variable is identified by: "1.3.6.1.2.1.5.15".
     *
     * "The number of ICMP messages which this entity did
     * not send due to problems discovered within ICMP
     * such as a lack of buffers.  This value should not
     * include errors discovered outside the ICMP layer
     * such as the inability of IP to route the resultant
     * datagram.  In some implementations there may be no
     * types of error which contribute to this counter's
     * value."
     *
     */
    protected Long IcmpOutErrors = new Long(1);

    /**
     * Variable for storing the value of "IcmpOutMsgs".
     * The variable is identified by: "1.3.6.1.2.1.5.14".
     *
     * "The total number of ICMP messages which this
     * entity attempted to send.  Note that this counter
     * includes all those counted by icmpOutErrors."
     *
     */
    protected Long IcmpOutMsgs = new Long(1);

    /**
     * Variable for storing the value of "IcmpInAddrMaskReps".
     * The variable is identified by: "1.3.6.1.2.1.5.13".
     *
     * "The number of ICMP Address Mask Reply messages
     * received."
     *
     */
    protected Long IcmpInAddrMaskReps = new Long(1);

    /**
     * Variable for storing the value of "IcmpInAddrMasks".
     * The variable is identified by: "1.3.6.1.2.1.5.12".
     *
     * "The number of ICMP Address Mask Request messages
     * received."
     *
     */
    protected Long IcmpInAddrMasks = new Long(1);

    /**
     * Variable for storing the value of "IcmpInTimestampReps".
     * The variable is identified by: "1.3.6.1.2.1.5.11".
     *
     * "The number of ICMP Timestamp Reply messages
     * received."
     *
     */
    protected Long IcmpInTimestampReps = new Long(1);

    /**
     * Variable for storing the value of "IcmpInTimestamps".
     * The variable is identified by: "1.3.6.1.2.1.5.10".
     *
     * "The number of ICMP Timestamp (request) messages
     * received."
     *
     */
    protected Long IcmpInTimestamps = new Long(1);

    /**
     * Variable for storing the value of "IcmpInEchoReps".
     * The variable is identified by: "1.3.6.1.2.1.5.9".
     *
     * "The number of ICMP Echo Reply messages received."
     *
     */
    protected Long IcmpInEchoReps = new Long(1);

    /**
     * Variable for storing the value of "IcmpInEchos".
     * The variable is identified by: "1.3.6.1.2.1.5.8".
     *
     * "The number of ICMP Echo (request) messages
     * received."
     *
     */
    protected Long IcmpInEchos = new Long(1);

    /**
     * Variable for storing the value of "IcmpInRedirects".
     * The variable is identified by: "1.3.6.1.2.1.5.7".
     *
     * "The number of ICMP Redirect messages received."
     *
     */
    protected Long IcmpInRedirects = new Long(1);

    /**
     * Variable for storing the value of "IcmpInSrcQuenchs".
     * The variable is identified by: "1.3.6.1.2.1.5.6".
     *
     * "The number of ICMP Source Quench messages
     * received."
     *
     */
    protected Long IcmpInSrcQuenchs = new Long(1);

    /**
     * Variable for storing the value of "IcmpInParmProbs".
     * The variable is identified by: "1.3.6.1.2.1.5.5".
     *
     * "The number of ICMP Parameter Problem messages
     * received."
     *
     */
    protected Long IcmpInParmProbs = new Long(1);

    /**
     * Variable for storing the value of "IcmpOutAddrMaskReps".
     * The variable is identified by: "1.3.6.1.2.1.5.26".
     *
     * "The number of ICMP Address Mask Reply messages
     * sent."
     *
     */
    protected Long IcmpOutAddrMaskReps = new Long(1);

    /**
     * Variable for storing the value of "IcmpInTimeExcds".
     * The variable is identified by: "1.3.6.1.2.1.5.4".
     *
     * "The number of ICMP Time Exceeded messages
     * received."
     *
     */
    protected Long IcmpInTimeExcds = new Long(1);

    /**
     * Variable for storing the value of "IcmpOutAddrMasks".
     * The variable is identified by: "1.3.6.1.2.1.5.25".
     *
     * "The number of ICMP Address Mask Request messages
     * sent."
     *
     */
    protected Long IcmpOutAddrMasks = new Long(1);

    /**
     * Variable for storing the value of "IcmpOutTimestampReps".
     * The variable is identified by: "1.3.6.1.2.1.5.24".
     *
     * "The number of ICMP Timestamp Reply messages
     * sent."
     *
     */
    protected Long IcmpOutTimestampReps = new Long(1);

    /**
     * Variable for storing the value of "IcmpInDestUnreachs".
     * The variable is identified by: "1.3.6.1.2.1.5.3".
     *
     * "The number of ICMP Destination Unreachable
     * messages received."
     *
     */
    protected Long IcmpInDestUnreachs = new Long(1);

    /**
     * Variable for storing the value of "IcmpInErrors".
     * The variable is identified by: "1.3.6.1.2.1.5.2".
     *
     * "The number of ICMP messages which the entity
     * received but determined as having ICMP-specific
     * errors (bad ICMP checksums, bad length, etc.)."
     *
     */
    protected Long IcmpInErrors = new Long(1);

    /**
     * Variable for storing the value of "IcmpOutTimestamps".
     * The variable is identified by: "1.3.6.1.2.1.5.23".
     *
     * "The number of ICMP Timestamp (request) messages
     * sent."
     *
     */
    protected Long IcmpOutTimestamps = new Long(1);

    /**
     * Variable for storing the value of "IcmpInMsgs".
     * The variable is identified by: "1.3.6.1.2.1.5.1".
     *
     * "The total number of ICMP messages which the
     * entity received.  Note that this counter includes
     * all those counted by icmpInErrors."
     *
     */
    protected Long IcmpInMsgs = new Long(1);

    /**
     * Variable for storing the value of "IcmpOutEchoReps".
     * The variable is identified by: "1.3.6.1.2.1.5.22".
     *
     * "The number of ICMP Echo Reply messages sent."
     *
     */
    protected Long IcmpOutEchoReps = new Long(1);

    /**
     * Variable for storing the value of "IcmpOutEchos".
     * The variable is identified by: "1.3.6.1.2.1.5.21".
     *
     * "The number of ICMP Echo (request) messages sent."
     *
     */
    protected Long IcmpOutEchos = new Long(1);

    /**
     * Variable for storing the value of "IcmpOutRedirects".
     * The variable is identified by: "1.3.6.1.2.1.5.20".
     *
     * "The number of ICMP Redirect messages sent.  For a
     * host, this object will always be zero, since hosts
     * do not send redirects."
     *
     */
    protected Long IcmpOutRedirects = new Long(1);


    /**
     * Constructor for the "Icmp" group.
     * If the group contains a table, the entries created through an SNMP SET will not be registered in Java DMK.
     */
    public Icmp(SnmpMib myMib) {
    }


    /**
     * Constructor for the "Icmp" group.
     * If the group contains a table, the entries created through an SNMP SET will be AUTOMATICALLY REGISTERED in Java DMK.
     */
    public Icmp(SnmpMib myMib, MBeanServer server) {
    }

    /**
     * Getter for the "IcmpOutSrcQuenchs" variable.
     */
    public Long getIcmpOutSrcQuenchs() throws SnmpStatusException {
        return IcmpOutSrcQuenchs;
    }

    /**
     * Getter for the "IcmpOutParmProbs" variable.
     */
    public Long getIcmpOutParmProbs() throws SnmpStatusException {
        return IcmpOutParmProbs;
    }

    /**
     * Getter for the "IcmpOutTimeExcds" variable.
     */
    public Long getIcmpOutTimeExcds() throws SnmpStatusException {
        return IcmpOutTimeExcds;
    }

    /**
     * Getter for the "IcmpOutDestUnreachs" variable.
     */
    public Long getIcmpOutDestUnreachs() throws SnmpStatusException {
        return IcmpOutDestUnreachs;
    }

    /**
     * Getter for the "IcmpOutErrors" variable.
     */
    public Long getIcmpOutErrors() throws SnmpStatusException {
        return IcmpOutErrors;
    }

    /**
     * Getter for the "IcmpOutMsgs" variable.
     */
    public Long getIcmpOutMsgs() throws SnmpStatusException {
        return IcmpOutMsgs;
    }

    /**
     * Getter for the "IcmpInAddrMaskReps" variable.
     */
    public Long getIcmpInAddrMaskReps() throws SnmpStatusException {
        return IcmpInAddrMaskReps;
    }

    /**
     * Getter for the "IcmpInAddrMasks" variable.
     */
    public Long getIcmpInAddrMasks() throws SnmpStatusException {
        return IcmpInAddrMasks;
    }

    /**
     * Getter for the "IcmpInTimestampReps" variable.
     */
    public Long getIcmpInTimestampReps() throws SnmpStatusException {
        return IcmpInTimestampReps;
    }

    /**
     * Getter for the "IcmpInTimestamps" variable.
     */
    public Long getIcmpInTimestamps() throws SnmpStatusException {
        return IcmpInTimestamps;
    }

    /**
     * Getter for the "IcmpInEchoReps" variable.
     */
    public Long getIcmpInEchoReps() throws SnmpStatusException {
        return IcmpInEchoReps;
    }

    /**
     * Getter for the "IcmpInEchos" variable.
     */
    public Long getIcmpInEchos() throws SnmpStatusException {
        return IcmpInEchos;
    }

    /**
     * Getter for the "IcmpInRedirects" variable.
     */
    public Long getIcmpInRedirects() throws SnmpStatusException {
        return IcmpInRedirects;
    }

    /**
     * Getter for the "IcmpInSrcQuenchs" variable.
     */
    public Long getIcmpInSrcQuenchs() throws SnmpStatusException {
        return IcmpInSrcQuenchs;
    }

    /**
     * Getter for the "IcmpInParmProbs" variable.
     */
    public Long getIcmpInParmProbs() throws SnmpStatusException {
        return IcmpInParmProbs;
    }

    /**
     * Getter for the "IcmpOutAddrMaskReps" variable.
     */
    public Long getIcmpOutAddrMaskReps() throws SnmpStatusException {
        return IcmpOutAddrMaskReps;
    }

    /**
     * Getter for the "IcmpInTimeExcds" variable.
     */
    public Long getIcmpInTimeExcds() throws SnmpStatusException {
        return IcmpInTimeExcds;
    }

    /**
     * Getter for the "IcmpOutAddrMasks" variable.
     */
    public Long getIcmpOutAddrMasks() throws SnmpStatusException {
        return IcmpOutAddrMasks;
    }

    /**
     * Getter for the "IcmpOutTimestampReps" variable.
     */
    public Long getIcmpOutTimestampReps() throws SnmpStatusException {
        return IcmpOutTimestampReps;
    }

    /**
     * Getter for the "IcmpInDestUnreachs" variable.
     */
    public Long getIcmpInDestUnreachs() throws SnmpStatusException {
        return IcmpInDestUnreachs;
    }

    /**
     * Getter for the "IcmpInErrors" variable.
     */
    public Long getIcmpInErrors() throws SnmpStatusException {
        return IcmpInErrors;
    }

    /**
     * Getter for the "IcmpOutTimestamps" variable.
     */
    public Long getIcmpOutTimestamps() throws SnmpStatusException {
        return IcmpOutTimestamps;
    }

    /**
     * Getter for the "IcmpInMsgs" variable.
     */
    public Long getIcmpInMsgs() throws SnmpStatusException {
        return IcmpInMsgs;
    }

    /**
     * Getter for the "IcmpOutEchoReps" variable.
     */
    public Long getIcmpOutEchoReps() throws SnmpStatusException {
        return IcmpOutEchoReps;
    }

    /**
     * Getter for the "IcmpOutEchos" variable.
     */
    public Long getIcmpOutEchos() throws SnmpStatusException {
        return IcmpOutEchos;
    }

    /**
     * Getter for the "IcmpOutRedirects" variable.
     */
    public Long getIcmpOutRedirects() throws SnmpStatusException {
        return IcmpOutRedirects;
    }

}