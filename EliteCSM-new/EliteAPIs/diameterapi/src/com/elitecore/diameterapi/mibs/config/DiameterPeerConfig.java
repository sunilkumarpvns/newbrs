package com.elitecore.diameterapi.mibs.config;

import java.util.Set;

import com.elitecore.diameterapi.core.common.util.constant.ApplicationEnum;
import com.elitecore.diameterapi.mibs.constants.RowStatus;
import com.elitecore.diameterapi.mibs.constants.SecurityProtocol;
import com.elitecore.diameterapi.mibs.constants.StorageTypes;
import com.elitecore.diameterapi.mibs.constants.TransportProtocols;
import com.elitecore.diameterapi.mibs.data.DiameterBasePeerIpAddressTable;
import com.elitecore.diameterapi.mibs.data.DiameterBasePeerVendorTable;

public interface DiameterPeerConfig{

	/**
	 * 
	 * @return The local identifier for the Diameter peer.
	 *        It must be unique and non-empty.
	 */
	public String getPeerId();

	/**
	 * 
	 * @return Firmware revision of peer.
	 */
	public int getPeerFirmwareRevison();

	/**
	 * 
	 * @return The storage type for this conceptual row.
	 *      Only the dbpPeerPortListen object is writable when
	 *      the conceptual row is permanent.
	 */
	public StorageTypes getPeerStorageType();

	/**
	 * 
	 * @return Status of the peer entry: creating the entry
	 *         enables the peer, destroying the entry disables
	 *         the peer.
	 */
	public RowStatus getPeerRowStatus();

	/**
	 * 
	 * @return The connection port used
              to connect to the Diameter peer.
              If there is no active connection, this
              value will be zero(0).
	 */
	public int getDbpPeerPortListen();

	/**
	 * 
	 * @return The transport protocol (tcp/sctp) the
              Diameter peer is using.
	 */
	public TransportProtocols getDbpPeerTransportProtocol();

	/**
	 * 
	 * @return The security the Diameter peer is using.

              other(1) - Unknown Security Protocol (DEFAULT VALUE)
              tls(2)   - Transport Layer Security Protocol
              ipsec(3) - Internet Protocol Security
	 */
	public SecurityProtocol getDbpPeerSecurity();

	/**
	 * 
	 * @return The port the peer is listening on.
	 */
	public int getDbpPeerPortConnect();

	/**
	 * 
	 * @return Set of ApplicationEnum, uniquely identifying the applications
            advertised as supported from each Diameter peer.
	 */
	public Set<ApplicationEnum> getDbpAppAdvFromPeer();
	
	
	/**
	 * 
	 * @return Set of ApplicationEnum, uniquely identifying the applications
            advertised to Diameter peer.
	 */
	public Set<ApplicationEnum> getDbpAppAdvToPeer();

	/**
	 * 
	 * @return Array of DiameterBasePeerVendorTable, uniquely identifying the Vendor
            ID supported by the peer. Upon reload,
            dbpPeerVendorIndex values may be changed.
	 */
	public DiameterBasePeerVendorTable[] getDbpPeerVendorTable();

	/**
	 * 
	 * @return Array of DiameterBasePeerIpAddressTable uniquely identifying an IP Address
              supported by this Diameter peer.
	 */
	public DiameterBasePeerIpAddressTable[] getPeerIpAddressIndex();
	
	/**
	 * 
	 * @return Array of IpAddresses having an IP Address of Type IP:Port
              supported by this Diameter peer.
	 */
	public String getPeerIpAddresses();
	
	/**
	 * 
	 * @return Number of times the server attempted to connect to that peer.  This is reset on disconnection.
	 */
	public long getDbpPerPeerStatsTimeoutConnAtmpts();
	
	/**
	 *	@return	okay   -Indicates the connection is presumed working.<br>
     *       	suspect-Indicates the connection is possibly
     *					congested or down.<br>
     *       	down  - The peer is no longer reachable, causing
     *					the transport connection to be shutdown.<br>
     *       	reopen- Three watchdog messages are exchanged with
     *					accepted round trip times, and the connection
     *					to the peer is considered stabilized."
	 */
	public int getPCBState();
	
	/**
	 * 
	 * @return The elapsed time (in hundredths of a second)
	 *			since the last state change
	 */
	public long getDbpPerPeerInfoStateDuration();
	
	/**
	 * 
	 * @return Connection state in the Peer State Machine of
              the peer with which this Diameter peer is
              communicating.<br>
              closed (1)     - Connection closed with this peer.<br>
              waitConnAck(2) - Waiting for an acknowledgment
                            	from this peer.<br>
              waitICea(3)    - Waiting for a Capabilities-Exchange-Answer
                            	from this peer.<br>
              elect (4)      - When the remote and local peers are both
                            	trying to bring up a connection with
                            	each other at the same time.  An
                            	election process begins which
                            	determines which socket remains open.<br>
              waitReturns(5) - Waiting for election returns.<br>
              r-open(6)    	 - Responder transport connection is
                            	used for communication.<br>
              i-open(7)   	 - Initiator transport connection is
                            	used for communication.<br>
              closing(8)   	 - Actively closing and doing cleanup."<br>
	 */
	public int getPeerState();
	
	/**
	 * 
	 * @return Device-Watchdog Request Timer, which
              is the interval between DWR messages sent to
              peers in case of idle connection
	 */
	public long getPeerWatchDogInterval();
	
	public long getDbpPeerIndex();
	
	public boolean isConnectionInitiationEnabled();
	
	public String getPeerLocalIpAddresses();
}
