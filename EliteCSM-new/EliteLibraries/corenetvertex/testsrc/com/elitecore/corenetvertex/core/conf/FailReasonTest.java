package com.elitecore.corenetvertex.core.conf;


import com.elitecore.commons.base.Collectionz;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class FailReasonTest {

    private FailReason failReason;

    @Before
    public void setUp(){
        failReason = new FailReason("Fail Reasons");
    }

    @Test
    public void isEmptyReturnsTrueWhenFailReasonsAndChildModuleFailReasonsAreEmpty(){
        Assert.assertEquals(failReason.isEmpty(),true);
    }

    @Test
    public void isEmptyReturnsFalseWhenFailReasonsAreNotEmpty(){
        failReason.add("Adding Fail Reason");
        Assert.assertEquals(failReason.isEmpty(),false);
    }

    @Test
    public void addFailReasonWillAddStringToFailReasonsList(){
        failReason.add("InValid Value Configured");
        Assert.assertEquals(failReason.toString(),"Fail Reasons:\n\tInValid Value Configured\n");

    }

    @Test
    public void addAllFailReasonWillAddStringToFailReasonsList(){
        List<String> failReasons = Collectionz.newArrayList();
        failReasons.add("Reason 1");
        failReasons.add("Reason 2");
        failReasons.add("Reason 3");
        failReason.addAll(failReasons);
        Assert.assertEquals(failReason.toString(),"Fail Reasons:\n" +
                "\tReason 1\n" +
                "\tReason 2\n" +
                "\tReason 3\n");

    }

    @Test
    public void addChildModuleFailReasonIfNotEmpty_DoesNotAddToFailReasonsWhenChileFailReasonEmpty(){
        FailReason failReasonTest = new FailReason("Fail Reason");
        failReason.addChildModuleFailReasonIfNotEmpty(failReasonTest);
        Assert.assertEquals(failReason.toString(),"Fail Reasons:\n" +
                "\tN/A\n");
    }

    @Test
    public void addChildModuleFailReasonIfNotEmpty_AddFailReasonsToTheChildModulesWhenChileFailReasonNotEmpty(){
        FailReason failReasonTest = new FailReason("Fail Reason");
        failReasonTest.add("Reason 1");
        failReason.addChildModuleFailReasonIfNotEmpty(failReasonTest);
        Assert.assertEquals(failReason.toString(),"Fail Reasons:\n" +
                "\tN/A\n" +
                ". Child Fail Reasons:\n" +
                "\tFail Reasons:\n" +
                "\t\tReason 1\n");
    }

    @Test
    public void addChildModuleFailReasonIfNotEmpty_willAddFailReasonsToTheSameChildModuleList(){
        FailReason failReasonTest = new FailReason("Fail Reason");
        failReasonTest.add("Reason 1");
        FailReason failReasonTest2 = new FailReason("Fail Reason");
        failReasonTest2.add("Reason 2");
        failReason.addChildModuleFailReasonIfNotEmpty(failReasonTest);
        failReason.addChildModuleFailReasonIfNotEmpty(failReasonTest2);
        Assert.assertEquals(failReason.toString(),"Fail Reasons:\n" +
                "\tN/A\n" +
                ". Child Fail Reasons:\n" +
                "\tFail Reasons:\n" +
                "\t\tReason 1\n\tFail Reasons:\n" +
                "\t\tReason 2\n");
    }

    @Test
    public void failReasonWillReturnNameOfTheFailReasonWhenGetNameMethodCalled(){
        Assert.assertEquals(failReason.getName(),"Fail Reasons");
    }



}
