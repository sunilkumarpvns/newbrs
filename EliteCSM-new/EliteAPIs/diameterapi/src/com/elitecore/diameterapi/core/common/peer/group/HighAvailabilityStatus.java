package com.elitecore.diameterapi.core.common.peer.group;

import java.io.Serializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Represents the high availability information that is used in case of stateful load balancing
 * to store high availability pair that was chosen and the active peer from those peers for the
 * particular session.
 *  
 * @author narendra.pathai
 *
 */
@SuppressWarnings("serial")
public class HighAvailabilityStatus implements Serializable {

	private String primaryPeer;
	private String secondaryPeer;
	private String activePeer;
	
	/**
	 * Creates a new instance of high availability status.
	 * 
	 * @param primaryPeer primary peer in the high availability group.
	 * @param secondaryPeer optional secondary peer.
	 * @param activePeer a non-null active peer from the high availability group. The value must
	 * be any of <code>primaryPeer</code> or <code>secondaryPeer</code>.
	 */
	public HighAvailabilityStatus(@Nonnull String primaryPeer,
			@Nullable String secondaryPeer, @Nonnull String activePeer) {
		this.primaryPeer = primaryPeer;
		this.secondaryPeer = secondaryPeer;
		this.activePeer = activePeer;
	}
	
	@Nonnull public String getPrimaryPeer() {
		return primaryPeer;
	}
	
	@Nullable public String getSecondaryPeer() {
		return secondaryPeer;
	}
	
	@Nonnull public String getActivePeer() {
		return activePeer;
	}
	
	public void setActivePeer(@Nonnull String activePeer) {
		this.activePeer = activePeer;
	}
	
	@Override
	public String toString() {
		return String.format("P - %s, S - %s, A - %s", getPrimaryPeer(), getSecondaryPeer(), getActivePeer());
	}
}
