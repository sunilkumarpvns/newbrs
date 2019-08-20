package com.elitecore.diameterapi.diameter.common.session;

import javax.annotation.Nonnull;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import com.elitecore.diameterapi.DummyDiameterDictionary;
import com.elitecore.diameterapi.core.common.peer.ResponseListener;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;
import com.elitecore.diameterapi.diameter.stack.application.sessionrelease.AppDefaultSessionReleaseIndicator;

import de.bechte.junit.runners.context.HierarchicalContextRunner;

/**
 * Created by harsh on 3/20/17.
 */
@RunWith(HierarchicalContextRunner.class)
public class ClientApplicationRequestResponseListenerTest {


    private String successHost= "timeouthost.elitecore.com";


    private ClientApplicationRequestResponseListener requestResponseListener;
    private UserResponseListener userResponseListener;
    @Mock private DiameterSession session;
    @Spy private AppDefaultSessionReleaseIndicator appDefaultSessionReleaseIndicator;

    @BeforeClass
    public static void readDictionary() {
        DummyDiameterDictionary.getInstance();
    }

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        appDefaultSessionReleaseIndicator = new AppDefaultSessionReleaseIndicator();
        userResponseListener = new UserResponseListener();
        requestResponseListener = new ClientApplicationRequestResponseListener(appDefaultSessionReleaseIndicator, userResponseListener);
        DummyDiameterDictionary.getInstance();
    }


    @Test
    public void requestTimedoutAddTimeoutHostAsReleaseKeyAndDestinationHostInSession() {
        final String timeoutHost= "timeouthost.elitecore.com";
        requestResponseListener.requestTimedout(timeoutHost, session);
        Mockito.verify(session).setParameter(DiameterSession.SESSION_RELEASE_KEY, timeoutHost);
        Mockito.verify(session).setParameter(DiameterAVPConstants.DESTINATION_HOST, timeoutHost);
    }

    @Test
    public void requestTimedoutCallTimeoutRequestWithTimedoutHostOnUserSession() {
        final String timeoutHost= "timeouthost.elitecore.com";
        requestResponseListener.requestTimedout(timeoutHost, session);
        userResponseListener.checkRequestTimeoutOn(timeoutHost);
    }


    public class responseReceivedOnSuccessResponseReceived {

        private DiameterAnswer diameterAnswer;

        @Before
        public void setUp() {
            diameterAnswer = new DiameterAnswer();
            diameterAnswer.addAvp(DiameterAVPConstants.RESULT_CODE, ResultCode.DIAMETER_SUCCESS.code);
            diameterAnswer.addAvp(DiameterAVPConstants.SESSION_ID, "123");
        }

        @Test
        public void addTimeoutHostAsReleaseKeyAndDestinationHostInSession() {


            requestResponseListener.responseReceived(diameterAnswer, successHost, session);
            Mockito.verify(session).setParameter(DiameterSession.SESSION_RELEASE_KEY, successHost);
            Mockito.verify(session).setParameter(DiameterAVPConstants.DESTINATION_HOST, successHost);
        }

    }

    public class responseReceivedOnFailedResponseReceived {

        private DiameterAnswer diameterAnswer;

        @Before
        public void setUp() {
            diameterAnswer = new DiameterAnswer();
            diameterAnswer.addAvp(DiameterAVPConstants.RESULT_CODE, ResultCode.DIAMETER_USER_UNKNOWN.code);
            diameterAnswer.addAvp(DiameterAVPConstants.SESSION_ID, "123");
        }

        @Test
        public void releaseTheSession() {
            requestResponseListener.responseReceived(diameterAnswer, successHost, session);
            Mockito.verify(session).release();
        }

        @Test
        public void callResponseReceivedWithSameResponseAndHostIdentityOnUserSession() {
            requestResponseListener.responseReceived(diameterAnswer, successHost, session);
            userResponseListener.checkResponseReceived(diameterAnswer, successHost);
        }

    }


    /*@Test
    public void responseReceivedOnSuccessResponseAddTimeoutHostAsReleaseKeyAndDestinationHostInSession() {

        diameterAnswer.addAvp(DiameterAVPConstants.RESULT_CODE, ResultCode.DIAMETER_SUCCESS.code);
        diameterAnswer.addAvp(DiameterAVPConstants.SESSION_ID, "123");
        requestResponseListener.responseReceived(diameterAnswer, successHost);

        Mockito.verify(session).setParameter(DiameterSession.SESSION_RELEASE_KEY, successHost);
        Mockito.verify(session).setParameter(DiameterAVPConstants.DESTINATION_HOST, successHost);
    }

    @Test
    public void responseReceivedOnFailedResponseReleaseTheSession() {

        diameterAnswer.addAvp(DiameterAVPConstants.RESULT_CODE, ResultCode.DIAMETER_UNKNOWN_SESSION_ID.code);
        diameterAnswer.addAvp(DiameterAVPConstants.SESSION_ID, "123");
        requestResponseListener.responseReceived(diameterAnswer, successHost);

        Mockito.verify(session).release();
    }

    @Test
    public void responseReceivedCallResponseReceivedWithSameResponseAndHostIdentityOnUserSession() {
        requestResponseListener.responseReceived(diameterAnswer, successHost);
        userResponseListener.checkResponseReceived(diameterAnswer, successHost);
    }*/




    private class UserResponseListener implements ResponseListener {

        private DiameterAnswer diameterAnswer;
        private String hostIdentity;

        @Override
        public void requestTimedout(@Nonnull String hostIdentity, DiameterSession session) {

            this.hostIdentity = hostIdentity;
        }

        @Override
        public void responseReceived(@Nonnull DiameterAnswer diameterAnswer, @Nonnull String hostIdentity
        		, DiameterSession session) {

            this.diameterAnswer = diameterAnswer;
            this.hostIdentity = hostIdentity;
        }

        public void checkRequestTimeoutOn(String expectedTimeoutHost) {

            if(hostIdentity != null) {
                Assert.assertSame("Request timedout called but actual is different than expected", expectedTimeoutHost, this.hostIdentity);
            } else {
                Assert.fail("Request timedout not called");
            }

        }

        public void checkResponseReceived(DiameterAnswer diameterAnswer, String successHost) {
            if (this.diameterAnswer != null) {
                Assert.assertSame("responseReceived called but actual response is different than expected", diameterAnswer, this.diameterAnswer);
                Assert.assertSame("responseReceived called but actual host identity is different than expected", hostIdentity, this.hostIdentity);
            } else {
                Assert.fail("responseReceived not called");
            }

        }
    }


}
