package com.elitecore.nvsmx.policydesigner.controller.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import com.elitecore.corenetvertex.pm.pkg.imspackage.IMSPackage;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(HierarchicalContextRunner.class)
public class StaffGroupIMSPackagePredicateTest {



    public class Create{
        @Test
        public void shouldReturnObjectOfStaffUserGroupPredicateIfNullStaffGroupIsPassed() {
            StaffGroupIMSPackagePredicate.create(null);
        }

        @Test
        public void shouldReturnObjectOfStaffUserGroupPredicateIfNonNullStaffGroupIsPassed() {
            List<String> staffGroups = Arrays.asList(new String[]{"Staff_1"});
            StaffGroupIMSPackagePredicate staffGroupIMSPackagePredicate = StaffGroupIMSPackagePredicate.create(staffGroups);
            assertTrue(Objects.nonNull(staffGroupIMSPackagePredicate));
        }

    }
    public class Apply {

        private static final String STAFF_1 = "staff_1";
        private static final String STAFF_2 = "staff_2";
        private static final String UNKNOWN_GROUP = "UNKNOWN_GROUP";
        @Mock
        private IMSPackage imsPackage;
        private StaffGroupIMSPackagePredicate staffGroupIMSPackagePredicate;

        @Before
        public void before() {
            MockitoAnnotations.initMocks(this);
            List<String> staffGroupList = Arrays.asList(new String[]{STAFF_1, STAFF_2});
            staffGroupIMSPackagePredicate = StaffGroupIMSPackagePredicate.create(staffGroupList);
        }

        @Test
        public void shouldReturnTrueIfIMSPackageDoesnotBelongToAnyGroup() {
            when(imsPackage.getGroupIds()).thenReturn(null);
            assertTrue(staffGroupIMSPackagePredicate.apply(imsPackage));
        }

        @Test
        public void shouldReturnFalseIfStaffGroupListIsEmpty() {
            StaffGroupIMSPackagePredicate staffGroupIMSPackagePredicate=StaffGroupIMSPackagePredicate.create(Collections.EMPTY_LIST);
            when(imsPackage.getGroupIds()).thenReturn(Arrays.asList(STAFF_1));
            assertFalse(staffGroupIMSPackagePredicate.apply(imsPackage));
        }


        @Test
        public void shouldReturnFalseIfIMSPackageDoesnotBelongToStaffGroup() {
            when(imsPackage.getGroupIds()).thenReturn(Arrays.asList(UNKNOWN_GROUP));
            assertFalse(staffGroupIMSPackagePredicate.apply(imsPackage));

        }

        @Test
        public void shouldReturnTrueIfIMSPackageBelongToAnyOfTheStaffGroup() {
            when(imsPackage.getGroupIds()).thenReturn(Arrays.asList(STAFF_2));
            assertTrue(staffGroupIMSPackagePredicate.apply(imsPackage));

        }

    }
}