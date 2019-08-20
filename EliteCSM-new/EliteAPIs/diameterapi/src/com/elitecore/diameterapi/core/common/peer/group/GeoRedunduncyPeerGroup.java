package com.elitecore.diameterapi.core.common.peer.group;

import javax.annotation.Nonnull;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.systemx.esix.CommunicationException;
import com.elitecore.diameterapi.core.common.peer.ResponseListener;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.session.DiameterSession;

public class GeoRedunduncyPeerGroup implements DiameterPeerCommGroup {

	private static final String MODULE = "GEO-REDUNDUNCY-GRP";
	@Nonnull private DiameterPeerCommGroup primaryGroup;
	@Nonnull private DiameterPeerCommGroup secondaryGroup;

	public GeoRedunduncyPeerGroup(@Nonnull DiameterPeerCommGroup primaryGroup, 
			@Nonnull DiameterPeerCommGroup secondaryGroup) {
		this.primaryGroup = primaryGroup;
		this.secondaryGroup = secondaryGroup;
	}
	
	@Override
	public void sendClientInitiatedRequest(DiameterSession session, DiameterRequest diameterRequest,
			ResponseListener listener) throws CommunicationException {
		if (primaryGroup.isAlive()) {
			try {
				primaryGroup.sendClientInitiatedRequest(session, diameterRequest, listener);
			} catch (CommunicationException ex) {
				if (LogManager.getLogger().isInfoLogLevel()) {
					LogManager.getLogger().info(MODULE, "Communication with primary group failed. Reason: " + ex.getMessage()
					+ ". Sending traffic to secondary group");
				}
				trySendSecondary(session, diameterRequest, listener);
			}
		} else {
			LogManager.getLogger().debug(MODULE, "Primary group: " + primaryGroup.getName() + " is dead, trying secondary group: " + secondaryGroup.getName());
			trySendSecondary(session, diameterRequest, listener);
		}
	}

	private void trySendSecondary(DiameterSession session, DiameterRequest diameterRequest, ResponseListener listener) throws CommunicationException {
		try {
			secondaryGroup.sendClientInitiatedRequest(session, diameterRequest, listener);
		} catch (CommunicationException ex) {
			throw new CommunicationException(getName() + " is dead");
		}
	}

	@Override
	public String getName() {
		return String.format("GR-Group [P - %s, S - %s]", primaryGroup.getName(), secondaryGroup.getName());
	}

	@Override
	public boolean isAlive() {
		return primaryGroup.isAlive() || secondaryGroup.isAlive();
	}

}
