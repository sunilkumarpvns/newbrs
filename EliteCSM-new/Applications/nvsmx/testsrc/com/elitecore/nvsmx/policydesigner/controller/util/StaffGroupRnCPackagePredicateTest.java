package com.elitecore.nvsmx.policydesigner.controller.util;

import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.pkg.ChargingType;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pkg.RnCPkgType;
import com.elitecore.corenetvertex.pm.rnc.pkg.RnCPackage;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class StaffGroupRnCPackagePredicateTest {

    private static final String CURRENCY="INR";

    @Test
    public void testsThatItFilterOutDataWhichDoesNotBelongToGroupIds(){
        List<String> staffGroup = new ArrayList<>();
        staffGroup.add("group_abc");
        staffGroup.add("group_pqr");

        List<String> productGroup = new ArrayList<>();
        productGroup.add("group_def");
        productGroup.add("group_xyz");

        RnCPackage rnCPackage = new RnCPackage(null, null, null,
                productGroup, new ArrayList<>(),
                null,null, RnCPkgType.BASE,
                PkgMode.TEST, PkgStatus.ACTIVE, null,
                null, null, ChargingType.SESSION,CURRENCY);

        Assert.assertFalse(StaffGroupRnCPackagePredicate.create(staffGroup).test(rnCPackage));
    }

    @Test
    public void testsThatItDoesNotReturnFalseIfProductBelongsToAtleastOneGroup(){
        List<String> staffGroup = new ArrayList<>();
        staffGroup.add("group_abc");
        staffGroup.add("group_def");

        List<String> productGroup = new ArrayList<>();
        productGroup.add("group_def");

        RnCPackage rnCPackage = new RnCPackage(null, null, null,
                productGroup, new ArrayList<>(),
                null,null, RnCPkgType.BASE,
                PkgMode.TEST, PkgStatus.ACTIVE, null,
                null, null, ChargingType.SESSION,CURRENCY);

        Assert.assertTrue(StaffGroupRnCPackagePredicate.create(staffGroup).test(rnCPackage));
    }

    @Test
    public void testsItReturnsFalseWhenEmptyListIsPassedInConstructor(){
        List<String> staffGroup = new ArrayList<>();

        List<String> productGroup = new ArrayList<>();
        productGroup.add("group_def");

        RnCPackage rnCPackage = new RnCPackage(null, null, null,
                productGroup, new ArrayList<>(),
                null,null, RnCPkgType.BASE,
                PkgMode.TEST, PkgStatus.ACTIVE, null,
                null, null, ChargingType.SESSION,CURRENCY);

        Assert.assertTrue(StaffGroupRnCPackagePredicate.create(staffGroup).test(rnCPackage));
    }

    @Test
    public void testsItReturnsTrueWhenEmptyListIsPassedInCreate(){
        List<String> staffGroup = new ArrayList<>();
        staffGroup.add("group_abc");
        staffGroup.add("group_def");

        List<String> productGroup = new ArrayList<>();

        RnCPackage rnCPackage = new RnCPackage(null, null, null,
                productGroup, new ArrayList<>(),
                null,null, RnCPkgType.BASE,
                PkgMode.TEST, PkgStatus.ACTIVE, null,
                null, null, ChargingType.SESSION,CURRENCY);

        Assert.assertTrue(StaffGroupRnCPackagePredicate.create(staffGroup).test(rnCPackage));
    }
}
