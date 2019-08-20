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
 * Created by kirpalsinh on 7/7/17.
 */
@RunWith(HierarchicalContextRunner.class)
public class NASPortIdAuthenticatorTest {

    private NASPortIdAuthenticator nasPortIdAuthenticator = new NASPortIdAuthenticator();
    private PCRFRequest pcrfRequest;
    private SPRInfoImpl sprInfoImpl;
    public static final String NAS_PORT_ID = "npid";

    @Before
    public void setUp() {
        pcrfRequest = new PCRFRequestImpl();
        sprInfoImpl = new SPRInfoImpl();
        sprInfoImpl.setSubscriberIdentity("raj");
        pcrfRequest.setSPRInfo(sprInfoImpl);
    }

    public class WhenSubscriberProfileHas {

        @Test
        public void Null_NASPortID() throws AuthenticationFailedException {
            sprInfoImpl.setNasPortId(null);
            pcrfRequest.setAttribute(PCRFKeyConstants.CS_NAS_PORT_ID.val, NAS_PORT_ID);
            nasPortIdAuthenticator.authenticate(pcrfRequest);
        }

        @Test
        public void Empty_NASPortID() throws AuthenticationFailedException {
            sprInfoImpl.setNasPortId("");
            pcrfRequest.setAttribute(PCRFKeyConstants.CS_NAS_PORT_ID.val, NAS_PORT_ID);
            nasPortIdAuthenticator.authenticate(pcrfRequest);
        }

        @Test
        public void Blank_NASPortID() throws AuthenticationFailedException {
            sprInfoImpl.setNasPortId("  ");
            pcrfRequest.setAttribute(PCRFKeyConstants.CS_NAS_PORT_ID.val, NAS_PORT_ID);
            nasPortIdAuthenticator.authenticate(pcrfRequest);
        }
    }

    public class WhenPCRFRequestHas{

        @Before
        public void setup(){
            sprInfoImpl.setNasPortId(NAS_PORT_ID);
        }

        @Test
        public void Null_NASPortID() throws AuthenticationFailedException {
            pcrfRequest.setAttribute(PCRFKeyConstants.CS_NAS_PORT_ID.val,null);
            nasPortIdAuthenticator.authenticate(pcrfRequest);
        }

        @Test
        public void Empty_NASPortID() throws AuthenticationFailedException {
            pcrfRequest.setAttribute(PCRFKeyConstants.CS_NAS_PORT_ID.val,"");
            nasPortIdAuthenticator.authenticate(pcrfRequest);
        }

        @Test( expected = AuthenticationFailedException.class)
        public void Blank_NASPortID() throws AuthenticationFailedException {
            pcrfRequest.setAttribute(PCRFKeyConstants.CS_NAS_PORT_ID.val,"  ");
            nasPortIdAuthenticator.authenticate(pcrfRequest);
        }

    }

    @Test( expected = AuthenticationFailedException.class)
    public void test_throws_AuthenticationFailedException_When_PCRFRequest_And_SubscriberProfile_Has_Different_NASPortID() throws AuthenticationFailedException {
        sprInfoImpl.setNasPortId(NAS_PORT_ID);
        pcrfRequest.setAttribute(PCRFKeyConstants.CS_NAS_PORT_ID.val,"1234");
        nasPortIdAuthenticator.authenticate(pcrfRequest);
    }

    @Test
    public void test_Authentication_Successful_When_PCRFRequest_And_SubscriberProfile_Has_Similar_NASPortID() throws AuthenticationFailedException {
        sprInfoImpl.setNasPortId(NAS_PORT_ID);
        pcrfRequest.setAttribute(PCRFKeyConstants.CS_NAS_PORT_ID.val, NAS_PORT_ID);
        nasPortIdAuthenticator.authenticate(pcrfRequest);
    }

}
