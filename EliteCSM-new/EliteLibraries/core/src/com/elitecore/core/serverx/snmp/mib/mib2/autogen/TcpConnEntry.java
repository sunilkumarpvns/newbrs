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
 * The class is used for implementing the "TcpConnEntry" group.
 * The group is defined with the following oid: 1.3.6.1.2.1.6.13.1.
 */
public class TcpConnEntry implements TcpConnEntryMBean, Serializable {

    /**
     * Variable for storing the value of "TcpConnRemPort".
     * The variable is identified by: "1.3.6.1.2.1.6.13.1.5".
     *
     * "The remote port number for this TCP connection."
     *
     */
    protected Integer TcpConnRemPort = new Integer(1);

    /**
     * Variable for storing the value of "TcpConnRemAddress".
     * The variable is identified by: "1.3.6.1.2.1.6.13.1.4".
     *
     * "The remote IP address for this TCP connection."
     *
     */
    protected String TcpConnRemAddress = new String("192.9.9.100");

    /**
     * Variable for storing the value of "TcpConnLocalPort".
     * The variable is identified by: "1.3.6.1.2.1.6.13.1.3".
     *
     * "The local port number for this TCP connection."
     *
     */
    protected Integer TcpConnLocalPort = new Integer(1);

    /**
     * Variable for storing the value of "TcpConnLocalAddress".
     * The variable is identified by: "1.3.6.1.2.1.6.13.1.2".
     *
     * "The local IP address for this TCP connection.  In
     * the case of a connection in the listen state which
     * is willing to accept connections for any IP
     * interface associated with the node, the value
     * 0.0.0.0 is used."
     *
     */
    protected String TcpConnLocalAddress = new String("192.9.9.100");

    /**
     * Variable for storing the value of "TcpConnState".
     * The variable is identified by: "1.3.6.1.2.1.6.13.1.1".
     *
     * "The state of this TCP connection.
     * 
     * The only value which may be set by a management
     * station is deleteTCB(12).  Accordingly, it is
     * appropriate for an agent to return a `badValue'
     * response if a management station attempts to set
     * this object to any other value.
     * 
     * If a management station sets this object to the
     * value deleteTCB(12), then this has the effect of
     * deleting the TCB (as defined in RFC 793) of the
     * corresponding connection on the managed node,
     * resulting in immediate termination of the
     * connection.
     * 
     * As an implementation-specific option, a RST
     * segment may be sent from the managed node to the
     * other TCP endpoint (note however that RST segments
     * are not sent reliably)."
     *
     */
    protected EnumTcpConnState TcpConnState = new EnumTcpConnState();


    /**
     * Constructor for the "TcpConnEntry" group.
     */
    public TcpConnEntry(SnmpMib myMib) {
    }

    /**
     * Getter for the "TcpConnRemPort" variable.
     */
    public Integer getTcpConnRemPort() throws SnmpStatusException {
        return TcpConnRemPort;
    }

    /**
     * Getter for the "TcpConnRemAddress" variable.
     */
    public String getTcpConnRemAddress() throws SnmpStatusException {
        return TcpConnRemAddress;
    }

    /**
     * Getter for the "TcpConnLocalPort" variable.
     */
    public Integer getTcpConnLocalPort() throws SnmpStatusException {
        return TcpConnLocalPort;
    }

    /**
     * Getter for the "TcpConnLocalAddress" variable.
     */
    public String getTcpConnLocalAddress() throws SnmpStatusException {
        return TcpConnLocalAddress;
    }

    /**
     * Getter for the "TcpConnState" variable.
     */
    public EnumTcpConnState getTcpConnState() throws SnmpStatusException {
        return TcpConnState;
    }

    /**
     * Setter for the "TcpConnState" variable.
     */
    public void setTcpConnState(EnumTcpConnState x) throws SnmpStatusException {
        TcpConnState = x;
    }

    /**
     * Checker for the "TcpConnState" variable.
     */
    public void checkTcpConnState(EnumTcpConnState x) throws SnmpStatusException {
        //
        // Add your own checking policy.
        //
    }

}
