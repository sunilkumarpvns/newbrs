package com.elitecore.netvertex.gateway.diameter.application;

import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.diameterapi.core.common.util.constant.ApplicationEnum;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.grouped.AvpGrouped;
import com.elitecore.diameterapi.diameter.common.util.constant.ApplicationIdentifier;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAttributeValueConstants;
import com.elitecore.diameterapi.diameter.stack.application.SessionReleaseIndiactor;
import com.elitecore.diameterapi.diameter.stack.application.sessionrelease.AppDefaultSessionReleaseIndicator;
import com.elitecore.netvertex.gateway.diameter.DiameterGatewayControllerContext;
import com.elitecore.netvertex.gateway.diameter.DummyDiameterDictionary;
import com.elitecore.netvertex.gateway.diameter.DummyDiameterGatewayControllerContext;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.apache.logging.log4j.ThreadContext;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(HierarchicalContextRunner.class)
public class NetvertexBaseServerApplicationTest {

    private NetvertexBaseServerApplication netvertexBaseServerApplication = new DummyNetVertexBaseServerApplication(new DummyDiameterGatewayControllerContext());

    @BeforeClass
    public static void initializeDictionary() {
        DummyDiameterDictionary.getInstance();
    }

    public class AddContextContextInformationAbout {

        private String framedIpAddress = "10.101.1.20";

        private String framedIpv6Address = "0x323030313a306462383a383561333a303030303a303030303a386132653a303337303a37333334";
        private String e164 = "E164";
        private String imsi = "IMSI";
        private String sip = "SIP";
        private String nai = "NAI";
        private String aPrivate = "PRIVATE";
        private String random = "RANDOM";
        private String requestType = "1";
        private int newType = 100;
        private DiameterRequest diameterRequest = new DiameterRequest();
        private String privateTypeLogValue;

        @Before
        public void addAttribute() {

            diameterRequest.addAvp(DiameterAVPConstants.FRAMED_IP_ADDRESS, framedIpAddress);

            diameterRequest.addAvp(DiameterAVPConstants.FRAMED_IPV6_PREFIX, framedIpv6Address);

            diameterRequest.addAvp(DiameterAVPConstants.CC_REQUEST_TYPE, "1");

            diameterRequest.addAvp(createSubscriptionIdAvp(DiameterAttributeValueConstants.DIAMETER_END_USER_E164, e164));

            diameterRequest.addAvp(createSubscriptionIdAvp(DiameterAttributeValueConstants.DIAMETER_END_USER_IMSI, imsi));


            diameterRequest.addAvp(createSubscriptionIdAvp(DiameterAttributeValueConstants.DIAMETER_END_USER_SIP_URI, sip));


            diameterRequest.addAvp(createSubscriptionIdAvp(DiameterAttributeValueConstants.DIAMETER_END_USER_NAI, nai));

            AvpGrouped privateAvp = createSubscriptionIdAvp(DiameterAttributeValueConstants.DIAMETER_END_USER_PRIVATE, aPrivate);
            privateTypeLogValue = privateAvp.getSubAttribute(DiameterAVPConstants.SUBSCRIPTION_ID_TYPE).getLogValue();
            diameterRequest.addAvp(privateAvp);


            diameterRequest.addAvp(createSubscriptionIdAvp(newType, random));
            netvertexBaseServerApplication.preProcess(null, diameterRequest);

        }

        private AvpGrouped createSubscriptionIdAvp(int type, String value) {
            AvpGrouped subscriptionIdAvp = (AvpGrouped) DummyDiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.SUBSCRIPTION_ID);
            subscriptionIdAvp.addSubAvp(DiameterAVPConstants.SUBSCRIPTION_ID_TYPE, type);
            subscriptionIdAvp.addSubAvp(DiameterAVPConstants.SUBSCRIPTION_ID_DATA, value);

            return subscriptionIdAvp;
        }



        @Test
        public void ipv4address() {
            assertThat(ThreadContext.get(PCRFKeyConstants.CS_SESSION_IPV4.val), is(equalTo(framedIpAddress)));
        }

        @Test
        public void ipv6address() {
            assertThat(ThreadContext.get(PCRFKeyConstants.CS_SESSION_IPV6.val), is(equalTo(framedIpv6Address)));
        }

        @Test
        public void requestType() {
            assertThat(ThreadContext.get(PCRFKeyConstants.REQUEST_TYPE.val), is(equalTo(requestType)));
        }

        @Test
        public void imsi() {
            assertThat(ThreadContext.get(PCRFKeyConstants.SUB_IMSI.val), is(equalTo(imsi)));
        }

        @Test
        public void sipUri() {
            assertThat(ThreadContext.get(PCRFKeyConstants.SUB_SIP_URL.val), is(equalTo(sip)));
        }

        @Test
        public void nai() {
            assertThat(ThreadContext.get(PCRFKeyConstants.CS_SUBSCRIPTION_ID_TYPE_NAI.val), is(equalTo(nai)));
        }

        @Test
        public void privateVal() {
            assertThat(ThreadContext.get(privateTypeLogValue), is(equalTo(aPrivate)));
        }

        @Test
        public void newValueOfSubscriptionType() {
            assertThat(ThreadContext.get(String.valueOf(newType)), is(equalTo(random)));
        }
    }

    public class NotAddContextContextInformationWhenNotreceived {

        private String framedIpAddress = "10.101.1.20";

        private String framedIpv6Address = "0x323030313a306462383a383561333a303030303a303030303a386132653a303337303a37333334";
        private String e164 = "E164";
        private String imsi = "IMSI";
        private String sip = "SIP";
        private String nai = "NAI";
        private String aPrivate = "PRIVATE";
        private String random = "RANDOM";
        private int newType = 100;
        private DiameterRequest diameterRequest = spy(new DiameterRequest());
        private String privateTypeLogValue;

        public void call() {
            netvertexBaseServerApplication.preProcess(null, diameterRequest);
        }


        public void checkAttributeCalled(String attributeId) {
            verify(diameterRequest).getAVPValue(attributeId);
        }

        @Test
        public void requestType() {
            call();
            checkAttributeCalled(DiameterAVPConstants.CC_REQUEST_TYPE);
            assertThat(ThreadContext.get(PCRFKeyConstants.REQUEST_TYPE.val), is(nullValue()));
        }

        @Test
        public void ipv4address() {
            call();
            checkAttributeCalled(DiameterAVPConstants.FRAMED_IP_ADDRESS);
            assertThat(ThreadContext.get(PCRFKeyConstants.CS_SESSION_IPV4.val), is(nullValue()));
        }

        @Test
        public void ipv6address() {
            call();
            checkAttributeCalled(DiameterAVPConstants.FRAMED_IPV6_PREFIX);
            assertThat(ThreadContext.get(PCRFKeyConstants.CS_SESSION_IPV6.val), is(nullValue()));
        }

        @Test
        public void imsi() {
            call();
            verify(diameterRequest).getAVPList(DiameterAVPConstants.SUBSCRIPTION_ID);
            assertThat(ThreadContext.get(PCRFKeyConstants.SUB_IMSI.val), is(nullValue()));
        }

        @Test
        public void sipUri() {
            call();
            verify(diameterRequest).getAVPList(DiameterAVPConstants.SUBSCRIPTION_ID);
            assertThat(ThreadContext.get(PCRFKeyConstants.SUB_SIP_URL.val), is(nullValue()));
        }

        @Test
        public void nai() {
            call();
            verify(diameterRequest).getAVPList(DiameterAVPConstants.SUBSCRIPTION_ID);
            assertThat(ThreadContext.get(PCRFKeyConstants.CS_SUBSCRIPTION_ID_TYPE_NAI.val), is(nullValue()));
        }

        @Test
        public void privateVal() {
            call();
            verify(diameterRequest).getAVPList(DiameterAVPConstants.SUBSCRIPTION_ID);
            assertThat(ThreadContext.get(privateTypeLogValue), is(nullValue()));
        }

        @Test
        public void newValue() {
            call();
            verify(diameterRequest).getAVPList(DiameterAVPConstants.SUBSCRIPTION_ID);
            assertThat(ThreadContext.get(String.valueOf(newType)), is(nullValue()));
        }

        @Test
        public void subscriptionTypeIsNotReceived() {
            AvpGrouped subscriptionIdAvp = (AvpGrouped) DummyDiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.SUBSCRIPTION_ID);
            subscriptionIdAvp = spy(subscriptionIdAvp);
            subscriptionIdAvp.addSubAvp(DiameterAVPConstants.SUBSCRIPTION_ID_DATA, "123");
            diameterRequest.addAvp(subscriptionIdAvp);
            call();
            verify(subscriptionIdAvp).getSubAttribute(DiameterAVPConstants.SUBSCRIPTION_ID_TYPE);
            assertThat(ThreadContext.get(String.valueOf(newType)), is(nullValue()));
        }

        @Test
        public void subscriptionDataIsNotReceived() {
            AvpGrouped subscriptionIdAvp = (AvpGrouped) DummyDiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.SUBSCRIPTION_ID);
            subscriptionIdAvp = spy(subscriptionIdAvp);
            subscriptionIdAvp.addSubAvp(DiameterAVPConstants.SUBSCRIPTION_ID_TYPE, 100);
            diameterRequest.addAvp(subscriptionIdAvp);
            call();
            verify(subscriptionIdAvp).getSubAttribute(DiameterAVPConstants.SUBSCRIPTION_ID_DATA);
            assertThat(ThreadContext.get(String.valueOf(newType)), is(nullValue()));
        }
    }

    private static class DummyNetVertexBaseServerApplication extends NetvertexBaseServerApplication {

        public DummyNetVertexBaseServerApplication(DiameterGatewayControllerContext context) {
            super(context, 0, ApplicationIdentifier.TGPP_GXX.applicationId, ApplicationIdentifier.TGPP_GXX.application);
        }

        @Override
        public String getApplicationIdentifier() {
            return ApplicationIdentifier.TGPP_GXX.application.getDisplayName();
        }

        @Nullable
        @Override
        protected SessionReleaseIndiactor createSessionReleaseIndicator(@Nonnull ApplicationEnum applicationEnum) {
            return new AppDefaultSessionReleaseIndicator();
        }
    }

    @After
    public void tearDown() {
        ThreadContext.clearAll();
    }
}
