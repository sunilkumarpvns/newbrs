package com.elitecore.diameterapi.core.common.peer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.elitecore.core.systemx.esix.CommunicationException;
import com.elitecore.core.systemx.esix.ESCommunicator;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.session.DiameterSession;

/**
 * Created by harsh on 6/17/15.
 */
public interface DiameterPeerCommunicator extends ESCommunicator {
    void sendClientInitiatedRequest(@Nonnull DiameterSession session, @Nonnull DiameterRequest diameterRequest, @Nonnull ResponseListener listener) throws CommunicationException;
    void sendServerInitiatedRequest(@Nonnull DiameterSession session, @Nonnull DiameterRequest diameterRequest, @Nonnull ResponseListener listener) throws CommunicationException;
    void sendAnswer(DiameterRequest diameterRequest, DiameterAnswer diameterAnswer) throws CommunicationException;
    @Nullable String getHostIdentity();
}
