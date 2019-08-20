package com.elitecore.diameterapi.core.common.transport.tcp;

import javax.net.ssl.SSLHandshakeException;

public enum ConnectionEvents {
    CONNECTION_ESTABLISHED,
    /**
     * Connection Close simply. This will help to bring state machines to close state.
     */
    CONNECTION_BREAK,
    /**
     * Socket Stream Closed. This is specifically for Network Failures.
     */
    DISCONNECTED,
    /**
     * Close Connection by Sending DPR
     */
    CONNECTION_DPR,
    SHUTDOWN,
    /**
     * Kill Connection
     */
    FORCE_CLOSE,			//Called only for Force Close.
    /**
     * Reject Connection due to some invalid reason
     */
    REJECT_CONNECTION,
    /**
     * When PCB State Machine detects Dead Connection
     * via DWRs, then this event is called.
     */
    TIMER_EXPIRED,
    /**
     * TLS Handshake failed. This is generated on SSL Exception
     * @see SSLHandshakeException
     */
    HANDSHAKE_FAIL,
    /**
     * Called for Event when Connection is initiated
     * by diameter api and connection is not established.
     */
    CONNECTION_FAILURE, 
    /**
     * Called for Event when Connection is initiated
     * by diameter api and connection is established.
     */
    CONNECTION_CREATED,
    ;
}
