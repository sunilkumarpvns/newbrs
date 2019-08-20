package com.elitecore.nvsmx.policydesigner.controller.util;

import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pkg.PkgType;
import com.elitecore.corenetvertex.pm.offer.ProductOffer;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StaffGroupProductOfferPredicateTest {
    public static final String CURRENCY = "INR";

    @Test
    public void testsThatItFilterOutDataWhichDoesNotBelongToGroupIds(){
        List<String> staffGroup = new ArrayList<>();
        staffGroup.add("group_abc");
        staffGroup.add("group_pqr");

        List<String> productGroup = new ArrayList<>();
        productGroup.add("group_def");
        productGroup.add("group_xyz");

        ProductOffer productOffer = new ProductOffer(
                null, null, null,
                PkgType.BASE, PkgMode.DESIGN, null,
                null, 0.0, 0.0,
                null, null,null,
                null, productGroup, null,
                null, null,  null, null,false, null, null, null,null,null,new HashMap<>(),CURRENCY
        );

        Assert.assertFalse(StaffGroupProductOfferPredicate.create(staffGroup).apply(productOffer));
    }

    @Test
    public void testsThatItDoesNotReturnFalseIfProductBelongsToAtleastOneGroup(){
        List<String> staffGroup = new ArrayList<>();
        staffGroup.add("group_abc");
        staffGroup.add("group_def");

        List<String> productGroup = new ArrayList<>();
        productGroup.add("group_def");

        ProductOffer productOffer = new ProductOffer(
                null, null, null,
                PkgType.BASE, PkgMode.DESIGN, null,
                null, 0.0, 0.0,
                 null, null,null,
                null, productGroup, null,
                null, null,  null, null, false, null,null, null,null,null,new HashMap<>(),CURRENCY
        );

        Assert.assertTrue(StaffGroupProductOfferPredicate.create(staffGroup).apply(productOffer));
    }

    @Test
    public void testsItReturnsFalseWhenEmptyListIsPassedInConstructor(){
        List<String> staffGroup = new ArrayList<>();

        List<String> productGroup = new ArrayList<>();
        productGroup.add("group_def");

        ProductOffer productOffer = new ProductOffer(
                null, null, null,
                PkgType.BASE, PkgMode.DESIGN, null,
                null, 0.0, 0.0, null, null,null,
                null, productGroup, null,
                null, null,  null, null, false, null,null, null,null, null,new HashMap<>(),CURRENCY
        );

        Assert.assertTrue(StaffGroupProductOfferPredicate.create(staffGroup).apply(productOffer));
    }

    @Test
    public void testsItReturnsTrueWhenEmptyListIsPassedInCreate(){
        List<String> staffGroup = new ArrayList<>();
        staffGroup.add("group_abc");
        staffGroup.add("group_def");

        List<String> productGroup = new ArrayList<>();

        ProductOffer productOffer = new ProductOffer(
                null, null, null,
                PkgType.BASE, PkgMode.DESIGN, null,
                null, 0.0, 0.0,
                 null, null, null,
                null, productGroup, null,
                null, null,  null, null, false, null, null, null, null, null,new HashMap<>(),CURRENCY
        );

        Assert.assertTrue(StaffGroupProductOfferPredicate.create(staffGroup).apply(productOffer));
    }
}
