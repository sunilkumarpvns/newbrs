package com.elitecore.diameterapi.mibs.custom.autogen;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling DIAMETER-STACK-MIB.
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
 * The class is used for implementing the "ResultCodeStatisticsEntry" group.
 * The group is defined with the following oid: 1.3.6.1.4.1.21067.5.102.3.3.1.1.
 */
public class ResultCodeStatisticsEntry implements ResultCodeStatisticsEntryMBean, Serializable {

	public ResultCodeStatisticsEntry() {
	}
	
    /**
     * Variable for storing the value of "RcwCompstIndexValue".
     * The variable is identified by: "1.3.6.1.4.1.21067.5.102.3.3.1.1.6".
     *
     * " Represents the value of composite index. using format peerIndex.AppId.ResultCode "
     *
     */
    protected String RcwCompstIndexValue = new String("JDMK 5.1");

    /**
     * Variable for storing the value of "RcwApplicationName".
     * The variable is identified by: "1.3.6.1.4.1.21067.5.102.3.3.1.1.5".
     *
     * " Name of the application "
     *
     */
    protected String RcwApplicationName = new String("JDMK 5.1");

    /**
     * Variable for storing the value of "RcwPeerIdentity".
     * The variable is identified by: "1.3.6.1.4.1.21067.5.102.3.3.1.1.4".
     *
     * " Identity of the peer "
     *
     */
    protected String RcwPeerIdentity = new String("JDMK 5.1");

    /**
     * Variable for storing the value of "RcwTx".
     * The variable is identified by: "1.3.6.1.4.1.21067.5.102.3.3.1.1.3".
     *
     * " Total number of answers with result code transmitted to peer for particular application "
     *
     */
    protected Long RcwTx = new Long(1);

    /**
     * Variable for storing the value of "RcwRx".
     * The variable is identified by: "1.3.6.1.4.1.21067.5.102.3.3.1.1.2".
     *
     * " Total number of answers with result code received from peer for particular application "
     *
     */
    protected Long RcwRx = new Long(1);

    /**
     * Variable for storing the value of "ResultCode".
     * The variable is identified by: "1.3.6.1.4.1.21067.5.102.3.3.1.1.1".
     *
     * " Local unique index composite with peerIndex and applicationID
     * for unique identification "
     *
     */
    protected Long ResultCode = new Long(1);

    /**
     * Variable for storing the value of "PeerIndex".
     * The variable is identified by: "1.3.6.1.4.1.21067.5.102.1.1.1".
     *
     * " Number uniquely identifies the Diameter Peer "
     *
     */
    protected Long PeerIndex = new Long(1);

    /**
     * Variable for storing the value of "ApplicationID".
     * The variable is identified by: "1.3.6.1.4.1.21067.5.101.1.1.1".
     *
     * " Number uniquely identifies the application "
     *
     */
    protected Long ApplicationID = new Long(1);


    /**
     * Constructor for the "ResultCodeStatisticsEntry" group.
     */
    public ResultCodeStatisticsEntry(SnmpMib myMib) {
    }

    /**
     * Getter for the "RcwCompstIndexValue" variable.
     */
    public String getRcwCompstIndexValue() throws SnmpStatusException {
        return RcwCompstIndexValue;
    }

    /**
     * Getter for the "RcwApplicationName" variable.
     */
    public String getRcwApplicationName() throws SnmpStatusException {
        return RcwApplicationName;
    }

    /**
     * Getter for the "RcwPeerIdentity" variable.
     */
    public String getRcwPeerIdentity() throws SnmpStatusException {
        return RcwPeerIdentity;
    }

    /**
     * Getter for the "RcwTx" variable.
     */
    public Long getRcwTx() throws SnmpStatusException {
        return RcwTx;
    }

    /**
     * Getter for the "RcwRx" variable.
     */
    public Long getRcwRx() throws SnmpStatusException {
        return RcwRx;
    }

    /**
     * Getter for the "ResultCode" variable.
     */
    public Long getResultCode() throws SnmpStatusException {
        return ResultCode;
    }

    /**
     * Getter for the "PeerIndex" variable.
     */
    public Long getPeerIndex() throws SnmpStatusException {
        return PeerIndex;
    }

    /**
     * Getter for the "ApplicationID" variable.
     */
    public Long getApplicationID() throws SnmpStatusException {
        return ApplicationID;
    }

}
