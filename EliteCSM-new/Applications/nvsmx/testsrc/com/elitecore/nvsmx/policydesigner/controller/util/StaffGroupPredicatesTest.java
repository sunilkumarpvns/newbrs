package com.elitecore.nvsmx.policydesigner.controller.util;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)

@Suite.SuiteClasses({
        StaffGroupQuotaTopUpPredicateTest.class,
        StaffGroupUserPackagePredicateTest.class,
        StaffGroupIMSPackagePredicateTest.class,
        GlobalPlanPredicatesTest.class
})
public class StaffGroupPredicatesTest {
}
