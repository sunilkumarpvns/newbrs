package com.elitecore.diameterapi.mibs.statistics;

public interface DiameterPeerStatsInfo {

	/**
	 * 
	 * @return Host Identity of Peer.
	 */
	public String getPeerIdentity();
	
	
	/**
	 * 
	 * @return Connection state in the Peer State Machine.
	 */
	public int getPeerState();
	
	/**
	 * 
	 * @return The elapsed time (in hundredths of a second)
              since the last state change.
	 */

	public long getPeerStateDuration();

	/**
	 * 
	 * @return Connection state in the Peer State Machine.
	 */
	public int getPeerStatsDWCurrentStatus();
	
	/**
	 * 
	 * @return The elapsed time since the last state change.
	 */
	public long getPeerInfoDWReqTimer();
	
	/**
	 * 
	 * @return If there is no transport connection with a peer,
              this is the number of times the local peer has attempted
              to connect to that peer.  This is reset on
              connection.
	 */
	public long getPeerStatsTimeoutConnAtmpts();

}
