package com.elitecore.netvertex.service.pcrf.servicepolicy.handler;


import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.spr.data.SPRInfoImpl;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.impl.PCRFRequestImpl;
import com.elitecore.netvertex.service.pcrf.servicepolicy.authprotocol.exception.AuthenticationFailedException;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by kirpalsinh on 6/7/17.
 */

@RunWith(HierarchicalContextRunner.class)
public class CallingStationIdAuthenticatorTest {

    public static final String CSID_1 = "CSID1";
    private CallingStationIdAuthenticator callingStationIdAuthenticator = new CallingStationIdAuthenticator();
    private PCRFRequest pcrfRequest;
    private SPRInfoImpl sprInfoImpl;

    @Before
    public void setUp() {
        pcrfRequest = new PCRFRequestImpl();
        sprInfoImpl = new SPRInfoImpl();
        sprInfoImpl.setSubscriberIdentity("raj");
        pcrfRequest.setSPRInfo(sprInfoImpl);
    }

    public class SkipAuthentication {

        public class WhenSubscriberProfileHas {

            private SPRInfoImpl sprInfo;
            @Before
            public void setUp() {
                sprInfo = (SPRInfoImpl) pcrfRequest.getSPRInfo();
                pcrfRequest.setAttribute(PCRFKeyConstants.CS_CALLING_STATION_ID.val, "10.105.10.1");
            }

            @Test
            public void Null_CallingStationID() throws AuthenticationFailedException {
                sprInfo.setCallingStationId(null);
                callingStationIdAuthenticator.authenticate(pcrfRequest);
            }

            @Test
            public void Empty_CallingStationID() throws AuthenticationFailedException {
                sprInfo.setCallingStationId("");
                callingStationIdAuthenticator.authenticate(pcrfRequest);
            }

            @Test
            public void Blank_CallingStationID() throws AuthenticationFailedException {
                sprInfo.setCallingStationId("  ");
                callingStationIdAuthenticator.authenticate(pcrfRequest);
            }
        }


        public class WhenPCRFRequestHas {

            @Before
            public void setUp() {
                ((SPRInfoImpl)pcrfRequest.getSPRInfo()).setCallingStationId("10.105.10.1");
            }

            @Test
            public void CallingStationID_NotFound() throws AuthenticationFailedException {
                pcrfRequest.removeAttribute(PCRFKeyConstants.CS_CALLING_STATION_ID.val);
                callingStationIdAuthenticator.authenticate(pcrfRequest);
            }

            @Test
            public void Empty_CallingStationID() throws AuthenticationFailedException {
                pcrfRequest.setAttribute(PCRFKeyConstants.CS_CALLING_STATION_ID.val, "");
                callingStationIdAuthenticator.authenticate(pcrfRequest);
            }

            @Test(expected = AuthenticationFailedException.class)
            public void Blank_CallingStationID() throws AuthenticationFailedException {
                pcrfRequest.setAttribute(PCRFKeyConstants.CS_CALLING_STATION_ID.val, "  ");
                callingStationIdAuthenticator.authenticate(pcrfRequest);
            }
        }
    }

    @Test(expected = AuthenticationFailedException.class)
    public void test_throws_AuthenticationFailedException_When_PCRFRequest_And_SubscriberProfile_Has_Different_CallingStationID() throws AuthenticationFailedException {
        pcrfRequest.setAttribute(PCRFKeyConstants.CS_CALLING_STATION_ID.val, CSID_1);
        sprInfoImpl.setCallingStationId("CSID2");
        callingStationIdAuthenticator.authenticate(pcrfRequest);
    }

    public class AuthenticationSuccess {
        @Test
        public void When_PCRFRequest_And_SubscriberProfile_Has_Similar_CallingStationID() throws AuthenticationFailedException {
            pcrfRequest.setAttribute(PCRFKeyConstants.CS_CALLING_STATION_ID.val, "CSID1");
            sprInfoImpl.setCallingStationId("CSID1");
            callingStationIdAuthenticator.authenticate(pcrfRequest);
        }

        @Test
        public void When_Request_CallingStationID_is_available_In_SPRInfo_CommaSeparated_CallingStationIDs() throws AuthenticationFailedException {
            pcrfRequest.setAttribute(PCRFKeyConstants.CS_CALLING_STATION_ID.val, "MAC4");
            sprInfoImpl.setCallingStationId("MAC1,MAC2,MAC3,MAC4");
            callingStationIdAuthenticator.authenticate(pcrfRequest);
        }

        @Test
        public void when_Request_CallingStationID_is_available_In__SPRInfo_SemicolonSeparated_CallingStationIDs() throws AuthenticationFailedException {
            pcrfRequest.setAttribute(PCRFKeyConstants.CS_CALLING_STATION_ID.val, "aa-bb-cc-dd");
            sprInfoImpl.setCallingStationId("MAC1;aa-bb-cc-dd;xx-yy-zz");
            callingStationIdAuthenticator.authenticate(pcrfRequest);
        }

        @Test
        public void When_Request_CallingStationID_is_available_In_SPRInfo_CommaAndSemicolonSeparated_CallingStationIDs() throws AuthenticationFailedException {

            pcrfRequest.setAttribute(PCRFKeyConstants.CS_CALLING_STATION_ID.val, "CSID9");
            sprInfoImpl.setCallingStationId("MAC1,AA-BB,MAC2;CSID9,MAC4;MAC90");
            callingStationIdAuthenticator.authenticate(pcrfRequest);

        }
    }
}
