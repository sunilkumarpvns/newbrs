package com.elitecore.nvsmx.policydesigner.controller.util;

import com.elitecore.corenetvertex.pm.util.MockQuotaTopUp;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@RunWith(HierarchicalContextRunner.class)
public class ApplicableQuotaTopUpByBasePackagePredicateTest {


    private List<String> pccProfiles;
    private MockQuotaTopUp mockQuotaTopUp;
    private static final String pccProfile1 = UUID.randomUUID().toString();
    private static final String pccProfile2 = UUID.randomUUID().toString();
    private static final String UNKNOWN = "UNKNOWN";
    private ApplicableQuotaTopUpByBasePackagePredicate applicableQuotaTopUpByBasePackagePredicate;


    @Before
    public void setUp() {
        pccProfiles = Arrays.asList(pccProfile1, pccProfile2);
        mockQuotaTopUp = MockQuotaTopUp.create(UUID.randomUUID().toString(), UUID.randomUUID().toString());
        applicableQuotaTopUpByBasePackagePredicate = ApplicableQuotaTopUpByBasePackagePredicate.create(pccProfiles);
    }


    public class ReturnsTrueWhen {

        @Test
        public void noApplicablePCCProfileConfiguredInQuotaTopUP() {
            mockQuotaTopUp.withApplicablePCCProfiles(Collections.emptyList());
            Assert.assertTrue(applicableQuotaTopUpByBasePackagePredicate.test(mockQuotaTopUp));
        }


        @Test
        public void anyApplicablePCCProfileConfiguredInQuotaTopUPMatches() {
            mockQuotaTopUp.withApplicablePCCProfiles(Arrays.asList(pccProfile1));
            Assert.assertTrue(applicableQuotaTopUpByBasePackagePredicate.test(mockQuotaTopUp));
        }


        @Test
        public void aLLApplicablePCCProfileConfiguredInQuotaTopUPMatches() {
            mockQuotaTopUp.withApplicablePCCProfiles(Arrays.asList(pccProfile1));
            Assert.assertTrue(applicableQuotaTopUpByBasePackagePredicate.test(mockQuotaTopUp));
        }
    }

    public class ReturnFalseWhen {

        @Test
        public void whenNoApplicablePCCProfileMatchesWithConfiguredInQuotaTop() {
            mockQuotaTopUp.withApplicablePCCProfiles(Arrays.asList(pccProfile1 + UNKNOWN, pccProfile2 + UNKNOWN));
            Assert.assertFalse(applicableQuotaTopUpByBasePackagePredicate.test(mockQuotaTopUp));
        }

        @Test
        public void whenPredicateCreatedWithEmptyPCCProfiles() {
            MockQuotaTopUp mockQuotaTopUp = MockQuotaTopUp.create(UUID.randomUUID().toString(), UUID.randomUUID().toString());
            mockQuotaTopUp.withApplicablePCCProfiles(Arrays.asList(pccProfile1 + UNKNOWN, pccProfile2 + UNKNOWN));
            ApplicableQuotaTopUpByBasePackagePredicate predicate = ApplicableQuotaTopUpByBasePackagePredicate.create(Collections.emptyList());
            Assert.assertFalse(predicate.test(mockQuotaTopUp));
        }


        @Test
        public void whenPredicateCreatedWithNullPCCProfiles() {
            MockQuotaTopUp mockQuotaTopUp = MockQuotaTopUp.create(UUID.randomUUID().toString(), UUID.randomUUID().toString());
            mockQuotaTopUp.withApplicablePCCProfiles(Arrays.asList(pccProfile1 + UNKNOWN, pccProfile2 + UNKNOWN));
            ApplicableQuotaTopUpByBasePackagePredicate predicate = ApplicableQuotaTopUpByBasePackagePredicate.create(null);
            Assert.assertFalse(predicate.test(mockQuotaTopUp));
        }
    }
}