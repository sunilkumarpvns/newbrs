package com.elitecore.netvertex.service.pcrf.servicepolicy.handler;

import java.util.Arrays;
import java.util.Collections;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.Uom;
import com.elitecore.corenetvertex.pm.pkg.DataServiceType;
import com.elitecore.corenetvertex.pm.pkg.RatingGroup;
import com.elitecore.netvertex.core.DummyNetvertexServerContextImpl;
import com.elitecore.netvertex.pm.DummyPolicyRepository;
import com.elitecore.netvertex.pm.FinalQoSSelectionData;
import com.elitecore.netvertex.pm.PackageType;
import com.elitecore.netvertex.pm.QoSProfile;
import com.elitecore.netvertex.pm.QoSProfileDetail;
import com.elitecore.netvertex.pm.RateCardBasedQoSProfileDetailBuilder;
import com.elitecore.netvertex.pm.RnCQoSProfileDetailFactory;
import com.elitecore.netvertex.pm.RnCQuotaProfileFactory;
import com.elitecore.netvertex.pm.qos.rnc.ratecard.DataRateCard;
import com.elitecore.netvertex.pm.quota.RnCQuotaProfileDetail;
import com.elitecore.netvertex.pm.util.MockBasePackage;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.impl.PCRFResponseImpl;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

@RunWith(HierarchicalContextRunner.class)
public class RatingGroupSectionStateProcessorTest {

    private RatingGroupSectionStateProcessor ratingGroupSectionStateProcessor;
    private DummyNetvertexServerContextImpl serverContext;
    private PCRFResponse pcrfResponse;
    private FinalQoSSelectionData finalQoSSelectionData;
    private QoSProfile qoSProfile;
    private QoSProfileDetail qoSProfileDetail;
    private final String name = "rateCardName";
    private final String id = "id";
    private DataRateCard dataRateCard;
    private static final String keyOne = "k1";
    private static final String keyTwo = "k2";
    private Uom pulseUom = Uom.SECOND;
    private Uom rateUom = Uom.SECOND;
    private String subscritionIdOrPkgId = "packageId";
    private String packageName = "packageName";
    private DummyPolicyRepository dummyPolicyRepository;

    @Before
    public void setUp() {
        MockBasePackage mockBasePackage = MockBasePackage.create(subscritionIdOrPkgId, packageName);
        mockBasePackage.quotaProfileTypeIsRnC();
        dummyPolicyRepository = new DummyPolicyRepository();
        dummyPolicyRepository.addBasePackage(mockBasePackage);
        serverContext = new DummyNetvertexServerContextImpl();
        serverContext.setPolicyRepository(dummyPolicyRepository);
        dataRateCard = spy(new DataRateCard(id, name, keyOne, keyTwo, Collections.emptyList(), pulseUom, rateUom));
        qoSProfile = mock(QoSProfile.class);
        finalQoSSelectionData = new FinalQoSSelectionData(PackageType.BASE);
        pcrfResponse = new PCRFResponseImpl();
        ratingGroupSectionStateProcessor = new RatingGroupSectionStateProcessor(serverContext);

    }

    public class WithRateCard {

        @Before
        public void setUp() {
            qoSProfileDetail = new RateCardBasedQoSProfileDetailBuilder().build();
            doReturn(dataRateCard).when(qoSProfile).getDataRateCard();
            finalQoSSelectionData.setQosProfileDetail(qoSProfileDetail, subscritionIdOrPkgId, qoSProfile);
        }

        @Test
        public void defaultRatingGroupAndDefaultServiceShouldBeAddedInRatingGroupSelectionState() {
            ratingGroupSectionStateProcessor.process(pcrfResponse, finalQoSSelectionData);
            assertEquals(1, pcrfResponse.getPccProfileSelectionState().getPackageSelectionStates().size());
            assertNotNull(pcrfResponse.getPccProfileSelectionState().getServiceSelectionState(CommonConstants.DEFAULT_RATING_GROUP_IDENTIFIER));
            assertNotNull(pcrfResponse.getPccProfileSelectionState().getServiceSelectionState(CommonConstants.DEFAULT_RATING_GROUP_IDENTIFIER).getPackageSelectionState(CommonConstants.ALL_SERVICE_IDENTIFIER));
        }
    }

    public class WithQuotaProfile {
        private String id;
        private String name;
        private RnCQuotaProfileDetail rnCQuotaProfileDetail;
        private String ratingGroupId = "rgId";
        private long ratingIdentifier = 10;
        private long serviceIdentifier = 1;

        @Before
        public void setUp() {
            RatingGroup ratingGroup = new RatingGroup(ratingGroupId, name, "description", ratingIdentifier);
            DataServiceType dataServiceType = new DataServiceType(CommonConstants.ALL_SERVICE_ID, randomUUID().toString(), serviceIdentifier,
                    null, Arrays.asList(ratingGroup));
            RnCQuotaProfileDetail rnCQuotaProfileDetail = new RnCQuotaProfileFactory(id, name).ratingGroup(ratingGroup).dataServiceType(dataServiceType).create();
            qoSProfileDetail = RnCQoSProfileDetailFactory.createQoSProfile().hasRnCQuota().quotaProfileDetail(rnCQuotaProfileDetail).build();
            finalQoSSelectionData.setQosProfileDetail(qoSProfileDetail, subscritionIdOrPkgId, qoSProfile);
        }

        @Test
        public void configuredRatingGroupAndServiceIdentifierShouldBeAddedInRatingGroupSelectionState() {
            ratingGroupSectionStateProcessor.process(pcrfResponse, finalQoSSelectionData);
            assertEquals(1, pcrfResponse.getPccProfileSelectionState().getPackageSelectionStates().size());
            assertNotNull(pcrfResponse.getPccProfileSelectionState().getServiceSelectionState(ratingIdentifier));
            assertNotNull(pcrfResponse.getPccProfileSelectionState().getServiceSelectionState(ratingIdentifier)
                    .getPackageSelectionState(serviceIdentifier));
        }
    }
}