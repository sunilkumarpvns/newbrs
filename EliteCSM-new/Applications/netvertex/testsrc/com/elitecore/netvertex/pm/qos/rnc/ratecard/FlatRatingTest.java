package com.elitecore.netvertex.pm.qos.rnc.ratecard;

import java.util.Collections;
import com.elitecore.netvertex.core.ddf.CacheAwareDDFTable;
import com.elitecore.netvertex.core.servicepolicy.ExecutionContext;
import com.elitecore.netvertex.pm.DummyPolicyRepository;
import com.elitecore.netvertex.pm.PCRFPolicyContextImpl;
import com.elitecore.netvertex.pm.PCRFQoSProcessor;
import com.elitecore.netvertex.pm.PolicyContext;
import com.elitecore.netvertex.pm.QoSInformation;
import com.elitecore.netvertex.pm.util.MockBasePackage;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.impl.PCRFRequestImpl;
import com.elitecore.netvertex.service.pcrf.impl.PCRFResponseImpl;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@RunWith(HierarchicalContextRunner.class)
public class FlatRatingTest {

    public static final String INR = "INR";
    private FlatRating flatRating;
    private static final String keyOne = "k1";
    private static final String keyTwo = "k2";
    private static final String keyOneValue = "v1";
    private static final String keyTwoValue = "v2";
    private PolicyContext policyContext;
    private PCRFRequest pcrfRequest;
    private PCRFResponse pcrfResponse;
    @Before
    public void setUp() {
        flatRating = new FlatRating(keyOneValue, keyTwoValue, Collections.emptyList(), null);
        pcrfRequest = new PCRFRequestImpl();
        pcrfResponse = new PCRFResponseImpl();
        policyContext = new PCRFPolicyContextImpl(pcrfRequest, pcrfResponse,
                new MockBasePackage(),
                new ExecutionContext(pcrfRequest, pcrfResponse, CacheAwareDDFTable.getInstance(), INR),
                new PCRFQoSProcessor(new QoSInformation()), new DummyPolicyRepository());
    }

    public class isApplicableTrueWhen {
        @Test
        public void bothAttributesValueEqualToConfiguredValue() {
            pcrfResponse.setAttribute(keyOne, keyOneValue);
            pcrfResponse.setAttribute(keyTwo, keyTwoValue);
            assertTrue(flatRating.isApplicable(policyContext, keyOne, keyTwo));
        }
    }

    public class isApplicableFalse {
        @Test
        public void whenKeyOneAttributeNotFoundFromPCRFRequest() {
            pcrfResponse.setAttribute(keyOne, null);
            pcrfResponse.setAttribute(keyTwo, keyTwoValue);
            assertFalse(flatRating.isApplicable(policyContext, keyOne, keyTwo));
        }

        @Test
        public void whenKeyTwoAttributeNotFoundFromPCRFRequest() {
            pcrfResponse.setAttribute(keyOne, keyOneValue);
            pcrfResponse.setAttribute(keyTwo, null);
            assertFalse(flatRating.isApplicable(policyContext, keyOne, keyTwo));
        }


        @Test
        public void whenBothKeyAttributeNotFoundFromPCRFRequest() {
            pcrfResponse.setAttribute(keyOne, null);
            pcrfResponse.setAttribute(keyTwo, null);
            assertFalse(flatRating.isApplicable(policyContext, keyOne, keyTwo));
        }

        @Test
        public void whenKeyOneValueAttributeValueNotEqualToConfiguredValue() {
            pcrfResponse.setAttribute(keyOne, "x");
            pcrfResponse.setAttribute(keyTwo, keyTwoValue);
            assertFalse(flatRating.isApplicable(policyContext, keyOne, keyTwo));
        }

        @Test
        public void whenKeyTwoValueAttributeValueNotEqualToConfiguredValue() {
            pcrfResponse.setAttribute(keyOne, keyOneValue);
            pcrfResponse.setAttribute(keyTwo, "x");
            assertFalse(flatRating.isApplicable(policyContext, keyOne, keyTwo));
        }

        @Test
        public void whenBothValueAttributeValueNotEqualToConfiguredValue() {
            pcrfResponse.setAttribute(keyOne, "y");
            pcrfResponse.setAttribute(keyTwo, "x");
            assertFalse(flatRating.isApplicable(policyContext, keyOne, keyTwo));
        }
    }
}