package com.elitecore.netvertex.core.mapping.gy;

import com.elitecore.corenetvertex.constants.PCRFKeyValueConstants;
import com.elitecore.corenetvertex.data.GyServiceUnits;
import com.elitecore.corenetvertex.data.ResultCode;
import com.elitecore.corenetvertex.pkg.rnc.VolumeUnitType;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfile;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfileDetail;
import com.elitecore.corenetvertex.util.commons.Supplier;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.packet.avps.grouped.AvpGrouped;
import com.elitecore.diameterapi.diameter.common.util.DiameterDictionary;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.DiameterPacketMappingValueProvider;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.gy.MSCCBuilder;
import com.elitecore.netvertex.core.util.Maps;
import com.elitecore.netvertex.gateway.diameter.DummyDiameterDictionary;
import com.elitecore.netvertex.gateway.diameter.DummyDiameterGatewayControllerContext;
import com.elitecore.netvertex.gateway.diameter.conf.DiameterGatewayConfiguration;
import com.elitecore.netvertex.gateway.diameter.gy.MSCC;
import com.elitecore.netvertex.gateway.diameter.utility.*;
import com.elitecore.netvertex.pm.DummyPolicyRepository;
import com.elitecore.netvertex.pm.quota.RnCQuotaProfileDetail;
import com.elitecore.netvertex.pm.util.MockBasePackage;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.impl.PCRFResponseImpl;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.apache.commons.lang.math.RandomUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.unitils.reflectionassert.ReflectionComparatorMode;

import java.util.*;

import static com.elitecore.diameterapi.diameter.DiameterMatchers.AvpMatcher.containsAttribute;
import static com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

@RunWith(HierarchicalContextRunner.class)
public class MSCCBuilderTest {

    private MSCCBuilder msccBuilder = new MSCCBuilder();
    private DiameterPacketMappingValueProvider valueProvider;
    private AvpAccumalatorTestSupport avpAccumalator;
    private AttributeFactory attributeFactory = AttributeFactories.fromDummyDictionary();


    @BeforeClass
    public static void initDictionary() {
        DummyDiameterDictionary.getInstance();
    }

    @Before
    public void setUp() {
        PCRFResponse pcrfResponse = new PCRFResponseImpl();
        DiameterAnswer diameterAnswer = new DiameterAnswer();
        DiameterGatewayConfiguration configuration = mock(DiameterGatewayConfiguration.class);
        DummyDiameterGatewayControllerContext controllerContext = new DummyDiameterGatewayControllerContext();
        controllerContext.setPolicyManager(new DummyPolicyRepository());
        avpAccumalator = new AvpAccumalatorTestSupport();
        valueProvider = new DiameterPacketMappingValueProvider(pcrfResponse, diameterAnswer, configuration, controllerContext);
    }

    @Test
    public void addValidityTimeAvpWhenValidityTimeIsGreaterThanZero() {

        GyServiceUnits gyServiceUnits = new GyServiceUnits();
        gyServiceUnits.setVolume(100);


        int validityTime = RandomUtils.nextInt(Integer.MAX_VALUE);
        MSCC mscc = new MSCC();
        mscc.setGrantedServiceUnits(gyServiceUnits);
        mscc.setRatingGroup(1l);
        mscc.setValidityTime(validityTime);


        MockBasePackage basePackage = ((DummyPolicyRepository)valueProvider.getControllerContext().getPolicyManager()).mockBasePackage();
        gyServiceUnits.setPackageId(basePackage.getId());
        basePackage.quotaProfileTypeIsRnC();
        QuotaProfile quotaProfile = Mockito.mock(QuotaProfile.class);
        when(basePackage.getQuotaProfile(gyServiceUnits.getQuotaProfileIdOrRateCardId())).thenReturn(quotaProfile);
        RnCQuotaProfileDetail rnCQuotaProfileDetail = Mockito.mock(RnCQuotaProfileDetail.class);
        Map<String, QuotaProfileDetail> hsqLevelServiceMap = Maps.newLinkedHashMap(Maps.Entry.newEntry(UUID.randomUUID().toString(), rnCQuotaProfileDetail));
        doReturn(hsqLevelServiceMap).when(quotaProfile).getHsqLevelServiceWiseQuotaProfileDetails();
        doReturn(VolumeUnitType.TOTAL).when(rnCQuotaProfileDetail).getUnitType();
        when(rnCQuotaProfileDetail.getUnitType()).thenReturn(VolumeUnitType.TOTAL);

        valueProvider.getPcrfResponse().setGrantedMSCCs(Arrays.asList(mscc));

        
        msccBuilder.addMSCCAVPs(valueProvider, avpAccumalator);
        assertNotNull("No MSCC avp created", avpAccumalator.getAll());
        assertTrue("No MSCC avp created", avpAccumalator.getAll().size() > 0);
        assertThat(avpAccumalator.getAll().get(0), containsAttribute(VALIDITY_TIME, validityTime));
    }


    @Test
    public void grantedServiceUnitNotAdded_When_GrantedServiceUnits_not_found_from_GrantedMSCC() {
        int validityTime = RandomUtils.nextInt(Integer.MAX_VALUE);
        MSCC mscc = new MSCC();
        mscc.setGrantedServiceUnits(null);
        mscc.setRatingGroup(1l);
        mscc.setValidityTime(validityTime);


        valueProvider.getPcrfResponse().setGrantedMSCCs(Arrays.asList(mscc));

        msccBuilder.addMSCCAVPs(valueProvider, avpAccumalator);

        AvpGrouped msccAvpGrouped = (AvpGrouped) avpAccumalator.getDiameterAVPs(DiameterAVPConstants.MULTIPLE_SERVICES_CREDIT_CONTROL).get(0);
        IDiameterAVP subAttribute = msccAvpGrouped.getSubAttribute(DiameterAVPConstants.GRANTED_SERVICE_UNIT);
        assertNull(subAttribute);
    }

    @Test
    public void notAdded_When_GrantedMSCCNotFound() {

        msccBuilder.addMSCCAVPs(valueProvider, avpAccumalator);
        assertTrue(avpAccumalator.getAll().isEmpty());
    }



    /*
    as HierarchicalContextRunner and ParamRunner can not work together,

    Below is the alternative
     */
    public class GrantedServiceUnitCreated_whenAtleastOneUsageTypeIsGranted {


        private long time = 0;
        private long total = 0;
        private long input = 0;
        private long output = 0;

        @Test
        public void timeGranted() {
            time = 1;
            IDiameterAVP mscc = createMSCC();
            IDiameterAVP expectedAvp = createExpectedAvp(() -> Arrays.asList(attributeFactory.create(CC_TIME, Long.toString(time))));
            assertReflectionEquals(expectedAvp, mscc, ReflectionComparatorMode.LENIENT_ORDER);
        }

        @Test
        public void totalGranted() {
            total = 1;
            IDiameterAVP mscc = createMSCC();
            IDiameterAVP expectedAvp = createExpectedAvp(() -> Arrays.asList(attributeFactory.create(CC_TOTAL_OCTETS, Long.toString(total))));
            assertReflectionEquals(expectedAvp, mscc, ReflectionComparatorMode.LENIENT_ORDER);
        }

        @Test
        public void allGranted() {
            Random random = new Random();
            total = random.nextInt(Integer.MAX_VALUE);
            time = random.nextInt(Integer.MAX_VALUE);

            IDiameterAVP mscc = createMSCC();

            IDiameterAVP expectedAvp = createExpectedAvp(() -> Arrays.asList(
                    attributeFactory.create(CC_TOTAL_OCTETS, Long.toString(total))
                    , attributeFactory.create(CC_TIME, Long.toString(time))));


            assertReflectionEquals(expectedAvp, mscc, ReflectionComparatorMode.LENIENT_ORDER);
        }


        public IDiameterAVP createMSCC() {

            GyServiceUnits gyServiceUnits = new GyServiceUnits();
            gyServiceUnits.setTime(time);
            gyServiceUnits.setVolume(total);

            MSCC mscc = new MSCC();
            mscc.setGrantedServiceUnits(gyServiceUnits);
            mscc.setRatingGroup(1l);
            mscc.setResultCode(ResultCode.SUCCESS);

            MockBasePackage basePackage = ((DummyPolicyRepository)valueProvider.getControllerContext().getPolicyManager()).mockBasePackage();
            gyServiceUnits.setPackageId(basePackage.getId());
            basePackage.quotaProfileTypeIsRnC();
            QuotaProfile quotaProfile = Mockito.mock(QuotaProfile.class);
            when(basePackage.getQuotaProfile(gyServiceUnits.getQuotaProfileIdOrRateCardId())).thenReturn(quotaProfile);
            RnCQuotaProfileDetail rnCQuotaProfileDetail = Mockito.mock(RnCQuotaProfileDetail.class);
            Map<String, QuotaProfileDetail> hsqLevelServiceMap = Maps.newLinkedHashMap(Maps.Entry.newEntry(UUID.randomUUID().toString(), rnCQuotaProfileDetail));
            doReturn(hsqLevelServiceMap).when(quotaProfile).getHsqLevelServiceWiseQuotaProfileDetails();
            doReturn(VolumeUnitType.TOTAL).when(rnCQuotaProfileDetail).getUnitType();
            when(rnCQuotaProfileDetail.getUnitType()).thenReturn(VolumeUnitType.TOTAL);

            valueProvider.getPcrfResponse().setGrantedMSCCs(Arrays.asList(mscc));
            msccBuilder.addMSCCAVPs(valueProvider, avpAccumalator);

            List<IDiameterAVP> umInfoAvps = avpAccumalator.getAll();
            assertNotNull(umInfoAvps);
            assertEquals(1, umInfoAvps.size());

            return umInfoAvps.get(0);

        }

        public IDiameterAVP createExpectedAvp(Supplier<List<IDiameterAVP>> avpSupplier) {
            MSCC grantedMSCC = valueProvider.getPcrfResponse().getGrantedMSCCs().get(0);

            AvpGrouped mscc = (AvpGrouped) DiameterDictionary.getInstance().getKnownAttribute(MULTIPLE_SERVICES_CREDIT_CONTROL);
            mscc.addSubAvp(RATING_GROUP, grantedMSCC.getRatingGroup());

            IDiameterAVP resultCodeAVP = ResultCodeMapping.getInstance().getResultCodeAVP(PCRFKeyValueConstants.RESULT_CODE_SUCCESS.val, (int) valueProvider.getDiameterPacket().getApplicationID());
            mscc.addSubAvp(resultCodeAVP);
            AvpGrouped serviceUnitAvp = (AvpGrouped) DiameterDictionary.getInstance().getKnownAttribute(GRANTED_SERVICE_UNIT);
            serviceUnitAvp.addSubAvps(avpSupplier.supply());
            mscc.addSubAvp(serviceUnitAvp);

            return mscc;
        }


    }

}