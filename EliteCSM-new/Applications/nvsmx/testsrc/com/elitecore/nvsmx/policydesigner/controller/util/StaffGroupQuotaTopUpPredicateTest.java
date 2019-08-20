package com.elitecore.nvsmx.policydesigner.controller.util;

import com.elitecore.corenetvertex.pm.pkg.datapackage.QuotaTopUp;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(HierarchicalContextRunner.class)
public class StaffGroupQuotaTopUpPredicateTest {




    public class Create {
        @Rule public ExpectedException expectedException = ExpectedException.none();

        @Test
        public void shouldThroughNPEifNullStaffGroupIsPassed() {
            expectedException.expect(NullPointerException.class);
            expectedException.expectMessage("Staff Group should not be null");
            StaffGroupQuotaTopUpPredicate.create(null);
        }

        @Test
        public void shouldReturnObjectOfStaffGroupPredicateIfNonNullStaffGroupIsPassed() {
            List<String> staffGroups = Arrays.asList(new String[]{"Staff_1"});
            StaffGroupQuotaTopUpPredicate staffGroupQuotaTopUpPredicate = StaffGroupQuotaTopUpPredicate.create(staffGroups);
            assertTrue(Objects.nonNull(staffGroupQuotaTopUpPredicate));
        }
    }

    public class Apply {

        private static final String STAFF_1 = "staff_1";
        private static final String STAFF_2 = "staff_2";
        @Mock private QuotaTopUp quotaTopUp;
        private StaffGroupQuotaTopUpPredicate staffGroupQuotaTopUpPredicate;

        @Before
        public void before() {
            MockitoAnnotations.initMocks(this);
            List<String> staffGroupList = Arrays.asList(new String[]{STAFF_1, STAFF_2});
            staffGroupQuotaTopUpPredicate = StaffGroupQuotaTopUpPredicate.create(staffGroupList);
        }

        @Test
        public void shouldReturnTrueIfQuotaTopUpDoesnotBelongToAnyGroup() {
            when(quotaTopUp.getGroupIds()).thenReturn(null);
            assertTrue(staffGroupQuotaTopUpPredicate.apply(quotaTopUp));
        }

        @Test
        public void shouldReturnFalseIfStaffGroupListIsEmpty() {
            StaffGroupQuotaTopUpPredicate staffGroupQuotaTopUpPredicate=StaffGroupQuotaTopUpPredicate.create(Collections.EMPTY_LIST);
            when(quotaTopUp.getGroupIds()).thenReturn(Arrays.asList(new String[]{STAFF_1}));
            assertFalse(staffGroupQuotaTopUpPredicate.apply(quotaTopUp));
        }


        @Test
        public void shouldReturnFalseIfQuotaTopUpDoesnotBelongToStaffGroup() {
            when(quotaTopUp.getGroupIds()).thenReturn(Arrays.asList(new String[]{"UKNOWN_GROUP"}));
            assertFalse(staffGroupQuotaTopUpPredicate.apply(quotaTopUp));

        }

        @Test
        public void shouldReturnTrueIfQuotaTopUpBelongToAnyOfTheStaffGroup() {
            when(quotaTopUp.getGroupIds()).thenReturn(Arrays.asList(new String[]{STAFF_2}));
            assertTrue(staffGroupQuotaTopUpPredicate.apply(quotaTopUp));

        }

    }
}

