package com.elitecore.netvertex.pm.qos.rnc.ratecard;

import com.elitecore.corenetvertex.constants.Uom;
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

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

@RunWith(HierarchicalContextRunner.class)
public class DataRateCardTest {

    public static final String INR = "INR";
    private final String name = "rateCardName";
    private final String id = "id";
    private DataRateCard dataRateCard;
    private static final String keyOne = "k1";
    private static final String keyTwo = "k2";
    private PolicyContext policyContext;
    private QoSInformation qoSInformation;
    private Uom pulseUom = Uom.SECOND;
    private Uom rateUom = Uom.SECOND;
    private List<com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc.ratecard.RateCardVersion> dataRateCardVersions;

    @Before
    public void setUp() {
        dataRateCardVersions = new ArrayList<>();
        dataRateCard = new DataRateCard(id, name, keyOne, keyTwo, dataRateCardVersions, pulseUom, rateUom);
        qoSInformation = new QoSInformation();
        PCRFRequest pcrfRequest = new PCRFRequestImpl();
        PCRFResponse pcrfResponse = new PCRFResponseImpl();
        policyContext = spy(new PCRFPolicyContextImpl(pcrfRequest, pcrfResponse,
                new MockBasePackage(),
                new ExecutionContext(pcrfRequest, pcrfResponse, CacheAwareDDFTable.getInstance(), INR),
                new PCRFQoSProcessor(qoSInformation), new DummyPolicyRepository()));
    }

    public class isApplicableTrueWhen {

        @Test
        public void latestVersionIsApplicable() {
            addRateCardVersionWithApplicability(true);
            assertTrue(dataRateCard.isApplicable(policyContext, qoSInformation));
        }

    }

    private void addRateCardVersionWithApplicability(boolean isApplicable) {
        DataRateCardVersion dataRateCardVersion = mock(DataRateCardVersion.class);
        doReturn(isApplicable).when(dataRateCardVersion).isApplicable(any(), eq(keyOne), eq(keyTwo));
        dataRateCardVersions.add(dataRateCardVersion);
    }

    public class isApplicableFalseThen {
        @Test
        public void latestVersionIsNotApplicable() {
            addRateCardVersionWithApplicability(false);
            assertFalse(dataRateCard.isApplicable(policyContext, qoSInformation));
        }
    }

    public class ApplyReservationTrueWhen {

        @Test
        public void latestVersionIsApplicable() {
            addRateCardVersionWithApplicability(true);
            assertTrue(dataRateCard.isApplicable(policyContext, qoSInformation));

        }
    }

    public class ApplyReservationFalseWhen {

        @Test
        public void latestVersionIsNotApplicable() {
            addRateCardVersionWithApplicability(false);
            assertFalse(dataRateCard.isApplicable(policyContext, qoSInformation));

        }
    }
}