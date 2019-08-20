package com.elitecore.diameterapi.core.common.peer.group;

import javax.annotation.Nonnull;

import com.elitecore.core.systemx.esix.CommunicationException;
import com.elitecore.diameterapi.core.common.peer.ResponseListener;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.session.DiameterSession;

/**
 * Created by harsh on 6/18/15.
 */
public interface DiameterPeerCommGroup {
    void sendClientInitiatedRequest(@Nonnull DiameterSession session, @Nonnull DiameterRequest diameterRequest, @Nonnull ResponseListener listener) throws CommunicationException;
    String getName();
    boolean isAlive();
}
