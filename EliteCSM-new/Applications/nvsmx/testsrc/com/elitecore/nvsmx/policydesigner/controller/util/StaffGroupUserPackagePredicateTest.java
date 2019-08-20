package com.elitecore.nvsmx.policydesigner.controller.util;

import com.elitecore.corenetvertex.pm.pkg.datapackage.UserPackage;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Before;
import org.junit.Test;
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
public class StaffGroupUserPackagePredicateTest {



    public class Create{
        @Test
        public void shouldReturnObjectOfStaffUserGroupPredicateIfNullStaffGroupIsPassed() {
            StaffGroupUserPackagePredicate.create(null);
        }

        @Test
        public void shouldReturnObjectOfStaffUserGroupPredicateIfNonNullStaffGroupIsPassed() {
            List<String> staffGroups = Arrays.asList(new String[]{"Staff_1"});
            StaffGroupUserPackagePredicate staffGroupUserPackagePredicate = StaffGroupUserPackagePredicate.create(staffGroups);
            assertTrue(Objects.nonNull(staffGroupUserPackagePredicate));
        }

    }

    public class Apply{

        private static final String STAFF_1 = "staff_1";
        private static final String STAFF_2 = "staff_2";
        private static final String UNKNOWN_GROUP = "UNKNOWN_GROUP";
        @Mock
        private UserPackage userPackage;
        private StaffGroupUserPackagePredicate staffGroupUserPackagePredicate;

        @Before
        public void before() {
            MockitoAnnotations.initMocks(this);
            List<String> staffGroupList = Arrays.asList(new String[]{STAFF_1, STAFF_2});
            staffGroupUserPackagePredicate = StaffGroupUserPackagePredicate.create(staffGroupList);
        }

        @Test
        public void shouldReturnTrueIfUserPackageDoesnotBelongToAnyGroup() {
            when(userPackage.getGroupIds()).thenReturn(null);
            assertTrue(staffGroupUserPackagePredicate.apply(userPackage));
        }


        @Test
        public void shouldReturnFalseIfStaffGroupListIsEmpty() {
            StaffGroupUserPackagePredicate staffGroupUserPackagePredicate=StaffGroupUserPackagePredicate.create(Collections.EMPTY_LIST);
            when(userPackage.getGroupIds()).thenReturn(Arrays.asList(new String[]{STAFF_1}));
            assertTrue(staffGroupUserPackagePredicate.apply(userPackage));
        }


        @Test
        public void shouldReturnFalseIfUserPackageDoesnotBelongToStaffGroup() {
            when(userPackage.getGroupIds()).thenReturn(Arrays.asList(new String[]{UNKNOWN_GROUP}));
            assertFalse(staffGroupUserPackagePredicate.apply(userPackage));

        }

        @Test
        public void shouldReturnTrueIfUserPackageBelongToAnyOfTheStaffGroup() {
            when(userPackage.getGroupIds()).thenReturn(Arrays.asList(new String[]{STAFF_2}));
            assertTrue(staffGroupUserPackagePredicate.apply(userPackage));

        }

    }


}