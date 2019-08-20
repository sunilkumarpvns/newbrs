package com.elitecore.nvsmx.policydesigner.controller.ratinggroup;

import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * This class will perform test cases on RatingGroupCTRL. It includes CRUD Operation testing, struts
 * flow testing as well as testing import export functionality of rating group.
 * @author ishani.bhatt
 */
@Ignore
@RunWith(Suite.class)

@Suite.SuiteClasses({
        RatingGroupCTRLCRUDOperationTest.class,
        RatingGroupCTRLExportTest.class,
        RatingGroupCTRLImportTest.class
})

public class RatingGroupCTRLTest {
}
