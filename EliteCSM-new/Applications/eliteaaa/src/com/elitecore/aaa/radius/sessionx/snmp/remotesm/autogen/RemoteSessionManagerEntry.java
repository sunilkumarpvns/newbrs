package com.elitecore.aaa.radius.sessionx.snmp.remotesm.autogen;

//
// Generated by mibgen version 5.1 (03/08/07) when compiling REMOTE-SESSION-MANAGER-MIB.
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
 * The class is used for implementing the "RemoteSessionManagerEntry" group.
 * The group is defined with the following oid: 1.3.6.1.4.1.21067.1.2.2.2.1.
 */
public class RemoteSessionManagerEntry implements RemoteSessionManagerEntryMBean, Serializable {

	public RemoteSessionManagerEntry() {
	}
	
    /**
     * Variable for storing the value of "RemoteSMAccessRequestRx".
     * The variable is identified by: "1.3.6.1.4.1.21067.1.2.2.2.1.9".
     *
     * "Total No. of access request received by Remote Session Manager"
     *
     */
    protected Long RemoteSMAccessRequestRx = new Long(1);

    /**
     * Variable for storing the value of "RemoteSMTimeoutRequest".
     * The variable is identified by: "1.3.6.1.4.1.21067.1.2.2.2.1.8".
     *
     * "Total No. of unknown request received by Remote Session Manager"
     *
     */
    protected Long RemoteSMTimeoutRequest = new Long(1);

    /**
     * Variable for storing the value of "RemoteSMUnknownRequestType".
     * The variable is identified by: "1.3.6.1.4.1.21067.1.2.2.2.1.7".
     *
     * "Total No. of unknown request received by Remote Session Manager"
     *
     */
    protected Long RemoteSMUnknownRequestType = new Long(1);

    /**
     * Variable for storing the value of "RemoteSMAcctStopRequestRx".
     * The variable is identified by: "1.3.6.1.4.1.21067.1.2.2.2.1.16".
     *
     * "Total No. of accounting stop received by Remote Session Manager"
     *
     */
    protected Long RemoteSMAcctStopRequestRx = new Long(1);

    /**
     * Variable for storing the value of "RemoteSMAcctUpdateRequestRx".
     * The variable is identified by: "1.3.6.1.4.1.21067.1.2.2.2.1.15".
     *
     * "Total No. of accounting update received by Remote Session Manager"
     *
     */
    protected Long RemoteSMAcctUpdateRequestRx = new Long(1);

    /**
     * Variable for storing the value of "RemoteSMRequestDropped".
     * The variable is identified by: "1.3.6.1.4.1.21067.1.2.2.2.1.6".
     *
     * "Total No. of request dropped by Remote Session Manager"
     *
     */
    protected Long RemoteSMRequestDropped = new Long(1);

    /**
     * Variable for storing the value of "RemoteSMAcctStartRequestRx".
     * The variable is identified by: "1.3.6.1.4.1.21067.1.2.2.2.1.14".
     *
     * "Total No. of accounting start received by Remote Session Manager"
     *
     */
    protected Long RemoteSMAcctStartRequestRx = new Long(1);

    /**
     * Variable for storing the value of "RemoteSMResponsesTx".
     * The variable is identified by: "1.3.6.1.4.1.21067.1.2.2.2.1.5".
     *
     * "Total No. of response given by Remote Session Manager"
     *
     */
    protected Long RemoteSMResponsesTx = new Long(1);

    /**
     * Variable for storing the value of "RemoteSMAcctResponseTx".
     * The variable is identified by: "1.3.6.1.4.1.21067.1.2.2.2.1.13".
     *
     * "Total No. of accounting response gived by Remote Session Manager"
     *
     */
    protected Long RemoteSMAcctResponseTx = new Long(1);

    /**
     * Variable for storing the value of "RemoteSMRequestRx".
     * The variable is identified by: "1.3.6.1.4.1.21067.1.2.2.2.1.4".
     *
     * "Total No. of request received by Remote Session Manager"
     *
     */
    protected Long RemoteSMRequestRx = new Long(1);

    /**
     * Variable for storing the value of "RemoteSMIPAddress".
     * The variable is identified by: "1.3.6.1.4.1.21067.1.2.2.2.1.3".
     *
     * "IP Address of configured Radius clients"
     *
     */
    protected String RemoteSMIPAddress = new String("192.9.9.100");

    /**
     * Variable for storing the value of "RemoteSMAcctRequestRx".
     * The variable is identified by: "1.3.6.1.4.1.21067.1.2.2.2.1.12".
     *
     * "Total No. of accounting request received by Remote Session Manager"
     *
     */
    protected Long RemoteSMAcctRequestRx = new Long(1);

    /**
     * Variable for storing the value of "RemoteSMName".
     * The variable is identified by: "1.3.6.1.4.1.21067.1.2.2.2.1.2".
     *
     * "indicate the name of Remote Session Manager"
     *
     */
    protected String RemoteSMName = new String("JDMK 5.1");

    /**
     * Variable for storing the value of "RemoteSMAccessRejectTx".
     * The variable is identified by: "1.3.6.1.4.1.21067.1.2.2.2.1.11".
     *
     * "Total No. of access reject given by Remote Session Manager"
     *
     */
    protected Long RemoteSMAccessRejectTx = new Long(1);

    /**
     * Variable for storing the value of "RemoteSMAccessAcceptTx".
     * The variable is identified by: "1.3.6.1.4.1.21067.1.2.2.2.1.10".
     *
     * "Total No. of access accept given by Remote Session Manager"
     *
     */
    protected Long RemoteSMAccessAcceptTx = new Long(1);

    /**
     * Variable for storing the value of "ClientIndex".
     * The variable is identified by: "1.3.6.1.4.1.21067.1.2.2.2.1.1".
     *
     * ""
     *
     */
    protected Integer ClientIndex = new Integer(1);


    /**
     * Constructor for the "RemoteSessionManagerEntry" group.
     */
    public RemoteSessionManagerEntry(SnmpMib myMib) {
    }

    /**
     * Getter for the "RemoteSMAccessRequestRx" variable.
     */
    public Long getRemoteSMAccessRequestRx() throws SnmpStatusException {
        return RemoteSMAccessRequestRx;
    }

    /**
     * Getter for the "RemoteSMTimeoutRequest" variable.
     */
    public Long getRemoteSMTimeoutRequest() throws SnmpStatusException {
        return RemoteSMTimeoutRequest;
    }

    /**
     * Getter for the "RemoteSMUnknownRequestType" variable.
     */
    public Long getRemoteSMUnknownRequestType() throws SnmpStatusException {
        return RemoteSMUnknownRequestType;
    }

    /**
     * Getter for the "RemoteSMAcctStopRequestRx" variable.
     */
    public Long getRemoteSMAcctStopRequestRx() throws SnmpStatusException {
        return RemoteSMAcctStopRequestRx;
    }

    /**
     * Getter for the "RemoteSMAcctUpdateRequestRx" variable.
     */
    public Long getRemoteSMAcctUpdateRequestRx() throws SnmpStatusException {
        return RemoteSMAcctUpdateRequestRx;
    }

    /**
     * Getter for the "RemoteSMRequestDropped" variable.
     */
    public Long getRemoteSMRequestDropped() throws SnmpStatusException {
        return RemoteSMRequestDropped;
    }

    /**
     * Getter for the "RemoteSMAcctStartRequestRx" variable.
     */
    public Long getRemoteSMAcctStartRequestRx() throws SnmpStatusException {
        return RemoteSMAcctStartRequestRx;
    }

    /**
     * Getter for the "RemoteSMResponsesTx" variable.
     */
    public Long getRemoteSMResponsesTx() throws SnmpStatusException {
        return RemoteSMResponsesTx;
    }

    /**
     * Getter for the "RemoteSMAcctResponseTx" variable.
     */
    public Long getRemoteSMAcctResponseTx() throws SnmpStatusException {
        return RemoteSMAcctResponseTx;
    }

    /**
     * Getter for the "RemoteSMRequestRx" variable.
     */
    public Long getRemoteSMRequestRx() throws SnmpStatusException {
        return RemoteSMRequestRx;
    }

    /**
     * Getter for the "RemoteSMIPAddress" variable.
     */
    public String getRemoteSMIPAddress() throws SnmpStatusException {
        return RemoteSMIPAddress;
    }

    /**
     * Getter for the "RemoteSMAcctRequestRx" variable.
     */
    public Long getRemoteSMAcctRequestRx() throws SnmpStatusException {
        return RemoteSMAcctRequestRx;
    }

    /**
     * Getter for the "RemoteSMName" variable.
     */
    public String getRemoteSMName() throws SnmpStatusException {
        return RemoteSMName;
    }

    /**
     * Getter for the "RemoteSMAccessRejectTx" variable.
     */
    public Long getRemoteSMAccessRejectTx() throws SnmpStatusException {
        return RemoteSMAccessRejectTx;
    }

    /**
     * Getter for the "RemoteSMAccessAcceptTx" variable.
     */
    public Long getRemoteSMAccessAcceptTx() throws SnmpStatusException {
        return RemoteSMAccessAcceptTx;
    }

    /**
     * Getter for the "ClientIndex" variable.
     */
    public Integer getClientIndex() throws SnmpStatusException {
        return ClientIndex;
    }

}
