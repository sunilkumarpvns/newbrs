package com.elitecore.corenetvertex.spr.voltdb;

import com.elitecore.commons.base.FixedTimeSource;
import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.pm.pkg.imspackage.IMSPackage;
import com.elitecore.corenetvertex.pm.store.ProductOfferStore;
import com.elitecore.corenetvertex.pm.util.MockBasePackage;
import com.elitecore.corenetvertex.spr.TestSubscriberEnabledSPInterface;
import com.elitecore.corenetvertex.spr.data.SPRFields;
import com.elitecore.corenetvertex.spr.data.SPRInfoImpl;
import com.elitecore.corenetvertex.spr.data.SubscriberDetails;
import com.elitecore.corenetvertex.spr.data.SubscriberProfileData;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.voltdb.InProcessVoltDBServer;

import java.util.ArrayList;
import java.util.UUID;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Ignore
@RunWith(HierarchicalContextRunner.class)
public class VoltSPRAddProfileTest {


    private static final String DS_NAME = "test-DB";
    private static InProcessVoltDBServer voltServer;
    private VoltDBSPInterface voltDBSPInterface;
    private SubscriberProfileData profileData;
    private VoltSubscriberRepositoryImpl voltSubscriberRepository;
    @Mock
    PolicyRepository policyRepository = mock(PolicyRepository.class);
    @Mock
    private TestSubscriberEnabledSPInterface testSubscriberEnabledSPInterface;


    @BeforeClass
    public static void beforeClass() throws ClassNotFoundException {
        voltServer = new InProcessVoltDBServer();
        voltServer.start();
        // Add SQL File containing Create Table and Create Procedure query
        voltServer.runDDLFromPath("testsrc/resources/voltdb-test-ddl.sql");

        Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
    }

    @Before
    public void setUp() throws Exception {
        //  String sid = UUID.randomUUID().toString();
        MockitoAnnotations.initMocks(this);

        profileData = createProfile();

        voltServer.runDDLFromString(profileData.insertQuery());

        voltDBSPInterface = new VoltDBSPInterface(null, new DummyVoltDBClient(voltServer.getClient()), new FixedTimeSource());

        voltSubscriberRepository = new VoltSubscriberRepositoryImpl(testSubscriberEnabledSPInterface,
                "id", "name", new DummyVoltDBClient(voltServer.getClient()), null,
                policyRepository, new ArrayList<>(), SPRFields.SUBSCRIBER_IDENTITY, null,
                new VoltUMOperation(null, policyRepository,
                        new FixedTimeSource(), null, null), new FixedTimeSource(),"INR",null, null, null);

    }

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private SubscriberProfileData createProfile() {
        return new SubscriberProfileData.SubscriberProfileDataBuilder()
                .withSubscriberIdentity("kirti")
                .withImsi("1234")
                .withMsisdn("9797979797")
                .withUserName("user1")
                .withPassword("user1")
                .withPhone("123456")
                .build();
    }


    public class ThrowOperatioFailedExceptionWhen {


        @Test
        public void InvalidBasePackageConfigured() throws
                OperationFailedException {
            when(policyRepository.getBasePackageDataByName(anyString())).thenReturn(null);
            SPRInfoImpl sprInfo = createProfile().getSPRInfo();
            expectedException.expect(OperationFailedException.class);
            voltSubscriberRepository.addProfile(new SubscriberDetails(sprInfo,null), null);
        }

        @Test
        public void BasePackageConfiguredIsInFailureState() throws
                OperationFailedException {
            SPRInfoImpl sprInfo = createProfile().getSPRInfo();
            MockBasePackage mockedBasePackage = MockBasePackage.create(UUID.randomUUID().toString(), sprInfo.getProductOffer());
            mockedBasePackage.policyStatusFailure();

            when(policyRepository.getBasePackageDataByName(sprInfo.getProductOffer())).thenReturn(mockedBasePackage);
            expectedException.expect(OperationFailedException.class);
            voltSubscriberRepository.addProfile(new SubscriberDetails(sprInfo,null), null);
        }

        @Test
        public void BasePackageConfiguredHasAvailabilityStatusIsNotActive() throws
                OperationFailedException {
            when(policyRepository.getProductOffer()).thenReturn(Mockito.mock(ProductOfferStore.class));
            SPRInfoImpl sprInfo = createProfile().getSPRInfo();
            MockBasePackage mockedBasePackage = MockBasePackage.create(UUID.randomUUID().toString(), sprInfo.getProductOffer());
            mockedBasePackage.policyStatusSuccess();
            mockedBasePackage.statusInActive();
            when(policyRepository.getBasePackageDataByName(sprInfo.getProductOffer())).thenReturn(mockedBasePackage);
            expectedException.expect(OperationFailedException.class);
            voltSubscriberRepository.addProfile(new SubscriberDetails(sprInfo,null), null);
        }


        @Test
        public void WhenConfiguredIMSDoesntExistInRepository() throws
                OperationFailedException {
            String imsPackageName = "IMS_PACKAGE";
            SPRInfoImpl sprInfo = createProfile().getSPRInfo();
            if (Strings.isNullOrBlank(sprInfo.getImsPackage())) {
                sprInfo.setImsPackage(imsPackageName);
            }
            MockBasePackage mockedBasePackage = MockBasePackage.create(UUID.randomUUID().toString(), sprInfo.getProductOffer());
            mockedBasePackage.statusActive();
            mockedBasePackage.policyStatusSuccess();

            when(policyRepository.getBasePackageDataByName(sprInfo.getProductOffer())).thenReturn(mockedBasePackage);

            IMSPackage mockedImsPackage = mock(IMSPackage.class);
            when(mockedImsPackage.getName()).thenReturn(imsPackageName);
            when(policyRepository.getIMSPackageByName(imsPackageName)).thenReturn(null);
            expectedException.expect(OperationFailedException.class);
            voltSubscriberRepository.addProfile(new SubscriberDetails(sprInfo,null), null);

        }

        @Test
        public void WhenConfiguredIMSISInFailureState() throws
                OperationFailedException {
            String imsPackageName = "IMS_PACKAGE";
            SPRInfoImpl sprInfo = createProfile().getSPRInfo();
            if (Strings.isNullOrBlank(sprInfo.getImsPackage())) {
                sprInfo.setImsPackage(imsPackageName);
            }
            MockBasePackage mockedBasePackage = MockBasePackage.create(UUID.randomUUID().toString(), sprInfo.getProductOffer());
            mockedBasePackage.policyStatusSuccess();
            mockedBasePackage.statusActive();

            when(policyRepository.getBasePackageDataByName(sprInfo.getProductOffer())).thenReturn(mockedBasePackage);

            IMSPackage mockedImsPackage = mock(IMSPackage.class);
            when(mockedImsPackage.getName()).thenReturn(imsPackageName);
            when(mockedImsPackage.getStatus()).thenReturn(PolicyStatus.FAILURE);
            when(policyRepository.getIMSPkgByName(imsPackageName)).thenReturn(mockedImsPackage);
            expectedException.expect(OperationFailedException.class);
            voltSubscriberRepository.addProfile(new SubscriberDetails(sprInfo,null), null);
        }

        @Test
        public void WhenConfiguredIMSAvailabilityStateIsNotActive() throws OperationFailedException {
            String imsPackageName = "IMS_PACKAGE";
            SPRInfoImpl sprInfo = createProfile().getSPRInfo();
            if (Strings.isNullOrBlank(sprInfo.getImsPackage())) {
                sprInfo.setImsPackage(imsPackageName);
            }
            MockBasePackage mockedBasePackage = MockBasePackage.create(UUID.randomUUID().toString(), sprInfo.getProductOffer());
            mockedBasePackage.statusActive();
            mockedBasePackage.policyStatusSuccess();
            when(policyRepository.getBasePackageDataByName(sprInfo.getProductOffer())).thenReturn(mockedBasePackage);

            IMSPackage mockedImsPackage = mock(IMSPackage.class);
            when(mockedImsPackage.getName()).thenReturn(imsPackageName);
            when(mockedImsPackage.getStatus()).thenReturn(PolicyStatus.SUCCESS);
            when(mockedImsPackage.getAvailabilityStatus()).thenReturn(PkgStatus.RETIRED);
            when(policyRepository.getIMSPkgByName(imsPackageName)).thenReturn(mockedImsPackage);
            expectedException.expect(OperationFailedException.class);
            voltSubscriberRepository.addProfile(new SubscriberDetails(sprInfo,null), null);
        }

        @Test
        public void RNCPackageIsConfigured() throws OperationFailedException {
            SPRInfoImpl sprInfo = createProfile().getSPRInfo();
            if (Strings.isNullOrBlank(sprInfo.getProductOffer())) {
                sprInfo.setProductOffer("RNC_PACKAGE");
            }
            MockBasePackage mockedBasePackage = MockBasePackage.create(UUID.randomUUID().toString(), sprInfo.getProductOffer());
            mockedBasePackage.policyStatusSuccess();
            mockedBasePackage.statusActive();
            when(policyRepository.getBasePackageDataByName(sprInfo.getProductOffer())).thenReturn(mockedBasePackage);
            expectedException.expect(OperationFailedException.class);
            voltSubscriberRepository.addProfile(new SubscriberDetails(sprInfo,null), null);
        }


    }

    @After
    public void tearDown() {
        voltServer.runDDLFromPath("testsrc/resources/voltdb-cleanup-ddl.sql");
    }

    @AfterClass
    public static void afterClass() {
        voltServer.shutdown();
    }
}
