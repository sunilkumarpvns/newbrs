package com.elitecore.diameterapi.diameter.common.session;

import com.elitecore.diameterapi.core.common.session.Session;
import com.elitecore.diameterapi.core.common.util.constant.ApplicationEnum;
import com.elitecore.diameterapi.core.stack.IStackContext;
import com.elitecore.diameterapi.diameter.common.data.impl.PeerDataImpl;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.peers.DummyPeerProvider;
import com.elitecore.diameterapi.diameter.stack.DummyStackContext;
import com.elitecore.diameterapi.diameter.stack.application.SessionReleaseIndiactor;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.rules.JUnitRule;
import org.mockito.internal.rules.JunitRuleImpl;
import org.mockito.junit.MockitoJUnitRule;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ApplicationListenerTest {

    @Mock Session session;

    @Test
    public void callPreProcessHookBeforeProcessingRequest() {
        MockitoAnnotations.initMocks(this);
        TestApplicationListener applicationListener = spy(new TestApplicationListener(new DummyStackContext(new DummyPeerProvider())));
        DiameterRequest diameterRequest = new DiameterRequest();
        PeerDataImpl peerData = new PeerDataImpl();
        peerData.setRealmName("sterlite.com");
        diameterRequest.setPeerData(peerData);
        applicationListener.handleApplicationRequest(session, diameterRequest);
        InOrder inOrder = inOrder(applicationListener);
        inOrder.verify(applicationListener, times(1)).preProcess(session, diameterRequest);
        inOrder.verify(applicationListener, times(1)).processApplicationRequest(session, diameterRequest);
    }

    private class TestApplicationListener extends ApplicationListener {

        public TestApplicationListener(IStackContext stackContext) {
            super(stackContext);
        }

        @Override
        public String getApplicationIdentifier() {
            return null;
        }

        @Override
        protected void processApplicationRequest(Session session, DiameterRequest diameterRequest) {

        }

        @Nullable
        @Override
        protected SessionReleaseIndiactor createSessionReleaseIndicator(@Nonnull ApplicationEnum applicationEnum) {
            return null;
        }
    }
}
