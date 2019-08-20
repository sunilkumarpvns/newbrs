package com.elitecore.nvsmx.policydesigner.controller.util;

import com.elitecore.commons.base.Predicate;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.elitecore.nvsmx.policydesigner.controller.util.GlobalPlanPredicates.createNonContainGroupPredicate;
import static junitparams.JUnitParamsRunner.$;

@RunWith(JUnitParamsRunner.class)
public class GlobalPlanPredicatesTest {

    private static final String STAFF_1="STAFF_1";
    private static final String STAFF_2="STAFF_2";

    public Object[] datafor_shouldCreateNonContainingGroupPredicateWhenGroupListIsPassed() {
        return $(
                $(new ArrayList<>()),
                $(Arrays.asList(STAFF_1,STAFF_2))
        );
    }

    @Test(expected = NullPointerException.class)
    @Parameters(method = "dataFor_testApplyMethod")
    public void shouldThrowNPEWhenPassedGroupListIsNull(String input,boolean expectedResult){
        Predicate<String> nonContainGroupPredicate = createNonContainGroupPredicate(null);
        nonContainGroupPredicate.apply(input);
    }
    @Test
    @Parameters(method = "datafor_shouldCreateNonContainingGroupPredicateWhenGroupListIsPassed")
    public void shouldCreateNonContainingGroupPredicateWhenGroupListIsPassed(List input) {
        Assert.assertTrue(Objects.nonNull(createNonContainGroupPredicate(input)));
    }

    @Test(expected = NullPointerException.class)
    @Parameters(method = "dataFor_testApplyMethod")
    public void shouldThrowNPEIfNullStaffListIsPassedToPredicate(String input,boolean expectedResult){
        Predicate<String> nonContainGroupPredicate = createNonContainGroupPredicate(null);
        nonContainGroupPredicate.apply(input);
    }


    public Object[][] dataFor_testApplyMethod(){
        return new Object[][]{
                {null, true},
                {STAFF_1, false},
                {STAFF_2, false},
                {"UNKNOWN_STAFF",true}

        };


    }


    @Test
    @Parameters(method = "dataFor_testApplyMethod")
    public void testApplyMethod(String input,boolean expectedResult){
        Predicate<String> nonContainGroupPredicate = createNonContainGroupPredicate(Arrays.asList(STAFF_1, STAFF_2));
        Assert.assertEquals(expectedResult,nonContainGroupPredicate.apply(input));

    }
}