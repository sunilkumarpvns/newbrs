package com.elitecore.netvertex.service.pcrf.servicepolicy.handler;

import com.elitecore.netvertex.pm.DummyPolicyRepository;
import com.elitecore.netvertex.pm.QoSProfile;
import com.elitecore.netvertex.pm.util.MockAddOnPackage;
import com.elitecore.netvertex.pm.util.MockBasePackage;
import com.elitecore.netvertex.pm.util.MockPromotionalPackage;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.unitils.reflectionassert.ReflectionAssert;

import java.util.LinkedHashMap;
import java.util.UUID;

import static org.apache.commons.lang3.RandomUtils.nextLong;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assume.assumeThat;

@RunWith(HierarchicalContextRunner.class)
public class RatingGroupSelectionStateTest {

    public class toJson {

        private RatingGroupSelectionState pccProfileSelectionState = new RatingGroupSelectionState(new LinkedHashMap<>());

        private MockBasePackage package1;
        private MockBasePackage package2;
        private QoSProfile qos1;
        private QoSProfile qos2;
        private String package1Id;
        private String package2Id;
        private String qos1Id;
        private String qos2Id;

        @Before
        public void setUp() {
            MockitoAnnotations.initMocks(this);

            package1Id = UUID.randomUUID().toString();
            package2Id = UUID.randomUUID().toString();

            package1 = MockBasePackage.create(package1Id, package1Id+"name");
            package2 = MockBasePackage.create(package2Id, package2Id+"name");
            qos1 = package1.quotaProfileTypeIsRnC().mockQoSProfile();
            qos2 = package2.quotaProfileTypeIsRnC().mockQoSProfile();
            qos1Id = qos1.getId();
            qos2Id = qos2.getId();
        }

        @Test
        public void forSinglePackage() {
            String subscriptionId = UUID.randomUUID().toString();
            long ratingGroup = nextLong(1, Long.MAX_VALUE);
            long serviceIdentifier = nextLong(1, Long.MAX_VALUE);
            pccProfileSelectionState.add(subscriptionId, package1, qos1, 0, ratingGroup, serviceIdentifier);
            Assert.assertEquals("{'states':[{'sid':'" + subscriptionId
                    + "','pid':'" + package1Id
                    + "','qid':'" + qos1Id + "','level':'0"
                    + "','rg':'" + ratingGroup
                    + "','service':'" + serviceIdentifier
                    + "'}]}", pccProfileSelectionState.toJson());

        }


        @Test
        public void forMultiplePackage() {
            String subscriptionId1 = UUID.randomUUID().toString();
            String subscriptionId2 = UUID.randomUUID().toString();
            long ratingGroup = nextLong(1, Long.MAX_VALUE);
            long serviceIdentifier = nextLong(1, Long.MAX_VALUE);
            pccProfileSelectionState.add(subscriptionId1, package1, qos1, 0, ratingGroup, serviceIdentifier);
            long ratingGroup1 = nextLong(1, Long.MAX_VALUE);
            long serviceIdentifier1 = nextLong(1, Long.MAX_VALUE);
            pccProfileSelectionState.add(subscriptionId2, package2, qos2, 0, ratingGroup1, serviceIdentifier1);
            Assert.assertEquals("{'states':[{'sid':'" + subscriptionId1
                    + "','pid':'" + package1Id
                    + "','qid':'" + qos1Id
                    + "','level':'0"
                    + "','rg':'" + ratingGroup
                    + "','service':'" + serviceIdentifier
                    +"'},"
                    + "{'sid':'" + subscriptionId2
                    + "','pid':'" + package2Id
                    + "','qid':'" + qos2Id
                    + "','level':'0"
                    + "','rg':'" + ratingGroup1
                    + "','service':'" + serviceIdentifier1
                    +"'}]}", pccProfileSelectionState.toJson());
        }


        @Test
        public void skipSubscriptionIdFromJsonIfSubscriptionIdNotProvided() {
            String subscriptionId2 = UUID.randomUUID().toString();
            long ratingGroup = nextLong(1, Long.MAX_VALUE);
            long serviceIdentifier = nextLong(1, Long.MAX_VALUE);
            pccProfileSelectionState.add(null, package1, qos1, 0, ratingGroup, serviceIdentifier);

            long ratingGroup1 = nextLong(1, Long.MAX_VALUE);
            long serviceIdentifier1 = nextLong(1, Long.MAX_VALUE);
            pccProfileSelectionState.add(subscriptionId2, package2, qos2, 0, ratingGroup1, serviceIdentifier1);
            Assert.assertEquals("{'states':[{'pid':'" + package1Id
                    + "','qid':'" + qos1Id
                    + "','level':'0"
                    + "','rg':'" + ratingGroup
                    + "','service':'" + serviceIdentifier
                    + "'},"
                    + "{'sid':'" + subscriptionId2
                    + "','pid':'" + package2Id
                    + "','qid':'" + qos2Id
                    + "','level':'0"
                    + "','rg':'" + ratingGroup1
                    + "','service':'" + serviceIdentifier1
                    + "'}]}", pccProfileSelectionState.toJson());
        }

    }

    public class fromJson {

        private MockBasePackage basePackage;
        private MockPromotionalPackage promotionalPackage;
        private MockAddOnPackage addOn;

        private QoSProfile basePackageQoS;
        private QoSProfile addQoS;
        private QoSProfile promotionalQos;
        private RatingGroupSelectionState pccProfileSelectionState = new RatingGroupSelectionState(new LinkedHashMap<>());
        private DummyPolicyRepository dummyPolicyRepository = new DummyPolicyRepository.PolicyRepositoryBuilder().build();

        @Before
        public void setUp() {
            MockitoAnnotations.initMocks(this);

            basePackage = dummyPolicyRepository.mockBasePackage();
            basePackage.quotaProfileTypeIsRnC();
            basePackageQoS = basePackage.mockQoSProfile();

            addOn = dummyPolicyRepository.mockAddOnPackage();
            addOn.quotaProfileTypeIsRnC();
            addQoS = addOn.mockQoSProfile();

            promotionalPackage = dummyPolicyRepository.mockPromotionalPackage();
            promotionalPackage.quotaProfileTypeIsRnC();
            promotionalQos = promotionalPackage.mockQoSProfile();
        }

        @Test
        public void forBasePackage() {
            long ratingGroup = nextLong(1, Long.MAX_VALUE);
            long serviceIdentifier = nextLong(1, Long.MAX_VALUE);
            pccProfileSelectionState.add(null, basePackage, basePackageQoS, 0, ratingGroup, serviceIdentifier);
            String json = pccProfileSelectionState.toJson();
            assumeThat(json, is(equalTo("{'states':[{'pid':'" + basePackage.getId()
                    + "','qid':'" + basePackageQoS.getId()
                    + "','level':'0"
                    + "','rg':'" + ratingGroup
                    + "','service':'" + serviceIdentifier
                    + "'}]}")));

            ReflectionAssert.assertReflectionEquals(RatingGroupSelectionState.fromJson(dummyPolicyRepository, json), pccProfileSelectionState);
        }

        @Test
        public void forPromotionalPackage() {
            int level = 0;
            long ratingGroup = nextLong(1, Long.MAX_VALUE);
            long serviceIdentifier = nextLong(1, Long.MAX_VALUE);
            pccProfileSelectionState.add(null, promotionalPackage, promotionalQos, level, ratingGroup, serviceIdentifier);
            String json = pccProfileSelectionState.toJson();
            assumeThat(json, is(equalTo("{'states':[{'pid':'" + promotionalPackage.getId()
                    + "','qid':'" + promotionalQos.getId()
                    + "','level':'" + level
                    + "','rg':'" + ratingGroup
                    + "','service':'" + serviceIdentifier
                    + "'}]}")));

            ReflectionAssert.assertReflectionEquals(RatingGroupSelectionState.fromJson(dummyPolicyRepository, json), pccProfileSelectionState);
        }

        @Test
        public void forAddOnPackage() {
            String subscriptionId = UUID.randomUUID().toString();
            long ratingGroup = nextLong(1, Long.MAX_VALUE);
            long serviceIdentifier = nextLong(1, Long.MAX_VALUE);
            pccProfileSelectionState.add(subscriptionId, addOn, addQoS, 0, ratingGroup, serviceIdentifier);
            String json = pccProfileSelectionState.toJson();
            assumeThat(json, is(equalTo("{'states':[{'sid':'" + subscriptionId
                    + "','pid':'" + addOn.getId()
                    + "','qid':'" + addQoS.getId() + "','level':'0"
                    + "','rg':'" + ratingGroup
                    + "','service':'" + serviceIdentifier
                    + "'}]}")));


            ReflectionAssert.assertReflectionEquals(RatingGroupSelectionState.fromJson(dummyPolicyRepository, json), pccProfileSelectionState);
        }


        @Test
        public void forMultiplePackage() {
            String subscriptionId2 = UUID.randomUUID().toString();
            long ratingGroup = nextLong(1, Long.MAX_VALUE);
            long serviceIdentifier = nextLong(1, Long.MAX_VALUE);
            pccProfileSelectionState.add(null, basePackage, basePackageQoS, 0, ratingGroup, serviceIdentifier);

            long ratingGroup1 = nextLong(1, Long.MAX_VALUE);
            long serviceIdentifier1 = nextLong(1, Long.MAX_VALUE);
            pccProfileSelectionState.add(subscriptionId2, addOn, addQoS, 1, ratingGroup1, serviceIdentifier1);

            long ratingGroup2 = nextLong(1, Long.MAX_VALUE);
            long serviceIdentifier2 = nextLong(1, Long.MAX_VALUE);
            pccProfileSelectionState.add(null, promotionalPackage, promotionalQos, 2, ratingGroup2, serviceIdentifier2);

            String basePackageJson = "{'pid':'" + basePackage.getId() + "','qid':'" + basePackageQoS.getId() + "','level':'0" + "','rg':'" + ratingGroup + "','service':'" + serviceIdentifier + "'}";
            String promotionalJson = "{'pid':'" + promotionalPackage.getId()
                    + "','qid':'" + promotionalQos.getId()
                    + "','level':'2"
                    + "','rg':'" + ratingGroup2
                    + "','service':'" + serviceIdentifier2
                    + "'}";

            String addOnJson = "{'sid':'" + subscriptionId2 +
                    "','pid':'" + addOn.getId() +
                    "','qid':'" + addQoS.getId() + "','level':'1"
                    + "','rg':'" + ratingGroup1
                    + "','service':'" + serviceIdentifier1
                    + "'}";

            String json = "{'states':[" + basePackageJson + "," + addOnJson + "," + promotionalJson + "]}";
            Assert.assertEquals(json, pccProfileSelectionState.toJson());

            ReflectionAssert.assertReflectionEquals(RatingGroupSelectionState.fromJson(dummyPolicyRepository, json), pccProfileSelectionState);
        }

    }

}