package com.elitecore.netvertex.core.mapping.gx;

import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyValueConstants;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.netvertex.core.mapping.DiameterRequestBuilder;
import com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc.PCRFRequestMappingValueProvider;
import com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc.gx.RequestedQoSMapping;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.impl.PCRFRequestImpl;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
@RunWith(HierarchicalContextRunner.class)
public class RequestedQoSMappingTest {

    private static final String BITS_1000 = "1000";
    private static final String QCI = "9";
    private RequestedQoSMapping requestedQoSMapping = new RequestedQoSMapping();
    private DiameterRequest diameterRequest;
    private PCRFRequest pcrfRequest;
    private PCRFRequestMappingValueProvider valueProvider;
    
    @Before
    public void before() {
        
        diameterRequest = new DiameterRequestBuilder().
                addRequstedQoSMappings(QCI, BITS_1000, BITS_1000, BITS_1000, BITS_1000)
                .addDefaultEPSBearerAVPs(QCI).build();

        pcrfRequest = new PCRFRequestImpl();

        valueProvider = new PCRFRequestMappingValueProvider(diameterRequest, pcrfRequest, null);
    }

    public class RequestedQoS {
        @Test
        public void test_apply_stores_requestQoS_when_QoSInformationAVPfoundInDiameterRequest() {

            diameterRequest.addAvp(DiameterAVPConstants.TGPP_QOS_UPGRADE, "1");
            requestedQoSMapping.apply(valueProvider);

            assertEquals("1", pcrfRequest.getAttribute(PCRFKeyConstants.QOS_UPGRADE.val));
            assertEquals(QCI , pcrfRequest.getAttribute(PCRFKeyConstants.REQ_QCI.val));
            assertEquals(BITS_1000 , pcrfRequest.getAttribute(PCRFKeyConstants.REQ_MBRDL.val));
            assertEquals(BITS_1000 , pcrfRequest.getAttribute(PCRFKeyConstants.REQ_AAMBRDL.val));
            assertEquals(BITS_1000 , pcrfRequest.getAttribute(PCRFKeyConstants.REQ_AAMBRUL.val));
            assertEquals(BITS_1000 , pcrfRequest.getAttribute(PCRFKeyConstants.REQ_MBRUL.val));
            assertEquals("1" , pcrfRequest.getAttribute(PCRFKeyConstants.REQ_PRIORITY_LEVEL.val));
            assertEquals("1" , pcrfRequest.getAttribute(PCRFKeyConstants.REQ_PREEMPTION_CAPABILITY.val));
            assertEquals("1" , pcrfRequest.getAttribute(PCRFKeyConstants.REQ_PREEMPTION_VULNERABILITY.val));
        }
    }

    public class DefaultEPSQoS {

        @Test
        public void test_apply_stores_defaultEPSBearer_when_DefaultEPSBearerfoundInDiameterRequest() {

            requestedQoSMapping.apply(valueProvider);

            assertEquals(QCI , pcrfRequest.getAttribute(PCRFKeyConstants.REQ_QCI.val));
            assertEquals("1" , pcrfRequest.getAttribute(PCRFKeyConstants.REQ_PRIORITY_LEVEL.val));
            assertEquals("1" , pcrfRequest.getAttribute(PCRFKeyConstants.REQ_PREEMPTION_CAPABILITY.val));
            assertEquals("1" , pcrfRequest.getAttribute(PCRFKeyConstants.REQ_PREEMPTION_VULNERABILITY.val));
        }

    }

    public class QoSUpgrade {


        @Test
        public void test_apply_stores_QoSUpgradeToTrueWhenQoSUpgradeAVPFoundWithValueEnabled() {


            diameterRequest.addAvp(DiameterAVPConstants.TGPP_QOS_UPGRADE, PCRFKeyValueConstants.QOS_UPGRADE_SUPPORTED.val);
            requestedQoSMapping.apply(valueProvider);

            assertTrue(pcrfRequest.getRequestedQoS().isQosUpgrade());
        }

        @Test
        public void test_apply_stores_QoSUpgradeToFalseWhneQoSUpgradeAVPFoundWithValueDisabled() {


            diameterRequest.addAvp(DiameterAVPConstants.TGPP_QOS_UPGRADE, PCRFKeyValueConstants.QOS_UPGRADE_NOT_SUPPORTED.val);
            requestedQoSMapping.apply(valueProvider);

            assertFalse(pcrfRequest.getRequestedQoS().isQosUpgrade());
        }

        @Test
        public void test_apply_stores_QoSUpgradeToFalseWhneQoSUpgradeAVPNotFound() {

            requestedQoSMapping.apply(valueProvider);

            assertFalse(pcrfRequest.getRequestedQoS().isQosUpgrade());
        }

        @Test
        public void test_apply_stores_add_attributeQoSUpgradeDisabledToPCRFRequestWhenAVPFoundWithValueEnabled() {

            diameterRequest.addAvp(DiameterAVPConstants.TGPP_QOS_UPGRADE, 0);

            requestedQoSMapping.apply(valueProvider);

            assertFalse(pcrfRequest.getRequestedQoS().isQosUpgrade());
        }

        @Test
        public void test_apply_stores_add_attributeQoSUpgradeEnabledToPCRFRequestWhenAVPFoundWithValueDisabled() {

            diameterRequest.addAvp(DiameterAVPConstants.TGPP_QOS_UPGRADE, 1);
            requestedQoSMapping.apply(valueProvider);

            assertTrue(pcrfRequest.getRequestedQoS().isQosUpgrade());
        }

        @Test
        public void test_apply_stores_NotAddAttributeQoSUpgradeToPCRFRequestWhenQoSUpgradeAVPNotFound() {

            requestedQoSMapping.apply(valueProvider);

            assertFalse(pcrfRequest.getRequestedQoS().isQosUpgrade());
        }
    }

    
}
